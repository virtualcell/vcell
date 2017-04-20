-------------------------------------------------------------------------------
-- Create StochTestCompare table
-------------------------------------------------------------------------------
CREATE TABLE stochtestcompare(
id integer PRIMARY KEY,
stochtestrunref1 integer NOT NULL REFERENCES stochtestrun(id) ON DELETE CASCADE,
stochtestrunref2 integer NOT NULL REFERENCES stochtestrun(id) ON DELETE CASCADE,
results varchar2(4000),
status varchar2(32),
errmsg varchar2(4000),
conclusion varchar2(4000),
smallest_pvalue NUMBER,
numexperiments integer,
numfail_95 integer,
numfail_99 integer,
numfail_999 integer);

--------------------------------------------------------------------------------
-- add new records to StochTestCompare
--------------------------------------------------------------------------------
merge into stochtestcompare a
using (select r1.id as ref1, r2.id as ref2
	  from stochtestrun r1, stochtestrun r2 
	  where r1.stochtestref = r2.stochtestref
			and r1.id < r2.id) b
on (a.stochtestrunref1 = b.ref1
    and a.stochtestrunref2 = b.ref2)
when not matched then
   insert (id, stochtestrunref1, stochtestrunref2, results, status)
   values (NewSeq.NEXTVAL, b.ref1, b.ref2, NULL, 'none');
commit;

--------------------------------------------------------------------------------
-- clear all results
--------------------------------------------------------------------------------
update stochtestcompare set
status = 'none',
results = NULL,
errmsg = NULL,
conclusion = NULL,
smallest_pvalue = NULL,
numexperiments = NULL,
numfail_95 = NULL,
numfail_99 = NULL,
numfail_999 = NULL;
commit;

--------------------------------------------------------------------------------
-- request to compare new results (c.status='none') for 'complete' runs
--------------------------------------------------------------------------------
merge into stochtestcompare a
using (select c.id as cid
		from stochtestcompare c, stochtestrun r1, stochtestrun r2
		where c.stochtestrunref1 = r1.id 
			and c.stochtestrunref2 = r2.id 
			and r1.status = 'complete' 
			and r2.status = 'complete'
			and c.status = 'none'
	   ) b
on (a.id = b.cid)
when matched then
update set a.status = 'waiting';
commit;

--------------------------------------------------------------------------------
-- Report: summarize status for all compare records
--------------------------------------------------------------------------------
select count(c.id), r1.mathtype as ref1_mathtype, r2.mathtype as ref2_mathtype, c.status 
from stochtestcompare c, stochtestrun r1, stochtestrun r2
where c.stochtestrunref1 = r1.id and c.stochtestrunref2 = r2.id
group by r1.mathtype, r2.mathtype, c.status
order by r1.mathtype, r2.mathtype, c.status;

--------------------------------------------------------------------------------
-- Report: all compare records with mathTypes and run status
--------------------------------------------------------------------------------
select r1.mathtype as ref1_mathtype, r2.mathtype as ref2_mathtype, c.* 
from stochtestcompare c, stochtestrun r1, stochtestrun r2
where c.stochtestrunref1 = r1.id and c.stochtestrunref2 = r2.id
	and r1.status = 'complete' 
	and r2.status = 'complete';

--------------------------------------------------------------------------------
-- Report: all results for completed datasets which failed the statistical tests
--------------------------------------------------------------------------------
select  u.USERID as userid, '"'||b.name||'",  "'||sc.name||'",  @ '||to_char(b.VERSIONDATE,'DY MON DD  HH24:MI:SS  YYYY') as biomodel, r1.mathtype as ref1_mathtype, r2.mathtype as ref2_mathtype, c.* 
from stochtestcompare c, stochtestrun r1, stochtestrun r2, stochtest t, vc_biomodel b, vc_userinfo u, vc_biomodelsimcontext bmsc, VC_SIMCONTEXT sc
where c.stochtestrunref1 = r1.id
and c.stochtestrunref2 = r2.id 
and bmsc.BIOMODELREF = b.id 
and bmsc.SIMCONTEXTREF = sc.id 
and sc.MATHREF = t.MATHREF 
and t.id = r1.stochtestref 
and b.id = t.biomodelref 
and u.id = b.ownerref
and r1.status = 'complete'  
and r2.status = 'complete'
and (c.status = 'failed' or c.status = 'verydifferent')
order by c.smallest_pvalue;

--------------------------------------------------------------------------------
-- Report: all failed compare results (with BioModel user/name/date)
--------------------------------------------------------------------------------
select vc_userinfo.userid, 
	vc_biomodel.NAME as biomodel_name, 
	vc_biomodel.VERSIONDATE as biomodel_date, 
	stochtestcompare.* 
from stochtestcompare, vc_userinfo, stochtestrun, stochtest, vc_biomodel 
where vc_userinfo.id = vc_biomodel.ownerref 
	and stochtestcompare.STOCHTESTRUNREF1 = stochtestrun.id
	and stochtestrun.stochtestref = stochtest.id 
	and stochtest.biomodelref = vc_biomodel.id 
	and (stochtestcompare.status = 'verydifferent' 
		or stochtestcompare.status = 'failed')
order by stochtestcompare.SMALLEST_PVALUE, stochtestcompare.id;

