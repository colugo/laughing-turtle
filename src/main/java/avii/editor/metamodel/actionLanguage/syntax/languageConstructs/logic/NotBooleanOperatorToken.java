/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.ILogicToken;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.ILogicNode;
import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageTokenIdentifier;
import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.BaseLogicNode;
import main.java.avii.editor.metamodel.entities.datatypes.BooleanEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.InvalidEntityDatatype;

public class NotBooleanOperatorToken extends BaseLogicNode implements ILogicToken, ILogicNode {
	String regex = "^NOT";
	private ILogicNode _negatedRoot;

	public String getRegex() {
		return regex;
	}

	public void addNegatedRoot(ILogicNode negatedRoot) {
		this._negatedRoot = negatedRoot;
		
	}
	
	public IEntityDatatype getDatatype(IActionLanguageTokenIdentifier tokenIdentifier) {
		IEntityDatatype negatedRootDatatype = this._negatedRoot.getDatatype(tokenIdentifier);
		if(negatedRootDatatype instanceof BooleanEntityDatatype)
		{
			return BooleanEntityDatatype.getInstance();
		}
		return InvalidEntityDatatype.getInstance();
	}

}
