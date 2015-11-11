package main.java.avii.simulator.exceptions;

import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

@SuppressWarnings("serial")
public class RelationCardinalityException extends BaseException {
	private SimulatedRelationship _relationship;
	private boolean _wasInstanceA;
	private boolean _wasLowerBound;
	private SimulatedInstanceIdentifier _instanceId;

	public void setRelation(SimulatedRelationship simulatedRelationship) {
		this._relationship = simulatedRelationship;
	}

	public void setWasInstanceA(boolean isInstanceA) {
		this._wasInstanceA = isInstanceA;
	}

	public void setWasLowerBound(boolean isLowerBound) {
		this._wasLowerBound = isLowerBound;
	}

	public void setInstanceId(SimulatedInstanceIdentifier instanceId) {
		this._instanceId = instanceId;		
	}
	
	@Override
	public String getMessage()
	{
		String message = this.getViolatingClassName() + getCardinalityString() + this.getOtherClassName() + getRelationString();
		return message;
	}

	
	private String getRelationString() {
		return " across " + this._relationship.getName();
	}

	private String getCardinalityString() {
		String cardinality = " was not related to a ";
		if(!this._wasLowerBound)
		{
			cardinality = " related to more then 1 "; 
		}
		return cardinality;
	}

	private String getViolatingClassName()
	{
		String className = this._relationship.getClassB().getName();
		if(this._relationship.isReflexive())
		{
			className += ".\"" + this._relationship.getVerbB() + "\"";
		}
		if(this._wasInstanceA)
		{
			className = this._relationship.getClassA().getName();
			if(this._relationship.isReflexive())
			{
				className += ".\"" + this._relationship.getVerbA() + "\"";
			}
		}
		
		String instanceName = null;
		try
		{
			instanceName = this._relationship.getSimulator().getSimulatingState().getNameForInstanceId(_instanceId);
			if(instanceName != null)
			{
				className += "(" + instanceName + ")";
			}
			else
			{
				className += "(#" + this._instanceId.toString() + ")";
			}
		}catch(Exception e)
		{
		}

		return className;
	}
	
	private String getOtherClassName()
	{
		String className = this._relationship.getClassA().getName();
		if(this._relationship.isReflexive())
		{
			className += ".\"" + this._relationship.getVerbA() + "\"";
		}
		if(this._wasInstanceA)
		{
			className = this._relationship.getClassB().getName();
			if(this._relationship.isReflexive())
			{
				className += ".\"" + this._relationship.getVerbB() + "\"";
			}
		}
		return className;
	}

}
