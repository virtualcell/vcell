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
update STOCHTESTRUN set CONCLUSION = NULL;
update STOCHTESTRUN set CONCLUSION = 'OK_reaction_limit_exceeded_generation' where instr(errmsg, 'Unable to generate Math for Application')>0 and instr(errmsg, 'Reactions limit exceeded: max allowed number')>0;
update STOCHTESTRUN set CONCLUSION = 'BAD_initial_count_not_constant' where (instr(errmsg, 'solver failed : error processing initial count of ParticleSpeciesPattern')>0 and instr(errmsg, 'cannot be evaluated as a constant')>0) or (instr(errmsg,'solver failed : Could not generate input file: variable')>0 and instr(errmsg,'initial condition is required to be a constant')>0);
update STOCHTESTRUN set CONCLUSION = 'BAD_unbound_identifier' where instr(errmsg, 'Failed to intepret kinetic rate for reaction')>0 and instr(errmsg, 'tryin to evaluate unbound identifier')>0;
update STOCHTESTRUN set CONCLUSION = 'BAD_unbound_identifier_in_mathgen' where instr(errmsg, 'Unable to sort, unknown identifier')>0 or instr(errmsg, 'is either not found in your model or is not allowed to be used in the current context')>0;
update STOCHTESTRUN set CONCLUSION = 'OK_non_numeric_mass_action' where instr(errmsg, 'not mass action: flattened Kf for reactionRule')>0 and instr(errmsg, 'is not numeric')>0;
update STOCHTESTRUN set CONCLUSION = 'OK_NFSIM_more_than_2' where instr(errmsg, 'NFSim doesn')>0 and instr(errmsg, 'support more than 2')>0;
update STOCHTESTRUN set CONCLUSION = 'OK_solver_failed_more_than_200000' where instr(errmsg, 'solver failed')>0 and instr(errmsg, 'higher than the limit of 200000')>0;
update STOCHTESTRUN set CONCLUSION = 'OK_structure_size_not_set' where instr(errmsg, 'size of structure')>0 and instr(errmsg, 'must be assigned a positive value')>0;
update STOCHTESTRUN set CONCLUSION = 'OK_complete' where status = 'complete';
update STOCHTESTRUN set CONCLUSION = 'OK_not_mass_action' where instr(errmsg, 'are unable to be intepreted to mass action')>0;
update STOCHTESTRUN set CONCLUSION = 'OK_timed_out' where instr(errmsg, 'timed out')>0;
update STOCHTESTRUN set CONCLUSION = 'OK_no_jump_processes' where instr(errmsg, 'model requires at least one jump process')>0;
update STOCHTESTRUN set CONCLUSION = 'BAD_bng_unable_to_generate' where instr(errmsg, 'BioNetGen was unable to generate reaction network')>0;
update STOCHTESTRUN set CONCLUSION = 'OK_trash_cannot_be_used' where instr(errmsg, 'Trash is a keyword in NFsim used for degradation, so cannot be used')>0;
update STOCHTESTRUN set CONCLUSION = 'BAD_molecule_name_invalid' where instr(errmsg, 'Name of Molecule is invalid')>0;
update STOCHTESTRUN set CONCLUSION = 'OK_events_not_supported' where instr(errmsg, 'events not yet supported for particle-based models')>0 or instr(errmsg, 'Error adding events to simulationContext')>0;
update STOCHTESTRUN set CONCLUSION = 'OK_only_mass_action_supported' where instr(errmsg, 'Only Mass Action Kinetics supported at this time')>0;
update STOCHTESTRUN set CONCLUSION = 'OK_mass_action_failed_to_interpret' where instr(errmsg, 'Failed to intepret kinetic rate for reaction')>0 and (instr(errmsg,'Mathmatical form incompatible with mass action')>0);
update STOCHTESTRUN set CONCLUSION = 'BAD_mass_action_identical_reactants_products' where instr(errmsg, 'Failed to intepret kinetic rate for reaction')>0 and (instr(errmsg,'Identical reactants and products not supported for stochastic models')>0);
update STOCHTESTRUN set CONCLUSION = 'BAD_error_parsing_output_function' where instr(errmsg, 'Error parsing the following output function in')>0;
update STOCHTESTRUN set CONCLUSION = 'BAD_null_error_message' where errmsg is NULL and status = 'failed';
update STOCHTESTRUN set CONCLUSION = 'OK_gibson_too_many_molecules' where instr(errmsg, 'solver failed : solver input file exception: The Initial count for Species')>0 and instr(errmsg, 'which is higher than the internal vCell limit of 1000000000.')>0;
update STOCHTESTRUN set CONCLUSION = 'OK_not_legal_identifier_RBM' where instr(errmsg, 'not legal identifier for rule-based stochastic applications, try')>0;
update STOCHTESTRUN set CONCLUSION = 'BAD_RULES_failed_location_mark' where instr(errmsg, 'solver failed : failed to apply location mark transformation: variable')>0;
update STOCHTESTRUN set CONCLUSION = 'BAD_RULES_string_index_out_of_range' where instr(errmsg, 'String index out of range:')>0;
update STOCHTESTRUN set CONCLUSION = 'BAD_flux_should_have_one_reactant' where instr(errmsg, 'Flux')>0 and instr(errmsg, 'should have only one reactant.')>0;
update STOCHTESTRUN set CONCLUSION = 'BAD_local_parameter_already_exists' where instr(errmsg, 'local parameter')>0 and instr(errmsg, 'already exists')>0;
update STOCHTESTRUN set CONCLUSION = 'BAD_force_continuous_in_nonspatial_stoch' where instr(errmsg, 'Non-constant species is forced continuous, not supported for nonspatial stochastic applications')>0;
update STOCHTESTRUN set CONCLUSION = 'BAD_Error_Executing_BNG' where instr(errmsg, 'Error executing BNG')>0;
update STOCHTESTRUN set CONCLUSION = 'BAD_Missing_DeleteMolecule_NFsim' where instr(errmsg, 'NFsim enforces the use of the DeleteMolecules')>0;
update STOCHTESTRUN set CONCLUSION = 'BAD_NFsim_AddBond_Null_Reactant' where instr(errmsg, 'solver failed : Null reactant component(s) for bond entry while generationg an addBond operation')>0;
update STOCHTESTRUN set CONCLUSION = 'BAD_Parser_syntax_error' where instr(errmsg, 'Encountered "')>0 and instr(errmsg, '" at line ')>0;
update STOCHTESTRUN set CONCLUSION = 'BAD_no_actual_patterns' where instr(errmsg, 'solver failed : Could not execute code: You have a pattern named')>0 and instr(errmsg, 'include any actual patterns')>0;
update STOCHTESTRUN set CONCLUSION = 'OK_Incompatible_reactant_product' where instr(errmsg, 'Unable to perform operation. Errors found')>0 and (instr(errmsg, 'have incompatible States')>0 or instr(errmsg,'have incompatible Bond Types')>0);
update STOCHTESTRUN set CONCLUSION = 'BAD_netgen_sc_name_already_used' where instr(errmsg, 'Unexpected Reaction Rule exception: conflict with Reaction Rule')>0 and instr(errmsg, 'name already used for Species Context in Structure')>0;
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
-- and r.status = 'failed'
and r.conclusion not like 'OK_%'
and (r.PARENTMATHTYPE = 'rules' or r.MATHTYPE = 'rules')
-- and r.CONCLUSION is NULL
;

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
and conclusion not like 'OK_%'
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
