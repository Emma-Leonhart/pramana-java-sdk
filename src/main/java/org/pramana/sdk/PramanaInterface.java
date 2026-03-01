package org.pramana.sdk;

import java.util.List;

/**
 * Interface that all Pramana-mapped objects implement, providing
 * access to the ontology roles (interfaces) the object participates in.
 */
public interface PramanaInterface {

    /**
     * Returns the PramanaRole instances that this object fulfils
     * within the Pramana ontology.
     */
    List<PramanaRole> getRoles();
}
