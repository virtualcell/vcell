CREATE TABLE stochtest(
id integer PRIMARY KEY,
simcontextref integer NOT NULL REFERENCES vc_simcontext(id) ON DELETE CASCADE,
biomodelref integer NOT NULL REFERENCES vc_biomodel(id) ON DELETE CASCADE,
mathref integer NOT NULL REFERENCES vc_math(id) ON DELETE CASCADE,
dimension integer NOT NULL,numcompartments integer ,
mathtype varchar2(64) );


insert into stochtest (id, simcontextref, biomodelref, mathref, dimension) 
select NewSeq.NEXTVAL, vc_simcontext.id, vc_biomodelsimcontext.biomodelref, vc_simcontext.mathref, vc_geometry.dimension
from vc_simcontext, vc_biomodelsimcontext, vc_geometry 
where vc_simcontext.geometryref = vc_geometry.id
and vc_biomodelsimcontext.SIMCONTEXTREF = vc_simcontext.id;

commit;

merge into stochtest a
using (select vc_simcontext.id scid, count(vc_modelstruct.id) numcomp from vc_modelstruct, vc_simcontext where vc_simcontext.modelref = vc_modelstruct.modelref group by vc_simcontext.id) S
on (a.simcontextref = S.scid)
when matched then 
update set a.numcompartments = S.numcomp;

commit;

merge into stochtest a
using (select id, language from vc_math m) S
on (a.mathref = S.id 
    and dbms_lob.instr(S.language,'StochasticVolumeVariable')>0)
when matched then 
update set a.mathtype = 'nonspatial-stochastic';

merge into stochtest a
using (select id, language from vc_math m) S
on (a.mathref = S.id 
    and a.numcompartments = 1 
    and a.dimension = 0;
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


select count(id), mathtype, dimension from stochtest group by mathtype, dimension order by count(id) desc;

select stochtest.*, dbms_lob.substr(vc_math.language,4000,1) vcml from stochtest, vc_math where stochtest.mathref = vc_math.id and stochtest.MATHTYPE is null and stochtest.DIMENSION>0;


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

alter table stochtestrun drop column runstatus;

alter table stochtestrun add status varchar2(32) default 'none' not null;

alter table stochtestrun add errmsg varchar2(4000);

rename column stochtestrun.mathgenstatus to status;

select * from stochtestrun where status = 'none' and parentmathtype <> mathtype and rownum < 100;

update stochtestrun set status = 'waiting' where status = 'none' and parentmathtype = 'nonspatial-stochastic' and mathtype = 'rules';

update stochtestrun set status = 'waiting' where parentmathtype = 'nonspatial-stochastic' and mathtype = 'rules';

update stochtestrun set status = 'waiting', errmsg = NULL where status = 'none' and parentmathtype = 'nonspatial-stochastic' and mathtype = 'nonspatial-stochastic';

update stochtestrun set status = 'none';

update stochtestrun set errmsg = NULL;

rollback;



commit;

select count(stochtestrun.id) from stochtestrun where stochtestrun.stochtestref in (select stochtest.id from stochtest where stochtest.numcompartments <> 1);

delete from stochtestrun where stochtestrun.stochtestref in (select stochtest.id from stochtest where stochtest.NUMCOMPARTMENTS <> 1);

select count(id), PARENTMATHTYPE, MATHTYPE, status, errmsg from stochtestrun group by status, PARENTMATHTYPE, MATHTYPE, errmsg order by parentmathtype, mathtype, status, errmsg;

select count(id), PARENTMATHTYPE, MATHTYPE, status from stochtestrun group by status, PARENTMATHTYPE, MATHTYPE order by parentmathtype, mathtype, status;

select count(*) from stochtestrun where status <> 'waiting';

SELECT 
stochtest.id id_1,
stochtest.simcontextref simcontextref_1,
stochtest.biomodelref biomodelref_1,
stochtest.mathref mathref_1,
stochtest.dimension dimension_1,
stochtest.numcompartments numcompartments_1,
stochtest.mathtype mathtype_1 ,
stochtestrun.id id_2,
stochtestrun.stochtestref stochtestref_2,
stochtestrun.parentmathtype parentmathtype_2,
stochtestrun.mathtype mathtype_2,
stochtestrun.status status_2 
FROM stochtestrun, stochtest 
WHERE stochtest.id = stochtestrun.stochtestref 
AND stochtestrun.status = 'waiting' 
AND ROWNUM = 1 
FOR UPDATE  
ORDER BY stochtestrun.id;



CREATE TABLE stochtestcompare(
id integer PRIMARY KEY,
stochtestrunref1 integer NOT NULL REFERENCES stochtestrun(id) ON DELETE CASCADE,
stochtestrunref2 integer NOT NULL REFERENCES stochtestrun(id) ON DELETE CASCADE,
results varchar2(4000) ,
status varchar2(32) )

select count(id), PARENTMATHTYPE, MATHTYPE from STOCHTESTRUN group by PARENTMATHTYPE, Mathtype order by parentMathType;

select unique mathtype from stochtest;
