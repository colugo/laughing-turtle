package main.java.avii.editor.contracts.metamodel.entities.datatypes;

import main.java.avii.editor.contracts.metamodel.entities.helpers.datatypes.EntityTypes.AttributeBaseImplementationType;

public interface IEntityDatatype {
	public AttributeBaseImplementationType getBaseType();
	public String getDefaultValue();
	public Object getDefaultValueAsObject();
	public String getHumanName();
	public boolean acceptsValue(String value);
	public boolean canBeSetToDatatype(IEntityDatatype otherDatatype);
	public boolean canBeComparedToDatatype(IEntityDatatype otherDatatype);
	public boolean canBeCombinedWithDatatype(IEntityDatatype otherDatatype);
	public IEntityDatatype getResultingDatatypeWhenCombinedWith(IEntityDatatype otherDatatype);
	public Object correctTypeOf(Object value);
	public String getValueForInsertionInCode(String attributeValue);
	public String getLookupValue(String priorValue);
}
