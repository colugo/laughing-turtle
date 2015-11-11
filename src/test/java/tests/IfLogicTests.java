package test.java.tests;

import javax.naming.NameAlreadyBoundException;

import test.java.helper.DomainBus;
import test.java.helper.TestHelper;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.validation.error.syntax.EntityDatatypesCannotBeCompared;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;
import junit.framework.Assert;
import junit.framework.TestCase;

//the majority of the code here has been tested via LogicExpressionTest
public class IfLogicTests extends TestCase {
	
	public IfLogicTests(String name) {
		super(name);
	}
	
	public void test_if_logic_fails_with_unknown_token() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "IF FRED.License == \"BUS001\" THEN\n";
		proc += "END IF;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Vehicle");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
	}
	
	public void test_if_logic_passes_with_boolean_literals() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "CREATE personal FROM Personal;\n";
		proc += "IF personal.HasToilet == true THEN\n";
		proc += "END IF;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Vehicle");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
	}
	
	public void test_if_logic_passes_with_integer_literals() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "CREATE car FROM Car;\n";
		proc += "IF car.NumberDoors >=2 THEN\n";
		proc += "END IF;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Vehicle");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
	}
	
	public void test_if_logic_passes_with_float_literals() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "CREATE personal FROM Personal;\n";
		proc += "IF personal.LeaseAmount >=2.3 THEN\n";
		proc += "END IF;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Vehicle");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
	}
	
	public void test_if_logic_passes_with_string_literals() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "CREATE personal FROM Personal;\n";
		proc += "IF personal.License == \"BUS001\" THEN\n";
		proc += "END IF;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Vehicle");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(procedure.validate());
	}
	
	public void test_if_logic_fails_with_integer_and_string_literals() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		String proc = "";
		proc += "CREATE personal FROM Personal;\n";
		proc += "IF personal.License == 1 THEN\n";
		proc += "END IF;\n";

		EntityDomain busd = DomainBus.getBusDomain();
		EntityClass user = busd.getEntityClassWithName("Vehicle");
		EntityState state = new EntityState("Initial");
		user.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		procedure.setProcedure(proc);

		Assert.assertTrue(!procedure.validate());
		Assert.assertTrue(TestHelper.checkValidationResultsForAnErrorOfType(procedure, EntityDatatypesCannotBeCompared.class));
	}
}

