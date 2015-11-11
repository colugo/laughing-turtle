package main.java.avii.simulator.exceptions;

import main.java.avii.editor.metamodel.actionLanguage.validation.EntityDomainValidator;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.IValidationError;

@SuppressWarnings("serial")
public class CannotSimulateDomainThatIsInvalidException extends Exception {

	
	private EntityDomainValidator _validator;

	public CannotSimulateDomainThatIsInvalidException(EntityDomainValidator validator) {
		this._validator = validator;
	}
	
	@Override public String toString()
	{
		String message = "Encountered errors when validating the domain:\n";
		for(IValidationError validationError : this._validator.getValidationErrors())
		{
			message += "\t" + validationError.explainError() + "\n";
		}
		
		return message;
	}

}
