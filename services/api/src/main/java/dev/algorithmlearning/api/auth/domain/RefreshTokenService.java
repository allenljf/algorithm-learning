package dev.algorithmlearning.api.auth.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** Creates opaque refresh values and derives only a keyed server-side hash for storage. */
public final class RefreshTokenService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final byte[] key;

    public RefreshTokenService(String keyMaterial) {
        this.key = keyMaterial.getBytes(StandardCharsets.UTF_8);
    }

    public String newToken() {
        var bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public byte[] hash(String token) {
        try {
            var mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(token.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to hash refresh token", exception);
        }
    }

    public boolean matches(String token, byte[] expectedHash) {
        return MessageDigest.isEqual(hash(token), expectedHash);
    }
}
