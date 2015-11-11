package main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IEqualityOperatorToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.validation.LogicTreeValidationException.LogicTreeValidationExceptionCodes;
import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageTokenIdentifier;
import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.AvailableDatatypes;
import main.java.avii.editor.metamodel.entities.datatypes.BooleanEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.FloatingEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.InvalidEntityDatatype;

public class BaseLogicEqualityParentNode extends BaseLogicParentNode implements IEqualityOperatorToken {

	@Override
	public void validate(IActionLanguageTokenIdentifier tokenIdentifier) {
		super.validate(tokenIdentifier);

		checkChildrenAreNotEqualityOperators();
	}

	private void checkChildrenAreNotEqualityOperators() {
		if (this.getLeft() instanceof IEqualityOperatorToken) {
			addError(this, LogicTreeValidationExceptionCodes.e0002);
		}
		if (this.getRight() instanceof IEqualityOperatorToken) {
			addError(this, LogicTreeValidationExceptionCodes.e0002);
		}
	}

	@Override
	public IEntityDatatype getDatatype(IActionLanguageTokenIdentifier tokenIdentifier) {
		if (this.left == null || this.right == null) {
			return InvalidEntityDatatype.getInstance();
		}

		IEntityDatatype leftDatatype = this.left.getDatatype(tokenIdentifier);
		IEntityDatatype rightDatatype = this.right.getDatatype(tokenIdentifier);

		if (leftDatatype.canBeComparedToDatatype(rightDatatype)) {
			return BooleanEntityDatatype.getInstance();
		}

		return InvalidEntityDatatype.getInstance();
	}

	protected double asDouble(Object value) {
		return Double.parseDouble(value.toString());
	}

	protected boolean asBoolean(Object value) {
		String valueString = value.toString().toUpperCase();
		if (valueString.equals("TRUE")) {
			return true;
		}
		return false;
	}

	@Override
	public Object calculateValue(Object left, Object right) {
		if (this.areLeftAndRightDoubles(left, right)) {
			return calculateValue(this.asDouble(left), this.asDouble(right));
		}
		if (this.areLeftAndRightBooleans(left, right)) {
			return calculateValue(this.asBoolean(left), this.asBoolean(right));
		}
		return calculateValue(left.toString(),right.toString());
	}

	private boolean areLeftAndRightDoubles(Object left, Object right) {
		try {
			String leftString = left.toString();
			String rightString = right.toString();
			IEntityDatatype leftDatatype = AvailableDatatypes.getDatatypeForInput(leftString);
			IEntityDatatype rightDatatype = AvailableDatatypes.getDatatypeForInput(rightString);
			if ((leftDatatype instanceof FloatingEntityDatatype || leftDatatype instanceof IntegerEntityDatatype)
					&& (rightDatatype instanceof FloatingEntityDatatype || rightDatatype instanceof IntegerEntityDatatype)) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	private boolean areLeftAndRightBooleans(Object left, Object right) {
		try {
			String leftString = left.toString();
			String rightString = right.toString();
			IEntityDatatype leftDatatype = AvailableDatatypes.getDatatypeForInput(leftString);
			IEntityDatatype rightDatatype = AvailableDatatypes.getDatatypeForInput(rightString);
			if (leftDatatype instanceof BooleanEntityDatatype && rightDatatype instanceof BooleanEntityDatatype) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	public boolean calculateValue(double left, double right) {
		return false;
	}
	
	public boolean calculateValue(boolean left, boolean right) {
		return false;
	}
	
	public boolean calculateValue(String left, String right) {
		return false;
	}
}
