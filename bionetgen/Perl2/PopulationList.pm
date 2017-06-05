package PopulationList;
# a class for managing a list of populations
 
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
use RxnRule;
use BNGModel;
use Population;




struct PopulationList =>
{
    List           => '@',
    MapSpecies     => '%',   # map species labels to population labels (mostly for checking if species is already mapped)
    MapPopulations => '%'    # map population labels to species labels (mostly for checking if population is already target of map)
};


# read string that defines a population mapping and add population to list
sub readString
{
    # get input arguments
    my $pop_list  = shift;
    my $string    = shift;
    my $model     = shift;

    # define return arguments
    my $err;
    my $pop;
  
    # parse string and get population object
    ($err,$pop) = Population::newPopulation( $string, $model, scalar @{$pop_list->List} );
    if ($err) {  return $err;  }
    
  
    # make sure population mapping is unique!
    my $species_label    = $pop->SpeciesGraph->StringExact;
    my $population_label = $pop->Population->StringExact;
    

    # check if this species was already mapped to a population
    if ( exists $pop_list->MapSpecies->{$species_label} )
    {
        $err = "Species with label '$species_label' is already mapped to a population object";
        return $err;
    }
    
    # check if some other species is mapped to this population
    if ( exists $pop_list->MapPopulations->{$population_label} )
    {
        $err = "Population with label '$population_label' is already the target of a mapping";
        return $err;
    }

    # add this population to the list
    push @{$pop_list->List}, $pop;
    $pop_list->MapSpecies->{$species_label} = $population_label;
    $pop_list->MapPopulations->{$population_label} = $species_label;

    # return normally
    return $err;
}



###
###
###



sub getNumPopulations
{
    my $pop_list = shift;
    return scalar @{$pop_list->List};
}

1;
