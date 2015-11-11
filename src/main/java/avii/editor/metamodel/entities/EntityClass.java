/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.editor.metamodel.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.contracts.metamodel.entities.CannotCreateAttributeCalledStateException;
import main.java.avii.editor.metamodel.actionLanguage.eventManager.GeneratedEventManager;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;

public class EntityClass {

	private String _name;
	private String _id;
	private EntityState _initial = null;
	private EntityDomain _domain;
	private ArrayList<EntityAttribute> _attributes = new ArrayList<EntityAttribute>();
	private ArrayList<EntityClass> _superClasses = new ArrayList<EntityClass>();
	private ArrayList<EntityClass> _subClasses = new ArrayList<EntityClass>();
	private ArrayList<EntityEventSpecification> _events = new ArrayList<EntityEventSpecification>();
	private ArrayList<EntityRelation> _relations = new ArrayList<EntityRelation>();
	private EntityRelation _associationRelation = null;
	private ArrayList<EntityState> _states = new ArrayList<EntityState>();
	private boolean _isGeneralisation = false;
	private HashMap<EntityEventSpecification, ArrayList<EntityEventInstance>> _eventInstances = new HashMap<EntityEventSpecification, ArrayList<EntityEventInstance>>();
	public static final String DEFAULT_EVENT_SPEC_ID = "DefaultEventSpecification";
	
	// diagram things
	public Double x = 0.0;
	public Double y = 0.0;
	public int superClassTriangleIndex;

	public EntityClass(String name) {
		this._name = name;
		this._id = name;
		addStateAttribute();
		addDefaultEventSpecification();
	}
	
	private void addStateAttribute() {
		EntityAttribute state = new EntityAttribute(EntityAttribute.STATE_ATTRIBUTE_NAME, StringEntityDatatype.getInstance());
		_attributes.add(state);
		state.setClass(this);
	}

	private void addDefaultEventSpecification(){
		EntityEventSpecification defaultSpec = new EntityEventSpecification(this, DEFAULT_EVENT_SPEC_ID);
		this._eventInstances.put(defaultSpec, new ArrayList<EntityEventInstance>());
	}
	
	public Collection<EntityAttribute> getAttributes()
	{
		return this._attributes;
	}

	public ArrayList<EntityEventSpecification> getEventSpecifications()
	{
		if(this.isGeneralisation())
		{
			EntityClass classWithStates = this.getClassInHierarchyWithStates();
			return classWithStates._events;
		}
		else
		{
			return this._events;
		}
	}
	
	public void addEventSpecification(EntityEventSpecification currentEvent) {
		currentEvent.setClass(this);
		boolean found = false;
		for (EntityEventSpecification e : _events) {
			if (e.getName().equals(currentEvent.getName())) {
				found = true;
			}
		}
		if (!found) {
			_events.add(currentEvent);
		}
	}

	public void addState(EntityState theState){
		if(this.isGeneralisation())
		{
			this.getDelegateSuperClass()._addState(theState);
		}
		else
		{
			_addState(theState);
		}
	}

	public void _addState(EntityState theState){
		if(_states.isEmpty())
		{
			this.setInitial(theState);
		}
		
		_states.add(theState);
		theState.setOwningClass(this);
	}

	public void removeState(EntityState state) {
		_states.remove(state);
	}
	
	public void addSubClass(EntityClass theSubClass) {
		if (!this._subClasses.contains(theSubClass)) {
			this._isGeneralisation = true;
			this._subClasses.add(theSubClass);
			theSubClass.addSuperClass(this);
		}
	}

	public void addSuperClass(EntityClass theSuperClass) {
		if (!this._superClasses.contains(theSuperClass)) {
			this._isGeneralisation = true;
			this._superClasses.add(theSuperClass);
			theSuperClass.addSubClass(this);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntityClass other = (EntityClass) obj;
		if (_associationRelation == null) {
			if (other._associationRelation != null)
				return false;
		} else if (!_associationRelation.equals(other._associationRelation))
			return false;
		if (_initial == null) {
			if (other._initial != null)
				return false;
		} else if (!_initial.equals(other._initial))
			return false;
		if (_isGeneralisation != other._isGeneralisation)
			return false;
		if (_name == null) {
			if (other._name != null)
				return false;
		} else if (!_name.equals(other._name))
			return false;
		if (_domain == null) {
			if (other._domain != null)
				return false;
		} else if (!_domain.equals(other._domain))
			return false;

		return true;
	}

	private ArrayList<EntityClass> getAllSubClasses() {
		ArrayList<EntityClass> allSub = new ArrayList<EntityClass>();
		for (EntityClass sub : _subClasses) {
			allSub.add(sub);
			for (EntityClass subSub : sub.getAllSubClasses()) {
				allSub.add(subSub);
			}
		}

		return allSub;
	}

	public EntityRelation getAssociationRelation() {
		return _associationRelation;
	}

	public EntityDomain getDomain() {
		return _domain;
	}

	public boolean hasInitial()
	{
		return this.getInitialState() != null;
	}
	
	public EntityState getInitialState() {
		return this._initial;	
	}

	private EntityClass getClassInHierarchyWithStates()
	{
		ArrayList<EntityClass> allClassesInHierarchy = this.getAllClassesInHierarchy();
		for(EntityClass someClass : allClassesInHierarchy)
		{
			if(!someClass._states.isEmpty())
			{
				return someClass;
			}
		}
		
		// if that fails, get the class with the lowest name - just to be consistent
		String lowestName = "ZZZZZZZZZZZZZZZZZZZZZZZZZZZ";
		for(EntityClass sortingClass : allClassesInHierarchy)
		{
			if(sortingClass.getName().compareTo(lowestName) < 0)
			{
				lowestName = sortingClass.getName();
			}
		}
		
		return this.getDomain().getEntityClassWithName(lowestName);
	}
	
	public boolean hasStates()
	{
		if(this.isGeneralisation())
		{
			return !this.getClassInHierarchyWithStates()._states.isEmpty();
		}
		else
		{
			return !this._states.isEmpty();
		}
	}

	public boolean hasSuperClasses() {
		return !this._superClasses.isEmpty();
	}
	
	public String getName() {
		return _name;
	}

	public void addRelation(EntityRelation theRelation) {
		_relations.add(theRelation);
	}

	public boolean hasRelation(String relationName) {
		for(EntityRelation relation : this._relations)
		{
			if(relation.getName().equals(relationName)){
				return true;
			}
		}
		return false;
	}
	
	public boolean hasRelationWithId(String relationId) {
		for(EntityRelation relation : this._relations)
		{
			if(relation.getId().equals(relationId)){
				return true;
			}
		}
		return false;
	}

	public boolean hasRelation(EntityRelation theRelation) {
		for(EntityRelation relation : this._relations)
		{
			if(relation.equals(theRelation)){
				return true;
			}
		}
		return false;
	}

	public ArrayList<EntityClass> getsubClasses() {
		return _subClasses;
	}

	public ArrayList<EntityClass> getSuperClasses() {
		return _superClasses;
	}

	public ArrayList<EntityClass> getAllSuperClasses() {
		ArrayList<EntityClass> allSuperClasses = new ArrayList<EntityClass>();
		for (EntityClass superClass : _superClasses) {
			allSuperClasses.add(superClass);
			for (EntityClass superSuperClass : superClass.getAllSuperClasses()) {
				if(!allSuperClasses.contains(superSuperClass))
				{
					allSuperClasses.add(superSuperClass);
				}
			}
		}
		allSuperClasses.add(this);
		return allSuperClasses;
	}

	public boolean isAssociation() {
		return _associationRelation != null;
	}

	public boolean isGeneralisation() {
		return _isGeneralisation;
	}

	public void setAssociationRelation(EntityRelation relation) {
		if(relation == null)
		{
			this._associationRelation = null;
			return;
		}
		if(this._associationRelation != null)
		{
			this._associationRelation.setAssociation(null);
		}
		this._associationRelation = relation;
		relation.setAssociation(this);
	}

	public void setDomain(EntityDomain entityDomain) {
		this._domain = entityDomain;
	}

	public void setInitial(EntityState initial) {
		this._initial = initial;
	}

	public void setName(String newClassName) {
		this._name = newClassName;
	}

	public String toString() {
		String domainName = "";
		if(this._domain != null)
		{
			domainName = this._domain.getName();
		}
		String toString = domainName + ":" + getName();
		return toString;
	}

	public EntityRelation getRelationWithName(String theRelationName) throws NameNotFoundException {
		if (!hasRelation(theRelationName)) {
			throw new NameNotFoundException(toString() + " does not have a relation called " + theRelationName);
		}
		for(EntityRelation relation : this._relations)
		{
			if(relation.getName().equals(theRelationName)){
				return relation;
			}
		}
		return null;
	}
	
	public EntityRelation getRelationWithId(String theRelationId){
		for(EntityRelation relation : this._relations)
		{
			if(relation.getId().equals(theRelationId)){
				return relation;
			}
		}
		return null;
	}

	public boolean hasAttribute(String theAttributeName) {
		for(EntityAttribute attribute : this._attributes)
		{
			if(attribute.getName().equals(theAttributeName))
			{
				return true;
			}
		}
		for(EntityClass superClass : this.getSuperClasses())
		{
			if(superClass.hasAttribute(theAttributeName))
			{
				return true;
			}
		}
		return false;
	}

	public void addAttribute(EntityAttribute theAttribute) throws NameAlreadyBoundException {
		if(theAttribute.getName().equals(EntityAttribute.STATE_ATTRIBUTE_NAME))
		{
			throw new CannotCreateAttributeCalledStateException();
		}
		for(EntityAttribute attribute : this._attributes)
		{
			if(attribute.getName().equals(theAttribute.getName()))
			{
				throw new NameAlreadyBoundException("Class " + this.getName() + " already has an attribute named " + theAttribute.getName());
			}
				}
		_attributes.add(theAttribute);
		theAttribute.setClass(this);
	}

	public void deleteAttribute(EntityAttribute theAttribute) throws NameNotFoundException {
		deleteAttributeWithName(theAttribute.getName());
	}

	public void deleteAttributeWithName(String attributeName) throws NameNotFoundException {
		if (!hasAttribute(attributeName)) {
			throw new NameNotFoundException(this + " does not have an attribute named : " + attributeName);
		}
		_attributes.remove(getAttributeWithName(attributeName));
	}

	public EntityAttribute getAttributeWithName(String attributeName) throws NameNotFoundException {
		if (!hasAttribute(attributeName)) {
			throw new NameNotFoundException(this + " does not have an attribute named : " + attributeName);
		}
		for(EntityAttribute attribute : this._attributes)
		{
			if(attribute.getName().equals(attributeName))
			{
				return attribute;
			}
		}
				
		EntityAttribute theAttribute = null;
		
		for(EntityClass superClass : this.getSuperClasses())
		{
			EntityAttribute attribute = superClass.getAttributeWithName(attributeName);
			if(attribute != null)
			{
				theAttribute = attribute;
				break;
			}
		}
		
		return theAttribute;
	}

	public boolean hasStateWithName(String stateName)
	{
		if(this.isGeneralisation())
		{
			EntityClass classWithStates = this.getClassInHierarchyWithStates();
			return classWithStates._hasStateWithName(stateName);
		}
		else
		{
			return _hasStateWithName(stateName);
		}
	}
	
	public boolean _hasStateWithName(String stateName) {
		for (EntityState state : _states) {
			if (state.getName().equals(stateName)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasStateWithId(String stateId)
	{
		if(this.isGeneralisation())
		{
			EntityClass classWithStates = this.getClassInHierarchyWithStates();
			return classWithStates._hasStateWithId(stateId);
		}
		else
		{
			return _hasStateWithId(stateId);
		}
	}
	
	public boolean _hasStateWithId(String stateId) {
		for (EntityState state : _states) {
			if (state.getId().equals(stateId)) {
				return true;
			}
		}
		return false;
	}

	public EntityClass getDelegateSuperClass()
	{
		ArrayList<EntityClass> hierarchyClasses = this.getAllClassesInHierarchy();
		EntityClass delegate = hierarchyClasses.get(0);
		return delegate;
	}
	
	public EntityState getStateWithName(String stateName) {
		if(this.isGeneralisation())
		{
			EntityClass classWithStates = this.getClassInHierarchyWithStates();
			return classWithStates._getStateWithName(stateName);
		}
		else
		{
			return _getStateWithName(stateName);
		}
	}

	public EntityState _getStateWithName(String stateName) {
		if (!hasStateWithName(stateName)) {
			return null;
		}
		EntityState theState = null;
		for (EntityState state : _states) {
			if (state.getName().equals(stateName)) {
				theState = state;
				break;
			}
		}
		return theState;
	}

	public EntityState getStateWithId(String stateId) {
		if(this.isGeneralisation())
		{
			EntityClass classWithStates = this.getClassInHierarchyWithStates();
			return classWithStates._getStateWithId(stateId);
		}
		else
		{
			return _getStateWithId(stateId);
		}
	}

	public EntityState _getStateWithId(String stateId) {
		if (!hasStateWithId(stateId)) {
			return null;
		}
		EntityState theState = null;
		for (EntityState state : _states) {
			if (state.getId().equals(stateId)) {
				theState = state;
				break;
			}
		}
		return theState;
	}
	
	public boolean hasEventSpecificationWithName(String eventName)
	{
		if(this.isGeneralisation())
		{
			EntityClass classWithStates = this.getClassInHierarchyWithStates();
			return classWithStates._hasEventSpecificationWithName(eventName);
		}
		else
		{
			return _hasEventSpecificationWithName(eventName);
		}
	}
	
	public boolean _hasEventSpecificationWithName(String eventName) {
		for (EntityEventSpecification event : _events) {
			if (event.getName().equals(eventName)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasEventSpecificationWithId(String id)
	{
		if(this.isGeneralisation())
		{
			EntityClass classWithStates = this.getClassInHierarchyWithStates();
			return classWithStates._hasEventSpecificationWithId(id);
		}
		else
		{
			return _hasEventSpecificationWithId(id);
		}
	}
	
	public boolean _hasEventSpecificationWithId(String id) {
		for (EntityEventSpecification event : _events) {
			if (event.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}
	
	public EntityEventSpecification getEventSpecificationWithName(String eventName) throws NameNotFoundException
	{
		if(this.isGeneralisation())
		{
			EntityClass classWithStates = this.getClassInHierarchyWithStates();
			return classWithStates._getEventSpecificationWithName(eventName);
		}
		else
		{
			return _getEventSpecificationWithName(eventName);
		}
	}
	
	public EntityEventSpecification _getEventSpecificationWithName(String eventName) throws NameNotFoundException {
		for (EntityEventSpecification event : _events) {
			if (event.getName().equals(eventName)) {
				return event;
			}
		}
		throw new NameNotFoundException("Could not find event with name :" + eventName + " on class : " + this);
	}

	public EntityEventInstance getEventInstance(String eventSpecificationName, String fromStateName, String toStateName) throws NameNotFoundException {
		if (!hasEventInstance(eventSpecificationName, fromStateName, toStateName)) {
			throw new NameNotFoundException("Could not find EntityEventSpecification with name = " + eventSpecificationName);
		}
		EntityEventInstance theInstance = null;
		EntityEventSpecification specKey = getSpecificationFromInstancesWithName(eventSpecificationName);
		ArrayList<EntityEventInstance> instances = _eventInstances.get(specKey);
		for (EntityEventInstance instance : instances) {
			if (instance.getFromState().getName().equals(fromStateName) && instance.getToState().getName().equals(toStateName)) {
				theInstance = instance;
				break;
			}
		}
		return theInstance;
	}
	
	public ArrayList<EntityEventInstance> getAllEntityEventInstances()
	{
		ArrayList<EntityEventInstance> instances = new ArrayList<EntityEventInstance>();
		
		if(this.isGeneralisation())
		{
			EntityClass classWithStates = this.getClassInHierarchyWithStates();
			for (EntityEventSpecification spec : classWithStates._eventInstances.keySet()) {
				instances.addAll(classWithStates._eventInstances.get(spec));
			}
		}
		else
		{
			for (EntityEventSpecification spec : this._eventInstances.keySet()) {
				instances.addAll(this._eventInstances.get(spec));
			}
		}
		
		return instances;
	}
	
	
	public ArrayList<EntityEventInstance> getEventInstancesForSpecification(EntityEventSpecification spec)
	{
		if(this.isGeneralisation())
		{
			EntityClass classWithStates = this.getClassInHierarchyWithStates();
			if(classWithStates._eventInstances.containsKey(spec)){
				return classWithStates._eventInstances.get(spec);
			}
			return new ArrayList<EntityEventInstance>();
		}
		else
		{
			if(this._eventInstances.containsKey(spec)){
				return this._eventInstances.get(spec);
			}
			return new ArrayList<EntityEventInstance>();
		}
	}

	public void addEventInstance(EntityEventSpecification entityEventSpecification, EntityEventInstance entityEventInstance) {
		if (!_eventInstances.containsKey(entityEventSpecification)) {
			_eventInstances.put(entityEventSpecification, new ArrayList<EntityEventInstance>());
		}
		ArrayList<EntityEventInstance> instances = _eventInstances.get(entityEventSpecification);
		if (!instances.contains(entityEventInstance)) {
			instances.add(entityEventInstance);
			_eventInstances.put(entityEventSpecification, instances);
		}
	}

	private EntityEventSpecification getSpecificationFromInstancesWithName(String eventSpecName) {
		for (EntityEventSpecification spec : _eventInstances.keySet()) {
			if (spec.getName().equals(eventSpecName)) {
				return spec;
			}
		}
		return null;
	}

	public boolean hasEventInstance(String eventSpecificationName, String fromStateName, String toStateName) throws NameNotFoundException {
		if(this.isGeneralisation())
		{
			EntityClass classWithStates = this.getClassInHierarchyWithStates();
			return classWithStates._hasEventInstance(eventSpecificationName, fromStateName, toStateName);
		}
		else
		{
			return _hasEventInstance(eventSpecificationName, fromStateName, toStateName);
		}
	}
	
	public boolean _hasEventInstance(String eventSpecificationName, String fromStateName, String toStateName) throws NameNotFoundException {
		EntityEventSpecification specKey = getSpecificationFromInstancesWithName(eventSpecificationName);
		if (specKey == null) {
			return false;
		}
		ArrayList<EntityEventInstance> instances = _eventInstances.get(specKey);
		for (EntityEventInstance instance : instances) {
			if (instance.getFromState().getName().equals(fromStateName) && instance.getToState().getName().equals(toStateName)) {
				return true;
			}
		}
		return false;
	}

	public void removeEventInstance(EntityEventSpecification entityEventSpecification, EntityEventInstance eventInstance) throws NameNotFoundException {
		if (!hasEventInstance(entityEventSpecification.getName(), eventInstance.getFromState().getName(), eventInstance.getToState().getName())) {
			throw new NameNotFoundException("Could not find EntityEventSpecification with name = " + entityEventSpecification.getName());
		}
		ArrayList<EntityEventInstance> newInstances = new ArrayList<EntityEventInstance>();
		ArrayList<EntityEventInstance> instanceSet = _eventInstances.get(entityEventSpecification);
		for (EntityEventInstance instance : instanceSet) {
			if (!(instance.getFromState().getName().equals(eventInstance.getFromState().getName()) && instance.getToState().getName()
					.equals(eventInstance.getToState().getName()))) {
				newInstances.add(instance);
			}
		}
		_eventInstances.put(entityEventSpecification, newInstances);
	}

	public boolean hasEventInstanceFromState(EntityEventSpecification entityEventSpecification, EntityState fromState) {
		if(!_eventInstances.containsKey(entityEventSpecification))
		{
			return false;
		}
		for (EntityEventInstance instance : _eventInstances.get(entityEventSpecification)) {
			if (instance.getFromState().equals(fromState))
			{
				return true;
			}
		}
		return false;
	}
	
	
	public boolean hasEventInstanceToState(EntityEventSpecification entityEventSpecification, EntityState toState) {
		if(!_eventInstances.containsKey(entityEventSpecification))
		{
			return false;
		}
		ArrayList<EntityEventInstance> instanceSet = _eventInstances.get(entityEventSpecification);
		for (EntityEventInstance instance : instanceSet) {
			if (instance.getToState().equals(toState))
			{
				return true;
			}
		}
		return false;
	}
	

	public ArrayList<String> getGeneratedEventNames() throws InvalidActionLanguageSyntaxException {
		ArrayList<String> generatedEventNames = new ArrayList<String>();
		for (EntityState state : _states) {
			EntityProcedure stateProcedure = state.getProcedure();
			stateProcedure.calculateInstanceLifespanMap();
			GeneratedEventManager eventManager = stateProcedure.getGeneratedEventManager();
			generatedEventNames.addAll(eventManager.getRegisteredEvents());
		}
		return generatedEventNames;
	}

	public boolean isInGeneralisationWith(EntityClass otherClass) {
		if(!isGeneralisation() || !otherClass.isGeneralisation())
		{
			return false;
		}
		
		EntityClass thisClassWithStates = this.getClassInHierarchyWithStates();
		EntityClass otherClassClassWithStates = otherClass.getClassInHierarchyWithStates();
		return thisClassWithStates.equals(otherClassClassWithStates);
	}

	public boolean isClassInParentPath(EntityClass theClass) {
		if(theClass.equals(this))
		{
			return true;
		}
		return this.getAllSuperClasses().contains(theClass);
	}

	public boolean hasSubClassWithName(String subClassName) {
		if(!_isGeneralisation)
		{
			return false;
		}
		for(EntityClass subClass : this.getAllSubClasses())
		{
			if(subClass.getName().equals(subClassName))
			{
				return true;
			}
		}
		return false;
	}

	public ArrayList<EntityState> getStates() {
		if(this.isGeneralisation())
		{
			return this.getClassInHierarchyWithStates()._states;
		}
		else
		{
			return this._states;
		}
	}

	public void deleteState(EntityState theState) {
		_states.remove(theState);
		if(this._initial.equals(theState))
		{
			this._initial = null;
			if(this.hasStates())
			{
				this._initial = this._states.get(0);
			}
		}
	}

	public boolean hasSubClasses() {
		return !this._subClasses.isEmpty();
	}

	public Collection<String> getAttributeNames() {
		ArrayList<String> attributeNames = new ArrayList<String>();
		for(EntityAttribute attribute : this.getAttributes())
		{
			attributeNames.add(attribute.getName());
		}
		return attributeNames;
	}

	public ArrayList<EntityClass> getAllClassesInHierarchy() {
		ArrayList<EntityClass> hierarchy = new ArrayList<EntityClass>();
		return this.getAllClassesInHierarchy(hierarchy);
	}
	
	
	
	public ArrayList<EntityClass> getAllClassesInHierarchy(ArrayList<EntityClass> hierarchy)
	{
		if(this.doesHierarchyContainAllSuperClasses(hierarchy))
		{
			if(!hierarchy.contains(this))
			{
				hierarchy.add(this);
			}

			// root has been found
			// process down
			for(EntityClass subClass : this._subClasses)
			{
				subClass.getAllClassesInHierarchy(hierarchy);
			}
		}
		else
		{
			// root not found
			// process up - not back to the same parent
			for(EntityClass superClass : this._superClasses)
			{
				if(!hierarchy.contains(superClass))
				{
					superClass.getAllClassesInHierarchy(hierarchy);
				}
			}
		}
		return hierarchy;
	}

	public boolean doesHierarchyContainAllSuperClasses(ArrayList<EntityClass> hierarchy) {
		for(EntityClass superClass : this._superClasses)
		{
			if(!hierarchy.contains(superClass))
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean doesHierarchyContainAllSubClasses(ArrayList<EntityClass> hierarchy) {
		for(EntityClass subClass : this._subClasses)
		{
			if(!hierarchy.contains(subClass))
			{
				return false;
			}
		}
		return true;
	}

	public Collection<EntityRelation> getRelations() {
		return this._relations;
	}

	public String describeStateMachine() {
		StringBuffer out = new StringBuffer();
		for(EntityState state : this._states)
		{
			out.append(state.getName().replace(" ", "") + ";\n");
		}
		
		for(ArrayList<EntityEventInstance> events : this._eventInstances.values())
		{
			for(EntityEventInstance event : events)
			{
				String from = event.getFromState().getName().replace(" ", "");
				String to = event.getToState().getName().replace(" ", "");
				String name = event.getSpecification().getName();
				out.append(from + "->" + to + "[label=\"" + name + "\"];\n");
			}
		}
		return out.toString();
	}

	public Collection<String> getStateNames() {
		ArrayList<String> names = new ArrayList<String>();
		for(EntityState state : this._states)
		{
			names.add(state.getName());
		}
		return names;
	}

	public String getId() {
		return this._id;
	}

	public void setId(String classId) {
		this._id = classId;
	}

	public EntityAttribute getAttributeWithId(String attributeUUID) throws NameNotFoundException {
		for(EntityAttribute attribute : this._attributes)
		{
			if(attribute.getId().equals(attributeUUID))
			{
				return attribute;
			}
		}
		throw new NameNotFoundException("No attribute with ID:" + attributeUUID + " found in class:" + this._name);
	}

	public boolean hasAttributeWithID(String attributeUUID) {
		for(EntityAttribute attribute : this._attributes)
		{
			if(attribute.getId().equals(attributeUUID))
			{
				return true;
			}
		}
		return false;
	}

	public void removeRelation(EntityRelation relation) {
		this._relations.remove(relation);
	}

	
	public void removeSuperClasses() {
		if(!this.hasSubClasses()){
			this._isGeneralisation = false;
		}
		
		for(EntityClass superClass : this.getSuperClasses())
		{
			superClass.removeSubClass(this);
		}

		this._superClasses.clear();
			
	}

	private void removeSubClass(EntityClass entityClass) {
		this._subClasses.remove(entityClass);
		if(!this.hasSubClasses())
		{
			this._isGeneralisation = false;
		}
	}

	public EntityEventSpecification getDefaultEventSpecification() {
		if(this.isGeneralisation())
		{
			EntityClass classWithStates = this.getClassInHierarchyWithStates();
			for(EntityEventSpecification spec : classWithStates._events){
				if(spec.getName().equals(DEFAULT_EVENT_SPEC_ID)){
					return spec;
				}
			}
		}
		else
		{
			for(EntityEventSpecification spec : this._events){
				if(spec.getName().equals(DEFAULT_EVENT_SPEC_ID)){
					return spec;
				}
			}
		}
		
		
		return null;
	}

	public EntityEventInstance getEventInstanceWithId(String uuid) {
		if(this.isGeneralisation())
		{
			EntityClass classWithStates = this.getClassInHierarchyWithStates();
			return classWithStates._getEventInstanceWithId(uuid);
		}
		else
		{
			return this._getEventInstanceWithId(uuid);
		}
	}
	
	private EntityEventInstance _getEventInstanceWithId(String uuid){
		for(EntityEventInstance instance : this.getAllEntityEventInstances()){
			if(instance.getId().equals(uuid)){
				return instance;
			}
		}
		return null;
	}

	public boolean hasEventInstanceWithId(String id){
		EntityEventInstance foundInstance = this.getEventInstanceWithId(id);
		return foundInstance != null;
	}
	
	public EntityEventSpecification getEventSpecificationWithId(String uuid) {
		if(this.isGeneralisation())
		{
			EntityClass classWithStates = this.getClassInHierarchyWithStates();
			return classWithStates._getEventSpecificationWithId(uuid);
		}
		else
		{
			return this._getEventSpecificationWithId(uuid);
		}
	}
	
	private EntityEventSpecification _getEventSpecificationWithId(String uuid){
		for(EntityEventSpecification specification : this._events){
			if(specification.getId().equals(uuid)){
				return specification;
			}
		}
		return null;
	}

	public EntityEventInstance changeEventInstance(EntityEventInstance eventInstance, EntityEventSpecification newEventSpec, EntityState fromState,	EntityState toState) {
		EntityEventSpecification oldSpec = eventInstance.getSpecification();
		try {
			oldSpec.removeEventInstance(eventInstance);
		} catch (NameNotFoundException e) {
		}

		EntityEventInstance newInstance = new EntityEventInstance(newEventSpec, fromState, toState);
		newInstance.setId(eventInstance.getId());
		return newInstance;
	}

	
	public void deleteSpecification(EntityEventSpecification theSpec) {
		if(this.isGeneralisation())
		{
			EntityClass classWithStates = this.getClassInHierarchyWithStates();
			EntityEventSpecification defaultSpec = classWithStates.getDefaultEventSpecification();
			for(EntityEventInstance instance : classWithStates._eventInstances.get(theSpec)){
				instance.setSpec(defaultSpec);
			}
			classWithStates._eventInstances.get(defaultSpec).addAll(classWithStates._eventInstances.get(theSpec));
			classWithStates._eventInstances.remove(theSpec);
			classWithStates._events.remove(theSpec);
		}
		else
		{
			EntityEventSpecification defaultSpec = this.getDefaultEventSpecification();
			for(EntityEventInstance instance : this._eventInstances.get(theSpec)){
				instance.setSpec(defaultSpec);
			}
			this._eventInstances.get(defaultSpec).addAll(this._eventInstances.get(theSpec));
			this._eventInstances.remove(theSpec);
			this._events.remove(theSpec);
		}
		
		
	}

}
