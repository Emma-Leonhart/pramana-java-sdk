package org.pramana.sdk;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class PramanaObjectTest {

    private static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Test
    void defaultConstructor_hasEmptyGuid() {
        PramanaObject obj = new PramanaObject();
        assertEquals(EMPTY_UUID, obj.pramanaGuid());
    }

    @Test
    void constructor_withId_setsGuid() {
        UUID id = UUID.randomUUID();
        PramanaObject obj = new PramanaObject(id);
        assertEquals(id, obj.pramanaGuid());
    }

    @Test
    void generateId_assignsNonEmptyGuid() {
        PramanaObject obj = new PramanaObject();
        obj.generateId();
        assertNotEquals(EMPTY_UUID, obj.pramanaGuid());
    }

    @Test
    void generateId_throwsOnSecondCall() {
        PramanaObject obj = new PramanaObject();
        obj.generateId();
        assertThrows(PramanaException.class, obj::generateId);
    }

    @Test
    void generateId_throwsWhenConstructedWithId() {
        PramanaObject obj = new PramanaObject(UUID.randomUUID());
        assertThrows(PramanaException.class, obj::generateId);
    }

    @Test
    void pramanaId_isNull_forRegularObject() {
        PramanaObject obj = new PramanaObject();
        assertNull(obj.pramanaId());
    }

    @Test
    void pramanaHashUrl_containsGuid() {
        UUID id = UUID.randomUUID();
        PramanaObject obj = new PramanaObject(id);
        assertEquals("https://pramana.dev/entity/" + id, obj.pramanaHashUrl());
    }

    @Test
    void pramanaUrl_equalsHashUrl_forRegularObject() {
        PramanaObject obj = new PramanaObject(UUID.randomUUID());
        assertEquals(obj.pramanaHashUrl(), obj.pramanaUrl());
    }

    @Test
    void classId_equalsRootId() {
        assertEquals(PramanaObject.ROOT_ID, PramanaObject.classId());
    }

    @Test
    void rootId_hasExpectedValue() {
        assertEquals(UUID.fromString("10000000-0000-4000-8000-000000000001"), PramanaObject.ROOT_ID);
    }

    @Test
    void classUrl_usesClassId() {
        assertEquals("https://pramana.dev/entity/" + PramanaObject.classId(), PramanaObject.classUrl());
    }

    @Test
    void getRoles_returnsEmpty_byDefault() {
        PramanaObject obj = new PramanaObject();
        assertTrue(obj.getRoles().isEmpty());
    }

    @Test
    void implementsPramanaLinkable() {
        PramanaObject obj = new PramanaObject();
        assertInstanceOf(PramanaLinkable.class, obj);
    }

    @Test
    void implementsPramanaInterface() {
        PramanaObject obj = new PramanaObject();
        assertInstanceOf(PramanaInterface.class, obj);
    }
}
