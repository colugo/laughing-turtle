ModelSelection = function(domain) {
	var self = this;
	self.domain = domain;
	self.selection = new ko.observableArray();
	
	self.isOneThingSelectedInner = function(){
		return self.selection().length == 1;
	};
	
	self.isOneThingSelectedInner = function(){
		isOneThingSelected = self.selection().length == 1;
		
		if(window.layout){
			if(isOneThingSelected){
				window.layout.open('south');
			}
			else{
				window.layout.close('south');
			}
		}
		return isOneThingSelected;
	};
	
	

	
	self.deselect = function(){
		self.selection([]);
	};
	
	self.isOneThingSelected = ko.computed(self.isOneThingSelectedInner, this);
	self.isAnyThingSelected = ko.computed(function(){return self.selection().length > 0;}, this);
	self.isManyThingSelected = ko.computed(function(){return self.selection().length > 1;}, this);

	self.select = function(thingToSelect) {
		if (self.selection.indexOf(thingToSelect) < 0) {
			  self.selection.push(thingToSelect);
			  self.selection.valueHasMutated();
		}
	};

	self.oneThingSelected = function() {
		return self.selection()[0];
	};

	self.typeOfOneThingSelected = function() {
		return self.oneThingSelected().type;
	};
	
	self.classWhenClasssOrStateSelected = function(){
		if(!self.isOneThingSelected()){
			return null;
		}
		
		if(self.typeOfOneThingSelected() == "EntityClass"){
			return self.oneThingSelected();
		}
		
		if(self.typeOfOneThingSelected() == "EntityState"){
			return self.oneThingSelected().stateMachine.theClass;
		}
		
		if(self.typeOfOneThingSelected() == "EventInstance"){
			return self.oneThingSelected().stateMachine.theClass;
		}
	};
};

ViewModel = function(projectId) {
	var self = this;
	self.projectId = projectId;
	self.domain = new ClassDiagram_Entities.EntityDomain();
	self.actionList = new ClassDiagram_Actions.ActionList(self.domain);
	self.modelSelection = new ModelSelection(self.domain);

	self.draggingRelationEndPoints = ko.observable(false);
	self.draggingSuperClassEndPoints = ko.observable(false);
	self.draggingTransitionEndPoints = ko.observable(false);
	
	self.width = new ko.computed(function(){
		var biggestWidth = 100;
		for(var i = 0; i < self.domain.entityClasses().length; i++){
			var currentClass = self.domain.entityClasses()[i];
			var currentX = currentClass.x() + currentClass.width();
			if(currentX > biggestWidth){
				biggestWidth = currentX;
			}
		}
		return biggestWidth + 200;
	}, this);
	
	self.height = new ko.computed(function(){
		var paneHeight = $('#classDiagramPane').height();
		var biggestHeight = 100;
		for(var i = 0; i < self.domain.entityClasses().length; i++){
			var currentClass = self.domain.entityClasses()[i];
			var currentY = currentClass.y() + currentClass.height();
			if(currentY > biggestHeight){
				biggestHeight = currentY;
			}
		}
		return Math.max(paneHeight, biggestHeight + 100);
	}, this);
	
	/*
	 * load
	 * 
	 */
	self.load = function() {
		$.getJSON("/getClassDiagram?projectId=" + this.projectId, function(
				classDiagram) {
			// EntityClasses
			var classes = classDiagram.diagram._classes;
			for ( var i = 0; i < classes.length; i++) {
				var jsonClass = classes[i];
				var newClass = self.domain.createClass();
				newClass.name(jsonClass._name);
				newClass.uuid = jsonClass._uuid;
				newClass.x(jsonClass._x);
				newClass.y(jsonClass._y);
				newClass.superClassTriangleIndex(jsonClass._superClassTriangleIndex);
				for(var j = 0; j < jsonClass._attributes.length; j++){
					var jsonAttribute = jsonClass._attributes[j];
					var theAttribute = new ClassDiagram_Entities.EntityAttribute(newClass);
					theAttribute.name(jsonAttribute._name);
					theAttribute.uuid = jsonAttribute._uuid;
					theAttribute.type(jsonAttribute._type);
			    	newClass.attributes().push(theAttribute);
				}
				newClass.attributes.valueHasMutated();
				for(var j = 0; j < jsonClass._states.length; j++){
					var jsonState = jsonClass._states[j];
					var theState = new StateDiagram_Entities.EntityState(newClass.stateMachine);
					theState.uuid = jsonState._uuid;
					theState.name(jsonState._name);
					theState.x(jsonState._x);
					theState.y(jsonState._y);
					newClass.stateMachine.states().push(theState);
				}
				
				for(var j = 0; j < jsonClass._specs.length; j++){
					var jsonSpec = jsonClass._specs[j];
					newSpecification = newClass.stateMachine.createSpecification();
					newSpecification.name(jsonSpec._name);
					newSpecification.uuid = jsonSpec._uuid;
					for(var k = 0; k < jsonSpec._params.length; k++){
						var jsonParam = jsonSpec._params[k];
						var param = newSpecification.createParam();
						param.name(jsonParam._name);
						param.uuid = jsonParam._uuid;
						param.type(jsonParam._type);
					}
					for(var k = 0; k < jsonSpec._instances.length; k++){
						var jsonInstance = jsonSpec._instances[k];
						var fromState = newClass.stateMachine.getStateWithUUID(jsonInstance._fromUUID);
						var toState = newClass.stateMachine.getStateWithUUID(jsonInstance._toUUID);
						var theInstance = newClass.stateMachine.createInstanceWithStates(fromState, toState);

						theInstance.uuid = jsonInstance._uuid;
						theInstance.specification(newSpecification);
						theInstance.toRelationPointIndex(jsonInstance._toIndex);
						theInstance.fromRelationPointIndex(jsonInstance._fromIndex);
						theInstance._fromDragX(jsonInstance._fromDragX);
						theInstance._fromDragY(jsonInstance._fromDragY);
						theInstance._toDragX(jsonInstance._toDragX);
						theInstance._toDragY(jsonInstance._toDragY);
					}
				}
				
				newClass.stateMachine.states.valueHasMutated();
				
			}
			// super class links
			var classes = classDiagram.diagram._classes;
			for ( var i = 0; i < classes.length; i++) {
				var jsonClass = classes[i];
				if(jsonClass._superClassId != null){
					var currentClass = self.domain.getClassWithUUID(jsonClass._uuid);
					var superClass = self.domain.getClassWithUUID(jsonClass._superClassId);
					currentClass.setSuperClass(superClass);
				}
			}
			self.domain.entityClasses.valueHasMutated();

			// EntityRelations
			var relations = classDiagram.diagram._relations;
			for ( var i = 0; i < relations.length; i++) {
				var jsonRelation = relations[i];
				var classA = self.domain
						.getClassWithUUID(jsonRelation._endAUuid);
				var classB = self.domain
						.getClassWithUUID(jsonRelation._endBUuid);
				var newRelation = new ClassDiagram_Entities.EntityRelation(
						classA, classB);
				
				if(jsonRelation._associationId != null){
					var associationClass = self.domain.getClassWithUUID(jsonRelation._associationId);
					newRelation.setAssociation(associationClass);
				}
				
				newRelation.name(jsonRelation._name);
				newRelation.uuid = jsonRelation._uuid;
				newRelation.verbA(jsonRelation._verbA);
				newRelation.cardinalityA(jsonRelation._cardinalityA);
				newRelation.verbB(jsonRelation._verbB);
				newRelation.cardinalityB(jsonRelation._cardinalityB);
				newRelation.relationPointIndexEndA(jsonRelation._indexA);
				newRelation.relationPointIndexEndB(jsonRelation._indexB);
				
				newRelation.verbAOffsetX(jsonRelation._verbAOffsetX);
				newRelation.verbAOffsetY(jsonRelation._verbAOffsetY);
				newRelation.verbBOffsetX(jsonRelation._verbBOffsetX);
				newRelation.verbBOffsetY(jsonRelation._verbBOffsetY);
				
				self.domain.entityRelations().push(newRelation);
			}
			self.domain.entityRelations.valueHasMutated();

		});
	};

	/*
	 * Save Actions
	 * 
	 */
	self.saveActions = function() {
		self.actionList.saveActions(this.projectId);
	};

	/*
	 * Undo
	 * 
	 */
	self.undoActions = function() {
		self.actionList.undo();
	};

	/*
	 * New Class Action
	 * 
	 */
	self.newClassAction = function() {
		var newClassAction = new ClassDiagram_Actions.NewClassAction();
		self.actionList.apply(newClassAction);
	};

	/*
	 * Rename Class Action
	 * 
	 */
	self.renameClassAction = function(theClass, theName) {
		var renameClassAction = new ClassDiagram_Actions.RenameClass(
				theClass.uuid, theName);
		self.actionList.apply(renameClassAction);
	};

	/*
	 * Delete Class Action
	 * 
	 */
	self.deleteClassAction = function(theClass) {
		var deleteClassAction = new ClassDiagram_Actions.DeleteClassAction(
				theClass.uuid);
		self.actionList.apply(deleteClassAction);
	};
	
	/*
	 * Add Attribute Action
	 * 
	 */
	self.addAttributeAction = function(theClass) {
		var addAttributeAction = new ClassDiagram_Actions.AddAttributeAction(theClass);
		self.actionList.apply(addAttributeAction);
	};
	
	/*
	 * Delete Attribute Action
	 * 
	 */
	self.deleteAttributeAction = function(theClass, theAttribute) {
		var deleteAttributeAction = new ClassDiagram_Actions.DeleteAttributeAction(theClass, theAttribute);
		self.actionList.apply(deleteAttributeAction);
	};
	
	/*
	 * Rename Attribute Action
	 * 
	 */
	self.renameAttributeAction = function(theClassUUID, theAttribute, theName) {
		var renameAttributeAction = new ClassDiagram_Actions.RenameAttributeAction(theClassUUID, theAttribute, theName);
		self.actionList.apply(renameAttributeAction);
	};
	
	/*
	 * Change Attribute Type Action
	 * 
	 */
	self.changeAttributeTypeAction = function(theClassUUID, theAttribute, theType) {
		var changeAttributeTypeAction = new ClassDiagram_Actions.ChangeAttributeTypeAction(theClassUUID, theAttribute, theType);
		self.actionList.apply(changeAttributeTypeAction);
	};
	
	/*
	 * Create Relation Action
	 * 
	 */
	self.createRelationAction = function(root) {
		var classAUUID = root.modelSelection.oneThingSelected().uuid;
		var classBUUID = root.modelSelection.oneThingSelected().newRelationUUID();
		var createRelationAction = new ClassDiagram_Actions.NewRelationAction(classAUUID, classBUUID);
		self.actionList.apply(createRelationAction);
	};
	
	/*
	 * Change Relation Action
	 * 
	 */
	self.changeRelationAction = function(relationId, idA, verbA, cardinalityA, idB, verbB, cardinalityB) {
		var changeRelationAction = new ClassDiagram_Actions.ChangeRelationAction(relationId, idA, verbA, cardinalityA, idB, verbB, cardinalityB);
		self.actionList.apply(changeRelationAction);
	};
	
	/*
	 * Rename Relation Action
	 * 
	 */
	self.renameRelationAction = function(relationId, newName) {
		var renameRelationAction = new ClassDiagram_Actions.RenameRelationAction(relationId, newName);
		self.actionList.apply(renameRelationAction);
	};
	
	/*
	 * Delete Relation Action
	 * 
	 */
	self.deleteRelationAction = function(theRelation) {
		var deleteRelationAction = new ClassDiagram_Actions.DeleteRelationAction(theRelation.uuid);
		self.actionList.apply(deleteRelationAction);
	};
	
	/*
	 * Create Association Action
	 * 
	 */
	self.createAssociationAction = function(uuidOfAssociationClass) {
		var theRelation = self.modelSelection.oneThingSelected();
		var createAssociationAction = new ClassDiagram_Actions.CreateAssociationAction(uuidOfAssociationClass);
		self.actionList.apply(createAssociationAction);
	};
	
	/*
	 * Change Super Class Action
	 * 
	 */
	self.changeSuperClassAction = function(uuidOfSuperClass) {
		var theClass = self.modelSelection.oneThingSelected();
		var changeSuperClassAction = new ClassDiagram_Actions.ChangeSuperClassAction(uuidOfSuperClass);
		self.actionList.apply(changeSuperClassAction);
	};

	/*
	 * Add State Action
	 * 
	 */
	self.addStateAction = function(theClass) {
		var addStateAction = new StateDiagram_Actions.AddStateAction(theClass);
		self.actionList.apply(addStateAction);
	};

	/*
	 * Rename State Action
	 * 
	 */
	self.renameStateAction = function(theState, theName) {
		var renameStateAction = new StateDiagram_Actions.RenameState(theState.uuid, theName);
		self.actionList.apply(renameStateAction);
	};
	
	
	/*
	 * Create State Transition Action
	 * 
	 */
	self.createStateTransitionAction = function(root) {
		var fromStateUUID = root.modelSelection.oneThingSelected().uuid;
		var toStateUUID = root.modelSelection.oneThingSelected().stateMachine.newStateToTransitionToUUID();
		var createTransitionAction = new StateDiagram_Actions.NewStateTransitionAction(fromStateUUID, toStateUUID);
		self.actionList.apply(createTransitionAction);
	};

	
	/*
	 * Change State Transition Action
	 * 
	 */
	self.changeStateTransitionAction = function(instanceUUID, fromUUID, toUUID, specUUID) {
		var changeTransitionAction = new StateDiagram_Actions.ChangeStateTransitionAction(instanceUUID, fromUUID, toUUID, specUUID);
		self.actionList.apply(changeTransitionAction);
	};

	/*
	 * Create Event Specification Action
	 * 
	 */
	self.createEventSpecificationAction = function(root) {
		var theClass = root.modelSelection.oneThingSelected().stateMachine.theClass;
		var createEventSpecificationAction = new StateDiagram_Actions.NewEventSpecificationAction(theClass);
		self.actionList.apply(createEventSpecificationAction);
	};
	
	
	/*
	 * Rename Spec Action
	 * 
	 */
	self.renameSpecAction = function(specUUID, classUUID, theName) {
		var renameSpecAction = new StateDiagram_Actions.RenameSpecAction(specUUID, classUUID, theName);
		self.actionList.apply(renameSpecAction);
	};

	/*
	 * Add Param Action
	 * 
	 */
	self.addParamAction = function(theSpec) {
		var addParamAction = new StateDiagram_Actions.AddParamAction(theSpec);
		self.actionList.apply(addParamAction);
	};

	
	/*
	 * Rename Param Action
	 * 
	 */
	self.renameParamAction = function(specUUID, classUUID, paramUUID, theName) {
		var renameParamAction = new StateDiagram_Actions.RenameSpecParamAction(specUUID, classUUID, paramUUID, theName);
		self.actionList.apply(renameParamAction);
	};
	
	
	/*
	 * Delete Param Action
	 * 
	 */
	self.deleteParamAction = function(param) {
		var specUUID = param.spec().uuid;
		var classUUID = param.spec().stateMachine.theClass.uuid;
		var deleteParamAction = new StateDiagram_Actions.DeleteSpecParamAction(specUUID, classUUID, param.uuid);
		self.actionList.apply(deleteParamAction);
	};
	
	/*
	 * Delete Param Action
	 * 
	 */
	self.changeParamTypeAction = function(specUUID, classUUID, paramUUID,  type) {
		var changeParamTypeAction = new StateDiagram_Actions.ChangeParamTypeAction(specUUID, classUUID, paramUUID,  type);
		self.actionList.apply(changeParamTypeAction);
	};
	
	/*
	 * Delete Spec Action
	 * 
	 */
	self.deleteSpecAction = function(spec, vm) {
		var specUUID = spec.uuid;
		var classUUID = spec.stateMachine.theClass.uuid;
		var deleteSpecAction = new StateDiagram_Actions.DeleteSpecificationAction(specUUID, classUUID);
		self.actionList.apply(deleteSpecAction);
	};
};

