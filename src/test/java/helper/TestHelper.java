package test.java.helper;

import javax.naming.NameAlreadyBoundException;

import junit.framework.Assert;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.command.history.EditorCommandHistorySerializer;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.validation.EntityClassValidator;
import main.java.avii.editor.metamodel.actionLanguage.validation.EntityDomainValidator;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.IValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.IValidationWarning;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.IActionLanguageValidationError;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventInstance;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;

public class TestHelper {
	public static int countValidationErrors(EntityProcedure procedure) throws InvalidActionLanguageSyntaxException {
		return procedure.getValidationErrors().size();
	}

	public static boolean checkValidationResultsForAnErrorOfType(EntityProcedure procedure, Class<?> errorClass) throws InvalidActionLanguageSyntaxException {
		boolean foundDesiredError = false;
		for (IActionLanguageValidationError error : procedure.getValidationErrors()) {

			if (error.getClass().equals(errorClass)) {
				foundDesiredError = true;
			}
		}
		return foundDesiredError;
	}
	
	public static boolean checkValidationResultsForAnErrorOfType(EntityClassValidator entityClassValidator, Class<?> errorClass) throws InvalidActionLanguageSyntaxException {
		boolean foundDesiredError = false;
		for (IValidationError error : entityClassValidator.getValidationErrors()) {

			if (error.getClass().equals(errorClass)) {
				foundDesiredError = true;
			}
		}
		return foundDesiredError;
	}

	public static void printValidationErrors(EntityProcedure procedure) throws InvalidActionLanguageSyntaxException {
		for (IActionLanguageValidationError error : procedure.getValidationErrors()) {
			System.out.println(error.getClass() + " -- " + error.explainError());
		}
	}
	
	public static void printValidationErrors(EntityClassValidator validator) throws InvalidActionLanguageSyntaxException {
		validator.validate();
		for (IValidationError error : validator.getValidationErrors()) {
			System.out.println(error.getClass() + " -- " + error.explainError());
		}
	}

	public static void printValidationErrors(EntityDomainValidator domainValidator) {
		domainValidator.validate();
		for (IValidationError error : domainValidator.getValidationErrors()) {
			System.out.println(error.getClass() + " -- " + error.explainError());
		}
	}

	public static boolean checkValidationResultsForAnErrorOfType(EntityDomainValidator domainValidator,	Class<?> errorClass) {
		boolean foundDesiredError = false;
		for (IValidationError error : domainValidator.getValidationErrors()) {

			if (error.getClass().equals(errorClass)) {
				foundDesiredError = true;
			}
		}
		return foundDesiredError;
	}

	
	public static void printSerialisedHistory(EditorCommandHistory editorCommandHistory) throws Exception
	{
		String serialisedInitialHistoryString = EditorCommandHistorySerializer.serialize(editorCommandHistory);
		System.out.println(serialisedInitialHistoryString);
	}
	
	public static EditorCommandHistory roundTripEditorHistoryCommandHelper(EditorCommandHistory editorCommandHistory) throws Exception
	{
		String serialisedInitialHistoryString = EditorCommandHistorySerializer.serialize(editorCommandHistory);
		EditorCommandHistory readInHistory = EditorCommandHistorySerializer.deSerialize(serialisedInitialHistoryString);
		
		// make sure that the read in history can be played
		readInHistory.playAll();
		
		String roundTripHistoryString = EditorCommandHistorySerializer.serialize(readInHistory);
		
		Assert.assertEquals(serialisedInitialHistoryString, roundTripHistoryString);
		
		return readInHistory;
	}

	public static boolean checkValidationResultsForAWarningOfType(EntityDomainValidator domainValidator, Class<?> warningClass) {
		boolean foundDesiredWarning = false;
		for (IValidationWarning warning : domainValidator.getValidationWarnings()) {

			if (warning.getClass().equals(warningClass)) {
				foundDesiredWarning = true;
			}
		}
		return foundDesiredWarning;
	}
	
	public static void addNewInitialStateToClassWithEventToPreviousInitialState(EntityClass klass) throws NameAlreadyBoundException
	{
		EntityState newInitial = new EntityState("NewInitialState");
		klass.addState(newInitial);
		
		EntityEventSpecification spec = new EntityEventSpecification(klass, "EventOne");
		EntityEventInstance instance = new EntityEventInstance(spec, newInitial, klass.getInitialState());
		klass.addEventInstance(spec, instance);
		
		newInitial.setInitial();
	}

	public static EntityState addNewDummyClassWithProcedure(EntityDomain domain, String procText) throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException {
		EntityClass dummy = new EntityClass("Dummy");
		domain.addClass(dummy);
		EntityState notInitial = new EntityState("State1");
		dummy.addState(notInitial);
		EntityProcedure notInitialProcedure = new EntityProcedure(notInitial);
		notInitialProcedure.setProcedure(procText);
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(dummy);
		return notInitial;
	}

	public static void printValidationWarnings(EntityDomainValidator validator) {
		validator.validate();
		for (IValidationError error : validator.getValidationWarnings()) {
			System.out.println(error.getClass() + " -- " + error.explainError());
		}
	}
	
}
