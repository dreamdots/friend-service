package com.dreamfoxick.friendservice.web.controller;

import com.dreamfoxick.friendservice.configuration.YamlPropertySourceFactory;
import com.dreamfoxick.friendservice.data.mongo.service.UserDTO;
import com.dreamfoxick.friendservice.data.mongo.entities.UserEntity;
import com.dreamfoxick.friendservice.service.crypto.Cryptographer;
import com.dreamfoxick.friendservice.service.vkclient.VkAPI;
import com.dreamfoxick.friendservice.util.annotation.LogMethodCall;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
@Profile({"dev", "test"})
@PropertySource(value = "classpath:vkapi.yml", factory = YamlPropertySourceFactory.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TokenController {
    private final Cryptographer cryptographer;
    private final UserDTO userDTO;
    private final VkAPI vkAPI;

    @Value("${common.version}")
    private String version;
    @Value("${common.client_ID}")
    private String clientID;
    @Value("${common.client_secret}")
    private String clientSecret;

    @Value("${auth_call_params.scheme}")
    private String authScheme;
    @Value("${auth_call_params.host}")
    private String authHost;
    @Value("${auth_call_params.redirect_uri}")
    private String authRedirectUri;

    @Value("${auth_call_params.auth_path}")
    private String authPath;
    @Value("${auth_call_params.display}")
    private String authDisplay;
    @Value("${auth_call_params.scope}")
    private String authScope;
    @Value("${auth_call_params.response_type}")
    private String authResponseType;

    @Value("${auth_call_params.get_token_path}")
    private String getTokenPath;

    @LogMethodCall
    @RequestMapping(value = "/token/alive", method = GET)
    public Mono<String> alive() {
        return Mono.just("Ok");
    }

    @LogMethodCall
    @RequestMapping(value = "/token/authorization/tokenlink", method = GET)
    public Mono<String> authorizationLink() {
        return Mono.just(UriComponentsBuilder.newInstance()
                .scheme(authScheme)
                .host(authHost)
                .path(authPath)
                .queryParam("client_id", clientID)
                .queryParam("display", authDisplay)
                .queryParam("scope", authScope)
                .queryParam("redirect_uri", authRedirectUri)
                .queryParam("v", version)
                .queryParam("response_type", authResponseType)
                .build()
                .toUri()
                .toString());
    }

    @LogMethodCall
    @RequestMapping(value = "/token/authorization/codelink", method = GET)
    public Mono<String> codeLink() {
        return Mono.just(UriComponentsBuilder.newInstance()
                .scheme(authScheme)
                .host(authHost)
                .path(getTokenPath)
                .queryParam("client_id", clientID)
                .queryParam("client_secret", clientSecret)
                .queryParam("redirect_url", authRedirectUri)
                .queryParam("code", "INSERT")
                .build()
                .toUri()
                .toString());
    }

    @LogMethodCall
    @RequestMapping(value = "/token/authorization/register", method = POST)
    public Mono<UserEntity> registerToken(
            @RequestParam(value = "ID") final int ID,
            @RequestParam(value = "token") final String token) {
        return userDTO.findByID(ID)
                .flatMap(u -> {
                    u.setToken(cryptographer.encode(token));
                    return userDTO.update(u);
                });
    }

    @LogMethodCall
    @RequestMapping(value = "/token/authorization/compute")
    public Mono<Void> computeTokenFromCode(
            @RequestParam(value = "code") final String code) {
        return Mono.error(UnsupportedOperationException::new);
    }
}
