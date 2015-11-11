package main.java.avii.simulator.exceptions;

import main.java.avii.simulator.relations.SimulatedRelationship;

@SuppressWarnings("serial")
public class DuplicateRelationFoundException extends SimulationException {

	private String _instanceAName;
	private String _instanceBName;
	private SimulatedRelationship _relationship;

	public void setInstanceAName(String instanceAName) {
		this._instanceAName = instanceAName;		
	}

	public void setInstanceBName(String instanceBName) {
		this._instanceBName = instanceBName;		
	}

	public void setRelation(SimulatedRelationship simulatedRelationship) {
		this._relationship = simulatedRelationship;
	}

	@Override
	public String getMessage()
	{
		String message = "Duplicate relation(" + this._relationship.getName() + ") found between " + this._instanceAName + " and " + this._instanceBName;
		return message;
	}
	
}
