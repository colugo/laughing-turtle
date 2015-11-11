package test.java.tests;

import test.java.helper.DomainTTD;
import test.java.helper.TestHelper;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CanNotChangeIteratingInstanceSetValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CodeBlockMismatchValidationError;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.DuplicateIdentifierFoundValidationError;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;

public class SyntaxCodeBlockTests extends TestCase {

	public SyntaxCodeBlockTests(String name) {
		super(name);
	}
	
	public void test_for_loop_must_have_closing_end_for() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "SELECT MANY tasks RELATED BY self->R1;\n";
		proc += "FOR task IN tasks DO\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CodeBlockMismatchValidationError.class));
	}
	
	public void test_second_for_loop_must_have_closing_end_for() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "SELECT MANY tasks RELATED BY self->R1;\n";
		proc += "FOR task IN tasks DO\n";
		proc += "END FOR;\n";
		proc += "FOR task2 IN tasks DO\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CodeBlockMismatchValidationError.class));
	}
	
	public void test_end_for_must_close_a_for() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "END FOR;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CodeBlockMismatchValidationError.class));
	}
	
	public void test_end_if_must_close_an_if() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "END IF;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CodeBlockMismatchValidationError.class));
	}
	
	public void test_cant_have_double_end_for() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "END FOR;\n";
		proc += "END FOR;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CodeBlockMismatchValidationError.class));
	}
	
	public void test_valid_for_loop() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "SELECT MANY tasks RELATED BY self->R1;\n";
		proc += "FOR task IN tasks DO\n";
		proc += "END FOR;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
	
	public void test_cant_clobber_iterating_instance_set_valid_for_loop() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "SELECT MANY tasks RELATED BY self->R1;\n";
		proc += "FOR task IN tasks DO\n";
		proc += "	SELECT MANY tasks RELATED BY self->R1;\n";
		proc += "END FOR;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CanNotChangeIteratingInstanceSetValidationError.class));
		Assert.assertEquals(1,TestHelper.countValidationErrors(procedure));
	}
	
	
	public void test_cant_reuse_loop_var_in_nested_for_loop() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "SELECT MANY tasks RELATED BY self->R1;\n";
		proc += "FOR task IN tasks DO\n";
		proc += "	FOR task IN tasks DO\n";
		proc += "	END FOR;\n";
		proc += "END FOR;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
	}
	
	
	public void test_valid_nested_for_loop() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "SELECT MANY tasks RELATED BY self->R1;\n";
		proc += "FOR task IN tasks DO\n";
		proc += "	FOR task2 IN tasks DO\n";
		proc += "	END FOR;\n";
		proc += "END FOR;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}

	public void test_for_loop_cannot_end_with_end_if() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "SELECT MANY tasks RELATED BY self->R1;\n";
		proc += "FOR task IN tasks DO\n";
		proc += "END IF;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CodeBlockMismatchValidationError.class));
	}
	
	
	public void test_open_if_must_be_closed_by_close_if() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "SELECT MANY tasks RELATED BY self->R1;\n";
		proc += "IF EMPTY tasks THEN\n";
		proc += "END FOR;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CodeBlockMismatchValidationError.class));
	}
	
	public void test_valid_if_empty() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "SELECT MANY tasks RELATED BY self->R1;\n";
		proc += "IF EMPTY tasks THEN\n";
		proc += "END IF;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	}
	
	public void test_valid_if_not_empty() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "SELECT MANY tasks RELATED BY self->R1;\n";
		proc += "IF NOT EMPTY tasks THEN\n";
		proc += "END IF;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	}
	
	public void test_valid_nested_if_empty() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "SELECT MANY tasks RELATED BY self->R1;\n";
		proc += "IF EMPTY tasks THEN\n";
		proc += "	IF EMPTY tasks THEN\n";
		proc += "	END IF;\n";
		proc += "END IF;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	}
	
	
	public void test_valid_if_empty_else() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "SELECT MANY tasks RELATED BY self->R1;\n";
		proc += "IF EMPTY tasks THEN\n";
		proc += "ELSE\n";
		proc += "END IF;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	}
	
	public void test_valid_if_not_empty_else() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "SELECT MANY tasks RELATED BY self->R1;\n";
		proc += "IF NOT EMPTY tasks THEN\n";
		proc += "ELSE\n";
		proc += "END IF;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	}

	public void test_for_loop_cant_have_else() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "SELECT MANY tasks RELATED BY self->R1;\n";
		proc += "FOR task IN tasks DO\n";
		proc += "ELSE\n";
		proc += "END FOR;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(3, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CodeBlockMismatchValidationError.class));		
	}

	public void test_valid_nested_if_empty_else() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "SELECT MANY tasks RELATED BY self->R1;\n";
		proc += "IF EMPTY tasks THEN\n";
		proc += "	IF EMPTY tasks THEN\n";
		proc += "		SELECT MANY t RELATED BY self->R1;\n";
		proc += "	ELSE\n";
		proc += "		SELECT ANY t RELATED BY self->R1;\n";
		proc += "	END IF;\n";
		proc += "END IF;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		TestHelper.printValidationErrors(procedure);
		
		Assert.assertTrue(procedure.validate());
	}
	
	public void test_cant_declare_nested_instance_with_same_name() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user1 FROM User;\n";
		proc += "IF 5 > 6 THEN\n";
		proc += "	CREATE user1 FROM User;\n";
		proc += "END IF;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(2, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, DuplicateIdentifierFoundValidationError.class));
		
	}
	
	public void test_negative_cant_declare_nested_instance_with_same_name() throws InvalidActionLanguageSyntaxException
	{
		String proc = "";
		proc += "CREATE user1 FROM User;\n";
		proc += "IF 5 > 6 THEN\n";
		proc += "	CREATE user2 FROM User;\n";
		proc += "END IF;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	}

}

