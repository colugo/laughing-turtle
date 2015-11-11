package main.java.avii.diagnostics;

import java.util.Collection;
import java.util.HashMap;

import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.Node;

import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class RelationshipDiagnostics {

	HashMap<SimulatedInstanceIdentifier, DiagnosticRelaionForInstance> _relationMap = new HashMap<SimulatedInstanceIdentifier, DiagnosticRelaionForInstance>(); 
	
	public void calculateRelationships(Collection<SimulatedRelationship> collection) {
		for(SimulatedRelationship relationship : collection)
		{
			RelationDiagnosticItemsForRelation diagnosticsForRelation = relationship.getRelationshipStorage().getDiagnosticRelationItems();
			for(SimulatedInstanceIdentifier identifier : diagnosticsForRelation.getInstances())
			{
				if(!hasRelationsForInstance(identifier))
				{
					_relationMap.put(identifier, new DiagnosticRelaionForInstance());
				}
				
				DiagnosticRelaionForInstance drfi = _relationMap.get(identifier);
				drfi.addRelations(relationship, diagnosticsForRelation.relationsForInstance(identifier));
			}
		}
	}

	public boolean hasRelationsForInstance(SimulatedInstanceIdentifier identifier) {
		return this._relationMap.containsKey(identifier);
	}

	public DiagnosticRelaionForInstance getRelationsForInstance(SimulatedInstanceIdentifier identifier) {
		return this._relationMap.get(identifier);
	}

	public Node serialise() {
		IIOMetadataNode relationshipsNode = new IIOMetadataNode("relationships");
		for(SimulatedInstanceIdentifier identifier : this._relationMap.keySet())
		{
			relationshipsNode.appendChild(this._relationMap.get(identifier).serialise(identifier));
		}
		return relationshipsNode;
	}

}
