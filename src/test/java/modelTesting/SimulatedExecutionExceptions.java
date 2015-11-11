package test.java.modelTesting;

import test.java.helper.DomainShoppingCart;

import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;
import main.java.avii.scenario.AssertionTestVectorProcedure;
import main.java.avii.scenario.TestScenario;
import main.java.avii.scenario.TestVector;
import main.java.avii.scenario.TestVectorClassInstance;
import main.java.avii.scenario.TestVectorClassTable;
import main.java.avii.scenario.TestVectorRelationInstance;
import main.java.avii.scenario.TestVectorRelationTable;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.exceptions.SimulationException;
import main.java.avii.simulator.simulatedTypes.SimulatedTestVector;
import test.java.tests.SimulatedTestScenario;
import test.java.tests.TestHarness;

public class SimulatedExecutionExceptions extends TestCase {
	public SimulatedExecutionExceptions(String name)
	{
		super(name);
	}
	
	public void test_when_creating_duplicate_relation_an_error_is_thrown() throws NameNotFoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityClass item = domain.getEntityClassWithName("Item");
		EntityClass itemSelection =domain.getEntityClassWithName("ItemSelection");
		EntityRelation r1 =  domain.getRelationWithName("R1");
	
		TestVector vector = new TestVector();
		vector.setDescription("Initialising shopping cart with relation table gets items with quantity");
		
		TestScenario scenarioAddingSelection = new TestScenario();
		scenarioAddingSelection.setName("Adding items to an existing cart");
		domain.addScenario(scenarioAddingSelection);
		scenarioAddingSelection.addVector(vector);
		
		TestVectorClassTable cartTable = new TestVectorClassTable(cart);
		TestVectorClassInstance newCart = cartTable.createInstance();
		newCart.setName("newCart");
		vector.addClassTable(cartTable);
		
		TestVectorClassTable itemTable = new TestVectorClassTable(item);
		itemTable.addAttribute(item.getAttributeWithName("Name"));
		TestVectorClassInstance newItem = itemTable.createInstance();
		newItem.setName("newItem");
		newItem.setInitialAttribute("Name", "CocoPops");
		vector.addClassTable(itemTable);
		
		TestVectorClassTable itemSelectionTable = new TestVectorClassTable(itemSelection);
		itemSelectionTable.addAttribute(itemSelection.getAttributeWithName("Quantity"));
		TestVectorClassInstance newItemSelection = itemSelectionTable.createInstance();
		newItemSelection.setName("newItemSelection");
		newItemSelection.setInitialAttribute("Quantity", "2");
		vector.addClassTable(itemSelectionTable);
		
		TestVectorRelationTable relationTable = new TestVectorRelationTable(r1);
		TestVectorRelationInstance r1_1 = relationTable.createInstance();
		r1_1.setEndA("newCart");
		r1_1.setEndB("newItem");
		r1_1.setAssociation("newItemSelection");
		vector.addRelationTable(relationTable);
		
		TestVectorRelationInstance r1_2 = relationTable.createInstance();
		r1_2.setEndA("newCart");
		r1_2.setEndB("newItem");
		vector.addRelationTable(relationTable);
		
		vector.createInitialProcedureFromTables();
		
		String assertionProcString = "";
		assertionProcString += "IF newItemSelection.Quantity != 2 THEN\n";
		assertionProcString += "	FAIL \"newCart should have had quantity of 2 CocoPops but was \" + newItemSelection.Quantity;\n";
		assertionProcString += "END IF;\n";
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(vector);
		assertionProcedure.setProcedure(assertionProcString);
		
		TestHarness harness = new TestHarness(domain);
		harness.execute();
		Assert.assertEquals(true, harness.allAssertionsPassed());
		Assert.assertEquals(true, harness.wereExceptionsRaised());
		SimulatedTestScenario scenario = harness.getScenarios().get(0);
		SimulatedTestVector testVector = scenario.getSimulatedVectors().get(0);
		SimulationException exception = (SimulationException) testVector.getException();
		
		Assert.assertEquals("Duplicate relation(R1) found between newCart and newItem", exception.getMessage());
		Assert.assertEquals(5, exception.getLineNumber());
		Assert.assertEquals("RELATE newCart TO newItem ACROSS R1 CREATING ItemSelection_1;", exception.getActionLanguage());
	}
	
	public void test_null_simulated_instance_exception() throws NameNotFoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		EntityClass cart = domain.getEntityClassWithName("ShoppingCart");
		EntityClass item = domain.getEntityClassWithName("Item");
		
		TestVector vector = new TestVector();
		vector.setDescription("Initialising shopping cart with relation table gets items with quantity");
		
		TestScenario scenarioAddingSelection = new TestScenario();
		scenarioAddingSelection.setName("Adding items to an existing cart");
		domain.addScenario(scenarioAddingSelection);
		scenarioAddingSelection.addVector(vector);
		
		TestVectorClassTable cartTable = new TestVectorClassTable(cart);
		TestVectorClassInstance newCart = cartTable.createInstance();
		newCart.setName("newCart");
		vector.addClassTable(cartTable);
		
		TestVectorClassTable itemTable = new TestVectorClassTable(item);
		itemTable.addAttribute(item.getAttributeWithName("Name"));
		TestVectorClassInstance newItem = itemTable.createInstance();
		newItem.setName("newItem");
		newItem.setInitialAttribute("Name", "CocoPops");
		vector.addClassTable(itemTable);
		
		vector.createInitialProcedureFromTables();
		
		String assertionProcString = "";
		assertionProcString += "SELECT ANY itemInCart RELATED BY newCart->R1;\n";
		assertionProcString += "FAIL \"newCart should have been empty, but it contained \" + itemInCart.Name;\n";
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(vector);
		assertionProcedure.setProcedure(assertionProcString);
		
		TestHarness harness = new TestHarness(domain);
		harness.execute();
		Assert.assertEquals(true, harness.allAssertionsPassed());
		Assert.assertEquals(true, harness.wereExceptionsRaised());
		SimulatedTestScenario scenario = harness.getScenarios().get(0);
		SimulatedTestVector testVector = scenario.getSimulatedVectors().get(0);
		SimulationException exception = (SimulationException) testVector.getException();
		
		Assert.assertEquals("An attempt was made to reference an instance(itemInCart.Name) that was null.", exception.getMessage());
		Assert.assertEquals(2, exception.getLineNumber());
		Assert.assertEquals("FAIL \"newCart should have been empty, but it contained \" + itemInCart.Name;", exception.getActionLanguage());

	}
	
	public void test_when_creating_duplicate_reflexive_relation_an_error_is_thrown() throws NameNotFoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = new EntityDomain("domain");
		EntityClass task = new EntityClass("Task");
		domain.addClass(task);
		EntityRelation r4 =  new EntityRelation("R4");
		r4.setEndA(task, CardinalityType.ZERO_TO_MANY, "leads");
		r4.setEndB(task, CardinalityType.ZERO_TO_MANY, "follows");
	
		TestVector vector = new TestVector();
		vector.setDescription("Cannot have duplicate reflexive relations");
		
		TestScenario scenarioAddingSelection = new TestScenario();
		scenarioAddingSelection.setName("Duplicate reflexive relations");
		domain.addScenario(scenarioAddingSelection);
		scenarioAddingSelection.addVector(vector);
		
		TestVectorClassTable cartTable = new TestVectorClassTable(task);
		cartTable.createInstance();
		cartTable.createInstance();
		vector.addClassTable(cartTable);
		
		TestVectorRelationTable relationTable = new TestVectorRelationTable(r4);
		vector.addRelationTable(relationTable);
		TestVectorRelationInstance r4_1 = relationTable.createInstance();
		r4_1.setEndA("Task_001");
		r4_1.setEndB("Task_002");
		TestVectorRelationInstance r4_2 = relationTable.createInstance();
		r4_2.setEndA("Task_001");
		r4_2.setEndB("Task_002");
		
		vector.createInitialProcedureFromTables();
		
		String assertionProcString = "";
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(vector);
		assertionProcedure.setProcedure(assertionProcString);
		
		TestHarness harness = new TestHarness(domain);
		harness.execute();
		Assert.assertEquals(true, harness.allAssertionsPassed());
		Assert.assertEquals(true, harness.wereExceptionsRaised());
		SimulatedTestScenario scenario = harness.getScenarios().get(0);
		SimulatedTestVector testVector = scenario.getSimulatedVectors().get(0);
		SimulationException exception = (SimulationException) testVector.getException();
		
		Assert.assertEquals("Duplicate relation(R4) found between Task_001 and Task_002", exception.getMessage());
		Assert.assertEquals(4, exception.getLineNumber());
		Assert.assertEquals("RELATE Task_001 TO Task_002 ACROSS R4.\"leads\";", exception.getActionLanguage());
	}
	
}
