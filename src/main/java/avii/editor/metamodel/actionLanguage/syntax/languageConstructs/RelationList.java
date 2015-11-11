/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.actionLanguage.syntax.languageConstructs;

import java.util.ArrayList;
import java.util.Iterator;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.IActionLanguageRelation;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.IRelationList;

public class RelationList implements IRelationList {

	private String line;
	private ArrayList<IActionLanguageRelation> relations = new ArrayList<IActionLanguageRelation>(); 
	
	public RelationList(String line)
	{
		this.line = line;
		computeRelations();
	}
	
	public boolean isEmpty()
	{
		return relations.isEmpty();
	}
	
	private void computeRelations()
	{
		String[] tokens = line.split("->");
		for(int i = 0 ; i<tokens.length;i++)
		{
			String[] nameAndVerb = tokens[i].split("\\.\"");
			String name = nameAndVerb[0];
			String verb;
			verb = null;
			if(nameAndVerb.length == 2)
			{	
				verb = nameAndVerb[1].substring(0, nameAndVerb[1].length()-1);
			}
			
			ActionLanguageRelation relation = new ActionLanguageRelation(name,verb);
			relations.add(relation);
		}
	}
	
	public String GetInitialInstance()
	{
		return relations.get(0).get_Name();
	}
	
	
	public Iterator<IActionLanguageRelation> iterator() {
		ArrayList<IActionLanguageRelation> relationNames = new ArrayList<IActionLanguageRelation>();
		relationNames.addAll(relations);
		relationNames.remove(0);
		return relationNames.iterator();
	}

	public void renameRelation(String oldRelationName, String newRelationName) {
		for(int i = 0; i < relations.size(); i ++)
		{
			if(relations.get(i).get_Name().equals(oldRelationName))
			{
				relations.get(i).set_Name(newRelationName);
			}
		}
	}
	
}
