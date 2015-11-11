package main.java.avii.editor.metamodel.actionLanguage.codeBlock;

import java.util.ArrayList;

import main.java.avii.editor.contracts.metamodel.actionLanguage.codeBlock.ICodeBlockClose;
import main.java.avii.editor.contracts.metamodel.actionLanguage.codeBlock.ICodeBlockOpen;

public class CodeBlockManager {

	private ArrayList<BaseCodeBlock> _codeBlocks = new ArrayList<BaseCodeBlock>();
	private boolean _isValid = true;
	
	private int count()
	{
		return _codeBlocks.size();
	}
	
	private BaseCodeBlock currentBlock()
	{
		if(count() > 0)
		{
			return _codeBlocks.get(count()-1);
		}
		return null;
	}
	
	private BaseCodeBlock previousBlock()
	{
		if(count() > 0)
		{
			return _codeBlocks.get(count()-2);
		}
		return null;
	}
	
	private boolean isCurrentCodeBlockAClose()
	{
		return currentBlock() instanceof ICodeBlockClose;
	}
	
	private boolean isCurrentCodeBlockAnOpen()
	{
		return currentBlock() instanceof ICodeBlockOpen;
	}
	
	private boolean isPreviousCodeBlockAnOpen()
	{
		return previousBlock() instanceof ICodeBlockOpen;
	}

	private boolean isThisTheFirstCodeBlockAndIsClosed()
	{
		if(count() == 1 && isCurrentCodeBlockAClose())
		{
			return true;
		}
		return false;
	}
	
	private boolean isPreviousBlockAnOpenAndCurrentIsANonMatchingClose()
	{
		if(count() == 1)
		{
			return false;
		}
		
		if(isPreviousCodeBlockAnOpen() && isCurrentCodeBlockAClose())
		{
			ICodeBlockOpen previousOpen = (ICodeBlockOpen)previousBlock();
			ICodeBlockClose currentClose = (ICodeBlockClose)currentBlock();
			return !previousOpen.acceptsCloseBlock(currentClose);
		}
		
		return false;
	}
	
	public boolean addBlock(BaseCodeBlock codeBlock, int lineNumber) {
		_codeBlocks.add(codeBlock);
		codeBlock.setLineNumber(lineNumber);
		
		if(isThisTheFirstCodeBlockAndIsClosed())
		{
			return invalidate();
		}
		
		if(count() >1 && isPreviousBlockAnOpenAndCurrentIsANonMatchingClose())
		{
			return invalidate();
		}
		
		return true;
	}

	public ArrayList<BaseCodeBlock> getCodeBlocks() {
		return _codeBlocks;
	}

	private boolean invalidate()
	{
		this._isValid = false;
		return false;
	}
	
	public boolean validate() {
		// valid if empty
		if(count() == 0)
		{
			return true;
		}
		
		// invalid if only 1 block
		if(count() == 1)
		{
			return false;
		}
		
		// return false if already invalidated
		if(!_isValid)
		{
			return invalidate();
		}
		
		// stacks must not end in an open block
		if(isCurrentCodeBlockAnOpen())
		{
			return false;
		}
		
		// don't allow mismatched opens to closes
		if(isPreviousBlockAnOpenAndCurrentIsANonMatchingClose())
		{
			return invalidate();
		}
		
		return _isValid;
	}

	public BaseCodeBlock getCurrentCodeBlock() {
		return currentBlock();
	}

	public int getLineOfCloseBlockForOpenBlockOnLine(int openingLine) {
		boolean codeBlockIsOpenging = false;
		boolean haveAlreadyFoundOpening = false;
		boolean foundOpeningLine = false;
		int openBlockCount = 0;
		
		for(BaseCodeBlock block : this._codeBlocks)
		{
			codeBlockIsOpenging = false;
			if(block.getLineNumber() == openingLine)
			{
				foundOpeningLine = true;
				if(!haveAlreadyFoundOpening)
				{
					codeBlockIsOpenging = true;
					haveAlreadyFoundOpening = true;
				}
			}
			
			if(foundOpeningLine)
			{
				if(block instanceof CodeBlockOpenIf || block instanceof CodeBlockOpenFor)
				{
					openBlockCount++;
				}
				if(block instanceof CodeBlockCloseIf || block instanceof CodeBlockCloseFor)
				{
					openBlockCount--;
					if(openBlockCount == 0)
					{
						return block.getLineNumber();
					}
				}
				if(block instanceof CodeBlockElse)
				{
					if(block.getLineNumber() != openingLine)
					{
						openBlockCount--;
						if(openBlockCount == 0)
						{
							// we want to jump over else statements
							return block.getLineNumber() + 1;
						}
					}
					// v
					if((block.getLineNumber() == openingLine) && !codeBlockIsOpenging)
					{
						openBlockCount--;
					}
					// ^
					openBlockCount++;
				}
			}
		}
		return -1;
	}
	
	private BaseCodeBlock getCodeBlockOnLineNumber(int theLineNumber)
	{
		for(BaseCodeBlock block : this._codeBlocks)
		{
			if(block.getLineNumber() == theLineNumber)
			{
				return block;
			}
		}
		return null;
	}
	
	public int getLineOfOpenBlockForCloseBlockOnLine(int closeLineNumber) {
		for(int i = 1; i < closeLineNumber; i++)
		{
			if(this.getLineOfCloseBlockForOpenBlockOnLine(i) == closeLineNumber)
			{
				return i;
			}
		}
		return -1;
	}

	public boolean doesLineFallWithinForLoop(int theLineNumber) {
		ArrayList<CodeBlockOpenFor> openingForBlocksForThisLine = this.getContainingOpeningBlocksForLine(theLineNumber);
		return !openingForBlocksForThisLine.isEmpty();
	}
	
	public ArrayList<CodeBlockOpenFor> getContainingOpeningBlocksForLine(int theLineNumber)
	{
		ArrayList<CodeBlockOpenFor> containingOpeningForBlocks = new ArrayList<CodeBlockOpenFor>();
		for(int possibleOpeningBlockLineNumber = theLineNumber; possibleOpeningBlockLineNumber > 0; possibleOpeningBlockLineNumber--)
		{
			BaseCodeBlock possibleOpeningBlock = this.getCodeBlockOnLineNumber(possibleOpeningBlockLineNumber);
			if(possibleOpeningBlock instanceof CodeBlockOpenFor)
			{
				BaseCodeBlock openingBlock = this.getCodeBlockOnLineNumber(possibleOpeningBlockLineNumber);
				if(openingBlock instanceof CodeBlockOpenFor)
				{
					containingOpeningForBlocks.add((CodeBlockOpenFor) openingBlock);
				}
			}
		}
		return containingOpeningForBlocks;
	}

}
