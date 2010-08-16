#!/usr/bin/perl


use strict;
use warnings;


open(IN, "data.txt") or die "$!";


my (@x, @y1, @y2, @y3);
while(chomp(my $line = <IN>)) {
	if ($line =~ /^[a-z]+/i) {
		next;
	}
	if ($line =~ /^\d/) {
		my @nums = split(/\s+/, $line);
		push(@x, shift @nums);
		push(@y1, shift @nums);
		push(@y2, shift @nums);
		push(@y3, shift @nums);
	}
}

open(OUT, ">javaformatted.txt") or die "$!";

print OUT "@x\n@y1\n@y2\n@y3\n";

close(OUT);