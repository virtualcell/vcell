package BNGOptions;
use strict;
use warnings;

use Getopt::Long;
use Class::Struct;

use BNGMessages qw(error warning message);

struct BNGOptions => {
	config=>    '%',
	nored=>		'@',      
};

#begin GetBNGOptions
sub GetBNGOptions
{
	my $self = shift;
	$self->config->{macro}='';         #default value (false) - enable macromodel
	$self->config->{silent}='';        #default value (false) - disable all (my only) messages
	$self->config->{debug}= 1;         #default value (false) - enable debug mode (debug output files)
	$self->config->{bnglfile}='';
	$self->config->{filetype}='';
	$self->config->{prefix}='';
	$self->config->{ModelID}='';
	$self->config->{MAX_LEVEL}=5;       #sets maximum level of allowed recursion
	
	
	GetOptions ('silent' => \$self->config->{silent},
	            'macro' => \$self->config->{macro},
	            'debug' => \$self->config->{debug},
	            'nored=s'=> \@{$self->nored});

	if ($self->config->{macro})
	{
		$self->config->{ModelID}="BNGModels::MacroBNGModel";
	}
	else
	{
		$self->config->{ModelID}="BNGModels::MicroBNGModel";
	}
	
	local $SIG{'__DIE__'} = sub { };
	local $SIG{'__WARN__'} = sub { };
	eval 'use '.$self->config->{ModelID};
	if ($@) {
		@_ = split( "\n", $@ );
		$_ = join( "\n\t", @_ );
        error("Module $self->config->{ModelID} not loaded because: \n\t$_");
	}
	else
	{
		eval 'use '.$self->config->{ModelID};
	}
	
	for my $file(@ARGV)
	{
	  error("File $file does not have required .bngl extension.") unless ( ($file=~ /[.]bngl/));
	  $self->config->{bnglfile}=$file;
	  $self->config->{prefix}=$file;
	  $self->config->{prefix}=~ s/[.]([^.]+)$//;
	  $self->config->{filetype}=uc("bngl");
	}
}
#end GetBNGOptions
1;
