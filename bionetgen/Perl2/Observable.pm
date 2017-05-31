package Observable;

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



# Members
struct Observable =>
{
    Name      => '$',		   
    Patterns  => '@',
    Weights   => '@',
    Type      => '$',
    Output    => '$',           # If false, suppress output.  (Feature not yet implemented)
};

my $print_match=0;

###
###
###


sub copy
{
    my $obs = shift @_;
    
    my $obs_copy = Observable->new( Name=>$obs->Name, Type=>$obs->Type, Output=>$obs->Output );
    
    foreach my $patt ( @{$obs->Patterns} )
    {
        my $patt_copy = $patt->copy();
        push @{$obs_copy->Patterns}, $patt_copy;
    }
    
    if ( defined $obs->Weights )
    {   $obs_copy->Weights( [@{$obs->Weights}] );   }

    return $obs_copy;
}


###
###
###


# call this method to link Compartments to a new CompartmentList
sub relinkCompartments
{
    my $obs = shift @_;
    my $clist = shift @_;
    
    my $err = undef;
    unless ( ref $clist eq 'CompartmentList' )
    {   return "Observable->relinkCompartments: Error!! Method called without CompartmentList object";   }
    
    foreach my $patt ( @{$obs->Patterns} )
    {
        $err = $patt->relinkCompartments( $clist );
        if (defined $err) {  return $err;  }
    }
    
    return undef;
}


###
###
###


sub readString
{
    my $obs    = shift @_;
    my $string = shift @_;
    my $model  = shift @_;
    
    my $err;

    # set output true (by default)
    $obs->Output(1);
    
    # Remove leading whitespace
    $string =~ s/^\s*//;

    # Check if first token is an index (This index will be ignored)
    # DEPRECATED as of BNG 2.2.6
    if ( $string =~ s/^\s*\d+\s+// )
    {
    		return "Leading index detected at '$string'. This is deprecated as of BNG 2.2.6.";
    }
    
    # Remove leading label, if exists
    if ( $string =~ s/^\s*(\w+)\s*:\s+// )
    {
	    # Check label for leading number
		my $label = $1;
		if ($label =~ /^\d/) {  return "Syntax error (label begins with a number) at '$label'";  }
    }
    
	# Check name for leading number
	my $string_left = $string;
	unless ( $string_left =~ s/^([A-Za-z_]\w*)// )
	{ 
		return "Syntax error (observable name begins with a number) at '$string'";
	}

    # Check if next token is observable type
    #  Adding Counter and Population types  --Justin, 5nov2010
    #  Function observable is undocumenterd, but its here to include function
    #    evaluations among the output observables --Justin
    if ( $string =~ s/^\s*(Molecules|Species|Counter)\s*// )
    {
        # record observable type
        $obs->Type($1);
    }
    else
    {   # Justin thinks defaults like this are dangerous and prone to unpredictable behavior
        #  when future changes are made to the language or code.  Return error instead
        return "Observable type is not valid";
    }

    # Next token is observable Name
    if ( $string =~ s/^\s*([A-z]\w*)\s+// )
    {
        my $name=$1;
        $obs->Name($name);
    }
    else
    {
        my ($name) = split(' ', $string);
        return "Invalid observable name $name: may contain only alphnumeric and underscore";
    }

    # Define parameter with name of the Observable
    if ( $model->ParamList->set( $obs->Name, "0", 1, "Observable", $obs) )
    {
  	    my $name= $obs->Name;
        return "Observable name $name matches previously defined Observable or Parameter";
    }

    # Remaining entries are patterns
    my $sep = '^\s+|^\s*,\s*';
    while ($string)
    {
        my $g = SpeciesGraph->new();
        my $count_autos = 1;
        $err = $g->readString( \$string, $model->CompartmentList, 0, $sep, $model->MoleculeTypesList, $count_autos );
        if ($err) { return "While reading observable " . $obs->Name . ": $err"; }

        $string =~ s/$sep//;

        if ( $g->isNull() )
        {   # this is the null pattern
            send_warning( sprintf("Found useless instance of null pattern in observable %s.", $obs->Name) );
            next;
        }

        if ( ($obs->Type eq 'Species') and (not defined($g->Quantifier) or $g->Quantifier eq '') )
        { 
            $g->MatchOnce(1);
        }
        push @{$obs->Patterns}, $g;
    }
    
    return $err;
}


###
###
###


# reset the observable weights to zero
sub reset_weights
{
    my $obs   = shift @_;
    my $alloc = @_ ? shift @_ : 0;
    $obs->Weights( [(0) x ($alloc+1)] );
}


###
###
###

#####################################DB#################################
sub writeMDL
{
     my $obs = shift @_; 
     my $string = ""; 
     my $indent = "   "; 
     
     $string .= "\n$indent/*".$obs->Name."*/\n"; 
     my $i = -1; 
     my $first = 1; 
     
     $string .= $indent."{ "; 
     foreach my $w (@{$obs->Weights}){
         ++$i; 
	 next unless $w; 
	 $string .= " + " unless $first; 
	 $string .= sprintf("%sCOUNT[s%s,WORLD]", $w > 1 ? $w."*" : "", $i); 
	 $first = 0;     
	 }
     $string .= sprintf(" }=> \"./react_data/%s.dat\"\n",$obs->Name); 
     return $string; 
}

############

# try to match observable to a speciesGraph and return the number of matches
sub match
{
    my $obs = shift @_;
    my $sg  = shift @_;
    my $root_src  = @_ ? shift @_ : -1;
    my $root_targ = @_ ? shift @_ : -1;
    #printf STDOUT "Observable::match(obs=%s, sg=%s, rootmol=%s)\n", $obs->Name, $sg->toString(), $root_targ;

    my $total_matches = 0;
    if ($obs->Type eq "Molecules")
    {
        my $mode = 0;
        if (exists $BNGModel::GLOBAL_MODEL->Options->{MoleculesObservables})
        {
            if ( $BNGModel::GLOBAL_MODEL->Options->{MoleculesObservables} eq "CountUnique" )
            {   $mode = 1;  }
        }

        foreach my $patt (@{$obs->Patterns})
        {
            # find matches of this pattern in species graph
            my @matches = $patt->isomorphicToSubgraph( $sg, $root_src, $root_targ );
            if (@matches)
            {
                ## SYMMETRY CORRECTION is disabled for the time being!
                if ($mode)
                {   $total_matches += scalar @matches / $patt->Automorphisms;    }
                else
                {   $total_matches += scalar @matches;   }
            }
        }
    }
    elsif ($obs->Type eq "Species")
    {
        my $mode = 0;
        if (exists $BNGModel::GLOBAL_MODEL->Options->{SpeciesObservables})
        {
            if ( $BNGModel::GLOBAL_MODEL->Options->{SpeciesObservables} eq "CountUnique" )
            {   $mode = 1;  }
        }

        foreach my $patt (@{$obs->Patterns})
        {
            # find matches of this pattern in species graph
            my @matches = $patt->isomorphicToSubgraph( $sg, $root_src, $root_targ );            
            ## SYMMETRY CORRECTION is disabled for the time being!
            #my $n_match = scalar @matches / $patt->Automorphisms;
            my $n_match = scalar @matches;
             
            if ($patt->Quantifier)
            {
                my $test_string = $n_match . $patt->Quantifier;
                my $result = eval $test_string;
                warn $@ if $@;
                $total_matches += $result ? 1 : 0;
                last if ($mode);
            }
            elsif ($n_match>0)
            {
                $total_matches += 1;
                last if ($mode);
            }
        }

    }
    return $total_matches;
}


###
###
###


# compute observable weights for the species in the array
sub update
{
    my $obs = shift @_;
    my $species = shift @_;
    my $idx_start = @_ ? shift @_ : 0;

    my $err = '';

    for ( my $ii = $idx_start; $ii < @$species; ++$ii )
    {
        my $sp = $species->[$ii];
        next if ($sp->ObservablesApplied);
        $obs->Weights->[$sp->Index] = $obs->match( $sp->SpeciesGraph );
    }
    return $err;
}


###
###
###


# evaluate observable.
# If observable evaluation is scoped to a complex or molecule,
#  the scope argument is the first element of @$args.
sub evaluate
{
    my $obs = shift @_;
    my $args  = @_ ? shift @_ : [];        
    my $plist = @_ ? shift @_ : undef;
    my $level = @_ ? shift @_ : 0;

    # first argument is observable name,
    #  remaining arguments should be pointers to species
    my $eval_args = [];
    my $ii=1;
    while ( $ii < @$args )
    {
        push @$eval_args, $args->[$ii]->evaluate( $plist, $level+1 );
        ++$ii;
    }
    
    my $val = 0;
    if (@$eval_args)
    {   # this is a local observable
        foreach my $ptr ( @$eval_args )
        {
            my ($species_idx,$mol_idx) = split( /\./, $ptr );
            if ( defined $mol_idx )
            {   # molecule-scoped observable
                unless ( defined $BNGModel::GLOBAL_MODEL )
                {  die "Observable->evaluate(): Error! Can't find current Model to evaluate local observable!";  }

                my $sg = $BNGModel::GLOBAL_MODEL->SpeciesList->Array->[$species_idx-1]->SpeciesGraph;
                $val += $obs->match( $sg, 0, $mol_idx );
            }   
            elsif ( $species_idx >= 0 )
            {   # complex-scoped (i.e. species-scoped) observable
                $val += (defined $obs->Weights->[$species_idx]) ? $obs->Weights->[$species_idx] : 0;
            }
            else
            {   die "Observable->evaluate(): Error! Observable argument was not a pointer to a species or a molecule!";   }

        }
    }
    else
    {   # global observable
        unless ( defined $BNGModel::GLOBAL_MODEL )
        {  die "Observable->evaluate(): Error! Can't find current Model to evaluate global observable!";  }

        my $conc  = $BNGModel::GLOBAL_MODEL->Concentrations();
        my $slist = $BNGModel::GLOBAL_MODEL->SpeciesList();

        # make sure oncentrations are initialized
        $slist->checkOrInitConcentrations($conc);
        # calculate global observables
        for ( my $sidx = 1; $sidx < @{$obs->Weights}; ++$sidx )
        {
            next unless ( defined $obs->Weights->[$sidx] );
            $val += $obs->Weights->[$sidx] * $conc->[$sidx-1];
        }
    }
    return $val;
}


###
###
###


sub toString
{
    my $obs    = shift;
    my $string = $obs->Type . ' ' . $obs->Name;
    foreach my $patt (@{$obs->Patterns})
    {
        $string .= ' ' . $patt->toString();
    }
    return $string;
}


sub toStringSSC
{
    my $obs=shift;
    my $string = '';
    foreach my $patt (@{$obs->Patterns})
    {
        my ($tempstring, $trash) = $patt->toStringSSC();
        $string .= ' ' . $tempstring;
    }
    return $string;
}


###
###
###


sub toCVodeString
{
    my $obs = shift;
    my $plist = (@_) ? shift : undef;
   
    if ( $obs->Type ne 'Function' )
    {
        # create linear sum of terms that contribute to the observable
        # BE CAREFUL: species indexed starting from 1.
        my @terms = ();
        for ( my $idx1=1; $idx1 < @{$obs->Weights}; $idx1++ )
        {  
            my $idx0 = $idx1 - 1;
            if ( defined $obs->Weights->[$idx1] and $obs->Weights->[$idx1]!=0 )
            {
                my $term;
                if ( $obs->Weights->[$idx1] == 1 )
                {  $term = "NV_Ith_S(species,$idx0)";  }
                else
                {  $term = $obs->Weights->[$idx1] . "*NV_Ith_S(species,$idx0)";  }
                push @terms, $term;
            }
        }
        if ( @terms )
        {   return join( ' +', @terms );   }
        else
        {   return "0.0";  }
    }
    else
    {
        # handle function type observable!
        ( my $param, my $err ) = $plist->lookup( $obs->Name );
        return $param->Expr->toCVodeString( $plist );
    }    
}


###
###
###


sub toMatlabString
{
    my $obs = shift;
    my $plist = (@_) ? shift : undef;
   
    if ( $obs->Type ne 'Function' )
    {
        # create linear sum of terms that contribute to the observable
        # BE CAREFUL: species indexed starting from 1.        
        my @terms = ();
        for ( my $idx1=1; $idx1 < @{$obs->Weights}; $idx1++ )
        {  
            if ( defined $obs->Weights->[$idx1] and $obs->Weights->[$idx1]!=0 )
            {
                my $term;
                if ( $obs->Weights->[$idx1] == 1 )
                {  $term = "species($idx1)";  }
                else
                {  $term = $obs->Weights->[$idx1] . "*species($idx1)";  }
                push @terms, $term;
            }
        }
        if ( @terms )
        {   return join( ' +', @terms );   }
        else
        {   return "0.0";  }
    }
    else
    {
        # handle function type observable!
        ( my $param, my $err ) = $plist->lookup( $obs->Name );
        return $param->Expr->toMatlabString( $plist );
    }    
}


###
###
###


sub toXML
{
    my $obs    = shift @_;
    my $indent = shift @_;
    my $index  = shift @_;

    my $id = "O" . $index;
    my $string = $indent . "<Observable";

    # Attributes
    # id 
    $string .= " id=\"" . $id . "\"";
    # name
    if ($obs->Name)
    {
        $string .= " name=\"" . $obs->Name . "\"";
    }
    # type
    if ($obs->Type)
    {
        $string .= " type=\"" . $obs->Type . "\"";
    }
    ## output flag (TODO: disable for now)
    #if (defined $obs->Output)
    #{
    #    $string .= " output=\"" . $obs->Output . "\"";
    #}
        

    # Objects contained
    my $indent2= "  ".$indent;
    my $ostring=$indent2."<ListOfPatterns>\n";
    my $ipatt=1;
    foreach my $patt (@{$obs->Patterns})
    {
        my $indent3 = "  ".$indent2;
        my $pid = $id."_P".$ipatt;
        $ostring .= $patt->toXML($indent3,"Pattern",$pid,"");
        ++$ipatt;
    }
    $ostring .= $indent2 . "</ListOfPatterns>\n";

    # Termination
    if ($ostring)
    {
        $string .= ">\n"; # terminate tag opening
        $string .= $ostring;
        $string .= $indent."</Observable>\n";
    }
    else
    {
        $string .= "/>\n"; # short tag termination
    }
}


###
###
###


sub getWeightVector
{
    my $obs = shift;

    my @wv = ();
    foreach my $i ( 1..$#{$obs->Weights} )
    {
        my $w = $obs->Weights->[$i];
        if ($w)
        {
            push @wv, $w;
        }
        else
        {
            push @wv, 0;
        }
    }
    return @wv;
}


###
###
###


sub toGroupString
{
    my $obs   = shift @_;
    my $slist = shift @_;

    my $out   = sprintf "%-20s ", $obs->Name;
    my $i     = -1;
    my $first = 1;
    my $n_elt = 0;
    foreach my $w (@{$obs->Weights})
    {
        ++$i;
        next unless $w;
        ++$n_elt;
        if ($first)
        {
            $first = 0;
        }
        else
        {
            $out .= ",";
        }

        if ($w==1)
        {
            $out .= "$i";
        }
        else
        {
            $out .= "$w*$i";
        }
    }
  
    if ($print_match)
    {
        print $obs->Patterns->[0]->toString(),": ";
        my $i=-1;
        foreach my $w (@{$obs->Weights})
        {
            ++$i;
            next unless $w;
            my $sstring = $slist->Array->[$i-1]->SpeciesGraph->toString();
            foreach my $nw (1..$w)
            {
	            print "$sstring, ";
            }
        }
        print "\n";
    }

    #printf "Group %s contains %d elements.\n", $obs->Name, $n_elt;
    return $out;
}


###
###
###


# Returns number of nonzero elements in the Group
sub sizeOfGroup
{
    my $obs = shift;
    my $n_elt = 0;
    foreach my $w (@{$obs->Weights})
    {
        next unless $w;
        ++$n_elt;
    }
    return $n_elt;
}


###
###
###


sub printGroup
{
    my $obs     = shift;
    my $fh      = shift;
    my $species = shift;
    my $idx_start = (@_) ? shift : 0;

    printf $fh "%s ", $obs->Name;
  
    my $first=1;
    for ( my $ii = $idx_start;  $ii < @$species;  ++$ii )
    {
        my $spec = $species->[$ii];
        my $sp_idx = $spec->Index;
        my $weight = $obs->Weights->[$sp_idx];
        next unless $weight;

        if ($first) {   $first = 0;   }
        else {   print $fh ",";   }
        
        if ( $weight==1 ) {   print $fh $sp_idx;   }
        else {   print $fh "$weight*$sp_idx";   }
    }
    print $fh "\n";
    return '';
}


###
###
###


sub toMathMLString
{
    my $obs = shift;

    my $string = "<math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n";
    my $n_elt = $obs->sizeOfGroup();

    $string .= "  <apply>\n";
    $string .= "    <plus/>\n";
    if ($n_elt<=1)
    {
        $string .= sprintf "    <cn> 0 </cn>\n";
    }

    my $i=-1;
    foreach my $w (@{$obs->Weights})
    {
        ++$i;
        next unless $w;
        if ($w==1)
        {
            $string .= sprintf "    <ci> S%d </ci>\n", $i;
        }
        else
        {
            $string.=   "    <apply>\n";
            $string.=   "      <times/>\n";
            $string.=   sprintf "      <cn> %s </cn>\n", $w;
            $string.=   sprintf "      <ci> S%d </ci>\n", $i;
            $string.=   "    </apply>\n";
        }
    }
    # Include zero entry if no nonzero weights
    if ($n_elt==0)
    {
        $string .= "    <cn> 0 </cn>\n";
    }

    $string .= "  </apply>\n";
    $string .= "</math>\n";

    return ($string, '');
}


###
###
###

1;
