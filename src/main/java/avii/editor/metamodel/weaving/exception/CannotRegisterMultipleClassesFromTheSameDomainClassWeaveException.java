package main.java.avii.editor.metamodel.weaving.exception;

import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.weaving.ClassWeave;

@SuppressWarnings("serial")
public class CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException extends Exception {
	private ClassWeave _theClassWeave = null;
	private EntityClass _theEntityClass = null;
	private EntityClass _theExistingEntityClass = null;
	
	public CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException (ClassWeave theClassWeave, EntityClass theEntityClass, EntityClass theExistingClass)
	{
		this._theClassWeave = theClassWeave;
		this._theEntityClass = theEntityClass;
		this._theExistingEntityClass = theExistingClass;
	}
	
	@Override
	public String toString()
	{
		String message = "Cannot add class '" + this._theEntityClass.getName() + "' to ClassWeave #" + this._theClassWeave.getWeaveIdentifier() + " because another class from the same domain '" + this._theExistingEntityClass.getName() + "' already is registered.";
		
		return message;
	}
	
}
