/*
 * BooleanVariable.java
 *
 * Created on 27. Mai 2003, 12:35
 */

package org.apache.lenya.workflow;

/**
 *
 * @author  andreas
 */
public interface BooleanVariable {

    /**
     * Returns the name of this variable.
     */
    String getName();
    
    /**
     * Returns the initial value of this variable.
     * @return A boolean value.
     */
    boolean getInitialValue();
}
