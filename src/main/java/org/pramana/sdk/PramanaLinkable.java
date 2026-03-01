package org.pramana.sdk;

import java.util.UUID;

/**
 * Interface for objects that can be linked to entities in the Pramana knowledge graph.
 * Provides identity and URL properties for graph integration.
 */
public interface PramanaLinkable {

    /** Gets the UUID (v4 or v5) identifying this entity in the Pramana graph. */
    UUID pramanaGuid();

    /**
     * Gets the Pramana identifier string (e.g. "pra:num:3,1,2,1").
     * Returns null for objects that are not pseudo-class instances.
     */
    String pramanaId();

    /**
     * Gets the Pramana entity URL using the hashed UUID,
     * e.g. "https://pramana.dev/entity/{pramanaGuid}".
     */
    String pramanaHashUrl();

    /**
     * Gets the Pramana entity URL. For pseudo-class instances this uses the
     * pramanaId string; otherwise it falls back to pramanaHashUrl.
     */
    String pramanaUrl();
}
