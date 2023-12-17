package cbit.vcell.mongodb;

import org.junit.jupiter.api.*;
import static org.junit.Assert.assertArrayEquals;

import org.bson.types.ObjectId;
@Tag("Fast")
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;

@Ignore
@Category(Fast.class)
public class VCMongoDbDriverTest {
	VCMongoDbDriver mongoDriver = null;

	@Before
	public void setUp() {
		System.getProperties().put("vcell.mongodb.host","localhost");
		System.getProperties().put("vcell.mongodb.port","27017");
		System.getProperties().put("vcell.mongodb.database","test");

		mongoDriver = VCMongoDbDriver.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		mongoDriver.shutdown();
	}

	@Test
	public void test() {
		byte[] data1 = new byte[] { 11,22,33,44,55 };
		ObjectId id1 = mongoDriver.storeBLOB("testblob1", "jmsblob", data1);
		byte[] d1 = mongoDriver.getBLOB(new ObjectId(id1.toHexString()));
		assertArrayEquals(data1, d1);

		byte[] data2 = new byte[] { 99,88,77,66,55,44,33,22 };
		ObjectId id2 = mongoDriver.storeBLOB("testblob2", "jmsblob", data2);
		byte[] d2 = mongoDriver.getBLOB(new ObjectId(id2.toHexString()));
		assertArrayEquals(data2, d2);
	}

}
