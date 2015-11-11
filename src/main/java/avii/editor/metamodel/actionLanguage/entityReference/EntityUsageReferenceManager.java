package main.java.avii.editor.metamodel.actionLanguage.entityReference;

import java.util.ArrayList;

import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_Return;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityEventParam;
import main.java.avii.editor.metamodel.entities.EntityEventSpecification;
import main.java.avii.editor.metamodel.entities.EntityRelation;

public class EntityUsageReferenceManager extends BaseEntityReferenceVisitor {

	public EntityUsageReferenceManager(EntityDomain domain) {
		super(domain);
	}

	public boolean isEntityClassReferenced(EntityClass theClass) {
		return this.hasEntityClassBeenReferenced(theClass);
	}

	public ArrayList<EntityClassReference> getEntityClassReferences(EntityClass theClass) {
		return this.getReferencesForEntityClass(theClass);
	}

	public boolean isEventReferenced(EntityEventSpecification theEvent) {
		return this.hasEventBeenReferenced(theEvent);
	}

	public ArrayList<EntityEventReference> getEventReferences(EntityEventSpecification theEvent) {
		return this.getReferencesForEvent(theEvent);
	}

	public boolean isEntityRelationReferenced(EntityRelation theRelation) {
		return this.hasRelationBeenReferenced(theRelation);
	}

	public ArrayList<EntityRelationReference> getEntityRelationshipReferences(EntityRelation theRelation) {
		return this.getReferencesForRelation(theRelation);
	}

	public boolean isEntityAttributeReferenced(EntityAttribute theAttribute) {
		return this.hasAttributeBeenReferenced(theAttribute);
	}
	
	public ArrayList<EntityAttributeReference> getEntityAttributeReferences(EntityAttribute theAttribute) {
		return this.getReferencesForAttribute(theAttribute);
	}

	public boolean isEntityEventParamReferenced(EntityEventParam theParam) {
		return this.hasEventParamBeenReferenced(theParam);
	}

	public ArrayList<EntityEventParamReference> getEntityEventParamReferences(EntityEventParam theParam) {
		return this.getReferencesForEventParam(theParam);
	}
	
	public void visit(Syntax_Return syntax_Return, int lineNumber) {
		// this method has been intentionally left blank
	}

}
