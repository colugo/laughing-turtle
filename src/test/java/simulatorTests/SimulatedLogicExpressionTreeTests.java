package test.java.simulatorTests;

import java.util.Random;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.LogicExpressionTree;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedLogicExpressionTree;

public class SimulatedLogicExpressionTreeTests extends TestCase {

	public SimulatedLogicExpressionTreeTests(String name) {
		super(name);
	}
	
	public void test_logic_expression_tree_can_evaluate1_plus2() throws Exception
	{
		String e = "1+2";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(3.0,slet.evaluateExpression());
	}
	
	public void test_logic_expression_tree_can_evaluate_minus1() throws Exception
	{
		String e = "(-1)";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(-1.0,slet.evaluateExpression());
	}
	
	public void test_logic_expression_tree_can_evaluate_minus1_without_brackets() throws Exception
	{
		String e = "-1";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(-1.0,slet.evaluateExpression());
	}
	
	public void test_logic_expression_tree_can_evaluate_negative_results_in_expression() throws Exception
	{
		String e = "1-2-3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(-4.0,slet.evaluateExpression());
	}
	
	public void test_logic_expression_tree_can_evaluate_negative_literals_in_expression() throws Exception
	{
		String e = "1 - -1 - -2 - -3 - 5 - 2";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(0.0,slet.evaluateExpression());
	}
	
	
	public void test_logic_expression_tree_can_evaluate1_plus2_plus3() throws Exception
	{
		String e = "1+2+3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(6.0,slet.evaluateExpression());
	}
	
	public void test_logic_expression_tree_can_evaluate1_plus2_times3() throws Exception
	{
		String e = "1+2*3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(7.0,slet.evaluateExpression());
	}
	
	public void test_logic_expression_tree_can_evaluate3_plus4_times5() throws Exception
	{
		String e = " 3 + 4 * 5";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(23.0,slet.evaluateExpression());
	}
	
	public void test_logic_expression_tree_can_evaluate4_times5_plus3() throws Exception
	{
		String e = "4 * 5 + 3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(23.0,slet.evaluateExpression());
	}
	
	public void test_logic_expression_tree_can_find_contained_bracteted_expression() throws Exception
	{
		String e = "(1 +2)*3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals("1 +2", slet.findContainedBracketedExpression(e));
	}
	
	public void test_logic_expression_tree_can_find_more_complex_contained_bracteted_expression() throws Exception
	{
		String e = "(1+(2*3))*3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals("2*3", slet.findContainedBracketedExpression(e));
	}
	
	public void test_logic_expression_tree_can_evaluate_b1_plus2_b_times3() throws Exception
	{
		String e = "(1+2)*3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(9.0,slet.evaluateExpression());
	}
	
	public void test_logic_expression_tree_can_evaluate_complex_bracketed_expression() throws Exception
	{
		String e = "( 1 + 2 ) * (3 + 1)";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(12.0,slet.evaluateExpression());
	}
	
	public void test_logic_expression_tree_can_evaluate_minus() throws Exception
	{
		String e = "4-1";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(3.0,slet.evaluateExpression());
	}
	
	public void test_logic_expression_tree_can_evaluate_divide() throws Exception
	{
		String e = "10\\4";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(2.5d,slet.evaluateExpression());
	}
	
	public void test_logic_expression_tree_can_evaluate_float_divide() throws Exception
	{
		String e = "10.4\\4";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(2.6,slet.evaluateExpression());
	}

	public void test_logic_expression_tree_can_evaluate1_plus_hello() throws Exception
	{
		String e = "1+\"Hello\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals("1Hello",slet.evaluateExpression());
	}
	
	
	public void test_logic_expression_tree_can_evaluate_brackets_have_no_effect() throws Exception
	{
		String e = "(1+\"Hello)\")";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals("1Hello)",slet.evaluateExpression());
	}
	
	public void test_logic_expression_tree_can_have_string_with_plus() throws Exception
	{
		String e = "1+\"Hel+lo)\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals("1Hel+lo)",slet.evaluateExpression());
	}
	
	public void test_logic_expression_tree_can_have_string_with_minus() throws Exception
	{
		String e = "1+\"Hel-lo)\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals("1Hel-lo)",slet.evaluateExpression());
	}
	
	public void test_logic_expression_tree_can_have_string_with_times() throws Exception
	{
		String e = "1+\"Hel*lo)\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals("1Hel*lo)",slet.evaluateExpression());
	}
	
	public void test_logic_expression_tree_can_have_string_with_divide() throws Exception
	{
		String e = "1+\"Hel/lo)\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals("1Hel/lo)",slet.evaluateExpression());
	}
	
	public void test_logic_expression_tree_can_have_string_with_divide2() throws Exception
	{
		String e = "1+\"Hel\\lo)\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals("1Hel\\lo)",slet.evaluateExpression());
	}

	public void test_logic_expression_tree_can_evaluate_true_and_true() throws Exception
	{
		String e = "true AND TrUe";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true && true,slet.evaluateExpression());
	}
	
	public void test_logic_expression_tree_can_evaluate_true_and_false() throws Exception
	{
		String e = "True AND false";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true && false,slet.evaluateExpression());
	}
	
	@SuppressWarnings("unused")
	public void test_logic_expression_tree_can_evaluate_falsee_and_false() throws Exception
	{
		String e = "False AND false";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(false && false,slet.evaluateExpression());
	}
	
	@SuppressWarnings("unused")
	public void test_logic_expression_tree_can_evaluate_true_or_true() throws Exception
	{
		String e = "true OR TrUe";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true || true,slet.evaluateExpression());
	}
	
	@SuppressWarnings("unused")
	public void test_logic_expression_tree_can_evaluate_true_or_false() throws Exception
	{
		String e = "True OR false";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true || false,slet.evaluateExpression());
	}
	
	public void test_logic_expression_tree_can_evaluate_false_or_false() throws Exception
	{
		String e = "False OR false";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(false || false,slet.evaluateExpression());
	}
	
	@SuppressWarnings("unused")
	public void test_logic_expression_tree_can_evaluate_complex_boolean_expressoin() throws Exception
	{
		String e = "false OR true AND false AND true AND false OR true OR false OR false OR true OR false OR false";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(false||true&&false&&true&&false||true||false||false||true||false||false,slet.evaluateExpression());
	}
	
	public String convertExpressionToJava(String expression)
	{
		String eval = expression.replace("AND", "&&").replace("OR","||");
		return eval;
	}
	
	public String getRandomLogicExpression()
	{
		Random rand = new Random();
		String expression = "";
		for(int i = 0; i < 10; i ++)
		{
			boolean trueOrFalse = rand.nextBoolean();
			boolean orOrAnd = rand.nextBoolean();
			
			if(trueOrFalse)
			{
				expression += "true";
			}
			else
			{
				expression += "false";
			}
			
			if(orOrAnd)
			{
				expression += "OR";
			}
			else
			{
				expression += "AND";
			}
		}
		boolean trueOrFalse = rand.nextBoolean();
		if(trueOrFalse)
		{
			expression += "true";
		}
		else
		{
			expression += "false";
		}
		return expression;
	}
	
	///////////////// == /////////////////////////////
	public void test_equality_operator() throws Exception
	{
		String e = "3 == 3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	
	public void test_equality_operator_not() throws Exception
	{
		String e = "3 == 4";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(false,slet.evaluateExpression());
	}
	
	public void test_equality_operator_with_booleans() throws Exception
	{
		String e = "true == True";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	
	public void test_equality_operator_with_booleans_false() throws Exception
	{
		String e = "true == false";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(false,slet.evaluateExpression());
	}
	
	public void test_equality_operator_with_arithmetic() throws Exception
	{
		String e = "(3 - 1) * 1 + 1  == 1 + 2";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	
	
	public void test_equality_operator_with_strings() throws Exception
	{
		String e = "\"fred\" == \"fred\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	////////////////////////// == ///////////////////////////
	
	///////////////// != /////////////////////////////
	public void test_not_equality_operator() throws Exception
	{
		String e = "3 != 3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(false,slet.evaluateExpression());
	}
	
	public void test_not_equality_operator_not() throws Exception
	{
		String e = "3 != 4";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	
	public void test_not_equality_operator_with_booleans() throws Exception
	{
		String e = "true != True";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(false,slet.evaluateExpression());
	}
	
	public void test_not_equality_operator_with_booleans_false() throws Exception
	{
		String e = "true != false";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	
	public void test_not_equality_operator_with_arithmetic() throws Exception
	{
		String e = "(3 - 1) * 1 + 1  != 1 + 2";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(false,slet.evaluateExpression());
	}
	
	
	public void test_not_equality_operator_with_strings() throws Exception
	{
		String e = "\"fred\" != \"fred\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(false,slet.evaluateExpression());
	}
	///////////////// != /////////////////////////////

	///////////////// < /////////////////////////////
	public void test_fail_less_then_operator() throws Exception
	{
		String e = "3 < 3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(false,slet.evaluateExpression());
	}
	
	public void test_pass_less_then_operator() throws Exception
	{
		String e = "2 < 3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	
	public void test_pass_less_then_operator_string() throws Exception
	{
		String e = "\"a\" < \"b\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	
	public void test_fail_less_then_operator_string() throws Exception
	{
		String e = "\"b\" < \"a\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(false,slet.evaluateExpression());
	}
	
	///////////////// < /////////////////////////////
	
	///////////////// > /////////////////////////////
	public void test_fail_greater_then_operator() throws Exception
	{
		String e = "3 > 3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(false,slet.evaluateExpression());
	}
	
	public void test_pass_greater_then_operator() throws Exception
	{
		String e = "3 > 2";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	
	public void test_pass_greater_then_operator_string() throws Exception
	{
		String e = "\"a\" > \"b\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(false,slet.evaluateExpression());
	}
	
	public void test_fail_greater_then_operator_string() throws Exception
	{
		String e = "\"b\" > \"a\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	
	///////////////// > /////////////////////////////
	
	///////////////// <= /////////////////////////////
	public void test_fail_less_than_or_equal_to_operator() throws Exception
	{
		String e = "3 <= 3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	
	public void test_pass_less_than_or_equal_to_operator() throws Exception
	{
		String e = "2 <= 3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	
	public void test_fail2_less_than_or_equal_to_operator() throws Exception
	{
		String e = "3 <= 2";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(false,slet.evaluateExpression());
	}
	
	public void test_pass_less_than_or_equal_to_operator_string() throws Exception
	{
		String e = "\"a\" <= \"b\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	
	public void test_pass2_less_than_or_equal_to_operator_string() throws Exception
	{
		String e = "\"a\" <= \"a\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	
	public void test_fail_less_than_or_equal_to_operator_string() throws Exception
	{
		String e = "\"b\" <= \"a\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(false,slet.evaluateExpression());
	}
	
	///////////////// <= /////////////////////////////
	
	///////////////// >= /////////////////////////////
	public void test_fail_greater_than_or_equal_to_operator() throws Exception
	{
		String e = "3 >= 3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	
	public void test_pass_greater_than_or_equal_to_operator() throws Exception
	{
		String e = "2 >= 3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(false,slet.evaluateExpression());
	}
	
	public void test_fail2_greater_than_or_equal_to_operator() throws Exception
	{
		String e = "3 >= 2";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	
	public void test_pass_greater_than_or_equal_to_operator_string() throws Exception
	{
		String e = "\"a\" >= \"b\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(false,slet.evaluateExpression());
	}
	
	public void test_pass2_greater_than_or_equal_to_operator_string() throws Exception
	{
		String e = "\"a\" >= \"a\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	
	public void test_fail_greater_than_or_equal_to_operator_string() throws Exception
	{
		String e = "\"b\" >= \"a\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	
	///////////////// >= /////////////////////////////
	
	
	public void test_complex_expression1() throws Exception
	{
		String e = "\"b\" >= \"a\" AND 2 > 1";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	
	public void test_complex_expression2() throws Exception
	{
		String e = "\"b\" >= \"a\" AND 2 > 2";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(false,slet.evaluateExpression());
	}
	
	public void test_not_true() throws Exception
	{
		String e = "NOT(True)";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(false,slet.evaluateExpression());
	}
	
	public void test_not_false() throws Exception
	{
		String e = "NOT(false)";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	
	public void test_not_complex_expression1() throws Exception
	{
		String e = "NOT ( \"b\" >= \"a\" AND 2 > 1 )";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(false,slet.evaluateExpression());
	}
	
	public void test_not_complex_expression2() throws Exception
	{
		String e = "NOT ( \"b\" >= \"a\" AND 2 > 2)";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	
	public void test_not_not_true() throws Exception
	{
		String e = "NOT(NOT(True))";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(true,slet.evaluateExpression());
	}
	
	public void test_not_not_false() throws Exception
	{
		String e = "NOT(NOT(false))";
		LogicExpressionTree let = new LogicExpressionTree(e);
		SimulatedLogicExpressionTree slet = new SimulatedLogicExpressionTree(let);
		Assert.assertEquals(false,slet.evaluateExpression());
	}
	
}

