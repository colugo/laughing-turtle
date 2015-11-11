package test.java.mock;

import main.java.avii.simulator.relations.SimulatedRelationship;

public class MockSimulatedRelation extends SimulatedRelationship {
	private String _name;

	public MockSimulatedRelation(String name){
		this._name = name;
	}
	
	@Override
	public String getName()
	{
		return this._name;
	}
}
