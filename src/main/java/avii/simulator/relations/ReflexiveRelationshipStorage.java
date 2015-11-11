package main.java.avii.simulator.relations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import main.java.avii.diagnostics.RelationInstanceDiagnosticItem;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyClass;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class ReflexiveRelationshipStorage extends BaseRelationshipStorage {

	public ReflexiveRelationshipStorage(SimulatedRelationship simulatedRelationship) {
		this._simulatedRelationship = simulatedRelationship;
	}

	public boolean isVerbClassA(String verb) {
		boolean isVerbForClassA = verb.equals(this._simulatedRelationship.getVerbA());
		return isVerbForClassA;
	}

	public boolean hasRelationForInstance(SimulatedInstanceIdentifier instanceIdentifier, String verbPhrase) {
		boolean hasRelation = false;
		if (isVerbClassA(verbPhrase)) {
			hasRelation = this._instanceAMap.containsKey(instanceIdentifier);
		} else {
			hasRelation = this._instanceBMap.containsKey(instanceIdentifier);
		}
		return hasRelation;
	}

	public void storeRelationInstance(SimulatedRelationInstance relationInstance) {
		SimulatedInstanceIdentifier instanceA = relationInstance.getInstanceA();
		SimulatedInstanceIdentifier instanceB = relationInstance.getInstanceB();

		String verb = this._simulatedRelationship.getVerbA();

		if (this.doesRelationExistBetween(instanceA, verb, instanceB)) {
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

	public Collection<SimulatedRelationInstance> getRelationsForInstance(SimulatedInstanceIdentifier instanceIdentifier, String verbPhrase) {
		Collection<SimulatedRelationInstance> relationsForInstance = null;

		if (isVerbClassA(verbPhrase)) {
			relationsForInstance = this._instanceAMap.get(instanceIdentifier).values();
		} else {
			relationsForInstance = this._instanceBMap.get(instanceIdentifier).values();
		}
		return relationsForInstance;
	}

	public boolean doesRelationExistBetween(SimulatedInstanceIdentifier instance1, String verb1, SimulatedInstanceIdentifier instance2, String verb2) {
		boolean doesRelationExist = false;

		if (!hasRelationForInstance(instance1, verb1)) {
			return false;
		}

		if (!hasRelationForInstance(instance2, verb2)) {
			return false;
		}

		if (isVerbClassA(verb1)) {
			doesRelationExist = this._instanceAMap.get(instance1).containsKey(instance2);
		} else {
			doesRelationExist = this._instanceBMap.get(instance1).containsKey(instance2);
		}

		return doesRelationExist;
	}

	public void unrelateInstances(SimulatedInstanceIdentifier instance1, String verb1, SimulatedInstanceIdentifier instance2) {
		if (isVerbClassA(verb1)) {
			removeInstanceRelationFromMap(this._instanceAMap, instance1, instance2);
			removeInstanceRelationFromMap(this._instanceBMap, instance2, instance1);
		} else {
			removeInstanceRelationFromMap(this._instanceAMap, instance2, instance1);
			removeInstanceRelationFromMap(this._instanceBMap, instance1, instance2);
		}

	}

	public boolean doesRelationExistBetween(SimulatedInstanceIdentifier firstInstanceIdentifier, String firstVerb,
			SimulatedInstanceIdentifier secondInstanceIdentifier) {
		boolean doesRelationExist = false;

		if (!hasRelationForInstance(firstInstanceIdentifier, firstVerb)) {
			return false;
		}

		if (!hasRelationForInstance(secondInstanceIdentifier, this._simulatedRelationship.getOtherVerb(firstVerb))) {
			return false;
		}

		if (isVerbClassA(firstVerb)) {
			doesRelationExist = this._instanceAMap.get(firstInstanceIdentifier).containsKey(secondInstanceIdentifier);
		} else {
			doesRelationExist = this._instanceBMap.get(firstInstanceIdentifier).containsKey(secondInstanceIdentifier);
		}

		return doesRelationExist;
	}

	public SimulatedInstanceIdentifier getAssociationInstance(SimulatedInstanceIdentifier instance1, String verb1, SimulatedInstanceIdentifier instance2) {
		SimulatedInstanceIdentifier assoc = null;

		if (!doesRelationExistBetween(instance1, verb1, instance2)) {
			return null;
		}

		if (isVerbClassA(verb1)) {
			assoc = this._instanceAMap.get(instance1).get(instance2).getAssociationInstance();
		} else {
			assoc = this._instanceBMap.get(instance1).get(instance2).getAssociationInstance();
		}
		return assoc;
	}

	public Collection<SimulatedInstanceIdentifier> getOtherEnd(SimulatedInstanceIdentifier instance, String verb) {
		if (!this.hasRelationForInstance(instance, verb)) {
			return new ArrayList<SimulatedInstanceIdentifier>();
		}
		if (isVerbClassA(verb)) {
			return this._instanceAMap.get(instance).keySet();
		}
		return this._instanceBMap.get(instance).keySet();
	}

	public Collection<SimulatedInstanceIdentifier> getOtherEnd(Collection<SimulatedInstanceIdentifier> instances, String verb) {
		HashSet<SimulatedInstanceIdentifier> set = new HashSet<SimulatedInstanceIdentifier>();
		for (SimulatedInstanceIdentifier instance : instances) {
			if (instance.getSimulatedClass() instanceof SimulatedHierarchyClass) {
				SimulatedInstanceIdentifier instanceInRelationship = this._simulatedRelationship.getSimulatedInstanceInRelationship(instance, verb)
						.getIdentifier();
				instance = instanceInRelationship;
				if (instance == null) {
					continue;
				}
			}

			if (this.hasRelationForInstance(instance, verb)) {
				if (isVerbClassA(verb)) {
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
		if(instanceMap == this._instanceAMap)
		{
			relationDiagnosticItem.setVerb(this._simulatedRelationship.getVerbA());
		}
		else
		{
			relationDiagnosticItem.setVerb(this._simulatedRelationship.getVerbB());
		}
	}

}
