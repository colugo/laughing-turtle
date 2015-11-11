package main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.validation;

import java.util.HashMap;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.logic.expressionTree.ILogicNode;

public class LogicTreeValidationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7408333858788835181L;

	public enum LogicTreeValidationExceptionCodes{e0000, e0001, e0002, e0003, e0004,e0005,e0006};
	
	ILogicNode _root;
	public LogicTreeValidationExceptionCodes _code;
	
	private static HashMap<LogicTreeValidationExceptionCodes,String> _errors = new HashMap<LogicTreeValidationExceptionCodes,String>()
	{/**
		 * 
		 */
		private static final long serialVersionUID = -8516792116717803412L;

	{
		put(LogicTreeValidationExceptionCodes.e0000,"There is an unknown error, perhaps a malformed expression.");
		put(LogicTreeValidationExceptionCodes.e0001,"All equality operators must have left and right operands.");
		put(LogicTreeValidationExceptionCodes.e0002,"Equality operators cannot have equality operator children.");
		put(LogicTreeValidationExceptionCodes.e0003,"Boolean operators cannot have arithmetic operator children.");
		put(LogicTreeValidationExceptionCodes.e0004,"Arithmeitc operators cannot have boolean operator children.");
		put(LogicTreeValidationExceptionCodes.e0005,"Both sides of an operator must have compatible datatypes.");
		put(LogicTreeValidationExceptionCodes.e0006,"Plus (+) is the only valid operator (including brackets) when dealing with strings.");
	}};
	
	public LogicTreeValidationException(ILogicNode theRoot, LogicTreeValidationExceptionCodes theCode)
	{
		this._root = theRoot;
		this._code = theCode;
	}
	
	public String toString()
	{
		String output = _errors.get(this._code) +"\n";
		output += this._root.asString() + "\n\n";
		return output;
	}
}
