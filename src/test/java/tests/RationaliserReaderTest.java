package test.java.tests;

import test.java.helper.DomainShoppingCart;

import java.util.ArrayList;

import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.eventManager.GeneratedEventManager;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;

public class RationaliserReaderTest extends TestCase {

	public RationaliserReaderTest(String name) {
		super(name);
	}

	public void test_generated_event_manager_can_register_event() throws NameNotFoundException
	{
		// add a new event to the manager
		// check count of events
		
		GeneratedEventManager generatedEventManager = new GeneratedEventManager();
		generatedEventManager.registerEvent("startCart");
		Assert.assertEquals(1, generatedEventManager.numberOfRegisteredEvents());
	}
	
	public void test_generated_event_manager_will_quelch_multiple_registered_event() throws NameNotFoundException
	{
		// add a new event to the manager
		// check count of events
		
		GeneratedEventManager generatedEventManager = new GeneratedEventManager();
		generatedEventManager.registerEvent("startCart");
		generatedEventManager.registerEvent("startCart");
		Assert.assertEquals(1, generatedEventManager.numberOfRegisteredEvents());
	}
	
	public void test_generated_event_manager_can_check_if_an_event_is_regisered() throws NameNotFoundException
	{
		GeneratedEventManager generatedEventManager = new GeneratedEventManager();
		
		// ask if an unregistered event is registered
		generatedEventManager.registerEvent("startCart");
		Assert.assertTrue(generatedEventManager.isEventRegistered("startCart"));
		
		// ask if a registered event is registered
		Assert.assertTrue(!generatedEventManager.isEventRegistered("addSelection"));
				
	}
	
	public void test_generated_event_manager_can_list_registered_events() throws NameNotFoundException
	{
		GeneratedEventManager generatedEventManager = new GeneratedEventManager();
		
		generatedEventManager.registerEvent("startCart");
		generatedEventManager.registerEvent("addSelection");
		
		ArrayList<String> registeredEvents = generatedEventManager.getRegisteredEvents();
		Assert.assertTrue(registeredEvents.contains("startCart"));
		Assert.assertTrue(registeredEvents.contains("addSelection"));

	}
	
	
	public void test_rationaliser_reader_can_identify_generated_events_from_state_procedure() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain cartDomain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass ShoppingCart = cartDomain.getEntityClassWithName("ShoppingCart");
		
		String proc = "CREATE cart FROM ShoppingCart;\n";
		proc += "GENERATE startCart(itemName=\"item1\",quantity=6) TO cart;\n";
		proc += "GENERATE addSelection(itemName=\"item1\",quantity=6) TO cart;\n";
		
		EntityState state = ShoppingCart.getInitialState();
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		GeneratedEventManager generatedEventManager = procedure.getGeneratedEventManager();
		Assert.assertTrue(generatedEventManager.isEventRegistered("startCart"));
		Assert.assertTrue(generatedEventManager.isEventRegistered("addSelection"));
	}
	
}

