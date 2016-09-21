------------------------------------------
-- create table StochTestRun
------------------------------------------
CREATE TABLE stochtestrun(
id integer PRIMARY KEY,
stochtestref integer NOT NULL REFERENCES stochtest(id) ON DELETE CASCADE,
parentmathtype varchar2(64) NOT NULL,
mathtype varchar2(64) NOT NULL,
status varchar2(32) NOT NULL,
errmsg varchar2(4000),
conclusion varchar2(4000),
exclude varchar2(4000),
networkGenProbs varchar2(4000) );
commit;

-----------------------------------------------------------------------------
-- merge new records to run without transformation 
--     ('nonspatial-stochastic' to 'nonspatial-stochastic')
--     ('rules' to 'rules')
-----------------------------------------------------------------------------
merge into stochtestrun a
using (select stochtest.id as stochid, stochtest.mathtype as mathType
	  from stochtest
	  where 
	     (stochtest.mathtype = 'nonspatial-stochastic'
	     or stochtest.mathtype = 'rules')
	  ) b
on (a.stochtestref = b.stochid
    and a.parentmathtype = b.mathtype
    and a.mathtype = b.mathtype)
when not matched then
   insert (id,stochtestref,parentmathtype,mathtype,status)
   values (NewSeq.NEXTVAL, b.stochid, b.mathType, b.mathtype, 'none');
commit;

-----------------------------------------------------------------------------
-- merge new records to run with transformation 
--     ('nonspatial-stochastic' to 'rules')
-----------------------------------------------------------------------------
merge into stochtestrun a
using (select stochtest.id as stochid, stochtest.mathtype as mathType
	  from stochtest
	  where stochtest.mathtype = 'nonspatial-stochastic'
	  ) b
on (a.stochtestref = b.stochid
    and a.parentmathtype = b.mathtype
    and a.mathtype = 'rules')
when not matched then
   insert (id,stochtestref,parentmathtype,mathtype,status)
   values (NewSeq.NEXTVAL, b.stochid, b.mathType, 'rules', 'none');
commit;

-----------------------------------------------------------------------------
-- merge new records to run with transformation 
--     ('rules' to 'nonspatial-stochastic')
-----------------------------------------------------------------------------
merge into stochtestrun a
using (select stochtest.id as stochid, stochtest.mathtype as mathType
	  from stochtest
	  where stochtest.mathtype = 'rules'
	  ) b
on (a.stochtestref = b.stochid
    and a.parentmathtype = b.mathtype
    and a.mathtype = 'nonspatial-stochastic')
when not matched then
   insert (id,stochtestref,parentmathtype,mathtype,status)
   values (NewSeq.NEXTVAL, b.stochid, b.mathType, 'nonspatial-stochastic', 'none');
commit;

-----------------------------------------------------------------------------
-- clear all run status, errmsg, conclusion, exclude, networkgenprobs
-----------------------------------------------------------------------------
update stochtestrun 
set status = 'none', 
    errmsg = NULL, 
    conclusion = NULL, 
    exclude = NULL, 
    networkGenProbs = NULL;
commit;

-----------------------------------------------------------------------------
-- request simulations (change status from 'none' to 'waiting')
--   example: for all unrun simulations with 'nonspatial-stochastic' to 'rules'
-----------------------------------------------------------------------------
update stochtestrun 
set status = 'waiting' 
where status = 'none' 
and parentmathtype = 'nonspatial-stochastic'
and mathtype = 'rules';
commit;

-----------------------------------------------------------------------------
-- categorize simulation attempts which have errors
-----------------------------------------------------------------------------
update STOCHTESTRUN set CONCLUSION = 'non_numeric_mass_action' where instr(errmsg, 'not mass action: flattened Kf for reactionRule')>0 and instr(errmsg, 'is not numeric')>0;
update STOCHTESTRUN set CONCLUSION = 'NFSIM_more_than_2' where instr(errmsg, 'NFSim doesn')>0 and instr(errmsg, 'support more than 2')>0;
update STOCHTESTRUN set CONCLUSION = 'solver_failed_more_than_200000' where instr(errmsg, 'solver failed')>0 and instr(errmsg, 'higher than the limit of 200000')>0;
update STOCHTESTRUN set CONCLUSION = 'structure_size_not_set' where instr(errmsg, 'size of structure')>0 and instr(errmsg, 'must be assigned a positive value')>0;
update STOCHTESTRUN set CONCLUSION = 'complete' where status = 'complete';
update STOCHTESTRUN set CONCLUSION = 'not_mass_action' where instr(errmsg, 'are unable to be intepreted to mass action')>0;
update STOCHTESTRUN set CONCLUSION = 'timed_out' where instr(errmsg, 'timed out')>0;
update STOCHTESTRUN set CONCLUSION = 'no_jump_processes' where instr(errmsg, 'model requires at least one jump process')>0;
update STOCHTESTRUN set CONCLUSION = 'bng_unable_to_generate' where instr(errmsg, 'BioNetGen was unable to generate reaction network')>0;
update STOCHTESTRUN set CONCLUSION = 'trash_cannot_be_used' where instr(errmsg, 'Trash is a keyword in NFsim used for degradation, so cannot be used')>0;
update STOCHTESTRUN set CONCLUSION = 'molecule_name_invalid' where instr(errmsg, 'Name of Molecule is invalid')>0;
update STOCHTESTRUN set CONCLUSION = 'events_not_supported' where instr(errmsg, 'events not yet supported for particle-based models')>0;
update STOCHTESTRUN set CONCLUSION = 'only_mass_action_supported' where instr(errmsg, 'Only Mass Action Kinetics supported at this time')>0;
update STOCHTESTRUN set CONCLUSION = 'error_parsing_output_function' where instr(errmsg, 'Error parsing the following output function in')>0;
update STOCHTESTRUN set CONCLUSION = 'null_error_message' where errmsg is NULL and status = 'failed';
update STOCHTESTRUN set CONCLUSION = 'gibson_too_many_molecules' where instr(errmsg, 'solver failed : solver input file exception: The Initial count for Species')>0 and instr(errmsg, 'which is higher than the internal vCell limit of 1000000000.')>0;
update STOCHTESTRUN set CONCLUSION = 'RULES_failed_location_mark' where instr(errmsg, 'solver failed : failed to apply location mark transformation: variable')>0;
update STOCHTESTRUN set CONCLUSION = 'RULES_string_index_out_of_range' where instr(errmsg, 'String index out of range:')>0;
update STOCHTESTRUN set CONCLUSION = 'flux_should_have_one_reactant' where instr(errmsg, 'Flux')>0 and instr(errmsg, 'should have only one reactant.')>0;
update STOCHTESTRUN set CONCLUSION = 'local_parameter_already_exists' where instr(errmsg, 'local parameter')>0 and instr(errmsg, 'already exists')>0;
update STOCHTESTRUN set CONCLUSION = 'force_continuous_in_nonspatial_stoch' where instr(errmsg, 'Non-constant species is forced continuous, not supported for nonspatial stochastic applications')>0;
commit;

---------------------------------------------------------------------------
-- Report:  all categorized failures
---------------------------------------------------------------------------
select * from STOCHTESTRUN where status = 'failed' and conclusion is not null;

---------------------------------------------------------------------------
-- Report:  all uncategorized failures
---------------------------------------------------------------------------
select * from STOCHTESTRUN where status = 'failed' and conclusion is null;


---------------------------------------------------------------------------
-- Report:  errors in running including BioModel
---------------------------------------------------------------------------
select  
u.USERID as userid, 
'"'||b.name||'",  "'||sc.name||'",  @ '||to_char(b.VERSIONDATE,'DY MON DD  HH24:MI:SS  YYYY') as biomodel, 
r.id as id, r.PARENTMATHTYPE as parentMathType, r.mathtype as MathType, r.status as status, r.conclusion as conclusion, r.NETWORKGENPROBS as netgenprob, r.ERRMSG as errmsg
from stochtestrun r, stochtest t, vc_biomodel b, vc_userinfo u, vc_biomodelsimcontext bmsc, VC_SIMCONTEXT sc
where 
bmsc.BIOMODELREF = b.id 
and bmsc.SIMCONTEXTREF = sc.id 
and sc.MATHREF = t.MATHREF 
and t.id = r.stochtestref 
and b.id = t.biomodelref 
and u.id = b.ownerref
and r.status = 'failed';

---------------------------------------------------------------------------
-- Report:  summarize by parentmathtype, mathtype, status and errmsg (detailed errors)
---------------------------------------------------------------------------
select count(id), parentmathtype, mathtype, status, errmsg 
from stochtestrun 
group by parentmathtype, mathtype, status, errmsg 
order by parentmathtype, mathtype, status, errmsg;

---------------------------------------------------------------------------
-- Report:  summarize by parentmathtype, mathtype, status and conclusion (grouped errors)
---------------------------------------------------------------------------
select count(stochtestrun.id) as ssrid, stochtestrun.parentmathtype, stochtestrun.mathtype, stochtestrun.status, stochtestrun.conclusion
from stochtestrun, stochtest
where stochtestrun.STOCHTESTREF = stochtest.id 
group by stochtestrun.conclusion, stochtestrun.parentmathtype, stochtestrun.mathtype, stochtestrun.status
order by stochtestrun.parentmathtype, stochtestrun.mathtype, count(stochtestrun.id) desc, stochtestrun.conclusion;

---------------------------------------------------------------------------
-- Report:  summarize by parentmathtype, mathtype, status and multicompartment
---------------------------------------------------------------------------
select count(stochtestrun.id) as ssrid, stochtestrun.parentmathtype, stochtestrun.mathtype, stochtestrun.status, sign(stochtest.NUMCOMPARTMENTS-1) as multicomp
from stochtestrun, stochtest
where stochtestrun.STOCHTESTREF = stochtest.id 
group by stochtestrun.parentmathtype, stochtestrun.mathtype, stochtestrun.status, sign(stochtest.NUMCOMPARTMENTS-1)
order by sign(stochtest.numcompartments-1), stochtestrun.parentmathtype, stochtestrun.mathtype, count(stochtestrun.id) desc;
