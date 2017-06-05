# $Id:$

package CompartmentList;

# pragmas
use strict;
use warnings;

# Perl Modules
use Class::Struct;
use FindBin;
use lib $FindBin::Bin;

# BNG Modules
use Compartment;
use ParamList;




struct CompartmentList =>
{
    Array => '@',   # list of Compartments
    Hash  => '%',   # map from Compartment name to compartment object ref.
    Used  => '$'    # Flag to indicate that compartment attribute is set in 1 or more species in model 
};


# copy the entire CompartmentList and all Compartments it contains
sub copy
{
    my $clist = shift @_;
    my $plist = @_ ? shift @_ : undef;
    
    # create copy of list
    my $clist_copy = CompartmentList->new();
    foreach my $comp ( @{$clist->Array} )
    {
        my $comp_copy = $comp->copy( $plist );
        push @{$clist_copy->Array}, $comp_copy;
        $clist_copy->Hash->{$comp_copy->Name} = $comp_copy;
    }
    $clist_copy->Used($clist->Used);  # TODO: is this the best assumption?
        
    # fix links to inside and outside neighbors
    foreach my $comp ( @{$clist_copy->Array} )
    {
        $comp->relinkNeighbors( $clist_copy );
    }
    
    return $clist_copy;
}


###
###
###



sub getNumCompartments
{
    my $clist = shift;
    return scalar @{$clist->Array};
}


###
###
###


# lookup a compartment by name.  Returns reference or empty string
sub lookup
{
    my $clist = shift;
    my $name  = shift;
  
    if ( exists $clist->Hash->{$name} )
    {
        return $clist->Hash->{$name};
    }
    else
    {
        return '';
    }
}


###
###
###


sub add
# '' = @clist->add( cref1, cref2, ... crefN )
# add compartment to a compartment list
# updated by justinshogg@gmail.com 17feb2009
# overloaded method to add compartments by
# reference.
{
    my $clist = shift;  # CompartmentList ref
    my $comp = shift;

    ref $comp eq 'Compartment'
         || return "CompartmentList: Attempt to add non-compartment object $comp to CompartmentList.";   

    if ( exists $clist->Hash->{ $comp->Name } )
    {   # compartment with same name is already in list
        # swap old compartment for the new one
        my $cref = $clist->Hash->{ $comp->Name };
        %{$cref} = %{$comp};
    }
    else
    {   # add new compartment
        $clist->Hash->{ $comp->Name } = $comp;
        push @{$clist->Array}, $comp;
    }

    # continue adding compartments (recursive)
    if ( @_ )
    {  return $clist->add(@_);  }
    else
    {  return '';  }
}




sub toString
{
    my $clist = shift;
    my $plist = (@_) ? shift : undef;

    my $string = "begin compartments\n";
    foreach my $comp (@{$clist->Array}){
        $string .= '  ' . $comp->toString($plist) . "\n";
    }
    $string .= "end compartments\n";

    return $string;
}




sub readString
# $err = $clist->readString(compartment_string,param_list)
# Reads a single compartment specification from a string with the following format:
# name type volume [outside compartments]
# updates by justinshogg@gmail.com 17feb2009
{
    my $clist  = shift;  # CompartmentList
    my $string = shift;  # compartment string to parse
    my $plist  = shift;  # parameter list
	
	# Remove leading whitespace
    $string =~ s/^\s*//;
	
	# Remove leading label, if any
	if ($string =~ s/^\s*(\w+)\s*:\s+//)
	{
	    # Check label for leading number
		my $label = $1;
		if ($label =~ /^\d/) {  return "Syntax error (label begins with a number) at '$label'";  }
	}
	
    # Read name (required argument), check for starting number
    $string =~ s/^([A-Za-z_]\w*)//   or return "Invalid compartment name in '$string' (must begin with a letter or underscore)";
    my $name = $1;
	
    # Read spatialDimensions (required argument)
    $string =~ s/^\s*(\d+)//   or return "Invalid Spatial Dimensions for Compartment in '$string'"; 
    my $spatialDimensions = $1;
    # moved spatial dimension check to newCompartment sub in Compartment module

    # Read size expression (required argument, expression created on the fly)
    my $size = Expression->new();
    $size->setAllowForward(1);
    $size->readString(\$string, $plist)  and return $_;

    # Read outside compartment (optional argument, but compartment must be in list if specified)
    my $outside = undef;
    if ( $string =~ s/^([A-Za-z_0-9]+)\s*// )
    {
        my $cname = $1;
        # Get compartment reference
        $outside = $clist->Hash->{$cname}
            ||  return("Outside compartment $cname is not in CompartmentList");
    }

    # check for extraneous arguments
    if ( $string =~ /\S+/ )
    {  return "Unrecognized trailing syntax in compartment specification";  }

    # create compartment
    my ($comp, $err) = Compartment->newCompartment($name, $spatialDimensions, $size, $outside);
    return $err if ($err);

    # add compartment to list
    return $clist->add($comp);
}




sub toXML
{
    my $clist  = shift;
    my $indent = shift;
    my $plist  = (@_) ? shift : undef;

    my $string = $indent."<ListOfCompartments>\n";
  
    foreach my $comp (@{$clist->Array}){
        $string .= $comp->toXML("  ".$indent, $plist);
    }

    $string .= $indent."</ListOfCompartments>\n";
    return $string;
}




sub validate
{
    my $clist = shift;

    foreach my $comp (@{$clist->Array})
    {
        if ($comp->SpatialDimensions eq '')
        {
            my $err = sprintf "Compartment %s referred to but not defined", $comp->Name;
        }
    }
    return '';
}

1;
