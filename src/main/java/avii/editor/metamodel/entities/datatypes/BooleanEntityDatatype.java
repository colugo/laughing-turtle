package main.java.avii.editor.metamodel.entities.datatypes;

import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.contracts.metamodel.entities.helpers.datatypes.EntityTypes.AttributeBaseImplementationType;

public class BooleanEntityDatatype implements IEntityDatatype {

	private static String regex = "true|TRUE|True|false|FALSE|False";
	
	public AttributeBaseImplementationType getBaseType() {
		return AttributeBaseImplementationType.BOOLEAN;
	}

	public String getDefaultValue() {
		return "FALSE";
	}

	public boolean acceptsValue(String value) {
		return value.toUpperCase().matches(regex);
	}
	
	private static BooleanEntityDatatype instance = new BooleanEntityDatatype();
	public static BooleanEntityDatatype getInstance()
	{
		return instance;
	}
	
	@Override
	public boolean equals(Object o)
	{
		return(o instanceof BooleanEntityDatatype);
	}

	public boolean canBeSetToDatatype(IEntityDatatype otherDatatype) {
		if(otherDatatype.getClass() == this.getClass())
		{
			return true;
		}
		return false;
	}

	public boolean canBeComparedToDatatype(IEntityDatatype otherDatatype) {
		if(otherDatatype.getClass() == this.getClass())
		{
			return true;
		}
		return false;
	}
	
	public IEntityDatatype getResultingDatatypeWhenCombinedWith(IEntityDatatype otherDatatype) {
		if(otherDatatype.getClass() == this.getClass())
		{
			return this;
		}
		if(otherDatatype.getClass() == StringEntityDatatype.class)
		{
			return otherDatatype;
		}
		return InvalidEntityDatatype.getInstance();
	}

	public String getHumanName() {
		return "boolean";
	}

	public Object getDefaultValueAsObject() {
		return false;
	}

	public Object correctTypeOf(Object value) {
		if(value instanceof String)
		{
			String strValue = (String) value;
			strValue = strValue.toLowerCase();
			if(strValue.equals("false"))
			{
				return false;
			}
			return true;
		}
		return value;
	}

	public boolean canBeCombinedWithDatatype(IEntityDatatype otherDatatype) {
		if(otherDatatype.getClass() == this.getClass())
		{
			return true;
		}
		if(otherDatatype.getClass() == StringEntityDatatype.class)
		{
			return true;
		}
		return false;
	}

	public String getValueForInsertionInCode(String attributeValue) {
		return attributeValue;
	}

	public String getLookupValue(String priorValue) {
		return priorValue;
	}
}
