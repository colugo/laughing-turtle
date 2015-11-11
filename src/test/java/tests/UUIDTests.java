/*
package test.java.tests;

import implementation.ConcreteUUIDIdentifier;

import java.io.StringWriter;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;


public class UUIDTests extends TestCase{

	public UUIDTests(String name) {
		super(name);
	}
	
	public void test_concrete_u_u_i_d_gets_different_u_u_i_d_every_instance()
	{
		ConcreteUUIDIdentifier cui1 = new ConcreteUUIDIdentifier();
		ConcreteUUIDIdentifier cui2 = new ConcreteUUIDIdentifier();
		
		Assert.assertTrue(!cui1.getUUIDString().equals(cui2.getUUIDString()));
	}
	
	public void test_concrete_u_u_i_d_get_method_returns_the_same_u_u_i_d_each_time()
	{
		ConcreteUUIDIdentifier cui1 = new ConcreteUUIDIdentifier();
		
		Assert.assertEquals(cui1.getUUIDString(), cui1.getUUIDString());
	}
	
	public void test_serialize_u_u_i_d() throws Exception
	{
		ConcreteUUIDIdentifier cui1 = new ConcreteUUIDIdentifier();
		cui1.getUUIDString();
		
		Serializer serializer = new Persister();
		StringWriter sw = new StringWriter();
		serializer.write(cui1,sw);
		
		String serialised = sw.toString();
		
		Serializer serializer2 = new Persister();
		ConcreteUUIDIdentifier cui2 = serializer2.read(ConcreteUUIDIdentifier.class, serialised);
		Assert.assertEquals(cui1.getUUIDString(), cui2.getUUIDString());
		
	}

}
*/


