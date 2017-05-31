package Map;

# pragmas
#use strict;
#use warnings;
no warnings 'redefine';

# Perl Modules
use Class::Struct;
use FindBin;
use lib $FindBin::Bin;

# BNG Modules
use BNGUtils;
use SpeciesGraph;



# A Map object manages forward and reverse mappings from a source SpeciesGraph
# into a target SpeciesGraph.  The mapping is maintained as a hash map with 
# source 'pointer' keys and target 'pointer' values.
# Pointer format:
#   molecule pointer  = "m"    : where m is index of the molecule in the 
#                                 SpeciesGraph's Molecule array
#   component pointer = "m.c"  : where c is the index of the component in the 
#                                 Molecule's Component array.
struct Map => {
   Source => 'SpeciesGraph',	       
   Target => 'SpeciesGraph',	       
   MapF   => '%',	           # Forward mapping of Source onto Target
   MapR   => '%',	           # Reverse mapping of Target onto Source
};


sub toString
# Map->toString()
# generates a string representation of Map.
# there is bijection from Maps to MapStrings.
#
# NOTE: this is for maps in the "mol.comp" style, not "patt.mol.comp"
{
   my $map = shift;

   my $string = '';
   my $molecules = $map->Source->Molecules;

   foreach my $im (0..$#{$molecules} )
   {
      my $im2 = $map->MapF->{$im};
      $string .= sprintf " %d->%d(", $im+1, $im2+1;
      next if ( $im2 < 0 );
      foreach my $ic ( 0..$#{$molecules->[$im]->Components} )
      {
         my ($im2, $ic2) = split( '\.', $map->MapF->{"$im.$ic"} );
         $string .= sprintf " %d->%d", $ic+1, $ic2+1;
      }
      $string .= ')';
   }
   return $string;
}



###
###
###



sub mapF
# p2 = Map->mapF(p1)
# returns the image of a pointer under map.
#
# p2 = Map->mapF(p1, offset)
# returns the image of a pointer under map, accounting for offset
#  due to embedding of pattern within a species.
{
   my $map = shift;
   my $p1  = shift;
   my $offset = (@_) ? shift : 0;

   my $p2 = $map->MapF->{$p1};
   if ($offset)
   {
      my ($im2, $ic2) = split('\.', $p2);
      $im2 += $offset;
#      if ($ic2 ne '')
      if (defined $ic2)
      {  $p2 = "$im2.$ic2";  }
      else
      {  $p2 = "$im2";       }
   }
  
   return $p2;
}



###
###
###



sub get_induced_permutation
# (autoP, err) = autoR->get_induced_permutation(map)
#
# autoR: R -> R
# map:   R -> P
# permP: P -> P
#
# NOTE: this is a replacement for mapAuto.
#  the new version is simplified, and creates a new Map object to contain
# autoP, rather than overwriting autoR.
#
# permP o map == map o autoR
# permP = map o autoR o map^-1
# let R be the reactant objects (molecules and components) 
# and P be the product objects.
#
# ... More details ...
# given maps:
#
#     autoR: R  -> R (automorphism of R)
#      map:  R* -> P (partial mapping of R into P)
#
# where R* is the subset of R for which map is defined
#  (members of R which are deleted in a rxn will not be in R*),
# and P* is the image of map(R*), 
#
# then find a permuation of P, permP: P -> P, such that for all r in R*
#
#   permP o map == map o autoR   ( "o" is the composition operator )
#
#           
#      R ----autoR-->  R'
#      |               |
#     map             map    
#      |               |
#      v               v
#      P ----candP---> P'
#
# permP is a candidate automorphism of P. 
{
    my $autoR = shift;
    my $map = shift;
  
    if ($autoR->Source != $autoR->Target)
    {   return undef, "Map::get_induced_permutation() called from a non-self map";   }
  
    if ($autoR->Source != $map->Source)
    {   return undef, "Map::get_induced_permuation(): autoR and map have different source patterns";   }

    # create map object to hold P-permutation
    my $permP = new Map;
    my $mapF = {};
    $permP->Source( $map->Target );
    $permP->Target( $map->Target );
    $permP->MapF( $mapF );

    # loop over nodes in P
    foreach my $Pnode ( keys %{$map->MapR} )
    {
        # get the inverse of P under the rule map
        my $inv_Pnode = $map->MapR->{$Pnode};
        if ( $inv_Pnode < 0 )
        {   # Pnode is created by rule (not in the image of R)
            # use identity mapping
            $mapF->{$Pnode} = $Pnode;
        }
        else
        {   # Pnode is in the image of R
            # calculate:  map o autoR o map^-1 (Pnode)
            $mapF->{$Pnode} = $map->MapF->{ $autoR->MapF->{$inv_Pnode} };
        }
    }
     
    return $permP, '';
}




## Transform self-map according to map given as argument
##  --Jim
##
## NOTE: This routine is deprecated, use "get_induced_permutation" instead.

#sub mapAuto
## err = autoR->mapAuto(map)
##
## autoR: R -> R
## map:   R* -> P
##
## note: candP is constructed on top of autoR
#{
#  my $autoR = shift;
#  my $map = shift;
#  
#  if ($autoR->Source != $autoR->Target)
#  {  return "mapAuto called on non-self map";  }
#  
#  if ($autoR->Source != $map->Source)
#  {  return "mapAuto: Argument map not compatible with autoR" ;  }

#  # Change map Source and Target
#  $autoR->Source($map->Target);
#  $autoR->Target($map->Target);

#  # Set up identity map
#  # (this handles molecules in Target that are not in the Source)
#  my $candP = {};
#  my $molecules = $map->Target->Molecules;
#  foreach my $im ( 0..$#{$molecules} )
#  {
#     $candP->{$im} = "$im";
#     foreach my $ic ( 0..$#{$molecules->[$im]->Components} )
#     {  $candP->{"$im.$ic"} = "$im.$ic";  }  
#  }
#  
#  # Transform self-map
#  foreach my $src_element ( keys %{$autoR->MapF} )
#  {
#     my $perm_src_element = $autoR->MapF->{$src_element};
#     my $targ_element = $map->MapF->{$src_element};
#     my $perm_targ_element = $map->MapF->{$perm_src_element};
#     next if ( $targ_element < 0 );
#     $candP->{$targ_element} = $perm_targ_element;
#  }

#  # Replace MapF (NOTE: MapR is not changed! This is  because it should not be
#  # used by self-map, which is generated by calling subGraphIsomorphism))
#  $autoR->MapF( $candP );
#  return '';
#}


###
###
###


# transfer Labels and Attributes on source graph to the target graph
sub transferLabels
{
    my $map = shift;
    
    my $source = $map->Source;
    my $target = $map->Target;
 
    # transfer speciesGraph label and attributes
    $target->Label( $source->Label )         if (defined $source->Label);
    $target->MatchOnce( $source->MatchOnce ) if (defined $source->MatchOnce);
    $target->Fixed( $source->Fixed )         if (defined $source->Fixed);
    
    # loop over molecules
    for ( my $imol = 0;  $imol < @{$source->Molecules};  ++$imol )
    {
        my $mol = $source->Molecules->[$imol];
        # transfer molecule label
        if (defined $mol->Label)
        {
            my $imol_targ = $map->MapF->{$imol};
            $target->Molecules->[$imol_targ]->Label( $mol->Label );
        }
        
        # loop over components
        for ( my $icomp = 0; $icomp < @{$mol->Components};  ++$icomp )
        {
            my $comp = $mol->Components->[$icomp];
            # transfer component label
            if (defined $comp->Label)
            {
                my ($imol_targ, $icomp_targ) = split /\./, $map->MapF->{"$imol.$icomp"};
                $target->Molecules->[$imol_targ]->Components->[$icomp_targ]->Label( $comp->Label );  
            }
        }
    }
}



###
###
###


sub copy
{
    my $map = shift;
    
    my $map_copy = Map::new();
    $map_copy->Source( $map->Source );
    $map_copy->Target( $map->Target );
    %{ $map_copy->MapF } = %{ $map->MapF };
    %{ $map_copy->MapR } = %{ $map->MapR };

    return $map_copy;
}


###
###
###


sub copy_map_and_target
{
    my $map = shift;
    
    my $target_copy = $map->Target->copy();
    
    my $map_copy = Map::new();
    $map_copy->Source( $map->Source );
    $map_copy->Target( $target_copy );
    %{ $map_copy->MapF } = %{ $map->MapF };
    %{ $map_copy->MapR } = %{ $map->MapR };

    return $map_copy;
}


###
###
###


sub copy_map_and_source
{
    my $map = shift;
    
    my $source_copy = $map->Source->copy();
    
    my $map_copy = Map::new();
    $map_copy->Source( $source_copy );
    $map_copy->Target( $map->Target );
    %{ $map_copy->MapF } = %{ $map->MapF };
    %{ $map_copy->MapR } = %{ $map->MapR };

    return $map_copy;
}


###
###
###


sub copy_map_source_and_target
{
    my $map = shift;
    
    my $target_copy = $map->Target->copy();    
    my $source_copy = $map->Source->copy();
    
    my $map_copy = Map::new();
    $map_copy->Source( $source_copy );
    $map_copy->Target( $target_copy );
    %{ $map_copy->MapF } = %{ $map->MapF };
    %{ $map_copy->MapR } = %{ $map->MapR };

    return $map_copy;
}


###
###
###


1;
