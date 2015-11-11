package main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.ILogicNode;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.ILogicParentNode;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.validation.LogicTreeValidationException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.validation.LogicTreeValidationException.LogicTreeValidationExceptionCodes;
import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageTokenIdentifier;
import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.InvalidEntityDatatype;

public abstract class BaseLogicParentNode extends BaseLogicNode implements ILogicParentNode {

	protected ILogicNode left;
	protected ILogicNode right;
	
	public ILogicNode getLeft() {
		return left;
	}
	
	public void setLeft(ILogicNode left) {
		this.left = left;
	}

	public ILogicNode getRight() {
		return right;
	}
	
	public void setRight(ILogicNode right) {
		this.right = right;
	}
	
	@Override 
	public void validate(IActionLanguageTokenIdentifier tokenIdentifier)
	{
		checkIfChildrenAreNotNull(tokenIdentifier);
		checkForInvalidDatatype(tokenIdentifier);
	}
	
	public void checkForInvalidDatatype(IActionLanguageTokenIdentifier tokenIdentifier)
	{
		if(this.getDatatype(tokenIdentifier) instanceof InvalidEntityDatatype)
		{
			addError(this,LogicTreeValidationExceptionCodes.e0005);
		}
	}

	private void checkIfChildrenAreNotNull(IActionLanguageTokenIdentifier tokenIdentifier) {
		if(this.getLeft() == null || this.getRight() == null)
		{
			addError(this,LogicTreeValidationExceptionCodes.e0001);	
		}
		else
		{
			this.getLeft().validate(tokenIdentifier);
			for(LogicTreeValidationException validationError : this.getLeft().getValidationErrors())
			{
				addError(validationError);
			}
			
			this.getRight().validate(tokenIdentifier);
			for(LogicTreeValidationException validationError : this.getRight().getValidationErrors())
			{
				addError(validationError);
			}
		}
	}
	
	public abstract IEntityDatatype getDatatype(IActionLanguageTokenIdentifier tokenIdentifier);

	public abstract Object calculateValue(Object left, Object right);
	
}
