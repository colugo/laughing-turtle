package main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IArithmeticOperatorToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.validation.LogicTreeValidationException.LogicTreeValidationExceptionCodes;
import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageTokenIdentifier;
import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.FloatingEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.InvalidEntityDatatype;

public abstract class BaseLogicArithmeticParentNode extends BaseLogicParentNode implements IArithmeticOperatorToken {
	
	@Override 
	public void validate(IActionLanguageTokenIdentifier tokenIdentifier)
	{
		super.validate(tokenIdentifier);
		
		this.checkIfArithmeticOperatorIsValidForLeftAndRightDatatypes(tokenIdentifier);
	}
	
	protected abstract void areDatatypesValidForOperator(IEntityDatatype left, IEntityDatatype right);
	
	protected boolean areLeftAndRightNumericDatatypes(IEntityDatatype left, IEntityDatatype right)
	{
		if((left instanceof FloatingEntityDatatype || left instanceof IntegerEntityDatatype) && (right instanceof FloatingEntityDatatype || right instanceof IntegerEntityDatatype))
		{
			return true;
		}
		return false;
	}
	
	private void checkIfArithmeticOperatorIsValidForLeftAndRightDatatypes(IActionLanguageTokenIdentifier tokenIdentifier) {
		if(this.left == null || this.right == null)
		{
			addError(this,LogicTreeValidationExceptionCodes.e0000);
			return;
		}
		IEntityDatatype leftDatatype = this.left.getDatatype(tokenIdentifier);
		IEntityDatatype rightDatatype = this.right.getDatatype(tokenIdentifier);
		
		areDatatypesValidForOperator(leftDatatype,rightDatatype);
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
		
		if(leftDatatype.canBeCombinedWithDatatype(rightDatatype))
		{
			return leftDatatype.getResultingDatatypeWhenCombinedWith(rightDatatype);
		}
		
		return InvalidEntityDatatype.getInstance();
	}
	
	protected boolean areLeftAndRightNumeric(Object left, Object right)
	{
			try {
				Double.parseDouble(left.toString());
				Double.parseDouble(right.toString());
				return true;
			} catch (NumberFormatException nfe) {
			}
			return false;
	}

	protected double asDouble(Object value)
	{
		return Double.parseDouble(value.toString());
	}
}
