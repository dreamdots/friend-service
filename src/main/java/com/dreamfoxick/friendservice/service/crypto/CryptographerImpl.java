package com.dreamfoxick.friendservice.service.crypto;

import com.dreamfoxick.friendservice.configuration.YamlPropertySourceFactory;
import com.dreamfoxick.friendservice.data.mongo.entities.TokenEntity;
import com.dreamfoxick.friendservice.service.crypto.Cryptographer;
import com.dreamfoxick.friendservice.util.annotation.LogMethodCall;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

import static java.lang.String.format;

@Slf4j
@Component
@Profile({"dev", "test"})
@PropertySource(value = "classpath:crypto.yml", factory = YamlPropertySourceFactory.class)
public class CryptographerImpl implements Cryptographer {

    @Value("${password}")
    private String cryptoPassword;

    private static byte[] saltToByte(final String salt) {
        return salt.getBytes();
    }

    private static String saltToString(final byte[] salt) {
        return new String(salt, Charset.defaultCharset());
    }

    @Override
    @LogMethodCall
    public TokenEntity encode(@NonNull String input) {
        val salt = KeyGenerators.string().generateKey();
        val encryptor = Encryptors.text(cryptoPassword, salt);
        val userToken = new TokenEntity();
        val tokenAfterEncode = encryptor.encrypt(input);
        userToken.setSalt(saltToByte(salt));
        userToken.setToken(tokenAfterEncode);
        return userToken;
    }

    @Override
    @LogMethodCall
    public String decode(@NonNull TokenEntity token) {
        val salt = token.getSalt();
        val tokenBeforeDecode = token.getToken();
        val encryptor = Encryptors.text(cryptoPassword, saltToString(salt));
        return encryptor.decrypt(tokenBeforeDecode);
    }

    @Override
    @LogMethodCall
    public Mono<TokenEntity> encode(@NonNull final Mono<String> input) {
        return input.map(this::encode);
    }

    @Override
    @LogMethodCall
    public Mono<String> decode(@NonNull final Mono<TokenEntity> token) {
        return token.map(this::decode);
    }
}
