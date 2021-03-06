package main.java.avii.editor.metamodel.weaving.exception;

import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.weaving.DomainWeave;

@SuppressWarnings("serial")
public class DomainNotInWeaveException extends Exception {

	private EntityDomain _entityDomain = null;
	private DomainWeave _domainWeave;

	public DomainNotInWeaveException(EntityDomain theEntityDomain, DomainWeave theDomainWeave) {
		this._entityDomain = theEntityDomain;
		this._domainWeave = theDomainWeave;
	}

	@Override
	public String toString() {
		String message = "Domain : '"
				+ this._entityDomain.getName()
				+ "' is not registered in DomainWeave '"+ this._domainWeave.getName() +"'.";
		return message;
	}
}
