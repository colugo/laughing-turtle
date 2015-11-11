package main.java.avii.editor.metamodel.actionLanguage.codeBlock;

import main.java.avii.editor.contracts.metamodel.actionLanguage.codeBlock.ICodeBlockClose;
import main.java.avii.editor.contracts.metamodel.actionLanguage.codeBlock.ICodeBlockOpen;

public class CodeBlockElse extends BaseCodeBlock implements ICodeBlockOpen, ICodeBlockClose {

	public boolean acceptsCloseBlock(ICodeBlockClose codeBlockClose) {
		return codeBlockClose instanceof CodeBlockCloseIf;
	}

}
