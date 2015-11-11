package main.java.avii.editor.metamodel.weaving.exception;

import java.util.HashSet;

import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.weaving.ClassWeave;

@SuppressWarnings("serial")
public class ClassesFromDomainAreUsedInClassWeavesException extends Exception {
	private EntityDomain _theDomain = null;
	private HashSet<ClassWeave> _weaves = new HashSet<ClassWeave>();
	
	public ClassesFromDomainAreUsedInClassWeavesException (EntityDomain theDomain)
	{
		this._theDomain = theDomain;
	}

	public boolean isException() {
		return !this._weaves.isEmpty();
	}

	public void addClassWeave(ClassWeave theClassWeave) {
		this._weaves.add(theClassWeave);
	}
	
	@Override
	public String toString()
	{
		String message = "Cannot remove domain '" + this._theDomain.getName() + "' as it's classes are used in ClassWeaves.";
		return message;
	}
}
