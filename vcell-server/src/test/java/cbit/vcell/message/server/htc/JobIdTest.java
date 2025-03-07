package cbit.vcell.message.server.htc;

import cbit.vcell.messaging.db.SimulationJobTable;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.server.HtcJobID.BatchSystemType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Test {@link HtcJobID#compareEqual(org.vcell.util.Matchable)},
 * {@link HtcJobID#equals(Object)} and {@link HtcJobID#hashCode()}
 */
@Tag("Fast")
public class JobIdTest {

	Random r = new Random( );

	private HtcJobID gen(long n, BatchSystemType bst, String server) {
		String dbString = bst + ":" + n;
		if (server != null) {
			dbString += "." + server;
		}
		return SimulationJobTable.fromDatabase(dbString) ;
	}

	private String randomServer( ) {
		int length = 1 + r.nextInt(15);
		return RandomStringUtils.randomAlphanumeric(length);
	}

	private void sysSame(BatchSystemType bt) {
		long n = r.nextLong();
		HtcJobID x = gen(n,bt,null);
		HtcJobID y = gen(n,bt,null);
        Assertions.assertEquals(x, y);
        Assertions.assertTrue(x.compareEqual(y));
        Assertions.assertEquals(x.hashCode(), y.hashCode());
		String server = randomServer( );
		x = gen(n,bt,server);
		y = gen(n,bt,server);
        Assertions.assertEquals(x, y);
        Assertions.assertTrue(x.compareEqual(y));
        Assertions.assertEquals(x.hashCode(), y.hashCode());
	}

	private void sysDiff(BatchSystemType bt) {
		long n = r.nextLong();
		HtcJobID x = gen(n, bt, null);
		HtcJobID y = gen(n + 1, bt, null);
        assertNotEquals(x, y);
		Assertions.assertFalse(x.compareEqual(y));

		String serverx = randomServer();
		String servery = randomServer();
		x = gen(n, bt, serverx);
		y = gen(n + 1, bt, servery);
        assertNotEquals(x, y);
		Assertions.assertFalse(x.compareEqual(y));
	}

	@Test
	public void sgeSame() {
		sysSame(BatchSystemType.SGE);
	}
	@Test
	public void sgeDiff() {
		sysDiff(BatchSystemType.SGE);
	}

	@Test
	public void slurmSame() {
		sysSame(BatchSystemType.SLURM);
	}
	@Test
	public void slurmDiff() {
		sysDiff(BatchSystemType.SLURM);
	}

	@Test
	public void pbsSame() {
		sysSame(BatchSystemType.PBS);
	}
	@Test
	public void pbsDiff() {
		sysDiff(BatchSystemType.PBS);
	}

	@Test
	public void diffBatch( ) {
		long n = r.nextLong();
		HtcJobID x = gen(n, BatchSystemType.SGE, null);
		HtcJobID y = gen(n, BatchSystemType.PBS, null);
		HtcJobID z = gen(n, BatchSystemType.SLURM, null);
        assertNotEquals(x, y);
		Assertions.assertFalse(x.compareEqual(y));
        assertNotEquals(x, z);
		Assertions.assertFalse(x.compareEqual(z));
        assertNotEquals(y, z);
		Assertions.assertFalse(y.compareEqual(z));

		String server = randomServer();
		x = gen(n, BatchSystemType.SGE, server);
		y = gen(n, BatchSystemType.PBS, server);
		z = gen(n, BatchSystemType.SLURM, server);
        assertNotEquals(x, y);
		Assertions.assertFalse(x.compareEqual(y));
        assertNotEquals(x, z);
		Assertions.assertFalse(x.compareEqual(z));
        assertNotEquals(y, z);
		Assertions.assertFalse(y.compareEqual(z));
	}
}
