#!/usr/bin/perl 

# Program verify.pl compares two time course files.  For the
# purpose of computing the norm error, the second file is the reference.
#
# Usage verify.pl datafile1 datafile2
#
# Outputs: 
#  Either PASSED or FAILED (or possibly a general error message)
#    depending upon magnitude of norm error. The configuration variable
#    $BUILD_ERR_THRESH can be set in this file (or bngrc or the local
#    .bngrc file).
#
# Thanks to Robert Seletsky, the original author of this script, and Stan
# Steinberg, who suggested using the norm error 
#   err= |m2-m1|/|m2|
#      = sqrt( sum_{ij}((m2_{ij} - m1_{ij})**2)/ sum_{ij}(m2_{ij})**2),
# where m1 and m2 are the arrays containing the time course data at i time
# points for j variables.

# UPDATE: 16 dec 2010 by Justin Hogg
#   Improved messages.
#   Added strict and warnings pragmas.

# Recall that $0 is the name of the the executing perl script!

use strict;
use warnings;


my $BUILD_ERR_THRESH = 1e-7;
my $COLUMN_OFFSET = 0;

my $INDENT = '';

# Read command line arguments
while ( $ARGV[0] =~ /^-/)
{
    my $arg = shift @ARGV;
    if ( $arg =~ /^-offset$/)
    {
        $COLUMN_OFFSET = shift;
    }
    else
    { 
        die $INDENT . "$0 ERROR: unrecognized command line option $arg";
    }
}

# check that we have two more arguments (filenames!)
if ( @ARGV != 2 )
{
    die $INDENT . "ERROR: usage = $0 filename filename2\n";
}

my $rfilename  = shift @ARGV;
my $rfilename2 = shift @ARGV;

print $INDENT . "$0 is comparing $rfilename and $rfilename2\n";


# read first data file
open (RFILE, $rfilename) or die $INDENT . "$0 ERROR: can't open $rfilename: $!\n";
my @data  = ();
my @times = ();
while ( my $line = <RFILE> )
{
    $line =~ s/\#.*$//; # remove comments
    $line =~ s/^\s+//;  # remove leading whitespace
    $line =~ s/\s+$//;  # remove trailing whitespace
    
    # is there any line left?
    next unless ( $line =~ /\S+/ );
    # split at whitespace
    my @elem = split /\s+/, $line;
    # first element is time
    push @times, shift @elem;
    # remaining elements are data
    push @data, [ @elem ];
}
close RFILE;


# read second data file
open (RFILE, $rfilename2) or die $INDENT . "$0 ERROR: can't open $rfilename2: $!!!\n";
my @data2=();
my @times2=();
while ( my $line = <RFILE> )
{
    $line =~ s/\#.*$//; # remove comments
    $line =~ s/^\s+//;  # remove leading whitespace
    $line =~ s/\s+$//;  # remove trailing whitespace
    
    # is there any line left?
    next unless ( $line =~ /\S+/ );
    # split at whitespace
    my @elem = split /\s+/, $line;
    # first element is time
    push @times2, shift @elem;
    # remaining elements are data
    push @data2, [ @elem ];
}
close RFILE;



# Check that the time points in the two files are the same
unless ( @times == @times2 )
{
    print  $INDENT . "FAILED!!  data files have different timepoints.\n";
    printf $INDENT . "  %d time point(s) in $rfilename.\n",  scalar @times;
    printf $INDENT . "  %d time point(s) in $rfilename2.\n", scalar @times2;
    exit 1;
}

# Verify time points are the same
for ( my $i=0;  $i < @times;  ++$i )
{
    unless ( $times[$i] == $times2[$i] )
    {
        print $INDENT . "FAILED!!  data files have different timepoints.\n";
        exit 1;
    }
} 

# Verify that the number of data points are compatible
for ( my $i=0;  $i < @times;  ++$i )
{
    unless ( @{$data[$i]} == @{$data2[$i]} + $COLUMN_OFFSET )
    {
        print $INDENT . "FAILED!!  datafiles have different number of data columns!\n";
        exit 1;
    }
} 


# Compute normerr
my $sresult = normerr( \@data, \@data2);


# Check if normerr is in bounds
if ( $sresult > $BUILD_ERR_THRESH )
{
    print $INDENT . sprintf "FAILED!!  norm error (%E) > threshold error (%E).\n", $sresult, $BUILD_ERR_THRESH;
    exit 1;
}
else
{
    print $INDENT . sprintf "PASSED!!  norm error (%E) <= threshold error (%E).\n", $sresult, $BUILD_ERR_THRESH;
    exit 0;
}





# Compute norm error between two data sets stored in two rectangular arrays m1
# and m2 of the same dimension (not checked)
sub normerr
{
    my $data1 = shift;
    my $data2 = shift;

    my $delta2 = 0;
    my $ref2   = 0;

    for ( my $i=0;  $i < @$data1;  ++$i )
    {
        for ( my $j=0;  $j < @{$data1->[$i]};  ++$j )
        {
            # offset columns in data2 (why??? -justin)
            my $j2 = $j + $COLUMN_OFFSET;
            $delta2 += ( $data2->[$i][$j2] - $data1->[$i][$j] )**2;
            $ref2  += ( $data2->[$i][$j2] )**2;
        }
    }
    return sqrt( $delta2/$ref2 );
}
