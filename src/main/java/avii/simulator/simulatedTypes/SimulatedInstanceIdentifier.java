package main.java.avii.simulator.simulatedTypes;

public class SimulatedInstanceIdentifier{
	private int _sequence;
	private SimulatedClass _simulatedClass = null;
	private String _className;
	
	public SimulatedInstanceIdentifier(int sequence, SimulatedClass simulatedClass)
	{
		this._sequence = sequence;
		this._simulatedClass = simulatedClass;
	}
	
	@Override
	public String toString()
	{
		if(this._className == null)
		{
			return this._simulatedClass.getName() + "_" + String.format("%05d", this._sequence);
		}
		return this._className + "_" + String.format("%05d", this._sequence); 
	}
	
	public SimulatedClass getSimulatedClass()
	{
		return this._simulatedClass;
	}
	
	public int getSequence()
	{
		return this._sequence;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof SimulatedInstanceIdentifier)
		{
			SimulatedInstanceIdentifier otherId = (SimulatedInstanceIdentifier) other;
			if(otherId._sequence != this._sequence)
			{
				return false;
			}
			if(otherId._simulatedClass != (this._simulatedClass))
			{
				return false;
			}
			return true;
		}
		return false;
	}

	public void setSequence(int nextClassSequence) {
		this._sequence = nextClassSequence;		
	}

	public void setSimulatedClass(SimulatedClass classType) {
		this._simulatedClass = classType;
	}

	public void setClassName(String className) {
		this._className = className;
	}

}
