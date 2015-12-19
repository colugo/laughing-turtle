package models;

import main.java.avii.editor.metamodel.entities.EntityEventParam;

public class JsonEventParam {
	public JsonEventParam(EntityEventParam param) {
		this._name = param.getName();
		this._uuid = param.getId();
		this._type = param.getType().getHumanName();
		
	}
	public String _name;
	public String _uuid;
	public String _type;
}
