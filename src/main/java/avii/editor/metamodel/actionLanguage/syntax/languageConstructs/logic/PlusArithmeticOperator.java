/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IAddSubtractOperatorToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.ILogicParentNode;
import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.BaseLogicArithmeticParentNode;

public class PlusArithmeticOperator extends BaseLogicArithmeticParentNode implements IAddSubtractOperatorToken, ILogicParentNode {
	String regex = "^\\+";

	public String getRegex() {
		return regex;
	}
	
	@Override
	public Object calculateValue(Object left, Object right) {
		
		if(this.areLeftAndRightNumeric(left,right))
		{
			return this.asDouble(left) + this.asDouble(right);
		}
		else
		{
			String leftAsString = left.toString();
			String rightAsString = right.toString();
			
			leftAsString = unwrapQuotes(leftAsString);
			rightAsString = unwrapQuotes(rightAsString);
			
			return leftAsString + rightAsString;
		}
	}

	private String unwrapQuotes(String value) {
		if(value.startsWith("\"") && value.endsWith("\""))
		{
			return value.substring(1, value.length() -1);
		}
		return value;
	}

	@Override
	protected void areDatatypesValidForOperator(IEntityDatatype left, IEntityDatatype right) {
		// plus is always ok		
	}
}
