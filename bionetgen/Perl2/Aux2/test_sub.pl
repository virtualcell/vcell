#!/usr/bin/perl

use FindBin;
use lib $FindBin::Bin;
use SpeciesGraph;

while(<>){
  next unless /S+/;
  my $sg= SpeciesGraph->new;
  chomp;
  $err=$sg->readString(\$_);
  if ($err){die($err);}
}
