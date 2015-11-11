package main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.ILogicToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.IMatchedToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.ILogicNode;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.ILogicParentNode;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.validation.LogicTreeValidationException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageTokenIdentifier;
import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.contracts.metamodel.entities.helpers.datatypes.CouldNotDetermineDatatypeFromValueException;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.MatchedToken;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenIdentifier;
import main.java.avii.editor.metamodel.entities.datatypes.AvailableDatatypes;
import main.java.avii.editor.metamodel.entities.datatypes.InvalidEntityDatatype;

public class BaseLogicNode implements ILogicNode {

	
	protected String internalValue = null;
	private Boolean _equalityProcessed = false;
	private Boolean _booleanProcessed = false;
	private Boolean _arithmeticProcessed = false;
	private ArrayList<LogicTreeValidationException> _errors = new ArrayList<LogicTreeValidationException>();

	public String getTokenValue() {
		return internalValue;
	}
	
	public void setValue(String newValue)
	{
		this.internalValue = newValue;
	}

	public void clearValue()
	{
		this.internalValue = "";
	}
	
	public Boolean getHasBeenEqualityProcessed() {
		return _equalityProcessed;
	}

	public void setHasBeenEqualityProcessed() {
		_equalityProcessed = true;
	}

	public Boolean getHasBeenBooleanProcessed() {
		return _booleanProcessed;
	}

	public void setHasBeenBooleanProcessed() {
		_booleanProcessed = true;
	}

	public Boolean getHasBeenArithmeticProcessed() {
		return _arithmeticProcessed;
	}

	public void setHasBeenArithmeticProcessed() {
		_arithmeticProcessed = true;
	}

	public String asString() {
		if (this instanceof ILogicParentNode) {
			String output = "";
			ILogicParentNode self = (ILogicParentNode) this;
			output += "(";

			output += getChildValue(self.getLeft());
			output += self.getTokenValue();
			output += getChildValue(self.getRight());

			output += ")";
			return output;
		}
		return getTokenValue();
	}

	public void getLeafNodes(ArrayList<ILogicNode> leafNodes) {
		if (this instanceof ILogicParentNode) {
			ILogicParentNode self = (ILogicParentNode) this;
			if (!self.hasChildren()) {
				addSelfToLeafNodes(leafNodes);
			}
			if (self.getLeft() != null) {
				self.getLeft().getLeafNodes(leafNodes);
			}
			if (self.getRight() != null) {
				self.getRight().getLeafNodes(leafNodes);
			}
		} else {
			addSelfToLeafNodes(leafNodes);
		}
	}

	public void getAllNodes(ArrayList<ILogicNode> leafNodes) {
		if (this instanceof ILogicParentNode) {
			ILogicParentNode self = (ILogicParentNode) this;

			if (self.getLeft() != null) {
				self.getLeft().getAllNodes(leafNodes);
			}
			addSelfToLeafNodes(leafNodes);

			if (self.getRight() != null) {
				self.getRight().getAllNodes(leafNodes);
			}
		} else {
			addSelfToLeafNodes(leafNodes);
		}
	}

	private void addSelfToLeafNodes(ArrayList<ILogicNode> leafNodes) {
		leafNodes.add(this);
	}

	private String getChildValue(ILogicNode child) {
		if (child == null) {
			return "";
		}
		return child.asString();
	}
	
	private String getChildValueWithoutIntroducingBrackets(ILogicNode child) {
		if (child == null) {
			return "";
		}
		return child.asStringWithoutIntroducingBrackets();
	}

	public IMatchedToken matchesString(String line, int offset) {
		Pattern p = Pattern.compile(getRegex());
		Matcher m = p.matcher(line);
		if (m.find()) {
			IMatchedToken match = new MatchedToken((ILogicToken) this, m.group(0));
			internalValue = m.group(0);
			return match;
		} else {
			return MatchedToken.NoMatch();
		}
	}

	public String getRegex() {
		return null;
	}

	public String toString() {
		return "\"" + internalValue + "\"";
	}

	public ArrayList<LogicTreeValidationException> getValidationErrors() {
		ArrayList<LogicTreeValidationException> outputErrors = new ArrayList<LogicTreeValidationException>();
		outputErrors.addAll(_errors);
		_errors = new ArrayList<LogicTreeValidationException>();
		return outputErrors;
	}

	public void validate(IActionLanguageTokenIdentifier tokenIdentifier) {
	}

	protected void addError(ILogicNode theNode, LogicTreeValidationException.LogicTreeValidationExceptionCodes theCode) {
		LogicTreeValidationException error = new LogicTreeValidationException(theNode, theCode);
		this._errors.add(error);
	}
	
	protected void addError(LogicTreeValidationException error)
	{
		this._errors.add(error);
	}

	public Boolean hasChildren() {
		if (this instanceof ILogicParentNode) {
			ILogicParentNode self = (ILogicParentNode) this;
			if (self.getLeft() != null || self.getRight() != null) {
				return true;
			}
		}
		return false;
	}

	public IEntityDatatype getDatatype(IActionLanguageTokenIdentifier tokenIdentifier) {
		if (tokenIdentifier == null) {
			String value = this.getTokenValue();
			IEntityDatatype theDataytpe = InvalidEntityDatatype.getInstance();
			try {
				theDataytpe = AvailableDatatypes.getDatatypeForInput(value);
			} catch (CouldNotDetermineDatatypeFromValueException e) {
			}
			return theDataytpe;
		}
		IActionLanguageToken token = ActionLanguageTokenIdentifier.IdentifyToken(this.getTokenValue());
		return tokenIdentifier.getDatatypeForToken(token);
	}

	public String asStringWithoutIntroducingBrackets() {
		if (this instanceof ILogicParentNode) {
			String output = "";
			ILogicParentNode self = (ILogicParentNode) this;
			output += getChildValueWithoutIntroducingBrackets(self.getLeft());
			output += " ";
			output += self.getTokenValue();
			output += " ";
			output += getChildValueWithoutIntroducingBrackets(self.getRight());
			return output;
		}
		return getTokenValue();
	}

}
