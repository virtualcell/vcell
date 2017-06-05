# $Id: Param.pm,v 1.3 2006/09/26 03:36:00 faeder Exp $

package Param;

# pragmas
#use strict;
#use warnings;
no warnings 'redefine';

# Perl Modules
use Class::Struct;

# BNG Modules
use ParamList;
use Expression;



# A "parameter" object is a name associated with an Expression, Function or Observable.
struct Param =>
{
    Name     => '$',          # Parameter name
    Expr     => 'Expression', # Refers to the expression object
    Type     => '$',          # Parameter type (see below for allowed types)
    Ref      => '$',          # For Observables and Functions: refers to the function or observable object
    CVodeRef => '$',          # describes the parameter location in a CVode vector
    Index    => '$',          # vector index for network outputs.  NOTE: Constant and ConstantExpression type
                              #   are indexed separately from Observable type.  Function and Local types not indexed.
};


# This hash describes the allowed parameter types
my %allowedTypes = ( Constant           => 1,  # A number or an expression involving only numbers
                     ConstantExpression => 1,  # An expression involving at least one other Constant or ConstantExpression
		             Observable         => 1,  # A variable defined by the name of an Observable - may take a single argument
		             Function           => 1,  # A function call w/ arguments
                     Local              => 1,  # local arguments to functions
                     RRef               => 1,  # Reactant Reference (not yet implemented)
                   );


###
###
###


# evaluate the expression that this parameter refers to
sub evaluate
{
    my $param = shift @_;
    my $args  = @_ ? shift @_ : [];  
    my $plist = @_ ? shift @_ : undef;    
    my $level = @_ ? shift @_ : 0; 

    #print STDERR "eval param name: ", $param->Name ,"  type: ", $param->Type, "\n";
    if ($param->Type eq 'Constant'  or  $param->Type eq 'ConstantExpression' )
    {
        if ( defined $param->Expr )
        {   return $param->Expr->evaluate($plist, $level+1);   }
    }
    elsif ( $param->Type eq 'Function'  or $param->Type eq 'Observable' )
    {
        if ( defined $param->Ref )
        {   return $param->Ref->evaluate($args, $plist, $level+1);   }
    }
    return undef;
}


###
###
###


sub copyConstant
{
    my $param = shift @_ ;
    my $plist = @_ ? shift @_ : undef;

    return undef unless ( $param->Type eq 'Constant'  or  $param->Type eq 'ConstantExpression' );

    my $param_copy = Param->new( 'Name' => $param->Name, 'Type' => $param->Type );
    if (defined $param->Expr)
    {
        my ($expr_copy, $err) = $param->Expr->clone($plist);
        if ($err) {  print "$err\n";  return undef;  }
        $param_copy->Expr( $expr_copy );
    }
    return $param_copy;
}


###
###
###


# set the type field of this parameter
sub setType
{
    my $param = shift @_;
    my $type  = shift @_;
    my $ref   = @_ ? shift @_ : '';

    unless ( exists $allowedTypes{$type} )
    {
        die "Type $type not recognized in setType";
    }

    $param->Type($type);
    if ($ref) {  $param->Ref($ref);  }
    return '';
}


###
###
###


# Write the expression that this parameter refers to:
# expand = 0,1 : always write expression
# expand = 2   : write expression, unless constant (just write param name in this case).
sub toString
{
    my $param  = shift @_;
    my $plist  = @_ ? shift @_ : undef;
    my $level  = @_ ? shift @_ : 0;
    my $expand = @_ ? shift @_ : 0;
  
    if ( $param->Type eq 'Constant'  and  $expand == 2 )
    {
        return $param->Name;
    }
    else
    {
        if ( defined $param->Expr )
        {
            return $param->Expr->toString($plist, $level, $expand);
        }   
        else
        {   # TODO: ??
            return '';
        }
    }
}


###
###
###


# Initialize Parameter from XML hash
# ($param, $err) = Param->readXML( \%xml_hash, $plist ) 
sub readXML
{
    my ($xml_hash, $plist) = @_;

    # Read expression
    my $string = $xml_hash->{'-value'};
    my $expr = Expression->new();
    $expr->setAllowForward(1);
    if ( my $err = $expr->readString( \$string, $plist ) ) { return undef, $err; }
    $expr->setAllowForward(0);

    # setup parameter
    my $param = Param->new( 'Name'  => $xml_hash->{'-id'},
                            'Type' => $xml_hash->{'-type'},
                            'Expr' => $expr );

    # return parameter
    return $param, undef;
}


###
###
###


# Write the parameter to XML
sub toXML
{
    my $param  = shift @_;
    my $plist  = @_ ? shift @_ : undef;
    my $level  = @_ ? shift @_ : 0;
    my $expand = @_ ? shift @_ : 0;

    if ( $param->Type eq 'Constant'  and  $expand == 2 )
    {
        return $param->Name;
    }
    else
    {  
        if ( $param->Expr )
        {
            return $param->Expr->toXML($plist, $level, $expand);
        }
        else
        {
            return '';
        }
    }
}



###
###
###


# write parameter expression as a CVode string
sub toCVodeString
{
    my $param  = shift @_;
    my $plist  = @_ ? shift @_ : undef;
    my $level  = @_ ? shift @_ : 0;
    my $expand = @_ ? shift @_ : 0;    
  
    if ( $param->Type eq 'Constant')
    {
        return $param->getCVodeName;
    }
    elsif ( defined $param->Expr )
    {
        return $param->Expr->toCVodeString($plist, $level, $expand);
    }
    else
    {
        return '';
    }
}


###
###
###


sub getCVodeName
{
    my $param = shift @_;
    
    my $type = $param->Type;
    my $expr = $param->Expr;
    
    my $name;
    if    ( $type eq 'Constant')
    {
        $name = 'NV_Ith_S(expressions,' . $param->Index .')';
    }
    elsif ( $type eq 'ConstantExpression' )
    {        
        $name = 'NV_Ith_S(expressions,' . $param->Index .')';   
    }
    elsif ( $type eq 'Observable' )
    {
        $name = 'NV_Ith_S(observables,' . $param->Index .')'; 
    }
    elsif ( $type eq 'Function' )
    {
        $name = $param->Name;
    }
    elsif ( $type eq 'Local' )
    {
        $name = $param->Name;
    }

    return $name;
}


###
###
###


sub getMatlabName
{
    my $param = shift;
    
    my $name;
    my $offset = 1;
    if    ( $param->Type eq 'Constant')
    {
        $name = 'expressions(' . ($param->Index + $offset) .')';
    }
    elsif ( $param->Type eq 'ConstantExpression' )
    {        
        $name = 'expressions(' . ($param->Index + $offset) .')';   
    }
    elsif ( $param->Type eq 'Observable' )
    {
        $name = 'observables(' . ($param->Index + $offset) .')'; 
    }
    elsif ( $type eq 'Function' )
    {
        $name = $param->Name;
    }
    elsif ( $type eq 'Local' )
    {
        $name = $param->Name;
    }
    elsif ( $type eq 'RRef' )
    {
        $name = $param->Name;
    }
    
    return $name;
}


###
###
###


# write the parameter for Matlab output
sub toMatlabString
{
    my $param  = shift;
    my $plist  = (@_) ? shift : undef;
    my $level  = (@_) ? shift : 0;
    my $expand = (@_) ? shift : 0;    
  
    my $offset = 1;
    if ( $param->Type eq 'Constant' )
    {
        return 'expressions(' . ($param->Index + $offset) . ')';
    }
    elsif ( $param->Expr )
    {
        return $param->Expr->toMatlabString($plist, $level, $expand);
    }
    else
    {
        return '';
    }
}



###
###
###


sub toXMLReference
{
    my $param = shift;
    my $head  = shift;
    my $id    = shift;
    my $plist = (@_) ? shift : '';
  
    if ($head eq ''){  $head = 'Reference';  }
    my $string="<$head";

    # Attributes
    # id
    if ($id ne '')
    {
        $string.= " id=\"".$param->Name."\"";
    }
    # name 
    $string.= " name=\"".$param->Name."\"";
    # type
    $string.= " type=\"".$param->Type."\"";
  
    $string.= "/>\n";
  
    return ($string);  
}


###
###
###


sub toMathMLString
{
    my $param  = shift;
    my $plist  = (@_) ? shift : '';
    my $indent = (@_) ? shift : '';

    if ($param->Expr)
    {
        return($param->Expr->toMathMLString($plist,$indent));
    }
    return ('');
}

1;
