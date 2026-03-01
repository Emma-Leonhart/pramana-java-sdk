package org.pramana.sdk;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class PramanaRoleTest {

    private static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Test
    void constructor_setsLabel() {
        PramanaRole role = new PramanaRole("Entity");
        assertEquals("Entity", role.getLabel());
    }

    @Test
    void constructor_withId_setsGuid() {
        UUID id = UUID.randomUUID();
        PramanaRole role = new PramanaRole("Entity", id);
        assertEquals(id, role.pramanaGuid());
    }

    @Test
    void constructor_withoutId_hasEmptyGuid() {
        PramanaRole role = new PramanaRole("Entity");
        assertEquals(EMPTY_UUID, role.pramanaGuid());
    }

    @Test
    void isPramanaObject() {
        PramanaRole role = new PramanaRole("Entity");
        assertInstanceOf(PramanaObject.class, role);
    }

    @Test
    void getRoles_returnsSelf() {
        PramanaRole role = new PramanaRole("Entity");
        List<PramanaRole> roles = role.getRoles();
        assertEquals(1, roles.size());
        assertSame(role, roles.get(0));
    }

    @Test
    void parentRoles_initiallyEmpty() {
        PramanaRole role = new PramanaRole("Entity");
        assertTrue(role.getParentRoles().isEmpty());
    }

    @Test
    void childRoles_initiallyEmpty() {
        PramanaRole role = new PramanaRole("Entity");
        assertTrue(role.getChildRoles().isEmpty());
    }

    @Test
    void instanceOf_defaultsToNull() {
        PramanaRole role = new PramanaRole("Entity");
        assertNull(role.getInstanceOf());
    }

    @Test
    void subclassOf_defaultsToNull() {
        PramanaRole role = new PramanaRole("Entity");
        assertNull(role.getSubclassOf());
    }

    @Test
    void canBuildRoleHierarchy() {
        PramanaRole parent = new PramanaRole("Thing");
        PramanaRole child = new PramanaRole("Person");
        child.setSubclassOf(parent);
        parent.getChildRoles().add(child);
        child.getParentRoles().add(parent);

        assertSame(parent, child.getSubclassOf());
        assertTrue(parent.getChildRoles().contains(child));
        assertTrue(child.getParentRoles().contains(parent));
    }

    @Test
    void instanceOf_canBeSet() {
        PramanaRole classRole = new PramanaRole("Class");
        PramanaRole instance = new PramanaRole("MyClass");
        instance.setInstanceOf(classRole);

        assertSame(classRole, instance.getInstanceOf());
    }

    @Test
    void generateId_worksOnRole() {
        PramanaRole role = new PramanaRole("Entity");
        role.generateId();
        assertNotEquals(EMPTY_UUID, role.pramanaGuid());
    }
}
