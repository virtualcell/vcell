package BNGUtils;
# TODO use strict;

# Perl Modules
use FindBin;
use lib $FindBin::Bin;
use File::Spec;
use Cwd;
use List::Util ("sum");

use constant VERSION_FILENAME => "VERSION";
use constant DEFAULT_VERSION  => "UNKNOWN";
use constant DEFAULT_CODENAME => "";


require Exporter;
our @ISA = qw( Exporter );
our @EXPORT = qw( BNGversion BNGcodename compareVersions isReal booleanToInt BNGroot BNGpath exit_error send_warning cpu_time average_runs create_sbml 
	              head split_obj split_comp verify_pattern process_val_string
		          validate_rate_law MIN MAX SQR log10 );

# Jim Faeder 3/13/2005.
{
    my $BNG_VERSION;
    my $BNG_CODENAME;
    my $BNG_ROOT;

    # find the BNG root directory
    sub BNGroot
    {
        unless ($BNG_ROOT)
        {   # Determine BNG root directory

            # Use environment variable BNGPATH, if defined and valid
            if ( exists $ENV{BNGPATH} ) 
            {
	            my $bin_dir = File::Spec->catdir( ($ENV{BNGPATH}) );
                if ( -d $bin_dir )
                {   $BNG_ROOT = $bin_dir;   }
                else
                {   send_warning( "While looking for BNG root: environment variable 'BNGPATH' does not point to a valid directory!" );   }
            }
            # Use environment variable BioNetGenRoot, if defined and valid
            elsif ( exists $ENV{BioNetGenRoot} )
            {
	            my $bin_dir = File::Spec->catdir( ($ENV{BioNetGenRoot}) );
                if ( -d $bin_dir )
                {   $BNG_ROOT = $bin_dir;   }
                else
                {   send_warning( "While looking for BNG root: environment variable 'BioNetGenRoot' does not point to a valid directory!" );   }
            }


            # Otherwise, try the same
            unless ( defined $BNG_ROOT )
            {
                if (-d $FindBin::RealBin)
                {   $BNG_ROOT = $FindBin::RealBin;   }
                else
                {   send_warning( "While looking for BNG root: could not get RealBin directory!" );   }
            }

            # As last resort, try current working directory
            unless ( defined $BNG_ROOT )
            {   
                $BNG_ROOT = Cwd::getcwd();
                send_warning( "While looking for BNG_ROOT: defaulting to current working directory!" );    
            }
        }
        return $BNG_ROOT;
    }
  
    sub BNGpath
    {
        unless ($BNG_ROOT)
        {   # find BNG root directory
            BNGroot();
        }
        return File::Spec->catdir( ($BNG_ROOT, @_) );
    }

    # extract BNG_VERSION and BNG_CODENAME from the version file located
    #  at BNG_ROOT/VERSION.
    sub readVersionFile
    {
        unless ($BNG_ROOT)
        {   # find BNG root directory
            BNGroot();
        }

        # Get BioNetGen version number
        unless (defined $BNG_VERSION)
        {
            # read version info from VERSION file
            if ( open my $fh, '<', File::Spec->catfile($BNG_ROOT, VERSION_FILENAME) )
            {
                # get first line of file
                my $version_string = <$fh>;
	            close $fh;
	            
                # extract version number
   				$version_string =~ s/^\s*(\d+\.\d+\.\d+)\s*//;
	            
                $BNG_VERSION = $1;
                unless ( defined $BNG_VERSION )
                {   $BNG_VERSION = DEFAULT_VERSION;   }

                # extract codename (if any)
                $version_string =~ s/^(\w+)//;
                $BNG_CODENAME = $1;
                unless ( defined $BNG_VERSION )
                {   $BNG_CODENAME = DEFAULT_CODENAME;   }
            } 
            else
            {
	            $BNG_VERSION  = DEFAULT_VERSION;
                $BNG_CODENAME = '';
            }
        }
        return;
    }

    # get BNG_VERSION
    sub BNGversion
    {
        unless (defined $BNG_VERSION)
        {   # find BNG root directory
            readVersionFile();
        }
        return $BNG_VERSION;
    }

    # get BNG_CODENAME
    sub BNGcodename
    {
        unless (defined $BNG_CODENAME)
        {   # find BNG root directory
            readVersionFile();
        }
        return $BNG_CODENAME;
    }

    # compare versions: returns -1 if version1  < version2,
    #                            1 if version1  > version2,
    #                            0 if version1 == version2
    sub compareVersions
    {
        my ($version1, $version2) = @_;
        
        my (@version1) = split (/\./, $version1);
        my (@version2) = split (/\./, $version2);

        # compare major, minor and release numbers
        while (@version1 and @version2)
        {
            my $comp = ( shift @version1 <=> shift @version2 );
            if ($comp) {  return $comp;  }
        }
        
        # if versions are equal so far, the version with finer defintion
        #  is greater than the other.
        # (e.g.  2.1.7 is greater than 2.1)
        return ( @version1 ? 1 : (@version2 ? -1 : 0) );
    }
}


# Determine if a string corresponds to a float or a double
sub isReal{
  my $string=shift;
  my $isdec=0;

  if ($string=~ s/^[+-]?\d+[.]?\d*//){
    $isdec=1;
  }
  elsif ($string=~ s/^[+-]?[.]\d+//){
    $isdec=1;
  } else {
    return(0);
  }
  if ($string eq ""){return(1);}

  if ($string=~ s/^[DEFGdefg][+-]?\d+$//){
    return(1);
  }
  return(0);
}

# Determine if a string is a Boolean
sub isBoolean{
  my $string=shift;
  my $isboo;

  if ($string=~/^true$/i){
    $isboo=1;
  } elsif ($string=~/^false$/i){
    $isboo=1;
  } else {
    $isboo=0;
  }
  return($isboo);
}

# Convert boolean to integer False=0 True=1 notBoolean=-1
sub booleanToInt{
  my $string=shift;
  my $intval;

  if ($string=~/^true$/i){
    $intval=1;
  } 
  elsif ($string=~/^false$/i){
    $intval=0;
  } 
  # Boolean that is not a assigned a value is True by default
  elsif ($string eq ""){
    $intval=1;
  }
  else {
    $intval=-1;
  }
  return($intval);
}
 
##---------------------------------------------------------------------------
# Check that a pattern referring to species objects is valid.

sub verify_pattern{
  my $pattern= shift;
  my $is_spec= shift;
  my $mol_ncomp= shift;
  
  $patt=$pattern;
  # remove valid termination characters
  $patt=~s/\.\*$//;
  $patt=~s/\*$//;
  
  # split pattern into molecules and check that each is valid
  my @molecules= split('\.',$patt);
  for my $mol (@molecules){
    my ($head, @components)= split_obj($mol);
    if ($head eq $mol){
      if ($#molecules>0){
	return(1, "Single state species $head may not appear in a complex.")
      } 
      elsif (!defined($$is_spec{$mol})){
	return(0, "Species not declared: $pattern");
      }
    } else {
      # Check that number of components matches species declaration 
      if (!defined($$mol_ncomp{$head})){
	return(1, "Molecule $head not defined.");
      } elsif ($$mol_ncomp{$head}!= $#components){
	return(1, "Number of components for molecule $head doesn't match species declaration.");
      }

      # Check that each component is valid
      for my $comp (@components){
	if ($comp=~/[^0-9\*]/){
	  return(1, "Invalid component state $comp: May only use integers or wildcard.",$entry);
	}
#	if ($comp=~/[^A-Za-z0-9_\-\*]/){
#	  return(1, "Invalid component state $comp in pattern $pattern: May only use alphanumeric characters, '-','_', or '*'.");
#	}
      }
    }
  }
  
  if ($#molecules<0){
    return(1, "No molecules specified in matching string\n");
  }    
  return(0,"");
}

##---------------------------------------------------------------------------
# Handle a value that is either a number, a parameter name, or a
# product of a number and a parameter name.

sub process_val_string{
    my $val_string= shift;
    my $entry= shift;
    my $parameters= shift;

    my ($fac,$val, $value);
    
    $value=$val_string;
    if ($value=~/(\S+)[*](\S+)/){
      $fac= $1;
      $val=$2;
    } else {
      $fac= 1;
      $val=$value;
    }

    # Make sure val is valid parameter if it is not a number.
    # NOTE: The checking here would be much easier with a perl routine
    # isnum(string) (which is part of Acme::Utils) that returns true if the
    # string is a number (in the C/Fortran sense).  I didn't use this routine
    # because it requires installation of additional perl modules, which might
    # be beyond the capabilities of our target user.
    if ($val=~/^[a-zA-Z]/){
      # Determine if parameters are allowed
      ($parameters) || exit_error("Parameter not allowed for value.",$entry);
      # Make sure parameter is valid
      defined($$parameters{$val}) || exit_error("Parameter $val is not defined.",$entry);
      # Make sure value is non-negative
      if ($$parameters{$val}{val}<0){exit_error("Parameter value must be non-negative.",$entry);}
      $value= $fac*$$parameters{$val}{val};
    } else {
      # Substitute numerical value 
      $value= $fac*$val;
      # Make sure value is non-negative
      if ($value<0){&exit_error("Parameter value must be non-negative",$entry);}
    }

#    print "fac:$fac val:$val value:$value\n";
    return($value);
}


# Send error message to STDERR and exit.
sub exit_error
{
    my @msgs = @_;
    print STDERR "ABORT: ";
    foreach my $msg (@msgs)
    {
	    print STDERR $msg, "\n";
    }
    exit(1);
}


# Send error message to STDERR, but do not exit.
sub send_error
{
    my @msgs = @_;
    print STDERR "ERROR: ";
    foreach my $msg (@msgs)
    {  print STDERR $msg, "\n";  }
    return 0;
}


# send warning to STDOUT
sub send_warning
{
    my @msgs = @_;
    print STDOUT "WARNING: ";
    foreach my $msg (@msgs)
    {  print STDOUT $msg,"\n";  }
    # Could have $STRICT flag to force exit
    return 0;
}


# send error message with line number
sub line_error
{
    my ($msg, $lno) = @_;
    print STDERR sprintf( "ABORT: [at line %s] %s\n", $lno, $msg );
    exit 1;
}

# send warning message with line number
sub line_warning
{
    my ($msg, $lno) = @_;
    print STDOUT sprintf( "WARNING: [at line %s] %s\n", $lno, $msg );
}


##------------------------------------------------------------------------
# Used to create list of all multi-state objects from a list of
# dimensions.  It returns an array containing all elements of the
# multidimensional array named $name.  
# e.g. expand_object("R", 2,2) returns the array
# (R(0,0), R(1,0), R(0,1), R(1,1)
# Earlier indices vary faster than later ones.

sub expand_object{
    my $name= shift;
    my @lengths=@{$t=shift};
    my @equiv=@{$t=shift};
    my @olist=("");

    for my $i (0..$#lengths){
	my @new_olist=();
	my $sep= ($i==$#lengths) ? "" : ",";
	# array of earlier indices that are equivalent to index $i
	my @eq=();
	if (my $class= $equiv[$i]){
	    for $j (0..$i-1){
		($equiv[$j]==$class) && push @eq,$j;
	    }
	}
	for $state (0..($lengths[$i]-1)){
	  OBJ:
	    for $obj (@olist){
		# Skip this object if the current state is greater
    	        # than the state of index equivalent to the current one.
		if (@eq){
		    my @states=split(',',$obj);
		    for $j (@eq){
			($state>$states[$j]) && next OBJ;
		    }
		}
		push @new_olist, $obj.$state.$sep;
	    }
	}
	@olist= @new_olist;
    }

    # Enclose indices in parentheses and prepend spcies name
    for $obj (@olist){
	$obj= $name."(".$obj.")";
    }

    return(@olist);
}

##---------------------------------------------------------------------------
# Create aggregates from lists of species in rows of matrix a.
# This subroutine uses a depth first algoritm, meaning here that the
# most distant index changes fastest.
# The second subroutine uses a breadth first algoritm, meaning here that the
# most nearest index changes fastest.
##---------------------------------------------------------------------------

sub make_aggregates{
    my @a= @{$t= shift};

    my $idepth=0;
    my $max_depth=$#a;
    my @olist=();
    my @agg=();
    my %agg_hash=();

    for $i (0..$#a){$iptrs[$i]=0;}
    push @olist, $a[0][0];
    
    while(1){
	#descend until idepth==max_depth
	while($idepth<$max_depth){
	    ++$idepth;
	    last if ($idepth==$max_depth);
	    # set ptr at new depth to start of array
	    $iptrs[$idepth]=0;
	    push @olist, $a[$idepth][0];
	}
	
	# loop over ptrs at lowest depth
	my @obj_low= @{$a[$max_depth]};
#	my $eq=$equiv[$idepth];
	for $obj (@obj_low){
	    push @olist, $obj;
	    my $new_agg= join(".", sort by_obj @olist);
	    my $new_agg_sort= join(".", sort by_obj @olist);
	    # Only add aggregate that is in sort order
	    #($new_agg eq $new_agg_sort) && push @agg, $new_agg;
	    # Another way is to add aggregate first time it is encountered
	    ($agg_hash{$new_agg}++==0) && push @agg, $new_agg;
	    pop @olist;
	}
	
	# go back up to depth where elements are not exhausted
	# stop when no more elements at any level
	while($idepth>=0){
	    --$idepth;
	    last if ($idepth<0);
	    pop @olist;
	    if (($iptr=++$iptrs[$idepth])<=$#{$a[$idepth]}){
		push @olist, $a[$idepth][$iptr];
		last;
	    }
	}
	last if ($idepth<0);
    }
#    print join("\n", @agg),"\n";
    return(@agg);
}

##------------------------------------------------------------------------
# Returns the total cputime (user+system) of the current process and
# all children since the last call to cpu_time.
# Optional argument can be used to reset the offset.
# Call cpu_time(0) to tare (i.e. "zero") the clock.
{
    my $store_time=0;
    sub cpu_time
    {
        my $prev_time = @_ ? shift @_ : $store_time;
        my $curr_time = sum times;
        $store_time = $curr_time;
        return $curr_time - $prev_time;
    }
}

# Compute average and standard deviation over a set of data in
# format 
#   time1 y1 ... yN
#   ...
#   timeM y1 ... yN
# 

# This routine returns data sets in xmgrace format.
# Sets are written separately, in format time val dval, where dval
# is the standard deviation of the average over all runs.

sub average_runs{
  my $outfile=shift;

  my $nfile=0;
  my @t=();
  my $ng;
  for my $file (@_){
    open(IN, $file);
    my $i_t=0;
    while(<IN>){
      next if (/^\#/);
      my($time, @dat)=split(' ');
      if ($nfile==1){
	push @t, $time;
	$ng= $#dat;
      }
      for my $j(0..$#dat){
	$y[$i_t][$j][$nfile]= $dat[$j];
      }
      ++$i_t;
    }
    close(IN);
    ++$nfile;
  }

  # Write results to outfile
  open(OUT,">$outfile");
    for $j (0..$ng){
      for my $i (0..$#t){
	print OUT $t[$i];
	my ($sig, $avg)= &stddev(@{$y[$i][$j]});
	print OUT " ", $avg;
	print OUT " ", $sig;
	print OUT "\n";
      }
      print OUT "&\n";
  }
  close(OUT);
}

sub average{
    return -1 unless $#_>=0;
    local($sum)=0.0;
    foreach $val (@_){
        $sum+=$val;
    }
    return $sum/($#_+1);
}

sub stddev{
    return -1 unless $#_>=0;
    local($sum)=0.0;
    local($average)= &average(@_);
    foreach $val (@_){
        $sum+=SQR($val-$average);
    }
    return (sqrt($sum/$#_), $average); # This is the correct formula for the 
                                       # variance---denom contains (n-1)
}

# Basic utilities

sub SQR{
    local($x)=shift;
    return ($x*$x);
}


sub log10 {
  my $x=shift;
  return log($x)/log(10);
}

sub MAX{
  my $a=shift;
  my $b=shift;

  return(($a>=$b)? $a : $b);
}

sub MIN{
  my $a=shift;
  my $b=shift;

  return(($a<=$b)? $a : $b);
}

sub bynum {$a<=>$b;}

1;
