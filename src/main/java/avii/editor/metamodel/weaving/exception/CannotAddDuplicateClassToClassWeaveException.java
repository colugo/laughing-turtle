package main.java.avii.editor.metamodel.weaving.exception;

import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.weaving.ClassWeave;

@SuppressWarnings("serial")
public class CannotAddDuplicateClassToClassWeaveException extends Exception {

	private EntityClass _entityClass = null;
	private ClassWeave _classWeave;

	public CannotAddDuplicateClassToClassWeaveException(EntityClass theEntityClass, ClassWeave classWeave) {
		this._entityClass = theEntityClass;
		this._classWeave = classWeave;
	}

	@Override
	public String toString() {
		String message = "Class : '"
				+ this._entityClass.getName()
				+ "' already is registered in ClassWeave #"+ this._classWeave.getWeaveIdentifier() +".";
		return message;
	}
}
