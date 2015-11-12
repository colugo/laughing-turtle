package models;

import java.util.ArrayList;

import main.java.avii.editor.metamodel.entities.EntityClass;

public class JsonEntityClass {
	public String _name;
	public String _uuid;
	public ArrayList<JsonEntityAttribute> _attributes = new ArrayList<JsonEntityAttribute>();
	public double _x;
	public double _y;
	public String _superClassId;
	public int _superClassTriangleIndex;
	public ArrayList<JsonEntityState> _states = new ArrayList<JsonEntityState>();
	public ArrayList<JsonEventSpec> _specs = new ArrayList<JsonEventSpec>();


	public JsonEntityClass(String name, String uuid, Double x, Double y, int superClassTriangleIndex) {
		this._name = name;
		this._uuid = uuid;
		this._x = x;
		this._y = y;
		this._superClassTriangleIndex = superClassTriangleIndex;
	}

	public void setSuperClass(EntityClass entityClass) {
		this._superClassId = entityClass.getId();
	}
}
