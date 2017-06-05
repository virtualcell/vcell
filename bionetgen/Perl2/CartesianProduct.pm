package CartesianProduct;
### A class for iterating over elements of a cartesian product


use Class::Struct;
use FindBin;
use lib $FindBin::Bin;

use warnings;
use strict;


### Members
struct CartesianProduct =>
{
    Lists        => '@',    # a list of references to lists
    NumLists     => '$',    # the number of lists participating in this CartesianProduct
    ListPosition => '$',    # minimum of list indices that were changed in last advance
    FirstIdx     => '@',    # a list of indices corresponding to the minimum element to iterate over
    CurrentIdx   => '@',    # a list of indices corresponding to the current element of the Cartesian product
    LastIdx      => '@',    # a list of indices corresponding to the end of each list
    MoreElements => '$'     # a boolean value: 1 if there are more elements in the Cartesian product, 0 otherwise.
};


### Methods:
###
### (public)
###   $cp  = CartesianProduct::new();
###   $cp_copy = $cp->copy();
###   bool = $cp->initialize( \@lists );
###   bool = $cp->getNext( \@elem );
###   bool = $cp->update( \@new_items, $list_idx );
###   bool = $cp->validate( );
###
### (private)
###   void = $cp->advance( );
###



# Copy the Cartesian Product
sub copy
{
    my $cp = shift @_;

    my $cp_copy = CartesianProduct::new();
    $cp_copy->Lists( [@{$cp->Lists}] );
    $cp_copy->NumLists( $cp->NumLists );
    $cp_copy->ListPosition( $cp->ListPosition );
    $cp_copy->FirstIdx( [@{$cp->FirstIdx}] );
    $cp_copy->CurrentIdx( [@{$cp->CurrentIdx}] );
    $cp_copy->LastIdx( [@{$cp->LastIdx}] );
    $cp_copy->MoreElements( $cp->MoreElements );

    return $cp_copy;
}



###
###
###



# Call this method to initialize a new CartesianProduct with a reference
#  to a set of lists provided as the first argument.  Returns true if the CartesianProduct
#  is initialized correctly.
sub initialize
{
    my $cp    = shift;
    my $lists = shift;
    
    # get item lists
    unless ( ref $lists eq 'ARRAY' )
    {   return 0;   }
    
    $cp->Lists( $lists );
    $cp->NumLists( scalar @$lists );


    # setup index vectors    
    my $current_idx = [];
    my $first_idx   = [];
    my $last_idx    = [];
    
    my $elem_exist = 1;
    my $ii = 0;
    while ( $ii < $cp->NumLists )
    {
        push @$current_idx, 0;
        push @$first_idx, 0;
        push @$last_idx, scalar @{$cp->Lists->[$ii]};       
        $elem_exist *= scalar @{$cp->Lists->[$ii]};
        ++$ii;
    }

    $cp->ListPosition( 0 );
    $cp->FirstIdx( $first_idx );
    $cp->CurrentIdx( $current_idx );
    $cp->LastIdx( $last_idx );      
    $cp->MoreElements( $elem_exist );
    
    return 1;
}



###
###
###



# call this method to load the next element of the CartesianProduct
#  into the reference provided as the first argument. Returns true if next
#  element was loaded into array at pointer $elem; returns false if no more elements
#  are available.
sub getNext
{
    my $cp   = shift;    # this CartesianProduct object
    my $elem = shift;    # pointer to array that store elements
    
    if ( $cp->MoreElements )
    {   # load current element and return true
        my $ii = $cp->ListPosition;
        while ( $ii < $cp->NumLists )
        {
            $elem->[$ii] = $cp->Lists->[$ii]->[ $cp->CurrentIdx->[$ii] ];
            ++$ii;
        }
        $cp->advance();
        return 1;
    }
    else
    {   # no more elements, return false
        return 0;
    }
}


###
###
###



# After iterating through all elements of the CartesianProduct,
#  add @$new_items to the list at index $list_idx.
#  Continue calling getNext to iterate over new elements of the cartesian product
#  NOTE: must iterate through all new elements before calling update again
sub update
{
    my $cp = shift;
    my $list_idx = shift;
    my $new_items = shift;
    
    if (     $cp->MoreElements
         or  $cp->NumLists != scalar @{$cp->Lists}
         or  $list_idx < 0 
         or  $list_idx >= $cp->NumLists 
         or  ref $new_items ne 'ARRAY'             )
    {   # invalid update!
        return 0;
    }
    else
    {
        # update FirstIdx: point to first element in each list, except the updated list
        #  that will point to the first new element.
        $cp->FirstIdx( [(0) x $cp->NumLists] );
        $cp->FirstIdx->[$list_idx] = @{$cp->Lists->[$list_idx]};
                
        # CurrentIdx is the same as FirstIdx
        $cp->CurrentIdx( [@{$cp->FirstIdx}] );
               
        # add new_items to list
        push @{$cp->Lists->[$list_idx]}, @$new_items;
        
        # update lastIdx
        my $ii = 0;
        my $elem_exist = $cp->NumLists ? 1 : 0;
        while ( $ii < $cp->NumLists )
        {
            $cp->LastIdx->[$ii] = scalar @{$cp->Lists->[$ii]};
            $elem_exist *= scalar @{$cp->Lists->[$ii]};
            ++$ii;
        }    
    
        $cp->ListPosition( 0 );
    
        if ( scalar @$new_items  and  $elem_exist )
        {   $cp->MoreElements(1);   }
        
        return 1;
    }
}



###
###
###



# This private method advances the index vector
sub advance
{
    my $cp = shift;
    
    if ( $cp->MoreElements )
    {
        my $ii = $cp->NumLists - 1;
        while ( $ii >= 0 )
        {
            ++($cp->CurrentIdx->[$ii]);
            last if ( $cp->CurrentIdx->[$ii] < $cp->LastIdx->[$ii] );
            $cp->CurrentIdx->[$ii] = $cp->FirstIdx->[$ii];
            --$ii;
        }
        
        $cp->ListPosition($ii);
        if ( $ii < 0 ) {  $cp->MoreElements(0);  }
    }
}






###
###
###



# This method validates the CartesianProduct state.
#  Returns true if everything looks good, false otherwise.
sub validate
{
    my $cp = shift;
    my $elem = (@_) ? shift : undef;

    # check for correct number of lists
    unless (      defined $cp->NumLists 
             and  @{$cp->Lists} == $cp->NumLists )
    {   return 0;   }

    # check that ListPosition is correctly defined
    unless (      defined $cp->ListPosition
             and  $cp->ListPosition >= 0
             and  $cp->ListPosition < $cp->NumLists )
    {   return 0;   }
    
    # check that CurrentIdx is defined
    unless ( defined $cp->CurrentIdx )
    {   return 0;   }
    
    # check that LastIdx is defined
    unless ( defined $cp->LastIdx )
    {   return 0;   }    
     
    # If we were passed $elem, check that its the correct size
    if ( defined $elem )
    {
        unless ( @$elem == @{$cp->Lists} ) {  return 0;  }
    }
    
    # loop over lists and check that everything is in order
    my $ii = 0;
    while ( $ii < $cp->NumLists )
    {
        # make sure things are defined
        unless (      defined $cp->Lists->[$ii]
                 and  defined $cp->CurrentIdx->[$ii] 
                 and  defined $cp->LastIdx->[$ii]    )
        {   return 0;   }
    
        # check that lastIndex is correct
        unless ( $cp->LastIdx->[$ii] == @{$cp->Lists->[$ii]} )
        {   return 0;   }
        
        # check that currentIndex has a valid range
        unless (      $cp->CurrentIdx->[$ii] >= 0
                 and  $cp->CurrentIdx->[$ii] < @{$cp->Lists->[$ii]} )  
        {   return 0;   }
        
        #  If we were passed $elem check that its defined correctly up to ListPosition
        if ( defined $elem  and  $ii < $cp->ListPosition )
        {
            unless ( $elem->[$ii] == $cp->Lists->[$ii]->[ $cp->CurrentIdx->[$ii] ] )
            {   return 0;   }
        }
        ++$ii;    
    }
    
    # everything checks out!
    return 1;
}


###
###
###


1;
