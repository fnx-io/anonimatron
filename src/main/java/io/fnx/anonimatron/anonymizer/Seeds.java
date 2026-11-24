package io.fnx.anonimatron.anonymizer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Random;

/**
 * Derives a {@link Random} from HMAC-SHA256(salt, domain + value + attempt). The same salt and
 * value give the same sequence on every run and JVM ({@link Random} has a specified algorithm),
 * while the original value cannot be recovered without the salt.
 */
final class Seeds {

    private static final String ALGORITHM = "HmacSHA256";

    private final String salt;
    private final SecretKeySpec key;

    Seeds(String salt) {
        if (salt == null || salt.isEmpty()) {
            throw new IllegalArgumentException("Deterministic anonymizers need a non-empty salt.");
        }
        this.salt = salt;
        this.key = new SecretKeySpec(salt.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    }

    String salt() {
        return salt;
    }

    Random random(String domain, String value, int attempt) {
        byte[] message = (domain + '\u0000' + value + '\u0000' + attempt).getBytes(StandardCharsets.UTF_8);
        return new Random(ByteBuffer.wrap(hmac(message)).getLong());
    }

    private byte[] hmac(byte[] message) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(key);
            return mac.doFinal(message);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Cannot compute " + ALGORITHM, e);
        }
    }
}
