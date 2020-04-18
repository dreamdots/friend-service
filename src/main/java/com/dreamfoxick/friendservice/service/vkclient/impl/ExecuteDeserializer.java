package com.dreamfoxick.friendservice.service.vkclient.impl;

import com.dreamfoxick.friendservice.service.vkclient.json.common.ExecuteObject;
import com.dreamfoxick.friendservice.service.vkclient.json.common.JsonMarker;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.val;

import java.io.IOException;

public class ExecuteDeserializer<R extends JsonMarker, E extends RuntimeException> extends StdDeserializer<ExecuteObject<E>> {

    protected ExecuteDeserializer() {
        this((Class<?>) null);
    }

    protected ExecuteDeserializer(Class<?> vc) {
        super(vc);
    }

    protected ExecuteDeserializer(JavaType valueType) {
        super(valueType);
    }

    protected ExecuteDeserializer(StdDeserializer<?> src) {
        super(src);
    }

    @Override
    public ExecuteObject<E> deserialize(final JsonParser parser,
                                        final DeserializationContext context) throws IOException, JsonProcessingException {
        ExecuteObject<E> obj = new ExecuteObject<>();
        val codec = parser.getCodec();

        val node = codec.readTree(parser);
        val resp = node.get("response");
        System.out.println(resp);

        return null;
    }
}
