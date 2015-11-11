package main.java.avii.simulator.relations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import main.java.avii.diagnostics.RelationInstanceDiagnosticItem;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyClass;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class NonReflexiveRelationshipStorage extends BaseRelationshipStorage {

	public NonReflexiveRelationshipStorage(SimulatedRelationship simulatedRelationship) {
		this._simulatedRelationship = simulatedRelationship;
	}

	public void storeRelationInstance(SimulatedRelationInstance relationInstance) {
		SimulatedInstanceIdentifier instanceA = relationInstance.getInstanceA();
		SimulatedInstanceIdentifier instanceB = relationInstance.getInstanceB();

		if (this.doesRelationExistBetween(instanceA, instanceB)) {
			throwDuplicateRelationException(instanceA, instanceB);
		}

		preSeedRelationMapsForInstances(instanceA, instanceB);
		this._instanceAMap.get(instanceA).put(instanceB, relationInstance);
		this._instanceBMap.get(instanceB).put(instanceA, relationInstance);
		if (this._simulatedRelationship.hasAssociation()) {
			this._associationMap.put(relationInstance.getAssociationInstance(), relationInstance);
		}
	}

	private void preSeedRelationMapsForInstances(SimulatedInstanceIdentifier instanceA, SimulatedInstanceIdentifier instanceB) {
		if (!this._instanceAMap.containsKey(instanceA)) {
			this._instanceAMap.put(instanceA, new HashMap<SimulatedInstanceIdentifier, SimulatedRelationInstance>());
		}
		if (!this._instanceBMap.containsKey(instanceB)) {
			this._instanceBMap.put(instanceB, new HashMap<SimulatedInstanceIdentifier, SimulatedRelationInstance>());
		}
	}

	public boolean hasRelationForInstance(SimulatedInstanceIdentifier instanceIdentifier) {
		boolean hasRelation = false;
		if (isInstanceClassA(instanceIdentifier)) {
			hasRelation = this._instanceAMap.containsKey(instanceIdentifier);
		} else {
			hasRelation = this._instanceBMap.containsKey(instanceIdentifier);
		}
		return hasRelation;
	}

	public boolean isInstanceClassA(SimulatedInstanceIdentifier instanceIdentifier) {
		SimulatedClass simulatedClassForInstanceIdentifier = instanceIdentifier.getSimulatedClass();
		boolean isSimulatedClassForInstanceClassA = simulatedClassForInstanceIdentifier.equals(this._simulatedRelationship.getClassA());
		return isSimulatedClassForInstanceClassA;
	}

	public Collection<SimulatedRelationInstance> getRelationsForInstance(SimulatedInstanceIdentifier instanceIdentifier) {
		Collection<SimulatedRelationInstance> relationsForInstance = null;

		if (isInstanceClassA(instanceIdentifier)) {
			relationsForInstance = this._instanceAMap.get(instanceIdentifier).values();
		} else {
			relationsForInstance = this._instanceBMap.get(instanceIdentifier).values();
		}
		return relationsForInstance;
	}

	public boolean doesRelationExistBetween(SimulatedInstanceIdentifier firstInstanceIdentifier, SimulatedInstanceIdentifier secondInstanceIdentifier) {
		boolean doesRelationExist = false;

		if (!hasRelationForInstance(firstInstanceIdentifier)) {
			return false;
		}

		if (!hasRelationForInstance(secondInstanceIdentifier)) {
			return false;
		}

		if (isInstanceClassA(firstInstanceIdentifier)) {
			doesRelationExist = this._instanceAMap.get(firstInstanceIdentifier).containsKey(secondInstanceIdentifier);
		} else {
			doesRelationExist = this._instanceBMap.get(firstInstanceIdentifier).containsKey(secondInstanceIdentifier);
		}

		return doesRelationExist;
	}

	public void unrelateInstances(SimulatedInstanceIdentifier instance1, SimulatedInstanceIdentifier instance2) {
		if (isInstanceClassA(instance1)) {
			removeInstanceRelationFromMap(this._instanceAMap, instance1, instance2);
			removeInstanceRelationFromMap(this._instanceBMap, instance2, instance1);
		} else {
			removeInstanceRelationFromMap(this._instanceAMap, instance2, instance1);
			removeInstanceRelationFromMap(this._instanceBMap, instance1, instance2);
		}

	}

	public SimulatedInstanceIdentifier getAssociationInstance(SimulatedInstanceIdentifier instance1, SimulatedInstanceIdentifier instance2) {
		SimulatedInstanceIdentifier assoc = null;

		if (!doesRelationExistBetween(instance1, instance2)) {
			return null;
		}

		if (isInstanceClassA(instance1)) {
			assoc = this._instanceAMap.get(instance1).get(instance2).getAssociationInstance();
		} else {
			assoc = this._instanceBMap.get(instance1).get(instance2).getAssociationInstance();
		}
		return assoc;
	}

	public Collection<SimulatedInstanceIdentifier> getOtherEnd(SimulatedInstanceIdentifier instance) {
		if (!this.hasRelationForInstance(instance)) {
			return new ArrayList<SimulatedInstanceIdentifier>();
		}
		if (isInstanceClassA(instance)) {
			return this._instanceAMap.get(instance).keySet();
		}
		return this._instanceBMap.get(instance).keySet();
	}

	public Collection<SimulatedInstanceIdentifier> getOtherEnd(Collection<SimulatedInstanceIdentifier> instances) {
		HashSet<SimulatedInstanceIdentifier> set = new HashSet<SimulatedInstanceIdentifier>();
		for (SimulatedInstanceIdentifier instance : instances) {
			if (instance.getSimulatedClass() instanceof SimulatedHierarchyClass) {
				SimulatedInstanceIdentifier instanceInRelationship = this._simulatedRelationship.getSimulatedInstanceInRelationship(instance).getIdentifier();
				instance = instanceInRelationship;
			}

			if (this.hasRelationForInstance(instance)) {
				if (isInstanceClassA(instance)) {
					set.addAll(this._instanceAMap.get(instance).keySet());
				} else {
					set.addAll(this._instanceBMap.get(instance).keySet());
				}
			}
		}
		return set;
	}

	@Override
	protected void addAdditionalInformationForRelation(RelationInstanceDiagnosticItem relationDiagnosticItem,
			HashMap<SimulatedInstanceIdentifier, HashMap<SimulatedInstanceIdentifier, SimulatedRelationInstance>> instanceMap) {
		// do nothing
	}

}
