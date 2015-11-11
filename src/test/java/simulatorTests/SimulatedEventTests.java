package test.java.simulatorTests;

import test.java.helper.DomainShoppingCart;

import javax.naming.NameNotFoundException;

import junit.framework.TestCase;

import org.junit.Assert;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventParam;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.simulator.events.SimulatedEvent;
import main.java.avii.simulator.events.SimulatedEventParam;

public class SimulatedEventTests extends TestCase {
	public SimulatedEventTests(String name)
	{
		super(name);
	}

	public void test_can_create_simulated_event_from_concrete_event() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		
		EntityClass ShoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityEventSpecification startCart = ShoppingCart.getEventSpecificationWithName("startCart");
		
		SimulatedEvent simulatedStartCart = new SimulatedEvent(startCart);
		Assert.assertEquals(startCart.getEventParams().size(), simulatedStartCart.getParams().size());
		
	}
	
	public void test_simulated_event_has_params_with_the_same_name_as_the_concrete_event() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		
		EntityClass ShoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityEventSpecification startCart = ShoppingCart.getEventSpecificationWithName("startCart");
		
		SimulatedEvent simulatedStartCart = new SimulatedEvent(startCart);
		
		for(SimulatedEventParam param : simulatedStartCart.getParams())
		{
			Assert.assertTrue(startCart.hasParamWithName(param.getName()));
		}
	}
	
	public void test_simulated_event_has_attributes_with_the_same_type_as_the_concrete_event() throws NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		
		EntityClass ShoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityEventSpecification startCart = ShoppingCart.getEventSpecificationWithName("startCart");
		
		SimulatedEvent simulatedStartCart = new SimulatedEvent(startCart);
		
		for(SimulatedEventParam param : simulatedStartCart.getParams())
		{
			EntityEventParam realParam = startCart.getParamWithName(param.getName());
			Assert.assertEquals(realParam.getType().getClass(), param.getType().getClass());
		}
	}
	
	

}
