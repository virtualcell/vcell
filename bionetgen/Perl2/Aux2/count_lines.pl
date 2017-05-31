#!/usr/bin/perl

$source=<<"EOF";
BNGAgent.pm
BNGMessages.pm
BNGModel.pm
BNGOptions.pm
BNGUtils.pm
Compartment.pm
CompartmentList.pm
Component.pm
ComponentType.pm
Expression.pm
HNauty.pm
MacroBNGModel.pm
Map.pm
Molecule.pm
MoleculeType.pm
MoleculeTypesList.pm
Observable.pm
Param.pm
ParamList.pm
RateLaw.pm
Rxn.pm
RxnList.pm
RxnRule.pm
Species.pm
SpeciesGraph.pm
SpeciesList.pm
EOF

# count lines of source code by the number of ;'s

if (@ARGV){
  @files=@ARGV;
} else {
  @files= split(' ',$source);
}
my $n_lines_tot=0;
for my $file (@files){
  my $n_lines=0;
  open(IN,$file) || die "Couldn't open $file:$?\n";
  while(<IN>){
    if (/;\s*$/){
      ++$n_lines;
    }  
  }
  print "$file:$n_lines\n";
  $n_lines_tot+= $n_lines;
}
print "TOTAL:$n_lines_tot\n";
