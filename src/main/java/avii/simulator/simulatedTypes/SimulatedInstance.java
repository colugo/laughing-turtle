package main.java.avii.simulator.simulatedTypes;

import java.util.Collection;
import java.util.HashMap;

import javax.naming.NameNotFoundException;

import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.weaving.AttributeWeave;
import main.java.avii.simulator.IInstanceModifier;
import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.weave.InstanceWeave;

public class SimulatedInstance {

	protected SimulatedClass _classType;
	protected HashMap<String,Object> _attributes = new HashMap<String,Object>();
	protected SimulatedInstanceIdentifier _identifier;
	protected SimulatedState _simulatingState;
	private InstanceWeave _instanceWeave;

	protected SimulatedInstance()
	{
		//hidden constructor for TestVectorSimulatedInstance
	}
	
	public SimulatedInstance(SimulatedClass simulatedClass, IInstanceModifier instanceCreator) {
		this._classType = simulatedClass;
		this.createAttributes();
	}

	@Override
	public String toString()
	{
		String domainName = this._classType.getConcreteClass().getDomain().getName();
		String className = this._classType.getConcreteClass().getName();
		int instanceNumber = this._identifier.getSequence();
		
		String description = className + '[' + domainName + ']' + instanceNumber; 
		return description;
	}
	
	public SimulatedClass getSimulatedClass()
	{
		return this._classType;
	}
	
	private void createAttributes() {
		for(SimulatedAttribute attribute : this._classType.getAttributes())
		{
			String name = attribute.getName();
			Object defaultValue = attribute.getType().getDefaultValueAsObject();
			this._attributes.put(name,defaultValue);
		}
	}

	public Object getAttribute(String attributeName){
		if(attributeName.equals(EntityAttribute.STATE_ATTRIBUTE_NAME))
		{
			String stateName = this._classType.getName() + " has no states.";
			if(this._simulatingState != null)
			{
				stateName = this._simulatingState.getName();
			}
			return stateName;
		}
		
		
		// class not in weave
		if(!this.isInWeave())
		{
			return this._attributes.get(attributeName);
		}
		
		EntityAttribute theAttribute = getEntityAttribute(attributeName);
		AttributeWeave attributeWeave = this._instanceWeave.getClassWeave().getAttributeWeaveContainingAttribute(theAttribute);
		
		// attribute not in weave
		if(attributeWeave == null)
		{
			return this._attributes.get(attributeName);
		}
		
		Object value = this._instanceWeave.getAttribute(attributeWeave);
		return value;
	}

	public void setAttribute(String attributeName, Object attributeValue){
		Object convertedAttributeValue = this._classType.convertNewAttributeValueToCorrectDatatype(attributeName, attributeValue);
		
		// class not in weave
		if(!this.isInWeave())
		{
			this._attributes.put(attributeName, convertedAttributeValue);	
			return;
		}
		
		EntityAttribute theAttribute = getEntityAttribute(attributeName);
		AttributeWeave attributeWeave = this._instanceWeave.getClassWeave().getAttributeWeaveContainingAttribute(theAttribute);
		
		// attribute not in weave
		if(attributeWeave == null)
		{
			this._attributes.put(attributeName, convertedAttributeValue);	
			return;
		}
		
		// attribute in weave
		this._instanceWeave.setAttribute(attributeWeave, convertedAttributeValue);
	}

	private EntityAttribute getEntityAttribute(String attributeName) {
		EntityAttribute theAttribute = null;
		try {
			theAttribute = this._classType.getConcreteClass().getAttributeWithName(attributeName);
		} catch (NameNotFoundException e) {
			// has been validated
		}
		return theAttribute;
	}
	
	public void onlyDeleteThisInstance() {
		this._classType.getSimulator().getInstanceModifier().aboutToDeleteInstance(this);
		this._classType.cleanupRemovedInstance(this);
	}
	
	public void deleteCAMInstance()
	{
		this.deleteWorker();
		this._classType.cleanupRemovedInstance(this);
	}
	
	public void deleteInstance() {
		this.deleteWorker();
		this.onlyDeleteThisInstance();
	}
	
	protected void deleteWorker()
	{
		this.deleteAllEventsForThisInstance();
		this.removeFromAllRelations();
		this.deleteAssociationRelation();
	}

	private void deleteAssociationRelation() {
		EntityClass entityClass = this._classType.getConcreteClass();
		if(entityClass.isAssociation())
		{
			EntityRelation associationRelation = entityClass.getAssociationRelation();
			SimulatedRelationship simulatedRelation = this._classType.getSimulator().getRelationshipWithName(associationRelation.getName());
			simulatedRelation.unrelateInstancesRelatedBy(this._identifier);
		}
	}

	private void removeFromAllRelations() {
		for(SimulatedRelationship relationship : this._classType.getSimulatedRelationships())
		{
			if(relationship.isReflexive())
			{
				String verbA = relationship.getVerbA();
				String verbB = relationship.getVerbB();
				Collection<SimulatedInstanceIdentifier> otherEnds = relationship.getOtherEnd(this._identifier, verbA);
				for(SimulatedInstanceIdentifier otherEnd : otherEnds)
				{
					relationship.unrelateInstances(this._identifier, verbA, otherEnd);
				}
				otherEnds = relationship.getOtherEnd(this._identifier, verbB);
				for(SimulatedInstanceIdentifier otherEnd : otherEnds)
				{
					relationship.unrelateInstances(this._identifier, verbA, otherEnd);
				}
			}
			else
			{
				Collection<SimulatedInstanceIdentifier> otherEnds = relationship.getOtherEnd(this._identifier);
				for(SimulatedInstanceIdentifier otherEnd : otherEnds)
				{
					relationship.unrelateInstances(this._identifier, otherEnd);
				}
			}
		}
	}

	private void deleteAllEventsForThisInstance() {
		this._classType.getSimulator().deleteAllEventsForInstance(this._identifier);	
	}

	public SimulatedInstanceIdentifier getIdentifier() {
		return this._identifier;
	}

	public void setIdentifier(SimulatedInstanceIdentifier simulatedInstanceIdentifier) {
		this._identifier = simulatedInstanceIdentifier;
	}

	public SimulatedState getSimulatedState() {
		return this._simulatingState;
	}

	public void setSimulatingState(SimulatedState theSimulatedState) {
		this._simulatingState = theSimulatedState;
	}
	
	public void setStateName(String stateName)
	{
		SimulatedState state = this._classType._simulatedStates.get(stateName);
		this._simulatingState = state;
	}

	public InstanceWeave getInstanceWeave() {
		return this._instanceWeave;
	}

	public boolean isInWeave() {
		return this._instanceWeave != null;
	}

	public void setWeave(InstanceWeave instanceWeave) {
		this._instanceWeave = instanceWeave;		
	}
	

}
