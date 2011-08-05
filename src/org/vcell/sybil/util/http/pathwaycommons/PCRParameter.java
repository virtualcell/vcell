/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.http.pathwaycommons;

/*   PCRPara  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Parameters to request data from Pathway Commons
 */

import org.vcell.sybil.util.keys.KeyOfTwo;

public class PCRParameter {
	protected String key, value;
	public PCRParameter(String keyNew, String valueNew) { key = keyNew; value = valueNew; }
	public String key() { return key; }
	public String value() { return value; }
	@Override
	public String toString() { return key + "=" + value; }

	public static class Command extends PCRParameter { 
		public Command(String cmd) { super("cmd", cmd); } 
	
		public static final Command search = new Command("search");
		public static final Command getPathways = new Command("get_pathways");
		public static final Command getNeighbours = new Command("get_neighbors");
		public static final Command getParents = new Command("get_parents");
		public static final Command getRecordByCPID = new Command("get_record_by_cpath_id");		
	}

	public static class Version extends PCRParameter { 
		public Version(String version) { super("version", version); } 
		
		public static final Version v2 = new Version("2.0");
		public static final Version v3 = new Version("3.0");
	}

	public static class CmdVersion extends KeyOfTwo<Command, Version> {
		public CmdVersion(Command cmd, Version version) { super(cmd, version); }
		public Command cmd() { return a(); }
		public Version version() { return b(); }
		
		public static final CmdVersion search = new CmdVersion(Command.search, Version.v2);
		public static final CmdVersion getPathways = new CmdVersion(Command.getPathways, Version.v2);
		public static final CmdVersion getNeighbours = new CmdVersion(Command.getNeighbours, Version.v3);
		public static final CmdVersion getParents = new CmdVersion(Command.getParents, Version.v2);
		public static final CmdVersion getRecordByCPID = new CmdVersion(Command.getRecordByCPID, Version.v2);
	}

	public static class Q extends PCRParameter { 
		public Q(String q) { super("q", q); }		
	}

	public static class MaxHits extends PCRParameter { 
		public MaxHits(int maxHits) { super("maxHits", ""+maxHits); }		
	}

	public static class Output extends PCRParameter { 
		public Output(String output) { super("output", output); } 
		
		public static final Output xml = new Output("xml");
		public static final Output biopax = new Output("biopax");
		public static final Output idList = new Output("id_list");
		public static final Output binarySif = new Output("binary_sif");
		public static final Output imageMap = new Output("image_map");
		public static final Output imageMapThumbnail = new Output("image_map_thumbnail");
		public static final Output imageMapFrameset = new Output("image_map_frameset");
	}

	public static class Organism extends PCRParameter { 
		public Organism(String organism) { super("organism", organism); } 
		
		public static final Organism human = new Organism("9606");
	}

	public static class InputIDType extends PCRParameter { 
		public InputIDType(String inputIDType) { super("input_id_type", inputIDType); } 
		
		public static final InputIDType uniprot = new InputIDType("UNIPROT");
		public static final InputIDType cpathID = new InputIDType("CPATH_ID");
		public static final InputIDType entrezGene = new InputIDType("ENTREZ_GENE");
	}

	public static class OutputIDType extends PCRParameter { 
		public OutputIDType(String outputIDType) { super("output_id_type", outputIDType); } 
		
		public static final OutputIDType uniprot = new OutputIDType("UNIPROT");
		public static final OutputIDType cpathID = new OutputIDType("CPATH_ID");
		public static final OutputIDType entrezGene = new OutputIDType("ENTREZ_GENE");
	}

	public static class DataSource extends PCRParameter { 
		public DataSource(String dataSource) { super("data_source", dataSource); } 
		
		public static final DataSource biogrid = new DataSource("BIOGRID");
		public static final DataSource cellMap = new DataSource("CELL_MAP");
		public static final DataSource hprd = new DataSource("HPRD");
		public static final DataSource humancyc = new DataSource("HUMANCYC");
		public static final DataSource intact = new DataSource("INTACT");
		public static final DataSource mint = new DataSource("MINT");
		public static final DataSource nciNature = new DataSource("NCI_NATURE");
		public static final DataSource reactome = new DataSource("REACTOME");
	}
	
}
