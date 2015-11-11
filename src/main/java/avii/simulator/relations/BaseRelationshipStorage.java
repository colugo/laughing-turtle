package main.java.avii.simulator.relations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import main.java.avii.diagnostics.RelationDiagnosticItemsForRelation;
import main.java.avii.diagnostics.RelationInstanceDiagnosticItem;
import main.java.avii.simulator.exceptions.DuplicateRelationFoundException;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;
import main.java.avii.simulator.simulatedTypes.SimulatedState;
import main.java.avii.util.SetHelper;

public abstract class BaseRelationshipStorage {

	protected SimulatedRelationship _simulatedRelationship;
	protected HashMap<SimulatedInstanceIdentifier, HashMap<SimulatedInstanceIdentifier, SimulatedRelationInstance>> _instanceAMap = new HashMap<SimulatedInstanceIdentifier, HashMap<SimulatedInstanceIdentifier, SimulatedRelationInstance>>();
	protected HashMap<SimulatedInstanceIdentifier, HashMap<SimulatedInstanceIdentifier, SimulatedRelationInstance>> _instanceBMap = new HashMap<SimulatedInstanceIdentifier, HashMap<SimulatedInstanceIdentifier, SimulatedRelationInstance>>();
	protected HashMap<SimulatedInstanceIdentifier, SimulatedRelationInstance> _associationMap = new HashMap<SimulatedInstanceIdentifier, SimulatedRelationInstance>();
	protected Collection<SimulatedInstanceIdentifier> _listOfSimulatedInstanceBs = new ArrayList<SimulatedInstanceIdentifier>();
	protected Collection<SimulatedInstanceIdentifier> _listOfSimulatedInstanceAs = new ArrayList<SimulatedInstanceIdentifier>();

	public abstract void storeRelationInstance(SimulatedRelationInstance relationInstance);

	protected void getDiagnosticItemsForEndOfRelation(RelationDiagnosticItemsForRelation diagnosticsForRelation,
			HashMap<SimulatedInstanceIdentifier, HashMap<SimulatedInstanceIdentifier, SimulatedRelationInstance>> instanceMap) {
		for (SimulatedInstanceIdentifier identifier : instanceMap.keySet()) {
			HashMap<SimulatedInstanceIdentifier, SimulatedRelationInstance> relationsForInstance = instanceMap.get(identifier);
			for (SimulatedInstanceIdentifier relatedInstanceIdentifier : relationsForInstance.keySet()) {
				RelationInstanceDiagnosticItem relationDiagnosticItem = new RelationInstanceDiagnosticItem();
				relationDiagnosticItem.setRelatedInstance(relatedInstanceIdentifier);
				SimulatedRelationInstance relationInstance = relationsForInstance.get(relatedInstanceIdentifier);
				if (this._simulatedRelationship.hasAssociation()) {
					relationDiagnosticItem.setAssociationInstance(relationInstance.getAssociationInstance());
				}
				this.addAdditionalInformationForRelation(relationDiagnosticItem, instanceMap);
				diagnosticsForRelation.addDiagnosticForInstance(identifier, relationDiagnosticItem);
			}
		}
	}

	protected abstract void addAdditionalInformationForRelation(RelationInstanceDiagnosticItem relationDiagnosticItem,
			HashMap<SimulatedInstanceIdentifier, HashMap<SimulatedInstanceIdentifier, SimulatedRelationInstance>> instanceMap);

	public void throwDuplicateRelationException(SimulatedInstanceIdentifier instanceA, SimulatedInstanceIdentifier instanceB) {
		DuplicateRelationFoundException exception = new DuplicateRelationFoundException();
		SimulatedState currentState = this._simulatedRelationship.getSimulator().getSimulatingState();
		String instanceAName = currentState.getNameForInstanceId(instanceA);
		String instanceBName = currentState.getNameForInstanceId(instanceB);
		exception.setInstanceAName(instanceAName);
		exception.setInstanceBName(instanceBName);
		exception.setRelation(this._simulatedRelationship);
		throw exception;
	}

	@SuppressWarnings("unchecked")
	public Collection<SimulatedInstanceIdentifier> getInstanceBsThatFailLowerBoundCardinality() {
		return (Collection<SimulatedInstanceIdentifier>) SetHelper.difference(this._listOfSimulatedInstanceBs, this._instanceBMap.keySet());
	}

	@SuppressWarnings("unchecked")
	public Collection<SimulatedInstanceIdentifier> getInstanceAsThatFailLowerBoundCardinality() {
		return (Collection<SimulatedInstanceIdentifier>) SetHelper.difference(this._listOfSimulatedInstanceAs, this._instanceAMap.keySet());
	}

	public void setCurrentListOfSimulatedInstanceAs(Collection<SimulatedInstanceIdentifier> instanceAList) {
		this._listOfSimulatedInstanceAs = instanceAList;
	}

	public void setCurrentListOfSimulatedInstanceBs(Collection<SimulatedInstanceIdentifier> instanceBList) {
		this._listOfSimulatedInstanceBs = instanceBList;
	}

	public void removeInstanceRelationFromMap(HashMap<SimulatedInstanceIdentifier, HashMap<SimulatedInstanceIdentifier, SimulatedRelationInstance>> map,
			SimulatedInstanceIdentifier instance1, SimulatedInstanceIdentifier instance2) {
		if (map.get(instance1).size() == 1) {
			map.remove(instance1);
		} else {
			map.get(instance1).remove(instance2);
		}
	}

	public void unralateInstancesRelatedBy(SimulatedInstanceIdentifier associationIdentifier) {
		SimulatedRelationInstance associationRelation = this._associationMap.get(associationIdentifier);
		SimulatedInstanceIdentifier a = associationRelation.getInstanceA();
		SimulatedInstanceIdentifier b = associationRelation.getInstanceB();
		removeInstanceRelationFromMap(this._instanceAMap, a, b);
		removeInstanceRelationFromMap(this._instanceBMap, b, a);
	}

	public RelationDiagnosticItemsForRelation getDiagnosticRelationItems() {
		RelationDiagnosticItemsForRelation diagnosticsForRelation = new RelationDiagnosticItemsForRelation();

		getDiagnosticItemsForEndOfRelation(diagnosticsForRelation, this._instanceAMap);
		getDiagnosticItemsForEndOfRelation(diagnosticsForRelation, this._instanceBMap);

		return diagnosticsForRelation;
	}

	public Collection<SimulatedInstanceIdentifier> getInstanceAsThatFailUpperBoundCardinality() {
		return getUpperBoundFailingInstances(this._instanceAMap);
	}

	public Collection<SimulatedInstanceIdentifier> getInstanceBsThatFailUpperBoundCardinality() {
		return getUpperBoundFailingInstances(this._instanceBMap);
	}

	private Collection<SimulatedInstanceIdentifier> getUpperBoundFailingInstances(
			HashMap<SimulatedInstanceIdentifier, HashMap<SimulatedInstanceIdentifier, SimulatedRelationInstance>> instanceMap) {
		HashSet<SimulatedInstanceIdentifier> fails = new HashSet<SimulatedInstanceIdentifier>();

		for (SimulatedInstanceIdentifier instance : instanceMap.keySet()) {
			if (instanceMap.get(instance).size() > 1) {
				fails.add(instance);
			}
		}
		return fails;
	}

}