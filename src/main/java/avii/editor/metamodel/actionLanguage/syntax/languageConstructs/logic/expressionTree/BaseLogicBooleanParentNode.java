package main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IArithmeticOperatorToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IBooleanOperatorToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.validation.LogicTreeValidationException.LogicTreeValidationExceptionCodes;
import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageTokenIdentifier;
import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.InvalidEntityDatatype;

public class BaseLogicBooleanParentNode extends BaseLogicParentNode implements IBooleanOperatorToken {
	
	@Override 
	public void validate(IActionLanguageTokenIdentifier tokenIdentifier)
	{
		super.validate(tokenIdentifier);
		checkChildrenAreNotArithmeticOperators();
	}
	
	@Override
	public IEntityDatatype getDatatype(IActionLanguageTokenIdentifier tokenIdentifier)
	{
		if(this.left == null || this.right == null)
		{
			return InvalidEntityDatatype.getInstance();
		}
		
		IEntityDatatype leftDatatype = this.left.getDatatype(tokenIdentifier);
		IEntityDatatype rightDatatype = this.right.getDatatype(tokenIdentifier);
		
		if(leftDatatype.canBeComparedToDatatype(rightDatatype))
		{
			return leftDatatype.getResultingDatatypeWhenCombinedWith(rightDatatype);
		}
		
		return InvalidEntityDatatype.getInstance();
	}
	
	private void checkChildrenAreNotArithmeticOperators() {
		if(this.getLeft() instanceof IArithmeticOperatorToken)
		{
			addError(this,LogicTreeValidationExceptionCodes.e0003);	
		}
		if(this.getRight() instanceof IArithmeticOperatorToken)
		{
			addError(this,LogicTreeValidationExceptionCodes.e0003);	
		}
	}

	@Override
	public Object calculateValue(Object left, Object right) {
		return null;
	}

	
	protected boolean asBoolean(Object value)
	{
		String valueString = value.toString().toUpperCase();
		if(valueString.equals("TRUE"))
		{
			return true;
		}
		return false;
	}	
	
}
