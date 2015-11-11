package test.java.tests;

import test.java.helper.DomainShoppingCart;
import test.java.helper.TestHelper;

import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CouldNotIdentifyDatatypeValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CouldNotIdentifyInstanceValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CreatorEventIsNotFromInitialState;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityClassNotFoundInDomainValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityDatatypesCannotBeSet;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EventNotFoundInClassValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EventParamNotFoundInEventInstanceValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EventParamNotFoundInEventSpecificationValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.InvalidDelayUnitsValidationException;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventInstance;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;


public class GenerateSyntaxTests extends TestCase {

	public GenerateSyntaxTests(String name) {
		super(name);
	}

	private EntityDomain GetDomain() throws NameNotFoundException
	{
		EntityDomain microwave = new EntityDomain("Microwave");
		AddClassesToDomain(microwave);
		return microwave;
	}
	
	private void AddClassesToDomain(EntityDomain microwave) throws NameNotFoundException {
		EntityClass oven = new EntityClass("Oven");
		microwave.addClass(oven);
		AddStatesToOven(oven);
		AddEventsToOven(oven);
		EntityClass dummy = new EntityClass("Dummy");
		microwave.addClass(dummy);
		AddStatesToDummy(dummy);
		AddEventsToDummy(dummy);
	}
	
	@SuppressWarnings("unused")
	private void AddEventsToOven(EntityClass oven) throws NameNotFoundException {
		EntityState doorClosed = oven.getStateWithName("DoorClosed");
		EntityState doorOpen = oven.getStateWithName("DoorOpen");
		EntityState placeItem = oven.getStateWithName("PlaceItem");
		
		EntityEventSpecification openDoorSpec = new EntityEventSpecification(oven, "openDoor");
		EntityEventSpecification placeItemEventSpec = new EntityEventSpecification(oven, "placeItem");
		
		// convert to event instances
		EntityEventInstance openDoorInstance = new EntityEventInstance(openDoorSpec,doorClosed,doorOpen);
		//EntityEventSpecification openDoor = new EntityEventSpecification(oven, "openDoor", doorClosed, doorOpen);
		
		EntityEventInstance placeItemEventInstance = new EntityEventInstance(placeItemEventSpec,doorOpen,placeItem);
		//EntityEventSpecification placeItemEvent = new EntityEventSpecification(oven, "placeItem", doorOpen, placeItem);
	}
	
	@SuppressWarnings("unused")
	private void AddEventsToDummy(EntityClass dummy) throws NameNotFoundException {
		EntityState dummy1 = dummy.getStateWithName("Dummy1");
		EntityState dummy2 = dummy.getStateWithName("Dummy2");
		EntityState dummy3 = dummy.getStateWithName("Dummy3");
		
		EntityEventSpecification openDoorSpec = new EntityEventSpecification(dummy, "eventDummy1");
		EntityEventSpecification placeItemEventSpec = new EntityEventSpecification(dummy, "eventDummy2");
		
		// convert to event instances
		EntityEventInstance openDoorInstance = new EntityEventInstance(openDoorSpec,dummy1,dummy2);
		EntityEventInstance placeItemEventInstance = new EntityEventInstance(placeItemEventSpec,dummy2,dummy3);
	}

	private void AddStatesToOven(EntityClass oven)
	{
		EntityState doorClosed = new EntityState("DoorClosed");
		EntityState doorOpen = new EntityState("DoorOpen");
		EntityState placeItem = new EntityState("PlaceItem");
		oven.addState(doorClosed);
		oven.addState(doorOpen);
		oven.addState(placeItem);
		oven.setInitial(doorClosed);
	}
	
	private void AddStatesToDummy(EntityClass dummy)
	{
		EntityState dummy1 = new EntityState("Dummy1");
		EntityState dummy2 = new EntityState("Dummy2");
		EntityState dummy3 = new EntityState("Dummy3");
		dummy.addState(dummy1);
		dummy.addState(dummy2);
		dummy.addState(dummy3);
		dummy.setInitial(dummy1);
	}
	

	public void test_domain_setup() throws NameNotFoundException
	{
		EntityDomain microwave = GetDomain();
		Assert.assertTrue(microwave.hasEntityClassWithName("Oven"));
		EntityClass oven = microwave.getEntityClassWithName("Oven");
		Assert.assertEquals("DoorClosed", oven.getInitialState().getName());
		Assert.assertTrue(oven.hasStateWithName("DoorOpen"));
		Assert.assertEquals("DoorOpen", oven.getStateWithName("DoorOpen").getName());
		Assert.assertTrue(oven.hasEventSpecificationWithName("openDoor"));
	}
	
	
	public void test_generate_syntax_can_identify_that_class_accepts_event_name() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE oven FROM Oven;\n";
		proc += "GENERATE openDoor() TO oven;";
		
		EntityDomain microwave = GetDomain();
		EntityClass oven = microwave.getEntityClassWithName("Oven");
		EntityState state = oven.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_generate_syntax_can_identify_that_class_does_not_accept_event_name() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE oven FROM Oven;\n";
		proc += "GENERATE notAnEventInThisDomain() TO oven;";
		
		EntityDomain microwave = GetDomain();
		EntityClass oven = microwave.getEntityClassWithName("Oven");
		EntityState state = oven.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EventNotFoundInClassValidationError.class));
	}

	
	public void test_generate_syntax_will_fail_if_instance_cant_be_identified() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "GENERATE openDoor() TO fred;";
		
		EntityDomain microwave = GetDomain();
		EntityClass oven = microwave.getEntityClassWithName("Oven");
		EntityState state = oven.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
	}
	
	//////////
	
	public void test_generate_delay_syntax_can_identify_that_class_accepts_event_name() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE oven FROM Oven;\n";
		proc += "GENERATE openDoor() TO oven DELAY 1000Seconds;";
		
		EntityDomain microwave = GetDomain();
		EntityClass oven = microwave.getEntityClassWithName("Oven");
		EntityState state = oven.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_generate_delay_syntax_can_identify_that_class_does_not_accept_event_name() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE oven FROM Oven;\n";
		proc += "GENERATE notAnEventInThisDomain() TO oven DELAY 1000Seconds;";
		
		EntityDomain microwave = GetDomain();
		EntityClass oven = microwave.getEntityClassWithName("Oven");
		EntityState state = oven.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EventNotFoundInClassValidationError.class));
	}

	
	public void test_generate_delay_syntax_will_fail_if_instance_cant_be_identified() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE oven FROM Oven;\n";
		proc += "GENERATE openDoor() TO NotAnOven DELAY 1000Seconds;";
		
		EntityDomain microwave = GetDomain();
		EntityClass oven = microwave.getEntityClassWithName("Oven");
		EntityState state = oven.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
	}
	
	public void test_delay_fails_when_delay_isnt_an_int() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "GENERATE openDoor() TO fred DELAY FredSeconds;";
		
		EntityDomain microwave = GetDomain();
		EntityClass oven = microwave.getEntityClassWithName("Oven");
		EntityState state = oven.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, InvalidDelayUnitsValidationException.class));

	}
	
	public void test_delay_fails_when_delay_is_a_float() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "GENERATE openDoor() TO fred DELAY 1.5Seconds;";
		
		EntityDomain microwave = GetDomain();
		EntityClass oven = microwave.getEntityClassWithName("Oven");
		EntityState state = oven.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, InvalidDelayUnitsValidationException.class));

	}
	
	public void test_generate_delay_syntax_fails_with_inappropriate_units() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE oven FROM Oven;\n";
		proc += "GENERATE openDoor() TO oven DELAY 1000things;";
		
		EntityDomain microwave = GetDomain();
		EntityClass oven = microwave.getEntityClassWithName("Oven");
		EntityState state = oven.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, InvalidDelayUnitsValidationException.class));
	}
	
	public void test_generate_creator_delay_syntax_fails_with_inappropriate_units() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "GENERATE openDoor() TO Oven CREATOR DELAY 1000things;";
		
		EntityDomain microwave = GetDomain();
		EntityClass oven = microwave.getEntityClassWithName("Oven");
		EntityState state = oven.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, InvalidDelayUnitsValidationException.class));
	}
	
	public void test_creator_delay_passes_when_delay_is_an_int() throws InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "";
		proc += "GENERATE openDoor() TO Oven CREATOR DELAY 1000Seconds;";
		
		EntityDomain microwave = GetDomain();
		EntityClass oven = microwave.getEntityClassWithName("Oven");
		EntityState state = oven.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_creator_delay_fails_when_delay_is_not_an_int() throws InvalidActionLanguageSyntaxException, NameNotFoundException
	{
		String proc = "";
		proc += "GENERATE openDoor() TO Oven CREATOR DELAY 100.0Seconds;";
		
		EntityDomain microwave = GetDomain();
		EntityClass oven = microwave.getEntityClassWithName("Oven");
		EntityState state = oven.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, InvalidDelayUnitsValidationException.class));
	}
	
	
	public void test_generate_creator_delay_can_identify_class() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "GENERATE openDoor() TO Oven CREATOR DELAY 100Seconds;";
		
		EntityDomain microwave = GetDomain();
		EntityClass oven = microwave.getEntityClassWithName("Oven");
		EntityState state = oven.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_generate_creator_delay_can_not_identify_class() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "GENERATE openDoor() TO NotAnOven CREATOR DELAY 100Seconds;";
		
		EntityDomain microwave = GetDomain();
		EntityClass oven = microwave.getEntityClassWithName("Oven");
		EntityState state = oven.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityClassNotFoundInDomainValidationError.class));
	}
	
	public void test_generate_creator_delay_allows_events_from_the_initial_state() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "GENERATE openDoor() TO Oven CREATOR DELAY 100Seconds;";
		
		EntityDomain microwave = GetDomain();
		EntityClass oven = microwave.getEntityClassWithName("Oven");
		EntityState state = oven.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	
	public void test_generate_creator_delay_dis_allows_events_not_from_the_initial_state() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "GENERATE placeItem() TO Oven CREATOR DELAY 100Seconds;";
		
		EntityDomain microwave = GetDomain();
		EntityClass oven = microwave.getEntityClassWithName("Oven");
		EntityState state = oven.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CreatorEventIsNotFromInitialState.class));
	}
	

	public void test_generate_creator_can_identify_class() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "GENERATE openDoor() TO Oven CREATOR;";
		
		EntityDomain microwave = GetDomain();
		EntityClass oven = microwave.getEntityClassWithName("Oven");
		EntityState state = oven.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_generate_creator_can_not_identify_class() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "GENERATE openDoor() TO NotAnOven CREATOR;";
		
		EntityDomain microwave = GetDomain();
		EntityClass oven = microwave.getEntityClassWithName("Oven");
		EntityState state = oven.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityClassNotFoundInDomainValidationError.class));
	}
	
	public void test_generate_creator_allows_events_from_the_initial_state() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "GENERATE openDoor() TO Oven CREATOR;";
		
		EntityDomain microwave = GetDomain();
		EntityClass oven = microwave.getEntityClassWithName("Oven");
		EntityState state = oven.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	
	public void test_generate_creator_dis_allows_events_not_from_the_initial_state() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "GENERATE placeItem() TO Oven CREATOR;";
		
		EntityDomain microwave = GetDomain();
		EntityClass oven = microwave.getEntityClassWithName("Oven");
		EntityState state = oven.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CreatorEventIsNotFromInitialState.class));
	}
	
	public void test_validator_will_pass_generation_of_event_with_no_params_that_does_not_require_them() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE cart FROM ShoppingCart;\n";
		proc += "GENERATE cancel() TO cart;";
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_event_params_must_have_identifyable_datatype() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE cart FROM ShoppingCart;\n";
		proc += "GENERATE startCart(a=b) TO cart;";
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(4, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyDatatypeValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EventParamNotFoundInEventSpecificationValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EventParamNotFoundInEventInstanceValidationError.class));
	}
	
	public void test_validator_will_fail_generation_of_event_if_extra_param_specified() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE cart FROM ShoppingCart;\n";
		proc += "GENERATE startCart(fred=1) TO cart;";
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(3, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EventParamNotFoundInEventSpecificationValidationError.class));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EventParamNotFoundInEventInstanceValidationError.class));
	}
	
	public void test_validator_will_fail_generation_of_event_with_no_params_that_requires_them() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE cart FROM ShoppingCart;\n";
		proc += "GENERATE startCart() TO cart;";
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EventParamNotFoundInEventInstanceValidationError.class));
	}
	
	
	public void test_validator_will_fail_generation_of_event_that_is_not_for_target_class() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE cart FROM ShoppingCart;\n";
		proc += "GENERATE eventDummy1() TO cart;";
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EventNotFoundInClassValidationError.class));
	}
	
	public void test_validator_will_fail_generation_delay_of_event_with_no_params_that_requires_them() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE cart FROM ShoppingCart;\n";
		proc += "GENERATE startCart() TO cart DELAY 10Seconds;";
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EventParamNotFoundInEventInstanceValidationError.class));
	}
	
	public void test_validator_will_fail_generation_creator_of_event_with_no_params_that_requires_them() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "GENERATE startCart() TO ShoppingCart CREATOR;";
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EventParamNotFoundInEventInstanceValidationError.class));
	}
	
	public void test_validator_will_fail_generation_creator_delay_of_event_with_no_params_that_requires_them() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "GENERATE startCart() TO ShoppingCart CREATOR DELAY 10Seconds;";
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EventParamNotFoundInEventInstanceValidationError.class));
	}
	
	public void test_can_use_more_then_literals_as_event_params() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE item FROM Item;\n";
		proc += "item.Name = \"CocoPops\";\n";
		proc += "temp = 6;\n";
		proc += "CREATE cart FROM ShoppingCart;\n";
		proc += "GENERATE startCart(itemName = item.Name, quantity = temp) TO cart;";
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	public void test_event_param_must_have_correct_datatype_from_temp() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE item FROM Item;\n";
		proc += "item.Name = \"CocoPops\";\n";
		proc += "temp = 6.8;\n";
		proc += "CREATE cart FROM ShoppingCart;\n";
		proc += "GENERATE startCart(itemName = item.Name, quantity = temp) TO cart;";
		
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityState state = cart.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeSet.class));
	}
	
}

