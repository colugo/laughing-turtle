/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.entities;

import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.NameNotFoundException;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;


public class EntityState {
	private String _name = null;
	private EntityProcedure _procedure = new EntityProcedure(this);
	private EntityClass _owningClass;
	private String _id;
	
	// diagram
	public double x = 0;
	public double y = 0;

	public EntityState(String theStateName) {
		this._id = theStateName;
		this._name = theStateName;
	}

	public EntityClass getOwningClass()
	{
		return _owningClass;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntityState other = (EntityState) obj;
		if (_procedure == null) {
			if (other._procedure != null)
				return false;
		} else if (!_procedure.equals(other._procedure))
			return false;
		if (isInitial() != other.isInitial())
			return false;
		if (_name == null) {
			if (other._name != null)
				return false;
		} else if (!_name.equals(other._name))
			return false;
		return true;
	}

	public boolean isInitial() {
		if(this._owningClass.isGeneralisation())
		{
			for(EntityClass generalisationClass : this._owningClass.getAllClassesInHierarchy())
			{
				if(generalisationClass.getInitialState()!= null && generalisationClass.getInitialState().equals(this))
				{
					return true;
				}
			}
			return false;
		}
		else
		{
			return this._owningClass.getInitialState().equals(this);	
		}
	}

	public EntityProcedure getProcedure()
	{
		return _procedure;
	}

	public String getName() {
		return _name;
	}

	public void setInitial() {
		this._owningClass.setInitial(this);
	}
	
	public void setProcedure(EntityProcedure procedure)
	{
		this._procedure = procedure;
	}

	public void setOwningClass(EntityClass theOwningClass) {
		this._owningClass = theOwningClass;
	}

	public boolean hasRcvdEvent() {
		for(EntityEventSpecification entityEventSpecification : this._owningClass.getEventSpecifications())
		{
			if(this._owningClass.hasEventInstanceToState(entityEventSpecification, this))
			{
				return true;
			}
		}
		return false;
	}

	public EntityEventSpecification getRcvdEvent() throws NameNotFoundException {
		if(!this.hasRcvdEvent())
		{
			throw new NameNotFoundException("State " + this._name + " doesn't have a triggering Event");
		}
		EntityEventSpecification theRcvdEvent = null;
		for(EntityEventSpecification entityEventSpecification : this._owningClass.getEventSpecifications())
		{
			if(this._owningClass.hasEventInstanceToState(entityEventSpecification, this))
			{
				theRcvdEvent = entityEventSpecification;
			}
		}
		return theRcvdEvent;
	}

	public boolean hasNonReflexiveTriggeringEvent() {
		ArrayList<EntityEventInstance> instances = this._owningClass.getAllEntityEventInstances();
		for(EntityEventInstance instance : instances)
		{
			if(instance.getToState().equals(this))
			{
				if(!instance.getFromState().equals(this))
				{
					return true;
				}
			}
		}
		return false;
	}

	public ArrayList<EntityEventInstance> getAllTriggeringEventInstances() {
		ArrayList<EntityEventInstance> owningClassInstanceSet = this._owningClass.getAllEntityEventInstances();
		ArrayList<EntityEventInstance> instanceSet = new ArrayList<EntityEventInstance>();
		for (EntityEventInstance instance : owningClassInstanceSet) {
			if (instance.getToState().equals(this))
			{
				instanceSet.add(instance);
			}
		}
		return instanceSet;
	}

	public  ArrayList<EntityEventSpecification>  getAllTriggeringEventSpecs() {
		ArrayList<EntityEventInstance> triggeringEventInstances = this.getAllTriggeringEventInstances();
		HashMap<EntityEventSpecification, Boolean> uniqueEventSpecs = new HashMap<EntityEventSpecification,Boolean> ();
		for(EntityEventInstance instance : triggeringEventInstances)
		{
			uniqueEventSpecs.put(instance.getSpecification(),true);
		}
		ArrayList<EntityEventSpecification> uniqueSpecList = new ArrayList<EntityEventSpecification>();
		for(EntityEventSpecification spec : uniqueEventSpecs.keySet())
		{
			uniqueSpecList.add(spec);
		}
		return uniqueSpecList;
	}

	public ArrayList<EntityEventInstance> getFromThisStateInstances() {
		ArrayList<EntityEventInstance> instances = this.getOwningClass().getAllEntityEventInstances();
		ArrayList<EntityEventInstance> fromThisStateInstances = new ArrayList<EntityEventInstance>();
		for(EntityEventInstance instance : instances)
		{
			if(instance.getFromState().equals(this))
			{
				if(!fromThisStateInstances.contains(instance))
				{
					fromThisStateInstances.add(instance);
				}
			}
		}
		return fromThisStateInstances;
	}

	public EntityState getNextStateForEventSpecification(EntityEventSpecification theEvent) {
		ArrayList<EntityEventInstance> fromThisStateInstances = this.getFromThisStateInstances();
		EntityState nextState = null;
		for(EntityEventInstance instance : fromThisStateInstances)
		{
			if(instance.getSpecification().equals(theEvent))
			{
				nextState = instance.getToState();
				break;
			}
		}
		return nextState;
	}

	public void setProcedureText(String procedureText) throws InvalidActionLanguageSyntaxException {
		if(this._procedure == null)
		{
			this._procedure = new EntityProcedure(this);
		}
		this._procedure.setProcedure(procedureText);
	}

	public String getRawText() {
		if(this._procedure == null)
		{
			return "";
		}
		return this._procedure.getRawText();
	}

	public void setName(String newStateName) {
		this._name = newStateName;
	}

	public String getId() {
		return this._id;
	}
	
	public void setId(String id){
		this._id = id;
	}
}
