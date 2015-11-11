/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.entities;

import javax.naming.NameAlreadyBoundException;

import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;

public class EntityAttribute {

	private String _name;
	private String _id;
	private IEntityDatatype _type;
	private EntityClass _class;
	public static final String STATE_ATTRIBUTE_NAME = "state";
		
	public EntityAttribute(String name,IEntityDatatype	 type)
	{
		this._id = name;
		this._name = name;
		this._type = type;
	}

	public String toString(){
		String asString = "";
		asString += this._class.getName() + ":" + this._name;
		return asString;
	}
	
	public String getName() {
		return _name;
	}
	
	public void setClass(EntityClass theClass)
	{
		this._class = theClass;
	}

	public void rename(String name) throws NameAlreadyBoundException{
		if(this._class.hasAttribute(name))
		{
			throw new NameAlreadyBoundException("An attribute with name : " + name + " already exists");
		}
		_name = name;
	}

	public IEntityDatatype getType() {
		return _type;
	}

	public void setType(IEntityDatatype type) {
		this._type = type;
	}

	public boolean acceptsValue(String value) {
		return _type.acceptsValue(value);
	}

	public EntityClass getOwningClass() {
		return this._class;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_name == null) ? 0 : _name.hashCode());
		result = prime * result + ((_type == null) ? 0 : _type.getClass().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EntityAttribute))
			return false;
		
		EntityAttribute other = (EntityAttribute) obj;
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

	public void setId(String attributeId) {
		this._id = attributeId;		
	}

	public boolean isNotStateAttribute() {
		return !this._name.equals(STATE_ATTRIBUTE_NAME);
	}

}
