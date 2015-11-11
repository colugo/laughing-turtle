package main.java.avii.editor.metamodel.weaving.exception;

import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.weaving.ClassWeave;

@SuppressWarnings("serial")
public class ClassNotInWeaveException extends Exception {

	private EntityClass _entityClass = null;
	private ClassWeave _classWeave;

	public ClassNotInWeaveException(EntityClass theEntityClass, ClassWeave theClassWeave) {
		this._entityClass = theEntityClass;
		this._classWeave = theClassWeave;
	}

	@Override
	public String toString() {
		String message = "Class : '"
				+ this._entityClass.getName()
				+ "' is not registered in ClassWeave #"+ this._classWeave.getWeaveIdentifier() +".";
		return message;
	}
}
