package main.java.avii.diagnostics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class RelationDiagnosticItemsForRelation {
	HashMap<SimulatedInstanceIdentifier, ArrayList<RelationInstanceDiagnosticItem>> _relations = new HashMap<SimulatedInstanceIdentifier, ArrayList<RelationInstanceDiagnosticItem>>(); 

	public boolean hasRelationsForInstane(SimulatedInstanceIdentifier identifier) {
		boolean hasRelations = this._relations.containsKey(identifier);
		return hasRelations;
	}

	public ArrayList<RelationInstanceDiagnosticItem> relationsForInstance(SimulatedInstanceIdentifier instance) {
		ArrayList<RelationInstanceDiagnosticItem> list = this._relations.get(instance);
		return list;
	}

	public void addDiagnosticForInstance(SimulatedInstanceIdentifier identifier, RelationInstanceDiagnosticItem relationDiagnosticItem) {
		if(!this._relations.containsKey(identifier))
		{
			this._relations.put(identifier, new ArrayList<RelationInstanceDiagnosticItem>());
		}
		this._relations.get(identifier).add(relationDiagnosticItem);
	}

	public Collection<SimulatedInstanceIdentifier> getInstances() {
		return this._relations.keySet();
	}

}
