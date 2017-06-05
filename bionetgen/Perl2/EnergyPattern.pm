package EnergyPattern;
# This class defines an energy pattern which contributes to the overall
#  energy of a Species and influences reaction rates.   --Justin

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
use BNGModel;
use SpeciesGraph;



struct EnergyPattern => 
{		   
    Pattern => 'SpeciesGraph',  # pattern graph
    Gf      => 'Expression',    # free-energy parameter (per match)
    Weights => '@'              # number of matches to each species
};


###
###
###


sub readString
{
    my ($epatt, $string, $model) = @_;

    my $err;

	# strip any leading whitesace
    $string =~ s/^\s+//;

    # Check if first token is an index (This index will be ignored)
    # DEPRECATED as of BNG 2.2.6
    if($string =~ s/^\s*\d+\s+//)
    {
    		return "Leading index detected at '$string'. This is deprecated as of BNG 2.2.6.";
    }

    # Remove leading label, if exists
    if ($string =~ s/^\s*(\w+)\s*:\s+//)
    {
	    # Check label for leading number
		my $label = $1;
		if ($label =~ /^\d/) {  return "Syntax error (label begins with a number) at '$label'";  }
    }
    
	# Check name for leading number
	my $string_left = $string;
	unless ( $string_left =~ s/^([A-Za-z_]\w*)// )
	{ 
		return "Syntax error (pattern name begins with a number) at $string.";
	}
    
    # Next read the SpeciesGraph that will define the Energy Pattern
    my $sep = '^\s+';
    my $sg = SpeciesGraph->new();
    $err = $sg->readString( \$string, $model->CompartmentList, 0, $sep, $model->MoleculeTypesList, 1 );
    if ($err) { return "While reading Energy Pattern: $err"; }

    $epatt->Pattern($sg);

    # Look for free-energy of formation expression (Gf)
    #  and construct equillibrium factor
    my $Gf_expr = Expression->new();
    if ( $string =~ /\S+/ )
    {
        # Read expression
        $err = $Gf_expr->readString( \$string, $model->ParamList );
        if ($err) { return undef, $err; }
    }
    else
    {
        $err = sprintf "Missing free-energy token for energy pattern %s", $epatt->Pattern->toString();
        return $err;
    }
    # set Keq expression
    $epatt->Gf($Gf_expr);
    
    # set Weights and RxnStoich to empty array
    $epatt->Weights([]);

    # return with error (if defined)
    return $err;
}



sub toString
{
    my $epatt = shift;
    my $plist = (@_) ? shift : undef;
    my $string = '';

    $string .=  $epatt->Pattern->toString() . "  "
              . $epatt->Gf->toString($plist);

    return $string;
}



sub toMatlabString
{
   my $epatt = shift;
   my $string = '';
   
   # TODO
     
   return $string, '';
}



sub toMexString
{
    my $epatt = shift;
    my $string = '';
   
    # TODO
   
    return $string, '';
}



sub toXML
{
    my $epatt  = shift;
    my $indent = shift;
    my $index  = shift;
    
    my $string = '';
    
    # TODO
    
    return $string, '';
}



sub toMathMLString
{
        my $epatt  = shift;
    my $string = '';

    # TODO

    return $string, '';
}


# reset the observable weights to zero
sub reset_weights
{
    my $epatt   = shift @_;
    my $alloc = @_ ? shift @_ : 0;
    $epatt->Weights( [(0) x ($alloc+1)] );
}



sub update
{
    my $epatt   = shift @_;
    my $species = shift @_;
    my $idx_start = @_ ? shift @_ : 0;

    my $err = undef;
    for ( my $ii = $idx_start; $ii < @$species; ++$ii )
    {
        my $spec = $species->[$ii];
        next if ($spec->ObservablesApplied);
        my @matches = $epatt->Pattern->isomorphicToSubgraph($spec->SpeciesGraph);
        $epatt->Weights->[$spec->Index] = (scalar @matches)/($epatt->Pattern->Automorphisms);
    }
    return $err;
}



sub getStoich
# returns an integer value corresponding to the stoichiometry of
#  this pattern w.r.t. a given reaction.
{
    my ($epatt, $rxn) = @_;
    
    my $err;
    my $stoich = 0;
    foreach my $reactant (@{$rxn->Reactants})
    {
        if ( $epatt->Weights->[$reactant->Index] )
        {   $stoich -= $epatt->Weights->[$reactant->Index];   }
    }
    foreach my $product (@{$rxn->Products})
    {
        if ( $epatt->Weights->[$product->Index] )
        {   $stoich += $epatt->Weights->[$product->Index];   }
    }
    return $stoich, $err;
}



sub getDeltaEnergy
{
    my ($epatt, $rxn, $plist) = @_;
    
    my $err;
    my $stoich;
 
    # get stoichiometry of this pattern w.r.t. this rxn
    ($stoich, $err) = $epatt->getStoich($rxn);
    if ( $err ) { return undef, $err; }
    
    # construct a rate constant expression
    my $rate_expr = undef;
    if ( $stoich!=0 )
    {
        if ( $stoich == 1 )
        {
            $rate_expr = $epatt->Gf;
        }
        elsif ( $stoich == -1 )
        {
            $rate_expr = Expression::operate("-", [$epatt->Gf], $plist);    
        }
        else
        {
            $rate_expr = Expression::operate("*", [$stoich, $epatt->Gf], $plist);
        }
    }
    
    return $rate_expr, $err;
}

###
###
###

1;
