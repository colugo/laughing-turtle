package test.java.tests;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageLineException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.CannotInterpretExpressionNodeException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.ILogicNode;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.ILogicParentNode;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSupportedSyntax;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_AttributeExpression;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_TempExpression;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.LogicExpressionTree;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.util.Text;

public class ArithmeticTests extends TestCase {

	public ArithmeticTests(String name) {
		super(name);
	}

	/*---------------------------------Attribute with literal--------------------------------------*/
	public void test_attribute_with_literal_arithmetic_plus() throws InvalidActionLanguageLineException, CannotInterpretExpressionNodeException {
		String line = "books.count = total + 1;";
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(lineSyntax instanceof Syntax_AttributeExpression);
		Syntax_AttributeExpression specificSyntax = (Syntax_AttributeExpression) lineSyntax;


		Assert.assertEquals("books", specificSyntax.get_Instance());
		Assert.assertEquals("count", specificSyntax.get_Attribute());
		LogicExpressionTree logic = specificSyntax.get_Logic();
		ILogicNode rootNode = logic.getRootNode();
		Assert.assertEquals("+", rootNode.getTokenValue());
		ILogicParentNode parentNode = (ILogicParentNode) rootNode;
		Assert.assertEquals("total", parentNode.getLeft().getTokenValue());
		Assert.assertEquals("1", parentNode.getRight().getTokenValue());

	}

	public void test_attribute_with_literal_arithmetic_minus() throws InvalidActionLanguageLineException, CannotInterpretExpressionNodeException {
		String line = "books.count = total - 1;";
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(lineSyntax instanceof Syntax_AttributeExpression);
		Syntax_AttributeExpression specificSyntax = (Syntax_AttributeExpression) lineSyntax;


		Assert.assertEquals("books", specificSyntax.get_Instance());
		Assert.assertEquals("count", specificSyntax.get_Attribute());
		LogicExpressionTree logic = specificSyntax.get_Logic();
		ILogicNode rootNode = logic.getRootNode();
		Assert.assertEquals("-", rootNode.getTokenValue());
		ILogicParentNode parentNode = (ILogicParentNode) rootNode;
		Assert.assertEquals("total", parentNode.getLeft().getTokenValue());
		Assert.assertEquals("1", parentNode.getRight().getTokenValue());
	}

	public void test_attribute_with_literal_arithmetic_times() throws InvalidActionLanguageLineException, CannotInterpretExpressionNodeException {
		String line = "books.count = total * 1;";
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(lineSyntax instanceof Syntax_AttributeExpression);
		Syntax_AttributeExpression specificSyntax = (Syntax_AttributeExpression) lineSyntax;


		Assert.assertEquals("books", specificSyntax.get_Instance());
		Assert.assertEquals("count", specificSyntax.get_Attribute());
		LogicExpressionTree logic = specificSyntax.get_Logic();
		ILogicNode rootNode = logic.getRootNode();
		Assert.assertEquals("*", rootNode.getTokenValue());
		ILogicParentNode parentNode = (ILogicParentNode) rootNode;
		Assert.assertEquals("total", parentNode.getLeft().getTokenValue());
		Assert.assertEquals("1", parentNode.getRight().getTokenValue());

	}

	public void test_attribute_with_literal_arithmetic_forward_div() throws InvalidActionLanguageLineException, CannotInterpretExpressionNodeException {
		String line = "books.count = total / 1;";
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(lineSyntax instanceof Syntax_AttributeExpression);
		Syntax_AttributeExpression specificSyntax = (Syntax_AttributeExpression) lineSyntax;


		Assert.assertEquals("books", specificSyntax.get_Instance());
		Assert.assertEquals("count", specificSyntax.get_Attribute());
		LogicExpressionTree logic = specificSyntax.get_Logic();
		ILogicNode rootNode = logic.getRootNode();
		Assert.assertEquals("/", rootNode.getTokenValue());
		ILogicParentNode parentNode = (ILogicParentNode) rootNode;
		Assert.assertEquals("total", parentNode.getLeft().getTokenValue());
		Assert.assertEquals("1", parentNode.getRight().getTokenValue());

	}

	public void test_attribute_with_literal_arithmetic_back_div() throws InvalidActionLanguageLineException, CannotInterpretExpressionNodeException {
		String line = "books.count = total \\ 1;";
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(lineSyntax instanceof Syntax_AttributeExpression);
		Syntax_AttributeExpression specificSyntax = (Syntax_AttributeExpression) lineSyntax;


		Assert.assertEquals("books", specificSyntax.get_Instance());
		Assert.assertEquals("count", specificSyntax.get_Attribute());
		LogicExpressionTree logic = specificSyntax.get_Logic();
		ILogicNode rootNode = logic.getRootNode();
		Assert.assertEquals("\\", rootNode.getTokenValue());
		ILogicParentNode parentNode = (ILogicParentNode) rootNode;
		Assert.assertEquals("total", parentNode.getLeft().getTokenValue());
		Assert.assertEquals("1", parentNode.getRight().getTokenValue());

	}
	/*---------------------------------/Attribute with literal--------------------------------------*/

	/*---------------------------------Attribute with literal--------------------------------------*/
	public void test_attribute_with_attribute_arithmetic_plus() throws InvalidActionLanguageLineException, CannotInterpretExpressionNodeException {
		String line = "books.count = total.count + 1;";
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(lineSyntax instanceof Syntax_AttributeExpression);
		Syntax_AttributeExpression specificSyntax = (Syntax_AttributeExpression) lineSyntax;


		Assert.assertEquals("books", specificSyntax.get_Instance());
		Assert.assertEquals("count", specificSyntax.get_Attribute());
		LogicExpressionTree logic = specificSyntax.get_Logic();
		ILogicNode rootNode = logic.getRootNode();
		Assert.assertEquals("+", rootNode.getTokenValue());
		ILogicParentNode parentNode = (ILogicParentNode) rootNode;
		Assert.assertEquals("total.count", parentNode.getLeft().getTokenValue());
		Assert.assertEquals("1", parentNode.getRight().getTokenValue());
	}

	public void test_attribute_with_attribute_and_attribute_arithmetic_plus() throws InvalidActionLanguageLineException, CannotInterpretExpressionNodeException {
		String line = "books.count = total.count + selected.amount;";
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(lineSyntax instanceof Syntax_AttributeExpression);
		Syntax_AttributeExpression specificSyntax = (Syntax_AttributeExpression) lineSyntax;


		Assert.assertEquals("books", specificSyntax.get_Instance());
		Assert.assertEquals("count", specificSyntax.get_Attribute());
		LogicExpressionTree logic = specificSyntax.get_Logic();
		ILogicNode rootNode = logic.getRootNode();
		Assert.assertEquals("+", rootNode.getTokenValue());
		ILogicParentNode parentNode = (ILogicParentNode) rootNode;
		Assert.assertEquals("total.count", parentNode.getLeft().getTokenValue());
		Assert.assertEquals("selected.amount", parentNode.getRight().getTokenValue());
	}
	
	public void test_temp_arithmetic_with_attribute_plus() throws InvalidActionLanguageLineException, CannotInterpretExpressionNodeException
	{
		String line = "total = books.count + 1;";
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(lineSyntax instanceof Syntax_TempExpression);
		Syntax_TempExpression specificSyntax = (Syntax_TempExpression) lineSyntax;

		Assert.assertEquals("total", specificSyntax.getTempName());
		LogicExpressionTree logic = specificSyntax.get_Logic();
		ILogicNode rootNode = logic.getRootNode();
		ILogicParentNode parentNode = (ILogicParentNode) rootNode;
		Assert.assertEquals("+", rootNode.getTokenValue());
		
		Assert.assertEquals("books.count", parentNode.getLeft().getTokenValue());
		Assert.assertEquals("1", parentNode.getRight().getTokenValue());
	}
	
	
	public void test_temp_arithmetic_with_temp_plus() throws InvalidActionLanguageLineException, CannotInterpretExpressionNodeException
	{
		String line = "total = huge + 1;";
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(lineSyntax instanceof Syntax_TempExpression);
		Syntax_TempExpression specificSyntax = (Syntax_TempExpression) lineSyntax;

		Assert.assertEquals("total", specificSyntax.getTempName());
		LogicExpressionTree logic = specificSyntax.get_Logic();
		ILogicNode rootNode = logic.getRootNode();
		ILogicParentNode parentNode = (ILogicParentNode) rootNode;
		Assert.assertEquals("+", rootNode.getTokenValue());
		
		Assert.assertEquals("huge", parentNode.getLeft().getTokenValue());
		Assert.assertEquals("1", parentNode.getRight().getTokenValue());
	}
	
	public void test_temp_arithmetic_with_temp_divide() throws InvalidActionLanguageLineException, CannotInterpretExpressionNodeException
	{
		String line = "serial = heat.Serial \\ 3 + \"fred\";\n";
		IActionLanguageSyntax lineSyntax = ActionLanguageSupportedSyntax.getSyntaxForLine(line);
		Assert.assertTrue(lineSyntax instanceof Syntax_TempExpression);
		Syntax_TempExpression specificSyntax = (Syntax_TempExpression) lineSyntax;

		Assert.assertEquals("serial", specificSyntax.getTempName());
		LogicExpressionTree logic = specificSyntax.get_Logic();
		ILogicNode rootNode = logic.getRootNode();
		ILogicParentNode parentNode = (ILogicParentNode) rootNode;
		Assert.assertEquals("+", rootNode.getTokenValue());
		
		Assert.assertEquals("\\", parentNode.getLeft().getTokenValue());
		Assert.assertEquals("\"fred\"", parentNode.getRight().getTokenValue());
	}

	public void test_can_identify_single_int_experssion() throws CannotInterpretExpressionNodeException
	{
		String e = "5";
		LogicExpressionTree logic = new LogicExpressionTree(e);
		ILogicNode rootNode = logic.getRootNode();
		Assert.assertEquals("5", rootNode.getTokenValue());
		Assert.assertEquals(true, rootNode.getDatatype(null) instanceof IntegerEntityDatatype);
	}
	
	public void test_can_replace_negative_numbers_with_brackets_but_leave_minuses()
	{
		Assert.assertEquals("5", Text.getNumberFromString("5"));
		Assert.assertEquals("5", Text.getNumberFromString("5a"));
		Assert.assertEquals("-5", Text.getNumberFromString("-5"));
		Assert.assertEquals("-5", Text.getNumberFromString("-5a"));
		Assert.assertEquals("5.0", Text.getNumberFromString("5.0"));
		Assert.assertEquals("5.0", Text.getNumberFromString("5.0a"));
		Assert.assertEquals("-5.0", Text.getNumberFromString("-5.0"));
		Assert.assertEquals("-5.0", Text.getNumberFromString("-5.0a"));
		
		Assert.assertEquals("(-5)", Text.wrapNegativeNumbersInBrackets("-5"));
		Assert.assertEquals("4-2", Text.wrapNegativeNumbersInBrackets("4-2"));
		Assert.assertEquals("4 - 2", Text.wrapNegativeNumbersInBrackets("4 - 2"));
		Assert.assertEquals("4* - 2", Text.wrapNegativeNumbersInBrackets("4* - 2"));
		Assert.assertEquals("4*(-2)", Text.wrapNegativeNumbersInBrackets("4*-2"));
		Assert.assertEquals("(-5) + \"-5\"", Text.wrapNegativeNumbersInBrackets("-5 + \"-5\""));
		Assert.assertEquals("(-5)", Text.wrapNegativeNumbersInBrackets("(-5)"));
	}
	
	public void test_can_identify_single_negative_int_experssion() throws CannotInterpretExpressionNodeException
	{
		String e = "-5";
		LogicExpressionTree logic = new LogicExpressionTree(e);
		ILogicNode rootNode = logic.getRootNode();
		Assert.assertEquals(true, rootNode.getDatatype(null) instanceof IntegerEntityDatatype);
	}
	
}

