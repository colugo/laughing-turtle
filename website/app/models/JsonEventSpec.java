package models;

import java.util.ArrayList;

import main.java.avii.editor.metamodel.entities.EntityEventInstance;
import main.java.avii.editor.metamodel.entities.EntityEventParam;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;

public class JsonEventSpec {
	public String _name;
	public String _uuid;
	
	public ArrayList<JsonEventInstance> _instances = new ArrayList<JsonEventInstance>();
	public ArrayList<JsonEventParam> _params = new ArrayList<JsonEventParam>();
	
	public JsonEventSpec(EntityEventSpecification theSpec) {
		this._name = theSpec.getName();
		this._uuid = theSpec.getId();
		for(EntityEventParam param : theSpec.getEventParams()){
			this._params.add(new JsonEventParam(param));
		}
		for(EntityEventInstance instance : theSpec.getKlass().getEventInstancesForSpecification(theSpec)){
			this._instances.add(new JsonEventInstance(instance));
		}
		
	}

	
}
