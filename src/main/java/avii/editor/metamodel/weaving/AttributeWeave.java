package main.java.avii.editor.metamodel.weaving;

import java.util.HashMap;

import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;

public class AttributeWeave {

	private HashMap<EntityClass,EntityAttribute> _attributes = new HashMap<EntityClass,EntityAttribute>();
	private int _id;

	public AttributeWeave(int id) {
		this._id = id;
	}

	public int getId()
	{
		return this._id;
	}
	
	public boolean isAttributeInWeave(EntityAttribute theAttribute) {
		return this._attributes.values().contains(theAttribute);
	}

	public void addAttribute(EntityAttribute theAttribute) {
		EntityClass theClass = theAttribute.getOwningClass();
		if(this._attributes.isEmpty())
		{
			this._attributes.put(theClass, theAttribute);
			return;
		}
		
		IEntityDatatype typeOfAttributesInWeave = getIEntityDatatTypeForWeave();
		if(theAttribute.getType().getClass().equals(typeOfAttributesInWeave.getClass()))
		{
			this._attributes.put(theClass, theAttribute);
		}
	}

	public IEntityDatatype getIEntityDatatTypeForWeave() {
		IEntityDatatype typeOfAttributesInWeave = this._attributes.values().iterator().next().getType();
		return typeOfAttributesInWeave;
	}

	@Override
	public int hashCode() {
		return _id;
	}

}
