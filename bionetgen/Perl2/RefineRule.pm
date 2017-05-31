package RxnRule;

use strict;
use warnings;


###
###
###


# parse population mapping string and return a RxnRule object
sub newPopulationMappingRule
{
    # get input arguments
	my $string = shift @_;
	my $model  = shift @_;

	# define return arguments
	my $err = undef;
	my $rr  = undef;
	
	# Remove leading whitespace
	$string =~ s/^\s*//;

	my $name = '';
    # check for rule identifier or index
	if ( $string =~ s/^([\w\s*]+):\s*// )
	{   # if the first token is alphanumeric and followed by a colon,
	    #  then its the name of the rule
		$name = $1;
	}
	elsif ( $string =~ s/^(\d+)\s+// )
	{   # if the first token is a number, this is the rule index
		$name = $1;
	}


	# some local variables
	my ( $ipatt, $g, $sep, $reversible );

    # data structures for reactants, products and references
    my $reac  = [];
    my $prod  = [];
    my $rrefs = {};
    my $prefs = {};
    # temporary structure to map references from reactants to products
	my %labels = ();


	# Read reactant patterns
	$sep = '^\s*[+]\s*|^\s*([<]?-[>])\s*';    #  "+"  or "->"  or "<->"
	$ipatt = -1;
	while ($string)
	{
		$g = SpeciesGraph->new();
		# use the MoleculeTypes list for reactant validation
		$err = $g->readString( \$string, $model->CompartmentList, SpeciesGraph::IS_SPECIES(),
		                         $sep, $model->MoleculeTypesList );
		if ($err) {  return ($err, $rr);  }
		++$ipatt;

        # Check Labels for this Reactant:
		if ( defined $g->Label  and  $g->Label ne '' )
		{
            $rrefs->{ $g->Label } = $ipatt;
        }

		# Checking Molecules...
		my $imol = -1;
		foreach my $mol ( @{ $g->Molecules } )
		{
			++$imol;
			my $label = $mol->Label;
			if ( defined $label  and  $label ne '' )
			{
				if ( $labels{$label} )
				{
					return ("Repeated label $label in reactants of reactant rule", $rr);
				}
				$labels{$label} = 'M';
				$rrefs->{$label} = join '.', ($ipatt, $imol);
			}
			my $icomp = -1;
				
			# Checking Components...
			foreach my $comp ( @{ $mol->Components } )
			{
				++$icomp;
				$label = $comp->Label;
				if ( defined $label  and  $label ne '' )
				{				
					if ( $labels{$label} )
					{
						return ("Repeated label $label in reactants of reaction rule", $rr);
					}
					$labels{$label} = 'C';
					$rrefs->{$label} = join '.', ($ipatt, $imol, $icomp);
				}
			}
		}
			
		# Save this reactant
		push @$reac, $g;
			
		# Find next separator (some problem with string if there is no separator)
		unless ( $string =~ s/$sep// )
		{
			return ("Invalid syntax in reaction rule: $string", $rr);
		}
			
		# If the arrow operator was the next separator, then it's captured in $1
		if ($1)
		{   # Check if this reaction is reversible
			$reversible = ( $1 eq "<->" ) ? 1 : 0;
            if ( $reversible )
            {   return ("Population mapping rule should not be reversible!", $rr);   }
		    last;
	    }
	}
    # only 1 reactant pattern allowed in population mapping rule
    unless (@$reac == 1)
    {   return ("Incorrect number of reactants in population mapping rule!", $rr);   }



    # Read Product Patterns
	$sep   = '^\s*([+])\s*|^\s+';    # "+" or "  "
	$ipatt = -1;
	while ($string)
	{
		my $g = SpeciesGraph->new();	
		# Validate product graphs using population types
		$err = $g->readString( \$string, $model->CompartmentList, SpeciesGraph::IS_SPECIES(),
		                       $sep, $model->PopulationTypesList );
		if ($err) {  return ($err, $rr);  }
		++$ipatt;


        # Check that pattern is a single molecule
        unless ( @{$g->Molecules} == 1 )
        {
            return ("Incorrect number of molecules in product of population mapping rule!", $rr);
        }
        # Check that population type does not collide with anything in the molecule type list
        if ( defined $model->MoleculeTypesList->findMoleculeType($g->Molecules->[0]->Name) )
        {
            return ("Population product type collides with a previously defined molecule type!", $rr);
        }
        # Check that the molecule does not have components
        unless ( @{$g->Molecules->[0]->Components} == 0 )
        {
            return ("Components are not permitted in product of population mapping rule!", $rr);
        }


        # Checking Labels for this Species..
		if ( defined $g->Label  and  $g->Label ne '' )
		{
			$prefs->{ $g->Label } = $ipatt;
		}

		# Checking Molecules... (verify correspondence and uniqueness!)
		my $imol = -1;
		foreach my $mol ( @{$g->Molecules} )
		{
			++$imol;
			my $label = $mol->Label;
			if ( defined $label  and  $label ne '' )
			{
				if ( $labels{$label} eq 'M' )
				{
					$labels{$label} = 'found';
				}
				elsif ( $labels{$label} eq 'found' )
				{
					return ("Repeated label $label in products of reaction rule $name", $rr);
				}
				else
				{
					return ("Mis- or un-matched label $label in products of reaction rule $name", $rr);
				}
				$prefs->{$label} = join '.', ($ipatt, $imol);
			}
				
			# Checking Components...
			my $icomp = -1;
			foreach my $comp ( @{$mol->Components} )
			{
				++$icomp;
				$label = $comp->Label;

				#print "label=$label\n";
				if ( defined $label  and  $label ne '' )
				{
					if ( $labels{$label} eq 'C' )
					{
						$labels{$label} = 'found';
					}
					elsif ( $labels{$label} eq 'found' )
					{
						return ("Repeated label $label in products of reaction rule $name", $rr);
					}
					else
					{
						return ("Mis- or un-matched label $label in products of reaction rule $name", $rr);
					}
					$prefs->{$label} = join '.', ($ipatt, $imol, $icomp);
				}
			}
		}
			
		# Save this Product pattern
		push @$prod, $g;
			
		# Check for next separator (store a "+" separator in $1)
		$string =~ s/$sep//;
		
		# See if there's more Products to parse..
		last unless $1;
	}
    # only 1 product pattern allowed in population mapping rule
    unless (@$prod == 1)
    {   return ("Incorrect number of products in population mapping rule!", $rr);   }



	# Read rateLaw
	my $rl;
	
    # Look for TotalRate attribute
    # (default behavior is PerSite rate)
    my $TotalRate  = 0;
    if ( $string =~ s/(^|\s)TotalRate(\s|$)/$2/ )
    {   $TotalRate = 1;   }
 
    # temporarily add Reference tags as names in the ParamList
	if (%$rrefs) {  setRefs( $rrefs, '', $model->ParamList );  }
 
    # Parse and Create the ratelaw
    ( $rl, $err ) = RateLaw::newRateLaw( \$string, $model, $TotalRate, $reac );
	if ($err) {  return ($err, $rr);  }
 
    # unset temporary names of Reference tags
    if (%$rrefs) {  unsetRefs( $rrefs, $model->ParamList );  } 


	# Check for syntax error due to extra ratelaw tokens
    if ( $string =~ /^\s*[,]/ )
	{   return ("Unidirection reaction may have only one rate law", $rr);   }
		
    # String should be empty now, otherwise there may be a problem.
	if ( $string =~ /\S+/ )
	{   return ("Syntax error in reaction rule at $string", $rr);   }
        

        
    # Construct RxnRule
	$rr = RxnRule->new();
	if ($name) {  $rr->Name($name);  }
	$rr->Reactants( $reac );
	$rr->Products(  $prod );
	$rr->Priority( 0 );
	$rr->RateLaw( $rl );
	$rr->Rexclude( [([])] );
	$rr->Pexclude( [([])] );
	$rr->Rinclude( [([])] );
	$rr->Pinclude( [([])] );
	$rr->TotalRate( $TotalRate );
	$rr->DeleteMolecules( 0 );
	$rr->MoveConnected( 0 );
	$rr->RRefs( $rrefs );
	$rr->PRefs( $prefs );

	# Find mapping of reactants onto products
	#  (pass population types list for handling of created populations
	if ( $err = $rr->findMap($model->PopulationTypesList) )
	{   return ($err, $rr);   }

    # Return normally
	return ($err, $rr);
}



###
###
###



##------------##
## expandRule ##
##------------##
sub expandRule
{
    # get input arguments
	my $rr       = shift @_;      # this reaction rule
	my $sg_list  = shift @_;      # expand rules w.r.t. these species graphs
	my $model    = shift @_;      # model
	my $hybrid_model = shift @_;  # new hybrid model
    my $user_params = (@_) ? shift : {};
    

    # Specify default params
	my $params = {   verbose => 0,
	                 indent  => ' ' x 8,
                     safe    => 0
	             };
	# overwrite defaults with user params
	while ( my ($opt,$val) = each %$user_params )
	{   $params->{$opt} = $val;   }


    # define return values
    my $err = undef;
    my $child_rule_list = [];
    my $child_rule_map = {};

    # clear out the RxnLabels hash
    %{$rr->RxnLabels} = ();
    
    # remember number of reactants
    my $n_reactants = scalar @{$rr->Reactants};


    # Map Reactant Patterns into SpeciesGraphs in @$patterns
	my $ipatt = 0;
	foreach my $patt ( @{$rr->Reactants} )
	{
	    # make sure rmatches is empty!
		$rr->Rmatches->[$ipatt] = [];
        if ( $params->{safe} )
        {
            # add self-embedding of reactant pattern its match list
            my $copy_patt = $patt->copy( !SpeciesGraph::COPY_LABEL(), SpeciesGraph::GET_LABEL() );        
            (my $copy_match) = $patt->isomorphicToSubgraph($copy_patt);
            push @{$rr->Rmatches->[$ipatt]}, $copy_match;

        }
        else
        {
            # check if reactant is isomorphic to anything in sg_list
            my $is_isomorph = 0;
            foreach my $sg (@$sg_list)
            {
                # NOTE: since $sg is a fully specified species, looking for a subgraph isomorphism in $rpatt
                #  is sufficient for establishing isomorphism. This is slower than the method 'isomorphicTo'
                #  but does not require sorting molecules (which is problematic for reactant patterns).
                if ( $sg->isomorphicToSubgraph($patt) )
                {
                    $is_isomorph = 1;
                    last;
                }
            }
            unless ($is_isomorph)
            {
                # add self-embedding of reactant pattern its match list
                my $copy_patt = $patt->copy( !SpeciesGraph::COPY_LABEL(), SpeciesGraph::GET_LABEL() );
                (my $copy_match) = $patt->isomorphicToSubgraph($copy_patt);
                push @{$rr->Rmatches->[$ipatt]}, $copy_match;
            }
	    }

	    # find all matches in $sg_list. load maps into Rmatches.
	    #my $n_matches = $rr->find_reactant_matches( $ipatt, $sg_list, $model );
		my $new_matches = $rr->find_embeddings( $ipatt, $sg_list, $model );
        push @{$rr->Rmatches->[$ipatt]}, @$new_matches;

		if ( $params->{verbose} )
		{
		    my $tot_matches = @{$rr->Rmatches->[$ipatt]};
		    printf $params->{indent} . "  ..found %d match%s to reactant pattern %d\n",
		        $tot_matches, ($tot_matches==1 ? '' : 'es'), $ipatt+1;
		}
		++$ipatt;
	}


    # create CartesianProduct object to iterate over all rule instances
    my $rule_instances = CartesianProduct::new();
    $rule_instances->initialize( $rr->Rmatches );
    
    # loop over every reactant set
    my $rule_instance = [];
    while ( $rule_instances->getNext($rule_instance) )
    {
        # get child rule by restricting rule to the rule_instance
        my $child_rule = $rr->restrict_rule( $rule_instance, $model, $hybrid_model, $params );
        next unless ( defined $child_rule );

        if ( exists $child_rule_map->{$child_rule->StringID} )
        {
            my $multiplicity = $child_rule->RateLaw->Factor;
            my $prior_child_rule = $child_rule_map->{$child_rule->StringID};
            $prior_child_rule->RateLaw->Factor( $prior_child_rule->RateLaw->Factor + $multiplicity );
        }
        else
        {           
            $child_rule_map->{$child_rule->StringID} = $child_rule;
            push @$child_rule_list, $child_rule;
        }
    }

    foreach my $child_rule ( @$child_rule_list )
    {
        # add statFactor into the RateLaw constant
        my $ratelaw = $child_rule->RateLaw;
        unless ( $ratelaw->Factor == 1.0 )
        {
            # get rate parameter
            my $param_name = $ratelaw->Constants->[0];
            my ($rate_param,$err) = $hybrid_model->ParamList->lookup($param_name); 
            # multiply rate parameter by Factor
            my $new_rate_expr = Expression::operate( '*', [$ratelaw->Factor, $rate_param->Expr], $hybrid_model->ParamList );
            my $new_param_name = $new_rate_expr->getName( $hybrid_model->ParamList, $param_name.'_' );
            $ratelaw->Constants->[0] = $new_param_name;
            # set statFactor to 1
            $ratelaw->Factor(1);
	    }
	}

    %{$child_rule_map} = ();
    return ($err, $child_rule_list);
}	



###
###
###



# $child_rule = $rule->restrict_rule( \@rule_instance, $model, $hpp_model, \%params )
sub restrict_rule
{
    # get input arguments
    my $rr = shift @_;
    my $rule_instance = shift @_;
    my $model = shift @_;
    my $hybrid_model = shift @_;
    my $user_params = (@_) ? shift : {};


    # Specify default params
	my $params = {  max_agg    => 1e99,
	                max_stoich => {},
	                verbose    => 0 
	             };
	# overwrite defaults with user params
	while ( my ($opt,$val) = each %$user_params )
    {   $params->{$opt} = $val;   }
    
    
    # remember number of reactants and products
    my $n_reactants = @{$rr->Reactants};
    my $n_products  = @{$rr->Products};

    
    # copy the target reactants and transfer labels from patterns to the copied reactants.
    my $matches   = [];   # holds maps from reactant patterns to (copies of) target SpeciesGraphs
    my $reactants = [];   # holds (copies of) target SpeciesGraphs
    foreach my $match ( @$rule_instance )
    {
        # copy the map and target graph (map copy points to the target copy)
        my $match_copy = $match->copy_map_and_target();
        # tranfer labels and attributes on the source graph to the target graph
        $match_copy->transferLabels();
        # save the map copy and the target copy
        push @$matches,   $match_copy;
        push @$reactants, $match_copy->Target;
    }
    
    
    # next, let's collect all the labels in reactant and product patterns
    my $temp_labels = {};
    {
        my $used_labels = {};
        
        foreach my $pattR ( @{$rr->Reactants} )
        {   $pattR->gatherLabels( $used_labels );   }

        foreach my $pattP ( @{$rr->Products} )
        {   $pattP->gatherLabels( $used_labels );   }    
    
        # assign unique labels to everything that isn't labeled
        my $i_label = 0;
        foreach my $reactant ( @$reactants )
        {
            $i_label += $reactant->assignLabels( $i_label, $temp_labels, $used_labels );
        }
    }    
    

    # TODO: we want to check that reactants can interact here, but compartments aren't yet 
    # supported in NFsim.  So we'll cross this bridge later.  --Justin


	my ( $products ) = $rr->apply_operations( $matches );
    return undef  unless ( defined $products );

    # Check for correct number of product graphs
    if ( @$products != $n_products )
    {
        # If Molecules are being deleted, it is allowed to have more subgraphs than product patterns
        if ( $rr->DeleteMolecules  and  @$products > $n_products )
        {
            if ($params->{verbose})
            {   printf "Deleting molecules in Rule %s\n", $rr->Name();  }
        }   
        # Otherwise, the reaction shouldn't happen
        else
        {
	        if ($params->{verbose})
	        {   printf "Rule %s: n_sub (%d)!= n_prod (%d)\n", $rr->Name, @$products, $n_products;   }
  	        return undef;
        }
    }

	# Check and Process Product Graphs
    for ( my $ip = 0; $ip < @$products; ++$ip )
	{
	    my $p = $products->[$ip];
	    my $iprod = ($ip < @{$rr->Products}) ? $ip : -1;

        # check for Max Aggregation violations
		if ( @{$p->Molecules} > $params->{max_agg} )
		{
            #printf "%d molecules exceeds max_agg of %d\n", $#{$p->Molecules}+1, $max_agg;
 		    return undef;
		}

		# check for Max Stoichiometry violations
		foreach my $key ( keys %{$params->{'max_stoich'}} )
		{
            my $max = $params->{'max_stoich'}->{$key};
			next if ($max eq "unlimited");
			if ( $p->stoich($key) > $max )
			{
                #printf "Stoichometry of $key in %s exceeds max of %d\n", $p->toString(), $max;
		  	    return undef;
			}
		}

        # Put product graph in canonical order (quasi-canonical for the time being)
	    if ( my $err = $p->sortLabel() )
	    {   # mysterious problem
	        print "WARNING: some problem in sortLabels.\n"
	    	    . ">> $err\n"
        	    . ">>", $rr->toString(), "\n";
	   	    return undef;
		}

		# Does product match excluded pattern?
		if ( $iprod >= 0 )
		{
			foreach my $patt_excl ( @{$rr->Pexclude->[$iprod]} )
			{
				if ( $patt_excl->isomorphicToSubgraph($p) )
				{
		            # Abort this reaction
                    return undef;
				}
			}

            # Does product match included pattern?  Must do so if include patterns are specified.
            if ( @{$rr->Pinclude->[$iprod]} )
			{
			    my $include = 0;
			    foreach my $patt_incl ( @{$rr->Pinclude->[$iprod]} )
			    {
				    if ( $patt_incl->isomorphicToSubgraph($p) )
				    {
				    	$include = 1;
				    	last;
				    }
			    }
			    return undef unless $include;
		    }
		}
    }


    # if reactant is isomorphic to a population graph, then substitute
    if ( defined $model->PopulationList )
    {
        # first replace reactants
        foreach my $reactant ( @$reactants )
        {
            # skip reactant if it's not a species!
            next unless ( defined $reactant->Species );
            foreach my $pop ( @{$model->PopulationList->List} )
            {
                if ( $pop->SpeciesGraph->isomorphicToSubgraph($reactant) )
                {
                    # replace reactant with population and transfer pattern label (the "Ref", not the "ID")
                    my $label = $reactant->Label();
                    $reactant = $pop->Population->copy();
                    $reactant->Label( $label );
                    last;
                }
            }
        }
        # now products
        foreach my $product ( @$products )
        {
            foreach my $pop ( @{$model->PopulationList->List} )
            {
                if ( $pop->SpeciesGraph->isomorphicToSubgraph($product) )
                {       
                    # replace product with population and transfer pattern label (the "Ref", not the "ID")
                    my $label = $product->Label();   
                    $product = $pop->Population->copy();
                    $product->Label( $label );
                    last;
                }
            }    
        }
    }
    # TODO: if population obscures a molecule rooted local function, we'll need to
    #  pre-compile the local function. But this can wait until we handle molecule rooted
    #  observables are implemented in BNG
         
    # remove unnecessary temporary labels
    SpeciesGraph::removeRedundantLabels( $reactants, $products, $temp_labels );
 
    # Create expanded rule
    my $child_rule = RxnRule::new();

    # write rule name (remove parantheses around "reverse")
    my $name = $rr->Name . '_v' . ((scalar (keys %{$rr->RxnLabels})) + 1);
    $name =~ s/\(reverse\)/_rev/g;
	    
    $child_rule->Name( $name );
    $child_rule->Reactants( $reactants );
    $child_rule->Products( $products );
    $child_rule->RateLaw( $rr->RateLaw->copy() );
    $child_rule->TotalRate( $rr->TotalRate );
    $child_rule->Priority( $rr->Priority );	    
    $child_rule->DeleteMolecules( $rr->DeleteMolecules );
    $child_rule->MoveConnected( $rr->MoveConnected );
        	    
    my $rinclude = [];
    my $rexclude = [];
    for ( my $ir = 0;  $ir < @$reactants; ++$ir )
    {
        my $reactant = $reactants->[$ir];
        if ( $reactant->isPopulationType($hybrid_model->MoleculeTypesList) )
        {   # no need to recheck include/exclude if reactant is a population
            push @$rinclude, [];
            push @$rexclude, [];
        }
        else
        {   
            push @$rinclude, [ map {$_->copy()->relinkCompartments($hybrid_model->CompartmentList)} @{$rr->Rinclude->[$ir]} ];
            push @$rexclude, [ map {$_->copy()->relinkCompartments($hybrid_model->CompartmentList)} @{$rr->Rexclude->[$ir]} ];
        }
    }
	    
    my $pinclude = [];
    my $pexclude = [];
    for ( my $ip = 0;  $ip < @$products; ++$ip )
    {
        my $product = $products->[$ip];
     
        if ( $ip >= @{$rr->Products}  or  $product->isPopulationType($hybrid_model->MoleculeTypesList) )
        {   # no need to recheck include/exclude if product is a population
            push @$pinclude, [];
            push @$pexclude, [];
        }
        else
        {
            push @$pinclude, [ map {$_->copy()->relinkCompartments($hybrid_model->CompartmentList)} @{$rr->Pinclude->[$ip]} ];
            push @$pexclude, [ map {$_->copy()->relinkCompartments($hybrid_model->CompartmentList)} @{$rr->Pexclude->[$ip]} ];
        }
    }	 
   
    $child_rule->Rinclude( $rinclude );
    $child_rule->Pinclude( $rexclude );
    $child_rule->Rexclude( $pinclude );
    $child_rule->Pexclude( $pexclude );
   

    # gather references
    my $rrefs = {};
    for ( my $i_sg = 0; $i_sg < @$reactants;  ++$i_sg)
    {
        my $sg = $reactants->[$i_sg];
        $sg->gatherLabels( $rrefs, $i_sg );
    }
    my $prefs = {};
    for ( my $i_sg = 0; $i_sg < @$products;   ++$i_sg)
    {
        my $sg = $products->[$i_sg];
        $sg->gatherLabels( $prefs, $i_sg );
    }
    $child_rule->RRefs( $rrefs );
    $child_rule->PRefs( $prefs );


    # construct class ID for this child rule
    # (et's be a little safe here and use the StringID instead of StringExact)
    my $stringID = join '+', (map {$_->toString()} @$reactants);
    $stringID .= '->';
    $stringID .= join '+', (map {$_->toString()} @$products[0..$n_products-1]);
    if ( @$products > $n_products )
    {
        $stringID .= '+' . join( '+', sort (map {$_->toString()} @$products[$n_products..$#$products]) );
    }
    $child_rule->StringID( $stringID );


    # Find mapping of reactants onto products
    if ( my $err = $child_rule->findMap( $hybrid_model->MoleculeTypesList ) )
    {
        send_warning( sprintf "Could not find reactant-product correspondence map for exapnded rule %s.", $child_rule->Name );
        return undef;
    }


    # Does child rule include a delete molecule transform?
    my $delmol = grep {$_ =~ /\./} @{$child_rule->MolDel};
    if ( $delmol )
    {
        unless ( $child_rule->DeleteMolecules )
        {
            # set delete molecules flag (NFsim requires this)
            $child_rule->DeleteMolecules(1);
            if ( @{$rr->MolDel} )
            {   # warn user since this may produce conflicts with delete species transforms
                send_warning( sprintf "DeleteMolecules flag added to rule %s.", $child_rule->Name );
            }
        }
    }


    # TODO: determine if we need to make any changes to handle MultScale
    $child_rule->RateLaw->Factor( 1 );
      
    # add this child rule to RxnLabels
    $rr->RxnLabels->{$stringID} = $child_rule;
    
	# all done!
    return $child_rule;
}



###
###
###



sub factorial
{
	my $n = shift;
	if   ( $n == 0 ) {  return 1;  }
	else             {  return $n * factorial( $n - 1 );  }
}



###
###
###

1;
