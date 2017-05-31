# $Id: Component.pm,v 1.10 2007/01/22 19:22:48 faeder Exp $

package Component;

# pragmas
use strict;
use warnings;

# Perl Modules
use Class::Struct;
use FindBin;
use lib $FindBin::Bin;

# BNG Modules
use BNGUtils;
use Compartment;

# constants
use constant { TRUE                 => 1,
               FALSE                => 0
             };


struct Component => {
    Name  => '$',
    State => '$',
    #Type  => 'ComponentType',
    Edges => '@',
    Label => '$', 
    Compartment => 'Compartment'
};



###
###
###



sub newComponent
{
    my $strptr = shift;

    my $comp = Component->new();
    my $err = $comp->readString($strptr);

    return ($comp, $err);
}



###
###
###



sub readString
{
    my $comp   = shift @_;
    my $strptr = shift @_;
    my $clist  = @_ ? shift @_ : undef;

    my $string_left = $$strptr;
    
    # Get component name (cannot start with a number)
	if ( $string_left =~ s/^([A-Za-z_]\w*)// )
	{
		$comp->Name($1);
	}
	else
	{
		return undef, "Invalid component name in '$string_left' (must begin with a letter or underscore).";
	}

    # Get component state (marked by ~) edges (marked by !) and label (marked by %) 
    my $edge_wildcard = 0;
    my $edge_labels = 0;
    while ($string_left)
    {
        if ( $string_left =~ s/^([~%!@])(\w+|\+|\?)// )
        {
            my $type = $1;
            my $arg  = $2;
            if ($type eq '~')
            {
                # State label
	            if (defined $comp->State)
                {   return undef, "Multiple state definitions";   }
	            $comp->State($arg);
            }
            elsif ($type eq '!')
            {
                # Bond label or wildcard
	            if ( $arg =~ /^[+?]$/ )
                { 
	                if ($edge_wildcard or $edge_labels)
                    {   return undef, "Multiple edge wildcards in component";   }
	                $edge_wildcard=1;
	            }
                else
                {
                    if ($edge_wildcard)
                    {   return undef, "Labeled edges mixed with wildcard";   }
                }
	            push @{$comp->Edges}, $arg;
            }
            elsif ($type eq '%')
            {
                # Tag label
	            if (defined $comp->Label)
                {   return undef, "Multiple label definitions";   }
	            $comp->Label($arg);
            }
            elsif ($type eq '@')
            {
                # Compartment label
	            if (defined $comp->Compartment)
                {   return undef, "Multiple compartment definitions";   }

                if ( defined $clist )
                {
				    if ( my $compart = $clist->lookup($arg) )
                    {   $comp->Compartment($compart);   }
                }
				else
                {
					return undef, "Undefined compartment $arg";
				}
            }
        }
        elsif ( $string_left =~ /^[,)]/ )
        {   # Terminator characters for component
            last;
        }
        else
        {   return undef, "Invalid syntax at $string_left";   }
    }

    $$strptr = $string_left;
    return '';
}



###
###
###



sub toString
{
    my $comp = shift @_;
    my $print_edges      = @_ ? shift @_ : TRUE;
    my $print_attributes = @_ ? shift @_ : TRUE;

    my $string .= $comp->Name;

    if (defined $comp->State )
    {   $string .= sprintf "~%s", $comp->State;   }

    if ($print_attributes)
    {   $string .= sprintf("%%%s", $comp->Label)  if (defined $comp->Label);   }

    if ( defined $comp->Compartment )
    {   $string .= sprintf "@%s", $comp->Compartment->Name;   }

    if (defined $comp->Edges )
    {
        if ($print_edges)
        {
            my $wildcard='';
            foreach my $edge (@{$comp->Edges})
            {
                if ($edge=~ /^\d+$/)
                {   $string .= sprintf "!%d", $edge+1;   }
                else
                {   $wildcard = "!$edge";   }
            }
            $string .= $wildcard;
        } 
        else
        {   $string .= "!" x @{$comp->Edges};   }
    }

    return $string;
}



###
###
###



sub toStringSSC
{
    my $comp        = shift @_;
    my $comp_label  = @_ ? shift @_: 0;
    my $print_edges = @_ ? shift @_ : TRUE;

    my $string      = $comp->Name;
    if ( $comp_label != 0 ) { $string .= $comp_label; } # for checking components with same name
                                                        # if comp_label != 0 same component exists

    if (defined $comp->Label)
    {   printf STDERR "ERROR: SSC Does not implement Label \n";   }

    if (defined $comp->Compartment)
    {   printf STDERR "ERROR: Compartments not implemented \n";   }

    if (defined $comp->State)
    {
        $string.= sprintf "=\"%s\"", $comp->State;
        if ($comp_label ==0 )
        {   # just appends <component name>Binds, as SSC doesnt allow states to bind
            $string .= ", " . $comp->Name . "Binds";
        }
        else
        {
            $string .= ", " . $comp->Name . $comp_label ."Binds";
        }
    }

    if (defined $comp->Edges)
    {
        $string .= "#"; #As SSC considers every non-bound component as empty, so attaching #
        if ($print_edges)
        {
            my $wildcard="";
            foreach my $edge (@{$comp->Edges})
            {
                if ($edge=~ /^\d+$/)
                {
                    $string.= sprintf "%d", $edge+1;
                }
                else
                {
                    $wildcard="$edge";
                }
                $string.=$wildcard;
            }
        }
        else
        {
            $string.= ""x scalar(@{$comp->Edges});
        }
    }
    $string =~ s/\+/_/; # SSC equivalent of '+' is '_', substituting that
    return $string;
}



###
###
###



sub toXML
{
    my $comp   = shift @_;
    my $indent = shift @_;
    my $id     = shift @_;
    my $index  = (@_) ? shift @_ : '';

    my $string = $indent . "<Component";

    # Attributes
    # id
    my $cid = sprintf "${id}_C%d", $index;
    $string .= " id=\"".$cid."\"";
    # type
    $string .= " name=\"" . $comp->Name . "\"";
    # state
    if (defined $comp->State)
    {
        $string .= " state=\"" . $comp->State . "\"";
    }
    if (defined $comp->Label)
    {
        $string .= " label=\"" . $comp->Label . "\"";
    }
    if ($comp->Compartment)
    {
        $string .= " compartment=\"" . $comp->Compartment->Name . "\"";
    }

    # NumberOfBonds
    my $nbonds = 0;
    my $wildcard = '';
    foreach my $edge (@{$comp->Edges})
    {
        if ($edge=~ /^[?+]$/)
        {
            $wildcard = $edge;
        }
        else
        {
            ++$nbonds;
        }
    }
    if ($wildcard)
    {
        $nbonds = ($nbonds>0) ? $nbonds.$wildcard : $wildcard;
    }
    $string .= " numberOfBonds=\"" . $nbonds . "\"";

    # Objects contained
    my $indent2 = '  ' . $indent;
    my $ostring = '';

    # Termination
    if ($ostring)
    {   # terminate tag opening
        $string .= ">\n";
        $string .= $ostring;
        $string .= $indent . "</Component>\n";
    }
    else
    {   # short tag termination
        $string .= "/>\n"; 
    }
}



###
###
###



# Make an exact copy of a component.
sub copy
{
    # get component that we want to copy
    my $comp = shift @_;
    # should we copy labels?
    my $copy_labels = (@_) ? shift @_ : 1;
    # prefix for edge names?
    my $prefix = (@_) ? shift @_ : '';

    # create a new component
    my $comp_copy = Component->new();

    # copy scalar attributes
    $comp_copy->Name( $comp->Name );
    $comp_copy->Label( $comp->Label ) if ($copy_labels);
    $comp_copy->State( $comp->State );
    $comp_copy->Compartment( $comp->Compartment ) if (defined $comp->Compartment);

    # copy edges
    if ( @{$comp->Edges} )
    {
        # add prefix to edge label, unless its a wildcard
        $comp_copy->Edges( [map {$_=~/^[*+?]$/ ? $_ : $prefix.$_} @{$comp->Edges}] );
    }

    # return the copy component  
    return $comp_copy;
}



###
###
###



# call this method to link Compartments to a new CompartmentList
sub relinkCompartments
{
    my $comp  = shift @_;
    my $clist = shift @_;
    
    my $err;
    unless ( ref $clist eq 'CompartmentList' )
    {   return "Component->relinkCompartments: Error!! Method called without CompartmentList object";   }
    
    if ( defined $comp->Compartment )
    {
        my $new_compart = $clist->lookup( $comp->Compartment->Name );
        unless ($new_compart)
        {   return "Molecule->relinkCompartments: Error!! could not find compartment name in list";   }
        $comp->Compartment( $new_compart );
    }
    
    return undef;
}



###
###
###



# Component comparison for isomorphism
sub compare_local
{
    my $a = shift;
    my $b = shift;

    my $cmp;
    # Component name
    if ( $cmp = ($a->Name cmp $b->Name) )
    {   return $cmp;   }

    # Component state
    if ( defined $a->State )
    {
        unless (defined $a->State) { return 1; }
        if ( $cmp = ($a->State cmp $b->State) )
        {   return $cmp;   }
    }
    elsif ( defined $b->State )
    {
        return -1;
    }

    # Component compartment
    if ( defined $a->Compartment )
    {
        unless (defined $b->Compartment) { return 1; }
        if ( $cmp = ($a->Compartment <=> $b->Compartment) )
        {   return $cmp;   }
    }
    elsif ( defined $b->Compartment )
    {
        return -1;
    }

    # Number of edges
    if ( $cmp = (@{$a->Edges} <=> @{$b->Edges}) )
    {   return $cmp;   }

    return 0;
}



###
###
###

1;
