package main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs;

import java.util.Iterator;

public interface IRelationList extends Iterable<IActionLanguageRelation> {

	public boolean isEmpty();

	public String GetInitialInstance();

	public Iterator<IActionLanguageRelation> iterator();

}