#!/usr/bin/perl
# $Id: runBNG.pl,v 1.2 2007/10/20 04:18:21 faeder Exp $

use FindBin;
use lib $FindBin::Bin;
use BNGModel;
use SpeciesList;
use BNGUtils;
use strict;

# Set to nonzero value to equilibrate model prior to introduction of one
# or more species.  This is required for the fceri model because there is a
# constitutive (present without ligand) interaction between Lyn and FceRI.
my $t_equil=1000;
my $max_equil=100;
my @spec_equil=("Lig(l,l)");

# End point of production simulation and time point at which outputs will
# be reported.
my $t_end=600;
my $n_steps=1;
# Names of observables reported at integration end point
my @output_vars=("RecSykPS");

# Force regeneration of network prior to simulation 
my $force_netgen=0;

#------ END OF USER DEFINED PARAMETERS -------

my $model= BNGModel->new();
my %params=();
while ($ARGV[0] =~ /^-/){
  $_ = shift;
  if (/^-g$/){
    $force_netgen=1;
  }
  else{
    exit_error("Unrecognized command line option $_");
  }
}

# Get prefix and determine whether network has been generated
my $prefix= shift(@ARGV);

# Dup original STDOUT
open my $oldout, ">&STDOUT"     or die "Can't dup STDOUT: $!";

# Redirect STDOUT to logfile 
open(STDOUT,">${prefix}_runBNG.log");

# turn off output buffering on STDOUT
(select(*STDOUT), $|=1)[0];

printf "BioNetGen version %s\n", BNGversion();

if (!$force_netgen && -r "$prefix.net"){
	# Read previously generate net file
	my $file= "$prefix.net";
	$params{file}=$file;
	if (my $err=$model->readFile(\%params)){exit_error($err);}
} elsif (-r "$prefix.bngl") {
	# Read BNGL file and generate network 
	my $file= "$prefix.bngl";
	$params{file}=$file;
	if (my $err=$model->readFile(\%params)){exit_error($err);}
	if (my $err=$model->generate_network({overwrite=>1})){exit_error($err);}
} else {
	exit_error("No BNGL or NET file could be found with prefix $prefix.");
}	

# Hash indices of observables
my %obs_ind=();
my $oind=0;
for my $obs (@{$model->Observables}){
	$obs_ind{$obs->Name}= $oind++;
}

# Set parameters to value of arguments
my $iparam=0;
for my $pvalue (@ARGV){
	if (my $err=$model->ParamList->setIndex($iparam,$pvalue)){exit_error($err);}
	++$iparam;
}	

# Perform equilibration
if ($t_equil>0){
	# Set equilibration variables to zero
	my %val_equil=();
	my $oldval;
	for my $spec (@spec_equil){
		# determine whether variable is species or concentration
		(my $err, my $oldval)= $model->setConcentration($spec,0);
		$err && exit_error($err);
 		$val_equil{$spec}= $oldval;
	}
	my $suff="runBNG_equil";
	if (my $err=$model->simulate_ode({suffix=>$suff,t_end=>$t_equil,n_steps=>$max_equil,steady_state=>1,sparse=>1})){exit_error($err);}
	# Reset equilibration variables	
	for my $spec (@spec_equil){
		(my $err, my $oldval)= $model->setConcentration($spec,$val_equil{$spec});
		$err && exit_error($err);
	}
}

# Run simulation
my $suff="runBNG_run";
if (my $err=$model->simulate_ode({suffix=>$suff,t_end=>$t_end,n_steps=>$n_steps})){exit_error($err);}	

# Report results
my $gdatfile= $prefix."_".$suff.".gdat";
my @output=();
if (-r "$gdatfile"){
  print "Updating observable concentrations from $gdatfile\n";
  open(GDAT, "$gdatfile");
  my $last="";
  while(<GDAT>){
    $last=$_;
  }
  close(GDAT);
  (my $time, my @gconc)= split(' ', $last);
  for my $oname (@output_vars){
#    push @output, "$oname:".$gconc[$obs_ind{$oname}];
    push @output, $gconc[$obs_ind{$oname}];
  }
}else {
  exit_error("Could not find $gdatfile");
}

printf "CPU TIME: total %.1f s.\n", cpu_time(0);

open STDOUT, ">&", $oldout or die "Can't dup \$oldout: $!";
# Print results to real STDOUT (to be read by Matlab)
print join(" ",@output),"\n";


