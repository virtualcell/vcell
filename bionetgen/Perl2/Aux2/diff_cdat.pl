#!/usr/bin/perl 

# Program verify.pl compares two time course files.  For the
# purpose of computing the norm error, the second file is the reference.
#
# Usage verify.pl datafile1 [datafile2]
#
# If no second filename is given, the script will automatically look in the
# database of results in the $BioNetGenRoot/VERIFY directory.
#
# Outputs: 
#  NORMERR: The computed norm error is printed
#  Either SUCCESSFUL BUILD or PROBLEM WITH BUILD depending upon magnitude of
#    norm error.  The configuration variable $BUILD_ERR_THRESH can be set in
#    bngrc or the local .bngrc file.
#
# Thanks to Robert Seletsky, the original author of this script, and Stan
# Steinberg, who suggested using the norm error 
#   err= |m2-m1|/|m2|
#      = sqrt( sum_{ij}((m2_{ij} - m1_{ij})**2)/ sum_{ij}(m2_{ij})**2),
# where m1 and m2 are the arrays containing the time course data at i time
# points for j variables.


$BUILD_ERR_THRESH= 1e-7;
$COLUMN_OFFSET=0;

# Read command line arguments
while ($ARGV[0] =~ /^-/){
  $_ = shift;
  if (/^-offset$/){
    $COLUMN_OFFSET= shift;
  }
  else{
    exit_error("Unrecognized command line option $_");
  }
}

if (($#ARGV!= 1)) {
  die "Usage: $0 filename filename2\n";
}
$rfilename = shift;
$rfilename2= shift;

#print "Comparing $rfilename $rfilename2\n";

open (RFILE, $rfilename) or die "Can't open $rfilename: $!\n";
$i=0;
@data=();
@times=();
while (<RFILE>){
   s/\#.*$//; # remove comments
   next unless /\S+/;
   ($time, @dat)= split(' ');
   push @times, $time;
   foreach $j (0..$#dat){
      $data[$i][$j]= $dat[$j];
   }
   ++$i;
}
close(RFILE);

open (RFILE, $rfilename2) or die "Can't open $rfilename2: $!\n";
$i=0;
@data2=();
@times2=();
while (<RFILE>){
   s/\#.*$//; # remove comments
   next unless /\S+/;
   ($time, @dat)= split(' ');
   push @times2, $time;
   foreach $j (0..$#dat){
      $data2[$i][$j]= $dat[$j];
   }
   ++$i;
}
close(RFILE);

# Check that the time points in the two files are the same
if ($#times != $#times2){
  printf "Time points are incompatible\n";
  printf "%d time point(s)in $rfilename.\n", $#times+1;
  printf "%d time point(s)in $rfilename2.\n", $#times2+1;
  exit(1);
}
for $i (0..$#times){
  if ($times[$i]!= $times2[$i]){
    printf "Time points are incompatible\n";
    exit(1);
  }
} 

# Compute normerr
$sresult= &normerr(\@data, \@data2);
print "$sresult\n";
exit(0);


# Compute norm error between two data sets stored in two rectangular arrays m1
# and m2 of the same dimension (not checked)
sub normerr {
  my $m1= shift;
  my $m2= shift;

  my $del2=0;
  my $ref2=0;

  my $ilast= $#$m1;
  my $jlast= $#{$$m1[0]};
  for my $i (0..$ilast){
    for my $j (0..$jlast){
      my $j2= $j+$COLUMN_OFFSET;
#      print $$m2[$i][$j]," ", $$m1[$i][$j],"\n";
#      printf "$j $j2 %.2e %.2e\n", $$m1[$i][$j], $$m2[$i][$j2];
      $del2+= ($$m2[$i][$j2] - $$m1[$i][$j])**2;
      $ref2+= ($$m2[$i][$j2])**2;
    }
  }
  return (sqrt($del2/$ref2));
}
