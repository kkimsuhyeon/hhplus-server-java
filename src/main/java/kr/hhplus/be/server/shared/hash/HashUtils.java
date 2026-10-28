package kr.hhplus.be.server.shared.hash;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HashUtils {
    public static String sha256(String value) {
        Objects.requireNonNull(value, "value");

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = value.getBytes(StandardCharsets.UTF_8);

            byte[] hash = digest.digest(bytes);
            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 알고리즘을 사용할 수 없음", e);
        }
    }
}
