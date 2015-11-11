package main.java.avii.editor.metamodel.weaving.exception;

import main.java.avii.editor.metamodel.entities.EntityClass;

@SuppressWarnings("serial")
public class CannotAddSuperClassToClassWeaveException extends Exception {

	private EntityClass _superClass = null;

	public CannotAddSuperClassToClassWeaveException(EntityClass theSuperClass) {
		this._superClass = theSuperClass;
	}

	@Override
	public String toString() {
		String message = "SuperClass : '"
				+ this._superClass.getName()
				+ "' cannot be added to ClassWeave. When one of the registered classes is instantiated, the others are automatically instantiated. SuperClasses cannot be instantiated, thus cannot be added to a ClassWeave.";
		return message;
	}
}
