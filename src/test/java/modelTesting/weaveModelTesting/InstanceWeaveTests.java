package test.java.modelTesting.weaveModelTesting;


import javax.naming.NameAlreadyBoundException;

import test.java.mock.MockSimulatedClass;

import junit.framework.Assert;
import junit.framework.TestCase;
import test.java.weaveModelTests.DomainFactory;
import test.java.weaveModelTests.DomainStore;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.weaving.ClassWeave;
import main.java.avii.editor.metamodel.weaving.DomainWeave;
import main.java.avii.editor.metamodel.weaving.exception.CannotAddClassToWeaveIfItsDomainIsntRegistered;
import main.java.avii.editor.metamodel.weaving.exception.CannotAddDuplicateClassToClassWeaveException;
import main.java.avii.editor.metamodel.weaving.exception.CannotAddDuplicateDomainToDomainWeaveException;
import main.java.avii.editor.metamodel.weaving.exception.CannotAddSuperClassToClassWeaveException;
import main.java.avii.editor.metamodel.weaving.exception.CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.weave.InstanceWeave;

public class InstanceWeaveTests extends TestCase {
	
	private DomainFactory _factoryDomain;
	private DomainStore _storeDomain;

	
	public InstanceWeaveTests(String name) {
		super(name);
		
	}

	private DomainWeave setupWeave() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotAddDuplicateDomainToDomainWeaveException, CannotAddSuperClassToClassWeaveException, CannotAddDuplicateClassToClassWeaveException, CannotAddClassToWeaveIfItsDomainIsntRegistered, CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException {
		DomainWeave weave = new DomainWeave("Store that gets items from factory");
		this._storeDomain = new DomainStore();
		this._factoryDomain = new DomainFactory();
		weave.registerDomain(this._storeDomain.domain);
		weave.registerDomain(this._factoryDomain.domain);
		
		ClassWeave itemWeave = weave.createClassWeave();
		itemWeave.registerClass(this._storeDomain.item);
		itemWeave.registerClass(this._factoryDomain.item);
		
		return weave;
	}
	
	public ClassWeave getClassWeave() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotAddDuplicateDomainToDomainWeaveException, CannotAddSuperClassToClassWeaveException, CannotAddDuplicateClassToClassWeaveException, CannotAddClassToWeaveIfItsDomainIsntRegistered, CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException
	{
		DomainWeave weave = setupWeave();
		ClassWeave classWeave = weave.getClassWeaves().iterator().next();
		return classWeave;
	}
	
	
	public void test_can_insert_instance_into_instance_weave() throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException, CannotAddDuplicateDomainToDomainWeaveException, CannotAddSuperClassToClassWeaveException, CannotAddDuplicateClassToClassWeaveException, CannotAddClassToWeaveIfItsDomainIsntRegistered, CannotRegisterMultipleClassesFromTheSameDomainClassWeaveException{
		ClassWeave classWeave = getClassWeave();
		InstanceWeave weave = new InstanceWeave(classWeave);
		
		MockSimulatedClass mockStore = new MockSimulatedClass("store");
		MockSimulatedClass mockFactory = new MockSimulatedClass("factory");
		
		SimulatedInstance storeInstance = mockStore.createInstance();
		SimulatedInstance factoryInstance = mockFactory.createInstance();

		Assert.assertEquals(false, storeInstance.isInWeave());
		Assert.assertEquals(false, factoryInstance.isInWeave());
		
		weave.putInstance(storeInstance);
		weave.putInstance(factoryInstance);
		
		Assert.assertEquals(storeInstance, weave.getInstanceForClass(mockStore));
		Assert.assertEquals(factoryInstance, weave.getInstanceForClass(mockFactory));
		
		Assert.assertEquals(true, storeInstance.isInWeave());
		Assert.assertEquals(true, factoryInstance.isInWeave());
		
		Assert.assertTrue(storeInstance.getInstanceWeave() == weave);
		Assert.assertTrue(factoryInstance.getInstanceWeave() == weave);
	}
	
}
