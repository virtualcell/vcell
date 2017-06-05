package BNGModel;
# BNGAction is part of the BNGModel package. This file contains Action commands
# including:  simulate, simulate_nf, simulate_pla, generate_hybrid_model, ...

#
# pragmas
use strict;
use warnings;

###
###
###



sub simulate_ode
{
    my $model = shift @_;
    my $params = (@_) ? shift @_ : {};

    $params->{method} = 'cvode';
    my $err = $model->simulate( $params );
    return $err;
}


sub simulate_ssa
{
    my $model = shift @_;
    my $params = (@_) ? shift @_ : {};

    $params->{method} = 'ssa';
    my $err = $model->simulate( $params );
    return $err;
}


sub simulate_pla
{
    my $model = shift @_;
    my $params = (@_) ? shift @_ : {};

    $params->{method} = 'pla';
    my $err = $model->simulate( $params );
    return $err;
}



###
###
###


# all purpose simulation method (supports NFsim by calling 'simulate_nf' if method = "nf")
sub simulate
{
    use IPC::Open3;
    use IO::Select;

    my $model  = shift @_;
    my $params = (@_) ? shift @_ : {};
    my $err;

    my $METHODS =
    {
        cvode => { binary=>'run_network', type=>'Network', input=>'net',
                   options=>{ atol=>1, rtol=>1, sparse=>1, steady_state=>1 } },
        ssa   => { binary=>'run_network', type=>'Network', input=>'net', 
                   options=>{ seed=>1 }                                      },
        pla   => { binary=>'run_network', type=>'Network', input=>'net',
                   options=>{ seed=>1 }                                      },
        nf    => { binary=>'NFsim', type=>'NetworkFree', input=>'xml',
                   options=>{ seed=>1 }                                      }
    };

    return '' if $BNGModel::NO_EXEC;

    # Read simulation arguments from file
    my @sample_times;
    my $argfile = defined $params->{argfile} ? $params->{argfile} : undef;
    if ($argfile)
    {
        print "Reading simulation arguments from $argfile.\n";
        open(ARGS, "<", $argfile) or return "Could not open argfile '$argfile'.";
        my $lineCounter = 0;
        while (my $line = <ARGS>)
        {
            ++$lineCounter;
            # trim comments
            $line =~ s/#.*$//;
            # skip empty lines
            next unless ( $line =~ /\S/ );
            # split tokens
            my @args = split " ", $line; 

            unless (@args == 2)
            {   return "Could not process line $lineCounter in $argfile: wrong number of arguments.";   }

            print "Processing argument: ";
            # Check if Arg is already defined in action call
            if ( !(defined $params->{$args[0]}) )
            {   
                if ( $args[0] ne 'sample_times' ){
                    $params->{$args[0]} = $args[1];
                    printf "$args[0] => %s\n", (looks_like_number($args[1]) ? $args[1] : "'$args[1]'");
                }
                else
                {   # Special handling for sample_times array
                    print "$args[0] => $args[1]\n";
                    # evaluate sample_times string to get array ref (hopefully)
                    my $sample_times = eval $args[1];
                    if ($@)
                    {    return "Problem parsing 'sample_times': Sample times must be comma-separated (no spaces) ints or floats "
                             . "(exponential format ok) enclosed in square brackets, e.g., [5e-1,1,5.0,1E1].";
                    }
                    @sample_times = @$sample_times;
                    foreach my $sample (@sample_times)
                    {   # check that sample time is a number
                        unless ( looks_like_number($sample) )
                        {    return "Problem parsing 'sample_times': Sample times must be comma-separated (no spaces) ints or floats "
                             . "(exponential format ok) enclosed in square brackets, e.g., [5e-1,1,5.0,1E1].";
                        }
                    }
                }
            }
            else
            {   # Args in the action call take precedence
                if ( $args[0] ne 'sample_times' )
                {   printf "'$args[0]' already defined with value %s, moving on...\n",
                           (looks_like_number($params->{$args[0]}) ? "$params->{$args[0]}" : "'$params->{$args[0]}'");
                }
                else
                {   printf "'$args[0]' already defined in action call, moving on...\n";   }
            }
        }
        close ARGS;
        printf "Finished.\n";
    }

    # general options
    my $method        = defined $params->{method} ? $params->{method} : undef;
    my $verbose       = defined $params->{verbose} ? $params->{verbose} : 0;
    my $prefix        = defined $params->{prefix} ? $model->getOutputPrefix( $params->{prefix} ) : $model->getOutputPrefix();
    my $suffix        = defined $params->{suffix} ? $params->{suffix} : undef;
    my $netfile       = defined $params->{netfile} ? $params->{netfile} : undef;
    my $print_end     = defined $params->{print_end} ? $params->{print_end} : 0;
    my $print_net     = defined $params->{print_net} ? $params->{print_net} : 0;
    my $save_progress = defined $params->{save_progress} ? $params->{save_progress} : 0; # Same as 'print_net'
    my $continue      = defined $params->{'continue'} ? $params->{'continue'} : 0;    
    my $print_active  = defined $params->{print_n_species_active} ? $params->{print_n_species_active} : 0;
    my $print_cdat    = defined $params->{print_CDAT} ? $params->{print_CDAT} : 1; # Default is to print .cdat
    my $print_fdat    = defined $params->{print_functions} ? $params->{print_functions} : 0; # Default is to NOT print .fdat
	my $stop_if       = defined $params->{stop_if} ? $params->{stop_if} : undef; # stop condition
	my $print_on_stop = defined $params->{print_on_stop} ? $params->{print_on_stop} : undef; # print at point that stop condition is met
	my $max_sim_steps = defined $params->{max_sim_steps} ? $params->{max_sim_steps} : undef;
	my $output_step_interval = defined $params->{output_step_interval} ? $params->{output_step_interval} : undef;
    
    # continuous options
    my $atol         = defined $params->{atol}         ? $params->{atol}         : 1e-8;
    my $rtol         = defined $params->{rtol}         ? $params->{rtol}         : 1e-8;
    my $sparse       = defined $params->{sparse}       ? $params->{sparse}       : 0;
    my $steady_state = defined $params->{steady_state} ? $params->{steady_state} : 0;

    # stochastic options
#    my $seed         = defined $params->{seed} ? $params->{seed} : floor(rand 2**31);
	if ( not defined $params->{seed} ){ 
    		# do this so that NFsim gets the seed generated by BNG --LAH
    		$params->{seed} = floor(rand 2**31);
	}
	my $seed        = $params->{seed};

    # check method
    unless ( $method )
    {  return "simulate() requires 'method' parameter (ode, ssa, pla, nf).";  }
    if ($method =~ /^ode$/){  $method = 'cvode';  } # Support 'ode' as a valid method
    unless ( exists $METHODS->{$method} )
    {  return "Simulation method '$method' is not a valid option.";  }

    printf "ACTION: simulate( method=>\"%s\" )\n", $method;

    # add optional suffix to output prefix
    if ( defined $suffix )
    {  $prefix .= "_" . $suffix;  }

	# If stop condition is defined add it to the list of functions
	if ($stop_if){
		(my $ref, $err) = $model->ParamList->lookup("_stop_if");
		if (!$ref){
			my $fun = Function->new();
			if ( $err = $fun->readString( "_stop_if() " . $stop_if, $model ) ){  return $err;  }
			# check paramlist for unresolved dependency, etc
			if ( $err = $model->ParamList->check() ){  return $err;  }
			# update netfile since new function was added
			$model->UpdateNet(1)
		}
	}

    # Find binary
    my $binary = $METHODS->{$method}->{binary}; 
    printf "%s simulation using %s\n", $METHODS->{$method}->{type}, $method;
    my $program;
    unless ( $program = findExec($binary) )
    {   return "Could not find executable $binary";   }

    # If method = "nf", call simulate_nf() and return
    if ( $method eq 'nf' ){
	    return $model->simulate_nf( $params );
    }

	# Network simulation
    # Find or Create netfile
    my $netpre;
    if ($netfile)
    {
        # user defined netfile
        # Make sure NET file has proper suffix
        ($netpre = $netfile) =~ s/[.]([^.]+)$//;
        unless ( $1 =~ /net/i )
        {   return "File $netfile does not have .net suffix";   }
    }
    else
    {
        # default to [prefix].net
        $netfile = "${prefix}.net";
        $netpre  = $prefix;

        # Generate NET file if not already created or if updateNet flag is set
        if ( !(-e $netfile) or $model->UpdateNet or defined $params->{prefix} or defined $suffix )
        {
            $err = $model->writeNetwork({include_model=>0, overwrite=>1, prefix=>"$netpre"});
            if ($err) { return $err; }
        }
    }
    
    # Begin writing command: start with program
    my @command = ($program);

    # add output prefix
    push @command, "-o", "$prefix";
    
    # add method to command
    push @command, "-p", "$method";
    
    # pla-specific arguments
    if ($method eq 'pla')
    {
        if (!exists $params->{pla_config}){
        		$params->{pla_config} = "fEuler|pre-neg:sb|eps=0.03";
        		send_warning("'pla_config' not defined, using default: $params->{pla_config}");
        }
        push @command, $params->{pla_config};
        if (defined $params->{pla_output}){
        		push @command, "--pla_output", $params->{pla_output};
        }
    }
    
    # add method options
    {
        my $opts = $METHODS->{$method}->{options};
        if ( exists $opts->{seed} )
        {   # random seed
            push @command, "-h", $seed;
        }
        if ( exists $opts->{atol} )
        {   # absolute tolerance
            push @command, "-a", $atol;
        }
        if ( exists $opts->{rtol} )
        {   # absolute tolerance
            push @command, "-r",  $rtol;
        }
        if ( exists $opts->{sparse} )
        {   # sparse methods
            if ($sparse) { push @command, "-b"; }
        }
        if ( exists $opts->{steady_state} )
        {   # check for steady state
            if ($steady_state) { push @command, "-c"; }
        }
    }

    # define update_interval
    my $update_interval = defined $params->{update_interval} ? $params->{update_interval} : 1.0;

    # define expand
    my $expand =  defined $params->{expand} ? $params->{expand} : 'lazy';
    if ( $expand eq 'lazy' ) { }
    elsif ( $expand eq 'layered' ) { }
    else { return "Unrecognized expand method $expand";    }

    # define maximum # of sim steps
    if (defined $max_sim_steps)
    {   push @command, "-M", $max_sim_steps;  }

    # define output step interval
    if (defined $output_step_interval)
    {   push @command, "-I", $output_step_interval;  }
    
    # stop condition
    if ($stop_if){   
	    	push @command, "--stop_cond", $stop_if;
	    	if ($print_on_stop){
	    		push @command, $print_on_stop;
	    	}
	    	else{
	    		push @command, "1"; # Default is to print on stop
	    	}
    }
    
    # output concentrations data
    push @command, "--cdat", $print_cdat;
    # output function values
    push @command, "--fdat", $print_fdat;

    # define print_net (save_progress)
    if (defined $params->{print_net} && defined $params->{save_progress}){ # Don't let them both be defined
        return "'print_net' and 'save_progress' are the same thing, cannot define both. " 
                . "Please only define one ('save_progress' is preferred).";
    }    
    if ($print_net || $save_progress) { push @command, "-n"; }
    # define print_end
    if ($print_end) { push @command, "-e"; }
    # More detailed output
    if ($verbose)   { push @command, "-v"; }
    # Continuation
    if ($continue)  { push @command, "-x"; }    
    # Print number of active species
    if ($print_active) { push @command, "-j"; }


    # Set start time for trajectory
    my $t_start;
    if ( defined $params->{t_start} )
    {
        $t_start = $params->{t_start};        
        # if this is a continuation, check that model time equals t_start
        if ($continue)
        {
            unless ( defined($model->Time)  and  ($model->Time == $t_start) )
            {  return "t_start must equal current model time for continuation.";  }
        }
    }
    else
    {   # t_start defaults to 0
        if ( $continue   and   defined($model->Time) )
        {  $t_start = $model->Time; }
         else
        {  $t_start = 0.0;  }
    }

    # Set the model time to t_start
    $model->Time($t_start);

      # To preserve backward compatibility: only output start time if != 0
    unless ( $t_start == 0.0 )
    {  push @command, "-i", "$t_start";  }

    # Use program to compute observables
    push @command, "-g", $netfile;

    # Read network from $netfile
    push @command, $netfile;

    # define t_end and n_steps
    my ($n_steps, $t_end);
    if ( (defined $params->{n_steps} || defined $params->{n_output_steps}) && 
         (defined $params->{sample_times} || @sample_times) ){
        # Throw warning if both n_steps and sample_times are defined
        my $x = ( defined $params->{n_steps} ) ? "n_steps" : "n_output_steps";
        send_warning("$x and sample_times both defined. $x takes precedence.");
    }
    if ( defined $params->{n_steps} || defined $params->{n_output_steps} || 
       ( !defined $params->{sample_times} && !@sample_times) )
       {
        if ( defined $params->{n_steps} && defined $params->{n_output_steps} ){ # Don't let them both be defined
            return "'n_steps' and 'n_output_steps' are the same thing, cannot define both. "
                    . "Please only define one ('n_output_steps' is preferred).";
        }

        if ( defined $params->{t_end} ){
            $t_end = $params->{t_end};
        }
        else{
            return "Parameter t_end must be defined.";
        }
        
        if ( ($t_end - $t_start) <= 0.0 )
        {
        	send_warning("t_end (" . $t_end . ") is not greater than t_start (" . $t_start . "). " .
                  	 "Simulation won't run.");
        }
        
        if (defined $params->{n_steps}){
            $n_steps = $params->{n_steps};
        }
        elsif (defined $params->{n_output_steps}){
            $n_steps = $params->{n_output_steps};
        }
        else{
            $n_steps = 1;
        }
        
        my $step_size = ($t_end - $t_start) / $n_steps;
        push @command, $step_size, $n_steps;
    }
    else
    {
        if (defined $params->{sample_times})
        { @sample_times = @{$params->{sample_times}}; }
        @sample_times = sort {$a <=> $b} @sample_times; # numeric sort
        if ( @sample_times > 2 ){
        	# remove all sample times <= t_start --Do this in run_network (LAH)
#        	while ($sample_times[0] <= $t_start){ 
#        		shift @sample_times;
#            }
            if ( defined $params->{t_end} ){
                $t_end = $params->{t_end};
                # remove all sample times >= t_end --Do this in run_network (LAH)
#                while ($sample_times[ $#sample_times ] >= $t_end){ 
#                    pop @sample_times;
#                }
                # push t_end as final sample time
                push @sample_times, $t_end; 
            }
            else{
                $t_end = $sample_times[ $#sample_times ];
            }
            push @command, @sample_times; # add sample times to argument list
        }
        else{
            return "'sample_times' array must contain 3 or more points.";
        }
    }
    
    # Determine index of last rule iteration
    my $n_iter = 0;
    if ( $model->SpeciesList )
    {
        foreach my $spec ( @{$model->SpeciesList->Array} )
        {
            my $iter = $spec->RulesApplied;
            $n_iter = ( $iter > $n_iter ) ? $iter : $n_iter;
        }
    }


    ### RUN SIMULATION ###
    print  "Running run_network on ", `hostname`;
    printf "full command: %s\n", join(" ", @command);

    # disable dospath warnings for Windows OS.
    use Config;
    if ( $Config{'osname'} eq 'MSWin32' )
    {   $ENV{'CYGWIN'}='nodosfilewarning';   }

    # start simulator as child process with communication pipes
    local ( *Reader, *Writer, *Err );
    my $pid = eval{ open3( \*Writer, \*Reader, \*Err, @command ) };
    if ($@) { return sprintf("%s failed: $@", join(" ", @command)); }

    # remember child PID
    $::CHILD_PID = $pid;
    print "[simulation PID is: $pid]\n";

    # Wait for messages from the Simulator
    my $last_msg = '';
    my $steady_state_reached = 0;
    my $edge_warning = 0;
    my $otf = 0;

    while ( my $message = <Reader> )
    {
        # If network generation is on-the-fly, look for signal that
        # species at the edge of the network is newly populated
        if ( $message =~ /Steady state reached/ )
        {   # steady state message
            $steady_state_reached = 1;
        }
        elsif ( $message =~ s/^edgepop:\s*// )
        {
            # remember that we've attempted On-the-fly!
            $otf = 1;

            unless ( $model->SpeciesList )
            {   # Can't generate new species if running from netfile
                # TODO: I don't think it's sufficient to check if SpeciesList is defined.
                #  It's possible that it exists but the Network generation infrastructure is missing --Justin
                ++$edge_warning;
                print Writer "continue\n";
                next;
            }

            # parse edgepop message
            my (@newspec) = split /\s+/, $message;

            my $species;
            ++$n_iter;
            if ( $expand eq 'lazy' )
            {
                my (@sarray, $spec);
                foreach my $sname (@newspec)
                {
                    unless ( $spec = $model->SpeciesList->lookup_bystring($sname) )
                    {  return "Couldn't find species $sname.";  }
                    push @sarray, $spec;
                }
                if ($verbose)
                {  printf "Applying rules to %d species\n", scalar @sarray;  }
                $species = \@sarray;
            }
            else
            {   # Do full next iteration of rule application
                $species = $model->SpeciesList->Array;
            }

            # Apply reaction rules
            my $nspec = $model->SpeciesList->size();
            my $nrxn  = $model->RxnList->size();
            my $irule = 1;
            my ($n_new, $t_off);
            foreach my $rset ( @{$model->RxnRules} )
            {
                if ($verbose) {  $t_off = cpu_time(0);  }
                $n_new = 0;
                foreach my $rr (@$rset)
                {
                    # expand rule
                    my ($err, $nr) = $rr->expand_rule( $species, $model, $params );
                    if (defined $err)
                    {   return "Some problem expanding rule (OTF): $err";   }                    
                    $n_new += $nr;

                }
                if ($verbose)
                {
                    printf "Rule %3d: %3d new reactions %.2e s CPU time\n",
                      $irule, $n_new, cpu_time(0) - $t_off;
                }
                ++$irule;
            }

            # Set RulesApplied attribute to everything in @$species
            foreach my $spec (@$species)
            {
                $spec->RulesApplied($n_iter) unless ($spec->RulesApplied);
            }

            # Set RulesApplied attribute to everything in SpeciesList
            my $new_species = [];
            foreach my $spec ( @{$model->SpeciesList->Array} )
            {
                push @$new_species, $spec  unless ($spec->RulesApplied);
            }

            # Print new species, reactions, and observable entries
            if ( $model->RxnList->size() > $nrxn )
            {
                print Writer "read\n";
                $model->SpeciesList->print( *Writer, $nspec );
                $model->RxnList->print( *Writer, $nrxn );
                print Writer "begin groups\n";
                my $i = 1;
                foreach my $obs ( @{$model->Observables} )
                {
                    print Writer "$i ";
                    $obs->printGroup( *Writer, $model->SpeciesList->Array, $nspec );
                    ++$i;
                }
                print Writer "end groups\n";
            }
            else
            {
                print Writer "continue\n";
            }
        }
        else
        {
            print $message;
            $last_msg = $message;
        }
    }

    # Simulator is finished
    my @err = <Err>;
    close Writer;
    close Reader;
    close Err;
    waitpid( $pid, 0 );

    # clear child pid
    $::CHILD_PID = undef;

    # Report number of times edge species became populated without network expansion
    if ($edge_warning)
    {   send_warning("Edge species of truncated network became populated $edge_warning times.");   }

    if (@err)
    {   # print any errors received from 
        print @err;
        return sprintf("%s\n  did not run successfully.", join(" ", @command));
    }

    unless ( $last_msg =~ /^Program times:/ )
    {   return sprintf("%s\n  did not run successfully.", join(" ", @command));  }



    
    # At this point, the simulation seems to be ok.
    #  Go ahead and print out final netfile (if there are new reactions or species)
    if ( $otf  and  $model->SpeciesList )
    {   # TODO: I don't think it's sufficient to check if SpeciesList is defined.
        #  It's possible that it exists but the Network generation infrastructure is missing --Justin
        $err = $model->writeNetwork({include_model=>0, overwrite=>1, prefix=>"$netpre"});
        if ($err) { return $err; }
    }


    # Report steady state
    if ($steady_state)
    {
        if ($steady_state_reached)
        {   # let user know simulation reached steady state
            print "Simulation reached steady state by t_end=${t_end}\n";
        }
        else
        {   # warn user about failure to acheive steady state
            send_warning("Steady_state status = $steady_state_reached.");
            return "Simulation did not reach steady state by t_end=${t_end}";
        }
    }

    # If there are no errors or flags so far, let's load output concentrations
    if ( !($model->RxnList) )
    {   # TODO: what does this accomplish? --Justin
        send_warning("Not updating species concentrations because no model has been read.");
    }
    elsif ( -e "$prefix.cdat" )
    {
        print "Updating species concentrations from $prefix.cdat\n";
        open CDAT, '<', "$prefix.cdat" ;
        my $last_line = '';
        while (my $line = <CDAT>) {  $last_line = $line;  }
        close CDAT;

        # Update Concentrations with concentrations from last line of CDAT file
        my $conc;
        ($t_end, @$conc) = split ' ', $last_line;
        my $species = $model->SpeciesList->Array;
        unless ( $#$conc == $#$species )
        {
            $err = sprintf "Number of species in model (%d) and CDAT file (%d) differ", scalar @$species, scalar @$conc;
            return $err;
        }
        $model->Concentrations( $conc );
        $model->UpdateNet(1);
    }
    else
    {    return "CDAT file is missing";   }

    # Set model time to t_end
    $model->Time($t_end);

    # all finished!
    return '';
}



###
###
###



sub simulate_nf
{
    use IPC::Open3;

    my $model  = shift @_;
    my $params = shift @_;
    my $err;

    printf "ACTION: simulate_nf( )\n";

    # get simulation output prefix
    my $prefix  = defined $params->{prefix} ? $model->getOutputPrefix( $params->{prefix} ) : $model->getOutputPrefix();
    my $suffix  = defined $params->{suffix} ? $params->{suffix} : "";

    unless ($suffix eq "")
    {  $prefix .= "_${suffix}";  }

    # TODO: detect unrecognized parameters

    # map BNG options to NFsim flags
    my %nfparams =
    (   # option name        type              defaults              simulator flags (one or more)
        binary_output   => { type => 'switch', default_arg => 0,     flags => ["-b"]                      },
        complex         => { type => 'switch', default_arg => 1,     flags => ["-cb"]                     },
        equil           => { type => 'param',  default_arg => undef, flags => ["-eq"]                     },
        get_final_state => { type => 'switch', default_arg => 1,     flags => ["-ss","${prefix}.species"] },
        gml             => { type => 'param',  default_arg => undef, flags => ["-gml"]                    },
        nocslf          => { type => 'switch', default_arg => 0,     flags => ["-nocslf"]                 },
        notf            => { type => 'switch', default_arg => 0,     flags => ["-notf"]                   },
        print_functions => { type => 'switch', default_arg => 0,     flags => ["-ogf"]                    },
        seed            => { type => 'param',  default_arg => undef, flags => ["-seed"]                   },
        utl             => { type => 'param',  default_arg => undef, flags => ["-utl"]                    },
        verbose         => { type => 'switch', default_arg => 0,     flags => ["-v"]                      }
    );

    # get nfsim arguments
    my @args = ();
    while ( my ($arg,$arg_hash) = each %nfparams )
    {
        if ($arg_hash->{type} eq "switch")
        {
            if (defined $params->{$arg})
            {   # user switch
                if ($params->{$arg})
                {   push @args, @{$arg_hash->{flags}};  }
            }
            elsif (defined $arg_hash->{default_arg})
            {   # default switch
                $params->{$arg} = $arg_hash->{default_arg};
                if ($arg_hash->{default_arg})
                {   push @args, @{$arg_hash->{flags}};  }
                
            }
        }
        elsif ($arg_hash->{type} eq "param")
        {
            if (defined $params->{$arg})
            {   # user parameter
                push @args, @{$arg_hash->{flags}}, $params->{$arg};
            }
            elsif (defined $arg_hash->{default_arg})
            {   # user parameter
                push @args, @{$arg_hash->{flags}}, $arg_hash->{default_arg};
                $params->{$arg} = $arg_hash->{default_arg};
            }
        }
    }

    # append other command line arguments not recognized by BNG
    if ( defined $params->{param} )
    {  push @args, split " ", $params->{param};  }

    # exit here if we're not executing
    return '' if $BNGModel::NO_EXEC;

    if ( $model->Params->{no_nfsim} )
    {   # don't execute NFsim if no_nfsim parameter is true
        send_warning( "simulate_nf(): skipping simulation, 'no-nfsim' flag is true.");
        return '';
    }


    # continue with simulation...
#    print "Netfree simulation using NFsim\n";
    my $program;
    unless ( $program = findExec("NFsim") )
    {  return "Could not find executable NFsim";  }
    my @command = ($program);

    # Write BNG xml file
    $model->writeXML( {'prefix'=>$prefix} );

    # Define command line
    push @command, "-xml", "${prefix}.xml", "-o", "${prefix}.gdat";

    # Append the run time and output intervals
    my $t_start;
    if (defined $params->{t_start})
    {
        $t_start = $params->{t_start};
        $model->Time($t_start);
    }
    else
    {
        $t_start = defined $model->Time ? $model->Time : 0;
    }


    unless ($t_start == 0)
    {   # warn user if t_start > 0
        send_warning("simulate_nf(): NFsim timepoints are reported as time elapsed since t_start=$t_start.");
    }

    if ($params->{continue})
    {   # warn user that continue is not supported
#        send_warning("simulate_nf(): NFsim does not support 'continue' option. NFsim will overwrite any existing trajectories.");
        return "NFsim does not support 'continue' option.";
    }

    my $t_end;
    if ( defined $params->{n_steps} )
    {
        my ($n_steps, $t_end);
        $n_steps = $params->{n_steps};
        if ( $n_steps < 1 )
        {   return "No simulation output requested: set n_steps>0";   }

        if ( defined $params->{t_end} )
        {   $t_end = $params->{t_end};   }
        else
        {   return "Parameter t_end must be defined";   }

        unless ( $t_end > $t_start )
        {   return "t_end must be greater than t_start.";   }

        push @command, "-sim", ($t_end-$t_start), "-oSteps", $n_steps;
    }
    elsif ( defined $params->{sample_times} )
    {
        return "sample_times not supported in this version of NFsim";
    }
    else
    {
        return "No simulation output requested: set n_steps>0";
    }


    # Append the other command line arguments
    push @command, @args;


    # Run NFsim
    print "Running NFsim on ", `hostname`;
    printf "full command: %s\n", join(" ", @command);

    # Compute timecourses using nfsim
    # start simulator as child process with communication pipes
    local ( *Reader, *Writer, *Err );
    my $pid = eval{ open3( \*Writer, \*Reader, \*Err, @command ) };
    if ($@) { return sprintf("%s failed: $@", join(" ", @command)); }

    # remember child PID
    $::CHILD_PID = $pid;
    print "[simulation PID is: $pid]\n";

    my $last = '';
    while ( <Reader> )
    {
        print;
        $last = $_;
    }
    ( my @err = <Err> );

    close Writer;
    close Reader;
    close Err;
    waitpid( $pid, 0 );

    # clear child pid
    $::CHILD_PID = undef;

    if (@err) {
        print "Error log:\n", @err;
		foreach my $e (@err){
			unless ( $e =~ /^\s*WARNING/i ){
	        	return sprintf("%s did not run successfully.", join(" ", @command));
			}
	    }
    }

    if ( $params->{get_final_state} )
    {   # Update final species concentrations to allow trajectory continuation
        if (my $err = $model->readNFspecies("${prefix}.species"))
        {  return $err;  }
        #if ( $params->{verbose} )
        #{  print $model->SpeciesList->writeBNGL( $model->Concentrations, $model->ParamList );  }
    }
    else
    {
        send_warning( "system state was not retrieved following simulate_nf. "
                     ."To retreive system state, call simulate_nf with option: get_final_state=>1." );
    }

    $model->Time($t_end);
    return '';
}



###
###
###



sub readNFspecies 
{
    # This function reads a list of species strings from NFsim output to form a 
    # canonical species list with correct concentrations. Note that it overwrites
    # any existing species.
    my $model = shift @_;
    my $fname = shift @_;

    my $conc_vec;
    if ($model->SpeciesList)
    {
        $conc_vec = [ (0) x scalar @{$model->SpeciesList->Array} ];
    }
    else
    {        
        $conc_vec = [];
        # create species list
        $model->SpeciesList( SpeciesList->new );
    }
    my $slist = $model->SpeciesList;

    # tell SpeciesLabel to use Quasi method for species w/ large number of molecules
    my $maxMols = 20;
    my $save_maxMols = SpeciesGraph::getSpeciesLabelMethod_MaxMols();
    SpeciesGraph::setSpeciesLabel( SpeciesGraph::getSpeciesLabelMethod(), $maxMols );

    # Read NFsim species file
    print "readNFspecies::Reading from file $fname\n";
    #my $FH;
    open(my $FH, "<", $fname)
        or return "Couldn't read from file $fname: $!";

    my $n_spec_read = 0;
    my $n_spec_new = 0;
    my $line_num = 0;
    while ( my $string = <$FH> )
    {
        ++$line_num;
        chomp $string;                 # remove line return
        $string =~ s/^\s+//;           # remove leading whitespace
        $string =~ s/\#.*$//;          # remove comments
        next unless $string =~ /\S+/;  # skip blank lines

        # Read species string
        my $sg = SpeciesGraph->new();
        my $err = $sg->readString( \$string, $model->CompartmentList, 1, '^\s+', 
                                    $model->MoleculeTypesList );
        if ($err) {  return $err." at line $line_num of file $fname";  }

        # Read species concentration - may only be integer value
        my $conc;
        if ( $string=~ /^\s*(\d+)\s*$/ )
        {   $conc = $1;   }
        else
        {   return "species concentration must be single integer at line $line_num of file $fname";   }

        # Check if isomorphic to existing species 
        my $existing_sg = $model->SpeciesList->lookup( $sg );
        if ($existing_sg)
        { 
            # Add concentration to concentration of existing species
            $conc_vec->[$existing_sg->Index - 1] += $conc;
        }
        else
        {
            # Create new Species entry in SpeciesList with zero default concentration
            my $newspec = $model->SpeciesList->add( $sg, 0 );
            $conc_vec->[ $newspec->Index - 1 ] = $conc;
            ++$n_spec_new;
        }
        ++$n_spec_read;
    }
    close $FH;

    $model->Concentrations( $conc_vec );
    printf "Read %d unique species of %d total.\n", $n_spec_new, $n_spec_read;

    # return SpeciesLable method to original setting
    SpeciesGraph::setSpeciesLabel( SpeciesGraph::getSpeciesLabelMethod(), $save_maxMols );

    return '';
}



###
###
###



# construct a hybrid particle population (HPP) model
#  --Justin, 21mar2011
sub generate_hybrid_model
{
    my $model        = shift;
    my $user_options = shift;


    my $indent = '    ';
    my $step_index = 0;
    printf "ACTION: generate_hybrid_model( %s )\n", $model->Name;


    # default options
    my $options =
    {   'prefix'       => undef,
        'suffix'       => 'hpp',
        'overwrite'    => 0,
        'verbose'      => 0,
        'actions'      => ['writeXML()'],
        'execute'      => 0,
        'safe'         => 0
    };
    # get user options
    foreach my $opt (keys %$user_options)
    {
        my $val = $user_options->{$opt};
        if ($opt eq "exact")
        {   # TODO: temporary patch to allow the old "exact" option
            send_warning("The 'exact' option has been renamed 'safe', please use this in the future.");
            $opt = "safe";
        }

        unless ( exists $options->{$opt} )
        {   return "Unrecognized option $opt in call to generate_hybrid_model";   }
        
        # overwrite default option
        $options->{$opt} = $val;
    }
    # print options
    print "options = \n";
    while ( my ($par,$val) = each %$options )
    {   
        next unless (defined $val);
        if ( ref $val eq 'ARRAY')
        {   printf( "%12s => %-60s\n", $par, "[array]" );   }
        elsif ( ref $val eq 'HASH')
        {   printf( "%12s => %-60s\n", $par, "{hash}" );   }
        elsif ( ref \$val eq 'SCALAR')
        {   printf( "%12s => %-60s\n", $par, $val );   }
    }

    # do nothing if $NO_EXEC is true
    return '' if $BNGModel::NO_EXEC;

    # determine HPP model name
    # (1) if prefix is defined, try to extract the file basename
    # (2) otherwise use the name of the parent model
#    my $modelname;
#    my $outdir;
#    if (defined $options->{prefix})
#    {
#        my ($vol,$dir,$filebase) = File::Spec->splitpath($options->{prefix});
#        if ($filebase eq '')
#        { return sprintf "Prefix value '%s' does not end with a file basename", $options->{prefix}; }
#        $outdir = File::Spec->catpath($vol, $dir);
#        $modelname = $filebase;
#    }
#    else
#    {   
 #   	$outdir = defined $options->{output_dir} ? $options->{output_dir} : $model->getOutputDir();
#        $modelname = $model->Name;
#    }
    # add suffix
#    $modelname .= "_" . $options->{suffix};


    # define prefix
#    my $prefix = defined $options->{prefix} ? $options->{prefix} : File::Spec->catfile($outdir, $modelname);
    my $prefix = defined $options->{prefix} ? $model->getOutputPrefix( $options->{prefix} ) : $model->getOutputPrefix();
	$prefix .= "_" . $options->{suffix};

	# define outdir and modelname
	my ($vol,$dir,$modelname) = File::Spec->splitpath($prefix);
	my $outdir = File::Spec->catpath($vol, $dir);
	
    # define filename
#    my $modelfile = $modelname . ".bngl";
	my $modelfile = $prefix . ".bngl";

    if ( -e $modelfile )
    {
        if ($options->{overwrite})
        {
            send_warning( "Overwriting older model file: $modelfile" );
            unlink $modelfile;
        }
        else
        {
            return "Model file $modelfile already exists. Set overwrite=>1 option to force overwrite.";
        }
    }

    # check if a ParamList exists
    unless ( defined $model->ParamList )
    {   return sprintf "Cannot continue! Model %s does not have a parameter list.", $model->Name;   }

    # Check for MoleculeTypes
    unless ( defined $model->MoleculeTypesList  and  %{$model->MoleculeTypesList->MolTypes} )
    {   return sprintf "Nothing to do! Model %s has zero molecule type definitions.", $model->Name;   }     

    # check if a SpeciesList exists
    unless ( defined $model->SpeciesList  and  @{$model->SpeciesList->Array} )
    {   return sprintf "Nothing to do! Model %s has zero seed species definitions.", $model->Name;   }

    # Check for RxnRules
    unless ( defined $model->RxnRules  and  @{$model->RxnRules} )
    {   return sprintf "Nothing to do! Model %s has zero reaction rule definitions.", $model->Name;   } 

    # check if PopulationTypesList exists
    unless ( defined $model->PopulationTypesList  and  %{$model->PopulationTypesList->MolTypes} )
    {   return sprintf "Nothing to do! Model %s has zero population type definitions.", $model->Name;   }

    # check if PopulationList exists
    unless ( defined $model->PopulationList  and  @{$model->PopulationList->List} )
    {   return sprintf "Nothing to do! Model %s has zero population map definitions.", $model->Name;   }
   
    
    # create new model!
    my $hybrid_model = BNGModel::new();
    
    $hybrid_model->Name( $modelname );
    $hybrid_model->Version( $model->Version );
    $hybrid_model->SubstanceUnits( $model->SubstanceUnits );

    # copy options    
    %{$hybrid_model->Options} = %{$model->Options};
    # set prefix
    $hybrid_model->Options->{prefix} = $prefix;
    #set output_dir
	$hybrid_model->setOutputDir($outdir);
	
    # copy the constants in the parameter list
    #  NOTE: we'll add observable and functions later
    print $indent . "$step_index:Fetching model parameters.. ";  ++$step_index;
    my $plist_new = $model->ParamList->copyConstant();
    $hybrid_model->ParamList( $plist_new );
    print sprintf "found %d constants and expressions.\n", scalar @{$plist_new->Array};
    
    
    # Copy compartments
    my $clist_new = undef;
    if ( defined $model->CompartmentList )
    {
        print $indent . "$step_index:Fetching compartments.. "; ++$step_index;
        $clist_new = $model->CompartmentList->copy( $plist_new );
        $hybrid_model->CompartmentList( $clist_new );
        print $indent . sprintf "found %d compartments.\n", scalar @{$clist_new->Array};
        send_warning( "generate_hybrid_model() does not support compartments at this time." ) if (@{$clist_new->Array});  
    }
    
    
    
    # Copying the moleculeTypesList and add population types
    print $indent . "$step_index:Fetching molecule types..   "; ++$step_index;
    my $mtlist_new =  $model->MoleculeTypesList->copy();
    $hybrid_model->MoleculeTypesList( $mtlist_new );
    print sprintf "found %d molecule types.\n", scalar keys %{$mtlist_new->MolTypes};
    {
        # Add population types
        print $indent . "$step_index:Adding population types..   "; ++$step_index;
        foreach my $name ( keys %{$model->PopulationTypesList->MolTypes} )
        {
            my $mt = $model->PopulationTypesList->MolTypes->{$name};
            my $mt_copy = $mt->copy();
            $mt_copy->PopulationType(1);
            unless ( $mtlist_new->add($mt_copy) )
            {   return "PopulationType $name clashes with MoleculeType of the same name";  }
        }
        print sprintf "found %d population types.\n", scalar keys %{$model->PopulationTypesList->MolTypes};
    }


    # Copy seed species, replacing with populations if possible, and add empty populations
    my $slist_new = SpeciesList::new();
    $hybrid_model->SpeciesList( $slist_new );   
    {    
        print $indent . "$step_index:Fetching seed species..\n"; ++$step_index; 
 
        # loop over species in species list
        foreach my $species ( @{$model->SpeciesList->Array} )
        {
            my $sg   = $species->SpeciesGraph;
            my $conc = $species->Concentration;
        
            # check if this is isomorphic to any of our populations
            my $is_pop = 0;
            foreach my $pop ( @{$model->PopulationList->List} )
            {
                if ( SpeciesGraph::isomorphicTo($species->SpeciesGraph, $pop->SpeciesGraph) )
                {   # add the population instead of the speciesGraph
                    my $sg_copy = $pop->Population->copy();
                    $sg_copy->relinkCompartments( $hybrid_model->CompartmentList );
                    $slist_new->add( $sg_copy, $species->Concentration );
                    $is_pop = 1;
                    if ( $options->{verbose} )
                    {
                        print $indent.$indent
                            . sprintf "replaced species %s with population %s.\n", $sg->toString(), $sg_copy->toString();
                    }
                    last;
                }
            }
            unless ($is_pop)
            {   # this isn't a population, so add SpeciesGraph directly.
                my $sg_copy = $species->SpeciesGraph->copy();
                $sg_copy->relinkCompartments( $hybrid_model->CompartmentList );
                $slist_new->add( $sg_copy, $species->Concentration );
            }
        }
        print $indent . sprintf "  ..found %d seed species.\n", scalar @{$slist_new->Array};    
    }

    
    # Add population species to seed species
    {
        print $indent . "$step_index:Adding populations with zero counts to seed species..\n"; ++$step_index;     
        my $zero_pops = 0;
        foreach my $pop ( @{$model->PopulationList->List} )
        {
            my ($sp) = $slist_new->lookup( $pop->Population );
            unless ( $sp )
            {
                my $sg_copy = $pop->Population->copy();
                $sg_copy->relinkCompartments( $hybrid_model->CompartmentList );  
                $slist_new->add( $sg_copy, 0 );
                ++$zero_pops;
            }
        }
        print $indent . sprintf "  ..added %d populations to seed species list.\n", $zero_pops;      
    }
            

    # Copy the observables and add matches to populations (also register observable names in parameter list)
    my $obslist_new = [];
    $hybrid_model->Observables( $obslist_new );
    {
        print $indent . "$step_index:Fetching observables and adding population matches..\n"; ++$step_index;         
        # loop over observables
        foreach my $obs ( @{$model->Observables} )
        {
            my $obs_copy = $obs->copy();
            $obs_copy->relinkCompartments( $hybrid_model->CompartmentList );
            push @{$obslist_new}, $obs_copy;
        
            # get a parameter that points to this observable
            if ( $plist_new->set( $obs_copy->Name, '0', 1, "Observable", $obs_copy) )
            {
                  my $name = $obs_copy->Name;
                return "Observable name $name clashes with previously defined Observable or Parameter";
            }
      
            # find populations to add to observable
            my @add_patterns = ();
            foreach my $pop ( @{$model->PopulationList->List} )
            {
                my $matches = $obs_copy->match( $pop->SpeciesGraph );
            
                if ($matches)
                {   
                    my $ii = 0;
                    while ( $ii < $matches )
                    {
                        push @add_patterns, $pop->Population->copy()->relinkCompartments( $hybrid_model->CompartmentList );
                        ++$ii
                    }
                    if ( $options->{verbose} )
                    {
                        print $indent.$indent . sprintf "observable '%s':  +%d match%s to %s.\n",
                                                        $obs_copy->Name, $matches, ($matches>1 ? 'es' : ''), $pop->Population->toString();
                    }
                }
            }      
            push @{$obs_copy->Patterns}, @add_patterns;      
        }
        print $indent . sprintf "  ..found %d observables.\n", scalar @{$obslist_new};
    }
    
    
    # Copy functions
    {
        print $indent . "$step_index:Fetching functions.. "; ++$step_index;     
        my $fcn_copies = $model->ParamList->copyFunctions();
        foreach my $fcn ( @$fcn_copies )
        {
            $hybrid_model->ParamList->set( $fcn->Name, $fcn->Expr, 0, 'Function', $fcn );
        }
        print sprintf "found %d functions.\n", scalar @{$fcn_copies};
    }
   

    # Expand rules
    my $rxnrules_new = [];
    $hybrid_model->RxnRules( $rxnrules_new );
    {
        print $indent . "$step_index:Expanding rules with respect to population objects..\n"; ++$step_index;   
    
        # get the species graphs corresponding to each population
        my $pop_species = [];
        foreach my $pop ( @{$model->PopulationList->List} )
        {   push @$pop_species, $pop->SpeciesGraph;   }
        my $n_popspec = scalar @$pop_species;
    
        # loop over rules
        my $rule_count = 0;
        foreach my $rset ( @{$model->RxnRules} )
        {
            # NOTE: each element of @RxnRules is an array of rules.
            #  If a rule is unidirectional, then the array has a single element.
            #  If a rule is bidirectional, then the array has two elements (forward and reverse)    
            foreach my $rr (@$rset)
            {    
                # first copy the rule so we don't mess with the orginal model
                my $rr_copy = $rr->copy();
                $rr_copy->resetLabels();
            
                # apply rule to population species
                my $child_rule_list = $rr_copy->expandRule(  $pop_species, $model, $hybrid_model, $options );
                foreach my $child_rule ( @$child_rule_list )
                {
                    push @$rxnrules_new, [$child_rule];
                }
                if ( $options->{verbose} )
                {
                    print $indent.$indent . sprintf "Rule '%s': expanded to %d child rule%s.\n",
                                                    $rr_copy->Name, scalar @$child_rule_list, ((scalar @$child_rule_list > 1)?'s':'');
                }
                ++$rule_count;
            }
        }
        print $indent . sprintf "  ..finished processing %d reaction rules.\n", $rule_count;
    }


    # Add population maps to the list of rules
    {
        print $indent . "$step_index:Fetching population maps.. "; ++$step_index;
        foreach my $pop ( @{$model->PopulationList->List} )
        {
            # write rule as string
            my $rr_string = $pop->MappingRule->toString();
            # remove the linebreak
            $rr_string =~ s/\\\s//;
            # parse string to create "copy" of rule
            my ($rrs, $err) = RxnRule::newRxnRule( $rr_string, $hybrid_model );
            push @$rxnrules_new, $rrs;
        }
        print sprintf "found %d maps.\n", scalar @{$model->PopulationList->List};
    }


    # create empty RxnList
    print $indent . "$step_index:Creating empty reaction list.\n"; ++$step_index;        
    my $rxnlist_new = RxnList::new();
    $hybrid_model->RxnList( $rxnlist_new );
    

    # Print hybrid model to file
    my $FH;
    print $indent . "$step_index:Attempting to write hybrid BNGL.. "; ++$step_index;        
    unless ( open $FH, '>', $modelfile ) {  return "Couldn't write to $modelfile: $!\n";  }

    print $FH $hybrid_model->writeBNGL( {'format'=>'bngl', 'include_model'=>1,'include_network'=>0,
                                         'pretty_formatting'=>1,'evaluate_expressions'=>0 } );
    # writing actions!
    if ( @{$options->{actions}} )
    {
        my $action_string = "\n\n## model actions ##\n";
        foreach my $action ( @{$options->{actions}} )
        {
            $action_string .= "$action\n";
        }
        $action_string .= "\n";
        print $FH $action_string;
    }
    close $FH;
    
    
    print "done.\n";
    print "Wrote hybrid model to file $modelfile.\n";
    
    
    if ( $options->{execute} )
    {   # execute actions
        $BNGModel::GLOBAL_MODEL = $hybrid_model;
        my $errors = [];
        foreach my $action ( @{$options->{actions}} )
        {
            my $action_string = "\$hybrid_model->$action";
            my $err = eval "$action_string";
            if ($@)   {  warn $@;  }
            if ($err) {  push @$errors, $err;  }
        }
        $BNGModel::GLOBAL_MODEL = $model;
        if (@$errors) {  return join "\n", $errors;  }
    }
    
    return '';
}



###
###
###


sub bifurcate
{
	my $model = shift @_;
    my $params = @_ ? shift @_ : {};
    
    my @scanfiles = ();
    my ($i,$j,$err);
    
    # don't reset concentrations after each run
    $params->{reset_conc} = 0;
    
    # update user
    printf "ACTION: bifurcate(par: %s, min: %s, max: %s, n_pts: %s, log: %s)\n", 
    		(exists $params->{parameter}  ? $params->{parameter}  : "UNKNOWN"),
    		(exists $params->{par_min}    ? $params->{par_min}    : "UNKNOWN"),
    		(exists $params->{par_max}    ? $params->{par_max}    : "UNKNOWN"),
    		(exists $params->{n_scan_pts} ? $params->{n_scan_pts} : "UNKNOWN"),
    		(exists $params->{log_scale}  ? $params->{log_scale}  : 0);
    		    
    # forward scan
    if (exists $params->{suffix}){ $params->{suffix} .= "_forward"; }
    else{ $params->{suffix} = "forward"; }
    $err = $model->parameter_scan( $params );
    if ($err){ return $err }
    push @scanfiles, $params->{scanfile}; # 'outfile' param set in parameter_scan
    
    # backwards scan
    $params->{suffix} =~ s/forward/backward/;
    my $par_min = $params->{par_min};
    $params->{par_min} = $params->{par_max};
    $params->{par_max} = $par_min;
    $err = $model->parameter_scan( $params );
    if ($err){ return $err }
    push @scanfiles, $params->{scanfile}; # 'outfile' param set in parameter_scan
    
    # extract forward scan data
    my @forward;
    open FILE, $scanfiles[0] or die "Couldn't open file: $!"; 
    my $line = <FILE>; # first line
    chomp $line;
    $line =~ s/^\s*#\s+//; # remove leading # and whitespace
    my @header = split('\s+',$line); # scan param + observable names
    $i = 0;
	while ($line = <FILE>){
		chomp $line;
		$line =~ s/^\s*//; # remove leading whitespace
		my @tmp = split('\s+',$line);
		for ($j=0;$j < scalar(@tmp);$j++){
			$forward[$i][$j] = $tmp[$j];
		}
		$i++;
	}
	close FILE;
	
	# extract backward scan data
	my @backward;
	open FILE, $scanfiles[1] or die "Couldn't open file: $!"; 
    $line = <FILE>; # first line
    $i = 0;
	while ($line = <FILE>){
		chomp $line;
		$line =~ s/^\s*//; # remove leading whitespace
		my @tmp = split('\s+',$line);
		for ($j=0;$j < scalar(@tmp);$j++){
			$backward[$i][$j] = $tmp[$j];
		}
		$i++;
	}
	close FILE;

	# generate one output file for each observable
    my $prefix = $scanfiles[0];
    $prefix =~ s/_forward.scan$/_bifurcation_/;
    for (my $j=1;$j < scalar(@header);$j++){
    		open FILE, '>', ($prefix . $header[$j] . ".scan") or die "Couldn't open file: $!";
		printf FILE "# %+14s %+16s %+16s\n",$header[0],"$header[$j]_fwd","$header[$j]_bwd";
		for (my $i=0;$i < scalar(@forward);$i++){
			printf FILE "%16.8e %16.8e %16.8e\n",$forward[$i][0],$forward[$i][$j],$backward[scalar(@forward)-1-$i][$j];
		}
		close FILE;
    }
	
	# delete scan files and return
    unlink @scanfiles;
	return;
}



###
###
###


sub parameter_scan
{
    my $model = shift @_;
    my $params = @_ ? shift @_ : {};

    # define default params
    my $default_params = {  'prefix'   	=> $model->getOutputPrefix(),
                            'log_scale'	=> 0,
                            'reset_conc' => 1
                         };

    # copy default values for undefined keys
    while ( my ($key, $val) = each %$default_params )
    {
        unless ( defined $params->{$key} )
        {   $params->{$key} = $val;   }
    }

	# If resetting concentrations, don't need to read in final state
	# (important for NFsim simulations, where read can be expensive)
	if ( $params->{'reset_conc'} ){
		$params->{'get_final_state'} = 0;
	}
    
    # Output prefix
   	$params->{prefix} = $model->getOutputPrefix($params->{prefix});

    # check for required parameters
    unless ( defined $params->{parameter} )
    {   return "Error in parameter_scan: 'parameter' is not defined.";   }

	unless ( defined $params->{par_scan_vals} ){
		
	    unless ( defined $params->{par_min} )
	    {   return "Error in parameter_scan: 'par_min' must be defined if 'par_scan_vals' is not.";   }
	
	    unless ( defined $params->{par_max} )
	    {   return "Error in parameter_scan: 'par_max' must be defined if 'par_scan_vals' is not.";   }
	    
	    unless ( defined $params->{n_scan_pts} )
	    {   return "Error in parameter_scan: 'n_scan_pts' must be defined if 'par_scan_vals' is not.";   }
	    
	    if ($params->{par_max} == $params->{par_min}){
		    	if ($params->{n_scan_pts} < 1){
		    		return "Error in parameter_scan: 'n_scan_pts' must be >= 1 if 'par_max' = 'par_min'.";
		    	}
	    }
	    elsif ($params->{n_scan_pts} <= 1){
		    	return "Error in parameter_scan: 'n_scan_pts' must be > 1 if 'par_max' != 'par_min'.";
	    }
	}

	# defined min/max takes precedence over par_scan_vals
	if ( defined $params->{par_min} and defined $params->{par_max} and defined $params->{n_scan_pts} ){
		# define parameter scan range
	    my $par_min = $params->{log_scale} ? log $params->{par_min} : $params->{par_min};
	    my $par_max = $params->{log_scale} ? log $params->{par_max} : $params->{par_max};
	    my $delta = ($par_max - $par_min) / ($params->{n_scan_pts} - 1); # note that this may be negative if par_max < par_min (not a problem)
		
		# add parameter values to 'par_scan_vals'
		$params->{par_scan_vals} = ();
		for ( my $k = 0;  $k < $params->{n_scan_pts};  ++$k ){
			my $par_value = $par_min + $k*$delta;
	        if ( $params->{log_scale} )
	        {   $par_value = exp $par_value;   }
	        push @{$params->{par_scan_vals}}, $par_value;
		}
	}
	
	# array of parameter scan values
	my @par_scan_vals = @{$params->{par_scan_vals}};

    # update user
#    printf "ACTION: parameter_scan(par: $params->{parameter}, min: $params->{par_min}, max: $params->{par_max}, ";
#    printf "n_pts: $params->{n_scan_pts}, log: $params->{log_scale})\n";
	printf "ACTION: parameter_scan( )";

    # define basename for scan results
    my $basename = $params->{prefix};
    if ( $params->{suffix} )
    {   $basename .= "_" . $params->{suffix};   }
    else
    {   $basename .= "_" . $params->{parameter};      }

    # define working directory for simulation data
    my $workdir = $basename;
    # define output file for parameter scan results
    my $outfile = $basename . ".scan";
    # define file prefix for output results
    my ($vol, $dir, $file_prefix) = File::Spec->splitpath( $basename );

    # create working directory
    if (-d $workdir)
    {   # delete working directory
        my $all_files = File::Spec->catfile( ($workdir), '*' );
        unlink <$all_files>;
        #system "rm -r $workdir";
    }
    else
    {   mkdir $workdir;   }

    # remember concentrations!
    $model->saveConcentrations("SCAN");

    # loop over scan points
	for ( my $k = 0;  $k < @par_scan_vals;  ++$k )
    {
        # define prefix
        my $local_prefix = File::Spec->catfile( ($workdir), sprintf("%s_%05d", $file_prefix, $k+1) );
    	
        # define parameter value
        my $par_value = $par_scan_vals[$k];

        # set parameter
        $model->setParameter( $params->{parameter}, $par_value );

        # reset concentrations
        if ( $params->{reset_conc} ){
        		$model->resetConcentrations("SCAN");
        }

        # define local params
        my $local_params;
        %$local_params = %$params;
        $local_params->{prefix} = $local_prefix;
        delete $local_params->{suffix};

        # run simulation
        my $err = $model->simulate( $local_params );
        if ( $err )
        {   # return error message
            $err = "Error in parameter_scan (step " . ($k+1) . "): $err";
            return $err;
        }   
    }

    # recover concentrations
    if ( $params->{reset_conc} ){
    		$model->resetConcentrations("SCAN");
    }

    # Extract last timepoint from each simulation and write to outfile
    my $ofh;
    unless ( open $ofh, '>', $outfile )
    {   return "Error in parameter_scan: problem opening parameter scan output file $outfile";   }

    for ( my $k = 0;  $k < @par_scan_vals;  ++$k )
    {
        my $par_value = $par_scan_vals[$k];

        # Get data from gdat file
        my $data_file = File::Spec->catfile( ($workdir), sprintf("%s_%05d.gdat", $file_prefix, $k+1) );
        print "Extracting observable trajectory from $data_file\n";
        my $ifh;
        unless ( open $ifh,'<', $data_file )
        {   return "Error in parameter_scan: problem opening observable file $data_file";   }

        # write header
        if ( $k == 0 )
        {
            my $headline = <$ifh>;
            $headline =~ s/^\s*\#//;
            my @headers = split ' ', $headline;
            shift @headers;
            printf $ofh "# %+14s", $params->{parameter};
            foreach my $header (@headers)
            {
                printf $ofh " %+16s", $header;
            }
            print $ofh "\n";
        }

        # get last line
        my $lastline;
        while ( my $linein = <$ifh> )
        {   $lastline = $linein;   }
        
        # close input file
        close $ifh;

        # extract data and write to output file
        my @data = split ' ', $lastline;
        my $time = shift @data;
        printf $ofh "%16.8e", $par_value;
        foreach my $data ( @data )
        {
            printf $ofh " %16.8e", $data;
        }
        print $ofh "\n";
    }  
    close $ofh;

    # return without error
    $params->{scanfile} = $outfile; # in case another method needs this (e.g. 'bifurcate')
	return;
}



###
###
###



sub LinearParameterSensitivity
{
    #This function will perform a brute force linear sensitivity analysis
    #bumping one parameter at a time according to a user specified bump
    #For each parameter, simulations are saved as:
    #'netfile_paramname_suffix.(c)(g)dat', where netfile is the .net model file
    #and paramname is the bumped parameter name, and c/gdat files have meaning as normal

    ######################
    # TODO: NOT IMPLEMENTED YET!!
    #Additional files are written containing the raw sensitivity coefficients
    #for each parameter bump
    #format: 'netfile_paramname_suffix.(c)(g)sc'
    #going across rows is increasing time
    #going down columns is increasing species/observable index
    #first row is time
    #first column is species/observable index
    ######################

    #Starting time assumed to be 0

    #Input Hash Elements:
    #REQUIRED PARAMETERS
    #net_file:  the base .net model to work with; string;
    #t_end:  the end simulation time; real;
    #OPTIONAL PARAMETERS
    #bump:  the percentage parameter bump; real; default 5%
    #inp_ppert:  model input parameter perturbations; hash{pnames=>array,pvalues=>array};
    #default empty
    #inp_cpert:  model input concentration perturbations; hash{cnames=>array,cvalues=>array};
    #default empty
    #stochast:  simulate_ssa (1) or simulate_ode (0) is used; boolean; default 0 (ode)
    #CANNOT HANDLE simulate_ssa CURRENTLY
    #sparse:    use sparse methods for integration?; boolean; 1
    #atol:  absolute tolerance for simulate_ode; real; 1e-8
    #rtol:  relative tolerance for simulate_ode; real; 1e-8
    #init_equil:  equilibrate the base .net model; boolean; default 1 (true)
    #re_equil:  equilibrate after each parameter bump but before simulation; boolean; default 1 (true)
    #n_steps:  the number of evenly spaced time points for sensitivity measures; integer;
    #default 50
    #suffix:  added to end of filename before extension; string; default ""

    #Variable Declaration and Initialization
    use strict;
    my $model;     #the BNG model
    my %params;    #the input parameter hash table
    my $net_file = "";
    my %inp_pert;
    my $t_end;
    my %readFileinputs;
    my %simodeinputs;
    my $simname;
    my $basemodel = BNGModel->new();
    my $plist;
    my $param_name;
    my $param_value;
    my $new_param_value;
    my $pperts;
    my $cperts;
    my $pert_names;
    my $pert_values;
#    my $pert_names;
#    my $pert_values;
    my $newbumpmodel = BNGModel->new();
    my $foo;
    my $i;

    #Initialize model and input parameters

    $model  = shift;
    my $params = shift;

    #Required params
    if ( defined( $params->{net_file} ) ) {
        $net_file = $params->{net_file};
    }
    else {
        $net_file = $model->getOutputPrefix();
    }
    if ( defined( $params->{t_end} ) ) {
        $t_end = $params->{t_end};
    }
    else {
        return ("t_end not defined");
    }

    #Optional params
    my $bump     = ( defined( $params->{bump} ) )     ? $params->{bump}     : 5;
    my $stochast = ( defined( $params->{stochast} ) ) ? $params->{stochast} : 0;
    my $sparse   = ( defined( $params->{sparse} ) )   ? $params->{sparse}   : 1;
    my $atol = ( defined( $params->{atol} ) ) ? $params->{atol} : 1e-8;
    my $rtol = ( defined( $params->{rtol} ) ) ? $params->{rtol} : 1e-8;
    my $init_equil =
      ( defined( $params->{init_equil} ) ) ? $params->{init_equil} : 1;
    my $t_equil = ( defined( $params->{t_equil} ) ) ? $params->{t_equil} : 1e6;
    my $re_equil = ( defined( $params->{re_equil} ) ) ? $params->{re_equil} : 1;
    my $n_steps = ( defined( $params->{n_steps} ) ) ? $params->{n_steps} : 50;
    my $suffix  = ( defined( $params->{suffix} ) )  ? $params->{suffix}  : "";

    #Run base case simulation
    %readFileinputs = ( file => "$net_file.net" );
    $basemodel->readFile( \%readFileinputs );

    #if initial equilibration is required
    if ($init_equil) {
        $simname      = "_baseequil_";
        %simodeinputs = (
            prefix       => "$net_file$simname$suffix",
            t_end        => $t_equil,
            sparse       => $sparse,
            n_steps      => $n_steps,
            steady_state => 1,
            atol         => $atol,
            rtol         => $rtol
        );
        $basemodel->simulate_ode( \%simodeinputs );
    }
    $simname      = "_basecase_";
    %simodeinputs = (
        prefix       => "$net_file$simname$suffix",
        t_end        => $t_end,
        sparse       => $sparse,
        n_steps      => $n_steps,
        steady_state => 0,
        atol         => $atol,
        rtol         => $rtol
    );

    #Implement input perturbations
    if ( defined( $params->{inp_ppert} ) ) {
        $pperts      = $params->{inp_ppert};
        $pert_names  = $pperts->{pnames};
        $pert_values = $pperts->{pvalues};
        $i           = 0;
        while ( $pert_names->[$i] ) {
            $param_name  = $pert_names->[$i];
            $param_value = $pert_values->[$i];
            $basemodel->setParameter( $param_name, $param_value );
            $i = $i + 1;
        }
    }
    if ( defined( $params->{inp_cpert} ) ) {
        $cperts      = $params->{inp_cpert};
        $pert_names  = $cperts->{cnames};
        $pert_values = $cperts->{cvalues};
        $i           = 0;
        while ( $pert_names->[$i] ) {
            $param_name  = $pert_names->[$i];
            $param_value = $pert_values->[$i];
            $basemodel->setConcentration( $param_name, $param_value );
            $i = $i + 1;
        }
    }
    $basemodel->simulate_ode( \%simodeinputs );

    $plist = $basemodel->ParamList;

    #For every parameter in the model
    foreach my $model_param ( @{ $plist->Array } )
    {
        $param_name      = $model_param->Name;
        $param_value     = $model_param->evaluate();
        $new_param_value = $param_value * ( 1 + $bump / 100 );

        #Get fresh model and bump parameter
        $newbumpmodel->readFile( \%readFileinputs );
        $newbumpmodel->setParameter( $param_name, $new_param_value );

        #Reequilibrate
        if ($re_equil) {
            $simname = "equil_${param_name}";
            %simodeinputs = ( prefix       => "${net_file}_${simname}_${suffix}",
                              t_end        => $t_equil,
                              sparse       => $sparse,
                              n_steps      => $n_steps,
                              steady_state => 1,
                              atol         => $atol,
                              rtol         => $rtol
                            );
            $newbumpmodel->simulate_ode( \%simodeinputs );
        }

        #Implement input and run simulation
        $simname = $param_name;
        %simodeinputs = (
            prefix       => "${net_file}_${simname}_${suffix}",
            t_end        => $t_end,
            sparse       => $sparse,
            n_steps      => $n_steps,
            steady_state => 0,
            atol         => $atol,
            rtol         => $rtol
        );
        if ( defined( $params->{inp_ppert} ) ) {
            $pperts      = $params->{inp_ppert};
            $pert_names  = $pperts->{pnames};
            $pert_values = $pperts->{pvalues};
            $i           = 0;
            while ( $pert_names->[$i] ) {
                $param_name  = $pert_names->[$i];
                $param_value = $pert_values->[$i];
                $newbumpmodel->setParameter( $param_name, $param_value );
                $i = $i + 1;
            }
        }
        if ( defined( $params->{inp_cpert} ) ) {
            $cperts      = $params->{inp_cpert};
            $pert_names  = $cperts->{cnames};
            $pert_values = $cperts->{cvalues};
            $i           = 0;
            while ( $pert_names->[$i] ) {
                $param_name  = $pert_names->[$i];
                $param_value = $pert_values->[$i];
                $newbumpmodel->setConcentration( $param_name, $param_value );
                $i = $i + 1;
            }
        }
        $newbumpmodel->simulate_ode( \%simodeinputs );

        #Evaluate sensitivities and write to file

        #Get ready for next bump
        $newbumpmodel = BNGModel->new();
    }

  
}

###
###
###

1;
