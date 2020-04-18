package com.dreamfoxick.friendservice.service.vkclient.impl;

import com.dreamfoxick.friendservice.configuration.YamlPropertySourceFactory;
import com.dreamfoxick.friendservice.data.mongo.entities.TokenEntity;
import com.dreamfoxick.friendservice.data.mongo.service.UserDTO;
import com.dreamfoxick.friendservice.service.crypto.Cryptographer;
import com.dreamfoxick.friendservice.service.vkclient.VkAPI;
import com.dreamfoxick.friendservice.service.vkclient.json.common.*;
import com.dreamfoxick.friendservice.service.vkclient.json.common.objects.ScreenNameJson;
import com.dreamfoxick.friendservice.service.vkclient.json.common.objects.UserJson;
import com.dreamfoxick.friendservice.service.vkclient.json.common.objects.UserTokenJson;
import com.dreamfoxick.friendservice.service.vkclient.json.error.SingleError;
import com.dreamfoxick.friendservice.service.vkclient.json.error.objects.ApiException;
import com.dreamfoxick.friendservice.service.vkclient.json.error.objects.InvalidJsonException;
import com.dreamfoxick.friendservice.util.annotation.CallPerSecond;
import com.dreamfoxick.friendservice.util.annotation.LogMethodCall;
import com.dreamfoxick.friendservice.util.exception.ExUtils;
import com.dreamfoxick.friendservice.util.logging.LogUtils;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static com.dreamfoxick.friendservice.service.vkclient.impl.Method.*;
import static com.dreamfoxick.friendservice.service.vkclient.limiter.TokenType.GROUP;
import static com.dreamfoxick.friendservice.service.vkclient.limiter.TokenType.USER;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
@Profile({"dev", "test"})
@PropertySource(value = "classpath:vkapi.yml", factory = YamlPropertySourceFactory.class)
@SuppressWarnings({"SameParameterValue", "rawtypes", "unused"})
public class ReactiveVkApi implements VkAPI {
    private static final Class<ItemsObject> ITEMS_OBJECT_CLASS = ItemsObject.class;
    private static final Class<ArrayObject> ARRAY_OBJECT_CLASS = ArrayObject.class;
    private static final Class<SingleObject> SINGLE_OBJECT_CLASS = SingleObject.class;

    private static final Class<SingleError> SINGLE_ERROR_CLASS = SingleError.class;

    private static final int RETRY_COUNT_API_TOO_MANY_REQUEST_EX = 3;
    private static final int RETRY_COUNT_WEB_CLIENT_RESPONSE_EX = 1;

    private static final String RESPONSE_JSON_PREFIX = "{\"response\":";
    private static final String SINGLE_ERROR_JSON_PREFIX = "{\"error\":";
    private static final String ARRAY_ERROR_JSON_MARKER = "\"execute_errors\":";

    private final Cryptographer cryptographer;
    private final Scheduler blockExec;

    private WebClient client;

    private TypeFactory typeFactory;
    private ObjectMapper jsonMapper;

    @Value("${common.version}")
    private String version;
    @Value("${common.client_ID}")
    private String clientID;
    @Value("${common.client_secret}")
    private String clientSecret;
    @Value("${common.group_token}")
    private String groupToken;

    @Value("${method_call_params.scheme}")
    private String methodScheme;
    @Value("${method_call_params.host}")
    private String methodHost;
    @Value("${method_call_params.path}")
    private String methodPath;

    @Value("${auth_call_params.scheme}")
    private String authScheme;
    @Value("${auth_call_params.host}")
    private String authHost;
    @Value("${auth_call_params.get_token_path}")
    private String getTokenPath;
    @Value("${auth_call_params.redirect_uri}")
    private String authRedirectUri;

    @Autowired
    public ReactiveVkApi(Cryptographer cryptographer,
                         UserDTO userDTO,
                         @Qualifier("blockingSch") Scheduler blockingSch) {
        this.cryptographer = cryptographer;
        this.blockExec = blockingSch;
    }

    @PostConstruct
    private void initialize() {
        client = WebClient
                .builder()
                // 100 MB size
                .codecs(configurer -> {
                    configurer.defaultCodecs().maxInMemorySize(100 * 1024 * 1024);
                })
                .build();
        jsonMapper = new ObjectMapper()
                .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        typeFactory = jsonMapper.getTypeFactory();
    }


    private <R> Mono<R> fromCallable(Callable<R> f) {
        return Mono.fromCallable(f).subscribeOn(blockExec);
    }

    private boolean isRespBody(final String json) {
        return json.startsWith(RESPONSE_JSON_PREFIX);
    }

    private boolean isErrorBody(final String json) {
        return json.startsWith(SINGLE_ERROR_JSON_PREFIX);
    }


    private boolean isExecErrorBody(final String json) {
        return json.contains(ARRAY_ERROR_JSON_MARKER);
    }

    @Override
    @LogMethodCall
    public Mono<UserTokenJson> getAccessToken(@NonNull String code) {
        return Mono.empty();
    }

    /**
     * Example: https://api.vk.com/method/users.get?user_ids=210700286&fields=sex,city&access_token=a529d15a6d72cdc9e3b51416f9fcddd35e003fe9bdc96af6fdf5f16073dd07eec52368fbc79b6b61ddd6d&v=5.103
     */
    @Override
    @LogMethodCall
    @CallPerSecond(type = GROUP)
    public Mono<List<UserJson>> usersGet(@NonNull final List<Integer> ids) {
        return params("user_ids", ids.stream().map(String::valueOf).collect(Collectors.joining(",")), "fields", "sex")
                .map(params -> putVersion(putGroupToken(params)))
                .flatMap(params -> get(USERS_GET, params))
                .flatMap(response -> parseResponseToArrayObject(response, UserJson.class, ApiException.class))
                .doOnError(ExUtils::isWebClientResponseEx, LogUtils::logRespEx)
                .doOnError(ExUtils::isApiEx, LogUtils::logApiEx)
                .retry(RETRY_COUNT_API_TOO_MANY_REQUEST_EX, ExUtils::isTooManyRequestEx)
                .retry(RETRY_COUNT_WEB_CLIENT_RESPONSE_EX, ExUtils::isWebClientResponseEx);
    }


    /**
     * Example: https://api.vk.com/method/friends.get?user_id=210700286&fields=sex,city&access_token=5768851741e619811684c3e95170b6059114fd990609169f4503b6d9dc8a1808c17ae0cc3b04b44d5adc1&v=5.103
     */
    @Override
    @LogMethodCall
    @CallPerSecond(type = USER)
    public Mono<Tuple2<Integer, List<UserJson>>> friendsGet(final int ID,
                                                            @NonNull final TokenEntity token) {
        return params("user_id", valueOf(ID), "fields", "sex")
                .map(params -> putVersion(putToken(params, cryptographer.decode(token))))
                .flatMap(params -> get(FRIENDS_GET, params))
                .flatMap(response -> parseResponseToItemsObject(response, UserJson.class, ApiException.class))
                .doOnError(ExUtils::isWebClientResponseEx, LogUtils::logRespEx)
                .doOnError(ExUtils::isApiEx, LogUtils::logApiEx)
                .retry(RETRY_COUNT_API_TOO_MANY_REQUEST_EX, ExUtils::isTooManyRequestEx)
                .retry(RETRY_COUNT_WEB_CLIENT_RESPONSE_EX, ExUtils::isWebClientResponseEx);
    }

    @Override
    @CallPerSecond(type = USER)
    public Mono<List<Tuple2<Integer, List<UserJson>>>> executeFriendsGet(@NonNull final String code,
                                                                         @NonNull final TokenEntity token) {
        return Mono.error(UnsupportedOperationException::new);
    }

    /**
     * Example: https://api.vk.com/method/utils.resolveScreenName?screen_name=dreamdots&access_token=a529d15a6d72cdc9e3b51416f9fcddd35e003fe9bdc96af6fdf5f16073dd07eec52368fbc79b6b61ddd6d&v=5.103
     */
    @Override
    @LogMethodCall
    @CallPerSecond(type = GROUP)
    public Mono<ScreenNameJson> resolveScreenName(@NonNull final String screenName) {
        return params("screen_name", screenName)
                .map(params -> putVersion(putGroupToken(params)))
                .flatMap(params -> get(RESOLVE_SCREEN_NAME, params))
                .flatMap(response -> parseResponseToSingleObject(response, ScreenNameJson.class, ApiException.class))
                .doOnError(ExUtils::isWebClientResponseEx, LogUtils::logRespEx)
                .doOnError(ExUtils::isApiEx, LogUtils::logApiEx)
                .retry(RETRY_COUNT_API_TOO_MANY_REQUEST_EX, ExUtils::isTooManyRequestEx)
                .retry(RETRY_COUNT_WEB_CLIENT_RESPONSE_EX, ExUtils::isWebClientResponseEx);
    }

    private Mono<ClientResponse> get(final Method method,
                                     final MultiValueMap<String, String> params) {
        return client
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme(methodScheme)
                        .host(methodHost)
                        .path(format("%s%s", methodPath, method.getMethod()))
                        .queryParams(params)
                        .build())
                .accept(APPLICATION_JSON)
                .exchange();
    }

    private Mono<ClientResponse> execGet(final String code,
                                         final MultiValueMap<String, String> params) {
        return client
                .get()
                .uri(uriBuilder -> {
                    val url = uriBuilder
                            .scheme(methodScheme)
                            .host(methodHost)
                            .path(format("%s%s", methodPath, EXECUTE.getMethod()))
                            .queryParams(params)
                            .queryParam("code", "CODE")
                            .build()
                            .toString();
                    return URI.create(url.replace(
                            "CODE",
                            URLEncoder.encode(code, Charset.defaultCharset())));
                })
                .accept(APPLICATION_JSON)
                .exchange();
    }

    private Mono<ClientResponse> authGet(final MultiValueMap<String, String> params) {
        return Mono.error(UnsupportedOperationException::new);
    }


    private Mono<String> bodyToString(final ClientResponse response) {
        return fromCallable(
                () -> {
                    val json = response
                            .bodyToMono(ByteArrayResource.class)
                            .map(ByteArrayResource::getByteArray)
                            .map(String::new)
                            .block();
                    System.out.println(json);
                    return json;
                });
    }


    @SuppressWarnings("unchecked")
    private <R extends JsonMarker,
            E extends RuntimeException> Mono<R> parseResponseToSingleObject(
            final ClientResponse response,
            final Class<R> responseClass,
            final Class<E> errorClass) {
        return bodyToString(response)
                .flatMap(json -> {
                    if (isRespBody(json)) {
                        return parseResponseToSingleObject(json, responseClass);
                    } else {
                        return isErrorBody(json)
                                ? (Mono<R>) throwSingleError(json, errorClass)
                                : Mono.error(InvalidJsonException::new);
                    }
                });
    }

    private <R extends JsonMarker> Mono<R> parseResponseToSingleObject(
            final String json,
            final Class<R> responseClass) {
        return fromCallable(
                (Callable<SingleObject<R>>) () ->
                        jsonMapper.readValue(
                                json,
                                typeFactory.constructParametricType(SINGLE_OBJECT_CLASS, responseClass)
                        ))
                .flatMap(body -> body.getResponse() != null
                        ? Mono.just(body.getResponse())
                        : Mono.empty());
    }


    @SuppressWarnings("unchecked")
    private <R extends JsonMarker,
            E extends RuntimeException> Mono<List<R>> parseResponseToArrayObject(
            final ClientResponse response,
            final Class<R> responseClass,
            final Class<E> errorClass) {
        return bodyToString(response)
                .flatMap(json -> {
                    if (isRespBody(json)) {
                        return parseResponseToArrayObject(json, responseClass);
                    } else {
                        return isErrorBody(json)
                                ? (Mono<List<R>>) throwSingleError(json, errorClass)
                                : Mono.error(InvalidJsonException::new);
                    }
                });
    }

    private <R extends JsonMarker> Mono<List<R>> parseResponseToArrayObject(
            final String json,
            final Class<R> responseClass) {
        return fromCallable(
                (Callable<ArrayObject<R>>) () ->
                        jsonMapper.readValue(
                                json,
                                typeFactory.constructParametricType(ARRAY_OBJECT_CLASS, responseClass)
                        ))
                .flatMap(body -> body.getResponse() != null
                        ? Mono.just(Arrays.asList(body.getResponse()))
                        : Mono.empty());
    }


    @SuppressWarnings("unchecked")
    private <R extends JsonMarker,
            E extends RuntimeException> Mono<Tuple2<Integer, List<R>>> parseResponseToItemsObject(
            final ClientResponse response,
            final Class<R> responseClass,
            final Class<E> errorClass) {
        return bodyToString(response)
                .flatMap(json -> {
                    if (isRespBody(json)) {
                        return parseResponseToItemsObject(json, responseClass);
                    } else {
                        return isErrorBody(json)
                                ? (Mono<Tuple2<Integer, List<R>>>) throwSingleError(json, errorClass)
                                : Mono.error(InvalidJsonException::new);
                    }
                });
    }

    private <R extends JsonMarker> Mono<Tuple2<Integer, List<R>>> parseResponseToItemsObject(
            final String json,
            final Class<R> responseClass) {
        return fromCallable(
                (Callable<SingleObject<ItemsObject<R>>>) () ->
                        jsonMapper.readValue(
                                json,
                                typeFactory.constructParametricType(
                                        SINGLE_OBJECT_CLASS,
                                        typeFactory.constructParametricType(ITEMS_OBJECT_CLASS, responseClass)
                                )
                        ))
                .flatMap(body -> body.getResponse().getItems() != null
                        ? Mono.just(Tuples.of(body.getResponse().getCount(), Arrays.asList(body.getResponse().getItems())))
                        : Mono.empty());
    }

//    @SuppressWarnings("unchecked")
//    private <R extends JsonMarker,
//            E extends RuntimeException> Mono<List<Tuple2<Integer, List<R>>>> parseResponseToArrayObjectWithItems(
//            final ClientResponse response,
//            final Class<R> responseClass,
//            final Class<E> errorClass) {
//        return bodyToString(response)
//                .flatMap(json -> {
//                    if (isRespBody(json)) {
//                       return parseResponseToArrayObjectWithItems(json, responseClass);
//                    } else {
//                        return isErrorBody(json)
//                                ? (Mono<List<Tuple2<Integer, List<R>>>>) throwSingleError(json, errorClass)
//                                : Mono.error(InvalidJsonException::new);
//                    }
//                });
//    }
//
//    private <R extends JsonMarker> Mono<List<Tuple2<Integer, List<R>>>> parseResponseToArrayObjectWithItems(
//            final String json,
//            final Class<R> responseClass) {
//        System.out.println(json);
//        return fromCallableOnVkSch(
//                (Callable<ArrayObject<ItemsObject<R>>>) () ->
//                        jsonMapper.readValue(
//                                json,
//                                typeFactory.constructParametricType(
//                                        ARRAY_OBJECT_CLASS,
//                                        typeFactory.constructParametricType(ITEMS_OBJECT_CLASS, responseClass)
//                                )
//                        ))
//                .flatMap(body -> {
//                    if (body.getResponse() != null) {
//                        val result = Arrays.stream(body.getResponse())
//                                .map(obj -> Tuples.of(obj.getCount(), Arrays.asList(obj.getItems())))
//                                .collect(Collectors.toList());
//                        return Mono.just(result);
//                    } else return Mono.empty();
//                });
//    }

    private <E extends RuntimeException> Mono<?> throwSingleError(final String json,
                                                                  final Class<E> errorClass) {
        return fromCallable(
                (Callable<SingleError<E>>) () ->
                        jsonMapper.readValue(json, typeFactory.constructParametricType(SINGLE_ERROR_CLASS, errorClass)))
                .flatMap(body -> Mono.error(body.getError()));
    }

    private MultiValueMap<String, String> putGroupToken(final MultiValueMap<String, String> params) {
        return putToken(params, groupToken);
    }

    private MultiValueMap<String, String> putVersion(final MultiValueMap<String, String> params) {
        params.add("v", version);
        return params;
    }

    private MultiValueMap<String, String> putToken(final MultiValueMap<String, String> params,
                                                   final String accessToken) {
        params.add("access_token", accessToken);
        return params;
    }

    private Mono<MultiValueMap<String, String>> params(final String key1,
                                                       final String value1,
                                                       final String key2,
                                                       final String value2,
                                                       final String key3,
                                                       final String value3,
                                                       final String key4,
                                                       final String value4) {
        return params(key1, value1, key2, value2, key3, value3)
                .map(m -> {
                    m.add(key4, value4);
                    return m;
                });
    }

    private Mono<MultiValueMap<String, String>> params(final String key1,
                                                       final String value1,
                                                       final String key2,
                                                       final String value2,
                                                       final String key3,
                                                       final String value3) {
        return params(key1, value1, key2, value2)
                .map(m -> {
                    m.add(key3, value3);
                    return m;
                });
    }

    private Mono<MultiValueMap<String, String>> params(final String key1,
                                                       final String value1,
                                                       final String key2,
                                                       final String value2) {
        return params(key1, value1)
                .map(m -> {
                    m.add(key2, value2);
                    return m;
                });
    }

    private Mono<MultiValueMap<String, String>> params(final String key,
                                                       final String value) {
        val params = new LinkedMultiValueMap<String, String>();
        params.add(key, value);
        return Mono.just(params);
    }

    private Mono<MultiValueMap<String, String>> params() {
        val params = new LinkedMultiValueMap<String, String>();
        return Mono.just(params);
    }
}
