package Viz;

use strict;
use warnings;
no warnings 'redefine';

# Perl Modules
use Class::Struct;

# BNG Modules
use Visualization::StructureGraph;

# basic structures and methods
struct GMLNode => 
{
	'ID' => '$',
	'type' => '$', 
	'label' => '$', 
	'fill' => '$', 
	'outlineStyle' => '$', 
	'fontStyle' => '$',
	'anchor'=>'$',
	'object'=>'$',
	'isGroup'=>'$',
	'gid'=>'$',
	'hasOutline' => '$',
	'outline'=>'$',
	'embed'=>'$'
};

struct GMLEdge =>
{
	'source' => '$',
	'target' => '$',
	'fill' => '$',
	'sourceArrow' => '$',
	'targetArrow' => '$',
	'object' => '$'
};

struct GMLGraph =>
{
	'Nodes'=>'@',
	'Edges'=>'@',
};

# new versions of things
struct NodeGML =>
{
	'ID' => '$',
	'label' => '$',
	'gid' => '$',
	'isGroup' => '$',

	'graphics' => '$',
	'LabelGraphics' => '$',
	
	'object' =>'$',
	'embed' => '$'
};

struct NodeGraphicsGML =>
{
	'type' => '$',
	'fill' => '$',
	'hasOutline' => '$',
	'outlineWidth' => '$',
	'outlineStyle' => '$',
	'outline' => '$',
};

struct LabelGraphicsGML =>
{
	'text' => '$',
	'fontSize' => '$',
	'fontName' => '$',
	'fontStyle' => '$',
	'anchor' => '$',
};

struct EdgeGML =>
{
	'source'=> '$',
	'target'=> '$',
	'object'=> '$',
	'graphics' => '$'
};

struct EdgeGraphicsGML =>
{
	'width' => '$',
	'style' => '$',
	'fill' => '$',
	'sourceArrow' => '$',
	'targetArrow' => '$'
};

sub newnode
{
	my ($id,$label,$object) = @_;
	my $gmlnode = NodeGML->new();
	$gmlnode->{'ID'} = $id;
	$gmlnode->{'label'} = $label;
	$gmlnode->{'object'} = $object;
	$gmlnode->{'isGroup'} = 0;
	$gmlnode->{'gid'} = "";
	my $graphics = NodeGraphicsGML->new();
	$graphics->{'type'} = "ellipse";
	$graphics->{'fill'} = "#CCCCFF";
	$gmlnode->{'graphics'} = $graphics;
	$graphics->{'hasOutline'} = "0";
	$graphics->{'outlineWidth'} = "1";
	$graphics->{'outlineStyle'} = "line";
	$graphics->{'outline'} = "#CCCCFF";
	my $labelgraphics = LabelGraphicsGML->new();
	$labelgraphics->{'text'} = $label;
	$labelgraphics->{'fontSize'} = "12";
	$labelgraphics->{'fontName'} = "Dialog";
	$labelgraphics->{'fontStyle'} = "plain";
	$labelgraphics->{'anchor'} = "c";
	$gmlnode->{'LabelGraphics'} = $labelgraphics;
	return $gmlnode;
};

sub printnode
{
	my $node = shift @_;
	my $q0 = " ";
	my $q1 = " \"";
	my $q2 = "\" ";
	my $q3 = " [ ";
	my $q4 = " ] ";
	my @nodestrs;
	my @strs = ();
	push @strs,"id".$q0.$node->{'ID'}.$q0;
	push @strs,"label".$q1.$node->{'label'}.$q2;
	push @strs,"isGroup".$q0.$node->{'isGroup'}.$q0;
	push @strs,($node->{'gid'} ne "") ? "gid".$q0.$node->{'gid'}.$q0 : "gid".$q1.$q2;
	push @nodestrs, join(" ",@strs);
	@strs = ();
	my $graphics = $node->{'graphics'};
	push @strs,"type".$q1.$graphics->{'type'}.$q2;
	push @strs,"fill".$q1.$graphics->{'fill'}.$q2;
	push @strs,"hasOutline".$q0.$graphics->{'hasOutline'}.$q0;
	push @strs,"outlineWidth".$q0.$graphics->{'outlineWidth'}.$q0;
	push @strs,"outlineStyle".$q1.$graphics->{'outlineStyle'}.$q2;
	push @strs,"outline".$q1.$graphics->{'outline'}.$q2;
	push @nodestrs, "graphics".$q3.join(" ",@strs).$q4;
	@strs = ();
	my $labelgraphics = $node->{'LabelGraphics'};
	push @strs,"text".$q1.$labelgraphics->{'text'}.$q2;
	push @strs,"fontSize".$q0.$labelgraphics->{'fontSize'}.$q0;
	push @strs,"fontName".$q1.$labelgraphics->{'fontName'}.$q2;
	push @strs,"fontStyle".$q1.$labelgraphics->{'fontStyle'}.$q2;
	push @strs,"anchor".$q1.$labelgraphics->{'anchor'}.$q2;
	push @nodestrs, "LabelGraphics".$q3.join(" ",@strs).$q4;
	my $str = "node [\n".join(" ",@nodestrs)."\n ]";
	return $str;
}

sub newedge
{
	my ($source,$target,$forwardarrow,$reversearrow,$object) = @_;
	my $gmledge = EdgeGML->new();
	$gmledge->{'source'} = $source;
	$gmledge->{'target'} = $target;
	$gmledge->{'object'} = $object;
	my $graphics = EdgeGraphicsGML->new();
	$graphics->{'width'} = "1";
	$graphics->{'style'} = "line";
	$graphics->{'fill'} = "#000000";
	$graphics->{'sourceArrow'} = $reversearrow ? "standard" : "none";
	$graphics->{'targetArrow'} = $forwardarrow ? "standard" : "none";
	$gmledge->{'graphics'} = $graphics;
	return $gmledge;
};

sub printedge
{
	my $edge = shift @_;
	my $q0 = " ";
	my $q1 = " \"";
	my $q2 = "\" ";
	my $q3 = " [ ";
	my $q4 = " ] ";
	my @edgestrs;
	my @strs = ();
	push @strs,"source".$q0.$edge->{'source'}.$q0;
	push @strs,"target".$q0.$edge->{'target'}.$q0;
	push @edgestrs, @strs;
	@strs = ();
	my $graphics = $edge->{'graphics'};
	push @strs,"width".$q0.$graphics->{'width'}.$q0;
	push @strs,"style".$q1.$graphics->{'style'}.$q2;
	push @strs,"fill".$q1.$graphics->{'fill'}.$q2;
	push @strs,"sourceArrow".$q1.$graphics->{'sourceArrow'}.$q2;
	push @strs,"targetArrow".$q1.$graphics->{'targetArrow'}.$q2;
	push @edgestrs, "graphics".$q3.join(" ",@strs).$q4;
	my $str = "edge [\n".join(" ",@edgestrs)."\n ]";
	return $str;
}

sub style_node_rule_network
{
	my $node = shift @_;
	my $nodetype = shift @_;
	my $graphtype = shift @_;
	my $prop = load_rule_network_nodestyledefaults($nodetype,$graphtype);
	foreach my $key(keys %$prop)
		{
		$node->{'graphics'}->{$key} = $prop->{$key};
		}
	$prop = load_rule_network_labelstyledefaults($nodetype,$graphtype);
	foreach my $key(keys %$prop)
		{
		$node->{'LabelGraphics'}->{$key} = $prop->{$key};
		}
	return $node;
};
sub style_edge_rule_network
{
	my $edge = shift @_;
	my $edgetype = shift @_;
	my $graphtype = shift @_;
	my $prop = load_rule_network_edgestyledefaults($edgetype,$graphtype);
	
	foreach my $key(keys %$prop)
		{
		$edge->{'graphics'}->{$key} = $prop->{$key};
		}

	return $edge;
};

sub style_node_rule_operation
{
	my $node = shift @_;
	my $nodetype = shift @_;
	my $prop = load_rule_operation_nodestyledefaults($nodetype);
	foreach my $key(keys %$prop)
		{
		$node->{'graphics'}->{$key} = $prop->{$key};
		}
	$prop = load_rule_operation_labelstyledefaults($nodetype);
	foreach my $key(keys %$prop)
		{
		$node->{'LabelGraphics'}->{$key} = $prop->{$key};
		}
	return $node;
};

sub style_edge_rule_operation
{
	my $edge = shift @_;
	my $edgetype = shift @_;
	
	my $prop = load_rule_operation_edgestyledefaults($edgetype);
	
	foreach my $key(keys %$prop)
		{
		$edge->{'graphics'}->{$key} = $prop->{$key};
		}

	return $edge;
};

sub style_node_rule_pattern
{
	my $node = shift @_;
	my $nodetype = shift @_;
	my $prop = load_rule_pattern_nodestyledefaults($nodetype);
	foreach my $key(keys %$prop)
		{
		$node->{'graphics'}->{$key} = $prop->{$key};
		}
	$prop = load_rule_pattern_labelstyledefaults($nodetype);
	return $node if ($nodetype eq 'Pattern');
	foreach my $key(keys %$prop)
		{
		$node->{'LabelGraphics'}->{$key} = $prop->{$key};
		}
	return $node;
}
sub style_edge_rule_pattern
{
	my $edge = shift @_;
	my $edgetype = shift @_;
	
	my $prop = load_rule_pattern_edgestyledefaults($edgetype);
	
	foreach my $key(keys %$prop)
		{
		$edge->{'graphics'}->{$key} = $prop->{$key};
		}

	return $edge;
};

sub style_node_rinf
{
	my $node = shift @_;
	my $nodetype = shift @_;
	my $prop = load_rinf_nodestyledefaults($nodetype);
	foreach my $key(keys %$prop)
		{
		$node->{'graphics'}->{$key} = $prop->{$key};
		}
	$prop = load_rinf_labelstyledefaults($nodetype);
	foreach my $key(keys %$prop)
		{
		$node->{'LabelGraphics'}->{$key} = $prop->{$key};
		}
	return $node;
};
sub style_edge_rinf
{
	my $edge = shift @_;
	my $edgetype = shift @_;
	my $prop = load_rinf_edgestyledefaults($edgetype);
	
	foreach my $key(keys %$prop)
		{
		$edge->{'graphics'}->{$key} = $prop->{$key};
		}

	return $edge;
};


sub load_rule_pattern_nodestyledefaults
{
	my $nodetype = shift @_;
	my %convert = ('Mol'=>'Molecule',
	'Comp'=>'Component',
	'CompState'=>'InternalState',
	'Pattern'=>'Pattern',
	'Rule'=>'Rule' );
	
	my %normal = 
	(
		'Molecule' => {'hasOutline'=>"1",'type'=>"ellipse",'fill'=>"#EFEFEF",
					'outlineWidth'=>"1",'outlineStyle'=>"line",'outline'=>"#999999"},
		'Component' => {'hasOutline'=>"1",'type'=>"ellipse",'fill'=>"#EFEFEF",
					'outlineWidth'=>"1",'outlineStyle'=>"line",'outline'=>"#999999"},
		'InternalState' => {'hasOutline'=>"1",'type'=>"ellipse",'fill'=>"#FFF0A7",
					'outlineWidth'=>"1",'outlineStyle'=>"line",'outline'=>"#999999"},
		'Rule' => {'hasOutline'=>"1",'type'=>"ellipse",'fill'=>"#CC99FF",
					'outlineWidth'=>"1",'outlineStyle'=>"line",'outline'=>"#999999"},
		'Pattern' => {'hasOutline'=>"1",'type'=>"roundrectangle",'fill'=>"#FFFFFF",
					'outlineWidth'=>"1",'outlineStyle'=>"line",'outline'=>"#999999"},
	);
	return $normal{$convert{$nodetype}};
}

sub load_rule_pattern_labelstyledefaults
{
	my $nodetype = shift @_;
	my %convert = ('Mol'=>'Molecule',
	'Comp'=>'Component',
	'CompState'=>'InternalState',
	'Pattern'=>'Pattern',
	'Rule'=>'Rule' );
	my %normal = 
	(
		'Molecule' => {'fontSize'=>"14",'fontName'=>"Dialog",'fontStyle'=>"bold",'anchor'=>"t"},
		'Component' => {'fontSize'=>"14",'fontName'=>"Dialog",'fontStyle'=>"plain",'anchor'=>"t"},
		'InternalState' => {'fontSize'=>"14",'fontName'=>"Dialog",'fontStyle'=>"plain",'anchor'=>"c"},
		'Rule' => {'fontSize'=>"14",'fontName'=>"Dialog",'fontStyle'=>"plain",'anchor'=>"c"},
		'Pattern' => {'fontSize'=>"14",'fontName'=>"Dialog",'fontStyle'=>"bold",'anchor'=>"t"},
	);

	return $normal{$convert{$nodetype}};

}

sub load_rule_pattern_edgestyledefaults
{
	my $edgetype = shift @_;
	my %normal = 
	(
		'Bond' => {'width'=>"1",'style'=>"line",'fill'=>"#999999",'sourceArrow'=>"none",'targetArrow'=>"none"},
		'Reactant' => {'width'=>"1",'style'=>"line",'fill'=>"#000000",'sourceArrow'=>"none",'targetArrow'=>"standard"},
		'Product' => {'width'=>"1",'style'=>"line",'fill'=>"#000000",'sourceArrow'=>"none",'targetArrow'=>"standard"},
	);
	return $normal{$edgetype};
}


sub load_rule_operation_nodestyledefaults
{
	my $nodetype = shift @_;
	my %convert = ('Mol'=>'Molecule',
	'Comp'=>'Component',
	'CompState'=>'InternalState',
	'GraphOp'=>'GraphOperation',
	'Rule'=>'Rule' );
	
	my %normal = 
	(
		'Molecule' => {'hasOutline'=>"1",'type'=>"ellipse",'fill'=>"#EFEFEF",
					'outlineWidth'=>"1",'outlineStyle'=>"line",'outline'=>"#999999"},
		'Component' => {'hasOutline'=>"1",'type'=>"ellipse",'fill'=>"#EFEFEF",
					'outlineWidth'=>"1",'outlineStyle'=>"line",'outline'=>"#999999"},
		'InternalState' => {'hasOutline'=>"1",'type'=>"ellipse",'fill'=>"#FFF0A7",
					'outlineWidth'=>"1",'outlineStyle'=>"line",'outline'=>"#999999"},
		'GraphOperation' => {'hasOutline'=>"1",'type'=>"ellipse",'fill'=>"#CC99FF",
					'outlineWidth'=>"1",'outlineStyle'=>"line",'outline'=>"#999999"},
		'Rule' => {'hasOutline'=>"1",'type'=>"roundrectangle",'fill'=>"#FFFFFF",
					'outlineWidth'=>"1",'outlineStyle'=>"line",'outline'=>"#999999"},
	);
	return $normal{$convert{$nodetype}};
}

sub load_rule_operation_labelstyledefaults
{
	my $nodetype = shift @_;
	my %convert = ('Mol'=>'Molecule',
	'Comp'=>'Component',
	'CompState'=>'InternalState',
	'GraphOp'=>'GraphOperation',
	'Rule'=>'Rule' );
	my %normal = 
	(
		'Molecule' => {'fontSize'=>"14",'fontName'=>"Dialog",'fontStyle'=>"bold",'anchor'=>"t"},
		'Component' => {'fontSize'=>"14",'fontName'=>"Dialog",'fontStyle'=>"plain",'anchor'=>"t"},
		'InternalState' => {'fontSize'=>"14",'fontName'=>"Dialog",'fontStyle'=>"plain",'anchor'=>"c"},
		'GraphOperation' => {'fontSize'=>"14",'fontName'=>"Dialog",'fontStyle'=>"plain",'anchor'=>"c"},
		'Rule' => {'fontSize'=>"14",'fontName'=>"Dialog",'fontStyle'=>"plain",'anchor'=>"t"},
	);

	return $normal{$convert{$nodetype}};

}

sub load_rule_operation_edgestyledefaults
{
	my $edgetype = shift @_;
	my %normal = 
	(
		'Bond' => {'width'=>"1",'style'=>"line",'fill'=>"#999999",'sourceArrow'=>"none",'targetArrow'=>"none"},
		'Reactant' => {'width'=>"1",'style'=>"line",'fill'=>"#000000",'sourceArrow'=>"none",'targetArrow'=>"standard"},
		'Product' => {'width'=>"1",'style'=>"line",'fill'=>"#000000",'sourceArrow'=>"none",'targetArrow'=>"standard"},
	);
	return $normal{$edgetype};
}

sub load_rule_network_nodestyledefaults
{
	my $nodetype = shift @_;
	my $graphtype = shift @_;
	my %normal = 
	(
		'Rule' => {'hasOutline'=>"1",'type'=>"ellipse",'fill'=>"#CC99FF",
					'outlineWidth'=>"1",'outlineStyle'=>"line",'outline'=>"#999999"},
		'RuleGroup' => {'hasOutline'=>"1",'type'=>"ellipse",'fill'=>"#E5CBFF",
					'outlineWidth'=>"1",'outlineStyle'=>"line",'outline'=>"#999999"},
		'AtomicPattern' => {'hasOutline'=>"1",'type'=>"roundrectangle",'fill'=>"#FFE9C7",
					'outlineWidth'=>"1",'outlineStyle'=>"line",'outline'=>"#999999"},
		'PatternGroup' => {'hasOutline'=>"1",'type'=>"roundrectangle",'fill'=>"#FFF4E3",
					'outlineWidth'=>"1",'outlineStyle'=>"line",'outline'=>"#999999"},
	);
	return $normal{$nodetype};
};
sub load_rule_network_labelstyledefaults
{
	my $nodetype = shift @_;
	my $graphtype = shift @_;
	
	my %normal = 
	(
		'Rule' => {'fontSize'=>"14",'fontName'=>"Dialog",'fontStyle'=>"plain",'anchor'=>"c"},
		'RuleGroup' => {'fontSize'=>"14",'fontName'=>"Dialog",'fontStyle'=>"bold",'anchor'=>"c"},
		'AtomicPattern' => {'fontSize'=>"14",'fontName'=>"Dialog",'fontStyle'=>"plain",'anchor'=>"c"},
		'PatternGroup' => {'fontSize'=>"14",'fontName'=>"Dialog",'fontStyle'=>"bold",'anchor'=>"c"},
	);

	return $normal{$nodetype};
};
sub load_rule_network_edgestyledefaults
{
	my $edgetype = shift @_;
	my $graphtype = shift @_;
	my %normal = 
	(
		'Reactant' => {'width'=>"1",'style'=>"line",'fill'=>"#000000",'sourceArrow'=>"none",'targetArrow'=>"standard"},
		'Product' => {'width'=>"1",'style'=>"line",'fill'=>"#000000",'sourceArrow'=>"none",'targetArrow'=>"standard"},
		'Context' => {'width'=>"1",'style'=>"line",'fill'=>"#AAAAAA",'sourceArrow'=>"none",'targetArrow'=>"standard"},
		'Inhibition' => {'width'=>"1",'style'=>"line",'fill'=>"#FF0000",'sourceArrow'=>"none",'targetArrow'=>"crows_foot_one"},
	);
	return $normal{$edgetype};
};


sub load_rinf_nodestyledefaults
{
	my $nodetype = shift @_;
	my %normal = 
	(
		'Rule' => {'hasOutline'=>"1",'type'=>"ellipse",'fill'=>"#CC99FF",
					'outlineWidth'=>"1",'outlineStyle'=>"line",'outline'=>"#999999"},
	);
	return $normal{$nodetype};
};
sub load_rinf_labelstyledefaults
{
	my $nodetype = shift @_;
	my %normal = 
	(
		'Rule' => {'fontSize'=>"14",'fontName'=>"Dialog",'fontStyle'=>"plain",'anchor'=>"c"},
	);

	return $normal{$nodetype};
};
sub load_rinf_edgestyledefaults
{
	my $edgetype = shift @_;
	my %normal = 
	(
		'Activation' => {'width'=>"1",'style'=>"line",'fill'=>"#66FF66",'sourceArrow'=>"none",'targetArrow'=>"standard"},
		'Inhibition' => {'width'=>"1",'style'=>"line",'fill'=>"#FF9999",'sourceArrow'=>"none",'targetArrow'=>"standard"},
		'Modulation' => {'width'=>"1",'style'=>"line",'fill'=>"#999999",'sourceArrow'=>"none",'targetArrow'=>"standard"},
	);
	return $normal{$edgetype};
};



sub copyhash
{
	my %hash = @_;
	my %newhash = %hash;
	return \%newhash;
};

# old versions
sub initializeGMLNode
{
	my $id = shift @_;
	my $label = shift @_;
	my $object = shift @_;
	
	my $gmlnode = GMLNode->new();
	$gmlnode->{'ID'} = $id;
	$gmlnode->{'label'} = $label;
	$gmlnode->{'object'} = $object;
	
	$gmlnode->{'fill'} = "#FFFFFF";
	$gmlnode->{'outlineStyle'} = "";
	$gmlnode->{'fontStyle'} = "";
	$gmlnode->{'anchor'} = "c";
	$gmlnode->{'isGroup'} = 0;
	$gmlnode->{'gid'} = "";
	
	return $gmlnode;
}

sub initializeGMLEdge
{
	my $source = shift @_;
	my $target = shift @_;

	#my $object = @_ ? shift @_ : "";
	my $targetArrow = @_ ? shift @_ : 0;
	my $sourceArrow = @_ ? shift @_ : 0;
	my $object = @_ ? shift @_ : 0;
	
	my $gmledge = GMLEdge->new();
	$gmledge->{'source'} = $source;
	$gmledge->{'target'} = $target;
	if ($targetArrow) { $gmledge->{'targetArrow'} = 1;}
	if ($sourceArrow) { $gmledge->{'sourceArrow'} = 1;}
	if ($object) { $gmledge->{'object'} = $object; }
	
	return $gmledge;
}

sub printGML
{
	my $gmlgraph = shift @_;
	my @nodes = @{$gmlgraph->{'Nodes'}};
	my @edges = @{$gmlgraph->{'Edges'}};
	
	my @nodestrings = ();
	my @edgestrings = ();
	
	my $q1 = " \"";
	my $q2 = "\" ";
	foreach my $node(@nodes)
	{
		my $string1= "";
		# graphics
		$string1 .= "type".$q1.$node->{'type'}.$q2;
		$string1 .= "fill".$q1.$node->{'fill'}.$q2;
		if ($node->{'hasOutline'}==0) { $string1 .= "hasOutline 0 ";}
		if (defined $node->{'outline'}) 
			{ $string1 .= "outline".$q1.$node->{'outline'}.$q2;}
		if ($node->{'outlineStyle'})
			{$string1 .= "outlineStyle".$q1.$node->{'outlineStyle'}.$q2;}
		#if ($node->{'closed'})
		#	{$string1 .= "closed 1";}
		$string1 = "graphics [ ".$string1." ]";
		
		# labelgraphics
		my $string2 = "";
		$string2 .= "text".$q1.$node->{'label'}.$q2;
		#$string2 .= "fill".$q1."#FFFFFF".$q2;
		$string2 .= "anchor".$q1.$node->{'anchor'}.$q2;
		if ($node->{'fontStyle'})
			{$string2 .= "fontStyle".$q1.$node->{'fontStyle'}.$q2;}
		$string2 = "LabelGraphics [ ".$string2." ]";
		
		my $string = "";
		$string .= "id ".$node->{'ID'}." ";
		$string .= "label".$q1.$node->{'label'}.$q2;
		if ($node->{'isGroup'})
			{$string .= "isGroup ".$node->{'isGroup'}." ";}
		if (defined $node->{'gid'} and length($node->{'gid'}) > 0)
			{$string .= "gid ".$node->{'gid'}." ";}
		
		my $string3 = '';
		if(defined $node->{'embed'})
		{
			my $somegraph = $node->{'embed'};
			my $type = ref $somegraph;
			if($type eq 'StructureGraph') 
			{
				if($somegraph->{'Type'} eq 'Rule')
					{$string3 = toGML_rule_operation($somegraph);}
				else
					{$string3 = toGML_pattern($somegraph);}
			}
			elsif($type eq 'NetworkGraph') 
			{ $string3 = toGML_rule_network($somegraph);}
		}
		
		my $str = join(" ",($string,$string1,$string2,$string3));
		my $str2= "node [\n".$str."\n ]";
		#$string = "node [".$string." ".$string1." ".$string2." ".$string2" ]";
		
		push @nodestrings, $str2;
	}
	foreach my $edge(@edges)
	{
		my $string = "";
		my $string2 = "";
		$string2 .= "fill".$q1.$edge->{'fill'}.$q2;
		my @temp = (0,1);
		if ($edge->{'sourceArrow'} and $edge->{'targetArrow'})
			{
			$string2 .= "arrow \"both\" ";
			}
		elsif($edge->{'sourceArrow'})
			{
			$string2 .= "arrow \"first\" ";
			}
		elsif($edge->{'targetArrow'})
			{
			$string2 .= "arrow \"last\" ";
			}
		if ($edge->{'sourceArrow'})
			{
				if(!has(\@temp,$edge->{'sourceArrow'}))
					{
					$string2 .= "sourceArrow".$q1.$edge->{'sourceArrow'}.$q2;
					}
			}
		if ($edge->{'targetArrow'})
			{
				if(!has(\@temp,$edge->{'targetArrow'}))
					{
					$string2 .= "targetArrow".$q1.$edge->{'targetArrow'}.$q2;
					}
			}
		if (defined $edge->{'width'}) 
		{
			$string2 .= "width ".$edge->{'width'}." ";
		}
		
		#my $string3 = "";
		if(defined $edge->{'arrowhead'})
		{
			if ($edge->{'arrowhead'} eq 'inhibition' and $edge->{'targetArrow'})
			{
			$string2 .= "targetArrow	\"crows_foot_one\"";
			}
			if ($edge->{'arrowhead'} eq 'inhibition' and $edge->{'sourceArrow'})
			{
			$string2 .= "sourceArrow	\"crows_foot_one\"";
			}
		}
		
		$string2 = "graphics [ ".$string2." ]";
		$string .= "source ".$edge->{'source'}." ";
		$string .= "target ".$edge->{'target'}." ";
		
		$string = "edge [ ".$string." ".$string2." ]";
		
		push @edgestrings,$string;
	}

	my $string = "graph\n[\n directed 1\n";
	$string .= join("\n",@nodestrings)."\n";
	$string .= join("\n",@edgestrings)."\n";
	$string .= "]\n";	
	return $string;
}

# toGML methods
sub toGML_rule_operation
{
	my $sg = shift @_; #imports a combined rule structure graph

	#this is a structure graph.
	# could be pattern or a rule or combination of rules
	my $type = $sg->{'Type'};
	my @nodelist = @{$sg->{'NodeList'}};
	
	# remap all the ids to integers
	my @idlist = map {$_->{'ID'}} @nodelist;
	my %indhash = indexHash(\@idlist);
	foreach my $node(@nodelist) { remapNode($node,\%indhash); }

	my @structnodes = grep ( { $_->{'Type'} ne 'BondState' and $_->{'Type'} ne 'GraphOp'} @nodelist);
	my @gmlnodes = ();
	
	# old gml nodes
	foreach my $node(@nodelist)
	{
		my $id = $node->{'ID'};
		my $name = $node->{'Name'};
		my $type = $node->{'Type'};
		
		# ignore if it is a bond with two parents
		if ( $type eq 'BondState' and scalar @{$node->{'Parents'}} == 2) 
		{ next; }
		
		my $gmlnode = initializeGMLNode($id,$name,$node);
		my $isstruct = 1;
		# treat rules
		if ($type eq 'Rule') { $gmlnode->{'isGroup'} = 1; $isstruct = 0;}
		# treat graph ops
		if ($type eq 'GraphOp')
		{
			if ($name eq 'ChangeState')
			{
				my @parents = @{$node->{'Parents'}};
				my $parent = findNode(\@nodelist,$parents[0]);
				my @grandparents = @{$parent->{'Parents'}};
				my $grandparent = $grandparents[0];
				$gmlnode->{'gid'} = $grandparents[0];
			}
			else
			{ $gmlnode->{'gid'} = $node->{'Rule'}; }
			$isstruct = 0;
		}
		# treat wildcard bonds
		if ($type eq 'BondState') 
		{
			$gmlnode->{'gid'} = $node->{'Rule'}; 
			$isstruct = 0; 
		}
		
		# remaining nodes are structural and nonbonds
		if ($isstruct)
		{
			if ($node->{'Parents'})
			{
				foreach my $parent_id(@{$node->{'Parents'}}) 
				{ 
					$gmlnode->{'gid'} = $parent_id;
				}
			}
			else 
			{ 
				$gmlnode->{'gid'} = $node->{'Rule'}; 
			}
			if (hasChildren(\@structnodes,$node))
			{
				$gmlnode->{'isGroup'} = 1;
			}
		}
		push @gmlnodes, $gmlnode;
	}
	
	my @bondnodes = grep ( { $_->{'Type'} eq 'BondState' } @nodelist);
	my @gmledges = ();
	# make edges for bonds
	foreach my $node (@bondnodes)
	{
		my @parents = @{$node->{'Parents'}};
		my $source;
		my $target;
		# address wildcards
		if (scalar @parents == 1) 
		{
			$source = $node->{'ID'};
			$target = $parents[0];
		}
		# ignore bonds that are made or removed
		elsif ($node->{'Side'} eq 'both')
		{ 			
			$source = $parents[0];
			$target = $parents[1]; 
		}
		else {next;}
		my $gmledge = initializeGMLEdge($source,$target);
		push @gmledges,$gmledge;
	}
	
	# make edges for edges adjacent to graph operation nodes
	my @graphopnodes = grep ( { $_->{'Type'} eq 'GraphOp'} @nodelist);
	foreach my $node (@graphopnodes)
	{
		my $name = $node->{'Name'};
		my $id = $node->{'ID'};
		my @parents = @{$node->{'Parents'}};
		my @c;
		my @p;
		my @consumed;
		my @produced;
		
		if ($name eq 'ChangeState')
			{
			my @compstates = grep ( { $_->{'Type'} eq 'CompState' } @nodelist);
			my @nodes = findNodes(\@compstates,\@parents);
			@c = grep ( { $_->{'Side'} eq 'left' } @nodes);
			@p = grep ( { $_->{'Side'} eq 'right' } @nodes);
			}
			
		if ($name eq 'AddMol' or $name eq 'DeleteMol')
			{
			my @mols = grep ( { $_->{'Type'} eq 'Mol' } @nodelist);
			my @nodes = findNodes(\@mols,\@parents);
			@c = grep ( { $_->{'Side'} eq 'left' } @nodes);
			@p = grep ( { $_->{'Side'} eq 'right' } @nodes);
			}
			
		if ($name eq 'AddBond' or $name eq 'DeleteBond')
			{
				my @allbonds = grep ( { $_->{'Type'} eq 'BondState' } @nodelist);
				my $bond = findNode(\@allbonds,$parents[0]);
				my @comps = grep ( { $_->{'Type'} eq 'Comp' } @nodelist);
				my @nodes = findNodes(\@comps, $bond->{'Parents'});
				if ($name eq 'DeleteBond') { @c = @nodes; }
				if ($name eq 'AddBond') { @p = @nodes; }
			}
		
		if (@c) { @consumed = map ($_->{'ID'},@c); }
		if (@p) { @produced = map ($_->{'ID'},@p); }	
		
		foreach my $id2(@consumed)
		{
			my $gmledge = initializeGMLEdge($id2,$id,1);
			push @gmledges,$gmledge;
		}
		
		foreach my $id2(@produced)
		{
			my $gmledge = initializeGMLEdge($id,$id2,1);
			push @gmledges,$gmledge;
		}
	}
	
	# styling
	my %stylemap = ('Rule'=>1,'Pattern'=>1, 'Mol'=>2, 'Comp'=>3,
	'BondState'=>3, 'CompState'=>4, 'GraphOp'=>5 );
	
	foreach my $node(@gmlnodes)
	{
	my $object = $node->{'object'};
	my $type = $object->{'Type'};
	styleNode($node,$stylemap{$type});
	}
	
	foreach my $edge(@gmledges)
	{
	styleEdge($edge);
	}
	
	# new gml nodes v2
	my @gmlnodes2 = ();
	foreach my $node(@nodelist)
	{
		my $id = $node->{'ID'};
		my $name = $node->{'Name'};
		my $type = $node->{'Type'};
		
		# ignore if it is a bond with two parents
		if ( $type eq 'BondState' and scalar @{$node->{'Parents'}} == 2) 
		{ next; }
		
		my $gmlnode = newnode($id,$name,$node);
		my $isstruct = 1;
		# treat rules
		if ($type eq 'Rule') { $gmlnode->{'isGroup'} = 1; $isstruct = 0;}
		# treat graph ops
		if ($type eq 'GraphOp')
		{
			if ($name eq 'ChangeState')
			{
				my @parents = @{$node->{'Parents'}};
				my $parent = findNode(\@nodelist,$parents[0]);
				my @grandparents = @{$parent->{'Parents'}};
				my $grandparent = $grandparents[0];
				$gmlnode->{'gid'} = $grandparents[0];
			}
			else
			{ $gmlnode->{'gid'} = $node->{'Rule'}; }
			$isstruct = 0;
		}
		# treat wildcard bonds
		if ($type eq 'BondState') 
		{
			$gmlnode->{'gid'} = $node->{'Rule'}; 
			$isstruct = 0; 
		}
		
		# remaining nodes are structural and nonbonds
		if ($isstruct)
		{
			if ($node->{'Parents'})
			{
				foreach my $parent_id(@{$node->{'Parents'}}) 
				{ 
					$gmlnode->{'gid'} = $parent_id;
				}
			}
			else 
			{ 
				$gmlnode->{'gid'} = $node->{'Rule'}; 
			}
			if (hasChildren(\@structnodes,$node))
			{
				$gmlnode->{'isGroup'} = 1;
			}
		}
		my $stylenodetype = ($type eq 'BondState') ? 'Comp' : $type;
		style_node_rule_operation($gmlnode,$stylenodetype);
		push @gmlnodes2, $gmlnode;
	}
	# @gmlnodes = @gmlnodes2;
	# new gml edges
	my @gmledges2 = ();
	foreach my $node (@bondnodes)
	{
		my @parents = @{$node->{'Parents'}};
		my $source;
		my $target;
		# address wildcards
		if (scalar @parents == 1) 
		{
			$source = $node->{'ID'};
			$target = $parents[0];
		}
		# ignore bonds that are made or removed
		elsif ($node->{'Side'} eq 'both')
		{ 			
			$source = $parents[0];
			$target = $parents[1]; 
		}
		else {next;}
		my $gmledge = newedge($source,$target,0,0,'');
		style_edge_rule_operation($gmledge,'Bond');
		push @gmledges2,$gmledge;
	}
	
	foreach my $node (@graphopnodes)
	{
		my $name = $node->{'Name'};
		my $id = $node->{'ID'};
		my @parents = @{$node->{'Parents'}};
		my @c;
		my @p;
		my @consumed;
		my @produced;
		
		if ($name eq 'ChangeState')
			{
			my @compstates = grep ( { $_->{'Type'} eq 'CompState' } @nodelist);
			my @nodes = findNodes(\@compstates,\@parents);
			@c = grep ( { $_->{'Side'} eq 'left' } @nodes);
			@p = grep ( { $_->{'Side'} eq 'right' } @nodes);
			}
			
		if ($name eq 'AddMol' or $name eq 'DeleteMol')
			{
			my @mols = grep ( { $_->{'Type'} eq 'Mol' } @nodelist);
			my @nodes = findNodes(\@mols,\@parents);
			@c = grep ( { $_->{'Side'} eq 'left' } @nodes);
			@p = grep ( { $_->{'Side'} eq 'right' } @nodes);
			}
			
		if ($name eq 'AddBond' or $name eq 'DeleteBond')
			{
				my @allbonds = grep ( { $_->{'Type'} eq 'BondState' } @nodelist);
				my $bond = findNode(\@allbonds,$parents[0]);
				my @comps = grep ( { $_->{'Type'} eq 'Comp' } @nodelist);
				my @nodes = findNodes(\@comps, $bond->{'Parents'});
				if ($name eq 'DeleteBond') { @c = @nodes; }
				if ($name eq 'AddBond') { @p = @nodes; }
			}
		
		if (@c) { @consumed = map ($_->{'ID'},@c); }
		if (@p) { @produced = map ($_->{'ID'},@p); }	
		
		foreach my $id2(@consumed)
		{
			my $gmledge = newedge($id2,$id,1,0,'');
			style_edge_rule_operation($gmledge,'Reactant');
			push @gmledges2,$gmledge;
		}
		
		foreach my $id2(@produced)
		{
			my $gmledge = newedge($id,$id2,1,0,'');
			style_edge_rule_operation($gmledge,'Product');
			push @gmledges2,$gmledge;
		}
	}
	@gmlnodes = @gmlnodes2;
	@gmledges = @gmledges2;
	
	my $gmlgraph = GMLGraph->new();
	$gmlgraph->{'Nodes'} = \@gmlnodes;
	$gmlgraph->{'Edges'} =\@gmledges;
	return printGML2($gmlgraph);
}

sub toGML_pattern
{
	my $sg = shift @_; #imports a rule pattern graph
	my @nodelist = @{$sg->{'NodeList'}};
	#remap all ids to integers
	my @idlist = map{$_->{'ID'}} @nodelist;
	my %indhash = indexHash(\@idlist);
	foreach my $node(@nodelist) { remapNode($node,\%indhash); }
	my @structnodes = grep ( 
	{ $_->{'Type'} ne 'BondState' 
	and $_->{'Type'} ne 'Rule'
	and $_->{'Type'} ne 'Pattern'} @nodelist);
	#print scalar @nodelist; print "\n";
	#print scalar @structnodes; print "\n";
	
	my @gmlnodes = ();
	foreach my $node(@nodelist)
	{
		my $id = $node->{'ID'};
		my $name = $node->{'Name'};
		my $type = $node->{'Type'};
				
		# ignore if it is a bond with two parents
		if ( $type eq 'BondState' and scalar @{$node->{'Parents'}} == 2) 
		{ next; }
		
		my $gmlnode = initializeGMLNode($id,$name,$node);
		my $isstruct = 1;
		
		# treat wildcard bonds
		if ($type eq 'BondState') 
		{
			my @comps = @{$node->{'Parents'}};
			my $comp = findNode(\@nodelist,$comps[0]);
			my @mols = @{$comp->{'Parents'}};
			my $mol = findNode(\@nodelist,$mols[0]);
			if(defined $mol->{'Parents'}) 
				{
				my @sp = @{$mol->{'Parents'}};
				$gmlnode->{'gid'} = $sp[0];
				}
			$isstruct = 0; 
		}
		if ($isstruct)
		{
			if ($node->{'Parents'})
			{
				foreach my $parent_id(@{$node->{'Parents'}}) 
				{ 
					$gmlnode->{'gid'} = $parent_id;
				}
			}
			if (hasChildren(\@structnodes,$node))
			{
				$gmlnode->{'isGroup'} = 1;
			}
		}
		push @gmlnodes, $gmlnode;
	}
	
	my @gmledges = ();
	# draw the bonds
	my @bondnodes = grep ( { $_->{'Type'} eq 'BondState' } @nodelist);
	foreach my $node(@bondnodes)
	{
		my @parents = @{$node->{'Parents'}};
		my $source;
		my $target;
		if (scalar @parents == 1)
		{
			$source = $node->{'ID'};
			$target = $parents[0];
		}
		else
		{
			$source = $parents[0];
			$target = $parents[1];		
		}
		my $gmledge = initializeGMLEdge($source,$target);
		push @gmledges,$gmledge;
		
	}
	# draw edges to rules
	
	
	my %stylemap = ('Rule'=>5,'Pattern'=>1, 'Mol'=>2, 'Comp'=>3,
	'BondState'=>3, 'CompState'=>4 );
	foreach my $node(@gmlnodes)
	{
	my $object = $node->{'object'};
	my $type = $object->{'Type'};
	styleNode($node,$stylemap{$type});
	}
	
	foreach my $edge(@gmledges)
	{
	styleEdge($edge);
	}
	
	my $gmlgraph = GMLGraph->new();
	$gmlgraph->{'Nodes'} = \@gmlnodes;
	$gmlgraph->{'Edges'} =\@gmledges;
	return printGML($gmlgraph);

}
sub toGML_rule_pattern
{
	my $sg = shift @_; #imports a rule pattern graph
	my @nodelist = @{$sg->{'NodeList'}};
	#remap all ids to integers
	my @idlist = map{$_->{'ID'}} @nodelist;
	my %indhash = indexHash(\@idlist);
	foreach my $node(@nodelist) { remapNode($node,\%indhash); }
	my @structnodes = grep ( 
	{ $_->{'Type'} ne 'BondState' 
	and $_->{'Type'} ne 'Rule'
	and $_->{'Type'} ne 'Pattern'} @nodelist);
	#print scalar @nodelist; print "\n";
	#print scalar @structnodes; print "\n";
	
	# old gml nodes and edges
	my @gmlnodes = ();
	foreach my $node(@nodelist)
	{
		my $id = $node->{'ID'};
		my $name = $node->{'Name'};
		my $type = $node->{'Type'};
				
		# ignore if it is a bond with two parents
		if ( $type eq 'BondState' and scalar @{$node->{'Parents'}} == 2) 
		{ next; }
		
		my $gmlnode = initializeGMLNode($id,$name,$node);
		my $isstruct = 1;
		
		# treat patterns
		if ($type eq 'Pattern') 
		{ 
			$gmlnode->{'isGroup'} = 1; 
			$gmlnode->{'label'} = '';
			#$gmlnode->{'gid'} = $node->{'Rule'};
			$isstruct = 0;
		}
		
		# treat wildcard bonds
		if ($type eq 'BondState') 
		{
			my @comps = @{$node->{'Parents'}};
			my $comp = findNode(\@nodelist,$comps[0]);
			my @mols = @{$comp->{'Parents'}};
			my $mol = findNode(\@nodelist,$mols[0]);
			my @sp = @{$mol->{'Parents'}};
			$gmlnode->{'gid'} = $sp[0]; 
			$isstruct = 0; 
		}
		if ($isstruct)
		{
			if ($node->{'Parents'})
			{
				foreach my $parent_id(@{$node->{'Parents'}}) 
				{ 
					$gmlnode->{'gid'} = $parent_id;
				}
			}
			if (hasChildren(\@structnodes,$node))
			{
				$gmlnode->{'isGroup'} = 1;
			}
		}
		push @gmlnodes, $gmlnode;
	}
	my @gmledges = ();
	# draw the bonds
	my @bondnodes = grep ( { $_->{'Type'} eq 'BondState' } @nodelist);
	foreach my $node(@bondnodes)
	{
		my @parents = @{$node->{'Parents'}};
		my $source;
		my $target;
		if (scalar @parents == 1)
		{
			$source = $node->{'ID'};
			$target = $parents[0];
		}
		else
		{
			$source = $parents[0];
			$target = $parents[1];		
		}
		my $gmledge = initializeGMLEdge($source,$target);
		push @gmledges,$gmledge;
		
	}
	# draw edges to rules
	
	my @rulenodes = grep ( { $_->{'Type'} eq 'Rule' } @nodelist);
	my @patterns = grep ( { $_->{'Type'} eq 'Pattern' } @nodelist);
	foreach my $rule(@rulenodes)
	{
	#find the participating species
	my $id = $rule->{'ID'};
	my @participating = grep ( { $_->{'Rule'} eq $id } @patterns);
	my @reac = grep ( { index($_->{'Name'},'R')==0 } @participating);
	my @prod = grep ( { index($_->{'Name'},'P')==0} @participating);
	my $targetarrow = 1;
	my $sourcearrow = $rule->{'Reversible'} ? 1 : 0;
	foreach my $patt(@reac)
		{
		my $source = $patt->{'ID'};
		my $target = $id;
		my $gmledge = initializeGMLEdge($source,$target,$targetarrow,$sourcearrow);
		push @gmledges,$gmledge;
		}
	foreach my $patt(@prod)
		{
		my $source = $id;
		my $target = $patt->{'ID'};
		my $gmledge = initializeGMLEdge($source,$target,$targetarrow,$sourcearrow);
		push @gmledges,$gmledge;
		}
	}
	
	my %stylemap = ('Rule'=>5,'Pattern'=>1, 'Mol'=>2, 'Comp'=>3,
	'BondState'=>3, 'CompState'=>4 );
	foreach my $node(@gmlnodes)
	{
	my $object = $node->{'object'};
	my $type = $object->{'Type'};
	styleNode($node,$stylemap{$type});
	}
	
	foreach my $edge(@gmledges)
	{
	styleEdge($edge);
	}
	
	# new gml rules and edges
	my @gmlnodes2 = ();
	foreach my $node(@nodelist)
	{
		my $id = $node->{'ID'};
		my $name = $node->{'Name'};
		my $type = $node->{'Type'};
				
		# ignore if it is a bond with two parents
		if ( $type eq 'BondState' and scalar @{$node->{'Parents'}} == 2) 
		{ next; }
		$name = '' if ($type eq 'Pattern');
		my $gmlnode = newnode($id,$name,$node);
		my $isstruct = 1;
		
		# treat patterns
		if ($type eq 'Pattern') 
		{ 
			$gmlnode->{'isGroup'} = 1; 
			#$gmlnode->{'label'} = '';
			#$gmlnode->{'graphics'}->{'text'} = '';
			#$gmlnode->{'gid'} = $node->{'Rule'};
			$isstruct = 0;
		}
		
		# treat wildcard bonds
		if ($type eq 'BondState') 
		{
			my @comps = @{$node->{'Parents'}};
			my $comp = findNode(\@nodelist,$comps[0]);
			my @mols = @{$comp->{'Parents'}};
			my $mol = findNode(\@nodelist,$mols[0]);
			my @sp = @{$mol->{'Parents'}};
			$gmlnode->{'gid'} = $sp[0]; 
			$isstruct = 0; 
		}
		if ($isstruct)
		{
			if ($node->{'Parents'})
			{
				foreach my $parent_id(@{$node->{'Parents'}}) 
				{ 
					$gmlnode->{'gid'} = $parent_id;
				}
			}
			if (hasChildren(\@structnodes,$node))
			{
				$gmlnode->{'isGroup'} = 1;
			}
		}
		my $nodestyletype = ($type eq 'BondState') ? 'Comp' : $type;
		style_node_rule_pattern($gmlnode,$nodestyletype);
		push @gmlnodes2, $gmlnode;
	}
	my @gmledges2 = ();
	# draw the bonds
	@bondnodes = grep ( { $_->{'Type'} eq 'BondState' } @nodelist);
	foreach my $node(@bondnodes)
	{
		my @parents = @{$node->{'Parents'}};
		my $source;
		my $target;
		if (scalar @parents == 1)
		{
			$source = $node->{'ID'};
			$target = $parents[0];
		}
		else
		{
			$source = $parents[0];
			$target = $parents[1];		
		}
		my $gmledge = newedge($source,$target,0,0,'');
		style_edge_rule_pattern($gmledge,'Bond');
		push @gmledges2,$gmledge;
		
	}
	# draw edges to rules
	
	@rulenodes = grep ( { $_->{'Type'} eq 'Rule' } @nodelist);
	@patterns = grep ( { $_->{'Type'} eq 'Pattern' } @nodelist);
	foreach my $rule(@rulenodes)
	{
	#find the participating species
	my $id = $rule->{'ID'};
	my @participating = grep ( { $_->{'Rule'} eq $id } @patterns);
	my @reac = grep ( { index($_->{'Name'},'R')==0 } @participating);
	my @prod = grep ( { index($_->{'Name'},'P')==0} @participating);
	my $targetarrow = 1;
	my $sourcearrow = $rule->{'Reversible'} ? 1 : 0;
	foreach my $patt(@reac)
		{
		my $source = $patt->{'ID'};
		my $target = $id;
		my $gmledge = newedge($source,$target,1,0,'');
		style_edge_rule_pattern($gmledge,'Reactant');
		push @gmledges2,$gmledge;
		}
	foreach my $patt(@prod)
		{
		my $source = $id;
		my $target = $patt->{'ID'};
		my $gmledge = newedge($source,$target,1,0,'');
		style_edge_rule_pattern($gmledge,'Product');
		push @gmledges2,$gmledge;
		}
	}
	@gmlnodes = @gmlnodes2;
	@gmledges = @gmledges2;
	
	my $gmlgraph = GMLGraph->new();
	$gmlgraph->{'Nodes'} = \@gmlnodes;
	$gmlgraph->{'Edges'} =\@gmledges;
	return printGML2($gmlgraph);
	
}


sub toGML_rule_network
{
	my $bpg = shift @_;
	my $collapsed = $bpg->{'Collapsed'};
	my $grouped = defined $bpg->{'NodeClass'} ? 1 : 0;
	my $embed = @_ ? shift @_ : 0;
	my $ruleNames = @_ ? shift @_: 0;
	
	#my @groups = ();
	#my @groups = @_ ? @{shift @_} : ();
	#my $closed = @_ ? shift @_ : 0;
	
	my @nodelist = @{$bpg->{'NodeList'}};
	my @edgelist = @{$bpg->{'EdgeList'}};
	my %nodetype = %{$bpg->{'NodeType'}};
	
	my @gmlnodes = ();
	
	# processing node classes
	my %gidhash;
	my %isgroup;
	my @classnodes;
	my @classed;
	my %classes;
	if (defined $bpg->{'NodeClass'})
	{
		%classes = %{$bpg->{'NodeClass'}};
		@classnodes = uniq(values %classes);
		push @nodelist, @classnodes;
		@classed = uniq(keys %classes);
		foreach my $item(@classed)
			{
			my $type = $nodetype{$item};
			my $grp = $classes{$item};
			$nodetype{$grp} = ($type eq 'Rule') ? 'RuleGroup' : 'PatternGroup';
			}
	}
	my %nodetypeorder = ( 'AtomicPattern' => 0, 'Rule' => 1, 'PatternGroup' => 2, 'RuleGroup' => 3);
	my @nodelist2 = sort { $nodetypeorder{$nodetype{$a}} <=> $nodetypeorder{$nodetype{$b}} || $a cmp $b } @nodelist; 
	#print join "\n",@nodelist2;exit;
	my %indhash = indexHash( [@nodelist2] );
	
	# OLD gmlnode & gmledge
	foreach my $node(@nodelist2)
	{
		my $id = $indhash{$node};
		my $name = (has(['Rule','RuleGroup'],$nodetype{$node}) and not $ruleNames) ? "": prettify($node);
		#my $name = prettify($node);
		my $gmlnode = initializeGMLNode($id,$name,$node);
		$gmlnode->{'gid'} = $indhash{$classes{$node}} if (has(\@classed,$node)==1);
		$gmlnode->{'isGroup'} = 1 if (has(\@classnodes,$node)==1);
		styleNode2($gmlnode);
		styleNode2($gmlnode,$nodetype{$node},'color');
		if((defined $collapsed and $collapsed == 1) or has(['Rule','AtomicPattern'],$nodetype{$node}) )
			{
			styleNode2($gmlnode,$nodetype{$node},'shape');
			}
		if($embed and ($nodetype{$node} eq 'AtomicPattern') and $node =~ /\(/)
			{
			$gmlnode->{'anchor'} = "t";
			my $psg = stringToPatternStructureGraph($node,0);
			$gmlnode->{'embed'} = $psg;
			}
		push @gmlnodes, $gmlnode;
	}
	my @gmledges = ();
	foreach my $edge( @edgelist )
	{
		my @splits = split(":",$edge);
		my $source = $indhash{$splits[0]};
		my $target = $indhash{$splits[1]};
		#print $source,",",$target,"\n";
		my $type = $splits[2];
		my $gmledge = initializeGMLEdge($source,$target,"","",$edge);
		styleEdge2($gmledge,$type);
		push @gmledges,$gmledge;
	}
	
	# GMLv2 new nodegml and edgegml 5/22/2016 
	my @gmlnodes2 = ();
	foreach my $node(@nodelist2)
	{
		my $id = $indhash{$node};
		my $name = (has(['Rule','RuleGroup'],$nodetype{$node}) and not $ruleNames) ? "": prettify($node);
		
		my $gmlnode = newnode($id,$name,$node);
		$gmlnode->{'gid'} = $indhash{$classes{$node}} if (has(\@classed,$node)==1);
		$gmlnode->{'isGroup'} = 1 if (has(\@classnodes,$node)==1);
		
		my $graphtype = $collapsed ? 'Collapsed' : $grouped ? 'Grouped' : 'Normal';
		style_node_rule_network($gmlnode,$nodetype{$node},$graphtype);
		push @gmlnodes2,$gmlnode;
	}
	@gmlnodes = @gmlnodes2;
	my @gmledges2 = ();
	foreach my $edge( @edgelist )
	{
		my ($r,$p,$type) = split(":",$edge);
		my $source;
		my $target;
		if( ($type eq "Reactant") or ($type eq "Context") )
			{
			$source = $indhash{$p}; $target = $indhash{$r};
			}
		else
			{
			$source = $indhash{$r}; $target = $indhash{$p};
			}
		my $gmledge = newedge($source,$target,1,0,$edge);
		
		my $graphtype = $collapsed ? 'Collapsed' : $grouped ? 'Grouped' : 'Normal';
		style_edge_rule_network($gmledge,$type,$graphtype);
		push @gmledges2,$gmledge;
	}
	@gmledges = @gmledges2;
	

	
	my $gmlgraph = GMLGraph->new();
	$gmlgraph->{'Nodes'} = \@gmlnodes;
	$gmlgraph->{'Edges'} =\@gmledges;
	#return printGML($gmlgraph);
	return printGML2($gmlgraph);
	
}

sub toGML_rinf
{
	my $rinf = shift @_;
	my @nodelist = @{$rinf->{'Nodes'}};
	my @edgelist = @{$rinf->{'Edges'}};
	my @gmlnodes2 = ();
	my %indhash = indexHash( [@nodelist] );
	foreach my $node(@nodelist)
	{
		my $id = $indhash{$node};
		my $name = $node;
		my $gmlnode = newnode($id,$name,$node);
		style_node_rinf($gmlnode,'Rule');
		push @gmlnodes2,$gmlnode;
	}
	my @gmlnodes = @gmlnodes2;
	my @gmledges2 = ();
	foreach my $edge( @edgelist )
	{
		my ($from,$to,$type) = split(":",$edge);
		my $source = $indhash{$from};
		my $target = $indhash{$to};
		my $gmledge = newedge($source,$target,1,0,$edge);
		style_edge_rinf($gmledge,$type);
		push @gmledges2,$gmledge;
	}
	my @gmledges = @gmledges2;
	

	
	my $gmlgraph = GMLGraph->new();
	$gmlgraph->{'Nodes'} = \@gmlnodes;
	$gmlgraph->{'Edges'} =\@gmledges;
	#return printGML($gmlgraph);
	return printGML2($gmlgraph);

	return '';
}

sub printGML2
{
	my $gmlgraph = shift @_;
	my @nodes = @{$gmlgraph->{'Nodes'}};
	my @edges = @{$gmlgraph->{'Edges'}};
	my @nodestrings = map { printnode($_) } @nodes;
	my @edgestrings = map { printedge($_) } @edges;
	
	my $string = "graph\n[\n directed 1\n";
	$string .= join("\n",@nodestrings)."\n";
	$string .= join("\n",@edgestrings)."\n";
	$string .= "]\n";	
	return $string;
}

sub toGML_process
{
	my $pg = shift @_;
	my $embed = (defined $pg->{'Embed'});
	
	my %indhash = indexHash( $pg->{'Processes'} );
	
	my @gmlnodes;
	foreach my $r(@{$pg->{'Processes'}})
	{
		my $id = $indhash{$r};
		my $name = $pg->{'Names'}->{$r};
		my $gmlnode = initializeGMLNode($id,$name,$r);
		styleNode2($gmlnode);
		if($embed)
			{
			$gmlnode->{'anchor'} = "t";
			$gmlnode->{'embed'} = $pg->{'Embed'}->{$r};
			}
		push @gmlnodes,$gmlnode;
	}
	
	my @gmledges;
	foreach my $edge(@{$pg->{'Edges'}})
	{
		my ($r1,$r2) = split(" ",$edge);
		my $source = $indhash{$r1};
		my $target = $indhash{$r2};
		my $gmledge = initializeGMLEdge($source,$target,"","",$edge);
		styleEdge2($gmledge,'Process');
		push @gmledges,$gmledge;
		
	}
	
	my $gmlgraph = GMLGraph->new();
	$gmlgraph->{'Nodes'} = \@gmlnodes;
	$gmlgraph->{'Edges'} =\@gmledges;
	return printGML($gmlgraph);
}

sub toGML_process2
{
	my $pg = shift @_;
	my $embed = (defined $pg->{'Embed'});
	
	#my %indhash = indexHash( $pg->{'Processes'} );
	
	my @processes = @{$pg->{'Nodes'}};
	my @names = @{$pg->{'Names'}};
	my @gmlnodes;
	foreach my $i(0..@processes-1)
	{
		#my $id = $i;
		#my $name = $names[$i];
		my $name = @names ? $names[$i]: '';
		my $gmlnode = initializeGMLNode($i,$name,$processes[$i]);
		styleNode2($gmlnode);
		if($embed)
			{
			$gmlnode->{'anchor'} = "t";
			$gmlnode->{'embed'} = $pg->{'Embed'}->[$i];
			}
		push @gmlnodes,$gmlnode;
	}
	
	my @gmledges;
	foreach my $edge(@{$pg->{'Edges'}})
	{
		my ($i,$j) = split(" ",$edge);
		my $source = $i;
		my $target = $j;
		my $gmledge = initializeGMLEdge($source,$target,"","",$edge);
		styleEdge2($gmledge,'Process');
		push @gmledges,$gmledge;
		
	}
	
	my $gmlgraph = GMLGraph->new();
	$gmlgraph->{'Nodes'} = \@gmlnodes;
	$gmlgraph->{'Edges'} =\@gmledges;
	return printGML($gmlgraph);
}

sub styleNode
{
	my $gmlnode = shift @_;
	my $arg = @_ ? shift @_ : 0;
	$gmlnode->{'hasOutline'} = 1;
	# defaults
	$gmlnode->{'type'} = "roundrectangle";
	$gmlnode->{'fill'} = "#FFFFFF";
	$gmlnode->{'outline'} = "#000000";
	if ($gmlnode->{'isGroup'}) {$gmlnode->{'anchor'} = "t";}
	else {$gmlnode->{'anchor'} = "c";}
	
	# node, Rule, yED
	if ($arg==1) { $gmlnode->{'outlineStyle'} = "dotted"; }
	# node, Mol, yED
	if ($arg==2) { $gmlnode->{'fontStyle'} = "bold";  $gmlnode->{'fill'} = "#D2D2D2"; }
	# node, Comp/BondState, yED
	if ($arg==3) { 
	#$gmlnode->{'fill'} = "#D2D2D2";
	$gmlnode->{'fill'} = "#FFFFFF";  }
	# node, CompState, yED
	if ($arg==4) { $gmlnode->{'fill'} = "#FFCC00"; }
	# node, GraphOp, yED
	if ($arg==5) 
	{ 
		$gmlnode->{'fill'} = "#CC99FF"; 
		$gmlnode->{'type'} = 'hexagon';
		$gmlnode->{'fontStyle'} = 'italic';
	}
	return;
}

sub styleNode2
{	
	my $gmlnode = shift @_;
	my $type = @_ ? shift @_ : undef;
	my $property = @_ ? shift @_ : undef;
	
	my @props = qw(shape fill);
	my @types =qw(Rule AtomicPattern RuleGroup PatternGroup);
	my %keywords = ( 'shape'=>'type', 'color'=>'fill');
	
	my %properties2 = 
		( 	
		'Rule' 			=> { 'shape' => 'hexagon', 			'color' => '#bbb8f4' },
		'AtomicPattern' => { 'shape' => 'roundrectangle', 	'color' => '#fda7a9' },
		'PatternGroup'	=> { 'shape' => 'roundrectangle', 	'color' => '#fee4e5' },
		'RuleGroup'		=> { 'shape' => 'hexagon', 			'color' => '#eae9fb' },
		);
		
	my %properties = 
		( 	
		'Rule' 			=> { 'shape' => 'hexagon', 			'color' => '#bbb8f4' },
		'AtomicPattern' => { 'shape' => 'roundrectangle', 	'color' => '#f4bbb8' },
		'PatternGroup'	=> { 'shape' => 'roundrectangle', 	'color' => '#fee4e5' },
		'RuleGroup'		=> { 'shape' => 'hexagon', 			'color' => '#eae9fb' },
		);
	
	if (defined $type and defined $property)
	{
	$gmlnode->{$keywords{$property}} = $properties{$type}->{$property};	
	return;
	}
	
	# defaults
	$gmlnode->{'hasOutline'} = 1;
	$gmlnode->{'type'} = "roundrectangle";
	$gmlnode->{'fill'} = "#FFFFFF";
	$gmlnode->{'outline'} = "#000000";
	if ($gmlnode->{'isGroup'}) {$gmlnode->{'anchor'} = "t";}
	else {$gmlnode->{'anchor'} = "c";}
	
	return;
}

sub styleEdge2
{
	my $gmledge = shift @_;
	my $type = @_ ? shift @_: undef;
	my %properties =
		(
		'Reactant' =>	{'color'=>'#5e3c58','source'=>1,'target'=>0,'width'=>1,'arrowhead'=>'standard'},
		'Product' => 	{'color'=>'#5e3c58','source'=>0,'target'=>1,'width'=>1,'arrowhead'=>'standard'},
		'Wildcard' => 	{'color'=>'#5e3c58','source'=>1,'target'=>0,'width'=>1,'arrowhead'=>'standard'},
		'Context' => 	{'color'=>'#798e87','source'=>1,'target'=>0,'width'=>1,'arrowhead'=>'standard'},
		'Process' => 	{'color'=>'#000000','source'=>0,'target'=>1,'width'=>1,'arrowhead'=>'standard'},
		'Inhibition' => {'color'=>'#5e3c58','source'=>0,'target'=>1,'width'=>1,'arrowhead'=>'inhibition'},
		);
	my %keywords = ('color'=>'fill','source'=>'sourceArrow','target'=>'targetArrow','width'=>'width','arrowhead'=>'arrowhead');
	
	if(defined $type)
	{
	foreach my $prop(keys %keywords)
		{
		$gmledge->{$keywords{$prop}} = $properties{$type}->{$prop};
		}
	}
	
	return;
	
}
	
sub styleEdge
{
	my $gmledge = shift @_;
	my $arg = @_ ? shift @_ : 0 ;
	# defaults
	$gmledge->{'fill'} = "#000000";
	return;
}



1;
