/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modeldb;
import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
/**
 * This type was created in VisualAge.
 */
public class StochtestTable extends Table {
	private static final String TABLE_NAME = "stochtest";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field simcontextref		= new Field("simcontextref",	SQLDataType.integer,		"NOT NULL " + SimContextTable.REF_TYPE + " ON DELETE CASCADE");
	public final Field biomodelref			= new Field("biomodelref",		SQLDataType.integer,		"NOT NULL " + BioModelTable.REF_TYPE + " ON DELETE CASCADE");
	public final Field mathref				= new Field("mathref",			SQLDataType.integer,		"NOT NULL " + MathDescTable.REF_TYPE + " ON DELETE CASCADE");
	public final Field dimension			= new Field("dimension",		SQLDataType.integer,		"NOT NULL");
	public final Field numcompartments		= new Field("numcompartments",	SQLDataType.integer,		"");
	public final Field mathtype				= new Field("mathtype",			SQLDataType.varchar2_64,	"");

	private final Field fields[] = {simcontextref,biomodelref,mathref,dimension,numcompartments,mathtype};
	
	public static final StochtestTable table = new StochtestTable();

/**
 * ModelTable constructor comment.
 */
private StochtestTable() {
	super(TABLE_NAME);
	addFields(fields);
}
}




/**
 * 
 * 
CREATE TABLE stochtest(
id integer PRIMARY KEY,
simcontextref integer NOT NULL REFERENCES vc_simcontext(id) ON DELETE CASCADE,
biomodelref integer NOT NULL REFERENCES vc_biomodel(id) ON DELETE CASCADE,
mathref integer NOT NULL REFERENCES vc_math(id) ON DELETE CASCADE,
dimension integer NOT NULL,
numcompartments integer ,
mathtype varchar2(64) );

insert into stochtest (id, simcontextref, biomodelref, mathref, dimension) 
select NewSeq.NEXTVAL, vc_simcontext.id, vc_biomodelsimcontext.biomodelref, vc_simcontext.mathref, vc_geometry.dimension
from vc_simcontext, vc_biomodelsimcontext, vc_geometry 
where vc_simcontext.geometryref = vc_geometry.id
and vc_biomodelsimcontext.SIMCONTEXTREF = vc_simcontext.id;

merge into stochtest a
using (select vc_simcontext.id scid, count(vc_modelstruct.id) numcomp from vc_modelstruct, vc_simcontext where vc_simcontext.modelref = vc_modelstruct.modelref group by vc_simcontext.id) S
on (a.simcontextref = S.scid)
when matched then 
update set a.numcompartments = S.numcomp;

merge into stochtest a
using (select id, language from vc_math m) S
on (a.mathref = S.id 
and a.numcompartments = 1 
and dbms_lob.instr(S.language,'StochasticVolumeVariable')>0)
when matched then 
update set a.mathtype = 'nonspatial-stochastic';

merge into stochtest a
using (select id, language from vc_math m) S
on (a.mathref = S.id 
and a.numcompartments = 1 
and dbms_lob.instr(S.language,'ParticleMolecularType')>0)
when matched then 
update set a.mathtype = 'rules';

merge into stochtest a
using (select id, language from vc_math m) S
on (a.mathref = S.id 
and a.dimension = 0 
and dbms_lob.instr(S.language,'OdeEquation')>0)
when matched then 
update set a.mathtype = 'ode';

merge into stochtest a
using (select id, language from vc_math m) S
on (a.mathref = S.id 
and a.dimension > 0
and dbms_lob.instr(S.language,'ParticleJumpProcess')>0)
when matched then 
update set a.mathtype = 'spatial-stochastic';

merge into stochtest a
using (select id, language from vc_math m) S
on (a.mathref = S.id 
and a.dimension > 0
and dbms_lob.instr(S.language,'PdeEquation')>0
and not dbms_lob.instr(S.language,'ParticleJumpProcess')>0)
when matched then 
update set a.mathtype = 'pde';

CREATE TABLE stochtestrun(
id integer PRIMARY KEY,
stochtestref integer NOT NULL REFERENCES stochtest(id) ON DELETE CASCADE,
parentmathtype varchar2(64) NOT NULL,
mathtype varchar2(64) NOT NULL,
mathgenstatus varchar2(256) ,
runstatus varchar2(256) );

insert into stochtestrun 
(id,stochtestref,parentmathtype,mathtype)
select NewSeq.NEXTVAL, stochtest.id, stochtest.MATHTYPE, stochtest.MATHTYPE
from stochtest
where stochtest.mathtype = 'rules';

insert into stochtestrun 
(id,stochtestref,parentmathtype,mathtype)
select NewSeq.NEXTVAL, stochtest.id, stochtest.MATHTYPE, stochtest.MATHTYPE
from stochtest
where stochtest.mathtype = 'nonspatial-stochastic';

insert into stochtestrun 
(id,stochtestref,parentmathtype,mathtype)
select NewSeq.NEXTVAL, stochtest.id, stochtest.MATHTYPE, 'nonspatial-stochastic'
from stochtest
where stochtest.mathtype = 'rules';

insert into stochtestrun 
(id,stochtestref,parentmathtype,mathtype)
select NewSeq.NEXTVAL, stochtest.id, stochtest.MATHTYPE, 'rules'
from stochtest
where stochtest.mathtype = 'nonspatial-stochastic';




CREATE TABLE stochtestcompare(
id integer PRIMARY KEY,
stochtestrunref1 integer NOT NULL REFERENCES stochtestrun(id) ON DELETE CASCADE,
stochtestrunref2 integer NOT NULL REFERENCES stochtestrun(id) ON DELETE CASCADE,
results varchar2(4000) ,
status varchar2(32) )

 *
 *
 */

