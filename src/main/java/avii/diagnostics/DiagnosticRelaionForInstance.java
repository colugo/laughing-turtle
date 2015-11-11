package main.java.avii.diagnostics;

import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.Node;

import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class DiagnosticRelaionForInstance {

	private HashMap<SimulatedRelationship, ArrayList<RelationInstanceDiagnosticItem>> _relations = new HashMap<SimulatedRelationship, ArrayList<RelationInstanceDiagnosticItem>>();

	public boolean hasRelationsOverRelation(SimulatedRelationship relationship) {
		boolean hasRelations = this._relations.containsKey(relationship);
		return hasRelations;
	}

	public ArrayList<RelationInstanceDiagnosticItem> getRelationsOverRelation(SimulatedRelationship relationship) {
		ArrayList<RelationInstanceDiagnosticItem> relationsForRelation = this._relations.get(relationship);
		return relationsForRelation;
	}

	public void addRelations(SimulatedRelationship relationship, ArrayList<RelationInstanceDiagnosticItem> relationsForInstance) {
		this._relations.put(relationship, relationsForInstance);
	}

	public Node serialise(SimulatedInstanceIdentifier identifier) {
		IIOMetadataNode instanceNode = new IIOMetadataNode("instance");
		instanceNode.setAttribute("instanceId", identifier.toString());
		
		for(SimulatedRelationship relationship : this._relations.keySet())
		{
			IIOMetadataNode relationshipNode = new IIOMetadataNode("relation");
			relationshipNode.setAttribute("name", relationship.getName());
			
			for(RelationInstanceDiagnosticItem ridi : this._relations.get(relationship))
			{
				IIOMetadataNode relationshipInstanceNode = new IIOMetadataNode("relationship");
				if(relationship.isReflexive())
				{
					relationshipInstanceNode.setAttribute("verb", ridi.getVerb());
				}
				relationshipInstanceNode.setAttribute("instanceId", ridi.getRelatedInstance().toString());
				if(ridi.hasAssociation())
				{
					relationshipInstanceNode.setAttribute("associationInstanceId", ridi.getAssociation().toString());	
				}
				relationshipNode.appendChild(relationshipInstanceNode);
			}
			
			instanceNode.appendChild(relationshipNode);
		}
		
		return instanceNode;
	}

}
