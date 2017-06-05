#!/usr/bin/perl
# Count number of species matching a group
my @gnames;
my %gsize;
while(<>){
  next unless (/^begin groups/);
  while(<>){
    last if (/^end groups/);
    (my $index, my $name, my $glist)= split(' ');
    my @specs= split(',',$glist);
    push @gnames, $name;
    $gsize{$name}=scalar(@specs); 
  }
}
foreach $group (@gnames){
  printf "%s %d\n", $group, $gsize{$group};
}
