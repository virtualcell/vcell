#!/usr/bin/perl

$bindir="../PerlBin";

@scripts= qw(
BNG2.pl
);

@modules= qw(
BNGModel.pm
BNGUtils.pm
Component.pm
ComponentType.pm
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
);
 
#print join(" ", @modules),"\n";
print "Compiling modules.\n";
$n_compiled=0;
for my $module (@modules){
  $modfile= "${bindir}/$module";
  if (!-e $modfile || (-M $module < -M $modfile)){
    print "Compiling module $module\n";
    # Makes byte compiled version of each module
    print `perlcc -B -o ${bindir}/$module $module`;
    ++$n_compiled;
  } else {
    print "Module $module up to date\n";
  }
}
if (!$n_compiled){
  print "All modules up to date.\n";
}

print "Compiling scripts.\n";
$n_compiled=0;
for my $script (@scripts){
  $sfile= "${bindir}/$script";
  $sfile=~ s/[.]pl$//;
  if (!-e $sfile || (-M $script < -M $sfile)){
    print "Compiling script $script\n";
    # Makes byte compiled version of each module
    print `perlcc -B -o ${bindir}/$sfile $script`;
    ++$n_compiled;
  } else {
    print "Script $script up to date\n";
  }
}
if (!$n_compiled){
  print "All scripts up to date.\n";
}


