package main.java.avii.editor.metamodel.actionLanguage.validation.error.structure;

import main.java.avii.editor.metamodel.entities.EntityClass;

public class GeneralisationClassWithStatesMustHaveInitialStateValidationError implements IValidationError {

	private EntityClass _class;

	public GeneralisationClassWithStatesMustHaveInitialStateValidationError(EntityClass theClass) {
		this._class = theClass;
	}

	public String explainError() {
		String message = "Generalisation class '" + this._class.getName() + "' has states, but not an intial state.";
		return message;
	}

}
