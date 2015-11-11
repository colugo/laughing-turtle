package main.java.avii.diagnostics;

import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;


public class RelationInstanceDiagnosticItem {

	SimulatedInstanceIdentifier _relatedInstance;
	SimulatedInstanceIdentifier _associationInstance = null;
	SimulatedRelationship _relation;
	private String _verb = null;
	
	
	public SimulatedInstanceIdentifier getRelatedInstance() {
		return _relatedInstance;
	}
	
	public void setRelatedInstance(SimulatedInstanceIdentifier relatedInstance) {
		this._relatedInstance = relatedInstance;
	}
	
	public SimulatedRelationship getRelation() {
		return _relation;
	}
	
	public void setRelation(SimulatedRelationship relation) {
		this._relation = relation;
	}

	public boolean hasAssociation() {
		return this._associationInstance != null;
	}

	public void setAssociationInstance(SimulatedInstanceIdentifier association) {
		this._associationInstance = association;
	}

	public SimulatedInstanceIdentifier getAssociation() {
		return this._associationInstance;
	}

	public void setVerb(String verb) {
		this._verb  = verb;
	}
	
	public String getVerb()
	{
		return this._verb;
	}

}
