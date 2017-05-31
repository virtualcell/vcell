# $Id: ParamList.pm,v 1.11 2007/08/24 15:10:13 faeder Exp $

package ParamList;

# pragmas
#use strict;
#use warnings;
no warnings 'redefine';

# Perl Modules
use Class::Struct;

# XML Modules
use XMLReader;

# BNG Modules
use Param;
use Expression;
use BNGUtils("isReal","send_warning");



# Members
struct ParamList =>
{
    Parent    => 'ParamList',
    Array     => '@',
    Hash      => '%',
    Unchecked => '@'
};


###
###
###



sub copyConstant
{
    my $plist = shift @_;
 
    my $plist_copy = ParamList::new();
    foreach my $param ( @{$plist->Array} )
    {
        next unless ($param->Type eq 'Constant'  or  $param->Type eq 'ConstantExpression' );
        my $param_copy = $param->copyConstant( $plist );
        $plist_copy->add( $param_copy );
    }
    
    # check and sort paramlist
	if ( my $err = $plist_copy->check() ) {  print "complain about parameter check\n";  return undef;  }
	if ( my $err = $plist_copy->sort()  ) {  print "complain about parameter sort\n";   return undef;  }    
    
    return $plist_copy;
}



###
###
###



sub getNumParams
{
    my $plist = shift;
    return scalar @{$plist->Array};
}

sub size
{
    my $plist = shift;
    return scalar @{$plist->Array};
}


###
###
###



# Lookup a parameter by name.
#  Returns reference to parameter, if found.
#  Otherwise returns undefined.
#
# ($param, $err) = $plist->lookup($name);
sub lookup
{
    my $plist = shift @_;
    my $name  = shift @_;

    my $param;  
    if ( exists $plist->Hash->{$name} )
    {
        return $plist->Hash->{$name}, "";
    }
    elsif ( defined $plist->Parent )
    {
        return $plist->Parent->lookup($name), "";
    }
    else
    {
        return undef, "Parameter $name not defined";
    }
}


###
###
###


sub getChildList
{
    my $plist = shift;
    
    my $child = ParamList::new();
    $child->Parent( $plist );
    
    return $child;
}


###
###
###




# Find an un-used name in the parameter list
#  that begins with "basename".
# Don't put the name in the list yet!
sub getName
{
    my $plist    = shift;
    my $basename = (@_) ? shift : "k";
  
    my $name;
    # Find unused name
    my $index = 1;
    while (1)
    {
        my ( $param, $err ) = $plist->lookup( "${basename}_${index}" );
        last unless $param;
        ++$index;
    }
    $name = "${basename}_${index}";
    return $name;
}



###
###
###



sub evaluate
{
    my $plist  = shift @_;
    my $name   = shift @_;
    my $args   = @_ ? shift @_ : [];
    my $level  = @_ ? shift @_ : 0;  

    my ($param,$err) = $plist->lookup($name);
    if (defined $param)
    {
        # evaluate the parameter and return value
        return $param->evaluate($args, $plist, $level + 1);
    }
    elsif ( isReal($name) )
    {   
        # in a few cases, a simple number may appear where a parameter name is expected
        # (e.g. Concentration). In these cases, just return the value.
        return $name;
    }
    else
    {
        print "$name is not a valid number or parameter.";
        return undef;
    }
}



###
###
###



# parse parameter from a BNGL string
sub readString
{
    my $plist  = shift @_;
    my $string = shift @_;
    my $allow_undefined = @_ ? shift @_ : 0;

    my $sptr = \$string;
    my ($name, $val);

    # Remove leading whitespace
    $$sptr =~ s/^\s*//;
    
    # Remove leading numeric index, if any
    $$sptr =~ s/^\d+\s+//; # Can't deprecate this because indices used in NET files
    
    # Remove leading label, if any
	if ( $$sptr =~ s/^(\w+)\s*:\s+// )
	{
		# Check label for leading number
		my $label = $1;
		if ($label =~ /^\d/) {  return "Syntax error (label begins with a number) at '$label'";  }
	}
		
	# Check name for leading number
	my $sptr_left = $$sptr;
	unless ( $sptr_left =~ s/^([A-Za-z_]\w*)// )
	{ 
		return "Syntax error (parameter name begins with a number) at '$$sptr'";
	}

    # Convert non assignment format to assignment
    unless ( $$sptr =~ /^\w+\s*=/ )
    {
        unless ( $$sptr =~ s/^(\w+)\s*(.+)/$1=$2/ )
        {
            return "Invalid parameter declaration $$sptr: format is [index] name[=]Expression";
        }
    }

    # Read expression
    my $expr = Expression->new();
    $expr->setAllowForward(1);
    if ( my $err = $expr->readString($sptr,$plist) ) {  return $err;  }

    # string should be empty now.
    if ($$sptr) {  return "Syntax error (invalid trailing characters) at $$sptr";  }
    $expr->setAllowForward(0);

    return '';
}


###
###
###


# parse parameters from XML file
sub readXML
{
    my $plist = shift @_;
    my $xml   = shift @_;
    my $allow_overwrite = @_ ? shift @_ : 0;
    my $verbose         = @_ ? shift @_ : 0;

    # error message
    my $err;
    # counters
    my $n_pars_new   = 0;
    my $n_pars_redef = 0;

    my $param_array = $xml->getParameters();
    foreach my $param_hash ( @$param_array )
    {
        (my $param, $err) = Parameter->readXML($param_hash, $plist);
        if ($err) { return $err; }

        # lookup parameter name
        (my $old_param, $err) = $plist->lookup($param->Name);
        if ($err) { return $err; }

        # add parameter to Plist
        if ( defined $old_param  and  defined $old_param->Expr )
        {   # if a parameter with this name is already defined..
            if ($allow_overwrite)
            {   # ..copy new param definition over old_param
                %$old_param = %$param;
                undef %$param;
                $param = $old_param;
                ++$n_pars_redef;
            }
            else
            {   # redefining parameters is not allowed
                $err = "Parameter->readXML: attempt to redefine parameter $name is not allowed"; 
                return $err;
            }
        }
        else
        {
            # add to plist array, unless type is Local or RRef
            unless ( $param->Type eq 'Local'  or  $param->Type eq 'RRef' )
            {   # add param to Plist Array
                push @{$plist->Array}, $param;
                # add param to unchecked list
                push @{$plist->Unchecked}, $param;
            }
            # add param to Plist hash
            $plist->Hash->{ $param->Name } = $param;
            # increment counter
            ++$n_pars_new;
        }
    }

    if ($verbose)
    {   # tell user how many parameters were defined or redefined
        print sprintf( "Parameter->readXML: read %d new parameters and redefined %d parameters.\n", $n_pars_new, $n_pars_redef );
    }

    return undef;
}



###
###
###



# add parameter object to list, or overwrite exisitng parameter
sub add
{
    use strict;
    use warnings;

    my $plist = shift @_;
    my $param = shift @_;
    my $no_overwrite = @_ ? shift @_ : 0;
    my $global       = @_ ? shift @_ : 0;

    #printf "ParamList->add( %s, %d, %d )\n", $param->Name, $no_overwrite, $global;

    if ($global)
    {   # try to add to parent
        if ( defined $plist->Parent )
        {   return $plist->Parent->add( $param, $no_overwrite, $global );   }
    }

    # Find existing parameter
    (my $old_param) = $plist->lookup( $param->Name );
    
    # Is this a new parameter definition or a redefinition?
    if ( defined $old_param )
    {   # overwriting exisitng parameter!
        if ( $no_overwrite )
        {   return sprintf "Redefining existing parameter '%s' is not permitted", $param->Name;   }

        # check if new param has type Local or RRef
        if ( $param->Type eq 'Local'  or  $param->Type eq 'RRef' )
        {
            return sprintf( "Redefining non-local parameter '%s' as a local parameter is not permitted",
                       $param->Name );
        }

        # find old parameter in array
        for ( my $idx = 0; $idx < @{$plist->Array}; ++$idx )
        {
            my $old_param = $plist->Array->[$idx];
            if( $old_param->Name eq $param->Name )
            {   # replace old parameter in array with new parameter
                $plist->Array->[$idx] = $param;
                # add this to list of parameters to check
                push @{$plist->Unchecked}, $param;
                # exit loop
                last;
            }
        }
    }
    else
    {   # this is a new parameter.
        # add to array unless the type is local
        unless ( $param->Type eq 'Local'  or  $param->Type eq 'RRef' )
        {
            push @{$plist->Array}, $param;
            push @{$plist->Unchecked}, $param;
        }
    }

    # always add new parameter to lookup table
    $plist->Hash->{ $param->Name } = $param;
    # all done
    return undef;
}


# ParamList->set( name, expression ) creates a parameter object from a name and an expression (or reference)
#  and then adds the parameter to the list. By default, allows previously defined variable to be overwritten.
#  Use $no_overwrite=1 to prevent this.
sub set
{
    my $plist        = shift @_;
    my $name         = shift @_;
    my $rhs          = @_ ? shift @_ : '';    # this is the expression!
    my $no_overwrite = @_ ? shift @_ : 0;
    my $type         = @_ ? shift @_ : '';    # Overrides derived type of rhs
    my $ref          = @_ ? shift @_ : '';    # Reference to Function or Observable
    my $global       = @_ ? shift @_ : 0;     # add parameter to top plist

    if ($global)
    {   # find top plist
        while (defined $plist->Parent)
        {
            $plist = $plist->Parent;
        }
    }

    # Find existing parameter
    my ($param,$err)= $plist->lookup($name);
    # or add new parameter to array
    unless (defined $param)
    {
        #print "Adding parameter $name\n";
        $param = Param->new( Name=>$name );
        # add to array, unless Local or RRef
        unless ( $type eq 'Local'  or  $type eq 'RRef' )
        {   push @{$plist->Array}, $param;   }
        # add to hash
        $plist->Hash->{$name} = $param;
        # Add parameter to list of parameters to be checked
        unless ( $type eq 'Local'  or  $type eq 'RRef' )
        {   push @{$plist->Unchecked}, $param;   }
        # Return leaving param unset if no rhs
#        if ($rhs eq '')
#        {
#        	return '';   
#        }
    }

	# Return leaving param unset if no rhs
    if ($rhs eq '')
    {
     	return '';   
    }

#    if ($param->Expr ne '')
    if (defined $param->Expr)
    {
        if ($no_overwrite)
        {   return "Changing value of previously defined variable '$name' is not allowed";   }
        else
        {   send_warning( "Changing value of previously defined variable '$name'" );   }
    }

    # Handle scalar (string) argument (probably from setParameter)
    if ( ref \$rhs eq 'SCALAR' )
    {
        my $expr = Expression->new( Type=>'NUM', Arglist=>[$rhs] );
        $rhs = $expr;
    } 

    # Set Param->Expression
    $param->Expr($rhs);
    
    # Set Param->Type
    if ($type ne '')
    {
        $param->setType($type);
        # Set reference
        if ($ref)
        {   $param->Ref($ref);   }
        elsif ( $type eq 'Function' )
        {
            # create a function out of the RHS expression
            my $fun = Function->new();   
            $fun->Name($param->Name);
            $fun->Expr($param->Expr);
            # look for variables with expression
            my $vhash = $param->Expr->getVariables($plist);       
            #  find local variables and assign as function arguments
            my @args = keys %{$vhash->{Local}};
            $fun->Args([@args]);           
            # finally, point param Ref field to this function
            $param->Ref($fun);                
        }
    }
    elsif ($rhs->Type eq 'NUM')
    {
        $param->setType('Constant');
    }
    else
    {
        # Get hash of variables reference in Expr
        my $vhash = $param->Expr->getVariables($plist);
        # If expression has undefined parameter types make it a 'Function' type to be safe
        if ( exists $vhash->{'Observable'} || exists $vhash->{'Function'} || exists $vhash->{'UNDEF'} ) 
        {
            $param->setType('Function');
            my $fun= Function->new();
            $fun->Name($param->Name);
            $fun->Expr($param->Expr);
            my @args = keys %{$vhash->{Local}};
            $fun->Args([@args]);
            $param->Ref($fun);
        }
        elsif ( $vhash->{'Constant'} or $vhash->{'ConstantExpression'} )
        {
            $param->setType('ConstantExpression');
        }
        else
        {
            # Expression contains only number arguments
            $param->setType('Constant');
        }
    }

    return '';
}



###
###
###



sub toString
{
    my $plist = shift;

    my $out = '';
    foreach my $param ( @{$plist->Array} )
    {
        $out .= sprintf "Parameter %s=%s\n", $param->Name, $param->toString($plist);
    }
    return $out;
}



###
###
###



# This serves an input file in SSC which contains information corresponding to our parameters block in BNG
sub writeSSCcfg
{
    my $plist = shift;   

    my $out = "# begin parameters\n";
    my $iparam = 1;
    foreach my $param ( @{$plist->Array} )
    {
        my $type = $param->Type;
        next unless ( $type =~ /^Constant/ );
        $out .= " " . $param->Name . " = ";
        $out .= $param->evaluate([],$plist) . "\n";
        ++$iparam;
    }
    $out .= "# end parameters\n";

    return $out;
}



###
###
###



sub writeBNGL
{
    my $plist       = shift @_;
    my $user_params = @_ ? shift @_ : { 'pretty_formatting'=>0, 'evaluate_expressions'=>0 };

    # find longest parameter name
    my $max_length = 0;
    foreach my $param (@{$plist->Array})
    {
        $max_length = ($max_length >= length $param->Name) ? $max_length : length $param->Name;
    }

    # now write parameter strings
    my $iparam = 1;
    my $out .= "begin parameters\n";
    foreach my $param (@{$plist->Array})
    {
        next unless ( $param->Type =~ /^Constant/ ); 

        if ( $user_params->{'pretty_formatting'} )
        {   # no parameter index
            $out .= '  ';
        }
        else
        {   # include index
            $out .= sprintf "%5d ", $iparam;
        }

        $out .= sprintf "%-${max_length}s  ", $param->Name;

        if ( $user_params->{'evaluate_expressions'} )
        {   # evaluate expression (return a number)
     	    $out .= $param->evaluate([], $plist);            
        }
        else
        {   # write parameter expression as a string
            $out .= $param->toString($plist);

        }

        if ( $user_params->{'pretty_formatting'} )
        {   # no parameter type
            $out .= "\n";
        }
        else
        {   # include parameter type
            $out .= "  # " . $param->Type . "\n";
        }

        ++$iparam; 
    }
    $out .= "end parameters\n";
    
    return $out;
}



###
###
###



sub writeFunctions
{
    my $plist        = shift @_;
    my $user_params  = @_ ? shift @_ : { 'pretty_formatting'=>0 };

    my $max_length = 0;
    if ( $user_params->{pretty_formatting} )
    {
        # find longest function name
        foreach my $param ( @{$plist->Array} )
        {
		    my $type= $param->Type;
            next unless ( $param->Type eq 'Function' );
        
            my $string = $param->Ref->toString( $plist, 1);
            $string =~ /\=/g;
            $max_length = ( pos $string > $max_length ) ? pos $string : $max_length;
        }
    }

    my $out = "begin functions\n";
    my $iparam=1;
    foreach my $param ( @{$plist->Array} )
    {
		my $type= $param->Type;
        next unless ( $param->Type eq 'Function' );
        
        if ( $user_params->{'pretty_formatting'} )
        {   # no function index
            $out .= '  ' . $param->Ref->toString( $plist, 1, $max_length) . "\n";
        }
        else
        {   # include function index
            next if ( $param->Ref->checkLocalDependency($plist) );
            $out .= sprintf "%5d", $iparam;
            $out .= ' ' . $param->Ref->toString( $plist, 0) . "\n";
        }
        ++$iparam;
    }
    $out.="end functions\n";
	
	# Don't output null block
    if ( $iparam==1 ){  $out = '';  }
 
    return $out;
}



###
###
###



sub copyFunctions
{
    my $plist = shift;
    
    my $fcn_copies = [];
    foreach my $param ( @{$plist->Array} )
    {
        if ( $param->Type eq 'Function' )
        {
            # arguments are: parameter list, level, same_name
            my ($fcn_copy,$err) = $param->Ref->clone( $plist, 0, 1 );
            push @$fcn_copies, $fcn_copy;
        }
    }
    
    return $fcn_copies;
}



###
###
###



# Delete a parameter from the ParamList by name
sub deleteLocal
{
    my $plist = shift;
    my $pname = shift;
    
    # Find parameter
    my ($param,$err) = $plist->lookup($pname);
    if ($err) {  return($err);  }
    
    # check that this is a local type
    if ($param->Type ne 'Local') {  return "Parameter $pname is not a local parameter";  }
    
    # remove param from lookup hash
    delete $plist->Hash->{$pname};
    
    # undefine parameter object
    %{$param} = ();
    $param = undef;

    return '';
}



###
###
###



# Delete a parameter from the ParamList by name
sub deleteParam
{
    my $plist = shift;
    my $pname = shift;
    
    # Find parameter
    my ($param,$err) = $plist->lookup($pname);
    if ($err) {  return($err);  }
    
    # remove param from lookup hash
    delete $plist->Hash->{$pname};
    
    # remove param from array (expensive)
    my $index = @{$plist->Array};
    while ($index > 1)
    {
        --$index;
        if ( $param == $plist->Array->[$index] )
        {
            splice( @{$plist->Array}, $index, 1);
            last;
        }
    }
  
    # remove param from unchecked (expensive)
    $index = @{$plist->Unchecked};
    while ($index > 1)
    {
        --$index;
        if ( $param == $plist->Unchecked->[$index] )
        {
            splice( @{$plist->Unchecked}, $index, 1);
        }
    }

    # undefine parameter object
    undef %{$param};
    $param = undef;

    return '';
}



###
###
###



# check the parameter list undefined or cyclic dependency
sub check
{
    my $plist = shift;
    my $err  = '';
  
    foreach my $param (@{$plist->Unchecked})
    {
        # Check that variable has defined value
        #printf "Checking if parameter %s is defined.\n", $param->Name;
        unless ( $param->Type )
        {
            $err= sprintf "Parameter '%s' is referenced but not defined", $param->Name;
            last;
        }
    }
    if ($err) { return ($err) };

    foreach my $param ( @{$plist->Unchecked} )
    {
        #printf "Checking parameter %s for cycles.\n", $param->Name;
        # Check that variable doesn't have cylic dependency
        (my $dep, $err) = ($param->Expr->depends( $plist, $param->Name ));
        if ($dep)
        {
            $err= sprintf "Parameter %s has a dependency cycle %s", $param->Name, $param->Name.'->'.$dep;
            last;
        }
    }
  
    # Reset list of Unchecked parameters if all parameters passed checks.
    unless ( $err )
    {
        undef @{$plist->Unchecked};
        #printf "Unchecked=%d\n", scalar(@{$plist->Unchecked});
    }
    return ($err);
}



###
###
###



# sort the Array of parameters by dependency
{
    my $plist;
    my $err;
    sub sort
    {
        $plist = shift;
        $err   = '';
    
        $plist->Array( [sort by_depends @{$plist->Array}] );
        return($err);
    }

    sub by_depends
    {
        (my $dep_a, $err) = $a->Expr->depends( $plist, $b->Name );
        if ($err)
        {   #printf "$err %s %s\n", $a->Name, $b->Name;
            return(0);
        }

        if ($dep_a)
        {   #printf "%s depends on %s\n", $a->Name, $b->Name;
            return(1);
        }

        (my $dep_b, $err) = $b->Expr->depends( $plist, $a->Name );
        if ($err)
        {
            return(0);
        }
    
        if ($dep_b)
        {   #printf "%s depends on %s\n", $b->Name, $a->Name;
            return(-1);
        }

        #printf "%s and %s are independent\n", $a->Name, $b->Name;
        return(0);
    }
}



###
###
###



# Assign indices to Parameters. BNG doesn't use this, but it's helpful
#  for writing network models in vector form.
#  NOTE:  Constant and ConstantExpression types are indexed separately
#   from Observable types.  Function and Local types not indexed.
sub indexParams
{
    my $plist = shift;
    my $err;  
    
    # check parameter list for undefined and cyclic dependency
    ($err) = $plist->check();
    if ( $err ) { return ( undef, $err) };
    
    # sort paramlist by dependency!
    ($err) = $plist->sort();
    if ( $err ) { return ( undef, $err) };    

    # index parameter types
    my $n_expressions = 0;
    my $n_observables = 0;
        
    # loop through parameters and generate
    foreach my $param ( @{ $plist->Array } )
    {
        my $type = $param->Type;
        my $expr = $param->Expr;
        if    ( $type eq 'Constant')
        {
            $param->Index( $n_expressions );
            $param->CVodeRef( "NV_Ith_S(expressions,$n_expressions)" );
            ++$n_expressions;
        }
        elsif ( $type eq 'ConstantExpression' )
        {        
            $param->Index( $n_expressions );
            $param->CVodeRef( "NV_Ith_S(expressions,$n_expressions)" );
            ++$n_expressions;    
        }
        elsif ( $type eq 'Observable' )
        {
            $param->Index( $n_observables );
            $param->CVodeRef( "NV_Ith_S(observables,$n_observables)" );
            ++$n_observables;
        }
        else
        {
            $param->Index( undef );
            $param->CVodeRef( '' );
        }
    }

    return ($err);
}



###
###
###



# return a string with CVode expression defintions.
#  Call "indexParams" before this!
sub getCVodeExpressionDefs
{
    my $plist = shift;
    
    # !!! Assume that parameter list is checked and sorted by dependency !!!

    # expression definition string
    my $expr_defs = '';
    # to hold errors..
    my $err;  
    # count constants
    my $n_constants   = 0;   
    # size of the indent
    my $indent = '    ';
    
    # loop through parameters and generate
    foreach my $param ( @{ $plist->Array } )
    {
        # get type
        my $type = $param->Type;
        if    ( $type eq 'Constant')
        {
            # constants are defined in terms of the input parameters
            $expr_defs .= $indent . $param->getCVodeName() . " = parameters[$n_constants];\n";
            ++$n_constants;
        }
        elsif ( $type eq 'ConstantExpression' )
        {   
            # constant expressions are defined in terms of other expressions
            $expr_defs .= $indent . $param->getCVodeName() . " = " .  $param->Expr->toCVodeString( $plist ) . ";\n";    
        }
    }

    return ($expr_defs, $err);
}



###
###
###



# return a string with Matlab expression defintions.
#  Call "indexParams" before this!
sub getMatlabExpressionDefs
{
    my $plist = shift;
    
    # !!! Assume that parameter list is checked and sorted by dependency !!!

    # expression definition string
    my $expr_defs = '';
    # to hold errors..
    my $err;  
    # count constants
    my $n_constants = 0;   
    # size of the indent
    my $indent = '    ';
    # matlab array index offset
    my $offset = 1;
    
    # loop through parameters and generate
    foreach my $param ( @{ $plist->Array } )
    {
        # get type
        my $type = $param->Type;
        if    ( $type eq 'Constant')
        {
            # constants are defined in terms of the input parameters
            $expr_defs .= $indent . $param->getMatlabName() . " = parameters(" . ($n_constants + $offset) . ");\n";
            ++$n_constants;
        }
        elsif ( $type eq 'ConstantExpression' )
        {   
            # constant expressions are defined in terms of other expressions
            $expr_defs .= $indent . $param->getMatlabName() . " = " .  $param->Expr->toMatlabString( $plist ) . ";\n";    
        }
    }

    return ($expr_defs, $err);
}



###
###
###



# return a string with CVode observable defintions.
sub getCVodeObservableDefs
{
    my $plist = shift;

    # expression definition string
    my $obsrv_defs = '';
    # to hold errors..
    my $err;   
    # size of the indent
    my $indent = '    ';
    
    # loop through parameters and generate observable definitions
    foreach my $param ( @{ $plist->Array } )
    {
        if ( $param->Type eq 'Observable')
        {
            my $obsrv = $param->Ref;
            # constants are defined in terms of the input parameters
            $obsrv_defs .= $indent . $param->getCVodeName() . " = " . $obsrv->toCVodeString($plist) . ";\n";
        }
    }

    return ($obsrv_defs, $err);
}



###
###
###



# return a string with Matlab observable defintions.
sub getMatlabObservableDefs
{
    my $plist = shift;

    # expression definition string
    my $obsrv_defs = '';
    # to hold errors..
    my $err;   
    # size of the indent
    my $indent = '    ';
    
    # loop through parameters and generate observable definitions
    foreach my $param ( @{ $plist->Array } )
    {
        if ( $param->Type eq 'Observable')
        {
            my $obsrv = $param->Ref;
            # constants are defined in terms of the input parameters
            $obsrv_defs .= $indent . $param->getMatlabName() . " = " . $obsrv->toMatlabString($plist) . ";\n";
        }
    }

    return ($obsrv_defs, $err);
}



###
###
###



# get names of constnat parameters and default values in a string that define
# matlab arrays
sub getMatlabConstantNames
{
    my $plist = shift @_;
    
    my $err;
    
    my @default_values = ();
    my @constant_names = ();  

    # loop through parameters and generate names and values for constants
    foreach my $param ( @{ $plist->Array } )
    {
        if ( $param->Type eq 'Constant')
        {
  		    push @default_values, $param->evaluate([], $plist); 
  		    push @constant_names, "'" . $param->Name . "'";
        }    
    }
    
    return ( join(', ', @constant_names), join(', ', @default_values), $err );
}



###
###
###



# get names of observables for matlab
sub getMatlabObservableNames
{
    my $plist = shift;
    
    my $err;
    
    my @observable_names = ();  
    # loop through params and find observables
    foreach my $param ( @{ $plist->Array } )
    {
        if    ( $param->Type eq 'Observable')
        {
	        push @observable_names, "'" . $param->Name . "'";
        }    
    }
    
    return ( join(', ', @observable_names), $err );
}



###
###
###


 
# count the number of parameters with a type
sub countType
{
    my $plist = shift;
    my $type  = shift;
    
    my $count = 0;
    foreach my $param ( @{$plist->Array} )
    {
        ++$count  if ( $param->Type  eq $type );
    }
    return $count;
}



###
###
###

1;
