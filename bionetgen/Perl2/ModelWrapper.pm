package ModelWrapper;

use strict;
use warnings;

# BNG Modules
use Class::Struct;
use FindBin;
use lib $FindBin::Bin;
use File::Spec;

# Perl Modules
use BNGUtils;
use BNGModel;




# A class that wraps a model and implements user friendly
#  actions for console users
struct ModelWrapper =>
{
    Name      => '$',
	Model     => 'BNGModel',
    Defaults  => '%',
};


###
###
###


# load model spec from file
sub load
{
    my $modelwrapper = shift @_;
    my $file         = @_ ? shift @_ : undef;

    if ( defined $modelwrapper->Model )
    {
        printf "MODEL.load(FILE): model %s is already defined.\n", $modelwrapper->Name;
        return 0;
    }

    unless ( $file )
    {
        printf "MODEL.load(FILE): file argument is empty.\n";
        return 0;
    }

    unless ( -e $file )
    {
        print "MODEL.load(FILE): file %s cannot be found.\n", $file;
        return 0;
    }

    unless ( -r $file )
    {
        print "MODEL.load(FILE): file %s is not readable.\n", $file;
        return 0;
    }

    # create BNGModel object
    $modelwrapper->Model = BNGModel->new();
    # make local copy of params
    my $local_params = { %{$modelwrapper->Defaults} };
    # add filename to params
    $local_params->{file} = $file;

    # read and process Model file
    my $err = $modelwrapper->Model->readFile( $local_params );
    if ($err)
    {
        print "MODEL.load(FILE): some problem parsing model file $file.\n";    
        print "errmsg: $err\n";
        return 0;
    }

    return 1;
}

###
###
###


# clear model spec
sub clear
{
    my $modelwrapper = shift @_;

    %{$modelwrapper->Model} = ();
    undef %{$modelwrapper->Model};
    $modelwrapper->Model(undef);

    return 1;
}


###
###
###


# get/set model state
sub state
{
    my $modelwrapper = shift @_;
    
    my $model = $modelwrapper->Model;    
    unless ( defined $model )
    {
        printf "MODEL.load(FILE): model %s is not defined.\n", $modelwrapper->Name;
        return 0;
    }

    if (@_)
    {
        # set state
        my $state = shift @_;

        unless ( @$state == $model->SpeciesList->getNumSpecies() )
        {
            print "MODEL.state(STATE): length of STATE vector does not equal the number of species.\n";    
            return 0;
        }
        
        my $state_copy = [ @$state ];
        $model->Concentrations( $state_copy );
        return 1;
    }
    else
    {
        # get state
        my $state_copy = [];
        if ( @{$model->Concentrations} )
        {
            @$state_copy = @{$model->Concentrations};
        }
        else
        {
            @$state_copy = grep {$_->Concentration} @{$model->SpeciesList->Array};
        }
        return 1, $state_copy;
    }
}

