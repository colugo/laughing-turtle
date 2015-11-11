package test.java.weaveModelTests;

import javax.naming.NameAlreadyBoundException;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventInstance;
import main.java.avii.editor.metamodel.entities.EntityEventParam;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;
import main.java.avii.scenario.AssertionTestVectorProcedure;
import main.java.avii.scenario.TestScenario;
import main.java.avii.scenario.TestVector;
import main.java.avii.scenario.TestVectorClassInstance;
import main.java.avii.scenario.TestVectorClassTable;
import main.java.avii.scenario.TestVectorRelationInstance;
import main.java.avii.scenario.TestVectorRelationTable;

public class DomainStore {
	public EntityDomain domain;
	public EntityClass store;
	public EntityClass item;
	public EntityAttribute itemQuanitity;
	public EntityRelation r1;
	public EntityAttribute itemName;
	public EntityEventSpecification outOfStockSpec;
	public EntityEventSpecification backInStockSpec; 
	private EntityAttribute itemReorderQuanitity;

	
	public DomainStore() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException {
		this.domain = new EntityDomain("Store Domain");
		this.createClasses();
		this.createRelations();
		this.createListStateMachine();
		this.createScenarios();
	}

	private void createClasses() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException {
		this.store = new EntityClass("Store");
		this.domain.addClass(this.store);
		
		this.item = new EntityClass("Item");
		this.domain.addClass(this.item);
		this.itemQuanitity = new EntityAttribute("Quantity", IntegerEntityDatatype.getInstance());
		this.item.addAttribute(this.itemQuanitity);
		this.itemReorderQuanitity = new EntityAttribute("ReOrderQuantity", IntegerEntityDatatype.getInstance());
		this.item.addAttribute(this.itemReorderQuanitity);
		this.itemName = new EntityAttribute("Name", StringEntityDatatype.getInstance());
		this.item.addAttribute(this.itemName);
	}

	private void createListStateMachine() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException {
		EntityState initial = new EntityState("Initial");
		EntityState sellingItem = new EntityState("Selling Item");
		EntityState outOfStockItem = new EntityState("No stock");
		EntityState backInStockItem = new EntityState("Back in stock");
		
		this.item.addState(initial);
		this.item.addState(sellingItem);
		this.item.addState(outOfStockItem);
		this.item.addState(backInStockItem);
		
		String sellingItemProcedureText = "";
		sellingItemProcedureText += "self.Quantity = self.Quantity - rcvd_event.quantity;\n";
		sellingItemProcedureText += "IF self.Quantity < self.ReOrderQuantity THEN\n";
		sellingItemProcedureText += "	GENERATE outOfStock() TO self;\n";
		sellingItemProcedureText += "END IF;\n";
		sellingItem.setProcedureText(sellingItemProcedureText);
		
		EntityEventSpecification sellItem = new EntityEventSpecification(this.item, "sellItem");
		sellItem.addEventParam(new EntityEventParam("itemName", StringEntityDatatype.getInstance()));
		sellItem.addEventParam(new EntityEventParam("quantity", IntegerEntityDatatype.getInstance()));
		
		outOfStockSpec = new EntityEventSpecification(this.item, "outOfStock");
		backInStockSpec = new EntityEventSpecification(this.item, "restock");
		
		new EntityEventInstance(sellItem, initial, sellingItem);
		new EntityEventInstance(sellItem, sellingItem, sellingItem);
		new EntityEventInstance(outOfStockSpec, sellingItem, outOfStockItem);
		new EntityEventInstance(backInStockSpec, outOfStockItem, backInStockItem);
		new EntityEventInstance(sellItem, backInStockItem, sellingItem);
	}

	private void createRelations() {
		this.r1 = new EntityRelation("R1");
		this.r1.setEndA(this.store, CardinalityType.ZERO_TO_MANY);
		this.r1.setEndB(this.item, CardinalityType.ONE_TO_ONE);
	}
	
	private void createScenarios() throws InvalidActionLanguageSyntaxException
	{
		this.createStaticRepresentationScenario();
		this.createSellingItemScenario();
		this.createNoStockScenario();
	}

	private void createNoStockScenario() throws InvalidActionLanguageSyntaxException {
		TestScenario scenario = new TestScenario();
		scenario.setName("Item out of stock");
		scenario.setDescription("Items can be out of stock");
		this.domain.addScenario(scenario);
		
		this.addCantSellOutOfStockItem(scenario);
		this.addCanRestockOutOfStockItem(scenario);
		this.addCanSellFromBackInStock(scenario);
	}
	
	private void addCanSellFromBackInStock(TestScenario scenario) throws InvalidActionLanguageSyntaxException {
		TestVector testVector = new TestVector();
		
		testVector.setDescription("Items can be restocked.");
		scenario.addVector(testVector);
		
		String generateText = "";
		generateText += "GENERATE sellItem(itemName=\"Bread\", quantity=5) TO bread;\n";
		
		addBreadAndMilkToIGA(testVector,100,50,generateText,"Back in stock");
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(testVector);
		String assertionProcedureText = "";
		assertionProcedureText += "SELECT ANY selectedBread RELATED BY iga->R1 WHERE selected.Name == \"Bread\";\n";
		assertionProcedureText += "IF selectedBread.state != \"Selling Item\" THEN\n";
		assertionProcedureText += "	FAIL \"bread should be in 'Selling Item' state, but is in \" + selectedBread.state;\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "IF selectedBread.Quantity != 95 THEN\n";
		assertionProcedureText += "	FAIL \"bread can be sold when back in stock, it should be 95 but is \" + selectedBread.Quantity;\n";
		assertionProcedureText += "END IF;\n";

		
		assertionProcedure.setProcedure(assertionProcedureText);
	}
	
	private void addCanRestockOutOfStockItem(TestScenario scenario) throws InvalidActionLanguageSyntaxException {
		TestVector testVector = new TestVector();
		
		testVector.setDescription("Items can be restocked.");
		scenario.addVector(testVector);
		
		String generateText = "";
		generateText += "GENERATE restock() TO bread;\n";
		
		addBreadAndMilkToIGA(testVector,100,50,generateText,"No stock");
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(testVector);
		String assertionProcedureText = "";
		assertionProcedureText += "SELECT ANY selectedBread RELATED BY iga->R1 WHERE selected.Name == \"Bread\";\n";
		assertionProcedureText += "IF selectedBread.Quantity != 100 THEN\n";
		assertionProcedureText += "	FAIL \"bread cannot be sold when it is out of stock, it had \" + selectedBread.Quantity;\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "IF selectedBread.state != \"Back in stock\" THEN\n";
		assertionProcedureText += "	FAIL \"bread should be in 'Back in stock' state, but is in \" + selectedBread.state;\n";
		assertionProcedureText += "END IF;\n";
		
		assertionProcedure.setProcedure(assertionProcedureText);
	}
	
	
	private void addCantSellOutOfStockItem(TestScenario scenario) throws InvalidActionLanguageSyntaxException {
		TestVector testVector = new TestVector();
		
		testVector.setDescription("Items that have no stock cannot be sold.");
		scenario.addVector(testVector);
		
		String generateText = "";
		generateText += "GENERATE sellItem(itemName=\"Bread\", quantity=99) TO bread;\n";
		
		addBreadAndMilkToIGA(testVector,100,50,generateText,"No stock");
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(testVector);
		String assertionProcedureText = "";
		assertionProcedureText += "SELECT ANY selectedBread RELATED BY iga->R1 WHERE selected.Name == \"Bread\";\n";
		assertionProcedureText += "IF selectedBread.Quantity != 100 THEN\n";
		assertionProcedureText += "	FAIL \"bread cannot be sold when it is out of stock, it had \" + selectedBread.Quantity;\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "IF selectedBread.state != \"No stock\" THEN\n";
		assertionProcedureText += "	FAIL \"bread should be in 'No stock' state, but is in \" + selectedBread.state;\n";
		assertionProcedureText += "END IF;\n";
		
		assertionProcedure.setProcedure(assertionProcedureText);
	}
	
	private void createSellingItemScenario() throws InvalidActionLanguageSyntaxException {
		TestScenario scenario = new TestScenario();
		scenario.setName("Selling items");
		scenario.setDescription("When selling items from the store");
		this.domain.addScenario(scenario);
		
		this.addSellHalfTheBread(scenario);
		this.addCanHave2BreadSales(scenario);
		this.addCanSellBelowReOrderQuantity(scenario);
	}
	
	private void addCanSellBelowReOrderQuantity(TestScenario scenario) throws InvalidActionLanguageSyntaxException {
		TestVector testVector = new TestVector();
		
		testVector.setDescription("When the quantity of bread drops below the ReOrderQuantity, bread moves to the 'No stock' state.");
		testVector.isVectorApplicableWhenWoven(false);
		
		
		scenario.addVector(testVector);
		
		String generateText = "";
		generateText += "GENERATE sellItem(itemName=\"Bread\", quantity=99) TO bread;\n";
		
		addBreadAndMilkToIGA(testVector,100,50,generateText);
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(testVector);
		String assertionProcedureText = "";
		assertionProcedureText += "SELECT ANY selectedBread RELATED BY iga->R1 WHERE selected.Name == \"Bread\";\n";
		assertionProcedureText += "IF selectedBread.Quantity != 1 THEN\n";
		assertionProcedureText += "	FAIL \"The store should have 1 bread, it had \" + selectedBread.Quantity;\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "IF selectedBread.state != \"No stock\" THEN\n";
		assertionProcedureText += "	FAIL \"bread should be in 'No stock' state, but is in \" + selectedBread.state;\n";
		assertionProcedureText += "END IF;\n";
		
		assertionProcedure.setProcedure(assertionProcedureText);
	}
	
	private void addSellHalfTheBread(TestScenario scenario) throws InvalidActionLanguageSyntaxException {
		TestVector testVector = new TestVector();
		
		testVector.setDescription("Selling half the bread should reduce the amount of bread.");
		scenario.addVector(testVector);
		
		String generateText = "";
		generateText += "GENERATE sellItem(itemName=\"Bread\", quantity=50) TO bread;\n";
		
		addBreadAndMilkToIGA(testVector,100,50,generateText);
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(testVector);
		String assertionProcedureText = "";
		assertionProcedureText += "SELECT ANY selectedBread RELATED BY iga->R1 WHERE selected.Name == \"Bread\";\n";
		assertionProcedureText += "IF EMPTY selectedBread THEN\n";
		assertionProcedureText += "	FAIL \"The store should have bread\";\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "IF selectedBread.Quantity != 50 THEN\n";
		assertionProcedureText += "	FAIL \"The store should have 50 bread, it had \" + selectedBread.Quantity;\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "SELECT ANY selectedMilk RELATED BY iga->R1 WHERE selected.Name == \"Milk\";\n";
		assertionProcedureText += "IF EMPTY selectedMilk THEN\n";
		assertionProcedureText += "	FAIL \"The store should have Milk\";\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "IF selectedMilk.Quantity != 50 THEN\n";
		assertionProcedureText += "	FAIL \"The store should have 50 Milk, it had \" + selectedMilk.Quantity;\n";
		assertionProcedureText += "END IF;\n";
		
		assertionProcedure.setProcedure(assertionProcedureText);
	}
	
	private void addCanHave2BreadSales(TestScenario scenario) throws InvalidActionLanguageSyntaxException {
		TestVector testVector = new TestVector();
		
		testVector.setDescription("Can have two sales of bread.");
		scenario.addVector(testVector);
		
		String generateText = "";
		generateText += "GENERATE sellItem(itemName=\"Bread\", quantity=50) TO bread;\n";
		generateText += "GENERATE sellItem(itemName=\"Bread\", quantity=40) TO bread;\n";
		
		addBreadAndMilkToIGA(testVector,100,50,generateText);
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(testVector);
		String assertionProcedureText = "";
		assertionProcedureText += "SELECT ANY selectedBread RELATED BY iga->R1 WHERE selected.Name == \"Bread\";\n";
		assertionProcedureText += "IF EMPTY selectedBread THEN\n";
		assertionProcedureText += "	FAIL \"The store should have bread\";\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "IF selectedBread.Quantity != 10 THEN\n";
		assertionProcedureText += "	FAIL \"The store should have 10 bread, it had \" + selectedBread.Quantity;\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "SELECT ANY selectedMilk RELATED BY iga->R1 WHERE selected.Name == \"Milk\";\n";
		assertionProcedureText += "IF EMPTY selectedMilk THEN\n";
		assertionProcedureText += "	FAIL \"The store should have Milk\";\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "IF selectedMilk.Quantity != 50 THEN\n";
		assertionProcedureText += "	FAIL \"The store should have 50 Milk, it had \" + selectedMilk.Quantity;\n";
		assertionProcedureText += "END IF;\n";
		
		assertionProcedure.setProcedure(assertionProcedureText);
	}
	
	private void createStaticRepresentationScenario() throws InvalidActionLanguageSyntaxException {
		TestScenario scenario = new TestScenario();
		scenario.setName("Static representation");
		scenario.setDescription("Class model can support instance");
		this.domain.addScenario(scenario);
		
		this.addStaticMultiItemItemVector(scenario);
	}

	private void addStaticMultiItemItemVector(TestScenario scenario) throws InvalidActionLanguageSyntaxException {
		TestVector testVector = new TestVector();
		
		testVector.setDescription("Store can have single type Item");
		scenario.addVector(testVector);
		
		addBreadAndMilkToIGA(testVector,100,50,"");
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(testVector);
		String assertionProcedureText = "";
		assertionProcedureText += "SELECT ANY selectedBread RELATED BY iga->R1 WHERE selected.Name == \"Bread\";\n";
		assertionProcedureText += "IF EMPTY selectedBread THEN\n";
		assertionProcedureText += "	FAIL \"The store should have bread\";\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "IF selectedBread.Quantity != 100 THEN\n";
		assertionProcedureText += "	FAIL \"The store should have 100 bread, it had \" + selectedBread.Quantity;\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "SELECT ANY selectedMilk RELATED BY iga->R1 WHERE selected.Name == \"Milk\";\n";
		assertionProcedureText += "IF EMPTY selectedMilk THEN\n";
		assertionProcedureText += "	FAIL \"The store should have Milk\";\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "IF selectedMilk.Quantity != 50 THEN\n";
		assertionProcedureText += "	FAIL \"The store should have 50 Milk, it had \" + selectedMilk.Quantity;\n";
		assertionProcedureText += "END IF;\n";
		
		assertionProcedure.setProcedure(assertionProcedureText);
	}


	public void addBreadAndMilkToIGA(TestVector testVector, int breadQuantity, int milkQuantity, String generateText) throws InvalidActionLanguageSyntaxException {
		addBreadAndMilkToIGA(testVector, breadQuantity, milkQuantity, generateText, "");
	}
	
	public void addBreadAndMilkToIGA(TestVector testVector, int breadQuantity, int milkQuantity, String generateText, String breadState) throws InvalidActionLanguageSyntaxException {
	
		TestVectorClassTable storeTable = new TestVectorClassTable(this.store);
		testVector.addClassTable(storeTable);
		TestVectorClassInstance iga = storeTable.createInstance();
		iga.setName("iga");
		
		TestVectorClassTable itemTable = new TestVectorClassTable(this.item);
		testVector.addClassTable(itemTable);
		itemTable.addAttribute(itemQuanitity);
		itemTable.addAttribute(itemReorderQuanitity);
		itemTable.addAttribute(itemName);
		
		TestVectorClassInstance bread = itemTable.createInstance();
		bread.setInitialAttribute("Quantity", Integer.toString(breadQuantity));
		bread.setInitialAttribute("ReOrderQuantity", "5");
		bread.setInitialAttribute("Name", "Bread");
		bread.setName("bread");
		if(breadState != "")
		{
			bread.setInitialState(breadState);
		}
		
		TestVectorClassInstance milk = itemTable.createInstance();
		milk.setInitialAttribute("Quantity", Integer.toString(milkQuantity));
		milk.setInitialAttribute("ReOrderQuantity", "5");
		milk.setInitialAttribute("Name", "Milk");
		milk.setName("milk");

		TestVectorRelationTable listListOrderTable = new TestVectorRelationTable(r1);
		testVector.addRelationTable(listListOrderTable);
		TestVectorRelationInstance igaBread = listListOrderTable.createInstance();
		igaBread.setEndA("iga");
		igaBread.setEndB("bread");
		TestVectorRelationInstance igaMilk = listListOrderTable.createInstance();
		igaMilk.setEndA("iga");
		igaMilk.setEndB("milk");
		
		testVector.addInitialGenerateLanguage(generateText);
		
		testVector.createInitialProcedureFromTables();
	}
	
}
