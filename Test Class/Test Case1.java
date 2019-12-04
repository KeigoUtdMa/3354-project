//This is the test class for the method public Contact readEntity(Cursor cursor, int offset)
//in the file ContactDao.java
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class readEntity{
	private readEntity tester;
	// test contact 			id 	name 	phone1 	phone2 	tel 	icon 	letter 	groupid 	isblacklist or not
	Contact steven = new Contact(0, "steven", "111", "112", "113", "icon", "letter", 999, false);
	Contact keigo = new Contact(1, "keigo", "221", "222", "223", "icon", "letter", 999, false);
	Contact robert = new Contact(3, "robert", "331", "332", "333", "icon", "letter", 999, false);
	Contact esther = new Contact(4, "esther", "441", "442", "443", "icon", "letter", 999, false);
	Contact empty = null;

	Cursor cursor = database.query(ColumnID.AGENT_TABLE,null,null,null,null,null,null);
	Contact actual = readEntity(cursor, 0);
	@Before
	public void setUp(){
		readEnity = new readEnity();
	}
	@Test // when input correct contact
	public void test1(){
		AssertEqual(actual, steven);
		AssertEqual(actual, keigo);
		AssertEqual(actual, robert);
		AssertEqual(actual, esther);
	}
	@Test // when input incorrect contact
	public void test2(){
		AssertEqual(actual, steven);
		AssertEqual(actual, keigo);
		AssertEqual(actual, robert);
		AssertEqual(actual, esther);
	}
	@Test // when input something then compare to null contact
	public void test3(){
		AssertEqual(actual, empty);
	}
	@Test // when input nothing then compare to null contact
	public void test4(){
		AssertEqual(actual, empty);
	}
}