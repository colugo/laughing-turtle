package main.java.avii.editor.metamodel.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.contracts.metamodel.entities.CannotRefactorInvalidDomainException;
import main.java.avii.editor.metamodel.actionLanguage.entityReference.EntityRenameManager;
import main.java.avii.editor.metamodel.actionLanguage.validation.EntityDomainValidator;
import main.java.avii.scenario.TestScenario;

public class EntityDomain {

	private String Name;
	private ArrayList<EntityClass> _classes = new ArrayList<EntityClass>();
	private ArrayList<TestScenario> _scenarios = new ArrayList<TestScenario>();
	
	private boolean isDomainValid()
	{
		EntityDomainValidator validator = new EntityDomainValidator(this);
		boolean result = validator.validate();
		return result;
	}
	
	public Collection<EntityClass> getClasses()
	{
		return _classes;
	}
	
	public EntityClass getEntityClassWithName(String className)
	{
		for(EntityClass theClass : this._classes)
		{
			if(theClass.getName().equals(className))
			{
				return theClass;
			}
		}
		return null;
	}
	
	public EntityClass getEntityClassWithId(String classId)
	{
		for(EntityClass theClass : this._classes)
		{
			if(theClass.getId().equals(classId))
			{
				return theClass;
			}
		}
		return null;
	}
	
	public int howManyClasses()
	{
		return _classes.size();
	}
	
	public String getName()
	{
		return Name;
	}
	
	public void setName(String theName)
	{
		this.Name = theName;
	}
	
	public EntityDomain(String theDomainName)
	{
		this.Name = theDomainName;
	}

	public void addClass(EntityClass theClass) {
		theClass.setDomain(this);
		_classes.add(theClass);
	}

	public void renameClass(String classId, String toClassName) throws NamingException, CannotRefactorInvalidDomainException
	{
		if(!isDomainValid())
		{
			throw new CannotRefactorInvalidDomainException();
		}
		
		if(hasEntityClassWithName(toClassName))
		{
			throw new NamingException("Cannot rename class, as class with target name already exists");
		}
		if(!hasEntityClassWithId(classId))
		{
			throw new NamingException("Cannot find class with specified id to rename");
		}
		
		EntityClass fromClass = getEntityClassWithId(classId);
		
		renameClassInActionLanguage(fromClass, toClassName);
		
		fromClass.setName(toClassName);
		
		recalculateAllProcedures();
	}
	
	private void renameClassInActionLanguage(EntityClass classToRename, String newClassName)
	{
		try
		{
			for(EntityProcedure procedure : this.allProceduresInDomain())
			{
				if(procedure.hasContent())
				{
					EntityRenameManager entityRenameManager = procedure.getEntityRenameManager();
					String updatedProcdure = entityRenameManager.renameEntityClass(classToRename,newClassName);
					procedure.setProcedure(updatedProcdure);
				}
			}
		}catch(Exception e){
			// this cant happen, the domain has been validated
		}
	}

	private void recalculateAllProcedures()
	{
		for(EntityProcedure procedure : this.allProceduresInDomain())
		{
			if(procedure.hasContent())
			{
				try {
					procedure.calculateInstanceLifespanMap();
				} catch (InvalidActionLanguageSyntaxException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private ArrayList<EntityProcedure> allProceduresInDomain() {
		ArrayList<EntityProcedure> procedures = new ArrayList<EntityProcedure>();
		for(EntityClass theClass : this._classes)
		{
			for(EntityState theState: theClass.getStates())
			{
				EntityProcedure procedure = theState.getProcedure();
				procedures.add(procedure);
			}
		}
		return procedures;
	}

	public EntityRelation getRelationWithName(String theRelationName) throws NameNotFoundException 
	{
		// one of the classes (probably 2) will have the relation
		for(EntityClass klass : _classes)
		{
			if(klass.hasRelation(theRelationName))
			{
				return klass.getRelationWithName(theRelationName);
			}
		}
		throw new NameNotFoundException("Could not find a relationship with name " + theRelationName);
	}
	
	public EntityRelation getRelationWithId(String theRelationId)
	{
		// one of the classes (probably 2) will have the relation
		for(EntityClass klass : _classes)
		{
			if(klass.hasRelationWithId(theRelationId))
			{
				return klass.getRelationWithId(theRelationId);
			}
		}
		return null;
	}

	public boolean hasEntityClassWithName(String theClassName) {
		return this.getEntityClassWithName(theClassName) != null;
	}
	
	public boolean hasEntityClassWithId(String theClassId) {
		return this.getEntityClassWithId(theClassId) != null;
	}

	public boolean hasRelationWithName(String theRelationName) {
		// one of the classes (probably 2) will have the relation
		for(EntityClass klass : _classes)
		{
			if(klass.hasRelation(theRelationName))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean hasRelationWithId(String theRelationId) {
		// one of the classes (probably 2) will have the relation
		for(EntityClass klass : _classes)
		{
			if(klass.hasRelationWithId(theRelationId))
			{
				return true;
			}
		}
		return false;
	}

	public Collection<String> getClassNames() {
		ArrayList<String> classNames = new ArrayList<String>();
		for(EntityClass klass : this.getClasses())
		{
			classNames.add(klass.getName());
		}
		return classNames;
	}
	
	public Collection<String> getClassIds() {
		ArrayList<String> classIds = new ArrayList<String>();
		for(EntityClass klass : this.getClasses())
		{
			classIds.add(klass.getId());
		}
		return classIds;
	}

	public Collection<EntityRelation> getRelations() {
		HashSet<EntityRelation> relations = new HashSet<EntityRelation>();
		for(EntityClass klass : _classes)
		{
			for(EntityRelation relation : klass.getRelations())
			{
				relations.add(relation);
			}
		}
		return relations;
	}

	public void addScenario(TestScenario scenario) {
		this._scenarios.add(scenario);
		scenario.setDomain(this);
	}

	public ArrayList<TestScenario> getScenarios() {
		return this._scenarios;
	}

	public void deleteClass(EntityClass theClass) {
		this._classes.remove(theClass);
		ArrayList<String> relationIdsToDelete = new ArrayList<String>();
		for(EntityRelation relation : theClass.getRelations()){
			relationIdsToDelete.add(relation.getId());
		}
		for(String relationId : relationIdsToDelete){
			theClass.getDomain().deleteRelationWithId(relationId);
		}
	}

	public void deleteRelationWithId(String relationId) {
		EntityRelation relation = this.getRelationWithId(relationId);

		if(relation.hasAssociation())
		{
			relation.clearAssociation();
		}
		
		relation.getClassA().removeRelation(relation);
		relation.getClassB().removeRelation(relation);
	}

}
