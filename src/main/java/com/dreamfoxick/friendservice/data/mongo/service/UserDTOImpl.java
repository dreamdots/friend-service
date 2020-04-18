package com.dreamfoxick.friendservice.data.mongo.service;

import com.dreamfoxick.friendservice.data.mongo.entities.TrackedUserEntity;
import com.dreamfoxick.friendservice.data.mongo.entities.UserEntity;
import com.dreamfoxick.friendservice.util.annotation.LogMethodCall;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Profile({"dev", "test"})
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserDTOImpl implements UserDTO {
    private static final Class<UserEntity> ENTITY_CLASS = UserEntity.class;
    private final CustomMongoTemplate DAO;

    private static Update allFields(final UserEntity userEntity) {
        Update fields = new Update();
        fields.set("first_name", userEntity.getFirstName());
        fields.set("last_name", userEntity.getLastName());
        fields.set("sex", userEntity.getSex());
        fields.set("tracked_users", userEntity.getTrackedUsers());
        fields.set("token", userEntity.getToken());
        return fields;
    }

    @Override
    @LogMethodCall
    public Mono<UserEntity> findByID(final int ID) {
        return DAO.findByID(ID, ENTITY_CLASS, CollectionName.USERS);
    }

    @Override
    @LogMethodCall
    public Flux<UserEntity> findAll() {
        return DAO.findAll(ENTITY_CLASS, CollectionName.USERS);
    }

    @Override
    @LogMethodCall
    public Mono<UserEntity> add(@NonNull final UserEntity userEntity) {
        return DAO.add(userEntity, ENTITY_CLASS, CollectionName.USERS);
    }

    @Override
    @LogMethodCall
    public Mono<UserEntity> update(@NonNull final UserEntity userEntity) {
        return DAO.update(userEntity, allFields(userEntity), ENTITY_CLASS, CollectionName.USERS)
                .map(ignore -> userEntity);
    }

    @Override
    @LogMethodCall
    public Mono<Boolean> exists(final int ID) {
        return DAO.exists(ID, ENTITY_CLASS, CollectionName.USERS);
    }


    @Override
    @LogMethodCall
    public Mono<TrackedUserEntity> addTrackedLink(final int ID,
                                                        @NonNull final TrackedUserEntity trackedUserEntity) {
        return findByID(ID)
                .flatMap(u -> DAO.addObjectToCollection(
                        u,
                        ENTITY_CLASS,
                        u.getTrackedUsers(),
                        trackedUserEntity,
                        CollectionName.USERS,
                        "tracked_users"))
                .map(ignore -> trackedUserEntity);
    }

    @Override
    @LogMethodCall
    public Mono<Boolean> remove(final int ID) {
        return DAO.remove(ID, ENTITY_CLASS, CollectionName.USERS)
                .map(DeleteResult::wasAcknowledged);
    }

    @Override
    @LogMethodCall
    public Mono<Boolean> removeTrackedLink(final int ID,
                                                 final int trackedID) {
        return findByID(ID)
                .flatMap(u -> DAO.removeObjectFromCollection(
                        u,
                        ENTITY_CLASS,
                        u.getTrackedUsers(),
                        trackedID,
                        CollectionName.USERS,
                        "tracked_users"))
                .map(UpdateResult::wasAcknowledged);
    }
}
