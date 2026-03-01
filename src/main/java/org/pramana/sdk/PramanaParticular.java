package org.pramana.sdk;

import java.util.UUID;

/**
 * A minimal subclass of {@link PramanaObject} used for testing
 * the Pramana OGM class hierarchy.
 */
public class PramanaParticular extends PramanaObject {

    /** The well-known class ID for PramanaParticular in the ontology. */
    public static final UUID CLASS_ID = UUID.fromString("13000000-0000-4000-8000-000000000004");

    /** Gets the class-level URL in the Pramana graph. */
    public static String classUrl() { return "https://pramana.dev/entity/" + CLASS_ID; }

    /**
     * Creates a new PramanaParticular with a null (empty) UUID.
     */
    public PramanaParticular() {
        super();
    }

    /**
     * Creates a new PramanaParticular with the given UUID.
     */
    public PramanaParticular(UUID id) {
        super(id);
    }
}
