package test.java.simulatorTests;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageLineException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSupportedSyntax;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.exceptions.CannotAccessNullSimulatedInstanceException;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.simulatedTypes.NullSimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedHierarchyInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedInstanceSet;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.ISimulatedActionLanguage;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.NoMatchingSimulatedActionLanguageException;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSynatx_CreateInstanceFromClass;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntaxFactory;

import org.junit.Test;

import test.java.helper.DomainShoppingCart;
import test.java.helper.DomainWarehouse;
import test.java.helper.TestHelper;
import test.java.simulator.helper.SimulatorTestHelper;

public class SimulatedActionLanguageTests extends TestCase {

	public SimulatedActionLanguageTests(String name) {
		super(name);
	}
	
	private Simulator getSimulator() throws CannotSimulateDomainThatIsInvalidException, NameNotFoundException, InvalidActionLanguageSyntaxException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		Simulator simulator = new Simulator(shoppingCartDomain);
		return simulator;
	}
	
	public void test_can_simulated_create_instance_action_language() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException
	{
		Simulator simulator = getSimulator();
		IActionLanguageSyntax concreteCreate = ActionLanguageSupportedSyntax.getSyntaxForLine("CREATE cart FROM ShoppingCart;");
		SimulatedSynatx_CreateInstanceFromClass simulatedCreate = new SimulatedSynatx_CreateInstanceFromClass(simulator,concreteCreate);
		
		try
		{
			Assert.assertTrue(simulator.getSimulatingState().getInstanceWithName("cart") == null);
			fail();
		}
		catch(Exception e)
		{}
		
		SimulatorTestHelper.pickFirstStateFromFirstClassForTesting(simulator);
		simulatedCreate.simulate();
		
		SimulatedInstance cart = simulator.getSimulatingState().getInstanceWithName("cart");
		Assert.assertEquals(cart.getSimulatedClass().getName(), "ShoppingCart");
	}
	
	public void test_can_create_simulated_action_language_from_concrete_action_language() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException
	{
		Simulator simulator = getSimulator();
		int currentCountOfInstances = simulator.getSimulatedInstanceCount();
		IActionLanguageSyntax concreteCreate = ActionLanguageSupportedSyntax.getSyntaxForLine("CREATE cart FROM ShoppingCart;");
		ISimulatedActionLanguage simulatedCreate = SimulatedSyntaxFactory.getSimulatedSyntax(simulator, concreteCreate);
		Assert.assertTrue(simulatedCreate instanceof SimulatedSynatx_CreateInstanceFromClass);
		SimulatorTestHelper.pickFirstStateFromFirstClassForTesting(simulator);
		simulatedCreate.simulate();
		Assert.assertEquals(currentCountOfInstances + 1, simulator.getSimulatedInstanceCount());
	}
	
	public void test_can_set_value_to_instance_attribute() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameAlreadyBoundException
	{	
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "CREATE user1 FROM User;\n";
		procedureText += "user1.Age = (4 + 1) * 2;\n";
		procedureText += "temp = user1.Age;\n";
		initialUserProcedure.setProcedure(procedureText);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		Object user1Age = simulator.getSimulatingState().getInstanceWithName("user1").getAttribute("Age");
		Assert.assertEquals(10, user1Age);
	}
	
	
	public void test_can_set_value_to_instance_attribute_using_attributes() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameAlreadyBoundException
	{	
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "CREATE user1 FROM User;\n";
		procedureText += "user1.Age = 2;\n";
		procedureText += "user1.Age = user1.Age * 3;\n";
		initialUserProcedure.setProcedure(procedureText);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		Object user1Age =simulator.getSimulatingState().getInstanceWithName("user1").getAttribute("Age");
		Assert.assertEquals(6, user1Age);
	}
	
	public void test_temp_expression() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		//user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "temp = 4.2;\n";
		initialUserProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		Object temp = simulator.getSimulatingState().getTempVariable("temp");
		String tempString = temp.toString();
		Float tempFloat = Float.parseFloat(tempString);
		Assert.assertEquals(4.2f, tempFloat);
	}

	
	public void test_temp_with_temp_expression() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		//user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "temp = 4.2;\n";
		procedureText += "temp = temp * 2;\n";
		initialUserProcedure.setProcedure(procedureText);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		Object temp = simulator.getSimulatingState().getTempVariable("temp");
		String tempString = temp.toString();
		Float tempFloat = Float.parseFloat(tempString);
		Assert.assertEquals(8.4f, tempFloat);
	}
	
	public void test_can_set_value_to_instance_attribute_using_string_attributes() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameAlreadyBoundException
	{	
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Name", StringEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "CREATE user1 FROM User;\n";
		procedureText += "user1.Name = \"Fred\";\n";
		procedureText += "user1.Name = user1.Name + 3;\n";
		initialUserProcedure.setProcedure(procedureText);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		Object user1Age =simulator.getSimulatingState().getInstanceWithName("user1").getAttribute("Name");
		Assert.assertEquals("Fred3", user1Age);
	}
	
	public void test_temp_string_with_temp_expression() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		//user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "temp = \"Rob\";\n";
		procedureText += "temp = temp + \"Banks\";\n";
		initialUserProcedure.setProcedure(procedureText);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		Object temp = simulator.getSimulatingState().getTempVariable("temp");
		String tempString = temp.toString();
		Assert.assertEquals("RobBanks", tempString);
	}

	
	public void test_can_simulate_simple_if_statement() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameAlreadyBoundException
	{	
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Name", StringEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "CREATE user1 FROM User;\n";
		procedureText += "user1.Name = \"Fred\";\n";
		procedureText += "IF 1 == 1 THEN\n";
		procedureText += "	user1.Name = \"Bob\";\n";
		procedureText += "END IF;\n";
		
		initialUserProcedure.setProcedure(procedureText);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		Object user1Name =simulator.getSimulatingState().getInstanceWithName("user1").getAttribute("Name");
		Assert.assertEquals("Bob", user1Name);
	}
	
	public void test_can_simulate_simple_not_taking_if_statement() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameAlreadyBoundException
	{	
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Name", StringEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "CREATE user1 FROM User;\n";
		procedureText += "user1.Name = \"Fred\";\n";
		procedureText += "IF 1 == 2 THEN\n";
		procedureText += "	user1.Name = \"Bob\";\n";
		procedureText += "	user1.Name = \"Bob1\";\n";
		procedureText += "	user1.Name = \"Bob2\";\n";
		procedureText += "END IF;\n";
		
		initialUserProcedure.setProcedure(procedureText);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		Object user1Name =simulator.getSimulatingState().getInstanceWithName("user1").getAttribute("Name");
		Assert.assertEquals("Fred", user1Name);
	}
	
	public void test_can_simulate_simple_taking_else_statement() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameAlreadyBoundException
	{	
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Name", StringEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "CREATE user1 FROM User;\n";
		procedureText += "user1.Name = \"Fred\";\n";
		procedureText += "IF 1 == 2 THEN\n";
		procedureText += "	user1.Name = \"Bob\";\n";
		procedureText += "ELSE\n";
		procedureText += "	user1.Name = \"Bob2\";\n";
		procedureText += "END IF;\n";
		
		initialUserProcedure.setProcedure(procedureText);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		Object user1Name =simulator.getSimulatingState().getInstanceWithName("user1").getAttribute("Name");
		Assert.assertEquals("Bob2", user1Name);
	}
	
	public void test_can_simulate_simple_not_taking_else_statement() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameAlreadyBoundException
	{	
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Name", StringEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "CREATE user1 FROM User;\n";
		procedureText += "user1.Name = \"Fred\";\n";
		procedureText += "IF 1 == 1 THEN\n";
		procedureText += "	user1.Name = \"Bob\";\n";
		procedureText += "ELSE\n";
		procedureText += "	user1.Name = \"Bob2\";\n";
		procedureText += "END IF;\n";
		
		initialUserProcedure.setProcedure(procedureText);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		Object user1Name =simulator.getSimulatingState().getInstanceWithName("user1").getAttribute("Name");
		Assert.assertEquals("Bob", user1Name);
	}
	
	public void test_can_simulate_simple_taking_nested_else_statement() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameAlreadyBoundException
	{	
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Name", StringEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "CREATE user1 FROM User;\n";
		procedureText += "user1.Name = \"Fred\";\n";
		procedureText += "IF 1 == 2 THEN\n";
		procedureText += "	user1.Name = \"Bob\";\n";
		procedureText += "ELSE\n";
		procedureText += "	IF 1 == 2 THEN\n";
		procedureText += "		user1.Name = \"Bob3\";\n";
		procedureText += "	ELSE\n";
		procedureText += "		user1.Name = \"Bob4\";\n";
		procedureText += "	END IF;\n";
		procedureText += "END IF;\n";
		
		initialUserProcedure.setProcedure(procedureText);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		Object user1Name =simulator.getSimulatingState().getInstanceWithName("user1").getAttribute("Name");
		Assert.assertEquals("Bob4", user1Name);
	}
	
	public void test_can_simulate_simple_not_taking_nested_else_statement() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameAlreadyBoundException
	{	
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Name", StringEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "";
		procedureText += /* 1*/ "CREATE user1 FROM User;\n";
		procedureText += /* 2*/ "user1.Name = \"Fred\";\n";
		procedureText += /* 3*/ "IF user1.Name != \"Fred\" THEN\n";
		procedureText += /* 4*/ "	user1.Name = \"Bob\";\n";
		procedureText += /* 5*/ "ELSE\n";
		procedureText += /* 6*/ "	IF user1.Name == \"Fred\" THEN\n";
		procedureText += /* 7*/ "		user1.Name = \"Bob3\";\n";
		procedureText += /* 8*/ "	ELSE\n";
		procedureText += /* 9*/ "		user1.Name = \"Bob4\";\n";
		procedureText += /*10*/ "	END IF;\n";
		procedureText += /*11*/ "END IF;\n";
		
		initialUserProcedure.setProcedure(procedureText);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		Object user1Name =simulator.getSimulatingState().getInstanceWithName("user1").getAttribute("Name");
		Assert.assertEquals("Bob3", user1Name);
	}
	
	public void test_can_simulate_from_instances_of_class_statement() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameAlreadyBoundException
	{	
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Name", StringEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "";
		procedureText += /* 1*/ "CREATE user1 FROM User;\n";
		procedureText += /* 2*/ "user1.Name = \"user1\";\n";
		procedureText += /* 3*/ "CREATE user2 FROM User;\n";
		procedureText += /* 4*/ "user2.Name = \"user2\";\n";
		procedureText += /* 5*/ "CREATE user3 FROM User;\n";
		procedureText += /* 6*/ "user3.Name = \"user3\";\n";
		procedureText += /* 7*/ "CREATE user4 FROM User;\n";
		procedureText += /* 8*/ "user4.Name = \"user4\";\n";
		procedureText += /* 9*/ "SELECT MANY users FROM INSTANCES OF User;\n";
		
		initialUserProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		SimulatedInstanceSet users = simulator.getSimulatingState().getInstanceSetWithName("users");
		Assert.assertEquals(4, users.size());

		SimulatedInstance user1 = users.getInstanceAt(0);
		Assert.assertEquals("user1", user1.getAttribute("Name"));
		SimulatedInstance user2 = users.getInstanceAt(1);
		Assert.assertEquals("user2", user2.getAttribute("Name"));
		SimulatedInstance user3 = users.getInstanceAt(2);
		Assert.assertEquals("user3", user3.getAttribute("Name"));
		SimulatedInstance user4 = users.getInstanceAt(3);
		Assert.assertEquals("user4", user4.getAttribute("Name"));
		
	}
	
	public void test_can_simulate_for_each_statement() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameAlreadyBoundException
	{	
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		
		String procedureText = "";
		procedureText += /* 1*/ "CREATE user1 FROM User;\n";
		procedureText += /* 2*/ "user1.Age = 1;\n";
		procedureText += /* 3*/ "CREATE user2 FROM User;\n";
		procedureText += /* 4*/ "user2.Age = 2;\n";
		procedureText += /* 5*/ "CREATE user3 FROM User;\n";
		procedureText += /* 6*/ "user3.Age = 3;\n";
		procedureText += /* 7*/ "CREATE user4 FROM User;\n";
		procedureText += /* 8*/ "user4.Age = 4;\n";
		procedureText += /* 9*/ "SELECT MANY users FROM INSTANCES OF User;\n";
		procedureText += /*10*/ "FOR user IN users DO\n";
		procedureText += /*11*/ "	user.Age = user.Age * 2;\n";
		procedureText += /*12*/ "END FOR;\n";
		procedureText += /*13*/ "user1.Age = user1.Age - 1;\n";
		procedureText += /*14*/ "user2.Age = user2.Age - 1;\n";
		procedureText += /*15*/ "user3.Age = user3.Age - 1;\n";
		procedureText += /*16*/ "user4.Age = user4.Age - 1;\n";
		
		initialUserProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		SimulatedInstanceSet users = simulator.getSimulatingState().getInstanceSetWithName("users");
		Assert.assertEquals(4, users.size());

		SimulatedInstance user1 = users.getInstanceAt(0);
		Assert.assertEquals(1, user1.getAttribute("Age"));
		SimulatedInstance user2 = users.getInstanceAt(1);
		Assert.assertEquals(3, user2.getAttribute("Age"));
		SimulatedInstance user3 = users.getInstanceAt(2);
		Assert.assertEquals(5, user3.getAttribute("Age"));
		SimulatedInstance user4 = users.getInstanceAt(3);
		Assert.assertEquals(7, user4.getAttribute("Age"));
	}
	
	public void test_can_simulate_for_each_with_single_result_statement() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameAlreadyBoundException
	{	
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		
		String procedureText = "";
		procedureText += /* 1*/ "CREATE user1 FROM User;\n";
		procedureText += /* 2*/ "user1.Age = 1;\n";
		procedureText += /* 9*/ "SELECT MANY users FROM INSTANCES OF User;\n";
		procedureText += /*10*/ "FOR user IN users DO\n";
		procedureText += /*11*/ "	user.Age = user.Age * 2;\n";
		procedureText += /*12*/ "END FOR;\n";
		procedureText += /*13*/ "user1.Age = user1.Age - 1;\n";

		initialUserProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		SimulatedInstanceSet users = simulator.getSimulatingState().getInstanceSetWithName("users");
		Assert.assertEquals(1, users.size());

		SimulatedInstance user1 = users.getInstanceAt(0);
		Assert.assertEquals(1, user1.getAttribute("Age"));
	}
	
	
	public void test_can_simulate_for_each_with_no_results_statement() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameAlreadyBoundException
	{	
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		
		String procedureText = "";
		procedureText += /* 9*/ "SELECT MANY users FROM INSTANCES OF User;\n";
		procedureText += /*10*/ "FOR user IN users DO\n";
		procedureText += /*11*/ "	user.Age = user.Age * 2;\n";
		procedureText += /*12*/ "END FOR;\n";
		
		initialUserProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		SimulatedInstanceSet users = simulator.getSimulatingState().getInstanceSetWithName("users");
		Assert.assertEquals(0, users.size());
	}
	
	public void test_can_simulate_select_from_instances_of_where_logic_statement() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameAlreadyBoundException
	{	
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		
		String procedureText = "";
		procedureText += /* 1*/ "CREATE user1 FROM User;\n";
		procedureText += /* 2*/ "user1.Age = 1;\n";
		procedureText += /* 3*/ "CREATE user2 FROM User;\n";
		procedureText += /* 4*/ "user2.Age = 2;\n";
		procedureText += /* 5*/ "CREATE user3 FROM User;\n";
		procedureText += /* 6*/ "user3.Age = 3;\n";
		procedureText += /* 7*/ "CREATE user4 FROM User;\n";
		procedureText += /* 8*/ "user4.Age = 4;\n";
		procedureText += /* 9*/ "SELECT MANY users FROM INSTANCES OF User WHERE selected.Age > 2;\n";
		procedureText += /*10*/ "FOR user IN users DO\n";
		procedureText += /*11*/ "	user.Age = user.Age * 2;\n";
		procedureText += /*12*/ "END FOR;\n";
		procedureText += /*13*/ "user1.Age = user1.Age - 1;\n";
		procedureText += /*14*/ "user2.Age = user2.Age - 1;\n";
		procedureText += /*15*/ "user3.Age = user3.Age - 1;\n";
		procedureText += /*16*/ "user4.Age = user4.Age - 1;\n";
		
		initialUserProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		SimulatedInstanceSet users = simulator.getSimulatingState().getInstanceSetWithName("users");
		Assert.assertEquals(2, users.size());

		SimulatedInstance user3 = users.getInstanceAt(0);
		Assert.assertEquals(5, user3.getAttribute("Age"));
		SimulatedInstance user4 = users.getInstanceAt(1);
		Assert.assertEquals(7, user4.getAttribute("Age"));
	}
	
	
	public void test_can_simulate_select_from_instances_of_where_logic_with_slightly_more_complex_where_statement() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameAlreadyBoundException
	{	
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		
		String procedureText = "";
		procedureText += /* 1*/ "CREATE user1 FROM User;\n";
		procedureText += /* 2*/ "user1.Age = 1;\n";
		procedureText += /* 3*/ "CREATE user2 FROM User;\n";
		procedureText += /* 4*/ "user2.Age = 2;\n";
		procedureText += /* 5*/ "CREATE user3 FROM User;\n";
		procedureText += /* 6*/ "user3.Age = 3;\n";
		procedureText += /* 7*/ "CREATE user4 FROM User;\n";
		procedureText += /* 8*/ "user4.Age = 4;\n";
		procedureText += /* 9*/ "SELECT MANY users FROM INSTANCES OF User WHERE selected.Age > 2 AND selected.Age < 4;\n";
		procedureText += /*10*/ "FOR user IN users DO\n";
		procedureText += /*11*/ "	user.Age = user.Age * 2;\n";
		procedureText += /*12*/ "END FOR;\n";
		procedureText += /*13*/ "user1.Age = user1.Age - 1;\n";
		procedureText += /*14*/ "user2.Age = user2.Age - 1;\n";
		procedureText += /*15*/ "user3.Age = user3.Age - 1;\n";
		procedureText += /*16*/ "user4.Age = user4.Age - 1;\n";
		
		initialUserProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		SimulatedInstanceSet users = simulator.getSimulatingState().getInstanceSetWithName("users");
		Assert.assertEquals(1, users.size());

		SimulatedInstance user3 = users.getInstanceAt(0);
		Assert.assertEquals(5, user3.getAttribute("Age"));
	}

	
	public void test_can_simulate_for_each_with_select_any_statement() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameAlreadyBoundException
	{	
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		
		String procedureText = "";
		procedureText += /* 1*/ "CREATE user1 FROM User;\n";
		procedureText += /* 2*/ "user1.Age = 0;\n";
		procedureText += /* 3*/ "CREATE user2 FROM User;\n";
		procedureText += /* 4*/ "user2.Age = 2;\n";
		procedureText += /* 5*/ "CREATE user3 FROM User;\n";
		procedureText += /* 6*/ "user3.Age = 3;\n";
		procedureText += /* 7*/ "CREATE user4 FROM User;\n";
		procedureText += /* 8*/ "user4.Age = 4;\n";
		procedureText += /* 9*/ "SELECT ANY selectedUser FROM INSTANCES OF User;\n";
		procedureText += /*11*/ "selectedUser.Age = selectedUser.Age * 2;\n";
		procedureText += /*13*/ "user1.Age = user1.Age - 1;\n";
		procedureText += /*14*/ "user2.Age = user2.Age - 1;\n";
		procedureText += /*15*/ "user3.Age = user3.Age - 1;\n";
		procedureText += /*16*/ "user4.Age = user4.Age - 1;\n";
		
		initialUserProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		Object user1Age = simulator.getSimulatingState().getInstanceWithName("user1").getAttribute("Age");
		Assert.assertEquals(-1, user1Age);
	}
	
	public void test_can_simulate_for_each_with_select_any_with_no_instances_statement() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameAlreadyBoundException
	{	
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		
		String procedureText = "";
		procedureText += /* 1*/ "SELECT ANY selectedUser FROM INSTANCES OF User;\n";
		
		initialUserProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		SimulatedInstance nullInstance = simulator.getSimulatingState().getInstanceWithName("selectedUser");
		Assert.assertTrue(nullInstance instanceof NullSimulatedInstance);
		
	}
	
	@Test(expected=CannotAccessNullSimulatedInstanceException.class)
	public void test_referencing_the_attributes_of_a_null_reference_will_fail_and_report_why_it_is_null() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameAlreadyBoundException
	{	
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		
		String procedureText = "";
		procedureText += /* 1*/ "SELECT ANY selectedUser FROM INSTANCES OF User;\n";
		procedureText += /* 2*/ "selectedUser.Age = selectedUser.Age * 2;\n";
		
		initialUserProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		try
		{
			simulator.getSimulatingState().simulate();
			fail();
		}
		catch(CannotAccessNullSimulatedInstanceException cansie)
		{
			Assert.assertTrue(cansie.getMessage().contains("selectedUser.Age"));
		}
	}

	
	public void test_can_simulate_select_any_from_instances_of_class_where_logic_statement() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameAlreadyBoundException
	{	
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Name", StringEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "";
		procedureText += /* 1*/ "CREATE user1 FROM User;\n";
		procedureText += /* 2*/ "user1.Name = \"user1\";\n";
		procedureText += /* 3*/ "CREATE user2 FROM User;\n";
		procedureText += /* 4*/ "user2.Name = \"user2\";\n";
		procedureText += /* 5*/ "SELECT ANY user FROM INSTANCES OF User WHERE selected.Name == \"user2\";\n";
		procedureText += /* 6*/ "user.Name = \"selectedUser\";\n";
		
		initialUserProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		SimulatedInstance selectedUser = simulator.getSimulatingState().getInstanceWithName("user");
		Object selectedUserName = selectedUser.getAttribute("Name");
		Assert.assertEquals("selectedUser", selectedUserName);
		
		SimulatedInstance user1 = simulator.getSimulatingState().getInstanceWithName("user1");
		Object user1Name = user1.getAttribute("Name");
		Assert.assertEquals("user1", user1Name);
		
		SimulatedInstance user2 = simulator.getSimulatingState().getInstanceWithName("user2");
		Object user2Name = user2.getAttribute("Name");
		Assert.assertEquals(selectedUserName, user2Name);
	}
	
	
	public void test_can_simulate_select_any_from_instances_of_class_where_logic_statement_that_returns_a_null() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameAlreadyBoundException
	{	
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Name", StringEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "";
		procedureText += /* 1*/ "CREATE user1 FROM User;\n";
		procedureText += /* 2*/ "user1.Name = \"user1\";\n";
		procedureText += /* 3*/ "CREATE user2 FROM User;\n";
		procedureText += /* 4*/ "user2.Name = \"user2\";\n";
		procedureText += /* 5*/ "SELECT ANY user FROM INSTANCES OF User WHERE selected.Name == \"not a user\";\n";
		procedureText += /* 6*/ "user.Name = \"selectedUser\";\n";
		
		initialUserProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		try
		{
			simulator.getSimulatingState().simulate();
			fail();
		}
		catch(CannotAccessNullSimulatedInstanceException cansie)
		{
			Assert.assertTrue(cansie.getMessage().contains("user.Name"));
		}
	}
	
	public void test_can_simulate_select_any_from_instances_of_class_where_logic_statement_that_returns_a_null_in_a_temp_expression() throws NameNotFoundException, CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException, InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameAlreadyBoundException
	{	
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityAttribute age = new EntityAttribute("Name", StringEntityDatatype.getInstance());
		user.addAttribute(age);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "";
		procedureText += /* 1*/ "CREATE user1 FROM User;\n";
		procedureText += /* 2*/ "user1.Name = \"user1\";\n";
		procedureText += /* 3*/ "CREATE user2 FROM User;\n";
		procedureText += /* 4*/ "user2.Name = \"user2\";\n";
		procedureText += /* 5*/ "SELECT ANY user FROM INSTANCES OF User WHERE selected.Name == \"not a user\";\n";
		procedureText += /* 6*/ "temp = user.Name + \"selectedUser\";\n";
		
		initialUserProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		try
		{
			simulator.getSimulatingState().simulate();
			fail();
		}
		catch(CannotAccessNullSimulatedInstanceException cansie)
		{
			Assert.assertTrue(cansie.getMessage().contains("user.Name"));
		}
	}
	
	public void test_can_create_generalisation_instance_with_action_language() throws NameAlreadyBoundException, NameNotFoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain warehouseDomain = DomainWarehouse.getWarehouseDomain();
		EntityClass warehouseClerk = warehouseDomain.getEntityClassWithName("WarehouseClerk");
		
		EntityState initialUserState = warehouseClerk.getInitialState();
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "RECLASSIFY TO ShippingClerk;\n";
		procedureText += "CREATE stockClerk FROM StockClerk;\n";
		procedureText += "stockClerk.ClerkId = 6;\n";

		initialUserProcedure.setProcedure(procedureText);
		
		Simulator simulator = new Simulator(warehouseDomain);
		simulator.setSimulatingState(initialUserState);

		// inject self
		SimulatedClass shippingClerkSimulated = simulator.getSimulatedClass("OffDutyClerk");
		SimulatedInstance self = shippingClerkSimulated.createInstance();
		simulator.getSimulatingState().registerInstance("self", self);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(warehouseClerk);
		
		simulator.getSimulatingState().simulate();
		
		SimulatedInstance stockClerk = simulator.getSimulatingState().getInstanceWithName("stockClerk");
		Assert.assertTrue(stockClerk instanceof SimulatedHierarchyInstance);
		
		Object user1Age = simulator.getSimulatingState().getInstanceWithName("stockClerk").getAttribute("ClerkId");
		Assert.assertEquals(6, user1Age);
	}
	
	public void test_can_select_from_instances_of_super_generalisation_classes() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain warehouseDomain = DomainWarehouse.getWarehouseDomain();
		EntityClass warehouseClerk = warehouseDomain.getEntityClassWithName("WarehouseClerk");
		
		EntityState initialUserState = warehouseClerk.getInitialState();
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "RECLASSIFY TO ShippingClerk;\n";
		procedureText += "CREATE stockClerk FROM StockClerk;\n";
		procedureText += "stockClerk.ClerkId = 6;\n";
		procedureText += "SELECT ANY warehouseClerk FROM INSTANCES OF WarehouseClerk WHERE selected.ClerkId == 6;\n";

		initialUserProcedure.setProcedure(procedureText);
		
		Simulator simulator = new Simulator(warehouseDomain);
		simulator.setSimulatingState(initialUserState);

		// inject self
		SimulatedClass shippingClerkSimulated = simulator.getSimulatedClass("OffDutyClerk");
		SimulatedInstance self = shippingClerkSimulated.createInstance();
		simulator.getSimulatingState().registerInstance("self", self);
		
		simulator.getSimulatingState().simulate();
		
		SimulatedInstance stockClerk = simulator.getSimulatingState().getInstanceWithName("warehouseClerk");
		Assert.assertTrue(stockClerk instanceof SimulatedHierarchyInstance);
		
		Object user1Age = simulator.getSimulatingState().getInstanceWithName("warehouseClerk").getAttribute("ClerkId");
		Assert.assertEquals(6, user1Age);
	}
	
	public void test_can_select_from_instances_of_generalisation_class() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain warehouseDomain = DomainWarehouse.getWarehouseDomain();
		EntityClass warehouseClerk = warehouseDomain.getEntityClassWithName("WarehouseClerk");
		
		EntityState initialUserState = warehouseClerk.getInitialState();
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "RECLASSIFY TO ShippingClerk;\n";
		procedureText += "CREATE stockClerk FROM StockClerk;\n";
		procedureText += "stockClerk.ClerkId = 6;\n";
		procedureText += "SELECT ANY stockClerk2 FROM INSTANCES OF StockClerk;\n";

		initialUserProcedure.setProcedure(procedureText);
		
		Simulator simulator = new Simulator(warehouseDomain);
		simulator.setSimulatingState(initialUserState);

		// inject self
		SimulatedClass shippingClerkSimulated = simulator.getSimulatedClass("OffDutyClerk");
		SimulatedInstance self = shippingClerkSimulated.createInstance();
		simulator.getSimulatingState().registerInstance("self", self);
		
		simulator.getSimulatingState().simulate();
		
		SimulatedInstance stockClerk = simulator.getSimulatingState().getInstanceWithName("stockClerk2");
		Assert.assertTrue(stockClerk instanceof SimulatedHierarchyInstance);
		
		Object user1Age = simulator.getSimulatingState().getInstanceWithName("stockClerk2").getAttribute("ClerkId");
		Assert.assertEquals(6, user1Age);
	}
	
	public void test_self_is_created_as_an_isnatnce() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain warehouseDomain = DomainWarehouse.getWarehouseDomain();
		EntityClass warehouseClerk = warehouseDomain.getEntityClassWithName("WarehouseClerk");
		
		EntityState initialUserState = warehouseClerk.getInitialState();
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "RECLASSIFY TO ShippingClerk;\n";
		procedureText += "self.AwaitingAssignment = true;\n";

		initialUserProcedure.setProcedure(procedureText);
		
		Simulator simulator = new Simulator(warehouseDomain);
		simulator.setSimulatingState(initialUserState);

		// inject self
		SimulatedClass shippingClerkSimulated = simulator.getSimulatedClass("OffDutyClerk");
		SimulatedInstance self = shippingClerkSimulated.createInstance();
		simulator.getSimulatingState().registerInstance("self", self);
		
		simulator.getSimulatingState().simulate();
		
		SimulatedInstance shippingClerk = simulator.getSimulatingState().getInstanceWithName("self");
		Assert.assertEquals("ShippingClerk", shippingClerk.getSimulatedClass().getName());
		
		Object isAwaitingAssignment = simulator.getSimulatingState().getInstanceWithName("self").getAttribute("AwaitingAssignment");
		Assert.assertEquals(true, isAwaitingAssignment);
	}
	
	public void test_simulated_reclassify_works() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityDomain warehouseDomain = DomainWarehouse.getWarehouseDomain();
		EntityClass warehouseClerk = warehouseDomain.getEntityClassWithName("WarehouseClerk");
		
		EntityState initialUserState = warehouseClerk.getInitialState();
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		String procedureText = "RECLASSIFY TO StockClerk;\n";
		procedureText += "self.Idle = false;\n";

		initialUserProcedure.setProcedure(procedureText);
		
		Simulator simulator = new Simulator(warehouseDomain);
		simulator.setSimulatingState(initialUserState);

		// inject self
		SimulatedClass shippingClerkSimulated = simulator.getSimulatedClass("OffDutyClerk");
		SimulatedInstance self = shippingClerkSimulated.createInstance();
		simulator.getSimulatingState().registerInstance("self", self);
		
		simulator.getSimulatingState().simulate();
		
		SimulatedInstance stockClerk = simulator.getSimulatingState().getInstanceWithName("self");
		Assert.assertEquals("StockClerk", stockClerk.getSimulatedClass().getName());
		
		Object isAwaitingAssignment = simulator.getSimulatingState().getInstanceWithName("self").getAttribute("Idle");
		Assert.assertEquals(false, isAwaitingAssignment);
	}
	
	public void test_if_empty_instanceset() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityClass task = new EntityClass("Task");
		testDomain.addClass(task);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		
		String procedureText = "";
		procedureText += /* 1*/ "flag = 0;\n";
		procedureText += /* 2*/ "SELECT ANY selectedTask FROM INSTANCES OF Task;\n";
		procedureText += /* 3*/ "IF EMPTY selectedTask THEN\n";
		procedureText += /* 4*/ "	flag = 1;\n";
		procedureText += /* 5*/ "ELSE\n";
		procedureText += /* 6*/ "	flag = 2;\n";
		procedureText += /* 7*/ "END IF;\n";
		
		initialUserProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		double flagValue = (Double) simulator.getSimulatingState().getTempVariable("flag");
		Assert.assertEquals(1.0, flagValue);
	}
	
	public void test_if_not_empty_else_instanceset() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityClass task = new EntityClass("Task");
		testDomain.addClass(task);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		
		String procedureText = "";
		procedureText += /* 0*/ "CREATE task1 FROM Task;\n";
		procedureText += /* 1*/ "flag = 0;\n";
		procedureText += /* 2*/ "SELECT ANY selectedTask FROM INSTANCES OF Task;\n";
		procedureText += /* 3*/ "IF NOT EMPTY selectedTask THEN\n";
		procedureText += /* 4*/ "	flag = 1;\n";
		procedureText += /* 5*/ "ELSE\n";
		procedureText += /* 6*/ "	flag = 2;\n";
		procedureText += /* 7*/ "END IF;\n";
		
		initialUserProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		double flagValue = (Double) simulator.getSimulatingState().getTempVariable("flag");
		Assert.assertEquals(1.0, flagValue);
	}
	
	
	public void test_if_not_empty_instanceset() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityClass task = new EntityClass("Task");
		testDomain.addClass(task);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		
		String procedureText = "";
		procedureText += /* 1*/ "flag = 0;\n";
		procedureText += /* 2*/ "SELECT ANY selectedTask FROM INSTANCES OF Task;\n";
		procedureText += /* 3*/ "IF NOT EMPTY selectedTask THEN\n";
		procedureText += /* 4*/ "	flag = 1;\n";
		procedureText += /* 5*/ "ELSE\n";
		procedureText += /* 6*/ "	flag = 2;\n";
		procedureText += /* 7*/ "END IF;\n";
		
		initialUserProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		double flagValue = (Double) simulator.getSimulatingState().getTempVariable("flag");
		Assert.assertEquals(2.0, flagValue);
	}
	
	public void test_if_empty_else_instanceset() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain testDomain = new EntityDomain("testDomain");
		EntityClass user = new EntityClass("User");
		testDomain.addClass(user);
		EntityClass task = new EntityClass("Task");
		testDomain.addClass(task);
		
		EntityState initialUserState = new EntityState("initial");
		user.addState(initialUserState);
		EntityProcedure initialUserProcedure = new EntityProcedure(initialUserState);
		
		String procedureText = "";
		procedureText += /* 0*/ "CREATE task1 FROM Task;\n";
		procedureText += /* 1*/ "flag = 0;\n";
		procedureText += /* 2*/ "SELECT ANY selectedTask FROM INSTANCES OF Task;\n";
		procedureText += /* 3*/ "IF EMPTY selectedTask THEN\n";
		procedureText += /* 4*/ "	flag = 1;\n";
		procedureText += /* 5*/ "ELSE\n";
		procedureText += /* 6*/ "	flag = 2;\n";
		procedureText += /* 7*/ "END IF;\n";
		
		initialUserProcedure.setProcedure(procedureText);

		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(user);
		
		Simulator simulator = new Simulator(testDomain);
		simulator.setSimulatingState(initialUserState);

		simulator.getSimulatingState().simulate();
		
		double flagValue = (Double) simulator.getSimulatingState().getTempVariable("flag");
		Assert.assertEquals(2.0, flagValue);
	}
	
}

