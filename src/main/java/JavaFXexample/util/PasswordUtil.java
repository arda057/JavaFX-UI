package JavaFXexample.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {
    public static final int ITERATIONS = 600_000;
    private static final int LEGACY_ITERATIONS = 65_536;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String HASH_PREFIX = "pbkdf2";
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static String generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt) {
        return encodeHash(ITERATIONS, pbkdf2(password, salt, ITERATIONS));
    }

    public static boolean verifyPassword(String password, String salt, String expectedHash) {
        if (expectedHash == null || expectedHash.isBlank()) {
            return false;
        }

        ParsedHash parsed = parseHash(expectedHash);
        byte[] actual = pbkdf2(password, salt, parsed.iterations);
        return MessageDigest.isEqual(actual, parsed.hash);
    }

    public static boolean needsRehash(String storedHash) {
        return parseHash(storedHash).iterations < ITERATIONS;
    }

    private static byte[] pbkdf2(String password, String salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                Base64.getDecoder().decode(salt),
                iterations,
                KEY_LENGTH);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Password hashing failed", e);
        } finally {
            spec.clearPassword();
        }
    }

    private static String encodeHash(int iterations, byte[] hash) {
        return HASH_PREFIX + "$" + iterations + "$" + Base64.getEncoder().encodeToString(hash);
    }

    private static ParsedHash parseHash(String storedHash) {
        String[] parts = storedHash.split("\\$", 3);
        if (parts.length == 3 && HASH_PREFIX.equals(parts[0])) {
            try {
                int iterations = Integer.parseInt(parts[1]);
                return new ParsedHash(iterations, Base64.getDecoder().decode(parts[2]));
            } catch (IllegalArgumentException ignored) {
                return new ParsedHash(LEGACY_ITERATIONS, new byte[0]);
            }
        }
        try {
            return new ParsedHash(LEGACY_ITERATIONS, Base64.getDecoder().decode(storedHash));
        } catch (IllegalArgumentException e) {
            return new ParsedHash(LEGACY_ITERATIONS, new byte[0]);
        }
    }

    private record ParsedHash(int iterations, byte[] hash) {
    }
}
