-----------------------------------------------------------------
-- create StochTest table
-----------------------------------------------------------------
CREATE TABLE stochtest(
id integer PRIMARY KEY,
simcontextref integer NOT NULL REFERENCES vc_simcontext(id) ON DELETE CASCADE,
biomodelref integer NOT NULL REFERENCES vc_biomodel(id) ON DELETE CASCADE,
mathref integer NOT NULL REFERENCES vc_math(id) ON DELETE CASCADE,
dimension integer NOT NULL,
numcompartments integer,
mathtype varchar2(64) );
commit;

------------------------------------------------------------------
-- first-time populate StochTest table (setting "dimension" column, but not setting numcompartments or mathtype)
------------------------------------------------------------------
insert into stochtest (id, simcontextref, biomodelref, mathref, dimension) 
select NewSeq.NEXTVAL, vc_simcontext.id, vc_biomodelsimcontext.biomodelref, vc_simcontext.mathref, vc_geometry.dimension
from vc_simcontext, vc_biomodelsimcontext, vc_geometry 
where vc_simcontext.geometryref = vc_geometry.id
and vc_biomodelsimcontext.SIMCONTEXTREF = vc_simcontext.id;
commit;

------------------------------------------------------------------
-- update StochTest table with new records (setting "dimension" column, but not setting numcompartments or mathtype)
------------------------------------------------------------------
merge into stochtest a
using (select vc_simcontext.id as scid, vc_biomodelsimcontext.biomodelref as bmid, vc_simcontext.mathref as mid, vc_geometry.dimension as dim
	  from vc_simcontext, vc_biomodelsimcontext, vc_geometry 
	  where vc_simcontext.geometryref = vc_geometry.id
	  and vc_biomodelsimcontext.SIMCONTEXTREF = vc_simcontext.id) b
on (a.simcontextref = b.scid
    and a.biomodelref = b.bmid)
when not matched then
   insert (id, simcontextref, biomodelref, mathref, dimension)
   values (NewSeq.NEXTVAL, b.scid, b.bmid, b.mid, b.dim);
commit;

-----------------------------------------
-- update numcompartments column
-----------------------------------------
drop table stochtesttemp;
create table stochtesttemp as (select unique mathref as tempmathid from stochtest where numcompartments is null);
merge into stochtest a
using (select vc_simcontext.id scid, count(vc_modelstruct.id) numcomp 
       from vc_modelstruct, vc_simcontext, stochtesttemp 
       where vc_simcontext.modelref = vc_modelstruct.modelref and stochtesttemp.tempmathid = mathref
       group by vc_simcontext.id) S
on (a.simcontextref = S.scid)
when matched then 
update set a.numcompartments = S.numcomp;
commit;


------------------------------------------
-- update mathtype for 'nonspatial-stochastic'
------------------------------------------
drop table stochtesttemp;
create table stochtesttemp as (select unique mathref as tempmathid from stochtest where mathtype is null);
merge into stochtest a
using (select id, language from vc_math m, stochtesttemp where stochtesttemp.tempmathid = m.id) S
on (a.mathref = S.id 
    and dbms_lob.instr(S.language,'StochasticVolumeVariable')>0)
when matched then 
update set a.mathtype = 'nonspatial-stochastic';
commit;

------------------------------------------
-- update mathtype for 'rules'
------------------------------------------
drop table stochtesttemp;
create table stochtesttemp as (select unique mathref as tempmathid from stochtest where DIMENSION = 0 and mathtype is null);
merge into stochtest a
using (select id, language from vc_math m, stochtesttemp where stochtesttemp.tempmathid = m.id) S
on (a.mathref = S.id 
    and a.dimension = 0 
    and dbms_lob.instr(S.language,'ParticleMolecularType')>0)
when matched then 
update set a.mathtype = 'rules';
commit;

------------------------------------------
-- update mathtype for 'ode'
------------------------------------------
drop table stochtesttemp;
create table stochtesttemp as (select unique mathref as tempmathid from stochtest where DIMENSION = 0 and mathtype is null);
merge into stochtest a
using (select id, language from vc_math m, stochtesttemp where stochtesttemp.tempmathid = m.id) S
on (a.mathref = S.id 
    and a.dimension = 0 
    and dbms_lob.instr(S.language,'OdeEquation')>0)
when matched then 
update set a.mathtype = 'ode';
commit;

------------------------------------------
-- update mathtype for 'spatial-stochastic' (hybrid or not)
------------------------------------------
drop table stochtesttemp;
create table stochtesttemp as (select unique mathref as tempmathid from stochtest where DIMENSION > 0 and mathtype is null);
merge into stochtest a
using (select id, language from vc_math m, stochtesttemp where stochtesttemp.tempmathid = m.id) S
on (a.mathref = S.id 
    and a.dimension > 0
    and (dbms_lob.instr(S.language,'ParticleJumpProcess')>0
         or dbms_lob.instr(S.language,'ParticleProperties')>0)
    and not dbms_lob.instr(S.language,'ParticleMolecularType')>0)
when matched then 
update set a.mathtype = 'spatial-stochastic';
commit;

------------------------------------------
-- update mathtype for 'pde'
------------------------------------------
drop table stochtesttemp;

create table stochtesttemp as (select unique mathref as tempmathid from stochtest where DIMENSION > 0 and mathtype is null);

merge into stochtest a
using (select id, language from vc_math m, stochtesttemp where stochtesttemp.tempmathid = m.id) S
on (a.mathref = S.id 
	and a.dimension > 0
    and ( dbms_lob.instr(S.language,'PdeEquation')>0
         or dbms_lob.instr(S.language,'OdeEquation')>0
         or dbms_lob.instr(S.language,'VolumeRegionEquation')>0
         or dbms_lob.instr(S.language,'MembraneRegionEquation')>0
         )
    and not dbms_lob.instr(S.language,'ParticleJumpProcess')>0)
when matched then 
update set a.mathtype = 'pde';

commit;
--------------------------------------------------------------
-- Report: summary report on number of BioModel applications by mathtype
--------------------------------------------------------------
select count(id), mathtype, dimension, numcompartments 
from stochtest 
group by mathtype, dimension, numcompartments 
order by mathtype, count(id) desc;

---------------------------------------------------------------------------------------
-- Report: look for uncategorized (unknown mathtype) entries in stochtest, and print the Math Language for easy browsing
----------------------------------------------------------------------------------------------------------------
select stochtest.*, dbms_lob.substr(vc_math.language,4000,1) vcml 
from stochtest, vc_math 
where stochtest.mathref = vc_math.id 
and stochtest.MATHTYPE is null 
and stochtest.DIMENSION>0;

