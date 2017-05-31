# $Id: RxnList.pm,v 1.6 2006/09/28 02:21:03 faeder Exp $

package RxnList;

# pragmas
use strict;
use warnings;
no warnings 'redefine';

# Perl Modules
use Class::Struct;
use FindBin;
use lib $FindBin::Bin;

# BNG Modules
use Rxn;
use SpeciesList;
use BNGUtils;

# This class manages a list of Rxn objects
struct RxnList => {
  Array       => '@',
  Hash        => '%',
  SpeciesList => 'SpeciesList',
  AsProduct   => '%'             # Number of times a species appears as product,
                                 # key is pointer to Species
};


###
###
###


# reset the rxn hash (i.e. forget about rxns that are in the array)
sub resetHash
{
    my $rlist = shift;
    undef %{ $rlist->Hash };
}


# get the size of the rxn list
sub size
{
    my $rlist = shift @_;
    return scalar @{$rlist->Array};
}


###
###
###



# Add a reaction to the list.
#  Returns the number of added reactions.
sub add
{
    my $rlist    = shift @_;
    my $rxn      = shift @_;
    my $add_zero = @_ ? shift @_ : 0;
    my $plist    = @_ ? shift @_ : undef;

    my $n_add   = 0;
    my $add_rxn = 1;

    # Modify the string returned by this call to affect when reactions are combined.
    my $rstring = $rxn->stringID();
    my ($r, $p) = split ' ', $rstring;

    # Check for identical reactants and products
    if ( $r eq $p )
    {   # null rxn, don't add to the list
        $add_rxn = 0;
    }
    elsif ( exists $rlist->Hash->{$rstring} )
    {
        foreach my $rxn2 ( @{ $rlist->Hash->{$rstring} } )
        {     
            # NOTE: This algorithm guarantees that all rxns on list have same priority.
            if ( $rxn->Priority == $rxn2->Priority )
            {
                # Reaction with same rate law as previous reaction is combined with it
                # TODO: this may be obsolete after implementing ratelaw hashing..
                if ( RateLaw::equivalent($rxn->RateLaw, $rxn2->RateLaw, $plist) )
                {
                	    $rxn2->StatFactor( $rxn2->StatFactor + $rxn->StatFactor );
                	    $add_rxn = 0;
						
						# For write_autos output (see BNG2.pl user options) - John Sekar 
						if(defined $BNGModel::GLOBAL_MODEL->Params->{'write_autos'}==1)
						{
						if($BNGModel::GLOBAL_MODEL->Params->{'write_autos'}==1) 
							{
							++$rxn2->InstanceHash->{$rxn2->RxnRule->Name};
							}
                	    }
                	    # Check if different rules are generating the same rxn 
                	   	if ($rxn->RxnRule != $rxn2->RxnRule){
                	   		# Add ref to new RxnRule to array of RxnRules
	                   	push @{$rxn2->RxnRuleArray}, @{$rxn->RxnRuleArray}; 
	                    	# Send a warning that a duplicate reaction was generated from a different rule
	                    	my $msg = "Duplicate of rxn " . $rxn2->Index . " detected. Rules generating this rxn are ";
	                    	my $i = 0;
	                    foreach $r (@{$rxn2->RxnRuleArray}){
	                    		if ($i > 0){ $msg .= ", "; }
	                    		$msg .= $r->Name;
	                    		if ($i == $#{$rxn2->RxnRuleArray}){ $msg .= "."; }
	                    		$i++;
	                    }
	                    send_warning($msg);
                	   	}
					
                    # Need to delete reaction and ratelaw? 
                    #  (if the ratelaws references are different and the rules are the same,
                    #   then we can safely delete the extra Ratelaw copy.  This is useful
                    #   for energy BNG where we derive new ratelaws from general rates, but often
                    #   the same derived law works for many reactions. Deleting redundant derived laws
                    #   allows us to save space.)
                    if ( ($rxn->RateLaw != $rxn2->RateLaw) and ($rxn->RxnRule == $rxn2->RxnRule) )
                    {
                        if ( defined $plist )
                        {
                            # delete parameters associated with this ratelaw
                            foreach my $const ( @{$rxn->RateLaw->Constants} )
                            {   $plist->deleteParam( $const );   }
                        }
                        
                        # undefine the ratelaw
                        undef %{$rxn->RateLaw};
                        
                        # set this rxn ratelaw equal to rxn2 ratelaw
                        #  (in practice, we don't need rxn anymore, so this is moot)
                        $rxn->RateLaw( $rxn2->RateLaw );
                    }
                    
                    # Exit from loop since rxn is now handled.
                    last;
                }
            }
            elsif ( $rxn->Priority < $rxn2->Priority )
            {
                # New reaction has lower priority so it's not added and we're done
                $add_rxn = 0;
                last;
            }
            elsif ( $rxn->Priority > $rxn2->Priority )
            {
                # New reaction has higher priority, causing previous reaction to be deleted
                # NOTE: All reactions with the lower priority will be deleted by looping over $rxn2
                # Find and delete old entry from Array
                $rlist->remove($rxn2);
                --$n_add;
            }
        } # END loop over previous reactions
    }

    # Add new entry
    if ($add_rxn)
    {
		# For write_autos output (see BNG2.pl user options) - John Sekar 
		if (defined $BNGModel::GLOBAL_MODEL->Params->{'write_autos'}==1)
		{
		if($BNGModel::GLOBAL_MODEL->Params->{'write_autos'}==1)
			{
			++$rxn->InstanceHash->{$rxn->RxnRule->Name};
			}
		}
		
        push @{ $rlist->Array }, $rxn;
        $rxn->Index(scalar @{$rlist->Array});
        push @{ $rlist->Hash->{$rstring} }, $rxn;
        foreach my $spec ( @{$rxn->Products} )
        {   ++($rlist->AsProduct->{$spec});   }
        ++$n_add;
    }
  
    return ($n_add);
}


###
###
###


# remove a reaction from the list
sub remove
{
    my $rlist = shift;
    my $rxn   = shift;

    # Remove entry from Array
    foreach my $i ( 0 .. $#{$rlist->Array} )
    {
        if ( $rxn == $rlist->Array->[$i] )
        {
            printf "Deleting rxn %s\n", $rxn->toString();
            splice( @{$rlist->Array}, $i, 1 );
            last;
        }
    }

    # Remove entry from Hash
    my $harray = $rlist->Hash->{ $rxn->stringID() };
    foreach my $i ( 0 .. $#{$harray} )
    {
        if ( $rxn == $harray->[$i] )
        {
            #printf "Deleting rxn from hash %s\n", $rxn->toString();
            splice( @$harray, $i, 1 );
            last;
        }
    }

    # Delete species that depend only on this reaction for production
    # Species with non-zero concentration must exist
    # Species with zero concentration must appear as product in at least one reaction
    #  *phash = $rlist->AsProduct;
    #  for my $spec ( @{ $rxn->Products } ) {
    #    if ( ( --$phash{$spec} ) == 0 ) {
    #
    #      # Remove species from SpeciesList if it has zero concentration
    #      $rlist->SpeciesList->remove($spec);
    #    }
    #  }

    return;
}


###
###
###


# read a reaction from a string and add it to the list
sub readString
{
    my $rlist  = shift;
    my $string = shift;
    my $slist  = shift;
    my $plist  = shift;

    my @tokens = split( ' ', $string );
    my $rxn = Rxn->new();
    my $err;

    my $nspec = scalar @{$slist->Array};

    # Check if token is an index
    if ( $tokens[0] =~ /^\d+$/ ) {
        my $index = shift @tokens;    # This index will be ignored
    }

    # Next token is list of reactant indices
    my @ptrs;
    if (@tokens)
    {
        @ptrs = ();
        my @inds = split( ',', shift(@tokens) );
        foreach my $index (@inds)
        {
            # check for bad index            
            if ( $index < 0 || ($index > $nspec) )
            {
                return ("Index $index to species in reaction out of range");
            }

            # NOTE: zero is the null species, so ignore indices less than 1
            unless ( $index==0 ) 
            {   push @ptrs, $slist->Array->[$index - 1];   }
        }
    }
    else
    {
        return ("Incomplete reactantion at reactants");
    }
    $rxn->Reactants( [@ptrs] );


    # Next token is list of product indices
    if (@tokens)
    {
        @ptrs = ();
        my @inds = split( ',', shift(@tokens) );
        foreach my $index (@inds)
        {
            # check for bad index
            if ( $index < 0 || ($index > $nspec) )
            {
                return ("Index $index to species in reaction out of range");
            }        
        
            # NOTE: zero is the null species, so ignore indices less than 1
            unless ( $index==0 )                     
            {   push @ptrs, $slist->Array->[$index - 1];   }

        }
    }
    else
    {
        return ( "Incomplete reaction at products" );
    }
    $rxn->Products( [@ptrs] );

    # Next token is rate law
    # This will create a separate RateLaw object for each reaction.  When reaction
    # rules are used to generate Rxns, only one RateLaw is used per RxnRule.
    # Information about which rule created the reacion is lost because of this.  Also,
    # the statistical factor gets folded into the rate law rather than being part of the
    # reaction, since it is not possible to separate the contributions to the overall
    # weight.
    my $rl;
    if (@tokens)
    {
        my $rlstring = join( ' ', @tokens );

        #print "rlstring=$rlstring\n";
        ( $rl, $err ) = RateLaw::newRateLawNet( \$rlstring, $plist );
        if ($err) { return ($err); }
    }
    else
    {
        return ("Incomplete reaction at rate law");
    }
    $rxn->RateLaw($rl);

    # Set remaining attributes of rxn
    $rxn->StatFactor(1);
    $rxn->Priority(0);

    # Create new Rxn entry in RxnList
    my $n_add = $rlist->add($rxn,0,$plist);

    return ('');
}


###
###
###


# write reaction list to a string formatted for a BNGL .NET output file
sub writeBNGL
{
    my $rlist  = shift @_;
    my $params = @_ ? shift @_ : { 'TextReaction'=>0, 'convert_intensive_to_extensive_units'=>1 };
    my $plist  = @_ ? shift : undef;

    my $out    = '';

	# write non-text reactions
	$out .= "begin reactions\n";
    my $irxn = 1;
    foreach my $rxn ( @{ $rlist->Array } )
    {
        $out .= sprintf "%5d %s\n", $irxn, $rxn->toString( 0, $plist, $params->{'convert_intensive_to_extensive_units'} );
        ++$irxn;
    }
	$out .= "end reactions\n";

    # write reactions as text?
    my $text = $params->{TextReaction};

	if ($text){
	    $out .= "begin reactions_text\n";
	    my $irxn = 1;
	    foreach my $rxn ( @{ $rlist->Array } )
	    {
	        $out .= sprintf "%5d %s\n", $irxn, $rxn->toString( $text, $plist, $params->{'convert_intensive_to_extensive_units'} );
	        ++$irxn;
	    }
	    $out .= "end reactions_text\n";
	}
    
    return $out;
}


###
###
###

sub writeMDL
{
     my $rlist = shift;
     my $plist = shift; 
     my $iscomp = shift; 
     my $py_reactions = shift; 
     
     my $string = "DEFINE_REACTIONS\n{\n"; 
     foreach my $rxn (@{$rlist->Array})
     {
        $string .= $rxn->getMDLrxn($plist, $iscomp, $py_reactions)."\n";  
      }
     $string .= "}"; 
     return $string; 
}


# print rxns to a file
sub print
{
    my $rlist   = shift;
    my $fh      = shift;
    my $i_start = (@_) ? shift : 0;

    print $fh "begin reactions\n";
    my $i = $i_start;
    while ( $i < @{$rlist->Array} )
    {
        my $rxn = $rlist->Array->[$i];
        printf $fh "%5d %s\n", $i-$i_start+1, $rxn->toString();
        ++$i;
    }
    print $fh "end reactions\n";
    return ('');
}


###
###
###


# index reactions. BNG doesn't use this, but it's useful for writing
#  vector output
sub updateIndex
{
    my $rlist = shift;
    my $plist = (@_) ? shift : undef;
    
    my $err;  
    my $n_rxns = 0;
    foreach my $rxn ( @{$rlist->Array} )
    {
        $rxn->Index( $n_rxns );
        ++$n_rxns;
    }

    return $err;
}


###
###
###


# return a string with CVode reaction rate defintions for
# each reaction in the list.
sub getCVodeRateDefs
{
    my $rlist = shift @_;
    my $plist = @_ ? shift @_ : undef;

    # expression definition string
    my $rate_defs = '';
    # to hold errors..
    my $err;   
    # size of the indent
    my $indent = '    ';
    
    # loop through reactions and generate rate desfinitions
    foreach my $rxn ( @{ $rlist->Array } )
    {
        $rate_defs .= $indent . $rxn->getCVodeName() . ' = ' .  $rxn->getCVodeRate($plist) . ";\n";
    }

    return ($rate_defs, $err);
}


###
###
###


# return a string with Matlab reaction rate defintions for
# each reaction in the list.
sub getMatlabRateDefs
{
    my $rlist = shift @_;
    my $plist = @_ ? shift @_ : undef;

    # expression definition string
    my $rate_defs = '';
    # to hold errors..
    my $err;   
    # size of the indent
    my $indent = '    ';
    
    # loop through reactions and generate rate desfinitions
    my $i_rxn = 1;
    foreach my $rxn ( @{ $rlist->Array } )
    {
        $rate_defs .= $indent . $rxn->getMatlabName() . ' = ' .  $rxn->getMatlabRate($plist) . ";\n";
        ++$i_rxn;
    }

    return ($rate_defs, $err);
}


###
###
###


# construct a sparsely encoded stoichiometry matrix
sub calcStoichMatrix
{
    my $rlist       = shift;
    my $stoich_hash = shift;

    my $err;

	my @fluxes = ();
	my $i_rxn = 1;
	foreach my $rxn ( @{ $rlist->Array } )
	{
		# Each reactant contributes a -1
		foreach my $reactant ( @{ $rxn->Reactants } )
		{   --( $stoich_hash->{ $reactant->Index }{$i_rxn} );   }

		# Each product contributes a +1
		foreach my $product ( @{ $rxn->Products } )
		{   ++( $stoich_hash->{  $product->Index }{$i_rxn} );   }
		
		++$i_rxn;
	}

    return ($err);
}


###
###
###


# Need join function to merge two lists.  Could make use of reaction
# precedence here if the rate law was suppressed from the string used to
# compare reactions

1;
