package Viz;
# pragmas
use strict;
use warnings;
no warnings 'redefine';
# Perl Modules
use Class::Struct;
# BNG Modules
use Visualization::Viz;
use Visualization::StructureGraph;
use SpeciesGraph;

struct NetworkGraph => 
{ 
	'NodeList' => '@', # array of strings
	'EdgeList' => '@', # array of strings
	'NodeType' => '%', # a hash indicating each node type
	'NodeClass' => '%', # a hash indicating equivalence class
	'Name' => '$', # a name which might come in handy to compare/combine rules
	# of the form <transformationstring>:<atomicpatternstring>:<edgetype>
	# or <wildcardpattern>:<atomicpatternstring>:Wildcard
	'Merged'=> 0,
	'Collapsed'=>0,
	'Filtered'=>0,
	
};
# is methods for checking
sub isWildcard{ return ($_[0] =~ /\!\+/) ? 1 : 0; }

# basic make methods
sub makeAtomicPattern
{
	my @nodelist = @{shift @_};
	my $node = shift @_;
	
	my $type = $node->{'Type'};
	my $ap;
	if ($type eq 'CompState')
		{
		my $comp = findNode(\@nodelist,${$node->{'Parents'}}[0]);
		my $mol = findNode(\@nodelist,${$comp->{'Parents'}}[0]);
		my $string = $mol->{'Name'}."(".$comp->{'Name'}."~".$node->{'Name'}.")";
		$ap = ($node->{'Name'} ne '?') ?  $string : "";
		}
	elsif ($type eq 'BondState')
		{
		my @comps = map (findNode(\@nodelist,$_), @{$node->{'Parents'}}) ;
		my @mols = map (findNode(\@nodelist,${$_->{'Parents'}}[0]), @comps) ;
		if (scalar @comps == 1)
			{
			# it's a wildcard
			my $string = $mols[0]->{'Name'}."(".$comps[0]->{'Name'}."!".$node->{'Name'}.")";
			$ap = ($node->{'Name'} ne '?') ?  $string : "";
			}
		else
			{
			# it's a specified bond
			my $string1 = $mols[0]->{'Name'}."(".$comps[0]->{'Name'}."!1)";
			my $string2 = $mols[1]->{'Name'}."(".$comps[1]->{'Name'}."!1)";
			$ap = join(".", sort {$a cmp $b} ($string1,$string2));
			}
		}
	elsif ($type eq 'Comp')
		{
		# return the unbound state
		# is it really unbound? check it external to this method
		my $mol = findNode(\@nodelist,${$node->{'Parents'}}[0]);
		my $string = $mol->{'Name'}."(".$node->{'Name'}.")";
		$ap = $string;
		}
	elsif ($type eq 'Mol')
		{
		$ap = $node->{'Name'};
		}
	return $ap;
}

sub makeAtomicPatterns 
{ 
	my $nodelist = shift @_;
	my $nodes = shift @_;
	my @aps = map { makeAtomicPattern($nodelist,$_)} @$nodes; 
	return @aps;
}

sub makeTransformation
{
	my @nodelist = @{shift @_};
	my $node = shift @_;
	my $type = $node->{'Type'};
	my $name = $node->{'Name'};
	my $arrow = "->";
	my $comma = ",";
	my $tr;
	if ($type ne 'GraphOp') { return undef; }
	if ($name eq 'ChangeState')
		{
		my @comps = map (findNode(\@nodelist,$_), @{$node->{'Parents'}});
		my @left = grep( $_->{'Side'} eq 'left', @comps) ;
		my @right = grep( $_->{'Side'} eq 'right', @comps) ;
		my $leftstr = makeAtomicPattern(\@nodelist,$left[0]);
		my $rightstr = makeAtomicPattern(\@nodelist,$right[0]);
		$tr = $leftstr.$arrow.$rightstr;
		}
	elsif ($name eq 'AddBond')
		{
		my $bond = findNode(\@nodelist,${$node->{'Parents'}}[0]);
		my @comps = map (findNode(\@nodelist,$_), @{$bond->{'Parents'}});
		my @leftstr = sort map ( makeAtomicPattern(\@nodelist,$_),@comps);
		my $rightstr = makeAtomicPattern(\@nodelist,$bond);
		$tr = join($comma,@leftstr).$arrow.$rightstr;
		}
	elsif ($name eq 'DeleteBond')
		{
		my $bond = findNode(\@nodelist,${$node->{'Parents'}}[0]);
		# bond wildcards are also being deleted when molecules are deleted
		# how do you transform them into processes?
		# need to talk to bngdev
		my @comps = map (findNode(\@nodelist,$_), @{$bond->{'Parents'}});
		if (scalar @comps == 1) { return ""; }
		my @rightstr = sort map ( makeAtomicPattern(\@nodelist,$_),@comps);
		my $leftstr = makeAtomicPattern(\@nodelist,$bond);
		$tr = $leftstr.$arrow.join($comma,@rightstr);
		}
	elsif ($name eq 'AddMol')
		{
		my $mol = findNode(\@nodelist,${$node->{'Parents'}}[0]);
		my $name = makeAtomicPattern(\@nodelist,$mol);
		$tr = $arrow.$name;
		}
	elsif ($name eq 'DeleteMol')
		{
		# species deletion is interpreted as molecule deletion
		# how to check? what to do?
		my $mol = findNode(\@nodelist,${$node->{'Parents'}}[0]);
		my $name = makeAtomicPattern(\@nodelist,$mol);
		$tr = $name.$arrow;
		}
	return $tr;
}

sub makeTransformationDeleteBond
{
	my @nodelist = @{shift @_};
	my $node = shift @_;
	my $type = $node->{'Type'};
	my $name = $node->{'Name'};
	my $arrow = "->";
	my $comma = ",";
	my $tr;
	if ($name eq 'DeleteBond')
		{
		my $bond = findNode(\@nodelist,${$node->{'Parents'}}[0]);
		return $tr if ($bond->{'Name'} eq '?');
		my @comps = grep {$_->{'Side'} eq 'both'} map (findNode(\@nodelist,$_), @{$bond->{'Parents'}});
		my @rightstr = sort map ( makeAtomicPattern(\@nodelist,$_),@comps);
		my $leftstr = makeAtomicPattern(\@nodelist,$bond);
		$tr = $leftstr.$arrow.join($comma,@rightstr);
		}
	return $tr;

}

sub makeEdge
{
	my %shortname = ( 'r'=>"Reactant", 'p'=>"Product", 'c'=>"Context", 's'=>"Syndel", 'w'=>"Wildcard", 'pp'=>"ProcessPair", 'co'=>"Cotransform", 'os'=>"Onsite" );
	
	my $node1 = shift @_;
	my $node2 = shift @_;
	my $rel = $shortname{shift @_};
	
	my $string = $node1.":".$node2.":".$rel;
	return $string;
	
}

# print for sanity check
sub printNetworkGraph
{
	my $bpg = shift @_;
	my @nodelist = @{$bpg->{'NodeList'}};
	my %nodetype = %{$bpg->{'NodeType'}};
	
	#get atomic patterns
	my @ap = grep { $nodetype{$_} eq 'AtomicPattern' } @nodelist;
	# get binding sites
	my @bs = sort {$a cmp $b} grep { $_ !~ /~/ and $_ !~ /\!/ } @ap;
	# get internal states
	my @is = sort {$a cmp $b} grep {$_ =~ /~/ } @ap;
	# get bonds
	my @bonds = sort {$a cmp $b} grep { $_ =~ /\!/ and $_ !~ /\!\+/ } @ap;
	# wildcards
	my @wc = sort {$a cmp $b} grep { $_ =~ /\!\+/ } @ap;
	# rules
	my @rules = sort {$a cmp $b} grep { $nodetype{$_} eq 'Rule' } @nodelist;
	# groups
	my %classes;
	if(defined $bpg->{'NodeClass'}) {%classes = %{$bpg->{'NodeClass'}};}
	
	my @rulegroups;
	my @patterngroups;
	if($bpg->{'Collapsed'}==1)
		{
		@rulegroups = grep {$nodetype{$_} eq 'RuleGroup'} @nodelist;
		@patterngroups = grep {$nodetype{$_} eq 'PatternGroup'} @nodelist;
		}
	else
		{
		my @classedrules = grep {$nodetype{$_} eq 'Rule'} keys %classes;
		@rulegroups = 	map 
							{
							my $x = $_; 
							$x.":".join(" ",
								sort {$a cmp $b}
								grep {$classes{$_} eq $x} @classedrules
								);
							}
							sort {$a cmp $b} 
							uniq( map $classes{$_}, @classedrules);
		my @classedpatterns = grep {$nodetype{$_} eq 'AtomicPattern'} keys %classes;
		@patterngroups = map 
							{
							my $x = $_; 
							$x.":".join(" ",
								sort {$a cmp $b}
								grep {$classes{$_} eq $x} @classedpatterns
								);
							}
							sort {$a cmp $b} 
							uniq( map $classes{$_}, @classedpatterns);
		}
	
	
	my @str;
	if(@bs) { push @str,"Binding Sites:\n".join("\n",@bs)."\n"; }
	if(@is) { push @str,"Internal States:\n".join("\n",@is)."\n"; }
	if(@bonds) { push @str,"Bonds:\n".join("\n",@bonds)."\n"; }
	if(@wc) { push @str,"Wildcards:\n".join("\n",@wc)."\n"; }
	if(@rules) { push @str,"Rules:\n".join("\n",@rules)."\n"; }
	#if(@groups)
	#	{
	#	my @grpstrs = map {$names[$_].":".join(" ",@{$groups[$_]}) } 0..@groups-1;
	#	push @str,"Groups:\n".join("\n",@grpstrs)."\n";
	#	}
	if(@patterngroups) { push @str,"Pattern Groups:\n".join("\n",@patterngroups)."\n"; }
	if(@rulegroups) { push @str,"Rule Groups:\n".join("\n",@rulegroups)."\n"; }
		
	my @edgelist = @{$bpg->{'EdgeList'}};
	my @reac = sort {$a cmp $b} map {$_ =~ /(.*:.*):.*/} grep {$_ =~ /Reactant$/} @edgelist;
	my @prod = sort {$a cmp $b} map {$_ =~ /(.*:.*):.*/} grep {$_ =~ /Product$/} @edgelist;
	my @context = sort {$a cmp $b} map {$_ =~ /(.*:.*):.*/} grep {$_ =~ /Context$/} @edgelist;
	my @wildcards = sort {$a cmp $b} map {$_ =~ /(.*:.*):.*/} grep {$_ =~ /Wildcard$/} @edgelist;
	
	if(@reac) { push @str,"Reactant Relationships:\n".join("\n",@reac)."\n"; }
	if(@prod) { push @str,"Product Relationships:\n".join("\n",@prod)."\n"; }
	if(@context) { push @str,"Context Relationships:\n".join("\n",@context)."\n"; }
	if(@wildcards) { push @str,"Wildcard Relationships:\n".join("\n",@wildcards)."\n"; }
	
	return join("\n",@str);
}
# text cleaning for atomic patterns and transformations
sub prettify
{
	my $string = shift @_;
	my $arrow = '->';
	#print ($string, $string =~ /$arrow/, "\n");
	# check if it is a transformation
	if ($string =~ /$arrow/)
	{
		# see if arrow has spaces already
		if ($string =~ /\b$arrow\b/) { return $string;}
		else  
		{
			my @splits = split $arrow,$string;
			if (scalar @splits == 1) { push @splits,"0"; }
			elsif (length $splits[0] == 0) { $splits[0]="0";}
			return join(" -> ",map prettify($_), @splits);
		}
	}
	my $comma = ',';
	if ($string =~ /$comma/) 
	{
		if ($string =~ /\b$comma\b/) { return $string; }
		else 
		{ 
			my @splits = split $comma,$string;
			return join(" , ", @splits); 
		}
	} 
	if ($string =~ /$0^/) { return $string; } 
	#if ($string =~ /\(/)  { return $string; }
	#else { return $string."\(\)"; }
	return $string;
}

sub unprettify
{
	my $string = shift @_;
	$string =~ s/\s//g;
	$string =~ s/\(\)//g;
	$string =~ s/^0//g;
	$string =~ s/0$//g;
	return $string;
}




sub combine3
{
	my @bpgs = @{shift @_};
	my @nodelist = ();
	my @edgelist = ();
	my %nodetype;
	foreach my $bpg(@bpgs)
	{
		push @nodelist, @{$bpg->{'NodeList'}};
		push @edgelist, @{$bpg->{'EdgeList'}};
		foreach my $node( keys %{$bpg->{'NodeType'}} )
		{
			$nodetype{$node} = $bpg->{'NodeType'}->{$node};
		}
		
	}
	my $bpg = NetworkGraph->new();
	$bpg->{'NodeList'} = [uniq(@nodelist)];
	$bpg->{'EdgeList'} = [uniq(@edgelist)];
	$bpg->{'NodeType'} = \%nodetype;
	return $bpg;
}
sub addWildcards
{
	my $bpg = shift @_;
	my @nodelist = @{$bpg->{'NodeList'}};
	my %nodetype = %{$bpg->{'NodeType'}};
	
	my @ap = grep {$nodetype{$_} eq 'AtomicPattern'} @nodelist;
	my @wildcards = grep (isWildcard($_), @ap);
	my @notwildcards = grep (!isWildcard($_), @ap);

	foreach my $wc(@wildcards)
		{
		my @splits = split '\+', $wc;
		my $string = $splits[0];
		
		my @matches = grep(index($_, $string) != -1, @notwildcards);
		foreach my $match(@matches)
			{
			my $edge = makeEdge($wc,$match,'w');
			push @{$bpg->{'EdgeList'}},$edge;
			}
		}
	return;
}

# get methods
sub getReactantsProducts
{
	my $in = shift @_;
	my $string = unprettify($in);
	my @splits = split '->',$string;
	my @reac = ();
	my @prod = ();
	if (scalar @splits == 1) { @reac = ($splits[0]); }
	elsif (length $splits[0] == 0) { @prod = ($splits[1]); }
	else { @reac = split ',',$splits[0]; @prod = split ',',$splits[1]; }
	return (\@reac,\@prod);
}

sub getStructures
{
	my @nodelist = @{shift @_};
	my %structures = ('Mol'=>1,'Comp'=>1,'CompState'=>1,'BondState'=>1,'GraphOp'=>0,'Rule'=>0);
	my @nodes = grep( $structures{$_->{'Type'}}==1, @nodelist);
	return @nodes;
}
sub getContext
{
	my @nodelist = @{shift @_};
	my @exclude = ();
	if (@_) { @exclude = @{shift @_} };
	my @exclude_ids = ();
	foreach my $exc (@exclude)
	{
		my @x = @$exc;
		my $y = shift @x;
		push @exclude_ids, map $_->{'ID'}, @x;
	}
	#print scalar @exclude_ids;
	my @nodes_struct = getStructures(\@nodelist);
	my @nodes = hasSide(\@nodes_struct,'both');
	my @context = ();
	
	# comp states
	my @compstates = 	grep has(\@exclude_ids,$_->{'ID'})==0,
						hasType(\@nodes,'CompState');
	if (@compstates)
	{
		foreach my $node(@compstates)
		{
			my $string = makeAtomicPattern(\@nodes_struct,$node);
			if ($string) { push @context,$string;}
		}
	}
	
	# bond states
	my @bondstates = 	grep has(\@exclude_ids,$_->{'ID'})==0,
						hasType(\@nodes,'BondState');
	foreach my $node(@bondstates)
	{
		my $string = makeAtomicPattern(\@nodes_struct,$node);
		if ($string) { push @context,$string;}
	}
	
	# unbound states
	my @comps =	grep has(\@exclude_ids,$_->{'ID'})==0,
				hasType(\@nodes,'Comp');
	my %unbound;
	foreach my $x(@comps) { $unbound{$x->{'ID'}}=1; }
	my @allbonds = hasType(\@nodelist,'BondState');
	my @allbondparents;
	foreach my $node(@allbonds) { push @allbondparents, @{$node->{'Parents'}}; }
	foreach my $x(@allbondparents) { $unbound{$x}=0; }
	foreach my $x(keys %unbound) 
	{ 
		if ($unbound{$x}) 
		{
			my $node = findNode(\@comps,$x);
			push @context,makeAtomicPattern(\@nodes_struct,$node);
		}
	}
	
	# mol nodes that do not have any components (hence identified by only label)
	my @mols = hasType(\@nodes,'Mol');
	my %havenocomps;
	foreach my $x(@mols) { $havenocomps{$x->{'ID'}}=1; }
	my @allcompparents;
	foreach my $node(hasType(\@nodes,'Comp')) 
		{ push @allcompparents, @{$node->{'Parents'}}; }
	foreach my $x(@allcompparents) { $havenocomps{$x}=0; }
	foreach my $x(keys %havenocomps) 
	{ 
		if ($havenocomps{$x}) 
		{
			my $node = findNode(\@mols,$x);
			push @context,makeAtomicPattern(\@nodes_struct,$node);
		}
	}
	
	return @context;
}

sub getSyndelContext
{
	my @nodelist = @{shift @_};
	my $op = shift @_;
	
	my $mol = findNode(\@nodelist,${$op->{'Parents'}}[0]);
	
	# get child components
	my @allcomps = hasType(\@nodelist,'Comp');
	my @comps = grep (${$_->{'Parents'}}[0] eq $mol->{'ID'}, @allcomps);
	my @comps_ids = map $_->{'ID'}, @comps;
	
	# get child component states
	my @allcompstates = hasType(\@nodelist,'CompState');
	my @compstates = ();
	foreach my $x(@allcompstates)
	{
		foreach my $y (@comps_ids)
			{
				if (${$x->{'Parents'}}[0] eq $y) { push @compstates,$x; }
			}
	}
	
	# get child bond states
	my %unbound;
	foreach my $y (@comps_ids) { $unbound{$y} = 1; }
			
	my @allbondstates = hasType(\@nodelist,'BondState');
	my @bondstates = ();
	foreach my $x(@allbondstates)
	{	
		my @parents = @{$x->{'Parents'}};
		foreach my $y (@comps_ids)
		{
			foreach my $z(@parents)
			{
			if ($y eq $z) 
				{
				push @bondstates,$x;
				$unbound{$z} = 0;
				}
			}
		}
	}
	
	my @unboundcomps = ();
	foreach my $x(keys %unbound)
	{
	if ($unbound{$x}) 
		{
		my $node = findNode(\@nodelist,$x);
		push @unboundcomps, $node;
		}
	}
	
	my @syndelnodes = (@compstates,@bondstates,@unboundcomps);
	my @syndel = ();
	foreach my $node(@syndelnodes) { push @syndel, makeAtomicPattern(\@nodelist,$node); }
	
	return @syndel;
}

sub getTransformations
{
	my $rsg = shift @_;
	my @nodelist = @{$rsg->{'NodeList'}};
	my @graphop = hasType(\@nodelist,'GraphOp');
	my @tr = map {makeTransformation(\@nodelist,$_);} @graphop;
	return @tr;
}
sub reverseTransformation
{
	my $tr = shift @_; #unprettified
	my @splits = reverse split('->',prettify($tr));
	#my @splits2 = map ( join(',',sort split(',',$_ =~ s/\s//g)), @splits);
	sub clean { $_ =~ s/\s//g; return $_; }
	my @splits2 = map ( join(',',sort split(',',clean($_))), @splits);
	my $tr2 = unprettify(join '->',@splits2 );
	return $tr2;
}

sub stringToAtomicPattern
{
		my $pat = shift @_;
		my $patstr = $pat;
		my $sg = SpeciesGraph->new();
		my $err = SpeciesGraph::readString($sg,\$patstr);
		my $psg = makePatternStructureGraph($sg);
		my @nodes = @{$psg->{'NodeList'}};
		my @ap = uniq(makeAtomicPatterns(\@nodes,\@nodes));
		my @pats;
		if($pat =~ /\!/ and $pat !~ /\!\+/) 
			{
			@pats = grep { $_ =~ /\!/ } @ap;
			}
		elsif($pat =~ /\!\+/) 
			{ 
			@pats = grep { $_ =~ /\!\+/ } @ap;
			}
		elsif($pat =~ /~/) 
			{ 
			@pats = grep { $_ =~ /~/ } @ap;
			}
		elsif($pat =~ /\(.{1,}\)/)
			{
			@pats = grep { $_ =~ /\(.{1,}\)/ } @ap;
			}
		elsif($pat =~ /\(\)/)
			{
			@pats = @ap;
			}
		if (scalar @pats != 1) 
			{
			return $pat;
			}
	return $pats[0];
}

# make graph methods
sub makeRuleNetworkGraph
{
	# from a rule structure graph
	my $rsg = shift @_;
	my $name = shift @_;
	# process name so that it doesnt begin with a number
	$name = ($name =~ /^[0-9]/) ? "_".$name : $name;
	
	my @nodelist = @{$rsg->{'NodeList'}};
	
	my $bpg = NetworkGraph->new();
	$bpg->{'Name'} = $name;
	
	my @graphop = hasType(\@nodelist,'GraphOp');
	my @contexts = getContext(\@nodelist);
	
	# add node for rule
	push @{$bpg->{'NodeList'}}, $name;
	$bpg->{'NodeType'}->{$name} = 'Rule';
	
	# add reactant and product edges
	foreach my $op(@graphop)
	{
		my $tr = makeTransformation(\@nodelist,$op);
		if($op->{'Name'} eq 'DeleteBond')
		{
			$tr = makeTransformationDeleteBond(\@nodelist,$op);
			# bond deletion is treated here
			# if there's a deletemol, AB -> A, then it shows only A as the product
			# wildcard delete! if A!+ -> A, then this shows A as the product
			# if A!? -> 0, ignore. Is this bad form? Testing needed.
		}
		if(not defined $tr) { next; }
		if(length $tr == 0) { next; }
		my ($reac,$prod) = getReactantsProducts($tr);
		push @{$bpg->{'NodeList'}}, @$reac, @$prod;
		foreach my $reactant (@$reac)
		{
			if (length $reactant == 0) {next;}
			my $edge = makeEdge($name,$reactant,'r');
			push @{$bpg->{'NodeList'}}, $reactant;
			push @{$bpg->{'EdgeList'}}, $edge;
			$bpg->{'NodeType'}->{$reactant} = 'AtomicPattern';
			
		}
		foreach my $product (@$prod)
		{
			if (length $product == 0) {next;}
			my $edge = makeEdge($name,$product,'p');
			push @{$bpg->{'NodeList'}}, $product;
			push @{$bpg->{'EdgeList'}}, $edge;
			$bpg->{'NodeType'}->{$product} = 'AtomicPattern';
		}
	}
	# add context edges
	foreach my $context(@contexts)
	{
		if (length $context == 0) {next;}
		my $edge = makeEdge($name,$context,'c');
		push @{$bpg->{'NodeList'}}, $context;
		push @{$bpg->{'EdgeList'}}, $edge;
		$bpg->{'NodeType'}->{$context} = 'AtomicPattern';
	}
	# add syndel edges
	foreach my $op(@graphop)
	{
		if ($op->{'Name'} =~ /Mol/)
		{
			my $rel = ($op->{'Name'} =~ /Add/) ? 'p' : 'r';
			my @syndels = getSyndelContext(\@nodelist,$op);
			foreach my $syndel(@syndels)
			{
				if (length $syndel == 0) {next;}
				my $edge = makeEdge($name,$syndel,$rel);
				push @{$bpg->{'NodeList'}}, $syndel;
				push @{$bpg->{'EdgeList'}}, $edge;
				$bpg->{'NodeType'}->{$syndel} = 'AtomicPattern';
			}
		}
	}
	
	uniqNetworkGraph($bpg);
	addWildcards($bpg);
	uniqNetworkGraph($bpg);
	return $bpg;
	
}

sub makeRxnNetworkGraph
{
	my $rxn = shift @_;
	my @reac = map {$_->SpeciesGraph->toString()} @{$rxn->Reactants};
	my @prod = map {$_->SpeciesGraph->toString()} @{$rxn->Products};
	my $name = "Rxn".$rxn->Index;
	my @nodes = uniq($name,@reac,@prod);
	my %nodetype;
	$nodetype{$name} = "Rule";
	map {$nodetype{$_} = "AtomicPattern"} (@reac,@prod);
	my @edges = ();
	foreach my $sp(@reac)
	{
		push @edges, makeEdge($name,$sp,"r");
	}
	foreach my $sp(@prod)
	{
		push @edges, makeEdge($name,$sp,"p");
	}
	my $bpg = makeRuleNetworkGraph_simple(\@nodes,\@edges,\%nodetype);
	return $bpg;
}
sub makeRuleNetworkGraph_simple
{
	my @nodes = @{shift @_};
	my @edges = @{shift @_};
	my %nodetype = %{shift @_};
	my $name = shift @_;
	
	my $bpg = NetworkGraph->new();
	$bpg->{'NodeType'} = \%nodetype;
	$bpg->{'NodeList'} = \@nodes;
	$bpg->{'EdgeList'} = \@edges;
	$bpg->{'Name'} = $name;
	return $bpg;

}

sub makeRuleNetworkGraphFromEdges
{
	my @edges = @{shift @_};
	my %nodetype = %{shift @_};
	my $name = shift @_;
	
	my @nodes = uniq(map {$_=~ /^(.*):(.*):.*/; ($1,$2);} @edges);
	my %types;
	updateDict(\%types,\%nodetype,\@nodes);
	my $bpg = NetworkGraph->new();
	$bpg->{'NodeType'} = \%nodetype;
	$bpg->{'NodeList'} = \@nodes;
	$bpg->{'EdgeList'} = \@edges;
	$bpg->{'Name'} = $name;
	return $bpg;

}


# do things to network graphs
sub uniqNetworkGraph
{
	my $bpg = shift(@_);
	$bpg->{'NodeList'} = [uniq(@{$bpg->{'NodeList'}})];
	$bpg->{'EdgeList'} = [uniq(@{$bpg->{'EdgeList'}})];
	return;
}
sub mergeNetworkGraphs
{
	my @x = @_;
	my $bpg = combine3(\@x);
	uniqNetworkGraph($bpg);
	addWildcards($bpg);
	uniqNetworkGraph($bpg);
	$bpg->{'Merged'} =1;
	return $bpg;
}
sub resolveWildcards
{
	my $bpg = shift @_;
	my @edgelist = @{$bpg->{'EdgeList'}};
	my %nodetype = %{$bpg->{'NodeType'}};
	my @nodelist = @{$bpg->{'NodeList'}};
	my @wc_edges = grep {$_ =~ /Wildcard$/} @edgelist;
	# don't do any work if there are no wildcards
	return $bpg if( not @wc_edges );
	
	print "Resolving wildcard edges.\n";
	
	my @wcs = 	grep { index($_,'!+') != -1 } 
				grep { $nodetype{$_} eq 'AtomicPattern'}
				@nodelist;
	my @wc_context = grep {$_ =~ /.*:(.*):.*/; my $x = $1; index($1,'!+') != -1;} 
					grep {$_ =~ /Context$/} 
					@edgelist;
	my @new_edges;
	foreach my $wc(@wcs)
	{
		my @r = map { $_ =~ /(.*):.*:.*/; $1; }
				grep { $_ =~ /.*:(.*):.*/; my $x = $1; index($x,$wc) != -1; } 
				@wc_context;
		my @b = map { $_ =~ /.*:(.*):.*/; $1; } 
				grep { $_ =~ /^(.*):.*:.*/; my $x = $1; index($x,$wc) != -1; } 
				@wc_edges;
		foreach my $rule(@r)
		{
			foreach  my $bond(@b)
				{
				push @new_edges, join(":",($rule,$bond,'Context'));
				}
		}
	}
	
	my @nodelist2 = grep { not has(\@wcs,$_); } @nodelist;
	my @edges_to_remove = (@wc_edges,@wc_context);
	my @edgelist2 = grep { not has(\@edges_to_remove,$_); } @edgelist;
	push @edgelist2,@new_edges;
	
	@nodelist2=  uniq(@nodelist2);
	@edgelist2 = uniq(@edgelist2);
	
	my %nodetype2;
	updateDict(\%nodetype2,\%nodetype,\@nodelist2);
	
	my $bpg2 = NetworkGraph->new();
	$bpg2->{'NodeList'} = \@nodelist2;
	$bpg2->{'EdgeList'} = \@edgelist2;
	$bpg2->{'NodeType'} = \%nodetype2;
	
	return $bpg2;
}

sub filterNetworkGraph
{
	# when $reverse is not mentioned, it simply removes the nodes that are
	# included in @$filter from the bpg
	# when $reverse eq 'reverse', it removes everything BUT those nodes
	my $bpg = shift @_;
	my $filter = shift @_;
	my $reverse = @_ ? shift @_ : '';
	my $includegroups = @_ ? shift @_ : 0;

	my @nodelist = @{$bpg->{'NodeList'}};
	my @edgelist = @{$bpg->{'EdgeList'}};
	my %nodetype = %{$bpg->{'NodeType'}};
	
	
	if( $reverse eq 'reverse' )
	{
		# this is if a reverse
		my @filter2 = grep { has($filter,$_)==0; } @nodelist;
		$filter = \@filter2;
	}
	
	my @new_nodelist = grep { has($filter,$_)==0; } @nodelist;
	my %new_nodetype = map { $_=>$nodetype{$_} } @new_nodelist;
	
	
	my @removed_edges;
	my @remove1 = 	grep { 
					my $x = $_; 
					$x =~ /.*:(.*):.*/; 
					has($filter,$1)==1;
					} @edgelist;
	my @remove2 = 	grep { 
					my $x = $_; 
					$x =~ /(.*):.*:.*/; 
					has($filter,$1)==1;
					} @edgelist;
	my @new_edgelist = grep { has([(@remove1,@remove2)],$_)==0;} @edgelist;
	
	my $bpg2 = NetworkGraph->new();
	$bpg2->{'NodeList'} = \@new_nodelist;
	$bpg2->{'EdgeList'} = \@new_edgelist;
	$bpg2->{'NodeType'} = \%new_nodetype;
	$bpg2->{'Merged'} = $bpg->{'Merged'};
	$bpg2->{'Filtered'} = 1;
	$bpg2->{'Collapsed'} = $bpg->{'Collapsed'};
	
	if($includegroups and defined $bpg->{'NodeClass'})
	{
	my %nodeclass = %{$bpg->{'NodeClass'}};
	my %new_nodeclass = map { $_=>$nodeclass{$_} } @new_nodelist;
	$bpg2->{'NodeClass'} = \%new_nodeclass;
	}
	
	return $bpg2;
}

sub filterNetworkGraphByList
{
	my $bpg = shift @_;
	my @items = @{shift @_};
	my $level = @_ ? shift @_ : 1;
	
	my @nodes = @{$bpg->{'NodeList'}};
	my @edges = @{$bpg->{'EdgeList'}};
	
	for (my $i=1; $i<=$level; $i++)
	{
		my @items2=();
		foreach my $edge(@edges)
		{
			$edge =~ /(.*):(.*):.*/;
			my $x = $1; my $y = $2;
			next if(has(\@items,$x)==has(\@items,$y));
			if(has(\@items,$x)==0) { push @items2,$x; }
			if(has(\@items,$y)==0) { push @items2,$y; }
			#print scalar @items2;print "\n";
		}
		push @items,uniq(@items2);
	}
	#print @items;
	@items = uniq(@items);
	my @remove = grep { has(\@items,$_)==0; } @{$bpg->{'NodeList'}};
	my $bpg2 = filterNetworkGraph($bpg,\@remove);
	uniqNetworkGraph($bpg2);
	if(defined $bpg->{'NodeClass'}) 
	{
		my %classes;
		updateDict(\%classes,$bpg->{'NodeClass'},$bpg2->{'NodeList'});
		$bpg2->{'NodeClass'} = \%classes;
	}
	return $bpg2;
}

sub collapseNetworkGraph
{
	
	my $bpg = shift @_;
	my $doNotCollapseEdges = @_ ? shift @_ : 0;
	my %classes = %{$bpg->{'NodeClass'}};
	
	
	my @classed = keys %classes;
	my @edges = @{$bpg->{'EdgeList'}};
						
	my @classed_rules = grep {$bpg->{'NodeType'}->{$_} eq 'Rule'} keys %classes;
	my @classed_patterns = grep {$bpg->{'NodeType'}->{$_} eq 'AtomicPattern'} keys %classes;
	my @rule_classes = uniq(map $classes{$_}, @classed_rules);
	my @pattern_classes = uniq(map $classes{$_}, @classed_patterns);
	
	my @nodelist2;
	my @edgelist2;
	my %nodetype2;
	foreach my $edge(@edges)
	{
		# deconstruct edge
		$edge =~ /^(.*):(.*):(.*)$/;
		my $x = $1;
		my $y = $2;
		my $z = $3;
		
		if(has([qw(Reactant Product Context Inhibition)],$z) )
		{
			if(has(\@classed_rules,$x)) { $x = $classes{$x}; }
			if(has(\@classed_patterns,$y)) { $y = $classes{$y}; }
		}
		if($z eq 'Wildcard')
		{
			if(has(\@classed_patterns,$x)) { $x = $classes{$x}; };
			if(has(\@classed_patterns,$y)) { $y = $classes{$y}; };
			next if($x eq $y);
		}
		
		#pushy stuff
		push @nodelist2, $x;
		push @nodelist2, $y;
		push @edgelist2, join(":",($x,$y,$z));
		
		if(has(\@rule_classes,$x)) { $nodetype2{$x} = 'RuleGroup'; }
		elsif(has(\@pattern_classes,$x)) { $nodetype2{$x} = 'PatternGroup'; }
		else {$nodetype2{$x} = $bpg->{'NodeType'}->{$x}; }
		
		if(has(\@pattern_classes,$y)) { $nodetype2{$y} = 'PatternGroup'; }
		else {$nodetype2{$y} = $bpg->{'NodeType'}->{$y}; }
	}
	
	@nodelist2=  uniq(@nodelist2);
	if ($doNotCollapseEdges==0) { @edgelist2 = uniq(@edgelist2);}
	
	
	my $bpg2 = NetworkGraph->new();
	$bpg2->{'NodeList'} = \@nodelist2;
	$bpg2->{'EdgeList'} = \@edgelist2;
	$bpg2->{'NodeType'} = \%nodetype2;
	$bpg2->{'Merged'} = $bpg->{'Merged'};
	$bpg2->{'Collapsed'} = 1;
	
	return $bpg2;
}

sub updateDict
{
	my $update_this = shift @_;
	my $update_using  = shift @_;
	my $update_list = shift @_;
	
	#my @keys1 = keys %{$update_this};
	my @keys2 = keys %{$update_using};
	my @common_keys = grep { has(\@keys2,$_)  } @{$update_list};
	
	map { $update_this->{$_} = $update_using->{$_} } @common_keys; 
	return;
}

sub duplicateNetworkGraph
{
	my $bpg = shift @_;
	my $bpg2 = NetworkGraph->new();
	
	$bpg2->{'NodeList'} = \@{$bpg->{'NodeList'}};
	$bpg2->{'EdgeList'} = \@{$bpg->{'EdgeList'}};
	$bpg2->{'NodeType'} = \%{$bpg->{'NodeType'}};
	if(defined $bpg->{'NodeClass'})
	 { $bpg2->{'NodeClass'} = \%{$bpg->{'NodeClass'}}; }
	$bpg2->{'Merged'} = $bpg->{'Merged'};
	$bpg2->{'Collapsed'} = $bpg->{'Collapsed'};
	$bpg2->{'Filtered'} = $bpg->{'Filtered'};
	return $bpg2;

}
1;

