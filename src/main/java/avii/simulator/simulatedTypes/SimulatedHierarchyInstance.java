package main.java.avii.simulator.simulatedTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import javax.naming.NameNotFoundException;

import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.simulator.IInstanceModifier;

public class SimulatedHierarchyInstance extends SimulatedInstance {

	private HashMap<SimulatedHierarchyClass, SimulatedHierarchyInstance> _hierarchyInstances = null;
	
	protected SimulatedHierarchyInstance(){}
	
	public SimulatedHierarchyInstance(SimulatedClass simulatedClass, IInstanceModifier instanceCreator) {
		super(simulatedClass, instanceCreator);
	}

	public HashMap<SimulatedHierarchyClass, SimulatedHierarchyInstance> getHierarchyInstances() {
		return _hierarchyInstances;
	}

	public void setHierarchyInstances(HashMap<SimulatedHierarchyClass, SimulatedHierarchyInstance> _hierarchyInstances) {
		this._hierarchyInstances = _hierarchyInstances;
	}

	public boolean hasHierarchyInstanceForClass(SimulatedClass theHierarchySimulatedClass) {
		return this._hierarchyInstances.containsKey(theHierarchySimulatedClass);
	}

	public SimulatedHierarchyInstance getHierarchyInstanceForClass(SimulatedHierarchyClass theHierarchySimulatedClass) {
		return this._hierarchyInstances.get(theHierarchySimulatedClass);
	}

	public SimulatedHierarchyInstance reclassifyTo(SimulatedHierarchyClass theNewSimulatedHierarchyClass) {
		Collection<SimulatedHierarchyClass> newDependants = theNewSimulatedHierarchyClass.getHierarchyDependants();
		Collection<SimulatedHierarchyClass> existingHierarchySet = new HashSet<SimulatedHierarchyClass>();
		existingHierarchySet.addAll(this._hierarchyInstances.keySet());
		
		SimulatedInstanceIdentifier idToPreserve = this.getLeafInstance().getIdentifier();
		
		for(SimulatedHierarchyClass existingHierachyClass : existingHierarchySet)
		{
			if(!newDependants.contains(existingHierachyClass))
			{
				SimulatedHierarchyInstance existingHierarchyInstance = this._hierarchyInstances.get(existingHierachyClass);
				this._hierarchyInstances.remove(existingHierachyClass);
				existingHierarchyInstance.onlyDeleteThisInstance();
			}
		}
		
		for(SimulatedHierarchyClass newDependant : newDependants)
		{
			if(!this._hierarchyInstances.containsKey(newDependant))
			{
				SimulatedHierarchyInstance newDependantInstance = (SimulatedHierarchyInstance) this._classType.getSimulator().getInstanceModifier().createInstance(newDependant);
				this._hierarchyInstances.put(newDependant, newDependantInstance);
				newDependantInstance.setHierarchyInstances(this._hierarchyInstances);
			}
		}
		
		SimulatedHierarchyInstance newLeaf = this.getLeafInstance();
		newLeaf.adoptIdentifier(idToPreserve);
		return newLeaf;
	}

	private void adoptIdentifier(SimulatedInstanceIdentifier idToPreserve) {
		int nextClassSequence = this._classType.getAndIncreaseInstanceSequence();
		this._identifier = idToPreserve;
		this._identifier.setSequence(nextClassSequence);
		this._identifier.setSimulatedClass(this._classType);
	}

	public boolean hasOwnedAttribute(String theAttributeName) {
		return this._attributes.containsKey(theAttributeName);
	}

	public boolean hasAttribute(String theAttributeName) {
		for(SimulatedHierarchyInstance hierarchyInstance : this._hierarchyInstances.values())
		{
			if(hierarchyInstance.hasOwnedAttribute(theAttributeName))
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Object getAttribute(String theAttributeName){
		if(this.hasOwnedAttribute(theAttributeName))
		{
			return super.getAttribute(theAttributeName);
		}
		for(SimulatedHierarchyInstance hierarchyInstance : this._hierarchyInstances.values())
		{
			if(hierarchyInstance.hasOwnedAttribute(theAttributeName))
			{
				return hierarchyInstance.getAttribute(theAttributeName);
			}
		}
		return null;
	}
	
	@Override
	public void setAttribute(String theAttributeName, Object attributeValue){
		if(this.hasOwnedAttribute(theAttributeName))
		{
			super.setAttribute(theAttributeName, attributeValue);
			return;
		}
		for(SimulatedHierarchyInstance hierarchyInstance : this._hierarchyInstances.values())
		{
			if(hierarchyInstance.hasOwnedAttribute(theAttributeName))
			{
				hierarchyInstance.setAttribute(theAttributeName, attributeValue);
			}
		}
	}

	public SimulatedHierarchyClass getEntityClassInHierarchyWithRelation(String relationName) {
		for(SimulatedHierarchyClass hierarchyClass : this._hierarchyInstances.keySet())
		{
			EntityClass entityClass = hierarchyClass.getConcreteClass();
			if(entityClass.hasRelation(relationName))
			{
				return (SimulatedHierarchyClass) hierarchyClass;
			}
		}
		return null;
	}

	public SimulatedHierarchyClass getEntityClassInHierarchyWithRelation(String relationName, String verb) {
		for(SimulatedHierarchyClass hierarchyClass : this._hierarchyInstances.keySet())
		{
			EntityClass entityClass = hierarchyClass.getConcreteClass();
			if(entityClass.hasRelation(relationName))
			{
				EntityClass returnClass = null;
				try {
					EntityRelation relation = entityClass.getRelationWithName(relationName);
					returnClass = relation.getEndWithVerb(verb);
					if(entityClass.equals(returnClass))
					{
						return hierarchyClass;
					}
				} catch (NameNotFoundException e) {
					// has been validated
				}
			}
		}
		return null;
	}
	
	@Override
	public void deleteInstance()
	{
		for(SimulatedHierarchyInstance instance : this._hierarchyInstances.values())
		{
			instance.deleteWorker();
		}
		for(SimulatedHierarchyInstance instance : this._hierarchyInstances.values())
		{
			instance.onlyDeleteThisInstance();
		}
	}

	public Collection<SimulatedInstanceIdentifier> getHierarchyIdentifiers() {
		ArrayList<SimulatedInstanceIdentifier> ids = new ArrayList<SimulatedInstanceIdentifier>();
		for(SimulatedInstance instance : this._hierarchyInstances.values())
		{
			ids.add(instance.getIdentifier());
		}
		return ids;
	}

	public SimulatedHierarchyInstance getRootInstance() {
		ArrayList<SimulatedHierarchyClass> potentialRoots = new ArrayList<SimulatedHierarchyClass>();
		for(SimulatedHierarchyClass hierarchyClass : this._hierarchyInstances.keySet())
		{
			EntityClass entityClass = hierarchyClass._classToSimulate;
			if(!entityClass.hasSuperClasses())
			{
				potentialRoots.add(hierarchyClass);
			}
		}
		
		Collections.sort(potentialRoots);
		SimulatedHierarchyClass foundRootClass =  potentialRoots.get(0);
		SimulatedHierarchyInstance foundRootInstance = this._hierarchyInstances.get(foundRootClass);
		return foundRootInstance;
	}

	public SimulatedHierarchyInstance getLeafInstance() {
		for(SimulatedHierarchyClass hierarchyClass : this._hierarchyInstances.keySet())
		{
			EntityClass entityClass = hierarchyClass._classToSimulate;
			if(!entityClass.hasSubClasses())
			{
				SimulatedHierarchyInstance theInstance = this._hierarchyInstances.get(hierarchyClass);
				return theInstance;
			}
		}
		return null;
	}
	
	public void setSimulatingState(SimulatedState theSimulatedState) {
		this._simulatingState = theSimulatedState;
		if(this._hierarchyInstances == null)
		{
			return;
		}
		for(SimulatedInstance instance : this._hierarchyInstances.values())
		{
			instance._simulatingState = theSimulatedState;
		}
	}
	
	public void setStateName(String stateName)
	{
		SimulatedState state = this._classType._simulatedStates.get(stateName);
		this._simulatingState = state;
		if(this._hierarchyInstances == null)
		{
			return;
		}
		for(SimulatedInstance instance : this._hierarchyInstances.values())
		{
			instance._simulatingState = state;
		}
	}
	
}
