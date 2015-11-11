package main.java.avii.editor.metamodel.entities.datatypes;

import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.contracts.metamodel.entities.helpers.datatypes.EntityTypes.AttributeBaseImplementationType;
import main.java.avii.util.Text;

public class StringEntityDatatype implements IEntityDatatype {

	private static String regex = "\".*\"";
	
	public AttributeBaseImplementationType getBaseType() {
		return AttributeBaseImplementationType.STRING;
	}

	public String getDefaultValue() {
		return "";
	}

	public boolean acceptsValue(String value) {
		return value.matches(regex);
	}
	
	private static StringEntityDatatype instance = new StringEntityDatatype();
	public static StringEntityDatatype getInstance()
	{
		return instance;
	}
	
	@Override
	public boolean equals(Object o)
	{
		return(o instanceof StringEntityDatatype);
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
		if(otherDatatype.getClass() == InvalidEntityDatatype.class)
		{
			return otherDatatype;
		}
		return this;
	}
	
	public String getHumanName() {
		return "string";
	}

	public Object getDefaultValueAsObject() {
		return "";
	}
	

	public Object correctTypeOf(Object value) {
		// I don't think anything has to happen here
		return value;
	}
	
	public boolean canBeCombinedWithDatatype(IEntityDatatype otherDatatype) {
		if(otherDatatype.getClass() == InvalidEntityDatatype.class)
		{
			return false;
		}
		return true;
	}
	
	public String getValueForInsertionInCode(String attributeValue) {
		return "\"" + attributeValue + "\"";
	}

	public String getLookupValue(String priorValue) {
		return Text.quote(priorValue);
	}
	
}
