package test.java.modelTesting;

import javax.naming.NameAlreadyBoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
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
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import test.java.tests.TestHarness;

public class SimulatedListTests extends TestCase {
	
	private EntityDomain _domain;
	private EntityClass _list;
	private EntityClass _listOrder;
	private EntityClass _listItem;
	private EntityAttribute _listSize;
	private EntityAttribute _listOrderOrder;
	private EntityAttribute _listItemName;
	private EntityRelation _rListListOrder;
	private EntityRelation _rListOrderListItem;

	public SimulatedListTests(String name) throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException {
		super(name);
		
		this.setupDomain();
	}
	
	private void setupDomain() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException {
		this._domain = new EntityDomain("List Domain");
		this.createClasses();
		this.createRelations();
		this.createListStateMachine();
		this.createScenarios();
	}

	private void createClasses() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException {
		this._list = new EntityClass("List");
		this._domain.addClass(this._list);
		this._listSize = new EntityAttribute("Size", IntegerEntityDatatype.getInstance());
		this._list.addAttribute(this._listSize);
		
		this._listOrder = new EntityClass("ListOrder");
		this._domain.addClass(this._listOrder);
		this._listOrderOrder = new EntityAttribute("Order", IntegerEntityDatatype.getInstance());
		this._listOrder.addAttribute(this._listOrderOrder);
		
		this._listItem = new EntityClass("ListItem");
		this._domain.addClass(this._listItem);
		this._listItemName = new EntityAttribute("Name", StringEntityDatatype.getInstance());
		this._listItem.addAttribute(_listItemName);
		
	}

	private void createListStateMachine() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException {
		EntityState initial = new EntityState("Initial");
		EntityState addingItemToList = new EntityState("Adding ListItem to List");
		EntityState deletingItemFromList = new EntityState("Deleting ListItem from List");
		this._list.addState(initial);
		this._list.addState(addingItemToList);
		this._list.addState(deletingItemFromList);
		
		String addingItemToListProcedureText = "";
		addingItemToListProcedureText += "self.Size = self.Size + 1;\n";
		addingItemToListProcedureText += "CREATE listItem FROM ListItem;\n";
		addingItemToListProcedureText += "listItem.Name = rcvd_event.itemName;\n";
		addingItemToListProcedureText += "CREATE listOrder FROM ListOrder;\n";
		addingItemToListProcedureText += "listOrder.Order = self.Size;\n";
		addingItemToListProcedureText += "RELATE listOrder TO self ACROSS ListListOrder;\n";
		addingItemToListProcedureText += "RELATE listOrder TO listItem ACROSS ListOrderListItem;\n";
		addingItemToList.setProcedureText(addingItemToListProcedureText);
		
		String deletingItemFromListProcedureText = "";
		deletingItemFromListProcedureText += "SELECT ANY itemToDelete RELATED BY self->ListListOrder->ListOrderListItem WHERE selected.Name == rcvd_event.itemName;\n";
		deletingItemFromListProcedureText += "IF EMPTY itemToDelete THEN\n";
		deletingItemFromListProcedureText += "	RETURN;\n";
		deletingItemFromListProcedureText += "END IF;\n";
		
		deletingItemFromListProcedureText += "SELECT ANY itemOrderToDelete RELATED BY itemToDelete->ListOrderListItem;\n";
		deletingItemFromListProcedureText += "shiftDownIfAbove = itemOrderToDelete.Order;\n";
		deletingItemFromListProcedureText += "DELETE itemToDelete;\n";
		deletingItemFromListProcedureText += "DELETE itemOrderToDelete;\n";
		deletingItemFromListProcedureText += "self.Size = self.Size - 1;\n";
		
		deletingItemFromListProcedureText += "SELECT MANY orders RELATED BY self->ListListOrder;\n";
		deletingItemFromListProcedureText += "FOR order IN orders DO\n";
		deletingItemFromListProcedureText += "	IF order.Order > shiftDownIfAbove THEN\n";
		deletingItemFromListProcedureText += "		order.Order = order.Order - 1;\n";
		deletingItemFromListProcedureText += "	END IF;\n";
		deletingItemFromListProcedureText += "END FOR;\n";
		
		deletingItemFromList.setProcedureText(deletingItemFromListProcedureText);
		
		EntityEventSpecification addListItem = new EntityEventSpecification(this._list, "addListItem");
		addListItem.addEventParam(new EntityEventParam("itemName", StringEntityDatatype.getInstance()));
		
		EntityEventSpecification deleteListItem = new EntityEventSpecification(this._list, "deleteListItem");
		deleteListItem.addEventParam(new EntityEventParam("itemName", StringEntityDatatype.getInstance()));
		
		new EntityEventInstance(addListItem, initial, addingItemToList);
		new EntityEventInstance(addListItem, addingItemToList, addingItemToList);
		new EntityEventInstance(deleteListItem, addingItemToList, deletingItemFromList);
	}

	private void createRelations() {
		this._rListListOrder = new EntityRelation("ListListOrder");
		this._rListListOrder.setEndA(this._list, CardinalityType.ZERO_TO_MANY);
		this._rListListOrder.setEndB(this._listOrder, CardinalityType.ONE_TO_ONE);
		
		this._rListOrderListItem = new EntityRelation("ListOrderListItem");
		this._rListOrderListItem.setEndA(this._listOrder, CardinalityType.ONE_TO_ONE);
		this._rListOrderListItem.setEndB(this._listItem, CardinalityType.ONE_TO_ONE);
	}
	
	private void createScenarios() throws InvalidActionLanguageSyntaxException
	{
		this.createStaticRepresentationScenario();
		this.createAddItemToListScenario();
		this.createDeleteItemFromListScenario();
	}

	private void createDeleteItemFromListScenario() throws InvalidActionLanguageSyntaxException {
		TestScenario scenario = new TestScenario();
		scenario.setName("Deletig items from a list");
		scenario.setDescription("Items must be in a unique order in the list.");
		this._domain.addScenario(scenario);
		
		this.addDeletingOnlyItemSetsListSizeTo0Vector(scenario);
		this.addDeletingFirstItemSetsSecondItemOrderTo1Vector(scenario);
		this.addDeletingSecondItemLeavesFirstOrderUnchangedVector(scenario);
	}
	
	private void addDeletingOnlyItemSetsListSizeTo0Vector(TestScenario scenario) throws InvalidActionLanguageSyntaxException {
		TestVector testVector = new TestVector();
		
		testVector= new TestVector();
		testVector.setDescription("List is empty when deleting only item");
		scenario.addVector(testVector);
		
		TestVectorClassTable listTable = new TestVectorClassTable(this._list);
		testVector.addClassTable(listTable);
		listTable.addAttribute(_listSize);
		TestVectorClassInstance list = listTable.createInstance();
		list.setInitialAttribute("Size", "1");
		list.setInitialState("Adding ListItem to List");
		
		TestVectorClassTable listOrderTable = new TestVectorClassTable(this._listOrder);
		testVector.addClassTable(listOrderTable);
		listOrderTable.addAttribute(_listOrderOrder);
		TestVectorClassInstance listOrder = listOrderTable.createInstance();
		listOrder.setInitialAttribute("Order", "1");
		
		TestVectorClassTable listItemTable = new TestVectorClassTable(this._listItem);
		testVector.addClassTable(listItemTable);
		listItemTable.addAttribute(_listItemName);
		TestVectorClassInstance listItem = listItemTable.createInstance();
		listItem.setInitialAttribute("Name", "First item");
		
		TestVectorRelationTable listListOrderTable = new TestVectorRelationTable(_rListListOrder);
		testVector.addRelationTable(listListOrderTable);
		TestVectorRelationInstance listListOrderInstance = listListOrderTable.createInstance();
		listListOrderInstance.setEndA("List_001");
		listListOrderInstance.setEndB("ListOrder_001");
		
		TestVectorRelationTable listOrderListItemTable = new TestVectorRelationTable(_rListOrderListItem);
		testVector.addRelationTable(listOrderListItemTable);
		TestVectorRelationInstance listOrderListitemInstance = listOrderListItemTable.createInstance();
		listOrderListitemInstance.setEndA("ListOrder_001");
		listOrderListitemInstance.setEndB("ListItem_001");
		
		testVector.createInitialProcedureFromTables();
		String generateText = "";
		generateText += "GENERATE deleteListItem(itemName=\"First item\") TO List_001;\n";
		testVector.addInitialGenerateLanguage(generateText);
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(testVector);
		String assertionProcedureText = "";
		assertionProcedureText += "SELECT ANY firstItem RELATED BY List_001->ListListOrder->ListOrderListItem;\n";
		assertionProcedureText += "IF NOT EMPTY firstItem THEN\n";
		assertionProcedureText += "	FAIL \"The first item should have been deleted. Its name is \" + firstItem.Name;\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "IF List_001.Size != 0 THEN\n";
		assertionProcedureText += "	FAIL \"The list should be empty, but has size of \" + List_001.Size;\n";
		assertionProcedureText += "END IF;\n";
		
		assertionProcedure.setProcedure(assertionProcedureText);
	}
	
	private void addDeletingFirstItemSetsSecondItemOrderTo1Vector(TestScenario scenario) throws InvalidActionLanguageSyntaxException {
		TestVector testVector = new TestVector();
		
		testVector= new TestVector();
		testVector.setDescription("List is empty when deleting only item");
		scenario.addVector(testVector);
		
		TestVectorClassTable listTable = new TestVectorClassTable(this._list);
		testVector.addClassTable(listTable);
		listTable.addAttribute(_listSize);
		TestVectorClassInstance list = listTable.createInstance();
		list.setInitialAttribute("Size", "2");
		list.setInitialState("Adding ListItem to List");
		
		TestVectorClassTable listOrderTable = new TestVectorClassTable(this._listOrder);
		testVector.addClassTable(listOrderTable);
		listOrderTable.addAttribute(_listOrderOrder);
		TestVectorClassInstance listOrder1 = listOrderTable.createInstance();
		listOrder1.setInitialAttribute("Order", "1");
		TestVectorClassInstance listOrder2 = listOrderTable.createInstance();
		listOrder2.setInitialAttribute("Order", "2");
		
		TestVectorClassTable listItemTable = new TestVectorClassTable(this._listItem);
		testVector.addClassTable(listItemTable);
		listItemTable.addAttribute(_listItemName);
		TestVectorClassInstance listItem1 = listItemTable.createInstance();
		listItem1.setInitialAttribute("Name", "First item");
		TestVectorClassInstance listItem2 = listItemTable.createInstance();
		listItem2.setInitialAttribute("Name", "Second item");
		
		TestVectorRelationTable listListOrderTable = new TestVectorRelationTable(_rListListOrder);
		testVector.addRelationTable(listListOrderTable);
		TestVectorRelationInstance listListOrderInstance1 = listListOrderTable.createInstance();
		listListOrderInstance1.setEndA("List_001");
		listListOrderInstance1.setEndB("ListOrder_001");
		TestVectorRelationInstance listListOrderInstance2 = listListOrderTable.createInstance();
		listListOrderInstance2.setEndA("List_001");
		listListOrderInstance2.setEndB("ListOrder_002");
		
		TestVectorRelationTable listOrderListItemTable = new TestVectorRelationTable(_rListOrderListItem);
		testVector.addRelationTable(listOrderListItemTable);
		TestVectorRelationInstance listOrderListitemInstance1 = listOrderListItemTable.createInstance();
		listOrderListitemInstance1.setEndA("ListOrder_001");
		listOrderListitemInstance1.setEndB("ListItem_001");
		TestVectorRelationInstance listOrderListitemInstance2 = listOrderListItemTable.createInstance();
		listOrderListitemInstance2.setEndA("ListOrder_002");
		listOrderListitemInstance2.setEndB("ListItem_002");
		
		testVector.createInitialProcedureFromTables();
		String generateText = "";
		generateText += "GENERATE deleteListItem(itemName=\"First item\") TO List_001;\n";
		testVector.addInitialGenerateLanguage(generateText);
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(testVector);
		String assertionProcedureText = "";
		assertionProcedureText += "SELECT ANY firstItem RELATED BY List_001->ListListOrder->ListOrderListItem WHERE selected.Name == \"First item\";\n";
		assertionProcedureText += "IF NOT EMPTY firstItem THEN\n";
		assertionProcedureText += "	FAIL \"The first item should have been deleted. Its name is \" + firstItem.Name;\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "IF List_001.Size != 1 THEN\n";
		assertionProcedureText += "	FAIL \"The list should have one item, but has size of \" + List_001.Size;\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "SELECT ANY item RELATED BY List_001->ListListOrder->ListOrderListItem;\n";
		assertionProcedureText += "SELECT ANY order RELATED BY item->ListOrderListItem;\n";
		assertionProcedureText += "IF order.Order != 1 THEN\n";
		assertionProcedureText += "	FAIL \"The the orders should all have decreased by 1, but the first order is \" + order.Order;\n";
		assertionProcedureText += "END IF;\n";
		
		assertionProcedure.setProcedure(assertionProcedureText);
	}
	
	private void addDeletingSecondItemLeavesFirstOrderUnchangedVector(TestScenario scenario) throws InvalidActionLanguageSyntaxException {
		TestVector testVector = new TestVector();
		
		testVector= new TestVector();
		testVector.setDescription("Orders before the deleted item are not changed");
		scenario.addVector(testVector);
		
		TestVectorClassTable listTable = new TestVectorClassTable(this._list);
		testVector.addClassTable(listTable);
		listTable.addAttribute(_listSize);
		TestVectorClassInstance list = listTable.createInstance();
		list.setInitialAttribute("Size", "2");
		list.setInitialState("Adding ListItem to List");
		
		TestVectorClassTable listOrderTable = new TestVectorClassTable(this._listOrder);
		testVector.addClassTable(listOrderTable);
		listOrderTable.addAttribute(_listOrderOrder);
		TestVectorClassInstance listOrder1 = listOrderTable.createInstance();
		listOrder1.setInitialAttribute("Order", "1");
		TestVectorClassInstance listOrder2 = listOrderTable.createInstance();
		listOrder2.setInitialAttribute("Order", "2");
		
		TestVectorClassTable listItemTable = new TestVectorClassTable(this._listItem);
		testVector.addClassTable(listItemTable);
		listItemTable.addAttribute(_listItemName);
		TestVectorClassInstance listItem1 = listItemTable.createInstance();
		listItem1.setInitialAttribute("Name", "First item");
		TestVectorClassInstance listItem2 = listItemTable.createInstance();
		listItem2.setInitialAttribute("Name", "Second item");
		
		TestVectorRelationTable listListOrderTable = new TestVectorRelationTable(_rListListOrder);
		testVector.addRelationTable(listListOrderTable);
		TestVectorRelationInstance listListOrderInstance1 = listListOrderTable.createInstance();
		listListOrderInstance1.setEndA("List_001");
		listListOrderInstance1.setEndB("ListOrder_001");
		TestVectorRelationInstance listListOrderInstance2 = listListOrderTable.createInstance();
		listListOrderInstance2.setEndA("List_001");
		listListOrderInstance2.setEndB("ListOrder_002");
		
		TestVectorRelationTable listOrderListItemTable = new TestVectorRelationTable(_rListOrderListItem);
		testVector.addRelationTable(listOrderListItemTable);
		TestVectorRelationInstance listOrderListitemInstance1 = listOrderListItemTable.createInstance();
		listOrderListitemInstance1.setEndA("ListOrder_001");
		listOrderListitemInstance1.setEndB("ListItem_001");
		TestVectorRelationInstance listOrderListitemInstance2 = listOrderListItemTable.createInstance();
		listOrderListitemInstance2.setEndA("ListOrder_002");
		listOrderListitemInstance2.setEndB("ListItem_002");
		
		testVector.createInitialProcedureFromTables();
		String generateText = "";
		generateText += "GENERATE deleteListItem(itemName=\"Second item\") TO List_001;\n";
		testVector.addInitialGenerateLanguage(generateText);
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(testVector);
		String assertionProcedureText = "";
		assertionProcedureText += "SELECT ANY secondItem RELATED BY List_001->ListListOrder->ListOrderListItem WHERE selected.Name == \"Second item\";\n";
		assertionProcedureText += "IF NOT EMPTY secondItem THEN\n";
		assertionProcedureText += "	FAIL \"The second item should have been deleted. Its name is \" + secondItem.Name;\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "IF List_001.Size != 1 THEN\n";
		assertionProcedureText += "	FAIL \"The list should have one item, but has size of \" + List_001.Size;\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "SELECT ANY item RELATED BY List_001->ListListOrder->ListOrderListItem;\n";
		assertionProcedureText += "SELECT ANY order RELATED BY item->ListOrderListItem;\n";
		assertionProcedureText += "IF order.Order != 1 THEN\n";
		assertionProcedureText += "	FAIL \"Orders before the deleted order should not be changed, the first order is now \" + order.Order;\n";
		assertionProcedureText += "END IF;\n";
		
		assertionProcedure.setProcedure(assertionProcedureText);
	}

	private void createAddItemToListScenario() throws InvalidActionLanguageSyntaxException {
		TestScenario scenario = new TestScenario();
		scenario.setName("Adding items to a list");
		scenario.setDescription("Items must be in a unique order in the list.");
		this._domain.addScenario(scenario);
		
		this.addAddingFirstItemGetsOrder1Vector(scenario);
		this.addAddingSecondItemGetsOrder2Vector(scenario);
	}

	private void addAddingFirstItemGetsOrder1Vector(TestScenario scenario) throws InvalidActionLanguageSyntaxException {
		TestVector testVector = new TestVector();
		
		testVector= new TestVector();
		testVector.setDescription("First item in list has order of 1");
		scenario.addVector(testVector);
		
		TestVectorClassTable listTable = new TestVectorClassTable(this._list);
		testVector.addClassTable(listTable);
		listTable.createInstance();
		
		String generateText = "";
		generateText += "GENERATE addListItem(itemName=\"First item\") TO List_001;\n";
		testVector.addInitialGenerateLanguage(generateText);
		
		testVector.createInitialProcedureFromTables();
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(testVector);
		String assertionProcedureText = "";
		assertionProcedureText += "SELECT ANY firstItem RELATED BY List_001->ListListOrder->ListOrderListItem;\n";
		assertionProcedureText += "IF firstItem.Name != \"First item\" THEN\n";
		assertionProcedureText += "	FAIL \"The first item must have a name of 'First item', but was \" + firstItem.Name;\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "SELECT ANY firstItemOrder RELATED BY firstItem->ListOrderListItem;\n";
		assertionProcedureText += "IF firstItemOrder.Order != 1 THEN\n";
		assertionProcedureText += "	FAIL \"The first item must be at order 1, but was \" + firstItemOrder.Order;\n";
		assertionProcedureText += "END IF;\n";
		
		assertionProcedure.setProcedure(assertionProcedureText);
	}
	
	private void addAddingSecondItemGetsOrder2Vector(TestScenario scenario) throws InvalidActionLanguageSyntaxException {
		TestVector testVector = new TestVector();
		
		testVector= new TestVector();
		testVector.setDescription("Second item in list has order of 2");
		scenario.addVector(testVector);
		
		TestVectorClassTable listTable = new TestVectorClassTable(this._list);
		testVector.addClassTable(listTable);
		listTable.createInstance();
		
		String generateText = "";
		generateText += "GENERATE addListItem(itemName=\"First item\") TO List_001;\n";
		generateText += "GENERATE addListItem(itemName=\"Second item\") TO List_001;\n";
		testVector.addInitialGenerateLanguage(generateText);
		
		testVector.createInitialProcedureFromTables();
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(testVector);
		String assertionProcedureText = "";
		assertionProcedureText += "SELECT ANY firstOrder RELATED BY List_001->ListListOrder WHERE selected.Order == 1;\n";
		assertionProcedureText += "SELECT ANY firstItem RELATED BY firstOrder->ListOrderListItem;\n";
		assertionProcedureText += "IF firstItem.Name != \"First item\" THEN\n";
		assertionProcedureText += "		FAIL \"The first item must have a name of 'First item', but was \" + firstItem.Name;\n";
		assertionProcedureText += "END IF;\n";
		assertionProcedureText += "SELECT ANY secondOrder RELATED BY List_001->ListListOrder WHERE selected.Order == 2;\n";
		assertionProcedureText += "SELECT ANY secondItem RELATED BY secondOrder->ListOrderListItem;\n";
		assertionProcedureText += "IF secondItem.Name != \"Second item\" THEN\n";
		assertionProcedureText += "		FAIL \"The second item must have a name of 'Second item', but was \" + secondItem.Name;\n";
		assertionProcedureText += "END IF;\n";
		
		assertionProcedure.setProcedure(assertionProcedureText);
		
	}

	private void createStaticRepresentationScenario() throws InvalidActionLanguageSyntaxException {
		TestScenario scenario = new TestScenario();
		scenario.setName("Static representation");
		scenario.setDescription("Class model can support instance");
		this._domain.addScenario(scenario);
		
		this.addStaticSingleListItemVector(scenario);
		this.addEmptyListIsValidVector(scenario);
	}

	private void addStaticSingleListItemVector(TestScenario scenario) throws InvalidActionLanguageSyntaxException {
		TestVector testVector = new TestVector();
		
		testVector.setDescription("List can have single item");
		scenario.addVector(testVector);
		
		TestVectorClassTable listTable = new TestVectorClassTable(this._list);
		testVector.addClassTable(listTable);
		listTable.addAttribute(_listSize);
		TestVectorClassInstance list = listTable.createInstance();
		list.setInitialAttribute("Size", "1");
		
		TestVectorClassTable listOrderTable = new TestVectorClassTable(this._listOrder);
		testVector.addClassTable(listOrderTable);
		listOrderTable.addAttribute(_listOrderOrder);
		TestVectorClassInstance listOrder = listOrderTable.createInstance();
		listOrder.setInitialAttribute("Order", "1");
		
		TestVectorClassTable listItemTable = new TestVectorClassTable(this._listItem);
		testVector.addClassTable(listItemTable);
		listItemTable.addAttribute(_listItemName);
		TestVectorClassInstance listItem = listItemTable.createInstance();
		listItem.setInitialAttribute("Name", "First item");
		
		TestVectorRelationTable listListOrderTable = new TestVectorRelationTable(_rListListOrder);
		testVector.addRelationTable(listListOrderTable);
		TestVectorRelationInstance listListOrderInstance = listListOrderTable.createInstance();
		listListOrderInstance.setEndA("List_001");
		listListOrderInstance.setEndB("ListOrder_001");
		
		TestVectorRelationTable listOrderListItemTable = new TestVectorRelationTable(_rListOrderListItem);
		testVector.addRelationTable(listOrderListItemTable);
		TestVectorRelationInstance listOrderListitemInstance = listOrderListItemTable.createInstance();
		listOrderListitemInstance.setEndA("ListOrder_001");
		listOrderListitemInstance.setEndB("ListItem_001");
		
		testVector.createInitialProcedureFromTables();
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		
		assertionProcedure.setVector(testVector);
		String assertionProcedureText = "";
		assertionProcedureText += "SELECT ANY firstItem RELATED BY List_001->ListListOrder->ListOrderListItem;\n";
		assertionProcedureText += "IF firstItem.Name != ListItem_001.Name THEN\n";
		assertionProcedureText += "	FAIL \"The first item must have a name of 'First item', but was \" + firstItem.Name;\n";
		assertionProcedureText += "END IF;\n";
		
		assertionProcedure.setProcedure(assertionProcedureText);
	}
		
	private void addEmptyListIsValidVector(TestScenario scenario) throws InvalidActionLanguageSyntaxException {
		TestVector testVector = new TestVector();
		
		testVector= new TestVector();
		testVector.setDescription("List can be empty");
		scenario.addVector(testVector);
		
		TestVectorClassTable listTable = new TestVectorClassTable(this._list);
		testVector.addClassTable(listTable);
		listTable.createInstance();
		
		testVector.createInitialProcedureFromTables();
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(testVector);
		String assertionProcedureText = "";
		assertionProcedureText += "# no procedure, checking static validity";
		
		assertionProcedure.setProcedure(assertionProcedureText);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void test_run_simulated_list_harness() throws CannotSimulateDomainThatIsInvalidException
	{
		TestHarness harness = new TestHarness(this._domain);
		harness.execute();

		SimulatedTestHelper.printHarnessResults(harness);
		Assert.assertEquals(true, harness.allAssertionsPassed());
		Assert.assertEquals(false, harness.wereExceptionsRaised());
	}
}
