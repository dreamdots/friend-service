package com.dreamfoxick.friendservice.web.controller;

import com.dreamfoxick.friendservice.data.mongo.service.UserDTO;
import com.dreamfoxick.friendservice.data.mongo.entities.TrackedUserEntity;
import com.dreamfoxick.friendservice.service.adapter.JsonAdapter;
import com.dreamfoxick.friendservice.service.extractor.impl.IdentifierExtractorImpl;
import com.dreamfoxick.friendservice.service.vkclient.VkAPI;
import com.dreamfoxick.friendservice.util.annotation.LogMethodCall;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Slf4j
@RestController
@Profile({"dev", "test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TrackedController {
    private final UserDTO userDTO;
    private final JsonAdapter adapter;
    private final VkAPI vkAPI;

    @LogMethodCall
    @RequestMapping(value = "/tracked/alive", method = GET)
    public Mono<String> alive() {
        return Mono.just("Ok");
    }

    @LogMethodCall
    @RequestMapping(value = "/tracked/register", method = POST)
    public Mono<TrackedUserEntity> registerTrackedLink(
            @RequestParam(name = "ID") final int ID,
            @RequestParam(name = "tracked_link") final String trackedLink) {
        val extractor = new IdentifierExtractorImpl(trackedLink);
        val trackedID = extractor.getID();
        val trackedScreenName = extractor.getScreenName();

        if (extractor.isIntID()) {
            return vkAPI.usersGet(Collections.singletonList(trackedID))
                    .map(list -> list.get(0))
                    .map(adapter::toTracked)
                    .flatMap(o -> userDTO.addTrackedLink(ID, o));
        } else {
            return vkAPI.resolveScreenName(trackedScreenName)
                    .flatMap(j -> vkAPI.usersGet(Collections.singletonList(j.getId())))
                    .map(list -> list.get(0))
                    .map(adapter::toTracked)
                    .flatMap(o -> userDTO.addTrackedLink(ID, o));
        }
    }

    @LogMethodCall
    @RequestMapping(value = "/tracked/remove", method = DELETE)
    public Mono<Boolean> removeTrackedLink(
            @RequestParam(name = "ID") final int ID,
            @RequestParam(name = "tracked_link") final int trackedId) {
        return userDTO.removeTrackedLink(ID, trackedId);
    }
}
