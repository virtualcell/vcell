# This package contains routines for running BNG as an interactive console
package Console;

# pragmas
use strict;
use warnings;

# Perl Modules
use FindBin;
use lib $FindBin::Bin;
use File::Spec;

require Exporter;
our @ISA = qw( Exporter );
our @EXPORT = qw( BNGconsole );

# BNG Modules
use BNGUtils;
use BNGModel;



# global variables
my $BNG_PROMPT = 'BNG> ';


sub BNGconsole
{   # take actions from interactive console!
    my $params = (@_) ? shift : {};
 
    # TODO:
    # + differentiate between STDIN from console and file?
    # + add more model query commands


    # Am I interacting with a user, or a pipeline?
    my $interactive = ( -t STDIN ) ? 1 : 0;

    # turn off output buffering on STDOUT
    select STDOUT;  $| = 1;

    # say hello!
    printf "BioNetGen version %s\n", BNGversion();
    print "Entering BNG console mode.\n";
    print $BNG_PROMPT;

    # model begins as undefined
    my $model = undef;

    # read input
    while ( my $linein = <STDIN> )
    {
        unless ($interactive) {  print $linein;  }
        

        # trim leading and trailing white space
        $linein =~ s/^\s+//;        
        $linein =~ s/\s+$//;       

        PROCESS_INPUT:
        {
            # READ a model
            if ( $linein =~ s/^load\s+// )
            {
                if (defined $model)
                {
                    send_warning( "Attempted to load model while another model is active." );
                    last PROCESS_INPUT;
                }
    
                # get filename
                $linein =~ s/^(\S+)\s*//;
                my $filename = $1;
                unless ($filename and -e $filename)
                {
                    send_warning( "Attempted to load model, but file '$filename' was not found." );
                    last PROCESS_INPUT;
                }

                # initialize parameters
                my $local_params = { %$params };
                # add filename to params
                $local_params->{file} = $filename;

                # process long args ('atomize' and 'blocks')
                while ( $linein ){
					my ($key, $val);
					# --atomize INT
					if ( $linein =~ s/\s*--(\w+)\s+(\w+)// ){ 
						$key = $1;
						$val = $2;
					}
					# --blocks ["STRING", "STRING", ...]
					elsif( $linein =~ s/\s*--(\w+)\s+\[(.+)\]// ){ 
						$key = $1;
						$val = [ split( '\s*,\s*', $2 ) ];
						foreach my $v (@$val){
							$v = eval $v; # need to evaluate each entry to a string
						}
					}
					else{
	                    send_warning( "Error processing command line arguments: " . $linein );
	                    last PROCESS_INPUT;
	                }
					$local_params->{$key} = $val;
                }

                # create BNGModel object
                $model = BNGModel->new();
                $model->initialize();
                $BNGModel::GLOBAL_MODEL = $model;
                {   # read model file
                    my $err = $model->readFile($local_params);
                    if ($err)
                    {   # undefine model and send warning
                        %$model = ();  undef %$model;  $model = undef;
                        $BNGModel::GLOBAL_MODEL = undef;
                        send_warning( "Some problem processing '$filename': $err" );
                        last PROCESS_INPUT;
                    }
                }
            }
    
            # EXECUTE an action
            elsif ( $linein =~ s/^action\s+// )
            {
                unless (defined $model)
                {
                    send_warning( "Attempt to execute action without loading model." );
                    last PROCESS_INPUT;
                }
                   
                my ($action, $options); 
			    if ( $linein =~ /^\s*(\w+)\s*\((.*)\);?\s*$/ )
			    {   # syntax:  "action(options)"
                    $action = $1;
                    $options = $2;
                }
                else
                {
                    send_warning( "Invalid action syntax. Try: action actionName(options)." );
                    last PROCESS_INPUT;
                }
		    	
                # define action
                my $command = '$model->' . $action . '(' . $options . ');';
                print "Begin action $action\n";
    
	            # Perform self-consistency checks before operations are performed on model
                {
                    my $err = $model->ParamList->check();
                    if ($err)
                    {
                        send_warning( "Problem executing action: paramlist failed consistency check." );
                        last PROCESS_INPUT;
                    }
                }
		    	
                # execute action
                my $t_start = cpu_time(0);
                {
		    	    my $err = eval $command;
                    if ($@)
                    {
                        send_warning("Problem executing action: $@.");
                        last PROCESS_INPUT;
                    }
                    if ($err)
                    {
                        send_warning("Problem executing action: $err.");
                        last PROCESS_INPUT;
                    }
		    	    my $t_elapsed = cpu_time($t_start);
                    printf "CPU TIME: %s %.2f s.\n", $action, $t_elapsed;
		    	}
            }
    
            # CLEAR model
            elsif ( $linein =~ m/clear/ )
            {
                unless (defined $model)
                {
                    send_warning( "Attempt to clear model without loaded model." );
                    last PROCESS_INPUT;
                }
                # undefine model
                %$model = ();
                undef %$model;
                $model = undef;
                $BNGModel::GLOBAL_MODEL = undef;
            }

            # DONE
            elsif ( $linein =~ m/done/ )
            {
                undef $model;
                print "Leaving BNG console.\n";
                exit(0);
            }
    
            elsif ( $linein =~ m/help/ )
            {
                print "HELP menu for BNG console\n";
                print "/-----------------------------------------------\n";
                print "  load MODEL [OPTS] : load a BNG model file         \n";
                print "  done              : leave BNG console             \n";
                print "  clear             : clear BNG model               \n";
                print "  action ACTION     : execute ACTION on loaded model\n";
                print "\n";
                print "  OPTS:\n";
                print "    --atomize INT\n";
                print "    --blocks [\"STRING\", \"STRING\", ...]\n";
                print "-----------------------------------------------/\n";
            }

            # unrecognized command
            else
            {
                send_warning( "Unrecognized input. Type 'help' for information" );
            }
        }

        print $BNG_PROMPT;
    }
    
    # stdin close
    return 0;
}
