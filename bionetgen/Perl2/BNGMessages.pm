package BNGMessages;
use strict;
use warnings;

use Class::Struct;

use Exporter 'import';
our @EXPORT_OK = qw(error warning message);

struct BNGMessages => {
  errors=>    '%',
  messages=>	  '%',
  warnings=>	  '%',
  lasterror=> '$'	      
};

#begin error
sub error{
    my @msgs= @_;
    print STDERR "ABORT: ";
    for my $msg (@msgs){
	print STDERR $msg,"\n";
    }
    exit(1);	
}
#end error

#begin warning
sub warning{
    my @msgs= @_;
    print STDOUT "WARNING: ";
    for my $msg (@msgs){
	print STDOUT $msg,"\n";
    }
}
#end warning

#begin message
sub message{
    my @msgs= @_;
    for my $msg (@msgs){
	print STDOUT $msg unless (BNGOptions::GetBNGOptions()->config->{silent});
    }
}
#end message
1;