#!/usr/bin/perl

use FindBin;
use lib $FindBin::Bin;
use Compartment;
use CompartmentList;

my $clist= CompartmentList->new;
my $plist= ParamList->new;
$clist->setDefault();
# Check valid compartment setup/listing
print $clist->toString();

# Test adjacency function
for my $comp1 (@{$clist->Array}){
  for my $comp2 (@{$clist->Array}){
    if ($comp1->adjacent($comp2)){
      printf "%s adjacent to %s\n", $comp1->Name, $comp2->Name;
    }
  }
}


# Check setting of compartment volume
my $x="100";
if ($err=$clist->lookup("PM")->setVolume($x,$plist)){
  die $err;
}
print "After setting PM volume to $x\n";
print $clist->setVolumeStrings();

# Test connected function

if (!($clist->lookup("EC")->connected($clist->lookup("NM"), $clist->lookup("NU")))){
  print "Connected PASSED test 1\n";
} else {
  print "Connected FAILED test 1\n";
}

if (($clist->lookup("EC")->connected($clist->lookup("CP"), $clist->lookup("PM")))){
  print "Connected PASSED test 2\n";
} else {
  print "Connected FAILED test 2\n";
}

if (($clist->lookup("EC")->connected($clist->lookup("CP")))){
  print "Connected FAILED test 3\n";
} else {
  print "Connected PASSED test 3\n";
}

if (($clist->lookup("EC")->connected($clist->lookup("CP"), $clist->lookup("PM"), $clist->lookup("NM")))){
  print "Connected PASSED test 4\n";
} else {
  print "Connected FAILED test 4\n";
}
