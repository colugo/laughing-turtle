package test.java.modelTesting.weaveModelTesting;

import test.java.helper.TestHelper;

import java.util.Collection;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import test.java.modelTesting.SimulatedTestHelper;
import test.java.weaveModelTests.DomainFactory;
import test.java.weaveModelTests.DomainStore;
import main.java.avii.diagnostics.Diagnostics;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.editor.metamodel.weaving.AttributeWeave;
import main.java.avii.editor.metamodel.weaving.ClassWeave;
import main.java.avii.editor.metamodel.weaving.DomainWeave;
import main.java.avii.editor.metamodel.weaving.EventWeave;
import main.java.avii.editor.metamodel.weaving.exception.CannotAddClassToWeaveIfItsDomainIsntRegistered;
import main.java.avii.editor.metamodel.weaving.exception.CannotAddDuplicateClassToClassWeaveException;
import main.java.avii.editor.metamodel.weaving.exception.CannotAddDuplicateDomainToDomainWeaveException;
import main.java.avii.editor.metamodel.weaving.exception.CannotAddSuperClassToClassWeaveException;
import main.java.avii.editor.metamodel.weaving.exception.CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.relations.SimulatedRelationship;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.weave.InstanceWeave;
import main.java.avii.simulator.weave.WeaveSimulator;
import test.java.tests.TestHarness;
import test.java.tests.WeaveTestHarness;

public class FactoryStoreWeaveTests extends TestCase {
	private DomainWeave _weave;
	private DomainFactory _factoryDomain;
	private DomainStore _storeDomain;

	public FactoryStoreWeaveTests(String name) throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotAddDuplicateDomainToDomainWeaveException, CannotAddSuperClassToClassWeaveException, CannotAddDuplicateClassToClassWeaveException, CannotAddClassToWeaveIfItsDomainIsntRegistered, CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException {
		super(name);
		setupWeave();
	}

	public void setupWeave() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotAddDuplicateDomainToDomainWeaveException, CannotAddSuperClassToClassWeaveException, CannotAddDuplicateClassToClassWeaveException, CannotAddClassToWeaveIfItsDomainIsntRegistered, CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException {
		this._weave = new DomainWeave("Store that gets items from factory");
		this._storeDomain = new DomainStore();
		this._factoryDomain = new DomainFactory();
		this._weave.registerDomain(this._storeDomain.domain);
		this._weave.registerDomain(this._factoryDomain.domain);
		
		ClassWeave itemWeave = this._weave.createClassWeave();
		itemWeave.registerClass(this._storeDomain.item);
		itemWeave.registerClass(this._factoryDomain.item);
		
		AttributeWeave itemQuantityWeave = itemWeave.createAttributeWeave();
		itemQuantityWeave.addAttribute(this._storeDomain.itemQuanitity);
		itemQuantityWeave.addAttribute(this._factoryDomain.itemQuanitity);
		
		EventWeave outOfStock_ProduceItemWeave = itemWeave.createEventWeave();
		outOfStock_ProduceItemWeave.addEvent(this._storeDomain.outOfStockSpec);
		outOfStock_ProduceItemWeave.addEvent(this._factoryDomain.produceItemSpec);
		
		EventWeave backInStock_FinishedProductionItemWeave = itemWeave.createEventWeave();
		backInStock_FinishedProductionItemWeave.addEvent(this._storeDomain.backInStockSpec);
		backInStock_FinishedProductionItemWeave.addEvent(this._factoryDomain.finishedProductionItemSpec);
	}
	
	@SuppressWarnings("unused")
	public void test_can_create_weave_simulator() throws CannotSimulateDomainThatIsInvalidException
	{
		WeaveSimulator wsimulator = new WeaveSimulator(this._weave);
	}
	
	public void test_weave_simulator_creates_simulated_classes_for_each_domain() throws CannotSimulateDomainThatIsInvalidException
	{
		WeaveSimulator wsimulator = new WeaveSimulator(this._weave);
		
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		Collection<String> domainClassNames = this._storeDomain.domain.getClassNames();
		Collection<String> simulatedClassNames = wsimulator.getSimulatedClassNames();
		Assert.assertEquals(domainClassNames.size(), simulatedClassNames.size());
		for(String domainClassName : domainClassNames)
		{
			Assert.assertTrue(simulatedClassNames.contains(domainClassName));
		}
		
		wsimulator.setCurrentDomain(this._factoryDomain.domain);
		domainClassNames = this._factoryDomain.domain.getClassNames();
		simulatedClassNames = wsimulator.getSimulatedClassNames();
		Assert.assertEquals(domainClassNames.size(), simulatedClassNames.size());
		for(String domainClassName : domainClassNames)
		{
			Assert.assertTrue(simulatedClassNames.contains(domainClassName));
		}
	}
	
	public void test_weave_simulator_creates_simulated_relation_for_each_relation_in_each_domain() throws CannotSimulateDomainThatIsInvalidException
	{
		WeaveSimulator wsimulator = new WeaveSimulator(this._weave);
		
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		Assert.assertEquals(this._storeDomain.domain.getRelations().size(), wsimulator.getSimulatedRelations().size());
		for(SimulatedRelationship relation : wsimulator.getSimulatedRelations())
		{
			Assert.assertTrue(this._storeDomain.domain.hasRelationWithName(relation.getName()));
		}
		
		wsimulator.setCurrentDomain(this._factoryDomain.domain);
		Assert.assertEquals(this._factoryDomain.domain.getRelations().size(), wsimulator.getSimulatedRelations().size());
		for(SimulatedRelationship relation : wsimulator.getSimulatedRelations())
		{
			Assert.assertTrue(this._factoryDomain.domain.hasRelationWithName(relation.getName()));
		}
	}
	
	public void test_weave_simulator_assigns_simulated_relation_to_classes_that_have_them_for_each_domain() throws CannotSimulateDomainThatIsInvalidException
	{
		WeaveSimulator wsimulator = new WeaveSimulator(this._weave);
		
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		for(SimulatedClass simulatedClass : wsimulator.getSimulatedClasses())
		{
			EntityClass concreteClass = simulatedClass.getConcreteClass();
			for(SimulatedRelationship relationship : simulatedClass.getSimulatedRelationships())
			{
				Assert.assertTrue(concreteClass.hasRelation(relationship.getName()));
			}
		}
		
		wsimulator.setCurrentDomain(this._factoryDomain.domain);
		for(SimulatedClass simulatedClass : wsimulator.getSimulatedClasses())
		{
			EntityClass concreteClass = simulatedClass.getConcreteClass();
			for(SimulatedRelationship relationship : simulatedClass.getSimulatedRelationships())
			{
				Assert.assertTrue(concreteClass.hasRelation(relationship.getName()));
			}
		}
	}
	
	public void test_when_creating_a_class_not_in_a_weave_only_the_requested_class_is_created() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityClass store = this._storeDomain.store;
		EntityState initial = new EntityState("initial store");
		store.addState(initial);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(store);
		
		String proc = "CREATE iga FROM Store;\n" +
				"CREATE baker FROM Store;\n";
		
		initial.setProcedureText(proc);
		
		WeaveSimulator wsimulator = new WeaveSimulator(this._weave);
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		
		wsimulator.setSimulatingState(initial);
		
		Assert.assertEquals(0,wsimulator.getSimulatedInstanceCount());
		wsimulator.getSimulatingState().simulate();
		
		Assert.assertEquals(2,wsimulator.getSimulatedInstanceCount());
		
		store.removeState(initial);
	}
	
	public void test_when_creating_a_class_from_a_weave_all_classes_in_weave_get_created() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityClass store = this._storeDomain.store;
		EntityState initial = new EntityState("initial store");
		store.addState(initial);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(store);
		
		String proc = "CREATE roll FROM Item;\n" +
				"CREATE loaf FROM Item;\n";
		
		initial.setProcedureText(proc);
		
		WeaveSimulator wsimulator = new WeaveSimulator(this._weave);
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		
		wsimulator.setSimulatingState(initial);
		
		Assert.assertEquals(0,wsimulator.getSimulatedInstanceCount());
		wsimulator.getSimulatingState().simulate();
		
		Assert.assertEquals(4,wsimulator.getSimulatedInstanceCount());
		
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		SimulatedInstance storeItem = wsimulator.getSimulatedClass("Item").getInstances().get(0);
		
		wsimulator.setCurrentDomain(this._factoryDomain.domain);
		SimulatedInstance factoryItem = wsimulator.getSimulatedClass("Item").getInstances().get(0);
		
		Assert.assertTrue(storeItem.isInWeave());
		Assert.assertTrue(factoryItem.isInWeave());
		
		InstanceWeave storeItemWeave = storeItem.getInstanceWeave();
		Assert.assertEquals(true, storeItemWeave.getInstances().contains(storeItem));
		Assert.assertEquals(true, storeItemWeave.getInstances().contains(factoryItem));
		
		
		InstanceWeave factorytemWeave = storeItem.getInstanceWeave();
		Assert.assertEquals(true, factorytemWeave.getInstances().contains(storeItem));
		Assert.assertEquals(true, factorytemWeave.getInstances().contains(factoryItem));

		store.removeState(initial);
	}
	
	public void test_when_deleting_a_class_not_in_a_weave_only_the_requested_class_is_created() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityClass store = this._storeDomain.store;
		EntityState initial = new EntityState("initial store");
		store.addState(initial);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(store);
		
		String proc = "CREATE iga FROM Store;\n" +
				"CREATE baker FROM Store;\n" +
				"DELETE iga;\n";
		
		initial.setProcedureText(proc);
		
		WeaveSimulator wsimulator = new WeaveSimulator(this._weave);
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		
		wsimulator.setSimulatingState(initial);
		
		Assert.assertEquals(0,wsimulator.getSimulatedInstanceCount());
		wsimulator.getSimulatingState().simulate();
		
		Assert.assertEquals(1,wsimulator.getSimulatedInstanceCount());
		
		store.removeState(initial);
	}
	
	public void test_when_deleting_a_class_from_a_weave_all_classes_in_weave_get_created() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityClass store = this._storeDomain.store;
		EntityState initial = new EntityState("initial store");
		store.addState(initial);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(store);
		
		String proc = "CREATE roll FROM Item;\n" +
				"CREATE loaf FROM Item;\n" +
				"DELETE roll;\n";
		
		initial.setProcedureText(proc);
		
		WeaveSimulator wsimulator = new WeaveSimulator(this._weave);
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		
		wsimulator.setSimulatingState(initial);
		
		Assert.assertEquals(0,wsimulator.getSimulatedInstanceCount());
		wsimulator.getSimulatingState().simulate();
		
		Assert.assertEquals(2,wsimulator.getSimulatedInstanceCount());
		
		store.removeState(initial);
	}

	public void test_can_get_attribute_from_cam_instances() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityClass store = this._storeDomain.store;
		EntityState initial = new EntityState("initial store");
		store.addState(initial);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(store);
		
		String proc = "CREATE roll FROM Item;\n";		
		initial.setProcedureText(proc);
		
		WeaveSimulator wsimulator = new WeaveSimulator(this._weave);
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		
		wsimulator.setSimulatingState(initial);
		
		Assert.assertEquals(0,wsimulator.getSimulatedInstanceCount());
		wsimulator.getSimulatingState().simulate();
		
		Assert.assertEquals(2,wsimulator.getSimulatedInstanceCount());
		
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		SimulatedInstance storeItem = wsimulator.getSimulatedClass("Item").getInstances().get(0);
		
		wsimulator.setCurrentDomain(this._factoryDomain.domain);
		SimulatedInstance factoryItem = wsimulator.getSimulatedClass("Item").getInstances().get(0);
		
		Assert.assertTrue(storeItem.isInWeave());
		Assert.assertTrue(factoryItem.isInWeave());

		Assert.assertEquals(0, storeItem.getAttribute("Quantity"));
		Assert.assertEquals(0, factoryItem.getAttribute("Quantity"));
		
		store.removeState(initial);
	}
	
	public void test_can_set_attribute_from_cam_instances() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityClass store = this._storeDomain.store;
		EntityState initial = new EntityState("initial store");
		store.addState(initial);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(store);
		
		String proc = "CREATE roll FROM Item;\n" +
				"roll.Quantity = 5;\n" +
				"roll.Name = \"fred\";\n";		
		initial.setProcedureText(proc);
		
		WeaveSimulator wsimulator = new WeaveSimulator(this._weave);
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		
		wsimulator.setSimulatingState(initial);
		
		Assert.assertEquals(0,wsimulator.getSimulatedInstanceCount());
		wsimulator.getSimulatingState().simulate();
		
		Assert.assertEquals(2,wsimulator.getSimulatedInstanceCount());
		
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		SimulatedInstance storeItem = wsimulator.getSimulatedClass("Item").getInstances().get(0);
		
		wsimulator.setCurrentDomain(this._factoryDomain.domain);
		SimulatedInstance factoryItem = wsimulator.getSimulatedClass("Item").getInstances().get(0);
		
		Assert.assertTrue(storeItem.isInWeave());
		Assert.assertTrue(factoryItem.isInWeave());

		Assert.assertEquals(5, storeItem.getAttribute("Quantity"));
		Assert.assertEquals("fred", storeItem.getAttribute("Name"));
		Assert.assertEquals(5, factoryItem.getAttribute("Quantity"));
		
		store.removeState(initial);
	}
	
	public void test_can_relate_cam_instance() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		EntityClass store = this._storeDomain.store;
		EntityState initial = new EntityState("initial store");
		store.addState(initial);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(store);
		
		String proc = "CREATE roll FROM Item;\n" +
				"CREATE iga FROM Store;\n" +
				"RELATE roll TO iga ACROSS R1;\n" +
				"SELECT ANY item RELATED BY iga->R1;\n" +
				"SELECT ANY store RELATED BY roll->R1;\n";
		initial.setProcedureText(proc);
		
		WeaveSimulator wsimulator = new WeaveSimulator(this._weave);
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		
		wsimulator.setSimulatingState(initial);
		wsimulator.getSimulatingState().simulate();
		
		SimulatedInstance rollInstance = wsimulator.getSimulatingState().getInstanceWithName("roll");
		SimulatedInstance igaInstance = wsimulator.getSimulatingState().getInstanceWithName("iga");
		SimulatedInstance itemInstance = wsimulator.getSimulatingState().getInstanceWithName("item");
		SimulatedInstance storeInstance = wsimulator.getSimulatingState().getInstanceWithName("store");

		Assert.assertEquals(rollInstance, itemInstance);
		Assert.assertEquals(igaInstance, storeInstance);
		
		store.removeState(initial);
	}
	
	public void test_can_generate_event_to_cam_instance_withStoreDomainCurrent() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityClass store = this._storeDomain.store;
		EntityState initial = new EntityState("initial store");
		store.addState(initial);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(store);
		
		String proc = "CREATE roll FROM Item;\n" +
				"roll.Quantity = 5;\n" +
				"roll.ReOrderQuantity = 5;\n" +
				"roll.Name = \"fred\";\n" +
				"GENERATE sellItem(itemName=\"Bread\", quantity=5) TO roll;\n";
		initial.setProcedureText(proc);
		
		WeaveSimulator wsimulator = new WeaveSimulator(this._weave);
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		
		wsimulator.setSimulatingState(initial);
		wsimulator.getSimulatingState().simulate();
		
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		SimulatedInstance storeItem = wsimulator.getSimulatedClass("Item").getInstances().get(0);
		Assert.assertEquals("Initial", storeItem.getAttribute("state"));

		wsimulator.setCurrentDomain(this._factoryDomain.domain);
		SimulatedInstance factoryItem = wsimulator.getSimulatedClass("Item").getInstances().get(0);
		Assert.assertEquals("Initial", factoryItem.getAttribute("state"));
		
		
		Assert.assertEquals(5, storeItem.getAttribute("Quantity"));
		Assert.assertEquals(5, factoryItem.getAttribute("Quantity"));
		
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		wsimulator.executeNextStateProcedure();
		
		Assert.assertEquals("Selling Item", storeItem.getAttribute("state"));
		Assert.assertEquals(0, storeItem.getAttribute("Quantity"));
		Assert.assertEquals(0, factoryItem.getAttribute("Quantity"));
		
		wsimulator.executeNextStateProcedure();
		
		Assert.assertEquals("Producing item", factoryItem.getAttribute("state"));
		Assert.assertEquals(50, storeItem.getAttribute("Quantity"));
		Assert.assertEquals(50, factoryItem.getAttribute("Quantity"));
		
		wsimulator.executeNextStateProcedure();
		
		Assert.assertEquals("No stock", storeItem.getAttribute("state"));
		Assert.assertEquals(50, storeItem.getAttribute("Quantity"));
		Assert.assertEquals(50, factoryItem.getAttribute("Quantity"));
		
		wsimulator.executeNextStateProcedure();
		
		Assert.assertEquals("Finished production", factoryItem.getAttribute("state"));
		Assert.assertEquals(50, storeItem.getAttribute("Quantity"));
		Assert.assertEquals(50, factoryItem.getAttribute("Quantity"));
		
		wsimulator.executeNextStateProcedure();
		
		Assert.assertEquals("Back in stock", storeItem.getAttribute("state"));
		Assert.assertEquals(50, storeItem.getAttribute("Quantity"));
		Assert.assertEquals(50, factoryItem.getAttribute("Quantity"));
		
		store.removeState(initial);
	}
	
	public void test_can_generate_event_to_cam_instance_with_factoryDomainCurrent() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityClass store = this._storeDomain.store;
		EntityState initial = new EntityState("initial store");
		store.addState(initial);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(store);
		
		String proc = "CREATE roll FROM Item;\n" +
				"roll.Quantity = 5;\n" +
				"roll.ReOrderQuantity = 5;\n" +
				"roll.Name = \"fred\";\n" +
				"GENERATE sellItem(itemName=\"Bread\", quantity=5) TO roll;\n";
		initial.setProcedureText(proc);
		
		WeaveSimulator wsimulator = new WeaveSimulator(this._weave);
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		
		wsimulator.setSimulatingState(initial);
		wsimulator.getSimulatingState().simulate();
		
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		SimulatedInstance storeItem = wsimulator.getSimulatedClass("Item").getInstances().get(0);
		Assert.assertEquals("Initial", storeItem.getAttribute("state"));

		wsimulator.setCurrentDomain(this._factoryDomain.domain);
		SimulatedInstance factoryItem = wsimulator.getSimulatedClass("Item").getInstances().get(0);
		Assert.assertEquals("Initial", factoryItem.getAttribute("state"));
		
		Diagnostics diagnostics = wsimulator.getDiagnostics();
		
		Assert.assertEquals(2, diagnostics.countOfInstanceLifeCycleStages());
		int countOfInstructions = 0;
		
		wsimulator.setCurrentDomain(this._factoryDomain.domain);
		wsimulator.executeNextStateProcedure();
		Assert.assertTrue(diagnostics.countOfInstructionsExecuted() >= countOfInstructions);
		countOfInstructions = diagnostics.countOfInstructionsExecuted();
		Assert.assertEquals(3, diagnostics.countOfInstanceLifeCycleStages());
		
		Assert.assertEquals("Selling Item", storeItem.getAttribute("state"));
		Assert.assertEquals(0, storeItem.getAttribute("Quantity"));
		
		wsimulator.executeNextStateProcedure();
		Assert.assertTrue(diagnostics.countOfInstructionsExecuted() >= countOfInstructions);
		countOfInstructions = diagnostics.countOfInstructionsExecuted();
		Assert.assertEquals(4, diagnostics.countOfInstanceLifeCycleStages());
		
		Assert.assertEquals("Producing item", factoryItem.getAttribute("state"));
		
		wsimulator.executeNextStateProcedure();
		Assert.assertTrue(diagnostics.countOfInstructionsExecuted() >= countOfInstructions);
		countOfInstructions = diagnostics.countOfInstructionsExecuted();
		Assert.assertEquals(5, diagnostics.countOfInstanceLifeCycleStages());
		
		Assert.assertEquals("No stock", storeItem.getAttribute("state"));
		
		wsimulator.executeNextStateProcedure();
		Assert.assertTrue(diagnostics.countOfInstructionsExecuted() >= countOfInstructions);
		countOfInstructions = diagnostics.countOfInstructionsExecuted();
		Assert.assertEquals(6, diagnostics.countOfInstanceLifeCycleStages());
		
		Assert.assertEquals("Finished production", factoryItem.getAttribute("state"));
		
		wsimulator.executeNextStateProcedure();
		Assert.assertTrue(diagnostics.countOfInstructionsExecuted() >= countOfInstructions);
		countOfInstructions = diagnostics.countOfInstructionsExecuted();
		Assert.assertEquals(7, diagnostics.countOfInstanceLifeCycleStages());
		
		Assert.assertEquals("Back in stock", storeItem.getAttribute("state"));
		
		store.removeState(initial);
	}
	
	
	public void test_can_execute_wsimulator_till_no_more_events() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityClass store = this._storeDomain.store;
		EntityState initial = new EntityState("initial store");
		store.addState(initial);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(store);
		
		String proc = "CREATE roll FROM Item;\n" +
				"roll.Quantity = 5;\n" +
				"roll.ReOrderQuantity = 5;\n" +
				"roll.Name = \"fred\";\n" +
				"GENERATE sellItem(itemName=\"Bread\", quantity=5) TO roll;\n";
		initial.setProcedureText(proc);
		
		WeaveSimulator wsimulator = new WeaveSimulator(this._weave);
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		
		wsimulator.setSimulatingState(initial);
		wsimulator.getSimulatingState().simulate();
		
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		SimulatedInstance storeItem = wsimulator.getSimulatedClass("Item").getInstances().get(0);
		Assert.assertEquals("Initial", storeItem.getAttribute("state"));

		wsimulator.setCurrentDomain(this._factoryDomain.domain);
		SimulatedInstance factoryItem = wsimulator.getSimulatedClass("Item").getInstances().get(0);
		Assert.assertEquals("Initial", factoryItem.getAttribute("state"));
		
		while(wsimulator.hasReadyEvent())
		{
			wsimulator.executeNextStateProcedure();
		}
		
		Assert.assertEquals("Finished production", factoryItem.getAttribute("state"));
		Assert.assertEquals("Back in stock", storeItem.getAttribute("state"));
		Assert.assertEquals(50, storeItem.getAttribute("Quantity"));
		Assert.assertEquals(50, factoryItem.getAttribute("Quantity"));
		
		store.removeState(initial);
	}
	
	public void test_wsimulator_slices_are_centrally_managed() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityClass store = this._storeDomain.store;
		EntityState initial = new EntityState("initial store");
		store.addState(initial);
		
		TestHelper.addNewInitialStateToClassWithEventToPreviousInitialState(store);
		
		String proc = "CREATE roll FROM Item;\n" +
				"roll.Quantity = 5;\n" +
				"roll.ReOrderQuantity = 5;\n" +
				"roll.Name = \"fred\";\n" +
				"GENERATE sellItem(itemName=\"Bread\", quantity=5) TO roll;\n";
		initial.setProcedureText(proc);
		
		WeaveSimulator wsimulator = new WeaveSimulator(this._weave);
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		
		wsimulator.setSimulatingState(initial);
		wsimulator.getSimulatingState().simulate();
		
		wsimulator.setCurrentDomain(this._storeDomain.domain);
		SimulatedInstance storeItem = wsimulator.getSimulatedClass("Item").getInstances().get(0);
		Assert.assertEquals("Initial", storeItem.getAttribute("state"));

		wsimulator.setCurrentDomain(this._factoryDomain.domain);
		SimulatedInstance factoryItem = wsimulator.getSimulatedClass("Item").getInstances().get(0);
		Assert.assertEquals("Initial", factoryItem.getAttribute("state"));
		
		int countOfProceduresExecuted = 0;
		while(wsimulator.hasReadyEvent())
		{
			wsimulator.executeNextStateProcedure();
			countOfProceduresExecuted++;
		}
		
		Assert.assertEquals(5, countOfProceduresExecuted);
		Assert.assertEquals(7, wsimulator.getDiagnostics().countOfInstanceLifeCycleStages());
		
		store.removeState(initial);
	}
	
	

	public void test_UNIMPLEMENTED_can_run_test_hareness_on_woven_simulator() throws CannotSimulateDomainThatIsInvalidException
	{
		TestHarness harness = new WeaveTestHarness(this._weave);
		harness.execute();

		SimulatedTestHelper.printHarnessResults(harness);
		// some tests will fail, but they are marked as not appropriate when woven
		Assert.assertEquals(true, harness.allAssertionsPassed());
		Assert.assertEquals(false, harness.wereExceptionsRaised());
	}
	
}
