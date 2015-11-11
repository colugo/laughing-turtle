/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.entities;

import javax.naming.NameNotFoundException;


public class EntityRelation {

	public enum CardinalityType {
		ZERO_TO_ONE, ONE_TO_ONE, ONE_TO_MANY, ZERO_TO_MANY;
		public static String toHuman(CardinalityType cardinality)
	    {
			switch (cardinality) {
			case ZERO_TO_ONE:
				return "0..1";
			case ONE_TO_ONE:
				return "1..1";
			case ZERO_TO_MANY:
				return "0..*";
			case ONE_TO_MANY:
				return "1..*";
			}
			return "Error";
	    };
	    
	    public static CardinalityType fromHuman(String value)
	    {
			if(value.equals("0..1")){
				return ZERO_TO_ONE;
			}
			if(value.equals("1..1")){
				return ONE_TO_ONE;
			}
			if(value.equals("0..*")){
				return ZERO_TO_MANY;
			}
			return ONE_TO_MANY;
	    };
	};
	
	private String id;
	private String name;
	private EntityClass ClassA;
	private String ClassAVerb;
	private CardinalityType ClassACardinality;
	private EntityClass ClassB;
	private String ClassBVerb;
	private CardinalityType ClassBCardinality;

	private EntityClass _associationClass;

	// diagram specific things
	public int endAIndex = 0;
	public int endBIndex = 0;
	public int verbAOffsetX = 0;
	public int verbAOffsetY = 0;
	public int verbBOffsetX = 0;
	public int verbBOffsetY = 0;

	public EntityRelation(String relationName) {
		this.name = relationName;
		this.id = this.name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntityRelation other = (EntityRelation) obj;
		if (_associationClass != null) {
			if (other._associationClass == null)
				return false;
		}
		if (_associationClass == null) {
			if (other._associationClass != null)
				return false;
		} else if (!_associationClass.getName().equals(other._associationClass.getName()))
			return false;
		if (ClassA == null) {
			if (other.ClassA != null)
				return false;
		} else if (!ClassA.equals(other.ClassA))
			return false;
		if (ClassAVerb == null) {
			if (other.ClassAVerb != null)
				return false;
		} else if (!ClassAVerb.equals(other.ClassAVerb))
			return false;
		if (ClassBCardinality == null) {
			if (other.ClassBCardinality != null)
				return false;
		} else if (!ClassBCardinality.equals(other.ClassBCardinality))
			return false;
		if (ClassACardinality == null) {
			if (other.ClassACardinality != null)
				return false;
		} else if (!ClassACardinality.equals(other.ClassACardinality))
			return false;
		if (ClassB == null) {
			if (other.ClassB != null)
				return false;
		} else if (!ClassB.equals(other.ClassB))
			return false;
		if (ClassBVerb == null) {
			if (other.ClassBVerb != null)
				return false;
		} else if (!ClassBVerb.equals(other.ClassBVerb))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public EntityClass getClassA() {
		return ClassA;
	}

	public String getClassAVerb() {
		return ClassAVerb;
	}

	public EntityClass getClassB() {
		return ClassB;
	}

	public String getClassBVerb() {
		return ClassBVerb;
	}

	public void setAssociation(EntityClass associationClass) {
		if(associationClass == null)
		{
			this._associationClass = null;
			return;
		}
		if (associationClass.isAssociation() && !associationClass.getAssociationRelation().equals(this)) {
			associationClass.setAssociationRelation(this);
		}
		if(!associationClass.isAssociation())
		{
			associationClass.setAssociationRelation(this);
		}
		this._associationClass = associationClass;
	}

	public EntityClass getAssociation() {
		return _associationClass;
	}
	
	public boolean hasAssociation()
	{
		return _associationClass != null;
	}


	public EntityClass getOppositeClass(EntityClass theClass) throws NameNotFoundException
	{
		return this.getOppositeClass(theClass.getName());
	}
	
	
	public EntityClass getOppositeClass(String startPointClassName) throws NameNotFoundException {
		if(ClassA.getName().equals(startPointClassName))
		{
			return ClassB;
		}
		if(ClassB.getName().equals(startPointClassName))
		{
			return ClassA;
		}
		// check if classA has startPointClassName as one of its subclassses -> return classB
		if(ClassA.hasSubClassWithName(startPointClassName))
		{
			return ClassB;
		}
		// check if classB has startPointClassName as one of its subclassses -> return classA
		if(ClassB.hasSubClassWithName(startPointClassName))
		{
			return ClassA;
		}
		
		throw new NameNotFoundException("No class called " + startPointClassName + " exists in relation " + this);
	}
	
	public EntityClass getClassWithRelation(String relationName) throws NameNotFoundException
	{
		if(ClassA.hasRelation(relationName))
		{
			return ClassA;
		}
		if(ClassB.hasRelation(relationName))
		{
			return ClassB;
		}
		throw new NameNotFoundException("Neither class in relation : " + this + " has an exiting relation named : " + relationName);
	}
	
	public void setEndA(EntityClass klass, CardinalityType cardinality) {
		//remove relation from previous endA ( if it exists )
		if(this.ClassA != null){
			this.ClassA.removeRelation(this);
		}
		
		this.ClassA = klass;
		klass.addRelation(this);
		this.ClassACardinality = cardinality;
		this.ClassAVerb = "";
	}

	public void setEndA(EntityClass klass, CardinalityType cardinality, String verb) {
		//remove relation from previous endA ( if it exists )
		if(this.ClassA != null){
			this.ClassA.removeRelation(this);
		}
		
		this.ClassA = klass;
		klass.addRelation(this);
		this.ClassACardinality = cardinality;
		this.ClassAVerb = verb;
	}

	public void setEndB(EntityClass klass, CardinalityType cardinality) {
		//remove relation from previous endB ( if it exists )
		if(this.ClassB != null){
			this.ClassB.removeRelation(this);
		}
		
		this.ClassB = klass;
		klass.addRelation(this);
		this.ClassBCardinality = cardinality;
		this.ClassBVerb = "";
	}
	
	public void setEndB(EntityClass klass, CardinalityType cardinality, String verb) {
		//remove relation from previous endB ( if it exists )
		if(this.ClassB != null){
			this.ClassB.removeRelation(this);
		}
		
		this.ClassB = klass;
		klass.addRelation(this);
		this.ClassBCardinality = cardinality;
		this.ClassBVerb = verb;
	}

	public boolean isReflexive() {
		if(ClassA == null || ClassB == null)
		{
			return false;
		}
		
		boolean nonHierarchialReflexive = ClassA.equals(ClassB);
		boolean isBSuperClassOfA = ClassA.getAllSuperClasses().contains(ClassB);
		boolean isASuperClassOfB = ClassB.getAllSuperClasses().contains(ClassA);
		
		return nonHierarchialReflexive || isASuperClassOfB || isBSuperClassOfA;
	}
	
	public String toString()
	{
		return name + "[" + ClassA.getName() + " - " + ClassB.getName() + "]";
	}

	public boolean hasClassWithName(String className) {
		if(ClassA == null || ClassB == null)
		{
			return false;
		}
		boolean doesClassAHaveSameNameAsInputClass = ClassA.getName().equals(className);
		boolean doesClassBHaveSameNameAsInputClass = ClassB.getName().equals(className);
		boolean doesEitherClassMatchInputClassName = doesClassAHaveSameNameAsInputClass || doesClassBHaveSameNameAsInputClass;
		return doesEitherClassMatchInputClassName;
	}
	
	public boolean hasClass(EntityClass theClass) {
		boolean isTheClassPartOfClassA = theClass.isClassInParentPath(ClassA);
		boolean isTheClassPartOfClassB = theClass.isClassInParentPath(ClassB);
		return isTheClassPartOfClassA || isTheClassPartOfClassB;
	}

	public boolean acceptsVerbPhrase(String verbPhrase) {
		if(ClassAVerb.equals(verbPhrase) || ClassBVerb.equals(verbPhrase))
		{
			return true;
		}
		return false;
	}

	public void clearAssociation() {
		boolean hadAssociationClass = this.hasAssociation();
		EntityClass previousAssociationClass = _associationClass;
		this._associationClass = null;
		if(hadAssociationClass)
		{
			previousAssociationClass.setAssociationRelation(null);
		}
		
	}

	public CardinalityType getCardinalityA() {
		return this.ClassACardinality;
	}
	public CardinalityType getCardinalityB() {
		return this.ClassBCardinality;
	}

	public EntityClass getEndWithVerb(String verb) {
		if(this.ClassAVerb.equals(verb))
		{
			return this.ClassA;
		}
		return this.ClassB;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String newName) {
		this.name = newName;
	}

}
