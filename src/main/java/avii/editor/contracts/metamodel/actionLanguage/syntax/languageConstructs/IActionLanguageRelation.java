package main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs;

public interface IActionLanguageRelation {

	public abstract String get_Name();

	public abstract String get_VerbPhrase();

	public abstract String toString();

	public abstract void set_Name(String newRelationName);

}