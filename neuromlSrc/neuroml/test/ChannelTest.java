package neuroml.test;

import java.io.*;
import java.math.*;
import java.util.*;

import neuroml.channel.*;
import neuroml.channel.Gate.*;
import neuroml.channel.Ohmic.*;
import neuroml.channel.RateAdjustments.*;
import neuroml.util.*;

import org.junit.*;

import static org.junit.Assert.*;

public class ChannelTest
{
	
	@Test public void testKCaChannelUnmarshalling() throws Exception
	{	
		NeuroMLConverter conv = new NeuroMLConverter();
		
		String confdir = AllTests.properties.get(AllTests.PROP_CONFDIR);
		String fpath = confdir + File.separator + "test"
				          + File.separator +"KCa_chan.xml";
		
		ChannelML chan = conv.xmlToChannel(fpath);
		
		List<Ion> ions = chan.getIon();
		assertTrue(ions.size() == 2);
		
		Ion k = chan.getIonByName("k");
		assertTrue("k".equals(k.getName()));
		assertTrue(k.getDefaultErev() == -0.09);
		assertTrue("1".equals(k.getCharge().toString()));
		assertTrue("PermeatedSubstance".equals(k.getRole().value()));
		
		Ion ca = chan.getIonByName("ca");
		assertTrue("ca".equals(ca.getName()));
		assertTrue("2".equals(ca.getCharge().toString()));
		assertTrue("ModulatingSubstance".equals(ca.getRole().value()));
		
		List<ChannelType> ctypes = chan.getChannelType();
		assertTrue(ctypes.size() == 1);
		
		ChannelType kca = chan.getChannelTypeByName("Generic_KCa");
		assertTrue("Generic_KCa".equals(kca.getName()));
		
		CurrentVoltageRelation iv = kca.getCurrentVoltageRelation();
		Ohmic o = iv.getOhmic();
		assertTrue("k".equals(o.getIon()));
		
		Conductance cond = o.getConductance();
		assertTrue(cond.getDefaultGmax() ==  0.179811);
		
		RateAdjustments adj = cond.getRateAdjustments();
		Q10Settings q10 = adj.getQ10Settings();
		assertTrue(q10.getExperimentalTemp() == 17.350264793);
		assertTrue(q10.getQ10Factor() == 3.0);
		Offset off = adj.getOffset();
		assertTrue(off.getValue() == 0.010);
				
		Gate g = cond.getGate().get(0);
		assertTrue(g.getPower().toString().equals("1"));
		List<State> states = g.getState();
		State m = states.get(0);
		assertTrue(m.getName().equals("m"));
		assertTrue(m.getFraction() == 1.0);
	}
	
	@Test public void testKCaChannelMarshalling() throws Exception
	{
		neuroml.channel.ObjectFactory obj = new neuroml.channel.ObjectFactory();
		
		ChannelML chan = obj.createChannelML();
		
		Ion k = obj.createIon();
		k.setName("k");
		k.setCharge(new BigInteger("1"));
		k.setDefaultErev(-0.09);		
		k.setRole(IonRole.PERMEATED_SUBSTANCE);
		
		chan.getIon().add(k);
		
		String tempdir = AllTests.properties.get(AllTests.PROP_TEMPDIR);
		
		NeuroMLConverter conv = new NeuroMLConverter();
		conv.channelToXml(chan, tempdir + File.separator + "kca-channel.xml");
	}
}
