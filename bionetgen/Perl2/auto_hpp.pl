#!/usr/bin/perl
use strict;
use warnings;
# Find Perl2 Module directory:
# Look for environment variable BNGPATH. If not defined, try current directory.
use FindBin;
use File::Spec;
use lib $FindBin::RealBin;
use lib exists $ENV{'BNGPATH'} ? File::Spec->catdir( ($ENV{'BNGPATH'}), "Perl2" ) : $FindBin::RealBin; 
# load Perl Modules
use List::Util ("sum");
use Scalar::Util ("looks_like_number");
use Getopt::Long;
# load BNG Module
use BNGModel;


unless (exists $ENV{'BNGPATH'})
{   # set BNGPATH environment variable
    my ($volume,$directories,$file) = File::Spec->splitpath( $FindBin::RealBin );
    my @dirs = File::Spec->splitdir( $directories );
    pop @dirs;   # BNG executable script should be down one directory from here
    $ENV{'BNGPATH'} = File::Spec->catpath( $volume, File::Spec->catdir(@dirs), '' );
}


# process arguments
my $complex   = 1;        # complex bookkeeping
my $dt        = 1;        # time step
my $fprof     = 0.01;     # volume fraction for profiling
my $fsim      = 0.1;      # volume fraction for simulation
my $gml       = 10000000; # global molecule limit
my $klump     = 100000;   # lumping rate
my $modelpath = '';       # path to bngl model file
my $nsteps    = 20;       # num sim steps
my $params    = '';       # name of parameter file
my $threshold = 50;       # lumping threshold
my $verbose   = 0;        # verbose output
GetOptions( "complex!"    => \$complex,
            "dt=f"        => \$dt,
            "fprof=f"     => \$fprof,
            "fsim=f"      => \$fsim,
            "gml=i"       => \$gml,
            "help"        => sub { display_help(); exit(0); },
            "klump=f"     => \$klump,
            "model=s"     => \$modelpath,
            "nsteps=i"    => \$nsteps,   
            "params=s"    => \$params,
            "threshold=f" => \$threshold,
            "verbose"     => \$verbose
          )
or die "Error in command line arguments";


# check for mandatory switches
if ($modelpath eq '') { die "Missing mandatory 'model' argument"; }
# warn if extra arguments
if (@ARGV) { warn "Ignoring extra command line arguments"; }

# check for positive arguments
if ($dt <= 0) { die "Argument 'dt' must be positive"; }
if ($fprof <= 0) { die "Argument 'fprof' must be positive"; }
if ($fsim <= 0)  { die "Argument 'fsim' must be positive"; }
if ($gml < 1)    { die "Argument 'gml' must be positive"; }
if ($klump <= 0) { die "Argument 'klump' must be positive"; }
if ($nsteps < 1) { die "Argument 'nsteps' must be positive"; }
if ($threshold < 0) { die "Argument 'threshold' must be non-negative"; }


# extract model prefix
my $model_prefix;
my ($vol,$dir,$file) = File::Spec->splitpath( $modelpath );

if ($file =~ /(.+)\.bngl$/)
{  $model_prefix = $1;  }
else
{  die "Model file is does not have BNGL extension";  }

# readfile options
my %readfile_params = ( "action_skip_warn"=>1, "allow_actions"=>0, "logging"=>"${model_prefix}.log" );
# nfsim configuration
my %simulate_params = ( "suffix"=>"nf", "t_end"=>$dt, "n_steps"=>1,
                        "gml"=>$gml, "complex"=>$complex, "verbose"=>$verbose );

# read model
my $model = BNGModel->new();
$model->initialize();
my $err = $model->readFile({"file"=>$modelpath, %readfile_params});
if ($err) { die "some problem reading model file: $err"; }

# load parameters from file
if ($params ne '')
{   
    my $err = $model->readFile({"file"=>$params, %readfile_params});
    if ($err) { die "some problem reading param file: $err"; }
}

# set volume fraction
$err = $model->setParameter("f", $fprof);
if ($err) { die "some problem reading param file: $err"; }

# open output files
my $runhpp_file = "run_${model_prefix}_hpp.bngl";
open(HppFH,">",$runhpp_file) or die "Could not open file $runhpp_file";

# get initial populations
my %population_data = ();
$model->SpeciesList->checkOrInitConcentrations( $model->Concentrations );
foreach my $species (@{$model->SpeciesList->Array})
{
    my $species_idx = $species->Index;
    my $species_pop = $model->Concentrations->[$species_idx-1];
    $species_pop = sprintf "%.0f", $model->ParamList->evaluate($species_pop);
    $population_data{$species_idx} = [$species_pop];
}

# run simulations and collect species populations
print "[auto_hpp.pl] Running NFsim to profile species populations . . .\n";
foreach my $iter (1 .. $nsteps)
{
    $model->simulate_nf( \%simulate_params );
    foreach my $species (@{$model->SpeciesList->Array})
    {
        my $species_idx = $species->Index;
        my $species_pop = $model->Concentrations->[$species_idx-1];
        $species_pop = sprintf "%.0f", $model->ParamList->evaluate($species_pop);
        if (exists $population_data{$species_idx})
        {   push @{$population_data{$species_idx}}, $species_pop;   }
        else
        {   $population_data{$species_idx} = [(0)x($iter), $species_pop];   }
    }
}

# select species for lumping
my $pop_species = select_species_for_lumping(\%population_data, $model->SpeciesList, $threshold*($fprof/$fsim));
printf "[auto_hpp.pl] Found %d species with average population greater than threshold=%.0f\n", scalar @$pop_species, $threshold;
printf "[auto_hpp.pl] Writing HPP script to file '%s' . . .\n", $runhpp_file;

# write run HPP file
my $tend = $dt*$nsteps;
print HppFH qq{# run HPP simulation of ${model_prefix}
version("2.2.4+")
setModelName("${model_prefix}")
readFile({file=>"${modelpath}",allow_actions=>0,skip_action_warn=>1})
};
if ($params ne '')
{  print HppFH "readFile({file=>\"${params}\",allow_actions=>0})\n"; }
print HppFH qq{# configure HPP
begin parameters
    # volume fraction, no units
    f     $fsim
    # population lumping rate, /time
    klump $klump
end parameters
begin population maps
};
foreach my $species_idx (sort {$a <=> $b} @$pop_species)
{
    my $species = $model->SpeciesList->lookup_by_index($species_idx);
    my $species_string = $species->SpeciesGraph->toString();
    printf HppFH "    %s -> P%04d()  klump\n",  $species_string, $species_idx;
}
print HppFH qq{end population maps
generate_hybrid_model({ suffix=>"hpp", overwrite=>1, verbose=>1, execute=>1,\\
                        actions=>["simulate_nf({ t_end=>${tend}, n_steps=>${nsteps} ,gml=>${gml}, complex=>${complex} })"] })
};

# all done
close(HppFH);
printf "[auto_hpp.pl] done.\n";
exit(0);



# pick species for lumping
sub select_species_for_lumping
{
    my ($popdata, $specieslist, $threshold) = @_;
    my $pop_species = [];
    my $avgpops = average_population($popdata);
    while ( my ($species,$avgpop) = each %$avgpops )
    {
        if ($avgpop > $threshold)
        {  push @$pop_species, $species;  }
    }
    return $pop_species;
}


# compute average populations
sub average_population
{
    my ($popdata) = @_;
    my $avgpops = {};
    while ( my ($species,$data) = each %$popdata )
    {  $avgpops->{$species} = sum( @{$popdata->{$species}} ) / @{$popdata->{$species}};  }
    return $avgpops;
}

# display help
sub display_help
{
    # print usage:
    print q{
auto_hpp.pl: profile species populations and write script that runs
HPP simulation where species are lumped if their population exceeded a
threshold during profiling. The output script is named "run_MODEL.bngl".
To generate and execute the HPP model, call "BNG2.pl run_MODEL.bngl".

USAGE: auto_hpp.pl --model FILE [OPTIONS]
       auto_hpp.pl --help

OPTIONS:

  --dt FLOAT        : time step (default=1)
  --fprof FLOAT     : vol. fraction for profiling (default=0.01)
  --fsim FLOAT      : vol. fraction for HPP simulation (default=0.1)
  --gml INT         : global molecule limit (default=10000000)
  --klump FLOAT     : lumping rate constant (default=100000)
  --no-complex      : disable complex bookkeeping
  --nsteps INT      : number of steps (default=20)
  --params FILE     : load parameter values from FILE 
  --threshold FLOAT : population threshold for lumping (default=50)
  --verbose         : enable verbose output

auto_hpp.pl must be able to find BNG modules! Set the environment variable
BNGPATH to the BioNetGen directory.

Note: vol. fraction is set via model parameter "f". If the model does
not implement the parameter, the feature will not work.

};

}

