/*
 * WorkflowInstanceImpl.java
 *
 * Created on 9. April 2003, 13:30
 */

package org.apache.lenya.workflow.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lenya.workflow.Action;
import org.apache.lenya.workflow.BooleanVariable;
import org.apache.lenya.workflow.BooleanVariableInstance;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.State;
import org.apache.lenya.workflow.Transition;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;
import org.apache.lenya.workflow.WorkflowListener;

/**
 *
 * @author  andreas
 */
public abstract class WorkflowInstanceImpl implements WorkflowInstance {

    /**
     * Creates a new instance of WorkflowInstanceImpl.
     */
    protected WorkflowInstanceImpl() {
    }

    private WorkflowImpl workflow;

    /**
     * Returns the workflow object of this instance.
     * @return A workflow object.
     */
    public Workflow getWorkflow() {
        return getWorkflowImpl();
    }

    /**
     * Returns the workflow object of this instance.
     * @return A workflow object.
     */
    protected WorkflowImpl getWorkflowImpl() {
        return workflow;
    }

    /** Returns the events that can be invoked in a certain situation.
     * @param situation The situation to check.
     * @return The events that can be invoked.
     */
    public Event[] getExecutableEvents(Situation situation) {
        Transition transitions[] = getWorkflow().getLeavingTransitions(getCurrentState());
        Set executableEvents = new HashSet();

        for (int i = 0; i < transitions.length; i++) {
            if (transitions[i].canFire(situation)) {
                executableEvents.add(transitions[i].getEvent());
            }
        }
        return (Event[]) executableEvents.toArray(new Event[executableEvents.size()]);
    }

    /** Invoke an event on this workflow instance.
     * @param situation The situation when the event was invoked.
     * @param event The event that was invoked.
     */
    public void invoke(Situation situation, Event event) throws WorkflowException {
        if (Arrays.asList(getExecutableEvents(situation)).contains(event)) {
            throw new WorkflowException(
                "The event '"
                    + event
                    + "' cannot be invoked in the situation '"
                    + situation
                    + "'.");
        }
        Transition transitions[] = getWorkflow().getLeavingTransitions(getCurrentState());
        for (int i = 0; i < transitions.length; i++) {
            if (transitions[i].getEvent().equals(event)) {
                fire((TransitionImpl) transitions[i]);
            }
        }
        for (Iterator iter = listeners.iterator(); iter.hasNext();) {
            WorkflowListener listener = (WorkflowListener) iter.next();
            listener.transitionFired(this, situation, event);
        }
    }

    /**
     * Invokes a transition.
     * @param transition The transition to invoke.
     * @throws WorkflowException if something goes wrong.
     */
    protected void fire(TransitionImpl transition) throws WorkflowException {
        Action actions[] = transition.getActions();
        for (int i = 0; i < actions.length; i++) {
            actions[i].execute(this);
        }
        setCurrentState(transition.getDestination());
    }

    private State currentState;

    /**
     * Sets the current state of this instance.
     * @param state The state to set.
     */
    protected void setCurrentState(State state) {
        assert state != null && ((WorkflowImpl) getWorkflow()).containsState(state);
        this.currentState = state;
    }

    /** Returns the current state of this WorkflowInstance.
     * @return A state object.
     */
    public State getCurrentState() {
        return currentState;
    }

    /**
     * Sets the workflow of this instance.
     * @param workflow A workflow object.
     */
    protected void setWorkflow(WorkflowImpl workflow) {
        assert workflow != null;
        this.workflow = workflow;
        setCurrentState(getWorkflow().getInitialState());
        initVariableInstances();
    }

    /**
     * Sets the workflow of this instance.
     * @param workflowName The identifier of the workflow.
     * @throws WorkflowException if something goes wrong.
     */
    protected void setWorkflow(String workflowName) throws WorkflowException {
        assert workflowName != null && !"".equals(workflowName);
        setWorkflow(getWorkflow(workflowName));
    }

    /**
     * Factory method to create a workflow object for a given identifier.
     * @param workflow The workflow identifier.
     * @return A workflow object.
     */
    protected abstract WorkflowImpl getWorkflow(String workflowName) throws WorkflowException;

    /**
     * Returns a workflow state for a given name.
     * @param id The state id.
     * @return A workflow object.
     */
    protected State getState(String id) throws WorkflowException {
        return getWorkflowImpl().getState(id);
    }

    private Map variableInstances = new HashMap();

    /**
     * Initializes the variable instances in the initial state.
     */
    protected void initVariableInstances() {
        variableInstances.clear();
        BooleanVariable variables[] = getWorkflowImpl().getVariables();
        for (int i = 0; i < variables.length; i++) {
            BooleanVariableInstance instance = new BooleanVariableInstanceImpl();
            instance.setValue(variables[i].getInitialValue());
            variableInstances.put(variables[i], instance);
        }
    }

    /**
     * Returns the corresponding instance of a workflow variable.
     * @param variable A variable of the corresponding workflow.
     * @return A variable instance object.
     */
    protected BooleanVariableInstance getVariableInstance(BooleanVariable variable)
        throws WorkflowException {
        if (!variableInstances.containsKey(variable)) {
            throw new WorkflowException("No instance for variable '" + variable.getName() + "'!");
        }
        return (BooleanVariableInstance) variableInstances.get(variable);
    }

    /* (non-Javadoc)
     * @see org.apache.lenya.workflow.WorkflowInstance#getValue(java.lang.String)
     */
    public boolean getValue(String variableName) throws WorkflowException {
        BooleanVariable variable = getWorkflowImpl().getVariable(variableName);
        BooleanVariableInstance instance = getVariableInstance(variable);
        return instance.getValue();
    }

    private List listeners = new ArrayList();

    /* (non-Javadoc)
     * @see org.apache.lenya.workflow.WorkflowInstance#addWorkflowListener(org.apache.lenya.workflow.WorkflowListener)
     */
    public void addWorkflowListener(WorkflowListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    /* (non-Javadoc)
     * @see org.apache.lenya.workflow.WorkflowInstance#removeWorkflowListener(org.apache.lenya.workflow.WorkflowListener)
     */
    public void removeWorkflowListener(WorkflowListener listener) {
        listeners.remove(listener);
    }

}
