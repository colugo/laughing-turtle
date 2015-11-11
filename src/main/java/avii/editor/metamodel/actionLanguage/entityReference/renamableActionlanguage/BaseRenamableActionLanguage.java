package main.java.avii.editor.metamodel.actionLanguage.entityReference.renamableActionlanguage;

import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.NameNotFoundException;
import javax.naming.OperationNotSupportedException;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.CannotInterpretExpressionNodeException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.EncounteredNonItemLeafNodeException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.ILogicNode;
import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageToken;
import main.java.avii.editor.metamodel.actionLanguage.instanceLifespan.StateInstanceLifespanManager;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.LogicExpressionTree;
import main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs.logic.ConstantToken;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenAttribute;
import main.java.avii.editor.metamodel.actionLanguage.token.ActionLanguageTokenIdentifier;

public abstract class BaseRenamableActionLanguage implements IRenamableActionLanguage {

	public void setOriginalSyntax(IActionLanguageSyntax syntax) {
	}

	public void renameClass(String newClassName) {
	}

	public void renameEvent(String newEventName) {
	}

	public void renameRelation(String oldRelationName, String theNewRelationName) {
	}

	public void renameAttribute(String theClassNeme, String theOldAttributeName, String theNewAttributeName,
			StateInstanceLifespanManager instanceLifespanManager, int lineNumber) {
	}

	public void renameEventParam(String theOldParamName, String theNewParamName) {
	}

	protected String renameAttributeInLogicExpressionTree(String theClassNeme, String theOldAttributeName, String theNewAttributeName,
			StateInstanceLifespanManager instanceLifespanManager, LogicExpressionTree tree, int lineNumber) throws CannotInterpretExpressionNodeException,
			NameNotFoundException, OperationNotSupportedException, EncounteredNonItemLeafNodeException {
		ILogicNode expressionRootNode = tree.getRootNode();
		ArrayList<ILogicNode> leafNodes = new ArrayList<ILogicNode>();
		expressionRootNode.getLeafNodes(leafNodes);
		for (ILogicNode node : leafNodes) {
			if (node instanceof ConstantToken) {
				ConstantToken constantNode = (ConstantToken) node;
				String tokenValue = node.getTokenValue();
				IActionLanguageToken nodeToken = ActionLanguageTokenIdentifier.IdentifyToken(tokenValue);
				if (nodeToken instanceof ActionLanguageTokenAttribute) {
					ActionLanguageTokenAttribute attributeToken = (ActionLanguageTokenAttribute) nodeToken;
					String instanceName = attributeToken.getInstanceName();
					String className = instanceLifespanManager.identifyInstance(instanceName, lineNumber);
					String attributeName = attributeToken.getAttributeName();

					if (className.equals(theClassNeme) && attributeName.equals(theOldAttributeName)) {
						constantNode.forceInternalValue(instanceName + "." + theNewAttributeName);
					}
				}
			}
		}
		return expressionRootNode.asStringWithoutIntroducingBrackets();
	}

	protected void renameAttributeInParamMap(String theClassNeme, String theOldAttributeName, String theNewAttributeName,
			StateInstanceLifespanManager instanceLifespanManager, int lineNumber, HashMap<String, String> params) throws NameNotFoundException,
			OperationNotSupportedException {
		for (String paramName : params.keySet()) {
			String paramValue = params.get(paramName);
			IActionLanguageToken paramToken = ActionLanguageTokenIdentifier.IdentifyToken(paramValue);
			if (paramToken instanceof ActionLanguageTokenAttribute) {
				ActionLanguageTokenAttribute attributeToken = (ActionLanguageTokenAttribute) paramToken;
				String instanceName = attributeToken.getInstanceName();
				String attributeName = attributeToken.getAttributeName();

				String className = instanceLifespanManager.identifyInstance(instanceName, lineNumber);
				if (className.equals(theClassNeme) && attributeName.equals(theOldAttributeName)) {
					params.put(paramName, instanceName + "." + theNewAttributeName);
				}
			}
		}
	}

	protected void renameEventParamInParamMap(String theOldParamName, String theNewParamName, HashMap<String, String> params) {
		String paramValue = params.get(theOldParamName);
		params.remove(theOldParamName);
		params.put(theNewParamName, paramValue);
	}

}
