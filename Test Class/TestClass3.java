//This is the test class for the method 
//public ContactGroup readEntity(Cursor cursor, int offset) 
//wrote in the file contactGroupDao.java

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class readEntity{
	private readEntity tester;
	
	ContactGroup g1 = new ContactGroup(1,"group1");
	ContactGroup g2 = new ContactGroup(2,"group2");
	ContactGroup g3 = new ContactGroup(3,"group3");
	ContactGroup g4 = new ContactGroup(4,"group4");
	ContactGroup empty = null;

	Cursor cursor = database.query(ColumnID.AGENT_TABLE,null,null,null,null,null,null);
	ContactGroup actual = readEntity(cursor, 0);
	@Before
	public void setUp(){
		readEnity = new readEnity();
	}
	@Test // when input correct contactgroup
	public void test1(){
		AssertEqual(actual, g1);
		AssertEqual(actual, g2);
		AssertEqual(actual, g3);
		AssertEqual(actual, g4);
	}
	@Test // when input incorrect contactgroup
	public void test2(){
		AssertEqual(actual, g1);
		AssertEqual(actual, g2);
		AssertEqual(actual, g3);
		AssertEqual(actual, g4);
	}
	@Test // when input something then compare to null contactgroup
	public void test3(){
		AssertEqual(actual, empty);
	}
	@Test // when input nothing then compare to null contactgroup
	public void test4(){
		AssertEqual(actual, empty);
	}
}