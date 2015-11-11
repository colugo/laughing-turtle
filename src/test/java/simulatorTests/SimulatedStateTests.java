package test.java.simulatorTests;

import test.java.helper.DomainShoppingCart;

import javax.naming.NameNotFoundException;

import test.java.mock.MockInstanceCreator;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedState;

public class SimulatedStateTests extends TestCase {

	public SimulatedStateTests(String name) {
		super(name);
	}
	
	private EntityState getState() throws CannotSimulateDomainThatIsInvalidException, NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass shoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityState newOrder = shoppingCart.getStateWithName("New Order");
		return newOrder;
	}
	
	public void test_simulated_state_can_be_created_from_concrete_state() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException
	{	
		EntityState concreteState = getState();
		SimulatedState simulatedState = new SimulatedState(concreteState,null);
		Assert.assertEquals(simulatedState.getName(), concreteState.getName());
	}
	
	public void test_simulated_state_has_in_scope_simulated_instances() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException
	{	
		String instanceName = "fred";
		EntityState concreteState = getState();
		SimulatedState simulatedState = new SimulatedState(concreteState,null);
		try
		{
			Assert.assertTrue(simulatedState.getInstanceWithName(instanceName) == null);
			fail();
		}
		catch(Exception e)
		{}		
		EntityClass entityClass = new EntityClass("Person");
		SimulatedClass simulatedClass = new SimulatedClass(entityClass, null);
		MockInstanceCreator creator = new MockInstanceCreator();
		SimulatedInstance instance01 = creator.createInstance(simulatedClass);
		simulatedState.registerInstance(instanceName,instance01);
		
		Assert.assertEquals(instance01,simulatedState.getInstanceWithName(instanceName));
	}

}

