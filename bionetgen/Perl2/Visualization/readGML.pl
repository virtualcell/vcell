#!/usr/bin/perl
use warnings;
use Getopt::Long;
use Class::Struct;

# USAGE
# $parsed = readGML::parseGML($filename);
# @$parsed;
# print recursivelyPrintList($parsed);

#my @list = ("node","bla","node","[", "id", "bla2", "id2", "bla3", "]", "edge","bla3");
struct Node =>
{
	'id'=>'$',
	'gid'=>'$',
	'label'=>'$',
	'isGroup'=>'$'
};

struct Edge =>
{
	'source'=>'$',
	'target'=>'$'
};


my $parsed = parseGML($ARGV[0]);
#print recursivelyPrintList($parsed);
#my $node = Node->new('id'=>1,'gid'=>2,'label'=>3);

#print $node->label;
my @nodes; my @edges;
getNodesEdges(\@nodes,\@edges,$parsed);
buildPattern(\@nodes,\@edges);


sub buildPattern
{
	my @nodes = @{shift @_};
	my @edges = @{shift @_};
	# cleaning labels
	map {$_->{'label'} =~ s/\"//g} @nodes;
	# ids
	my @ids = map {$_->{'id'}} @nodes;
	# labels
	my @labels = map {$_->{'label'}} @nodes;
	# nodes that are groups
	my @groups = grep {defined $_->{'isGroup'} and $_->{'isGroup'} eq '1'} @nodes;
	# nodes that are nested
	my @grouped = grep {defined $_->{'gid'} and has(\@ids,$_->{'gid'})} @nodes;
	


	
}


sub getNodesEdges
{
	my $nodes = shift @_;
	my $edges = shift @_;
	my @arr = @{shift @_};
	foreach my $i(0..@arr-1)
	{
		my $element = $arr[$i];
		if(ref($element) eq "ARRAY") { getNodesEdges($nodes,$edges,$element);}
		elsif($element eq "node") { push @$nodes, getNode($arr[1]);}
		elsif($element eq "edge") { push @$edges, getEdge($arr[1]);}
	}
}

sub getNode
{
	my @arr = @{shift @_};
	my $node = newNode();
	foreach my $i(0..@arr-1)
	{
		my ($x,$y) = @{$arr[$i]};
		if($x eq 'id') {$node->{'id'} = $y; }
		elsif($x eq 'gid') {$node->{'gid'} = $y; } 
		elsif($x eq 'label') {$node->{'label'} = $y;}
		elsif($x eq 'isGroup') {$node->{'isGroup'} = $y;}
	}
	return $node;
}

sub getEdge
{
	my @arr = @{shift @_};
	my $edge = newEdge();
	foreach my $i(0..@arr-1)
	{
		my ($x,$y) = @{$arr[$i]};
		if($x eq 'source') {$edge->{'source'} = $y;}
		elsif($x eq 'target') {$edge->{'target'} = $y;}
	}
	return $edge;
}

sub newNode
{
	my $node = Node->new();
	$node->{'id'} = '';
	$node->{'gid'} = '';
	$node->{'label'} = '';
	$node->{'isGroup'} = '';
	return $node;
}
sub newEdge
{
	my $edge = Edge->new();
	$edge->{'source'} = '';
	$edge->{'target'} = '';
	return $edge;
}

sub parseGML
{
	my $filename = shift @_;
	open(my $fh, "<", $filename) 
		or die "cannot open < ${filename}: $!";
	my @lines = <$fh>;
	close $fh;
	my @list = getTokens(\@lines);
	my @parsed = parseList(\@list);
	#my $str = recursivelyPrintList(\@parsed);
	#print $str;;
	return \@parsed;
		# returns a reference to a list of pairs with recursive nesting.
}

sub getTokens
{
	my @lines = @{shift @_};
	my $line = join("",@lines);
	$line =~ s/\n/ /g;
	$line =~ s/\s+/ /g;
	return split(/\s/,$line);
}

sub parseList
{
	my @tokenlist = @{shift @_};
	my @outlist;
	while(@tokenlist)
	{
		my $key = shift @tokenlist;
		#print "key = ".$key."\n";
		my $token = shift @tokenlist;
		my $value = $token;
		#print "token = ".$token."\n";
		if($token eq "[")
			{
				my $i = 1;
				my @newtokenlist;
				while($i>0)
				{
					my $tok = shift @tokenlist;
					$i++ if ($tok eq "[");
					$i-- if ($tok eq "]");
					if($i==0) 
					{
						#print "New token list = ".join(",",@newtokenlist)."\n";
						my $out = parseList(\@newtokenlist);
						$value = $out;
					}
					else
					{
						push @newtokenlist,$tok;
					}
				}
			}
		#print "Value = ".$value."\n";
		my @pair = ($key,$value);
		push @outlist, \@pair;	
	}
		return \@outlist;
}

sub recursivelyPrintList
{
	my $var = shift @_;
	if (ref($var) ne "ARRAY") {return $var;}
	if (ref($var) eq "ARRAY") {
		#my @arr = map {recursivelyPrintList($_)} @$var;
		return "(".join(",",map {recursivelyPrintList($_)} @$var).")";
	}
	return;
}

sub uniq  { my %seen = (); grep { not $seen{$_}++ } @_; }
sub has { scalar grep ( $_ eq $_[1], @{$_[0]}); }
