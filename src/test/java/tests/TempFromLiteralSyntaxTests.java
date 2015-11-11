package test.java.tests;

import test.java.helper.DomainTTD;
import test.java.helper.TestHelper;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;

public class TempFromLiteralSyntaxTests extends TestCase {

	public TempFromLiteralSyntaxTests(String name) {
		super(name);
	}
	

	public void test_temp_variable_from_literal_integer() throws InvalidActionLanguageSyntaxException
	{
		String proc = "HUGE = 15000;";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		
	}
	
	public void test_temp_variable_from_negative_literal_integer() throws InvalidActionLanguageSyntaxException
	{
		String proc = "HUGE = (-15000);";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
				
		Assert.assertTrue(procedure.validate());
		
	}
	
	public void test_temp_variable_from_literal_float() throws InvalidActionLanguageSyntaxException
	{
		String proc = "HUGE = 15000.0;";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	}
	
	public void test_temp_variable_from_literal_float_with_brackets() throws InvalidActionLanguageSyntaxException
	{
		String proc = "HUGE = (15000.0);";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	}
	
	public void test_temp_variable_from_negative_literal_float() throws InvalidActionLanguageSyntaxException
	{
		String proc = "HUGE = -15000.0;";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		TestHelper.printValidationErrors(procedure);
		Assert.assertTrue(procedure.validate());
	}
	
	public void test_temp_variable_from_negative_literal_float_with_brackets() throws InvalidActionLanguageSyntaxException
	{
		String proc = "HUGE = (-15000.0);";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
	}

	public void test_temp_variable_from_literal_string() throws InvalidActionLanguageSyntaxException
	{
		String proc = "HUGE = \"15000.0\";";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
		
	}
	
	public void test_temp_variable_from_literal_bool() throws InvalidActionLanguageSyntaxException
	{
		String proc = "HUGE = false;";
		
		EntityDomain ttdDomain = DomainTTD.getTTDDomain();
		EntityClass user = ttdDomain.getEntityClassWithName("User");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);
		
		Assert.assertTrue(procedure.validate());
	}
	
	
}

