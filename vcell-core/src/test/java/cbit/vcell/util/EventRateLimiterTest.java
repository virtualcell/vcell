package cbit.vcell.util;

import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;

@Ignore
@Category(Fast.class)
public class EventRateLimiterTest {

	@Test
	public void test() {
		
		try {
			EventRateLimiter eventRateLimiter = new EventRateLimiter();
			long testStartTime = System.currentTimeMillis();
			System.out.println("Starting at " + testStartTime);
			int approvedEventCount = 0;
			while (System.currentTimeMillis() < testStartTime + 60000) {
				if (eventRateLimiter.isOkayToFireEventNow()) {
					approvedEventCount++;
					System.out.println("Now it is "+ System.currentTimeMillis());
				}
			}
			if ((approvedEventCount > 67) && (approvedEventCount < 73)) {
				System.out.println(String.valueOf(approvedEventCount)+ " events approved in one minute");
				// indicate pass?
			} else {
				System.out.println(String.valueOf(approvedEventCount)+" events approved in one minute is out of expected bounds");
				fail(String.valueOf(approvedEventCount)+" events approved in one minute is out of expected bounds");
			}
		} catch (Exception e) {
			fail("Exception occured: "+e.getMessage());
		}

	}

}
