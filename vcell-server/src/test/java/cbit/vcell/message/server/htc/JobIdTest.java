package cbit.vcell.message.server.htc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import cbit.vcell.messaging.db.SimulationJobTable;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.server.HtcJobID.BatchSystemType;

/**
 * Test {@link HtcJobID#compareEqual(org.vcell.util.Matchable)},
 * {@link HtcJobID#equals(Object)} and {@link HtcJobID#hashCode()}
 */
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
		assertTrue(x.equals(y));
		assertTrue(x.compareEqual(y));
		assertTrue(x.hashCode( ) == y.hashCode());
		String server = randomServer( );
		x = gen(n,bt,server);
		y = gen(n,bt,server);
		assertTrue(x.equals(y));
		assertTrue(x.compareEqual(y));
		assertTrue(x.hashCode( ) == y.hashCode());
	}

	private void sysDiff(BatchSystemType bt) {
		long n = r.nextLong();
		HtcJobID x = gen(n,bt,null);
		HtcJobID y = gen(n + 1,bt,null);
		assertFalse(x.equals(y));
		assertFalse(x.compareEqual(y));

		String serverx = randomServer( );
		String servery = randomServer( );
		x = gen(n,bt,serverx);
		y = gen(n + 1,bt,servery);
		assertFalse(x.equals(y));
		assertFalse(x.compareEqual(y));
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
		HtcJobID x = gen(n,BatchSystemType.SGE,null);
		HtcJobID y = gen(n,BatchSystemType.PBS,null);
		HtcJobID z = gen(n,BatchSystemType.SLURM,null);
		assertFalse(x.equals(y));
		assertFalse(x.compareEqual(y));
		assertFalse(x.equals(z));
		assertFalse(x.compareEqual(z));
		assertFalse(y.equals(z));
		assertFalse(y.compareEqual(z));

		String server = randomServer( );
		x = gen(n,BatchSystemType.SGE,server);
		y = gen(n,BatchSystemType.PBS,server);
		z = gen(n,BatchSystemType.SLURM,server);
		assertFalse(x.equals(y));
		assertFalse(x.compareEqual(y));
		assertFalse(x.equals(z));
		assertFalse(x.compareEqual(z));
		assertFalse(y.equals(z));
		assertFalse(y.compareEqual(z));
	}
}
