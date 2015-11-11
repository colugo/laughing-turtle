package main.java.avii.simulator.relations;

import java.util.ArrayList;
import java.util.Collection;

import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;
import main.java.avii.simulator.ISimulator;
import main.java.avii.simulator.exceptions.RelationCardinalityException;
import main.java.avii.simulator.simulatedTypes.NullSimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyClass;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class SimulatedRelationship {

	private String _relationName;
	private SimulatedClass _classA;
	private SimulatedClass _classB;
	private SimulatedClass _associationClass;
	private String _verbA;
	private String _verbB;
	private CardinalityType _cardinalityA;
	private CardinalityType _cardinalityB;
	private BaseRelationshipStorage _relationshipStorage;
	private boolean _isReflexive;
	private ISimulator _simulator;

	public SimulatedRelationship() {
	}
	
	private boolean needToValidateLowerBound(CardinalityType cardinality)
	{
		if(cardinality.equals(CardinalityType.ONE_TO_MANY) || cardinality.equals(CardinalityType.ONE_TO_ONE))
		{
			return true;
		}
		return false;
	}
	
	private boolean needToValidateUpperBound(CardinalityType cardinality)
	{
		if(cardinality.equals(CardinalityType.ZERO_TO_ONE) || cardinality.equals(CardinalityType.ONE_TO_ONE))
		{
			return true;
		}
		return false;
	}
	
	private boolean needToValidateLowerA()
	{
		return this.needToValidateLowerBound(this._cardinalityA);
	}
	private boolean needToValidateLowerB()
	{
		return this.needToValidateLowerBound(this._cardinalityB);
	}
	private boolean needToValidateUpperA()
	{
		return this.needToValidateUpperBound(this._cardinalityA);
	}
	private boolean needToValidateUpperB()
	{
		return this.needToValidateUpperBound(this._cardinalityB);
	}
	
	public SimulatedRelationship(EntityRelation relation, ISimulator simulator) {
		this._relationName = relation.getName();
		this._classA = simulator.getSimulatedClass(relation.getClassA().getName());
		this._cardinalityA = relation.getCardinalityA();
		this._verbA = relation.getClassAVerb();
		this._classB = simulator.getSimulatedClass(relation.getClassB().getName());
		this._cardinalityB = relation.getCardinalityB();
		this._verbB = relation.getClassBVerb();
		this._isReflexive = relation.isReflexive();
		this.setSimulator();
		if(relation.hasAssociation())
		{
			this._associationClass = simulator.getSimulatedClass(relation.getAssociation().getName());
		}
		this.createRelationshipStorage();
	}

	private void setSimulator() {
		this._simulator = this._classA.getSimulator();		
	}
	
	public ISimulator getSimulator()
	{
		return this._simulator;
	}

	private void createRelationshipStorage() {
		if(this.isReflexive())
		{
			this._relationshipStorage = new ReflexiveRelationshipStorage(this);
		}
		else
		{
			this._relationshipStorage = new NonReflexiveRelationshipStorage(this);
		}
		
	}

	public String getName() {
		return this._relationName;
	}

	public void setName(String relationName) {
		this._relationName = relationName;	
	}

	public void setClassA(SimulatedClass a) {
		this._classA = a;
	}

	public SimulatedClass getClassA() {
		return this._classA;
	}
	
	public void setClassB(SimulatedClass b) {
		this._classB = b;
	}

	public SimulatedClass getClassB() {
		return this._classB;
	}

	public boolean hasAssociation() {
		return this._associationClass != null;
	}

	public void setAssociationClass(SimulatedClass associationClass) {
		this._associationClass = associationClass;
	}

	public SimulatedClass getAssociationClass() {
		return this._associationClass;
	}

	public boolean isReflexive() {
		return this._isReflexive;
	}

	public SimulatedRelationInstance createInstance() {
		SimulatedRelationInstance instance = new SimulatedRelationInstance(this);
		return instance;
	}

	public void setVerbA(String verbA) {
		this._verbA = verbA;
	}

	public void setVerbB(String verbB) {
		this._verbB = verbB;
	}

	public String getVerbA() {
		return this._verbA;
	}

	public String getVerbB() {
		return this._verbB;
	}

	public void setCardinalityA(CardinalityType cardinality) {
		this._cardinalityA = cardinality;
	}
	
	public void setCardinalityB(CardinalityType cardinality) {
		this._cardinalityB = cardinality;
	}

	public CardinalityType getCardinalityA() {
		return this._cardinalityA;
	}
	
	public CardinalityType getCardinalityB() {
		return this._cardinalityB;
	}

	public BaseRelationshipStorage getRelationshipStorage() {
		return this._relationshipStorage;
	}

	///////////// storage operations //////////////////
	public void storeRelationInstance(SimulatedRelationInstance relationInstance) {
		this._relationshipStorage.storeRelationInstance(relationInstance);
	}

	public boolean doesRelationshipExistBetween(SimulatedInstanceIdentifier instance1, SimulatedInstanceIdentifier instance2) {
		return ((NonReflexiveRelationshipStorage)this._relationshipStorage).doesRelationExistBetween(instance1, instance2);
	}

	public boolean doesRelationshipExistBetween(SimulatedInstanceIdentifier instance1, String verb1, SimulatedInstanceIdentifier instance2, String verb2) {
		return ((ReflexiveRelationshipStorage)this._relationshipStorage).doesRelationExistBetween(instance1, verb1, instance2, verb2);
	}

	public String getOtherVerb(String knownVerb) {
		if(this._verbA.equals(knownVerb))
		{
			return this._verbB;
		}
		return this._verbA;
	}

	public void unrelateInstances(SimulatedInstanceIdentifier instance1, SimulatedInstanceIdentifier instance2) {
		((NonReflexiveRelationshipStorage)this._relationshipStorage).unrelateInstances(instance1, instance2);
	}

	public void unrelateInstances(SimulatedInstanceIdentifier instance1, String instance1Verb, SimulatedInstanceIdentifier instance2) {
		((ReflexiveRelationshipStorage)this._relationshipStorage).unrelateInstances(instance1, instance1Verb, instance2);
		
	}

	public Collection<SimulatedInstanceIdentifier> getOtherEnd(Collection<SimulatedInstanceIdentifier> instances) {
		return ((NonReflexiveRelationshipStorage)this._relationshipStorage).getOtherEnd(instances);
	}
	
	public Collection<SimulatedInstanceIdentifier> getOtherEnd(SimulatedInstanceIdentifier instance) {
		ArrayList<SimulatedInstanceIdentifier> instances = new ArrayList<SimulatedInstanceIdentifier>();
		instances.add(instance);
		return ((NonReflexiveRelationshipStorage)this._relationshipStorage).getOtherEnd(instances);
	}

	public Collection<SimulatedInstanceIdentifier> getOtherEnd(Collection<SimulatedInstanceIdentifier> instances, String verb) {
		return ((ReflexiveRelationshipStorage)this._relationshipStorage).getOtherEnd(instances, verb);
	}
	
	public Collection<SimulatedInstanceIdentifier> getOtherEnd(SimulatedInstanceIdentifier instance, String verb) {
		ArrayList<SimulatedInstanceIdentifier> instances = new ArrayList<SimulatedInstanceIdentifier>();
		instances.add(instance);
		return ((ReflexiveRelationshipStorage)this._relationshipStorage).getOtherEnd(instances, verb);
	}

	public SimulatedInstance getSimulatedInstanceInRelationship(SimulatedInstanceIdentifier instanceIdentifier)
	{
		SimulatedHierarchyClass initialClass = (SimulatedHierarchyClass) instanceIdentifier.getSimulatedClass();
		SimulatedHierarchyClass hierarchyClass = (SimulatedHierarchyClass) instanceIdentifier.getSimulatedClass();
		
		SimulatedHierarchyInstance initialInstance = (SimulatedHierarchyInstance) hierarchyClass.getInstanceFromIdentifier(instanceIdentifier);
		hierarchyClass = initialInstance.getEntityClassInHierarchyWithRelation(this._relationName);

		SimulatedHierarchyInstance instanceToRelate = (SimulatedHierarchyInstance) initialClass.getInstanceFromIdentifier(instanceIdentifier);
		instanceToRelate = instanceToRelate.getHierarchyInstanceForClass(hierarchyClass);
		
		return instanceToRelate;
	}

	public SimulatedInstance getSimulatedInstanceInRelationship(SimulatedInstanceIdentifier instanceIdentifier, String verb) {
		SimulatedHierarchyClass initialClass = (SimulatedHierarchyClass) instanceIdentifier.getSimulatedClass();
		SimulatedHierarchyClass hierarchyClass = (SimulatedHierarchyClass) instanceIdentifier.getSimulatedClass();
		
		SimulatedHierarchyInstance initialInstance = (SimulatedHierarchyInstance) hierarchyClass.getInstanceFromIdentifier(instanceIdentifier);
		hierarchyClass = initialInstance.getEntityClassInHierarchyWithRelation(this._relationName, verb);

		if(hierarchyClass == null)
		{
			return new NullSimulatedInstance(initialClass);
		}
		
		SimulatedHierarchyInstance instanceToRelate = (SimulatedHierarchyInstance) initialClass.getInstanceFromIdentifier(instanceIdentifier);
		instanceToRelate = instanceToRelate.getHierarchyInstanceForClass(hierarchyClass);
		
		return instanceToRelate;
	}

	public boolean isInstanceInRelationship(SimulatedInstanceIdentifier instanceIdentifier) {
		return ((NonReflexiveRelationshipStorage)this._relationshipStorage).hasRelationForInstance(instanceIdentifier);
	}
	
	public boolean isInstanceInRelationship(SimulatedInstanceIdentifier instanceIdentifier, String verb) {
		return ((ReflexiveRelationshipStorage)this._relationshipStorage).hasRelationForInstance(instanceIdentifier, verb);
	}

	public void unrelateInstancesRelatedBy(SimulatedInstanceIdentifier associationIdentifier) {
		this._relationshipStorage.unralateInstancesRelatedBy(associationIdentifier);
	}

	public void checkRelationsForCardinalityViolations() {
		
		this._relationshipStorage.setCurrentListOfSimulatedInstanceAs(this._classA.getInstanceIds());
		this._relationshipStorage.setCurrentListOfSimulatedInstanceBs(this._classB.getInstanceIds());
		
		if(needToValidateLowerA())
		{
			for(SimulatedInstanceIdentifier instanceId : this._relationshipStorage.getInstanceAsThatFailLowerBoundCardinality())
			{
				this.raiseCardinalityException(true, true, instanceId);
			}
		}
		if(needToValidateLowerB())
		{
			for(SimulatedInstanceIdentifier instanceId : this._relationshipStorage.getInstanceBsThatFailLowerBoundCardinality())
			{
				this.raiseCardinalityException(false, true, instanceId);
			}
		}
		if(needToValidateUpperA())
		{
			for(SimulatedInstanceIdentifier instanceId : this._relationshipStorage.getInstanceAsThatFailUpperBoundCardinality())
			{
				this.raiseCardinalityException(true, false, instanceId);
			}
		}
		if(needToValidateUpperB())
		{
			for(SimulatedInstanceIdentifier instanceId : this._relationshipStorage.getInstanceBsThatFailUpperBoundCardinality())
			{
				this.raiseCardinalityException(false, false, instanceId);
			}
		}
	}

	private void raiseCardinalityException(boolean isInstanceA, boolean isLowerBound, SimulatedInstanceIdentifier instanceId) {
		RelationCardinalityException relationException = new RelationCardinalityException();
		relationException.setRelation(this);
		relationException.setWasInstanceA(isInstanceA);
		relationException.setWasLowerBound(isLowerBound);
		relationException.setInstanceId(instanceId);
		throw(relationException);
	}

	public void setRelationshipStorage(NonReflexiveRelationshipStorage relationshipStorage) {
		this._relationshipStorage = relationshipStorage;
	}

}
