# A container class for Species objects
package SpeciesList;

# pragmas
use strict;
use warnings;

# Perl Modules
use Class::Struct;

# BNG Modules
use BNGUtils;
use Species;
use SpeciesGraph;
use MoleculeTypesList;
use ParamList;


# Members
struct SpeciesList =>
{
    Array      => '@',  # array of pointers to species
    Hash       => '%',  # a hash map from species' StringID to species pointers
    Hash_exact => '%'   # a hash map from species' StringExact to species pointers
};



###
###
###


sub getNumSpecies
{
    my $slist = shift;
    return scalar @{$slist->Array};
}

sub size
{
    my $slist = shift @_;
    return scalar @{$slist->Array};
}


###
###
###


sub sort
{
    my $slist = shift;

    print "Sorting species list\n";
    my @newarr = sort {$a->SpeciesGraph->StringExact cmp $b->SpeciesGraph->StringExact} @{$slist->Array};
    $slist->Array(\@newarr);
    my $ispec = 1;
    foreach my $spec ( @{$slist->Array} )
    {
        $spec->Index($ispec);
        ++$ispec;
    }

    return $slist;
}



###
###
###



# Returns pointer to matching species in $slist or undef if no match found
sub lookup
{
    my $slist = shift @_;
    my $sg = shift @_;
    my $check_iso = @_ ? shift @_ : 1;

    if( $sg->IsCanonical ) {  $check_iso = 0;  }

    my $spec = undef;
    my $sstring = $sg->StringID;
    if ( exists $slist->Hash->{$sstring} )
    {
        # Since string is not completely canonical, need to check isomorphism with other list members
        # Determine whether the graph is isomorphic to any on the current list
        if ($check_iso)
        {
            foreach my $spec2 ( @{$slist->Hash->{$sstring}} )
            {
	            if ($sg->isomorphicTo($spec2->SpeciesGraph))
	            {
                    $spec = $spec2;
                    last;
                }
            }
        }
        else
        {
            $spec = $slist->Hash->{$sstring}->[0];
        }
    }
    return $spec;
}

# Look up species by StringExact, returns pointer to species or undef if not found.
sub lookup_bystring
{
    my $slist   = shift @_;
    my $sstring = shift @_;

    my $species = undef;
    if ( exists $slist->Hash_exact->{$sstring} )
    {
        $species = $slist->Hash_exact->{$sstring};
    }
    return $species;
}

# Look up species by Index, returns pointer to species or undef if not found.
sub lookup_by_index
{
    my ($slist, $idx) = @_;
    my $species = undef;
    if ( exists $slist->Array->[$idx-1] and defined $slist->Array->[$idx-1] )
    {  $species = $slist->Array->[$idx-1];  }
    return $species;
}


# Returns reference to Species object either newly created or found in $slist
# Should check if species already exists in list before adding
sub add
{
    my $slist = shift @_;
    my $sg    = shift @_;
    my $conc  = @_ ? shift @_ : 0;

    # Create new species from SpeciesGraph
    my $spec = Species->new;
    push @{$slist->Array}, $spec;
    push @{$slist->Hash->{$sg->StringID}}, $spec;
     # Can only be one entry. TODO: generate exit_error if there is a clash!
    $slist->Hash_exact->{$sg->StringExact} = $spec;
    $spec->SpeciesGraph($sg);
    $spec->Concentration($conc);
    $spec->Index( scalar @{$slist->Array} );
    $spec->RulesApplied(0);
    # Put ref to species in SpeciesGraph to bind it
    $sg->Species($spec);

    return $spec;
}

sub remove
{
    my $slist = shift @_;
    my $spec  = shift @_;

    # Remove from Array
    splice( @{$slist->Array}, $spec->Index-1, 1 );

    # Remove from Hash 
    my $harray = $slist->Hash->{$spec->SpeciesGraph->StringID};
    foreach my $i (0..$#$harray)
    {
        if ($spec == $harray->[$i])
        {
            splice( @$harray, $i, 1 );
            unless (@$harray)
            {
                undef $slist->Hash->{$spec->SpeciesGraph->StringID};
            }
            last;
        }
    }

    # Remove from Hash_exact
    undef $slist->Hash_exact->{$spec->SpeciesGraph->StringExact};

    return;
}



# Read entry from input species block
sub readString
{
    my $slist  = shift @_;
    my $string = shift @_;
    my $plist  = shift @_;
    my $clist  = shift @_;
    my $mtlist = shift @_;

    my ($conc, $sg, $err);

    my $name = '';

    # remove leading index
    $string =~ s/^\s*\d+\s+//; # Can't deprecate this because indices used in NET files
    
    # Remove leading label, if any
	if ($string =~ s/^\s*(\w+)\s*:\s+//)
	{
		# Check label for leading number
		my $label = $1;
		if ($label =~ /^\d/) {  return "Syntax error (label begins with a number) at '$label'.";  }
	}
	
    # Read species string
    $sg = SpeciesGraph->new;
    $string =~ s/^\s+//;
    $err = $sg->readString( \$string, $clist, 1, '^\s+', $mtlist );
    if ($err) { return $err; }

    if ( $sg->isNull() )
    {   # this is the null pattern
        send_warning( "Found useless instance of null pattern in SpeciesList" );
        return '';
    }

    # Check if isomorphic to existing species
    my $existing= $slist->lookup($sg);
    if ($existing)
    {
        my $sstring = $sg->StringExact;
        my $index = $existing->Index;
        return "Species $sstring isomorphic to previously defined species index $index";
    }

    # Read species concentration as math expression.
    # Set species concentration to number or variable name.
    if ( $string =~ /\S+/ )
    {
        # Read expression
        my $expr = Expression->new();
        if ( my $err = $expr->readString(\$string, $plist) ) {  return ('', $err);  }
        if ( $expr->Type eq 'NUM' )
        {
            $conc = $expr->evaluate();
        }
        else
        {
            $conc = $expr->getName( $plist, '_InitialConc' );
        }
    }
    else
    {
        return "Species is missing expression for population or concentration";
    }

    # Create new Species entry in SpeciesList
    $slist->add($sg, $conc);
  
    return '';
}



###
###
###


# string = $slist->writeBNGL( *Array, [*ParamList, bool, *Hash] ) 
sub writeBNGL
{
    my $slist       = shift @_;
    my $conc        = @_ ? shift @_ : [];
    my $plist       = @_ ? shift @_ : undef;
    my $user_params = @_ ? shift @_ : { 'TextSpecies'=>0, 'pretty_formatting'=>0 };

    # this method loads @$conc with initial conditions (if @$conc is empty)
    #  OR checks that @$conc is okay (if @$conc has elements)
    $slist->checkOrInitConcentrations($conc);

    ## Determine length of longest species string
    #my $maxlen = 0;
    #foreach my $spec ( @{$slist->Array} )
    #{
    #    my $len = 1;
    #    $len += defined $spec->SpeciesGraph->Name        ? length $spec->SpeciesGraph->Name        : 0;
    #    $len += defined $spec->SpeciesGraph->StringExact ? length $spec->SpeciesGraph->StringExact : 0;
    #    $maxlen = ($len > $maxlen) ? $len : $maxlen;
    #}

    my $out .= "begin species\n";
    foreach my $spec ( @{$slist->Array} )
    {
        if ( $user_params->{'pretty_formatting'} )
        {   # no species index, ident
            $out .= '  ';
        }
        else
        {   # include index
            $out .= sprintf "%5d ", $spec->Index;
        }

        # get species graph string
#        my $sexact = $spec->SpeciesGraph->toString();
		if ($user_params->{'TextSpecies'}){
			$out .= sprintf "%s", $spec->SpeciesGraph->toString();
		}
		else{
			$out .= sprintf "S%d", $spec->Index;
		}
        #$out .= sprintf "%-${maxlen}s", $sexact;
#        $out .= sprintf "%s", $sexact;
        
        my $c = $conc->[ $spec->Index - 1 ];
        $out .= sprintf " %s\n", $c;
    }  
    $out .= "end species\n";
    
    return $out;
}



###
###
###



sub writeSSC
{
    my $slist = shift @_;
    my $conc  = @_ ? shift @_ : [];
    my $plist = @_ ? shift @_ : undef;
    my $print_names = @_ ? shift : 0;

    my $string = '';

    # this method loads @$conc with initial conditions (if @$conc is empty)
    #  OR checks that @$conc is okay (if @$conc has elements)
    $slist->checkOrInitConcentrations($conc);

    my $idx=0;
    foreach my $spec (@{$slist->Array})
    {
        my $sname;
        my $sexact= $spec->SpeciesGraph->toStringSSCMol();
        $sname=$sexact;
        $string .= "new $sname at ";
        my $c;
        $c = $conc->[$idx];
        $string .= $c;
        $string.= "\n";
        ++$idx;
    }

    return $string;
}


sub print
{
    my $slist = shift @_;
    my $fh = shift @_;
    my $i_start = @_ ? shift : 0;

    print $fh "begin species\n";
    foreach my $i ( $i_start..$#{$slist->Array} )
    {
        my $spec = $slist->Array->[$i];
        printf $fh "%5d %s %s\n", $i-$i_start+1, $spec->SpeciesGraph->StringExact, $spec->Concentration;
    }
    print $fh "end species\n";
    return '';
}

sub toXML
{
    my $slist  = shift @_;
    my $indent = @_ ? shift @_ : "";
    # Use concentration array if provided
    my $conc   = @_ ? shift @_ : [];

    # this method loads @$conc with initial conditions (if @$conc is empty)
    #  OR checks that @$conc is okay (if @$conc has elements)
    $slist->checkOrInitConcentrations($conc);

    my $string = $indent . "<ListOfSpecies>\n";

    my $i=0;
    foreach my $spec (@{$slist->Array})
    {
        $string.= $spec->toXML("  ".$indent, $conc->[$i]);
        ++$i;
    }

    $string .= $indent."</ListOfSpecies>\n";
    return $string;
}


###
###
###
sub writeMDL
{
    my $slist  = shift @_; 
    my $model  = shift @_; 
    my $indent = shift @_; 
    my $py_species = shift @_;
    my $py_param = shift @_; 
	  
    my $string = ""; 
    my $tpy_param = {}; 
    my @tpy_keys = ('name','value','unit','type');
    # Define diffusion constants for compartments
    $string .= $indent."T = 298.15      /* Temperature, K */\n";
    $tpy_param = {'name'=>"T",'value'=>"298.25",'unit'=>"K",'type'=>""}; 
    push(@{$py_param},"{".join(",",map("\"$_\"".":\"".$tpy_param->{$_}."\"", @tpy_keys))."}");
   
    if (@{$model->CompartmentList->Array}) {
        $string .= $indent."h = rxn_layer_t      /* Thickness of 2D compartment, um */\n";
        $tpy_param = {'name'=>"h",'value'=>"rxn_layer_t",'unit'=>"um",'type'=>""}; 
        push(@{$py_param},"{".join(",",map("\"$_\"".":\"".$tpy_param->{$_}."\"", @tpy_keys))."}");       
        }
    $string .= $indent."Rs = 0.002564      /* Radius of a (spherical) molecule in 3D compartment, um */\n";
    $tpy_param = {'name'=>"Rs",'value'=>"0.002564",'unit'=>"um",'type'=>""}; 
    push(@{$py_param},"{".join(",",map("\"$_\"".":\"".$tpy_param->{$_}."\"", @tpy_keys))."}");
 
    if (@{$model->CompartmentList->Array}) {
        $string .= $indent."Rc = 0.0015      /* Radius of a (cylindrical) molecule in 2D compartment, um */\n";
        $tpy_param = {'name'=>"Rc",'value'=>"0.0015",'unit'=>"um",'type'=>""}; 
        push(@{$py_param},"{".join(",",map("\"$_\"".":\"".$tpy_param->{$_}."\"", @tpy_keys))."}");
        }

    $string .= $indent."gamma = 0.5722      /* Euler's constant */\n";
    $tpy_param = {'name'=>"gamma",'value'=>"0.5722",'unit'=>"",'type'=>"Euler's constant"}; 
    push(@{$py_param},"{".join(",",map("\"$_\"".":\"".$tpy_param->{$_}."\"", @tpy_keys))."}");
    $string .= $indent."KB = 1.3806488e-19     /* Boltzmann constant, cm^2.kg/K.s^2 */\n"; 
    $tpy_param = {'name'=>"KB",'value'=>"1.3806488e-19",'unit'=>"cm^2.kg/K.s^2",'type'=>"Boltzmann constant"}; 
    push(@{$py_param},"{".join(",",map("\"$_\"".":\"".$tpy_param->{$_}."\"", @tpy_keys))."}");
  
    my %difconst = (); 
    my %difexp   = (); 	  
    if (@{$model->CompartmentList->Array}){
          foreach my $comp (@{$model->CompartmentList->Array}){
              if ($comp->SpatialDimensions == 3){
	          $difexp{$comp} = "KB*T/(6*PI*mu_".$comp->Name."*Rsp)" ; 
	      }
	      
              	      
	      if ($comp->SpatialDimensions == 2){
	          my $vi = "";
	          my $vo = ""; 
		  my $vavg = ""; 

	          if (@{$comp->Inside}){
		     foreach (@{$comp->Inside}){
		          if ($_->Outside == $comp){
			      $vi = "mu_".$_->Name; 
			      last;
			      } 
		     }
		  }
		  
		  if ($comp->Outside){
		      $vo = "mu_".$comp->Outside->Name;
		      }
		  
		  if (($vi eq "")&&($vo eq "")){
		     $vavg = "mu_".$comp->Name; 
		     }
		  elsif (($vi eq "")&&($vo ne "")){
		     $vavg = $vo;
		     }
		  elsif (($vi ne "")&&($vo eq "")){
		     $vavg = $vi;
		     }
		  else{
		     $vavg = "(".$vo."+".$vi.")/2"; 
		     }  
		  
	          $difexp{$comp} = "KB*T*LOG((mu_".$comp->Name."*h/(Rsp*".$vavg."))-gamma)/(4*PI*mu_".$comp->Name."*h)";
	      } 
	      
              my $tstring = "$indent"."mu_".$comp->Name;  
	      $string .= $tstring." = 1e-9      /* Viscosity in compartment ".$comp->Name.", kg/um.s */\n"; 
	      $tpy_param = {'name'=>"mu_".$comp->Name,'value'=>"1e-9",'unit'=>"kg/um.s",'type'=>"viscosity"}; 
              push(@{$py_param},"{".join(",",map("\"$_\"".":\"".$tpy_param->{$_}."\"", @tpy_keys))."}");
	      }
         }      
    else {
         $string .= $indent."mu = 1e-9\n"; # Default viscosity 
         }
		      
    $difconst{"DEFAULT"} = "DIF"; 
    $difexp{"DEFAULT"} = "KB*T/(6*PI*mu"."*Rsp)"; 
   
    $string .= "\nDEFINE_MOLECULES\n{\n"; 
    foreach my $spec (@{$slist->Array}){
	 $string .= $indent.$spec->writeMDL(\%difexp,$py_species,$py_param);
         }
    
    $string .= "}\n";
    return $string;
}
###
###
###


sub toCVodeString
{
    my $slist       = shift;
    my $rlist       = shift;
    my $stoich_hash = shift;
    my $plist       = (@_) ? shift : undef;

    my $deriv_defs = '';
    my $indent = '    ';
    my $err;

    # construct derivative definition for each species
    foreach my $species ( @{ $slist->Array } )
    {
        # get species vector in stoich hash
        my $species_vector = $stoich_hash->{ $species->Index };
        my $species_deriv = '';
        
        if ( $species->SpeciesGraph->Fixed )
        {   # handle species with fixed population
            $species_deriv = 0.0;
        }
        else
        {   # handle all other species...
            # add rates and stoich for each reaction that influences this speices
            foreach my $i_rxn ( sort { $a <=> $b } keys %$species_vector )
            {
                # get species stoichiometry under this reaction
                my $stoich = $species_vector->{$i_rxn};
                
                # look up reaction object
                my $i_rxn0 = $i_rxn - 1;
                my $rxn = $rlist->Array->[$i_rxn0];
                
                # add this reaction flux to the species derivative
                if    ( $stoich == 1 )
                {   $species_deriv .= " +" . $rxn->getCVodeName();             }
                elsif ( $stoich == 0 )
                {                                                              }
                elsif ( $stoich == -1 )
                {   $species_deriv .= " -" . $rxn->getCVodeName();             }
                elsif ( $stoich > 0 )
                {   $species_deriv .= " +$stoich.0*" . $rxn->getCVodeName();   }
                elsif ( $stoich < 0 )
                {   $species_deriv .= " $stoich.0*" . $rxn->getCVodeName();    } 
            } 

            # trim leading " +"
            $species_deriv =~ s/^ \+?//;
        
            # replace empty string with a zero rate
            if ($species_deriv eq '')
            {   $species_deriv = '0.0';   }
        }
            
        # add derivative to list of definitions
        $deriv_defs .= $indent . $species->getCVodeDerivName() . " = $species_deriv;\n"; 
    }

    return ( $deriv_defs, $err );
}


###
###
###


sub toMatlabString
{
    my $slist       = shift;
    my $rlist       = shift;
    my $stoich_hash = shift;
    my $plist       = (@_) ? shift : undef;

    my $deriv_defs = '';
    my $indent = '    ';
    my $err;

    # construct derivative definition for each species
    foreach my $species ( @{ $slist->Array } )
    {
        # get species vector in stoich hash
        my $species_vector = $stoich_hash->{ $species->Index };
        my $species_deriv = '';
        
        if ( $species->SpeciesGraph->Fixed )
        {   # handle species with fixed population
            $species_deriv = '0.0';
        }
        else
        {   # handle all other species...
            # add rates and stoich for each reaction that influences this speices
            foreach my $i_rxn ( sort { $a <=> $b } keys %$species_vector )
            {
                # get species stoichiometry under this reaction
                my $stoich = $species_vector->{$i_rxn};
                
                # look up reaction object
                my $i_rxn0 = $i_rxn - 1;
                my $rxn = $rlist->Array->[$i_rxn0];
                
                # add this reaction flux to the species derivative
                if    ( $stoich == 1 )
                {   $species_deriv .= " +" . $rxn->getMatlabName();             }
                elsif ( $stoich == 0 )
                {                                                               }
                elsif ( $stoich == -1 )
                {   $species_deriv .= " -" . $rxn->getMatlabName();             }
                elsif ( $stoich > 0 )
                {   $species_deriv .= " +$stoich.0*" . $rxn->getMatlabName();   }
                elsif ( $stoich < 0 )
                {   $species_deriv .= " $stoich.0*" . $rxn->getMatlabName();    } 
            } 

            # trim leading " +"
            $species_deriv =~ s/^ \+?//;
        
            # replace empty string with a zero rate
            if ($species_deriv eq '')
            {   $species_deriv = '0.0';   }
        }
            
        # add derivative to list of definitions
        $deriv_defs .= $indent . $species->getMatlabDerivName() . " = $species_deriv;\n"; 
    }

    return ( $deriv_defs, $err );
}


###
###
###


# get names of species and formulas for initial concentrations for matlab
#  NOTE: this method ALWAYS writes the original initial concentration definitions
#   and ignores any changes made by setConcentrations
sub getMatlabSpeciesNames
{
    my $slist = shift @_;
    my $model = shift @_;
    
    my $plist = $model->ParamList;

    my $err;
    my $species_names = '';
    my $species_init = '';
    my $indent = '    ';
    
    # TODO: this matlab output is a hack.  improve this.  --justin

    # generate a map from param names to matlab references
    my $ref_map = {};
    my $m_idx = 1;
    foreach my $param ( @{$plist->Array} )
    {
        if ( $param->Type eq 'Constant' )
        {
            $ref_map->{ $param->Name } = "params($m_idx)";
            ++$m_idx;
        }
    }
    
    # gather names and init expressions for all species
    $m_idx = 1;
    my @species_names = ();    
    foreach my $species ( @{ $slist->Array } )
    {
        push @species_names, "'" . $species->SpeciesGraph->StringExact . "'";    
        (my $param) = $plist->lookup( $species->Concentration );    
    
        if ( $param )
        {   # initial concentration is given by a Parameter
            # expand the expression (recursively past parameters!)
            $species_init .= $indent . "species_init($m_idx) = " . $param->toString( $plist, 0, 2 ) . ";\n";          
        }
        else
        {   # initial concentration is a number
            $species_init .= $indent . "species_init($m_idx) = " . $species->Concentration . ";\n";
        }  
        ++$m_idx;
    }
    
    # replace param names with Matlab references   
    foreach my $pname ( keys %$ref_map )
    {
        my $matlab_ref = $ref_map->{$pname};
        my $regex = 
        $species_init =~ s/(^|\W)$pname(\W|$)/$1$matlab_ref$2/g;
    }
    
    return (  join(', ', @species_names), $species_init, $err );
}

sub getMatlabSpeciesNamesOnly
{
	my $slist = shift;
	my $err;
	my @species_names = ();    
    foreach my $species ( @{ $slist->Array } )
    	{
    		push @species_names, "'" . $species->SpeciesGraph->StringExact . "'";
    	}
    
    return (  join(', ', @species_names), $err );

}

# Check or initialize a species concentration vector: If @$cond is empty, it is loaded
# with species concentrations from the SpeciesList. If @$conc is non-empty, then 
# it's checked for size compatibility with SpeciesList. If @$conc is shorter than the
# SpeciesList, it is padded with zeros. If @$conc is longer, it's an error.
#
# $slist->checkOrInitConcentrations( $conc )
sub checkOrInitConcentrations
{
    my $slist = shift @_;
    my $conc  = shift @_;

    # decide to use concentration vector or direct values from species
    if ( @$conc )
    {   # check concentration vector
        if ( @$conc > @{$slist->Array} )
        {   # this case is not well-defined
            exit_error( "SpeciesList->initConcentrations(): concentration array is longer than species array" );
        }
        elsif ( @$conc < @{$slist->Array} )
        {   # pad with zeros
            my $n_zeros = @{$slist->Array} - @$conc;
            push @$conc, (0) x $n_zeros;
        }
    }
    else
    {   # get concentrations from species
        @$conc = map {$_->Concentration} @{$slist->Array};
    }
}


1;
