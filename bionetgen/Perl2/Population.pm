package Population;
# an object for managing population species:
#   maps a species' graph to its representative population molecule
 
# pragmas
use strict;
use warnings;
no warnings 'redefine';

# Perl Modules
use Class::Struct;
use FindBin;
use lib $FindBin::Bin;

# BNG Modules
use BNGUtils;
use Molecule;
use SpeciesGraph;
use Species;
use RxnRule;
use BNGModel;




struct Population =>
{
    SpeciesGraph => 'SpeciesGraph',
    Population   => 'SpeciesGraph',
    MappingRule  => 'RxnRule'
};

##
##

sub newPopulation
{
    # get input arguments
    my ($string, $model, $index) = @_;
    
    # define return arguments
    my $err = undef;
    my $pop = undef;

    # parse population mapping rule
    my $rr;
    ($err, $rr) = RxnRule::newPopulationMappingRule( $string, $model );
    if ($err) {  return $err, $pop;  }
    
    unless ( defined $rr->Name )
    {   # give map rule a name
        $rr->Name( '_MapRule' . $index );
    }
    
    # define population
    $pop = Population->new( SpeciesGraph=>$rr->Reactants->[0], Population=>$rr->Products->[0], MappingRule=>$rr );

    # create species object to go along with this population
    my $spec = Species->new( SpeciesGraph=>$pop->SpeciesGraph, Concentration=>0, Index=>$index, RulesApplied=>0, ObservablesApplied=>0 );
    $pop->SpeciesGraph->Species($spec);

    # return normally
    return ($err, $pop); 
};



1;
