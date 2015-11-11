package test.java.modelTesting;

import test.java.helper.DomainShoppingCart;

import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.scenario.AssertionTestVectorProcedure;
import main.java.avii.scenario.TestScenario;
import main.java.avii.scenario.TestVector;
import main.java.avii.scenario.TestVectorClassInstance;
import main.java.avii.scenario.TestVectorClassTable;
import main.java.avii.scenario.TestVectorRelationInstance;
import main.java.avii.scenario.TestVectorRelationTable;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import test.java.tests.TestHarness;

public class SimulatedShoppingCartDomainTests extends TestCase {
	
	EntityDomain _domain = null;
	EntityClass _cart = null;
	EntityClass _item = null;
	EntityClass _itemSelection = null;
	EntityRelation _r1 = null;
	
	TestScenario _scenarioNewCart = new TestScenario();
	TestScenario _scenarioAddingSelection = new TestScenario();
	TestScenario _scenarioCancellingOrder = new TestScenario();
	
	public SimulatedShoppingCartDomainTests(String name) throws NameNotFoundException, InvalidActionLanguageSyntaxException {
		super(name);
		this._domain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		//this._domain = DomainShoppingCart.getShoppingCartDomain();
		this._cart = this._domain.getEntityClassWithName("ShoppingCart");
		this._item = this._domain.getEntityClassWithName("Item");
		this._itemSelection = this._domain.getEntityClassWithName("ItemSelection");
		this._r1 = this._domain.getRelationWithName("R1");
		this.addTestScenarios();
	}
	
	private void addTestScenarios() throws InvalidActionLanguageSyntaxException, NameNotFoundException {
		addNewShoppingCartScenario();
		addAddingSelectionScenario();
		addCancellingScenario();
	}

	public void addAddingSelectionScenario() throws InvalidActionLanguageSyntaxException, NameNotFoundException {
		_scenarioAddingSelection.setName("Adding items to an existing cart");
		this._domain.addScenario(_scenarioAddingSelection);
		
		this.addAddingExistingItemUpdatesQuantity();
		this.addNonExistingItem();
		this.addSecondNonExistingItem();
		this.addItemToCartViaRelationTable();
		this.addItemToCartViaRelationTableAndVerifyWithSelect();
		this.addAddingExistingItemUpdatesQuantityForItemAddedWithRelationTable();
	}

	public void addNewShoppingCartScenario() throws InvalidActionLanguageSyntaxException, NameNotFoundException {
		_scenarioNewCart.setName("Creating a shopping cart");
		this._domain.addScenario(_scenarioNewCart);
		
		this.addNewCartHasNoItemsScenario();
		this.addInitialiseCartGetsItemWithQuantity();
		this.addInitialiseCartWithEventParamsNotLiteralsGetsItemWithQuantity();
		this.addGeneratingSecondInitialiseCartDoesNotAffectCart();
	}
	
	public void addCancellingScenario() throws InvalidActionLanguageSyntaxException, NameNotFoundException {
		_scenarioCancellingOrder.setName("Cancelling an order");
		this._domain.addScenario(_scenarioCancellingOrder);
		
		this.addCanCancelCartAfterInitialItem();
	}

	private void addCanCancelCartAfterInitialItem() throws InvalidActionLanguageSyntaxException, NameNotFoundException {
		TestVector vector = new TestVector();
		vector.setDescription("Cancelling an order can happen even with only the initial items in the cart");
		
		_scenarioCancellingOrder.addVector(vector);
		
		TestVectorClassTable cartTable = new TestVectorClassTable(this._cart);
		TestVectorClassInstance newCart = cartTable.createInstance();
		newCart.setName("newCart");
		vector.addClassTable(cartTable);
		
		TestVectorClassTable itemTable = new TestVectorClassTable(this._item);
		itemTable.addAttribute(_item.getAttributeWithName("Name"));
		TestVectorClassInstance newItem = itemTable.createInstance();
		newItem.setInitialAttribute("Name", "CocoPops");
		vector.addClassTable(itemTable);
		
		String generateString = "";
		generateString += "GENERATE startCart(itemName=\"CocoPops\",quantity=2) TO newCart;\n";
		generateString += "GENERATE cancel() TO newCart;";
		vector.addInitialGenerateLanguage(generateString);
		
		vector.createInitialProcedureFromTables();
		
		String assertionProcString = "";
		assertionProcString += "SELECT ANY itemInCart RELATED BY newCart->R1;\n";
		assertionProcString += "IF NOT EMPTY itemInCart THEN\n";
		assertionProcString += "	FAIL \"newCart should have been empty, but it contained \" + itemInCart.Name;\n";
		assertionProcString += "END IF;\n";
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(vector);
		assertionProcedure.setProcedure(assertionProcString);		
	}
	
	private void addAddingExistingItemUpdatesQuantity() throws InvalidActionLanguageSyntaxException, NameNotFoundException {
		TestVector vector = new TestVector();
		vector.setDescription("Adding additional items to a cart that already contains the newly added item");
		
		_scenarioAddingSelection.addVector(vector);
		
		TestVectorClassTable cartTable = new TestVectorClassTable(this._cart);
		TestVectorClassInstance newCart = cartTable.createInstance();
		newCart.setName("newCart");
		vector.addClassTable(cartTable);
		
		TestVectorClassTable itemTable = new TestVectorClassTable(this._item);
		itemTable.addAttribute(_item.getAttributeWithName("Name"));
		TestVectorClassInstance newItem = itemTable.createInstance();
		newItem.setInitialAttribute("Name", "CocoPops");
		vector.addClassTable(itemTable);
		
		String generateString = "";
		generateString += "GENERATE startCart(itemName=\"CocoPops\",quantity=2) TO newCart;\n";
		generateString += "GENERATE addSelection(itemName=\"CocoPops\",quantity=4) TO newCart;\n";
		vector.addInitialGenerateLanguage(generateString);
		
		vector.createInitialProcedureFromTables();
		
		String assertionProcString = "";
		assertionProcString += "SELECT ANY itemInCart RELATED BY newCart->R1;\n";
		assertionProcString += "IF EMPTY itemInCart THEN\n";
		assertionProcString += "	FAIL \"newCart should have had CocoPops in cart!\";\n";
		assertionProcString += "END IF;\n";
		assertionProcString += "SELECT ONE itemSelection THAT RELATES newCart TO itemInCart ACROSS R1;\n";
		assertionProcString += "IF itemSelection.Quantity != 6 THEN\n";
		assertionProcString += "	FAIL \"newCart should have had quantity of 6 CocoPops!\";\n";
		assertionProcString += "END IF;\n";
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(vector);
		assertionProcedure.setProcedure(assertionProcString);		
	}
	
	
	private void addNonExistingItem() throws InvalidActionLanguageSyntaxException, NameNotFoundException {
		TestVector vector = new TestVector();
		vector.setDescription("Adding additional items to a cart that does not contain the item");
		
		_scenarioAddingSelection.addVector(vector);
		
		TestVectorClassTable cartTable = new TestVectorClassTable(this._cart);
		TestVectorClassInstance newCart = cartTable.createInstance();
		newCart.setName("newCart");
		vector.addClassTable(cartTable);
		
		TestVectorClassTable itemTable = new TestVectorClassTable(this._item);
		itemTable.addAttribute(_item.getAttributeWithName("Name"));
		TestVectorClassInstance cocoPops = itemTable.createInstance();
		cocoPops.setInitialAttribute("Name", "CocoPops");
		TestVectorClassInstance cornFlakes = itemTable.createInstance();
		cornFlakes.setInitialAttribute("Name", "CornFlakes");
		vector.addClassTable(itemTable);
		
		String generateString = "";
		generateString += "GENERATE startCart(itemName=\"CocoPops\",quantity=2) TO newCart;\n";
		generateString += "GENERATE addSelection(itemName=\"CornFlakes\",quantity=4) TO newCart;\n";
		vector.addInitialGenerateLanguage(generateString);
		
		vector.createInitialProcedureFromTables();
		
		String assertionProcString = "";
		assertionProcString += "SELECT ANY cocoPops RELATED BY newCart->R1 WHERE selected.Name == \"CocoPops\";\n";
		assertionProcString += "IF EMPTY cocoPops THEN\n";
		assertionProcString += "	FAIL \"newCart should have had CocoPops in cart!\";\n";
		assertionProcString += "END IF;\n";
		assertionProcString += "SELECT ONE cocoPopsSelection THAT RELATES newCart TO cocoPops ACROSS R1;\n";
		assertionProcString += "IF cocoPopsSelection.Quantity != 2 THEN\n";
		assertionProcString += "	FAIL \"newCart should have had quantity of 2 CocoPops!\";\n";
		assertionProcString += "END IF;\n";
		
		assertionProcString += "SELECT ANY cornFlakes RELATED BY newCart->R1 WHERE selected.Name == \"CornFlakes\";\n";
		assertionProcString += "IF EMPTY cornFlakes THEN\n";
		assertionProcString += "	FAIL \"newCart should have had CornFlakes in cart!\";\n";
		assertionProcString += "END IF;\n";
		assertionProcString += "SELECT ONE cornFlakesSelection THAT RELATES newCart TO cornFlakes ACROSS R1;\n";
		assertionProcString += "IF cornFlakesSelection.Quantity != 4 THEN\n";
		assertionProcString += "	FAIL \"newCart should have had quantity of 4 CornFlakes!, but it was \" + cornFlakesSelection.Quantity;\n";
		assertionProcString += "END IF;\n";
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(vector);
		assertionProcedure.setProcedure(assertionProcString);		
	}
	
	private void addSecondNonExistingItem() throws InvalidActionLanguageSyntaxException, NameNotFoundException {
		TestVector vector = new TestVector();
		vector.setDescription("Adding additional items to a cart that does not contain the item");
		
		_scenarioAddingSelection.addVector(vector);
		
		TestVectorClassTable cartTable = new TestVectorClassTable(this._cart);
		TestVectorClassInstance newCart = cartTable.createInstance();
		newCart.setName("newCart");
		vector.addClassTable(cartTable);
		
		TestVectorClassTable itemTable = new TestVectorClassTable(this._item);
		itemTable.addAttribute(_item.getAttributeWithName("Name"));
		TestVectorClassInstance cocoPops = itemTable.createInstance();
		cocoPops.setInitialAttribute("Name", "CocoPops");
		TestVectorClassInstance cornFlakes = itemTable.createInstance();
		cornFlakes.setInitialAttribute("Name", "CornFlakes");
		TestVectorClassInstance riceBubbles = itemTable.createInstance();
		riceBubbles.setInitialAttribute("Name", "RiceBubbles");
		vector.addClassTable(itemTable);
		
		String generateString = "";
		generateString += "GENERATE startCart(itemName=\"CocoPops\",quantity=2) TO newCart;\n";
		generateString += "GENERATE addSelection(itemName=\"CornFlakes\",quantity=4) TO newCart;\n";
		generateString += "GENERATE addSelection(itemName=\"RiceBubbles\",quantity=1) TO newCart;\n";
		vector.addInitialGenerateLanguage(generateString);
		
		vector.createInitialProcedureFromTables();
		
		String assertionProcString = "";
		assertionProcString += "SELECT ANY cocoPops RELATED BY newCart->R1 WHERE selected.Name == \"CocoPops\";\n";
		assertionProcString += "IF EMPTY cocoPops THEN\n";
		assertionProcString += "	FAIL \"newCart should have had CocoPops in cart!\";\n";
		assertionProcString += "END IF;\n";
		assertionProcString += "SELECT ONE cocoPopsSelection THAT RELATES newCart TO cocoPops ACROSS R1;\n";
		assertionProcString += "IF cocoPopsSelection.Quantity != 2 THEN\n";
		assertionProcString += "	FAIL \"newCart should have had quantity of 2 CocoPops!\";\n";
		assertionProcString += "END IF;\n";
		
		assertionProcString += "SELECT ANY cornFlakes RELATED BY newCart->R1 WHERE selected.Name == \"CornFlakes\";\n";
		assertionProcString += "IF EMPTY cornFlakes THEN\n";
		assertionProcString += "	FAIL \"newCart should have had CornFlakes in cart!\";\n";
		assertionProcString += "END IF;\n";
		assertionProcString += "SELECT ONE cornFlakesSelection THAT RELATES newCart TO cornFlakes ACROSS R1;\n";
		assertionProcString += "IF cornFlakesSelection.Quantity != 4 THEN\n";
		assertionProcString += "	FAIL \"newCart should have had quantity of 4 CornFlakes!, but it was \" + cornFlakesSelection.Quantity;\n";
		assertionProcString += "END IF;\n";
		
		assertionProcString += "SELECT ANY riceBubbles RELATED BY newCart->R1 WHERE selected.Name == \"RiceBubbles\";\n";
		assertionProcString += "IF EMPTY riceBubbles THEN\n";
		assertionProcString += "	FAIL \"newCart should have had RiceBubbles in cart!\";\n";
		assertionProcString += "END IF;\n";
		assertionProcString += "SELECT ONE riceBubblesSelection THAT RELATES newCart TO riceBubbles ACROSS R1;\n";
		assertionProcString += "IF riceBubblesSelection.Quantity != 1 THEN\n";
		assertionProcString += "	FAIL \"newCart should have had quantity of 1 riceBubbles!, but it was \" + riceBubblesSelection.Quantity;\n";
		assertionProcString += "END IF;\n";
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(vector);
		assertionProcedure.setProcedure(assertionProcString);		
	}
	
	private void addInitialiseCartGetsItemWithQuantity() throws InvalidActionLanguageSyntaxException, NameNotFoundException {
		TestVector vector = new TestVector();
		vector.setDescription("Initialising shopping cart gets items with quantity");
		
		_scenarioNewCart.addVector(vector);
		
		TestVectorClassTable cartTable = new TestVectorClassTable(this._cart);
		TestVectorClassInstance newCart = cartTable.createInstance();
		newCart.setName("newCart");
		vector.addClassTable(cartTable);
		
		TestVectorClassTable itemTable = new TestVectorClassTable(this._item);
		itemTable.addAttribute(_item.getAttributeWithName("Name"));
		TestVectorClassInstance newItem = itemTable.createInstance();
		newItem.setInitialAttribute("Name", "CocoPops");
		vector.addClassTable(itemTable);
		
		String generateString = "";
		generateString += "GENERATE startCart(itemName=\"CocoPops\",quantity=2) TO newCart;\n";
		vector.addInitialGenerateLanguage(generateString);
		
		vector.createInitialProcedureFromTables();
		
		String assertionProcString = "";
		assertionProcString += "SELECT ANY itemInCart RELATED BY newCart->R1;\n";
		assertionProcString += "IF EMPTY itemInCart THEN\n";
		assertionProcString += "	FAIL \"newCart should have had CocoPops in cart!\";\n";
		assertionProcString += "END IF;\n";
		assertionProcString += "SELECT ONE itemSelection THAT RELATES newCart TO itemInCart ACROSS R1;\n";
		assertionProcString += "IF itemSelection.Quantity != 2 THEN\n";
		assertionProcString += "	FAIL \"newCart should have had quantity of 2 CocoPops!\";\n";
		assertionProcString += "END IF;\n";
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(vector);
		assertionProcedure.setProcedure(assertionProcString);		
	}
	
	
	private void addInitialiseCartWithEventParamsNotLiteralsGetsItemWithQuantity() throws InvalidActionLanguageSyntaxException, NameNotFoundException {
		TestVector vector = new TestVector();
		vector.setDescription("Initialising shopping cart gets items with quantity using non-literals as event params");
		
		_scenarioNewCart.addVector(vector);
		
		TestVectorClassTable cartTable = new TestVectorClassTable(this._cart);
		TestVectorClassInstance newCart = cartTable.createInstance();
		newCart.setName("newCart");
		vector.addClassTable(cartTable);
		
		TestVectorClassTable itemTable = new TestVectorClassTable(this._item);
		itemTable.addAttribute(_item.getAttributeWithName("Name"));
		TestVectorClassInstance newItem = itemTable.createInstance();
		newItem.setName("newItem");
		newItem.setInitialAttribute("Name", "CocoPops");
		vector.addClassTable(itemTable);
		
		String generateString = "";
		generateString += "temp = 2;\n";
		generateString += "GENERATE startCart(itemName=newItem.Name,quantity=temp) TO newCart;\n";
		vector.addInitialGenerateLanguage(generateString);
		
		vector.createInitialProcedureFromTables();
		
		String assertionProcString = "";
		assertionProcString += "SELECT ANY itemInCart RELATED BY newCart->R1;\n";
		assertionProcString += "IF EMPTY itemInCart THEN\n";
		assertionProcString += "	FAIL \"newCart should have had CocoPops in cart!\";\n";
		assertionProcString += "END IF;\n";
		assertionProcString += "SELECT ONE itemSelection THAT RELATES newCart TO itemInCart ACROSS R1;\n";
		assertionProcString += "IF itemSelection.Quantity != 2 THEN\n";
		assertionProcString += "	FAIL \"newCart should have had quantity of 2 CocoPops but was \" + itemSelection.Quantity ;\n";
		assertionProcString += "END IF;\n";
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(vector);
		assertionProcedure.setProcedure(assertionProcString);		
	}
	
	
	
	private void addAddingExistingItemUpdatesQuantityForItemAddedWithRelationTable() throws InvalidActionLanguageSyntaxException, NameNotFoundException {
		TestVector vector = new TestVector();
		vector.setDescription("Initialising shopping cart with relation table gets items with quantity");
		
		_scenarioAddingSelection.addVector(vector);
		
		TestVectorClassTable cartTable = new TestVectorClassTable(this._cart);
		TestVectorClassInstance newCart = cartTable.createInstance();
		newCart.setName("newCart");
		newCart.setInitialState("Adding Selection To Order");
		vector.addClassTable(cartTable);
		
		TestVectorClassTable itemTable = new TestVectorClassTable(this._item);
		itemTable.addAttribute(_item.getAttributeWithName("Name"));
		TestVectorClassInstance newItem = itemTable.createInstance();
		newItem.setName("newItem");
		newItem.setInitialAttribute("Name", "CocoPops");
		vector.addClassTable(itemTable);
		
		TestVectorClassTable itemSelectionTable = new TestVectorClassTable(this._itemSelection);
		itemSelectionTable.addAttribute(_itemSelection.getAttributeWithName("Quantity"));
		TestVectorClassInstance newItemSelection = itemSelectionTable.createInstance();
		newItemSelection.setName("newItemSelection");
		newItemSelection.setInitialAttribute("Quantity", "2");
		vector.addClassTable(itemSelectionTable);
		
		TestVectorRelationTable relationTable = new TestVectorRelationTable(this._r1);
		TestVectorRelationInstance r1_1 = relationTable.createInstance();
		r1_1.setEndA("newCart");
		r1_1.setEndB("newItem");
		r1_1.setAssociation("newItemSelection");
		vector.addRelationTable(relationTable);
		
		String generateString = "";
		generateString += "GENERATE addSelection(itemName=\"CocoPops\",quantity=4) TO newCart;\n";
		vector.addInitialGenerateLanguage(generateString);
		
		vector.createInitialProcedureFromTables();
		
		String assertionProcString = "";
		assertionProcString += "IF newItemSelection.Quantity != 6 THEN\n";
		assertionProcString += "	FAIL \"newCart should have had quantity of 6 CocoPops but was \" + newItemSelection.Quantity;\n";
		assertionProcString += "END IF;\n";
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(vector);
		assertionProcedure.setProcedure(assertionProcString);		
	}
	
	private void addItemToCartViaRelationTable() throws InvalidActionLanguageSyntaxException, NameNotFoundException {
		TestVector vector = new TestVector();
		vector.setDescription("Initialising shopping cart with relation table gets items with quantity");
		
		_scenarioNewCart.addVector(vector);
		
		TestVectorClassTable cartTable = new TestVectorClassTable(this._cart);
		TestVectorClassInstance newCart = cartTable.createInstance();
		newCart.setName("newCart");
		vector.addClassTable(cartTable);
		
		TestVectorClassTable itemTable = new TestVectorClassTable(this._item);
		itemTable.addAttribute(_item.getAttributeWithName("Name"));
		TestVectorClassInstance newItem = itemTable.createInstance();
		newItem.setName("newItem");
		newItem.setInitialAttribute("Name", "CocoPops");
		vector.addClassTable(itemTable);
		
		TestVectorClassTable itemSelectionTable = new TestVectorClassTable(this._itemSelection);
		itemSelectionTable.addAttribute(_itemSelection.getAttributeWithName("Quantity"));
		TestVectorClassInstance newItemSelection = itemSelectionTable.createInstance();
		newItemSelection.setName("newItemSelection");
		newItemSelection.setInitialAttribute("Quantity", "2");
		vector.addClassTable(itemSelectionTable);
		
		TestVectorRelationTable relationTable = new TestVectorRelationTable(this._r1);
		TestVectorRelationInstance r1_1 = relationTable.createInstance();
		r1_1.setEndA("newCart");
		r1_1.setEndB("newItem");
		r1_1.setAssociation("newItemSelection");
		vector.addRelationTable(relationTable);
		
		vector.createInitialProcedureFromTables();
		
		String assertionProcString = "";
		assertionProcString += "IF newItemSelection.Quantity != 2 THEN\n";
		assertionProcString += "	FAIL \"newCart should have had quantity of 2 CocoPops but was \" + newItemSelection.Quantity;\n";
		assertionProcString += "END IF;\n";
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(vector);
		assertionProcedure.setProcedure(assertionProcString);		
	}
	
	private void addItemToCartViaRelationTableAndVerifyWithSelect() throws InvalidActionLanguageSyntaxException, NameNotFoundException {
		TestVector vector = new TestVector();
		vector.setDescription("Initialising shopping cart with relation table gets items with quantity and verified over select");
		
		_scenarioNewCart.addVector(vector);
		
		TestVectorClassTable cartTable = new TestVectorClassTable(this._cart);
		TestVectorClassInstance newCart = cartTable.createInstance();
		newCart.setName("newCart");
		vector.addClassTable(cartTable);
		
		TestVectorClassTable itemTable = new TestVectorClassTable(this._item);
		itemTable.addAttribute(_item.getAttributeWithName("Name"));
		TestVectorClassInstance newItem = itemTable.createInstance();
		newItem.setName("newItem");
		newItem.setInitialAttribute("Name", "CocoPops");
		vector.addClassTable(itemTable);
		
		TestVectorClassTable itemSelectionTable = new TestVectorClassTable(this._itemSelection);
		itemSelectionTable.addAttribute(_itemSelection.getAttributeWithName("Quantity"));
		TestVectorClassInstance newItemSelection = itemSelectionTable.createInstance();
		newItemSelection.setName("newItemSelection");
		newItemSelection.setInitialAttribute("Quantity", "2");
		vector.addClassTable(itemSelectionTable);
		
		TestVectorRelationTable relationTable = new TestVectorRelationTable(this._r1);
		TestVectorRelationInstance r1_1 = relationTable.createInstance();
		r1_1.setEndA("newCart");
		r1_1.setEndB("newItem");
		r1_1.setAssociation("newItemSelection");
		vector.addRelationTable(relationTable);
		
		vector.createInitialProcedureFromTables();
		
		String assertionProcString = "";
		assertionProcString += "SELECT ANY itemInCart RELATED BY newCart->R1;\n";
		assertionProcString += "IF EMPTY itemInCart THEN\n";
		assertionProcString += "	FAIL \"newCart should have had CocoPops in cart!\";\n";
		assertionProcString += "END IF;\n";
		assertionProcString += "SELECT ONE itemSelection THAT RELATES newCart TO itemInCart ACROSS R1;\n";
		assertionProcString += "IF itemSelection.Quantity != 2 THEN\n";
		assertionProcString += "	FAIL \"newCart should have had quantity of 2 CocoPops but was \" + itemSelection.Quantity;\n";
		assertionProcString += "END IF;\n";
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(vector);
		assertionProcedure.setProcedure(assertionProcString);		
	}
	
	private void addGeneratingSecondInitialiseCartDoesNotAffectCart() throws InvalidActionLanguageSyntaxException, NameNotFoundException {
		TestVector vector = new TestVector();
		vector.setDescription("Initialised shopping cart can update quantity when item is re-added");
		
		_scenarioNewCart.addVector(vector);
		
		TestVectorClassTable cartTable = new TestVectorClassTable(this._cart);
		TestVectorClassInstance newCart = cartTable.createInstance();
		newCart.setName("newCart");
		vector.addClassTable(cartTable);
		
		TestVectorClassTable itemTable = new TestVectorClassTable(this._item);
		itemTable.addAttribute(_item.getAttributeWithName("Name"));
		TestVectorClassInstance newItem = itemTable.createInstance();
		newItem.setInitialAttribute("Name", "CocoPops");
		vector.addClassTable(itemTable);
		
		String generateString = "";
		generateString += "GENERATE startCart(itemName=\"CocoPops\",quantity=2) TO newCart;\n";
		generateString += "GENERATE startCart(itemName=\"CocoPops\",quantity=4) TO newCart;\n";
		vector.addInitialGenerateLanguage(generateString);
		
		vector.createInitialProcedureFromTables();
		
		String assertionProcString = "";
		assertionProcString += "SELECT ANY itemInCart RELATED BY newCart->R1;\n";
		assertionProcString += "IF EMPTY itemInCart THEN\n";
		assertionProcString += "	FAIL \"newCart should have had CocoPops in cart!\";\n";
		assertionProcString += "END IF;\n";
		assertionProcString += "SELECT ONE itemSelection THAT RELATES newCart TO itemInCart ACROSS R1;\n";
		assertionProcString += "IF itemSelection.Quantity != 2 THEN\n";
		assertionProcString += "	FAIL \"newCart should have had quantity of 2 CocoPops!\";\n";
		assertionProcString += "END IF;\n";
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(vector);
		assertionProcedure.setProcedure(assertionProcString);		
	}

	private void addNewCartHasNoItemsScenario() throws InvalidActionLanguageSyntaxException {
		TestVector vector = new TestVector();
		vector.setDescription("New shopping carts should have no items");
		
		_scenarioNewCart.addVector(vector);
		
		TestVectorClassTable table = new TestVectorClassTable(this._cart);
		TestVectorClassInstance newCart = table.createInstance();
		newCart.setName("newCart");
		vector.addClassTable(table);
		
		vector.createInitialProcedureFromTables();
		String assertionProcString = "";
		assertionProcString += "SELECT MANY itemsInCart RELATED BY newCart->R1;\n";
		assertionProcString += "IF NOT EMPTY itemsInCart THEN\n";
		assertionProcString += "	FAIL \"newCart had items in the cart when it should have had none!\";\n";
		assertionProcString += "END IF;\n";
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(vector);
		assertionProcedure.setProcedure(assertionProcString);
	}

	public void test_shopping_cart_domain() throws CannotSimulateDomainThatIsInvalidException
	{
		TestHarness harness = new TestHarness(this._domain);
		harness.execute();
		SimulatedTestHelper.printHarnessResults(harness);
		Assert.assertEquals(true, harness.allAssertionsPassed());
	}
}
