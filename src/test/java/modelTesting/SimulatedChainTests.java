package test.java.modelTesting;

import javax.naming.NameAlreadyBoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventInstance;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
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
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import test.java.tests.TestHarness;

public class SimulatedChainTests extends TestCase {
	private EntityDomain _domain;
	private EntityClass _head;
	private EntityClass _link;
	private EntityAttribute _order;
	private EntityRelation _r1;
	private EntityRelation _r2;
	private TestScenario _addingFirstLinkToHead;
	private EntityClass _body;
	private TestScenario _addingBodyToBody;
	private TestScenario _insertingLinkBetween;

	public SimulatedChainTests(String name) throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException {
		super(name);
		this.setupDomain();
		this.setupScenarios();
	}

	private void setupDomain() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException {
		this._domain = new EntityDomain("ChainDomain");
		
		this._link = new EntityClass("Link");
		this._order = new EntityAttribute("Order", IntegerEntityDatatype.getInstance());
		this._link.addAttribute(this._order);
		
		this._head = new EntityClass("Head");
		this._head.addSuperClass(this._link);
		this._body = new EntityClass("Body");
		this._body.addSuperClass(this._link);
		
		this._domain.addClass(this._link);
		this._domain.addClass(this._head);
		this._domain.addClass(this._body);
		
		this._r1 = new EntityRelation("R1");
		this._r1.setEndA(this._head, CardinalityType.ZERO_TO_ONE, "Is head of");
		this._r1.setEndB(this._body, CardinalityType.ZERO_TO_ONE, "Is lead by");
		
		this._r2 = new EntityRelation("R2");
		this._r2.setEndA(this._body, CardinalityType.ZERO_TO_ONE, "Leads");
		this._r2.setEndB(this._body, CardinalityType.ZERO_TO_ONE, "Follows");
		
		this.createHeadStateMachine();
	}
	
	@SuppressWarnings("unused")
	private void createHeadStateMachine() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException {
		EntityState initial = new EntityState("Initial");		
		this._head.addState(initial);
		
		this._head.setInitial(initial);
		this._body.setInitial(initial);
		
		EntityState hasFirstLink = addAddFirstLinkState(initial);
		EntityState addNextLink = addNextLinkState(initial);
		EntityState insertBeforeState = addInsertBeforeState(addNextLink);
		addPushDownState(initial, insertBeforeState);
	}

	private EntityState addPushDownState(EntityState fromState, EntityState addNextLink) throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException {
		EntityState toState = new EntityState("PushDownOrderState");
		this._head.addState(toState);
		
		EntityProcedure procedure = new EntityProcedure(toState);
		String proc = "";
		proc += "RECLASSIFY TO Body;\n";
		proc += "self.Order = self.Order + 1;\n";
		proc += "SELECT ANY nextBody RELATED BY self->R2.\"Leads\";\n";
		proc += "IF NOT EMPTY nextBody THEN\n";
		proc += "	GENERATE pushDownOrder() TO nextBody;\n";
		proc += "END IF;\n";
			
		procedure.setProcedure(proc);
		
		
		EntityEventSpecification spec = new EntityEventSpecification(this._link, "pushDownOrder");
		this._link.addEventSpecification(spec);

		EntityEventInstance addNewLinkInitialHasFirstLink = new EntityEventInstance(spec, fromState, toState);
		this._link.addEventInstance(spec, addNewLinkInitialHasFirstLink);
		
		EntityEventInstance otherInstance = new EntityEventInstance(spec, addNextLink, toState);
		this._link.addEventInstance(spec, otherInstance);
		
		return toState;
	}
	
	private EntityState addInsertBeforeState(EntityState addNextLinkState) throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException {
		EntityState insertBeforeState = new EntityState("InsertingBeforeState");
		this._head.addState(insertBeforeState);
		
		EntityProcedure procedure = new EntityProcedure(insertBeforeState);
		String proc = "";
		proc += "RECLASSIFY TO Body;\n";
		proc += "SELECT ANY nextBodyInsertBefore RELATED BY self->R2.\"Leads\";\n";
		proc += "IF NOT EMPTY nextBodyInsertBefore THEN\n";
		proc += "	UNRELATE self FROM nextBodyInsertBefore ACROSS R2.\"Leads\";\n";
		proc += "	GENERATE pushDownOrder() TO nextBodyInsertBefore;\n";
		proc += "END IF;\n";
		proc += "SELECT ANY previousBody RELATED BY self->R2.\"Follows\";\n";
		proc += "UNRELATE previousBody FROM self ACROSS R2.\"Leads\";\n";
		proc += "CREATE body FROM Body;\n";
		proc += "body.Order = self.Order;\n";
		proc += "RELATE self TO body ACROSS R2.\"Follows\";\n";
		proc += "RELATE previousBody TO body ACROSS R2.\"Leads\";\n";
		proc += "GENERATE pushDownOrder() TO self;\n";
		
		procedure.setProcedure(proc);
		
		
		EntityEventSpecification spec = new EntityEventSpecification(this._link, "insertLinkBefore");
		this._link.addEventSpecification(spec);
		EntityEventInstance addNewLinkInitialHasFirstLink = new EntityEventInstance(spec, addNextLinkState, insertBeforeState);
		this._link.addEventInstance(spec, addNewLinkInitialHasFirstLink);
		return insertBeforeState;
	}
	
	private EntityState addNextLinkState(EntityState initial) throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException {
		EntityState addNextLinkState = new EntityState("AddingNextLink");
		this._head.addState(addNextLinkState);
		
		EntityProcedure procedure = new EntityProcedure(addNextLinkState);
		String proc = "";
		proc += "RECLASSIFY TO Body;\n";
		proc += "CREATE body FROM Body;\n";
		proc += "body.Order = self.Order + 1;\n";
		proc += "RELATE self TO body ACROSS R2.\"Leads\";\n";
			
		procedure.setProcedure(proc);
		
		
		EntityEventSpecification spec = new EntityEventSpecification(this._link, "addNextLink");
		this._link.addEventSpecification(spec);
		EntityEventInstance addNewLinkInitialHasFirstLink = new EntityEventInstance(spec, initial, addNextLinkState);
		this._link.addEventInstance(spec, addNewLinkInitialHasFirstLink);
		return addNextLinkState;
	}
	
	private EntityState addAddFirstLinkState(EntityState initial) throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException {
		EntityState hasFirstLink = new EntityState("HasFirstLink");
		this._head.addState(hasFirstLink);
		
		EntityProcedure procedure = new EntityProcedure(hasFirstLink);
		String proc = "";
		proc += "RECLASSIFY TO Head;\n";
		proc += "CREATE body FROM Body;\n";
		proc += "body.Order = 1;\n";
		proc += "RELATE body TO self ACROSS R1;\n";
			
		procedure.setProcedure(proc);
		
		
		EntityEventSpecification addNewLink = new EntityEventSpecification(this._link, "addNewLink");
		this._link.addEventSpecification(addNewLink);
		EntityEventInstance addNewLinkInitialHasFirstLink = new EntityEventInstance(addNewLink, initial, hasFirstLink);
		this._link.addEventInstance(addNewLink, addNewLinkInitialHasFirstLink);
		return hasFirstLink;
	}

	private void setupScenarios() throws InvalidActionLanguageSyntaxException
	{
		this.setupAddingFirstLinkToHead();
		this.setupAddingLinkToLink();
		this.setupInsertingBeforeLink();
	}
	
	private void setupInsertingBeforeLink() throws InvalidActionLanguageSyntaxException
	{
		this._insertingLinkBetween = new TestScenario();
		this._insertingLinkBetween.setName("When inserting a link between links");
		this._domain.addScenario(this._insertingLinkBetween);
		
		this.pushedDownBodyIncreasesOrder();
		this.canInsertBeforeLink();
	}
	
	private void pushedDownBodyIncreasesOrder() throws InvalidActionLanguageSyntaxException {
		TestVector nextExists = new TestVector();
		nextExists.setDescription("The body that was pushed down should increase order by 1");
		this._insertingLinkBetween.addVector(nextExists);
		
		TestVectorClassTable bodyTable = new TestVectorClassTable(this._body);
		bodyTable.addAttribute(this._order);
		nextExists.addClassTable(bodyTable);
		TestVectorClassInstance order1 = bodyTable.createInstance();
		order1.setName("Body1");
		order1.setInitialAttribute("Order", "1");
		
		String initialGenerateProc = "";
		initialGenerateProc += "GENERATE pushDownOrder() TO Body1;\n";
		nextExists.addInitialGenerateLanguage(initialGenerateProc);
		nextExists.createInitialProcedureFromTables();
		
		AssertionTestVectorProcedure assertionProc = new AssertionTestVectorProcedure();
		assertionProc.setVector(nextExists);
		
		String assertionString = "";
		assertionString += "IF Body1.Order != 2 THEN\n";
		assertionString += "	FAIL \"Body1 should have had an order of 2, but was : \" + Body1.Order;\n" ;
		assertionString += "END IF;\n";
		
		assertionProc.setProcedure(assertionString);
	}
	
	private void canInsertBeforeLink() throws InvalidActionLanguageSyntaxException {
		TestVector nextExists = new TestVector();
		nextExists.setDescription("The body that was inserted before should have an increased order");
		this._insertingLinkBetween.addVector(nextExists);
		
		TestVectorClassTable bodyTable = new TestVectorClassTable(this._body);
		bodyTable.addAttribute(this._order);
		nextExists.addClassTable(bodyTable);
		TestVectorClassInstance order1 = bodyTable.createInstance();
		order1.setName("Body1");
		order1.setInitialAttribute("Order", "1");
		
		
		TestVectorClassInstance order2 = bodyTable.createInstance();
		order2.setName("Body2");
		order2.setInitialAttribute("Order", "2");
		order2.setInitialState("AddingNextLink");
		
		TestVectorRelationTable r2 = new TestVectorRelationTable(_r2);
		nextExists.addRelationTable(r2);
		TestVectorRelationInstance b1b2 = r2.createInstance();
		b1b2.setEndA("Body1");
		b1b2.setEndB("Body2");
		
		
		String initialGenerateProc = "";
		initialGenerateProc += "GENERATE insertLinkBefore() TO Body2;\n";
		nextExists.addInitialGenerateLanguage(initialGenerateProc);
		nextExists.createInitialProcedureFromTables();
		
		AssertionTestVectorProcedure assertionProc = new AssertionTestVectorProcedure();
		assertionProc.setVector(nextExists);
		
		String assertionString = "";
		assertionString += "IF Body1.Order != 1 THEN\n";
		assertionString += "	FAIL \"Body1 should have had an order of 1, but was : \" + Body1.Order;\n" ;
		assertionString += "END IF;\n";
		assertionString += "IF Body2.Order != 3 THEN\n";
		assertionString += "	FAIL \"Body2 should have had an order of 3, but was : \" + Body2.Order;\n" ;
		assertionString += "END IF;\n";
		assertionString += "SELECT ANY nextBody RELATED BY Body1->R2.\"Leads\";\n";
		assertionString += "IF nextBody.Order != 2 THEN\n";
		assertionString += "	FAIL \"The added after Body should have had an order of 2, but was : \" + nextBody.Order;\n";
		assertionString += "END IF;\n";
		assertionString += "SELECT ANY beforeBody RELATED BY Body2->R2.\"Follows\";\n";
		assertionString += "IF beforeBody.Order != 2 THEN\n";
		assertionString += "	FAIL \"The added before Body should have had an order of 2, but was : \" + beforeBody.Order;\n";
		assertionString += "END IF;\n";
		
		assertionProc.setProcedure(assertionString);
	}

	private void setupAddingLinkToLink() throws InvalidActionLanguageSyntaxException {
		this._addingBodyToBody = new TestScenario();
		this._addingBodyToBody.setName("When adding a link to a link");
		this._domain.addScenario(this._addingBodyToBody);
		
		this.nextBodyExists();
		this.nextBodyExistsAndHasOrderOf2();
		this.nextBodyExistsAndHasOrderOfPreviousBodyPlus1();
	}

	private void setupAddingFirstLinkToHead() throws InvalidActionLanguageSyntaxException {
		this._addingFirstLinkToHead = new TestScenario();
		this._addingFirstLinkToHead.setName("When adding the first link to the Head");
		this._domain.addScenario(this._addingFirstLinkToHead);
		
		this.firstLinkActuallyExists();
		this.firstLinkGetsOrderOfOne();		
	}
	
	private void nextBodyExistsAndHasOrderOf2() throws InvalidActionLanguageSyntaxException {
		TestVector nextExists = new TestVector();
		nextExists.setDescription("The next body should exist and should have an order of 2");
		this._addingBodyToBody.addVector(nextExists);
		
		TestVectorClassTable bodyTable = new TestVectorClassTable(this._body);
		bodyTable.addAttribute(this._order);
		nextExists.addClassTable(bodyTable);
		TestVectorClassInstance testInstance = bodyTable.createInstance();
		testInstance.setInitialAttribute("Order", "1");
		
		String initialGenerateProc = "";
		initialGenerateProc += "GENERATE addNextLink() TO Body_001;\n";
		nextExists.addInitialGenerateLanguage(initialGenerateProc);
		nextExists.createInitialProcedureFromTables();
		
		AssertionTestVectorProcedure assertionProc = new AssertionTestVectorProcedure();
		assertionProc.setVector(nextExists);
		
		String assertionString = "";
		assertionString += "SELECT ANY nextBody RELATED BY Body_001->R2.\"Leads\";\n";
		assertionString += "IF nextBody.Order != 2 THEN\n";
		assertionString += "	FAIL \"The added Body should have had an order of 2, but was : \" + nextBody.Order;\n";
		assertionString += "END IF;\n";
		
		assertionProc.setProcedure(assertionString);
	}
	
	private void nextBodyExistsAndHasOrderOfPreviousBodyPlus1() throws InvalidActionLanguageSyntaxException {
		TestVector nextExists = new TestVector();
		nextExists.setDescription("The next body should exist and should have an order of 4");
		this._addingBodyToBody.addVector(nextExists);
		
		TestVectorClassTable bodyTable = new TestVectorClassTable(this._body);
		bodyTable.addAttribute(this._order);
		nextExists.addClassTable(bodyTable);
		TestVectorClassInstance testInstance = bodyTable.createInstance();
		testInstance.setInitialAttribute("Order", "3");
		
		String initialGenerateProc = "";
		initialGenerateProc += "GENERATE addNextLink() TO Body_001;\n";
		nextExists.addInitialGenerateLanguage(initialGenerateProc);
		nextExists.createInitialProcedureFromTables();
		
		AssertionTestVectorProcedure assertionProc = new AssertionTestVectorProcedure();
		assertionProc.setVector(nextExists);
		
		String assertionString = "";
		assertionString += "SELECT ANY nextBody RELATED BY Body_001->R2.\"Leads\";\n";
		assertionString += "IF nextBody.Order != Body_001.Order + 1 THEN\n";
		assertionString += "	FAIL \"The added Body should have had an order of \" + (Body_001.Order + 1) + \", but was : \" + nextBody.Order;\n";
		assertionString += "END IF;\n";
		
		assertionProc.setProcedure(assertionString);
	}
	
	private void nextBodyExists() throws InvalidActionLanguageSyntaxException {
		TestVector nextExists = new TestVector();
		nextExists.setDescription("The next body should exist");
		this._addingBodyToBody.addVector(nextExists);
		
		TestVectorClassTable bodyTable = new TestVectorClassTable(this._body);
		nextExists.addClassTable(bodyTable);
		bodyTable.createInstance();
		
		String initialGenerateProc = "";
		initialGenerateProc += "GENERATE addNextLink() TO Body_001;\n";
		nextExists.addInitialGenerateLanguage(initialGenerateProc);
		nextExists.createInitialProcedureFromTables();
		
		AssertionTestVectorProcedure assertionProc = new AssertionTestVectorProcedure();
		assertionProc.setVector(nextExists);
		
		String assertionString = "";
		assertionString += "SELECT ANY nextBody RELATED BY Body_001->R2.\"Leads\";\n";
		assertionString += "IF EMPTY nextBody THEN\n";
		assertionString += "	FAIL \"The added Body did not exist\";\n";
		assertionString += "END IF;\n";
		
		assertionProc.setProcedure(assertionString);
	}
	
	private void firstLinkActuallyExists() throws InvalidActionLanguageSyntaxException {
		TestVector firstLinkActuallyExists = new TestVector();
		firstLinkActuallyExists.setDescription("The first link should exist");
		this._addingFirstLinkToHead.addVector(firstLinkActuallyExists);
		
		TestVectorClassTable headTable = new TestVectorClassTable(this._head);
		firstLinkActuallyExists.addClassTable(headTable);
		headTable.createInstance();
		
		String initialGenerateProc = "";
		initialGenerateProc += "GENERATE addNewLink() TO Head_001;\n";
		firstLinkActuallyExists.addInitialGenerateLanguage(initialGenerateProc);
		firstLinkActuallyExists.createInitialProcedureFromTables();
		
		AssertionTestVectorProcedure assertionProc = new AssertionTestVectorProcedure();
		assertionProc.setVector(firstLinkActuallyExists);
		
		String assertionString = "";
		assertionString += "SELECT ANY firstLink RELATED BY Head_001->R1;\n";
		assertionString += "IF EMPTY firstLink THEN\n";
		assertionString += "	FAIL \"The first added link did not exist\";\n";
		assertionString += "END IF;\n";
		
		assertionProc.setProcedure(assertionString);
	}
	
	private void firstLinkGetsOrderOfOne() throws InvalidActionLanguageSyntaxException {
		TestVector firstLinkGetOrderOfOne = new TestVector();
		firstLinkGetOrderOfOne.setDescription("The first link should get an order of 1");
		this._addingFirstLinkToHead.addVector(firstLinkGetOrderOfOne);
		
		TestVectorClassTable headTable = new TestVectorClassTable(this._head);
		firstLinkGetOrderOfOne.addClassTable(headTable);
		headTable.createInstance();
		
		String initialGenerateProc = "";
		initialGenerateProc += "GENERATE addNewLink() TO Head_001;\n";
		firstLinkGetOrderOfOne.addInitialGenerateLanguage(initialGenerateProc);
		firstLinkGetOrderOfOne.createInitialProcedureFromTables();
		
		AssertionTestVectorProcedure assertionProc = new AssertionTestVectorProcedure();
		assertionProc.setVector(firstLinkGetOrderOfOne);
		
		String assertionString = "";
		assertionString += "SELECT ANY firstLink RELATED BY Head_001->R1;\n";
		assertionString += "IF firstLink.Order != 1 THEN\n";
		assertionString += "	FAIL \"The first added link should have an order of 1, but it was : \" + firstLink.Order;\n";
		assertionString += "END IF;\n";
		
		assertionProc.setProcedure(assertionString);
	}

	public void test_run_chain_harness() throws CannotSimulateDomainThatIsInvalidException
	{
		TestHarness harness = new TestHarness(this._domain);
		harness.execute();

		SimulatedTestHelper.printHarnessResults(harness);
		Assert.assertEquals(true, harness.allAssertionsPassed());
		Assert.assertEquals(false, harness.wereExceptionsRaised());
	}

}
