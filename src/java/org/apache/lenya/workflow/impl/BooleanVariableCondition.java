/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/* $Id$  */

package org.apache.lenya.workflow.impl;

import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;
import org.apache.log4j.Logger;

/**
 * Implementation of a boolean variable condition.
 */
public class BooleanVariableCondition extends AbstractCondition {
    
    private static final Logger log = Logger.getLogger(BooleanVariableCondition.class);

    private String variableName;
    private boolean value;

    /**
     * Returns the variable value to check.
     * @return A boolean value.
     */
    protected boolean getValue() {
        return value;
    }

    /**
     * Returns the variable name to check.
     * @return A string.
     */
    protected String getVariableName() {
        return variableName;
    }

    /**
     * @see org.apache.lenya.workflow.Condition#setExpression(java.lang.String)
     */
    public void setExpression(String expression) throws WorkflowException {
        super.setExpression(expression);
        String[] sides = expression.split("=");
        if (sides.length != 2) {
            throw new WorkflowException(
                "The expression '" + expression + "' must be of the form 'name = [true|false]'");
        }
        
        variableName = sides[0].trim();
        value = Boolean.valueOf(sides[1].trim()).booleanValue();
        
        if (log.isDebugEnabled()) {
            log.debug("Expression:    [" + sides[1].trim() + "]");
            log.debug("Setting value: [" + value + "]");
        }
    }

    /**
     * @see org.apache.lenya.workflow.Condition#isComplied(org.apache.lenya.workflow.Situation, org.apache.lenya.workflow.WorkflowInstance)
     */
    public boolean isComplied(Situation situation, WorkflowInstance instance) throws WorkflowException {
        if (log.isDebugEnabled()) {
            log.debug("Checking boolean variable condition");
            log.debug("    Condition value: [" + getValue() + "]");
            log.debug("    Variable value:  [" + instance.getValue(getVariableName()) + "]");
        }
        return instance.getValue(getVariableName()) == getValue();
    }
}
