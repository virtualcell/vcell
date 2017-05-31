#!/usr/bin/perl

#    BioNetGen : rule-based modeling language and simulation platform
#
#                  Copyright (C) 2006,2009,2012 by
#
#      James R. Faeder    (faeder at pitt dot edu)
#      Justin S. Hogg     (justinshogg at gmail dot com)
#      Leonard A. Harris  (lh64 at cornell dot com)
#      John A. P. Sekar   (johnarul dot sekar at gmail dot com)
#      Jose Juan Tapia
#      Arshi Arora
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

# Perl Modules
use Config;
use File::Spec;
use FindBin;
use Getopt::Long;
use IO::Handle;

# get Perl2 Module directory: look for environment variables BNGPATH or BioNetGenRoot.
# If neither are defined, use RealBin module
use lib File::Spec->catdir( ( exists $ENV{'BNGPATH'}
                              ? $ENV{'BNGPATH'}
                              : ( exists $ENV{'BioNetGenRoot'} 
                                  ? $ENV{'BioNetGenRoot'}
                                  : $FindBin::RealBin
                                )
                            ),
                            'Perl2'
                          );
# BNG Modules
use BNGUtils;
use BNGModel;
use Console;


# Set up Signal Handlers..
# Define global variable to store PID of child process.
$::CHILD_PID = undef;
# Get signal names
my $i = 0;
my %SIGNO=();
defined($Config{sig_name}) or die "No signals defined";
foreach my $signame ( split " ", $Config{sig_name} )
{
    $SIGNO{$signame} = $i;
    $i++;
}
# TERM signal handler: make sure any child processes are shutdown before termination
$SIG{'TERM'} = sub
{
    if (defined $::CHILD_PID)
    {   # kill off child process
        print "\n>>> relaying TERM signal to child with PID: ", $::CHILD_PID, " <<<\n";
        kill $SIGNO{"TERM"}, $::CHILD_PID;
    }
    exit_error( sprintf "BioNetGen received TERM signal (%d)", $SIGNO{"TERM"} );
};
# INT signal handler: make sure any child processes are shutdown before termination
$SIG{'INT'} = sub
{
    if (defined $::CHILD_PID)
    {   # kill off child process
        print "\n>>> relaying INT signal to child with PID: ", $::CHILD_PID, " <<<\n";
        kill $SIGNO{"INT"}, $::CHILD_PID;
    }
    exit_error( sprintf "BioNetGen received TERM signal (%d)", $SIGNO{"INT"} );
};



# Defaults params for File mode
my %default_args         = ( 'write_xml'     => 0,  'write_mfile'      => 0,
                             'write_SBML'    => 0,  'generate_network' => 0,
                             'skip_actions'  => 0,  'action_skip_warn' => 1,
                             'logging'       => 0,  'no_exec'          => 0,
                             'allow_perl'    => 0,  'no_nfsim'         => 0,
                             'output_dir'    => File::Spec->curdir(),
                             'no_atomizer'   => 0,
							 'write_autos'   => 0
                           );
# Default params for Console mode
my %default_args_console = ( 'write_xml'     => 0,  'write_mfile'      => 0,
                             'write_SBML'    => 0,  'generate_network' => 0,
                             'skip_actions'  => 1,  'action_skip_warn' => 1,
                             'logging'       => 0,  'no_exec'          => 0,
                             'allow_perl'    => 0,  'no_nfsim'         => 0,
                             'output_dir'    => File::Spec->curdir(),
                             'no_atomizer'   => 0,
							 'write_autos'   => 0
                           );


# variables to contain user args
my $console = 0;
my $findbin = '';
my $help    = 0;
my $version = 0;
my %user_args = ( 'console' => \$console,
                  'findbin' => \$findbin,
                  'help'    => \$help,
                  'version' => \$version
                );

# parse command line arguments
GetOptions( \%user_args, 
            'help|h',
            'version|v',
            'console',
            'findbin=s',
            'skip_actions|check',
            'no_nfsim|no-nfsim',
            'no_atomizer|no-atomizer',
            'output_dir|outdir=s',
            'logging|log',
            'generate_network|netgen',
            'write_SBML|sbml',
            'write_mfile|mfile',
            'write_xml|xml',
			'write_autos|autos'
          )
or die "Error in command line arguments (try: BNG2.pl --help)";

# display help if requested
if ($help)
{
    display_help();
    exit(0);
}

# display version info
if ($version)
{
    printf "BioNetGen version %s\n", BNGversion();
    exit(0);
}

# try to find binary
if (not( $findbin eq ''))
{
    # exit with value 0 if binary is found, 1 otherwise
    exit( BNGModel::findExec($findbin) ? 0 : 1 );
}



if ( $console )
{
    # get arguments
    my %args = ();
    while ( my ($opt,$val) = each %default_args_console )
    {
        $args{$opt} = exists $user_args{$opt} ? $user_args{$opt} : $val;
    }

    # check if output directory exists and is writable
    unless ( -d $args{output_dir} )
    {  send_warning( sprintf "Default output directory '%s' is not a directory.", $args{output_dir});  }
    unless ( -w $args{output_dir} )
    {  send_warning( sprintf "Not able to write to default output directory '%s'.", $args{output_dir});  }

    # start console
    my $err = BNGconsole( \%args );
    exit_error($err) if ($err);
}
else
{
    unless (@ARGV)
    {  display_help();  }

    # get arguments
    my %args = ();
    while ( my ($opt,$val) = each %default_args )
    {
        $args{$opt} = exists $user_args{$opt} ? $user_args{$opt} : $val;
    }

    # check if output directory exists and is writable
    unless ( -d $args{output_dir} )
    {  send_warning( sprintf "Default output directory '%s' is not a directory.", $args{output_dir});  }
    unless ( -w $args{output_dir} )
    {  send_warning( sprintf "Not able to write to default output directory '%s'.", $args{output_dir});  }

    # Process any files
    while ( my $file = shift @ARGV )
    {
        # create BNGMOdel object
        my $model = BNGModel->new();
        $model->initialize();
        $BNGModel::GLOBAL_MODEL = $model;

        # set file argument
        $args{'file'} = $file;

        # read and process Model file
        my $err = $model->readFile( \%args );
        exit_error($err) if ($err);

        # undefine model
        %$model = ();  undef %$model;
        $BNGModel::GLOBAL_MODEL = undef;
    }
}

# all done!
exit(0);




# Display Help Menu
sub display_help
{
    printf "\nBioNetGen version %s\n", BNGversion();
    print  "--------------------------------------------------/ HELP MENU /-----\n"
          ."  SYNOPSIS                                                          \n"
          ."    process MODEL:      BNG2.pl [OPTION]... MODEL...                \n"
          ."    start BNG console:  BNG2.pl --console                           \n"
          ."    display help:       BNG2.pl -h                                  \n" 
          ."    display version:    BNG2.pl -v                                  \n"           
          ."                                                                    \n"
          ."  OPTIONS                                                           \n"
          ."    --log          write log to file MODEL.log (default is STDOUT)  \n"
          ."    --xml          write XML output after processing MODEL          \n"
          ."    --mfile        write MATLAB M-file output after processing MODEL\n"
          ."    --sbml         write SBML output after processing MODEL         \n"
          ."    --check        read MODEL, but do not execute actions           \n"
          ."    --outdir PATH  change default output path                       \n"
          ."                                                                    \n"
          ."  For more information, visit bionetgen.org                         \n"
          ."--------------------------------------------------------------------\n";
}

