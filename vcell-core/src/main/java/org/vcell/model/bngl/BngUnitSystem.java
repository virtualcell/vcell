package org.vcell.model.bngl;

import cbit.vcell.model.ModelUnitSystem;

public class BngUnitSystem {

	public enum TimeUnitSystem {
		TimeUnitSymbol_s("s","s (seconds)"),
		TimeUnitSymbol_ms("ms","ms (milliseconds)"),
		TimeUnitSymbol_min("min","min (minutes)"),
		TimeUnitSymbol_hr("hour","hr (hours)");

		public final String timeSymbol;
		public final String description;

		private TimeUnitSystem(String timeUnitSymbol, String description){
			this.timeSymbol = timeUnitSymbol;
			this.description = description;
		}
	};

	public enum VolumeUnitSystem {
		VolumeUnit_um3("um3 (micron^3)","molecules","molecules","molecules","um3","um2","um"),
		VolumeUnit_nl("nl (nanoliter)","molecules","molecules","molecules","nl","um2","um"),
		VolumeUnit_pl("pl (picoliter)","molecules","molecules","molecules","pl","um2","um");

		public final String description;
		public final String volumeSubstanceSymbol;
		public final String membraneSubstanceSymbol;
		public final String lumpedReactionSubstanceSymbol;
		public final String volumeSymbol;
		public final String areaSymbol;
		public final String lengthSymbol;

		private VolumeUnitSystem(String volumeUnitDescription, String volumeSubstance, String membraneSubstance, String lumpedReactionSubstance, String volume, String area, String length){
			this.description = volumeUnitDescription;
			this.volumeSubstanceSymbol = volumeSubstance;
			this.membraneSubstanceSymbol = membraneSubstance;
			this.lumpedReactionSubstanceSymbol = lumpedReactionSubstance;
			this.volumeSymbol = volume;
			this.areaSymbol = area;
			this.lengthSymbol = length;
		}
	};

	public enum ConcUnitSystem {
		ConcUnitSymbol_M("M (molar)","M.um3","molecules","molecules","um3","um2","um"),
		ConcUnitSymbol_uM("uM (micromolar)","uM.um3","molecules","molecules","um3","um2","um"),
		ConcUnitSymbol_nM("nM (nanomolar)","nM.um3","molecules","molecules","um3","um2","um");

		public final String description;
		public final String volumeSubstanceSymbol;
		public final String membraneSubstanceSymbol;
		public final String lumpedReactionSubstanceSymbol;
		public final String volumeSymbol;
		public final String areaSymbol;
		public final String lengthSymbol;

		private ConcUnitSystem(String concUnitDescription, String volumeSubstance, String membraneSubstance, String lumpedReactionSubstance, String volume, String area, String length){
			this.description = concUnitDescription;
			this.volumeSubstanceSymbol = volumeSubstance;
			this.membraneSubstanceSymbol = membraneSubstance;
			this.lumpedReactionSubstanceSymbol = lumpedReactionSubstance;
			this.volumeSymbol = volume;
			this.areaSymbol = area;
			this.lengthSymbol = length;
		}
	};

	public enum BngUnitOrigin { DEFAULT, PARSER, USER };
	private final BngUnitSystem.BngUnitOrigin o;

	private final boolean isConcentration;
	private final Double volume;
	private final BngUnitSystem.VolumeUnitSystem volumeUnit;
	private final BngUnitSystem.ConcUnitSystem concUnit;
	private final BngUnitSystem.TimeUnitSystem timeUnit;

	public BngUnitSystem(BngUnitSystem.BngUnitOrigin bngUnitOrigin) {
		this.o = bngUnitOrigin;
		this.volume = 10.0;		// in the dialog it'll show as 10 cubic micrometers, which is a good initial value
		this.isConcentration = true;
		this.volumeUnit = null;
		this.concUnit = ConcUnitSystem.ConcUnitSymbol_uM;
		this.timeUnit = TimeUnitSystem.TimeUnitSymbol_s;
	}
	public BngUnitSystem(BngUnitSystem us) {
		this.o = us.o;
		this.volume = us.volume;
		this.isConcentration = us.isConcentration;
		this.volumeUnit = us.volumeUnit;
		this.concUnit = us.concUnit;
		this.timeUnit = us.timeUnit;
	}
	private BngUnitSystem(BngUnitSystem.BngUnitOrigin o, boolean isConcentration, Double volume, BngUnitSystem.VolumeUnitSystem volumeUnitSymbol, BngUnitSystem.ConcUnitSystem concUnitSymbol, BngUnitSystem.TimeUnitSystem timeUnitSymbol){
		this.o = o;
		this.isConcentration = isConcentration;
		this.volume = volume;
		this.volumeUnit = volumeUnitSymbol;
		this.concUnit = concUnitSymbol;
		this.timeUnit = timeUnitSymbol;
	}

	public static BngUnitSystem createAsConcentration(BngUnitSystem.BngUnitOrigin o, double volume, BngUnitSystem.ConcUnitSystem concUnitSymbol, BngUnitSystem.TimeUnitSystem timeUnitSymbol){
		return new BngUnitSystem(o, true, volume, null, concUnitSymbol, timeUnitSymbol);
	}

	public static BngUnitSystem createAsMolecules(BngUnitSystem.BngUnitOrigin o, double volume, BngUnitSystem.VolumeUnitSystem volumeUnitSymbol, BngUnitSystem.TimeUnitSystem timeUnitSymbol){
		return new BngUnitSystem(o, false, volume, volumeUnitSymbol, null, timeUnitSymbol);
	}

	public BngUnitSystem.BngUnitOrigin getOrigin(){
		return this.o;
	}
	public Double getVolume(){
		return this.volume;
	}
	public boolean isConcentration(){
		return this.isConcentration;
	}
	public ModelUnitSystem createModelUnitSystem(){
		if (isConcentration){
			return ModelUnitSystem.createVCModelUnitSystem(
					concUnit.volumeSubstanceSymbol,
					concUnit.membraneSubstanceSymbol,
					concUnit.lumpedReactionSubstanceSymbol,
					concUnit.volumeSymbol,
					concUnit.areaSymbol,
					concUnit.lengthSymbol,
					timeUnit.timeSymbol);
		}else{
			return ModelUnitSystem.createVCModelUnitSystem(
					volumeUnit.volumeSubstanceSymbol,
					volumeUnit.membraneSubstanceSymbol,
					volumeUnit.lumpedReactionSubstanceSymbol,
					volumeUnit.volumeSymbol,
					volumeUnit.areaSymbol,
					volumeUnit.lengthSymbol,
					timeUnit.timeSymbol);
		}
	}
}