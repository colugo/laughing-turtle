package test.java.simulatorTests;

import test.java.helper.DomainShoppingCart;

import javax.naming.NameNotFoundException;

import junit.framework.TestCase;

import org.junit.Assert;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.events.SimulatedEvent;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedEventInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;

public class SimulatedStateTransisionTests extends TestCase {
	public SimulatedStateTransisionTests(String name)
	{
		super(name);
	}
	
	public void test_can_determine_if_an_event_is_ignored() throws NameNotFoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass shoppingCart = domain.getEntityClassWithName("ShoppingCart");
		EntityState cartInitial = shoppingCart.getInitialState();
		
		Simulator simulator = new Simulator(domain);
		
		SimulatedClass simulatedCart = simulator.getSimulatedClass("ShoppingCart");
		SimulatedInstance selfInstance = simulatedCart.createInstance();
		SimulatedInstanceIdentifier selfId = selfInstance.getIdentifier();
		
		SimulatedEvent simulatedCartCancel = simulatedCart.getEventWithName("cancel");
		SimulatedEventInstance event1 = new SimulatedEventInstance(simulatedCartCancel, selfId, selfId);
		simulator.registerEventInstance(event1);
		
		Assert.assertEquals(cartInitial, selfInstance.getSimulatedState().getConcreteState());
				
		boolean wasEventIgnored = simulator.executeNextStateProcedure();
		
		Assert.assertEquals(false, wasEventIgnored);
		Assert.assertEquals(cartInitial, selfInstance.getSimulatedState().getConcreteState());
	}
	
	public void test_state_machine_can_transition_an_instance() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass shoppingCart = domain.getEntityClassWithName("ShoppingCart");
		EntityState cartInitial = shoppingCart.getInitialState();
		EntityState newOrder = shoppingCart.getStateWithName("New Order");
		
		Simulator simulator = new Simulator(domain);
		
		SimulatedClass simulatedCart = simulator.getSimulatedClass("ShoppingCart");
		SimulatedInstance selfInstance = simulatedCart.createInstance();
		SimulatedInstanceIdentifier selfId = selfInstance.getIdentifier();
		
		SimulatedEvent simulatedCartInitial = simulatedCart.getEventWithName("startCart");
		SimulatedEventInstance event1 = new SimulatedEventInstance(simulatedCartInitial, selfId, selfId);
		simulator.registerEventInstance(event1);
		
		Assert.assertEquals(cartInitial, selfInstance.getSimulatedState().getConcreteState());
				
		simulator.executeNextStateProcedure();
		
		Assert.assertEquals(newOrder, selfInstance.getSimulatedState().getConcreteState());
		Assert.assertEquals(selfInstance, simulator.getSimulatingInstance());
	}
	
	public void test_state_machine_will_only_transition_specific_instance() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomain();
		EntityClass shoppingCart = domain.getEntityClassWithName("ShoppingCart");
		EntityState cartInitial = shoppingCart.getInitialState();
		EntityState newOrder = shoppingCart.getStateWithName("New Order");
		
		Simulator simulator = new Simulator(domain);
		
		SimulatedClass simulatedCart = simulator.getSimulatedClass("ShoppingCart");
		SimulatedInstance selfInstance = simulatedCart.createInstance();
		SimulatedInstanceIdentifier selfId = selfInstance.getIdentifier();
		SimulatedInstance otherInstance = simulatedCart.createInstance();
		SimulatedInstanceIdentifier otherId = otherInstance.getIdentifier();
		
		SimulatedEvent simulatedCartInitial = simulatedCart.getEventWithName("startCart");
		SimulatedEventInstance event1 = new SimulatedEventInstance(simulatedCartInitial, selfId, selfId);
		SimulatedEventInstance event2 = new SimulatedEventInstance(simulatedCartInitial, otherId, otherId);
		simulator.registerEventInstance(event1);
		simulator.registerEventInstance(event2);
		
		Assert.assertEquals(cartInitial, selfInstance.getSimulatedState().getConcreteState());
		Assert.assertEquals(cartInitial, otherInstance.getSimulatedState().getConcreteState());
		
		simulator.executeNextStateProcedure();
		
		Assert.assertEquals(newOrder, selfInstance.getSimulatedState().getConcreteState());
		Assert.assertEquals(cartInitial, otherInstance.getSimulatedState().getConcreteState());
		Assert.assertEquals(selfInstance, simulator.getSimulatingInstance());
		
		simulator.executeNextStateProcedure();
		
		Assert.assertEquals(newOrder, selfInstance.getSimulatedState().getConcreteState());
		Assert.assertEquals(newOrder, otherInstance.getSimulatedState().getConcreteState());
		Assert.assertEquals(otherInstance, simulator.getSimulatingInstance());
	}

}

