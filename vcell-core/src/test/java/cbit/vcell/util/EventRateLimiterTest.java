package cbit.vcell.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

@Disabled
@Tag("Fast")
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
				System.out.println(approvedEventCount + " events approved in one minute");
				// indicate pass?
			} else {
				System.out.println(approvedEventCount +" events approved in one minute is out of expected bounds");
                fail(approvedEventCount + " events approved in one minute is out of expected bounds");
			}
		} catch (Exception e) {
            fail("Exception occured: " + e.getMessage());
		}

	}

}
