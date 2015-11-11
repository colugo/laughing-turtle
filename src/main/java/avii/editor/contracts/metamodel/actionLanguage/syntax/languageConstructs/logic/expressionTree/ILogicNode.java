package main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree;

import java.util.ArrayList;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.validation.LogicTreeValidationException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageTokenIdentifier;
import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;

public interface ILogicNode {
	public String asStringWithoutIntroducingBrackets();
	public void clearValue();
	public void setValue(String newValue);
	public String getTokenValue();
	public Boolean getHasBeenArithmeticProcessed();
	public void setHasBeenArithmeticProcessed();
	public Boolean getHasBeenEqualityProcessed();
	public void setHasBeenEqualityProcessed();
	public Boolean getHasBeenBooleanProcessed();
	public void setHasBeenBooleanProcessed();
	public String asString();
	public String getRegex();
	public ArrayList<LogicTreeValidationException> getValidationErrors();
	public void validate(IActionLanguageTokenIdentifier tokenIdentifier);
	public void getLeafNodes(ArrayList<ILogicNode> leafNodes);
	public void getAllNodes(ArrayList<ILogicNode> leafNodes);
	public Boolean hasChildren();
	public IEntityDatatype getDatatype(IActionLanguageTokenIdentifier tokenIdentifier);
}
