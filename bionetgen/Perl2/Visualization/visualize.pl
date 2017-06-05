#!/usr/bin/perl 
# pragmas
package Viz;
use File::Spec;
use warnings;						  
use FindBin;
use Getopt::Long;
use lib 	File::Spec->catdir(
				(	exists $ENV{'BNGPATH'} ? $ENV{'BNGPATH'} :
					( 	exists $ENV{'BioNetGenRoot'} ? $ENV{'BioNetGenRoot'} : 
						do 	{
							my ($vol,$dir,$file) = File::Spec->splitpath( $FindBin::RealBin );
							my @dirs = File::Spec->splitdir($dir);
							pop @dirs;
							pop @dirs;
							File::Spec->catpath($vol,File::Spec->catdir(@dirs))
						} 
					)
				),'Perl2'
			);

use BNGModel;
use Visualization::Viz;
use Visualization::VisOptParse;

if (scalar (@ARGV) == 0) { display_help(); exit; }

my %args = ('help'=>0,'background'=>0,'collapse'=>0,'each'=>0,'groups'=>0,'textonly'=>0,'suffix'=>'','filter'=>0,'level'=>1,'mergepairs'=>0,'embed'=>0) ;

GetOptions(	\%args,
			'help!',
			'bngl=s',
			'db=s',
			'type=s',
			'opts=s@',
			'background!',
			'collapse!',
			'each!',
			'groups!',
			'textonly!',
			'suffix=s',
			'filter!',
			'level=i',
			'mergepairs!',
			'embed!',
			'ruleNames!',
			'doNotUseContextWhenGrouping!',
			'removeReactantContext!',
			'makeInhibitionEdges!',
			'removeProcessNodes!',
			'compressRuleMotifs!',
			'doNotCollapseEdges!',
		) or die "Error in command line arguments.";
		
#if(defined $args{'db'})
#{
#	my $name = $args{'db'};
#	$name =~ /(.*)\.db/;
#	my $prefix = $1;
#	
#	filecheck($name,'DB');
#	my $dbh = newDBFile($name);
#	my $background = $args{'background'};
#	my $groups = $args{'groups'};
#	my $collapse = $args{'collapse'};
#	my $bpg = getBPG($dbh,$background,$groups,$collapse);
#	my $str = toGML_rule_network($bpg);
#	my $suffix = $args{'suffix'};
#	my $type = 'regulatory';
#	my %params = ('str'=>$str,'suffix'=>$suffix,'type'=>$type,'prefix'=>$prefix);
#	writeGML2(\%params);
#	exit;
#}

if($args{'help'}) 
{
	if(defined($args{'type'}) ) { display_help(\%args); }
	else { display_help(); }
	exit;
}

if(not defined $args{'type'}) {$args{'type'} = 'regulatory'; }

filecheck($args{'bngl'},'BNGL');
map { filecheck($_,'Opts') } @{$args{'opts'}} if defined $args{'opts'};

my $model = getModel($args{'bngl'});
my $exec_params = getExecParams(\%args);
execute_params($model,$exec_params);


sub filecheck
{
	my $file = shift @_;
	my $type = shift @_;
	if(not defined $file) { print $type." file not defined.\n"; exit;}
	if(not -e $file) { print $type." file ".$file." not found.\n"; exit;}
	print "Found ".$type." file ".$file.".\n";
	return;
}

sub getModel
{
	my $filename = shift @_;
	print "Importing model from ".$filename."\n";
	my %args = ();
	$args{'file'} = $filename;
	$args{'skip_actions'} = 1;
	$args{'action_skip_warn'} = 1;

	#print $filename;
	my $model = BNGModel->new();
	$model->initialize();
	$BNGModel::GLOBAL_MODEL = $model;
	
	#print "Opening file: ".$filename."\n";
	my $err = $model->readModel(\%args) ;
	
	if ($err) { print "ERROR:".$err."\n";}
	
	return $model;
}

sub display_help
{
	print qq{
---------------------------------------------/ HELP MENU /----------
SYNOPSIS:

  visualize.pl --help                	   show this help menu
  visualize.pl --bngl MODEL --type TYPE    make visualization of type TYPE for bngl model MODEL
  TYPE "regulatory" is used as default if --type is not used.
  
  -----------------------
  Allowed values for TYPE
  -----------------------
  conventional                         conventional rule visualization
  compact                              compact rule visualization (using graph operation nodes)
  regulatory                           rule-derived regulatory graph
  opts                                 options template for regulatory graph
  contactmap                           contact map
  reaction_network                     reaction network
  
OPTIONS:

  --------------
  Common Options
  --------------
  --suffix STR                         add suffix STR to output filename
 
  --------------------------------------------------------
  Options for TYPE "conventional", "compact", "regulatory"
  --------------------------------------------------------
  --each                               show all rules in separate GML files (default: 0)
  
  -----------------------------
  Options for TYPE "regulatory"
  -----------------------------
  --opts FILE                          import options file named FILE 
  --opts FILE1 --opts FILE2            import multiple options files
  --background                         enable background (default: disabled)
  --groups                             enable groups (default: disabled)
  --collapse                           enable collapsing of groups (default: disabled)
  --ruleNames                          enable display of rule names (default: disabled)
  --doNotUseContextWhenGrouping 	   use permissive edge signature (default: strict)
  --doNotCollapseEdges                 when collapsing nodes, retain duplicate edges (default: removed)
  
NOTES:
  (i)   To obtain a template options file for a model, use TYPE "opts". 
        You can edit this file and import it as --opts FILE.
  (ii)  To obtain full model regulatory graph, use --background and all other options set to default.
  (iii) To show rule names and rule group names on the regulatory graph, use --ruleNames.
  
  }
  
}



sub display_help_old
{
	
		print qq{
---------------------------------------------/ HELP MENU /----------

SYNOPSIS:

  visualize.pl --help                	show this help menu
  visualize.pl --type TYPE              make visualization of type TYPE
  
  Allowed types: {ruleviz_pattern, ruleviz_operation, regulatory, contactmap }
  Default type when --type is not used: regulatory

OPTIONS:

  File I/O
  --------
  --bngl FILE					   use BNGL model file (required)
  --opts FILE1 [--opts FILE2 [..]]     use input options files FILE1 FILE2 .. (optional)
  --suffix "STR"                       add suffix STR to output filename (optional)

USAGE:
  Visualizing individual rules
  ----------------------------
  visualize.pl --type ruleviz_pattern|ruleviz_operation [--each]
  
  ruleviz_pattern: rules are visualized as bipartite graphs with embedded pattern graphs
  ruleviz_operation: rules are visualized as pattern graphs with graph operation nodes
  each: enable output of separate gml file for each rule
  
  Visualizing contact map
  -----------------------
  visualize.pl --type contactmap
  
  Visualizing regulatory graphs of individual rules
  -------------------------------------------------
  visualize.pl --type regulatory [--each] [--background]
  
  background: turning background on shows all nodes (default is background off)
  each: enable output of a separate gml file for each rule
  
  Visualizing text-only regulatory graph of model
  -----------------------------------------------
  visualize.pl --type regulatory --background --textonly
  
  Visualizing regulatory graph of model
  -------------------------------------
  visualize.pl --type regulatory [--background] [--groups [--collapse]] [--filter --level INT] [--opts FILE1 [--opts FILE2 [..] ] ]
  
  background: turning background on shows all nodes (default is background off)
  groups: enable automated grouping of rules and user-assisted grouping of patterns
  collapse: replace groups of nodes by a single node representative node
  filter: filter regulatory graph starting from seed nodes and traversing edges upto an integer level deep
  opts: one or more options files 
  
  groups, background and filter options can be defined in options files which have the following structure:
   
	begin background
		begin include
			<atomic patterns>
		end include
		
		begin exclude
			<atomic patterns>
		end exclude
	end background
	
	begin filter
		<atomic patterns>
	end filter
	
	begin classes
		begin classname
			<atomic patterns>
		end classname
	end classes

	<atomic patterns> is a whitespace-separated list of patterns.
	'classname' refers to arbitrary names for pattern classes.
	All blocks are optional. 
	
	Include and exclude blocks determine which patterns are
	included and excluded in the list of background patterns.
	Filter block is for listing seed nodes for filtering regulatory graph.
  
};
	return "";	
}

