package main.java.avii.simulator.relations;

import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyClass;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;


public class SimulatedRelationInstance {
	private SimulatedRelationship _simulatedRelationship;
	private SimulatedInstanceIdentifier _instanceA;
	private SimulatedInstanceIdentifier _instanceB;
	private SimulatedInstanceIdentifier _associationInstance = null;

	public SimulatedRelationInstance(SimulatedRelationship simulatedRelationship) {
		this._simulatedRelationship = simulatedRelationship;
	}

	public boolean isInsanceInRelation(SimulatedInstanceIdentifier instanceIdentifier) {
		
		if(instanceIdentifier.getSimulatedClass() instanceof SimulatedHierarchyClass)
		{
			SimulatedInstance instanceInRelationship = this._simulatedRelationship.getSimulatedInstanceInRelationship(instanceIdentifier);
			instanceIdentifier = instanceInRelationship.getIdentifier();
		}
		
		boolean isInstanceA = instanceIdentifier.equals(this._instanceA);
		boolean isInstanceB = instanceIdentifier.equals(this._instanceB);
		boolean isEitherInstance = isInstanceA || isInstanceB;
		return isEitherInstance;
	}
	
	public SimulatedRelationInstance relateNonReflexiveInstance(SimulatedInstanceIdentifier instanceIdentifier) {
		SimulatedClass simulatedClassOfIdentifier = instanceIdentifier.getSimulatedClass();
		
		if(simulatedClassOfIdentifier instanceof SimulatedHierarchyClass)
		{
			SimulatedInstance instanceInRelationship = this._simulatedRelationship.getSimulatedInstanceInRelationship(instanceIdentifier);
			simulatedClassOfIdentifier = instanceInRelationship.getSimulatedClass();
			instanceIdentifier = instanceInRelationship.getIdentifier();
		}
		
		boolean isSimulatedClassOfIdentifierClassA = this._simulatedRelationship.getClassA().equals(simulatedClassOfIdentifier);
		if(isSimulatedClassOfIdentifierClassA)
		{
			this._instanceA = instanceIdentifier;
		}
		else
		{
			this._instanceB = instanceIdentifier;
		}
		
		// easy to chain creation of relations
		return this;
	}
	
	public void relateReflexiveInstance(SimulatedInstanceIdentifier instanceIdentifier, String verbPhrase) {
		SimulatedClass simulatedClassOfIdentifier = instanceIdentifier.getSimulatedClass();
		if(simulatedClassOfIdentifier instanceof SimulatedHierarchyClass)
		{
			SimulatedInstance instanceInRelationship = this._simulatedRelationship.getSimulatedInstanceInRelationship(instanceIdentifier, verbPhrase);
			simulatedClassOfIdentifier = instanceInRelationship.getSimulatedClass();
			instanceIdentifier = instanceInRelationship.getIdentifier();
		}
		
		
		boolean isVerbPhraseClassAVerbPhrase = this._simulatedRelationship.getVerbA().equals(verbPhrase);
		if(isVerbPhraseClassAVerbPhrase)
		{
			this._instanceA = instanceIdentifier;
		}
		else
		{
			this._instanceB = instanceIdentifier;
		}
	}

	public SimulatedInstanceIdentifier getInstanceA() {
		return this._instanceA;
	}
	
	public SimulatedInstanceIdentifier getInstanceB() {
		return this._instanceB;
	}

	public SimulatedInstanceIdentifier getReflexiveInstance(String verbPhrase) {
		boolean isVerbPhraseClassAVerbPhrase = this._simulatedRelationship.getVerbA().equals(verbPhrase);
		if(isVerbPhraseClassAVerbPhrase)
		{
			return this._instanceA;
		}
		return this._instanceB;
	}

	public SimulatedInstanceIdentifier getAssociationInstance() {
		if(this._associationInstance == null)
		{
			SimulatedClass associationClass = this._simulatedRelationship.getAssociationClass();
			SimulatedInstance associationInstance = associationClass.createInstance();
			this._associationInstance = associationInstance.getIdentifier();
		}
		return this._associationInstance;
	}
}
