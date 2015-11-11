package test.java.tests;

import java.util.ArrayList;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.codeBlock.ICodeBlock;
import main.java.avii.editor.contracts.metamodel.actionLanguage.codeBlock.ICodeBlockOpen;
import main.java.avii.editor.metamodel.actionLanguage.codeBlock.BaseCodeBlock;
import main.java.avii.editor.metamodel.actionLanguage.codeBlock.CodeBlockCloseFor;
import main.java.avii.editor.metamodel.actionLanguage.codeBlock.CodeBlockCloseIf;
import main.java.avii.editor.metamodel.actionLanguage.codeBlock.CodeBlockElse;
import main.java.avii.editor.metamodel.actionLanguage.codeBlock.CodeBlockManager;
import main.java.avii.editor.metamodel.actionLanguage.codeBlock.CodeBlockOpenFor;
import main.java.avii.editor.metamodel.actionLanguage.codeBlock.CodeBlockOpenIf;

public class CodeBlockTest extends TestCase {

	public CodeBlockTest(String name) {
		super(name);
	}
	
	public void test_opening_if_block_knows_what_its_acceptable_closing_block_is()
	{
		CodeBlockOpenIf openIf = new CodeBlockOpenIf();
		CodeBlockCloseIf closeIf = new CodeBlockCloseIf();
		Assert.assertTrue(((ICodeBlockOpen)openIf).acceptsCloseBlock(closeIf));
	}
	
	public void test_opening_for_block_knows_what_its_acceptable_closing_block_is()
	{
		CodeBlockOpenFor openFor = new CodeBlockOpenFor();
		CodeBlockCloseFor closeFor = new CodeBlockCloseFor();
		Assert.assertTrue(((ICodeBlockOpen)openFor).acceptsCloseBlock(closeFor));
	}
	
	public void test_opening_if_block_will_not_accept_closing_for_block()
	{
		CodeBlockOpenIf openIf = new CodeBlockOpenIf();
		CodeBlockCloseFor closeFor = new CodeBlockCloseFor();
		Assert.assertTrue(!((ICodeBlockOpen)openIf).acceptsCloseBlock(closeFor));
	}
	
	public void test_opening_for_block_will_not_accept_closing_if_block()
	{
		CodeBlockOpenFor openFor = new CodeBlockOpenFor();
		CodeBlockCloseIf closeIf = new CodeBlockCloseIf();
		Assert.assertTrue(!((ICodeBlockOpen)openFor).acceptsCloseBlock(closeIf));
	}

	public void test_code_block_manager_can_have_code_blocks_added()
	{
		CodeBlockManager codeBlockManager = new CodeBlockManager();
		codeBlockManager.addBlock(new CodeBlockOpenIf(), 0);
		codeBlockManager.addBlock(new CodeBlockCloseIf(), 0);
		ArrayList<BaseCodeBlock> codeBlocks = codeBlockManager.getCodeBlocks();
		ICodeBlock firstBlock = codeBlocks.get(0);
		ICodeBlock secondBlock = codeBlocks.get(1);
		Assert.assertEquals(firstBlock.getClass(), CodeBlockOpenIf.class);
		Assert.assertEquals(secondBlock.getClass(), CodeBlockCloseIf.class);
	}
	
	public void test_code_block_fails_if_only_open_if_block_is_added()
	{
		CodeBlockManager codeBlockManager = new CodeBlockManager();
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenIf(), 0));
		Assert.assertTrue(!codeBlockManager.validate());
	}
	
	public void test_code_block_fails_if_only_open_for_block_is_added()
	{
		CodeBlockManager codeBlockManager = new CodeBlockManager();
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenFor(), 0));
		Assert.assertTrue(!codeBlockManager.validate());
	}
	
	public void test_code_block_fails_close_block_if_is_added_first()
	{
		CodeBlockManager codeBlockManager = new CodeBlockManager();
		Assert.assertTrue(!codeBlockManager.addBlock(new CodeBlockCloseIf(), 0));
		Assert.assertTrue(!codeBlockManager.validate());
	}
	
	public void test_code_block_fails_close_block_for_is_added_first()
	{
		CodeBlockManager codeBlockManager = new CodeBlockManager();
		Assert.assertTrue(!codeBlockManager.addBlock(new CodeBlockCloseFor(), 0));
		Assert.assertTrue(!codeBlockManager.validate());
	}
	
	public void test_code_block_manager_allows_open_close_if()
	{
		CodeBlockManager codeBlockManager = new CodeBlockManager();
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenIf(), 0));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockCloseIf(), 0));
		Assert.assertTrue(codeBlockManager.validate());
	}
	
	public void test_code_block_manager_allows_open_close_for()
	{
		CodeBlockManager codeBlockManager = new CodeBlockManager();
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenFor(), 0));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockCloseFor(), 0));
		Assert.assertTrue(codeBlockManager.validate());
	}
	
	public void test_code_block_manager_fails_when_adding_a_close_block_that_doesnt_match_the_last_open()
	{
		CodeBlockManager codeBlockManager = new CodeBlockManager();
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenIf(), 0));
		Assert.assertTrue(!codeBlockManager.addBlock(new CodeBlockCloseFor(), 0));
		Assert.assertTrue(!codeBlockManager.validate());
	}
	
	public void test_code_block_manager_fails_if_there_is_a_block_remaining()
	{
		CodeBlockManager codeBlockManager = new CodeBlockManager();
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenFor(), 0));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockCloseFor(), 0));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenFor(), 0));
		Assert.assertTrue(!codeBlockManager.validate());
	}
	
	public void test_else_is_an_accepted_close_for_open_if()
	{
		CodeBlockOpenIf openIf = new CodeBlockOpenIf();
		CodeBlockElse els = new CodeBlockElse();
		Assert.assertTrue(openIf.acceptsCloseBlock(els));
	}
	
	public void test_else_is_not_an_accepted_close_for_open_for()
	{
		CodeBlockOpenFor openFor = new CodeBlockOpenFor();
		CodeBlockElse els = new CodeBlockElse();
		Assert.assertTrue(!openFor.acceptsCloseBlock(els));
	}
	
	public void test_else_is_not_an_accepted_close_for_else()
	{
		CodeBlockElse els1 = new CodeBlockElse();
		CodeBlockElse els2 = new CodeBlockElse();
		Assert.assertTrue(!els1.acceptsCloseBlock(els2));
	}
	
	public void test_end_if_is_accepted_close_for_else()
	{
		CodeBlockCloseIf closeIf = new CodeBlockCloseIf();
		CodeBlockElse els = new CodeBlockElse();
		Assert.assertTrue(els.acceptsCloseBlock(closeIf));
	}
	
	public void test_end_for_is_not_accepted_close_for_else()
	{
		CodeBlockCloseFor closeFor = new CodeBlockCloseFor();
		CodeBlockElse els = new CodeBlockElse();
		Assert.assertTrue(!els.acceptsCloseBlock(closeFor));
	}
	
	public void test_code_block_manager_allows_open_else_close_if()
	{
		CodeBlockManager codeBlockManager = new CodeBlockManager();
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenIf(), 0));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockElse(), 0));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockCloseIf(), 0));
		Assert.assertTrue(codeBlockManager.validate());
	}

	public void test_code_block_manager_does_not_allow_open_else_close_for()
	{
		CodeBlockManager codeBlockManager = new CodeBlockManager();
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenFor(), 0));
		Assert.assertTrue(!codeBlockManager.addBlock(new CodeBlockElse(), 0));
		Assert.assertTrue(!codeBlockManager.addBlock(new CodeBlockCloseFor(), 0));
		Assert.assertTrue(!codeBlockManager.validate());
	}
	
	public void test_code_block_manager_fails_open_else_else_close_if()
	{
		CodeBlockManager codeBlockManager = new CodeBlockManager();
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenIf(), 0));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockElse(), 0));
		Assert.assertTrue(!codeBlockManager.addBlock(new CodeBlockElse(), 0));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockCloseIf(), 0));
		Assert.assertTrue(!codeBlockManager.validate());
	}
	
	
	public void test_code_block_manager_can_determine_line_of_end_if_from_if()
	{
		CodeBlockManager codeBlockManager = new CodeBlockManager();
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenIf(), 2));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockCloseIf(), 10));
		Assert.assertTrue(codeBlockManager.validate());
		Assert.assertEquals(10,codeBlockManager.getLineOfCloseBlockForOpenBlockOnLine(2));
	}
	
	public void test_code_block_manager_can_determine_line_of_else_from_if()
	{
		CodeBlockManager codeBlockManager = new CodeBlockManager();
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenIf(), 2));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockElse(), 5));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockCloseIf(), 10));
		Assert.assertTrue(codeBlockManager.validate());
		// jump over else
		Assert.assertEquals(6,codeBlockManager.getLineOfCloseBlockForOpenBlockOnLine(2));
		Assert.assertEquals(10,codeBlockManager.getLineOfCloseBlockForOpenBlockOnLine(5));
	}
	
	public void test_code_block_manager_can_determine_line_of_else_from_if2()
	{
		CodeBlockManager codeBlockManager = new CodeBlockManager();
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenIf(), 2));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockElse(), 5));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenIf(), 6));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockElse(), 7));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockCloseIf(), 8));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockCloseIf(), 10));
		Assert.assertTrue(codeBlockManager.validate());
		Assert.assertEquals(6,codeBlockManager.getLineOfCloseBlockForOpenBlockOnLine(2));
		Assert.assertEquals(10,codeBlockManager.getLineOfCloseBlockForOpenBlockOnLine(5));
		Assert.assertEquals(8,codeBlockManager.getLineOfCloseBlockForOpenBlockOnLine(6));
		Assert.assertEquals(8,codeBlockManager.getLineOfCloseBlockForOpenBlockOnLine(7));
	}

	public void test_code_block_manager_can_determine_start_for_line_from_end_for()
	{
		CodeBlockManager codeBlockManager = new CodeBlockManager();
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenFor(), 2));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockCloseFor(), 10));
		Assert.assertTrue(codeBlockManager.validate());
		Assert.assertEquals(10,codeBlockManager.getLineOfCloseBlockForOpenBlockOnLine(2));
		Assert.assertEquals(2,codeBlockManager.getLineOfOpenBlockForCloseBlockOnLine(10));
	}
	

	public void test_code_block_manager_can_determine_start_for_line_from_end_for2()
	{
		CodeBlockManager codeBlockManager = new CodeBlockManager();
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenFor(), 2));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenFor(), 3));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockCloseFor(), 9));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockCloseFor(), 10));
		Assert.assertTrue(codeBlockManager.validate());
		Assert.assertEquals(10,codeBlockManager.getLineOfCloseBlockForOpenBlockOnLine(2));
		Assert.assertEquals(2,codeBlockManager.getLineOfOpenBlockForCloseBlockOnLine(10));
		Assert.assertEquals(9,codeBlockManager.getLineOfCloseBlockForOpenBlockOnLine(3));
		Assert.assertEquals(3,codeBlockManager.getLineOfOpenBlockForCloseBlockOnLine(9));
	}
	
	
	public void test_can_determine_if_line_number_falls_within_a_for_loop()
	{
		CodeBlockManager codeBlockManager = new CodeBlockManager();
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenFor(), 2));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockCloseFor(), 10));
		Assert.assertTrue(codeBlockManager.validate());
		Assert.assertTrue(codeBlockManager.doesLineFallWithinForLoop(3));
		Assert.assertTrue(!codeBlockManager.doesLineFallWithinForLoop(1));
	}
	
	public void test_can_determine_if_line_number_falls_within_a_nested_for_loop()
	{
		CodeBlockManager codeBlockManager = new CodeBlockManager();
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenFor(), 2));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenFor(), 5));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockCloseFor(), 7));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockCloseFor(), 10));
		Assert.assertTrue(codeBlockManager.validate());
		Assert.assertTrue(codeBlockManager.doesLineFallWithinForLoop(3));
		Assert.assertTrue(codeBlockManager.doesLineFallWithinForLoop(6));
		Assert.assertTrue(!codeBlockManager.doesLineFallWithinForLoop(1));
	}
	
	public void test_can_get_all_for_loops_containing_a_line()
	{
		CodeBlockManager codeBlockManager = new CodeBlockManager();
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenFor(), 2));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockCloseFor(), 10));
		Assert.assertTrue(codeBlockManager.validate());

		ArrayList<CodeBlockOpenFor> openForBlocksForThisLine = codeBlockManager.getContainingOpeningBlocksForLine(3);
		Assert.assertEquals(2,openForBlocksForThisLine.get(0).getLineNumber());
	}
	
	public void test_can_get_all_for_loops_nested_containing_a_line()
	{
		CodeBlockManager codeBlockManager = new CodeBlockManager();
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenFor(), 2));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockOpenFor(), 5));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockCloseFor(), 7));
		Assert.assertTrue(codeBlockManager.addBlock(new CodeBlockCloseFor(), 10));
		Assert.assertTrue(codeBlockManager.validate());
		
		ArrayList<CodeBlockOpenFor> openForBlocksForThisLine = codeBlockManager.getContainingOpeningBlocksForLine(3);
		Assert.assertEquals(2,openForBlocksForThisLine.get(0).getLineNumber());
		
		openForBlocksForThisLine = codeBlockManager.getContainingOpeningBlocksForLine(6);
		Assert.assertEquals(5,openForBlocksForThisLine.get(0).getLineNumber());
		Assert.assertEquals(2,openForBlocksForThisLine.get(1).getLineNumber());
	}
	
}

