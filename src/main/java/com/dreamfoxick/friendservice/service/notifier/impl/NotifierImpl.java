package com.dreamfoxick.friendservice.service.notifier.impl;

import com.dreamfoxick.friendservice.configuration.YamlPropertySourceFactory;
import com.dreamfoxick.friendservice.data.mongo.entities.EntityMarker;
import com.dreamfoxick.friendservice.data.mongo.entities.FriendEntity;
import com.dreamfoxick.friendservice.data.mongo.entities.TrackedUserEntity;
import com.dreamfoxick.friendservice.data.mongo.entities.UserEntity;
import com.dreamfoxick.friendservice.service.notifier.Notifier;
import com.dreamfoxick.friendservice.util.annotation.LogMethodCall;
import com.dreamfoxick.friendservice.util.exception.ExUtils;
import com.dreamfoxick.friendservice.util.logging.LogUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import javax.annotation.PostConstruct;

import static java.lang.String.format;
import static org.springframework.http.MediaType.*;

@Slf4j
@Component
@Profile({"dev", "test"})
@PropertySource(value = "classpath:vkbot.yml", factory = YamlPropertySourceFactory.class)
public class NotifierImpl implements Notifier {
    private static final String REMOVE_FRIEND_MESSAGE = "\n[id%d|%s %s] удалил(а) друга: \n" +
            "  1) Ссылка: https://vk.com/id%d \n" +
            "  2) Имя: %s\n" +
            "  3) Фамилия: %s\n" +
            "  4) Пол: %s\n" +
            "  5) %s\n";
    private static final String ADD_FRIEND_MESSAGE = "\n[id%d|%s %s] добавил(а) друга: \n" +
            "  1) Ссылка: https://vk.com/id%d \n" +
            "  2) Имя: %s\n" +
            "  3) Фамилия: %s\n" +
            "  4) Пол: %s\n" +
            "  5) %s\n";
    private static final String DELETE_PAGE_MESSAGE = "\nПользователь: https://vk.com/id%d удалил страничку " +
            "или его заблокировали.Больше не могу за ним наблюдать!(";
    private static final String PRIVATE_PAGE_MESSAGE = "\nПользователь: https://vk.com/id%d закрыл страничку." +
            "Больше не могу за ним наблюдать!(";
    private static final String WRONG_TOKEN_MESSAGE = "\nКажется я больше не могу наблюдать за вашими целями. " +
            "Пожалуйста, перейдите по ссылке: /регистрация и следуйте дальнейшим инструкциям";

    private final Scheduler blockExec;
    private WebClient client;

    @Value("${common.port}")
    private int port;
    @Value("${common.scheme}")
    private String scheme;
    @Value("${common.host}")
    private String host;

    @Value("${paths.remove_friend_path}")
    private String removeFriendPath;
    @Value("${paths.add_friend_path}")
    private String addFriendPath;
    @Value("${paths.delete_page_path}")
    private String deletePagePah;
    @Value("${paths.private_page_path}")
    private String privatePagePath;
    @Value("${paths.wrong_token_path}")
    private String wrongTokenPath;

    @PostConstruct
    private void initialize() {
        client = WebClient.builder().build();
    }

    @Autowired
    public NotifierImpl(@Qualifier("blockingSch") Scheduler blockExec) {
        this.blockExec = blockExec;
    }

    private static String closedToString(final FriendEntity friend) {
        String closed;
        if (friend.isClosed()) {
            closed = "Страничка закрыта";
        } else {
            closed = "Страничка открыта";
        }
        return closed;
    }

    private static String sexToString(final FriendEntity friend) {
        String sex;
        if (friend.getSex() == 0) {
            sex = "не указан";
        } else if (friend.getSex() == 1) {
            sex = "женский";
        } else {
            sex = "мужской";
        }
        return sex;
    }

    @Override
    @LogMethodCall
    public Mono<Void> notifyOfRemovalFriend(@NonNull final UserEntity user,
                                            @NonNull final TrackedUserEntity trc,
                                            @NonNull final FriendEntity friend) {
        val message = format(REMOVE_FRIEND_MESSAGE,
                trc.getID(),
                trc.getFirstName(),
                trc.getLastName(),
                friend.getID(),
                friend.getFirstName(),
                friend.getLastName(),
                sexToString(friend),
                closedToString(friend));
        log.warn(message);
        return notify(user.getID(), trc, friend, removeFriendPath);
    }

    @Override
    @LogMethodCall
    public Mono<Void> notifyOfAdditionFriend(@NonNull final UserEntity user,
                                             @NonNull final TrackedUserEntity trc,
                                             @NonNull final FriendEntity friend) {
        val message = format(ADD_FRIEND_MESSAGE,
                trc.getID(),
                trc.getFirstName(),
                trc.getLastName(),
                friend.getID(),
                friend.getFirstName(),
                friend.getLastName(),
                sexToString(friend),
                closedToString(friend));
        log.warn(message);
        return notify(user.getID(), trc, friend, addFriendPath);
    }

    private Mono<Void> notify(final int ID,
                              final TrackedUserEntity trc,
                              final FriendEntity friend,
                              final String path) {
        return Mono.empty();
    }

    @Override
    @LogMethodCall
    public Mono<Void> notifyOfDeletedPage(@NonNull final UserEntity user,
                                          @NonNull final TrackedUserEntity trc) {
        val message = format(DELETE_PAGE_MESSAGE, trc.getID());
        log.warn(message);
        return notify(user.getID(), trc, deletePagePah);
    }

    @Override
    @LogMethodCall
    public Mono<Void> notifyOfPrivatePage(@NonNull final UserEntity user,
                                          @NonNull final TrackedUserEntity trc) {
        val message = format(PRIVATE_PAGE_MESSAGE, trc.getID());
        log.warn(message);
        return notify(user.getID(), trc, privatePagePath);
    }

    @Override
    @LogMethodCall
    public Mono<Void> notifyOfWrongToken(@NonNull final UserEntity user) {
        log.warn(WRONG_TOKEN_MESSAGE);
        return notify(user.getID(), user, wrongTokenPath);
    }

    private <T extends EntityMarker<Integer>> Mono<Void> notify(final int ID,
                                                                final T json,
                                                                final String path) {
        return client.post()
                .uri(uriBuilder -> uriBuilder
                        .scheme(scheme)
                        .host(host)
                        .port(port)
                        .path(path)
                        .build())
                .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .doOnError(WebClientResponseException.class, LogUtils::logRespEx)
                .retry(3, ExUtils::isWebClientResponseEx)
                .subscribeOn(blockExec)
                .then();
    }
}
