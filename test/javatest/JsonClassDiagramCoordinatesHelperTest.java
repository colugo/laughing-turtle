package javatest;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.models.JsonClassDiagramCoordinatesHelper;
import models.JsonCoordinateHelper;

public class JsonClassDiagramCoordinatesHelperTest extends TestCase {
	public JsonClassDiagramCoordinatesHelperTest(String name){
		super(name);
	}
	
	public void test_can_deserialise_test_json_string(){
		String json = "{\"classes\":[{\"uuid\":\"YTfnwlMmFe\",\"x\":164,\"y\":41},{\"uuid\":\"Ue35ykjOs5\",\"x\":330,\"y\":81},{\"uuid\":\"SZnzAPCBOS\",\"x\":118,\"y\":120}]}";
		JsonClassDiagramCoordinatesHelper helper = new JsonClassDiagramCoordinatesHelper();
		JsonCoordinateHelper.help(json, helper);
		Assert.assertEquals(3, helper.classes.size());
		Assert.assertEquals("YTfnwlMmFe", helper.classes.get(0).uuid);
		Assert.assertEquals((double)164, helper.classes.get(0).x);
		Assert.assertEquals((double)41, helper.classes.get(0).y);
		
		Assert.assertEquals("Ue35ykjOs5", helper.classes.get(1).uuid);
		Assert.assertEquals((double)330, helper.classes.get(1).x);
		Assert.assertEquals((double)81, helper.classes.get(1).y);
		
		Assert.assertEquals("SZnzAPCBOS", helper.classes.get(2).uuid);
		Assert.assertEquals((double)118, helper.classes.get(2).x);
		Assert.assertEquals((double)120, helper.classes.get(2).y);
	}
}
