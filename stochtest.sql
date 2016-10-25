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

------------------------------------------------------------------
-- first-time populate StochTest table (setting "dimension" column, but not setting numcompartments or mathtype)
------------------------------------------------------------------
insert into stochtest (id, simcontextref, biomodelref, mathref, dimension) 
select NewSeq.NEXTVAL, vc_simcontext.id, vc_biomodelsimcontext.biomodelref, vc_simcontext.mathref, vc_geometry.dimension
from vc_simcontext, vc_biomodelsimcontext, vc_geometry 
where vc_simcontext.geometryref = vc_geometry.id
and vc_biomodelsimcontext.SIMCONTEXTREF = vc_simcontext.id;

commit;

-----------------------------------------
-- update numcompartments column
-----------------------------------------
merge into stochtest a
using (select vc_simcontext.id scid, count(vc_modelstruct.id) numcomp from vc_modelstruct, vc_simcontext where vc_simcontext.modelref = vc_modelstruct.modelref group by vc_simcontext.id) S
on (a.simcontextref = S.scid)
when matched then 
update set a.numcompartments = S.numcomp;

commit;


------------------------------------------
-- update mathtype for 'nonspatial-stochastic'
------------------------------------------
merge into stochtest a
using (select id, language from vc_math m) S
on (a.mathref = S.id 
    and dbms_lob.instr(S.language,'StochasticVolumeVariable')>0)
when matched then 
update set a.mathtype = 'nonspatial-stochastic';

------------------------------------------
-- update mathtype for 'rules'
------------------------------------------
merge into stochtest a
using (select id, language from vc_math m) S
on (a.mathref = S.id 
    and a.numcompartments = 1 
    and a.dimension = 0;
    and dbms_lob.instr(S.language,'ParticleMolecularType')>0)
when matched then 
update set a.mathtype = 'rules';

------------------------------------------
-- update mathtype for 'ode'
------------------------------------------
merge into stochtest a
using (select id, language from vc_math m) S
on (a.mathref = S.id 
    and a.dimension = 0 
    and dbms_lob.instr(S.language,'OdeEquation')>0)
when matched then 
update set a.mathtype = 'ode';

------------------------------------------
-- update mathtype for 'spatial-stochastic' (hybrid or not)
------------------------------------------
merge into stochtest a
using (select id, language from vc_math m) S
on (a.mathref = S.id 
    and a.dimension > 0
    and dbms_lob.instr(S.language,'ParticleJumpProcess')>0)
when matched then 
update set a.mathtype = 'spatial-stochastic';

------------------------------------------
-- update mathtype for 'pde'
------------------------------------------
merge into stochtest a
using (select id, language from vc_math m) S
on (a.mathref = S.id 
    and a.dimension > 0
    and ( dbms_lob.instr(S.language,'PdeEquation')>0
         or dbms_lob.instr(S.language,'OdeEquation')>0
         )
    and not dbms_lob.instr(S.language,'ParticleJumpProcess')>0)
when matched then 
update set a.mathtype = 'pde';

--------------------------------------------------------------
-- summary report on number of BioModel applications by mathtype
--------------------------------------------------------------
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

alter table stochtestcompare add conclusion varchar2(4000);

alter table stochtestrun add conclusion varchar2(4000);

alter table stochtestrun add exclude varchar2(4000);

update stochtestcompare set conclusion = 'zero-order mass action reverse' where id = 96750698;

rename column stochtestrun.mathgenstatus to status;

select * from stochtestrun where status = 'none' and parentmathtype <> mathtype and rownum < 100;

update stochtestrun set status = 'waiting' where status = 'none' and parentmathtype = 'nonspatial-stochastic' and mathtype = 'rules';

update stochtestrun set status = 'waiting' where parentmathtype = 'nonspatial-stochastic' and mathtype = 'rules';

update stochtestrun set status = 'waiting', errmsg = NULL where status = 'none' and parentmathtype = 'nonspatial-stochastic' and mathtype = 'nonspatial-stochastic';

update stochtestrun set status = 'waiting', errmsg = NULL where status = 'accepted' and parentmathtype = 'rules' and mathtype = 'nonspatial-stochastic' and rownum < 600;

update stochtestrun set errmsg = NULL;

update stochtestcompare set status = 'waiting', errmsg = NULL, results = NULL where status = 'none';

select count(id), status, results from stochtestcompare group by status, results order by status, results;

select * from stochtestcompare where status = 'verydifferent' ;

select count(id), status from stochtestcompare  group by status order by status;

select * from stochtestcompare where smallest_pvalue is not NULL;



select vc_userinfo.userid, vc_biomodel.NAME as biomodel_name, vc_biomodel.VERSIONDATE as biomodel_date, stochtestcompare.* 
from stochtestcompare, vc_userinfo, stochtestrun, stochtest, vc_biomodel 
where vc_userinfo.id = vc_biomodel.ownerref 
and stochtestcompare.STOCHTESTRUNREF1 = stochtestrun.id
and stochtestrun.stochtestref = stochtest.id 
and stochtest.biomodelref = vc_biomodel.id 
and (stochtestcompare.status = 'verydifferent' or stochtestcompare.status = 'failed')
and stochtest.NUMCOMPARTMENTS = 1
order by stochtestcompare.SMALLEST_PVALUE, stochtestcompare.id;

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

select c.*, r1.mathtype as mt1, r2.mathtype as mt2, r1.status status1, r2.status status2 
from stochtestcompare c, stochtestrun r1, stochtestrun r2
where r1.id = c.stochtestrunref1
and r2.id = c.stochtestrunref2;

select count(c.id), r1.mathtype as mt1, r1.status as status1, r2.mathtype as mt2, r2.status as status2 
from stochtestcompare c, stochtestrun r1, stochtestrun r2
where r1.id = c.stochtestrunref1
and r2.id = c.stochtestrunref2
group by r1.mathtype, r1.status, r2.mathtype, r2.status
order by r1.mathtype, r1.status, r2.mathtype, r2.status;

select * from stochtestcompare where status <> 'waiting';

commit;

update stochtestrun set status = 'waiting', errmsg = NULL where PARENTMATHTYPE = 'nonspatial-stochastic' and MATHTYPE = 'nonspatial-stochastic';


select stochtestrun.id from stochtest, stochtestrun, vc_speciescontextspec where stochtest.id = stochtestrun.stochtestref and stochtest.simcontextref = vc_speciescontextspec.simcontextref and vc_speciescontextspec.bforceconst = 1;

create table stochtestrun_old as select * from stochtestrun;

delete from stochtestrun;

select count(stochtestrun.id) from stochtestrun where stochtestrun.stochtestref in (select stochtest.id from stochtest where stochtest.numcompartments <> 1);

delete from stochtestrun where stochtestrun.stochtestref in (select stochtest.id from stochtest where stochtest.NUMCOMPARTMENTS <> 1);

-------------------------------------------------------------
-- rerun stochtest run records
-------------------------------------------------------------
update stochtestrun set status = 'waiting', errmsg = NULL, conclusion = NULL, exclude = NULL
where status = 'accepted'
-- and errmsg like '%cygwin%'
-- and PARENTMATHTYPE = 'rules'
-- and PARENTMATHTYPE = 'nonspatial-stochastic'
-- and MATHTYPE = 'nonspatial-stochastic'
-- and MATHTYPE = 'rules'
-- and conclusion = 'bng-null-pointer-exception'
-- and rownum < 1;
;

select count(id) from stochtestrun where errmsg like '%cygwin%';

-------------------------------------------------------------
-- retun stochest compare
-------------------------------------------------------------
update stochtestcompare 
set status = 'waiting', 
errmsg = NULL, 
results = NULL, 
smallest_pvalue=NULL, 
numexperiments=NULL, 
numfail_95=NULL, 
numfail_99=NULL, 
numfail_999=NULL,
CONCLUSION=NULL
where status = 'failed'
;



---------------------------------------------------------
-- stochtest compare - detailed query with biomodel info
---------------------------------------------------------
select vc_userinfo.userid as owner, 
vc_biomodel.VERSIONDATE as bmdate, 
vc_biomodel.name as bmname, 
stochtestcompare.id as compare_id,
stochtestcompare.results as compare_results,
stochtestcompare.status as compare_status,
stochtestcompare.errmsg as compare_errmsg, 
stochtestcompare.SMALLEST_PVALUE,
stochtestcompare.NUMEXPERIMENTS,
stochtestcompare.NUMFAIL_95,
stochtestcompare.NUMFAIL_99,
stochtestcompare.NUMFAIL_999,
r1.status as r1stat, 
r2.status as r2stat, 
stochtest.dimension, stochtest.numcompartments
from stochtestcompare, vc_userinfo, stochtestrun r1, stochtestrun r2, stochtest, vc_biomodel 
where stochtestcompare.STOCHTESTRUNREF1 = r1.id 
and stochtestcompare.STOCHTESTRUNREF2 = r2.id 
and vc_userinfo.id = vc_biomodel.ownerref 
and r1.stochtestref = stochtest.id 
and stochtest.biomodelref = vc_biomodel.id 
-- and (stochtestrun.status = 'failed' or stochtestcompare.status = 'verydifferent')
-- and stochtestcompare.status = 'not_verydifferent'
-- and vc_userinfo.userid = 'mblinov'
and r1.status = 'complete' and r2.status = 'complete'
order by stochtestcompare.smallest_pvalue;

---------------------------------------------------------
-- stochtest compare - summary
---------------------------------------------------------
select 
count(stochtestcompare.id) as id,
stochtestcompare.status as compare_status
from stochtestcompare, stochtestrun r1, stochtestrun r2, stochtest 
where stochtestcompare.STOCHTESTRUNREF1 = r1.id 
and stochtestcompare.STOCHTESTRUNREF2 = r2.id 
and r1.stochtestref = stochtest.id 
-- and (stochtestrun.status = 'failed' or stochtestcompare.status = 'verydifferent')
-- and stochtestcompare.status = 'failed'
and r1.status = 'complete' and r2.status = 'complete' and stochtest.numcompartments = 1
group by stochtestcompare.status;

select count(id) from stochtestcompare;

---------------------------------------------------------------------------
-- stochtest run - terse summary - status only (no conclusions, no errmsg)
---------------------------------------------------------------------------
select count(id), PARENTMATHTYPE, MATHTYPE, status 
from stochtestrun 
where exclude is NULL
group by status, PARENTMATHTYPE, MATHTYPE 
order by parentmathtype, mathtype, status;

-------------------------------------------------------------------
-- stochtest run - short summary - with conclusions (no errmsg)
-------------------------------------------------------------------
select count(id), PARENTMATHTYPE, MATHTYPE, status, conclusion
-- , max(errmsg)
from stochtestrun 
-- where exclude is NULL
group by status, conclusion, PARENTMATHTYPE, MATHTYPE 
order by parentmathtype, mathtype, status, count(id) desc;

-------------------------------------------------------------------
-- stochtest run - short summary - with conclusions (no errmsg) ... with biomodel and user
-------------------------------------------------------------------
select count(stochtestrun.id), 
stochtestrun.PARENTMATHTYPE, 
stochtestrun.MATHTYPE, 
stochtestrun.status, 
stochtestrun.conclusion, 
max('user('||vc_userinfo.userid||'),     biomodel('||vc_biomodel.name||'),     date('||TO_CHAR(vc_biomodel.versiondate, 'dd/mon/yyyy  hh24:mi:ss')||')') as max_model
-- , max(errmsg)
from stochtestrun, stochtest, vc_biomodel, vc_userinfo 
where stochtestrun.STOCHTESTREF = stochtest.id
and stochtest.biomodelref = vc_biomodel.id
and vc_userinfo.id = vc_biomodel.ownerref
-- where exclude is NULL
and stochtest.NUMCOMPARTMENTS = 1
group by stochtestrun.status, stochtestrun.conclusion, stochtestrun.PARENTMATHTYPE, stochtestrun.MATHTYPE 
order by stochtestrun.parentmathtype, stochtestrun.mathtype, stochtestrun.status, count(stochtestrun.id) desc;

-------------------------------------------------------------------
-- stochtest run - detailed summary - with conclusions and errmsg
-------------------------------------------------------------------
select count(id), PARENTMATHTYPE, MATHTYPE, status, conclusion, errmsg 
from stochtestrun 
-- where conclusion = 'bng-null-pointer-exception'
where exclude is NULL
group by status, conclusion, errmsg, PARENTMATHTYPE, MATHTYPE 
order by parentmathtype, mathtype, status, conclusion, errmsg;

-------------------------------------------------------------------------
-- stochtest run - full query with conclusions, errmsg and biomodel info
-------------------------------------------------------------------------
select vc_userinfo.userid as owner, 
vc_biomodel.VERSIONDATE as bmdate, 
vc_biomodel.name as bmname, 
stochtestrun.id as runid, 
stochtestrun.parentmathtype, 
stochtestrun.mathtype,
stochtestrun.status, 
stochtestrun.conclusion,
stochtestrun.errmsg
-- stochtest.*
from vc_userinfo, stochtestrun, stochtest, vc_biomodel 
where vc_userinfo.id = vc_biomodel.ownerref 
and stochtestrun.stochtestref = stochtest.id 
and stochtest.biomodelref = vc_biomodel.id 
and stochtestrun.status <> 'none'
and stochtestrun.status <> 'waiting' 
and stochtestrun.status <> 'complete' 
and stochtestrun.PARENTMATHTYPE = 'rules' and stochtestrun.mathtype = 'nonspatial-stochastic'
--and conclusion is NULL
--and exclude is NULL
-- and errmsg like '%more than 2%'
order by status, errmsg
;

------------------------------------------------------
-- stochtest run - update stochtestrun.conclusion
------------------------------------------------------
update stochtestrun set exclude = 'structure-size-not-set' where conclusion = 'nfsim-structure-size-not-set';

update stochtestrun set status = 'waiting', errmsg = NULL, conclusion = NULL where status = 'failed' and conclusion is NULL and errmsg like '%cygwin%';

update stochtestrun set conclusion = 'nfsim-exe-failed' where status = 'failed' and conclusion is NULL and errmsg like '%solver failed : Could not execute code:%NFsim_x64%';

update stochtestrun set conclusion = NULL where conclusion = 'nfsim-exe-failed';

update stochtestrun set conclusion = 'nfsim-solver-exception-couldnt-find-binding-sites' where errmsg like '%solver failed : Could not execute code: It seems that I couldn&apos;t find the binding sites or states you are refering to%' and status = 'failed' and parentmathtype = 'rules' and mathtype = 'rules';
update stochtestrun set conclusion = NULL where status = 'complete';



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
