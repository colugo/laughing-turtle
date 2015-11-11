package main.java.avii.editor.metamodel.actionLanguage.validation;

import java.util.ArrayList;
import java.util.HashMap;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_ReclassifyInstanceToClass;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.AllSubClassesDeclareSameAttributeValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.AssociationClassCannotHaveSubClassesValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.EntityClassCannotHaveIslandStatesValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.EntityEventParamaterIsNotUsedValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.EntityStateCannotHaveMultipleExitingTransitionsForTheSameEventSpecificationValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.EntityStateCannotHaveMultipleTriggeringEventSpecificationsWithDifferentSignaturesValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.FirstInstructionOfGeneralisationStatemachineMustBeReclassifyValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.GeneralisationClassWithStatesMustHaveInitialStateValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.IValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.InitialStateCannotHaveActionLanguageValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.InitialStateCannotHaveTriggeringEventValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.ReflexiveEntityRelationMustHaveDifferingVerbPhrasesValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.ReflexiveEntityRelationMustHaveVerbPhrasesValidationError;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityEventInstance;
import main.java.avii.editor.metamodel.entities.EntityEventParam;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityState;

public class EntityClassValidator {

	private EntityClass _class = null;
	private ArrayList<IValidationError> _errors = new ArrayList<IValidationError>();

	public EntityClassValidator(EntityClass theClass) {
		this._class = theClass;
	}

	private void addError(IValidationError error) {
		if (!doesErrorListHaveErrorAlready(error)) {
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

	public boolean validate() throws InvalidActionLanguageSyntaxException {
		this._errors = new ArrayList<IValidationError>();

		this.getStructuralValidatoinErrors();
		this.getValidationErrorsFromActionLanguage();

		return this._errors.isEmpty();
	}

	private void getStructuralValidatoinErrors() {
		checkForIslandStates();
		checkInitialStateHasNoTriggeringEventsForNonGeneralisationClasses();
		checkAllStatesOnlyHaveTriggeringEventSpecsWithIdenticalSignatures();
		checkAllEventSpecificationsForThisClassHaveAllTheirParamatersRead();
		checkAllGeneralisationClassesHaveReclassifyAsFirstSyntax();
		checkAllStatesHaveMaximumOfOneExitingInstanceOfAnyEventSpec();
		checkAllGeneralisationClassesWithStatesHaveAnInitialState();
		checkInitialStateDoesNotHaveActionLanguage();
		checkAllReflexiveRelationsHaveNonNullVerbPhrases();
		checkAllReflexiveRelationsHaveDifferingVerbPhrases();
		checkAssociationClassDoesNotHaveSubClasses();
		checkAllSubClassesDontDeclareTheSameAttribute();
	}

	private void checkAllSubClassesDontDeclareTheSameAttribute() {
		if(this._class.getsubClasses().isEmpty())
		{
			return;
		}
		
		ArrayList<EntityClass> subClasses = this._class.getsubClasses();
		int subClassCount = subClasses.size();
		HashMap<EntityAttribute, Integer> attributeUsage = new HashMap<EntityAttribute, Integer>();
		
		for(EntityClass subclass : subClasses)
		{
			for(EntityAttribute attribute : subclass.getAttributes())
			{
				if(attribute.getName().equals(EntityAttribute.STATE_ATTRIBUTE_NAME))
				{
					continue;
				}
				if(!attributeUsage.containsKey(attribute))
				{
					attributeUsage.put(attribute, 0);
				}
				int attributeCount = attributeUsage.get(attribute) + 1;
				attributeUsage.put(attribute, attributeCount);
			}
		}
		for(EntityAttribute attribute : attributeUsage.keySet())
		{
			int attributeCount = attributeUsage.get(attribute);
			if(attributeCount == subClassCount)
			{
				addError(new AllSubClassesDeclareSameAttributeValidationError(this._class, attribute.getName()));
			}
		}
	}

	private void checkAssociationClassDoesNotHaveSubClasses() {
		if(this._class.isAssociation())
		{
			if(this._class.hasSubClasses())
			{
				addError(new AssociationClassCannotHaveSubClassesValidationError(this._class));
			}
		}
	}

	private void checkAllReflexiveRelationsHaveDifferingVerbPhrases() {
		for(EntityRelation relation : this._class.getRelations())
		{
			if(!relation.isReflexive())
			{
				continue;
			}
			
			if(relation.getClassAVerb().equals("") || relation.getClassAVerb().equals("") || relation.getClassAVerb() == null || relation.getClassBVerb() == null)
			{
				continue;
			}
			
			if(relation.getClassAVerb().equals(relation.getClassBVerb()))
			{
				addError(new ReflexiveEntityRelationMustHaveDifferingVerbPhrasesValidationError(relation));
			}
		}
	}

	private void checkAllReflexiveRelationsHaveNonNullVerbPhrases() {
		for(EntityRelation relation : this._class.getRelations())
		{
			if(!relation.isReflexive())
			{
				continue;
			}
			
			if(relation.getClassAVerb().equals("") || relation.getClassAVerb().equals("") || relation.getClassAVerb() == null || relation.getClassBVerb() == null)
			{
				addError(new ReflexiveEntityRelationMustHaveVerbPhrasesValidationError(relation));
			}
		}
	}

	private void checkInitialStateDoesNotHaveActionLanguage() {
		// only sub classes ( or classes not in heirarchies) need worry about this
		if(this._class.getsubClasses().isEmpty())
		{
			if (this._class.hasStates()) {
				EntityState initialState = this._class.getInitialState();
				if(initialState != null)
				{
					EntityProcedure initialProcedure = initialState.getProcedure();
					if(initialProcedure != null)
					{
						if(initialProcedure.hasContent())
						{
							addError(new InitialStateCannotHaveActionLanguageValidationError(initialState));
						}
					}
				}
			}
		}
	}

	private void checkAllGeneralisationClassesWithStatesHaveAnInitialState() {
		if (this._class.isGeneralisation()) {
			if(this._class.hasStates())
			{
				if(this._class.getInitialState() == null)
				{
					addError(new GeneralisationClassWithStatesMustHaveInitialStateValidationError(this._class));
				}
			}
		}
	}

	private void checkAllStatesHaveMaximumOfOneExitingInstanceOfAnyEventSpec() {
		for (EntityState state : this._class.getStates()) {
			HashMap<EntityEventSpecification, Integer> countOfExitingSpecs = new HashMap<EntityEventSpecification, Integer>();
			ArrayList<EntityEventInstance> fromThisStateInstances = state.getFromThisStateInstances();
			for(EntityEventInstance instance : fromThisStateInstances)
			{
				EntityEventSpecification spec = instance.getSpecification();
				if(!countOfExitingSpecs.containsKey(spec))
				{
					countOfExitingSpecs.put(spec, 0);
				}
				int currentCount = countOfExitingSpecs.get(spec);
				countOfExitingSpecs.put(spec, currentCount + 1);
			}
			for(EntityEventSpecification spec : countOfExitingSpecs.keySet())
			{
				int currentCount = countOfExitingSpecs.get(spec);
				if(currentCount > 1)
				{
					addError(new EntityStateCannotHaveMultipleExitingTransitionsForTheSameEventSpecificationValidationError(state, spec));
				}
			}
		}
	}

	private void checkAllGeneralisationClassesHaveReclassifyAsFirstSyntax() {
		if (this._class.isGeneralisation()) {
			for (EntityState state : this._class.getStates()) {
				EntityProcedure procedure = state.getProcedure();
				if (procedure.hasContent()) {
					try {
						IActionLanguageSyntax firstEffectiveSyntax = procedure.getFirstEffectiveSyntax();
						if (!(firstEffectiveSyntax instanceof Syntax_ReclassifyInstanceToClass)) {
							addError(new FirstInstructionOfGeneralisationStatemachineMustBeReclassifyValidationError(state));
						}
					} catch (InvalidActionLanguageSyntaxException e) {
					}
				}
			}
		}
	}

	private void checkAllEventSpecificationsForThisClassHaveAllTheirParamatersRead() {
		for (EntityEventSpecification eventSpecification : this._class.getEventSpecifications()) {
			for (EntityEventParam eventParam : eventSpecification.getEventParams()) {
				boolean wasEventParamaterUsed = false;
				for (EntityState state : this._class.getStates()) {
					EntityProcedure procedure = state.getProcedure();
					try {
						wasEventParamaterUsed = procedure.doesProcedureReadEventParam(eventSpecification, eventParam);
					} catch (InvalidActionLanguageSyntaxException e) {
						// this validation error is picked up elsewhere
					}
					if (wasEventParamaterUsed) {
						break;
					}
				}
				if (!wasEventParamaterUsed) {
					addError(new EntityEventParamaterIsNotUsedValidationError(this._class, eventSpecification, eventParam));
				}
			}
		}
	}

	private void checkAllStatesOnlyHaveTriggeringEventSpecsWithIdenticalSignatures() {
		for (EntityState state : this._class.getStates()) {
			ArrayList<EntityEventSpecification> triggeringSpecs = state.getAllTriggeringEventSpecs();
			if (triggeringSpecs.size() > 1) {
				EntityEventSpecification firstSpec = triggeringSpecs.get(0);

				for (EntityEventSpecification subsequentSpec : triggeringSpecs) {
					if (!firstSpec.isEquivalentTo(subsequentSpec)) {
						addError(new EntityStateCannotHaveMultipleTriggeringEventSpecificationsWithDifferentSignaturesValidationError(this._class, state,
								firstSpec, subsequentSpec));
					}
				}
			}
		}
	}

	private void checkInitialStateHasNoTriggeringEventsForNonGeneralisationClasses() {
		if(this._class.isGeneralisation())
		{
			// generalisation state machines can do this
			return;
		}
		if (this._class.hasStates()) {
			EntityState initialState = this._class.getInitialState();
			if(initialState != null)
			{
			if (initialState.hasRcvdEvent()) {
				EntityEventSpecification rcvdEvent = null;
				try {
						rcvdEvent = initialState.getRcvdEvent();
					} catch (Exception e) {
					}
					addError(new InitialStateCannotHaveTriggeringEventValidationError(this._class, initialState, rcvdEvent));
				}
			}
		}
	}

	private void checkForIslandStates() {
		// every state (except the initial(all initials for generalisation)) must have atleast 1 to event
		// instance (that isnt a reflexive event)
		for (EntityState state : this._class.getStates()) {
			if (!state.isInitial()) {
				if (!state.hasNonReflexiveTriggeringEvent()) {
					addError(new EntityClassCannotHaveIslandStatesValidationError(this._class, state));
				}
			}
		}
		
	}

	private void getValidationErrorsFromActionLanguage() throws InvalidActionLanguageSyntaxException {
		for (EntityState currentState : this._class.getStates()) {
			EntityProcedure currentProcedure = currentState.getProcedure();
			if (currentProcedure != null) {
				boolean currentProcdureIsValid = currentProcedure.validate();
				if (!currentProcdureIsValid) {
					this._errors.addAll(currentProcedure.getValidationErrors());
				}
			}
		}
	}

	public ArrayList<IValidationError> getValidationErrors() {
		return this._errors;
	}

}
