package com.dreamfoxick.friendservice.data.mongo.service;

import com.dreamfoxick.friendservice.data.exception.EntityAlreadyExists;
import com.dreamfoxick.friendservice.data.exception.EntityDoesNotExist;
import com.dreamfoxick.friendservice.data.mongo.entities.EntityMarker;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CustomMongoTemplate {
    private final ReactiveMongoTemplate template;

    private static <R> Query byID(R ID) {
        val query = new Query();
        query.addCriteria(Criteria.where("ID").is(ID));
        return query;
    }

    <T extends EntityMarker<R>, R> Mono<T> findByID(@NonNull final R ID,
                                                    @NonNull final Class<T> entityClass,
                                                    @NonNull final CollectionName collection) {
        return template.findOne(
                byID(ID),
                entityClass,
                collection.getValue())
                .switchIfEmpty(Mono.defer(() -> Mono.error(EntityDoesNotExist::new)));
    }

    final <T> Flux<T> findAll(@NonNull final Class<T> entityClass,
                              @NonNull final CollectionName collection) {
        return template.findAll(entityClass, collection.getValue());
    }

    final <T extends EntityMarker<R>, R> Mono<T> add(@NonNull final T object,
                                                     @NonNull final Class<T> entityClass,
                                                     @NonNull final CollectionName collection) {
//        return template
//                .inTransaction()
//                .execute(session -> session
//                        .exists(byID(object.getID()), entityClass, collection.getValue())
//                        .flatMap(exists -> exists
//                                ? Mono.error(EntityAlreadyExists::new)
//                                : session.save(object, collection.getValue())))
//                .next();
        return exists(object.getID(), entityClass, collection)
                .flatMap(exists -> exists
                        ? Mono.error(EntityAlreadyExists::new)
                        : template.save(object, collection.getValue()));
    }

    final <T extends EntityMarker<R>, R> Mono<Boolean> exists(@NonNull final R ID,
                                                              @NonNull final Class<T> entityClass,
                                                              @NonNull final CollectionName collection) {
        return template.exists(
                byID(ID),
                entityClass,
                collection.getValue());
    }

    final <T extends EntityMarker<R>, R> Mono<UpdateResult> update(@NonNull final T object,
                                                                   @NonNull final Update fields,
                                                                   @NonNull final Class<T> entityClass,
                                                                   @NonNull final CollectionName collection) {
//        return template
//                .inTransaction()
//                .execute(session -> session
//                        .exists(byID(object.getID()), entityClass, collection.getValue())
//                        .flatMap(exists -> {
//                            if (exists) {
//                                return describeObject(object)
//                                        .flatMap(fields -> session.updateFirst(
//                                                byID(object.getID()),
//                                                fields,
//                                                entityClass,
//                                                collection.getValue()));
//                            } else return Mono.error(EntityDoesNotExist::new);
//                        }))
//                .next();

        return exists(object.getID(), entityClass, collection)
                .flatMap(exists -> {
                    if (exists) {
                        return template.updateFirst(
                                byID(object.getID()),
                                fields,
                                entityClass,
                                collection.getValue());
                    } else return Mono.error(EntityDoesNotExist::new);
                });
    }

    final <T extends EntityMarker<R>, R> Mono<DeleteResult> remove(@NonNull final R ID,
                                                                   @NonNull final Class<T> entityClass,
                                                                   @NonNull final CollectionName collection) {
//        return template
//                .inTransaction()
//                .execute(session -> session.remove(
//                        byID(ID),
//                        entityClass,
//                        collection.getValue()))
//                .next();
        return template.remove(byID(ID), entityClass, collection.getValue());
    }

    final <T extends EntityMarker<R>, Z extends EntityMarker<R>, R> Mono<UpdateResult> addObjectToCollection(
            @NonNull final T updateObject,
            @NonNull final Class<T> entityClass,
            @NonNull final List<Z> entities,
            @NonNull final Z addedObject,
            @NonNull final CollectionName collection,
            @NonNull final String entityCollectionName) {
        val ids = entities.stream()
                .map(EntityMarker::getID)
                .collect(Collectors.toUnmodifiableList());
        if (ids.contains(addedObject.getID())) return Mono.error(EntityAlreadyExists::new);
        else {
            val fields = new Update();
            entities.add(addedObject);
            fields.set(entityCollectionName, entities);
            return update(updateObject, fields, entityClass, collection);
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    final <T extends EntityMarker<R>, Z extends EntityMarker<R>, R> Mono<UpdateResult> removeObjectFromCollection(
            @NonNull final T updateObject,
            @NonNull final Class<T> entityClass,
            @NonNull final List<Z> entities,
            @NonNull final R deleteObjectID,
            @NonNull final CollectionName collection,
            @NonNull final String entityCollectionName) {
        val ids = entities.stream()
                .map(EntityMarker::getID)
                .collect(Collectors.toUnmodifiableList());
        if (entities.isEmpty()) return Mono.error(EntityDoesNotExist::new);
        else if (!ids.contains(deleteObjectID)) return Mono.error(EntityDoesNotExist::new);
        else {
            val fields = new Update();
            val deletedObject = entities.stream()
                    .filter(e -> e.getID().equals(deleteObjectID))
                    .findFirst()
                    .get();
            entities.remove(deletedObject);
            fields.set(entityCollectionName, entities);
            return update(updateObject, fields, entityClass, collection);
        }
    }
}
