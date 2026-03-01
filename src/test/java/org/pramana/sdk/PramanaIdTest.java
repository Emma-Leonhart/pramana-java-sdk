package org.pramana.sdk;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class PramanaIdTest {

    @Test
    void testNamespaceIsCorrect() {
        assertEquals("a6613321-e9f6-4348-8f8b-29d2a3c86349", PramanaId.NUM_NAMESPACE.toString());
    }

    @Test
    void testUuidV5Deterministic() {
        UUID id1 = PramanaId.uuidV5(PramanaId.NUM_NAMESPACE, "1,1,0,1");
        UUID id2 = PramanaId.uuidV5(PramanaId.NUM_NAMESPACE, "1,1,0,1");
        assertEquals(id1, id2);
    }

    @Test
    void testUuidV5Version() {
        UUID id = PramanaId.uuidV5(PramanaId.NUM_NAMESPACE, "1,1,0,1");
        assertEquals(5, id.version());
    }

    @Test
    void testUuidV5Variant() {
        UUID id = PramanaId.uuidV5(PramanaId.NUM_NAMESPACE, "1,1,0,1");
        assertEquals(2, id.variant());
    }

    @Test
    void testDifferentInputsDifferentIds() {
        UUID id1 = PramanaId.forNumber("1,1,0,1");
        UUID id2 = PramanaId.forNumber("2,1,0,1");
        assertNotEquals(id1, id2);
    }

    @Test
    void testEntityUrl() {
        UUID id = PramanaId.forNumber("1,1,0,1");
        String url = PramanaId.entityUrl(id);
        assertTrue(url.startsWith("https://pramana.dev/entity/"));
    }

    @Test
    void testLabel() {
        assertEquals("pra:num:1,1,0,1", PramanaId.label("1,1,0,1"));
    }
}
