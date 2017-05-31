# $Id: Compartment.pm,v 1.1 2006/09/25 04:50:31 faeder Exp $

package Compartment;

# pragmas
use strict;
use warnings;

# Perl Modules
use Class::Struct;
use FindBin;
use lib $FindBin::Bin;

# BNG Modules
use Expression;



struct Compartment => {
  Name => '$',               # name of compartment (valid chars: \w).
  SpatialDimensions => '$',  # dimension of compartment: 0-3.
  Size => 'Expression',      # volume/area/length expression.
  Outside => 'Compartment',  # ref to compartment (if any) that contains this.
  Inside => '@',             # list of compartments contained by Compartment. [JSH]
};


# get copy of compartment
# (careful here.. the Outside and Inside continue pointing to the same old place.
#    If copying an entire compartment list, these references need to be updated after copy!)
sub copy
{
    my $comp = shift;
    my $plist = (@_) ? shift : undef;
    
    my $comp_copy = Compartment::new();
    $comp_copy->Name( $comp->Name );
    $comp_copy->SpatialDimensions( $comp->SpatialDimensions );
    
    $comp_copy->Size( $comp->Size->clone($plist) ) if ( defined $comp->Size );
    $comp_copy->Outside( $comp->Outside )         if ( defined $comp->Outside );
    
    @{ $comp_copy->Inside } = @{ $comp->Inside };

    return $comp_copy;
}



###
###
###



# after copying a compartment list, we need to relink the inside and outside neighbors
#    the arguments should be the copied compartment and list
sub relinkNeighbors
{
    my $comp = shift;
    my $clist = shift;
    
    # relink outside
    $comp->Outside = $clist->lookup( $comp->Outside->Name );
    # relink inside
    foreach my $inside_comp ( @{$comp->Inside} )
    {
        $inside_comp = $clist->lookup( $inside_comp->Name );
    }
}



###
###
###


sub newCompartment
# ($comp_ref, $err) = Compartment->newCompartment(name,dim,size[,outside]);
# creates a new compartment object
# sub added by justinshogg@gmail.com 18feb09
{
   my ($class, $name, $dim, $size, $outside) = @_;

   $name =~ /^[\w]+$/  or  return( undef, "newCompartment: attempt to define compartment with invalid name: '$name'." );
   $dim =~ /^[2-3]$/   or  return( undef, "newCompartment: spatialDimension '$dim' must be integer in [2,3]." );
   ref $size eq 'Expression'  or  return( undef, "newCompartment: size parameter '$size' must be an expression." );

   my $err = '';  
   my $comp = Compartment->new(Name=>$name,SpatialDimensions=>$dim,Size=>$size);
   $err = $comp->put_inside($outside) if ( defined $outside );

   return($comp, $err);
}


###
###
###


sub put_inside
# $err = $comp->put_inside( $outside ); 
# set $comp1->Outside = $comp;
{
   my ($comp, $outside) = @_;

   # check that $outside is a compartment!
   ref $outside eq 'Compartment'
       ||  return("Compartment->put_inside > Outside parameter '$outside' must be a Compartment.");

   !defined($comp->Outside)
       ||  return("Compartment->put_inside: compartment is already contained by another compartment.");

   # enforce topology.  (1) alternating dimensions:  ..->volume->surface->volume->surface->..
   abs( $comp->SpatialDimensions - $outside->SpatialDimensions ) == 1
       ||  return("Compartment->put_inside: Outside has same dimension as compartment.");

   # enforce topology.  (2) only one volume inside a surface
   if ($comp->SpatialDimensions == 3  &&  $outside->SpatialDimensions == 2)
   {
     (@{$outside->Inside} == 0)
         ||  return("Compartment->put_inside: Outside 2-D compartment already contains a 3-D compartment.");
   }

   # all good, do the operation.
   $comp->Outside($outside);
   push @{$outside->Inside}, $comp;
   return '';
}




sub adjacent
# int = $comp1->adjacent( $comp2 )
# return +1 if $comp1 is Outside of Comp2
# return -1 if $comp2 is Outside of Comp1
# return 0 if compartments are not adjacent
{
    my $comp1 = shift;
    my $comp2 = shift;

    # Compartment 1 contains Compartment 2
    if ( defined $comp2->Outside )
    {
        if ( $comp2->Outside == $comp1 )
        {  return 1;  }
    }
    # Compartment 2 contains Compartment 1
    if ( defined $comp1->Outside )
    {
        if ( $comp1->Outside == $comp2 )
        {  return -1;  }
    }
    return 0;
}




sub is_surface_of
# int = $comp1->is_surface_of($comp2)
# return 1 if $comp1 is a surface facing volume $comp2
# return -1 if $comp2 is a surface facing volume $comp1
# else return 0.
# justinshogg@gmail.com 16feb2009
{
  my ($comp1,$comp2) = @_;
  my $adj = $comp1->adjacent($comp2);
  if ($adj)
  {  return($comp2->SpatialDimensions - $comp1->SpatialDimensions);  }
  else
  {  return 0;  }
}



sub separated_by_surface
# int = Compartment1->separated_by_surface(Compartment2) 
# return 1 if Compartment1 and Compartment2 are volumes
#   separated by a surface and Comp1 is outer-most compartment.
# return -1 if Compartment1 and Compartment2 are volumes
#   separated by a surface and Comp2 is outer-most compartment.
# return 0 otherwise.
#
# Note: assumes alternating tree topology
{
  my ($comp1, $comp2) = @_;

  # comp1 and comp2 must be volumes
  return 0 unless ( ($comp1->SpatialDimensions==3)  and  ($comp2->SpatialDimensions==3) );

  # check if comp1 and comp2 are separated by a membrane
  # and comp1 is outer-most:
  return -1  if (  defined $comp1->Outside
                    and  defined $comp1->Outside->Outside 
                    and  ($comp1->Outside->Outside == $comp2)       );

  # and comp2 is outer-most:
  return  1  if (  defined $comp2->Outside
                    and  defined $comp2->Outside->Outside 
                    and  ($comp2->Outside->Outside == $comp1)       );

  return  0;
}


sub separated_by_volume
# int = Compartment1->separated_by_surface(Compartment2) 
# return 1 if Compartment1 and Compartment2 are surfaces
#   separated by a volume and the volume does not contain both surfaces.
# return -1 if Compartment1 and Compartment2 are surfaces
#   separated by a volume and the volume contains both surfaces.
# return 0 otherwise.
#
# NOTE: assumes alternating tree topology
# for endocytosis: Both the Outside volume of the outermost surface
#                    and Inside volume of the innermost surface MUST EXIST.
# for exocytosis: Both of the Surfaces must have an Inside volume defined.
{
  my ($comp1, $comp2) = @_;

  # comp1 and comp2 must be surfaces
  return 0 unless ( ($comp1->SpatialDimensions==2)  and  ($comp2->SpatialDimensions==2) );

  # exocytosis
  # check if comp1 and comp2 have the same Outside (and both contain some volume)
  return -1  if (  defined $comp1->Outside
                    and  defined $comp2->Outside
                    and  ($comp1->Outside == $comp2->Outside) 
                    and  exists $comp1->Inside->[0]
                    and  exists $comp2->Inside->[0] 
                );

  # endocytosis
  # Check if comp1 Outside == comp2 Inside
  return  1  if (  defined $comp1->Outside
                    and  defined $comp1->Outside->Outside 
                    and  ($comp1->Outside->Outside == $comp2)
                    and  exists $comp1->Inside->[0]
                    and  defined $comp2->Inside
                );

  # endocytosis
  # Check if comp2 Outside == comp1 Inside
  return  1  if (  defined $comp2->Outside
                    and  defined $comp2->Outside->Outside 
                    and  ($comp2->Outside->Outside == $comp1)
                    and  exists $comp2->Inside->[0]
                    and  defined $comp1->Outside
                );

  return  0;
}




sub connected {
# $bool = connected( $comp1, $comp2, .. $comp3 );
# check if a list of compartments are connected
  my @nodes_conn= (shift);
  my @nodes_left= @_;

  my $link_found;
  while(@nodes_left){
    $link_found=0;
    for my $n1 (@nodes_conn){
      for my $i (0..$#nodes_left){
	     if ($n1->adjacent($nodes_left[$i])){
	       push @nodes_conn, $nodes_left[$i];
	       splice @nodes_left, $i, 1;
	       $link_found=1;
	       last;
	     }
      }
      last if $link_found;
    }
    last unless $link_found;
  }
  # Compartment graph is not connected unless link was found on last loop
  return($link_found)
}




sub toString{
  my $comp=shift;
  my $plist=(@_) ? shift : "";

  my $string="";

  $string.=$comp->Name;
  $string.=" ".$comp->SpatialDimensions;
  if ($plist){
    $string.=" ".$comp->Size->toString($plist);
  }
  if (my $ccomp=$comp->Outside){
    $string.=" ".$ccomp->Name;
  }
  return($string);
}




sub toXML{
  my $comp=shift;
  my $indent=shift;
  my $plist = (@_) ? shift : undef;

  my $string=$indent."<compartment";

  # Attributes
  # id
  $string.=" id=\"".$comp->Name."\"";
  # spatialDimensions
  $string.= " spatialDimensions=\"".$comp->SpatialDimensions."\"";
  # size
  my $size = $comp->Size->toString();
  unless ( BNGUtils::isReal($size) )
  {   $size = $comp->Size->evaluate($plist);   } # evaluate to a number
#  $string.= " size=\"".$comp->Size->toString()."\"";
  $string.= " size=\"".$size."\"";
  # outside
  if ($comp->Outside){
    $string.= " outside=\"".$comp->Outside->Name."\"";
  }

  $string.="/>\n"; # short tag termination

  return($string);
}

############DB###################
# This function calculates scaling factor for the compartment volumes/areas of compartments based on the user-specified volumes 
#provided in the BNGL file. The scaling is determined by the ration of the user-specified volume and the default volume of a 
# M-cell produced geometry, which in this case is a sphere. The default size of a MCell/Blender-created is r = 1 micron (volume
# = 4.19 cubic micron, and surface area is 12.57 sq. micron. The function first determines size of a sphere based on the sum of 
# all nested compartment volumes (by calling the getMDLSize function). It then determines the linear scaling factor, which is the 
# ratio from the volume of the sphere and the volume of the MCell/Blender-created default sphere. In addition, the function
# stores the first word from the first line into a hash called %shape. The first word referes to the name of the geometry object itself (in this 
# case it is "Sphere". 

 
sub getMDLgeometry
{
   my $comp = shift; 
   my $plist = shift; 
   my $geometry = shift; 
   my $object = shift; 
   my $shape = shift; 
   my $scale = shift; 
   my $string = ""; 
   
    my $surf; 
   my $i=0; 
   foreach (@{$geometry}){
        if ($_ =~ /POLYGON_LIST/){
	    $object->{$comp->Name} = $_;
	    $object->{$comp->Name} =~ s/POLYGON_LIST//; 
	    $object->{$comp->Name} =~ s/^\s*//; 
	    $object->{$comp->Name} =~ s/\s*$//;
	    $object->{$comp->Name} = $comp->Name."_".$object->{$comp->Name}; 
	    }
	    
	if ($_ =~ /DEFINE_SURFACE_REGIONS/){
	    $surf = $geometry->[$i+2];
	    $surf =~ s/^\s*//;
	    $surf =~ s/\s*$//;
	    }
	++$i; 
   }

   my $volume_mcell;   
   my $area_mcell; 
   my $volume; 
   my $area; 
   
   if ($object->{$comp->Name} =~ /Sphere/){
      $volume_mcell = 4.1904762;  # Default volume of sphere in Blender;
      $area_mcell = 12.571429;  # Default surface area of sphere in Blender;
      $volume = getMDLSize($comp, $plist);  # Volume of sphere inclosing the compartment and nested inside compartments
      $area = 4.836624601*($volume)**(2/3);  # Surface area of sphere
      }
      
   
   if ((!$comp->Outside) || ($comp->SpatialDimensions == 2)){ # Write geometries defined by 2D surfaces
      $string = join "", map {$_=~ /POLYGON_LIST/ ? $comp->Name."_".$_ : $_ } (@{$geometry}); 
      $shape->{$comp->Name} .= $object->{$comp->Name}."[$surf]";
      my ($volume, $area) = ($comp->SpatialDimensions == 3) ? ($volume, undef) : (undef, $area);  
      # $volume is relevant only in the case of the outermost space if it has no defined boundary (in BNGL). 
      $scale->{$comp->Name} = (defined $volume) ? ($volume/$volume_mcell)**(1/3) : sqrt($area/$area_mcell); 
      }
     
   if (@{$comp->Inside}){
        foreach my $incomp (@{$comp->Inside}){
            $string .= getMDLgeometry($incomp,$plist,$geometry,$object,$shape,$scale);  # Recursive call 
	}  
   }
   return $string; 
}

# This function returns a reference to an array. The first array element is a compartment name. Subsequent elements are the names of nested compartments. 
sub getMDLRelSite
{
   my $comp = shift; 
   my @array;
   if ($comp->SpatialDimensions == 2){
       push(@array, $comp->Name);    # The array contains a single element because membrane compartment does not hold any nested compartments. 
       return \@array;
       }
   
   push (@array, $comp->Outside ? $comp->Outside->Name : $comp->Name); # If outermost 3D space, then name the 'hypothetical' outer membrane with  the same name as the compartment 
   foreach (@{$comp->Inside}){
       push (@array, $_->Name);   # First element is the primary compartment. Subsequent elements are nested compartments. 
       }
   return \@array;     
} 
 
# This function calculates total volume of a sphere (i.e., volume of (compartment + all inside compartments).
# Membrane volume is assumed zero regardless of the user-assigned value
# because potential contradiction can arise from independently assigning both the membrane and enclosed volume size
# Size of a sphere is only based on the assigned 3D volumes.
 
sub getMDLSize
{
  my $comp = shift; 
  my $plist = shift; 
  my $custom_geometry = shift; 
  my $volume = 0;
  my $childvolume = 0; 
  
  $volume = $comp->Size->evaluate($plist);
  return $volume if ($custom_geometry);
  
  $volume =  0 if ($comp->SpatialDimensions == 2);  
  
  foreach my $child (@{$comp->Inside}){    # Calculate volume of all nested compartments inside the current compartment 
          $childvolume = $childvolume + $child->getMDLSize($plist, $custom_geometry); 
	  }
  $volume = $volume + $childvolume; # Sphere volume = volume of (current compartment + all nested compartments inside)
  return ($volume); 	    
}   

1;
