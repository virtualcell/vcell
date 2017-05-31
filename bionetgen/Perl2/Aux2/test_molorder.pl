#!/usr/bin/perl


$perm= [3,1,2];
@molecules=("a","b","c");
print "before sort: ",join(" ",@molecules),"\n";

# Reorder Molecules according to canonical order
my @mol_perm=();

#$imol=0;
#for $mol (@molecules){
#  push @mol_perm, [$$perm[$imol],$mol];
#  ++$imol;
#}
@mol_perm= map {[$perm->[$_],$molecules[$_]]} (0..$#molecules);
@mol_perm= sort {$a->[0]<=>$b->[0]} @mol_perm;
@molecules= map {$_->[1]} @mol_perm;
print " after sort: ",join(" ",@molecules),"\n";

