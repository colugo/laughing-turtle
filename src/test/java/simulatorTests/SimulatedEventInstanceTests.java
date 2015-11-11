package test.java.simulatorTests;

import javax.naming.NameNotFoundException;

import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IGenerateDelayActionLanguageSyntax.DelayUnits;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.simulator.ISimulator;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.events.SimulatedEvent;
import main.java.avii.simulator.events.SimulatedEventTimeHelper;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

import org.junit.Assert;
import org.junit.Ignore;

import test.java.helper.DomainShoppingCart;
import test.java.helper.DomainTTD;

public class SimulatedEventInstanceTests extends TestCase {

	public SimulatedEventInstanceTests(String string)
	{
		super(string);
	}
	
	public void test_newly_created_events_get_the_current_timestamp()
	{
		SimulatedEventInstance event = new SimulatedEventInstance(null, null);
		long now = SimulatedEventTimeHelper.getCurrentTimeHelper();
		Assert.assertTrue(now >= event.getGoTime());
	}
	
	public void test_event_instances_have_an_event_specification() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		
		EntityClass ShoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityEventSpecification startCart = ShoppingCart.getEventSpecificationWithName("startCart");
		SimulatedEvent simulatedStartCart = new SimulatedEvent(startCart);
		
		SimulatedEventInstance eventInstance = new SimulatedEventInstance(simulatedStartCart, null, null);
		Assert.assertEquals(startCart, eventInstance.getSpecification().getConcreteEvent());
		
	}
	
	public void test_event_instances_can_have_params_set() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass ShoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityEventSpecification startCart = ShoppingCart.getEventSpecificationWithName("startCart");
		SimulatedEvent simulatedStartCart = new SimulatedEvent(startCart);
		SimulatedEventInstance eventInstance = new SimulatedEventInstance(simulatedStartCart, null, null);
		eventInstance.setParam("itemName","item1");
		eventInstance.setParam("quantity", 6.0);
		Assert.assertEquals("item1",eventInstance.getParam("itemName"));
		Assert.assertEquals(6,eventInstance.getParam("quantity"));
	}
	
	public void test_event_instance_params_get_their_datatypes_intial_value() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass ShoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityEventSpecification startCart = ShoppingCart.getEventSpecificationWithName("startCart");
		SimulatedEvent simulatedStartCart = new SimulatedEvent(startCart);
		SimulatedEventInstance eventInstance = new SimulatedEventInstance(simulatedStartCart, null, null);
		Assert.assertEquals("",eventInstance.getParam("itemName"));
		Assert.assertEquals(0,eventInstance.getParam("quantity"));
	}
	
	public void test_events_determine_if_they_are_to_self_events() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		ISimulator simulator = new Simulator(ttdDomain);
		SimulatedClass userSimulated = simulator.getSimulatedClass("User");
		SimulatedClass taskSimulated = simulator.getSimulatedClass("Task");
		
		SimulatedInstanceIdentifier sender = new SimulatedInstanceIdentifier(0, userSimulated);
		SimulatedInstanceIdentifier destination = new SimulatedInstanceIdentifier(1, taskSimulated);
		
		SimulatedEventInstance event = new SimulatedEventInstance(sender, destination);
		Assert.assertTrue(!event.isToSelf());
	}
	
	public void test_events_determine_if_they_arent_to_self_events() throws CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		ISimulator simulator = new Simulator(ttdDomain);
		SimulatedClass userSimulated = simulator.getSimulatedClass("User");
		
		SimulatedInstanceIdentifier sender = new SimulatedInstanceIdentifier(0, userSimulated);
				
		SimulatedEventInstance event = new SimulatedEventInstance(sender, sender);
		Assert.assertTrue(event.isToSelf());
	}
	
	public void test_can_add_offset_to_event_timestamp_on_construction()
	{
		SimulatedEventInstance event = new SimulatedEventInstance(null, null);
		Assert.assertEquals(false, event.hasDelay());
		event.setDelay(1, DelayUnits.Second);
		Assert.assertEquals(true, event.hasDelay());
		long now = SimulatedEventTimeHelper.getCurrentTimeHelper();
		Assert.assertTrue(now < event.getGoTime());
		Assert.assertTrue(now + 1000 >= event.getGoTime());
	}
	
}

