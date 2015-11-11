package test.java.tests;

/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */

import java.util.ArrayList;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IEqualityOperatorToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.CannotInterpretExpressionNodeException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.EncounteredNonItemLeafNodeException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.ILogicNode;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.ILogicParentNode;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.validation.LogicTreeValidationException;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.LogicExpressionTree;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.ConstantToken;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.logicNodeProvider.LogicNodeProvider;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenAttribute;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenIdentifier;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenLiteral;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenTemp;
import main.java.avii.editor.metamodel.entities.datatypes.BooleanEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.InvalidEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;

public class LogicExpressionTest extends TestCase {

	public LogicExpressionTest(String name) {
		super(name);
	}

	public void test_logic_node_provider_returns_all_tokens() throws CannotInterpretExpressionNodeException {
		String e = "1 == 2";
		LogicNodeProvider provider = new LogicNodeProvider(e);
		Assert.assertTrue(provider.hasNext());
		Assert.assertEquals("1", provider.next().getTokenValue());
		Assert.assertEquals("==", provider.next().getTokenValue());
		Assert.assertEquals("2", provider.next().getTokenValue());
		Assert.assertEquals(false, provider.hasNext());
	}

	public void test_logic_node_provider_can_skip_tokens() throws CannotInterpretExpressionNodeException {
		String e = "1 == 2";
		LogicNodeProvider provider = new LogicNodeProvider(e);
		Assert.assertTrue(provider.hasNext());
		Assert.assertEquals("1", provider.next().getTokenValue());
		provider.advance();
		Assert.assertEquals("2", provider.next().getTokenValue());
		Assert.assertEquals(false, provider.hasNext());
	}

	public void testbuild_equality_parents_will_collapse_equality_tokens() throws CannotInterpretExpressionNodeException {
		String e = "1 == 2";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ArrayList<ILogicNode> tokens = let.collapseEqualityParents(new LogicNodeProvider(e));
		Assert.assertEquals(tokens.get(0).getTokenValue(), "==");
		Assert.assertEquals(1, tokens.size());
		ILogicParentNode firstParent = (ILogicParentNode) tokens.get(0);
		Assert.assertEquals(firstParent.getLeft().getTokenValue(), "1");
		Assert.assertEquals(firstParent.getRight().getTokenValue(), "2");
	}

	public void testbuild_equality_parents_will_collapse_equality_tokens_with_booleans() throws CannotInterpretExpressionNodeException {
		String e = "1 == 2 AND 2 == 3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ArrayList<ILogicNode> tokens = let.buildEqualityParents(new LogicNodeProvider(e));
		Assert.assertEquals(tokens.get(0).getTokenValue(), "==");
		Assert.assertEquals(tokens.get(1).getTokenValue(), " AND ");
		Assert.assertEquals(tokens.get(2).getTokenValue(), "==");
		Assert.assertEquals(3, tokens.size());
		ILogicParentNode firstParent = (ILogicParentNode) tokens.get(0);
		ILogicParentNode secondParent = (ILogicParentNode) tokens.get(2);
		Assert.assertEquals(firstParent.getLeft().getTokenValue(), "1");
		Assert.assertEquals(firstParent.getRight().getTokenValue(), "2");
		Assert.assertEquals(secondParent.getLeft().getTokenValue(), "2");
		Assert.assertEquals(secondParent.getRight().getTokenValue(), "3");
	}

	public void testbuild_equality_parents_will_collapse_equality_tokens_but_not_fail_when_operand_is_null() throws CannotInterpretExpressionNodeException {
		String e = "1 == 2 AND 2 ==";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ArrayList<ILogicNode> tokens = let.buildEqualityParents(new LogicNodeProvider(e));
		Assert.assertEquals(tokens.get(0).getTokenValue(), "==");
		Assert.assertEquals(tokens.get(1).getTokenValue(), " AND ");
		Assert.assertEquals(tokens.get(2).getTokenValue(), "==");
		Assert.assertEquals(3, tokens.size());
		ILogicParentNode firstParent = (ILogicParentNode) tokens.get(0);
		ILogicParentNode secondParent = (ILogicParentNode) tokens.get(2);
		Assert.assertEquals(firstParent.getLeft().getTokenValue(), "1");
		Assert.assertEquals(firstParent.getRight().getTokenValue(), "2");
		Assert.assertEquals(secondParent.getLeft().getTokenValue(), "2");
		Assert.assertNull(secondParent.getRight());
	}

	public void test_combine_equality_parents_will_collapse_equality_tokens() throws CannotInterpretExpressionNodeException {
		String e = "1 == 2 AND 2 == 3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ArrayList<ILogicNode> tokens = let.collapseEqualityParents(new LogicNodeProvider(e));
		Assert.assertEquals(tokens.get(0).getTokenValue(), " AND ");
		Assert.assertEquals(1, tokens.size());
		ILogicParentNode andParent = (ILogicParentNode) tokens.get(0);
		ILogicParentNode firstParent = (ILogicParentNode) andParent.getLeft();
		ILogicParentNode secondParent = (ILogicParentNode) andParent.getRight();
		Assert.assertEquals(firstParent.getLeft().getTokenValue(), "1");
		Assert.assertEquals(firstParent.getRight().getTokenValue(), "2");
		Assert.assertEquals(secondParent.getLeft().getTokenValue(), "2");
		Assert.assertEquals(secondParent.getRight().getTokenValue(), "3");
	}

	public void test_combine_equality_parents_will_collapse_equality_tokens_doesnt_fail_with_null_sub_tree() throws CannotInterpretExpressionNodeException {
		String e = "1 == 2 AND   ";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ArrayList<ILogicNode> tokens = let.collapseEqualityParents(new LogicNodeProvider(e));
		Assert.assertEquals(tokens.get(0).getTokenValue(), " AND ");
		Assert.assertEquals(1, tokens.size());
		ILogicParentNode andParent = (ILogicParentNode) tokens.get(0);
		ILogicParentNode firstParent = (ILogicParentNode) andParent.getLeft();
		ILogicParentNode secondParent = (ILogicParentNode) andParent.getRight();
		Assert.assertNull(secondParent);
		Assert.assertEquals(firstParent.getLeft().getTokenValue(), "1");
		Assert.assertEquals(firstParent.getRight().getTokenValue(), "2");
	}

	public void test_expression_with_double_and_will_not_fail_construction() throws CannotInterpretExpressionNodeException {
		String e = "1 == 2 AND  AND 2 == 3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ArrayList<ILogicNode> tokens = let.collapseEqualityParents(new LogicNodeProvider(e));
		Assert.assertEquals(tokens.get(0).getTokenValue(), " AND ");
		Assert.assertEquals(2, tokens.size());
		ILogicParentNode andParent = (ILogicParentNode) tokens.get(0);
		ILogicParentNode firstParent = (ILogicParentNode) andParent.getLeft();
		Assert.assertEquals(firstParent.getLeft().getTokenValue(), "1");
		Assert.assertEquals(firstParent.getRight().getTokenValue(), "2");
	}

	public void test_expression_tree_will_process_order_of_operations() throws CannotInterpretExpressionNodeException {
		String e = "4 == 5 AND 1 == 2 AND 2 == 3";
		// ((4==5)AND(1==2))AND(2==3)

		String eBrackets = "4 == 5 AND (1 == 2 AND 2 == 3)";

		LogicExpressionTree let = new LogicExpressionTree(e);
		LogicExpressionTree letBrackets = new LogicExpressionTree(eBrackets);

		ArrayList<ILogicNode> tokens = let.collapseEqualityParents(new LogicNodeProvider(e));
		ILogicParentNode eBracketsRoot = (ILogicParentNode) letBrackets.getRootNode();

		Assert.assertEquals(1, tokens.size());
		Assert.assertEquals(tokens.get(0).getTokenValue(), " AND ");
		ILogicParentNode eRoot = (ILogicParentNode) tokens.get(0);
		Assert.assertEquals(" AND ", eRoot.getLeft().getTokenValue());
		Assert.assertEquals("==", eRoot.getRight().getTokenValue());

		Assert.assertEquals(eBracketsRoot.getTokenValue(), " AND ");
		Assert.assertEquals("==", eBracketsRoot.getLeft().getTokenValue());
		Assert.assertEquals(" AND ", eBracketsRoot.getRight().getTokenValue());

		Assert.assertEquals("(((4==5) AND (1==2)) AND (2==3))", eRoot.asString());
		Assert.assertEquals("((4==5) AND ((1==2) AND (2==3)))", eBracketsRoot.asString());
	}

	public void test_can_wrap_simple_expression_in_useless_brackets() throws CannotInterpretExpressionNodeException {
		String e = "(1 == 2)";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicParentNode firstParent = (ILogicParentNode) let.getRootNode();
		Assert.assertEquals(firstParent.getTokenValue(), "==");
		Assert.assertEquals(firstParent.getLeft().getTokenValue(), "1");
		Assert.assertEquals(firstParent.getRight().getTokenValue(), "2");
	}

	public void test_find_first_self_contained_bracket_range() throws CannotInterpretExpressionNodeException {
		// 01 23 456 78
		String e = "1==2 AND (2==3)";
		LogicExpressionTree let = new LogicExpressionTree(e);
		// 4--8
		ArrayList<ILogicNode> tokens = let.getAllNodesAsArrayList(new LogicNodeProvider(e));
		Assert.assertEquals(4, let.findFirstSelfContainedBracketRange(tokens).opening);
		Assert.assertEquals(8, let.findFirstSelfContainedBracketRange(tokens).closing);
	}

	public void test_get_sub_list_from_tokens() throws CannotInterpretExpressionNodeException {
		String e = "1==2 AND (2==3)";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ArrayList<ILogicNode> tokens = let.getAllNodesAsArrayList(new LogicNodeProvider(e));
		ArrayList<ILogicNode> subList = let.getSubList(tokens, 4, 8);
		Assert.assertEquals("(", subList.get(0).getTokenValue());
		Assert.assertEquals("2", subList.get(1).getTokenValue());
		Assert.assertEquals("==", subList.get(2).getTokenValue());
		Assert.assertEquals("3", subList.get(3).getTokenValue());
		Assert.assertEquals(")", subList.get(4).getTokenValue());
	}

	public void test_logic_node_provider_can_handle_plus() throws CannotInterpretExpressionNodeException {
		String e = "1 == 2 + 3";
		LogicNodeProvider provider = new LogicNodeProvider(e);
		Assert.assertTrue(provider.hasNext());
		Assert.assertEquals("1", provider.next().getTokenValue());
		provider.advance();
		Assert.assertEquals("2", provider.next().getTokenValue());
		Assert.assertEquals("+", provider.next().getTokenValue());
		Assert.assertEquals("3", provider.next().getTokenValue());
		Assert.assertEquals(false, provider.hasNext());
	}

	public void test_logic_expression_tree_handles_o_r() throws CannotInterpretExpressionNodeException {
		String e = "4 == 5 OR 1 == 2";

		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();

		Assert.assertEquals("((4==5) OR (1==2))", root.asString());
	}

	public void test_logic_expression_tree_handles_not_equal() throws CannotInterpretExpressionNodeException {
		String e = "4 == 5 OR 1 != 2";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("((4==5) OR (1!=2))", root.asString());
	}

	public void test_logic_expression_tree_handles_less_then_or_equal() throws CannotInterpretExpressionNodeException {
		String e = "4 == 5 OR 1 <= 2";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("((4==5) OR (1<=2))", root.asString());
	}

	public void test_logic_expression_tree_handles_less_then() throws CannotInterpretExpressionNodeException {
		String e = "4 == 5 OR 1 < 2";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("((4==5) OR (1<2))", root.asString());
	}

	public void test_logic_expression_tree_handles_greater_then_or_equal() throws CannotInterpretExpressionNodeException {
		String e = "4 == 5 OR 1 >= 2";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("((4==5) OR (1>=2))", root.asString());
	}

	public void test_logic_expression_tree_handles_greater_then() throws CannotInterpretExpressionNodeException {
		String e = "4 == 5 OR 1 > 2";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("((4==5) OR (1>2))", root.asString());
	}

	public void test_logic_expression_tree_handles_attributes_and_temps() throws CannotInterpretExpressionNodeException {
		String e = "1 > selected.Age";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("(1>selected.Age)", root.asString());
	}

	public void test_logic_expression_tree_handles_multiple_plusses() throws CannotInterpretExpressionNodeException {
		String e = "1 == 2 + 3 + 4";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("(1==((2+3)+4))", root.asString());
	}

	public void test_logic_expression_tree_handles_multiple_plusses_on_either_side() throws CannotInterpretExpressionNodeException {
		String e = "1 + 2 + 3  == 2 + 3 + 4";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("(((1+2)+3)==((2+3)+4))", root.asString());
	}

	public void test_logic_expression_tree_handles_arithmetic_equality_and_boolean() throws CannotInterpretExpressionNodeException {
		String e = "(1 + 2 + 3  == 2 + 3 + 4) AND (3 > 2 + 3) ";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("((((1+2)+3)==((2+3)+4)) AND (3>(2+3)))", root.asString());
	}

	public void test_logic_expression_tree_handles_minuses() throws CannotInterpretExpressionNodeException {
		String e = "1 == 2 - 3 + 4";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("(1==((2-3)+4))", root.asString());
	}

	public void test_logic_expression_tree_handles_multiplication() throws CannotInterpretExpressionNodeException {
		String e = "1 == 2 - 3 * 4";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("(1==((2-3)*4))", root.asString());
	}

	public void test_logic_expression_tree_handles_division() throws CannotInterpretExpressionNodeException {
		String e = "1 == 2 / 3 * 4";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("(1==((2/3)*4))", root.asString());
	}

	public void test_empty_expression_tree_is_not_allowed() {
		String e = "";
		LogicExpressionTree let = new LogicExpressionTree(e);
		Assert.assertTrue(!let.isValid());
	}

	@SuppressWarnings("unused")
	public void test_single_term_expression_is_allowed() throws CannotInterpretExpressionNodeException {
		String e = "==";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
	}

	@SuppressWarnings("unused")
	public void test_multi_root_expression_is_not_allowed() {
		String e = "1==2 3==4";
		try {
			LogicExpressionTree let = new LogicExpressionTree(e);
			ILogicNode root = let.getRootNode();
			fail();
		} catch (CannotInterpretExpressionNodeException ex) {
		}
	}

	public void test_equality_with_both_operands_is_valid() throws CannotInterpretExpressionNodeException {
		String e = "1==2";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(0, errors.size());
	}

	public void test_equality_with_no_operands_is_invalid() throws CannotInterpretExpressionNodeException {
		String e = "==";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(2, errors.size());
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0001, errors.get(0)._code);
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0005, errors.get(1)._code);
	}

	public void test_equality_with_no_right_operand_is_invalid() throws CannotInterpretExpressionNodeException {
		String e = "1==";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(2, errors.size());
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0001, errors.get(0)._code);
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0005, errors.get(1)._code);
	}

	public void test_equality_with_no_left_operand_is_invalid() throws CannotInterpretExpressionNodeException {
		String e = "==1";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(2, errors.size());
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0001, errors.get(0)._code);
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0005, errors.get(1)._code);
	}

	public void test_boolean_with_both_operands_is_valid() throws CannotInterpretExpressionNodeException {
		String e = "1 AND 2";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(0, errors.size());
	}

	public void test_boolean_with_no_operands_is_invalid() throws CannotInterpretExpressionNodeException {
		String e = " AND ";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(2, errors.size());
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0001, errors.get(0)._code);
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0005, errors.get(1)._code);
	}

	public void test_boolean_with_no_right_operand_is_invalid() throws CannotInterpretExpressionNodeException {
		String e = "1 AND ";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(2, errors.size());
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0001, errors.get(0)._code);
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0005, errors.get(1)._code);	}

	public void test_boolean_with_no_left_operand_is_invalid() throws CannotInterpretExpressionNodeException {
		String e = " AND 1";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(2, errors.size());
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0001, errors.get(0)._code);
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0005, errors.get(1)._code);	}

	public void test_arithmetic_with_both_operands_is_valid() throws CannotInterpretExpressionNodeException {
		String e = "1+2";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(0, errors.size());
	}

	public void test_arithmetic_with_no_operands_is_invalid() throws CannotInterpretExpressionNodeException {
		String e = "+";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(3, errors.size());
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0000, errors.get(2)._code);
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0001, errors.get(0)._code);
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0005, errors.get(1)._code);	}

	public void test_arithmetic_with_no_right_operand_is_invalid() throws CannotInterpretExpressionNodeException {
		String e = "1+";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(3, errors.size());
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0001, errors.get(0)._code);
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0005, errors.get(1)._code);	
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0000, errors.get(2)._code);
		}

	public void test_arithmetic_with_no_left_operand_is_invalid() throws CannotInterpretExpressionNodeException {
		String e = "+1";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(3, errors.size());
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0001, errors.get(0)._code);
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0005, errors.get(1)._code);
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0000, errors.get(2)._code);
	}

	/**
	 * Rules for logic parent node children
	 *  = : + | c
	 *  + : + | c
	 *  A : = | A
	 */

	public void test_equality_operators_cannot_have_equality_operator_children() throws CannotInterpretExpressionNodeException {
		String e = "1+2==2+3==3+4";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("(((1+2)==(2+3))==(3+4))", root.asString());
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(2, errors.size());
	}

	public void test_equality_operators_can_have_arithmetic_operator_children() throws CannotInterpretExpressionNodeException {
		String e = "1+2==2+3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("((1+2)==(2+3))", root.asString());
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(0, errors.size());
	}

	public void test_boolean_operators_can_have_equality_operator_children() throws CannotInterpretExpressionNodeException {
		String e = "1+2==2+3 AND 1+2==2+3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("(((1+2)==(2+3)) AND ((1+2)==(2+3)))", root.asString());
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(0, errors.size());
	}

	public void test_boolean_operators_cannot_have_arithmetic_children() throws CannotInterpretExpressionNodeException {
		// (1+2) AND (3+4)
		String e = "1+2 AND 3+4";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("((1+2) AND (3+4))", root.asString());
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(2, errors.size());
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0003, errors.get(0)._code);

	}

	public void test_boolean_operators_can_have_simple_child() throws CannotInterpretExpressionNodeException {
		// x AND 2==3
		// this should fail unless x is of BooleanDatatype
		String e = "x AND 2==3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("(x AND (2==3))", root.asString());
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(1, errors.size());
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0005, errors.get(0)._code);	}

	@SuppressWarnings("unused")
	public void test_arithmetic_operators_cannot_have_boolean_chilren() throws CannotInterpretExpressionNodeException {
		String e = "1+AND2==3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		try {
			ILogicNode root = let.getRootNode();
			fail("This should have failed");
		} catch (CannotInterpretExpressionNodeException ex) {

		}
		/*
		 * Assert.assertEquals("(1+AND(2==3))", root.asString());
		 * root.validate(); ArrayList<LogicTreeValidationException> errors =
		 * root.getValidationErrors(); Assert.assertEquals(1, errors.size());
		 * Assert.assertEquals(LogicTreeValidationException.
		 * LogicTreeValidationExceptionCodes.e0004,errors.get(0)._code);
		 */
	}

	public void test_logic_expression_tree_can_get_all_child_nodes() throws CannotInterpretExpressionNodeException, EncounteredNonItemLeafNodeException {
		String e = "1+2==2+3 AND 1+2==2+3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("(((1+2)==(2+3)) AND ((1+2)==(2+3)))", root.asString());
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(0, errors.size());
		ArrayList<ConstantToken> leafNodes = let.getLeafNodes();
		Assert.assertEquals(2, countOccurancesOfConstantTokenWithValueInArrayList("1", leafNodes));
		Assert.assertEquals(4, countOccurancesOfConstantTokenWithValueInArrayList("2", leafNodes));
		Assert.assertEquals(2, countOccurancesOfConstantTokenWithValueInArrayList("3", leafNodes));

	}

	@SuppressWarnings("unused")
	public void test_logic_expression_tree_get_all_child_nodes_fails_if_leaf_is_not_constant_token() throws CannotInterpretExpressionNodeException,
			EncounteredNonItemLeafNodeException {
		String e = "1+==";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("(1+(==))", root.asString());
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(3, errors.size());
		try {
			ArrayList<ConstantToken> leafNodes = let.getLeafNodes();
			fail();
		} catch (Exception ex) {
		}

	}

	@SuppressWarnings("unused")
	public void test_logic_expression_tree_can_identify_type_of_leaf_nodes_with_constant_root() throws CannotInterpretExpressionNodeException,
			EncounteredNonItemLeafNodeException {
		String e = "13";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("13", root.asString());
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(0, errors.size());
		ArrayList<ConstantToken> leafNodes = let.getLeafNodes();
		Assert.assertEquals(ActionLanguageTokenLiteral.class, ActionLanguageTokenIdentifier.IdentifyToken(root.getTokenValue()).getClass());
	}

	public void test_logic_expression_tree_can_identify_type_of_leaf_nodes_with_complex_expression() throws CannotInterpretExpressionNodeException,
			EncounteredNonItemLeafNodeException {
		String e = "1+self.Age==2+tempVariable AND 1+2==2+3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("(((1+self.Age)==(2+tempVariable)) AND ((1+2)==(2+3)))", root.asString());
		root.validate(null);
		root.getValidationErrors();

		ArrayList<ConstantToken> leafNodes = let.getLeafNodes();
		Assert.assertEquals(2, countOccurancesOfConstantTokenWithValueInArrayList("1", leafNodes));
		Assert.assertEquals(3, countOccurancesOfConstantTokenWithValueInArrayList("2", leafNodes));
		Assert.assertEquals(1, countOccurancesOfConstantTokenWithValueInArrayList("3", leafNodes));
		Assert.assertEquals(1, countOccurancesOfConstantTokenWithValueInArrayList("self.Age", leafNodes));
		Assert.assertEquals(1, countOccurancesOfConstantTokenWithValueInArrayList("tempVariable", leafNodes));

		Assert.assertTrue(testAllOccurancesOfConstantTokenAreOfTokenType("1", leafNodes, ActionLanguageTokenLiteral.class));
		Assert.assertTrue(testAllOccurancesOfConstantTokenAreOfTokenType("2", leafNodes, ActionLanguageTokenLiteral.class));
		Assert.assertTrue(testAllOccurancesOfConstantTokenAreOfTokenType("3", leafNodes, ActionLanguageTokenLiteral.class));
		Assert.assertTrue(testAllOccurancesOfConstantTokenAreOfTokenType("self.Age", leafNodes, ActionLanguageTokenAttribute.class));
		Assert.assertTrue(testAllOccurancesOfConstantTokenAreOfTokenType("tempVariable", leafNodes, ActionLanguageTokenTemp.class));
	}
	
	public void test_logic_expression_tree_can_get_all_child_nodes_of_any_type() throws CannotInterpretExpressionNodeException, EncounteredNonItemLeafNodeException {
		String e = "1+2==2+3 AND 1+2==2+3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("(((1+2)==(2+3)) AND ((1+2)==(2+3)))", root.asString());
		root.validate(null);
		ArrayList<ILogicNode> leafNodes = new ArrayList<ILogicNode>();
		root.getAllNodes(leafNodes);
		Assert.assertEquals(15, leafNodes.size());

	}
	
	public void test_logic_expression_tree_can_get_all_child_nodes_of_type_boolean_operator() throws CannotInterpretExpressionNodeException, EncounteredNonItemLeafNodeException {
		String e = "1+2==2+3 AND 1+2==2+3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals("(((1+2)==(2+3)) AND ((1+2)==(2+3)))", root.asString());
		root.validate(null);
		ArrayList<ILogicNode> leafNodes = let.getAllNodesOfType(IEqualityOperatorToken.class);
		Assert.assertEquals(2, leafNodes.size());
		Assert.assertEquals(leafNodes.get(0).getTokenValue(), "==");
		Assert.assertEquals(leafNodes.get(1).getTokenValue(), "==");

	}

	private int countOccurancesOfConstantTokenWithValueInArrayList(String constant, ArrayList<ConstantToken> tokens) {
		int count = 0;
		for (ConstantToken c : tokens) {
			if (c.getTokenValue().equals(constant)) {
				count++;
			}
		}
		return count;
	}

	private boolean testAllOccurancesOfConstantTokenAreOfTokenType(String constant, ArrayList<ConstantToken> tokens, Class<?> tokenClass) {
		Boolean allMatch = true;
		int count = 0;
		for (ConstantToken c : tokens) {
			if (c.getTokenValue().equals(constant)) {
				count++;
				if (tokenClass != ActionLanguageTokenIdentifier.IdentifyToken(c.getTokenValue()).getClass()) {
					allMatch = false;
				}
			}
		}
		if (count == 0) {
			return false;
		}
		return allMatch;
	}

	
	public void test_logic_expression_tree_can_identify_end_point_data_type() throws CannotInterpretExpressionNodeException, EncounteredNonItemLeafNodeException {
		String e = "1";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals(root.getDatatype(null).getClass(), IntegerEntityDatatype.class);
	}
	
	public void test_logic_expression_tree_can_identify_end_point_data_type_of_addition() throws CannotInterpretExpressionNodeException, EncounteredNonItemLeafNodeException {
		String e = "1+2";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals(root.getDatatype(null).getClass(), IntegerEntityDatatype.class);
	}
	
	public void test_logic_expression_tree_can_identify_invalid_end_point_data_type_of_addition() throws CannotInterpretExpressionNodeException, EncounteredNonItemLeafNodeException {
		String e = "1+\"hello\" + 2";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals(StringEntityDatatype.class, root.getDatatype(null).getClass());
	}
	
	public void test_logic_expression_tree_can_identify_end_point_data_type_of_boolean() throws CannotInterpretExpressionNodeException, EncounteredNonItemLeafNodeException {
		String e = "true AND false";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals(BooleanEntityDatatype.class, root.getDatatype(null).getClass());
	}
	
	public void test_complex_logic_expression_tree_can_identify_end_point_data_type_of_boolean() throws CannotInterpretExpressionNodeException, EncounteredNonItemLeafNodeException {
		String e = "1+2==2+3 AND 1+2==2+3";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals(BooleanEntityDatatype.class, root.getDatatype(null).getClass());
	}
	
	public void test_arithmetic_with_different_types_is_invalid() throws CannotInterpretExpressionNodeException {
		String e = "1+\"hello\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(0, errors.size());
		//Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0005, errors.get(0)._code);
	}
	
	public void test_dont_allow_minus_in_expression_when_token_is_a_string() throws CannotInterpretExpressionNodeException
	{
		String e = "1-\"hello\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(1, errors.size());
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0006, errors.get(0)._code);
	}
	
	public void test_dont_allow_multiply_in_expression_when_token_is_a_string() throws CannotInterpretExpressionNodeException
	{
		String e = "1*\"hello\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(1, errors.size());
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0006, errors.get(0)._code);
	}
	
	public void test_dont_allow_divide_in_expression_when_token_is_a_string() throws CannotInterpretExpressionNodeException
	{
		String e = "1/\"hello\"";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		root.validate(null);
		ArrayList<LogicTreeValidationException> errors = root.getValidationErrors();
		Assert.assertEquals(1, errors.size());
		Assert.assertEquals(LogicTreeValidationException.LogicTreeValidationExceptionCodes.e0006, errors.get(0)._code);
	}
	
	public void test_logic_expression_tree_handle_nots() throws CannotInterpretExpressionNodeException, EncounteredNonItemLeafNodeException {
		String e = "NOT(true)";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals(BooleanEntityDatatype.class, root.getDatatype(null).getClass());
	}
	
	public void test_logic_expression_tree_doesnt_handle_nots_of_non_booleans() throws CannotInterpretExpressionNodeException, EncounteredNonItemLeafNodeException {
		String e = "NOT(1 + 1)";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals(InvalidEntityDatatype.class, root.getDatatype(null).getClass());
	}
	
	
	public void test_logic_expression_tree_can_handle_double_not() throws CannotInterpretExpressionNodeException, EncounteredNonItemLeafNodeException {
		String e = "NOT(NOT(True))";
		LogicExpressionTree let = new LogicExpressionTree(e);
		ILogicNode root = let.getRootNode();
		Assert.assertEquals(BooleanEntityDatatype.class, root.getDatatype(null).getClass());
	}
	
	public void test_logic_expression_tree_cant_handle_mis_matched_brackets() throws CannotInterpretExpressionNodeException, EncounteredNonItemLeafNodeException {
		String e = "NOT(NOT(True)))";
		LogicExpressionTree let = new LogicExpressionTree(e);
		try
		{
			@SuppressWarnings("unused")
			ILogicNode root = let.getRootNode();
			fail();
		}catch(Exception ex){}
	}
	
	
}

