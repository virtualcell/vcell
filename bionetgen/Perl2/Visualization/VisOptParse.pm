package Viz;

use strict;
use warnings;
no warnings 'redefine';


sub parseOpts
{
	my $file = shift @_;
	open FILE,$file or die "Cannot open file ".$file."\n";
	my @lines = <FILE>;
	close FILE;
	print "Processing Opts file ".$file."\n";
	@lines = clean(\@lines);
	# parsing main block
	my ($names,$arrs) = getblocks(\@lines,['background','classes','toggle','filter','inhibition','motifs']);
	my %toggle;
	#my %background = {'include'=>[],'exclude'=>[]};
	my %background;
	my %classes;
	my %filter;
	my @inhibition;
	my %motifs;
	foreach my $i(0..@$names-1)
		{
		my $name = $names->[$i];
		my @arr = @{$arrs->[$i]};
		if ($name eq 'toggle')
			{
			my %opts = get_numericopts(\@arr);
			optcheck(['background','groups','collapse','each'],[keys %opts]);
			map { $toggle{$_} = $opts{$_} } keys %opts;
			}
		if ($name eq 'background')
			{
			my ($names2,$arrs2) = getblocks(\@arr,['include','exclude']);
			foreach my $j(0..@$names2-1)
				{
				my $nm = $names2->[$j];
				my @ar = genlist($arrs2->[$j]);
				$background{$nm} = \@ar;
				}
			}
		if ($name eq 'classes')
			{
			my ($names2,$arrs2) = getblocks(\@arr);
			foreach my $j(0..@$names2-1)
				{
				my $nm = $names2->[$j];
				my @ar = genlist($arrs2->[$j]);
				$classes{$nm} = \@ar;
				}
			}
		if ($name eq 'motifs')
			{
			my ($names2,$arrs2) = getblocks(\@arr);
			foreach my $j(0..@$names2-1)
				{
				my $nm = $names2->[$j];
				my @ar = genlist($arrs2->[$j]);
				$motifs{$nm} = \@ar;
				}
			}
		if($name eq 'filter')
			{
			my @ar = genlist(\@arr);
			$filter{'items'} =\@ar;
			}
		if($name eq 'inhibition')
			{
			@inhibition = genlist(\@arr);
			}
			
		}
	#print map { $_.":".join(",",@{$classes{$_}})."\n"; } keys %classes;
	
	return (\%toggle,\%background,\%classes,\%filter,\@inhibition,\%motifs);
}

sub clean
{
	my @lines = @{shift @_};
	my @lines2;
	foreach my $line(@lines)
		{
			# removing leading whitespace
			$line =~ s/^\s+//g;
			# removing trailing whitespace
			$line =~ s/\s+$//g;
			next if (length $line==0);
			next if ($line =~ /^#/);
			# compressing intermediate whitespace
			$line =~ s/\s+/ /g;
			push @lines2,$line;
		}
	return @lines2;
}


sub getblocks
{
	my @lines = @{shift @_};
	my @choices = @_ ? @{shift @_} : ();
	my @names = ();
	my @arrs = ();
	my @arr = ();
	my $blockname = '';
	my $err;
	foreach my $line(@lines)
	{
		if ($line =~ /begin (.*)/ and $blockname eq '')
		{
			@arr = (); $blockname = $1; 
			if(@choices)
			{
				if(not has(\@choices,$blockname))
				{
					$err = $blockname." not recognized as a block name.";
				}
			}
			next;
		}
			
		if ($line eq 'end '.$blockname)
		{ 
			my @arr2 = @arr;
			push @arrs,\@arr2; 
			push @names,$blockname; 
			$blockname = ''; 
			next;
		}
		push @arr, $line;
	}
	if($blockname ne '') 
		{ $err = "Problem reading block ".$blockname."."; }
	if($err) { print "Error: ".$err."\n"; exit; }
	#foreach my $i(0..@names-1)
	#	{ print $names[$i].":".join(",",@{$arrs[$i]})."\n";}
	return (\@names,\@arrs);
}

sub genlist
{
	return split(" ",join(" ",@{shift @_}));
}
sub optcheck
{
	my $standard = shift @_;
	my $checkthis = shift @_;
	
	foreach my $x(@$checkthis)
	{
		if(not has($standard,$x))
			{
			print $x." is not a valid option.";
			print "Valid options are ".join(",",@$standard)."\n";
			exit;
			}	
	}
	return;
}

sub get_numericopts
{
	my @lines = @{shift @_};
	my %opts;
	foreach my $line(@lines)
	{
		$line =~ /(.*)\s(.*)/;
		$opts{$1} = $2;
	}
	return %opts;
}
1;