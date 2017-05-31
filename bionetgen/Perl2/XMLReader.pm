package XMLReader;

use strict;
use warnings;

use XML::TreePP;
use Data::Dumper;



sub new{
	my ($class) = @_;
	
	my $self = {
		_tree => XML::TreePP->new((force_hash=>['Model']))->parsefile($_[1])
	};
	bless $self, $class;
	return $self;
}

sub getParameters{
	my ($self) = @_;
	return $self->{_tree}->{sbml}->{Model}->{ListOfParameters}->{Parameter};
}

sub getParameter{
	my ($self, $idx) = @_;
	return $self->{_tree}->{sbml}->{Model}->{ListOfParameters}->{Parameter}->[$idx];
	
}

sub getNumParameters{
	my ($self) = @_;
	return $self->{_tree}->{sbml}->{Model}->{ListOfParameters}->{Parameter};
}

sub getMolecules{
	my ($self) = @_;
	return $self->{_tree}->{sbml}->{Model}->{ListOfMoleculeTypes}->{MoleculeType} ;
}

sub getSpecies{
	my ($self) = @_;
	return $self->{_tree}->{sbml}->{Model}->{ListOfSpecies}->{Species};
}

sub getCompartments{
	my ($self) = @_;
	return $self->{_tree}->{sbml}->{Model}->{ListOfCompartments}->{Compartment};
}

sub getReactionRules{
	my ($self) = @_;
	return $self->{_tree}->{sbml}->{Model}->{ListOfReactionRules}->{ReactionRule};
}

sub getObservables{
	my ($self) = @_;
	return $self->{_tree}->{sbml}->{Model}->{ListOfObservables}->{Observable};
}

sub getFunctions{
	my ($self) = @_;
	return $self->{_tree}->{sbml}->{Model}->{ListOfFunctions};
}

1;
