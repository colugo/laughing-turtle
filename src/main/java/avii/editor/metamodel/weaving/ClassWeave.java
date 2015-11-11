package main.java.avii.editor.metamodel.weaving;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.weaving.exception.CannotAddClassToWeaveIfItsDomainIsntRegistered;
import main.java.avii.editor.metamodel.weaving.exception.CannotAddDuplicateClassToClassWeaveException;
import main.java.avii.editor.metamodel.weaving.exception.CannotAddSuperClassToClassWeaveException;
import main.java.avii.editor.metamodel.weaving.exception.CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException;
import main.java.avii.editor.metamodel.weaving.exception.ClassNotInWeaveException;

public class ClassWeave {

	private DomainWeave _domainWeave = null;
	private int _classWeaveIdentifier = Integer.MIN_VALUE;
	private HashSet<EntityClass> _wovenClasses = new HashSet<EntityClass>();
	private ArrayList<AttributeWeave> _attributeWeaves = new ArrayList<AttributeWeave>();
	private int _attributeWeaveId = 0;
	private ArrayList<EventWeave> _eventWeaves = new ArrayList<EventWeave>();

	public ClassWeave(DomainWeave theDomainWeave, int theIdentifier) {
		this._domainWeave = theDomainWeave;
		this._classWeaveIdentifier = theIdentifier;
	}

	public int getWeaveIdentifier() {
		return _classWeaveIdentifier;
	}

	public DomainWeave getDomainWeave() {
		return this._domainWeave;
	}

	public void registerClass(EntityClass theEntityClass) throws CannotAddSuperClassToClassWeaveException, CannotAddDuplicateClassToClassWeaveException,
			CannotAddClassToWeaveIfItsDomainIsntRegistered, CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException {
		EntityDomain theDomain = theEntityClass.getDomain();
		if (!this._domainWeave.hasRegisteredDomain(theDomain)) {
			throw new CannotAddClassToWeaveIfItsDomainIsntRegistered(theEntityClass);
		}
		if (theEntityClass.hasSubClasses()) {
			throw new CannotAddSuperClassToClassWeaveException(theEntityClass);
		}
		if (this.isClassRegistered(theEntityClass)) {
			throw new CannotAddDuplicateClassToClassWeaveException(theEntityClass, this);
		}
		if (areAnyClassesFromDomainRegistered(theEntityClass.getDomain())) {
			EntityClass existingClass = getExistingClassFromSameDomainAs(theEntityClass);
			throw new CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException(this, theEntityClass, existingClass);
		}

		this._wovenClasses.add(theEntityClass);
	}

	private EntityClass getExistingClassFromSameDomainAs(EntityClass theEntityClass) {
		EntityClass existingClass = null;
		for (EntityClass theClass : this._wovenClasses) {
			if (theClass.getDomain().equals(theEntityClass.getDomain())) {
				existingClass = theClass;
				break;
			}
		}
		return existingClass;
	}

	public boolean areAnyClassesFromDomainRegistered(EntityDomain theDomain) {
		for (EntityClass theClass : theDomain.getClasses()) {
			if (isClassRegistered(theClass)) {
				return true;
			}
		}
		return false;
	}

	public boolean isClassRegistered(EntityClass theEntityClass) {
		return this._wovenClasses.contains(theEntityClass);
	}

	public Collection<EntityClass> getRegisteredClasses() {
		return this._wovenClasses;
	}

	public void deregisterClass(EntityClass theEntityClass) throws ClassNotInWeaveException {
		throwExceptionIfClassNotInWeave(theEntityClass);
		this._wovenClasses.remove(theEntityClass);
	}

	public Collection<EntityClass> getWovenClasses() {
		Collection<EntityClass> allClasses = new ArrayList<EntityClass>();
		for (EntityClass theClass : this._wovenClasses) {
			allClasses.add(theClass);
		}
		return allClasses;
	}

	public Collection<EntityClass> getWovenClassesExceptFor(EntityClass theEntityClass) throws ClassNotInWeaveException {
		Collection<EntityClass> allClassesExceptFor = getWovenClasses();
		throwExceptionIfClassNotInWeave(theEntityClass);
		allClassesExceptFor.remove(theEntityClass);
		return allClassesExceptFor;
	}

	private void throwExceptionIfClassNotInWeave(EntityClass theEntityClass) throws ClassNotInWeaveException {
		if (!this.isClassRegistered(theEntityClass)) {
			throw new ClassNotInWeaveException(theEntityClass, this);
		}
	}

	public AttributeWeave createAttributeWeave() {
		AttributeWeave attributeWeave = new AttributeWeave(_attributeWeaveId++);
		this._attributeWeaves.add(attributeWeave);
		return attributeWeave;
	}

	public ArrayList<AttributeWeave> getAttributeWeaves() {
		return this._attributeWeaves;
	}

	public boolean isAttributeInAnyWeave(EntityAttribute theAttribute) {
		AttributeWeave weaveContainingAttribute = this.getAttributeWeaveContainingAttribute(theAttribute);
		return weaveContainingAttribute != null;
	}

	public AttributeWeave getAttributeWeaveContainingAttribute(EntityAttribute theAttribute) {
		for (AttributeWeave weave : this._attributeWeaves) {
			if (weave.isAttributeInWeave(theAttribute)) {
				return weave;
			}
		}
		return null;
	}

	public EventWeave createEventWeave() {
		EventWeave weave = new EventWeave();
		this._eventWeaves .add(weave);
		return weave;
	}

	public ArrayList<EventWeave> getEventWeaves() {
		return this._eventWeaves;
	}

	public boolean isEventInAnyWeave(EntityEventSpecification eventSpec) {
		EventWeave weave = this.getEventWeaveContainingAttribute(eventSpec);
		return weave != null;
	}

	public EventWeave getEventWeaveContainingAttribute(EntityEventSpecification eventSpec) {
		for(EventWeave weave : this._eventWeaves)
		{
			if(weave.isEventInWeave(eventSpec))
			{
				return weave;
			}
		}
		return null;
	}

}
