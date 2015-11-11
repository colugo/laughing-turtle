/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.entities;

import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;

public class EntityEventParam {

	private EntityEventSpecification _event;
	private String _name;
	private IEntityDatatype _type;
	private String _id;

	public void setEvent(EntityEventSpecification event)
	{
		this._event = event;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public IEntityDatatype getType()
	{
		return _type;
	}
		
	public EntityEventSpecification getEvent()
	{
		return _event;
	}
	
	
	public EntityEventParam(String paramName, IEntityDatatype paramType)
	{
		this._name = paramName;
		this._id = this._name;
		this._type = paramType;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntityEventParam other = (EntityEventParam) obj;
		if (_name == null) {
			if (other._name != null)
				return false;
		} else if (!_name.equals(other._name))
			return false;
		if (_type == null) {
			if (other._type != null)
				return false;
		} else if (!_type.getClass().equals(other._type.getClass()))
			return false;
		return true;
	}

	public String getId() {
		return this._id;
	}
	public void setId(String id){
		this._id = id;
	}

	public void setName(String newName) {
		this._name = newName;
	}

	public void setType(IEntityDatatype datatype) {
		this._type = datatype;
	}


	
	
	

}
