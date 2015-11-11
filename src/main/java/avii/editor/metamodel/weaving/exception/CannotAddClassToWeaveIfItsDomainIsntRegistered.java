package main.java.avii.editor.metamodel.weaving.exception;

import main.java.avii.editor.metamodel.entities.EntityClass;

@SuppressWarnings("serial")
public class CannotAddClassToWeaveIfItsDomainIsntRegistered extends Exception {

	private EntityClass _entityClass = null;
	
	public CannotAddClassToWeaveIfItsDomainIsntRegistered(EntityClass theEntityClass)
	{
		this._entityClass = theEntityClass;
	}
	
	@Override
	public String toString()
	{
		String message = "Class '" + this._entityClass.getName()+ "' cannot be added to ClassWeave because it's domain '" + this._entityClass.getDomain().getName() + "' is not added to the DomainWeave.";
		return message;
	}
}
