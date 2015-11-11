package main.java.avii.scenario;

public class TestVectorRelationInstance {

	private TestVectorRelationTable _table;
	private String _endA = null;
	private String _endB = null;
	private String _assoc = null;

	public TestVectorRelationInstance(TestVectorRelationTable testVectorRelationTable) {
		this._table = testVectorRelationTable;
	}

	public void setEndA(String endAInstance) {
		this._endA = endAInstance;		
	}

	public void setEndB(String endBInstance) {
		this._endB = endBInstance;
	}

	public String getEndA() {
		return this._endA;
	}

	public String getEndB() {
		return this._endB;
	}

	public TestVectorRelationTable getTable() {
		return this._table;
	}

	public void setAssociation(String associationInstance) {
		this._assoc = associationInstance;
	}

	public String getAssociation() {
		return this._assoc;
	}
	
	public boolean hasAssociation()
	{
		return this._assoc != null;
	}

}
