package Species;

# pragmas
use strict;
use warnings;

# Perl Modules
use Class::Struct;
use FindBin;
use lib $FindBin::Bin;

# BNG Modules
use SpeciesGraph;



struct Species =>
{
    SpeciesGraph        => 'SpeciesGraph',
    Concentration       => '$',              # this will either be a number or a parameter name (I think)
    Index               => '$',
    RulesApplied        => '$',
    ObservablesApplied  => '$',
};



###
###
###


sub toXML
{
    my $spec = shift @_;
    my $indent = @_ ? shift @_ : "";
    my $conc   = @_ ? shift @_ : $spec->Concentration;
    my $id = @_ ? shift : "S".$spec->Index;  # what's this for??

    my $type = 'Species';
    my $attributes = '';

    # Attributes
    # concentration
    $attributes .= " concentration=\"" . $conc . "\"";
    # name
    $attributes .= " name=\"" . $spec->SpeciesGraph->toString() . "\"";

    # Objects contained
    my $string = $spec->SpeciesGraph->toXML($indent,$type,$id,$attributes);

    return $string;
}


###
###
###
###
###
###

################DB#########################
sub writeMDL
{
    my $spec = shift @_;
    my $difexp = @_ ? shift @_ : {}; 
    my $py_species = @_ ? shift @_ : [];
    my $indent = "   "; 
    my $py_string = "";
    my $string = "s".$spec->Index." /* ".$spec->SpeciesGraph->toString()." */"; 
    my $comp = $spec->SpeciesGraph->Compartment ? $spec->SpeciesGraph->Compartment : undef; 
    my ($difconst, $cdimension) = (defined $comp) ? ($difexp->{$comp}, $comp->SpatialDimensions)  : ($difexp->{"DEFAULT"}, 3);
    
    if (defined $comp && $comp->SpatialDimensions == 2){
       my $count = (scalar @{$spec->SpeciesGraph->Molecules}) - ($spec->SpeciesGraph->toString() =~ tr/@//) + 1;        # Number of membrane molecules in species 
       my $radius = ($count == 1) ? "Rsp" : "SQRT(".$count.")*Rsp"; 
       $difconst =~ s/Rsp/$radius/g; 
       $difconst =~ s/Rsp/Rc/g; 
       }
    else{
       my $radius = (scalar @{$spec->SpeciesGraph->Molecules} == 1) ? "Rsp" : "(".scalar @{$spec->SpeciesGraph->Molecules}.")^(1/3)*Rsp"; 
       $difconst =~ s/Rsp/$radius/g; 
       $difconst =~ s/Rsp/Rs/g;
       }
    
    $string .= "\n"; 
    $string .= $indent."{\n";
    $string .= $indent.$indent.sprintf("DIFFUSION_CONSTANT_%dD = %s\n", $cdimension, $difconst);
    $string .= $indent."}\n";   
    
    $py_string = "{";
    #$py_string .= "'name':"."'s".$spec->Index." /* ".$spec->SpeciesGraph->toString()."* /',";
    $py_string .= "\"name\":"."\"s".$spec->Index."\",";
    $py_string .= "\"type\":"."\"".sprintf("%dD",$cdimension)."\",";
    $py_string .= "\"dif\":"."\"".sprintf("%s", $difconst)."\"," ;
    $py_string .= "\"extendedName\":"."\"".sprintf("%s", $spec->SpeciesGraph->toString())."\"";
    $py_string .= "}";
    
    push(@{$py_species},$py_string);
    
    return $string; 
}

################DB#########################
###
###
###

###############DB##########################
sub getMDLRelSite
{
    my $sp = shift; 
    my $object = shift; 
    my $shape = shift; 
    my $custom_geometry = shift; 

    my $comp; 
    my $site = []; 
    $site = ($comp = $sp->SpeciesGraph->Compartment) ? $comp->getMDLRelSite() : undef;

    my $string; 
    my $i = 1; 
    if (defined $site){
       foreach (@{$site}){
          if ($custom_geometry){
              if ($comp->SpatialDimensions == 2){
	         $string .= "Scene.".$shape->{$_}; 
	         }
	      elsif (($comp->SpatialDimensions == 3)&&(!@{$comp->Inside})){
		 $string .= "Scene.".$object->{$_};
		 }
	      else{
	         $string .= $i ? "Scene.".$shape->{$object->{$_}} : " - Scene.".$shape->{$object->{$_}};
	         }
	      }
	  else{
	      $string .= $i ? (@{$comp->Inside} ? "Scene.".$shape->{$_}: "Scene.".$object->{$_}) : " - Scene.".$shape->{$_};
	      }
	  $i = 0; 
	  }
       }
    else{
          $string = "Scene."."default_Sphere";
	}
	
    return $string; 	
       
}

###
###
###

sub getCVodeName
{
    my $species = shift;
    my $offset = -1;
    return 'NV_Ith_S(species,' . ($species->Index + $offset). ')';
}

sub getCVodeDerivName
{
    my $species = shift;
    my $offset = -1;
    return 'NV_Ith_S(Dspecies,' . ($species->Index + $offset). ')';
}


###
###
###


sub getMatlabName
{
    my $species = shift;
    my $offset  = 0;
    return 'species(' . ($species->Index + $offset). ')';
}

sub getMatlabDerivName
{
    my $species = shift;
    my $offset  = 0;
    return 'Dspecies(' . ($species->Index + $offset). ')';
}


###
###
###

1;
