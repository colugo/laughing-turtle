package main.java.avii.editor.metamodel.weaving;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.weaving.exception.CannotAddDuplicateDomainToDomainWeaveException;
import main.java.avii.editor.metamodel.weaving.exception.ClassesFromDomainAreUsedInClassWeavesException;
import main.java.avii.editor.metamodel.weaving.exception.DomainNotInWeaveException;

public class DomainWeave {

	private String _domainWeaveName = null;
	private HashSet<EntityDomain> _wovenDomains = new HashSet<EntityDomain>();
	private ArrayList<ClassWeave> _classWeaves = new ArrayList<ClassWeave>();

	public DomainWeave(String theNewWeaveName) {
		this._domainWeaveName = theNewWeaveName;
	}

	public String getName() {
		return _domainWeaveName;
	}

	public void setName(String theNewWeaveName) {
		this._domainWeaveName = theNewWeaveName;
	}

	public void registerDomain(EntityDomain theNewDomain) throws CannotAddDuplicateDomainToDomainWeaveException {
		if (this._wovenDomains.contains(theNewDomain)) {
			throw new CannotAddDuplicateDomainToDomainWeaveException(theNewDomain, this);
		}
		this._wovenDomains.add(theNewDomain);
	}

	public boolean hasRegisteredDomain(EntityDomain theDomain) {
		return this._wovenDomains.contains(theDomain);
	}

	public Collection<EntityDomain> getWovenDomains() {
		return this._wovenDomains;
	}

	public ClassWeave createClassWeave() {
		ClassWeave newClassWeave = new ClassWeave(this, this._classWeaves.size() + 1);
		this._classWeaves.add(newClassWeave);
		return newClassWeave;
	}

	public void deregisterDomain(EntityDomain theDomain) throws DomainNotInWeaveException, ClassesFromDomainAreUsedInClassWeavesException {
		if (!hasRegisteredDomain(theDomain)) {
			throw new DomainNotInWeaveException(theDomain, this);
		}

		checkToBeDeregisteredDomainIsNotUsedInWeaving(theDomain);

		this._wovenDomains.remove(theDomain);
	}

	private void checkToBeDeregisteredDomainIsNotUsedInWeaving(EntityDomain theDomain) throws ClassesFromDomainAreUsedInClassWeavesException {
		ClassesFromDomainAreUsedInClassWeavesException classesUsedException = new ClassesFromDomainAreUsedInClassWeavesException(theDomain);
		for (ClassWeave classWeave : this._classWeaves) {
			if (classWeave.areAnyClassesFromDomainRegistered(theDomain)) {
				classesUsedException.addClassWeave(classWeave);
			}
		}
		if (classesUsedException.isException()) {
			throw classesUsedException;
		}
	}

	public Collection<ClassWeave> getClassWeaves() {
		return this._classWeaves;
	}

	public boolean hasClassBeenWoven(EntityClass theEntityClass) {
		EntityDomain theDomain = theEntityClass.getDomain();
		if (!this.hasRegisteredDomain(theDomain)) {
			return false;
		}

		for (ClassWeave classWeave : this._classWeaves) {
			if (classWeave.isClassRegistered(theEntityClass)) {
				return true;
			}
		}

		return false;
	}

	public EntityDomain getDomainWithName(String domainName) {
		for(EntityDomain domain : this._wovenDomains)
		{
			if(domain.getName().equals(domainName))
			{
				return domain;
			}
		}
		return null;
	}

	public ClassWeave getClassWeaveContainingClass(EntityClass theClass)
	{
		
		for(ClassWeave weave : this._classWeaves)
		{
			if(weave.isClassRegistered(theClass))
			{
				return weave;
			}
		}
		return null;
	}
	
}
