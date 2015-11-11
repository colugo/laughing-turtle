package test.java.simulatorTests;
import java.util.Collection;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.contracts.metamodel.entities.CannotRefactorInvalidDomainException;
import main.java.avii.editor.metamodel.actionLanguage.validation.EntityDomainValidator;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.simulator.ISimulator;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceIdentifier;
import test.java.helper.DomainShoppingCart;
import test.java.simulator.helper.SimulatorTestHelper;


public class SimulatorTesting extends TestCase {

	public SimulatorTesting(String name) {
		super(name);
	}
	
	public void test_can_create_simulator_for_a_domain() throws NameNotFoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		new Simulator(shoppingCartDomain);
	}
	
	public void test_cant_create_simulator_for_a_domain_that_is_invalid() throws InvalidActionLanguageSyntaxException, NamingException, CannotRefactorInvalidDomainException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityDomainValidator validator = new EntityDomainValidator(shoppingCartDomain);
		Assert.assertTrue(validator.validate());

		// break the domain
		EntityClass item = shoppingCartDomain.getEntityClassWithName("Item");
		item.deleteAttributeWithName("Name");
		Assert.assertTrue(!validator.validate());
		try {
			new Simulator(shoppingCartDomain);
			fail();
		} catch (CannotSimulateDomainThatIsInvalidException e) {
		}
	}
	
	public void test_simulator_creates_simulated_classes_for_each_class_in_domain() throws NameNotFoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		ISimulator simulator = new Simulator(shoppingCartDomain);
		Collection<String> domainClassNames = shoppingCartDomain.getClassNames();
		Collection<String> simulatedClassNames = simulator.getSimulatedClassNames();
		Assert.assertEquals(domainClassNames.size(), simulatedClassNames.size());
		for(String domainClassName : domainClassNames)
		{
			Assert.assertTrue(simulatedClassNames.contains(domainClassName));
		}
	}
	
	public void test_simulator_creates_simulated_relation_for_each_relation_in_domain() throws NameNotFoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		ISimulator simulator = new Simulator(shoppingCartDomain);

		Assert.assertEquals(shoppingCartDomain.getRelations().size(), simulator.getSimulatedRelations().size());
		for(SimulatedRelationship relation : simulator.getSimulatedRelations())
		{
			Assert.assertTrue(shoppingCartDomain.hasRelationWithName(relation.getName()));
		}
	}
	
	
	public void test_simulator_assigns_simulated_relation_to_classes_that_have_them() throws NameNotFoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		ISimulator simulator = new Simulator(shoppingCartDomain);

		for(SimulatedClass simulatedClass : simulator.getSimulatedClasses())
		{
			EntityClass concreteClass = simulatedClass.getConcreteClass();
			for(SimulatedRelationship relationship : simulatedClass.getSimulatedRelationships())
			{
				Assert.assertTrue(concreteClass.hasRelation(relationship.getName()));
			}
		}
	}
	
	public void test_simulator_has_got_currently_simulating_state() throws CannotSimulateDomainThatIsInvalidException, NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		Simulator simulator = new Simulator(shoppingCartDomain);
		
		// until I setup test vectors, the simulator picks the initial (or first) procedure of the first class (that has a procedure)
		SimulatorTestHelper.pickFirstStateFromFirstClassForTesting(simulator);
		Assert.assertEquals("Initial",simulator.getSimulatingState().getName());
	}
	
	
	public void test_executing_a_small_procedure() throws NameNotFoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass shoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityState shoppingCartState = shoppingCart.getStateWithName("New Order");
		EntityProcedure shoppingCartProcedure = new EntityProcedure(shoppingCartState);
		String procedureText = "CREATE cart1 FROM ShoppingCart;\n";
		procedureText += "CREATE cart2 FROM ShoppingCart;\n";
		shoppingCartProcedure.setProcedure(procedureText);
		
		ISimulator simulator = new Simulator(shoppingCartDomain);
		simulator.setSimulatingState(shoppingCartState);
		
		Assert.assertEquals(0,simulator.getSimulatedInstanceCount());
		simulator.getSimulatingState().simulate();
		
		Assert.assertEquals(2,simulator.getSimulatedInstanceCount());
		
	}
	
	public void test_fetching_an_instance_from_an_identifier() throws NameNotFoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass shoppingCart = shoppingCartDomain.getEntityClassWithName("ShoppingCart");
		EntityState shoppingCartState = shoppingCart.getStateWithName("New Order");
		EntityProcedure shoppingCartProcedure = new EntityProcedure(shoppingCartState);
		String procedureText = "CREATE item1 FROM Item;\n";
		procedureText += "item1.Name=\"item1\";\n";
		procedureText += "CREATE item2 FROM Item;\n";
		procedureText += "item2.Name=\"item2\";\n";
		shoppingCartProcedure.setProcedure(procedureText);
		
		ISimulator simulator = new Simulator(shoppingCartDomain);
		SimulatedClass simulatedCart = simulator.getSimulatedClass("Item");
		simulator.setSimulatingState(shoppingCartState);
		simulator.getSimulatingState().simulate();
		
		SimulatedInstanceIdentifier id0 = new SimulatedInstanceIdentifier(0, simulatedCart);
		SimulatedInstanceIdentifier id1 = new SimulatedInstanceIdentifier(1, simulatedCart);
		SimulatedInstanceIdentifier id2 = new SimulatedInstanceIdentifier(2, simulatedCart);
		
		Assert.assertTrue(simulator.canIdentifyInstance(id0));
		Assert.assertTrue(simulator.canIdentifyInstance(id1));
		Assert.assertTrue(!simulator.canIdentifyInstance(id2));
		
		SimulatedInstance item1 = simulator.getInstanceFromIdentifier(id0);
		SimulatedInstance item2 = simulator.getInstanceFromIdentifier(id1);
		Assert.assertEquals("item1", item1.getAttribute("Name"));
		Assert.assertEquals("item2", item2.getAttribute("Name"));
	}


	
	
	
}

