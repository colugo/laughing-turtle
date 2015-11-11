package test.java.tests;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.metamodel.entities.datatypes.BooleanEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.FloatingEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.IntegerEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.InvalidEntityDatatype;
import main.java.avii.editor.metamodel.entities.datatypes.StringEntityDatatype;


public class DatatypeTests extends TestCase{

	public DatatypeTests(String name) {
		super(name);
	}
	
	public void test_boolean_datatype_accepts_upper_case_booleans()
	{
		BooleanEntityDatatype booleanDataType = BooleanEntityDatatype.getInstance();
		Assert.assertTrue(booleanDataType.acceptsValue("TRUE"));
		Assert.assertTrue(booleanDataType.acceptsValue("FALSE"));
	}
	
	public void test_boolean_datatype_accepts_lower_case_booleans()
	{
		BooleanEntityDatatype booleanDataType = BooleanEntityDatatype.getInstance();
		Assert.assertTrue(booleanDataType.acceptsValue("true"));
		Assert.assertTrue(booleanDataType.acceptsValue("false"));
	}
	
	public void test_boolean_datatype_accepts_camel_case_booleans()
	{
		BooleanEntityDatatype booleanDataType = BooleanEntityDatatype.getInstance();
		Assert.assertTrue(booleanDataType.acceptsValue("True"));
		Assert.assertTrue(booleanDataType.acceptsValue("False"));
	}
	
	public void test_boolean_datatype_does_not_accepts_junk()
	{
		BooleanEntityDatatype booleanDataType = BooleanEntityDatatype.getInstance();
		Assert.assertTrue(!booleanDataType.acceptsValue("asdfa"));
		Assert.assertTrue(!booleanDataType.acceptsValue("234"));
	}

	public void test_integer_datatype_accepts_integers()
	{
		IntegerEntityDatatype integerDataType = IntegerEntityDatatype.getInstance();
		Assert.assertTrue(integerDataType.acceptsValue("1"));
		Assert.assertTrue(integerDataType.acceptsValue("2"));
		Assert.assertTrue(integerDataType.acceptsValue("2192874098234"));
		Assert.assertTrue(integerDataType.acceptsValue("-2"));
	}
	
	public void test_integer_datatype_does_not_accepts_junk()
	{
		IntegerEntityDatatype integerDataType = IntegerEntityDatatype.getInstance();
		Assert.assertTrue(!integerDataType.acceptsValue(""));
		Assert.assertTrue(!integerDataType.acceptsValue("s2"));
		Assert.assertTrue(!integerDataType.acceptsValue("1.2"));
		Assert.assertTrue(!integerDataType.acceptsValue("1fred"));
		Assert.assertTrue(!integerDataType.acceptsValue("  1"));
		Assert.assertTrue(!integerDataType.acceptsValue("1  "));
	}
	
	
	public void test_floating_datatype_accepts_floatings()
	{
		FloatingEntityDatatype FloatingDataType = FloatingEntityDatatype.getInstance();
		Assert.assertTrue(FloatingDataType.acceptsValue("1"));
		Assert.assertTrue(FloatingDataType.acceptsValue("2"));
		Assert.assertTrue(FloatingDataType.acceptsValue("2192874098234"));
		Assert.assertTrue(FloatingDataType.acceptsValue("0.2"));
	}
	
	public void test_floating_datatype_does_not_accepts_junk()
	{
		FloatingEntityDatatype FloatingDataType = FloatingEntityDatatype.getInstance();
		Assert.assertTrue(!FloatingDataType.acceptsValue(""));
		Assert.assertTrue(!FloatingDataType.acceptsValue("s2"));
		Assert.assertTrue(!FloatingDataType.acceptsValue("1.2a"));
		Assert.assertTrue(!FloatingDataType.acceptsValue(".2"));
		Assert.assertTrue(!FloatingDataType.acceptsValue("1fred"));
		Assert.assertTrue(!FloatingDataType.acceptsValue("  1"));
		Assert.assertTrue(!FloatingDataType.acceptsValue("1  "));
	}
	
	
	public void test_string_datatype_accepts_strings()
	{
		StringEntityDatatype StringDataType = StringEntityDatatype.getInstance();
		Assert.assertTrue(StringDataType.acceptsValue("\"\""));
		Assert.assertTrue(StringDataType.acceptsValue("\"test 124\""));
		Assert.assertTrue(StringDataType.acceptsValue("\"hello, world\""));
		Assert.assertTrue(StringDataType.acceptsValue("\"\"\""));
	}

	public void test_string_datatype_rejects_non_strings()
	{
		StringEntityDatatype StringDataType = StringEntityDatatype.getInstance();
		Assert.assertTrue(!StringDataType.acceptsValue(""));
		Assert.assertTrue(!StringDataType.acceptsValue("test 124"));
		Assert.assertTrue(!StringDataType.acceptsValue("hello, world"));
		Assert.assertTrue(!StringDataType.acceptsValue("\""));
	}
	
	public void test_string_datatype_only_can_be_compared_to_string_datatype()
	{
		StringEntityDatatype StringDataType = StringEntityDatatype.getInstance();
		FloatingEntityDatatype FloatingDataType = FloatingEntityDatatype.getInstance();
		IntegerEntityDatatype IntegerDataType = IntegerEntityDatatype.getInstance();
		BooleanEntityDatatype BooleanDataType = BooleanEntityDatatype.getInstance();
		
		Assert.assertTrue(StringDataType.canBeComparedToDatatype(StringDataType));
		Assert.assertTrue(StringDataType.canBeSetToDatatype(StringDataType));
		
		Assert.assertTrue(!StringDataType.canBeComparedToDatatype(FloatingDataType));
		Assert.assertTrue(!StringDataType.canBeSetToDatatype(FloatingDataType));
		
		Assert.assertTrue(!StringDataType.canBeComparedToDatatype(IntegerDataType));
		Assert.assertTrue(!StringDataType.canBeSetToDatatype(IntegerDataType));
		
		Assert.assertTrue(!StringDataType.canBeComparedToDatatype(BooleanDataType));
		Assert.assertTrue(!StringDataType.canBeSetToDatatype(BooleanDataType));
	}
	
	public void test_boolean_datatype_only_can_be_compared_to_boolean_datatype()
	{
		StringEntityDatatype StringDataType = StringEntityDatatype.getInstance();
		FloatingEntityDatatype FloatingDataType = FloatingEntityDatatype.getInstance();
		IntegerEntityDatatype IntegerDataType = IntegerEntityDatatype.getInstance();
		BooleanEntityDatatype BooleanDataType = BooleanEntityDatatype.getInstance();
		
		Assert.assertTrue(BooleanDataType.canBeComparedToDatatype(BooleanDataType));
		Assert.assertTrue(BooleanDataType.canBeSetToDatatype(BooleanDataType));
		
		Assert.assertTrue(!BooleanDataType.canBeComparedToDatatype(FloatingDataType));
		Assert.assertTrue(!BooleanDataType.canBeSetToDatatype(FloatingDataType));
		
		Assert.assertTrue(!BooleanDataType.canBeComparedToDatatype(IntegerDataType));
		Assert.assertTrue(!BooleanDataType.canBeSetToDatatype(IntegerDataType));
		
		Assert.assertTrue(!BooleanDataType.canBeComparedToDatatype(StringDataType));
		Assert.assertTrue(!BooleanDataType.canBeSetToDatatype(StringDataType));
	}
	
	
	public void test_integer_datatype_only_can_be_compared_to_int_and_float_and_set_to_int_datatype()
	{
		StringEntityDatatype StringDataType = StringEntityDatatype.getInstance();
		FloatingEntityDatatype FloatingDataType = FloatingEntityDatatype.getInstance();
		IntegerEntityDatatype IntegerDataType = IntegerEntityDatatype.getInstance();
		BooleanEntityDatatype BooleanDataType = BooleanEntityDatatype.getInstance();
		
		Assert.assertTrue(IntegerDataType.canBeComparedToDatatype(IntegerDataType));
		Assert.assertTrue(IntegerDataType.canBeSetToDatatype(IntegerDataType));
		
		Assert.assertTrue(IntegerDataType.canBeComparedToDatatype(FloatingDataType));
		Assert.assertTrue(!IntegerDataType.canBeSetToDatatype(FloatingDataType));
		
		Assert.assertTrue(!IntegerDataType.canBeComparedToDatatype(StringDataType));
		Assert.assertTrue(!IntegerDataType.canBeSetToDatatype(StringDataType));
		
		Assert.assertTrue(!IntegerDataType.canBeComparedToDatatype(BooleanDataType));
		Assert.assertTrue(!IntegerDataType.canBeSetToDatatype(BooleanDataType));
	}
	
	public void test_float_datatype_only_can_be_compared_to_int_and_float_and_set_to_int_and_float_datatype()
	{
		StringEntityDatatype StringDataType = StringEntityDatatype.getInstance();
		FloatingEntityDatatype FloatingDataType = FloatingEntityDatatype.getInstance();
		IntegerEntityDatatype IntegerDataType = IntegerEntityDatatype.getInstance();
		BooleanEntityDatatype BooleanDataType = BooleanEntityDatatype.getInstance();
		
		Assert.assertTrue(FloatingDataType.canBeComparedToDatatype(FloatingDataType));
		Assert.assertTrue(FloatingDataType.canBeSetToDatatype(FloatingDataType));
		
		Assert.assertTrue(FloatingDataType.canBeComparedToDatatype(IntegerDataType));
		Assert.assertTrue(FloatingDataType.canBeSetToDatatype(IntegerDataType));
		
		Assert.assertTrue(!FloatingDataType.canBeComparedToDatatype(StringDataType));
		Assert.assertTrue(!FloatingDataType.canBeSetToDatatype(StringDataType));
		
		Assert.assertTrue(!FloatingDataType.canBeComparedToDatatype(BooleanDataType));
		Assert.assertTrue(!FloatingDataType.canBeSetToDatatype(BooleanDataType));
	}
	
	public void test_all_datatypes_cannot_be_set_or_compared_to_invalid_datatypes()
	{
		StringEntityDatatype StringDataType = StringEntityDatatype.getInstance();
		FloatingEntityDatatype FloatingDataType = FloatingEntityDatatype.getInstance();
		IntegerEntityDatatype IntegerDataType = IntegerEntityDatatype.getInstance();
		BooleanEntityDatatype BooleanDataType = BooleanEntityDatatype.getInstance();
		InvalidEntityDatatype InvalidDatatype = InvalidEntityDatatype.getInstance(); 
		
		Assert.assertTrue(!FloatingDataType.canBeComparedToDatatype(InvalidDatatype));
		Assert.assertTrue(!FloatingDataType.canBeSetToDatatype(InvalidDatatype));
		
		Assert.assertTrue(!StringDataType.canBeComparedToDatatype(InvalidDatatype));
		Assert.assertTrue(!StringDataType.canBeSetToDatatype(InvalidDatatype));
		
		Assert.assertTrue(!IntegerDataType.canBeComparedToDatatype(InvalidDatatype));
		Assert.assertTrue(!IntegerDataType.canBeSetToDatatype(InvalidDatatype));
		
		Assert.assertTrue(!BooleanDataType.canBeComparedToDatatype(InvalidDatatype));
		Assert.assertTrue(!BooleanDataType.canBeSetToDatatype(InvalidDatatype));
	}
	
	public void test_invalid_datatypes_cannot_be_set_or_compared_to_all_datatypes()
	{
		StringEntityDatatype StringDataType = StringEntityDatatype.getInstance();
		FloatingEntityDatatype FloatingDataType = FloatingEntityDatatype.getInstance();
		IntegerEntityDatatype IntegerDataType = IntegerEntityDatatype.getInstance();
		BooleanEntityDatatype BooleanDataType = BooleanEntityDatatype.getInstance();
		InvalidEntityDatatype InvalidDatatype = InvalidEntityDatatype.getInstance(); 
		
		Assert.assertTrue(!InvalidDatatype.canBeComparedToDatatype(StringDataType));
		Assert.assertTrue(!InvalidDatatype.canBeSetToDatatype(StringDataType));
		
		Assert.assertTrue(!InvalidDatatype.canBeComparedToDatatype(FloatingDataType));
		Assert.assertTrue(!InvalidDatatype.canBeSetToDatatype(FloatingDataType));
		
		Assert.assertTrue(!InvalidDatatype.canBeComparedToDatatype(IntegerDataType));
		Assert.assertTrue(!InvalidDatatype.canBeSetToDatatype(IntegerDataType));

		Assert.assertTrue(!InvalidDatatype.canBeComparedToDatatype(BooleanDataType));
		Assert.assertTrue(!InvalidDatatype.canBeSetToDatatype(BooleanDataType));
		
		Assert.assertTrue(!InvalidDatatype.canBeComparedToDatatype(InvalidDatatype));
		Assert.assertTrue(!InvalidDatatype.canBeSetToDatatype(InvalidDatatype));
	}
	
	
	public void test_datatype_knows_what_type_to_return_if_compared_to()
	{
		StringEntityDatatype StringDataType = StringEntityDatatype.getInstance();
		FloatingEntityDatatype FloatingDataType = FloatingEntityDatatype.getInstance();
		IntegerEntityDatatype IntegerDataType = IntegerEntityDatatype.getInstance();
		BooleanEntityDatatype BooleanDataType = BooleanEntityDatatype.getInstance();
		InvalidEntityDatatype InvalidDatatype = InvalidEntityDatatype.getInstance(); 

		Assert.assertEquals(StringDataType.getResultingDatatypeWhenCombinedWith(StringDataType).getClass(), StringDataType.getClass());
		Assert.assertEquals(StringDataType.getResultingDatatypeWhenCombinedWith(FloatingDataType).getClass(), StringDataType.getClass());
		Assert.assertEquals(StringDataType.getResultingDatatypeWhenCombinedWith(IntegerDataType).getClass(), StringDataType.getClass());
		Assert.assertEquals(StringDataType.getResultingDatatypeWhenCombinedWith(BooleanDataType).getClass(), StringDataType.getClass());
		Assert.assertEquals(StringDataType.getResultingDatatypeWhenCombinedWith(InvalidDatatype).getClass(), InvalidDatatype.getClass());
		
		Assert.assertEquals(FloatingDataType.getResultingDatatypeWhenCombinedWith(StringDataType).getClass(), StringDataType.getClass());
		Assert.assertEquals(FloatingDataType.getResultingDatatypeWhenCombinedWith(FloatingDataType).getClass(), FloatingDataType.getClass());
		Assert.assertEquals(FloatingDataType.getResultingDatatypeWhenCombinedWith(IntegerDataType).getClass(), FloatingDataType.getClass());
		Assert.assertEquals(FloatingDataType.getResultingDatatypeWhenCombinedWith(BooleanDataType).getClass(), InvalidDatatype.getClass());
		Assert.assertEquals(FloatingDataType.getResultingDatatypeWhenCombinedWith(InvalidDatatype).getClass(), InvalidDatatype.getClass());
		

		Assert.assertEquals(IntegerDataType.getResultingDatatypeWhenCombinedWith(StringDataType).getClass(), StringDataType.getClass());
		Assert.assertEquals(IntegerDataType.getResultingDatatypeWhenCombinedWith(FloatingDataType).getClass(), FloatingDataType.getClass());
		Assert.assertEquals(IntegerDataType.getResultingDatatypeWhenCombinedWith(IntegerDataType).getClass(), IntegerDataType.getClass());
		Assert.assertEquals(IntegerDataType.getResultingDatatypeWhenCombinedWith(BooleanDataType).getClass(), InvalidDatatype.getClass());
		Assert.assertEquals(IntegerDataType.getResultingDatatypeWhenCombinedWith(InvalidDatatype).getClass(), InvalidDatatype.getClass());
		

		Assert.assertEquals(BooleanDataType.getResultingDatatypeWhenCombinedWith(StringDataType).getClass(), StringDataType.getClass());
		Assert.assertEquals(BooleanDataType.getResultingDatatypeWhenCombinedWith(FloatingDataType).getClass(), InvalidDatatype.getClass());
		Assert.assertEquals(BooleanDataType.getResultingDatatypeWhenCombinedWith(IntegerDataType).getClass(), InvalidDatatype.getClass());
		Assert.assertEquals(BooleanDataType.getResultingDatatypeWhenCombinedWith(BooleanDataType).getClass(), BooleanDataType.getClass());
		Assert.assertEquals(BooleanDataType.getResultingDatatypeWhenCombinedWith(InvalidDatatype).getClass(), InvalidDatatype.getClass());

		Assert.assertEquals(InvalidDatatype.getResultingDatatypeWhenCombinedWith(StringDataType).getClass(), InvalidDatatype.getClass());
		Assert.assertEquals(InvalidDatatype.getResultingDatatypeWhenCombinedWith(FloatingDataType).getClass(), InvalidDatatype.getClass());
		Assert.assertEquals(InvalidDatatype.getResultingDatatypeWhenCombinedWith(IntegerDataType).getClass(), InvalidDatatype.getClass());
		Assert.assertEquals(InvalidDatatype.getResultingDatatypeWhenCombinedWith(BooleanDataType).getClass(), InvalidDatatype.getClass());
		Assert.assertEquals(InvalidDatatype.getResultingDatatypeWhenCombinedWith(InvalidDatatype).getClass(), InvalidDatatype.getClass());
	}
	
	public void test_datatype_can_convert_to_correct_value_from_string()
	{
		Assert.assertEquals(1, IntegerEntityDatatype.getInstance().correctTypeOf("1"));
		Assert.assertEquals(1.0, FloatingEntityDatatype.getInstance().correctTypeOf("1"));
		Assert.assertEquals(false, BooleanEntityDatatype.getInstance().correctTypeOf("false"));
	}
	
	public void test_datatype_knows_what_they_can_be_combined_with()
	{
		StringEntityDatatype StringDataType = StringEntityDatatype.getInstance();
		FloatingEntityDatatype FloatingDataType = FloatingEntityDatatype.getInstance();
		IntegerEntityDatatype IntegerDataType = IntegerEntityDatatype.getInstance();
		BooleanEntityDatatype BooleanDataType = BooleanEntityDatatype.getInstance();
		InvalidEntityDatatype InvalidDatatype = InvalidEntityDatatype.getInstance(); 

		Assert.assertTrue(StringDataType.canBeCombinedWithDatatype(StringDataType));
		Assert.assertTrue(StringDataType.canBeCombinedWithDatatype(FloatingDataType));
		Assert.assertTrue(StringDataType.canBeCombinedWithDatatype(IntegerDataType));
		Assert.assertTrue(StringDataType.canBeCombinedWithDatatype(BooleanDataType));
		Assert.assertTrue(!StringDataType.canBeCombinedWithDatatype(InvalidDatatype));

		Assert.assertTrue(FloatingDataType.canBeCombinedWithDatatype(StringDataType));
		Assert.assertTrue(FloatingDataType.canBeCombinedWithDatatype(FloatingDataType));
		Assert.assertTrue(FloatingDataType.canBeCombinedWithDatatype(IntegerDataType));
		Assert.assertTrue(!FloatingDataType.canBeCombinedWithDatatype(BooleanDataType));
		Assert.assertTrue(!FloatingDataType.canBeCombinedWithDatatype(InvalidDatatype));
		
		Assert.assertTrue(IntegerDataType.canBeCombinedWithDatatype(StringDataType));
		Assert.assertTrue(IntegerDataType.canBeCombinedWithDatatype(FloatingDataType));
		Assert.assertTrue(IntegerDataType.canBeCombinedWithDatatype(IntegerDataType));
		Assert.assertTrue(!IntegerDataType.canBeCombinedWithDatatype(BooleanDataType));
		Assert.assertTrue(!IntegerDataType.canBeCombinedWithDatatype(InvalidDatatype));
		
		Assert.assertTrue(BooleanDataType.canBeCombinedWithDatatype(StringDataType));
		Assert.assertTrue(!BooleanDataType.canBeCombinedWithDatatype(FloatingDataType));
		Assert.assertTrue(!BooleanDataType.canBeCombinedWithDatatype(IntegerDataType));
		Assert.assertTrue(BooleanDataType.canBeCombinedWithDatatype(BooleanDataType));
		Assert.assertTrue(!BooleanDataType.canBeCombinedWithDatatype(InvalidDatatype));

		Assert.assertTrue(!InvalidDatatype.canBeCombinedWithDatatype(StringDataType));
		Assert.assertTrue(!InvalidDatatype.canBeCombinedWithDatatype(FloatingDataType));
		Assert.assertTrue(!InvalidDatatype.canBeCombinedWithDatatype(IntegerDataType));
		Assert.assertTrue(!InvalidDatatype.canBeCombinedWithDatatype(BooleanDataType));
		Assert.assertTrue(!InvalidDatatype.canBeCombinedWithDatatype(InvalidDatatype));
		
	}
	
}

