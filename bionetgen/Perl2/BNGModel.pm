package BNGModel;

#    BioNetGen : rule-based modeling language and simulation platform
#
#                  Copyright (C) 2006,2009,2012,2014 by
#
#      James R. Faeder    (faeder at pitt dot edu)
#      Justin S. Hogg     (justinshogg at gmail dot com)
#      Leonard A. Harris  (lh64 at cornell dot com)
#      John A. P. Sekar	  (johnarul dot sekar at gmail dot com)
#      Jose Juan Tapia    (jjtapia at gmail dot com)
#      Arshi Arora
#      Dipak Barua
#
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.


# pragmas
use strict;
use warnings;
no warnings 'redefine';

# Perl Modules
use Class::Struct;
use FindBin;
use lib $FindBin::Bin;
use File::Spec;
use File::Spec::Win32;
use POSIX ("floor", "ceil");
use Scalar::Util ("looks_like_number");
use Config;

use Cwd;

# BNGOutput contains BNGModel methods related to third-party output
#  e.g. writeXML, writeSBML, writeMfile, writeMexfile, toSSC...
#  Note that .NET and .BNGL writer methods are contained in THIS file.
use BNGOutput;

# BNGAction contains BNGModel action methods
#  e.g. simulate, simulate_pla, simulate_nf, parameter_scan, generate_hybrid_model...
#  Note that some core actions are contained here: generate_network, setParameter, etc.
use BNGAction;

# BNG Modules
use Cache;
use BNGUtils;
use MoleculeTypesList;
use ParamList;
use Function;
use Compartment;
use CompartmentList;
use SpeciesList;
use RxnRule;
use EnergyPattern;
use Observable;
use PopulationList;

#Variables that determine whether the network has been generated
my $NetFlag = 0; 

# A place to store a reference to the current active model.
# Useful when other classes need to find the model.
$BNGModel::GLOBAL_MODEL = undef;

# Global package variables
my $NO_EXEC = 0;  # Prevents execution of functions to allow file syntax checking
my $HAVE_PS = 0;  # Set to 0 for MS Windows systems with no ps command - disables
                  #  reporting of memory usage at each iteration of network generation

# Structure containing BioNetGen model data
struct BNGModel =>
{
    Name                => '$',
    Time                => '$',
    Concentrations      => '@',
    MoleculeTypesList   => 'MoleculeTypesList',
    SpeciesList         => 'SpeciesList',
    SeedSpeciesList     => 'SpeciesList',
    RxnList             => 'RxnList',
    RxnRules            => '@',
    ParamList           => 'ParamList',
    Observables         => '@',
    EnergyPatterns      => '@',                  # for energy BNG only: Holds a list of energy patterns
    CompartmentList     => 'CompartmentList',    # list of reaction compartments (volumes and surfaces)
    PopulationTypesList => 'MoleculeTypesList',  # list of population molecule types
    PopulationList      => 'PopulationList',     # list of population species
    SubstanceUnits      => '$',
    UpdateNet           => '$',  # This variable is set to force update of NET file before simulation.
    Version             => '$',  # Indicates set of version requirements- output to BNGL and NET files
    Options             => '%',  # Options used to control behavior of model and associated methods
    Params              => '%',  # run-time parameters (not to be saved)
    ParameterCache      => 'Cache',   
    ConcentrationCache  => 'Cache',
	VizGraphs			=> '$',
};



###
###
###



# Initialize BNGModel data structures
sub initialize
{
    my $model = shift @_;

    $model->Name('');
    $model->Version('');
    $model->Time(0);
    $model->UpdateNet(0);
    $model->ParamList( ParamList->new() );
    $model->MoleculeTypesList( MoleculeTypesList->new('StrictTyping'=>0) );
    $model->PopulationTypesList( MoleculeTypesList->new('StrictTyping'=>0) );
    $model->PopulationList( PopulationList->new() );
    $model->CompartmentList( CompartmentList->new('Used'=>0) );
    $model->SpeciesList( SpeciesList->new() );
    $model->RxnList( RxnList->new('SpeciesList'=>$model->SpeciesList) );
    $model->SubstanceUnits('');
    $model->ConcentrationCache( Cache->new() );
    $model->ParameterCache( Cache->new() );
}


# read Model from file
# $err = $model->readModel({file=>FILENAME}) 
sub readModel
{
    my $model = shift @_;
    my $user_args = @_ ? shift @_ : {};

    my %args = ();
    # copy user_params into pass_params structures
    while ( my ($key,$val) = each %$user_args )
    {   $args{$key} = $val;    }

    # writeFile will generate the output
    return $model->readFile( \%args );
}


# read Network from file
# $err = $model->readModel({file=>FILENAME}) 
sub readNetwork
{
    my $model = shift @_;
    my $user_args = @_ ? shift @_ : {};

    my %args = ();
    # copy user_params into pass_params structures
    while ( my ($key,$val) = each %$user_args )
    {   $args{$key} = $val;    }

    # writeFile will generate the output
    return $model->readFile( \%args );
}

sub readSBML
{
	my $model  = shift @_;
	my $filepath = shift @_;
	unless ( -e $filepath )
    {   return 1, "Could not find '$filepath'";   }
	my ($vol, $dir, $filename) = File::Spec->splitpath( $filepath );
	$filename =~ s/\.xml//;
	my $outfile = File::Spec->catpath('', $model->getOutputDir(), $filename.'.bngl');
    my $user_args = @_ ? shift @_ : {};
    
    # Collect user arguments
	my %args = ();
    while ( my ($key,$val) = each %$user_args )
    {   
    		$args{$key} = $val;
#    	printf "$key=>$val\n";
    }
    
    # Find program and save path to directory
    my $program;
    unless ( $program = findExec("sbmlTranslator") )
    {   return 1, "Could not find executable 'sbmlTranslator'";   }
    ($vol, $dir, my $bin) = File::Spec->splitpath( $program );
	my $bindir = File::Spec->catpath($vol, $dir);

    # Begin writing command: start with 'program'
    my $cmd = $program;
	$cmd .= ' -i "' . $filepath .'"';
	$cmd .= ' -o "' . $outfile . '"';
	if ($args{"atomize"}){
		$cmd .= ' -a';
	}
	
	# Run the translator
	printf "SBML translation: $cmd\n";
	system($cmd);
	
	# Return full path to generated BNGL file
	return 0, $outfile
}


# Read bionetgen data in blocks enclosed by begin param end param
# lines.  Prevents overwriting of variables possible with eval.
#
# To do:
# 1.  Receive a valid list of parameter names to be read
# 2.  Check syntax of lines- this is currently done when parameter is
#     handled.  Some basic checks could be done here.
#
# Lines between begin and end commands are put into arrays with the name given
#  by the block name
{

    my ($filename, $line_number, $file_data);
    my (@filename_stack, @line_number_stack, @file_data_stack, @params_stack);
    my $level = -1;
    my $MAX_LEVEL = 10;    # Sets maximum level of allowed recursion
    my %bngdata;
    my $t_start;
    my $stdout_handle;

    sub readFile
    {
        # get arguments
        my $model  = shift @_;
        my $params = @_ ? shift @_ : {};

		# supported blocks
		my %blocks = (
			'parameters' => 1,
			'compartments' => 1,
			'molecule types' => 1,
			'species' => 1,
			'seed species' => 1,
			'observables' => 1,
			'functions' => 1,
			'energy patterns' => 1,
			'population types' => 1,
			'population maps' => 1,
			'reaction rules' => 1,
			'reactions' => 1,
			'groups' => 1,
			'actions' => 1,
		);

		# user-specified list of blocks to read
		if (exists $params->{blocks}){
			my %tmp;
			foreach my $block (@{$params->{blocks}}){
				$tmp{$block} = 1;
			}
			foreach (keys %blocks){
				unless (exists $tmp{$_}){
					$blocks{$_} = 0;
				}
			}
		}

        # a place for error messages
        my $err;

        # get the filename
        my $filename = exists $params->{file} ? $params->{file} : undef;
        unless ( defined $filename )
        {   # Filename argument is mandatory
            $err = errgen( "'file' parameter is required for action readFile()" );
            goto EXIT;
        }
        
		# if file path is relative, change Unix path to Windows path, and vice versa,
		# based on OS (improves cross-platform portability --LAH)
		if ( not File::Spec::Win32->file_name_is_absolute( $filename ) ){
			if ($Config{myarchname} =~ /MSWin32/)
			{ $filename =~ s/\//\\/g; }
			else
			{ $filename =~ s/\\/\//g; }
		}

        # increment level
        ++$level;
        if ($level > $MAX_LEVEL)
        {
            $err = errgen( "Recursion level exceeds maximum of $MAX_LEVEL" );
            goto EXIT;
        }

        # Top level stuff
        if ($level==0)
        {   
            # get start time
            $t_start = cpu_time(0);

            # set model update flag
            $model->UpdateNet(1);

            # set model name
            if ($model->Name eq '')
            {
                if ( $params->{basename} )
                {   # set model name to basename
                    $model->Name( $params->{basename} );
                }
                else
                {   # determine model basename from filename
                    my ($vol, $dir, $fn) = File::Spec->splitpath( $filename );
					
                    my $basename;
                    # file = basename.ext
                    if ( $fn =~ /^(.+)\.([^\.]+)$/ )
                    {   $basename = $1;   }
                    # file = basename
                    elsif ( $fn =~ /^([^\.]+)$/ )
                    {   $basename = $1;   }
                    # file = ???
                    else
                    {   $basename = $fn;  }

                    $model->Name($basename);
                }
            }

            # set model parameters
            $model->Params($params);

            # set output directory
            unless ( defined $model->Params->{output_dir} )
            {   # default is current directory
                $model->setOutputDir();
            } 

            # set up log file, if needed
            if ( $model->Params->{logging} )
            {  
                # generate logfile name
                my $logfilename = $model->getOutputPrefix() . ".log" ;
                # remember where to find STDOUT
                unless( open $stdout_handle, ">&STDOUT" )
                {  $err = "Problem finding handle for STDOUT: $!";  goto EXIT;  }       
                # redirect STDOUT to logfile
                unless ( open STDOUT, '>', $logfilename )
                {  $err = "Problem redirecting STDOUT to logfile: $!";;  goto EXIT;  }
            }

            # turn off output buffering on STDOUT
            select STDOUT;  $| = 1;

            # Say Hello to the user
            printf "BioNetGen version %s\n", BNGversion();   
        }
        elsif ($level > 0)
        {
            # save state of previous level on stacks
            push @filename_stack, $filename;
            push @file_data_stack, $file_data;
            push @line_number_stack, $line_number;
            push @params_stack, $model->Params;

            # inherit params from previous level
            $model->Params( {%{$model->Params}} );
            # overwrite any params that were explicitly changed
            while ( my ($opt,$val) = each %$params )
            {  $model->Params->{$opt} = $val;  }
        }

        # SBML translator
		if ( $filename =~ /\.xml$/ )
		{
            if ( $model->Params->{no_atomizer} )
            {
                send_warning( "readFile(): BNG processing was halted. Attempted to import XML file with 'no-atomizer' flag activated.");
                exit(0);
            }
			my $out;
			($err, $out) = $model->readSBML($filename,$model->Params);
			if ($err){
				$err = $out;
				goto EXIT;
			}
			$filename = $out
			# Generated BNGL file will now be read in below
		}
		
		# Read BNGL or NET file
		if ( $filename =~ /\.bngl$/ || $filename =~ /\.net$/ )
		{
	        # Read BNG model data        
	        print "Reading from file $filename (level $level)\n";
	        unless( open FILE, '<', $filename )
	        {   
	        		unless (File::Spec->file_name_is_absolute( $filename )){
	        			$filename = File::Spec->rel2abs( $filename );
	        		}
	            $err = "Couldn't read from file $filename: $!";
	            goto EXIT;
	        }
	        # read all lines of the file into an array at $file_data
	        $file_data = [<FILE>];
	        # close file
	        close FILE;
	
	
	        # Read data from file into data hash
	        $line_number = 0;
	        my $begin_model = 0;
	        my $in_model    = 1;
	        while ( my $string = get_line() )
	        {
	            # chop leading spaces
	            $string =~ s/^\s+//;
	
	            if ( $string =~ /^begin\s+model\s*$/ )
	            {
	                ++$begin_model;
	                if ( $begin_model > 1 )
	                {
	                    $err = errgen("Only one model definition allowed per file");
	                    goto EXIT;
	                }
	                $in_model = 1;
	                next;
	            }
	            elsif ( $string =~ /^end\s+model\s*$/ )
	            {
	                unless ($in_model)
	                {
	                    $err = errgen("end model encountered without enclosing begin model");
	                    goto EXIT;
	                }
	                $in_model = 0;
	                next;
	            }
	
	            # Process multi-line block
	            if ( $string =~ s/^begin\s*// )
	            {
	                # get block name
	                my $name = $string;
	                # Remove trailing white space
	                $name =~ s/\s*$//;
	                # Remove repeated white space
	                $name =~ s/\s+/ /g;
	
	                unless ($in_model or ($name eq 'actions'))
	                {
	                    $err = errgen("$name cannot be defined outside of a model");
	                    goto EXIT;
	                }
	
	                # Read block data
	                my $block_dat;
	                ( $block_dat, $err ) = read_block_array($name);
	                if ($err) {  goto EXIT;  }
#	                $bngdata{$name} = 1;
	                
	                # Move on if block has been suppressed by the user
	                # (if the block name is not recognized, continue so an error will be thrown)
					if (exists $blocks{$name} and $blocks{$name}==0) { next; }
	
	                ### Read Parameters Block
	                if ( $name eq 'parameters' )
	                {
	                    # Read model parameters
	                    my $plast = $model->ParamList->getNumParams();
	                    my ($entry, $lno);
	                    foreach my $line ( @$block_dat )
	                    {
	                        ($entry, $lno) = @$line;
	                        $err = $model->ParamList->readString($entry);
	                        if ($err) {  $err = errgen( $err, $lno );  goto EXIT;  }
	                    }
	                    # check parameter list
	                    if ( $err = $model->ParamList->check() )
	                    {
	                        $err = errgen( $err, $lno );
	                        goto EXIT;
	                    }
	                    # sort parameters
	                    if ( $err = $model->ParamList->sort() )    
	                    {
	                        $err = errgen( $err, $lno );
	                        goto EXIT;
	                    }
	                    # update user
	                    printf "Read %d $name.\n", $model->ParamList->getNumParams() - $plast;
	                }
	
	                    
	                ### Read Functions Block
	                elsif ( $name eq 'functions' )
	                {
	                    # Model functions
	                    my $nread = 0;
	                    my ($entry, $lno);
	                    foreach my $line ( @$block_dat )
	                    {
	                        ($entry, $lno) = @{$line};
	                        my $fun = Function->new();
	                        $err = $fun->readString( $entry, $model );
	                        if ($err) {  $err = errgen( $err, $lno );  goto EXIT;  }
	                        ++$nread;
	                    }
	                    
	                    # check paramlist for unresolved dependency, etc
	                    #   GIVE warning here, don't terminate!
	                    if ( $err = $model->ParamList->check() )
	                    {
	                        $err = errgen( $err, $lno );
	                        print "Warning: $err\n"
	                             ."  (if parameter is defined in a subsequent block,\n"
	                             ."  then this warning can be safely ignored.)\n";
	                    }                            
	                    # update user
	                    printf "Read %d ${name}.\n", $nread;
	                }
	                
	    
	                ### Read Molecule Types block
	                elsif ( $name eq 'molecule types' )
	                {
	                    # read MoleculeTypes
	                    $model->MoleculeTypesList->StrictTyping(1);
	                    foreach my $line ( @$block_dat )
	                    {
	                        my ( $entry, $lno ) = @$line;
	                        $err = $model->MoleculeTypesList->readString($entry);
	                        if ($err) {  $err = errgen( $err, $lno );  goto EXIT;  }
	                    }
	                    # update user
	                    printf "Read %d molecule types.\n", $model->MoleculeTypesList->getNumMolTypes();
	                }
	
	
	                ### Read Population Types block
	                elsif ( $name eq 'population types' )
	                {
	                    # read PopulationTypes
	                    $model->PopulationTypesList->StrictTyping(1);
	                    foreach my $line ( @$block_dat )
	                    {
	                        my ( $entry, $lno ) = @$line;
	                        $err = $model->PopulationTypesList->readString($entry);
	                        if ($err) {  $err = errgen( $err, $lno );  goto EXIT;  }
	                    }
	                    # update user
	                    printf "Read %d population types.\n", $model->PopulationTypesList->getNumMolTypes();
	                }
	
	
	                ### Read Population Maps block
	                elsif ( $name eq 'population maps' )
	                {
	                    unless ( $model->MoleculeTypesList->StrictTyping )
	                    {
	                        $err = errgen("A $name block cannot be defined unless molecule types are defined explicitly");
	                        goto EXIT;        
	                    }
	                    # read Population Maps
	                    foreach my $line ( @$block_dat )
	                    {
	                        my ($entry, $lno) = @$line;
	                        $err = $model->PopulationList->readString($entry,$model);
	                        if ($err) {  $err = errgen( $err, $lno );  goto EXIT;  }
	                    }
	                    # update user
	                    printf "Read %d population maps.\n", $model->PopulationList->getNumPopulations;
	                }
	                
	                    
	                ### Read Compartments Block
	                elsif ( $name eq 'compartments' )
	                {	
	                    # Read Compartments
	                    my ($entry, $lno);
	                    foreach my $line ( @$block_dat )
	                    {
	                        ($entry, $lno) = @$line;
	                        $err = $model->CompartmentList->readString( $entry, $model->ParamList );
	                        if ($err) {  $err = errgen( $err, $lno );  goto EXIT;  }
	                    }
	                    # validate compartments
	                    if ( $err = $model->CompartmentList->validate() )
	                    {
	                        $err = errgen( $err, $lno );
	                        goto EXIT;
	                    }
	                    if ($model->CompartmentList->getNumCompartments() > 0){
	                    		# set flag to indicate compartments are being used
	            				$model->CompartmentList->Used(1);
	            				# update user
	            				printf "Read %d compartments.\n", $model->CompartmentList->getNumCompartments;
	                    }
	                }
	                
	                    
	                ### Read Species/Seed Species Block
	                elsif ( ($name eq 'species') or ($name eq 'seed species') )
	                {
	                    # read Species
	                    foreach my $line ( @$block_dat )
	                    {
	                        my ($entry, $lno) = @$line;
	                        $err = $model->SpeciesList->readString( $entry, $model->ParamList,
	                                                                $model->CompartmentList,
	                                                                $model->MoleculeTypesList );
	                        if ($err) {  $err = errgen( $err, $lno );  goto EXIT;  }                            
	                    }
	                    # update user
	                    printf "Read %d species.\n", $model->SpeciesList->getNumSpecies();
	                }
	                
	
	                ### Read Reaction Rules Block
	                elsif ( $name eq 'reaction rules' )
	                {
	                		# Read reaction rules
	                		my $nerr = 0;
#						my $rrules = [];
#	                    	$model->RxnRules( $rrules );
	                    	my $rrules = $model->RxnRules;
	                    	my $counter = 1;
	                    	foreach my $line ( @$block_dat )
	                    	{
	                    		my ($entry, $lno) = @$line;
	                        	# create new rule
	                        	(my $rrs, $err) = RxnRule::newRxnRule( $entry, $model, $lno );
	                        	if ($err)
	                        	{   # some error encountered
	                        		$err = errgen( $err, $lno );
	                            	printf "ERROR: $err\n";
	                            	++$nerr;
	                        	}
	                        	# check rule name (if given)
	                        	elsif ( $rrs->[0]->Name ){
	                            	foreach my $r (@$rrules){ # loop over all existing rules
	                            		foreach my $x (@$r){  # consider forward and reverse (if exists) for existing rule (just to be safe)
	                                		if ( $rrs->[0]->Name eq $x->Name ){ # duplicate rule name found
											$err = "Duplicate rule name detected (\"" . $rrs->[0]->Name . "\").";
											$err = errgen( $err, $lno );
											printf "ERROR: $err\n";
											++$nerr;
											last;
										}
	                            		}
								}
	                        }
	                        unless ($err)
	                        {
	                        		# rule is ok
	                            push @$rrules, $rrs;

	                            # give names, if not defined
	                            unless ( $rrs->[0]->Name )
	                            {   
									#$rrs->[0]->Name( 'Rule' . scalar @$rrules );
									my $rname = '_R' . $counter++;
									# avoid duplicate names (just to be safe)
									for (my $i=0; $i < @$rrules-1; $i++){
										if ($rname eq @$rrules[$i]->[0]->Name){ # duplicate rule name
											$rname = '_R' . $counter++;
											$i = -1; # start over
										}
									}
									$rrs->[0]->Name( $rname );
	                            }
	                            if ( @$rrs > 1 )
	                            {
	                                unless ($rrs->[1]->Name)
	                                {   
										#$rrs->[1]->Name( 'Rule' . scalar @$rrules . 'r' );
										$rrs->[1]->Name( '_reverse_' . $rrs->[0]->Name);
									}
	                            }
	                        }
		                    if ($nerr)
		                    {
		                        $err = "Reaction rule list could not be read because of errors.";
		                        goto EXIT;
		                    }
	                    	}
	                    # update user
	                    printf "Read %d reaction rule(s).\n", scalar @{$model->RxnRules};
	                }
	                
	
	                ### Read Reactions Block
	                elsif ( $name eq 'reactions' )
	                {
	                    # Reactions (when reading NET file)
	                    my $rlist = RxnList->new;
	                    foreach my $line ( @{$block_dat} )
	                    {
	                        my ( $entry, $lno ) = @{$line};
	                        $err = $rlist->readString( $entry,
	                                                   $model->SpeciesList,
	                                                   $model->ParamList    );
	                        if ($err) {  $err = errgen( $err, $lno );  goto EXIT;  }
	                    }
	                    printf "Read %d reaction(s).\n", scalar( @{$block_dat} );
	                    $model->RxnList($rlist);
	                }
	
	
	                ### Read Groups Block
	                elsif ( $name eq 'groups' )
	                {
	                	my $iobs = 0;
	                    if ( @{$model->Observables} )
	                    {   # Associate groups with exisiting observables
	                        # my $iobs   = 0;
	                        foreach my $line ( @$block_dat )
	                        {
	                            my ($entry, $lno) = @$line;
	                    
	                            # split into tokens (note: using ' ' is different than / /, see perlfunc)
	                            my @tokens = split ' ', $entry;
	
	                            # Skip first entry if it's an index
	                            if ( $tokens[0] =~ /^\d+$/ ) {  shift @tokens;  }
	     
	                            if ( $iobs >= @{$model->Observables} )
	                            {   # more groups than observables!
	                                $err = errgen( "More groups than observables", $lno );
	                                goto EXIT;
	                            }
	
	                            # get observable
	                            my $obs = $model->Observables->[$iobs];
	
	                            # Check that Observable and Group names match
	                            my $group_name = @tokens ? shift @tokens : '';
	                            unless ( $group_name eq $obs->Name )
	                            {
	                                $err = errgen("Group named '$tokens[0]' is not compatible with any observable", $lno );
	                                goto EXIT;
	                            }
	
	                            # get group weights
	                            my @group_weights = split (/,/, $tokens[0]);
	
	                            # Zero the weights (TODO..)
	                            @{$obs->Weights} = (0) x scalar @{$obs->Weights};
	                            my ($weight, $species_idx);
	                            foreach my $component (@group_weights)
	                            {
	                                if ( $component =~ m/^(\d+)\*(\d+)$/ )
	                                {
	                                    $weight = $1;
	                                    $species_idx = $2;
	                                }
	                                elsif ( $component =~ m/^(\d+)$/ )
	                                {
	                                    $weight = 1;
	                                    $species_idx = $1;
	                                }
	                                else
	                                {
	                                    $err = errgen( "Invalid group entry: $component", $lno );
	                                    goto EXIT;
	                                }
	                                $obs->Weights->[$species_idx] += $weight;
	                            }
	                            ++$iobs;
	                        }
	                    }
	                    else
	                    {   # create a dummy observable for each group
	                        send_warning("Found 'groups' block before 'observables': creating observables.");
	
	                        # get the number of species
	                        my $n_species = $model->SpeciesList->getNumSpecies();
	
	                        #my $iobs   = 0;
	                        foreach my $line ( @$block_dat )
	                        {
	                            my ($entry, $lno) = @$line;
	                    
	                            # split into tokens (note: using ' ' is different than / /, see perlfunc)
	                            my @tokens = split ' ', $entry;
	
	                            # Skip first entry if it's an index
	                            if ( $tokens[0] =~ /^\d+$/ ) {  shift @tokens;  }
	
	                            # Group name is next token
	                            my $group_name = @tokens ? shift @tokens : '';
	                            unless ( $group_name =~ /^\w+$/ )
	                            {
	                                $err = errgen("Invalid group name '$group_name'", $lno );
	                                goto EXIT;
	                            }                        
	
	                            # create dummy observable
	                            my $obs = Observable->new( Name=>$group_name, Patterns=>[], Weights=>[], Type=>"Molecule", Output=>1 );
	                            push @{$model->Observables}, $obs;
	
	                            # Add paramter to observable list
	                            if ( $model->ParamList->set( $obs->Name, "0", 1, "Observable", $obs) )
	                            {
	                          	    my $name = $obs->Name;
	                                $err = errgen( "Observable name $name matches previously defined Observable or Parameter", $lno );
	                                goto EXIT;
	                            }
	
	                            # get group weights
	                            my @group_weights = ($tokens[0]) ? split( /,/ , $tokens[0] ) : ();
	                            
	                            # Zero the weights
	                            @{$obs->Weights} = (0) x ($n_species+1);
	                            my ($weight, $species_idx);
	                            foreach my $component (@group_weights)
	                            {
	                                if ( $component =~ /^(\d+)\*(\d+)$/ )
	                                {
	                                    $weight = $1;
	                                    $species_idx = $2;
	                                }
	                                elsif ( $component =~ /^(\d+)$/ )
	                                {
	                                    $weight = 1;
	                                    $species_idx = $1;
	                                }
	                                else
	                                {
	                                    $err = errgen( "Invalid group entry: $component", $lno );
	                                    goto EXIT;
	                                }
	                                $obs->Weights->[$species_idx] += $weight;
	                            }
	                            ++$iobs;
	                        }
	                    }
	                    # update user
	                    printf "Read %d group(s).\n", $iobs;
	                }
	
	
	                ### Read Observables Block
	                elsif ( $name eq 'observables' )
	                {
	                    # Read observables
	                    my ($entry, $lno );
	                    foreach my $line ( @$block_dat )
	                    {
	                        ($entry, $lno ) = @$line;
	                        my $obs = Observable->new();
	                        $err = $obs->readString($entry, $model);
	                        if ($err)
	                        {
	                            $err = errgen( $err, $lno );
	                            goto EXIT;
	                        }
	                        push @{$model->Observables}, $obs;
	                    }    
	
	                    # check paramlist for unresolved dependency, etc
	                    #   GIVE warning here, don't terminate!                    
	                    if ( $err = $model->ParamList->check() )
	                    {
	                        $err = errgen( $err, $lno );
	                        print "Warning: $err\n"
	                             ."  (if parameter is defined in a subsequent block,\n"
	                             ."  then this warning can be safely ignored.)\n";
	                    }                    
	                    # update user            
	                    printf "Read %d observable(s).\n", scalar @{$model->Observables};
	                }
	
	                
	                ### Read Energy Patterns Block
	                elsif ( $name eq 'energy patterns' )
	                {
	                    # read energy patterns
	                    foreach my $line ( @$block_dat )
	                    {
	                        my ($entry, $lno) = @$line;
	                        my $epatt = EnergyPattern->new();
	                        $err = $epatt->readString( $entry, $model );
	                        if ($err) {  $err = errgen($err, $lno);  goto EXIT;  }
	                        push @{$model->EnergyPatterns}, $epatt;
	                    }
	                    # update 
	                    printf "Read %d energy patterns(s).\n", scalar @{$model->EnergyPatterns};  
	                
	                }                    
	
	                
	                ### Read Actions Block
	                elsif ( $name eq 'actions' )
	                {
	                    if ($model->Params->{'skip_actions'})
	                    {
	                        unless ($model->Params->{'action_skip_warn'})
	                        {   send_warning( err_gen("Skipping actions") );   }
	                        next;
	                    }
	                    # Read actions
	                    foreach my $line ( @$block_dat )
	                    {
	                        my ($entry, $lno) = @$line;
	                        # Remove (and ignore) leading index from line
	                        $entry =~ s/^\d+\s+//;
	                        # Get action and options
	                        my ($action, $options);
	                        if ( $entry =~ /^\s*(\w+)\s*\((.*)\);?\s*$/ )
	                        {
	                            $action  = $1;
	                            $options = $2;
	                            # replace double quotes with single quotes so that Perl won't
	                            #  try to interpret special characters.    
	                            $options =~ s/"/'/g;
	                        }
	                        else                        
	                        {
	                            $err = "Line $entry does not appear to contain a command";
	                            $err = errgen( $err, $lno );
	                        }
	
	                        # TODO: validate action                        
	                        # TODO: validate option syntax
	
	                        # Perform self-consistency checks before operations are performed on model
	                        if ( $err = $model->ParamList->check() )
	                        {
	                            $err = errgen($err);
	                            goto EXIT;
	                        }
	
	                        # execute action        
	                        my $command = sprintf "\$model->%s(%s);", $action, $options;
	                        my $t_start = cpu_time(0);
	                        $err = eval $command;
	                        if ($@)   { $err = errgen($@);    goto EXIT; }
	                        if ($err) { $err = errgen($err);  goto EXIT; }
	                        my $t_elapsed = cpu_time($t_start);
	                        printf "CPU TIME: %s %.2f s.\n", $action, $t_elapsed;
	                    }
	                }
	                
	
	                ### Try to read any other Block type (probably an error)
	                else
	                {   # exit
	                    $err = errgen("Could not process block type '$name'");
	                    goto EXIT;
	                }
	            }
	
	            elsif ( $string =~ /^\s*(setOption)\s*\((.*)\);?\s*$/ )
	            {   # special action: setOption(opts)
	                my $action = $1;
	                my $options = $2;
	                
	                # Perform self-consistency checks before operations are performed on model
	                if ( $err = $model->ParamList->check() )
	                {  $err = errgen($err);  goto EXIT;  }
	
	                # call to methods associated with $model
	                my $command = '$model->' . $action . '(' . $options . ');';
	                $err = eval $command;
	                if ($@)   {  $err = errgen($@);    goto EXIT;  }
	                if ($err) {  $err = errgen($err);  goto EXIT;  }
	            }
	
	            elsif ( $string =~ s/^\s*(parameter|param|par)\s+//i )
	            {   # Define a parameter outside of the Parameter block
	                unless ($in_model)
	                {
	                    $err = errgen("Parameter cannot be defined outside of a model");
	                    goto EXIT;
	                }
	                # read parameter
	                $err = $model->ParamList->readString($string);
	                if ($err) {  $err = errgen($err);  goto EXIT;  }
	            }
	
	            elsif ( $string =~ /^\s*(\w+)\s*\((.*)\);?\s*$/ )
	            {   # execute an Action:  "action(options)"
	                my $action = $1;
	                my $options = $2;
	                # replace double quotes with single quotes so that Perl won't
	                #  try to interpret special characters.    
	                $options =~ s/"/'/g;
	
	                if ($model->Params->{'skip_actions'})
	                {
	                    unless ($model->Params->{'action_skip_warn'})
	                    {   send_warning( errgen("Skipping actions") );   }
	                    next;
	                }
	
	                # Perform self-consistency checks before operations are performed on model
	                if ( $err = $model->ParamList->check() )
	                {
	                    $err = errgen($err);
	                    goto EXIT;
	                }
	
	                # execute action
	                my $command = sprintf "\$model->%s(%s);", $action, $options;
		
			#Check if the network is being read from a file
			if($action eq "readFile")
			{
				my $file_name = $options;
				$file_name  =~ s/(.*)file(\s*)=>(\s*)("|')//;
                                $file_name  =~ s/("|')(.*)//;
				$file_name =~ s/(.*)\.//;
				if($file_name eq 'net'){$NetFlag = 1;}
			}
					
			#The user has generated the network
			if($action eq "generate_network"){$NetFlag = 1;}
			my $method_name;
			if ($action eq "simulate" && $NetFlag == 0)
			{
				#Extract method
				$method_name = $options;
				$method_name =~ s/(.*)method(\s*)=>(\s*)('|")//;
				$method_name  =~ s/("|')(.*)//;
				if($method_name =~ /^(ode|ssa|pla)$/)
				{
				 	my $t_start = cpu_time(0);					
					#The simulation method requires a network
					#The user has not supplied a command. Use defaults
					my $cmd = sprintf "\$model->%s(%s);", "generate_network", "({overwrite=>1})";
                                	$err = eval $cmd;
                                	if ($@)   { $err = errgen($@);    goto EXIT; }
                                 	if ($err) { $err = errgen($err);  goto EXIT; }
                                 	my $t_elapsed = cpu_time($t_start);
                                 	printf "CPU TIME: %s %.2f s.\n","generate_network", $t_elapsed;
				 	$NetFlag = 1;
				}
			
			}

			if ($action =~ /^(simulate_ode|simulate_ssa|simulate_pla)$/ && $NetFlag == 0)
                        {
				 #The simulation method requires a network and the user has not generated one
				 my $t_start = cpu_time(0); 
                                 my $cmd = sprintf "\$model->%s(%s);", "generate_network", "({overwrite=>1})";
                                 $err = eval $cmd;
				 if ($@)   { $err = errgen($@);    goto EXIT; }	 
				 if ($err) { $err = errgen($err);  goto EXIT; }
                                 my $t_elapsed = cpu_time($t_start);
				 printf "CPU TIME: %s %.2f s.\n","generate_network", $t_elapsed;
			         $NetFlag = 1;
                        }

				my $t_start = cpu_time(0);
	                	$err = eval $command;
	                	if ($@)   { $err = errgen($@);    goto EXIT; }
	                	if ($err) { $err = errgen($err);  goto EXIT; }
	                	my $t_elapsed = cpu_time($t_start);
	                	printf "CPU TIME: %s %.2f s.\n", $action, $t_elapsed;

		    }
	            else
	            {   # Try to execute general PERL code (Dangerous!!)
	                if ( $model->Params->{allow_perl} )
	                {
	                    # General Perl code
	                    eval $string;
	                    if ($@) { $err = errgen($@);  goto EXIT; }
	                }
	                else
	                {
	                    send_warning( errgen("Unidentified input! Will not attempt to execute as Perl.") );
	                    next;
	                }
	            }
	        }
    		} # end read BNGL or NET
		else{
			printf "$filename\n";
			$filename =~ /(\.\w+)$/;
			$err = errgen("Cannot read file $filename. Unknown file extension '$1'.");
			goto EXIT;
		}

      EXIT:
        unless ($err)
        {   
            # if we're back at level 0, perform any required actions
            if ($level == 0)
            {
                if ( $model->Params->{'write_xml'} )
                {  $model->writeXML();  }

                if ( $model->Params->{'generate_network'} )
                {  $model->generate_network({'overwrite'=>1});  }

                if ( $model->Params->{'write_mfile'} )
                {  $model->writeMfile();  }

                if ( $model->Params->{'write_sbml'} )
                {  $model->writeSBML();  }
            }

            # indicate that we're finished
            print "Finished processing file $filename.\n";          
            if ($level == 0)
            {   # write time info
                printf "CPU TIME: total %.2f s.\n", cpu_time($t_start);
                # restore STDOUT
                if ($params->{logging})
                {
                    unless( open STDOUT, ">&", $stdout_handle )
                    {  $err = "Problem restoring STDOUT: $!\n";  } 
                }
            }
        }

        if ($level > 0)
        {   # retrieve state of previous level from stack
            $filename = pop @filename_stack;
            $file_data = pop @file_data_stack;
            $line_number = pop @line_number_stack;
            $model->Params( pop @params_stack );
        }    
        # drop a level
        --$level;
        # return with any error messages
        return $err;
    }

    ###
    ###

    sub read_block_array
    {
        my $name  = shift @_;
        my @array = ();

        my $got_end = 0;
        while ( $_ = get_line() )
        {
            # Look for end of block or errors
            if ( s/^\s*end\s+// )
            {
                my $ename = $_;
                $ename =~ s/\s*$//;
                $ename =~ s/\s+/ /g;
                if ( $ename ne $name )
                {
                    return ( [], errgen("end $ename does not match begin $name") );
                }
                else
                {
                    $got_end = 1;
                    #print "end at $line_number\n";
                    last;
                }
            }
            elsif ( /^\s*begin\s*/ )
            {
                return [], errgen("begin block before end of previous block $name");
            }

            # Add declarations from current line
            push @array, [$_, $line_number];

            #print "$_ $line_number\n";
        }

        unless ($got_end)
        {  return [], errgen("begin $name has no matching end $name");  }

        return [@array];
    }

    ###
    ###

    sub errgen
    {
        my $err = shift @_;
        my $lno = @_ ? shift @_ : $line_number;
        $err =~ s/[*]/\*/g;
        my $reterr = sprintf "%s\n  at line $lno", $err;
        if (defined $filename) { $reterr .= " of file '$filename'"; }
        return $reterr;
    }

    ###
    ###

    sub get_line
    {
        my $line;
        while ( $line = shift @$file_data )
        {
            ++$line_number;
            chomp $line;                         # remove newline character
            $line =~ s/\#.*$//;                  # remove comments
            next unless ($line =~ /\S+/);        # skip blank lines
            while ( $line =~ s/\\\s*$// )
            {   # line continuations "\"
                last unless (@$file_data);       # end if there are no more lines
                ++$line_number;
                my $nline = shift @$file_data;
                chomp $nline;
                $nline =~ s/\#.*$//;             # remove comments
                $line .= $nline;                 # append to previous line    
            }
            last;
        }
        return $line;
    }

    # END of readFile method
}



###
###
###



# write Model to file (default is BNGL format)
# $err = $model->writeModel({opt=>val,..}) 
sub writeModel
{
    use strict;
    use warnings;

    my $model       = shift @_;
    my $user_params = @_ ? shift @_ : {};

    my %params = (
        'evaluate_expressions' => 0,
        'format'               => 'bngl',
        'include_model'        => 1,
        'include_network'      => 0,
        'overwrite'            => 0,
        'pretty_formatting'    => 1,
    );

    # copy user_params into pass_params structures
    while ( my ($key,$val) = each %$user_params )
    {   $params{$key} = $val;    }

    # writeFile will generate the output
    return $model->writeFile( \%params );
}


# write Network in NET format
# $err = $model->writeNetwork({opt=>val,..}) 
sub writeNetwork
{
    use strict;
    use warnings;

    my $model       = shift @_;
    my $user_params = @_ ? shift @_ : {};

    my %params = (
        'evaluate_expressions' => 0,
        'format'               => 'net',
        'include_model'        => 0,
        'include_network'      => 1,
        'overwrite'            => 0,
        'pretty_formatting'    => 0,
    );

    # copy user_params into pass_params structures
    while ( my ($key,$val) = each %$user_params )
    {   $params{$key} = $val;    }

    # writeFile will generate the output
    return $model->writeFile( \%params );
}


# Write Reaction Network to .NET file
# This action will be Deprecated! writeModel and writeNetwork should be used instead
sub writeNET
{
    my $model       = shift @_;
    my $user_params = @_ ? shift @_ : {};

    # default parameters
    my %params = (
        'evaluate_expressions' => 1,
        'format'               => 'net',
        'include_model'        => 1,
        'include_network'      => 1,
        'overwrite'            => 1,
        'pretty_formatting'    => 0,
        'TextReaction'         => 0,
        'TextSpecies'          => 1,
    );

    # get any user parameters 
    while ( my ($key,$val) = each %$user_params )
    {   $params{$key} = $val;   }

    # call writeFile to output the network
    return $model->writeFile( \%params );
}


# This is a general method for writing the model and/or network to a file in various formats.
#  (This method does the heavy lifting for writeModel and writeNetwork)
#
# $err = $model->writeFile({OPT=>VAL,..})
#
# OPTIONS:
#   evaluate_expressions => 0,1 : evaluate math expressions output as numbers (default=0).
#   format => "FORMAT"          : select output format, where FORMAT=bngl,net,xml,sbml,ssc (default=net).
#   include_model => 0,1        : include model blocks in output file (default=1).
#   include_network => 0,1      : include network blocks in output file (default=1).
#   overwrite => 0,1            : allow writeFile to overwrite exisiting files (default=0).
#   prefix => "string"          : set prefix of output file name (default=outdir/MODELNAME).
#   pretty_formatting => 0,1    : write output in "pretty" form (default=0).
#   suffix => "string"          : set suffix of output file name (default=NONE).
#   TextReaction => 0,1         : write reactions as BNGL strings (default=0).
#   TextSpecies => 0,1          : write species as BNGL string (default=1).
#
# TODO: set up additional formats: SBML, SSC, etc.
sub writeFile
{
    use strict;
    use warnings;

    my $model       = shift @_;
    my $user_params = @_ ? shift @_ : {};

    my %params = (
        'evaluate_expressions' => 0,
        'format'               => 'net',
        'include_model'        => 1,
        'include_network'      => 1,
        'overwrite'            => 1,
        'prefix'               => $model->getOutputPrefix(),
        'pretty_formatting'    => 1,
        'suffix'               => '',
        'TextReaction'         => 0,
        'TextSpecies'          => 1,
    );

    # change this to a constant?
    my %allowed_formats = ( 'net'=>1, 'bngl'=>1, 'sbml'=>0, 'xml'=>1, 'ssc'=>0 );

    # copy user_params into params and pass_params structures
    foreach my $key ( keys %$user_params )
    {
        my $val = $user_params->{$key};
        if ( exists $params{$key} )
        {   $params{$key} = $val;   }
        else
        {   die "writeFile(): Unrecognized option parameter $key.";   }
    }

    # check if format is allowed
    unless ( $allowed_formats{ $params{'format'} } )
    {
        return sprintf( "writeFile() does not currently support '%s' format.", $params{'format'} );
    }

    # check if there's anything to write
    unless ( $params{'include_model'} or $params{'include_network'} )
    {
        return "writeFile() has nothing to write! Include model or network and try again.";
    }

    # check for reactions if we're writing the network
    if ( $params{include_network} )
    {
        if ( @{$model->RxnList->Array} == 0 )
        {
            return "writeFile() was asked to write the network, but no reactions were found.\n"
                  ."Did you remember to call generate_network() before writing network output?\n";
        }
    }

    # do nothing if we're not executing actions
    return undef if $NO_EXEC;

    ## Execute the Action ##
    # first, build output filename
    my $file = $params{prefix};
    unless ( $params{suffix} eq '' )
    {   $file .= "_$params{suffix}";   }
    $file .= ".$params{format}";

    # now check if we're overwriting an existing file
    if ( -e $file )
    {
        if ( $params{overwrite} )
        {
            send_warning("writeFile(): Overwriting existing file $file.");
            unlink $file;
        }
        else
        {   return "writeFile(): file $file exists. Set option overwrite=>1 to overwrite.";   }
    }

    # make a string that describes outputs to user
    my @outputs; 
    if ( $params{'include_model'} )
    {   push @outputs, "model";   }
    if ( $params{'include_network'} )
    {   push @outputs, "network";   }   
    my $output = join " and ", @outputs;

    # get file output in string format
    my $file_string;
    if ( $params{'format'} eq 'net' )
    {   # write NET format
        $file_string = $model->writeBNGL( \%params );
    }
    elsif ( $params{'format'} eq 'bngl' )
    {   # write BNGL format
        $file_string = $model->writeBNGL( \%params );
    }
    elsif ( $params{'format'} eq 'xml' )
    {   # write XML format
        $file_string = $model->toXML( \%params );
    }

    # write the string to file
    my $FH;
    open($FH, '>', $file)  or  return "Couldn't write to $file: $!\n";
    print $FH $file_string;
    close $FH;

    # all done
    print sprintf( "Wrote %s in %s format to %s.\n", $output, $params{'format'}, $file);
    return undef;
}


# Write Model to a BNGL formatted string.
# This method returns a string and does NOT write to file.
#  (also writes reaction network, if it exists)
sub writeBNGL
{
    use strict;
    use warnings;

    my $model = shift @_;
    my $user_params = @_ ? shift @_ : {};

    # Default parameters required by this method.
    # NOTE: since this method is not a user action, we don't need to check parameters.
    # Instead, we assume the programmers call this method with valid options.
    my %params = (
        'convert_intensive_to_extensive_units' => 1,
        'evaluate_expressions' => 0,
        'format'               => 'net',
        'include_model'        => 1,
        'include_network'      => 1,
        'pretty_formatting'    => 1,
        'TextReaction'         => 0,
        'TextSpecies'          => 1,
    );

    # get any user parameters 
    while ( my ($key,$val) = each %$user_params )
    {   $params{$key} = $val;    }

    return '' if $NO_EXEC;


    # !!! Begin writing file !!!
    my $out    = '';

    # Header
    my $version = BNGversion();
    my $codename = BNGcodename();
    $out .= "# Created by BioNetGen ${version}-${codename}\n";

    # Version requirements
    unless ( $model->Version eq '' )
    {  $out .= sprintf "version(\"%s\")\n", $model->Version;  }

    # Options
    while ( my ($opt,$val) = each %{$model->Options} )
    {
        next if ( $opt eq 'prefix' ); # don't write prefix
        next if ( $opt eq 'suffix' ); # don't write suffix
        $out .= "setOption(\"$opt\",\"$val\")\n";
    }

    # Units
    unless ($model->SubstanceUnits eq '')
    {  $out .= sprintf "substanceUnits(\"%s\")\n", $model->SubstanceUnits;  }

    # Begin Model (BNGL only)
    $out .= "\nbegin model\n"  if ( $params{'format'} eq 'bngl'  and  $params{'pretty_formatting'} );

    # Parameters
    $out .= $model->ParamList->writeBNGL( \%params );

    # Model blocks
    if ( $params{'include_model'} )
    {
        # Compartments
        if ( defined $model->CompartmentList  and  @{$model->CompartmentList->Array} )
        {   $out .= $model->CompartmentList->toString( $model->ParamList );   }

        # MoleculeTypes
        if ( $model->MoleculeTypesList->StrictTyping )
        {   $out .= $model->MoleculeTypesList->writeBNGL( \%params );   }

        # Observables    
        if ( @{$model->Observables} )
        {
            # find max length of observable name
            my $max_length = 0;
            foreach my $obs ( @{$model->Observables} )
            {
                $max_length = ( length $obs->Name > $max_length ) ? length $obs->Name : $max_length;
            }
            
            $out .= "begin observables\n";
            my $io = 1;
            foreach my $obs ( @{$model->Observables} )
            {
                if ( $params{'pretty_formatting'} )
                {   # no observable index
                    $out .= sprintf "  %s\n", $obs->toString($max_length);
                }
                else
                {   # include index
                    $out .= sprintf "%5d %s\n", $io, $obs->toString();
                }
                ++$io;
            }    
            $out .= "end observables\n";
        }

        # Energy Patterns
        if ( @{$model->EnergyPatterns} )
        {
            $out .= "begin energy patterns\n";
            my $io = 1;
            foreach my $epatt ( @{ $model->EnergyPatterns } )
            {
                if ( $params{'pretty_formatting'} )
                {   # no energy pattern index
                    $out .=  sprintf "  %s\n", $epatt->toString($model->ParamList);
                }
                else
                {   # include index
                    $out .=  sprintf "%5d %s\n", $io, $epatt->toString($model->ParamList);
                }

                ++$io;
            };        
            $out .= "end energy patterns\n";
        }
    }

    # Functions
    $out .= $model->ParamList->writeFunctions( \%params );
        
    # Species
    $out .= $model->SpeciesList->writeBNGL( $model->Concentrations, $model->ParamList, \%params );


    # Model Blocks
    if ( $params{include_model} )
    {
        # Reaction rules
        $out .= "begin reaction rules\n";
        {
            my $irxn = 1;
            foreach my $rset ( @{$model->RxnRules} )
            {
                my $rreverse = ( @$rset > 1 ) ? $rset->[1] : undef;

                # write BNGL rule
                $out .= sprintf "  %s\n", $rset->[0]->toString($rreverse);
        
                # write actions
                if ( $params{'pretty_formatting'} )
                {   # pretty, don't write actions
                    # do nothing!
                }
                else
                {   # write actions
                    $out .= $rset->[0]->listActions();
                    if ( defined $rreverse )
                    {
                        $out .= "  # Reverse\n";
                        $out .= $rset->[1]->listActions();
                    }    
                }
                ++$irxn;
            }
        }
        $out .= "end reaction rules\n";
    }

    # Network blocks
    if ( $params{'include_network'} )
    {
        # Reactions    
        if ( $params{'TextReaction'} )
        {   print "Writing full species names in reactions.\n";   }
        $out .= $model->RxnList->writeBNGL( \%params, $model->ParamList );

        # Groups
        if ( @{$model->Observables} )
        {
            $out .= "begin groups\n";
            my $io = 1;
            foreach my $obs ( @{$model->Observables} )
            {
                $out .= sprintf "%5d %s\n", $io, $obs->toGroupString( $model->SpeciesList );
                ++$io;
            }
            $out .= "end groups\n";
        }
    }

    # End Model (BNGL only)
    $out .= "end model\n"  if ( $params{'format'} eq 'bngl' and  $params{'pretty_formatting'} );

    return $out;
}



###
###
###



# Syntax
#   setOption(name,value,name,value,...) Set option value pairs
# First call will cause initialization with default values.
sub setOption
{
    my $model = shift @_;
    my $err   = '';

    # Process options
    while (@_)
    {
        my $arg = shift @_;
        unless (@_) { return "No value specified for option $arg"; }
        my $val = shift @_;
        
        # TODO: print arg and val to user?

        if ( $arg eq "SpeciesLabel" )
        {
            # SpeciesLabel method can only be changed prior to reading species.
            # Otherwise, inconsistent behavior could arise from changing the
            # labeling method.
            if ( $model->SpeciesList->size() )
            {   return "$arg option can only be changed prior to defining species.";  }
            $err = SpeciesGraph::setSpeciesLabel($val);
            if ($err) { return $err; }
            $model->Options->{$arg} = $val;
        }
        elsif ( $arg eq "energyBNG" )
        {   # enable energy mode
            send_warning("The energyBNG option is now deprecated (energy features available by default).");
        }
        elsif ( $arg eq "NumberPerQuantityUnit" )
        {   # set conversion from quantity units to pure numbers
            # TODO: allow this to be a parameter?
            $model->Options->{$arg} = $val;
        }
        elsif ( $arg eq "MoleculesObservables" )
        {   # set molecules observables mode
            unless ($val eq "CountUnique"  or $val eq "CountAll")
            {   return "Invalid option for or $arg (valid options are 'CountUnique' and 'CountAll')";   }
            $model->Options->{$arg} = $val;
        }
        elsif ( $arg eq "SpeciesObservables" )
        {   # set species observables mode
            unless ($val eq "CountUnique"  or $val eq "CountAll")
            {   return "Invalid option for or $arg (valid options are 'CountUnique' and 'CountAll')";   }
            $model->Options->{$arg} = $val;
        }
        else
        {
            return "Unrecognized option $arg in setOption";
        }
    }

    return '';
}


sub substanceUnits
{
    my $model = shift @_;
    my $units = shift @_;

    my $ucommand = "";
    if ( $units =~ /^conc/i ) {
        $ucommand = "Concentration";
    }
    elsif ( $units =~ /^num/i ) {
        $ucommand = "Number";
    }
    else {
        return "Invalid argument to subtanceUnits $units: valid arguments are Number and Concentration";
    }

    print "SubstanceUnits set to $ucommand.\n";
    $model->SubstanceUnits($ucommand);
    return '';
}


sub setVolume
{
    my $model            = shift @_;
    my $compartment_name = shift @_;
    my $value            = shift @_;

    return $model->CompartmentList->setVolume( $compartment_name, $value );
}



###
###
###



sub setParameter
{
    my $model = shift @_;
    my $pname = shift @_;
    my $value = shift @_;

    return '' if $NO_EXEC;

    my $plist = $model->ParamList;
    my ($param, $err);

    # Error if parameter doesn't exist
    ( $param, $err ) = $plist->lookup($pname);
    if ($err) { return ($err) }

    # Read expression
    my $expr    = Expression->new();
    my $estring = "$pname=$value";
    if ( $err = $expr->readString( \$estring, $plist ) ) { return $err; }

    # Set flag to update netfile when it's used
    $model->UpdateNet(1);

    printf "Set parameter %s to value %s\n", $pname, $expr->evaluate($plist);
    return '';
}


# Save the current parameter definitions.
#  Optionally specify a label to associate with the saved parameters.
sub saveParameters
{
    my $model = shift @_;
    my $label = @_ ? shift @_ : Cache::DEFAULT_LABEL;

    return '' if $NO_EXEC;

    # copy paramList (exclude non-constant types)
    my $paramlist = $model->ParamList->copyConstant();

    # put saved concentration into cache
    $model->ParameterCache->cache($paramlist,$label);
    # Send message to user
    printf "Saved current parameters with label '%s'\n", $label;
    return undef;
} 


# Reset parameters to saved defintions.
#  Optionally specify a label used to find the saved parameters
sub resetParameters
{
    my $model = shift @_;
    my $label = @_ ? shift @_ : Cache::DEFAULT_LABEL;

    return '' if $NO_EXEC;
    
	# get a COPY so that subsequent 'setParameter' calls don't modify the saved list
    my $saved_paramlist = $model->ParameterCache->browse($label)->copyConstant();

    unless (defined $saved_paramlist)
    {   return "resetParameters(): cannot find saved parameters";   }

    unless (ref $saved_paramlist eq 'ParamList')
    {   return "resetParameters(): problem retrieving saved parameters";   }

    # copy saved parameters into main ParamList
    my $err;
    foreach my $param ( @{$saved_paramlist->Array} )
    {
        $err = $model->ParamList->add( $param );
        if ($err) { return "resetParameters(): problem resetting parameters ($err)"; }
    }

    # Set flag to update netfile when it's used
    $model->UpdateNet(1);

    # Send message to user
    printf "Reloaded parameters saved with label '%s'\n", $label;
    # all done
    return undef;
}



###
###
###



# Set the concentration of a species to specified value.
# Value may be a number or a parameter.
sub setConcentration
{
    my $model = shift @_;
    my $sname = shift @_;
    my $value = shift @_;
    
    return '' if $NO_EXEC;

    my $plist = $model->ParamList;
    my $err;

    # SpeciesGraph specified by $sname
    my $sg = SpeciesGraph->new;
    $err = $sg->readString( \$sname, $model->CompartmentList, 1, '', $model->MoleculeTypesList );
    if ($err) { return $err; }

    # Should check that this SG specifies a complete species, otherwise
    # may match a number of species.

    # Find matching species
    my $spec;
    unless ( $spec = $model->SpeciesList->lookup($sg) )
    {
        $err = sprintf "Species %s not found in SpeciesList", $sg->toString();
        return $err;
    }

    # Read expression
    my $expr    = Expression->new();
    my $estring = $value;
    if ( my $err = $expr->readString( \$estring, $plist ) ){
        return '', $err;
    }
    
    # Evaluate observable and function names to values and reset
    # the expression. Don't do this for parameters so that parameter 
    # scans can be run.
    my $estring2 = $value;
    my $variables = $expr->getVariables($plist);
	foreach my $var ($variables->{'Observable'}){ # Observables
		foreach my $name (keys %{$var}){
			my $val = $plist->evaluate($name);
			$estring2 =~ (s/$name/$val/g);
		}
	}
	foreach my $var ($variables->{'Function'}){ # Functions
		foreach my $name (keys %{$var}){
			my $val = $plist->evaluate($name);
			$estring2 =~ (s/$name(\(\))?/$val/g);
		}
	}
	if ( my $err = $expr->readString( \$estring2, $plist ) ){
        return '', $err;
    }
    
    # Either evaluate expression or create a new one with prefix 'NewConc'
    my $conc; # = $expr->evaluate($plist);
    if ( $expr->Type eq 'NUM' ){
    		$conc = $expr->evaluate();
    }
    else{
        $conc = $expr->getName( $plist, 'NewConc' );
    }

    # load Concentration array (if not already done)
    $model->SpeciesList->checkOrInitConcentrations( $model->Concentrations );

    # set concentration
    $model->Concentrations->[$spec->Index - 1] = $conc;

    # Set flag to update netfile when it's used
    $model->UpdateNet(1);

    printf "Set concentration of species %s to value %s\n", $spec->SpeciesGraph->StringExact, 
    		($expr->Type eq 'NUM' ? $conc : $expr->toString());
    return undef;
}


# Add concentration to a species.
# Value may be a number or a parameter.
sub addConcentration
{
    my $model = shift @_;
    my $sname = shift @_;
    my $value = shift @_;
    
    return '' if $NO_EXEC;

    my $plist = $model->ParamList;
    my $err;

    # SpeciesGraph specified by $sname
    my $sg = SpeciesGraph->new;
    $err = $sg->readString( \$sname, $model->CompartmentList, 1, '', $model->MoleculeTypesList );
    if ($err) { return $err; }

    # Should check that this SG specifies a complete species, otherwise
    # may match a number of species.

    # Find matching species
    my $spec;
    unless ( $spec = $model->SpeciesList->lookup($sg) )
    {
        $err = sprintf "Species %s not found in SpeciesList", $sg->toString();
        return $err;
    }

    # Read expression
    my $expr    = Expression->new();
    my $estring = $value;
    if ( my $err = $expr->readString( \$estring, $plist ) )
    {
        return ( '', $err );
    }
    my $add_conc = $expr->evaluate($plist);

    # load Concentration array (if not already done)
    $model->SpeciesList->checkOrInitConcentrations( $model->Concentrations );

    # Add original concentration 
    my $orig_conc = $model->Concentrations->[$spec->Index - 1];
    unless ( isReal($orig_conc) )
    {   # evaluate parameter
        $orig_conc = $plist->evaluate($spec->Concentration);
    }        
    my $conc = $add_conc + $orig_conc;

    # set new concentration
    $model->Concentrations->[$spec->Index - 1] = $conc;

    # Set flag to update netfile when it's used
    $model->UpdateNet(1);

    printf "Add %s counts (or concentration units) to species %s for total of %s\n",
         $add_conc, $spec->SpeciesGraph->StringExact, $conc;
    return undef;
}


# Save the current species concentrations.
#  Optionally specify a label to associate with the saved state.
sub saveConcentrations
{
    my $model = shift @_;
    my $label = @_ ? shift @_ : Cache::DEFAULT_LABEL;

    return '' if $NO_EXEC;

    # create new concentration array
    my $conc = [];
    if (@{$model->Concentrations})
    {   # copy concentration from primary concentration array
        @$conc = @{$model->Concentrations};
    }
    else
    {   # if that's not defined, copy directly from SpeciesList
        @$conc = map {$_->Concentration} @{$model->SpeciesList->Array};
    }
    # put saved concentration into cache
    $model->ConcentrationCache->cache($conc,$label);
    print "Saved current species concentrations with label \"$label\"\n";
    return undef;
}


# Reset species concentrations to saved values.
#  Optionally specify a label used to find the saved concentrations
sub resetConcentrations
{
    my $model = shift @_;
    my $label = @_ ? shift @_ : Cache::DEFAULT_LABEL;

    return '' if $NO_EXEC;

    # lookup up concentrations in the cashe
    my $saved_conc = $model->ConcentrationCache->browse($label);

    my $conc;
    if ( (defined $saved_conc) and (ref $saved_conc eq 'ARRAY') )
    {   # get a copy of the values (don't want to mess with originals)
        @$conc = @$saved_conc;
    }
    elsif ( !(defined $saved_conc) )
    {   # didn't find anything in the cache ...
        if ( $label eq Cache::DEFAULT_LABEL )
        {   # if we were looking for default values, get them directly from the species objects ...
            @$conc = map {$_->Concentration} @{$model->SpeciesList->Array};
        }
        else
        {   # otherwise return an error
            return "resetConcentrations(): cannot find saved concentrations with label \"$label\"";
        }
    }
    else
    {   # cache returned unexpected reference type
        return "resetConcentrations(): some problem retrieving saved concentrations";
    }

    if ( @$conc > @{$model->SpeciesList->Array} )
    {   # this case is not well-defined
        return "resetConcentrations(): length of concentration vector is larger than the number of species";
    }
    elsif ( @$conc < @{$model->SpeciesList->Array} )
    {   # pad with zeros
        my $n_zeros = @{$model->SpeciesList->Array} - @$conc;
        push @$conc, (0) x $n_zeros;
    }

    # finally, set concentrations to the saved values
    $model->Concentrations($conc);
    # Set flag to update netfile when it's used
    $model->UpdateNet(1);
    # all done
    print "Reset species concentrations to \"$label\" saved values.\n";
    return undef;
}



###
###
###



sub setModelName
{
    my $model = shift @_;
    my $name  = shift @_;

    $model->Name($name);
    return '';
}


###
###
###



# Function to require the version conform to specified requirement
# Syntax: version(string);
# string = major[.minor][.dist][+-][codename]
#
# major, minor, and dist. indicate the major, minor, and distribution number
# respectively against which the BioNetGen version numbers will be compared.
# + indicates version should be the specified version or later (default)
# - indicates version should be the specified version or earlier
sub version
{
    my $model   = shift @_;
    my $vstring = shift @_;

    return '' if $NO_EXEC;

    if (@_)
    {   # complain about too many arguments
        return "Version called with too many arguments.";
    }

    # extract version and codename
    my ($version, $relation, $codename) = ( $vstring =~ /^(\d+\.\d+\.\d+)([+-]?)\s*(\w*)/ );

    unless ( defined $version )
    {   # complain that version is invalid
        return "version argument '$vstring' has invalid format (make sure argument is enclosed in double quotes \"\").";
    }

    if ( $relation eq "" )
    {   # default relation is "+"
        $relation = "+";
    }

    my $bng_version = BNGversion();
    if ( $bng_version eq 'UNKNOWN' )
    {   # complain that BNG version is unknown
        return "BNG version is UNKNOWN!";
    }

    # compare versions (returns -1 if version < bng_version)
    my $comp = compareVersions($version,$bng_version);

    # is active BNG version suitable?
    if    ( $relation eq '+'  and  $comp == 1 )
    {   # BNG version is less than minimum required!
        return "Requested BioNetGen version $version or greater. Active version is $bng_version.";
    }
    elsif ( $relation eq '-'  and  $comp == -1 )
    {   # BNG version is greater than maximum allowed!
        return "Requested BioNetGen version $version or lesser. Active version is $bng_version.";
    }
    
    # check codename
    unless ($codename eq "")
    {
        my $bng_codename = BNGcodename();
        unless ( $codename eq $bng_codename )
        {
            return "Requested BioNetGen codename '${codename}'. Active codename is '${bng_codename}'.";
        }
    }
    
    # Set version requirement
    $model->Version( $vstring );

    # everything is good
    return undef;
}

sub codename
{
    my $model    = shift @_;
    my $codename = shift @_;

    return '' if $NO_EXEC;

    if (@_)
    {   # complain about too many arguments
        return "Codename called with too many arguments.";
    }

    unless ( $codename )
    {   # complaiin about empty codename
        return "codename argument is empty.";
    }

    # get BNG codename
    my $bng_codename = BNGcodename();
    if ( $bng_codename eq 'UNKNOWN' )
    {   # complain that BNG codename is unknown
        return "BNG version is UNKNOWN!";
    }

    # compare codename
    unless ($codename eq $bng_codename)
    {   # BNG codename is not correct
        return "Requested BioNetGen codename $codename. Active version is $bng_codename.";
    }
    
    # Add current version requirement to the model
    push @{$model->Codename}, $codename;

    # everything is good
    return undef;
}


###
###
###



sub quit
{
    # quick exit. no cleanup. no error messages
    # This is useful when the user desires to exit before
    #  performing a set of actions and it would be tedious to 
    #  comment out all those actions.
    print "quitting BioNetGen!\n";
    exit(0);
}



###
###
###



# Add equilibrate option, which uses additional parameters
# t_equil and spec_nonequil.  If spec_nonequil is set, these
# species are not used in equilibration of the network and are only
# added after equilibration is performed. Network generation should
# re-commence after equilibration is performed if spec_nonequil has
# been set.

sub generate_network
{
    my $model       = shift @_;
    my $user_params = @_ ? shift @_ : {};

    printf "ACTION: generate_network( %s )\n", $model->Name;

    # default params
    my %params = (
        'continue'     => 1,
        'max_iter'     => 100,
        'max_agg'      => 1e9,
        'max_stoich'   => {},
        'check_iso'    => 1,
        'prefix'       => $model->getOutputPrefix(),
        'suffix'       => undef,
        'overwrite'    => 0,
        'print_iter'   => 0,
        'TextSpecies'  => 1,
        'TextReaction' => 0,
        'verbose'      => 0,
		'write'		   => 1
    );

    # overwrite default params with user params
    foreach my $key (keys %$user_params)
    {
        my $val = $user_params->{$key};
        unless ( exists $params{$key} )
        {   return "Unrecognized parameter $key in generate_network";   }

        $params{$key} = $val;
    }

    return '' if $NO_EXEC;
    
    # Output prefix
    if (defined $user_params->{prefix}){
    	$params{prefix} = $model->getOutputPrefix($user_params->{prefix});
    }
    
    # add optional suffix to output prefix
    if ( $params{suffix} )
    {  $params{prefix} .= "_" . $params{suffix};  }

    # default params for calling writeNetwork
    # (only need to change if we want non-default)
    my $params_writeNetwork = {
        'include_model' => 0,
        'overwrite'     => 1,
        'prefix'        => $params{prefix},
        'TextSpecies'   => $params{TextSpecies},
        'TextReaction'  => $params{TextReaction}
    };

    # default params for calling expand_rule
    my $params_expand_rule = {
        'check_iso'  => $params{check_iso},
        'max_agg'    => $params{max_agg},
        'max_stoich' => $params{max_stoich},
        'verbose'    => $params{verbose},
    };

	# make sure 'max_stoich' molecules are valid names
	foreach my $mol ( keys %{$params{'max_stoich'}} ){
		if ( not $mol =~ /^[A-Za-z_]\w*$/ ){
			return "Error in max_stoich: '$mol' is not a valid molecule name.";
		}
	}
	
    # check verbose option
    my $verbose = $params{verbose};

    # Check if existing net file has been created since last modification time of .bngl file
    my $prefix = $params{prefix};
    if ( -e "$prefix.net"  and  -e "$prefix.bngl" )
    {
        if ($params{overwrite})
        {
            send_warning("Removing old network file $prefix.net.");
            unlink "$prefix.net";
        }
        elsif ( -M "$prefix.net" < -M "$prefix.bngl" )
        {
            send_warning("$prefix.net is newer than $prefix.bngl so reading NET file.");
            my $err = $model->readFile({file=>"${prefix}.net"});
            return $err;
        }
        else
        {
            return "Previously generated $prefix.net exists.  Set overwrite=>1 option to overwrite.";
        }
    }
    
    if ( $model->SpeciesList->size() == 0 )
    {   # warn user if the seed species list is empty.
        send_warning("The seed species block is empty: reaction network will be empty "
                    ."unless zero-order synthesis rules are defined.");
    }

    # nothing to do if no rules are defined
    if ( @{$model->RxnRules} == 0 )
    {   return "Nothing to do: no reaction rules defined.";   }

    # if no reactions have been generated previously, then we have to initialize some things...
    if ( $model->RxnList->size()==0 or $params{'continue'}==0 )
    {
        # initialize rules
        foreach my $rset ( @{$model->RxnRules} )
        {
            foreach my $rr (@$rset)
            {   $rr->initializeRule();   }
        }
        # Initialize observables
        foreach my $obs ( @{$model->Observables} )
        {   
            $obs->reset_weights( $model->SpeciesList->size() );
            $obs->update( $model->SpeciesList->Array );
        }
        # Initialize energy patterns (for energy BNG only)
        foreach my $epatt ( @{$model->EnergyPatterns} )
        {
            $epatt->reset_weights($model->SpeciesList->size());
            $epatt->update($model->SpeciesList->Array);
        }
        # remember that we applied the observables
        foreach my $sp ( @{$model->SpeciesList->Array} )
        {   $sp->ObservablesApplied(1);   }
    }
    else
    {   # friendly warning that we're continuing network generation 
        send_warning("Reaction list is already populated. Continuing network generation from where we last left off.");
    }


    my @rule_timing = ();
    my @rule_nrxn   = ();
    
    # update user with initial report
    report_iter( 0, $model->SpeciesList->size(), $model->RxnList->size() );

    # now perform network generation steps
    foreach my $niter ( 1 .. $params{max_iter} )
    {
        my $t_start_iter = cpu_time(0);
        my @species = @{$model->SpeciesList->Array};

        # Apply reaction rules
        my $irule = 0;
        my ($n_new, $t_off, $n_new_tot);
        $n_new_tot = 0;
        # NOTE: each element of @{$model->RxnRules} is an array of reactions.
        #  If a rule is unidirectional, then the array has a single element.
        #  If a rule is bidirectional, then the array has two elements (forward and reverse)
        foreach my $rset ( @{$model->RxnRules} )
        {
            if ($verbose) { printf "Rule %d:\n", $irule + 1; }
            $n_new = 0;
            $t_off = cpu_time(0);
            my $dir = 0;
            foreach my $rr (@$rset)
            {
                if ($verbose)
                {
                    if ($dir == 0) { print "  forward:\n"; }
                    else           { print "  reverse:\n"; }
                }
                # expand rule
                my ($err, $nr) = $rr->expand_rule( \@species, $model, $params_expand_rule );
                if (defined $err) { return "Some problem expanding rule: $err"; }
                $n_new += $nr;
                ++$dir;
            }
        
            my $time = cpu_time(0) - $t_off;
            $rule_timing[$irule] += $time;
            $rule_nrxn[$irule]   += $n_new;

            if ($verbose)
            {   printf "Result: %5d new reactions %.2e CPU s\n", $n_new, $time;   }

            $n_new_tot += $n_new;
            ++$irule;
        }

        # update RulesApplied for species processed in this interation
        foreach my $spec (@species)
        {   $spec->RulesApplied($niter) unless ($spec->RulesApplied);   }

        # report iteration to user
        report_iter( $niter, $model->SpeciesList->size(), $model->RxnList->size(), $t_start_iter );

        # Free memory associated with RxnList hash
        $model->RxnList->resetHash();

        # Stop iteration if no new species were generated
        last if ( $model->SpeciesList->size() == scalar @species );

        # Print network after current iteration to netfile
        if ( $params{print_iter} and $params{write} )
        {
            $params_writeNetwork->{prefix} = "${prefix}_${niter}";
            my $err = $model->writeNetwork($params_writeNetwork);
            if ($err) { return $err; }
            $params_writeNetwork->{prefix} = $prefix;
        }
    }
        
                
    # Print rule timing information
    printf "Cumulative CPU time for each rule\n";
    my $t_tot = 0;
    my $n_tot = 0;
    foreach my $irule ( 0 .. $#{$model->RxnRules} )
    {
        my $eff = ( $rule_nrxn[$irule] ) ? $rule_timing[$irule] / $rule_nrxn[$irule] : 0.0;
        printf "Rule %3d: %5d reactions %.2e CPU s %.2e CPU s/rxn\n",
                   $irule + 1, $rule_nrxn[$irule], $rule_timing[$irule], $eff;
        $t_tot += $rule_timing[$irule];
        $n_tot += $rule_nrxn[$irule];
    }
    my $eff = ($n_tot) ? $t_tot / $n_tot : 0.0;
    printf "Total   : %5d reactions %.2e CPU s %.2e CPU s/rxn\n", $n_tot, $t_tot, $eff;

	
	# this is used in visualization, where a network is generated, but not written.
	return if ($params{write}==0);
	
    # Print result to netfile
    my $err = $model->writeNetwork($params_writeNetwork);
    if ($err) { return $err; }
	
	# STATISTICAL FACTOR FOR REACTIONS- DEBUGGING
	# OUTPUTS A FILE FOR EACH RULE 
	# SHOWING REACTION INSTANCES AND LUMPING
	# DURING generate_network()
	# NAME YOUR RULES FIRST!
	# - JOHN SEKAR
	
	my $aut = $BNGModel::GLOBAL_MODEL->Params->{'write_autos'};
	if($aut==1)
	{
	foreach my $rxn(@{$model->RxnList->Array})
		{
		my $str = $rxn->toString(1);
		my %inst = %{$rxn->InstanceHash};
		my @k = keys %inst;
		foreach my $rule(@k)
			{
			my $modelname = $BNGModel::GLOBAL_MODEL->Name;
			my $rulename = $rule;
			my $filename = join("_",($modelname,$rulename,"StatFactorCalculation")).".txt";
			open(my $autfile,">>",$filename) or die "Not found!";
			print $autfile "\nReaction\n".$str;
			print $autfile "\nLumpFactor ".$inst{$rule};
			print $autfile "\nReactionStatFactor: RuleStatFactor*LumpFactor = ".$rxn->StatFactor."\n";
			close($autfile);
			}
		}
	}

    return '';


    ###
    ###


    sub report_iter
    {
        my $niter = shift @_;
        my $nspec = shift @_;
        my $nrxn  = shift @_;
        my $t_start_iter = @_ ? shift @_ : undef;

        printf "Iteration %3d: %5d species %6d rxns", $niter, $nspec, $nrxn;
        my $t_cpu = defined $t_start_iter ? cpu_time(0) - $t_start_iter : 0;
        printf "  %.2e CPU s", $t_cpu;
        if ($HAVE_PS) {
            my ( $rhead, $vhead, $rmem, $vmem ) = split ' ', `ps -o rss,vsz -p $$`;
            printf " %.2e (%.2e) Mb real (virtual) memory.",
                    $t_cpu, $rmem / 1000, $vmem / 1000;
        }
        printf "\n";
    }

}



###
###
###



# given a generic program name, returns the specific executable binary.
#  returns empty string if binary can't be found
sub findExec
{
    use Config;
    my $prog = shift @_;

    my $base = BNGpath( "bin", $prog );
    my $arch = $Config{myarchname};
    
    # First look for generic binary in BNGpath
    my $exec = $base;
    if ($arch =~ /MSWin32/) { $exec .= ".exe"; }
    if (-x $exec) { return $exec; }

    # Then look for OS-specific binary
    $exec = "${base}_${arch}";
    if ($arch =~ /MSWin32/){
    		my $bitness = $Config{longsize}*8;
    		$exec .= "-${bitness}bit" . ".exe";
    }

    if (-x $exec) { return $exec; }
    else
    {
        print "findExec: $exec not found.\n";
        return '';
    }
}



###
###
###



# get output prefix = outputDir/modelName[_outputSuffix]
sub getOutputPrefix
{
    my $model = shift @_;
    my $file_prefix = @_ ? shift @_ : $model->Name;

    my $is_absolute = File::Spec->file_name_is_absolute( $file_prefix );

    if ( $model->Params->{suffix} )
    {   $file_prefix .= '_' . $model->Params->{output_suffix};   }

    if ($is_absolute or $model->getOutputDir() eq "")
    {
        return $file_prefix;
    }
    else
    {
        return File::Spec->catfile( ($model->getOutputDir()), $file_prefix );
    }
}

###
###

# set the output directory, defaults to curdir if no argument is provided 
sub setOutputDir
{
    my $model = shift @_;
    my $dir   = @_ ? shift @_ : undef;

    unless (defined $dir)
    {   # default to current directory
        $dir = File::Spec->curdir();
    }

    # set output directory
    $model->Params->{output_dir} = $dir;
}

###
###

# get the output directory
sub getOutputDir
{
    my $model = shift @_;

    unless ( defined $model->Params->{output_dir} )
    {   # output directory not defined, set to default
        $model->setOutputDir();
    }

    return $model->Params->{output_dir};
}



###
###
###

1;

