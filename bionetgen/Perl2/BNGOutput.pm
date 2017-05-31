package BNGModel;
# BNGOutput is part of the BNGModel package. This file contains Output commands
# including:  writeXML, writeSBML, writeSSC, writeMfile, writeMexfile, ...


# pragmas
use strict;
use warnings;


###
###
###


# write Model in XML format
# $err = $model->writeXML({opt=>val,..}) 
sub writeXML
{
    use strict;
    use warnings;

    my $model       = shift @_;
	my $user_params = @_ ? shift @_ : {};

	my %params = (
        'evaluate_expressions' => 1,
		'format'               => 'xml',
        'include_model'        => 1,
        'include_network'      => 0,
        'overwrite'            => 1,
	);

    # copy user_params into pass_params structures
	while ( my ($key,$val) = each %$user_params )
	{   $params{$key} = $val;	}

    # writeFile will generate the output
    return $model->writeFile( \%params );
}

# code for writeMDL starts here
sub writeMDL
{
	my $model = shift @_;
    my $params = @_ ? shift @_ : {};

 	printf "ACTION: writeMDL( %s )\n", $model->Name;

    # a place to hold errors
    my $err;

    # nothing to do if NO_EXEC is true
	return '' if $BNGModel::NO_EXEC;

    # nothing to do if there are no reactions
	if ( @{$model->RxnList->Array} == 0 )
	{
	    send_warning( "Reaction network not found. Attempting to generate network now.");
	    $model->generate_network({overwrite=>1});	
	    #return ( "writeMDL() has nothing to do: no reactions in current model.\n"
	    #        ."  Did you remember to call generate_network() before attempting to\n"
	    #        ."  write network output?");
	}
	
    # get reference to parameter list
	my $plist = $model->ParamList;

	# get model name
	my $model_name = $model->Name;

    # Build output file name
	# ..use prefix if defined, otherwise use model name
	my $prefix = defined $params->{prefix} ? $model->getOutputPrefix( $params->{prefix} ) : $model->getOutputPrefix();
	# ..add suffix, if any
	my $suffix = ( defined $params->{suffix} ) ? $params->{suffix} : undef;
	if ( $suffix )
	{   $prefix .= "_${suffix}";   }
		
    # split prefix into volume, path and filebase
    my ($vol, $path, $filebase) = File::Spec->splitpath($prefix);

	# define mdl-script file name
	my $mdlscript_filedir = $path; #File::Spec->catpath($vol, $path); 
    my $mdlscript_filebase = "${filebase}";
    my $mdlscript_filename = "${mdlscript_filebase}.mdl";
    my $MDL; 

   	# create output directory where MDL files will be dumped 
	my $mdlscript_dumpdir = File::Spec->catdir($mdlscript_filedir,'MDL');
	unless(-e $mdlscript_dumpdir or mkdir $mdlscript_dumpdir) {
		die "Unable to create $mdlscript_dumpdir\n";
	}

   	# open file to write MDL script
	my $mdlscript_path = File::Spec->catfile($mdlscript_dumpdir,$mdlscript_filename);
	open( $MDL, '>', $mdlscript_path )  or die "Couldn't open $mdlscript_path: $!\n";
	
   	# get BNG version
	my $version = BNGversion(); 
	use File::Basename; 
    my $bngpath = dirname(dirname(__FILE__));
	my $custom_geometry = 0; 
	my $bnglfiledir = dirname($model->Params->{'file'});

   	# look for custom geometry file in the directory where bngl file is located  
 	my $custom_geometry_file = File::Spec->catfile($bnglfiledir,$mdlscript_filebase.".geometry.mdl"); 
	if  ( -e  $custom_geometry_file){
		$custom_geometry = 1; 
	}
   	# look for default geometry file in the directory where bngl file is located 
	my $default_geometry_file = File::Spec->catfile($bnglfiledir,"default.geometry.mdl");
      
	# Read template geometry (Sphere with radius 1 micron)
	my $iscomp = @{$model->CompartmentList->Array} ? 1 : 0; 
	my $text = "";
	my %object; 
	my %shape;
	my %scale; 
	my @geometry; 
	
	if (!$iscomp){
    	open (READSHAPE, "<", $default_geometry_file) || die "Error loading default geometry: $default_geometry_file is missing or corrupted"; 
        @geometry = <READSHAPE>; 
        close(READSHAPE); 

        $text = join "", map {$_=~ /POLYGON_LIST/ ? $shape{"default"} = "default_".$_ : $_} @geometry; 
	    $shape{"default"} =~ s/POLYGON_LIST//; $shape{"default"} =~ s/^\s+//; $shape{"default"} =~ s/\s+$//;
	    $object{"default"} = $shape{"default"};
	    $shape{"default"} .= "[surface]"; 
	    $scale{"default"} = (1000/4.19048)**(1/3); 	 
    }	
	    
  	if ($iscomp && !$custom_geometry){
    	open (READSHAPE, "<", $default_geometry_file) || die "Error loading default geometry: $default_geometry_file is missing or corrupted"; 
        @geometry = <READSHAPE>; 
        close(READSHAPE); 

 	    foreach my $comp (@{$model->CompartmentList->Array}){
	        if (!$comp->Outside){   # If the outermost compartment is found, start from here. 
		    	$text .= $comp->getMDLgeometry($plist,\@geometry,\%object,\%shape,\%scale);
		    	last; 
		    }
	    }
    }   

    my %surf; #Each key of this hash represents a surface element in the geometry file. Corresponding value represents the name of the object/geometry to which the surface element belongs
    my $overwrite_custom_geometry = 1; 
    if ($iscomp && $custom_geometry){    # This bloc will be excecuted if only a custom geometry is provided 
    	open (READ_CUSTOM_GEOMETRY, "<", $custom_geometry_file) || die "Error loading custom geometry: $custom_geometry_file is missing or corrupted "; 
        my @custom_geometry = <READ_CUSTOM_GEOMETRY>; 
        close(READ_CUSTOM_GEOMETRY); 
	
	    my $lnum; 
	    my $curly = 0; 
	    my $brack = 0;
	    my $elemn  = 0; 
	    my $i = 0;
	    my $j = 0; 
	    my $next_curly = 0;  
	    my %obj;  # A key of this hash repersents an object/geometry in the geometry file. Each value of this hash is a hash, whose keys are all surface element names belonging to the object
	    my $objname; 
	    my %complist = map {$_->Name=>$_->Name} @{$model->CompartmentList->Array}; 
	     
	    %shape = ();  
	    %object = (); 
	    %scale = (); 
        my %dummy_shape = ();  
	    foreach my $line (@custom_geometry){
        	if ($line =~ /obj_wall/){
            	$overwrite_custom_geometry = 0;
            }
	        $text .= $line; 
	        ++$curly if ($line =~ /{/); 
	        --$curly if ($line =~ /}/); 
	        ++$lnum;
		 
	        if ($line =~/POLYGON_LIST/){  # A new geometric object found 
	        	$line =~ s/POLYGON_LIST//; 
	            $line =~ s/^\s*//; # Remove preceding white spaces 
		     	$line =~ s/\s*$//; # Remove traling white spaces
		     	die "Unknown compartment detected in custom geometry file $custom_geometry_file at line $lnum" unless exists $complist{$line}; 
		
		     	$objname = $line;
		     	$obj{$objname} = {};  
	        }
		 
		  	$i = 1 if ($line =~ /ELEMENT_CONNECTIONS/);
		  	if ($i){
            	++$brack if ($line =~ /{/);
		     	--$brack if ($line =~ /}/);
		     	++$elemn if ($line =~ /\[/); 
		     	$i = 0 if (!$brack && $elemn > 0); 
		    }
		     
		  	$j=1 if ($line =~ /DEFINE_SURFACE_REGIONS/); 
		  
		  	if (($j == 1)&&($line =~/{/)){
		    	$j=0; 
		     	my $buf = $line;
		     	$buf =~ s/\s*$//;
		     	$buf =~ s/{//;
		     	$buf .= "  ";   
		     	$text .= $buf."obj_wall";
		     	$text .= "\n".$buf."{";
		     	$text .= "\n".$buf."  ELEMENT_LIST = ["; 
		     	for (my $k=0; $k<$elemn; $k++){
		        	$text .= ($k < $elemn-1) ? $k.", " : $k."]\n";
		        }
		     	$text .= $buf."}\n"; 
		     	$elemn = 0; 

                $shape{$objname} = $objname."[obj_wall]" unless exists $dummy_shape{$objname."[obj_wall]"};
                $dummy_shape{$objname."[obj_wall]"} = 1; 
		     	$object{$objname} = $objname;
		     	$scale{$objname} = 1; 
		    }          

	        if (($curly > 0)&&($line =~/ELEMENT_LIST/)){
	        	$line = $custom_geometry[$lnum-3];
		     	$line =~ s/^\s*//;
		     	$line =~ s/\s*$//;
		     	$surf{$line} = $objname; 
		     	$obj{$objname}->{$line} = 1; 
		     	$shape{$line} = $objname."[$line]" unless $line=~/obj_wall/; 
                $dummy_shape{$line} = 1; 
            } 
		}
	
        # Send error if any 2D compartment is not found as a surface region in the geometry file  
		foreach (@{$model->CompartmentList->Array}){
	    	if ($_->SpatialDimensions == 2){
	        	my $cname = $_->Name;  
                die "Compartment $cname is not specified as any surface region in the geometry file" unless (exists $surf{$cname}); 
	        }
	    } 
	}

    if ($overwrite_custom_geometry){
    	my $gfile = File::Spec->catfile($mdlscript_dumpdir,$mdlscript_filebase.".geometry.mdl");
        open (WRITEGEOMETRY, '>',$gfile) || die "Could not open $gfile: $!"; 
        print WRITEGEOMETRY $text; 
        close WRITEGEOMETRY;
        print "Wrote MCell geometry file to $gfile.\n"; 	
	}    
	
    my $mdl;
	my $indent = "   "; 
	
    # Define default iteration size and time step for mdl
	$mdl  = "ITERATIONS = 100000\n"."TIME_STEP = 5e-06\n"."VACANCY_SEARCH_DISTANCE = 100\n\n";  
	$mdl .= "INCLUDE_FILE = \"${mdlscript_filebase}.geometry.mdl\"\n\n"; # Produced as a separate text file in the current directory
    $mdl .= "DEFINE_SURFACE_CLASSES\n{\n".$indent."reflect {REFLECTIVE = ALL_MOLECULES}\n}\n\n"; 
	$mdl .= "MODIFY_SURFACE_REGIONS\n{\n";  
    for my $key (keys %shape){
		$mdl .= $indent.$shape{$key}."\n"; 
	    $mdl .= $indent."{\n";
	    $mdl .= $indent.$indent."SURFACE_CLASS = reflect\n";
	    $mdl .= $indent."}\n";
	}
	$mdl .= "}\n\n";
			
	# Define parameter block
	my @py_param; 
    my $tpy_param = {}; 
	my @tpy_keys = ('name', 'value', 'unit', 'type'); 
	$mdl .= "\n/* Model Parameters */\n"; 
	$mdl .= $indent."Nav = 6.022e8               /* Avogadro number based on a volume size of 1 cubic um */\n" unless (exists $plist->{"Nav"}); 
	$tpy_param = {"name"=>"Nav", "value"=>"6.022e8", "unit"=>"", "type"=>"Avogadro number for 1 um^3"};   
	#print "{"."'name':"."Nav,"."'value':"."6.022e8,"."'type':"."\"\","."'unit':"."Avogadro number for 1 um^3"."}";
        
	push(@py_param,"{".join(",",map("\"$_\"".":\"".$tpy_param->{$_}."\"", @tpy_keys))."}"); 
	
    my $rxn_layer_t = 0.01; 
	if ($iscomp){
		$mdl .= $indent."rxn_layer_t = $rxn_layer_t\n";
	    $tpy_param={'name'=>"rxn_layer_t",'value'=>"0.01",'unit'=>"um",'type'=>""}; 
	    push(@py_param,"{".join(",",map("\"$_\"".":\"".$tpy_param->{$_}."\"", @tpy_keys))."}"); 
	}

	# Scale parameters for MDL units
	my %tcomp = (); 
	if ($iscomp){
		foreach (@{$model->CompartmentList->Array}){
	    	my $cname = $_->Size->toString();
	    	my $csize = $_->Size->evaluate($plist);
			my $ssize = $_->getMDLSize($plist, $custom_geometry); # Volume of corresponding sphere 
			my $ssurf = ($_->SpatialDimensions == 2) ? ($custom_geometry? $ssize."/rxn_layer_t" : 4.836624601*($ssize)**(2/3)) : undef; # Surface area of corresponding sphere
	    	# 2D compartments as the surface area of the enclosing sphere
	    	$tcomp{$cname} = (defined $ssurf) ? $ssurf : $csize; 
	    	$mdl .= $indent.$cname." = ".$tcomp{$cname}.($ssurf ? "  /*Surface area*/" : "")."\n"; 
	    	$tpy_param = {'name'=>$cname,'value'=>$tcomp{$cname},'unit'=>$ssurf ? "um^2": "um^3",'type'=>""}; 
	    	push(@py_param,"{".join(",",map("\"$_\"".":\"".$tpy_param->{$_}."\"", @tpy_keys))."}"); 
	    }
	}
	
	my %tparam = (); 
	my %rpy_param = (); 
	foreach my $rxn (@{$model->RxnList->Array})
	{
		# Check if rate law type is elementary
	    if ($rxn->RateLaw->Type =~ "Ele"){
	    	my $rconst = $rxn->RateLaw->Constants->[0];
			# If rxn parameter unit has already been converted, no need for further calculation
			if (exists $tparam{$rconst}) { next; } 
			# Find rate const value 
			my $rconst_val = $plist->evaluate($rconst); 
			# Store in new hash without converting, i.e., assuming reaction is unimolecular 
			$tparam{$rconst} = $rconst_val; 
			$tpy_param->{'name'} = $rconst; 
			$tpy_param->{'type'} = "Unimolecular reaction"; 
			$tpy_param->{'unit'} = "/s"; 
			# If reaction is bimolecular, overwrite hash value with the converted parameter value
	        if (@{$rxn->Reactants}==2){ 
		    	$tpy_param->{'type'} = "Bimolecular reaction"; 
		      	$tpy_param->{'unit'} = "/M.s"; 
		      	# Assume non-compartmental model. Apply default scaling in unit conversion (default volume 10^3 cubic micron)
		      	my $scale = "1000*Nav"; 
		      	$tparam{$rconst} = $rconst_val."*".$scale; 
		      	# If model is compartmental, re-calculate the conversion
		      	if ($iscomp){		      
		        	# Volume and dimension for compartment of the first reactant species
		            my ($vol1, $dim1, $ssize1) = ($_= $rxn->Reactants->[0]->SpeciesGraph->Compartment)? ($_->Size->toString(), $_->SpatialDimensions, $_->getMDLSize($plist, $custom_geometry)):(undef, undef, undef); 
			    	# Volume and dimension for compartment of the second reactant species 
		            my ($vol2, $dim2, $ssize2) = ($_= $rxn->Reactants->[1]->SpeciesGraph->Compartment)? ($_->Size->toString(), $_->SpatialDimensions, $_->getMDLSize($plist, $custom_geometry)):(undef, undef, undef); 

			    	if ((defined $dim1) && (defined $dim2)){
			        	my $sarea = $custom_geometry ? $_->Size->toString() : 4.836624601*($ssize1)**(2/3); 
		                #$scale = ($dim1 > $dim2)? $vol1."*Nav" : (($dim2 > $dim1)? $vol2."*Nav" : (($dim1==3)? $vol1."*Nav" : $sarea)) ;  # Sphere surface area comes into play only if both compartments are 2D
		                $scale = ($dim1 == 3) ? "*Nav" : (($dim2 ==3) ? "*Nav" : "/rxn_layer_t"); 
				  		if (($dim1 ==3) || ($dim2==3)){
				       		$tpy_param->{'unit'} = "/M.s"; 
				       	}
				  		else{
				       		$tpy_param->{'unit'} = "um^2/#.s"; 
				       	}
				  		#$scale = ($dim1 == 3)? "" : (($dim2 ==3 )? "" : "") ;  # Sphere surface area comes into play only if both compartments are 2D
				  		# Overwrite previously stored hash value with the new calculation
			          	$tparam{$rconst} = $rconst_val.$scale;  
			    	} 
		      	}
	    	}
			$tpy_param->{'value'} = $tparam{$rconst}; 
			$rpy_param{$rconst} = "{".join(",",map("\"$_\"".":\"".$tpy_param->{$_}."\"", @tpy_keys))."}"; 
	    }
	}    

	# Copy into the new hash any parameter that has not yet been included 
	foreach (@{$plist->Array}){
		unless ($tparam{$_->Name}) {$tparam{$_->Name} = $_->evaluate([],$plist) if ($_->Type =~ /^Constant/)}; 
	}
	
    foreach (@{$plist->Array}){
		if ($rpy_param{$_->Name}){
	    	push(@py_param, $rpy_param{$_->Name}) if ($_->Type =~ /^Constant/) ; 
		}
	}

	$mdl .= join "", map {(exists $tparam{$_->Name} && (!exists $tcomp{$_->Name}))? $indent.$_->Name." = ".$tparam{$_->Name}."\n":""} @{$plist->Array};

	# Molecule Types
	#$mdl .= "\n/* Molecule Types */\n".$model->MoleculeTypesList->writeMDL($indent);
	
	# Define species and diffusion constants
	my @py_species;   # For writing python file for cell blender
    my @dpy_param; 
    $mdl .= "\n/* Diffusion bloc */\n".$model->SpeciesList->writeMDL($model, $indent,\@py_species, \@dpy_param);
        
	my @py_reactions; 
	# Define reactions
	$mdl .= "\n/* Reactions bloc */\n".$model->RxnList->writeMDL($plist, $iscomp, \@py_reactions); 
        
 	my @SeedSpecies; 
	foreach my $spc (@{$model->SpeciesList->Array}){
		push (@SeedSpecies, $spc) unless (!$spc->Concentration); 
 	}
        
	# To include seed species concentrations as parameters for the CellBlender python file. 
    my @conc_array = map($_->Concentration, @SeedSpecies); 
    foreach my $par (@{$plist->Array}){
    	if ($par->Type =~ /^Constant/) {
        	if (grep $_ eq $par->Name, @conc_array){
            	$tpy_param = {'name'=>$par->Name,'value'=>$par->evaluate(),'unit'=>"Number",'type'=>""}; 
                push(@py_param,"{".join(",",map("\"$_\"".":\"".$tpy_param->{$_}."\"", @tpy_keys))."}"); 
          	}
     	}
    }
    push(@py_param, @dpy_param); 

    my @py_SeedSpecies;
	foreach (@SeedSpecies){
		my $tpytext = "{"; 
	    my $comp; 
	    my $dim = ( $comp = $_->SpeciesGraph->Compartment) ?  $comp->SpatialDimensions : 3; 
	    my $orient = ($dim == 2)? "'" : ","; 
	    $tpytext .= "\"name\":".sprintf("\"Release_Site_s%d\",",$_->Index);
	    $tpytext .= "\"molecule\":".sprintf("\"s%d\",",$_->Index);
	    $tpytext .= "\"shape\":".sprintf("\"%s\",","OBJECT");
	    $tpytext .= "\"orient\":".sprintf("\"%s\",",$orient);
	    $tpytext .= "\"quantity_type\":".sprintf("\"%s\",","DENSITY");
	    my $factor; 
	    if ( $comp = $_->SpeciesGraph->Compartment){
	    	$factor = ($dim == 2 )? "/vol_".$comp->Name : "/(Nav*vol_".$comp->Name.")"; 
		}
	    else{
	    	$factor = "/(Nav*1000)"; 
		}
	    $tpytext .= "\"quantity_expr\":".sprintf("\"%s%s\",",$_->Concentration,$factor); 
	    push(@py_SeedSpecies, $tpytext); 
	}
	     
	$mdl .= ($custom_geometry) ? "" : "\n\n/************ DEFAULT GEOMETRY LOADED **********/\n";
	$mdl .= "\n\nINSTANTIATE Scene OBJECT\n{\n";
	     
	if ($custom_geometry){
		foreach my $key (keys %object){ # Outermost compartment is defined in BNGL file as a 2D boundary 
	    	$mdl .= $indent.$object{$key}." OBJECT ".$object{$key}." { SCALE = [$scale{$key}, $scale{$key}, $scale{$key}] }\n"; 
	    }
	}
	else{
		foreach my $key (keys %shape){ # 2D buondary of the outer-most compartment is not defined in BNGL file as an additional compartment
	    	$mdl .= $indent.$object{$key}." OBJECT ".$object{$key}." { SCALE = [$scale{$key}, $scale{$key}, $scale{$key}] }\n"; 
	    }
	}
	     
	my $i = 0; 
	foreach (@SeedSpecies){
		my $relsite = $custom_geometry ?  $_->getMDLRelSite(\%surf,\%shape,$custom_geometry) : $_->getMDLRelSite(\%object,\%shape); 
	    $mdl .= "\n".$indent."s".$_->Index."_rel RELEASE_SITE\n$indent"."{\n$indent SHAPE = $relsite"; 
	    $mdl .= "\n$indent MOLECULE = s".($iscomp ? ($_->SpeciesGraph->Compartment->SpatialDimensions == 3 ? $_->Index : $_->Index."'") : $_->Index);
	    $mdl .= "\n$indent NUMBER_TO_RELEASE = ".$_->Concentration."\n$indent RELEASE_PROBABILITY = 1"."\n$indent"."}"; 
		my $pyrelsite = $relsite;
		$pyrelsite =~ s/Scene.//g;
		$pyrelsite =~ s/obj_wall/ALL/g;
	    $py_SeedSpecies[$i] .= "\"object_expr\":".sprintf("\"%s\"}",$pyrelsite);
		$i++;
	}
	$mdl .= "\n}";
        
    my $pytext="{\n"; 
    $i = 0; 	


    #@py_param = map{sprintf("%d", ++$i).":".$_} @py_param;

	$pytext .= "\"par_list\":[\n".$indent; 
    $pytext .= join(",\n$indent",@py_param); 
	$pytext .= "],"; 

	$pytext .= "\n\n\"mol_list\":[\n".$indent; 
    $pytext .= join(",\n$indent",@py_species); 
	$pytext .= "],";  

    $i = 0; 	
	#@py_reactions = map{sprintf("%d", ++$i).":".$_} @py_reactions;
    $pytext .= "\n\n\"rxn_list\":[\n".$indent; 
    $pytext .= join(",\n$indent",@py_reactions); 
	$pytext .= "],";


    $i = 0; 	
	#@py_SeedSpecies = map{sprintf("%d", ++$i).":".$_} @py_SeedSpecies;
	$pytext .= "\n\n\"rel_list\":[\n".$indent; 
    $pytext .= join(",\n$indent",@py_SeedSpecies); 
	$pytext .= "]\n}"; 


	#my $pyfile = File::Spec->catfile(,"net.py");
	my $pyfile = "${path}${filebase}.bngl.json";
	open (WRITEPYFILE, '>', $pyfile);
    print WRITEPYFILE $pytext; 
	close(WRITEPYFILE);
    print "Wrote CellBlender reaction network script to $pyfile.\n"; 	    
    $mdl .= "\n\n/* Observables bloc */\nREACTION_DATA_OUTPUT\n{";
	$mdl .= "\n$indent"."STEP = 1e-6\n"; 
	foreach my $obs (@{$model->Observables}) { $mdl .= $obs->writeMDL(); }
	$mdl .= "\n}\n\n"; 
	
	$mdl .= "VIZ_OUTPUT {\n"."$indent"."MODE = CELLBLENDER\n"."$indent"."FILENAME = \"./viz_data/$mdlscript_filebase\"\n";
	$mdl .= "$indent"."MOLECULES\n"."$indent"."{\n"."$indent"."$indent"."NAME_LIST {ALL_MOLECULES}\n"."$indent"."$indent"."ITERATION_NUMBERS {ALL_DATA @ [1, 100, [200 TO 100000 STEP 100]]}\n";
	$mdl .= "$indent"."}\n"."}"; 
	
	print $MDL $mdl;
	close($mdlscript_path); 
    print "Wrote MDL file to $mdlscript_path.\n";	
	return ();       
}
# code for writeMDL ends here 

# generate XML string representing the BNGL model
sub toXML
{
	my $model = shift @_;
	my $user_params = @_ ? shift @_ : {};

    # default parameters
    my %params = (
        'evaluate_expressions' => 1,
    );

    # add user parameters
    while ( my ($key,$val) = each %$user_params )
    {   $params{$key} = $val;   }

	return '' if $BNGModel::NO_EXEC;

    # get BNG version	
	my $version = BNGversion();

    # get mopdel name
	my $model_name = $model->Name;

    # define size of indent
	my $indent = "    ";

    # are we evaluating expressions?
    my $evaluate_expressions = $params{'evaluate_expressions'};

    # Begin writing XML #
	# HEADER
	my $xml =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
              ."<!-- Created by BioNetGen $version  -->\n"
              ."<sbml xmlns=\"http://www.sbml.org/sbml/level3\" level=\"3\" version=\"1\">\n"
              ."  <model id=\"$model_name\">\n";


	# Parameters
	$xml .= $indent . "<ListOfParameters>\n";
	my $indent2 = "  " . $indent;
	my $plist   = $model->ParamList;
	foreach my $param ( @{$plist->Array} )
    {
		my $value;
		my $type;
		my $do_print = 0;
		if ( $param->Type =~ /^Constant/ )
		{
			$value = ($evaluate_expressions) ? sprintf "%.8g", $param->evaluate([], $plist) : $param->toString($plist);
			$value =~ s/(e[+-])0+(\d+)/$1$2/; # strip any leading zeros in exponent (improves cross-platform portability)
			$type  = ($evaluate_expressions) ? "Constant" : $param->Type;
			$do_print = 1;
		}
		next unless $do_print;
		$xml .= sprintf( "$indent2<Parameter id=\"%s\"", $param->Name );
		$xml .= " type=\"$type\"";
		$xml .= " value=\"$value\"";
		$xml .= "/>\n";
	}
	$xml .= $indent . "</ListOfParameters>\n";

	# Molecule Types
	$xml .= $model->MoleculeTypesList->toXML($indent);

	# Compartments
	$xml .= $model->CompartmentList->toXML($indent, $plist);

	# Species
	if (@{$model->Concentrations}){
	    $xml .= $model->SpeciesList->toXML($indent,$model->Concentrations);
	} else {
	    $xml .= $model->SpeciesList->toXML($indent);
	}

	# Reaction rules
	my $string = $indent . "<ListOfReactionRules>\n";
	$indent2 = "  " . $indent;
	my $rindex  = 1;
	foreach my $rset ( @{$model->RxnRules} )
    {
		foreach my $rr ( @$rset )
        {
			$string .= $rr->toXML( $indent2, $rindex, $plist );
			++$rindex;
		}
	}
	$string .= $indent . "</ListOfReactionRules>\n";
	$xml .= $string;

	# Observables
	$string  = $indent . "<ListOfObservables>\n";
	$indent2 = "  " . $indent;
	my $oindex  = 1;
	foreach my $obs ( @{$model->Observables} )
    {
		$string .= $obs->toXML( $indent2, $oindex );
		++$oindex;
	}
	$string .= $indent . "</ListOfObservables>\n";
	$xml .= $string;

	# Functions
	$xml .= $indent . "<ListOfFunctions>\n";
	$indent2 = "  " . $indent;
	foreach my $param ( @{$plist->Array} )
    {
		next unless ( $param->Type eq "Function" );
		$xml .= $param->Ref->toXML( $plist, $indent2 );
	}
	$xml .= $indent . "</ListOfFunctions>\n";

	# FOOTER
	$xml .=  "  </model>\n"
            ."</sbml>\n";

	return $xml;
}



###
###
###



# write reaction network to SBML Level 2 Version 3 format
sub writeSBML
{
	my $model  = shift @_;
	my $params = @_ ? shift @_ : {};

	return '' if $BNGModel::NO_EXEC;

    # nothing to do unless a reactions are defined
	unless ( defined $model->RxnList  and  @{$model->RxnList->Array} )
	{   return "writeSBML(): No reactions in current model--nothing to do.";   }

    # get parameter list
	my $plist = $model->ParamList;

    # get model name
	my $model_name = $model->Name;

	# Strip prefixed path
	my $prefix = defined $params->{prefix} ? $model->getOutputPrefix( $params->{prefix} ) : $model->getOutputPrefix();
	my $suffix = ( defined $params->{suffix} ) ? $params->{suffix} : 'sbml';
#	unless ( $suffix eq '' )
#    {   
    	$prefix .= "_${suffix}";   
#    }

    # define file name
	my $file = "${prefix}.xml";

    # open file
    my $SBML;
	open( $SBML, '>', $file )  or die "Couldn't open $file: $!\n";

    # get BNG version
	my $version = BNGUtils::BNGversion();


	# 0. HEADER
#	print $SBML <<"EOF";
#<?xml version="1.0" encoding="UTF-8"?>
#<!-- Created by BioNetGen $version  -->
#<sbml xmlns="http://www.sbml.org/sbml/level2" level="2" version="1">
#  <model id="$model_name">
#EOF
	print $SBML qq{<?xml version="1.0" encoding="UTF-8"?>
<!-- Created by BioNetGen $version  -->
<sbml xmlns="http://www.sbml.org/sbml/level2/version3" level="2" version="3">
  <model id="$model_name">
};


	# 1. Compartments
#	print $SBML <<"EOF";
#    <listOfCompartments>
#      <compartment id="cell" size="1"/>
#    </listOfCompartments>
#EOF
	
	if ($model->CompartmentList->Used) { # @a is not empty...
        print $SBML "    <listOfCompartments>\n";
        foreach my $comp (@{$model->CompartmentList->Array})
        {
        		print $SBML $comp->toXML("      ", $plist);
        }
        print $SBML "    </listOfCompartments>\n";
		#printf $SBML "%s",$model->CompartmentList->toXML("     ");
	} else { # @a is empty
	print $SBML qq{    <listOfCompartments>
      <compartment id="cell" size="1"/>
    </listOfCompartments>
};
	}


	# 2. Species
	print $SBML "    <listOfSpecies>\n";
	my $use_array = @{$model->Concentrations} ? 1 : 0;
	foreach my $spec ( @{$model->SpeciesList->Array} )
	{
		my $conc;
		if ($use_array)
        {   $conc = $model->Concentrations->[ $spec->Index - 1 ];   }
		else
        {   $conc = $spec->Concentration;   }

        # If concentration is a parameter name, then evaluate the parameter
		unless ( BNGUtils::isReal($conc) )
        {   $conc = $plist->evaluate($conc, []);   }
        
        # NOTE: In SBML Level 2 Version 2, the InitialAssignment construct was introduced that
        # "permits the calculation of the value of a constant or the initial value of a variable 
        # from the values of other quantities in a model" -- see
        # [http://sbml.org/Software/libSBML/docs/java-api/org/sbml/libsbml/InitialAssignment.html].
        # We could use this in the next version of writeSBML() to allow for variable initial
        # concentrations. --LAH
        my $compartmentString; 
        if (defined($spec->SpeciesGraph->Compartment)){
        		$compartmentString = $spec->SpeciesGraph->Compartment->Name;
        }
        else{
        		$compartmentString = "cell";
        }
		printf $SBML "      <species id=\"S%d\" compartment=\"%s\" initialConcentration=\"%.8g\"",
		                                                                $spec->Index, $compartmentString, $conc;

		if ( $spec->SpeciesGraph->Fixed )
        {   printf $SBML " boundaryCondition=\"true\"";   }

		printf $SBML " name=\"%s\"", $spec->SpeciesGraph->StringExact;
		print $SBML "/>\n";
	}
	print $SBML "    </listOfSpecies>\n";


	# 3. Parameters
	# A. Rate constants
	print $SBML "    <listOfParameters>\n";
	if ($plist->countType('Constant')){
		print $SBML "      <!-- Independent variables -->\n";
		foreach my $param ( @{$plist->Array} )
		{
		    next unless ( $param->Type eq 'Constant' );
			printf $SBML "      <parameter id=\"%s\" value=\"%.8g\"/>\n", $param->Name, $param->evaluate([], $plist);
		}
	}
	if ($plist->countType('ConstantExpression')){
		print $SBML "      <!-- Dependent variables -->\n";
		foreach my $param ( @{$plist->Array} )
		{
		    next unless ( $param->Type eq 'ConstantExpression' );	
			printf $SBML "      <parameter id=\"%s\" constant=\"true\"/>\n", $param->Name;
		}
	}
	
	# B. Observables
	if ( @{$model->Observables} )
	{
		print $SBML "      <!-- Observables -->\n";
		foreach my $obs ( @{$model->Observables} )
		{
			printf $SBML "      <parameter id=\"%s\" constant=\"false\"/>\n", $obs->Name;
		}
	}
	
	# C. Global functions
	if ($plist->countType('Function')){
		print $SBML "      <!-- Global functions -->\n";
		foreach my $param ( @{$plist->Array} )
		{
		    next unless ( $param->Type eq 'Function');
		    next if ( @{$param->Ref->Args} ); # Don't print local functions
		    printf $SBML "      <parameter id=\"%s\" constant=\"false\"/>\n", $param->Name;
		}
	}
	
	print $SBML "    </listOfParameters>\n";
	

	# 3.5. Initial assignments (for dependent variables)
	if ($plist->countType('ConstantExpression')){
	    print $SBML "    <listOfInitialAssignments>\n";
		print $SBML "      <!-- Dependent variables -->\n";
		foreach my $param ( @{$plist->Array} )
	    {
	#		next if ( $param->Expr->Type eq 'NUM' );
			next unless ( $param->Type eq 'ConstantExpression');
			printf $SBML "      <initialAssignment symbol=\"%s\">\n", $param->Name;
	        #print  $SBML "        <notes>\n";
	        #print  $SBML "          <xhtml:p>\n";
	        #printf $SBML "            %s=%s\n", $param->Name,$param->toString($plist);
	        #print  $SBML "          </xhtml:p>\n";
	        #print  $SBML "        </notes>\n";
			printf $SBML $param->toMathMLString( $plist, "        " );
			print $SBML "      </initialAssignment>\n";
		}
		print $SBML "    </listOfInitialAssignments>\n";
	}
	
	# 4. Assignment rules (for observables and functions)
	if ( @{$model->Observables} or $plist->countType('Function') ){
		print $SBML "    <listOfRules>\n";
		if ( @{$model->Observables} ){
			print $SBML "      <!-- Observables -->\n";
			foreach my $obs ( @{$model->Observables} )
	        {
	#			printf $SBML "      <assignmentRule variable=\"%s\">\n", "Group_" . $obs->Name;
				printf $SBML "      <assignmentRule variable=\"%s\">\n", $obs->Name;
				my ( $ostring, $err ) = $obs->toMathMLString();
				if ($err) { return $err; }
				foreach my $line ( split "\n", $ostring )
	            {
					print $SBML "          $line\n";
				}
				print $SBML "      </assignmentRule>\n";
			}
		}
		if ($plist->countType('Function')){
			print $SBML "      <!-- Global functions -->\n";
			foreach my $param ( @{$plist->Array} )
		    {
				next unless ( $param->Type eq 'Function');
				next if ( @{$param->Ref->Args} ); # Don't print local functions
				printf $SBML "      <assignmentRule variable=\"%s\">\n", $param->Name;
				printf $SBML $param->toMathMLString( $plist, "        " );
				print $SBML "      </assignmentRule>\n";
			}
		}
		print $SBML "    </listOfRules>\n";
	}

	# 5. Reactions
	print $SBML "    <listOfReactions>\n";
	my $index = 0;
	foreach my $rxn ( @{$model->RxnList->Array} )
    {
		++$index;
		printf $SBML "      <reaction id=\"R%d\" reversible=\"false\">\n", $index;

		#Get indices of reactants
		my @rindices = ();
		foreach my $spec ( @{$rxn->Reactants} )
        {
			push @rindices, $spec->Index;
		}
		@rindices = sort { $a <=> $b } @rindices;

		#Get indices of products
		my @pindices = ();
		foreach my $spec ( @{$rxn->Products} )
        {
			push @pindices, $spec->Index;
		}
		@pindices = sort { $a <=> $b } @pindices;

		if (scalar(@rindices) > 0){
			print $SBML "        <listOfReactants>\n";
			foreach my $i (@rindices)
	        {
				printf $SBML "          <speciesReference species=\"S%d\"/>\n", $i;
			}
			print $SBML "        </listOfReactants>\n";
		}
		
		if (scalar(@pindices) > 0){
			print $SBML "        <listOfProducts>\n";
			foreach my $i (@pindices)
	        {
				printf $SBML "          <speciesReference species=\"S%d\"/>\n", $i;
			}
			print $SBML "        </listOfProducts>\n";
		}
		
		print $SBML "        <kineticLaw>\n";
		my ( $rstring, $err ) = $rxn->RateLaw->toMathMLString( \@rindices, \@pindices, $rxn->StatFactor );
		if ($err) { return $err; }

		foreach my $line ( split "\n", $rstring )
        {
			print $SBML "          $line\n";
		}
		print $SBML "        </kineticLaw>\n";

		print $SBML "      </reaction>\n";
	}
	print $SBML "    </listOfReactions>\n";


	# 6. FOOTER
#	print $SBML <<"EOF";
#  </model>
#</sbml>
#EOF
	print $SBML qq{  </model>
</sbml>
};

    close $SBML;
	print "Wrote SBML to $file.\n";
	return;
}



###
###
###



sub writeSSC
{
	my $model  = shift @_;
	my $params = @_ ? shift @_ : {};
	return '' if $BNGModel::NO_EXEC;

    # get model name
	my $model_name = $model->Name;

	my $prefix = defined $params->{prefix} ? $model->getOutputPrefix( $params->{prefix} ) : $model->getOutputPrefix();
	my $suffix = ( defined $params->{suffix} ) ? $params->{suffix} : undef;
	if ( $suffix ) {
		$prefix .= "_${suffix}";
	}
	my $file = "${prefix}.rxn";
	open( SSCfile, ">$file" ) || die "Couldn't open $file: $!\n";
	my $version = BNGversion();
	print SSCfile
	  "--# SSC-file for model $model_name created by BioNetGen $version\n";
	print "Writing SSC translator .rxn file.....";

	#-- Compartment default for SSC ---- look more into it
	printf SSCfile
	  "region World \n  box width 1 height 1 depth 1\nsubvolume edge 1";

	# --This part corresponds to seed species
	print SSCfile "\n\n";
	print SSCfile "--# Initial molecules and their concentrations\n";
	my $sp_string = $model->SpeciesList->writeSSC( $model->Concentrations,
                                                   $model->ParamList       );
	print SSCfile $sp_string;

	# --This part in SSC corrsponds to Observables
	if ( @{$model->Observables })
    {
		print SSCfile"\n\n--# reads observables";
		print SSCfile "\n";
		foreach my $obs ( @{$model->Observables} )
        {
			my $ob_string = $obs->toStringSSC();
			if ( $ob_string =~ /\?/ )
            {
				print STDOUT " \n WARNING: SSC does not implement ?. The observable has been commented. Please see .rxn file for more details \n";
				print STDOUT "\n See Observable\n", $obs->toString();
				$ob_string = "\n" . "--#" . "record " . $ob_string;
				print SSCfile $ob_string;
			}    #putting this string as a comment and carrying on
			else
            {
				print SSCfile "\nrecord ", $ob_string;
			}
		}
	}

	# --Reaction rules
	print SSCfile" \n\n--# reaction rules\n";
	foreach my $rset ( @{$model->RxnRules} )
    {
		my $id = 0;
		my $rreverse = ( $#$rset > 0 ) ? $rset->[1] : "";
		( my $reac1, my $errorSSC ) = $rset->[0]->toStringSSC($rreverse);
		if ( $errorSSC == 1 )
        {
			print STDOUT "\nSee rule in .rxn \n",
			  $rset->[0]->toString($rreverse);
			$reac1 = "--#" . $reac1;
		}
		print SSCfile $reac1;
		print SSCfile "\n";
		if ($rreverse)
        {
			( my $reac2, my $errorSSC ) = $rset->[1]->toStringSSC($rreverse);
			if ( $errorSSC == 1 ) { $reac2 = "--#" . $reac2; }
			print SSCfile $reac2;
			print SSCfile "\n";
		}
	}
	print "\nWritten SSC file\n";
	return ();
}



# This subroutine writes a file which contains the information corresponding to the parameter block in BNG
sub writeSSCcfg
{
	my $model  = shift @_;
	my $params = @_ ? shift @_ : {};
	return '' if $BNGModel::NO_EXEC;

    # get model name
	my $model_name = $model->Name;

	# Strip prefixed path
	my $prefix = defined $params->{prefix} ? $model->getOutputPrefix( $params->{prefix} ) : $model->getOutputPrefix();
	my $suffix = ( defined $params->{suffix} ) ? $params->{suffix} : '';
	if ( $suffix ne '' )
    {
		$prefix .= "_${suffix}";
	}

	my $file    = "${prefix}.cfg";
	my $version = BNGversion();

	open( SSCcfgfile, ">$file" ) || die "Couldn't open $file: $!\n";
	print STDOUT "\n Writting SSC cfg file \n";
	print SSCcfgfile "# SSC cfg file for model $model_name created by BioNetGen $version\n";
	print SSCcfgfile $model->ParamList->writeSSCcfg();

	return;
}



###
###
###



# Write model to a MATLAB M-file
sub writeMfile
{	
	my $model = shift @_;
	my $params = @_ ? shift @_ : {};

    # a place to hold errors
    my $err;

    # nothing to do if NO_EXEC is true
	return '' if $BNGModel::NO_EXEC;

    # nothing to do if there are no reactions
	if ( @{$model->RxnList->Array}==0 )
	{
	    return "writeMfile() has nothing to do: no reactions in current model. "
	          ."Did you remember to call generate_network() before attempting to "
	          ."write network output?";
	}

    # get reference to parameter list
	my $plist = $model->ParamList;
	
	# get model name
	my $model_name = $model->Name;

    # Build output file name
	# ..use prefix if defined, otherwise use model name
	my $prefix = defined $params->{prefix} ? $model->getOutputPrefix( $params->{prefix} ) : $model->getOutputPrefix();
	# ..add suffix, if any
	my $suffix = ( defined $params->{suffix} ) ? $params->{suffix} : undef;
	if ( $suffix )
	{   $prefix .= "_${suffix}";   }

    # split prefix into volume, path and filebase
    my ($vol, $path, $filebase) = File::Spec->splitpath($prefix);
    
	# define m-script file name
    my $mscript_filebase = "${filebase}";
    my $mscript_filename = "${mscript_filebase}.m";
	my $mscript_path     = File::Spec->catpath($vol,$path,$mscript_filename);
    my $mscript_filebase_caps = uc $mscript_filebase;

    # configure options (see Matlab documentation of functions ODESET and ODE15S)
    my $odeset_abstol = 1e-4;
    if ( exists $params->{'atol'} )
    {   $odeset_abstol = $params->{'atol'};  }
    
    my $odeset_reltol = 1e-8;
    if ( exists $params->{'rtol'} )
    {   $odeset_reltol = $params->{'rtol'};  } 

    my $odeset_stats = 'off';
    if ( $params->{'stats'} )
    {   $odeset_stats = 'on';  } 
#    if ( exists $params->{'stats'} )
#    {   $odeset_stats = $params->{'stats'};  } 

    my $odeset_bdf = 'off';
    if ( $params->{'bdf'} )
    {   $odeset_bdf = 'on';  }
#    if ( exists $params->{'bdf'} )
#    {   $odeset_bdf = $params->{'bdf'};  }

    my $odeset_maxorder = 5;
    if ( exists $params->{'maxOrder'} )
    {   $odeset_maxorder = $params->{'maxOrder'};  } 

    # time options for mscript
    my $t_start = 0;
    if ( exists $params->{'t_start'} )
    {   $t_start = $params->{'t_start'};  }  

    my $t_end = 10;
    if ( exists $params->{'t_end'} )
    {   $t_end = $params->{'t_end'};  } 

    my $n_steps = 20;
    if ( exists $params->{'n_steps'} )
    {   $n_steps = $params->{'n_steps'};  } 

    # configure time step dependent options
    my $odeset_maxstep = undef;
    if ( exists $params->{'max_step'} )
    {   $odeset_maxstep = $params->{'max_step'};  }     
    
    # construct ODESET function call
    my $mscript_call_odeset;
    if ( defined $odeset_maxstep )
    {
        $mscript_call_odeset = "opts = odeset( 'RelTol',   $odeset_reltol,   ...\n"
                              ."               'AbsTol',   $odeset_abstol,   ...\n"
                              ."               'Stats',    '$odeset_stats',  ...\n"
                              ."               'BDF',      '$odeset_bdf',    ...\n"
                              ."               'MaxOrder', $odeset_maxorder, ...\n"
                              ."               'MaxStep',  $odeset_maxstep    );\n";
    }
    else
    {
        $mscript_call_odeset = "opts = odeset( 'RelTol',   $odeset_reltol,   ...\n"
                              ."               'AbsTol',   $odeset_abstol,   ...\n"
                              ."               'Stats',    '$odeset_stats',  ...\n"
                              ."               'BDF',      '$odeset_bdf',    ...\n"
                              ."               'MaxOrder', $odeset_maxorder   );\n";    
    }
    # strip any leading zeros in exponent (improves cross-platform portability)
    $mscript_call_odeset =~ s/(e[+-])0+(\d+)/$1$2/g;

    # Index parameters associated with Constants, ConstantExpressions and Observables
    ($err) = $plist->indexParams();
    if ($err) { return $err };

    # and retrieve a string of expression definitions
    my $n_parameters = $plist->countType( 'Constant' );
    my $n_expressions = $plist->countType( 'ConstantExpression' ) + $n_parameters;
    (my $calc_expressions_string, $err) = $plist->getMatlabExpressionDefs();    
    if ($err) { return $err };

    # get list of parameter names and defintions for matlab
	my $mscript_param_names;
	my $mscript_param_values;
	($mscript_param_names, $mscript_param_values, $err) = $plist->getMatlabConstantNames();
    if ($err) { return $err };

    # get number of species
    my $n_species = scalar @{$model->SpeciesList->Array};
     
	# retrieve a string of observable definitions
    my $n_observables = scalar @{$model->Observables};
    my $calc_observables_string;
    ($calc_observables_string, $err) = $plist->getMatlabObservableDefs();
    if ($err) { return $err };
    
    # get list of observable names for matlab
	my $mscript_observable_names;
	($mscript_observable_names, $err) = $plist->getMatlabObservableNames();
    if ($err) { return $err };
    
    # Construct user-defined functions
    my $user_fcn_declarations = '';
    my $user_fcn_definitions  = '';
	foreach my $param ( @{ $model->ParamList->Array } )
	{
		if ( $param->Type eq 'Function' )
		{
		    # get reference to the actual Function
		    my $fcn = $param->Ref;
		    
		    # don't write function if it depends on a local observable evaluation (this is useless
		    #   since CVode can't do local evaluations)
		    next if ( $fcn->checkLocalDependency($plist) );
		    		    
		    # get function definition			    
		    my $fcn_defn = $fcn->toMatlabString( $plist, {fcn_mode=>'define', indent=>''} );

		    # add definition to the user_fcn_definitions string
		    $user_fcn_definitions .= $fcn_defn . "\n";
        }
	}
	
    # index reactions
    ($err) = $model->RxnList->updateIndex( $plist );
    if ($err) { return $err };

	# retrieve a string of reaction rate definitions
	my $n_reactions = scalar @{$model->RxnList->Array};
    my $calc_ratelaws_string;
    ($calc_ratelaws_string, $err) = $model->RxnList->getMatlabRateDefs( $plist );
    if ($err) { return $err };
    

    # get stoichiometry matrix (sparse encoding in a hashmap)
	my $stoich_hash = {};
	($err) = $model->RxnList->calcStoichMatrix( $stoich_hash );

	# retrieve a string of species deriv definitions
    my $calc_derivs_string;
    ($calc_derivs_string, $err) = $model->SpeciesList->toMatlabString( $model->RxnList, $stoich_hash, $plist );
    if ($err) { return $err };   	


    # get list of species names and initial value expressions for matlab
	my $mscript_species_names;
	my $mscript_species_init;
	($mscript_species_names, $mscript_species_init, $err) = $model->SpeciesList->getMatlabSpeciesNames( $model );
    if ($err) { return $err }; 


    ## Set up MATLAB Plot
    # fontsizes
    my $title_fontsize = 14;
    my $axislabel_fontsize = 12;
    my $legend_fontsize = 10;

    # generate code snippets for plotting observables or species
    my $mscript_plot_labels;
    my $mscript_make_plot;

    # get ylabel (either Number of Concentration)
    my $ylabel;
    if ( $model->SubstanceUnits eq 'Number' )
    {   $ylabel = 'number';   }
    elsif ( $model->SubstanceUnits eq 'Concentration' )
    {   $ylabel = 'concentration';   }
    else
    {   $ylabel = 'number or concentration';   }

    
    if ( @{$model->Observables} )
    {   # plot observables
        $mscript_plot_labels = "    observable_labels = { $mscript_observable_names };\n";
        
        $mscript_make_plot = "    plot(timepoints,observables_out);\n"
                            ."    title('${mscript_filebase} observables','fontSize',${title_fontsize},'Interpreter','none');\n"
                            ."    axis([${t_start} timepoints(end) 0 inf]);\n"
                            ."    legend(observable_labels,'fontSize',${legend_fontsize},'Interpreter','none');\n"
                            ."    xlabel('time','fontSize',${axislabel_fontsize},'Interpreter','none');\n"
                            ."    ylabel('${ylabel}','fontSize',${axislabel_fontsize},'Interpreter','none');\n";
    
    }
    else
    {   # plot species
        $mscript_plot_labels = "    species_labels = { $mscript_species_names };\n";
    
        $mscript_make_plot = "    plot(timepoints,species_out);\n"
                            ."    title('${mscript_filebase} species','fontSize',${title_fontsize},'Interpreter','none');\n"
                            ."    axis([${t_start} timepoints(end) 0 inf]);\n"
                            ."    legend(species_labels,'fontSize',${legend_fontsize},'Interpreter','none');\n"
                            ."    xlabel('time','fontSize',${axislabel_fontsize},'Interpreter','none');\n"
                            ."    ylabel('${ylabel}','fontSize',${axislabel_fontsize},'Interpreter','none');\n";
    }
    


    # open Mexfile and begin printing...
	open( Mscript, ">$mscript_path" ) || die "Couldn't open $mscript_path: $!\n";
    print Mscript <<"EOF";
function [err, timepoints, species_out, observables_out] = ${mscript_filebase}( timepoints, species_init, parameters, suppress_plot )
%${mscript_filebase_caps} Integrate reaction network and plot observables.
%   Integrates the reaction network corresponding to the BioNetGen model
%   '${model_name}' and then (optionally) plots the observable trajectories,
%   or species trajectories if no observables are defined. Trajectories are
%   generated using either default or user-defined parameters and initial
%   species values. Integration is performed by the MATLAB stiff solver
%   'ode15s'. ${mscript_filebase_caps} returns an error value, a vector of timepoints,
%   species trajectories, and observable trajectories.
%   
%   [err, timepoints, species_out, observables_out]
%        = $mscript_filebase( timepoints, species_init, parameters, suppress_plot )
%
%   INPUTS:
%   -------
%   species_init    : row vector of $n_species initial species populations.
%   timepoints      : column vector of time points returned by integrator.
%   parameters      : row vector of $n_parameters model parameters.
%   suppress_plot   : 0 if a plot is desired (default), 1 if plot is suppressed.
%
%   Note: to specify default value for an input argument, pass the empty array.
%
%   OUTPUTS:
%   --------
%   err             : 0 if the integrator exits without error, non-zero otherwise.
%   timepoints      : a row vector of timepoints returned by the integrator.
%   species_out     : array of species population trajectories
%                        (columns correspond to species, rows correspond to time).
%   observables_out : array of observable trajectories
%                        (columns correspond to observables, rows correspond to time).
%
%   QUESTIONS about the BNG Mfile generator?  Email justinshogg\@gmail.com



%% Process input arguments

% define any missing arguments
if ( nargin < 1 )
    timepoints = [];
end

if ( nargin < 2 )
    species_init = [];
end

if ( nargin < 3 )
    parameters = [];
end

if ( nargin < 4 )
    suppress_plot = 0;
end


% initialize outputs (to avoid error msgs if script terminates early
err = 0;
species_out     = [];
observables_out = [];


% setup default parameters, if necessary
if ( isempty(parameters) )
   parameters = [ $mscript_param_values ];
end
% check that parameters has proper dimensions
if (  size(parameters,1) ~= 1  ||  size(parameters,2) ~= $n_parameters  )
    fprintf( 1, 'Error: size of parameter argument is invalid! Correct size = [1 $n_parameters].\\n' );
    err = 1;
    return;
end

% setup default initial values, if necessary
if ( isempty(species_init) )
   species_init = initialize_species( parameters );
end
% check that species_init has proper dimensions
if (  size(species_init,1) ~= 1  ||  size(species_init,2) ~= $n_species  )
    fprintf( 1, 'Error: size of species_init argument is invalid! Correct size = [1 $n_species].\\n' );
    err = 1;
    return;
end

% setup default timepoints, if necessary
if ( isempty(timepoints) )
   timepoints = linspace($t_start,$t_end,$n_steps+1)';
end
% check that timepoints has proper dimensions
if (  size(timepoints,1) < 2  ||  size(timepoints,2) ~= 1  )
    fprintf( 1, 'Error: size of timepoints argument is invalid! Correct size = [t 1], t>1.\\n' );
    err = 1;
    return;
end

% setup default suppress_plot, if necessary
if ( isempty(suppress_plot) )
   suppress_plot = 0;
end
% check that suppress_plot has proper dimensions
if ( size(suppress_plot,1) ~= 1  ||  size(suppress_plot,2) ~= 1 )
    fprintf( 1, 'Error: suppress_plots argument should be a scalar!\\n' );
    err = 1;
    return;
end

% define parameter labels (this is for the user's reference!)
param_labels = { $mscript_param_names };



%% Integrate Network Model
 
% calculate expressions
[expressions] = calc_expressions( parameters );

% set ODE integrator options
$mscript_call_odeset

% define derivative function
rhs_fcn = @(t,y)( calc_species_deriv( t, y, expressions ) );

% simulate model system (stiff integrator)
try 
    [~, species_out] = ode15s( rhs_fcn, timepoints, species_init', opts );
    if(length(timepoints) ~= size(species_out,1))
        exception = MException('ODE15sError:MissingOutput','Not all timepoints output\\n');
        throw(exception);
    end
catch
    err = 1;
    fprintf( 1, 'Error: some problem encountered while integrating ODE network!\\n' );
    return;
end

% calculate observables
observables_out = zeros( length(timepoints), $n_observables );
for t = 1 : length(timepoints)
    observables_out(t,:) = calc_observables( species_out(t,:), expressions );
end


%% Plot Output, if desired

if ( ~suppress_plot )
    
    % define plot labels
$mscript_plot_labels
    % construct figure
$mscript_make_plot
end


%~~~~~~~~~~~~~~~~~~~~~%
% END of main script! %
%~~~~~~~~~~~~~~~~~~~~~%


% initialize species function
function [species_init] = initialize_species( params )

    species_init = zeros(1,$n_species);
$mscript_species_init
end


% user-defined functions
$user_fcn_definitions


% Calculate expressions
function [ expressions ] = calc_expressions ( parameters )

    expressions = zeros(1,$n_expressions);
$calc_expressions_string   
end



% Calculate observables
function [ observables ] = calc_observables ( species, expressions )

    observables = zeros(1,$n_observables);
$calc_observables_string
end


% Calculate ratelaws
function [ ratelaws ] = calc_ratelaws ( species, expressions, observables )

    ratelaws = zeros(1,$n_observables);
$calc_ratelaws_string
end

% Calculate species derivatives
function [ Dspecies ] = calc_species_deriv ( time, species, expressions )
    
    % initialize derivative vector
    Dspecies = zeros($n_species,1);
    
    % update observables
    [ observables ] = calc_observables( species, expressions );
    
    % update ratelaws
    [ ratelaws ] = calc_ratelaws( species, expressions, observables );
                        
    % calculate derivatives
$calc_derivs_string
end


end
EOF

	close(Mscript);
	print "Wrote M-file script $mscript_path.\n";
	return ();	
}



###
###
###






sub writeMexfile
{
	my $model = shift;
	my $params = (@_) ? shift : {};

    # a place to hold errors
    my $err;

    # nothing to do if NO_EXEC is true
	return '' if $BNGModel::NO_EXEC;

    # nothing to do if there are no reactions
	if ( @{$model->RxnList->Array}==0 )
	{
	    return "writeMexfile() has nothing to do: no reactions in current model. "
	          ."Did you remember to call generate_network() before attempting to "
	          ."write network output?";
	}

    # get reference to parameter list
	my $plist = $model->ParamList;
	
	# get model name
	my $model_name = $model->Name;
    
	# Strip prefixed path
	my $prefix = defined $params->{prefix} ? $model->getOutputPrefix( $params->{prefix} ) : $model->getOutputPrefix();
	my $suffix = ( defined $params->{suffix} ) ? $params->{suffix} : undef;
	if ( $suffix )
	{   $prefix .= "_${suffix}";   }
	
    # split prefix into volume, path and filebase
    my ($vol, $path, $filebase) = File::Spec->splitpath($prefix);

	# define mexfile name
    my $mex_filebase = "${filebase}_cvode";
	my $mex_filename = "${mex_filebase}.c";
    my $mex_path     = File::Spec->catpath($vol,$path,$mex_filename);
    
	# define m-script files name
    my $mscript_filebase = "${filebase}";
    my $mscript_filename = "${mscript_filebase}.m";
	my $mscript_path     = File::Spec->catpath($vol,$path,$mscript_filename);
    my $mscript_filebase_caps = uc $mscript_filebase;

    # configure options
    my $cvode_abstol = 1e-6;
    if ( exists $params->{'atol'} )
    {   $cvode_abstol = $params->{'atol'};  }
    
    my $cvode_reltol = 1e-8;
    if ( exists $params->{'rtol'} )
    {   $cvode_reltol = $params->{'rtol'};  }    

    my $cvode_max_num_steps = 2000;
    if ( exists $params->{'max_num_steps'} )
    {   $cvode_max_num_steps = $params->{'max_num_steps'};  }  

    my $cvode_max_err_test_fails = 7;
    if ( exists $params->{'max_err_test_fails'} )
    {   $cvode_max_err_test_fails = $params->{'max_err_test_fails'};  }  

    my $cvode_max_conv_fails = 10;
    if ( exists $params->{'max_conv_fails'} )
    {   $cvode_max_conv_fails = $params->{'max_conv_fails'};  }  

    my $cvode_max_step = '0.0';
    if ( exists $params->{'max_step'} )
    {   $cvode_max_step = $params->{'max_step'};  }

    # Stiff = CV_BDF,CV_NEWTON (Default); Non-stiff = CV_ADAMS,CV_FUNCTIONAL
    my $cvode_linear_multistep = 'CV_BDF';
    my $cvode_nonlinear_solver = 'CV_NEWTON';
    if ( exists $params->{'stiff'} )
    {   
        # if stiff is FALSE, then change to CV_ADAMS and CV_FUNCTIONAL
        unless ( $params->{'stiff'} )
        {
            $cvode_linear_multistep = 'CV_ADAMS';    
            $cvode_nonlinear_solver = 'CV_FUNCTIONAL';
        }
    }

    # set sparse option (only permitted with CV_NEWTON)
    my $cvode_linear_solver;
    if ( ($cvode_nonlinear_solver eq 'CV_NEWTON')  and  ($params->{'sparse'}) )
    {
        $cvode_linear_solver =     "flag = CVSpgmr(cvode_mem, PREC_NONE, 0);\n"
                              ."    if (check_flag(&flag, \"CVSpgmr\", 1))";
    }
    else
    {
        $cvode_linear_solver =     "flag = CVDense(cvode_mem, __N_SPECIES__);\n"
                              ."    if (check_flag(&flag, \"CVDense\", 1))";
    }

    # time options for mscript
    my $t_start = 0;
    if ( exists $params->{'t_start'} )
    {   $t_start = $params->{'t_start'};  }  

    my $t_end = 10;
    if ( exists $params->{'t_end'} )
    {   $t_end = $params->{'t_end'};  } 

    my $n_steps = 20;
    if ( exists $params->{'n_steps'} )
    {   $n_steps = $params->{'n_steps'};  } 

    # code snippet for cleaning up dynamic memory before exiting CVODE-MEX
    my $cvode_cleanup_memory =     "{                                  \n"
                              ."        N_VDestroy_Serial(expressions);\n"
                              ."        N_VDestroy_Serial(observables);\n"
                              ."        N_VDestroy_Serial(ratelaws);   \n"
                              ."        N_VDestroy_Serial(species);    \n"
                              ."        CVodeFree(&cvode_mem);         \n"
                              ."        return_status[0] = 1;          \n"
                              ."        return;                        \n"
                              ."    }                                  ";

    # Index parameters associated with Constants, ConstantExpressions and Observables
    ($err) = $plist->indexParams();
    if ($err) { return $err };

    # and retrieve a string of expression definitions
    my $n_parameters = $plist->countType( 'Constant' );
    my $n_expressions = $plist->countType( 'ConstantExpression' ) + $n_parameters;
    (my $calc_expressions_string, $err) = $plist->getCVodeExpressionDefs();    
    if ($err) { return $err };

    # get list of parameter names and defintions for matlab
	my $mscript_param_names;
	my $mscript_param_values;
	($mscript_param_names, $mscript_param_values, $err) = $plist->getMatlabConstantNames();
    if ($err) { return $err };


    # generate CVode references for species
    # (Do this now, because we need references to CVode species for Observable definitions and Rxn Rates)
    my $n_species = scalar @{$model->SpeciesList->Array};
    
     
	# retrieve a string of observable definitions
    my $n_observables = scalar @{$model->Observables};
    my $calc_observables_string;
    ($calc_observables_string, $err) = $plist->getCVodeObservableDefs();
    if ($err) { return $err };    
    
    # get list of observable names for matlab
	my $mscript_observable_names;
	($mscript_observable_names, $err) = $plist->getMatlabObservableNames();
    if ($err) { return $err };
    
    # Construct user-defined functions
    my $user_fcn_declarations = '';
    my $user_fcn_definitions = '';
	foreach my $param ( @{ $model->ParamList->Array } )
	{
		if ( $param->Type eq 'Function' )
		{
		    # get reference to the actual Function
		    my $fcn = $param->Ref;
		    
		    # don't write function if it depends on a local observable evaluation (this is useless
		    #   since CVode can't do local evaluations)
		    next if ( $fcn->checkLocalDependency($plist) );
		    		    
		    # get function declaration, add it to the user_fcn_declarations string
		    $user_fcn_declarations .= $fcn->toCVodeString( $plist, {fcn_mode=>'declare',indent=>''} );
		    
		    # get function definition			    
		    my $fcn_defn = $fcn->toCVodeString( $plist, {fcn_mode=>'define', indent=>''} );

		    # add definition to the user_fcn_definitions string
		    $user_fcn_definitions .= $fcn_defn . "\n";
        }
	}
	
    # index reactions
    ($err) = $model->RxnList->updateIndex( $plist );
    if ($err) { return $err };

	# retrieve a string of reaction rate definitions
	my $n_reactions = scalar @{$model->RxnList->Array};
    my $calc_ratelaws_string;
    ($calc_ratelaws_string, $err) = $model->RxnList->getCVodeRateDefs( $plist );
    if ($err) { return $err };
    

    # get stoichiometry matrix (sparse encoding in a hashmap)
	my $stoich_hash = {};
	($err) = $model->RxnList->calcStoichMatrix( $stoich_hash );

	# retrieve a string of species deriv definitions
    my $calc_derivs_string;
    ($calc_derivs_string, $err) = $model->SpeciesList->toCVodeString( $model->RxnList, $stoich_hash, $plist );
    if ($err) { return $err };   	



    # get list of species names and initial value expressions for matlab
	my $mscript_species_names;
	my $mscript_species_init;
	($mscript_species_names, $mscript_species_init, $err) = $model->SpeciesList->getMatlabSpeciesNames( $model );
    if ($err) { return $err };


    ## Set up MATLAB Plot
    # fontsizes
    my $title_fontsize = 14;
    my $axislabel_fontsize = 12;
    my $legend_fontsize = 10;

    # generate code snippets for plotting observables or species
    my $mscript_plot_labels;
    my $mscript_make_plot;

    # get ylabel (either Number of Concentration)
    my $ylabel;
    if ( $model->SubstanceUnits eq 'Number' )
    {   $ylabel = 'number';   }
    elsif ( $model->SubstanceUnits eq 'Concentration' )
    {   $ylabel = 'concentration';   }
    else
    {   $ylabel = 'number or concentration';   }

    
    if ( @{$model->Observables} )
    {   # plot observables
        $mscript_plot_labels = "    observable_labels = { $mscript_observable_names };\n";
        
        $mscript_make_plot = "    plot(timepoints,observables_out);\n"
                            ."    title('${mscript_filebase} observables','fontSize',${title_fontsize},'Interpreter','none');\n"
                            ."    axis([${t_start} timepoints(end) 0 inf]);\n"
                            ."    legend(observable_labels,'fontSize',${legend_fontsize},'Interpreter','none');\n"
                            ."    xlabel('time','fontSize',${axislabel_fontsize},'Interpreter','none');\n"
                            ."    ylabel('${ylabel}','fontSize',${axislabel_fontsize},'Interpreter','none');\n";
    
    }
    else
    {   # plot species
        $mscript_plot_labels = "    species_labels = { $mscript_species_names };\n";
    
        $mscript_make_plot = "    plot(timepoints,species_out);\n"
                            ."    title('${mscript_filebase} species','fontSize',${title_fontsize},'Interpreter','none');\n"
                            ."    axis([${t_start} timepoints(end) 0 inf]);\n"
                            ."    legend(species_labels,'fontSize',${legend_fontsize},'Interpreter','none');\n"
                            ."    xlabel('time','fontSize',${axislabel_fontsize},'Interpreter','none');\n"
                            ."    ylabel('${ylabel}','fontSize',${axislabel_fontsize},'Interpreter','none');\n";
    }


    # open Mexfile and begin printing...
	open( Mexfile, ">$mex_path" ) or die "Couldn't open $mex_path: $!\n";
    print Mexfile <<"EOF";
/*   
**   ${mex_filename}
**	 
**   Cvode-Mex implementation of BioNetGen model '$model_name'.
**
**   Code Adapted from templates provided by Mathworks and Sundials.
**   QUESTIONS about the code generator?  Email justinshogg\@gmail.com
**
**   Requires the CVODE libraries:  sundials_cvode and sundials_nvecserial.
**   https://computation.llnl.gov/casc/sundials/main.html
**
**-----------------------------------------------------------------------------
**
**   COMPILE in MATLAB:
**   mex -L<path_to_cvode_libraries> -I<path_to_cvode_includes>  ...
**          -lsundials_nvecserial -lsundials_cvode -lm ${mex_filename}
**
**   note1: if cvode is in your library path, you can omit path specifications.
**
**   note2: if linker complains about lib stdc++, try removing "-lstdc++"
**     from the mex configuration file "gccopts.sh".  This should be in the
**     matlab bin folder.
** 
**-----------------------------------------------------------------------------
**
**   EXECUTE in MATLAB:
**   [error_status, species_out, observables_out]
**        = ${mex_filebase}( timepoints, species_init, parameters )
**
**   timepoints      : column vector of time points returned by integrator.
**   parameters      : row vector of $n_parameters parameters.
**   species_init    : row vector of $n_species initial species populations.
**
**   error_status    : 0 if the integrator exits without error, non-zero otherwise.
**   species_out     : species population trajectories
**                        (columns correspond to states, rows correspond to time).
**   observables_out : observable trajectories
**                        (columns correspond to observables, rows correspond to time).
*/

/* Library headers */
#include "mex.h"
#include "matrix.h"
#include <stdlib.h>
#include <math.h>
#include <cvode/cvode.h>             /* prototypes for CVODE  */
#include <nvector/nvector_serial.h>  /* serial N_Vector       */
#include <cvode/cvode_dense.h>       /* prototype for CVDense */
#include <cvode/cvode_spgmr.h>       /* prototype for CVSpgmr */

/* Problem Dimensions */
#define __N_PARAMETERS__   $n_parameters
#define __N_EXPRESSIONS__  $n_expressions
#define __N_OBSERVABLES__  $n_observables
#define __N_RATELAWS__     $n_reactions
#define __N_SPECIES__      $n_species

/* core function declarations */
void  mexFunction ( int nlhs, mxArray *plhs[], int nrhs, const mxArray *prhs[] );
int   check_flag  ( void *flagvalue, char *funcname, int opt );
void  calc_expressions ( N_Vector expressions, double * parameters );
void  calc_observables ( N_Vector observables, N_Vector species, N_Vector expressions );
void  calc_ratelaws    ( N_Vector ratelaws,  N_Vector species, N_Vector expressions, N_Vector observables );
int   calc_species_deriv ( realtype time, N_Vector species, N_Vector Dspecies, void * f_data );

/* user-defined function declarations */
$user_fcn_declarations

/* user-defined function definitions  */
$user_fcn_definitions

/* Calculate expressions */
void
calc_expressions ( N_Vector expressions, double * parameters )
{
$calc_expressions_string   
}

/* Calculate observables */
void
calc_observables ( N_Vector observables, N_Vector species, N_Vector expressions )
{
$calc_observables_string
}

/* Calculate ratelaws */
void
calc_ratelaws ( N_Vector ratelaws, N_Vector species, N_Vector expressions, N_Vector observables )
{  
$calc_ratelaws_string
}


/* Calculate species derivatives */
int
calc_species_deriv ( realtype time, N_Vector species, N_Vector Dspecies, void * f_data )
{
    int         return_val;
    N_Vector *  temp_data;
    
    N_Vector    expressions;
    N_Vector    observables;
    N_Vector    ratelaws;

    /* cast temp_data */
    temp_data = (N_Vector*)f_data;
     
    /* sget ratelaws Vector */
    expressions = temp_data[0];
    observables = temp_data[1];
    ratelaws    = temp_data[2];
       
    /* calculate observables */
    calc_observables( observables, species, expressions );
    
    /* calculate ratelaws */
    calc_ratelaws( ratelaws, species, expressions, observables );
                        
    /* calculate derivatives */
$calc_derivs_string

    return(0);
}


/*
**   ========
**   main MEX
**   ========
*/
void mexFunction( int nlhs, mxArray * plhs[], int nrhs, const mxArray * prhs[] )
{
    /* variables */
    double *  return_status;
    double *  species_out;
    double *  observables_out;    
    double *  parameters;
    double *  species_init;
    double *  timepoints; 
    size_t    n_timepoints;
    size_t    i;
    size_t    j;

    /* intermediate data vectors */
    N_Vector  expressions;
    N_Vector  observables;
    N_Vector  ratelaws;

    /* array to hold pointers to data vectors */
    N_Vector  temp_data[3];
    
    /* CVODE specific variables */
    realtype  reltol;
    realtype  abstol;
    realtype  time;
    N_Vector  species;
    void *    cvode_mem;
    int       flag;

    /* check number of input/output arguments */
    if (nlhs != 3)
    {  mexErrMsgTxt("syntax: [err_flag, species_out, obsv_out] = network_mex( timepoints, species_init, params )");  }
    if (nrhs != 3)
    {  mexErrMsgTxt("syntax: [err_flag, species_out, obsv_out] = network_mex( timepoints, species_init, params )");  }


    /* make sure timepoints has correct dimensions */
    if ( (mxGetM(prhs[0]) < 2)  ||  (mxGetN(prhs[0]) != 1) )
    {  mexErrMsgTxt("TIMEPOINTS must be a column vector with 2 or more elements.");  }

    /* make sure species_init has correct dimensions */
    if ( (mxGetM(prhs[1]) != 1)  ||  (mxGetN(prhs[1]) != __N_SPECIES__) )
    {  mexErrMsgTxt("SPECIES_INIT must be a row vector with $n_species elements.");  } 

    /* make sure params has correct dimensions */
    if ( (mxGetM(prhs[2]) != 1)  ||  (mxGetN(prhs[2]) != __N_PARAMETERS__) )
    {  mexErrMsgTxt("PARAMS must be a column vector with $n_parameters elements.");  }

    /* get pointers to input arrays */
    timepoints   = mxGetPr(prhs[0]);
    species_init = mxGetPr(prhs[1]);
    parameters   = mxGetPr(prhs[2]);

    /* get number of timepoints */
    n_timepoints = mxGetM(prhs[0]);

    /* Create an mxArray for output trajectories */
    plhs[0] = mxCreateDoubleMatrix(1, 1, mxREAL );
    plhs[1] = mxCreateDoubleMatrix(n_timepoints, __N_SPECIES__, mxREAL);
    plhs[2] = mxCreateDoubleMatrix(n_timepoints, __N_OBSERVABLES__, mxREAL);

    /* get pointers to output arrays */
    return_status   = mxGetPr(plhs[0]);
    species_out     = mxGetPr(plhs[1]);
    observables_out = mxGetPr(plhs[2]);    
   
    /* initialize intermediate data vectors */
    expressions  = NULL;
    expressions = N_VNew_Serial(__N_EXPRESSIONS__);
    if (check_flag((void *)expressions, "N_VNew_Serial", 0))
    {
        return_status[0] = 1;
        return;
    }

    observables = NULL;
    observables = N_VNew_Serial(__N_OBSERVABLES__);
    if (check_flag((void *)observables, "N_VNew_Serial", 0))
    {
        N_VDestroy_Serial(expressions);
        return_status[0] = 1;
        return;
    }

    ratelaws    = NULL; 
    ratelaws = N_VNew_Serial(__N_RATELAWS__);
    if (check_flag((void *)ratelaws, "N_VNew_Serial", 0))
    {   
        N_VDestroy_Serial(expressions);
        N_VDestroy_Serial(observables);        
        return_status[0] = 1;
        return;
    }
    
    /* set up pointers to intermediate data vectors */
    temp_data[0] = expressions;
    temp_data[1] = observables;
    temp_data[2] = ratelaws;

    /* calculate expressions (expressions are constant, so only do this once!) */
    calc_expressions( expressions, parameters );

        
    /* SOLVE model equations! */
    species   = NULL;
    cvode_mem = NULL;

    /* Set the scalar relative tolerance */
    reltol = $cvode_reltol;
    abstol = $cvode_abstol;

    /* Create serial vector for Species */
    species = N_VNew_Serial(__N_SPECIES__);
    if (check_flag((void *)species, "N_VNew_Serial", 0))
    {  
        N_VDestroy_Serial(expressions);
        N_VDestroy_Serial(observables);
        N_VDestroy_Serial(ratelaws);
        return_status[0] = 1;
        return;
    }
    for ( i = 0; i < __N_SPECIES__; i++ )
    {   NV_Ith_S(species,i) = species_init[i];   }
    
    /* write initial species populations into species_out */
    for ( i = 0; i < __N_SPECIES__; i++ )
    {   species_out[i*n_timepoints] = species_init[i];   }
    
    /* write initial observables populations into species_out */ 
    calc_observables( observables, species, expressions );  
    for ( i = 0; i < __N_OBSERVABLES__; i++ )
    {   observables_out[i*n_timepoints] = NV_Ith_S(observables,i);   }

    /*   Call CVodeCreate to create the solver memory:    
     *   CV_ADAMS or CV_BDF is the linear multistep method
     *   CV_FUNCTIONAL or CV_NEWTON is the nonlinear solver iteration
     *   A pointer to the integrator problem memory is returned and stored in cvode_mem.
     */
    cvode_mem = CVodeCreate($cvode_linear_multistep, $cvode_nonlinear_solver);
    if (check_flag((void *)cvode_mem, "CVodeCreate", 0))
    $cvode_cleanup_memory



    /*   Call CVodeInit to initialize the integrator memory:     
     *   cvode_mem is the pointer to the integrator memory returned by CVodeCreate
     *   rhs_func  is the user's right hand side function in y'=f(t,y)
     *   T0        is the initial time
     *   y         is the initial dependent variable vector
     */
    flag = CVodeInit(cvode_mem, calc_species_deriv, timepoints[0], species);
    if (check_flag(&flag, "CVodeInit", 1))
    $cvode_cleanup_memory
   
    /* Set scalar relative and absolute tolerances */
    flag = CVodeSStolerances(cvode_mem, reltol, abstol);
    if (check_flag(&flag, "CVodeSStolerances", 1))
    $cvode_cleanup_memory   
   
    /* pass params to rhs_func */
    flag = CVodeSetUserData(cvode_mem, &temp_data);
    if (check_flag(&flag, "CVodeSetFdata", 1))
    $cvode_cleanup_memory
    
    /* select linear solver */
    $cvode_linear_solver
    $cvode_cleanup_memory
    
    flag = CVodeSetMaxNumSteps(cvode_mem, $cvode_max_num_steps);
    if (check_flag(&flag, "CVodeSetMaxNumSteps", 1))
    $cvode_cleanup_memory

    flag = CVodeSetMaxErrTestFails(cvode_mem, $cvode_max_err_test_fails);
    if (check_flag(&flag, "CVodeSetMaxErrTestFails", 1))
    $cvode_cleanup_memory

    flag = CVodeSetMaxConvFails(cvode_mem, $cvode_max_conv_fails);
    if (check_flag(&flag, "CVodeSetMaxConvFails", 1))
    $cvode_cleanup_memory

    flag = CVodeSetMaxStep(cvode_mem, $cvode_max_step);
    if (check_flag(&flag, "CVodeSetMaxStep", 1))
    $cvode_cleanup_memory

    /* integrate to each timepoint */
    for ( i=1;  i < n_timepoints;  i++ )
    {
        flag = CVode(cvode_mem, timepoints[i], species, &time, CV_NORMAL);
        if (check_flag(&flag, "CVode", 1))
        {
            N_VDestroy_Serial(expressions);
            N_VDestroy_Serial(observables);           
            N_VDestroy_Serial(ratelaws);
            N_VDestroy_Serial(species);
            CVodeFree(&cvode_mem);
            return_status[0] = 1; 
            return;
        }

        /* copy species output from nvector to matlab array */
        for ( j = 0; j < __N_SPECIES__; j++ )
        {   species_out[j*n_timepoints + i] = NV_Ith_S(species,j);   }
        
        /* copy observables output from nvector to matlab array */
        calc_observables( observables, species, expressions );         
        for ( j = 0; j < __N_OBSERVABLES__; j++ )
        {   observables_out[j*n_timepoints + i] = NV_Ith_S(observables,j);   }      
    }
 
    /* Free vectors */
    N_VDestroy_Serial(expressions);
    N_VDestroy_Serial(observables);  
    N_VDestroy_Serial(ratelaws);        
    N_VDestroy_Serial(species);

    /* Free integrator memory */
    CVodeFree(&cvode_mem);

    return;
}


/*  Check function return value...
 *   opt == 0 means SUNDIALS function allocates memory so check if
 *            returned NULL pointer
 *   opt == 1 means SUNDIALS function returns a flag so check if
 *            flag >= 0
 *   opt == 2 means function allocates memory so check if returned
 *            NULL pointer 
 */
int check_flag(void *flagvalue, char *funcname, int opt)
{
    int *errflag;

    /* Check if SUNDIALS function returned NULL pointer - no memory allocated */
    if (opt == 0 && flagvalue == NULL)
    {
        mexPrintf( "\\nSUNDIALS_ERROR: %s() failed - returned NULL pointer\\n", funcname );    
        return(1);
    }

    /* Check if flag < 0 */
    else if (opt == 1)
    {
        errflag = (int *) flagvalue;
        if (*errflag < 0)
        {
            mexPrintf( "\\nSUNDIALS_ERROR: %s() failed with flag = %d\\n", funcname, *errflag );
            return(1);
        }
    }

    /* Check if function returned NULL pointer - no memory allocated */
    else if (opt == 2 && flagvalue == NULL)
    {
        mexPrintf( "\\nMEMORY_ERROR: %s() failed - returned NULL pointer\\n", funcname );
        return(1);
    }

    return(0);
}
EOF
	close(Mexfile);



    # open Mexfile and begin printing...
	open( Mscript, ">$mscript_path" ) or die "Couldn't open $mscript_path: $!\n";
    print Mscript <<"EOF";
function [err, timepoints, species_out, observables_out ] = ${mscript_filebase}( timepoints, species_init, parameters, suppress_plot )
%${mscript_filebase_caps} Integrate reaction network and plot observables.
%   Integrates the reaction network corresponding to the BioNetGen model
%   '${model_name}' and then (optionally) plots the observable trajectories,
%   or species trajectories if no observables are defined. Trajectories are
%   generated using either default or user-defined parameters and initial
%   species values. Integration is performed by the CVode library interfaced
%   to MATLAB via the MEX interface. Before running this script, the model
%   source in file ${mex_filename} must be compiled (see that file for details).
%   ${mscript_filebase_caps} returns an error value, a vector of timepoints,
%   species trajectories, and observable trajectories.
%   
%   [err, timepoints, species_out, observables_out]
%        = ${mscript_filebase}( timepoints, species_init, parameters, suppress_plot )
%
%   INPUTS:
%   -------
%   timepoints      : column vector of time points returned by integrator.
%   species_init    : row vector of $n_species initial species populations.
%   parameters      : row vector of $n_parameters model parameters.
%   suppress_plot   : 0 if a plot is desired (default), 1 if plot is suppressed.
%
%   Note: to specify default value for an input argument, pass the empty array.
%
%   OUTPUTS:
%   --------
%   err             : 0 if the integrator exits without error, non-zero otherwise.
%   timepoints      : a row vector of timepoints returned by the integrator.
%   species_out     : array of species population trajectories
%                        (columns correspond to species, rows correspond to time).
%   observables_out : array of observable trajectories
%                        (columns correspond to observables, rows correspond to time).
%
%   QUESTIONS about the BNG Mfile generator?  Email justinshogg\@gmail.com



%% Process input arguments

% define any missing arguments
if ( nargin < 1 )
    timepoints = [];
end

if ( nargin < 2 )
    species_init = [];
end

if ( nargin < 3 )
    parameters = [];
end

if ( nargin < 4 )
    suppress_plot = 0;
end


% initialize outputs (to avoid error msgs if script terminates early
err = 0;
species_out     = [];
observables_out = [];


% setup default parameters, if necessary
if ( isempty(parameters) )
   parameters = [ $mscript_param_values ];
end
% check that parameters has proper dimensions
if (  size(parameters,1) ~= 1  ||  size(parameters,2) ~= $n_parameters  )
    fprintf( 1, 'Error: size of parameter argument is invalid! Correct size = [1 $n_parameters].\\n' );
    err = 1;
    return;
end

% setup default initial values, if necessary
if ( isempty(species_init) )
   species_init = initialize_species( parameters );
end
% check that species_init has proper dimensions
if (  size(species_init,1) ~= 1  ||  size(species_init,2) ~= $n_species  )
    fprintf( 1, 'Error: size of species_init argument is invalid! Correct size = [1 $n_species].\\n' );
    err = 1;
    return;
end

% setup default timepoints, if necessary
if ( isempty(timepoints) )
   timepoints = linspace($t_start,$t_end,$n_steps+1)';
end
% check that timepoints has proper dimensions
if (  size(timepoints,1) < 2  ||  size(timepoints,2) ~= 1  )
    fprintf( 1, 'Error: size of timepoints argument is invalid! Correct size = [t 1], t>1.\\n' );
    err = 1;
    return;
end

% setup default suppress_plot, if necessary
if ( isempty(suppress_plot) )
   suppress_plot = 0;
end
% check that suppress_plot has proper dimensions
if ( size(suppress_plot,1) ~= 1  ||  size(suppress_plot,2) ~= 1 )
    fprintf( 1, 'Error: suppress_plots argument should be a scalar!\\n' );
    err = 1;
    return;
end

% define parameter labels (this is for the user's reference!)
param_labels = { $mscript_param_names };



%% Integrate Network Model
try 
    % run simulation
    [err, species_out, observables_out] = ${mex_filebase}( timepoints, species_init, parameters );
catch
    fprintf( 1, 'Error: some problem integrating ODE network! (CVODE exitflag %d)\\n', err );
    err = 1;
    return;
end



%% Plot Output, if desired

if ( ~suppress_plot )
    
    % define plot labels
$mscript_plot_labels
    % construct figure
$mscript_make_plot
end



%~~~~~~~~~~~~~~~~~~~~~%
% END of main script! %
%~~~~~~~~~~~~~~~~~~~~~%



% initialize species function
function [species_init] = initialize_species( params )

    species_init = zeros(1,$n_species);
$mscript_species_init
end


end
EOF
	close Mscript;
	print "Wrote Mexfile $mex_path and M-file script $mscript_path.\n";
	return ();
}



###
###
###



sub writeMfile_QueryNames
{
	my $model = shift;
	my $plist = $model->ParamList;
	my $slist = $model->SpeciesList;
	my $err;
	
	my $mscript_param_names;
	my $mscript_param_values;
	($mscript_param_names, $mscript_param_values, $err) = $plist->getMatlabConstantNames();
    if ($err) { return ($err) };
    
    my $mscript_observable_names;
	($mscript_observable_names, $err) = $plist->getMatlabObservableNames();
    if ($err) { return ($err) };
    
    my $mscript_species_names;
	($mscript_species_names, $err) = $slist->getMatlabSpeciesNamesOnly();
    if ($err) { return ($err) };
    
    my $q_mscript = 'QueryNames.m';
    
    open(Q_Mscript,">$q_mscript");
    print Q_Mscript <<"EOF";
function [ param_labels, param_defaults, obs_labels, species_labels] = QueryNames( inputlist )
% % Loads all the parameter labels, parameter defaults, observable labels and species labels in the model
% % If generate_network() was executed, then the nanmes of all species are passed
% % If generate_network() was not executed, then the names of the seed speceis are passed

	param_labels = { $mscript_param_names };
	param_defaults = [ $mscript_param_values ];
	obs_labels = { $mscript_observable_names };
	species_labels = { $mscript_species_names };
end

EOF
	close Q_Mscript;
	print "Wrote M-file script $q_mscript.\n";
	return ();
    
}



###
###
###



sub writeMfile_ParametersObservables
{
	# John Sekar created this subroutine
	my $model = shift;
	my $params = (@_) ? shift : {};
	
	my $err;
	
	#Get ref to parameter list
	my $plist = $model->ParamList;
	
	#Names of M-file
	my $par_mscript = 'ParameterList.m';
	my $obs_mscript = 'ObservableList.m';
	
	#Getting param names and observable names
	my $mscript_param_names;
	my $mscript_param_values;
	($mscript_param_names, $mscript_param_values, $err) = $plist->getMatlabConstantNames();
    if ($err) { return ($err) };
    $mscript_param_names =~ s/[\\]//g;
    
    my $mscript_observable_names;
	($mscript_observable_names, $err) = $plist->getMatlabObservableNames();
    if ($err) { return ($err) };
    $mscript_observable_names =~ s/[\\]//g;
  
    
    #Writing parameter list script
	open( Par_Mscript, ">$par_mscript" ) || die "Couldn't open $par_mscript: $!\n";
    print Par_Mscript <<"EOF";
function [outputlist,defaultvals ] = ParameterList( inputlist )
% Used to manipulate and access parameter names
% If inputlist is empty, the entire list of labels is given as output
% If inputlist is a vector of indices, output is a cell array of parameter
% names corresponding to those indices, returns default error if not found
% If inputlist is a cell array of names, output is a vector of indices
% corresponding to those parameter names, returns zero if not found
	param_labels = { $mscript_param_names };
	param_defaults = [ $mscript_param_values ];
	
    param_num = max(size(param_labels));
	
    if nargin < 1
        outputlist = param_labels;
        defaultvals = param_defaults;
        return;
    end

    defaultvals = zeros(size(inputlist));

    if(isnumeric(inputlist))
        outputlist = cell(size(inputlist));
        
        	
        for i=1:1:max(size(inputlist))
            outputlist{i} = param_labels{inputlist(i)}; 
            defaultvals(i) = param_defaults(inputlist(i));
        end
    end
    
   if(iscellstr(inputlist))
       outputlist = zeros(size(inputlist));
       for i=1:1:max(size(inputlist))
           compare = strcmp(inputlist{i},param_labels);
           if(sum(compare)>0)
               outputlist(i) = find(compare,1);
               if(outputlist(i))
                   defaultvals(i) = param_defaults(outputlist(i));
               end
           end
           
       end
	end
end
EOF
	close Par_Mscript;
	print "Wrote M-file script $par_mscript.\n";
	
	#Writing observable list script
	open( Obs_Mscript, ">$obs_mscript" ) || die "Couldn't open $obs_mscript: $!\n";
    print Obs_Mscript <<"EOF";
function [outputlist ] = ObservableList( inputlist )
% Used to manipulate and access observable names
% If inputlist is empty, the entire list of labels is given as output
% If inputlist is a vector of indices, output is a cell array of observable
% names corresponding to those indices, returns default error if not found
% If inputlist is a cell array of names, output is a vector of indices
% corresponding to those observable names, returns zero if not found
	obs_labels = { $mscript_observable_names };
    obs_num = max(size(obs_labels));

    if nargin < 1
        outputlist = obs_labels;
        return;
    end
    
    if(isnumeric(inputlist))
        outputlist = cell(size(inputlist));
        for i=1:1:max(size(inputlist))
            outputlist{i} = obs_labels{inputlist(i)};
        end
    end
    
   if(iscellstr(inputlist))
       outputlist = zeros(size(inputlist));
       for i=1:1:max(size(inputlist))
           compare = strcmp(inputlist{i},obs_labels);
           if(sum(compare)>0)
               outputlist(i) = find(compare,1);
           else
               outputlist(i) = 0;
           end
       end 
end
EOF
	close Obs_Mscript;
	print "Wrote M-file script $obs_mscript.\n";
	return ();
}



###
###
###



sub writeLatex
{
	my $model = shift @_;
	my $params = @_ ? shift @_ : {};

	return '' if $BNGModel::NO_EXEC;

	unless ( $model->RxnList )
    {   return "writeLatex(): No reactions in current model--nothing to do.";   }

    # parameter list
	my $plist = $model->ParamList;

    # model name
	my $model_name = $model->Name;

	# Strip prefixed path
	my $prefix = defined $params->{prefix} ? $model->getOutputPrefix( $params->{prefix} ) : $model->getOutputPrefix();
	my $suffix = ( defined $params->{suffix} ) ? $params->{suffix} : undef;
	unless ( $suffix eq '' ) { $prefix .= "_${suffix}"; }

    # latex filename
	my $file = "${prefix}.tex";

    # open file
    my $Lfile;
	open( $Lfile, ">$file" ) or die "Couldn't open $file: $!\n";

	my $version = BNGversion();
	print$Lfile "% Latex formatted differential equations for model $prefix created by BioNetGen $version\n";

	# Document Header
	print $Lfile <<'EOF';
\documentclass{article}
\begin{document}
EOF

	# Dimensions
	my $Nspecies   = scalar @{$model->SpeciesList->Array};
	my $Nreactions = scalar @{$model->RxnList->Array};
	print $Lfile "\\section{Model Summary}\n";
	printf $Lfile "The model has %d species and %d reactions.\n", $Nspecies, $Nreactions;
	print $Lfile "\n";


	# Stoichiometry matrix
	my %S      = ();
	my @fluxes = ();
	my $irxn   = 1;
	foreach my $rxn ( @{$model->RxnList->Array} )
    {
		# Each reactant contributes a -1
		foreach my $r ( @{$rxn->Reactants} )
        {
			--$S{ $r->Index }{$irxn};
		}

		# Each product contributes a +1
		foreach my $p ( @{$rxn->Products} )
        {
			++$S{ $p->Index }{$irxn};
		}
		my ($flux, $err) = $rxn->RateLaw->toLatexString( $rxn->Reactants, $rxn->StatFactor,
			                                               $model->ParamList );
		if ($err) { return $err; }
		push @fluxes, $flux;
		++$irxn;
	}

	print $Lfile "\\section{Differential Equations}\n";
	print $Lfile "\\begin{eqnarray*}\n";
	foreach my $ispec ( sort {$a <=> $b} keys %S )
    {
		printf $Lfile "\\dot{x_{%d}}&=& ", $ispec;
		my $nrxn = 1;
		foreach my $irxn ( sort { $a <=> $b } keys %{ $S{$ispec} } )
        {
            my $mod;
			my $s = $S{$ispec}{$irxn};
			if ( $s == 1 ) {
				$mod = "+";
			}
			elsif ( $s == -1 ) {
				$mod = "-";
			}
			elsif ( $s > 0 ) {
				$mod = "+$s";
			}
			else {
				$mod = "+($s)";
			}
			if ( ($nrxn % 5) == 0 ) { print $Lfile "\\\\ &&"; }
			if ($s)
            {
				printf $Lfile " %s %s", $mod, $fluxes[ $irxn - 1 ];
				++$nrxn;
			}
		}

		if ( $nrxn == 1 ) {	print $Lfile "0"; }
		print $Lfile "\n\\\\\n";
	}
	print $Lfile "\\end{eqnarray*}\n";
	print $Lfile "\n";

	# Document Footer
	print $Lfile <<'EOF';
\end{document}
EOF
	close $Lfile;
	print "Wrote Latex equations to  $file.\n";
	return;
}

sub writeMfile_all
{

# Author: John Sekar
# Writes 3 files
# initSpecies_$name.m which holds the species initialization relations
# $name.m which is the model simulator (same as writeMfile)
# bngModel_$name.m which is a class which inherits bngModel from the MatLab toolbox	
	my $model = shift @_;
	my $params = @_ ? shift @_ : {};

    # a place to hold errors
    my $err;

    # nothing to do if NO_EXEC is true
	return '' if $BNGModel::NO_EXEC;

    # nothing to do if there are no reactions
	unless ( $model->RxnList )
	{
	    return ( "writeMfile() has nothing to do: no reactions in current model.\n"
	            ."  Did you remember to call generate_network() before attempting to\n"
	            ."  write network output?");
	}

    # get reference to parameter list
	my $plist = $model->ParamList;

	
	# get model name
	my $model_name = $model->Name;

    # Build output file name
	# ..use prefix if defined, otherwise use model name
	my $prefix = defined $params->{prefix} ? $model->getOutputPrefix( $params->{prefix} ) : $model->getOutputPrefix();
	# ..add suffix, if any
	my $suffix = ( defined $params->{suffix} ) ? $params->{suffix} : undef;
	if ( $suffix )
	{   $prefix .= "_${suffix}";   }

    # split prefix into volume, path and filebase
    my ($vol, $path, $filebase) = File::Spec->splitpath($prefix);
    
	# define m-script file name
    my $mscript_filebase = "${filebase}";
    my $mscript_filename = "${mscript_filebase}.m";
	my $mscript_path     = File::Spec->catpath($vol,$path,$mscript_filename);
    my $mscript_filebase_caps = uc $mscript_filebase;

    # configure options (see Matlab documentation of functions ODESET and ODE15S)
    my $odeset_abstol = 1e-4;
    if ( exists $params->{'atol'} )
    {   $odeset_abstol = $params->{'atol'};  }
    
    my $odeset_reltol = 1e-8;
    if ( exists $params->{'rtol'} )
    {   $odeset_reltol = $params->{'rtol'};  } 

    my $odeset_stats = 'off';
    if ( exists $params->{'stats'} )
    {   $odeset_stats = $params->{'stats'};  } 

    my $odeset_bdf = 'off';
    if ( exists $params->{'bdf'} )
    {   $odeset_bdf = $params->{'bdf'};  }

    my $odeset_maxorder = 5;
    if ( exists $params->{'maxOrder'} )
    {   $odeset_maxorder = $params->{'maxOrder'};  } 

    # time options for mscript
    my $t_start = 0;
    if ( exists $params->{'t_start'} )
    {   $t_start = $params->{'t_start'};  }  

    my $t_end = 10;
    if ( exists $params->{'t_end'} )
    {   $t_end = $params->{'t_end'};  } 

    my $n_steps = 20;
    if ( exists $params->{'n_steps'} )
    {   $n_steps = $params->{'n_steps'};  } 

    # configure time step dependent options
    my $odeset_maxstep = undef;
    if ( exists $params->{'max_step'} )
    {   $odeset_maxstep = $params->{'max_step'};  }     
    
    # construct ODESET function call
    my $mscript_call_odeset;
    if ( defined $odeset_maxstep )
    {
        $mscript_call_odeset = "opts = odeset( 'RelTol',   $odeset_reltol,   ...\n"
                              ."               'AbsTol',   $odeset_abstol,   ...\n"
                              ."               'Stats',    '$odeset_stats',  ...\n"
                              ."               'BDF',      '$odeset_bdf',    ...\n"
                              ."               'MaxOrder', $odeset_maxorder, ...\n"
                              ."               'MaxStep',  $odeset_maxstep    );\n";
    }
    else
    {
        $mscript_call_odeset = "opts = odeset( 'RelTol',   $odeset_reltol,   ...\n"
                              ."               'AbsTol',   $odeset_abstol,   ...\n"
                              ."               'Stats',    '$odeset_stats',  ...\n"
                              ."               'BDF',      '$odeset_bdf',    ...\n"
                              ."               'MaxOrder', $odeset_maxorder   );\n";    
    }

    # Index parameters associated with Constants, ConstantExpressions and Observables
    ($err) = $plist->indexParams();
    if ($err) { return $err };

    # and retrieve a string of expression definitions
    my $n_parameters = $plist->countType( 'Constant' );
    my $n_expressions = $plist->countType( 'ConstantExpression' ) + $n_parameters;
    (my $calc_expressions_string, $err) = $plist->getMatlabExpressionDefs();    
    if ($err) { return $err };

    # get list of parameter names and defintions for matlab
	my $mscript_param_names;
	my $mscript_param_values;
	($mscript_param_names, $mscript_param_values, $err) = $plist->getMatlabConstantNames();
    if ($err) { return $err };

    # get number of species
    my $n_species = scalar @{$model->SpeciesList->Array};
     
	# retrieve a string of observable definitions
    my $n_observables = scalar @{$model->Observables};
    my $calc_observables_string;
    ($calc_observables_string, $err) = $plist->getMatlabObservableDefs();
    if ($err) { return $err };
    
    # get list of observable names for matlab
	my $mscript_observable_names;
	($mscript_observable_names, $err) = $plist->getMatlabObservableNames();
    if ($err) { return $err };
    
    # Construct user-defined functions
    my $user_fcn_declarations = '';
    my $user_fcn_definitions  = '';
	foreach my $param ( @{ $model->ParamList->Array } )
	{
		if ( $param->Type eq 'Function' )
		{
		    # get reference to the actual Function
		    my $fcn = $param->Ref;
		    
		    # don't write function if it depends on a local observable evaluation (this is useless
		    #   since CVode can't do local evaluations)
		    next if ( $fcn->checkLocalDependency($plist) );
		    		    
		    # get function definition			    
		    my $fcn_defn = $fcn->toMatlabString( $plist, {fcn_mode=>'define', indent=>''} );

		    # add definition to the user_fcn_definitions string
		    $user_fcn_definitions .= $fcn_defn . "\n";
        }
	}
	
    # index reactions
    ($err) = $model->RxnList->updateIndex( $plist );
    if ($err) { return $err };

	# retrieve a string of reaction rate definitions
	my $n_reactions = scalar @{$model->RxnList->Array};
    my $calc_ratelaws_string;
    ($calc_ratelaws_string, $err) = $model->RxnList->getMatlabRateDefs( $plist );
    if ($err) { return $err };
    

    # get stoichiometry matrix (sparse encoding in a hashmap)
	my $stoich_hash = {};
	($err) = $model->RxnList->calcStoichMatrix( $stoich_hash );

	# retrieve a string of species deriv definitions
    my $calc_derivs_string;
    ($calc_derivs_string, $err) = $model->SpeciesList->toMatlabString( $model->RxnList, $stoich_hash, $plist );
    if ($err) { return $err };   	


    # get list of species names and initial value expressions for matlab
	my $mscript_species_names;
	my $mscript_species_init;
	($mscript_species_names, $mscript_species_init, $err) = $model->SpeciesList->getMatlabSpeciesNames( $model );
    if ($err) { return $err }; 


    ## Set up MATLAB Plot
    # fontsizes
    my $title_fontsize = 14;
    my $axislabel_fontsize = 12;
    my $legend_fontsize = 10;

    # generate code snippets for plotting observables or species
    my $mscript_plot_labels;
    my $mscript_make_plot;

    # get ylabel (either Number of Concentration)
    my $ylabel;
    if ( $model->SubstanceUnits eq 'Number' )
    {   $ylabel = 'number';   }
    elsif ( $model->SubstanceUnits eq 'Concentration' )
    {   $ylabel = 'concentration';   }
    else
    {   $ylabel = 'number or concentration';   }

    
    if ( @{$model->Observables} )
    {   # plot observables
        $mscript_plot_labels = "    observable_labels = { $mscript_observable_names };\n";
        
        $mscript_make_plot = "    plot(timepoints,observables_out);\n"
                            ."    title('${mscript_filebase} observables','fontSize',${title_fontsize},'Interpreter','none');\n"
                            ."    axis([${t_start} timepoints(end) 0 inf]);\n"
                            ."    legend(observable_labels,'fontSize',${legend_fontsize},'Interpreter','none');\n"
                            ."    xlabel('time','fontSize',${axislabel_fontsize},'Interpreter','none');\n"
                            ."    ylabel('${ylabel}','fontSize',${axislabel_fontsize},'Interpreter','none');\n";
    
    }
    else
    {   # plot species
        $mscript_plot_labels = "    species_labels = { $mscript_species_names };\n";
    
        $mscript_make_plot = "    plot(timepoints,species_out);\n"
                            ."    title('${mscript_filebase} species','fontSize',${title_fontsize},'Interpreter','none');\n"
                            ."    axis([${t_start} timepoints(end) 0 inf]);\n"
                            ."    legend(species_labels,'fontSize',${legend_fontsize},'Interpreter','none');\n"
                            ."    xlabel('time','fontSize',${axislabel_fontsize},'Interpreter','none');\n"
                            ."    ylabel('${ylabel}','fontSize',${axislabel_fontsize},'Interpreter','none');\n";
    }
    


    # open Mfile and begin printing...
	open( Mscript, ">$mscript_path" ) || die "Couldn't open $mscript_path: $!\n";
    print Mscript <<"EOF";
function [err, timepoints, species_out, observables_out ] = ${mscript_filebase}( timepoints, species_init, parameters, suppress_plot )
%${mscript_filebase_caps} Integrate reaction network and plot observables.
%   Integrates the reaction network corresponding to the BioNetGen model
%   '${model_name}' and then (optionally) plots the observable trajectories,
%   or species trajectories if no observables are defined. Trajectories are
%   generated using either default or user-defined parameters and initial
%   species values. Integration is performed by the MATLAB stiff solver
%   'ode15s'. ${mscript_filebase_caps} returns an error value, a vector of timepoints,
%   species trajectories, and observable trajectories.
%   
%   [err, timepoints, species_out, observables_out]
%        = $mscript_filebase( timepoints, species_init, parameters, suppress_plot )
%
%   INPUTS:
%   -------
%   species_init    : row vector of $n_species initial species populations.
%   timepoints      : column vector of time points returned by integrator.
%   parameters      : row vector of $n_parameters model parameters.
%   suppress_plot   : 0 if a plot is desired (default), 1 if plot is suppressed.
%
%   Note: to specify default value for an input argument, pass the empty array.
%
%   OUTPUTS:
%   --------
%   err             : 0 if the integrator exits without error, non-zero otherwise.
%   timepoints      : a row vector of timepoints returned by the integrator.
%   species_out     : array of species population trajectories
%                        (columns correspond to species, rows correspond to time).
%   observables_out : array of observable trajectories
%                        (columns correspond to observables, rows correspond to time).
%
%   QUESTIONS about the BNG Mfile generator?  Email justinshogg\@gmail.com



%% Process input arguments

% define any missing arguments
if ( nargin < 1 )
    timepoints = [];
end

if ( nargin < 2 )
    species_init = [];
end

if ( nargin < 3 )
    parameters = [];
end

if ( nargin < 4 )
    suppress_plot = 0;
end


% initialize outputs (to avoid error msgs if script terminates early
err = 0;
species_out     = [];
observables_out = [];


% setup default parameters, if necessary
if ( isempty(parameters) )
   parameters = [ $mscript_param_values ];
end
% check that parameters has proper dimensions
if (  size(parameters,1) ~= 1  ||  size(parameters,2) ~= $n_parameters  )
    fprintf( 1, 'Error: size of parameter argument is invalid! Correct size = [1 $n_parameters].\\n' );
    err = 1;
    return;
end

% setup default initial values, if necessary
if ( isempty(species_init) )
   species_init = initialize_species( parameters );
end
% check that species_init has proper dimensions
if (  size(species_init,1) ~= 1  ||  size(species_init,2) ~= $n_species  )
    fprintf( 1, 'Error: size of species_init argument is invalid! Correct size = [1 $n_species].\\n' );
    err = 1;
    return;
end

% setup default timepoints, if necessary
if ( isempty(timepoints) )
   timepoints = linspace($t_start,$t_end,$n_steps+1)';
end
% check that timepoints has proper dimensions
if (  size(timepoints,1) < 2  ||  size(timepoints,2) ~= 1  )
    fprintf( 1, 'Error: size of timepoints argument is invalid! Correct size = [t 1], t>1.\\n' );
    err = 1;
    return;
end

% setup default suppress_plot, if necessary
if ( isempty(suppress_plot) )
   suppress_plot = 0;
end
% check that suppress_plot has proper dimensions
if ( size(suppress_plot,1) ~= 1  ||  size(suppress_plot,2) ~= 1 )
    fprintf( 1, 'Error: suppress_plots argument should be a scalar!\\n' );
    err = 1;
    return;
end

% define parameter labels (this is for the user's reference!)
param_labels = { $mscript_param_names };



%% Integrate Network Model
 
% calculate expressions
[expressions] = calc_expressions( parameters );

% set ODE integrator options
$mscript_call_odeset

% define derivative function
rhs_fcn = @(t,y)( calc_species_deriv( t, y, expressions ) );

% simulate model system (stiff integrator)
try 
    [timepoints, species_out] = ode15s( rhs_fcn, timepoints, species_init', opts );
catch
    err = 1;
    fprintf( 1, 'Error: some problem encounteredwhile integrating ODE network!\\n' );
    return;
end

% calculate observables
observables_out = zeros( length(timepoints), $n_observables );
for t = 1 : length(timepoints)
    observables_out(t,:) = calc_observables( species_out(t,:), expressions );
end


%% Plot Output, if desired

if ( ~suppress_plot )
    
    % define plot labels
$mscript_plot_labels
    % construct figure
$mscript_make_plot
end


%~~~~~~~~~~~~~~~~~~~~~%
% END of main script! %
%~~~~~~~~~~~~~~~~~~~~~%


% initialize species function
function [species_init] = initialize_species( params )

    species_init = zeros(1,$n_species);
$mscript_species_init
end


% user-defined functions
$user_fcn_definitions


% Calculate expressions
function [ expressions ] = calc_expressions ( parameters )

    expressions = zeros(1,$n_expressions);
$calc_expressions_string   
end



% Calculate observables
function [ observables ] = calc_observables ( species, expressions )

    observables = zeros(1,$n_observables);
$calc_observables_string
end


% Calculate ratelaws
function [ ratelaws ] = calc_ratelaws ( species, expressions, observables )

    ratelaws = zeros(1,$n_observables);
$calc_ratelaws_string
end

% Calculate species derivatives
function [ Dspecies ] = calc_species_deriv ( time, species, expressions )
    
    % initialize derivative vector
    Dspecies = zeros($n_species,1);
    
    % update observables
    [ observables ] = calc_observables( species, expressions );
    
    % update ratelaws
    [ ratelaws ] = calc_ratelaws( species, expressions, observables );
                        
    % calculate derivatives
$calc_derivs_string
end


end
EOF

	close(Mscript);
	print "Wrote M-file script $mscript_path.\n";
	
# define m-script file name
     $mscript_filebase = "initSpecies_${filebase}";
     $mscript_filename = "${mscript_filebase}.m";
	 $mscript_path     = File::Spec->catpath($vol,$path,$mscript_filename);
     $mscript_filebase_caps = uc $mscript_filebase;
	 
	 open( Mscript, ">$mscript_path" ) || die "Couldn't open $mscript_path: $!\n";
	print Mscript <<"EOF";
	function [species_init] = initialize_species( params )

    species_init = zeros(1,$n_species);
$mscript_species_init
end
	
EOF

	close(Mscript);
	print "Wrote M-file script $mscript_path.\n";

	$mscript_filebase = "bngModel_${filebase}";
     $mscript_filename = "${mscript_filebase}.m";
	 $mscript_path     = File::Spec->catpath($vol,$path,$mscript_filename);
     $mscript_filebase_caps = uc $mscript_filebase;
	
	 open( Mscript, ">$mscript_path" ) || die "Couldn't open $mscript_path: $!\n";
	print Mscript <<"EOF";
classdef ${mscript_filebase} < bngModel

	properties 
	end
	
	methods
	
	function obj = ${mscript_filebase}
	obj = obj\@bngModel;
	
	species_labels = { $mscript_species_names };
	param_labels =  { $mscript_param_names };
	param_defaults =  [ $mscript_param_values ] ;
	obs_labels = { $mscript_observable_names };
	simulators = { \'$filebase\' };
	
	obj.Param = bngParam(param_labels,param_defaults);
    obj.Obs = bngObs(obs_labels);
    obj.Species = bngSpecies(species_labels,\'initSpecies_${filebase}\');
    obj.simulators = simulators;
	
	end
	
	end


end
	
EOF

	close(Mscript);
	print "Wrote M-file script $mscript_path.\n";

return();
}

###
###
###

sub visualize
{
	use Visualization::Viz;
	use Visualization::VisOptParse;

    my $model       = shift @_;
    my $user_params = @_ ? shift @_ : {};
	
    # default options
    my $params =
    {   'help'         =>  0,
    		'type'         =>  'regulatory',
    		'opts'         =>  undef,
    		'background'   =>  0,
    		'collapse'     =>  0,
    		'each'         =>  0,
    		'groups'       =>  0,
    		'textonly'     =>  0,
    		'suffix'       =>  '',
    		'filter'       =>  0,
    		'level'        =>  1,
    		'mergepairs'   =>  0,
    		'embed'        =>  0,
			'reset'		   =>  1,
			'ruleNames'	   =>  0,
			'doNotUseContextWhenGrouping' => 0,
			'removeReactantContext' => 0,
			'makeInhibitionEdges' => 0,
			'removeProcessNodes' => 0,
			'compressRuleMotifs' => 0,
			'doNotCollapseEdges' => 0,
    };
    
    # get user options
    foreach my $par (keys %$user_params)
    {
        my $val = $user_params->{$par};
        #unless ( exists $params->{$par} )
        #{   return "Unrecognized option $par in call to 'visualize'";   }
		
		# Special handling for 'opts':
		# If a single filename is passed, convert $val to an array
		if ($par eq 'opts'){
			if (not ref($val)){
				$val = [$val];
			}
		}
        
        # overwrite default option
        $params->{$par} = $val;
    }
    
    if($params->{'help'}) 
	{
		defined $user_params->{'type'} ? Viz::display_viz_help(\%$params) : Viz::display_viz_help();
		return '';
	}
    
	my $err = Viz::execute_params($model, Viz::getExecParams(\%$params));
	if ($err) { return $err; }
	return '';
}

1;
