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

select count(id) from stochtestrun;

update stochtestrun set status = 'waiting';

insert into stochtestrun 
(id,stochtestref,parentmathtype,mathtype)
select NewSeq.NEXTVAL, stochtest.id, stochtest.MATHTYPE, 'rules'
from stochtest
where stochtest.mathtype = 'nonspatial-stochastic';

select * from stochtestrun_old where errmsg like '%cygwin%';

alter table stochtestrun drop column runstatus;

alter table stochtestrun add status varchar2(32) default 'none' not null;

alter table stochtestrun add errmsg varchar2(4000);

alter table stochtestcompare add smallest_pvalue NUMBER;

alter table stochtestcompare add numExperiments NUMBER;

alter table stochtestcompare add numFail_95 NUMBER;

alter table stochtestcompare add numFail_999 NUMBER;

alter table stochtestcompare add errmsg varchar2(4000);

rename column stochtestrun.mathgenstatus to status;

select * from stochtestrun where status = 'none' and parentmathtype <> mathtype and rownum < 100;

update stochtestrun set status = 'waiting' where status = 'none' and parentmathtype = 'nonspatial-stochastic' and mathtype = 'rules';

update stochtestrun set status = 'waiting' where parentmathtype = 'nonspatial-stochastic' and mathtype = 'rules';

update stochtestrun set status = 'waiting', errmsg = NULL where status = 'none' and parentmathtype = 'nonspatial-stochastic' and mathtype = 'nonspatial-stochastic';

update stochtestrun set status = 'waiting', errmsg = NULL where status = 'accepted' and parentmathtype = 'rules' and mathtype = 'nonspatial-stochastic' and rownum < 600;

update stochtestrun set errmsg = NULL;

update stochtestcompare set status = 'waiting', errmsg = NULL, results = NULL where status = 'none';

update stochtestcompare set status = 'waiting', errmsg = NULL, results = NULL, smallest_pvalue=NULL, numexperiments=NULL, numfail_95=NULL, numfail_99=NULL, numfail_999=NULL;

select count(id), status, results from stochtestcompare group by status, results order by status, results;

select * from stochtestcompare where status = 'verydifferent' ;

select count(id), status from stochtestcompare  group by status order by status;

select * from stochtestcompare where smallest_pvalue is not NULL;

commit;

rollback;

insert into stochtestcompare
(id, stochtestrunref1, stochtestrunref2, results, status)
select 
NewSeq.NEXTVAL as id, 
r1.id as ref1, r2.id as ref2,
NULL, 'none'
from stochtestrun r1, stochtestrun r2  
where r1.stochtestref = r2.stochtestref
and r1.id < r2.id;

select c.*, r1.mathtype as mt1, r2.mathtype as mt2, r1.status status1, r2.status status2 from stochtestcompare c, stochtestrun r1, stochtestrun r2
where r1.id = c.stochtestrunref1
and r2.id = c.stochtestrunref2;

select count(c.id), r1.mathtype as mt1, r1.status as status1, r2.mathtype as mt2, r2.status as status2 from stochtestcompare c, stochtestrun r1, stochtestrun r2
where r1.id = c.stochtestrunref1
and r2.id = c.stochtestrunref2
group by r1.mathtype, r1.status, r2.mathtype, r2.status
order by r1.mathtype, r1.status, r2.mathtype, r2.status;

select * from stochtestcompare where status <> 'waiting';

commit;

create table stochtestrun_old as select * from stochtestrun;

delete from stochtestrun;

select count(stochtestrun.id) from stochtestrun where stochtestrun.stochtestref in (select stochtest.id from stochtest where stochtest.numcompartments <> 1);

delete from stochtestrun where stochtestrun.stochtestref in (select stochtest.id from stochtest where stochtest.NUMCOMPARTMENTS <> 1);

select count(id), PARENTMATHTYPE, MATHTYPE, status, errmsg from stochtestrun group by status, PARENTMATHTYPE, MATHTYPE, errmsg order by parentmathtype, mathtype, status, errmsg;

select * from stochtestrun where errmsg like '%speciesPattern22';

select stochtestcompare.*, stochtestrun.*, stochtest.*, vc_userinfo.userid, vc_biomodel.* 
from stochtestcompare, vc_userinfo, stochtestrun, stochtest, vc_biomodel 
where stochtestcompare.STOCHTESTRUNREF1 = stochtestrun.id 
and vc_userinfo.id = vc_biomodel.ownerref 
and stochtestrun.stochtestref = stochtest.id 
and stochtest.biomodelref = vc_biomodel.id 
and (stochtestrun.status = 'failed' or stochtestcompare.status = 'verydifferent')
order by stochtestcompare.smallest_pvalue;

select count(id), PARENTMATHTYPE, MATHTYPE, status from stochtestrun group by status, PARENTMATHTYPE, MATHTYPE order by parentmathtype, mathtype, status;

select count(*) from stochtestcompare;

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
