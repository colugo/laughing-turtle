package test.java.tests;

import javax.naming.NameAlreadyBoundException;

import test.java.helper.DomainTTD;
import test.java.helper.TestHelper;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.CouldNotIdentifyInstanceSetValidationError;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;

public class IfInstanceSetTests extends TestCase {

	public IfInstanceSetTests(String name) {
		super(name);
	}
	
	public void test_i_f_empty_instance_set_fails_if_instance_set_cannot_be_identified() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "IF EMPTY cantFind THEN\n";
		proc += "END IF;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceSetValidationError.class));
	}
	
	public void test_i_f_empty_instance_set_fails_if_instance_set_is_not_a_set() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "CREATE notASet FROM User;\n";
		proc += "IF EMPTY notASet THEN\n";
		proc += "END IF;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	}
	
	public void test_i_f_empty_instance_set_fails_if_instance_set_is_from_an_any_select() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "SELECT ANY notASet RELATED BY self->R1;\n";
		proc += "IF EMPTY notASet THEN\n";
		proc += "END IF;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	}
	
	
	public void test_valid_if_empty() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
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
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}

	
	public void test_i_f_not_empty_instance_set_fails_if_instance_set_cannot_be_identified() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "IF NOT EMPTY cantFind THEN\n";
		proc += "END IF;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(!procedure.validate());
		Assert.assertEquals(1, TestHelper.countValidationErrors(procedure));
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, CouldNotIdentifyInstanceSetValidationError.class));
	}
	
	public void test_i_f_not_empty_instance_set_fails_if_instance_set_is_not_a_set() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "CREATE notASet FROM User;\n";
		proc += "IF NOT EMPTY notASet THEN\n";
		proc += "END IF;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	}
	
	public void test_i_f_not_empty_instance_set_fails_if_instance_set_is_from_an_any_select() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "SELECT ANY notASet RELATED BY self->R1;\n";
		proc += "IF NOT EMPTY notASet THEN\n";
		proc += "END IF;\n";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	}
	
	
	public void test_valid_if_not_empty() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
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
		Assert.assertEquals(0, TestHelper.countValidationErrors(procedure));
	}
	
}

