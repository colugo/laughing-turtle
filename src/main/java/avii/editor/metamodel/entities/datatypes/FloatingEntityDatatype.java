package main.java.avii.editor.metamodel.entities.datatypes;

import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.contracts.metamodel.entities.helpers.datatypes.EntityTypes.AttributeBaseImplementationType;

public class FloatingEntityDatatype implements IEntityDatatype {

	private static String regex = "(-)?\\d+(\\.\\d+)?";
	
	public AttributeBaseImplementationType getBaseType() {
		return AttributeBaseImplementationType.NUMERIC;
	}

	public String getDefaultValue() {
		return "0.0";
	}

	public boolean acceptsValue(String value) {
		return value.matches(regex);
	}
	
	private static FloatingEntityDatatype instance = new FloatingEntityDatatype();
	public static FloatingEntityDatatype getInstance()
	{
		return instance;
	}
	
	@Override
	public boolean equals(Object o)
	{
		return(o instanceof FloatingEntityDatatype);
	}

	public boolean canBeSetToDatatype(IEntityDatatype otherDatatype) {
		if(otherDatatype.getClass() == this.getClass() || otherDatatype.getClass() == IntegerEntityDatatype.class)
		{
			return true;
		}
		return false;
	}

	public boolean canBeComparedToDatatype(IEntityDatatype otherDatatype) {
		if(otherDatatype.getClass() == this.getClass() || otherDatatype.getClass() == IntegerEntityDatatype.class)
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
		if(otherDatatype.getClass() == IntegerEntityDatatype.class)
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
		return "float";
	}

	public Object getDefaultValueAsObject() {
		return 0.0d;
	}

	public Object correctTypeOf(Object value) {
		if(value instanceof String)
		{
			return Double.parseDouble((String)value);
		}
		return value;
	}
	
	public boolean canBeCombinedWithDatatype(IEntityDatatype otherDatatype) {
		if(otherDatatype.getClass() == this.getClass())
		{
			return true;
		}
		if(otherDatatype.getClass() == IntegerEntityDatatype.class)
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
