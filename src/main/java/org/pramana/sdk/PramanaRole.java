package org.pramana.sdk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Represents a role (interface) in the Pramana ontology.
 * Roles form a hierarchy via {@link #getSubclassOf()} and {@link #getInstanceOf()},
 * and track their position in the role graph through
 * {@link #getParentRoles()} and {@link #getChildRoles()}.
 */
public class PramanaRole extends PramanaObject {

    private String label;
    private PramanaRole instanceOf;
    private PramanaRole subclassOf;
    private final List<PramanaRole> parentRoles = new ArrayList<>();
    private final List<PramanaRole> childRoles = new ArrayList<>();

    /**
     * Creates a new PramanaRole with the given label.
     */
    public PramanaRole(String label) {
        super();
        this.label = label;
    }

    /**
     * Creates a new PramanaRole with the given label and UUID.
     */
    public PramanaRole(String label, UUID id) {
        super(id);
        this.label = label;
    }

    /** Gets the human-readable label for this role. */
    public String getLabel() { return label; }

    /** Sets the human-readable label for this role. */
    public void setLabel(String label) { this.label = label; }

    /** Gets the role that this role is an instance of. */
    public PramanaRole getInstanceOf() { return instanceOf; }

    /** Sets the role that this role is an instance of. */
    public void setInstanceOf(PramanaRole instanceOf) { this.instanceOf = instanceOf; }

    /** Gets the role that this role is a subclass of. */
    public PramanaRole getSubclassOf() { return subclassOf; }

    /** Sets the role that this role is a subclass of. */
    public void setSubclassOf(PramanaRole subclassOf) { this.subclassOf = subclassOf; }

    /** Gets the parent roles of this role in the hierarchy. */
    public List<PramanaRole> getParentRoles() { return parentRoles; }

    /** Gets the child roles of this role in the hierarchy. */
    public List<PramanaRole> getChildRoles() { return childRoles; }

    @Override
    public List<PramanaRole> getRoles() { return Collections.singletonList(this); }
}
