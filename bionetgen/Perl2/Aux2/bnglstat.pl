#!/usr/bin/perl

use FindBin;
use lib $FindBin::Bin;
use BNGModel;
use BNGUtils;

while (@ARGV){
  my $file= shift(@ARGV);
  my $model= BNGModel->new();
  if ($err=$model->readFile($file,{no_exec=>1})){exit_error($err);}
}
