package main.java.avii.editor.metamodel.actionLanguage.validation;

import java.util.ArrayList;
import java.util.HashMap;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.EntityAttributeIsNotReadValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.EntityAttributeIsNotSetValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.IValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.IValidationWarning;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.MultipleSuperClassesWithSameLineageValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.UnknownValidationError;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.scenario.AssertionTestVectorProcedure;
import main.java.avii.scenario.TestScenario;
import main.java.avii.scenario.TestVector;
import main.java.avii.scenario.TestVectorProcedure;

public class EntityDomainValidator {

	private EntityDomain _domain = null;
	private ArrayList<IValidationError> _errors = new ArrayList<IValidationError>();
	private ArrayList<IValidationWarning> _warnings = new ArrayList<IValidationWarning>();
	

	public EntityDomainValidator(EntityDomain theDomain) {
		this._domain = theDomain;
	}

	private void addError(IValidationError error) {
		if(error instanceof IValidationWarning)
		{
			IValidationWarning warning = (IValidationWarning) error;
			if(!doesWarningListHaveWarningAlready(warning)) {
				_warnings.add(warning);
			}
		}
		else if (!doesErrorListHaveErrorAlready(error)) {
			_errors.add(error);
		}
	}
	
	private boolean doesErrorListHaveErrorAlready(IValidationError theNewError) {
		String newErrorString = theNewError.explainError();
		for (IValidationError error : _errors) {
			if (error.explainError().equals(newErrorString)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean doesWarningListHaveWarningAlready(IValidationWarning theNewWarning) {
		String newWarningString = theNewWarning.explainError();
		for (IValidationWarning warning : _warnings) {
			if (warning.explainError().equals(newWarningString)) {
				return true;
			}
		}
		return false;
	}
	

	public boolean validate(){
		this._errors = new ArrayList<IValidationError>();
		
		this.validateClassesInDomain();
		this.getStructuralValidationErrors();
		
		this.validateScenarios();
		
		return this._errors.isEmpty();
	}

	private void validateScenarios() {
		for(TestScenario scenario : this._domain.getScenarios())
		{
			for(TestVector vector : scenario.getVectors())
			{
				this.validateVector(vector);
			}
		}
	}

	private void validateVector(TestVector vector) {
		HashMap<String,EntityClass> tableLookup = null;
		if(vector.getInitialProcedure() != null)
		{
			validateInitialProcedure(vector);
			tableLookup = vector.calculateInstanceLookup();
		}
		if(vector.getAssertionProcedure() != null && tableLookup != null)
		{
			validateAssertionProcedure(vector, tableLookup);
		}
	}

	public void validateAssertionProcedure(TestVector vector, HashMap<String, EntityClass> tableLookup) {
		try {
			
			AssertionTestVectorProcedure assertionProcedure = vector.getAssertionProcedure();
			assertionProcedure.setupForValidation(tableLookup);
			
			boolean assertionProcedureIsValid = assertionProcedure.validate();
			if (!assertionProcedureIsValid) {
				this._errors.addAll(assertionProcedure.getValidationErrors());
			}
		} catch (InvalidActionLanguageSyntaxException e) {
			addError(new UnknownValidationError("Unable to create vector assertion procedure for vector : " + vector.getDescription()));
		}
	}
	
	public void validateInitialProcedure(TestVector vector) {
		try {
			TestVectorProcedure initialProcedure = vector.getInitialProcedure();
			boolean initialProcedureIsValid = initialProcedure.validate();
			if (!initialProcedureIsValid) {
				this._errors.addAll(initialProcedure.getValidationErrors());
			}
		} catch (InvalidActionLanguageSyntaxException e) {
			addError(new UnknownValidationError("Unable to create vector initial procedure for vector : " + vector.getDescription()));
		}
	}

	private void validateClassesInDomain()
	{
		for(EntityClass theClass : this._domain.getClasses())
		{
			EntityClassValidator classValidator = new EntityClassValidator(theClass);
			try {
				if(!classValidator.validate())
				{
					ArrayList<IValidationError> classErrors = classValidator.getValidationErrors();
					for(IValidationError classValidationError : classErrors)
					{
						addError(classValidationError);
					}
				}
			} catch (InvalidActionLanguageSyntaxException e) {
				addError(new UnknownValidationError("There is a severe error with the acition language in class : " + theClass.getName()));
			}
			
		}
	}
	
	private void getStructuralValidationErrors() {
		checkAllClassAttributesAreReadAtLeastOnce();
		checkAllClassAttributesAreSetAtLeastOnce();
		checkClassGeneralisationsDontShareCommonLineage();
	}

	private void checkClassGeneralisationsDontShareCommonLineage() {
		for(EntityClass theClass : this._domain.getClasses())
		{
			if(theClass.isGeneralisation())
			{
				ArrayList<EntityClass> superClasses = theClass.getSuperClasses();
				if(superClasses.size() > 1)
				{
					HashMap<EntityClass,EntityClass>  superClassMap = new HashMap<EntityClass,EntityClass> ();
					for(EntityClass superClass : superClasses)
					{
						ArrayList<EntityClass> superSuperClasses = superClass.getAllSuperClasses();
						for(EntityClass superSuperClass : superSuperClasses)
						{
							if(superClassMap.containsKey(superSuperClass))
							{
								EntityClass superClass1 = superClassMap.get(superSuperClass);
								EntityClass superClass2 = superClass;
								
								MultipleSuperClassesWithSameLineageValidationError error = new MultipleSuperClassesWithSameLineageValidationError(theClass, superClass1, superClass2, superSuperClass);
								addError(error);
							}
							else
							{
								superClassMap.put(superSuperClass, superClass);
							}
						}
					}
				}
			}
		}
	}

	private void checkAllClassAttributesAreReadAtLeastOnce() {
		for(EntityClass theClass : this._domain.getClasses())
		{
			for(EntityAttribute theAttribute : theClass.getAttributes())
			{
				if(theAttribute.getName().equals(EntityAttribute.STATE_ATTRIBUTE_NAME))
				{
					continue;
				}
				boolean wasAttributeRead = false;
				for(EntityProcedure procedure : getAllProceduresInDomain())
				{
					try {
						if(procedure.doesProcedureReadAttributeValue(theClass, theAttribute))
						{
							wasAttributeRead = true;
							break;
						}
					} catch (InvalidActionLanguageSyntaxException e) {
						// I dont care about this validation error here
					}
				}
				if(!wasAttributeRead)
				{
					addError(new EntityAttributeIsNotReadValidationError(theClass,theAttribute));
				}
			}
		}
	}
	
	private void checkAllClassAttributesAreSetAtLeastOnce() {
		for(EntityClass theClass : this._domain.getClasses())
		{
			for(EntityAttribute theAttribute : theClass.getAttributes())
			{
				if(theAttribute.getName().equals(EntityAttribute.STATE_ATTRIBUTE_NAME))
				{
					continue;
				}
				boolean wasAttributeRead = false;
				for(EntityProcedure procedure : getAllProceduresInDomain())
				{
					try {
						if(procedure.doesProcedureSetValueToAttribute(theClass, theAttribute))
						{
							wasAttributeRead = true;
							break;
						}
					} catch (InvalidActionLanguageSyntaxException e) {
						// I dont care about this validation error here
					}
				}
				if(!wasAttributeRead)
				{
					addError(new EntityAttributeIsNotSetValidationError(theClass,theAttribute));
				}
			}
		}
	}

	private ArrayList<EntityProcedure> getAllProceduresInDomain()
	{
		ArrayList<EntityProcedure> procedures = new ArrayList<EntityProcedure>();
		for(EntityClass theClass : this._domain.getClasses())
		{
			for(EntityState theState : theClass.getStates())
			{
				procedures.add(theState.getProcedure());
			}
		}
		return procedures;
	}
	
	public ArrayList<IValidationError> getValidationErrors() {
		return this._errors;
	}
	
	public ArrayList<IValidationWarning> getValidationWarnings() {
		return this._warnings;
	}

	public boolean hasWarnings() {
		return !this._warnings.isEmpty();
	}

	
}
