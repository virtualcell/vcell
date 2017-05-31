package Viz;

use strict;
use warnings;
no warnings 'redefine';

use Class::Struct;
use Visualization::NetworkGraph;
use Visualization::StructureGraph;
use Visualization::Viz;


struct ContactMap =>
{
	'MType'	=> '%', 
	'Bonds' => '@',
	'ReacProds' => '%',
	'Names' => '%',
};




sub makeContactMap
{
	my @bpgs = @{shift @_};
	my @statepatternlist = @{shift @_};
	
	my @ap = uniq (map { 
					my $bpg = $_;
					grep { $bpg->{'NodeType'}->{$_} eq 'AtomicPattern' }
					@{$bpg->{'NodeList'}}; 
				} @bpgs);
	
	
	#my @ap = uniq ((@ap1,@ap2));
	
	my %cmap;
	my @bonds;
	
	# assigning mols
	my @mols = grep !/\(/, @ap;
	foreach my $mol(@mols) { $cmap{$mol} = {}; };

	
	# assigning comps
	my @comps =	grep !/~/,
				grep !/\!/, 
				grep /\(/,
				@ap;
	foreach my $str(@comps)
	{
		$str =~ /(.*)\((.*)\)/;
		my @arr = ();
		$cmap{$1}{$2} = \@arr;
	}
	foreach my $str(@statepatternlist)
	{
		$str =~ /(.*)\((.*)~(.*)\)/;
		my @arr = ();
		$cmap{$1}{$2} = \@arr;
	}
	
	
	# assigning comp states
	my @compstates = grep /~/,uniq(@ap,@statepatternlist);
	foreach my $str(@compstates)
	{
		$str =~ /(.*)\((.*)~(.*)\)/;
		#push $cmap{$1}{$2}, $3;
		push2ref($cmap{$1}{$2},$3);
	}

	# getting bonds
	my @bondstates = 	grep !/\!\+/,
						grep /\!/, @ap;
	foreach my $bond(@bondstates)
	{
		my @splits = split /\./, $bond;
		$splits[0] =~ /(.*)\((.*)\!/;
		my $mol1 = $1;
		my $comp1 = $2;
		$splits[1] =~ /(.*)\((.*)\!/;
		my $mol2 = $1; 
		my $comp2 = $2;
		push @bonds, $mol1." ".$comp1." ".$mol2." ".$comp2;
		
		if(!exists $cmap{$mol1}) {$cmap{$mol1} = {};}
		if(!exists $cmap{$mol2}) {$cmap{$mol1} = {};}
		if(!exists $cmap{$mol1}{$comp1}) { my @arr = (); $cmap{$mol1}{$comp1} = \@arr;}
		if(!exists $cmap{$mol2}{$comp2}) { my @arr = (); $cmap{$mol2}{$comp2} = \@arr;}
		
	}
	
	
	# building the contact map
	my @new_nodes = ();
	my $mol_ind = -1;
	foreach my $mol(sort keys %cmap)
	{
		$mol_ind++;
		push @new_nodes, makeNode('Mol',$mol,$mol_ind);
		my $comp_ind = -1;
		foreach my $comp (sort keys %{$cmap{$mol}})
		{
			$comp_ind++;
			my $c_id = $mol_ind.".".$comp_ind;
			my @parents = ($mol_ind);
			push @new_nodes, makeNode('Comp',$comp,$c_id,\@parents);
			my $state_ind = -1;
			foreach my $compstate(@{$cmap{$mol}{$comp}})
			{
				$state_ind++;
				my $id = $mol_ind.".".$comp_ind.".".$state_ind;
				my @parents = ($c_id);
				push @new_nodes, makeNode('CompState',$compstate,$id,\@parents);
			}
		}
	}
	
	foreach my $bond(@bonds)
	{
		my @splits = split " ",$bond;
		my $comp1 = findComp(\@new_nodes,$splits[0],$splits[1]);
		my $comp2 = findComp(\@new_nodes,$splits[2],$splits[3]);
		my @parents = sort ($comp1,$comp2);
		my $id = $parents[0].".1";
		push @new_nodes,makeNode('BondState',"+",$id,\@parents); 
	}
	
	my $psg = makeStructureGraph('ContactMap',\@new_nodes);
	#print printStructureGraph($psg);
	return $psg;
}

sub findComp
{
	my @nodelist = @{shift @_};
	my $molname = shift @_;
	my $compname = shift @_;
	my @nodes = grep $_->{'Name'} eq $molname, 
				grep $_->{'Type'} eq 'Mol',
				grep $_->{'Type'} eq 'Mol', 
				@nodelist;
	my $mol_id = $nodes[0]->{'ID'};
	@nodes = grep has($_->{'Parents'},$mol_id),
				grep $_->{'Name'} eq $compname,
				grep $_->{'Type'} eq 'Comp',
				@nodelist;
	
	return $nodes[0]->{'ID'};
}
	


1;