# $Id: MoleculeTypesList.pm,v 1.4 2006/09/13 03:44:06 faeder Exp $

# List of MoleculeType objects
package MoleculeTypesList;

# pragmas
use strict;
use warnings;
no warnings 'redefine';

# Perl Modules
use Class::Struct;
use FindBin;
use lib $FindBin::Bin;

# BNG Modules
use MoleculeType;
use SpeciesGraph;
use Molecule;




struct MoleculeTypesList =>
{
    MolTypes     => '%',
    StrictTyping => '$'
};



###
###
###



sub readString
{
    my $mtl = shift;
    my $entry = shift;

    # Check if token is an index
    # DEPRECATED as of BNG 2.2.6
    if ($entry=~ s/^\s*(\d+)\s+//)
    {
        return "Leading index detected at '$entry'. This is deprecated as of BNG 2.2.6.";
    }

    # Remove leading label, if exists
    if ($entry =~ s/^\s*(\w+)\s*:\s+//)
    {
		# Check label for leading number
		my $label = $1;
		if ($label =~ /^\d/) {  return "Syntax error (label begins with a number) at '$label'.";  }
    }
    
    # Next token is string for species graph
    $entry =~ s/^\s*//;
    my $mt = MoleculeType->new;
    my $err = $mt->readString( \$entry, 1, '', $mtl );
    if ($err) { return($err); }
    if ($entry =~ /\S+/)
    {
        return "Syntax error in MoleculeType declaration";
    }
  
    # Check if type previously defined
    if ( $mtl->MolTypes->{$mt->Name} )
    {
        $err = sprintf "Molecule type %s previously defined.", $mt->Name;
        return $err;
    }

    # Create new MoleculeType entry
    $mtl->MolTypes->{$mt->Name} = $mt;

    return '';
}



###
###
###


sub getNumMolTypes
{
    my $mtlist = shift;
    return scalar keys %{$mtlist->MolTypes};

}


###
###
###



# get a copy of a moleculeTypesList, along with copies of all the moleculeTypes
sub copy
{
    my $mtlist = shift;
    
    my $mtlist_copy = MoleculeTypesList::new();
    while ( my ($name,$mt) = each %{$mtlist->MolTypes} )
    {
        my $mt_copy = $mt->copy();
        $mtlist_copy->MolTypes->{$mt_copy->Name} = $mt_copy;
    }
    $mtlist_copy->StrictTyping( $mtlist->StrictTyping );
    
    return $mtlist_copy;
}



###
###
###



# add a moleculeType to the list
sub add
{
    my $mtlist = shift;
    my $mt = shift;
 
    if ( exists $mtlist->MolTypes->{$mt->Name} )
    {   # molecule type is already in list
        return 0;
    }
    else
    {   # add molecule type to list
        $mtlist->MolTypes->{$mt->Name} = $mt;
        return 1;
    }
}



###
###
###



# find a moleculeType by name
sub findMoleculeType
{
    my $mtl  = shift;
    my $name = shift;

    my $mt = undef;
    if ( exists $mtl->MolTypes->{$name} )
    {
        $mt = $mtl->MolTypes->{$name};
    }
    return $mt;
}



###
###
###


# Check whether Molecules in SpeciesGraph match declared types
# Set $params->{IsSpecies} to 1 to force all components to be 
# declared with defined states (if states are defined for the component)
sub checkSpeciesGraph
{
    my $mtl = shift;
    my $sg = shift;
    my $params = (@_) ? shift : '';

    my $IsSpecies     = (defined $params->{IsSpecies}    ) ? $params->{IsSpecies}     : 1;
    my $AllowNewTypes = (defined $params->{AllowNewTypes}) ? $params->{AllowNewTypes} : 0;

    foreach my $mol (@{$sg->Molecules})
    {
        my $mtype;
        if ($mol->Name =~ /[*]/)
        {
            my $found_match=0;
            # Handle mol names containing wildcards
            foreach $mtype (keys %{$mtl->MolTypes})
            {
	            next unless ($mol->Name =~ $mtype);
	            if ( $mtype->check($mol,$params) eq '' )
                {
	                ++$found_match;
	            }
	            unless ($found_match)
                {
	                my $err = sprintf "Molecule string %s does not match any declared molecule types", $mol->toString();
	                return $err;
	            }
            }
        }
        elsif ( $mtype = $mtl->MolTypes->{$mol->Name} )
        {
            # Validate against declared type
            if ( my $err = $mtype->check($mol,$params) )
            {
	            return $err;
            }
        }
        else
        {
            # Type not found.  
            if ($AllowNewTypes)
            {
	            #Define a new type
	            my $mtype= MoleculeType->new;
	            $mtype->add($mol);
	            $mtl->MolTypes->{$mol->Name} = $mtype;
            }
            else
            {
	            my $err = sprintf "Molecule %s does not match any declared molecule types", $mol->toString();
	            #$err.= "\n".$mtl->writeBNGL();
	            return $err;
            }
        }
    }

    return '';
}



###
###
###



sub checkMolecule
{
    my $mtl = shift @_;
    my $mol = shift @_;
    my $params = @_ ? shift @_ : {};

    my $IsSpecies     = exists $params->{IsSpecies}     ? $params->{IsSpecies}     : 1;
    my $AllowNewTypes = exists $params->{AllowNewTypes} ? $params->{AllowNewTypes} : 0;

    my $mtype;
    if ( $mtype = $mtl->MolTypes->{$mol->Name} )
    {
        # Validate against declared type
        if ( my $err = $mtype->check($mol,$params) )
        {   return $err;   }
    }
    else
    {
        # Type not found.  
        if ($AllowNewTypes)
        {
            # Define a new type
            my $mtype = MoleculeType->new;
            $mtype->add($mol);
            $mtl->MolTypes->{$mol->Name} = $mtype;
        }
        else
        {
            my $err = sprintf "Molecule %s does not match any declared molecule types", $mol->toString();
            return $err;
        }
    }
  
    return '';
}



###
###
###



sub writeBNGL
{
    my $mtlist      = shift @_;
    my $user_params = @_ ? shift @_ : { 'pretty_formatting'=>0 };

    my $max_length = 0;
    if ( $user_params->{pretty_formatting} ) 
    {   # find longest molecule type string
        while ( my ($name, $mt) = each %{$mtlist->MolTypes} )
        {    
            my $string = $mt->toString();
            $max_length = ( length $string > $max_length ) ? length $string : $max_length;
        }
    }

    my $out = "begin molecule types\n";
    my $index = 1;
#    while ( my ($name, $mt) = each %{$mtlist->MolTypes} )
	foreach my $key (sort keys %{$mtlist->MolTypes})
    {
    		my $mt = $mtlist->MolTypes->{$key};
        if ( $user_params->{pretty_formatting} )
        {   # no molecule type index
            $out .= '  ' . $mt->toString($max_length) . "\n";
        }
        else
        {   # include index
            $out .= sprintf "%5d %s\n", $index, $mt->toString();
        }

        ++$index;
    }    
    $out .= "end molecule types\n";
    
    return $out;
}



###
###
###



sub toXML
{
    my $mtlist = shift;
    my $indent = shift;
    my $string = $indent."<ListOfMoleculeTypes>\n";
    # loop over molecule types
    foreach my $mname (sort keys %{$mtlist->MolTypes})
    {
        my $mt = $mtlist->MolTypes->{$mname};
        $string .= $mt->toXML("  ".$indent);
    }
    $string .= $indent."</ListOfMoleculeTypes>\n";
    return $string;
}

sub writeMDL
{
    my $mtlist = shift;
    my $indent = shift; 
    my $string = ""; 
    # loop over molecule types
    foreach my $mname (sort keys %{$mtlist->MolTypes})
    {
 	my $mt = $mtlist->MolTypes->{$mname};
        $string .= $indent.$mt->writeMDL()."\n";
    }
    return $string;
}


1;
