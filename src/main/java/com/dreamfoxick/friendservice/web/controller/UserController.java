package com.dreamfoxick.friendservice.web.controller;


import com.dreamfoxick.friendservice.data.mongo.service.UserDTO;
import com.dreamfoxick.friendservice.data.mongo.entities.UserEntity;
import com.dreamfoxick.friendservice.service.adapter.JsonAdapter;
import com.dreamfoxick.friendservice.service.crypto.Cryptographer;
import com.dreamfoxick.friendservice.service.extractor.impl.IdentifierExtractorImpl;
import com.dreamfoxick.friendservice.service.vkclient.VkAPI;
import com.dreamfoxick.friendservice.util.annotation.LogMethodCall;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static java.lang.String.format;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Slf4j
@RestController
@Profile({"dev", "test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {
    private final Cryptographer cryptographer;
    private final UserDTO userDTO;
    private final JsonAdapter adapter;
    private final VkAPI vkAPI;

    @LogMethodCall
    @RequestMapping(value = "/user/alive", method = GET)
    public Mono<String> alive() {
        return Mono.just("Ok");
    }

    @LogMethodCall
    @RequestMapping(value = "/user/register", method = POST)
    public Mono<UserEntity> registerUser(@RequestParam(name = "link") final String link) {
        val extractor = new IdentifierExtractorImpl(link);
        val ID = extractor.getID();
        val screenName = extractor.getScreenName();
        if (extractor.isIntID()) {
            return Mono.just(Collections.singletonList(ID))
                    .flatMap(vkAPI::usersGet)
                    .switchIfEmpty(Mono.error(
                            new IllegalArgumentException(String.format("User with ID: %d does not exist", ID))))
                    .map(list -> list.get(0))
                    .map(adapter::toUser)
                    .flatMap(userDTO::add);
        } else {
            return Mono.just(screenName)
                    .flatMap(vkAPI::resolveScreenName)
                    .switchIfEmpty(Mono.error(
                            new IllegalArgumentException(String.format("User with nickname: %s does not exist", screenName))))
                    .flatMap(j -> vkAPI.usersGet(Collections.singletonList(j.getId())))
                    .map(list -> list.get(0))
                    .map(adapter::toUser)
                    .flatMap(userDTO::add);
        }
    }

    @LogMethodCall
    @RequestMapping(value = "/user/find/{ID}", method = GET)
    public Mono<UserEntity> findUser(@PathVariable(value = "ID") final int ID) {
        return userDTO.findByID(ID);
    }

    @LogMethodCall
    @RequestMapping(value = "/user/findAll", method = GET)
    public Flux<UserEntity> findAllUsers() {
        return userDTO.findAll();
    }

    @LogMethodCall
    @RequestMapping(value = "/user/remove/{ID}", method = DELETE)
    public Mono<String> removeUser(@PathVariable(value = "ID") final int ID) {
        return userDTO
                .remove(ID)
                .then(Mono.just(format("User with the ID: %s removed", ID)));
    }
}
