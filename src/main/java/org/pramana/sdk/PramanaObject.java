package org.pramana.sdk;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Base class for all objects mapped into the Pramana knowledge graph.
 * Implements {@link PramanaLinkable} for graph identity and
 * {@link PramanaInterface} for ontology role participation.
 *
 * <p><b>Friction by design:</b> IDs are never auto-generated. A new
 * PramanaObject starts with a null UUID and only receives a real UUID v4
 * when {@link #generateId()} is explicitly called. This prevents
 * disposable or transient objects from polluting the graph with
 * throw-away identifiers. Once assigned, the ID is immutable — calling
 * generateId a second time throws {@link PramanaException}.</p>
 */
public class PramanaObject implements PramanaLinkable, PramanaInterface {

    private static final String EMPTY_UUID = "00000000-0000-0000-0000-000000000000";

    private UUID pramanaGuid;

    /** The well-known root ID for the PramanaObject class itself in the ontology. */
    public static final UUID ROOT_ID = UUID.fromString("10000000-0000-4000-8000-000000000001");

    /** Gets the class-level ID, which for PramanaObject is {@link #ROOT_ID}. */
    public static UUID classId() { return ROOT_ID; }

    /** Gets the class-level URL in the Pramana graph. */
    public static String classUrl() { return "https://pramana.dev/entity/" + ROOT_ID; }

    /**
     * Creates a new PramanaObject with a null (empty) UUID.
     */
    public PramanaObject() {
        this.pramanaGuid = UUID.fromString(EMPTY_UUID);
    }

    /**
     * Creates a new PramanaObject with the given UUID.
     */
    public PramanaObject(UUID id) {
        this.pramanaGuid = id != null ? id : UUID.fromString(EMPTY_UUID);
    }

    @Override
    public UUID pramanaGuid() { return pramanaGuid; }

    /**
     * Gets the Pramana identifier string. Regular objects do not belong to a
     * pseudo-class, so this returns null.
     */
    @Override
    public String pramanaId() { return null; }

    @Override
    public String pramanaHashUrl() { return "https://pramana.dev/entity/" + pramanaGuid; }

    @Override
    public String pramanaUrl() { return pramanaHashUrl(); }

    /**
     * Assigns a new UUID v4 to this object. Throws PramanaException
     * if the object already has a non-empty ID — IDs are write-once by design.
     */
    public void generateId() {
        if (pramanaGuid.equals(UUID.fromString(EMPTY_UUID))) {
            pramanaGuid = UUID.randomUUID();
        } else {
            throw new PramanaException("Cannot reassign a PramanaObject ID once it has been set.");
        }
    }

    @Override
    public List<PramanaRole> getRoles() { return Collections.emptyList(); }
}
