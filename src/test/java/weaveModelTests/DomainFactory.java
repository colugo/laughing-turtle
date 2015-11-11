package test.java.weaveModelTests;

import javax.naming.NameAlreadyBoundException;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventInstance;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.scenario.AssertionTestVectorProcedure;
import main.java.avii.scenario.TestScenario;
import main.java.avii.scenario.TestVector;
import main.java.avii.scenario.TestVectorClassInstance;
import main.java.avii.scenario.TestVectorClassTable;
import main.java.avii.scenario.TestVectorRelationInstance;
import main.java.avii.scenario.TestVectorRelationTable;

public class DomainFactory {
	public EntityDomain domain;
	public EntityClass factory;
	public EntityClass item;
	public EntityAttribute itemQuanitity;
	public EntityEventSpecification produceItemSpec;
	public EntityEventSpecification finishedProductionItemSpec;
	public EntityRelation r1;
	
	public DomainFactory() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException {
		this.domain = new EntityDomain("Factory Domain");
		this.createClasses();
		this.createRelations();
		this.createListStateMachine();
		this.createScenarios();
	}

	private void createClasses() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException {
		this.factory = new EntityClass("Store");
		this.domain.addClass(this.factory);
		
		this.item = new EntityClass("Item");
		this.domain.addClass(this.item);
		this.itemQuanitity = new EntityAttribute("Quantity", IntegerEntityDatatype.getInstance());
		this.item.addAttribute(this.itemQuanitity);
	}

	private void createListStateMachine() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException {
		EntityState initial = new EntityState("Initial");
		EntityState production = new EntityState("Producing item");
		EntityState finishedProduction = new EntityState("Finished production");
		
		this.item.addState(initial);
		this.item.addState(production);
		this.item.addState(finishedProduction);
		
		String producingItemProcedureText = "self.Quantity = self.Quantity + 50;\n" +
				"GENERATE finishedProduction() TO self;\n";
		production.setProcedureText(producingItemProcedureText);
		
		produceItemSpec = new EntityEventSpecification(this.item, "produceItem");
		finishedProductionItemSpec = new EntityEventSpecification(this.item, "finishedProduction");
		
		new EntityEventInstance(produceItemSpec, initial, production);
		new EntityEventInstance(produceItemSpec, production, production);
		new EntityEventInstance(finishedProductionItemSpec, production, finishedProduction);
		new EntityEventInstance(produceItemSpec, finishedProduction, production);
		
	}

	private void createRelations() {
		this.r1 = new EntityRelation("R1");
		this.r1.setEndA(this.factory, CardinalityType.ZERO_TO_MANY);
		this.r1.setEndB(this.item, CardinalityType.ONE_TO_ONE);
	}
	
	private void createScenarios() throws InvalidActionLanguageSyntaxException
	{
		this.createStaticRepresentationScenario();
		this.createProduceItemScenario();
	}

	private void createProduceItemScenario() throws InvalidActionLanguageSyntaxException {
		TestScenario scenario = new TestScenario();
		scenario.setName("Produce item");
		scenario.setDescription("can produce items");
		this.domain.addScenario(scenario);
		
		this.addCanProduceItemVector(scenario);
		this.addAfterProductionInStateOfProduced(scenario);
		this.addCanProduceManyItemVector(scenario);
	}

	private void addAfterProductionInStateOfProduced(TestScenario scenario) throws InvalidActionLanguageSyntaxException {
		TestVector testVector = new TestVector();
		
		testVector.setDescription("after production, item is in produced state");
		scenario.addVector(testVector);
		
		addBreadsToBaker(testVector,100,50,"GENERATE produceItem() TO loaf;\n");
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(testVector);
		String assertionProcedureText = "" +
				"IF loaf.Quantity != 150 THEN\n" +
				"	FAIL \"loaf should have had 150, but has \" + loaf.Quantity;\n" +
				"END IF;\n" +
				"IF loaf.state != \"Finished production\" THEN\n" +
				"	FAIL \"loaf should be in 'Finished production' state, but is in \" + loaf.state;\n"  +
				"END IF;\n";
		assertionProcedure.setProcedure(assertionProcedureText);
	}
	
	private void addCanProduceManyItemVector(TestScenario scenario) throws InvalidActionLanguageSyntaxException {
		TestVector testVector = new TestVector();
		
		testVector.setDescription("can produce bread");
		scenario.addVector(testVector);
		
		addBreadsToBaker(testVector,100,50,"GENERATE produceItem() TO loaf;\nGENERATE produceItem() TO loaf;\n");
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(testVector);
		String assertionProcedureText = "" +
				"IF loaf.Quantity != 200 THEN\n" +
				"	FAIL \"loaf should have had 200, but has \" + loaf.Quantity;\n" +
				"END IF;\n";
		assertionProcedure.setProcedure(assertionProcedureText);
	}
	
	private void addCanProduceItemVector(TestScenario scenario) throws InvalidActionLanguageSyntaxException {
		TestVector testVector = new TestVector();
		
		testVector.setDescription("can produce bread");
		scenario.addVector(testVector);
		
		addBreadsToBaker(testVector,100,50,"GENERATE produceItem() TO loaf;\n");
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(testVector);
		String assertionProcedureText = "" +
				"IF loaf.Quantity != 150 THEN\n" +
				"	FAIL \"loaf should have had 150, but has \" + loaf.Quantity;\n" +
				"END IF;\n";
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
		
		testVector.setDescription("Factory can have multiple types of Item");
		scenario.addVector(testVector);
		
		addBreadsToBaker(testVector,100,50,"");
		
		AssertionTestVectorProcedure assertionProcedure = new AssertionTestVectorProcedure();
		assertionProcedure.setVector(testVector);
		String assertionProcedureText = "";
		assertionProcedure.setProcedure(assertionProcedureText);
	}

	private void addBreadsToBaker(TestVector testVector, int breadQuantity, int milkQuantity, String generateText) throws InvalidActionLanguageSyntaxException {
	
		TestVectorClassTable storeTable = new TestVectorClassTable(this.factory);
		testVector.addClassTable(storeTable);
		TestVectorClassInstance iga = storeTable.createInstance();
		iga.setName("baker");
		
		TestVectorClassTable itemTable = new TestVectorClassTable(this.item);
		testVector.addClassTable(itemTable);
		itemTable.addAttribute(itemQuanitity);
		
		TestVectorClassInstance loaf = itemTable.createInstance();
		loaf.setInitialAttribute("Quantity", Integer.toString(breadQuantity));
		loaf.setName("loaf");
		
		TestVectorClassInstance roll = itemTable.createInstance();
		roll.setInitialAttribute("Quantity", Integer.toString(milkQuantity));
		roll.setName("hotdog");

		TestVectorRelationTable listListOrderTable = new TestVectorRelationTable(r1);
		testVector.addRelationTable(listListOrderTable);
		TestVectorRelationInstance igaBread = listListOrderTable.createInstance();
		igaBread.setEndA("baker");
		igaBread.setEndB("loaf");
		TestVectorRelationInstance igaMilk = listListOrderTable.createInstance();
		igaMilk.setEndA("baker");
		igaMilk.setEndB("hotdog");
		
		testVector.addInitialGenerateLanguage(generateText);
		
		testVector.createInitialProcedureFromTables();
	}
	
}
