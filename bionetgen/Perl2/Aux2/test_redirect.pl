#!/usr/bin/perl

open my $oldout, ">&STDOUT"     or die "Can't dup STDOUT: $!";
open OLDERR,     ">&", \*STDERR or die "Can't dup STDERR: $!";

open STDOUT, '>', "$0.out" or die "Can't redirect STDOUT: $!";
open STDERR, ">&STDOUT"     or die "Can't dup STDOUT: $!";

select STDERR; $| = 1;	# make unbuffered
select STDOUT; $| = 1;	# make unbuffered

print STDOUT "stdout 1\n";	# this works for
print STDERR "stderr 1\n"; 	# subprocesses too

open STDOUT, ">&", $oldout or die "Can't dup \$oldout: $!";
open STDERR, ">&OLDERR"    or die "Can't dup OLDERR: $!";

print STDOUT "stdout 2\n";
print STDERR "stderr 2\n";
