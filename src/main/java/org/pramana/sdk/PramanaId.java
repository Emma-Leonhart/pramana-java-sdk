package org.pramana.sdk;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Deterministic UUID v5 generation for the Pramana knowledge graph.
 */
public final class PramanaId {

    /** Pramana namespace UUID for number pseudo-class identifiers. */
    public static final UUID NUM_NAMESPACE =
            UUID.fromString("a6613321-e9f6-4348-8f8b-29d2a3c86349");

    /** Base URL for Pramana entities. */
    public static final String ENTITY_BASE_URL = "https://pramana-data.ca/entity/";

    private PramanaId() {
        // utility class
    }

    /**
     * Generates a UUID v5 (name-based, SHA-1) per RFC 4122.
     *
     * @param namespace the namespace UUID
     * @param name      the name to hash
     * @return a deterministic UUID v5
     */
    public static UUID uuidV5(UUID namespace, String name) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(toBytes(namespace));
            md.update(name.getBytes(StandardCharsets.UTF_8));
            byte[] hash = md.digest();

            // Set version 5
            hash[6] = (byte) ((hash[6] & 0x0F) | 0x50);
            // Set variant to IETF (10xx)
            hash[8] = (byte) ((hash[8] & 0x3F) | 0x80);

            return fromBytes(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 not available", e);
        }
    }

    /**
     * Generates a Pramana ID for a number with canonical key "a,b,c,d".
     * The key is wrapped in curly braces before hashing to match the
     * Python SDK convention: uuid5(namespace, "{a,b,c,d}").
     */
    public static UUID forNumber(String canonicalKey) {
        return uuidV5(NUM_NAMESPACE, "{" + canonicalKey + "}");
    }

    /**
     * Returns the Pramana entity URL for the given UUID.
     */
    public static String entityUrl(UUID id) {
        return ENTITY_BASE_URL + id;
    }

    /**
     * Returns the Pramana label string "pra:num:key".
     */
    public static String label(String canonicalKey) {
        return "pra:num:" + canonicalKey;
    }

    /**
     * Converts a UUID to its big-endian byte representation (RFC 4122 layout).
     */
    private static byte[] toBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    /**
     * Constructs a UUID from the first 16 bytes of a hash.
     */
    private static UUID fromBytes(byte[] hash) {
        ByteBuffer bb = ByteBuffer.wrap(hash, 0, 16);
        long msb = bb.getLong();
        long lsb = bb.getLong();
        return new UUID(msb, lsb);
    }
}
