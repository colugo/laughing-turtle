package main.java.avii.editor.metamodel.entities.datatypes;

import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.contracts.metamodel.entities.helpers.datatypes.EntityTypes.AttributeBaseImplementationType;

public class IntegerEntityDatatype implements IEntityDatatype {

	private static String regex = "(-)?\\d+";
	
	public AttributeBaseImplementationType getBaseType() {
		return AttributeBaseImplementationType.NUMERIC;
	}

	public String getDefaultValue() {
		return "0";
	}

	public boolean acceptsValue(String value) {
		return value.matches(regex);
	}
	
	
	private static IntegerEntityDatatype instance = new IntegerEntityDatatype();
	public static IntegerEntityDatatype getInstance()
	{
		return instance;
	}
	
	@Override
	public boolean equals(Object o)
	{
		return(o instanceof IntegerEntityDatatype);
	}

	public boolean canBeSetToDatatype(IEntityDatatype otherDatatype) {
		if(otherDatatype.getClass() == this.getClass())
		{
			return true;
		}
		return false;
	}

	public boolean canBeComparedToDatatype(IEntityDatatype otherDatatype) {
		if(otherDatatype.getClass() == this.getClass() || otherDatatype.getClass() == FloatingEntityDatatype.class)
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
		if(otherDatatype.getClass() == FloatingEntityDatatype.class)
		{
			return otherDatatype;
		}
		if(otherDatatype.getClass() == StringEntityDatatype.class)
		{
			return otherDatatype;
		}
		return InvalidEntityDatatype.getInstance();
	}
	
	public String getHumanName() {
		return "int";
	}

	public Object getDefaultValueAsObject() {
		return 0;
	}

	public Object correctTypeOf(Object value) {
		if(value instanceof Integer)
		{
			return value;
		}
		if(value instanceof String)
		{
			return Integer.parseInt((String)value);
		}
		try
		{	double floatVal = (Double)value;
			int realVal = (int)floatVal;
			return realVal;
		}
		catch(ClassCastException cce)
		{
		}
		
		return value;
	}

	public boolean canBeCombinedWithDatatype(IEntityDatatype otherDatatype) {
		if(otherDatatype.getClass() == this.getClass())
		{
			return true;
		}
		if(otherDatatype.getClass() == FloatingEntityDatatype.class)
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
