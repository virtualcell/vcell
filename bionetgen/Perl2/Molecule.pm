# $Id: Molecule.pm,v 1.9 2007/07/06 04:48:21 faeder Exp $

package Molecule;

# pragmas
use strict;
use warnings;

# Perl Modules
use Class::Struct;
use FindBin;
use lib $FindBin::Bin;

# BNG Modules
use Component;
use BNGUtils;

# constants
use constant { TRUE                 => 1,
               FALSE                => 0
             };



struct Molecule =>
{
    Name        => '$',
    State       => '$',
    Edges       => '@',
    Label       => '$',
    Compartment => 'Compartment',
    Components  => '@',
    Context     => '$'
};



###
###
###



sub newMolecule 
{
	my $string = shift @_;
	my $clist  = shift @_;

	my $mol = Molecule->new();
	my $err = $mol->readString( $string, $clist );
	return ($mol, $err);
}



###
###
###



sub readString
{
	my $mol    = shift;
	my $strptr = shift;
	my $clist  = shift;

	my $string_left = $$strptr;

	# Get molecule name (cannot start with a number)
	if ( $string_left =~ s/^([A-Za-z_]\w*)// )
	{
		$mol->Name($1);
	}
	else
	{
		return undef, "Invalid Molecule name in '$string_left' (must begin with a letter or underscore).";
	}

	# Get molecule state (marked by ~) edges (marked by !) and label (marked by
	# %) and components (enclosed in ())
	my $edge_wildcard = 0;
	while ($string_left)
    {
		# Read components in parentheses
		if ( $string_left =~ s/^[(]// )
        {
			if (@{$mol->Components}) 
            {
				return undef, "Multiple component definitions";
			}
			while ($string_left)
            {
				# Continue characters
				next if ( $string_left =~ s/^,// );
				# Stop characters
				last if ( $string_left =~ s/^\)// );

				# Read component
				my ( $comp, $err ) = Component::newComponent( \$string_left );
				if ($err) {  return undef, $err;  }
			
                # Save components	
                push @{$mol->Components}, $comp;
			}
		}

		# Read attributes in braces
		elsif ( $string_left =~ s/^[{]// )
		{
			while ( !( $string_left =~ s/^\}// ) )
			{
				my $attr  = '';
				my $value = '';

				# Get attribute name
				if ( $string_left =~ s/^([^,=\}]+)// )
                {
					$attr = $1;
				}
				else
                {
					return undef, "Null attribute for Molecule at $string_left";
				}

				# Get (optional) attribute value
				if ( $string_left =~ s/^=([^,\}]+)// )
                {
					$value = $1;
				}

				# Remove trailing comma
				$string_left =~ s/^,//;

				if ( $attr eq "Context" )
                {
					my $val = booleanToInt($value);
					if ( $val == -1 )
                    {
						return undef, "Invalid value $value assigned to Boolean attribute $attr";
					}
					$mol->Context($val);
				}
				else
                {
					return undef, "Invalid attribute $attr for Molecule";
				}
			}
		}

		# Read state, edge, label, or compartment
		elsif ( $string_left =~ s/^([~%!@])(\w+|\+|\?)// )
		{
			my $type = $1;
			my $arg  = $2;
			if ( $type eq '~' )
            {   # State label
				if ( defined $mol->State )
                {
					return undef, "Multiple state definitions";
				}
				if ( $arg =~ /(\w+|\?)/ ) {  $mol->State($arg);  }
                else
                {
                    return undef, "Invalid state label in molecule";
                }
			}
			elsif ( $type eq '!' )
            {   # Bond label or wildcard
				if ( $arg =~ /^[+?]$/ )
                {
					if ($edge_wildcard)
                    {
						return undef, "Multiple edge wildcards in molecule";
					}
					$edge_wildcard = 1;
					push @{$mol->Edges}, $arg;
				}
			}
			elsif ( $type eq '%' )
            {   # Tag label
				if ( defined $mol->Label )
                {
					return undef, "Multiple label definitions";
				}
				$mol->Label($arg);
			}
			elsif ( $type eq '@' )
            {   # Compartment label
				if ( defined $mol->Compartment )
                {
					return undef, "Multiple compartment definitions";
				}

				if ( my $comp = $clist->lookup($arg) )
                {
					$mol->Compartment($comp);
				}
				else
                {
					return undef, "Undefined compartment $arg";
				}
			}
		}
		# Stop characters
		elsif ( $string_left =~ /^(\.|\+|\s+|<?->)/ )
		{
			last;
		}
		# Stop at unrecognized syntax
		else
		{
            last;
		}
	}
	
    $$strptr = $string_left;
	return '';
}



###
###
###



sub toString
{
	my $mol                = shift @_;
	my $print_edges        = @_ ? shift @_ : TRUE;
	my $print_attributes   = @_ ? shift @_ : TRUE;
	my $speciesCompartment = @_ ? shift @_ : undef;

	my $string .= $mol->Name;

	$string .= sprintf("~%s", $mol->State)  if (defined $mol->State);

    if ($print_attributes)
    {   $string .= sprintf("%%%s", $mol->Label)  if (defined $mol->Label);   }
	
	if ( defined $mol->Edges )
	{
		if ( $print_edges)
		{
			my $wildcard = "";
			foreach my $edge ( @{$mol->Edges} )
            {
				if ( $edge =~ /^\d+$/ )
                {   $string .= sprintf "!%d", $edge + 1;   }
				else
                {   $wildcard = "!$edge";   }
				$string .= $wildcard;
			}
		}
		else
		{   $string .= "!" x scalar( @{$mol->Edges} );   }
	}

	if ( defined $mol->Components )
	{
		my $icomp = 0;
		$string .= "(";
		foreach my $comp ( @{$mol->Components} )
		{
			if ($icomp)
			{   $string .= ',';   }
			$string .= $comp->toString($print_edges, $print_attributes);
			++$icomp;
		}
		$string .= ")";
	}
	else
    {   $string .= "()";   }

	if ( defined $mol->Compartment )
	{  
		unless ( (defined $speciesCompartment) and ($mol->Compartment == $speciesCompartment) )
		{   $string .= sprintf "@%s", $mol->Compartment->Name;   }
	}

	# attributes
    if ($print_attributes)
    {
	    my @attr = ();
	    if ( $mol->Context )
        {   push @attr, "Context";   }
	    if (@attr)
        {   $string .= '{' . join( ',', @attr ) . '}';   }
    }

	return $string;
}



###
###
###



sub toStringSSC
{
	my $mol = shift @_;

	my $mname   = $mol->Name;
	my $string .= $mol->Name;

	my %checkComp;           # A hash to check same component names
	my $sameCompExists = 0;  # Have we found repeated components? (This doesn't do anything now)

    $string .= '(';
	my $icomp = 0;
	foreach my $comp (@{$mol->Components})
    {
    	unless ($icomp == 0) { $string .= ','; }

		if (defined $comp->Name)
        {
			my $cname = $comp->Name;
			if (exists $checkComp{$cname})
            {
				$checkComp{$cname} = ++$checkComp{$cname};

				print "\n same component exists for $mname \n";
				print "   SSC rules do not handle components with same name \n";
				print"    In this preliminary version of BNG-SSC translator we do not handle this case";
			}
			else
            {
				$checkComp{$cname} = 0;
			}

			if ( $sameCompExists == 0 ) { $string .= $comp->toStringSSC(); }
		}
		++$icomp;
	}

    $string .= ')';
	return $string, 0;
}



###
###
###



# a subroutine which fetches the number of same components in a molecule.
# returns a hash. key = name of teh component; value = no. of same components
sub getCompHash
{
    my $mol = shift;
    my $string .= $mol->Name;
    my %checkComp;    # A hash to check same component names

    if ( defined $mol->Components )
    {
        foreach my $comp ( @{$mol->Components} )
        {
            if ( defined $comp->Name )
            {
                my $cname = $comp->Name;
                if ( exists $checkComp{$cname} )
                {
	                $checkComp{$cname} = ++$checkComp{$cname};
                }
                else
                {
	                $checkComp{$cname} = 0;
                }
            }
        }
    }
    return %checkComp;
}

# this toString is just used in corresponding seed species block.
# As in SSC one only specifies molecules, molecules if they hava a defined states
# Or molecules with bonds.

sub toStringSSCMol
{
	my $mol         = shift @_;
	my $print_edges = @_ ? shift @_ : TRUE;

	my $string              = '';
	my $icomp               = 0;
	my $test                = 0;
	foreach my $comp ( @{ $mol->Components } ) {
		if ( defined( $comp->Edges ) ) {
			$test = 0;
			foreach my $edge ( @{ $comp->Edges } ) {
				if ( $edge =~ /^\d+$/ ) {
					++$test;
					if ( $icomp != 0 ) { $string .= ","; }
					if ( defined( $comp->State ) ) {
						if ( $icomp == 0 ) { $string .= "("; }
						$string .= $comp->toStringSSC();
						++$icomp;
					}    #Dont do anything if state has a bond
					     #Changes already in toString of Component.pm
					if ( ( !defined( $comp->State ) ) ) {
						if ( $icomp == 0 ) { $string .= "("; }
						$string .= $comp->Name . "#" . ( $edge + 1 );
						++$icomp;
					}
				}
			}
		}
		if ( $test == 0 ) {
			if ( defined( $comp->State ) ) {
				if ( $icomp == 0 ) { $string .= "("; }
				if ( $icomp != 0 ) { $string .= ","; }
				$string .= $comp->Name . "=\"" . $comp->State . "\"";
				++$icomp;
			}
		}

	}

	if ( $icomp != 0 ) { $string .= ")"; }
	if ( $icomp == 0 ) { $string .= "()"; }
	return ($string);
}



###
###
###



sub toStringMCell
{
	my $mol                = shift @_;
	my $print_edges        = @_ ? shift @_ : TRUE;
	my $print_attributes   = @_ ? shift @_ : TRUE;
	my $speciesCompartment = @_ ? shift @_ : "";

	my $string .= $mol->Name;

	if ( defined( $mol->State ) ) {
	#do something	$string .= sprintf "~%s", $mol->State;
	}

	if ( defined( $mol->Label ) ) {
		#$string .= sprintf "%%%s", $mol->Label;
	}

	if ( defined( $mol->Edges ) ) {
		# do something
	}

	if ( defined( $mol->Components ) ) {
		#do something
	}
	
	if ( defined( $mol->Compartment ) ) {
	#do something
	}

	return ($string);
}



###
###
###



sub toXML
{
	my $mol    = shift @_;
	my $indent = shift @_;
	my $id     = shift @_;
	my $index  = (@_) ? shift @_ : '';

	my $string = $indent . "<Molecule";

	# Attributes
	# id
	my $mid = sprintf "${id}_M%d", $index;
	$string .= " id=\"" . $mid . "\"";

	# molecule type name
	$string .= " name=\"" . $mol->Name . "\"";
    # molecule label
	if ( defined $mol->Label )
    {
		$string .= " label=\"" . $mol->Label . "\"";
	}
    # compartment
	if ( defined $mol->Compartment )
    {
		$string .= " compartment=\"" . $mol->Compartment->Name . "\"";
	}

	# Objects contained
	my $indent2 = '  ' . $indent;
	my $ostring = '';

	# Molecules
	if ( @{$mol->Components} )
    {
		$ostring .= $indent2 . "<ListOfComponents>\n";
		my $cindex = 1;
		foreach my $comp ( @{$mol->Components} )
        {
			$ostring .= $comp->toXML( '  ' . $indent2, $mid, $cindex );
			++$cindex;
		}
		$ostring .= $indent2 . "</ListOfComponents>\n";
	}

	# Termination
	if ($ostring)
    {   # terminate tag opening
		$string .= ">\n";
		$string .= $ostring;
		$string .= $indent . "</Molecule>\n";
	}
	else
    {   # short tag termination
		$string .= "/>\n";
	}
}



###
###
###



# make exact copy of molecule
sub copy
{
    # get molecule that we want to copy
	my $mol = shift;
    # should we copy labels?
    my $copy_labels = (@_) ? shift : 1;
    # add prefix to edges
    my $prefix = (@_) ? shift : '';

    # create new molecule
	my $mol_copy = Molecule->new();
    
	# copy scalar attributes
	$mol_copy->Name( $mol->Name );
    $mol_copy->State( $mol->State );
    $mol_copy->Label( $mol->Label ) if ($copy_labels);
    $mol_copy->Context( $mol->Context );
    $mol_copy->Compartment( $mol->Compartment ) if (defined $mol->Compartment);

    # copy edges
	if ( @{$mol->Edges} )
	{
	    # add prefix to edge label, unless its a wildcard
	    $mol_copy->Edges( [map {$_=~/^[*+?]$/ ? $_ : $prefix.$_} @{$mol->Edges}] );
	}
	# copy components
	if ( @{$mol->Components} )
	{
		$mol_copy->Components( [map {$_->copy($copy_labels,$prefix)} @{$mol->Components} ] );
	}

    # return molecule copy
	return $mol_copy;
}


###
###
###


# call this method to link Compartments to a new CompartmentList
sub relinkCompartments
{
    my $mol = shift;
    my $clist = shift;
    
    my $err;
    unless ( ref $clist eq 'CompartmentList' )
    {   return "Molecule->relinkCompartments: Error!! Method called without CompartmentList object";   }
    
    if ( defined $mol->Compartment )
    {
        my $new_comp = $clist->lookup( $mol->Compartment->Name );
        unless ($new_comp)
        {   return "Molecule->relinkCompartments: Error!! could not find compartment name in list";   }
        $mol->Compartment( $new_comp );
    }
    
    foreach my $comp ( @{$mol->Components} )
    {
        $err = $comp->relinkCompartments( $clist );
        if (defined $err) {  return $err;  }
    }
    
    return undef;
}



###
###
###



# Molecule comparison for isomorphism
sub compare_local
{
	my $a = shift;
	my $b = shift;

	my $cmp;

	# Molecule name
	if ( $cmp = ($a->Name cmp $b->Name) )
    {	return $cmp;   }

	# Molecule state
	if ( $cmp = ($a->State cmp $b->State) )
    {   return $cmp;   }

	# Molecule compartment
	if ( $cmp = ( $a->Compartment->Name cmp $b->Compartment->Name ) )
    {   return $cmp;   }

	# Number of edges
	if ( $cmp = (@{$a->Edges} <=> @{$b->Edges}) )
    {	return $cmp;   }

	# Number of Components
	if ( $cmp = (@{$a->Components} <=> @{$b->Components}) )
    {   return $cmp;   }

	# Components
	for ( my $ic = 0;  $ic < @{$a->Components};  ++$ic )
    {
		if ( $cmp = $a->Components->[$ic]->compare($b->Components->[$ic]) )
        {   return $cmp;   }
	}

	return 0;
}



###
###
###

1;
