package Viz;

use strict;
use warnings;
no warnings 'redefine';

use Class::Struct;
use Visualization::NetworkGraph;
use Visualization::GML;


struct ProcessGraph =>
{
	'Processes'	=> '@', 
	'Edges' => '@',
	'ReacProds' => '%', # to be deprecated
	'Names' => '%',
	'Embed' => '%',
};

struct ProcessGraph2 =>
{
	'Nodes'=> '@',
	'Edges'=> '@',
	'Embed'=> '@',
	'Names'=> '@',

};
sub initializeProcessGraph
{
	my $pg = ProcessGraph2->new();
	$pg->{'Nodes'} = shift @_;
	$pg->{'Edges'} = shift @_;
	if(@_) { $pg->{'Embed'} = shift @_; }
	if(@_) { $pg->{'Names'} = shift @_; }
	#else {my @x = @{$pg->{'Nodes'}}; $pg->{'Names'} = \@x;}
	#print @{$pg->{'Names'}};
	return $pg;
}
sub printProcessGraph
{
	my $pg = shift @_;
	my $str = ();
	$str .= "Processes:\n";
	$str .= join "\n", map { $_.":".$pg->{'Names'}->{$_} } @{$pg->{'Processes'}};
	$str .= "\n";
	$str .= "Influences:\n";
	$str .= join "\n", @{$pg->{'Edges'}};
	return $str;

}

sub makeProcessGraph
{
	print "Building process graph for whole model.\n";
	my $bpg = shift @_;
	
	my $mergepairs = @_ ? shift @_ : 0;
	my $embed = @_ ? shift @_ : 0;

	my @edges = @{$bpg->{'EdgeList'}};
	my @edges2;
	
	my @processes = grep {$bpg->{'NodeType'}->{$_} =~ /Rule/} @{$bpg->{'NodeList'}};
	my @wcs = uniq(map {$_ =~ /^(.*):.*:.*/; $1; } grep { $_ =~ /Wildcard/ } @edges);
	
	my %reacprod;
	my %context;
	my %reac;
	my %prod;
	map { my @x = (); $reacprod{$_} = \@x; } @processes;
	map { my @x = (); $context{$_} = \@x; } @processes;
	
	foreach my $proc(@processes)
	{
		my $r = quotemeta $proc;
		my @rps = 	uniq( map { $_ =~ /.*:(.*):.*/;  $1; }
					grep { $_ =~ /Reactant|Product/ }
					grep {$_ =~ /^$r:/ } @edges );
		if(@wcs) { push @rps, getWCs(\@rps,\@wcs,\@edges); }
		$reacprod{$proc} = \@rps;
				
		my @cont = 	uniq( map { $_ =~ /.*:(.*):.*/;  $1; }
					grep { $_ =~ /Context/ }
					grep {$_ =~ /^$r:/ } @edges );
		$context{$proc} = \@cont;
	}
	foreach my $r1(@processes)
	{
		foreach my $r2(@processes)
		{
			if(has_overlap($reacprod{$r1},$context{$r2}))
			{
				push @edges2, join(" ",($r1,$r2));
			}
		}
	}
	my %namesarr;
	my %bpgs;
	foreach my $proc(@processes)
	{
	my $r = quotemeta $proc;
	my @reacs = 	sort {$a cmp $b}
					uniq( map { $_ =~ /.*:(.*):.*/;  $1; }
					grep { $_ =~ /Reactant/ }
					grep {$_ =~ /^$r:/ } @edges );
	$reac{$proc} = \@reacs;
		
	my @prods = 	sort {$a cmp $b}
					uniq( map { $_ =~ /.*:(.*):.*/;  $1; }
					grep { $_ =~ /Product/ }
					grep {$_ =~ /^$r:/ } @edges );
	$prod{$proc} = \@prods;
	
	my $namearr = [[$proc],\@reacs,\@prods];
	#my $name = $proc."\n:".join("+",@reacs)."->".join("+",@prods)." }";
	#my $name = make_name($namearr);
	$namesarr{$proc} = $namearr;
	
	# building embed graph here
	if($embed)
		{
			my @embed_edges = grep { $_ =~ /Reactant|Product/ }
							  grep {$_ =~ /^$r:/ } @edges ;
			my @embed_nodes = uniq( map { $_ =~ /.*:(.*):.*/;  $1; } @embed_edges);
			push @embed_nodes,$proc;
			my %embed_nodetype;
			my %nodetype = %{$bpg->{'NodeType'}};
			@embed_nodetype { @embed_nodes } = @nodetype {@embed_nodes};
			my $bpg2 = makeRuleNetworkGraph_simple(\@embed_nodes,\@embed_edges,\%embed_nodetype,$proc);
			$bpgs{$proc} = $bpg2;
		}
	}
	
	my %names;
	map {$names{$_} = make_name($namesarr{$_});} @processes;
	
	my $pg = ProcessGraph->new();
	if($mergepairs==0)
	{
		$pg->{'Processes'} = \@processes;
		#$pg->{'ReacProds'} = \%reacprod;
		$pg->{'Names'} = \%names;
		$pg->{'Edges'} = \@edges2;
		if($embed) {$pg->{'Embed'} = \%bpgs;}
		return $pg;
	}

	# mergepairs needs to be done correctly!
	my @procs = @processes;
	my @pairs;
	my @unpaired;
	if($mergepairs==1)
	{
		# build pairs;
		# get a process from the stack
		my @stack = @processes;
		
		while(@stack)
		{
			my $proc1 = shift @stack;
			my @stack2 = @stack;
			my @stack3;
			while(@stack2)
				{
				my $proc2 = shift @stack2;
				if(is_reverse_of($reac{$proc1},$prod{$proc1},$reac{$proc2},$prod{$proc2}))
					{
					push @pairs, $proc1." ".$proc2;
					#$pairs{$proc1} = $proc2;
					last;
					}
				else 
					{
					push @stack3,$proc2;
					}
				if(not @stack2) { push @unpaired,$proc1;}
				}
			@stack = (@stack2,@stack3);
		}
	}
	
	my @procs_p;
	my @edges_p;
	my %names_p;
	my %embed_p;
	my %remaphash;
	my %bpgs_p;
	foreach my $pair(@pairs)
	{
		my ($dom,$sub) = split(" ",$pair);
		my $proc = join(",",($dom,$sub));
		push @procs_p, $proc;
		$remaphash{$dom} = $proc;
		$remaphash{$sub} = $proc;
		
		my @name_arr = @{$namesarr{$dom}};
		push2ref($name_arr[0],$sub);
		my $name = make_name(\@name_arr);
		$names_p{$proc} = $name; 
		if($embed)
		{
			my @bpgs2 = map {$bpgs{$_} } ($dom,$sub);
			my $bpg = mergeNetworkGraphs(@bpgs2);
			$bpgs_p{$proc} = $bpg;
		}
	}
	foreach my $proc(@unpaired)
	{
		push @procs_p, $proc;
		$remaphash{$proc} = $proc;
		my $name = make_name($namesarr{$proc});
		$names_p{$proc} = $name;
		if($embed)
		{
			$bpgs_p{$proc} = $bpgs{$proc};
		}
	}
	@edges_p =uniq( map 
	{
		my @x = split(" ",$_);
		join(" ",map {$remaphash{$_}} @x);
	} @edges2);
	
	
	$pg->{'Processes'} = \@procs_p;
	#$pg->{'ReacProds'} = \%reacprod;
	$pg->{'Names'} = \%names_p;
	$pg->{'Edges'} = \@edges_p;
	if($embed) {$pg->{'Embed'} = \%bpgs_p;}
	return $pg;
	
}

sub reprocessWildcards
{
	my @edgelist = @{shift @_};
	my @wc_edges = grep {$_ =~ /.*:.*:Wildcard$/ } @edgelist ;
	my @wcs = uniq (map {$_ =~ /^(.*):.*:.*/; $1; } @wc_edges);
	my @other_edges = grep { not has(\@wc_edges,$_) } @edgelist;
	
	my @edges2;
	foreach my $edge(@other_edges)
	{
		$edge =~ /^(.*):(.*):(.*)$/;
		my ($rule,$pat,$rel) = ($1,$2,$3);
		if($rel ne 'Context') { push @edges2,$edge; next;}
		if(not has(\@wcs,$pat)) { push @edges2,$edge; next;}
		
		my @matches = uniq( map {$_ =~ /^.*:(.*):.*$/; $1;} grep {$_ =~ /^(.*):.*:.*$/; $1 eq $pat} @wc_edges);
		foreach my $pat2(@matches) { push @edges2, join(":",($rule,$pat2,$rel)); }
	}
	return uniq(@edges2);
}

sub makeProcessGraph2
{
	my $bpg = shift @_;
	my %nodetype = %{$bpg->{'NodeType'}};
	my @allnodes = @{$bpg->{'NodeList'}};
	my @alledges = reprocessWildcards($bpg->{'EdgeList'});
	
	
	my %args = %{shift @_};
	my @processgrps = ();
	my $pg;
	
	if($args{'groups'}==0)
	{
		if($args{'mergepairs'}==0)
			{
				my @rules = grep { $nodetype{$_} eq 'Rule' } @allnodes;
				my @reacprods = map [getRelationships(\@alledges,$_,['Reactant','Product'])],  @rules;
				my @contexts = map [getRelationship(\@alledges,$_,'Context')],  @rules;
				
				my @processes = @rules;
				my @relations = ();
				foreach my $i(0..@processes-1)
				{
					foreach my $j($i..@processes-1)
						{
							if( has_overlap($reacprods[$i],$contexts[$j]) ) 
								{ push @relations, join(" ",($i,$j));}
							next if($i == $j);
							if( has_overlap($reacprods[$j],$contexts[$i]) ) 
								{ push @relations, join(" ",($j,$i));}

						}
				}
				@relations = uniq(@relations);
				my @names = ($args{'embed'}==0) ? @processes : () x @processes;
				$pg = initializeProcessGraph(\@processes,\@relations,[],\@names);
			}
		else
			{
				my @rules = grep { $nodetype{$_} eq 'Rule' } @allnodes;
				# group rules and their reverses
				my %revmap;
				foreach my $rule(@rules)
					{
					# stupid naming conventions!
					if($rule =~ /^(Rule[0-9]{.*})r$/)
						{
						my $pair = $1;
						if(has(\@rules,$pair)) {$revmap{$rule} = $pair;}
						}
					elsif($rule =~ /^(.*)\(reverse\)$/)
						{
						my $pair = $1;
						if(has(\@rules,$pair)) {$revmap{$rule} = $pair;}
						}
					}
				my @paired =  (keys %revmap,values %revmap);
				my @stack = ();
				foreach my $rule(@rules)
				{
					if(not has(\@paired,$rule)) { my @x = ($rule); push @stack, \@x;}
					if(has([keys %revmap],$rule)) { push @stack, [$revmap{$rule},$rule]; }				
				}
				
				my @processes = @stack;
				my @reacprods = map {
									my @x = @$_; 
									my @y = map getRelationships(\@alledges,$_,['Reactant','Product']),@x;
									\@y;
									} @processes;
				my @contexts = map {
									my @x = @$_; 
									my @y = map getRelationship(\@alledges,$_,'Context'),@x;
									\@y;
									} @processes;
				
				my @relations = ();
				foreach my $i(0..@processes-1)
				{
					foreach my $j($i..@processes-1)
						{
							if( has_overlap($reacprods[$i],$contexts[$j]) ) 
								{ push @relations, join(" ",($i,$j));}
							next if($i == $j);
							if( has_overlap($reacprods[$j],$contexts[$i]) ) 
								{ push @relations, join(" ",($j,$i));}
						}
				}
				@relations = uniq(@relations);
				
				my @pr1 = map join(",",@$_), @processes;
				my @names = ($args{'embed'}==0) ? @pr1 : () x @pr1;
				$pg = initializeProcessGraph(\@processes,\@relations,[],\@names);
			}
	}
	
	if($args{'groups'}==1)
	{
		my @rules = grep { $nodetype{$_} eq 'Rule' } @allnodes;
		my %nodeclass = %{$bpg->{'NodeClass'}};
		my %extended;
		foreach my $node(@allnodes)
		{
			if(has([keys %nodeclass],$node)) { $extended{$node} = $nodeclass{$node}; }
			else { $extended{$node} = $node; }
		}
		my @processes = uniq( map $extended{$_}, @rules);
		my @reacprods = map {
							my $p = $_;
							my @x = grep {$extended{$_} eq $p} @rules; 
							my @y = map getRelationships(\@alledges,$_,['Reactant','Product']),@x;
							my @z = uniq( map {$extended{$_}} @y );
							\@z;
							} @processes;
		my @contexts = map {
							my $p = $_;
							my @x = grep {$extended{$_} eq $p} @rules; 
							my @y = map getRelationship(\@alledges,$_,'Context'),@x;
							my @z = uniq( map {$extended{$_}} @y );
							\@z;
							} @processes;

		if($args{'mergepairs'}==0)
			{				
				my @relations = ();
				foreach my $i(0..@processes-1)
				{
					foreach my $j($i..@processes-1)
						{
							if( has_overlap($reacprods[$i],$contexts[$j]) ) 
								{ push @relations, join(" ",($i,$j));}
							next if($i == $j);
							if( has_overlap($reacprods[$j],$contexts[$i]) ) 
								{ push @relations, join(" ",($j,$i));}
						}
				}
				@relations = uniq(@relations);
				
				#my @names = @processes;
				my @names = ($args{'embed'}==0) ? @processes : () x @processes;
				$pg = initializeProcessGraph(\@processes,\@relations,[],\@names);
			}
		else
			{
				my @procs2;
				my @reacprods2;
				my @contexts2;
				my %merged;
				@merged { 0..@processes-1 } = (0) x @processes;
				foreach my $i(0..@processes-1)
				{
					next if ($merged{$i}==1);
					my @rp1 = sort {$a cmp $b} uniq(@{$reacprods[$i]});
					foreach my $j(($i+1)..@processes-1)
						{
							my @rp2 = sort {$a cmp $b} uniq(@{$reacprods[$j]});
							if( arrayEquals(\@rp1,\@rp2) ) 
							{ 
								push @procs2, [$processes[$i],$processes[$j]];
								push @reacprods2, \@rp1;
								my @x = (@{$contexts[$i]},@{$contexts[$j]});
								push @contexts2,\@x;
								$merged{$i} = 1;
								$merged{$j} = 1;
							}
						}
					if($merged{$i} == 0) 
						{
							push @procs2, [$processes[$i]];
							push @reacprods2, \@rp1;
							push @contexts2,[uniq(@{$contexts[$i]})];
						}
				}
				my @relations = ();
				foreach my $i(0..@procs2-1)
				{
					foreach my $j($i..@procs2-1)
						{
							if( has_overlap($reacprods2[$i],$contexts2[$j]) ) 
								{ push @relations, join(" ",($i,$j));}
							next if($i == $j);
							if( has_overlap($reacprods2[$j],$contexts2[$i]) ) 
								{ push @relations, join(" ",($j,$i));}
						}
				}
				@relations = uniq(@relations);
				my @pr1 = map join(",",@$_), @procs2;
				my @names = ($args{'embed'}==0) ? @pr1 : () x @pr1;
				$pg = initializeProcessGraph(\@procs2,\@relations,[],\@names);
			}	
	}
	return $pg;
}

sub embedProcessGraph
{
	my $pg = shift @_;
	my $gr = shift @_;
	my %args = %{shift @_};
	my $bpg = $gr->{'RuleNetworkCurrent'};
	my $bpg2;
	#my $bpg2 = collapseNetworkGraph($bpg);
	
	my @nodes = @{$pg->{'Nodes'}};
	my @names = @{$pg->{'Names'}};
	my @embed = () x @nodes;
	my $mergepairs = $args{'mergepairs'};
	my $groups = $args{'groups'};
	if($groups) {$bpg2 = collapseNetworkGraph($bpg);}
	else {$bpg2 = $bpg;}
	
	my @reacprods = grep {$_ =~ /^.*:.*:(.*)/; has(['Reactant','Product'],$1);} 
					@{$bpg2->{'EdgeList'}};
	my @rsgs = map {@$_;} flat($gr->{'RuleStructureGraphs'});
	my @rnames = map {@$_;} flat($gr->{'RuleNames'}); 
	foreach my $i(0..@nodes-1)
	{
		my $node = $nodes[$i];
		my @arr = ($mergepairs==1) ? @$node : ($node);
		if($groups==1)
		{
			my @edges = map { 
							my $x = $_;
							grep {$_ =~ /^(.*):.*:.*/; $1 eq $x} 
							@reacprods
							} @arr;
			@edges = uniq(@edges);
			$embed[$i] = makeRuleNetworkGraphFromEdges(\@edges,$bpg2->{'NodeType'},$names[$i]);
		}
		else
		{
			my @rsgs1 = map {
							my $x = $_;
							map {$rsgs[$_]}
							grep {$rnames[$_] eq $x} 0..@rnames-1;
							} @arr;
			$embed[$i] = combine2(\@rsgs1);
		}
	}
	$pg->{'Embed'} = \@embed;
	return;
}
sub getRelationship
{
	my @edgelist = @{shift @_};
	my $node = shift @_;
	my $reltype = shift @_;

	my @edges = grep { $_ =~ /.*:.*:(.*)$/; $1 eq $reltype} @edgelist;
	my @arr1 = map { $_ =~ /^(.*):.*:.*$/; $1;} grep { $_ =~ /.*:(.*):.*$/; $1 eq $node} @edges;
	my @arr2 = map { $_ =~ /.*:(.*):.*$/; $1;} grep { $_ =~ /^(.*):.*:.*$/; $1 eq $node} @edges;
	return (@arr1,@arr2);
}

sub getRelationships
{
	my $edgelist = shift @_;
	my $node = shift @_;
	my @reltypes = @{shift @_};
	my @arr = map {getRelationship($edgelist,$node,$_)} @reltypes;
	return @arr;
}
sub is_reverse_of
{
	my @proc1_reac = sort {$a cmp $b} @{shift @_};
	my @proc1_prod = sort {$a cmp $b} @{shift @_};
	my @proc2_reac = sort {$a cmp $b} @{shift @_};
	my @proc2_prod = sort {$a cmp $b} @{shift @_};
	my $ret = 0;
	$ret = 1 if(scalar @proc1_reac and arrayEquals(\@proc1_reac,\@proc2_prod));
	$ret = 1 if(scalar @proc1_prod and arrayEquals(\@proc1_prod,\@proc2_reac));
	return $ret;
}

sub has_overlap
{
	my @x = @{shift @_};
	my @y = @{shift @_};
	my @z = grep {has(\@y,$_) } @x;
	return (scalar(@z) > 0) ? 1: 0;
}
sub getWCs
{
	my @aps = @{shift @_};
	my @wcs = @{shift @_};
	my @edges = @{shift @_};
	my @rets = ();
	foreach my $ap(@aps)
	{
		foreach my $wc(@wcs)
		{
			my $str = join(":",($wc,$ap,'Wildcard'));
			my @matches = grep {$_ eq $str} @edges;
			if(@matches) { push @rets,map {$_ =~ /^(.*):.*:.*/; $1; } @matches; }
		}
	}
	return uniq(@rets);
}

sub make_name
{
	my ($x,$y,$z) = @{shift @_};
	my @procs = @$x;
	my @reac = sort {$a cmp $b} @$y;
	my @prod = sort {$a cmp $b} @$z;
	
	my $str1 = join(",",@procs);
	my $str2 = join("+",@reac);
	my $str3 = join("+",@prod);
	
	my $arrow = (scalar @procs > 1) ? "<->" : "->";
	#return $str1."\n".$str2.$arrow.$str3;
	return $str2.$arrow.$str3;
}

sub arrayEquals
{
	my @arr1 = sort {$a cmp $b} @{shift @_};
	my @arr2 = sort {$a cmp $b} @{shift @_};
	
	return 0 if (scalar @arr1 != scalar @arr2);
	foreach my $i(0..@arr1-1)
		{
			return 0 if ($arr1[$i] ne $arr2[$i]);
		}
	return 1;
}
1;
