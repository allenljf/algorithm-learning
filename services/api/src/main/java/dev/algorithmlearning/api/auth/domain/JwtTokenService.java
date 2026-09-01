package dev.algorithmlearning.api.auth.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** Minimal HMAC-SHA256 JWT issuer/verifier used only at the HTTP security boundary. */
public final class JwtTokenService {
    private final byte[] key;
    private final String issuer;
    private final String audience;
    private final Clock clock;

    public JwtTokenService(String key, String issuer, String audience, Clock clock) {
        this.key = key.getBytes(StandardCharsets.UTF_8);
        this.issuer = issuer;
        this.audience = audience;
        this.clock = clock;
    }

    public String issue(UUID userId) {
        var now = clock.instant();
        var header = encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        var payload = encode("{\"sub\":\"" + userId + "\",\"iss\":\"" + issuer + "\",\"aud\":\"" + audience
                + "\",\"iat\":" + now.getEpochSecond() + ",\"exp\":" + now.plusSeconds(900).getEpochSecond()
                + ",\"jti\":\"" + UUID.randomUUID() + "\"}");
        var signed = header + "." + payload;
        return signed + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(hmac(signed));
    }

    public UUID authenticate(String token) {
        var parts = token.split("\\.");
        if (parts.length != 3 || !MessageDigest.isEqual(hmac(parts[0] + "." + parts[1]), decode(parts[2]))) {
            throw new IllegalArgumentException("Invalid access token");
        }
        var payload = new String(decode(parts[1]), StandardCharsets.UTF_8);
        if (!payload.contains("\"iss\":\"" + issuer + "\"") || !payload.contains("\"aud\":\"" + audience + "\"")) {
            throw new IllegalArgumentException("Invalid access token");
        }
        var expiry = Long.parseLong(value(payload, "exp"));
        if (!clock.instant().isBefore(Instant.ofEpochSecond(expiry))) throw new IllegalArgumentException("Expired access token");
        return UUID.fromString(value(payload, "sub"));
    }

    public Instant expiresAt(String token) {
        var payload = new String(decode(token.split("\\.")[1]), StandardCharsets.UTF_8);
        return Instant.ofEpochSecond(Long.parseLong(value(payload, "exp")));
    }

    private String value(String json, String name) {
        var match = java.util.regex.Pattern.compile("\\\"" + name + "\\\":(?:\\\"([^\\\"]+)\\\"|(\\d+))").matcher(json);
        if (!match.find()) throw new IllegalArgumentException("Invalid access token");
        return match.group(1) != null ? match.group(1) : match.group(2);
    }
    private String encode(String value) { return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8)); }
    private byte[] decode(String value) { return Base64.getUrlDecoder().decode(value); }
    private byte[] hmac(String value) {
        try { var mac = Mac.getInstance("HmacSHA256"); mac.init(new SecretKeySpec(key, "HmacSHA256")); return mac.doFinal(value.getBytes(StandardCharsets.UTF_8)); }
        catch (Exception e) { throw new IllegalStateException("Unable to sign JWT", e); }
    }
}
