#!/usr/bin/perl

use strict;
use warnings;
use FindBin;
use lib $FindBin::Bin;

use BNGOptions;
use BNGMessages qw(error warning message);
use BNGUtils;

#Init  
my $t_begin=cpu_time(0);
our $options = new BNGOptions;
$options->GetBNGOptions();
our $messages = new BNGMessages;
message("BioNetGen version ".BNGversion()."\n");

#Calculation
our $model;
eval '$model = new '.$options->config->{ModelID};
$model->ProcessModel($options->config->{bnglfile});
message("Processing complete.\n");

#Duration of calculation
my $t_end=cpu_time(0);
message(sprintf "CPU TIME: total %.2f s.\n", ($t_end-$t_begin));
