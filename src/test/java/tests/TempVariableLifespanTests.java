package test.java.tests;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.OperationNotSupportedException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.CannotInterpretExpressionNodeException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.ILogicNode;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_TempExpression;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.LogicExpressionTree;
import main.java.avii.editor.metamodel.actionLanguage.tempLifespan.TempVariableLifespanRange;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityState;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;

public class TempVariableLifespanTests extends TestCase {

	public TempVariableLifespanTests(String name) {
		super(name);
	}

	public void test_can_identify_temp_syntax_type_on_declaring_line() throws NameNotFoundException {
		TempVariableLifespanRange range = new TempVariableLifespanRange();
		range.declare(3, new Syntax_TempExpression());
		Assert.assertEquals(Syntax_TempExpression.class, range.identify(3).getClass());
	}

	public void test_can_identify_temp_syntax_type_on_given_line() throws NameNotFoundException {
		TempVariableLifespanRange range = new TempVariableLifespanRange();
		range.declare(3, new Syntax_TempExpression());
		Assert.assertEquals(Syntax_TempExpression.class, range.identify(6).getClass());
	}
	
	public void test_empty_temp_lifespan_range_fails() throws NameNotFoundException {
		TempVariableLifespanRange range = new TempVariableLifespanRange();
		try
		{
			Assert.assertEquals(Syntax_TempExpression.class, range.identify(6).getClass());
			fail("Sould not get here");
		}
		catch(Exception e)
		{}
	}
	
	public void test_adding_multiple_declarations_to_the_same_line_fails() throws NameNotFoundException {
		TempVariableLifespanRange range = new TempVariableLifespanRange();
		range.declare(3, new Syntax_TempExpression());
		range.declare(3, new Syntax_TempExpression());
		try {
			range.identify(2);
			fail();
		} catch (Exception e) {
		}	}
	
	
	public void test_can_not_identify_temp_syntax_type_on_earlier_line() throws NameNotFoundException {
		TempVariableLifespanRange range = new TempVariableLifespanRange();
		range.declare(3, new Syntax_TempExpression());
		try {
			range.identify(2);
			fail();
		} catch (Exception e) {
		}
	}

	public void test_can_add_multiple_lines_for_temp_range() throws NameNotFoundException {
		TempVariableLifespanRange range = new TempVariableLifespanRange();
		range.declare(3, new Syntax_TempExpression());
		range.declare(6, new Syntax_TempExpression());
		Assert.assertEquals(Syntax_TempExpression.class, range.identify(4).getClass());
		Assert.assertEquals(Syntax_TempExpression.class, range.identify(7).getClass());
	}

	public void test_can_identify_syntax_line_of_temp_variable_after_initial_set() throws InvalidActionLanguageSyntaxException, NameNotFoundException,
			OperationNotSupportedException {
		String proc = "";
		proc += "TEMP = 4;\n";

		EntityDomain domain = new EntityDomain("domain");
		EntityClass dummy = new EntityClass("Person");
		domain.addClass(dummy);

		EntityState state = new EntityState("Initial");
		dummy.addState(state);
		state.getProcedure().setProcedure(proc);

		Assert.assertNotNull(state.getProcedure().identifyTempDatatype("TEMP", 1));
		Assert.assertEquals(Syntax_TempExpression.class, state.getProcedure().identifyTempDatatype("TEMP", 1).getClass());
	}

	public void test_can_identify_datatype_of_temp_variable_after_initial_set() throws InvalidActionLanguageSyntaxException, NameNotFoundException,
			OperationNotSupportedException, CannotInterpretExpressionNodeException {
		String proc = "";
		proc += "TEMP = 4;\n";

		EntityDomain domain = new EntityDomain("domain");
		EntityClass dummy = new EntityClass("Person");
		domain.addClass(dummy);

		EntityState state = new EntityState("Initial");
		dummy.addState(state);
		state.getProcedure().setProcedure(proc);

		Assert.assertNotNull(state.getProcedure().identifyTempDatatype("TEMP", 1));
		Assert.assertEquals(Syntax_TempExpression.class, state.getProcedure().identifyTempDatatype("TEMP", 1).getClass());
		Syntax_TempExpression tempSyntax = (Syntax_TempExpression) state.getProcedure().identifyTempDatatype("TEMP", 1);
		
		LogicExpressionTree logic = tempSyntax.get_Logic();
		ILogicNode rootNode = logic.getRootNode();
		Assert.assertEquals("4", rootNode.getTokenValue());
		Assert.assertEquals(IntegerEntityDatatype.class, rootNode.getDatatype(null).getClass());
	}
	
	
	public void test_can_identify_datatype_when_instance_used_in_datatype_initialisation_is_deleted() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, NameNotFoundException, CannotInterpretExpressionNodeException
	{
		String proc = "";
		proc += "CREATE p FROM Person;\n";
		proc += "TEMP = 4 + p.Age;\n";
		proc += "DELETE p;\n";
		proc += "TEMP2 = TEMP;\n";

		EntityDomain domain = new EntityDomain("domain");
		EntityClass dummy = new EntityClass("Person");
		EntityAttribute dummyAttribute = new EntityAttribute("Age", IntegerEntityDatatype.getInstance());
		dummy.addAttribute(dummyAttribute);
		domain.addClass(dummy);

		EntityState state = new EntityState("Initial");
		dummy.addState(state);
		state.getProcedure().setProcedure(proc);

		Assert.assertEquals(true, state.getProcedure().validate());
	}
}

