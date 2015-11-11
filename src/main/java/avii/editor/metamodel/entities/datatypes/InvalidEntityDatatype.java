package main.java.avii.editor.metamodel.entities.datatypes;

import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.contracts.metamodel.entities.helpers.datatypes.EntityTypes.AttributeBaseImplementationType;

public class InvalidEntityDatatype implements IEntityDatatype {

	
	public AttributeBaseImplementationType getBaseType() {
		return AttributeBaseImplementationType.STRING;
	}

	public String getDefaultValue() {
		return "";
	}

	public boolean acceptsValue(String value) {
		return false;
	}
	
	private static InvalidEntityDatatype instance = new InvalidEntityDatatype();
	public static InvalidEntityDatatype getInstance()
	{
		return instance;
	}
	
	@Override
	public boolean equals(Object o)
	{
		return false;
	}
	
	public boolean canBeSetToDatatype(IEntityDatatype otherDatatype) {
		return false;
	}

	public boolean canBeComparedToDatatype(IEntityDatatype otherDatatype) {
		return false;
	}
	
	public IEntityDatatype getResultingDatatypeWhenCombinedWith(IEntityDatatype otherDatatype) {
		return InvalidEntityDatatype.getInstance();
	}
	
	public String getHumanName() {
		return "Invalid Datatype";
	}

	public Object getDefaultValueAsObject() {
		return null;
	}

	public Object correctTypeOf(Object value) {
		// I don't think anything has to happen here
		return value;
	}
	
	public boolean canBeCombinedWithDatatype(IEntityDatatype otherDatatype) {
		return false;
	}
	
	public String getValueForInsertionInCode(String attributeValue) {
		return attributeValue;
	}
	
	public String getLookupValue(String priorValue) {
		return priorValue;
	}
}
