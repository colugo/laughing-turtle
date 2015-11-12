package models;

public class JsonEntityAttribute {
	public JsonEntityAttribute(String name, String id, String humanName) {
		this._name = name;
		this._uuid = id;
		this._type = humanName;
	}
	public String _name;
	public String _uuid;
	public String _type;
}
