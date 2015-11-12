package models;

public class JsonEntityRelation {
	public String _name;
	public String _uuid;
	public String _endAUuid;
	public String _endBUuid;
	public String _verbA;
	public String _cardinalityA;
	public String _verbB;
	public String _cardinalityB;
	public String _associationId = null;
	public int _indexA;
	public int _indexB;
	public int _verbAOffsetX;
	public int _verbAOffsetY;
	public int _verbBOffsetX;
	public int _verbBOffsetY;
	
	public JsonEntityRelation(String name, String uuid, String endA, String endB, String verbA, String cardinalityA, String verbB, String cardinalityB, int indexA, int indexB, int verbAOffsetX, int verbAOffsetY, int verbBOffsetX, int verbBOffsetY)
	{
		this._name = name;
		this._uuid = uuid;
		this._endAUuid = endA;
		this._endBUuid = endB;
		this._verbA = verbA;
		this._cardinalityA = cardinalityA;
		this._verbB = verbB;
		this._cardinalityB = cardinalityB;
		this._indexA = indexA;
		this._indexB = indexB;
		this._verbAOffsetX = verbAOffsetX;
		this._verbAOffsetY = verbAOffsetY;
		this._verbBOffsetX = verbBOffsetX;
		this._verbBOffsetY = verbBOffsetY;
	}

	public void setAssociation(String associationId) {
		this._associationId = associationId;
	}
}
