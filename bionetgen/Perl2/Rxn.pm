# $Id: Rxn.pm,v 1.9 2007/07/06 04:46:32 faeder Exp $

package Rxn;

# pragmas
#use strict;
#use warnings;
no warnings 'redefine';

# Perl Modules
use Class::Struct;
use FindBin;
use lib $FindBin::Bin;

# BNG Modules
use BNGUtils;
use SpeciesGraph;
use EnergyPattern;



struct Rxn => {
  Reactants      => '@',         # Array of reactant Species
  Products       => '@',         # Array of product Species
  RateLaw        => 'RateLaw',   # Reference to a RateLaw object
  StatFactor     => '$',	     	# Statistical (multiplicity) factor for multiple rxn pathways
  Priority       => '$',         # Priority of this Rxn (Deprecated?)
  RxnRule        => '$',         # RxnRule that generated this Rxn
  RxnRuleArray   => '@',         # Stores all RxnRules that generate this Rxn (can be more than one)
  Index          => '$',         # Reaction Index for writing network output
  InstanceHash   => '$',		 # Keeps track of instances generated from rules. (#John Sekar)
};


sub toString
{
    my $rxn   = shift @_;
    my $text  = @_ ? shift @_ : 0;
    my $plist = @_ ? shift @_ : undef;
    my $convert_units = @_ ? shift @_ : 1;  # convert from intensive to extensive units (true/false)

    my $err;
    my $string;
  
    if ($text)
    {   # write Reactants and Products as BNGL strings
        my @rstrings=();
        my @pstrings=();
        foreach my $r ( @{$rxn->Reactants} )
        {   push @rstrings, $r->SpeciesGraph->toString();   }
        foreach my $p ( @{$rxn->Products} )
        {   push @pstrings, $p->SpeciesGraph->toString();   }
        $string = join(' + ', @rstrings) . " -> " . join(' + ', @pstrings);
    }
    else
    {   # just write Reactant and Product species indexes
        $string = $rxn->stringID(); 
    }

    $string .= ' ';

    # get the expression for converting intensive units to extensive units (if any)
    my $conv_expr = undef;
    if ($convert_units)
    {  ($conv_expr, $err) = $rxn->get_intensive_to_extensive_units_conversion($BNGModel::GLOBAL_MODEL);  }

    # Write the Ratelaw...
    if (defined $rxn->RateLaw)
    {   $string .= $rxn->RateLaw->toString($rxn->StatFactor, 1, $plist, $conv_expr);   }   

    # append comments
    $string .= " #";

    # write source RxnRule
#    if (defined $rxn->RxnRule) {  $string .= $rxn->RxnRule->Name;  }
	if (defined $rxn->RxnRule)
	{
		my $i = 0;
		foreach my $rr (@{$rxn->RxnRuleArray}){
			if ($i > 0) { $string .= "," }
			$string .= $rr->Name;
			$i++;
		}
	}
	
    # write unit conversions
    if (defined $conv_expr)
    {   $string .= " unit_conversion=" . $conv_expr->toString($plist);   }

    return $string;
}


###
###
###


sub get_intensive_to_extensive_units_conversion
# ($vol_expr, $err) = $rxn->get_intensive_to_extensive_units_conversion($model)
# Get expression that converts intrinsic rate constants to extrinsic units.
#  Returns undefined if the conversion expression is '1'.
#
# for bi-molecular reactions, the reaction compartment is the 3-D volume [V]
# unless all reactants are at a 2-D surface [S].
#
#  rxn type                adjustment
#  ----------------------------------------------------
#   S                      none
#   V                      none
#   S + S                  /S
#   S + V                  /V
#   V + V                  /V 
#   S + S + S              /S/S
#   S + S + V              /S/V
#   S + V + V              /V/V
#   V + V + V              /V/V            etc...
#   0 -> S                 S
#   0 -> V                 V
{
    my ($rxn, $model)  = @_;

    my $err;
    my $conv_expr = undef;

    # get all the defined compartments
    my @reactant_compartments = grep {defined $_} (map {$_->SpeciesGraph->Compartment} @{$rxn->Reactants});
    my @product_compartments  = grep {defined $_} (map {$_->SpeciesGraph->Compartment} @{$rxn->Products});
   
    # return undefined volume expr if there are no compartments
    if ( @reactant_compartments )
    {   # order >=1 reactions
        # divide into surfaces and volumes
        my @surfaces = ( grep {$_->SpatialDimensions==2} @reactant_compartments );
        my @volumes  = ( grep {$_->SpatialDimensions==3} @reactant_compartments );

        # Pick and toss an anchor reactant.  If there's a surface reactant, toss it.
        # Otherwise toss a volume.
        (@surfaces) ? shift @surfaces : shift @volumes;

        # if there are surfaces or volumes remaining, we need to define a volume expression
        if ( @surfaces  or  @volumes )
        {   # order >=2 reactions
            my $number_per_quantity;
            if ( exists $model->Options->{NumberPerQuantityUnit} )
            {   $number_per_quantity = $model->Options->{NumberPerQuantityUnit};   }

            # construct the volume expression
            my @terms = map {$_->Size} (@surfaces, @volumes);
            if (defined $number_per_quantity)
            {   @terms = map { Expression::operate('*', [$number_per_quantity, $_], $model->ParamList) } @terms;   }
            
            $conv_expr = Expression::operate('/', [1, @terms], $model->ParamList);
            unless ( defined $conv_expr )
            {   $err = "RateLaw::get_intensive_to_extensive_units_conversion() - Some problem defining unit conversion expression.";  }
        }
    }
    elsif ( @product_compartments>0 )
    {   # zero-order reactions

        # check if products are in the same compartment
        my $consistent = 1;
        my $comp1 = $product_compartments[0];
        foreach my $comp2 ( @product_compartments[1..$#product_compartments] )
        {
            unless ($comp1 == $comp2)
            {
                $consistent = 0;
                last;
            }
        }
        if ($consistent)
        {   # construct the volume expression
            $conv_expr = $comp1->Size;
        }
        else
        {   send_warning("BioNetGen doesn't know how to handle zero-order synthesis of products in multiple compartments.");  }
    }
    elsif ( @product_compartments=0 )
    {   # zero-order synthesis products that do not have a compartment
		$err = "BioNetGen cannot complete the zero-order synthesis of products if a compartment is not specified.";
    }
    
    # return the expression (possibly undefined) and the error msg (if any).
    return $conv_expr, $err;
}


###
###
###


sub getCVodeName
{
    my $rxn = shift;
    return 'NV_Ith_S(ratelaws,' . $rxn->Index . ')'
}


###
###
###


sub getMatlabName
{
    my $rxn = shift;
    my $offset = 1;
    return 'ratelaws(' . ($rxn->Index + $offset) . ')';
}


###
###
###

sub getCVodeRate
{
    my $rxn   = shift @_;
    my $plist = shift @_;
    my $convert_units = @_ ? shift @_ : 1;  # convert from intensive to extensive units (true/false)


    # get the expression for converting intensive units to extensive units (if any)
    my $conv_expr = undef;
    if ($convert_units)
    {  ($conv_expr, $err) = $rxn->get_intensive_to_extensive_units_conversion($BNGModel::GLOBAL_MODEL);  }

    # get reference to RxnRule RRef hash (TODO: may be obsolete)
    my $rrefs = undef;
    if ( $rxn->RxnRule )
    {   $rrefs = $rxn->RxnRule->RRefs;   }
    # get ratelaw string   
    return $rxn->RateLaw->toCVodeString( $rxn->StatFactor, $rxn->Reactants, $rrefs, $plist, $conv_expr );
}


###
###
###


sub getMatlabRate
{
    my $rxn   = shift @_;
    my $plist = shift @_;
    my $convert_units = @_ ? shift @_ : 1;  # convert from intensive to extensive units (true/false)

    # get the expression for converting intensive units to extensive units (if any)
    my $conv_expr = undef;
    if ($convert_units)
    {  ($conv_expr, $err) = $rxn->get_intensive_to_extensive_units_conversion($BNGModel::GLOBAL_MODEL);  }

    # get reference to RxnRule RRef hash
    my $rrefs = undef;
    if ( $rxn->RxnRule )
    {   $rrefs = $rxn->RxnRule->RRefs;   }
    # get ratelaw string  
    return $rxn->RateLaw->toMatlabString( $rxn->StatFactor, $rxn->Reactants, $rrefs, $plist, $conv_expr );
}


###
###
###

sub getMDLrxn
{
    my $rxn     = shift;
    my $plist   = shift; 
    my $iscomp  = shift; 
    my $py_reactions = shift;
    my $string  = "    ";
    my $py_string = "";
    
    my $mcomp = undef;  
    my %orient; 
    
    if ($iscomp){
         foreach (@{$rxn->Reactants}){
               if ($_->SpeciesGraph->Compartment->SpatialDimensions == 2){ 
	           $mcomp = $_->SpeciesGraph->Compartment; 
	           last; 
	       }
         }
    }
         
    if (defined $mcomp){
        foreach (@{$rxn->Reactants}){
                my $scomp = $_->SpeciesGraph->Compartment; 
	        if ($scomp->SpatialDimensions == 2){
	           $orient{$_->Index} = "'"; 
	           }
	        if ($scomp->SpatialDimensions == 3){
	           $orient{$_->Index} = ($scomp->Outside) ?  ($mcomp == $scomp->Outside ? "," : "'") : "'" ; 
		   }
		      
        foreach (@{$rxn->Products}){
                my $scomp = $_->SpeciesGraph->Compartment; 
	        if ($scomp->SpatialDimensions == 2){
	           $orient{$_->Index} = "'"; 
	           }
	        if ($scomp->SpatialDimensions == 3){
	           $orient{$_->Index} = ($scomp->Outside) ?  ($mcomp == $scomp->Outside ? "," : "'") : "'" ; 
		   }
		}
	    }
         }
   else{
         foreach (@{$rxn->Reactants}){
	         $orient{$_->Index} = "";
	 }
	 foreach (@{$rxn->Products}){
	            $orient{$_->Index} = ""; 
        }
     }
 
    my $reactant_expr = join " + ", map {"s".$_->Index.$orient{$_->Index}} @{$rxn->Reactants};
    my $product_expr = join " + ", map {"s".$_->Index.$orient{$_->Index}} @{$rxn->Products};
    
    $string .= $reactant_expr; 
    $string .= " -> ";
    $string .= $product_expr;
    
    $py_string .= "{\"reactants\":\"".$reactant_expr."\","; 
    $py_string .= "\"products\":\"".$product_expr."\",";
   
   
    # Write the Ratelaw...
    #   First prcoess reaction multipliers (statistical factor, compartment volumes, etc)
    my $err = undef;
    
    #my $rxn_mult_temp=""; 
    #if (scalar @{$rxn->Reactants} >1){
       #my $i = 0; 
       #foreach (@{$rxn->Reactants}){
             #$i = 1 if ($_->SpeciesGraph->Compartment->SpatialDimensions == 3); 
	     #}
      #$rxn_mult_temp = ($i == 1) ? "*Nav" : "/rxn_layer_t";      
      #}
      
   
    my $rxn_mult = undef; 

    # get ratelaw string
    #$string .= sprintf("   [%s$rxn_mult_temp]",$rxn->RateLaw->toString( $rxn_mult, 1, $plist ));
    my $rate_expr = sprintf("%s",$rxn->RateLaw->toString($rxn->StatFactor, 1, $plist)); 
    $string .= "    [".$rate_expr."]"; 
    $py_string .= "\"fwd_rate\":\"".$rate_expr."\"}"; 
    push (@{$py_reactions}, $py_string); 
    if ($rxn->RxnRule)
    {  $string .= "       /* BNG ".$rxn->RxnRule->Name."  */" ;  }

    return $string; 
     
}    


# Used to compare whether reactions are identical
#  (only in terms of species involved)
sub stringID
{
    my $rxn = shift;
    my $string = '';
  
    # Prior to 2.1.7, a reaction with zero reactants or zero products produced an
    # empty string for the respective field. Network2 does not recognize the null
    # string, leading to a parsing error. To resolve this issue, a null reactant (or product)
    # indicated by the index "0" will be output for the reactant (resp. product) field
    # if a reaction has zero reactants (or products).  --Justin, 29oct2010
  
    # get reactant indices
    my @rstrings=();
    if ( @{$rxn->Reactants} )
    {   foreach my $r (@{$rxn->Reactants}) { push @rstrings, $r->Index; }   }
    else
    {   push @rstrings, "0";   }
  
    # get product indices       
    my @pstrings=();
    if ( @{$rxn->Products} )
    {   foreach my $p (@{$rxn->Products}) { push @pstrings, $p->Index; };   }
    else
    {   push @pstrings, "0";   }
  
    # sort reactants and products (if ratelaw is elementary or zero-order)
    my $type= $rxn->RateLaw->Type;
    if ( $type eq "Ele" )
    {   # don't sort MM, Sat, or Hill...  TODO: sort Function ratelaws? (since local context is already evaluated)
        @rstrings = sort {$a<=>$b} @rstrings;
        @pstrings = sort {$a<=>$b} @pstrings;
    }
    
    $string .= join(',', @rstrings) . " " . join(',', @pstrings);
    return $string;
}

###
###
###

1;
