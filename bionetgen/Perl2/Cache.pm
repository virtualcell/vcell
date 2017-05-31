package Cache;
# a simple class for stowing and fetching things by label

# pragmas
use strict;
use warnings;

use constant DEFAULT_LABEL => 'DEFAULT';

# Structure containing BioNetGen model data
sub new 
{
    my ($class) = @_;
    my $cache = {};
    bless ($cache, $class);
    return $cache;
};

# copy cache (this will NOT copy objects contained in cache)
sub copy
{
    my ($cache) = @_;
    my $copy = {};
    %$copy = %$cache;
    bless ($copy, ref $cache);
    return $copy;
}

# stow an item in cache
sub cache
{
    my ($cache, $item, $label) = @_;

    unless (defined $label)
    {   $label = DEFAULT_LABEL;   }

    $cache->{$label} = $item;
}

# browse an item in cache (get item reference, but do not remove from cache)
sub browse
{
    my ($cache, $label) = @_;

    unless (defined $label)
    {   $label = DEFAULT_LABEL;   }

    return (exists $cache->{$label} ? $cache->{$label} : undef);
}

# fetch an item from cache (item is removed from cache!)
sub fetch
{
    my ($cache, $label) = @_;

    unless (defined $label)
    {   $label = DEFAULT_LABEL;   }

    my $item = undef;
    if (exists $cache->{$label})
    {
        $item = $cache->{$label};
        delete $cache->{$label};
    }
    return $item;
}

# check if a label is defined
sub is_label_defined
{
    my ($cache, $label) = @_;
    
    unless (defined $label)
    {   $label = DEFAULT_LABEL;   }

    return exists $cache->{$label};
}

# get the number of items in the cache
sub size
{
    my ($cache) = @_;
    return (scalar keys %$cache);
}

# empty the cache
sub empty
{
    my ($cache) = @_;
    $cache = {};
}

1;
