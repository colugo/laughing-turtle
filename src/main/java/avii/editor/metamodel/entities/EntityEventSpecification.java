/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.entities;

import java.util.ArrayList;

import javax.naming.NameNotFoundException;

public class EntityEventSpecification {

	private ArrayList<String> _generatingClasses = null;
	private String _name;
	private ArrayList<EntityEventParam> _eventParams = new ArrayList<EntityEventParam>();
	private EntityClass _klass;
	private String _id;

	public EntityEventSpecification(EntityClass klass, String eventName) {
		this._name = eventName;
		this.setId(this._name);
		this._klass = klass;

		klass.addEventSpecification(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_generatingClasses == null) ? 0 : _generatingClasses.hashCode());
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntityEventSpecification other = (EntityEventSpecification) obj;
		if (!getId().equals(other.getId()))
			return false;
		if (!getParamString().equals(other.getParamString()))
			return false;
		return true;
	}

	public void setClass(EntityClass klass) {
		this._klass = klass;
	}

	public EntityClass getKlass() {
		return _klass;
	}

	public String toString() {
		return _name;
	}

	public void setGeneratingClasses(ArrayList<String> gc) {
		this._generatingClasses = gc;
	}

	public ArrayList<String> getGeneratingClasses() {
		return _generatingClasses;
	}

	public ArrayList<EntityEventParam> getEventParams() {
		return _eventParams;
	}

	public String getName() {
		return _name;
	}
	
	public void setName(String name){
		this._name = name;
	}

	public String getParamString() {
		String out = "";
		if (_eventParams.isEmpty()) {
			return "()";
		}
		for (EntityEventParam eventParam : _eventParams) {
			out += eventParam.getName() + ":" + eventParam.getType() + ", ";
		}
		out = out.substring(0, out.length() - 2);
		return "(" + out + ")";
	}

	public boolean hasParamWithName(String theParamName) {
		for (EntityEventParam param : _eventParams) {
			if (param.getName().equals(theParamName)) {
				return true;
			}
		}
		return false;
	}

	public EntityEventParam getParamWithName(String theParamName) throws NameNotFoundException {
		for (EntityEventParam param : _eventParams) {
			if (param.getName().equals(theParamName)) {
				return param;
			}
		}
		throw new NameNotFoundException("Could not find EntityEventParam with name - " + theParamName);
	}

	public void addEventParam(EntityEventParam theParam) {
		if (hasParamWithName(theParam.getName())) {
			try {
				removeEventParamWithName(theParam.getName());
			} catch (NameNotFoundException e) {
				// this had better work
			}
		}
		_eventParams.add(theParam);
		theParam.setEvent(this);
	}

	public void removeEventParamWithName(String theParamName) throws NameNotFoundException {
		if (!hasParamWithName(theParamName)) {
			throw new NameNotFoundException("Could not find EntityEventParam with name - " + theParamName);
		}
		ArrayList<EntityEventParam> newParams = new ArrayList<EntityEventParam>();
		for (EntityEventParam param : _eventParams) {
			if (!param.getName().equals(theParamName)) {
				newParams.add(param);
			}
		}
		_eventParams = newParams;
	}

	public void addEventInstance(EntityEventInstance entityEventInstance) {
		this._klass.addEventInstance(this,entityEventInstance);		
	}

	public void removeEventInstance(EntityEventInstance eventInstance) throws NameNotFoundException {
		this._klass.removeEventInstance(this,eventInstance);		
	}

	public boolean isEquivalentTo(EntityEventSpecification subsequentSpec) {
		if(this.getEventParams().size() != subsequentSpec.getEventParams().size())
		{
			return false;
		}
		
		for(EntityEventParam sourceParam : this._eventParams)
		{
			if(!subsequentSpec.getEventParams().contains(sourceParam))
			{
				return false;
			}
		}
		
		return true;
	}

	public void setId(String id){
		this._id = id;
	}
	
	public String getId() {
		return this._id;
	}

	public boolean hasParamWithId(String theParamId) {
		for (EntityEventParam param : _eventParams) {
			if (param.getId().equals(theParamId)) {
				return true;
			}
		}
		return false;
	}

	public EntityEventParam getParamWithId(String paramId) {
		for (EntityEventParam param : _eventParams) {
			if (param.getId().equals(paramId)) {
				return param;
			}
		}
		return null;
	}

	public void removeEventParamWithId(String paramId) {
		EntityEventParam param = this.getParamWithId(paramId);
		this._eventParams.remove(param);
	}
}
