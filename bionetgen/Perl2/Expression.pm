# $Id: Expression.pm,v 1.7 2007/02/20 17:39:38 faeder Exp $

# updated by msneddon, 2009/11/04
#   -added if statement as built in function
#   -added binary logical operators, <,>.<=,>=,==,!=,~=,&&,||
#    to the basic functional parser, the toString function, and 
#    to the evaluate function

#   -todo: add binary operators to method toMathMLString function
#   -todo: add the unary operator not: '!' (this is implemented, but not tested. --JSH)

package Expression;

# pragmas
use strict;
use warnings;
no warnings 'redefine';

# Perl Modules
use Class::Struct;
use FindBin;
use lib $FindBin::Bin;
use Scalar::Util ("looks_like_number");

# BNG Modules
use Param;
use ParamList;

#use POSIX qw/floor ceil/; # safer to use 'floor' and 'ceil' instead of 'int'
use POSIX qw/floor/; # 'floor' and 'ceil' not supported by muParser. Use 'floor' for 'rint'.
use Math::Trig qw(tan asin acos atan sinh cosh tanh asinh acosh atanh pi); 
use List::Util qw(min max sum);

struct Expression =>
{
    Type    => '$',    # Valid types are 'NUM', 'VAR', 'FunctionCall', '+', '-', '*', '/', '^', '**',
                       # '>','<','>=','<=','==','!=','~=','&&','||','!','~'
    Arglist => '@',
    Err     => '$',
};


# NOTE: it's weird that some built-in functions with names (like exp, cos, etc) are handled
#  differently thant built-ins with operator symbols (like +, -, etc).  We could really simplify this.
#  --Justin
# Supported most muParser built-in functions. --LAH 
# See http://muparser.sourceforge.net/mup_features.html#idDef2 for the complete list.
my %functions =
(
  "_pi"   => { FPTR => sub { pi },                   NARGS => 0 }, # <pi>
  "_e"    => { FPTR => sub { exp(1) },               NARGS => 0 }, # <exponentiale> (MathML 2.0)
  "time"  => { FPTR => sub 
  	{ 
  		if (defined($BNGModel::GLOBAL_MODEL->time)) 
  		{$BNGModel::GLOBAL_MODEL->time} 
  		else {0} 
  	}, 												 NARGS => 0 }, # ???
  "exp"   => { FPTR => sub { exp( $_[0] ) },         NARGS => 1 }, # <exp/> (MathML 2.0)
  "ln"    => { FPTR => sub { log( $_[0] ) },         NARGS => 1 }, # <ln/>
  "log10" => { FPTR => sub { log($_[0])/log(10) },   NARGS => 1 }, # <log/>
  "log2"  => { FPTR => sub { log($_[0])/log(2) },    NARGS => 1 }, # <log/><logbase><cn>2</cn></logbase>
  "abs"   => { FPTR => sub { abs( $_[0] ) },         NARGS => 1 }, # <abs/>
# "int"   => { FPTR => sub { int( $_[0] ) },         NARGS => 1 }, # deprecated!
# "floor" => { FPTR => sub { floor( $_[0] ) },       NARGS => 1 }, # not supported by muParser
# "ceil"  => { FPTR => sub { ceil( $_[0] ) },        NARGS => 1 }, # not supported by muParser
  "rint"  => { FPTR => sub { floor( $_[0] + 0.5 ) }, NARGS => 1 }, # requires special handling (see toMathMLString)
  "sqrt"  => { FPTR => sub { sqrt( $_[0] ) },        NARGS => 1 }, # <root/>
  "cos"   => { FPTR => sub { cos( $_[0] ) },         NARGS => 1 }, # <cos/>
  "sin"   => { FPTR => sub { sin( $_[0] ) },         NARGS => 1 }, # <sin/>
  "tan"   => { FPTR => sub { tan( $_[0] ) },         NARGS => 1 }, # <tan/>
  "asin"  => { FPTR => sub { asin( $_[0] ) },        NARGS => 1 }, # <arcsin/>
  "acos"  => { FPTR => sub { acos( $_[0] ) },        NARGS => 1 }, # <arccos/>
  "atan"  => { FPTR => sub { atan( $_[0] ) },        NARGS => 1 }, # <arctan/>
  "sinh"  => { FPTR => sub { sinh( $_[0] ) },        NARGS => 1 }, # <sinh/>
  "cosh"  => { FPTR => sub { cosh( $_[0] ) },        NARGS => 1 }, # <cosh/>
  "tanh"  => { FPTR => sub { tanh( $_[0] ) },        NARGS => 1 }, # <tanh/>
  "asinh" => { FPTR => sub { asinh( $_[0] ) },       NARGS => 1 }, # <arcsinh/> (MathML 2.0)
  "acosh" => { FPTR => sub { acosh( $_[0] ) },       NARGS => 1 }, # <arccosh/> (MathML 2.0)
  "atanh" => { FPTR => sub { atanh( $_[0] ) },       NARGS => 1 }, # <arctanh/> (MathML 2.0)
  "if"    => { FPTR => sub { if($_[0]) { $_[1] } else { $_[2] } }, NARGS => 3 }, # requires special handling (see toMathMLString)
  "min"   => { FPTR => sub { min(@_) },              NARGS => scalar(@_) }, # <min/>
  "max"   => { FPTR => sub { max(@_) },              NARGS => scalar(@_) }, # <max/>
  "sum"   => { FPTR => sub { sum(@_) },              NARGS => scalar(@_) }, # <sum/>
  "avg"   => { FPTR => sub { sum(@_)/scalar(@_) },   NARGS => scalar(@_) }, # <mean/>
);

my $MAX_LEVEL = 500;    # Prevent infinite loop due to dependency loops

# this hash maps operators to the min and max number of arguments
my %NARGS = ( '+'  => { 'min'=>2           }, # <plus/>
              '-'  => { 'min'=>1           }, # <minus/>
              '*'  => { 'min'=>2           }, # <times/>
              '/'  => { 'min'=>2           }, # <divide/>
              '^'  => { 'min'=>2, 'max'=>2 }, # <power/>
              '**' => { 'min'=>2, 'max'=>2 }, # Not supported by muParser
              '&&' => { 'min'=>2           }, # <and/>
              '||' => { 'min'=>2           }, # <or/>
              '<'  => { 'min'=>2, 'max'=>2 }, # <lt/>
              '>'  => { 'min'=>2, 'max'=>2 }, # <gt/>
              '<=' => { 'min'=>2, 'max'=>2 }, # <leq/>
              '>=' => { 'min'=>2, 'max'=>2 }, # <geq/>
              '!=' => { 'min'=>2, 'max'=>2 }, # <neq/>
              '~=' => { 'min'=>2, 'max'=>2 }, # Not supported by muParser
              '==' => { 'min'=>2, 'max'=>2 }, # <equivalent/> (MathML 2.0)
              '!'  => { 'min'=>1, 'max'=>1 }, # Not supported by muParser
              '~'  => { 'min'=>1, 'max'=>1 }  # Not supported by muParser
            );

# muParser operators
# ------------------
# +   addition	 
# -   subtraction	 
# *   multiplication	 
# /   division	 
# ^   raise x to the power of y	 
# &&  logical and	 
# ||  logical or	 
# <   less than	 
# >   greater than	 
# <=  less or equal	 
# >=  greater or equal	 
# !=  not equal	 
# ==  equal	 


# this regex matches param names (letter followed optional by word characters)
my $PARAM_REGEX  = '^\w+$';


###
###
###

sub isBuiltIn
{
	my $name = shift @_;
	return (exists $functions{$name} ? 1 : 0);
}

###
###
###


# get a recursive copy of an expression.
#   Note that the recursion does not descend past VAR and FunctionCall type expressions.
#   Returns a reference to the clone and any error messages.
sub clone
{
    my $expr = shift;
    my $plist = (@_) ? shift : undef;
    my $level = (@_) ? shift : 0;
    
    if ( $level > $MAX_LEVEL ) {  die "Max recursion depth $MAX_LEVEL exceeded.";  }
    
    my $err = '';

    # create a new array for cloned argument    
    my $clone_args = [];
    # create clone
    my $clone = Expression->new();
    $clone->Type( $expr->Type );
    $clone->Arglist( $clone_args );
    $clone->Err( '' );

    # clone argument expressions
    foreach my $arg (  @{$expr->Arglist} )
    {
        my $clone_arg;
        if ( ref $arg eq 'Expression' )
        {
            # recursively expand expressions
            my $clone_arg;
            ($clone_arg, $err) = $arg->clone($plist,$level+1);
            push @$clone_args, $clone_arg;   
        }
        else
        {
            push @$clone_args, $arg;
        }      
    }      
 
    return ($clone, $err);
}


# TODO: simplify method
# eliminate 1's from mulitplications and divisions
# eliminate 0's from additions and subtractions.
# cancel terms in basic arthimetic operations.


###
###
###


# create a new expression from a number or a param name
sub newNumOrVar
{
    my $value = shift @_;
    my $plist = (@_) ? shift @_ : undef;
    
    my $expr;
    my $err;
    # is this a number?
    if ( looks_like_number($value) )
    {   # create a new number expression
        $expr = Expression->new();
        $expr->Type('NUM');
        $expr->Arglist( [$value] );
        $expr->Err( undef );
    }
    # or possibly a parameter name?
    elsif ( $value =~ /$PARAM_REGEX/ )
    {
        # we need a paramlist to continue
        if ( ref $plist eq "ParamList" )
        {
            # check that parameter exists
            (my $param, $err) = $plist->lookup( $value );
            if (defined $param)
            {   # create a new number expression
                $expr = Expression->new();
                $expr->Type('VAR');
                $expr->Arglist( [$value] );
                $expr->Err( undef );            
            }
        }
    }
    
    unless (defined $expr)
    {   die "Expression::newNumOrVar() - Attempted but failed to create number or variable expression";   }

    # return expression or undefined
    return $expr;
}


###
###
###


# Creates a new expression from a list of existing expressions.
# The type argument indicates the operation used to combine the expressions.
#   E.g. '+', '-', 'FunctionCall'.
# If the type is 'FunctionCall', then the first argument should be the name of
#   a built-in or user-defined function.
sub operate
{
    my ($type, $args, $plist) = @_;
   
    # can't do anything without arguments!
    unless (@$args) { return undef; }
    
    # operate is unhappy without a paramlist!
    unless (ref $plist eq 'ParamList') { return undef; }
    
    my $err;
    my $args_copy;
    
    # copy the arguments
    @$args_copy = @$args;
        
    # Check for the right number of arguments..
   
    # is this a function?
    if ( $type eq 'FunctionCall' )
    {
        # get function name (first argument)
        my $fcn_name = $args_copy->[0];

        # is this a built-in function?
        if ( defined $fcn_name  and  exists $functions{$fcn_name} )
        {   # correct number of arguments?
            return undef  unless ( $functions{$fcn_name}->{NARGS} == (@$args_copy - 1) );
        }   
        # or is this custom?
        else
        {
            # lookup function in parameter list
            (my $fcn_param, $err) = $plist->lookup( $fcn_name );
            if ( $err  or  !defined($fcn_param) ) {   return undef;   }

            if ( ref $fcn_param  ne 'Param'  or  $fcn_param->Type ne 'Function' )
            {   return undef;  }

            # correct number of arguments?
            unless (  @{$fcn_param->Ref->Args} == (@$args_copy - 1)  )
            {   return undef;   }
        }
        
        # clone arguments (not first argument, which is the fcn name)
        foreach my $arg ( @$args_copy[1..$#$args_copy] )
        {
            # clone argument, if it's an expression
            if ( ref $arg eq 'Expression' ) 
            {   ($arg, $err) = $arg->clone($plist);   }
        
            # if arg isn't an expression, try to create one  
            else      
            {   $arg = Expression::newNumOrVar( $arg );   }
    
            # check that are is still defined
            unless (defined $arg) { return undef; }
        }        
    }
    # or an operator?
    else
    {
        # check that the operator exists and has the right number of arguments      
        unless ( exists $NARGS{$type} )
        {   return undef;   }
        if (  (exists $NARGS{$type}->{min})  and  (($NARGS{$type}->{min}) > (@$args_copy))  )
        {   return undef;   } 
        if (  (exists $NARGS{$type}->{max})  and  (($NARGS{$type}->{max}) < (@$args_copy))  )
        {   return undef;   }
        
        # clone arguments
        foreach my $arg ( @$args_copy )
        {
            # clone argument, if it's an expression
            if ( ref $arg eq 'Expression' ) 
            {   ($arg, $err) = $arg->clone($plist);   }
        
            # if arg isn't an expression, try to create one  
            else      
            {   $arg = Expression::newNumOrVar($arg,$plist);   }
    
            # check that are is still defined
            unless (defined $arg) { return undef; }  
        }             
    }
            
    # return undefined if there were any errors
    if ($err) { return undef; }
   
    # create the new expression
    my $expr = Expression->new();
    $expr->Type( $type );
    $expr->Arglist( $args_copy );
    $expr->Err( undef );
    
    # return reference to expression!
    return $expr;
}


###
###
###


# load an expression by parsing a string
{
    my $string_sav;
    my $variables = {};
    my $allowForward = 0;


    sub setAllowForward
    {
        my $expr = shift @_;
        $allowForward = shift @_;
    }


    sub readString
    {
        # get arguments
        my $expr      = shift @_;
        my $sptr      = shift @_;
        my $plist     = @_ ? shift @_ : undef;
        my $end_chars = @_ ? shift @_ : '';
        my $level     = @_ ? shift @_ : 0;
        
        my $err       = '';
        my $ops_bi    = '\*\*|[+\-*/^]|>=|<=|[<>]|==|!=|~=|&&|\|\|';
        my $ops_un    = '[+\-!~]';

        if ( $level==0 )
        {
            $string_sav = $$sptr;
            %$variables  = ();
        }

        # parse string into form expr op expr op ...
        # a+b*(c+d)
        # -5.0e+3/4
        my $last_read       = '';
        my $expr_new        = '';
        my @list            = ();
        my $expect_op       = 0;
        my $assign_var_name = '';
        while ( $$sptr ne '' )
        {
            # OPERATORS
            if ($expect_op)
            {
                # Binary operator
                if ( $$sptr =~ s/^\s*($ops_bi)// )
                {
                    my $express = Expression->new( Type=>$1 );
                    push @list, $express;
                    $expect_op = 0;
                    next;
                }
    
                # Assignment using '='.  Valid syntax is PARAM = EXPRESSION
                # NOTE: this really stops the current expression (which should be a PARAM) and
                #  starts parsing a RHS expression which will be assigned to PARAM.
                elsif ( $$sptr =~ s/^\s*=// )
                {
                    # Check that only preceding argument is variable
                    unless ( @list==1 )
                    {  return "Invalid assignment syntax (VAR = EXPRESSION) in $string_sav at $$sptr";  }

                    my $param      = $list[0];
                    my $param_name = $param->Arglist->[0];
                    
					# Make sure parameter name not the same as built-in functions --Leonard
					if ( exists $functions{ $param_name } ){
						return "Cannot use built-in function name '$param_name' as a parameter name.";
					}
					
                    unless ( $param->Type eq 'VAR' )
                    {   return "Attempted assignment to non-variable type in $string_sav at $$sptr.";   }

                    # save old variables
                    my $old_variables;
                    %{$old_variables} = %{$variables};
                    %{$variables} = ();
                    # save old string_sav
                    my $old_string_sav = $string_sav;

                    # Read remainder as expression
                    my $rhs = Expression->new();
                    $err = $rhs->readString( $sptr, $plist, $end_chars, $level + 1 );
                    if ($err) {  return $err;  }

                    # check for recursive definitions
                    if ( exists $variables->{$param_name} )
                    {  return "Parameter $param_name is defined recursively.";  }

                    # fetch old variables and string_sav
                    %{$variables} = %{$old_variables};
                    $string_sav = $old_string_sav;

                    # assign the rhs expression to the parameter $vname
                    $plist->set( $param_name, $rhs );
                    last;
                }
    
                # Look for end characters
                elsif ( $end_chars  and  ( $$sptr =~ /^\s*${end_chars}/ ) )
                {   # end of expression
                    last;
                }
                else
                {   # nothing more to do
                    # TODO: add warning for trailing characters?
                    $$sptr =~ s/^\s*//;
                    last;
                }
            }
    
            # Chop leading whitespace
            $$sptr =~ s/^\s*//;
    
            # look for a NUMBER
            if ( my $num_expr = getNumber($sptr) )
            {
                push @list, $num_expr;
                $expect_op = 1;
                next;
            }
    
            # look for a function call
            if ( $$sptr =~ s/^($ops_un)?\s*(\w+)\s*\(// )
            {
                my $express_u;
                if ( my $op = $1 )
                {   # (optional) UNARY OP at start of expression, as in -a + b, or -a^2
                    $express_u = Expression->new( Type=>$1 );
                }

                my $name  = $2;
                my @fargs = ();
                my $type  = '';
                my $nargs;
                my ($param, $err);
                if ($plist)
                { 
                    ($param, $err) = $plist->lookup($name); 
                }
                if ( exists $functions{$name} )
                {
                    $type  = "B";
                    $nargs = $functions{$name}->{NARGS};
                }
#                elsif ( $param  and  ($param->Type eq 'Observable') )
                elsif ( $param  and defined $param->Type and ($param->Type eq 'Observable') )
                {
                    $type = "O";
                    # number of args may be zero or one.
                }
#                elsif ( $param  and  ($param->Type eq 'Function') )
                elsif ( $param  and defined $param->Type and ($param->Type eq 'Function') )
                {
                    $type  = "F";
                    $nargs = scalar( @{ $param->Ref->Args } );
                }
                else
                {
					unless ( defined $param )
					{
	                    if ($allowForward)
	                    { $plist->set($name); }
	                    else
	                    {
	                        return "Function $name is not a built-in function, Observable, or defined Function";
	                    }
					}
                }
    
                # Read arguments to function
                while (1)
                {
                    my $express = Expression->new();
                    $err = $express->readString( $sptr, $plist, ',\)', $level + 1 );
                    if ($err) { return $err; }
                    if ($express->Type) { push @fargs, $express; }
                    
                    if ( $$sptr =~ s/^\)// )
                    {   last;   }
                    elsif ( $$sptr =~ s/^,// )
                    {   next;   }
                }
    
                # Check Argument list for consistency with function
                if ( $type eq "O" )
                {
                    $nargs= scalar(@fargs);
                    if  ($nargs>1){
                        return ("Observables $name is called with too many arguments");
                    }
                    elsif ($nargs==1)
                    {
                        # Argument must be VAR
                        if ($fargs[0]->Type ne "VAR"){
                            return("Argument to observable must be a variable");
                        }
                        # Argument to Observable must be Local type
                        (my $lv) = $plist->lookup($fargs[0]->Arglist->[0]);
                        if ($lv->Type ne "Local"){
                            return( "Argument to observable must be a local variable" );
                        }
                    }
                }
                else
                {
#					if ( $param  and  ($nargs != @fargs) )
					if ( $type ne '' and $param  and  ($nargs != @fargs) )
                    {   return "Incorrect number of arguments to function $name";   }
                }
                my $express = Expression->new( Type=>'FunctionCall', Arglist=>[$name, @fargs] );

                if (defined $express_u)
                {
                    push @{$express_u->Arglist}, $express;
                    push @list, $express_u;
                } 
                else
                {   push @list, $express;   }

                $expect_op = 1;
                next;
            }
    
            # VARIABLE
            elsif ( $$sptr =~ s/^($ops_un)?\s*(\w+)// )
            {
                my $express_u;
                if ( my $op = $1 )
                {   # (optional) UNARY OP at start of expression, as in -a + b, or -a^2
                    $express_u = Expression->new( Type=>$1 );
                }
                my $name = $2;

                # Validate against ParamList, if present
                if ($plist)
                {
                    # Create and set variable if next token is '='
                    # otherwise create referenced variable but leave its Expr unset
                    unless ( $$sptr =~ /^\s*=/ )
                    {
                        my ( $param, $err ) = $plist->lookup($name);
                        unless ( defined $param )
                        {
                            if ($allowForward)
                            {   $plist->set($name);   }
                            else
                            {   return "Can't reference undefined parameter $name";   }
                        }
                    }
                }
                else
                {   return "No parameter list provided";   }

                my $express = Expression->new( Type=>'VAR', Arglist=>[$name] );
                ++($variables->{$name});

                if (defined $express_u)
                {
                    push @{$express_u->Arglist}, $express;
                    push @list, $express_u;
                } 
                else
                {   push @list, $express;   }

                $expect_op = 1;
                next;
            }

            # Get expression enclosed in parentheses
            elsif ( $$sptr =~ s/^($ops_un)?\s*\(// )
            {
                my $express_u;
                if ( my $op = $1 )
                {   # (optional) UNARY OP at start of expression, as in -a + b, or -a^2
                    $express_u = Expression->new( Type=>$1 );
                }
                my $express = Expression->new();
                $err = $express->readString( $sptr, $plist, '\)', $level + 1 );
                if ($err) {  return ($err);  }
                unless ( $$sptr =~ s/^\s*\)// )
                {   return "Missing end parentheses in $string_sav at $$sptr";   }
    
                #printf "express=%s %s\n", $express->toString($plist), $$sptr;
                if (defined $express_u)
                {
                    push @{$express_u->Arglist}, $express;
                    push @list, $express_u;
                } 
                else
                {   push @list, $express;   }

                $expect_op = 1;
                next;
            }
            elsif ( $end_chars  and  ($$sptr =~ /^\s*[${end_chars}]/) )
            {   last;   }
          
            # ERROR
            else
            {   return "Expecting operator argument in $string_sav at $$sptr";   }
        }

        # Transform list into expression preserving operator precedence
        if (@list) { $expr->copy(arrayToExpression(@list)); }
        
        return $err;
    }
}


###
###
###




# check if expression depends on parameter "varname"
sub depends
{
    my $expr    = shift;
    my $plist   = shift;
    my $varname = shift;
    my $level   = (@_) ? shift : 0;
    my $dep     = (@_) ? shift : {};

    my $retval = '';
    my $err    = '';

    my $type = $expr->Type;
    if ( $type eq 'NUM' )
    {
        # this expression is a number: no dependency on varname
    }
    elsif ( $type eq 'VAR' )
    {
        # this expression is a parameter name: check if parameter depends on varname
        my $param_name = $expr->Arglist->[0];

        if ( exists $dep->{$param_name} and $dep->{$param_name} )
        {
            # cyclic dependency detected
            $err = sprintf "Cycle in parameter $param_name looking for dep in %s", $varname;
            print "$err\n";
            $retval = $param_name;
        }
        else
        {
            if ( $varname eq $param_name )
            {
                # the parameter is varname: we found a dependency!
                $retval = $param_name;
            }
            elsif ($plist)
            {
                # lookup parameter and see what it depends on..
                (my $param) = $plist->lookup($param_name);
                if ( defined $param )
                {
                    # copy dependency hash and pass it down
                    my $new_dep = { %$dep };
                    $new_dep->{$param_name} = 1;
                    (my $ret, $err) = $param->Expr->depends($plist, $varname, $level+1, $new_dep);
                    if ($ret)
                    {
                        $retval = $param_name . '->' . $ret;
                    }
                }
            }
        }
    }
    else
    {
        # this expression is an operator, function or observable
        # ..check if this depends on varname
        my @arglist = @{ $expr->Arglist };
        # Skip function name if this is a function
        if ( $type eq 'FunctionCall' ) {  shift @arglist;  }
        foreach my $expr (@arglist)
        {
            ($retval, $err) = $expr->depends($plist, $varname, $level + 1, $dep);
            last if $retval;
        }
    }

    return ($retval, $err);
}



###
###
###



# copy the contents of this expression into a second expression.
#  NOTE: this is not recursive!!  use the clone method to get a recursive copy
sub copy
{
    my $edest   = shift;
    my $esource = shift;

    $edest->Type( $esource->Type );
    $edest->Arglist( [ @{ $esource->Arglist } ] );
    return ($edest);
}



###
###
###


# evaluate an expression and return a numerical value
sub evaluate
{
    my $expr  = shift;
    my $plist = (@_) ? shift : undef;
    my $level = (@_) ? shift : 0;

    if ( $level > $MAX_LEVEL ) {  die "Max recursion depth $MAX_LEVEL exceeded.";  }

    my $val = undef;
    if ( $expr->Type eq 'NUM' )
    {
        $val = $expr->Arglist->[0];
    }
    elsif ( $expr->Type eq 'VAR' )
    {        
        unless (defined $plist)
        {  die "Expression->evaluate: Error! Cannot evaluate VAR type without ParamList.";  }
    
        my $name = $expr->Arglist->[0];
        $val = $plist->evaluate( $name, [], $level+1 );
        unless (defined $val)
        {  die "Expression->evaluate: Error! Parameter $name is not defined!\n";  }
    }
    elsif ( $expr->Type eq 'FunctionCall' )
    {
        # first argument is function name
        my $name  = $expr->Arglist->[0];

        if ( ref $name eq "Function" )
        {   # anonymous function (TODO: double-check that its ok to be lazy about evaluating the args
            $val = $name->evaluate( $expr->Arglist, $plist, $level+1);
        }
        elsif ( exists $functions{$name} )
        {   # built-in function
            my $f = $functions{$name}->{FPTR};
            # evaluate all the remaining arguments
            my $eval_args = [];
            my $ii=1;
            while ( $ii < @{$expr->Arglist} )
            {
                push @$eval_args, $expr->Arglist->[$ii]->evaluate($plist, $level+1);
                ++$ii;
            }             
            $val = $f->(@$eval_args);
        }
        
        else
        {   # lookup user-defined function in paramlist
            unless (defined $plist)
            {  die "Expression->evaluate: Error! Cannot evaluate user Function without ParamList.";  }
        
            $val = $plist->evaluate( $name, $expr->Arglist, $level+1 );
        }
    }
    else
    {
        my $eval_string;
        my $operator = $expr->Type;

        # replace non-perl operators with the perl equivalents
        if    ( $operator eq '~=' ) {  $operator = '!=';  }
        elsif ( $operator eq '^'  ) {  $operator = '**';  }
        elsif ( $operator eq '~'  ) {  $operator = '!';   } 
        
        if ( @{$expr->Arglist} == 1 )
        {   # handle unary operators
            if ( $operator eq "/" )
            {
                $eval_string = "1.0/(\$expr->Arglist->[0]->evaluate(\$plist,\$level+1))";
            }
            else
            {
                $eval_string = "$operator(\$expr->Arglist->[0]->evaluate(\$plist,\$level+1))";
            }
        
        }
        else
        {
            my $last = @{$expr->Arglist} - 1;
            $eval_string = join "$operator", map {"(\$expr->Arglist->[$_]->evaluate(\$plist,\$level+1))"} (0..$last);
        }
        
        # check if this is boolean type
        if ( $operator =~ /[<>|&!=]/ )
        {
            # evaluate the expression
            local $SIG{__WARN__} = sub {};
            $val = eval "$eval_string" ? 1 : 0;
            if ($@) {  die $@;  }
        }
        else
        {
            # evaluate the expression
            local $SIG{__WARN__} = sub {};
            $val = eval "$eval_string";
            if ($@) {  die $@;  }
        }
    }

    return $val;
}



###
###
###



# Call this method to clone an expression and then descend into the expression
#  and evaluate any local observables.  The method returns the cloned variable
#  with local observables evaluated as numbers.  NOTE: this method will not
#  work correctly if observables haven't been computed prior to the call.
sub evaluate_local
{
    my $expr  = shift @_;
    my $plist = @_ ? shift @_ : undef;
    my $level = @_ ? shift @_ : 0;
    
    if ( $level > $MAX_LEVEL ) {  return (undef, "Max recursion depth $MAX_LEVEL exceeded.");  }
    unless (defined $plist)    {  die "Expression->evaluate_local: Error! Function called without required ParamList.";  }    
    
    # local variables
    my $local_expr = undef;
    my $err = '';

    # clone expression
    ($local_expr, $err) = $expr->clone( $plist, $level+1 );

    # evaluate local dependencies in arguments
    foreach my $arg ( @{$local_expr->Arglist} )
    {
        # only need to do this for expression arguments
        if ( ref $arg eq 'Expression' )
        {   $arg = $arg->evaluate_local($plist, $level+1);   }
    } 
   
    # some additional handling for Function expressions!
    if ( $expr->Type eq 'FunctionCall' )
    {
        # if local arguments are passed to this function, then we
        #  must go into the function and evaluate the local bits.  Then
        #  we need to create a clone of the function that has the local bits evaluated.
        #  Yuck!
        
        # First argument is the function name
        my $name = $expr->Arglist->[0];

        if ( ref $name eq "Function" )
        {   # anonymous function..
            my $fcn = $name;
            # get locally evaluated function
            my ($local_fcn, $elim_args) = $fcn->evaluate_local( $local_expr->Arglist, $plist, $level+1 );

            # if the local_fcn does not refer to observables of named functions,
            # then we can convert it to a constant expression
            my $dependencies = $local_fcn->Expr->getVariables($plist);
            if ( @{$local_fcn->Args}==0
                 and not exists $dependencies->{'Observable'}
                 and not exists $dependencies->{'Function'}   )
            {   # replace this fcn call with the localfcn expression
                $local_expr = $local_fcn->Expr;
            }                
            else
            {   # point this fcn call to the local expr
                $local_expr->Arglist->[0] = $local_fcn;
                # eliminate unused arguments
                foreach my $iarg (@$elim_args)
                {   splice @{$local_expr->Arglist}, $iarg, 1;   }
            }
        }
        elsif ( exists $functions{$name} )
        {
            # nothing to do
        }
        else
        {   # custom, named function 
            # lookup function parameter:
            (my $fcn_param) = $plist->lookup( $name );
            
            # Is this a true function or an observable??
            if ( $fcn_param->Type eq 'Function' )
            {                    
                # get locally evaluated function
                my ($local_fcn, $elim_args) = $fcn_param->Ref->evaluate_local( $local_expr->Arglist, $plist, $level+1 );

                # if the local_fcn does not refer to observables of named functions,
                # then we can convert it to a constant expression
                my $dependencies = $local_fcn->Expr->getVariables($plist);
                if ( @{$local_fcn->Args}==0
                     and not exists $dependencies->{'Observable'}
                     and not exists $dependencies->{'Function'}   )
                {   # replace this fcn call with the localfcn expression
                    $local_expr = $local_fcn->Expr;
                }                
                else
                {   # point this fcn call to the local expr
                    $local_expr->Arglist->[0] = $local_fcn;
                    # eliminate unused arguments
                    foreach my $iarg (@$elim_args)
                    {   splice @{$local_expr->Arglist}, $iarg, 1;   }
                }
            }
            # This function is Really an Observable!!    
            elsif ( $fcn_param->Type eq 'Observable' )
            {         
                if ( @{$expr->Arglist} > 1 )
                {
                    # get locally evaluated function
                    my $val = $fcn_param->Ref->evaluate( $local_expr->Arglist, $plist, $level+1 );      
           
                    # replace local expression with the evaluation
                    my $args = [ $val ];
                    $local_expr->Type('NUM');
                    $local_expr->Arglist($args);
                    $local_expr->Err(undef);
                }
            }
            # The reference type is not known, abort with error!
            else
            {   $err = "ERROR in Expression->evaluate_local(): expression is a function, but ref type is unknown!";   }   
        }
    }

    return $local_expr;
}



###
###
###


# check for local observable dependency, return true if found
sub checkLocalDependency
{
    my $expr  = shift;
    my $plist = shift;
    my $level = (@_) ? shift : 0;

    unless ( defined $plist )
    {   die "Expression->checkLocalDependency: Error! Missing argument ParamList!";   }
    

    # check dependence of arguments
    foreach my $arg ( @{$expr->Arglist} )
    {
        if ( ref $arg eq 'Expression' )
        {
            return 1  if ( $arg->checkLocalDependency( $plist, $level+1 ) );
        }
    }    
       
    if ( $expr->Type eq 'FunctionCall' )
    {    
        my $name = $expr->Arglist->[0];

        if ( ref $name eq "Function" )
        {   # anonymous function..
            my $fcn = $name;        
            return 1  if ( $fcn->checkLocalDependency( $plist, $level+1 ) );
        }
        elsif ( exists $functions{$name} )
        {
            # nothing to do
        }
        else
        {   # lookup custom function by name
            my ($fcn_param) = $plist->lookup( $expr->Arglist->[0] );
            # is this a true function or an observable?
            if ( $fcn_param->Type eq 'Function' ) 
            {   
                my $fcn = $fcn_param->Ref;            
                return 1  if ( $fcn->checkLocalDependency( $plist, $level+1 ) );
            }
            elsif ( $fcn_param->Type eq 'Observable' )
            {
                # function observables are locally dependent!!
                return (@{$expr->Arglist} > 1 ? 1 : 0);
            }
        }
    }
    
    return 0;
}


###
###
###


# check if two expressions are equivalent
sub equivalent
{
    my $expr1 = shift @_;
    my $expr2 = shift @_;
    my $plist = (@_) ? shift : undef;
    my $level = (@_) ? shift : 0;
  
    # make sure we have defined expressions!
    return 0  unless ( defined $expr1  and  ref $expr1 eq 'Expression' );
    return 0  unless ( defined $expr2  and  ref $expr2 eq 'Expression' );

    # shortcut: first check if we're looking at the same object
    return 1  if ( $expr1 == $expr2 );
    
    # check type equivalence
    return 0  unless ( $expr1->Type  eq  $expr2->Type );
    
    # check for equal number of arguments
    return 0  unless ( @{$expr1->Arglist} == @{$expr2->Arglist} );
    
    # now we have to look deeper into the arguments
    if    ( $expr1->Type eq 'NUM' )
    {
        # compare numbers
        return ( $expr1->Arglist->[0]  ==  $expr2->Arglist->[0] );
    }
    elsif ( $expr2->Type eq 'VAR' )
    {
        # compare var names
        return ( $expr1->Arglist->[0]  eq  $expr2->Arglist->[0] );
    }
    elsif ( $expr2->Type eq 'FunctionCall' )
    {
        # compare function names (or refs)
        return 0  unless ( ref $expr1->Arglist->[0] eq ref $expr2->Arglist->[0] );
        return 0  unless ( $expr1->Arglist->[0] eq $expr2->Arglist->[0] );
    
        # check argument equivalence
        for ( my $i = 1;  $i < @{$expr1->Arglist};  ++$i )
        {   
            return 0
                unless ( Expression::equivalent($expr1->Arglist->[$i], $expr2->Arglist->[$i], $plist, $level+1) );
        }
    }
    else
    {
        # check argument equivalence
        for ( my $i = 0;  $i < @{$expr1->Arglist};  ++$i )
        {   
            return 0
                unless ( Expression::equivalent($expr1->Arglist->[$i], $expr2->Arglist->[$i], $plist, $level+1) );
        }
    }
    
    # return true if no differences have been found   
    return 1;
}




# write this expression as a string.
#  The expression is expanded up to the named Parameters and Functions.
sub toString
{
    my $expr   = shift;
    my $plist  = (@_) ? shift : undef;
    my $level  = (@_) ? shift : 0;
    my $expand = (@_) ? shift : 0;

    # simple error checking
    if ( $level > $MAX_LEVEL ) { die "Max recursion depth $MAX_LEVEL exceeded."; }
    if ( $expand  and  !$plist ) { die "Can't expand expression past parameters without a parameter list."; }

    # local variables
    my $err;
    my $string;
    
    # different handling depending on the type
    my $type = $expr->Type;
    if ( $type eq 'NUM' )
    {
        # if number, print the numerical value!
        $string = $expr->Arglist->[0];
        #print "NUM=$string\n";
    }
    elsif ( $type eq 'VAR' )
    {
        if ( $expand )
        {   # descend recursively into parameter!
            ( my $param, $err ) = $plist->lookup( $expr->Arglist->[0] );
             $string = $param->toString( $plist, $level+1, $expand );
        }
        else
        {   # just write the parameter name
            $string = $expr->Arglist->[0];
        }
        #$string= $expr->evaluate($plist);
        #print "VAR=$string\n";
    }
    elsif ( $type eq 'FunctionCall' )
    {
        my $name = $expr->Arglist->[0];
        if ( $expand  or  (ref $name eq "Function") )
        {   # expand the function
            my @sarr = ($expr->Arglist->[0]);
            foreach my $i ( 1 .. $#{$expr->Arglist} )
            {   push @sarr, $expr->Arglist->[$i]->toString($plist, $level+1, $expand);   }

            if (ref $name eq "Function")
            {   # anonymous function
                my $fcn = $name;
                (my $local_fcn) = $fcn->evaluate_local( \@sarr, $plist, $level+1 );
                $string = $local_fcn->Expr->toString( $plist, $level+1, $expand );
            }
            elsif ( exists $functions{$name} )
            {   # built-in function
                $string = $expr->Arglist->[0] . "(" . join(",", @sarr[1..$#sarr]) . ")";
            }
            else
            {   # lookup custom function by name and expand
                ( my $param, $err ) = $plist->lookup($name);
                (my $local_fcn) = $param->Ref->evaluate_local( \@sarr, $plist, $level+1 );
                $string = $local_fcn->Expr->toString( $plist, $level+1, $expand );
            }
        }
        else
        {   # just write the function and its argument values
            my @sarr = ();
            foreach my $i ( 1 .. $#{$expr->Arglist} )
            {
                push @sarr, $expr->Arglist->[$i]->toString( $plist, $level + 1 );
            }
            $string = $name . '(' . join( ',', @sarr ) . ')';
        }
    }
    else
    {
        if ( $expand )
        {
            my @sarr = ();
            foreach my $e ( @{ $expr->Arglist } ) {
                push @sarr, $e->toString( $plist, $level+1, $expand );
            }
            if ( $#sarr > 0 )
            {   $string = join( $type, @sarr );   }
            else
            {   $string = $type . $sarr[0];       }

            # enclose in brackets if not at top level
            #    print "level=$level\n";
            if ($level)
            {   $string = '(' . $string . ')';    }          
        }
        else
        {
            my @sarr = ();
            foreach my $e ( @{ $expr->Arglist } ) {
                push @sarr, $e->toString( $plist, $level + 1 );
            }
            if ( $#sarr > 0 )
            {
                $string = join( $type, @sarr );
            }
            else {
                $string = $type . $sarr[0];
            }

            # enclose in brackets if not at top level
            #    print "level=$level\n";
            if ($level) {
                $string = '(' . $string . ')';
            }
            #printf "%s=$string\n", $expr->Type;
        }
    }

    return $string;
}



# write this expression as an XML string.
#  Same as toString, except a few operators are replaced to avoid clashes with XML
sub toXML
{
    my $expr   = shift;
    my $plist  = (@_) ? shift : undef;
    my $level  = (@_) ? shift : 0;
    my $expand = (@_) ? shift : 0;

    # simple error checking
    if ( $level > $MAX_LEVEL ) { die "Max recursion depth $MAX_LEVEL exceeded."; }
    if ( $expand  and  !$plist ) { die "Can't expand expression past parameters without a parameter list."; }

    # local variables
    my $err;
    my $string;
    
    # different handling depending on the type
    my $type = $expr->Type;
    if ( $type eq 'NUM' )
    {
        # if number, print the numerical value!
        $string = $expr->Arglist->[0];
    }
    elsif ( $type eq 'VAR' )
    {
        if ( $expand )
        {
            # descend recursively into parameter!
            ( my $param, $err ) = $plist->lookup( $expr->Arglist->[0] );
             $string = $param->toXML( $plist, $level+1, $expand );
        }
        else
        {
            # just write the parameter name
            $string = $expr->Arglist->[0];
        }
    }
    elsif ( $type eq 'FunctionCall' )
    {
        if ( $expand )
        {
            # TODO
            my @sarr = ();
            foreach my $i ( 1 .. $#{$expr->Arglist} )
            {
                push @sarr, $expr->Arglist->[$i]->toXML( $plist, $level + 1 );
            }
            $string = $expr->Arglist->[0] . '(' . join( ',', @sarr ) . ')';
        }
        else
        {
            my @sarr = ();
            foreach my $i ( 1 .. $#{$expr->Arglist} )
            {
                push @sarr, $expr->Arglist->[$i]->toXML( $plist, $level + 1 );
            }
            $string = $expr->Arglist->[0] . '(' . join( ',', @sarr ) . ')';
        }
    }
    else
    {
        if ( $expand )
        {
            my @sarr = ();
            foreach my $e ( @{$expr->Arglist} )
            {
                push @sarr, $e->toXML( $plist, $level+1, $expand );
            }
            if ( $#sarr > 0 )
            {   $string = join( $type, @sarr );   }
            else
            {   $string = $type . $sarr[0];       }

            # enclose in brackets if not at top level
            #    print "level=$level\n";
            if ($level)
            {   $string = '(' . $string . ')';    }          
        }
        else
        {
            my @sarr = ();
            foreach my $e ( @{ $expr->Arglist } ) {
                push @sarr, $e->toXML( $plist, $level + 1 );
            }
            if ( $#sarr > 0 )
            {
                $string = join( $type, @sarr );
            }
            else {
                $string = $type . $sarr[0];
            }

            # enclose in brackets if not at top level
            #    print "level=$level\n";
            if ($level) {
                $string = '(' . $string . ')';
            }
            #printf "%s=$string\n", $expr->Type;
        }
    }

    # TODO: special handling for XML output should be handled by a special option
    #  or a toXML sub.  --Justin
    
    #BEGIN edit, msneddon
    # for outputting to XML, we need to make sure we put in some special
    # characters and operators to match the muParser library and to allow
    # the XML parser to work.<" with "&lt;", ">" with "&gt;", and
    #"&" with "&amp
    #print "before XML replacement: $string\n";
    $string =~ s/</&lt\;/;
    $string =~ s/>/&gt\;/;
    $string =~ s/&&/and/;
    $string =~ s/\|\|/or/;
    #print "after XML replacement: $string\n";
    #END edit, msneddon

    return ($string);
}




# write expression as a string suitable for
#  export to CVode.  This is the same as toString,
#  except variable names are replaced with pointers into
#  arrays.
sub toCVodeString
{
    my $expr   = shift;
    my $plist  = (@_) ? shift : '';
    my $level  = (@_) ? shift : 0;
    my $expand = (@_) ? shift : 0;

    if ( $level > $MAX_LEVEL ) {  die "Max recursion depth $MAX_LEVEL exceeded.";  }

    my $string;
    my $err;
    
    my $type   = $expr->Type;
    
    if ( $type eq 'NUM' )
    {
        $string = $expr->Arglist->[0];
        # if this is a pure integer,
        #  add a decimal place to make sure C knows this has type double
        $string =~ s/^(\d+)$/$1.0/;
    }
    elsif ( $type eq 'VAR' )
    {   
        # lookup corresponding parameter ...
        (my $param, $err) = $plist->lookup( $expr->Arglist->[0] );
        if ($param)
        {   # return cvode ref
            $string = $param->getCVodeName();
        }
        else
        {   # parameter not defined, assume it's a local argument and write its name
            $string = $expr->Arglist->[0];
        }
    }
    elsif ( $type eq 'FunctionCall' )
    {
        # the first argument is the function name
        my $fcn_name = $expr->Arglist->[0];
        
        if ( ref $fcn_name eq "Function" )
        {   # anonymous function
            my $fcn = $fcn_name;
            # we can't call function by name, so we have to expand the function expression with args in place
            (my $local_fcn) = $fcn->evaluate_local( [@{$expr->Arglist}], $plist, $level+1 );
            unless (defined $local_fcn) { die "Error in Expression->toMatlabString(): some problem evaluating anonymous function"; }
            $string = $local_fcn->Expr->toCVodeString($plist, $level+1, $expand);
        }
        elsif ( exists $functions{ $expr->Arglist->[0] } )
        {   # this is a built-in function
            # handle built-ins with 1 argument that have the same name in the C library
            if ( $fcn_name =~ /^(sin|cos|exp|log|abs|sqrt|floor|ceil)$/ )
            {
                my @sarr = ( map {$_->toCVodeString($plist, $level+1, $expand)} @{$expr->Arglist}[1..$#{$expr->Arglist}] );
                $string = $fcn_name .'('. join( ',', @sarr ) .')';
            }
            # handle the 'if' built-in with 3 arguments
            elsif ( $fcn_name eq 'if' )
            {   
                # substitute the "?" operator for the if function
                my @sarr = ( map {$_->toCVodeString($plist, $level+1)} @{$expr->Arglist}[1..$#{$expr->Arglist}] );

                if ( @sarr == 3)
                {   $string = '('. $sarr[0] .' ? '. $sarr[1] .' : '. $sarr[2] .')';   }
                else
                {   die "Error in Expression->toCVodeString():  built-in function 'if' must have three arguments!";   }    
            }
            # fatal error if the built-in is not handled above
            else
            {   die "Error in Expression->toCVodeString():  don't know how to handle built-in function $fcn_name!";   }
        }
        else
        {   # user-defined function or observable
            # lookup function parameter:
            (my $fcn_param) = $plist->lookup( $fcn_name );
            unless ($fcn_param)
            {   die "Error in Expression->toCVodeString: could not find function parameter!";   }
            
            # Is this a true function or an observable??
            if ( $fcn_param->Type eq 'Function' )
            {   # Handling a true Function!                            
                # expand argument expressions up until named entities
                my @sarr = ( map {$_->toCVodeString($plist, $level+1, $expand)} @{$expr->Arglist}[1..$#{$expr->Arglist}] );
                # pass arguments pointing to the expressions array and observables array
                push @sarr, 'expressions', 'observables';
                $string = $fcn_name . '(' . join( ',', @sarr ) . ')';
            }
            elsif ( $fcn_param->Type eq 'Observable' )
            {
                # TODO: if there are arguments, then we should warn the user that we can't evaluate a local
                # observables in a CVode function!!
                $string = $fcn_param->getCVodeName();
            }
            else
            {   die "Error in Expression->toCVodeString(): don't know how to process function expression of non-function type!";   }
            
        }
        
    }
    elsif ( ($type eq '**') or ($type eq '^') )
    {  
        # substitute the "pow" function for the exponentiation operator
        my @sarr = ( map {$_->toCVodeString($plist, $level+1)} @{$expr->Arglist} );
    
        if ( @sarr == 2 )
        {   $string = 'pow(' . $sarr[0] . ',' . $sarr[1] . ')';  }
        else
        {   die "Error in Expression->toCVodeString(): Exponentiation must have exactly two arguments!";   }
    }
    else
    {   
        # handling some other operator (+,-,*,/)
        # enclose in brackets (always. just to be safe)        
        my @sarr = ( map {$_->toCVodeString($plist, $level+1, $expand)} @{$expr->Arglist} );
        if ( @sarr > 1 )
        {   # binary or higher order
            $string = '(' . join( $type, @sarr ) . ')';
        }
        else
        {   # unary operator
            $string = '(' . $type . $sarr[0] . ')';
        }
    }

    return ($string);
}




# write expression as a string suitable for
#  export to a Matlab M-file.
sub toMatlabString
{
    my $expr   = shift @_;
    my $plist  = @_ ? shift @_ : undef;
    my $level  = @_ ? shift @_ : 0;
    my $expand = @_ ? shift @_ : 0;

    if ( $level > $MAX_LEVEL ) {  die "Max recursion depth $MAX_LEVEL exceeded.";  }

    my $string;
    my $err;
    
    my $type   = $expr->Type;
    
    if ( $type eq 'NUM' )
    {
        $string = $expr->Arglist->[0];
    }
    elsif ( $type eq 'VAR' )
    {   
        # lookup corresponding parameter ...
        (my $param, $err) = $plist->lookup( $expr->Arglist->[0] );
        if ($param)
        {   # return matlab ref
            $string = $param->getMatlabName();   
        }
        else
        {   # parameter not defined, assume it's a local argument and write its name
            $string = $expr->Arglist->[0];
        }        
    }
    elsif ( $type eq 'FunctionCall' )
    {
        # the first argument is the function name
        my $fcn_name = $expr->Arglist->[0];
        
        if ( ref $fcn_name eq "Function" )
        {   # anonymous function!
            my $fcn = $fcn_name;
            # we can't call function by name, so we have to expand the function expression with args in place
            (my $local_fcn) = $fcn->evaluate_local( [@{$expr->Arglist}], $plist, $level+1 );
            unless (defined $local_fcn) { die "Error in Expression->toMatlabString(): some problem evaluating anonymous function"; }
            $string = $local_fcn->Expr->toMatlabString($plist, $level+1, $expand);
        }
        elsif ( exists $functions{ $expr->Arglist->[0] } )
        {
            # handle built-ins with 1 argument that have the same name in Matlab
            if ( $fcn_name =~ /^(sin|cos|exp|log|abs|sqrt|floor|ceil)$/ )
            {
                my @sarr = ( map {$_->toMatlabString($plist, $level+1, $expand)} @{$expr->Arglist}[1..$#{$expr->Arglist}] );
                $string = $fcn_name .'('. join( ',', @sarr ) .')';
            }
            # handle the 'if' built-in with 3 arguments
            elsif ( $fcn_name eq 'if' )
            {   
                # substitute the "?" operator for the if function
                my @sarr = ( map {$_->toMatlabString($plist, $level+1)} @{$expr->Arglist}[1..$#{$expr->Arglist}] );

                if ( @sarr == 3)
                {   # TODO: find better solution here. this version will return NaN if either return value is Inf.
                    $string = sprintf( "((%s~=0)*%s + (%s==0)*%s)", $sarr[0], $sarr[1], $sarr[0], $sarr[2]);
                }
                else
                {   die "Error in Expression->toMatlabString():  built-in function 'if' must have three arguments!";   }    
            }
            # fatal error if the built-in is not handled above
            else
            {   die "Error in Expression->toMatlabString():  don't know how to handle built-in function $fcn_name!";   }
        }
        else
        {   # this is a user-defined function or observable
            # lookup function parameter:
            (my $fcn_param) = $plist->lookup( $fcn_name );
            unless ($fcn_param)
            {   die "Error in Expression->toMatlabString: could not find function parameter!";   }
            
            # Is this a true function or an observable??
            if ( $fcn_param->Type eq 'Function' )
            {   # Handling a true Function!                            
                # expand argument expressions up until named entities
                my @sarr = ( map {$_->toMatlabString($plist, $level+1, $expand)} @{$expr->Arglist}[1..$#{$expr->Arglist}] );
                # pass arguments pointing to the expressions array and observables array
                push @sarr, 'expressions', 'observables';
                $string = $fcn_name . '(' . join( ',', @sarr ) . ')';
            }
            elsif ( $fcn_param->Type eq 'Observable' )
            {
                # TODO: if there are arguments, then we should warn the user that we can't evaluate a local
                # observables in a CVode function!!
                $string = $fcn_param->getMatlabName();
            }
            else
            {   die "Error in Expression->toMatlabString(): don't know how to process function expression of non-function type!";   }   
        }
        
    }
    else
    {   
        # handling some other operator (+,-,*,/)
        # enclose in brackets (always. just to be safe)        
        my @sarr = ( map {$_->toMatlabString($plist, $level+1, $expand)} @{$expr->Arglist} );
        if ( @sarr > 1 )
        {   # binary or higher order
            $string = '(' . join( $type, @sarr ) . ')';
        }
        else
        {   # unary operator
            $string = '(' . $type . $sarr[0] . ')';
        }
    }

    return $string;
}


###
###
###


{
    my %ophash =
    (
        '+'  => 'plus',
        '-'  => 'minus',
        '*'  => 'times',
        '/'  => 'divide',
        '**' => 'power',
        '^'  => 'power',
        '&&' => 'and',
        '||' => 'or',
        '<'  => 'lt',
        '>'  => 'gt',
        '<=' => 'leq',
        '>=' => 'geq',
        '!=' => 'neq',
        '==' => 'equivalent'
    );

	my %fnhash =
	(
	  	'_pi'   => 'pi',
  		'_e'    => 'exponentiale',
		'exp'   => 'exp',
  		'ln'    => 'ln',
  		'log10' => 'log',
#  		'log2'  => Requires special handling (see below)
  		'abs'   => 'abs',
#  		'rint'  => Requires special handling (see below)
  		'sqrt'  => 'root',
  		'cos'   => 'cos',
  		'sin'   => 'sin',
  		'tan'   => 'tan',
  		'asin'  => 'arcsin',
  		'acos'  => 'arccos',
  		'atan'  => 'arctan',
  		'sinh'  => 'sinh',
  		'cosh'  => 'cosh',
  		'tanh'  => 'tanh',
  		'asinh' => 'arcsinh',
  		'acosh' => 'arccosh',
  		'atanh' => 'arctanh',
#  		'if'    => Requires special handling (see below)
  		'min'   => 'min',
  		'max'   => 'max',
  		'sum'   => 'sum',
  		'avg'   => 'mean'		
	);

    sub toMathMLString
    {
        my $expr   = shift;
        my $plist  = (@_) ? shift : '';
        my $indent = (@_) ? shift : '';
        my $level  = (@_) ? shift : 0;

        if ( $level > $MAX_LEVEL ) { die "Max recursion depth $MAX_LEVEL exceeded."; }

        my $string  = "";
        my $indentp = $indent;
        if ( $level == 0 )
        {
            $string .=
            $indent . "<math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n";
            $indentp .= "  ";
        }

        my $type = $expr->Type;
        if ( $type eq 'NUM' )
        {
            $string .= sprintf "%s<cn> %s </cn>\n", $indentp, $expr->Arglist->[0];
        }
        elsif ( $type eq 'VAR' )
        {
            $string .= sprintf "%s<ci> %s </ci>\n", $indentp, $expr->Arglist->[0];
        }
        elsif ( $type eq 'FunctionCall' ) {
			my @arglist  = @{ $expr->Arglist };
			my $func_name = shift(@arglist); # Get function name
			
			# Built-in functions
			if (isBuiltIn($func_name)){
				
				# Special handling for log2(x)
				if ($func_name eq 'log2'){
					$string .= $indentp . "<apply>\n"; 
					$string .= $indentp . "  <log/><logbase><cn>2</cn></logbase>\n";
					$string .= $arglist[0]->toMathMLString( $plist, $indentp . "  ", $level + 1 );
					$string .= $indentp . "</apply>\n";
				}
				# Special handling for rint(x)
				elsif ($func_name eq 'rint'){
					$string .= $indentp . "<apply>\n"; 
					$string .= $indentp . "  <floor/>\n";
					$string .= $indentp . "  <apply>\n";
					$string .= $indentp . "    <plus/>\n";
					$string .= $indentp . "    <cn> 0.5 </cn>\n";
					$string .= $arglist[0]->toMathMLString( $plist, $indentp . "    ", $level + 1 );
					$string .= $indentp . "  </apply>\n";
					$string .= $indentp . "</apply>\n";
				}
				# Special handling for if(x,y,z)
				elsif ($func_name eq 'if'){
					$string .= $indentp . "<piecewise>\n";
				    $string .= $indentp . "  <piece>\n";
					$string .= $arglist[1]->toMathMLString( $plist, $indentp . "    ", $level + 1 );
					$string .= $arglist[0]->toMathMLString( $plist, $indentp . "    ", $level + 1 );
				    $string .= $indentp . "  </piece>\n";
					$string .= $indentp . "  <otherwise>\n";
					$string .= $arglist[2]->toMathMLString( $plist, $indentp . "    ", $level + 1 );
					$string .= $indentp . "  </otherwise>\n";
					$string .= $indentp . "</piecewise>\n";
				}
				# All other built-ins
				else{
		            $string .= $indentp . "<apply>\n";
		            my $indentpp = $indentp . "  ";
		            $string .= sprintf "%s<%s/>\n", $indentpp, $fnhash{ $func_name }; #shift(@arglist);
		            foreach my $e (@arglist)
		            {
		                $string .= $e->toMathMLString( $plist, $indentpp, $level + 1 );
		            }
		            $string .= $indentp . "</apply>\n";
				}
			}
			# User-defined functions
			else{
				if (@arglist){ # There better not be any arguments
					die "Expression::toMathMLString: User-defined functions should not have arguments.";
				}
				$string .= sprintf "%s<ci> %s </ci>\n", $indentp, $func_name;
			}
        }
        else
        {
            $string .= $indentp . "<apply>\n";
            my $indentpp = $indentp . "  ";
            $string .= sprintf "%s<%s/>\n", $indentpp, $ophash{ $expr->Type };
            foreach my $e ( @{ $expr->Arglist } )
            {
                $string .= $e->toMathMLString( $plist, $indentpp, $level + 1 );
            }
            $string .= $indentp . "</apply>\n";
        }

        if ( $level == 0 )
        {
            $string .= $indent . "</math>\n";
        }
        return $string;
    }
}

# Convert an array of type EXPR OP EXPR OP ... to a single Expression.
sub arrayToExpression
{
    my @earr = @_;

    # list of optypes in order of precedence
    my @operators = ('\*\*|\^', '[*/]', '[+-]', '[<>]|==|!=|~=|>=|<=', '&&|\|\|');
    my $optype = shift @operators;
    while ($optype)
    {
        my $i = 0;
        # Consolidate EXPR OP EXPR into EXPR
        while ($i < $#earr)
        {
            my $expr = $earr[$i];
            if ( $expr->Type =~ /$optype/  and  !@{$expr->Arglist} )
            {
                if ( $i > 0 )
                {
                    $expr->Arglist->[0] = $earr[ $i - 1 ];
                    $expr->Arglist->[1] = $earr[ $i + 1 ];
                    splice @earr, $i - 1, 3, $expr;
                    next;
                }
                else
                {
                    # Handle leading unary op, as in -a + b
                    $expr->Arglist->[0] = $earr[ $i + 1 ];
                    splice @earr, $i, 2, $expr;
                    ++$i;
                    next;
                }
            }
            ++$i;
        }
        # Finished with current optype
        $optype = shift @operators;
    }

    return $earr[0];
}



# extract a number expression from a BNG string.
# NOTE: the method newNumOrVar is appropriate when
#  the string ONLY contains a number or param name.  This
#  method, however, is suitable for cases where the string 
#  contains additional content.
sub getNumber
{
    my $string = shift;

    my $number = '';
    # Decimal part
    if ( $$string =~ s/^([+-]?\d+)([.]?\d*)// )
    {
        $number = $1;
        if ($2 eq '.')
        {   # pad number ending in decimal point
            $number .= ".0";
        }
        else
        {
            $number .= $2;
        }
    }
    elsif ( $$string =~ s/^([+-]?[.]\d+)// )
    {
        $number = $1;
    }
    else
    {
        return '';
    }

    # Exponent part
    if ( $$string =~ s/^([DEFGdefg][+-]?\d+)// )
    {
        $number .= $1;
    }
    elsif ( $$string =~ /^[A-Za-z_]/ )
    {
        # String is non a number; restore value of string
        $$string = $number . $$string;
        return '';
    }
  
    # create number expression and return
    my $express = Expression->new();
    $express->Type('NUM');
    $express->Arglist( [$number] );
    return $express;
}



# Returns name of VAR if expression is an existing VAR or
# creates a new VAR with name derived from $basename and 
# returns name of new VAR containing expression.
sub getName
{
    my $expr      = shift @_;
    my $plist     = shift @_;
    my $basename  = @_ ? shift @_ : "k";
    my $force_fcn = @_ ? shift @_ : 0; 

    my $name;
    if ( $expr->Type eq 'VAR'  and  !$force_fcn )
    {
        $name = $expr->Arglist->[0];
    }
    elsif ( $expr->Type eq "FunctionCall"
             and @{$expr->Arglist}==1
             and ref $expr->Arglist->[0] ne "Function" )
    {   # function call without arguments, no need to create a new parameter
        $name = $expr->Arglist->[0];
    }
    else 
    {
        # Find unused name
        my $index = 1;
        while (1)
        {
            my ($param, $err) = $plist->lookup($basename . $index);
            last unless $param;
            ++$index;
        }
        $name = $basename . $index;         
        # set parameter in list (with type Function, if force)
        $plist->set( $name, $expr, 0, ($force_fcn ? 'Function' : '') );
    }

    return $name;
}



# Return a hash of all the variable names referenced in the current expression.
sub getVariables
{
    my $expr    = shift @_;
    my $plist   = shift @_;
    my $level   = @_ ? shift @_ : 0;
    my $rethash = @_ ? shift @_ : {};

    if ($level > $MAX_LEVEL) { die "Max recursion depth $MAX_LEVEL exceeded."; }

    my $type = $expr->Type;
    if ( $type eq 'NUM' )
    {
        # nothing to do
    }
    elsif ( $type eq 'VAR' )
    {
        my ($param, $err) = $plist->lookup( $expr->Arglist->[0] );
        if ($err) { die $err };    # Shouldn't be an undefined variable name here
        if ( defined $param->Type )
        {   # add parameter name to type hash
            $rethash->{$param->Type}->{$param->Name} = $param;
        }
        else
        {   # this parameter has undefined type!
            $rethash->{'UNDEF'}->{$param->Name} = $param;
        }
    }
    elsif ( $type eq 'FunctionCall' )
    {
        if ( ref $expr->Arglist->[0] eq "Function" )
        {   # anonymous function
            # we have to descend into the function expression to see what it may reference
            $expr->Arglist->[0]->Expr->getVariables($plist, $level+1, $rethash);
        }
        elsif ( exists $functions{$expr->Arglist->[0]} )
        {   # built-in function..
            # nothing to do
        }
        else
        {   # named function
            my ($param, $err) = $plist->lookup( $expr->Arglist->[0] );
#			if (defined $param)
            if (defined $param && defined $param->Type)
            {   # add named function to rethash
                $rethash->{$param->Type}->{$param->Name} = $param;
            }
        }

        # handle the function arguments
        foreach my $i ( 1 .. $#{$expr->Arglist} )
        {
            $expr->Arglist->[$i]->getVariables($plist, $level+1, $rethash);
        }
    }
    else
    {
        foreach my $e ( @{$expr->Arglist} )
        {
            $e->getVariables($plist, $level + 1, $rethash);
        }
    }

    return $rethash;
}

1;
