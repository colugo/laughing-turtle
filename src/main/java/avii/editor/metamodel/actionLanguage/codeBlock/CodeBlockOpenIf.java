package main.java.avii.editor.metamodel.actionLanguage.codeBlock;

import main.java.avii.editor.contracts.metamodel.actionLanguage.codeBlock.ICodeBlockClose;
import main.java.avii.editor.contracts.metamodel.actionLanguage.codeBlock.ICodeBlockOpen;

public class CodeBlockOpenIf extends BaseCodeBlock implements ICodeBlockOpen {

	public boolean acceptsCloseBlock(ICodeBlockClose codeBlockClose) {
		return
			(codeBlockClose instanceof CodeBlockCloseIf) ||
			(codeBlockClose instanceof CodeBlockElse);
	}

}
