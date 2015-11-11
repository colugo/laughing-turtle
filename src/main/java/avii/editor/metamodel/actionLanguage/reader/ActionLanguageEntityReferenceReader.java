package main.java.avii.editor.metamodel.actionLanguage.reader;

import java.io.InputStream;
import java.util.ArrayList;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.BaseEntityReferenceVisitor;
import main.java.avii.editor.metamodel.actionLanguage.instanceLifespan.StateInstanceLifespanManager;
import main.java.avii.editor.metamodel.actionLanguage.validation.IVisitableActionLanguage;

public class ActionLanguageEntityReferenceReader extends ActionLanguageReader {

	private StateInstanceLifespanManager _instanceLifespanManager;
	private BaseEntityReferenceVisitor _visitor;
	
	private ArrayList<IActionLanguageSyntax> _foundSyntax = new ArrayList<IActionLanguageSyntax>();

	public ActionLanguageEntityReferenceReader(InputStream in, StateInstanceLifespanManager instanceLifespanManager, BaseEntityReferenceVisitor visitor) {
		super(in);
		this._instanceLifespanManager = instanceLifespanManager;
		this._visitor = visitor;
		this._visitor.setInstanceLifespanManager(this._instanceLifespanManager);
	}

	@Override
	public void foundSyntaxLine(IActionLanguageSyntax lineSyntax, int lineNumber, int fileOffset, String line) {
		if (lineSyntax instanceof IVisitableActionLanguage) {
			IVisitableActionLanguage validatableActionLanguage = (IVisitableActionLanguage) lineSyntax;
			validatableActionLanguage.accept(_visitor, lineNumber);
			_foundSyntax.add(lineSyntax);
		}
	}

	public ArrayList<IActionLanguageSyntax> getFoundSyntax() {
		return this._foundSyntax;
	}
}
