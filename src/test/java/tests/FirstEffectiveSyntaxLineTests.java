package test.java.tests;

import javax.naming.NameAlreadyBoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_TempExpression;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityProcedure;
import main.java.avii.editor.metamodel.entities.EntityState;

public class FirstEffectiveSyntaxLineTests extends TestCase {

	public FirstEffectiveSyntaxLineTests(String name) {
		super(name);
	}


	public void test_get_first_effective_line_will_succeed_for_non_comment_or_blank_line() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		EntityClass klass = new EntityClass("klass");
		EntityState state = new EntityState("state");
		klass.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		String proc = "";
		proc += "temp = 6;\n";
		procedure.setProcedure(proc);
		
		IActionLanguageSyntax firstEffectiveSyntax = procedure.getFirstEffectiveSyntax();
		Assert.assertEquals(Syntax_TempExpression.class, firstEffectiveSyntax.getClass());
	}
	
	
	public void test_get_first_effective_line_will_skip_commented_lines() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		EntityClass klass = new EntityClass("klass");
		EntityState state = new EntityState("state");
		klass.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		String proc = "";
		proc += "#comment line 1\n";
		proc += "#comment line 2\n";
		proc += "temp = 6;\n";
		procedure.setProcedure(proc);
		
		IActionLanguageSyntax firstEffectiveSyntax = procedure.getFirstEffectiveSyntax();
		Assert.assertEquals(Syntax_TempExpression.class, firstEffectiveSyntax.getClass());
	}
	
	public void test_get_first_effective_line_will_skip_blank_lines() throws InvalidActionLanguageSyntaxException, NameAlreadyBoundException
	{
		EntityClass klass = new EntityClass("klass");
		EntityState state = new EntityState("state");
		klass.addState(state);
		EntityProcedure procedure = new EntityProcedure(state);
		String proc = "";
		proc += "\n";
		proc += "\n";
		proc += "temp = 6;\n";
		procedure.setProcedure(proc);
		
		IActionLanguageSyntax firstEffectiveSyntax = procedure.getFirstEffectiveSyntax();
		Assert.assertEquals(Syntax_TempExpression.class, firstEffectiveSyntax.getClass());
	}
}

