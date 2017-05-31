#!/usr/bin/perl 
# pragmas
package Viz;
use strict;
use warnings;
use File::Spec;
use warnings;						  
use FindBin;
use Getopt::Long;
use lib 	File::Spec->catdir(
				(	exists $ENV{'BNGPATH'} ? $ENV{'BNGPATH'} :
					( 	exists $ENV{'BioNetGenRoot'} ? $ENV{'BioNetGenRoot'} : 
						do 	{
							my ($vol,$dir,$file) = File::Spec->splitpath( $FindBin::RealBin );
							my @dirs = File::Spec->splitdir($dir);
							pop @dirs;
							pop @dirs;
							File::Spec->catpath($vol,File::Spec->catdir(@dirs))
						} 
					)
				),'Perl2'
			);

use BNGModel;
use Visualization::Viz;
use Visualization::VisOptParse;
use Cwd;
use Class::Struct;

# Get list of bngls
my $dir = getcwd;
my @bngls = getModelsList($dir);

#@bngls = ();

my $outfile1 = "data_rules.csv";
open(my $fh1, ">", $outfile1) or die "Cannot open";
print $fh1 "Model,RuleName,RuleSize,RuleStructSize,RuleRegSize\n";
foreach my $bngl(@bngls)
{
	$bngl =~ /^(.*).bngl$/;
	my $modelname = $1;
	my $model = getModel($bngl);
	initializeGraphsObject($model);
	my $gr = $model->VizGraphs;
	
	# 1
	getRuleNames($model);
	my @rules = flat @{$gr->{'RuleNames'}};
	my @reverses = grep { $_ =~ /^_reverse.*$/} @rules;
	
	# 2
	getRulePatternGraphs($model);
	my @rpgs = flat @{$gr->{'RulePatternGraphs'}};
	my %lstathash; my %rstathash; # hash in which values are stathashes
	# left and right merged pattern structure graphs
	# example of how to access data
	# print $rstathash{'R19'}->{'UnboundComponents'};
	foreach my $rpg(@rpgs)
	{
		my ($lstats,$rstats) =  getPSGstats($rpg);
		my $name = $lstats->{'RuleName'};
		$lstathash{$name} = $lstats;
		$rstathash{$name} = $rstats;
	}
	
	# example of how to access data
	# print $rstathash{'R19'}->{'UnboundComponents'};
	getRuleStructureGraphs($model);
	my @rsgs = flat @{$gr->{'RuleStructureGraphs'}};
	my %rsg_stathash;
	foreach my $rsg(@rsgs)
	{
		my $stats = getRSGstats($rsg);
		my $name = $stats->{'RuleName'};
		$rsg_stathash{$name} = $stats;
	}
	
	getRuleNetworkGraphs($model);
	my @rrgs = flat @{$gr->{'RuleNetworkGraphs'}};
	my %rrgsize;
	foreach my $rrg(@rrgs)
	{
		
		my %nodetype = %{$rrg->{'NodeType'}};
		my @nodelist = @{$rrg->{'NodeList'}};
	
		my $name = join "", grep {$nodetype{$_} eq 'Rule'} @nodelist;
		my $size = scalar @nodelist;
		$rrgsize{$name} = $size;
	}
	
	# rule size (as defined in syntax)
	my %rulesize;
	foreach my $rule(@rules)
	{
		my %lhash = %{$lstathash{$rule}};
		my %rhash = %{$rstathash{$rule}};
		my @arr = qw(Molecules Components UnboundComponents Bonds WildcardBonds InternalStates);
		my $size = 0;
		map { $size = $size + $lhash{$_} + $rhash{$_}} @arr;
		$rulesize{$rule} = $size;
		#print $modelname." ".$rule." ".$rulesize{$rule}."\n";
	}
	
	# rsg size
	my %rsgsize;
	foreach my $rule(@rules)
	{
		my %stat = %{$rsg_stathash{$rule}};
		my @arr = qw(Molecules Components UnboundComponents Bonds WildcardBonds InternalStates);
		my $size = 0;
		map { $size = $size + $stat{$_} } @arr;
		$rsgsize{$rule} = $size;
	}
	
	#my $outfile = "data_rules.csv";
	#open(my $fh, ">", $outfile) or die "Cannot open";
	#print $fh "Model,RuleName,RuleSize,RuleStructSize,RuleRegSize\n";
	
	foreach my $rule(@rules)
	{
		print $fh1 join(",",$modelname,$rule,$rulesize{$rule},$rsgsize{$rule},$rrgsize{$rule})."\n";
	}
	
	#undef $gr,$model;
}
close $fh1;

# model-wide statistics
my $outfile2 = "data_models_nodes.csv";
my $outfile3 = "data_models_edges.csv";
open(my $fh2, ">", $outfile2) or die "Cannot open";
open(my $fh3, ">", $outfile3) or die "Cannot open";
print $fh2 "Model,RInf,CMap,ConvRule,CompactRule,SimmuneNet,FullReg,RegNoBkg,RegGrps,RegGrpsNoCtxt\n";
print $fh3 "Model,RInf,CMap,ConvRule,CompactRule,SimmuneNet,FullReg,RegNoBkg,RegGrps,RegGrpsNoCtxt\n";
#@bngls = ('Faeder2003.bngl');
#@bngls = ('An2009.bngl');
#@bngls = ('Faeder2003.bngl','An2009.bngl','Ligon2014.bngl');
my %rinfnodes; my %rinfedges;
my %cmapnodes; my %cmapedges;
my %convrulenodes; my %convruleedges;
my %compactrulenodes; my %compactruleedges;
my %fullregnodes; my %fullregedges;
my %regnobkgnodes; my %regnobkgedges;
my %reggrpsnodes; my %reggrpsedges;
my %reggrpsnoctxtnodes; my %reggrpsnoctxtedges; 
my %simmunenodes; my %simmuneedges;
#my %rxnnetnodes; my %rxnnetedges;
foreach my $bngl(@bngls) 
{
	$bngl =~ /^(.*).bngl$/;
	my $modelname = $1;
	my $model = getModel($bngl);
	initializeGraphsObject($model);
	my $gr = $model->VizGraphs;
	getRuleNames($model);
	getRuleNetwork($model);
	
	# RINF
	my $bpg = $gr->{'RuleNetwork'};
	my $rinf = makeRINF($bpg);
	$rinfnodes{$modelname} = scalar @{$rinf->{'Nodes'}};
	$rinfedges{$modelname} = scalar @{$rinf->{'Edges'}};
	
	# CMap
	my $mtypes = $model->MoleculeTypesList->MolTypes;
	my @cmapstats = getCMAPStats($mtypes,$bpg);
	$cmapnodes{$modelname} = $cmapstats[0];
	$cmapedges{$modelname} = $cmapstats[1];
	
	# ConvRule
	getRulePatternGraphs($model);
	my ($convnodes,$convedges) = getConvRuleStats($gr->{'RulePatternGraphs'},$gr->{'RuleNames'});
	$convrulenodes{$modelname} = $convnodes;
	$convruleedges{$modelname} = $convedges;
	
	# CompactRule
	getRuleStructureGraphs($model);
	my ($comprulenodes,$compruleedges) = getCompactRulesStats($gr->{'RuleStructureGraphs'});
	$compactrulenodes{$modelname} = $comprulenodes;
	$compactruleedges{$modelname} = $compruleedges;
	
	# FullReg
	getRuleNetwork($model);
	$bpg = $gr->{'RuleNetwork'};
	$fullregnodes{$modelname} = scalar @{$bpg->{'NodeList'}};
	$fullregedges{$modelname} = scalar @{$bpg->{'EdgeList'}};
	
	# OptsFileOut
	my %q = initializeExecParams(); 
	$q{'type'} = 'opts';
	execute_params($model,\%q);
	
	# RegNoBkg
	my %p = initializeExecParams();
	my $optsname = $modelname."_opts.txt";
	$p{'background'} = 0;$p{'opts'} = [$optsname]; $p{'groups'} = 0; $p{'collapse'}=0;$p{'type'}='regulatory';
	my @x = parseOpts($optsname);
	my %bkg = %{$x[1]}; my %cls = %{$x[2]};
	$bpg = filterNetworkGraph($bpg,$bkg{'include'});
	$regnobkgnodes{$modelname} = scalar @{$bpg->{'NodeList'}};
	$regnobkgedges{$modelname} = scalar @{$bpg->{'EdgeList'}};
	
	# RegGrps
	my $bpg2 = $bpg;
	my $donotusecontext = 0;
	my %classes_in;
	foreach my $grpname(keys %cls)
	{
		map {$classes_in{$_} = $grpname;} @{$cls{$grpname}};
	}
	syncClasses($model,$bpg2,\%classes_in,$donotusecontext);
	$bpg2 = collapseNetworkGraph($bpg2);
	$reggrpsnodes{$modelname} = scalar @{$bpg2->{'NodeList'}};
	$reggrpsedges{$modelname} = scalar @{$bpg2->{'EdgeList'}};
	
	# RegGrpsNoCtxt
	$bpg2 = $bpg; $donotusecontext = 1;
	$model->VizGraphs->{'Classes'} = {};
	syncClasses($model,$bpg2,\%classes_in,$donotusecontext);
	$bpg2 = collapseNetworkGraph($bpg2);
	$reggrpsnoctxtnodes{$modelname} = scalar @{$bpg2->{'NodeList'}};
	$reggrpsnoctxtedges{$modelname} = scalar @{$bpg2->{'EdgeList'}};
	
	# SimmuneNet
	my ($sim_n,$sim_e) = getSimmuneNetworkStats($model->RxnRules,$model->MoleculeTypesList->MolTypes);
	$simmunenodes{$modelname} = $sim_n; 
	$simmuneedges{$modelname} = $sim_e;
	
	# reaction network - old
	#if( -e $modelname.".net") {
	#my ($rxn_n,$rxn_e)  = getRxnNetStats($modelname);
	#$rxnnetnodes{$modelname} = $rxn_n;  
	#$rxnnetedges{$modelname} = $rxn_e;
	#}
	#else
	#{
	#$rxnnetnodes{$modelname} = "NaN";  
	#$rxnnetedges{$modelname} = "NaN";
	#}
	my @str1 = ($modelname,$rinfnodes{$modelname},$cmapnodes{$modelname},
		$convrulenodes{$modelname},$compactrulenodes{$modelname},$simmunenodes{$modelname},
		$fullregnodes{$modelname},$regnobkgnodes{$modelname},
		$reggrpsnodes{$modelname},$reggrpsnoctxtnodes{$modelname});
	my @str2 = ($modelname,$rinfedges{$modelname},$cmapedges{$modelname},
		$convruleedges{$modelname},$compactruleedges{$modelname},$simmuneedges{$modelname},
		$fullregedges{$modelname},$regnobkgedges{$modelname},
		$reggrpsedges{$modelname},$reggrpsnoctxtedges{$modelname});

	print $fh2 join(",",@str1)."\n";
	print $fh3 join(",",@str2)."\n";
}
close $fh2;
close $fh3;
#@bngls = ('Faeder2003.bngl');

# rule vs rxnnet statistics
my $outfile4 = "data_models_rxns.csv";
open(my $fh4, ">", $outfile4) or die "Cannot open";
print $fh4 "Model,Patterns,Rules,Species,Reactions\n";
foreach my $bngl(@bngls)
{
	$bngl =~ /^(.*).bngl$/;
	my $modelname = $1;
	next unless (-e $modelname.".net");
	my $model = getModel($bngl);
	initializeGraphsObject($model);
	getRuleNames($model);
	getRulePatternGraphs($model);
	my ($patt,$rules) = getConvRuleStats2($model->VizGraphs->{'RulePatternGraphs'});
	my ($sp,$rxn) = getRxnNetStats($modelname);
	my @x = ($modelname,$patt,$rules,$sp,$rxn);
	print $fh4 join(",",@x)."\n";
}
close $fh4;

sub getConvRuleStats2
{
	# this only counts patterns and rules
	my @rrs = @{shift @_};
	my $patt=0; my $rules=0;
	foreach my $rr(@rrs)
	{
		my $rule = $rr->[0];
		my ($lstats,$rstats) = getPSGstats($rule);
		my $hasrev = scalar(@$rr) - 1;
		$patt += $lstats->{'Patterns'} + $rstats->{'Patterns'};
		$rules += 1;
		if($hasrev) {$rules+=1;}
	}
	return ($patt,$rules);	
}

sub getRxnNetStats
{
	my $modelname = shift @_; my $outfile = $modelname.".net";
	open(my $fh, "<", $outfile) or die "Cannot open";
	my @lines = <$fh>;
	close $fh;

	# counters for species and reactions
	my $sp = 0; my $rxn = 0;
	# toggles
	my $is_rxn = 0; my $is_sp = 0;
	foreach my $line(@lines)
	{
		if($line =~ /begin reactions/) {$is_rxn = 1; $is_sp = 0; next;}
		if($line =~ /begin species/) {$is_rxn = 0; $is_sp = 1; next;}
		if($line =~ /end reactions/ or $line =~ /end species/  ) {$is_rxn = 0; $is_sp = 0; next;}	
		if($is_rxn==1) {$rxn++;}
		if($is_sp==1) {$sp++;}
	}
	return ($sp,$rxn);
}
sub getCompactRulesStats
{
	my @rsgs = flat(@{shift @_});
	my $n = 0; my $e = 0;
	foreach my $rsg(@rsgs)
	{
		$n++; $e++;
		my %stats = %{getRSGstats($rsg)};
		$n = $n + $stats{'Molecules'} + $stats{'Components'} + $stats{'InternalStates'};
		$e = $e + $stats{'Components'} + $stats{'InternalStates'};
		$n = $n + $stats{'WildcardBonds'};
		$e = $e + $stats{'WildcardBonds'};
		$e = $e + $stats{'Bonds'} - $stats{'BondOperations'};
		$n = $n + $stats{'BondOperations'} + $stats{'StateOperations'} + $stats{'MoleculeOperations'};
		$e = $e + 2*$stats{'BondOperations'} + 2*$stats{'StateOperations'} + $stats{'MoleculeOperations'};	
	}
	return ($n,$e);
}
sub getConvRuleStats
{
	my @rrs = @{shift @_};
	my @rnames = flat(@{shift @_});
	my $n = 0; my $e = 0;
	$n = $n + scalar(@rnames);
	foreach my $rr(@rrs)
	{
		my $rule  = $rr->[0];
		my $hasrev = scalar(@$rr) - 1;
		my $rname = $rule->{'Name'};
		my ($lstats,$rstats) = getPSGstats($rule);
		$n = $n + $lstats->{'Patterns'} + $rstats->{'Patterns'};
		$e = $e + $lstats->{'Patterns'} + $rstats->{'Patterns'};
		if($hasrev) {$e = $e + $lstats->{'Patterns'} + $rstats->{'Patterns'};}
		$n = $n + $lstats->{'Molecules'} + $lstats->{'Components'} + $lstats->{'InternalStates'};
		$n = $n + $rstats->{'Molecules'} + $rstats->{'Components'} + $rstats->{'InternalStates'};
		$e = $e + $lstats->{'Components'} + $lstats->{'InternalStates'};
		$e = $e + $rstats->{'Components'} + $rstats->{'InternalStates'};
		$n = $n + $lstats->{'WildcardBonds'} + $rstats->{'WildcardBonds'};
		$e = $e + $lstats->{'WildcardBonds'} + $rstats->{'WildcardBonds'} + $lstats->{'Bonds'} + $rstats->{'Bonds'};;
	}
	return ($n,$e);
}
sub getCMAPStats
{
	my %mtypes = %{shift @_};
	my $bpg = shift @_;
	my $n=0; my $e = 0; my %stoich;
	foreach my $mname(keys %mtypes)
	{
		$n++;
		my $ctypes = $mtypes{$mname}->Components;
		foreach my $comp(@$ctypes)
			{
				$n++;$e++;
				my $cname = $comp->Name; $stoich{$mname.".".$cname}++;
				my @states = @{$comp->States};
				for(my $i=0; $i<@states; $i++) { $n++;$e++;}
			}
	}
	my @bonds = grep { /\!/} @{$bpg->{'NodeList'}};
	foreach my $bond(@bonds)
	{
		$bond =~ /^(.*)\((.*)\!1\)[.](.*)\((.*)\!1\)$/;
		my $c1 = $1.".".$2;
		my $c2 = $3.".".$4;
		my $count = $stoich{$c1}*$stoich{$c2};
		$e = $e + $count;
	}
	return ($n,$e);
}
sub new_PSG_StatsHash
{
	return (
	'RuleName' => $_[0],
	'Side' => $_[1],
	'Patterns' => $_[2],
	'Molecules' => $_[3],
	'Components' => $_[4],
	'BoundComponents' => $_[5],
	'UnboundComponents' => $_[6],
	'Bonds' => $_[7],
	'WildcardBonds' => $_[8],
	'InternalStates' => $_[9],
	);
}
sub getSimmuneNetworkStats
{
	my @rules = flat(@{shift @_});
	my %mtypes = %{shift @_};
	my %reacprods;
	my @patts;
	my $n= 0; my $e = 0;
	foreach my $rule(@rules)
	{
		# strip away components and bonds, leaving only molecules
		my @reac = 	sort map {my $x = $_; $x =~ s/\(.*?\)//g; join ".", sort split /[.]/,$x;} 
					map {$_->toString()} 
					@{$rule->Reactants};
		my @prod = 	sort map {my $x = $_; $x =~ s/\(.*?\)//g; join ".", sort split /[.]/,$x;}
					map {$_->toString()} 
					@{$rule->Products};
		push @patts, (@reac,@prod);
		# each rule is a node
		$n++;
		# each reactant and product needs an edge
		$e = $e + scalar(@reac) + scalar(@prod);		
	}
	
	@patts = uniq @patts;
	my %bindingsitenum; my %internalstatenum;
	foreach my $mname(keys %mtypes)
	{
		$bindingsitenum{$mname} = scalar @{$mtypes{$mname}->Components};
		$internalstatenum{$mname} = scalar map {@{$_->States}} @{$mtypes{$mname}->Components};
	}
	foreach my $patt(@patts)
	{
		my @mols = split /[.]/,$patt;
		# node for each molecule
		map {$n += 1} @mols;
		# node for each binding site and internal state
		# edge for each binding site and internal state (hierarchical relation to molecule)
		map {$n += $bindingsitenum{$_}+$internalstatenum{$_}} @mols;
		map {$e += $bindingsitenum{$_}+$internalstatenum{$_}} @mols;
		# edge for each bond, which is 1 less than number of molecules
		map {$e += 1} @mols; $e = $e-1;
	}
	return ($n,$e);
}

sub getRxnNetStats_old
{
	my $modelname = shift @_; my $outfile = $modelname.".net";
	open(my $fh, "<", $outfile) or die "Cannot open";
	my @lines = <$fh>;
	close $fh;

	# counters for species, reactions and edges
	my $n = 0; my $e = 0;
	# toggles
	my $is_rxn = 0; my $is_sp = 0;
	foreach my $line (@lines)
	{
		if($line =~ /begin reactions/) {$is_rxn = 1; $is_sp = 0; next;}
		if($line =~ /begin species/) {$is_rxn = 0; $is_sp = 1; next;}
		if($line =~ /end reactions/ or $line =~ /end species/  ) {$is_rxn = 0; $is_sp = 0; next;}	
		if($is_rxn==1 or $is_sp==1) { $n++;}
		if($is_rxn==1) 
		{
			$line =~ s/#.*//g;
			my @commas = $line =~ /[,]/;
			my @zeros = $line =~ /\s[0]\s/;
			$e = $e + 2 + scalar(@commas) - scalar(@zeros); 
		}
	}
	return ($n,$e);
}




sub new_RSG_StatsHash
{
	return (
	'RuleName' => $_[0],
	'Molecules' => $_[1],
	'Components' => $_[2],
	'BoundComponents' => $_[3],
	'UnboundComponents' => $_[4],
	'Bonds' => $_[5],
	'WildcardBonds' => $_[6],
	'InternalStates' => $_[7],
	'BondOperations'=> $_[8],
	'StateOperations'=>$_[9],
	'MoleculeOperations'=>$_[10],
	);
}

sub initializeGraphsObject
{
	my $model = shift @_;
	my $gr = Graphs->new();
	$model->VizGraphs($gr);
	$gr->{'NewName'} = -1;
	return;
}


sub getPSGstats
{
	my $rpsg = shift @_;
	my @nodelist = @{$rpsg->{'NodeList'}};
	my @ids = map { $_->{'ID'}; } @nodelist;
	# node ids are like this
	# RuleNum.[Re=0/Pr=1].PattNum.PattNum.MolNum.CompNum.[IntState=0,BondState=1]
	# No idea why PattNum is repeated. Clean this up later.
	my %lstat = new_PSG_StatsHash(); $lstat{'Side'} = 'Reactant';
	my %rstat = new_PSG_StatsHash(); $rstat{'Side'} = 'Product';
	
	# partition left nodes versus right nodes versus rule node
	my @rule_arr = grep { /^\d+$/ } @ids; # should be array of size 1
	
	my @left = grep { /^\d+[.]0/} @ids;
	my @right = grep { /^\d+[.]1/} @ids;	
	
	my $rule_id = $rule_arr[0];
	my $rulename = join "", map {$_->{'Name'}} grep {$_->{'ID'} eq $rule_id; } @nodelist;	
	my $leftpatts = scalar grep { /^\d+[.]0[.]\d+$/} @left;
	my $rightpatts = scalar grep { /^\d+[.]1[.]\d+$/} @right;
	my $leftmols = scalar grep { /^\d+[.]\d+[.]\d+[.]\d+[.]\d+$/} @left;
	my $rightmols = scalar grep { /^\d+[.]\d+[.]\d+[.]\d+[.]\d+$/} @right;
	my $leftcomps = scalar grep { /^\d+[.]\d+[.]\d+[.]\d+[.]\d+[.]\d+$/} @left;
	my $rightcomps = scalar grep { /^\d+[.]\d+[.]\d+[.]\d+[.]\d+[.]\d+$/} @right;
	my $leftints = scalar grep { /^\d+[.]\d+[.]\d+[.]\d+[.]\d+[.]\d+[.]0$/} @left;
	my $rightints = scalar grep { /^\d+[.]\d+[.]\d+[.]\d+[.]\d+[.]\d+[.]0$/} @right;
	
	my @lbonds =  grep { /^\d+[.]\d+[.]\d+[.]\d+[.]\d+[.]\d+[.]1$/} @left;
	my @rbonds =  grep { /^\d+[.]\d+[.]\d+[.]\d+[.]\d+[.]\d+[.]1$/} @right;
	my @lbondnodes = grep { has(\@lbonds,$_->{'ID'})==1 } @nodelist;
	my @rbondnodes = grep { has(\@rbonds,$_->{'ID'})==1 } @nodelist;
	my $leftwcs = scalar grep { scalar(@{$_->{'Parents'}}) == 1 } @lbondnodes;
	my $rightwcs = scalar grep { scalar(@{$_->{'Parents'}}) == 1 } @rbondnodes;
	my $leftbonds = scalar grep { scalar(@{$_->{'Parents'}}) == 2 } @lbondnodes;
	my $rightbonds = scalar grep { scalar(@{$_->{'Parents'}}) == 2 } @rbondnodes;
	my @lcomps = grep { /^\d+[.]\d+[.]\d+[.]\d+[.]\d+[.]\d+$/} @left;
	my @rcomps = grep { /^\d+[.]\d+[.]\d+[.]\d+[.]\d+[.]\d+$/} @right;
	my $lboundcomps = scalar uniq map { @{$_->{'Parents'}} } @lbondnodes;
	my $rboundcomps = scalar uniq map { @{$_->{'Parents'}} } @rbondnodes;
	my $l_unboundcomps = $leftcomps - $lboundcomps;
	my $r_unboundcomps = $leftcomps - $rboundcomps;
	
	my @arr = ($rulename,scalar(@rule_arr),scalar(@left),scalar(@right),$leftpatts,$rightpatts,$leftmols,$rightmols,$leftcomps,$rightcomps,$leftints,$rightints,$leftbonds,$rightbonds,$leftwcs,$rightwcs,$leftbonds,$rightbonds,$lboundcomps,$rboundcomps,$l_unboundcomps,$r_unboundcomps);
	
	my %lstats = new_PSG_StatsHash($rulename,'Reactant',$leftpatts,$leftmols,$leftcomps,$lboundcomps,$l_unboundcomps,$leftbonds,$leftwcs,$leftints);
	my %rstats = new_PSG_StatsHash($rulename,'Product',$rightpatts,$rightmols,$rightcomps,$rboundcomps,$r_unboundcomps,$rightbonds,$rightwcs,$rightints);
	
	return (\%lstats,\%rstats);
}

sub getRSGstats
{
	my $rsg = shift @_;
	#print printStructureGraph($rsg)."\n\n";
	
	my @nodelist = @{$rsg->{'NodeList'}};
	my @ids = map { $_->{'ID'}; } @nodelist;
	# node ids are like this
	# RuleNum.PattNum.PattNum.MolNum.CompNum.[IntState=0,BondState=1]
	# RuleNum.GraphOp
	# No idea why PattNum is repeated. Clean this up later.
	# PattNum is meaningless in RSG, but it remains over from merging of PSGs
	
	my @rule_arr = grep { /^\d+$/ } @ids; # should be array of size 1
	
	my $rule_id = $rule_arr[0];
	my $rulename = join "", map {$_->{'Name'}} grep {$_->{'ID'} eq $rule_id; } @nodelist;
	my $mols = scalar grep { /^\d+[.]\d+[.]\d+[.]\d+$/} @ids;
	my $comps = scalar grep { /^\d+[.]\d+[.]\d+[.]\d+[.]\d+$/} @ids;
	my $ints = scalar grep { /^\d+[.]\d+[.]\d+[.]\d+[.]\d+[.]0$/} @ids;
	
	my @bond_ids =  grep { /^\d+[.]\d+[.]\d+[.]\d+[.]\d+[.]1$/} @ids;
	
	my @bondnodes = grep { has(\@bond_ids,$_->{'ID'})==1 } @nodelist;
	my $wcs = scalar grep { scalar(@{$_->{'Parents'}}) == 1 } @bondnodes;
	my $bonds_num = scalar grep { scalar(@{$_->{'Parents'}}) == 2 } @bondnodes;
	my $boundcomps = scalar uniq map { @{$_->{'Parents'}} } @bondnodes;
	my $unboundcomps = $comps - $boundcomps;

	
	my @graphops = map {$_->{'Name'}} grep {$_->{'Type'} eq "GraphOp"} @nodelist;
	my $bond_ops = scalar grep { $_ =~ /Bond/} @graphops;
	my $mol_ops = scalar grep { $_ =~ /Mol/} @graphops;
	my $state_ops = scalar grep { $_ =~ /State/} @graphops;
	
	my @arr = ($rulename,$mols,$comps,$boundcomps,$unboundcomps,$bonds_num,$wcs,$ints,$bond_ops,$state_ops,$mol_ops);
	my %stat = new_RSG_StatsHash(@arr); 
	return \%stat;
	
}


sub getModelsList
{
	opendir (DIR, shift @_) or die $!;
	my @bngls;
	while(my $file = readdir(DIR))
	{ if ($file=~ /.bngl$/) {push @bngls, $file;} }
	closedir DIR;
	return @bngls;
}

# Models
# Model-Name
# Rules - number of rules
# CRV-Nodes - conventional rule visualization
# CRV-Edges
# CMV-Nodes - compact rule visualization
# CMV-Edges
# SNV-Nodes - Simmune Network Viewer
# SNV-Edges
# MRG-Nodes - model regulatory graph 
# MRG-Edges
# MRGC-Nodes - model regulatory graph with pruning, grouping and collapsing
# MRGC-Edges
# RINF-Nodes - rule influence diagram
# RINF-Edges
# RXN-Nodes - reaction network
# RXN-Edges





#filecheck($args{'bngl'},'BNGL');
#map { filecheck($_,'Opts') } @{$args{'opts'}} if defined $args{'opts'};
#my $model = getModel($args{'bngl'});


#my $exec_params = getExecParams(\%args);
#execute_params($model,$exec_params);

sub filecheck
{
	my $file = shift @_;
	my $type = shift @_;
	if(not defined $file) { print $type." file not defined.\n"; exit;}
	if(not -e $file) { print $type." file ".$file." not found.\n"; exit;}
	print "Found ".$type." file ".$file.".\n";
	return;
}

sub getModel
{
	my $filename = shift @_;
	print "Importing model from ".$filename."\n";
	my %args = ();
	$args{'file'} = $filename;
	$args{'skip_actions'} = 1;
	$args{'action_skip_warn'} = 1;

	#print $filename;
	my $model = BNGModel->new();
	$model->initialize();
	$BNGModel::GLOBAL_MODEL = $model;
	
	#print "Opening file: ".$filename."\n";
	my $err = $model->readModel(\%args) ;
	
	if ($err) { print "ERROR:".$err."\n";}
	
	return $model;
}

1;
