package test.java.modelTesting;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.scenario.AssertionTestVectorProcedure;
import main.java.avii.scenario.InitialTestVectorProcedure;
import main.java.avii.scenario.TestScenario;
import main.java.avii.scenario.TestVector;
import main.java.avii.scenario.TestVectorClassInstance;
import main.java.avii.scenario.TestVectorClassTable;
import main.java.avii.scenario.TestVectorProcedure;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedTestVector;
import main.java.avii.simulator.simulatedTypes.TestVectorInstanceMap;
import main.java.avii.simulator.simulatedTypes.TestVectorSimulatedInstance;
import test.java.simulatorTests.SimulatedGenerateEventTests;
import test.java.tests.SimulatedTestScenario;
import test.java.tests.TestHarness;

public class SimulatedTestVectorTests extends TestCase {
	public SimulatedTestVectorTests(String name){
		super(name);
	}
	
	public void test_can_create_simulated_vector()
	{
		TestVector vector = new TestVector();
		SimulatedTestVector simulated = new SimulatedTestVector(vector);
		Assert.assertEquals(vector, simulated.getVector());
	}
	
	public void test_scenario_creates_simulated_test_vectors()
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		TestScenario scenario = new TestScenario();
		domain.addScenario(scenario);
		TestVector vector = new TestVector();
		scenario.addVector(vector);
		
		SimulatedTestScenario simulated = new SimulatedTestScenario(scenario);
		Assert.assertEquals(1, simulated.getSimulatedVectors().size());
	}
	
	private Simulator initSimulatorForTests(SimulatedTestVector vector) throws CannotSimulateDomainThatIsInvalidException {
		Simulator simulator = new Simulator(vector.getVector().getScenario().getDomain());
		initSimulatorForTestsInner(simulator, vector);
		vector.setSimulator(simulator);
		return simulator;
	}

	private void resetSimulator(Simulator simulator, SimulatedTestVector vector)
	{
		initSimulatorForTestsInner(simulator, vector);
	}
	
	private void initSimulatorForTestsInner(Simulator simulator, SimulatedTestVector vector) {
		simulator.setSimulatingVector(vector);
		simulator.setSimulatingInstance(new TestVectorSimulatedInstance());
	}
	
	public void test_can_execute_test_vector() throws InvalidActionLanguageSyntaxException, NameNotFoundException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		TestScenario scenario = new TestScenario();
		TestVector vector = new TestVector();
		EntityClass car = new EntityClass("Car");
		
		domain.addScenario(scenario);
		domain.addClass(car);
		scenario.addVector(vector);
		
		TestVectorProcedure procedure = new InitialTestVectorProcedure();
		procedure.setVector(vector);
		String proc = "";
		proc += "CREATE car FROM Car;\n";
		procedure.setProcedure(proc);
		
		TestHarness harness = new TestHarness(domain);
		SimulatedTestScenario simScenario = harness.getScenarios().iterator().next();
		SimulatedTestVector simVector = simScenario.getSimulatedVectors().iterator().next();
		
		initSimulatorForTests(simVector);
		
		simVector.executeTestSetupVector();
		SimulatedInstance car001 = simVector.getInstanceWithName("car");
		Assert.assertEquals(0, car001.getIdentifier().getSequence());
	}
	
	public void test_can_execute_vector_and_kick_off_simulator() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		String proc1 = "self.Tag=\"worked\";\n";
		String proc2 = "";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		
		TestScenario scenario = new TestScenario();
		domain.addScenario(scenario);
		TestVector vector = new TestVector();
		scenario.addVector(vector);
		
		TestVectorProcedure procedure = new InitialTestVectorProcedure();
		procedure.setVector(vector);
		String proc = "";
		proc += "CREATE class1 FROM Class1;\n";
		proc += "GENERATE EventOne() TO class1;\n";
		procedure.setProcedure(proc);
		
		TestHarness harness = new TestHarness(domain);
		SimulatedTestScenario simScenario = harness.getScenarios().iterator().next();
		SimulatedTestVector simVector = simScenario.getSimulatedVectors().iterator().next();
		
		initSimulatorForTests(simVector);
		
		simVector.executeTestSetupVector();
		SimulatedInstance class1 = simVector.getInstanceWithName("class1");
		
		Assert.assertEquals("", class1.getAttribute("Tag"));
		
		simVector.executeNextStateProcedure();
		
		Assert.assertEquals("worked", class1.getAttribute("Tag"));
	}
	
	public void test_can_execute_vector_and_kick_off_simulator_with_multiple_events() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		String proc1 = "self.Tag=\"worked1\";\n";
		String proc2 = "self.Tag=\"worked2\";\n";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		
		TestScenario scenario = new TestScenario();
		domain.addScenario(scenario);
		TestVector vector = new TestVector();
		scenario.addVector(vector);
		
		TestVectorProcedure procedure = new InitialTestVectorProcedure();
		procedure.setVector(vector);String proc = "";
		proc += "CREATE class1 FROM Class1;\n";
		proc += "GENERATE EventOne() TO class1;\n";
		proc += "GENERATE Event1() TO class1;\n";
		procedure.setProcedure(proc);
		
		TestHarness harness = new TestHarness(domain);
		SimulatedTestScenario simScenario = harness.getScenarios().iterator().next();
		SimulatedTestVector simVector = simScenario.getSimulatedVectors().iterator().next();
		
		initSimulatorForTests(simVector);
		
		simVector.executeTestSetupVector();
		SimulatedInstance class1 = simVector.getInstanceWithName("class1");
		
		Assert.assertEquals("", class1.getAttribute("Tag"));
		
		simVector.executeNextStateProcedure();
		
		Assert.assertEquals("worked1", class1.getAttribute("Tag"));
		
		simVector.executeNextStateProcedure();
		
		Assert.assertEquals("worked2", class1.getAttribute("Tag"));
	}
	
	
	public void test_can_execute_vector_get_last_time_slice() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		String proc1 = "self.Tag=\"worked1\";\n";
		String proc2 = "self.Tag=\"worked2\";\n";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		
		TestScenario scenario = new TestScenario();
		domain.addScenario(scenario);
		TestVector vector = new TestVector();
		scenario.addVector(vector);
		
		TestVectorProcedure procedure = new InitialTestVectorProcedure();
		procedure.setVector(vector);
		String proc = "";
		proc += "CREATE class1 FROM Class1;\n";
		proc += "GENERATE EventOne() TO class1;\n";
		proc += "GENERATE Event1() TO class1;\n";
		procedure.setProcedure(proc);
		
		TestHarness harness = new TestHarness(domain);
		SimulatedTestScenario simScenario = harness.getScenarios().iterator().next();
		SimulatedTestVector simVector = simScenario.getSimulatedVectors().iterator().next();
		
		initSimulatorForTests(simVector);
		
		simVector.executeTestSetupVector();
		SimulatedInstance class1 = simVector.getInstanceWithName("class1");
		
		Assert.assertEquals("", class1.getAttribute("Tag"));
		
		simVector.executeNextStateProcedure();
		simVector.executeNextStateProcedure();
	
		Assert.assertEquals("worked2", class1.getAttribute("Tag"));

	}

	public void test_can_execute_vector_from_tables() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		String proc1 = "";
		String proc2 = "";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		EntityClass klass = domain.getEntityClassWithName("Class1");
		EntityAttribute tag = klass.getAttributeWithName("Tag");
		
		TestScenario scenario = new TestScenario();
		domain.addScenario(scenario);
		TestVector vector = new TestVector();
		scenario.addVector(vector);
		
		TestVectorClassTable table = new TestVectorClassTable(klass);
		table.addAttribute(tag);
		TestVectorClassInstance instance = table.createInstance();
		instance.setInitialAttribute("Tag", "tagValue");
		
		vector.addClassTable(table);
		
		vector.createInitialProcedureFromTables();
		
		TestHarness harness = new TestHarness(domain);
		SimulatedTestScenario simScenario = harness.getScenarios().iterator().next();
		SimulatedTestVector simVector = simScenario.getSimulatedVectors().iterator().next();
		
		initSimulatorForTests(simVector);
		
		simVector.executeTestSetupVector();
		SimulatedInstance klass1 = simVector.getInstanceWithName("Class1_001");
		Assert.assertEquals("tagValue", klass1.getAttribute("Tag"));
	}

	public void test_can_execute_vector_from_tables_and_capture_ids() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		String proc1 = "";
		String proc2 = "";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		EntityClass klass = domain.getEntityClassWithName("Class1");
		EntityAttribute tag = klass.getAttributeWithName("Tag");
		
		TestScenario scenario = new TestScenario();
		domain.addScenario(scenario);
		TestVector vector = new TestVector();
		scenario.addVector(vector);
		
		TestVectorClassTable table = new TestVectorClassTable(klass);
		table.addAttribute(tag);
		TestVectorClassInstance instance = table.createInstance();
		instance.setInitialAttribute("Tag", "tagValue");
		
		vector.addClassTable(table);
		
		vector.createInitialProcedureFromTables();
		
		TestHarness harness = new TestHarness(domain);
		SimulatedTestScenario simScenario = harness.getScenarios().iterator().next();
		SimulatedTestVector simVector = simScenario.getSimulatedVectors().iterator().next();
		
		initSimulatorForTests(simVector);
		
		simVector.executeTestSetupVector();
		SimulatedInstance klass1 = simVector.getInstanceWithName("Class1_001");

		TestVectorInstanceMap map = simVector.getVectorInstanceMap();
		Assert.assertEquals(klass1.getIdentifier(), map.getIdentifierForInstanceName("Class1_001"));
	}

	public void test_can_execute_vector_from_tables_and_fail_with_assertion() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException, CannotSimulateDomainThatIsInvalidException
	{
		String proc1 = "";
		String proc2 = "";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		EntityClass klass = domain.getEntityClassWithName("Class1");
		EntityAttribute tag = klass.getAttributeWithName("Tag");
		
		TestScenario scenario = new TestScenario();
		domain.addScenario(scenario);
		TestVector vector = new TestVector();
		scenario.addVector(vector);
		
		TestVectorClassTable table = new TestVectorClassTable(klass);
		table.addAttribute(tag);
		TestVectorClassInstance instance = table.createInstance();
		instance.setInitialAttribute("Tag", "tagValue");
		
		vector.addClassTable(table);
		
		vector.createInitialProcedureFromTables();
		
		TestVectorProcedure procedure = new AssertionTestVectorProcedure();
		procedure.setVector(vector);
		
		String rawProc = "FAIL 5;\n";
		procedure.setProcedure(rawProc);

		TestHarness harness = new TestHarness(domain);
		SimulatedTestScenario simScenario = harness.getScenarios().iterator().next();
		SimulatedTestVector simVector = simScenario.getSimulatedVectors().iterator().next();
		
		initSimulatorForTests(simVector);
		
		simVector.executeTestSetupVector();
		SimulatedInstance klass1 = simVector.getInstanceWithName("Class1_001");

		TestVectorInstanceMap map = simVector.getVectorInstanceMap();
		Assert.assertEquals(klass1.getIdentifier(), map.getIdentifierForInstanceName("Class1_001"));
		
		simVector.executeAssertionVector();
		Assert.assertEquals(true, simVector.hasAssertionFailed());
		Assert.assertEquals(5.0, simVector.getAssertionFail());
	}
	
	
	public void test_can_execute_vector_from_tables_and_fail_with_assertion_for_instance_from_table() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException, CannotSimulateDomainThatIsInvalidException
	{
		String proc1 = "";
		String proc2 = "";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		EntityClass klass = domain.getEntityClassWithName("Class1");
		EntityAttribute tag = klass.getAttributeWithName("Tag");
		
		TestScenario scenario = new TestScenario();
		domain.addScenario(scenario);
		TestVector vector = new TestVector();
		scenario.addVector(vector);
		
		TestVectorClassTable table = new TestVectorClassTable(klass);
		table.addAttribute(tag);
		TestVectorClassInstance instance = table.createInstance();
		instance.setInitialAttribute("Tag", "tagValue");
		
		vector.addClassTable(table);
		
		vector.createInitialProcedureFromTables();
		
		TestVectorProcedure procedure = new AssertionTestVectorProcedure();
		procedure.setVector(vector);
		
		String rawProc = "FAIL Class1_001.Tag;\n";
		procedure.setProcedure(rawProc);

		TestHarness harness = new TestHarness(domain);
		SimulatedTestScenario simScenario = harness.getScenarios().iterator().next();
		SimulatedTestVector simVector = simScenario.getSimulatedVectors().iterator().next();

		Simulator simulator = initSimulatorForTests(simVector);
		
		simVector.executeTestSetupVector();
		SimulatedInstance klass1 = simVector.getInstanceWithName("Class1_001");

		TestVectorInstanceMap map = simVector.getVectorInstanceMap();
		Assert.assertEquals(klass1.getIdentifier(), map.getIdentifierForInstanceName("Class1_001"));
		
		resetSimulator(simulator, simVector);
		
		simVector.executeAssertionVector();
		Assert.assertEquals(true, simVector.hasAssertionFailed());
		Assert.assertEquals("tagValue", simVector.getAssertionFail());
	}
	
	public void test_can_execute_vector_and_kick_off_simulator_and_check_assertion() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		String proc1 = "self.Tag=\"worked\";\n";
		String proc2 = "";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		
		TestScenario scenario = new TestScenario();
		domain.addScenario(scenario);
		TestVector vector = new TestVector();
		scenario.addVector(vector);
		
		TestVectorProcedure procedure = new InitialTestVectorProcedure();
		procedure.setVector(vector);
		String proc = "";
		proc += "CREATE class1 FROM Class1;\n";
		proc += "GENERATE EventOne() TO class1;\n";
		procedure.setProcedure(proc);
		
		TestVectorProcedure assertProcedure = new AssertionTestVectorProcedure();
		assertProcedure.setVector(vector);
		String rawProc = "SELECT ANY c1 FROM INSTANCES OF Class1;\n";
		rawProc += "IF c1.Tag != \"hasn't worked\" THEN\n";
		rawProc += "	FAIL \"attribute should have been 'worked'\";\n";
		rawProc += "END IF;\n";
		
		assertProcedure.setProcedure(rawProc);
		
		TestHarness harness = new TestHarness(domain);
		SimulatedTestScenario simScenario = harness.getScenarios().iterator().next();
		SimulatedTestVector simVector = simScenario.getSimulatedVectors().iterator().next();
		
		Simulator simulator = initSimulatorForTests(simVector);
		
		simVector.executeTestSetupVector();
		SimulatedInstance class1 = simVector.getInstanceWithName("class1");
		
		Assert.assertEquals("", class1.getAttribute("Tag"));
		
		simVector.executeNextStateProcedure();
		
		resetSimulator(simulator, simVector);
		
		simVector.executeAssertionVector();
		
		Assert.assertEquals(true, simVector.hasAssertionFailed());
		Assert.assertEquals("attribute should have been 'worked'", simVector.getAssertionFail());
	}
	
	public void test_can_execute_vector_and_kick_off_simulator_and_check_failing_assertion() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		String proc1 = "self.Tag=\"worked\";\n";
		String proc2 = "";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		
		TestScenario scenario = new TestScenario();
		domain.addScenario(scenario);
		TestVector vector = new TestVector();
		scenario.addVector(vector);
		
		TestVectorProcedure procedure = new InitialTestVectorProcedure();
		procedure.setVector(vector);;
		String setupProcString = "";
		setupProcString += "CREATE class1 FROM Class1;\n";
		setupProcString += "GENERATE EventOne() TO class1;\n";
		procedure.setProcedure(setupProcString);
		
		TestVectorProcedure assertProcedure = new AssertionTestVectorProcedure();
		assertProcedure.setVector(vector);
		String assertProcString = "SELECT ANY c1 FROM INSTANCES OF Class1;\n";
		assertProcString += "IF c1.Tag != \"worked\" THEN\n";
		assertProcString += "	FAIL \"attribute should have been 'worked'\";\n";
		assertProcString += "END IF;\n";
		assertProcedure.setProcedure(assertProcString);
		
		TestHarness harness = new TestHarness(domain);
		SimulatedTestScenario simScenario = harness.getScenarios().iterator().next();
		SimulatedTestVector simVector = simScenario.getSimulatedVectors().iterator().next();
		
		Simulator simulator = initSimulatorForTests(simVector);
		
		simVector.executeTestSetupVector();
		simVector.executeNextStateProcedure();
		
		resetSimulator(simulator, simVector);
		
		simVector.executeAssertionVector();
		
		Assert.assertEquals(false, simVector.hasAssertionFailed());
	}
	
	public void test_can_add_1_to_attribute_and_check_in_assertion() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException, NameNotFoundException
	{
		String proc1 = "self.Value = self.Value + 1;\n";
		String proc2 = "self.Value = self.Value + 10;\n";
		EntityDomain domain = SimulatedGenerateEventTests.get2StateDomain(proc1, proc2);
		
		TestScenario scenario = new TestScenario();
		domain.addScenario(scenario);
		TestVector vector = new TestVector();
		scenario.addVector(vector);
		
		TestVectorProcedure procedure = new InitialTestVectorProcedure();
		procedure.setVector(vector);
		String setupProcString = "";
		setupProcString += "CREATE class1 FROM Class1;\n";
		setupProcString += "GENERATE EventOne() TO class1;\n";
		setupProcString += "GENERATE Event1() TO class1;\n";
		setupProcString += "GENERATE Event1() TO class1;\n";
		procedure.setProcedure(setupProcString);
		
		TestVectorProcedure assertProcedure = new AssertionTestVectorProcedure();
		assertProcedure.setVector(vector);
		String assertProcString = "SELECT ANY c1 FROM INSTANCES OF Class1;\n";
		assertProcString += "IF c1.Value != 11 THEN\n";
		assertProcString += "	FAIL \"value should have been 11 but was \" + c1.Value;\n";
		assertProcString += "END IF;\n";
		assertProcedure.setProcedure(assertProcString);
		
		TestHarness harness = new TestHarness(domain);
		SimulatedTestScenario simScenario = harness.getScenarios().iterator().next();
		SimulatedTestVector simVector = simScenario.getSimulatedVectors().iterator().next();
		
		Simulator simulator = initSimulatorForTests(simVector);
		
		simVector.executeTestSetupVector();
		simVector.executeNextStateProcedure();
		simVector.executeNextStateProcedure();
		simVector.executeNextStateProcedure();
		
		resetSimulator(simulator, simVector);
		
		simVector.executeAssertionVector();
		
		Assert.assertEquals(true, simVector.hasAssertionFailed());
		Assert.assertEquals("value should have been 11 but was 21", simVector.getAssertionFail());
	}
	
}
