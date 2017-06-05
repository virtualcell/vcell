package Function;

# pragmas
use strict;
use warnings;

# Perl Modules
use Class::Struct;
use FindBin;
use lib $FindBin::Bin;

# BNG Modules
use ParamList;
use Expression;



struct Function =>
{
    Name => '$',
    Args => '@',
    Expr => 'Expression',
    LocalHash => '%',
};


###
###
###


# create a copy of this function.
# 
sub clone
{
    my $fcn   = shift @_;
    my $plist = @_ ? shift @_ : undef;
    my $level = @_ ? shift @_ : 0;
    my $named = @_ ? shift @_ : 0;

    # if the clone doesn't have a name, it will be anonymous
    my $clone_name = $named ? $fcn->Name : undef;

    my $err = '';
    (my $clone_expr, $err) = $fcn->Expr->clone( $plist, $level+1 );
    
    my $clone = Function->new( Name=>$clone_name, Expr=>$clone_expr, Args=>[@{$fcn->Args}] );
    return $clone, $err;
}


###
###
###

sub evaluate
{
    my $fcn   = shift @_;
    my $args  = @_ ? shift @_ : [];
    my $plist = @_ ? shift @_ : undef;
    my $level = @_ ? shift @_ : 0;

    # create local parameterList
    my $local_plist = $plist->getChildList();
    
    # set local variables equal to the arguments
    my $ii=1;  # first arg is function name
    while ( $ii < @$args )
    {                
        $local_plist->set( $fcn->Args->[$ii-1], $args->[$ii], 1, $args->[$ii]->Type );
        ++$ii;
    }
       
    # evaluate function expression
    return $fcn->Expr->evaluate($local_plist, $level+1);
}


###
###
###



# return a cloned function with local elements evaluated
sub evaluate_local
{
    my $fcn   = shift;
    my $args  = (@_) ? shift : [];
    my $plist = (@_) ? shift : undef;
    my $level = (@_) ? shift : 0;

    my ($local_fcn, $err) = $fcn->clone( $plist, $level+1 );
    
    # create local parameterList
    my $local_plist = $plist->getChildList();
    # set local variables equal to the arguments
    my $ii=1;  # first arg is function name
    while ( $ii < @$args )
    {
        $local_plist->set( $fcn->Args->[$ii-1], $args->[$ii], 1 );
        ++$ii;
    }
    
    # locally evaluate function expression
    my $expr = $local_fcn->Expr->evaluate_local($local_plist, $level+1);
    $local_fcn->Expr($expr);

    # check if local variables are unused
    my $bad_args = [];
    $ii=0;
    while ( $ii < @{$local_fcn->Args} )
    {
        my ($dep, $err) = $expr->depends( $plist, $fcn->Args->[$ii] );
        if ($dep)
        {
            # do nothing
            ++$ii;
        }
        else
        {
            # remove argument
            splice @{$local_fcn->Args}, $ii, 1;
            push @$bad_args, $ii+1;
        }
    }    

    return $local_fcn, $bad_args;
}


###
###
###


# check for local observable dependency, return true if found
sub checkLocalDependency
{
    my $fcn = shift;
    my $plist = (@_) ? shift : undef;
    my $level = (@_) ? shift : 0;

    return  $fcn->Expr->checkLocalDependency( $plist, $level+1 );
}


###
###
###


# check function equivalence
sub equivalent
{
    my $fcn1 = shift;
    my $fcn2 = shift;
    my $plist = (@_) ? shift : undef;
    
    # make sure we have defined expressions!
    return 0  unless ( defined $fcn1  and  ref $fcn1 eq 'Function' );
    return 0  unless ( defined $fcn2  and  ref $fcn2 eq 'Function' );
    
    # check if this is the same function object!
    return 0  if ( $fcn1 == $fcn2 );
    
    # don't compare names!!!
    
    # check for same number of arguments
    return 0  unless ( @{$fcn1->Args} == @{$fcn2->Args} );
    
    # compare arguments
    for ( my $i = 0;  $i < @{$fcn1->Args};  ++$i )
    {
        return 0 unless ( $fcn1->Args->[$i] eq $fcn2->Args->[$i] );
    }
    
    # check Expr equivalence
    return 0  unless ( Expression::equivalent($fcn1->Expr, $fcn2->Expr, $plist) );

    # no differences found, return true!  
    return 1;
}


###
###
###


sub readString
{
    my $fun    = shift;
    my $string = shift;
    my $model  = shift;
    my $err    = '';

    my $plist = $model->ParamList;
    
    # Remove leading whitespace
    $string =~ s/^\s*//;

    # Check if first token is an index
    $string =~ s/^\s*\d+\s+//; # Can't deprecate this because indices used in NET files

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
		return "Syntax error (function name begins with a number) at '$string'";
	}

    # Next token is function Name
    if ( $string =~ s/^\s*([A-Za-z0-9_]+)\s*// )
    {
        my $name = $1;
        $fun->Name($name);
    
        # Make sure function name not the same as built-in functions --Leonard
		if ( Expression::isBuiltIn($name) ){
			return "Cannot use built-in function name '$name' as a user-defined function name.";
		}
    }
    else
    {
        my ($name) = split( ' ', $string );
        return ("Invalid function name '$name': may contain only alphanumeric characters and underscore");
    }

    # Process arguments to function (if any)
    my @Args = ();
    if ( $string =~ s/^[(]\s*// )
    {
        while (1)
        {
            if ( $string =~ s/^\s*([A-Za-z0-9_]+)\s*// )
            {
                my $arg = $1;
                # Define argument as an allowed local variable in $plist
                if ( $plist->set( $arg, '0', 1, 'Local' ) )
                {
                    my $name = $fun->Name;
                    return ("Local argument $arg to Function $name matches previously defined variable");
                }
                #printf "Added argument %s to function %s\n", $arg, $fun->Name;
                push @Args, $arg;
            }
            elsif ( $string =~ s/^[,]\s*// )
            {
                next;
            }
            elsif ( $string =~ s/^[)]\s*// )
            {
                last;
            }
            else
            {
                my $name= $fun->Name;
                return ("Unrecognized argument at $string in declaration of function $name.");
            }
        }
        $fun->Args( [@Args] );
    }

    # Remove '=' if present (but NOT '==')
    $string=~ s/(?<!=)[=](?!=)\s*//;# use lookarounds to avoid matching '=='

    # Read expression defining function.  Function arguments are "local" variables
    my $expr = Expression->new();
    $expr->setAllowForward(1);  # don't complain if expression refers to undefined parameters
    if ( my $err = $expr->readString( \$string, $plist ) ) {  return ($err);  }
    if ($string) {  return ("Syntax error at $string");  }
    $expr->setAllowForward(0);

    $fun->Expr($expr);

    # Define parameter with name of the Function
    if ( $plist->set( $fun->Name, $expr, 1, "Function", $fun ) )
    {
        my $name = $fun->Name;
        return ("Function name $name matches previously defined variable");
    }

    $fun->unsetArgs($plist);

    return '';
}


###
###
###


sub toString
{
    my $fun   = shift @_;
    my $plist = @_ ? shift @_ : undef;
    my $include_equal = (@_) ? shift : 0;
    # used for aligning columns nicely
    my $max_length    = (@_) ? shift : 0;
    
    my $name = defined $fun->Name ? $fun->Name : "anon";
    my $string = $name . '(' . join(',', @{$fun->Args}) . ')';
    if ( $fun->Expr )
    {
        $string .= ($include_equal) ? ' = ' : ' ';
        $string .= $fun->Expr->toString($plist);
    }

    if ($include_equal and $max_length)
    {   # align equal signs
        $string =~ /=/g;
        my $n_spaces = $max_length - pos $string;
        if ($n_spaces > 0)
        {
            my $spaces = ' ' x $n_spaces;
            $string =~ s/=/$spaces=/;
        }
    }

    return $string;
}


###
###
###


sub toCVodeString
# construct a call, declaration or definition for this function in CVode.
{
    my $fcn     = shift;                 # this function
    my $plist   = (@_) ? shift : undef;  # reference to ParamList
    my $arghash = (@_) ? shift : {};     # reference to argument hash
   
    # set default mode
    unless ( exists $arghash->{fcn_mode} )
    {    $arghash->{fcn_mode} = 'call';   }

    # set default indent
    unless ( exists $arghash->{indent} )
    {    $arghash->{indent} = '';   }
   
    my $string = '';
    if ( $arghash->{fcn_mode} eq 'call' )
    {
        # generate the function call
        my @args = ( @{$fcn->Args}, 'expressions', 'observables' );
        $string = $arghash->{indent} . $fcn->Name . '(' . join(',', @args) . ')';
    }
    elsif ( $arghash->{fcn_mode} eq 'declare' ) 
    {
        # generate a declaration string
        my @args = ( (map { "double $_" } @{$fcn->Args}), 'N_Vector expressions', 'N_Vector observables' );
        $string = $arghash->{indent} . "double " . $fcn->Name . " ( " . join(', ', @args) . " );\n";
    }
    elsif ( $arghash->{fcn_mode} eq 'define' )
    {
        # generate a definition string
        my @args = ( (map { "double $_" } @{$fcn->Args}), 'N_Vector expressions', 'N_Vector observables' );

        $string .= $arghash->{indent} . "/* user-defined function " . $fcn->Name . " */\n";
        $string .= $arghash->{indent} . "double " . $fcn->Name . " ( " . join(', ', @args) . " )\n";
        $string .= $arghash->{indent} . "{\n";
        $string .= $arghash->{indent} . "    return " . $fcn->Expr->toCVodeString($plist) . ";\n"; 
        $string .= $arghash->{indent} . "}\n";     
    }
    else
    {    die "Error in Function->toCVodeString(): did not recognize fcn_mode argument!";   }

    return $string;
}



###
###
###



sub toMatlabString
# construct a call, declaration or definition for this function in CVode.
{
    my $fcn     = shift @_;               # this function
    my $plist   = @_ ? shift @_ : undef;  # reference to ParamList
    my $arghash = @_ ? shift @_ : {};     # reference to argument hash
   
    # set default mode
    unless ( exists $arghash->{fcn_mode} )
    {    $arghash->{fcn_mode} = 'call';   }

    # set default indent
    unless ( exists $arghash->{indent} )
    {    $arghash->{indent} = '';   }
   
    my $string = '';
    if ( $arghash->{fcn_mode} eq 'call' )
    {
        # generate the function call
        my @args = ( @{$fcn->Args}, 'expressions', 'observables' );
        $string = $arghash->{indent} . $fcn->Name . '(' . join(',', @args) . ')';
    }
    elsif ( $arghash->{fcn_mode} eq 'declare' ) 
    {
        # generate a declaration string
        # NOTHING TO DO: Matlab does not require function declarations
        $string = '';
    }
    elsif ( $arghash->{fcn_mode} eq 'define' )
    {
        # generate a definition string
        my @args = ( @{$fcn->Args}, 'expressions', 'observables' );
        $string .= $arghash->{indent} . '% function ' . $fcn->Name . "\n";
        $string .= $arghash->{indent} . 'function [val] = ' . $fcn->Name . '(' . join(', ', @args) . ")\n"
                                      . '    val = ' . $fcn->Expr->toMatlabString($plist) . ";\n"
                                      . "end\n";                      
    }
    else
    {    die "Error in Function->toMatlabString(): did not recognize fcn_mode argument!";   }

    return $string;
}



###
###
###



sub setArgs
{
    my $fun= shift;
    my $plist= shift;
  
    foreach my $arg ( @{$fun->Args} )
    {
        $plist->set( $arg, '0', 1, 'Local' ); 
    }
    return '';
}



###
###
###



sub unsetArgs
{
    my $fun   = shift;
    my $plist = shift;
  
    # Delete ParamList entries for Local arguments
    foreach my $arg ( @{$fun->Args} )
    {
        $plist->deleteLocal($arg);
    }
    return '';
}



###
###
###



sub toXML
{
    my $fun    = shift;
    my $plist  = (@_) ? shift : '';
    my $indent = (@_) ? shift : '';

    $fun->setArgs($plist);

    my $indent2 = '  ' . $indent;
    my $indent3 = '  ' . $indent2;
    my $string  = $indent . "<Function";

    # Attributes
    # id
    $string .= " id=\"".$fun->Name."\"";
    $string .= ">\n";

    # Arguments
    if ( @{$fun->Args} )
    {
        $string.= $indent2 . "<ListOfArguments>\n";
        foreach my $arg (@{$fun->Args})
        {
            $string .= $indent3 . "<Argument";
            $string .= " id=" . "\"" . $arg . "\"";
            $string .= "/>\n"
        }
        $string .= $indent2 . "</ListOfArguments>\n";
    }

    # References
    $string.= $indent2."<ListOfReferences>\n";
    my $vhash= $fun->Expr->getVariables($plist);
  
    foreach my $type (sort keys %{$vhash})
    {
        foreach my $var (sort keys %{$vhash->{$type}})
        {
            #print "$type $var\n";
            $string .= $indent3 . ($vhash->{$type}->{$var})->toXMLReference('Reference', '', $plist);
        }    
    }
    $string .= $indent2 . "</ListOfReferences>\n";
    
    $string .= $indent2 . "<Expression> ";
    $string .= $fun->Expr->toXML($plist);
    $string .= " </Expression>\n";

    $string .= $indent."</Function>\n";

    $fun->unsetArgs($plist);

    return ($string);
}





1;
