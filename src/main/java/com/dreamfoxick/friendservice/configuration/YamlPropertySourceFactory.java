package com.dreamfoxick.friendservice.configuration;

import lombok.val;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;

public class YamlPropertySourceFactory extends DefaultPropertySourceFactory {

    @Override
    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    public PropertySource<?> createPropertySource(String name,
                                                  EncodedResource resource) throws IOException {
        if (resource == null) {
            return super.createPropertySource(name, resource);
        } else {
            val list = new YamlPropertySourceLoader()
                    .load(resource.getResource().getFilename(), resource.getResource());
            if (!list.isEmpty()) {
                return list.iterator().next();
            }
            return super.createPropertySource(name, resource);
        }
    }
}
