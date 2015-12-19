var ClassDiagram_Actions = new function() {

	/*
	 * Action List
	 * 
	 */
	this.ActionList = function(domain) {
		var self = this;
		self.lastSaveResult = null;
		self.domain = domain;
		self.actions = new ko.observableArray();
		self.message = new ko.observable(null);

		self.apply = function(theAction) {
			var result = theAction.apply(domain);
			if(result != undefined){
				self.message(result);
				return;
			}else{
				self.message(null);
			};
			self.actions().push(theAction);
			self.actions.valueHasMutated();
			self.domain.entityClasses.valueHasMutated();
			self.domain.entityRelations.valueHasMutated();
		};
		
		self.lastAction = ko.computed(function(){
			return self.actions()[self.actions().length - 1];
		}, this);
		
		self.typeOfLastAction = ko.computed(function() {
			if(self.actions().length > 0){
				return self.lastAction().type.replace("Action","");
			}
			return "";
		}, this);
		
		self.undo = function() {
			self.lastAction().undo(self.domain);
			self.actions.remove(self.lastAction());
			self.domain.entityClasses.valueHasMutated();
			self.domain.entityRelations.valueHasMutated();
		};
		
		self.saveActions = function(projectId) {
			self.lastSaveResult = "Initiated";
			
			
			// add coordinate action before serialising
			var coordinateAction = new ClassDiagram_Actions.SaveClassDiagramCoordinatesAction();
			coordinateAction.apply(self.domain);
			self.actions().push(coordinateAction);
			var json = ko.toJSON(self);
			// remove coordinate action after serialising
			self.actions.remove(coordinateAction);
			
			
			$.ajax({
				url : "/saveClassDiagramActions?projectId=" + projectId,
				type : "POST",
				dataType : "text",
				contentType : "text",
				data : json,
				async : false,
				success : function() {
					self.lastSaveResult = "Success";
					self.actions.removeAll();
					self.message(null);
				},
				error : function(data) {
					self.lastSaveResult = "Error";
					messageData = jQuery.parseJSON(data.responseText);
					self.message(messageData.message);
					for(var i = 0; i < messageData.okActions; i++) {
						var validAction = self.actions()[0];
						self.actions.remove(validAction);
					}
				}
			});
		};
	
	};

	/*
	 * Save ClassDiagram Coordinates Action
	 * 
	 */
	this.SaveClassDiagramCoordinatesAction = function() {
		var self = this;
		self.type = "SaveClassDiagramCoordinatesAction";
		self.entityClassPositions = null;
		self.apply = function(domain) {
			self.entityClassPositions = new util.ClassDiagramCoordinateSet(domain);
		};
		
		self.undo = function(domain) {
			// nop
		};
		
	};
	
	
	/*
	 * New Class Action
	 * 
	 */
	this.NewClassAction = function() {
		var self = this;
		self.type = "NewClassAction";
		self.apply = function(domain) {
			var newClass = domain.createClass();
			self.uuidOfClass = newClass.uuid;
		};
		
		self.undo = function(domain) {
			domain.removeClassWithUUID(self.uuidOfClass);
		};
		
	};

	/*
	 * Rename Class
	 * 
	 */
	this.RenameClass = function(uuid, newName) {
		var self = this;
		self.type = "RenameClassAction";
		self.uuidOfClass = uuid;
		self.newName = newName;
		self.oldClassName = "";
		self.apply = function(domain) {
			var theClass = domain.getClassWithUUID(self.uuidOfClass);
			
			var errorFromClassName = theClass.isClassNameValid(self.newName);
			if(errorFromClassName != true){
				return errorFromClassName;
			}
			
			self.oldClassName = theClass.name();
			theClass.name(self.newName);
		};
		
		self.undo = function(domain) {
			var theClass = domain.getClassWithUUID(self.uuidOfClass);
			theClass.name(self.oldClassName);
		};
	};

	/*
	 * New Relation
	 * 
	 */
	this.NewRelationAction = function(uuidA, uuidB) {
		var self = this;
		self.type = "NewRelationAction";
		self.uuidA = uuidA;
		self.uuidB = uuidB;
		self.theRelation = null;
		self.apply = function(domain) {
			var classA = domain.getClassWithUUID(self.uuidA);
			var classB = domain.getClassWithUUID(self.uuidB);
			self.theRelation = new ClassDiagram_Entities.EntityRelation(classA, classB);
			self.uuid = self.theRelation.uuid;
			domain.entityRelations().push(self.theRelation);
		};
		self.undo = function(domain) {
			domain.entityRelations.remove(self.theRelation);
		};
	};
	
	/*
	 * Delete Class Action
	 * 
	 */
	this.DeleteClassAction = function(uuidToDelete) {
		var self = this;
		self.uuidToDelete = uuidToDelete;
		self.type = "DeleteClassAction";
		self.deletedClass = null;
		self.deletedRelations = null;
		self.apply = function(domain) {
			self.deletedClass = domain.getClassWithUUID(self.uuidToDelete);
			self.deletedRelations = domain.getRelationsInvolvingClass(self.deletedClass);
			for ( var i = 0; i < self.deletedRelations.length; i++) {
				var currentRelation = self.deletedRelations[i];
				domain.removeRelationWithUUID(currentRelation.uuid);
			}
			
			self.subClasses = self.deletedClass.getSubClasses();
			for ( var i = 0; i < self.subClasses.length; i++) {
				var currentSubClass = self.subClasses[i];
				currentSubClass.clearSuperClass();
			}
			
			domain.removeClassWithUUID(self.uuidToDelete);
		};
		
		self.undo = function(domain) {
			domain.entityClasses().push(self.deletedClass);
			for ( var i = 0; i < self.deletedRelations.length; i++) {
				var currentRelation = self.deletedRelations[i];
				domain.entityRelations().push(currentRelation);
			}
			for ( var i = 0; i < self.subClasses.length; i++) {
				var currentSubClass = self.subClasses[i];
				currentSubClass.setSuperClass(self.deletedClass);
			}
			
		};
	};
	//this.DeleteRelationAction.prototype.toJSON = function() {
	//	return "";
	//};

	
	/*
	 * Add Attribute Action
	 * 
	 */
	this.AddAttributeAction = function(classAddingTo) {
		var self = this;
		self.classAddingTo = classAddingTo;
		self.uuidOfClass = classAddingTo.uuid;
		self.newAttribute = null;
		self.uuidOfAttribute = null;
		self.type = "AddAttributeAction";
		self.apply = function(domain) {
			self.newAttribute = self.classAddingTo.addAttribute();
			self.uuidOfAttribute = self.newAttribute.uuid;
		};
		
		self.undo = function(domain) {
			 self.classAddingTo.deleteAttribute(self.newAttribute);
		};
	};
	
	/*
	 * Delete Attribute Action
	 * 
	 */
	this.DeleteAttributeAction = function(theClass, theAttribute) {
		var self = this;
		self.classAddingTo = theClass;
		self.uuidOfClass = theClass.uuid;
		self.theAttribute = theAttribute;
		self.uuidOfAttribute = theAttribute.uuid;
		self.type = "DeleteAttributeAction";
		self.apply = function(domain) {
			self.classAddingTo.deleteAttribute(theAttribute);
		};
		
		self.undo = function(domain) {
			 self.classAddingTo.attributes.push(theAttribute);
		};
	};
	
	/*
	 * Rename Attribute Action
	 * 
	 */
	this.RenameAttributeAction = function(theClassUuid, theAttribute, newName) {
		var self = this;
		self.uuidOfClass = theClassUuid;
		self.theAttribute = theAttribute;
		self.uuidOfAttribute = theAttribute.uuid;
		self.newAttributeName = newName;
		self.oldAttributeName = theAttribute.name();
		self.type = "RenameAttributeAction";
		self.apply = function(domain) {
			var errorFromAttributeName = self.theAttribute.isAttributeNameValid(self.newAttributeName);
			if(errorFromAttributeName != true){
				return errorFromAttributeName;
			}
			self.theAttribute.name(self.newAttributeName);
		};
		
		self.undo = function(domain) {
			 self.theAttribute.name(self.oldAttributeName);
		};
	};
	
	/*
	 * Change Attribute Type Action
	 * 
	 */
	this.ChangeAttributeTypeAction = function(theClassUuid, theAttribute, newType) {
		var self = this;
		self.uuidOfClass = theClassUuid;
		self.theAttribute = theAttribute;
		self.uuidOfAttribute = theAttribute.uuid;
		self.newType = newType;
		self.oldType = theAttribute.type();
		self.type = "ChangeAttributeTypeAction";
		self.apply = function(domain) {
			self.theAttribute.type(self.newType);
		};
		
		self.undo = function(domain) {
			self.theAttribute.type(self.oldType);
		};
	};
	
	/*
	 * Change Relation Action
	 * 
	 */
	this.ChangeRelationAction = function(theRelationId, classAId, classAVerb, classACardinality, classBId, classBVerb, classBCardinality) {
		var self = this;
		self.type = "ChangeRelationAction";
		self.theRelationId = theRelationId;
		self.classAId = classAId;
		self.classAVerb = classAVerb;
		self.classACardinality = classACardinality;
		self.classBId = classBId;
		self.classBVerb = classBVerb;
		self.classBCardinality = classBCardinality;
		
		self.oldClassAId = null;
		self.oldVerbA = null;
		self.oldCardinalityA = null;
		self.oldClassBId = null;
		self.oldVerbB = null;
		self.oldCardinalityB = null;
		
		self.apply = function(domain) {
			var theRelation = domain.getRelationWithUUID(self.theRelationId);
			self.oldClassAId = theRelation.endA().uuid;
			self.oldVerbA = theRelation.verbA();
			self.oldCardinalityA = theRelation.cardinalityA();
			self.oldClassBId = theRelation.endB().uuid;
			self.oldVerbB = theRelation.verbB();
			self.oldCardinalityB = theRelation.cardinalityB();
			
			var classA = domain.getClassWithUUID(self.classAId);
			theRelation.endA(classA);
			theRelation.verbA(self.classAVerb);
			theRelation.cardinalityA(self.classACardinality);
			var classB = domain.getClassWithUUID(self.classBId);
			theRelation.endB(classB);
			theRelation.verbB(self.classBVerb);
			theRelation.cardinalityB(self.classBCardinality);
		};
		
		self.undo = function(domain) {
			var theRelation = domain.getRelationWithUUID(self.theRelationId);
			var classA = domain.getClassWithUUID(self.oldClassAId);
			theRelation.endA(classA);
			theRelation.verbA(self.oldVerbA);
			theRelation.cardinalityA(self.oldCardinalityA);
			var classB = domain.getClassWithUUID(self.oldClassBId);
			theRelation.endB(classB);
			theRelation.verbB(self.oldVerbB);
			theRelation.cardinalityB(self.oldCardinalityB);
		};
	};

	/*
	 * Rename Relation Action
	 * 
	 */
	this.RenameRelationAction = function(theRelationUuid, newName) {
		var self = this;
		self.uuidOfRelation = theRelationUuid;
		self.newRelationName = newName;
		self.oldRelationName = null;
		self.type = "RenameRelationAction";
		self.apply = function(domain) {
			var theRelation = domain.getRelationWithUUID(self.uuidOfRelation);
			self.oldRelationName = theRelation.name();
			var errorFromRelationName = theRelation.isRelationNameValid(self.newRelationName);
			if(errorFromRelationName != true){
				return errorFromRelationName;
			}
			theRelation.name(self.newRelationName);
		};
		
		self.undo = function(domain) {
			var theRelation = domain.getRelationWithUUID(self.uuidOfRelation);
			theRelation.name(self.oldRelationName);
		};
	};
	
	/*
	 * Delete Relation Action
	 * 
	 */
	this.DeleteRelationAction = function(theRelationUuid, newName) {
		var self = this;
		self.uuidOfRelation = theRelationUuid;
		self.theRelation = null;
		self.type = "DeleteRelationAction";
		self.apply = function(domain) {
			self.theRelation = domain.getRelationWithUUID(self.uuidOfRelation);
			domain.removeRelationWithUUID(self.uuidOfRelation);
		};
		
		self.undo = function(domain) {
			domain.entityRelations().push(self.theRelation);
		};
	};
	
	/*
	 * Delete Relation Action
	 * 
	 */
	this.CreateAssociationAction = function(associationClassUUID) {
		var self = this;
		self.uuidOfRelation = null;
		self.uuidOfAssociationClass = associationClassUUID;
		self.previousUUIDOfClass = null;
		self.type = "CreateAssociationAction";
		self.apply = function(domain) {
			theRelation = window.vm.modelSelection.oneThingSelected();
			self.uuidOfRelation = theRelation.uuid;

			if(theRelation.associationClass() != null){
				self.previousUUIDOfClass = theRelation.associationClass().uuid;
			}
			
			if(associationClassUUID == undefined){
				// delete association
				theRelation.clearAssociation();
			} else {
				var associationClass = domain.getClassWithUUID(associationClassUUID);
				theRelation.setAssociation(associationClass);
			}
		};
		
		self.undo = function(domain) {
			theRelation = domain.getRelationWithUUID(self.uuidOfRelation);
			
			if(self.previousUUIDOfClass == null){
				theRelation.clearAssociation();
			} else {
				var associationClass = domain.getClassWithUUID(self.previousUUIDOfClass);
				theRelation.setAssociation(associationClass);
			}

		};
	};
	
	/*
	 * Delete Relation Action
	 * 
	 */
	this.ChangeSuperClassAction = function(superClassUUID) {
		var self = this;
		self.uuidOfClass = null;
		self.uuidOfSuperClass = superClassUUID;
		//self.previousUUidOfSuperClass = null;
		self.type = "ChangeSuperClassAction";
		self.apply = function(domain) {
			theClass = window.vm.modelSelection.oneThingSelected();
			self.uuidOfClass = theClass.uuid;
			
			if(superClassUUID == undefined){
				// delete superClass
				self.previousUUidOfSuperClass = theClass.getSuperClass().uuid;
				theClass.clearSuperClass();
			} else {
				var superClass = domain.getClassWithUUID(superClassUUID);
				theClass.setSuperClass(superClass);
			}
		};
		
		self.undo = function(domain) {
			theClass = window.vm.domain.getClassWithUUID(self.uuidOfClass);
			if(theClass.getSuperClass() == null){
				// undo from deleting superClass
				var superClass = domain.getClassWithUUID(self.previousUUidOfSuperClass);
				theClass.setSuperClass(superClass);
			} else {
				theClass.clearSuperClass();
			}
		};
	};
	
};