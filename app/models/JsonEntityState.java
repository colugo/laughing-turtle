package models;

import main.java.avii.editor.metamodel.entities.EntityState;

public class JsonEntityState {
	public String _uuid;
	public String _name;
	public double _x;
	public double _y;

	public JsonEntityState(EntityState theState) {
		this._uuid = theState.getId();
		this._x = theState.x;
		this._y = theState.y;
		this._name = theState.getName();
	}

}
