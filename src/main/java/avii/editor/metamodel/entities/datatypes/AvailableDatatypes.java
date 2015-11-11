package main.java.avii.editor.metamodel.entities.datatypes;

import java.util.ArrayList;

import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.contracts.metamodel.entities.helpers.datatypes.CouldNotDetermineDatatypeFromValueException;

public class AvailableDatatypes {
	@SuppressWarnings("serial")
	private static ArrayList<IEntityDatatype> availableDatatypes = new ArrayList<IEntityDatatype>()
	{{
		add(new BooleanEntityDatatype());
		// I would like all ints to be ints and not floats, so put ints first
		add(new IntegerEntityDatatype());
		add(new FloatingEntityDatatype());
		add(new StringEntityDatatype());
	}};
	
	public static IEntityDatatype getDatatypeForInput(String value) throws CouldNotDetermineDatatypeFromValueException
	{
		for(IEntityDatatype datatype : availableDatatypes)
		{
			if(datatype.acceptsValue(value))
			{
				return datatype;
			}
		}
		throw new CouldNotDetermineDatatypeFromValueException(value);
	}
	
	public static IEntityDatatype getInstanceBasedOnClassName(String className)
	{
		IEntityDatatype datatype = InvalidEntityDatatype.getInstance();
		
		for(IEntityDatatype dt : availableDatatypes)
		{
			if(dt.getClass().getName().equals(className))
			{
				return dt;
			}
		}
		
		return datatype;
	}

	public static IEntityDatatype getDatatypeForHumanName(String newTypeHumanName) {
		IEntityDatatype datatype = InvalidEntityDatatype.getInstance();
		for(IEntityDatatype dt : availableDatatypes)
		{
			if(dt.getHumanName().equals(newTypeHumanName))
			{
				datatype = dt;
				break;
			}
		}
		return datatype;
	}
}
