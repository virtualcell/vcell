package Viz;

use strict;
use warnings;
no warnings 'redefine';

use Class::Struct;
use Visualization::NetworkGraph;
use Visualization::GML;
use SpeciesGraph;


struct RINF=>
{
	'Nodes'=> '@',
	'Edges'=> '@',

};	

sub newRINF
{
	my $rinf = RINF->new();
	$rinf->{'Nodes'} = shift @_;
	$rinf->{'Edges'} = shift @_;
	return $rinf;
};

sub makeRINF
{
	my $bpg = shift @_;
	my $model = shift @_;
	my @allnodes = @{$bpg->{'NodeList'}};
	my %nodetype = %{$bpg->{'NodeType'}};
	my @rules = grep {$nodetype{$_} eq 'Rule'} @allnodes;
	
	
	my %hash_of_vecs;
	
	foreach my $r(@rules)
	{
		$hash_of_vecs{$r} = getNeighbors($bpg->{'EdgeList'},$r);
	}
	
	my @nodes = @rules;
	my @edges;
	
	my %typehash = ("11"=>"Modulation","10"=>"Activation","01"=>"Inhibition");
	foreach my $i(0..@rules-2)
	{
		foreach my $j($i+1..@rules-1)
			{
				my $arr1 = $hash_of_vecs{$rules[$i]};
				my $arr2 = $hash_of_vecs{$rules[$j]};
				my @opts = ([0,0],[0,1],[0,2],[1,0],[1,1],[1,2],[2,0],[2,1],[2,2]);
				# rule_i -> rule_j
				# 0 (re,re), 1 (re,pr), 2 (re,co),
				# 3 (pr,re), 4 (pr,pr), 5 (pr,co),
				# 6 (co,re), 7 (co,pr),8 (co,co)
				my @overlaps = map {my @opt = @$_; has_overlap($arr1->[$opt[0]],$arr2->[$opt[1]]); } @opts;
				my $act = 0; my $inh = 0;
				# i->j influence
				$inh = ( $overlaps[0]==1 or $overlaps[2]==1) ? 1: 0;
				$act = ( $overlaps[3]==1 or $overlaps[5]==1) ? 1: 0;
				unless ($inh==0 and $act==0) 
				{
					push @edges, join(":",($rules[$i],$rules[$j],$typehash{$act.$inh}));
				}
				
				# j->i influence
				$inh = ( $overlaps[0]==1 or $overlaps[6]==1) ? 1: 0;
				$act = ( $overlaps[1]==1 or $overlaps[7]==1) ? 1: 0;
				unless ($inh==0 and $act==0) 
				{
					push @edges, join(":",($rules[$j],$rules[$i],$typehash{$act.$inh}));
				}
			}
		#i->i influence
		my $arr = $hash_of_vecs{$rules[$i]};
		my @opts = ([0,2],[1,2]); #re->co, pr->co
		my @overlaps = map {my @opt = @$_; has_overlap($arr->[$opt[0]],$arr->[$opt[1]]); } @opts; 
		my $act = 0; my $inh = 0;
		$act = ($overlaps[1]==1) ? 1 :0;
		$inh = ($overlaps[0]==1) ? 1 :0;
		unless ($inh==0 and $act==0) 
				{
					push @edges, join(":",($rules[$i],$rules[$i],$typehash{$act.$inh}));
				}
	}
	
	my $rinf = newRINF(\@nodes,\@edges);
	return $rinf;
}

sub getNeighbors
{
	my $edgelist = shift @_;
	my $rule = shift @_;
	
	sub getRel { 
		my $r = $_[0]; my $t = $_[1]; my @e = @{$_[2]};
		return sort map { $_ =~ /^.*:(.*):.*$/; $1 ; }  
			grep { $_ =~ /^.*:.*:(.*)$/; $1 eq $t; }
			grep { $_ =~ /^(.*):.*:.*$/; $1 eq $r; } @e;
	}
	my @re =	getRel($rule,'Reactant',$edgelist);
	my @pr =	getRel($rule,'Product',$edgelist);
	my @co =	getRel($rule,'Context',$edgelist);
	return [\@re,\@pr,\@co];
}
sub has_overlap
{
	my @x = @{shift @_};
	my @y = @{shift @_};
	my @z = grep {has(\@y,$_) } @x;
	return (scalar(@z) > 0) ? 1: 0;
}


1;