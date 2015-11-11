package test.java.tests;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageLineException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.CannotInterpretExpressionNodeException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.ILogicNode;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSupportedSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_AttributeExpression;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_TempExpression;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.LogicExpressionTree;
import main.java.avii.editor.metamodel.entities.datatypes.BooleanEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.FloatingEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;


public class TempVariableAndAttributeAssignationTests extends TestCase {

	public TempVariableAndAttributeAssignationTests(String name) {
		super(name);
	}
	
	
	/*----------------------------------Literal Temp---------------------------------------*/
	public void test_temp_variable_from_literal_integer() throws InvalidActionLanguageLineException, CannotInterpretExpressionNodeException
	{
		String line = "HUGE = 15000;";
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(lineSyntax instanceof Syntax_TempExpression);
		Syntax_TempExpression specificSyntax = (Syntax_TempExpression) lineSyntax;
		
		LogicExpressionTree logic = specificSyntax.get_Logic();
		ILogicNode rootNode = logic.getRootNode();
		Assert.assertEquals("15000", rootNode.getTokenValue());
		Assert.assertEquals(IntegerEntityDatatype.class, rootNode.getDatatype(null).getClass());
	}
	
	public void test_temp_variable_from_literal_float() throws InvalidActionLanguageLineException, CannotInterpretExpressionNodeException
	{
		String line = "HUGE = 15000.0;";
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(lineSyntax instanceof Syntax_TempExpression);
		Syntax_TempExpression specificSyntax = (Syntax_TempExpression) lineSyntax;
		
		LogicExpressionTree logic = specificSyntax.get_Logic();
		ILogicNode rootNode = logic.getRootNode();
		Assert.assertEquals("15000.0", rootNode.getTokenValue());
		Assert.assertEquals(FloatingEntityDatatype.class, rootNode.getDatatype(null).getClass());
	}
	
	public void test_temp_variable_from_literal_string() throws InvalidActionLanguageLineException, CannotInterpretExpressionNodeException
	{
		String line = "HUGE = \"15000.0\";";
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(lineSyntax instanceof Syntax_TempExpression);
		Syntax_TempExpression specificSyntax = (Syntax_TempExpression) lineSyntax;
		
		LogicExpressionTree logic = specificSyntax.get_Logic();
		ILogicNode rootNode = logic.getRootNode();
		Assert.assertEquals("\"15000.0\"", rootNode.getTokenValue());
		Assert.assertEquals(StringEntityDatatype.class, rootNode.getDatatype(null).getClass());
	}
	
	public void test_temp_variable_from_literal_bool() throws InvalidActionLanguageLineException, CannotInterpretExpressionNodeException
	{
		String line = "HUGE = false;";
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(lineSyntax instanceof Syntax_TempExpression);
		Syntax_TempExpression specificSyntax = (Syntax_TempExpression) lineSyntax;
		
		LogicExpressionTree logic = specificSyntax.get_Logic();
		ILogicNode rootNode = logic.getRootNode();
		Assert.assertEquals("false", rootNode.getTokenValue());
		Assert.assertEquals(BooleanEntityDatatype.class, rootNode.getDatatype(null).getClass());
	}
	
	public void test_temp_variable_cannot_be_mistaken_for_a_variable_assignment() throws InvalidActionLanguageLineException
	{
		String line = "HUGE.attribute = false;";
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(!(lineSyntax instanceof Syntax_TempExpression));
	}

	/*---------------------------------/Literal Temp--------------------------------------*/
	/*---------------------------------Literal Attribute--------------------------------------*/

	public void test_attribute_assign_from_literal_integer() throws InvalidActionLanguageLineException, CannotInterpretExpressionNodeException
	{
		
		String line = "fred.name = 15000;";
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(lineSyntax instanceof Syntax_AttributeExpression);
		Syntax_AttributeExpression specificSyntax = (Syntax_AttributeExpression) lineSyntax;
		
		LogicExpressionTree logic = specificSyntax.get_Logic();
		ILogicNode rootNode = logic.getRootNode();
		Assert.assertEquals("15000", rootNode.getTokenValue());
		Assert.assertEquals(IntegerEntityDatatype.class, rootNode.getDatatype(null).getClass());

	}
	
	public void test_attribute_assign_from_literal_float() throws InvalidActionLanguageLineException, CannotInterpretExpressionNodeException
	{
		String line = "fred.name = 15000.0;";

		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(lineSyntax instanceof Syntax_AttributeExpression);
		Syntax_AttributeExpression specificSyntax = (Syntax_AttributeExpression) lineSyntax;
		LogicExpressionTree logic = specificSyntax.get_Logic();
		ILogicNode rootNode = logic.getRootNode();
		Assert.assertEquals("15000.0", rootNode.getTokenValue());
		Assert.assertEquals(FloatingEntityDatatype.class, rootNode.getDatatype(null).getClass());
	}
	
	public void test_attribute_assign_from_literal_string() throws InvalidActionLanguageLineException, CannotInterpretExpressionNodeException
	{
		String line = "fred.name = \"15000.0\";";
		
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(lineSyntax instanceof Syntax_AttributeExpression);
		Syntax_AttributeExpression specificSyntax = (Syntax_AttributeExpression) lineSyntax;
		LogicExpressionTree logic = specificSyntax.get_Logic();
		ILogicNode rootNode = logic.getRootNode();
		Assert.assertEquals("\"15000.0\"", rootNode.getTokenValue());
		Assert.assertEquals(StringEntityDatatype.class, rootNode.getDatatype(null).getClass());
	}
	
	public void test_attribute_assign_from_literal_bool() throws InvalidActionLanguageLineException, CannotInterpretExpressionNodeException
	{
		String line = "fred.name = false;";
		
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(lineSyntax instanceof Syntax_AttributeExpression);
		Syntax_AttributeExpression specificSyntax = (Syntax_AttributeExpression) lineSyntax;
		LogicExpressionTree logic = specificSyntax.get_Logic();
		ILogicNode rootNode = logic.getRootNode();
		Assert.assertEquals("false", rootNode.getTokenValue());
		Assert.assertEquals(BooleanEntityDatatype.class, rootNode.getDatatype(null).getClass());
	}
	
	public void test_attribute_assign_cannot_be_mistaken_for_a_temp_literal() throws InvalidActionLanguageLineException
	{
		String line = "fred.name = false;";
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(!(lineSyntax instanceof Syntax_TempExpression));
	}
	
	public void test_attribute_assign_cannot_be_assigned_a_attribute_assign() throws InvalidActionLanguageLineException
	{
		String line = "fred.name = SlightlySmaller;";
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(!(lineSyntax instanceof Syntax_TempExpression));
	}
	//---------------------------------/Literal Attribute--------------------------------------
	
	//---------------------------------Attribute to Attribute--------------------------------------
	public void test_attribute_assign_can_take_temp_literal() throws InvalidActionLanguageLineException
	{
		String line = "fred.name = SlightlySmaller;";
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(lineSyntax instanceof Syntax_AttributeExpression);
	}
	//---------------------------------/Attribute to Attribute--------------------------------------
	
	//---------------------------------Temp to Attribute--------------------------------------
	public void test_temp_literal_can_take_attribute() throws InvalidActionLanguageLineException
	{
		String line = "SlightlySmaller = fred.name;";
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(lineSyntax instanceof Syntax_TempExpression);
	}
	//---------------------------------/Temp to Attribute--------------------------------------
	
	
}

