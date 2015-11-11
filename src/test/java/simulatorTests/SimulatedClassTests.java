package test.java.simulatorTests;

import test.java.helper.DomainShoppingCart;
import test.java.helper.DomainTTD;
import test.java.helper.DomainWarehouse;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.simulator.simulatedTypes.SimulatedAttribute;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedState;

public class SimulatedClassTests extends TestCase {

	public SimulatedClassTests(String name) {
		super(name);
	}
	
	public void test_create_new_simulated_class_from_domain_class()
	{
		EntityClass itemSelection = new EntityClass("ItemSelection");
		SimulatedClass simulatedClass = new SimulatedClass(itemSelection, null);
		Assert.assertEquals(simulatedClass.getName(), itemSelection.getName());
	}
	
	public void test_simulated_class_has_all_attributes_of_real_class() throws NameNotFoundException
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		
		SimulatedClass simulatedUser = new SimulatedClass(user, null);
		
		Assert.assertEquals(user.getAttributes().size(), simulatedUser.getAttributes().size());
		for(SimulatedAttribute simulatedAttribute : simulatedUser.getAttributes())
		{
			EntityAttribute realAttribute = user.getAttributeWithName(simulatedAttribute.getName());
			Assert.assertTrue(simulatedAttribute.getType().getClass().equals(realAttribute.getType().getClass()));
		}
	}
	
	public void test_simulated_class_has_all_states_of_concrete_class() throws NameNotFoundException
	{
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		
		SimulatedClass simulatedUser = new SimulatedClass(user, null);
		
		Assert.assertEquals(user.getStates().size(), simulatedUser.getStates().size());
		
		for(SimulatedState simulatedState : simulatedUser.getStates())
		{
			EntityState realState = user.getStateWithName(simulatedState.getName());
			Assert.assertNotNull(realState);
		}
	}
	
	public void test_simulated_class_has_all_event_specifications_of_concrete_class() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass shoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		
		SimulatedClass simulatedShoppingCart = new SimulatedClass(shoppingCart, null);
		
		Assert.assertEquals(shoppingCart.getEventSpecifications().size(), simulatedShoppingCart.getEvents().size());
	}
	
	
	public void test_check_simulated_events_on_hierarchy_classes() throws NameAlreadyBoundException, NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain warehouseDomain = DomainWarehouse.getWarehouseDomainWithActionLanguage();
		EntityClass shippingClerk = warehouseDomain.getEntityClassWithName("ShippingClerk");
		SimulatedClass simulatedShippingClerk = new SimulatedClass(shippingClerk, null);
		Assert.assertEquals(shippingClerk.getEventSpecifications().size(), simulatedShippingClerk.getEvents().size());
	}
	
}

