# $Id: ComponentType.pm,v 1.3 2006/05/01 03:32:43 faeder Exp $

# Objects for typing and checking Components of Molecules
package ComponentType;

use Class::Struct;
use FindBin;
use lib $FindBin::Bin;

struct ComponentType=> {
  Name=> '$',
  States=> '@', # First element of States array is the default state, which 
                # is used in creating new molecules that don't have user-specified
                # state attributes.
  Edges=> '@',         # Last two are not currently used in checking
  Compartments=> '@'
};
sub readString{
  my $ctype= shift;
  my $strptr= shift;

  my $string_left=$$strptr;
  
  # Get component name (cannot start with a number)
  if ( $string_left =~ s/^([A-Za-z_]\w*)// )
  {
  	$ctype->Name($1);
  }
  else
  {
	return undef, "Invalid component name in '$string_left' (must begin with a letter or underscore).";
  }
	
  # Get component state (marked by ~) edges (marked by !)
  my @states=();
  my @compartments=();
  my @elabels=();
  while ($string_left){
    if ($string_left=~ s/^([~!@])([A-Za-z0-9_]+)//){
      my $type=$1;
      my $arg=$2;
      if ($type eq '~'){
	push @states, $arg;
      }
      elsif ($type eq '!'){
	push @elabels, $arg;
      }
      elsif ($type eq '@'){
	push @compartments, $arg;
      }
    }
    elsif ($string_left=~ /^[,)]/){ # Terminator characters for component
      last;
    }
    else {
      return("Invalid syntax at $string_left");
    }
  }

  if (@states){
    $ctype->States([@states]);
  }
  if (@elabels){
    $ctype->Edges([@elabels]);
  }
  if (@compartments){
    $ctype->Compartments([@compartments]);
  }
  
  $$strptr= $string_left;
  return("");
}


sub copy
{
    my $comp = shift;
    
    my $comp_copy = ComponentType::new();    
    $comp_copy->Name( $comp->Name );
    $comp_copy->States( [@{$comp->States}] );
    $comp_copy->Edges( [@{$comp->Edges}] );
    $comp_copy->Compartments( [@{$comp->Compartments}] );            
    
    return $comp_copy;   
}

sub toString{
  my $ctype= shift;
  my $string="";

  $string.= $ctype->Name;
  for my $state (@{$ctype->States}){
    $string.= "~$state";
  }
  return($string);
}

sub toStringSSC{
  my $ctype= shift;
  my $string="";
  $string.= $ctype->Name;
  $string .= "#";
  for my $state (@{$ctype->States}){
    $string.= "=\"$state\"";
  }
  return($string);
}

sub toXML{
  my $ctype= shift;
  my $indent= shift;
  my $string=$indent."<ComponentType";

  # Attributes
  # id
  $string.=" id=\"".$ctype->Name."\"";

  # Objects
  my $indent2= "  ".$indent;
  my $ostring="";
  if (@{$ctype->States}){
    $ostring.= $indent2."<ListOfAllowedStates>\n";
    for my $state (@{$ctype->States}){
      $ostring.=$indent2."  <AllowedState";
      $ostring.=" id=\"".$state."\"";
      $ostring.="/>\n";
    }
    $ostring.= $indent2."</ListOfAllowedStates>\n";
  }

  if ($ostring){
    $string.=">\n";
    $string.=$ostring;
    $string.=$indent."</ComponentType>\n";
  } else {
    $string.="/>\n";
  }

  return($string);
}

sub writeMDL
{
    my $ctype = shift; 
    my $string = $ctype->Name; 
    
    if (@{$ctype->States}){
       foreach my $state (@{$ctype->States}){
              $string .= "~".$state; 
	      }
     }
    return $string; 
}


1;
