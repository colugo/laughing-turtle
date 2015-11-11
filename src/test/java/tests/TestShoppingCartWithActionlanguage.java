package test.java.tests;

import test.java.helper.DomainShoppingCart;
import test.java.helper.TestHelper;

import java.util.ArrayList;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.validation.EntityClassValidator;
import main.java.avii.editor.metamodel.actionLanguage.validation.EntityDomainValidator;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.EntityAttributeIsNotReadValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.EntityAttributeIsNotSetValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.EntityClassCannotHaveIslandStatesValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.EntityEventParamaterIsNotUsedValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.EntityStateCannotHaveMultipleExitingTransitionsForTheSameEventSpecificationValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.EntityStateCannotHaveMultipleTriggeringEventSpecificationsWithDifferentSignaturesValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.IValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.structure.InitialStateCannotHaveTriggeringEventValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.IActionLanguageValidationError;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventInstance;
import main.java.avii.editor.metamodel.entities.EntityEventParam;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;

public class TestShoppingCartWithActionlanguage extends TestCase {

	public TestShoppingCartWithActionlanguage(String name) {
		super(name);
	}
	
	
	public void test_vanilla_shopping_cart_class_validates() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass shoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		Assert.assertEquals("ShoppingCart", shoppingCart.getName());

		EntityClassValidator entityClassValidator = new EntityClassValidator(shoppingCart);
		
		Assert.assertTrue(entityClassValidator.validate());
		ArrayList<IValidationError> errors = entityClassValidator.getValidationErrors();
		Assert.assertEquals(0, errors.size());
	}
	
	public void test_validation_error_can_identify_which_state_caused_the_error() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass shoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		Assert.assertEquals("ShoppingCart", shoppingCart.getName());
	
		
		EntityState scCancellingOrder = shoppingCart.getStateWithName("Cancelling Entire Order");
		String cancellingOrderText = "";
		cancellingOrderText += "SELECT MANY itemsInCart RELATED BY self->R1;\n";
		cancellingOrderText += "FOR currentItem IN itemsInCart DO\n";
		cancellingOrderText += "	UNRELATE currentItem FROM self ACROSS R2;\n";
		cancellingOrderText += "END FOR;\n";
		EntityProcedure cancellingOrderProc = new EntityProcedure(scCancellingOrder);
		cancellingOrderProc.setProcedure(cancellingOrderText);
		
		EntityClassValidator entityClassValidator = new EntityClassValidator(shoppingCart);
		
		Assert.assertTrue(!entityClassValidator.validate());
		ArrayList<IValidationError> errors = entityClassValidator.getValidationErrors();
		Assert.assertEquals(1, errors.size());
		Assert.assertTrue(errors.get(0) instanceof IActionLanguageValidationError);
		IActionLanguageValidationError actionLangValidError = (IActionLanguageValidationError) errors.get(0);
		Assert.assertEquals(scCancellingOrder, actionLangValidError.getThrowingState());
	}
	
	public void test_there_are_no_island_states() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass shoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		Assert.assertEquals("ShoppingCart", shoppingCart.getName());
	
		
		EntityEventSpecification addSelectionSpec = shoppingCart.getEventSpecificationWithName("addSelection");
		EntityEventInstance addSelectionInstance = shoppingCart.getEventInstance(addSelectionSpec.getName(), "New Order", "Adding Selection To Order");
		shoppingCart.removeEventInstance(addSelectionSpec, addSelectionInstance);
		
		EntityClassValidator entityClassValidator = new EntityClassValidator(shoppingCart);
		
		Assert.assertTrue(!entityClassValidator.validate());
		ArrayList<IValidationError> errors = entityClassValidator.getValidationErrors();
		Assert.assertEquals(1, errors.size());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(entityClassValidator, EntityClassCannotHaveIslandStatesValidationError.class));
	}
	
	public void test_init_state_has_no_triggering_events() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass shoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		Assert.assertEquals("ShoppingCart", shoppingCart.getName());
	
		
		EntityEventSpecification addSelectionSpec = shoppingCart.getEventSpecificationWithName("addSelection");
		
		EntityState scInitial = shoppingCart.getInitialState();
		EntityState scCancellingOrder = shoppingCart.getStateWithName("Cancelling Entire Order");
		
		new EntityEventInstance(addSelectionSpec, scCancellingOrder, scInitial);
		
		
		EntityClassValidator entityClassValidator = new EntityClassValidator(shoppingCart);
		
		Assert.assertTrue(!entityClassValidator.validate());
		ArrayList<IValidationError> errors = entityClassValidator.getValidationErrors();
		Assert.assertEquals(1, errors.size());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(entityClassValidator, InitialStateCannotHaveTriggeringEventValidationError.class));
	}

	public void test_states_can_have_to_events_from_entity_event_specs_with_identical_params() throws NameNotFoundException, InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass shoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		Assert.assertEquals("ShoppingCart", shoppingCart.getName());
	
		
		EntityEventSpecification startCartSpec = shoppingCart.getEventSpecificationWithName("startCart");
		EntityEventSpecification cancelSpec = shoppingCart.getEventSpecificationWithName("cancel");
		EntityState fakeState = new EntityState("fake");
		shoppingCart.addState(fakeState);
		
		EntityState scAddingSelection = shoppingCart.getStateWithName("Adding Selection To Order");
		new EntityEventInstance(cancelSpec, shoppingCart.getInitialState(), fakeState);
		new EntityEventInstance(startCartSpec, fakeState, scAddingSelection);
		
		
		EntityClassValidator entityClassValidator = new EntityClassValidator(shoppingCart);
	
		TestHelper.printValidationErrors(entityClassValidator);
		Assert.assertTrue(entityClassValidator.validate());
	}
	
	public void test_states_cant_have_to_events_from_entity_event_specs_with_different_params() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass shoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		Assert.assertEquals("ShoppingCart", shoppingCart.getName());
	
		
		EntityEventSpecification anotherEventSpec = new EntityEventSpecification(shoppingCart, "anotherEvent");
		anotherEventSpec.addEventParam(new EntityEventParam("anotherParam", StringEntityDatatype.getInstance()));
		EntityState scInitial = shoppingCart.getInitialState();
		EntityState scAddingSelection = shoppingCart.getStateWithName("Adding Selection To Order");
		new EntityEventInstance(anotherEventSpec, scInitial, scAddingSelection);
		
		
		EntityClassValidator entityClassValidator = new EntityClassValidator(shoppingCart);
		
		Assert.assertTrue(!entityClassValidator.validate());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(entityClassValidator, EntityStateCannotHaveMultipleTriggeringEventSpecificationsWithDifferentSignaturesValidationError.class));
	}

	public void test_all_event_params_are_read_at_least_once() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass shoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityClassValidator entityClassValidator = new EntityClassValidator(shoppingCart);
		
		
		Assert.assertTrue(entityClassValidator.validate());
	}
	
	public void test_all_event_params_arent_read_least_once() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass shoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityEventSpecification cancelSpec = shoppingCart.getEventSpecificationWithName("cancel");
		cancelSpec.addEventParam(new EntityEventParam("quantity",IntegerEntityDatatype.getInstance()));
		EntityClassValidator entityClassValidator = new EntityClassValidator(shoppingCart);
		
		
		Assert.assertTrue(!entityClassValidator.validate());
		ArrayList<IValidationError> errors = entityClassValidator.getValidationErrors();
		Assert.assertEquals(1, errors.size());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(entityClassValidator, EntityEventParamaterIsNotUsedValidationError.class));
	}

	

	public void test_all_class_attributes_are_set_at_least_once() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityDomainValidator domainValidator = new EntityDomainValidator(shoppingCartDomain);
		
		Assert.assertTrue(domainValidator.validate());
		Assert.assertTrue(domainValidator.hasWarnings());
	}
	
	public void test_all_class_attributes_are_read_at_least_once() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityDomainValidator domainValidator = new EntityDomainValidator(shoppingCartDomain);
		
		Assert.assertTrue(domainValidator.validate());
		Assert.assertTrue(domainValidator.hasWarnings());
	}
	
	public void test_all_class_attributes_arent_set_at_least_once() throws NameNotFoundException, InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityDomainValidator domainValidator = new EntityDomainValidator(shoppingCartDomain);
		EntityClass shoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		shoppingCart.addAttribute(new EntityAttribute("newAttribute", IntegerEntityDatatype.getInstance()));
		
		Assert.assertTrue(domainValidator.validate());
		Assert.assertTrue(domainValidator.hasWarnings());
		Assert.assertTrue(TestHelper.checkValidationResultsForAWarningOfType(domainValidator, EntityAttributeIsNotSetValidationError.class));
	}
	
	public void test_all_class_attributes_arent_read_at_least_once() throws NameNotFoundException, InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityDomainValidator domainValidator = new EntityDomainValidator(shoppingCartDomain);
		EntityClass shoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		shoppingCart.addAttribute(new EntityAttribute("newAttribute", IntegerEntityDatatype.getInstance()));
		
		Assert.assertTrue(domainValidator.validate());
		Assert.assertTrue(domainValidator.hasWarnings());
		Assert.assertTrue(TestHelper.checkValidationResultsForAWarningOfType(domainValidator, EntityAttributeIsNotReadValidationError.class));
	}
	
	public void test_validating_domain_validates_all_classes_in_domain() throws NameNotFoundException, InvalidActionLanguageSyntaxException{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass shoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		Assert.assertEquals("ShoppingCart", shoppingCart.getName());
	
		
		EntityState scCancellingOrder = shoppingCart.getStateWithName("Cancelling Entire Order");
		String cancellingOrderText = "";
		cancellingOrderText += "SELECT MANY itemsInCart RELATED BY self->R1;\n";
		cancellingOrderText += "FOR currentItem IN itemsInCart DO\n";
		cancellingOrderText += "	UNRELATE currentItem FROM self ACROSS R2;\n";
		cancellingOrderText += "END FOR;\n";
		EntityProcedure cancellingOrderProc = new EntityProcedure(scCancellingOrder);
		cancellingOrderProc.setProcedure(cancellingOrderText);
		
		EntityDomainValidator domainValidator = new EntityDomainValidator(shoppingCartDomain);
		Assert.assertTrue(!domainValidator.validate());
		ArrayList<IValidationError> errors = domainValidator.getValidationErrors();
		Assert.assertEquals(1, errors.size());
		Assert.assertTrue(errors.get(0) instanceof IActionLanguageValidationError);
		IActionLanguageValidationError actionLangValidError = (IActionLanguageValidationError) errors.get(0);
		Assert.assertEquals(scCancellingOrder, actionLangValidError.getThrowingState());
		
	}
	
	public void test_there_is_only_instance_of_each_event_specification_leaving_a_state() throws NameNotFoundException, InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass shoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		Assert.assertEquals("ShoppingCart", shoppingCart.getName());
		
		EntityState scNewOrder = shoppingCart.getStateWithName("New Order");
		EntityState fakeState = new EntityState("fake");
		shoppingCart.addState(fakeState);
		
		EntityEventSpecification addSelectionSpec = shoppingCart.getEventSpecificationWithName("addSelection");
		@SuppressWarnings("unused")
		EntityEventInstance brokenEvent = new EntityEventInstance(addSelectionSpec, scNewOrder, fakeState);
		
		EntityClassValidator entityClassValidator = new EntityClassValidator(shoppingCart);
		
		Assert.assertTrue(!entityClassValidator.validate());
		ArrayList<IValidationError> errors = entityClassValidator.getValidationErrors();
		Assert.assertEquals(1, errors.size());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(entityClassValidator, EntityStateCannotHaveMultipleExitingTransitionsForTheSameEventSpecificationValidationError.class));
	}
	
}

