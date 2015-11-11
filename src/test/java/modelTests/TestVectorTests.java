package test.java.modelTests;

import test.java.helper.TestHelper;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityClassNotFoundInDomainValidationError;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.scenario.AssertionTestVectorProcedure;
import main.java.avii.scenario.InitialTestVectorProcedure;
import main.java.avii.scenario.TestScenario;
import main.java.avii.scenario.TestVector;
import main.java.avii.scenario.TestVectorClassTable;
import main.java.avii.scenario.TestVectorProcedure;

public class TestVectorTests extends TestCase {
	public TestVectorTests(String name) {
		super(name);
	}
	
	public void test_vector_has_description()
	{
		String vectorDescription = "When adding an item with a quantity higher then what is on hand, the cart should wait in a particular state";
		TestVector vector = new TestVector();
		vector.setDescription(vectorDescription);
		Assert.assertEquals(vectorDescription, vector.getDescription());
	}
	
	public void test_vector_can_be_added_to_scenario()
	{
		TestScenario scenario = new TestScenario();
		TestVector vector = new TestVector();
		scenario.addVector(vector);
		Assert.assertEquals(1, scenario.getVectors().size());
		Assert.assertEquals(true, scenario.getVectors().contains(vector));
		Assert.assertEquals(scenario, vector.getScenario());
	}
	
	public void test_vector_has_procedure()
	{
		TestVector vector = new TestVector();
		InitialTestVectorProcedure procedure = new InitialTestVectorProcedure();
		procedure.setVector(vector);
		Assert.assertEquals(procedure, vector.getInitialProcedure());
		Assert.assertEquals(vector, procedure.getVector());
	}
	
	public void test_vector_procedure_passes_standard_validation() throws InvalidActionLanguageSyntaxException
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		TestScenario scenario = new TestScenario();
		TestVector vector = new TestVector();
		EntityClass car = new EntityClass("Car");
		
		domain.addScenario(scenario);
		domain.addClass(car);
		scenario.addVector(vector);
		
		InitialTestVectorProcedure procedure = new InitialTestVectorProcedure();
		procedure.setVector(vector);
		String proc = "";
		proc += "CREATE car FROM Car;\n";
		procedure.setProcedure(proc);
		Assert.assertEquals(true, procedure.validate());
	}
	
	public void test_vector_procedure_fails_standard_validation_with_valid_errors() throws InvalidActionLanguageSyntaxException
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		TestScenario scenario = new TestScenario();
		TestVector vector = new TestVector();
		
		domain.addScenario(scenario);
		scenario.addVector(vector);
		
		InitialTestVectorProcedure procedure = new InitialTestVectorProcedure();
		procedure.setVector(vector);
		String proc = "";
		proc += "CREATE car FROM Car;\n";
		procedure.setProcedure(proc);
		Assert.assertEquals(false, procedure.validate());
		Assert.assertEquals(true, TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityClassNotFoundInDomainValidationError.class));
	}
	
	
	public void test_vector_can_add_tables_to_the_initial_state()
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Klass");
		domain.addClass(klass);
		
		TestScenario scenario = new TestScenario();
		TestVector vector = new TestVector();
		
		domain.addScenario(scenario);
		scenario.addVector(vector);
		
		TestVectorClassTable table = new TestVectorClassTable(klass);
		vector.addClassTable(table);
	}
	
	public void test_vector_can_add_tables_and_create_procedure() throws InvalidActionLanguageSyntaxException
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Klass");
		domain.addClass(klass);
		
		TestScenario scenario = new TestScenario();
		TestVector vector = new TestVector();
		
		domain.addScenario(scenario);
		scenario.addVector(vector);
		
		TestVectorClassTable table = new TestVectorClassTable(klass);
		table.createInstance();
		vector.addClassTable(table);
		
		vector.createInitialProcedureFromTables();
		TestVectorProcedure proc = vector.getInitialProcedure();
		String rawProc = proc.getRawText();
		Assert.assertEquals("CREATE Klass_001 FROM Klass;\n#HOOK FOR STATE CHANGE\n", rawProc);
		Assert.assertEquals(true, proc.validate());
	}
	
	public void test_vector_can_add_assertion_procedure() throws InvalidActionLanguageSyntaxException
	{
		EntityDomain domain = new EntityDomain("TestDomain");
		EntityClass klass = new EntityClass("Klass");
		domain.addClass(klass);
		
		TestScenario scenario = new TestScenario();
		TestVector vector = new TestVector();
		
		domain.addScenario(scenario);
		scenario.addVector(vector);
		
		TestVectorClassTable table = new TestVectorClassTable(klass);
		table.createInstance();
		vector.addClassTable(table);
		
		AssertionTestVectorProcedure procedure = new AssertionTestVectorProcedure();
		procedure.setVector(vector);
		
		String rawProc = "FAIL 5;\n";
		procedure.setProcedure(rawProc);
		Assert.assertEquals(rawProc, procedure.getRawText());
		
		Assert.assertTrue(procedure.validate());
	}
	

}
