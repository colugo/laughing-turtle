/*
package serviceTests;

import implementation.EditorService;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.metamodel.entities.datatypes.BooleanEntityDatatype;
import main.java.avii.editor.service.DomainNotFoundException;
import main.java.avii.editor.service.EditorServiceException;

public class ServiceTests extends TestCase {

	public ServiceTests(String name) {
		super(name);
	}

	public void test_add_and_list_classes() throws DomainNotFoundException, EditorServiceException {
		String className = "className";

		EditorService service = new EditorService();
		EditorCommandHistory retrievedHistory = service.createNewDomain("domainName");
	
		service.addClass(retrievedHistory, className);
		Assert.assertTrue(service.getClassIdsInDomain(retrievedHistory).contains(className));
	}

	public void test_add_and_rename_class() throws DomainNotFoundException, EditorServiceException {
		String classId = "classId";
		String newClassName = "newClassName";

		EditorService service = new EditorService();
		EditorCommandHistory retrievedHistory = service.createNewDomain("domainName");
	
		service.addClass(retrievedHistory, classId);
		Assert.assertTrue(service.getClassIdsInDomain(retrievedHistory).contains(classId));
		Assert.assertTrue(!service.getClassNamesInDomain(retrievedHistory).contains(newClassName));
		service.changeClassName(retrievedHistory, classId, newClassName);
		Assert.assertTrue(service.getClassIdsInDomain(retrievedHistory).contains(classId));
		Assert.assertTrue(service.getClassNamesInDomain(retrievedHistory).contains(newClassName));
	}

	public void test_cant_add_duplicate_class_and_list_classes() throws DomainNotFoundException, EditorServiceException {
		String className = "className";

		EditorService service = new EditorService();
		EditorCommandHistory retrievedHistory = service.createNewDomain("domainName");
	
		service.addClass(retrievedHistory, className);
		try {
			service.addClass(retrievedHistory, className);
			fail();
		} catch (EditorServiceException ese) {
		}

		Assert.assertTrue(service.getClassIdsInDomain(retrievedHistory).contains(className));
	}

	public void test_cant_rename_non_existant_class() throws DomainNotFoundException, EditorServiceException {

		EditorService service = new EditorService();
		EditorCommandHistory retrievedHistory = service.createNewDomain("domainName");
	
		try {
			service.changeClassName(retrievedHistory, "someClass", "newClassName");
			fail();
		} catch (EditorServiceException ese) {
		}
	}

	public void test_can_add_attribute_to_class() throws DomainNotFoundException, EditorServiceException {
		String classId = "classId";
		String attributeId = "attributeId";

		EditorService service = new EditorService();
		EditorCommandHistory retrievedHistory = service.createNewDomain("domainName");
	
		service.addClass(retrievedHistory, classId);
		service.addAttribute(retrievedHistory, classId, attributeId);
		Assert.assertTrue(service.getAttributeNamesOnClass(retrievedHistory, classId).contains("Attribute_" + attributeId));
	}

	public void test_cant_get_attribute_names_when_class_doesnt_exist() throws DomainNotFoundException, EditorServiceException {
		String className = "className";

		EditorService service = new EditorService();
		EditorCommandHistory retrievedHistory = service.createNewDomain("domainName");
	
		try {
			service.getAttributeNamesOnClass(retrievedHistory, className);
			fail();
		} catch (Exception e) {
		}
	}

	public void test_cant_get_attribute_datatype_when_class_doesnt_exist() throws DomainNotFoundException, EditorServiceException {
		String className = "className";

		EditorService service = new EditorService();
		EditorCommandHistory retrievedHistory = service.createNewDomain("domainName");
	
		try {
			service.getAttributeDatatype(retrievedHistory, className, "fred");
			fail();
		} catch (Exception e) {
		}
	}

	public void test_can_rename_attribute_in_class() throws DomainNotFoundException, EditorServiceException {
		String classId = "classId";
		String attributeId = "attributeId";
		String newAttributeName = "PersonAge";

		EditorService service = new EditorService();
		EditorCommandHistory retrievedHistory = service.createNewDomain("domainName");
	
		service.addClass(retrievedHistory, classId);
		service.addAttribute(retrievedHistory, classId, attributeId);
		Assert.assertTrue(service.getAttributeNamesOnClass(retrievedHistory, classId).contains("Attribute_" + attributeId));
		Assert.assertTrue(!service.getAttributeNamesOnClass(retrievedHistory, classId).contains(newAttributeName));
		service.changeAttributeName(retrievedHistory, classId, attributeId, newAttributeName);
		Assert.assertTrue(!service.getAttributeNamesOnClass(retrievedHistory, classId).contains("Attribute_" + attributeId));
		Assert.assertTrue(service.getAttributeNamesOnClass(retrievedHistory, classId).contains(newAttributeName));
	}

	public void test_can_change_attribute_datatype() throws DomainNotFoundException, EditorServiceException {
		String classId = "classId";
		String attributeId = "attributeId";

		EditorService service = new EditorService();
		EditorCommandHistory retrievedHistory = service.createNewDomain("domainName");
	
		service.addClass(retrievedHistory, classId);
		service.addAttribute(retrievedHistory, classId, attributeId);
		Assert.assertEquals("string", service.getAttributeDatatype(retrievedHistory, classId, attributeId));

		service.changeAttributeDatatype(retrievedHistory, classId, attributeId, BooleanEntityDatatype.getInstance());
		Assert.assertEquals("boolean", service.getAttributeDatatype(retrievedHistory, classId, attributeId));
	}

	public void test_cant_change_non_existant_attribute_datatype() throws DomainNotFoundException, EditorServiceException {
		String className = "className";
		String attributeName = "notAnAttribute";

		EditorService service = new EditorService();
		EditorCommandHistory retrievedHistory = service.createNewDomain("domainName");
	
		service.addClass(retrievedHistory, className);

		try {
			service.changeAttributeDatatype(retrievedHistory, className, attributeName, BooleanEntityDatatype.getInstance());
			fail();
		} catch (EditorServiceException e) {

		}
	}

	public void test_cant_get_nonexistant_attribute_datatype() throws DomainNotFoundException, EditorServiceException {
		String className = "className";
		String attributeName = "Age";

		EditorService service = new EditorService();
		EditorCommandHistory retrievedHistory = service.createNewDomain("domainName");
	
		service.addClass(retrievedHistory, className);
		try {
			Assert.assertEquals("int", service.getAttributeDatatype(retrievedHistory, className, attributeName));
			fail();
		} catch (Exception e) {
		}
	}


}
*/
