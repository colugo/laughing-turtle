package main.java.avii.editor.metamodel.entities.helpers;

import java.util.ArrayList;

import javax.naming.NameNotFoundException;

import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityRelation;

public class EntityRelationChainHelper {

	public static String determineEndpointType(EntityDomain theDomain, ArrayList<String> orderedRelations, String startingPointClassName) throws NameNotFoundException {
		return determineEndPoint(theDomain, orderedRelations, startingPointClassName).getName();
	}

	private static EntityClass determineEndPoint(EntityDomain theDomain, ArrayList<String> orderedRelations, String startingPointClassName) throws NameNotFoundException {
		EntityRelation firstRelation = getFirstRelationFromList(theDomain, orderedRelations);
		EntityClass oppositeClass = getOppositeEndOfFirstRelation(startingPointClassName, firstRelation);
		EntityClass endingClass = getClassAtEndOfChain(orderedRelations, oppositeClass);
		return endingClass;
	}
	
	private static EntityClass getClassAtEndOfChain(ArrayList<String> orderedRelations, EntityClass oppositeClass) throws NameNotFoundException {
		while(haveMoreRelations(orderedRelations))
		{
			oppositeClass = identifyNextOppositeEnd(oppositeClass, orderedRelations); 
		}
		return oppositeClass;
	}
	
	private static boolean haveMoreRelations(ArrayList<String> orderedRelations)
	{
		return !orderedRelations.isEmpty();
	}
	
	private static EntityClass identifyNextOppositeEnd(EntityClass currentClass, ArrayList<String> orderedRelations) throws NameNotFoundException
	{
		String firstRelationName = removeFirstRelationFromList(orderedRelations);
		EntityRelation theRelation = currentClass.getRelationWithName(firstRelationName);
		EntityClass oppositeEnd = theRelation.getOppositeClass(currentClass);
		return oppositeEnd;
	}
	
	private static EntityRelation getFirstRelationFromList(EntityDomain theDomain, ArrayList<String> orderedRelations) throws NameNotFoundException
	{
		String firstRelationName = removeFirstRelationFromList(orderedRelations);
		EntityRelation firstRelation = theDomain.getRelationWithName(firstRelationName);
		return firstRelation;
	}

	private static String removeFirstRelationFromList(ArrayList<String> orderedRelations) {
		String firstRelationName = orderedRelations.get(0);
		orderedRelations.remove(0);
		return firstRelationName;
	}

	private static EntityClass getOppositeEndOfFirstRelation(String startPointClassName, EntityRelation theRelation) throws NameNotFoundException
	{
		EntityClass oppositeClass = theRelation.getOppositeClass(startPointClassName);
		return oppositeClass;
	}
	
}
