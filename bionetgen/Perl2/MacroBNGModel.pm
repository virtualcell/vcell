 # $Id: MacroBNGModel.pm,v 1.17 2006/07/27 06:43:37 faeder Exp $
package MacroBNGModel;
##use strict;

use Class::Struct;
use FindBin;
use lib $FindBin::Bin;
use MoleculeTypesList;
use ParamList;
use SpeciesList;
use RxnRule;
use Observable;
use BNGUtils;
use BNGModel;


##########################################################################################################################
my $NO_EXEC=0; # Prevents execution of functions to allow file syntax checking
my $HAVE_PS=0; # Set to 0 for MS Windows systems with no ps command - disables reporting of 
               # memory usage at each iteration of network generation

my $base_model;

# Structure containing all BioNetGen model data
struct MacroBNGModel=>{
};

{
  my ($file, $line_number, $file_dat);
  my %bngdata;

  sub readFile{
    our $model= shift;
    our $params= (@_) ? shift : "";

    # Internal control parameters
    my $fname="";
    my $prefix="";
    $base_model= BNGModel->new();

    # Process optional parameters
    if ($params){
      for my $param (keys %$params){
	if ($param eq "no_exec"){
	$NO_EXEC=$params->{no_exec};
	} 
	elsif ($param eq "file"){
	$fname=$params->{file};
        $name= $fname;                        
        $name=~ s/[.]([^.]+)$//;              
        $param_prefix = $name;                     
        &pre_macr($param_prefix);
        $params{file}="macr_".$fname;           
	}
	elsif ($param eq "prefix"){
	$prefix=$params->{prefix};
	}
	else {
	  send_warning("Parameter $param ignored");
	}
      }
    }

    if ($fname eq ""){
      return("Parameter file must be specified in readFile");
    }

print "\n#################-START BASEMODEL-#####################\n";
  if ($err=$base_model->readFile(\%params)){ 
    exit_error($err);                     
  }
print "\n#################-FINISH BASEMODEL-#####################\n";

    $file = "macr_".$param_prefix.".net"; # macr_fceri_ji3.net
    $params{file}=$file;                  # macr_fceri_ji3.net
#++++++++++++++++++++++++++++++++++++++++++
my $entry = 'TRASH 0';
my $slist=$base_model->SpeciesList;
$err = $slist->readString($entry,$base_model->ParamList,$base_model->MoleculeTypesList);
##$slist->print(*STDOUT, $mtlist);
#++++++++++++++++++++++++++++++++++++++++++
    &cor_net($param_prefix);              # correct *.net
    print WFILErec " END COR_NET \n";
    close(WFILErec);       # *.rec
    $filen = "macr_".$param_prefix.".bnglisx";  #macr_fceri_ji3.bnglisx
    $file= "macr_".$param_prefix.".bngl";  
    rename($file,$filen);                   

    open (WFILEbngl, ">$file") or die "Can't open $file: $!\n";
      print WFILEbngl $simul1;
    close (WFILEbngl);
    $params{file}=$file;   # macr_fceri_ji3.bngl
#    $generate_network=0;
    $base_model->UpdateNet(0);
    if ($err=$base_model->readFile(\%params)){ 
      exit_error($err);                  
    }
      return($err);
  }


#########################
#########################
#pre_macr-begin
sub pre_macr { 
my ($param_prefix) = @_;
 my ($recfile, $bnglfile, $parfile, $netfile);
 my ($spec2file, $rulesfile, $obserfile, $macrfile);
 my ($line, $entry, $lno, $name, $suff);
 my (@rab);
 my (%nm_site, %nm2_site, %skf, %dpp_site, %lis1h);
  my ($file, $line_number, $file_dat);
  my %bngdata;


 $bnglfile= "<${param_prefix}.bngl";     # $bnglfile= "<${param_prefix}.bngl";
 $recfile= ">macr_${param_prefix}.rec";  # $recfile= ">macr_${param_prefix}.rec";
 $parfile= ">macr_${param_prefix}.par";  # $parfile= ">macr_${param_prefix}.par";

 open (WFILErec, $recfile) or die "Can't open $recfile: $!\n";
 open (WFILEpar, $parfile) or die "Can't open $parfile: $!\n";

 print WFILErec $recfile,"\n";

READ: 
      print "Reading from file $bnglfile\n";
      if (!open(FH, $bnglfile)) {
	return("Couldn't read from file $bnglfile: $!");
      }
      $file_dat=[<FH>];
      close(FH);                                  
      my $plist= ParamList->new;
      $base_model->ParamList($plist);
      my $mtlist= MoleculeTypesList->new;
      $base_model->MoleculeTypesList($mtlist); 
      while($_= get_line()){
	if (s/^\s*begin\s*//){
	  $name= $_;
	  $name=~ s/\s*$//;
	  $name=~ s/\s+/ /g;
	  my $block_dat;
	  ($block_dat ,$err)= read_block_array($name);
	  if ($err){ last READ;}
	  $bngdata{$name}=1;
	  if ($name eq "parameters"){
            open (WFILEpar, $parfile) or die "Can't open $parfile: $!\n";
            print WFILEpar "begin parameters\n"; 
	    for my $line (@{$block_dat}){
	      my ($entry, $lno)= @{$line};
              print WFILEpar "$entry\n";
              my @tok1ens= split(' ',$entry);
              my ($ind1ex, $na1m, $va1l);
              if ($tok1ens[0]=~/^\d+$/){
                $ind1ex= shift(@tok1ens); # This index will be ignored
              }
              if (@tok1ens){
                $na1m= shift(@tok1ens);
              } else {
                return("No parameter name specified");
              }
              if (@tok1ens){
                $va1l= shift(@tok1ens);
              } else {
                return("No parameter value specified");
              }
              $para1ms{$na1m} = $va1l;  
	      if ($err=$plist->readString($entry)){
		$err=errgen($err,$lno);
		last READ;
	      }
	    }
	    printf "Read %d parameters.\n", scalar(@{$plist->Array});
            print WFILEpar "end parameters\n";  
            close(WFILEpar);  
	  } 
	  elsif ($name=~ /^molecule[_ ]types$/){
	    my $mtlist= $base_model->MoleculeTypesList;
	    for my $line (@{$block_dat}){
	      my ($entry, $lno)= @{$line};
	      if ($err=$mtlist->readString($entry,$mtlist)){
		$err=errgen($err,$lno);
		last READ;
	      }
	    }
	    printf "Read %d molecule types.\n", scalar(keys %{$mtlist->MolTypes});
	  }
	  elsif ($name=~ /^seed[_ ]species$/){
	    my $slist= SpeciesList->new;
	    for my $line (@{$block_dat}){
	      my ($entry, $lno)= @{$line};
	      if ($err=$slist->readString($entry,$base_model->ParamList,$base_model->MoleculeTypesList)){
		$err=errgen($err,$lno);
		last READ;
	      }
	    }
	    printf "Read %d species.\n", scalar(@{$slist->Array});
	    $base_model->SeedSpeciesList($slist);
	    $base_model->SpeciesList($slist);
	  }
	  elsif ($name eq "species"){
	    my $slist= SpeciesList->new;
	    for my $line (@{$block_dat}){
	      my ($entry, $lno)= @{$line};
              push(@species2,$entry);
	      if ($err=$slist->readString($entry,$base_model->ParamList,$base_model->MoleculeTypesList)){
		$err=errgen($err,$lno);
		last READ;
	      }
	    }
	    printf "Read %d species.\n", scalar(@{$slist->Array});
	    $base_model->SpeciesList($slist);
	  } 
	  elsif ($name=~ /^reaction[_ ]rules$/){
	    my $nerr=0;
	    my @rrules=();
	    for my $line (@{$block_dat}){
	      my ($entry, $lno)= @{$line};
              my $nam1e = '';
              my $str1ing = $entry;
              if ($str1ing=~ s/^\s*([^:].*)[:]\s*//){
                $nam1e= $1;
                print "ERROR 1002   rules  (user) name=$nam1e\n";
                exit;
              }
              elsif ($str1ing=~ s/^\s*(\d+)\s+//){
                $nam1e=$1;
              }
              $str1ing=~ s/^\s*//;
              push(@reac1tion,$str1ing); 
	      my $rrs;
	      ($rrs,$err)= RxnRule::newRxnRule($entry, $base_model->ParamList, $base_model->MoleculeTypesList);
	      if ($err ne ""){
		$err=errgen($err,$lno);
		printf STDERR "ERROR: $err\n";
		++$nerr;
	      } else {
		push @rrules, $rrs;
	      }
	    }
	    if ($nerr){
	      $err= "Reaction rule list could not be read because of errors";
	      last READ;
	    }
	    $base_model->RxnRules([@rrules]);
	    printf "Read %d reaction rule(s).\n", scalar(@{$base_model->RxnRules});
	  }
	  elsif ($name eq "reactions"){
	    my $rlist= RxnList->new;
	    for my $line (@{$block_dat}){
	      my ($entry, $lno)= @{$line};
	      if ($err=$rlist->readString($entry,$base_model->SpeciesList,$base_model->ParamList)){
		$err=errgen($err,$lno);
		last READ;
	      }
	    }
	    printf "Read %d reaction(s).\n", scalar(@{$block_dat});
	    $base_model->RxnList($rlist);
	  }
	  elsif ($name eq "groups"){
	    if (!$base_model->Observables){
	      $err=errgen("Observables must be defined before Groups",$lno); 
	      last READ;
	    }
	    my $iobs=0;
	    my $maxobs= $#{$base_model->Observables};
	    for my $line (@{$block_dat}){
	      my ($entry, $lno)= @{$line};
	      my @tokens= split(' ',$entry);
	      # Skip first entry if it's an index
	      if ($tokens[0]=~ /^\d+$/){ shift(@tokens)};
	      if ($iobs>$maxobs){ $err=errgen("More groups than observables",$lno); last READ;}
	      my $obs= $base_model->Observables->[$iobs];
	      if ($tokens[0] ne $obs->Name){
		$err=errgen("Group named $tokens[0] is not compatible with any observable",$lno);
		last READ;
	      }
	      shift(@tokens);
	      my @array= split(',',$tokens[0]);
	      *weights= $obs->Weights;
	      @weights= (0)x@weights;
	      my ($w, $ind);
	      for $elt (@array){
		if($elt=~ s/^([^*]*)\*//){
		  $w=$1;
		} else {
		  $w=1;
		}
		if ($elt=~ /\D+/){
		  $err=errgen("Non-integer group entry: $elt",$lno);
		  last READ;
		}
		$weights[$elt]+= $w;
	      }
	      ++$iobs;
	    }
	    printf "Read %d group(s).\n", scalar(@{$block_dat});
	  }
	  elsif ($name eq "observables"){
	    my @observables=();
	    for my $line (@{$block_dat}){
	      my ($entry, $lno)= @{$line};
              my $str1ing = $entry;
              if ($str1ing=~ s/^\s*\d+\s+//){
              }
              push(@obser1vable,$str1ing); 
	      my $obs= Observable->new();
	      if ($err=$obs->readString($entry,$base_model->MoleculeTypesList)){
		$err=errgen($err,$lno);
		last READ;
	      }
	      push @observables, $obs;
	    }
	    $base_model->Observables([@observables]);
	    printf "Read %d observable(s).\n", scalar(@{$base_model->Observables});
	  } else {	
	    $err=errgen("Could not process block type $name");
	    send_warning($err);
	    $err="";
	  }
	} elsif (/^([A-Za-z][^(]*)/){
          push(@gene1rate,$_);
	} else {
	  eval $_;
	  if ($@){ $err=errgen($@); last READ;};
	}
      } ##while bngl
    &pre_species1(\%nm_site,\%nm2_site);
    &pre_rules(\%nm_site,\%nm2_site,$param_prefix,\%skf,\%dpp_site,\%lis1h);
    &pre_obs1(\%nm_site,\%nm2_site,$param_prefix,\%skf,\%dpp_site,\%lis1h);

 print WFILErec " EXIT FROM sub PRE_MACR for *.rec \n";
 print WFILErec " BEGIN COR_NET \n";


 $macrfile= ">macr_${param_prefix}.bngl";   # $macrfile= ">macr_${param_prefix}.bngl";
 $parfile= "<macr_${param_prefix}.par";  # $parfile= "<macr_${param_prefix}.par";
 $spec2file= "<macr_${param_prefix}.spec2";  # $spec2file= "<macr_${param_prefix}.spec2";
 $rulesfile= "<macr_${param_prefix}.rules";  # $rulesfile= "<macr_${param_prefix}.rules";
 $obserfile= "<macr_${param_prefix}.obser";  # $obserfile= "<macr_${param_prefix}.obser";

 open (WFILEmacr, $macrfile) or die "Can't open $macrfile: $!\n";
 open (RFILEpar, $parfile) or die "Can't open $parfile: $!\n";
 open (RFILEspec2, $spec2file) or die "Can't open $spec2file: $!\n";
 open (RFILErules, $rulesfile) or die "Can't open $rulesfile: $!\n";
 open (RFILEobser, $obserfile) or die "Can't open $obserfile: $!\n";

 while ($line=<RFILEpar>) {        # *.par
   print WFILEmacr "$line";        # macr_*.bngl
 }## while
 while ($line=<RFILEspec2>) {      # *.spec2
   print WFILEmacr "$line";        # *.macr
 }## while
 while ($line=<RFILErules>) {      # *.rules
   print WFILEmacr "$line";        # *.macr
 }## while
 while ($line=<RFILEobser>) {      # *.obser
   print WFILEmacr "$line";        # *.macr
 }## while

 close(WFILEmacr);
 close(RFILEpar);
 close(RFILEspec2);
 close(RFILErules);
 close(RFILEobser);

    for $suff ("obser","par","spec2","spec1","rules","cdat"){
      $file= "macr_${param_prefix}.$suff";
      next unless -e $file;
        unlink($file)|| die "Can't DELETE : $!)";
    }
} 
#pre_macr-end

##pre_species1-begin
sub pre_species1{                
my ($nm_site, $nm2_site) = @_;   
 my ($entry, $name, $key, $val, $ii, $na, $va);
 my (@sit);
foreach $ii (@species2) {
  $name=""; $va = '';
  if ($ii=~ s/^\s*([^:].*)[:]\s*//){
    $name= $1;
    print "ERROR 1001 block species      (user) name=$name\n";
    exit;
  }
  elsif ($ii=~ s/^\s*(\d+)\s+//){
  }

  my @tok1ens= split(' ',$ii);
  $entry = $tok1ens[0];                           # Grb2(SH2,SH3!1).Sos(dom!1)
  while ( $entry =~ s/(?:[\(])(.*?)(?:[)])//) {   # select inside ( and )
    $name  = $`;                                  # L     Grb2 Sos
    if ( !(exists($$nm_site{$name})) )   {
      @sit = '';
      @sit= split(/\,/,$1);                       # l l   SH2 SH3!1 
      foreach (@sit) {  s/[!~].*//; }             
      foreach (@sit) {  $nm_site->{$name.":".$_}++; }   # L:l 2; 
      while(($key,$val) = each(%$nm_site)) {
        if ( $key=~/:/ && $val gt 1 ) { $nm2_site->{$key} = $val; }  
      }
      $nm_site->{$name}= scalar(@sit);        # L 2
    }##if !(exists
    $entry =~ s/$name\.//;
  } ##while
}##foreach
  return;
}
#pre_species1-end

#pre_rules-begin
sub pre_rules{  
my ($nm_site, $nm2_site, $param_prefix, $skf, $dpp_site, $lis1h) = @_;
 my ($rulesfile, $obserfile, $spec2file);
 my ($line, $endl, $entry, $lno, $name, $i, $key_d);#, $endl);
 my ( @lis1, @rab);
 my (%site_lig);

                 #     ----------RULES-------------

 $rulesfile= ">macr_${param_prefix}.rules"; 
 open (WFILErules, $rulesfile) or die "Can't open $rulesfile: $!\n";
 print WFILErules "begin reaction_rules\n";
 @lis1 = ();
 &del_blank(\@reac1tion,\@lis1);   

 &hash_skf(\@lis1,$skf,$nm2_site);
 &hash_sor(\@lis1,$nm2_site,$lis1h,$dpp_site); 

 &hierarch_sit($dpp_site);        #----prepare hierarchy set2 of variables

 &min_set($dpp_site,$lis1h,$nm_site,$nm2_site,\%site_lig); 

 &trans_rec($dpp_site,$lis1h,\@lis1,\%site_lig);  
 print WFILErules "end reaction_rules\n";
 close(WFILErules);                    # *.rules
}
#pre_rules-end

#del_blank-begin
sub del_blank{
my ($str, $lis1) = @_;
 for ( @{$str} ) {        # 1  L(r) + R(l,d) <-> L(r!1).R(l!1,d) kp1, km1 14
   $_ =~ s/\#.*//;        # delete comment
   s/^\s{0,}//;          
   if (length($_)) {
     if (!($_ =~ /^\s{0,}$/)) {  
       s/\s{0,}$//;             
       s/\s{1,}/ /g;           
       s/\s{1}/;/g;             #1;L(r);+;R(l,d);<->;L(r!1).R(l!1,d);kp1,;km1
       if ( $_ =~ /^\d.*?;/ ) {
         $_ = ";".$';
       } else {                  #;L(r);+;R(l,d);<->;L(r!1).R(l!1,d);kp1,;km1
         $_ = ";".$_;
       }
       push @{$lis1}, $_;        #;L(r);+;R(l,d);<->;L(r!1).R(l!1,d);kp1,;km1
     } ##if2
   } ##if1
 } ##for
}
#del_blank-end

#hash_skf-begin
sub hash_skf{
my ($lis1, $skf, $nm2_site) = @_;
 my ($rab, $reac, $prod, $key_d, );
 my ($r1, $r2, $p1, $p2, $i);
 print WFILErec " from hash_skf\n";
 for (@{$lis1}) {          # loop string (rules i.e.)
$testbngl =$_;
   $rab = $_;              #;Rec(a);+;Lig(l,l);<->;Rec(a!1).Lig(l!1,l);kp1,;km1
   $rab =~ /<->|->/;    
   $reac = $`;             # reactants ;Rec(a);+;Lig(l,l);
   $prod = $';             # products  ;Rec(a!1).Lig(l!1,l);kp1,;km1
   $r1 = "";
   $p1 = "";
   $r2 = "";
   $p2 = "";
   ($r1,$r2) = split(/;\+;/,$reac); #;Rec(a); ;Lig(l,l);
   ($p1,$p2) = split(/;\+;/,$prod); #;Rec(a!1).Lig(l!1,l);kp1,;km1
   &skf0($r1,$skf,$nm2_site);       #;Rec(a)
   &skf0($r2,$skf,$nm2_site);       #Lig(l,l);
   &skf0($p1,$skf,$nm2_site);       #;Rec(a!1).Lig(l!1,l);kp1,;km1
   &skf0($p2,$skf,$nm2_site);
 } ##for

   $i = 1;
   foreach $key_d (sort keys %{$skf}) {        # sorting hash on key
     print WFILErec " $i  $key_d ";
     foreach (@{$skf->{$key_d}}) { print WFILErec " $_ "; }
     print WFILErec "\n";
     $i++;
   }
}
#hash_skf-end

#inc_elt-begin
sub inc_elt{               
my ($re,$pr) = @_;
 my $jj = 0;
  foreach (@{$pr}) {
    if ($re eq $_) {
      return($jj+1);
    } else {
     ++$jj;
    }
  }
  return(0);
}
#inc_elt-end

#inc-set-begin                 # There are dubl-elements !!!
sub inc_set{                   # check of inclusion set @re into @pr
my ($re,$pr) = @_;             # @rab=@(12)    @rab2=@(134)
 my (@pm, $jj, $out);
  @pm = @$pr;
  foreach (@{$re}) {
    if ( !($jj = inc_elt($_,\@pm)) ) {
      return(0);               # no include
    } else {
      $jj--;
      $out = splice(@pm,$jj,1);
    }
  }
  return(1);                   # include
}
#inc-set-end
                   # &del_set(\@rem,\@prm,\@rem_prm);
#del-set-begin                 # There are dubl-elements !!!
sub del_set{                   # elements of rem which not into @prm
my ($rem,$prm,$rem_prm) = @_;     
 my (@pm, $jj, $out);
  @pm = @$prm;
  @$rem_prm = ();
  foreach (@{$rem}) {
    if ( !($jj = inc_elt($_,\@pm)) ) {
      push(@{$rem_prm},$_);    # no include
    } else {
      $jj--;
      $out = splice(@pm,$jj,1);
    }
  }
}
#del-set-end

=pod
#inc-set-begin
sub inc_set{                   # check of inclusion set @re into @pr
my ($re,$pr) = @_;             # @rab=@(12)    @rab2=@(134)
 my (@rm,@pm);
 my %seen = ();              
 my @se = () ;
   @rm = @$re;   @pm = @$pr;   @seen{@rm} = ();
   delete @seen{@pm};
   @se = keys %seen;
   if (@se) {
     return(0);          # no include
   } else {
     return(1);          # include
   }
}
#inc-set-end
=cut
#activ_sit-begin
sub activ_sit{    
my  ($typrul, $reac, $prod, $mreac) = @_;  
 my ($name,$r1);
 while($$reac) {
  $$reac =~ /[\(].*?[)]/;   # select (a!1)  i.e. first ()  (r!1)
  $name = $`;               # name:=R                      A
  $r1 = $name.$&;           # r1:=R(a!1) from reactant     A(r!1)
  $$reac = $';              # .A(r!1); delete R(a!1) from reactant 
  $$reac =~ s/^(?:[;+.])*//;  # A(r!1);         
  if ( $$prod =~ /\Q$r1\E/ )  {
     push(@$mreac,$r1);    # R(a!1) from $reac to array   A(r!1)
  } else {
     push(@$mreac,$r1);    # R(a!1) from $reac to array   A(r!1)
  }
 } ## while($reac)
}
#activ_sit-end

#hierarch_sit-begin
sub hierarch_sit{
my ($dpp_site) = @_;  
 my ($fl, $key, $name, $keyold, $bsite, $keyr, $iter);
 my (@a, @b, @bb, @c);
 my (%tab_site, %m_kv);     		

 $fl = 1;
 $iter = 1;
 while($fl) {             
 %m_kv = %$dpp_site;     
  $fl=0;                
  foreach $key (keys %m_kv) {  # Lyn:SH2; Lyn:U;         
    %tab_site = ();
    @bb = ();
    @b = @{$m_kv{$key}};       # SH2 U       SH2 U      
    ($name,$keyold) = split(/\:/,$key);  # Lyn:SH2
    foreach (@b) {             # SH2 U; SH2 U;
      $tab_site{$_} = 1;       # SH2->1 U->1  
    }
    for $bsite (@b) {          # SH2 U                  
      $keyr = $name.':'.($bsite);           # Lyn:SH2 
      if ( @{$m_kv{$keyr}} ) {
        @a = @{$m_kv{$keyr}};  # set @a=SH2 U
        foreach (@a) {
          if (!$tab_site{$_}) {
            $tab_site{$_} = 1;
            push(@bb,$_);      #  SH2 U
            $fl = 1;
          }
        } ##@a
      } ##if
    }   ##@b
    push(@b,@bb);              #  SH2 U
    @{$m_kv{$key}}=@b;         #  SH2 U   
    delete $dpp_site->{$key};  #  Lyn:SH2    Lyn:U        
    @c = sort sort {$a<=>$b} @b;
    @{$dpp_site->{$key}} = @c;        # (13456)
  } #foreach $key
  print WFILErec " iteration $iter dpp_site sort key of variable  (n/n hierarch_sit)\n";
  foreach $key (sort keys %{$dpp_site}) {      
    print WFILErec " $key @{$dpp_site->{$key}}\n";
  }
  $iter++;
 } ##while
}
#hierarch_sit-end

#min_set-begin
sub min_set{                    #----prepare minimal set of variables
my ($dpp_site, $lis1h, $nm_site, $nm2_site, $site_lig) = @_;
 my ($key, $name, $si1,, $si2, $key2, $name2, $fl, $ii, $r1, $r2);
 my (@key_del, @rabm, @rab2);
 my %f_obr = ();
 @rabm = keys(%{$dpp_site});
 $fl = scalar(@rabm);          
 $ii = 0;
 @key_del = ();                 # key of string which will delete into @m_kv1
 while ( ($key,$r1) = each(%{$dpp_site}) ) {
   $ii++;
   if ( $f_obr{$key} )  { next; }
   $f_obr{$key} =1 ;
   ($name,$si1) = split(/\:/,$key);     # S:site3       A r
   while ( ($key2,$r2)=each(%{$dpp_site}) ) {
      if ( $f_obr{$key2} )  { next; }
      ($name2,$si2) = split(/\:/,$key2);
      if (($name eq $name2)&&($key ne $key2)) {
        if ( ($#$r1 == $#$r2) && ($si1 eq "%" || $si2 eq "0") ) {
         if (&inc_set($r1,$r2) && ($si1 eq "%")) {  push(@key_del,$key);  next;   }
         if (&inc_set($r2,$r1) && ($si2 eq "%")) {  push(@key_del,$key2); next;   }
        } else {
         if (&inc_set($r1,$r2)) {  push(@key_del,$key);  next;   }
         if (&inc_set($r2,$r1)) {  push(@key_del,$key2); next;   }
        }
      }  ##if name
    } ##foreach2 dpp_site
    keys %$dpp_site;
    if ($ii == $fl) { last; }
 } ##foreach1 dpp_site
 foreach (@key_del) {
   delete $dpp_site->{$_};
 }
 while ( ($key,$r1) = each(%$dpp_site) ) {
   ($key2,$ii) = split(/\:/,$key);
   @rabm = @{$dpp_site->{$key}};
   if ( ($nm_site->{$key2}) == ($#rabm+1) )  {
      delete $dpp_site->{$key} ;
   }
 } ## while
 print WFILErec " MINIMUM set of variable  (n/n min_set)\n";
 foreach $key (sort keys %{$dpp_site}) {
   print WFILErec " $key, @{$dpp_site->{$key}}\n";
   ($name,undef) = split(/\:/,$key);     # S:site3
   @rabm = ();
   @rabm = @{$dpp_site->{$key}};          # site1 site3
   foreach(@rabm) { ++$$site_lig{$name.":".$_};}
 }
 print WFILErec " hash site_lig. \n";
 if ( !(%$dpp_site) ) {  print " REDUCTION NO\n";  exit; } 
 foreach $key (sort keys %{$site_lig}) {
   if ( $site_lig->{$key} > 1 ) {
     print WFILErec " $key, $site_lig->{$key}\n";
   } else {
     delete $site_lig->{$key};
   }
 }
 @rabm = keys %{$site_lig};
 print WFILErec " hash lis1h. \n";
 foreach $key (sort keys %{$lis1h}) {
   if ( &inc_elt($key,\@rabm) ) {
     print WFILErec " $key, @{$lis1h->{$key}}\n";
   } else {
     delete $lis1h->{$key};
   }
 }
 print WFILErec " Macro rules  (n/n trans_rec)\n";
}
#min_set-end

#num_site-begin
sub num_site{           
my ($re, $pr, $nm2_site, $dep_sit) = @_;
 my ($is, $ii, $ss, $name, $eqv);         
 my (@rem, @prm, @res, @aonly);
 my (%seen);
  $eqv = 0;
  if ( $re eq $pr) { $eqv = 1;} 
  $re =~ /(?:[\(])(.*)(?:[)])/;    # select into ( and ) Lig(l,l) from reactant
  $name = $`;                      # Lig
  $ss= $1;                         # l,l
  @rem= split(/\,/,$ss);           # l l
  $pr =~ /(?:[\(])(.*)(?:[)])/;    # Lig(l!1,l)
  $ss = $1;                        # select into ( and )  Lig(l!1,l) from product
  @prm= split(/\,/,$ss);           # l!1 l
  @res = @rem;                     # l l  
  foreach (@res) {  s/[!~].*//; }  # select name site  l l  
  %seen = (); @aonly = ();
  foreach $ii (@res) {
     push(@aonly,$ii) unless $seen{$ii}++;  # delete dubl l l => l
  }   
  if ( $eqv ) { @$dep_sit = @aonly; $ii = "%"; return($ii); }
  if ( $#res != $#aonly )  {
    for ($ii=0;$ii<=$#rem;$ii++) {  
      ($rem[$ii],$prm[$ii]) = ($prm[$ii],$rem[$ii]);  # rem=l!1 l  prm=l l
    }
  }
  foreach (@aonly) {                                      # l
    if ( defined($nm2_site->{$name.":".$_}) ) {           # Lig:l
      for ($ii=2;$ii<=$nm2_site->{$name.":".$_};$ii++) {
        push(@rem,$_.":".$ii);      # l!1 l adding r:2 => l!1 l l:2
        push(@prm,$_.":".$ii);      # l l   adding r:2 => l l l:2
      }
    }
  }
   &del_set(\@rem,\@prm,\@aonly);    # there are dubl !!!
##   %seen = (); @seen{@rem} = ();   # search elements from $rem which not into @prm 
##   delete @seen{@prm};             # delete from @rem keys from @prm
##   @aonly = keys %seen;            # only keys rem-prm  = l!1 wich changed
   if ( $#aonly != 0 ) {
     print STDERR "ERROR 1003:  aonly= $#aonly reactant=$re product=$pr \n";
     exit;
   }
   $ii = $aonly[0];         # l!1
   @prm = ();
   push(@prm,$ii);          # activnyi site l!1 
   &del_set(\@rem,\@prm,\@aonly);    # there are dubl
##   %seen = ();              # search elements from $rem which not into @prm
##   @seen{@rem} = ();        # creat tabl view  dependence site
##   delete @seen{@prm};      # delete from dependence sites  one activny site
##   @aonly = keys %seen;     # only keys rem-prm=l l:2
   foreach (@aonly) {  s/[!~].*//;   }      # select name site
   @$dep_sit = @aonly; 	      #l l:2        # depended sites without activ_site
   $ii =~ s/[!~].*//;         #l!1=>l       # select name site
   push(@$dep_sit,$ii);       #l l:2 l      # add activ_site
   %seen = (); @res = ();
   foreach $ii (@$dep_sit) {  push(@res,$ii) unless $seen{$ii}++; }   # l l:2  delete dubl 
   @$dep_sit = @res;          # l l:2
   return($ii);               # l=name of site of reaction
}
#num_site-end

#hash_sor-begin
sub hash_sor{
my ($lis1, $nm2_site, $lis1h, $dpp_site) = @_;
 my ($rab, $name, $reac, $prod, $coef, $n_site, $uni);
 my ($key_dubl, $key_d, $key_l, $ind, $r1, $p1, $i, $typrul);
 my (@mreac, @mprod, @dep_sit, @rxd, @lis_sit, @rxl);
 print WFILErec " for hash lis1h  (n/n hash_sor)\n";
 $ind = -1;
 $uni=1;
 for (@{$lis1}) {          # loop rules
   $ind++;
   $rab = $_;              #;R(a!1).A(r!1);->;R(a);+;A(r);kmS #;Rec(a);+;Lig(l,l);<->;Rec(a!1).Lig(l!1,l);kp1,;km1
   $rab =~ s/^;//;         #R(a!1).A(r!1);->;R(a);+;A(r);kmS #Rec(a);+;Lig(l,l);<->;Rec(a!1).Lig(l!1,l);kp1,;km1
   $rab =~ /<->|->/;       # <->  or -> separation
   $reac = $`;             # reactants R(a!1).A(r!1);
   $prod = $';             # products  ;R(a);+;A(r);kp2,;km1 
   $prod =~ /.*\);/;
   $prod =$&;              # ;R(a);+;A(r);
   $coef = $';             # kms   # kp2,;km1    
   @dep_sit=();
   @lis_sit=();
   @mreac = ();
   @mprod = ();
   $r1 = "";
   $p1 = "";
   $typrul = 0;
   if ($prod =~ /;\+;/) { $typrul = 1;}
   &activ_sit($typrul,\$reac,\$prod,\@mreac);   # 1,R(a!1).A(r!1);, ;R(a);+;A(r);, blank
   for (@mreac) {              
     $r1 = $_;                  #R(a!1) A(r!1)  #  Rec(a) or Lig(l,l)
     $_ =~ /\(/;                # select (
     $name = $`;                         #R       # name:=Rec   or Lig
     $prod =~ /\Q$name\E[\(].*?[)]/;     #R(a)    # Rec(...)
     $p1 = $&;                  #p1=R(a) from product   # p1:=Rec(a!1)  or Lig(l!1,l)
     $prod =~ s/\Q$p1\E//;      # delete R(a) from product
     push(@mprod,$p1);          #R(a)       # Rec(a!1) from $prod to array
     $n_site = &num_site($r1,$p1,$nm2_site,\@dep_sit);  # R(a!1) R(a) -> a
     if ( $n_site eq "%" ) {
       $n_site = "%".$uni; 
       $uni++;
     }
     @lis_sit=();
     push(@lis_sit,$ind);       # hash matrix->key=number of string
     @rxd = ();
     @rxl = ();
     $key_dubl = "";
     $key_d = $name.':'.$n_site;              # R:a
     if ( exists($$nm2_site{$key_d}) ) {     
       $key_dubl = $name.':'.$n_site.":2";    # L:l:2
     }
     $key_l = $key_d;                         # R:a
     if ( @{$dpp_site->{$key_d}} ) {
        @rxd = @{$dpp_site->{$key_d}};        # a b g
     }
     foreach (@rxd) {                         # a b g
       if (!&inc_elt($_,\@dep_sit)) {  push(@dep_sit,$_)  }
     }
     @{$dpp_site->{$key_d}} = @dep_sit;      
     if ( @{$lis1h->{$key_l}} ) {
       @rxl = @{$lis1h->{$key_l}};        
     }
     foreach (@rxl) {
       if (!&inc_elt($_,\@lis_sit)) {  push(@lis_sit,$_)  }
     }
     @{$lis1h->{$key_l}} = @lis_sit;        
     print WFILErec " $key_l ";               # Rec:a
     foreach (@lis_sit) { print WFILErec " $_ "; }  # 0  n-string into rules
     print WFILErec "  $$lis1[$ind]\n";       # ;Rec(a);+;Lig(l,l);<->;Rec(a!1).Lig(l!1,l);kp1,km1
     if ($key_dubl) {
       print WFILErec " $key_dubl ";                  # L:l:2
       foreach (@lis_sit) { print WFILErec " $_ "; }  # 0  n-string into rules
       print WFILErec "  $$lis1[$ind]\n";  # ;Rec(a);+;Lig(l,l);<->;Rec(a!1).Lig(l!1,l);kp1,km1
     }
   } ##for @mreac
   if ($#mreac != $#mprod) {
     print "ERROR: 1004 n_mreac= $#mreac  : n_mprod= $#mprod \n";
     exit;
   }
 } ## @$lis1   for hash

 print WFILErec " sort hash dpp_site  (n/n hash_sor)\n";
 $i=1;
 foreach $key_d (sort keys %{$dpp_site}) {        # sorting hash on key
   print WFILErec " $i  $key_d ";
   foreach (@{$dpp_site->{$key_d}}) { print WFILErec " $_ "; }
   print WFILErec "\n";
   $i++;
 }
}
#hash_sor-end

#add_skf-begin
sub add_skf{             # @list  r r input
my  ($skf1, $skf2, $sit1, $skf, $nm2_site) = @_;  # @dub   r 2 output
 my ($key, $ii);
 my (@rabm);
     @rabm = ();
     $key = $skf1.":".$skf2;                       # L:R
     if ( defined($skf->{$key}) ) {
       @rabm = @{$skf->{$key}};
       if ( !(&inc_elt($sit1,\@rabm)) ) {          # r
         push(@{$skf->{$key}},$sit1);              # L:R r
       }
     } else {
       push(@{$skf->{$key}},$sit1);                # L:R r
     }
     if ( defined($nm2_site->{$skf1.":".$sit1}) ) {           # L:r
       for ($ii=2;$ii<=$nm2_site->{$skf1.":".$sit1};$ii++) {
         push(@{$skf->{$key}},$sit1.":".$ii);                 # L:R r r:2
       }
     }
}
#add_skf-end

#skf0-begin
sub skf0{                                    #processing of links
my ($rp1, $skf, $nm2_site) = @_;             #;Rec(a!1).Lig(l!1,l)
 my ($p1, $skf1, $skf2, $sit1, $sit2, $lnk1, $lnk2, $lef1, $rit1, $lef2, $rit2);
 my (@rabm);

   $p1 =$rp1;              #;R(b~pY!2).A(s).P(s!2);k3,;k_3 
   while($p1 =~ /!/) {     #;R(b~pY!1!2).A(s!1).P(s!2);k3,;k_3 
     $lef1 = $`;           #;R(b~pY   ;Rd(Y~pY
     $rit1 = $';           #1!2).A(s!1).P(s!2);k3;k_3   
     $rit1 =~ /[,)!]/;     #1!2).A(s!1).P(s!2);k3;k_3  
     $lnk1 = "!".$`;       #!1        lnk1***
     if ( ($` eq "+") || ($` eq "?") ) {      #  (visjak + and ?)
       $p1 =~ s/\Q$lnk1\E//;     #;R(b~pY!2).A(s!1).P(s!2);k3,;k_3
       next;
     }  
     $lef1 =~ /.*[,\(]/;   #;R(b~pY     
     $skf1 = $&;           #;R(              ;Rd(
     $sit1 = $';           #b~pY             Y!1!2
     $sit1 =~ s/[~!].*//;  #b                Y
     $skf1 =~ s/.*[\.;]//; #R(               Rd(
     $skf1 =~ s/\(.*//;    #R                Rd     
     $p1 =~ s/\Q$lnk1\E//; #;R(b~pY!2).A(s!1).P(s!2);k3,;k_3 
     if (!($p1 =~ /\Q$lnk1\E/)) {    # bob? (visjak?)
       next;
     }
     $lef2 = $`;           #;R(b~pY!2).A(s                
     $rit2 = $';           #).P(s!2);k3,;k_3         
     $lnk2 = $lnk1;        #!1        lnk2***
     $lef2 =~ /.*[,\(]/;   #;R(b~pY!2).A(s      
     $skf2 = $&;           #;R(b~pY!2).A(        
     $sit2 = $';           #s                       
     $sit2 =~ s/[~!].*//;  #s                     
     $skf2 =~ s/.*[\.;]//; #A(                  
     $skf2 =~ s/\(.*//;    #A               
     $p1 =~ s/\Q$lnk2\E//;     #;R(b~pY!2).A(s).P(s!2);k3,;k_3  
     &add_skf($skf1,$skf2,$sit1,$skf,$nm2_site);  #  R:L l
     $lef1 ="";
     &add_skf($skf2,$skf1,$sit2,$skf,$nm2_site);  #  L:R r r:2
     $lef1 ="";
   } ##while p1
}
#skf0-end

#cor_net-begin
sub cor_net{  
my ($param_prefix) = @_;
my ($recfile,$netfile,$rabfile,$namenet,$namerab,$linen,$rec,$linei,$line);
my ($nn,$fl,$tei1,$out,$endl,$key,$ii,$jj,$r1,$r2,$fm1,$fm2,$fs1,$fs2,$file,$suff);
my ($fli,$r1i,$r2i,$fm1i,$fm2i,$fs1i,$fs2i,$n_subtracts,$n_adds,$s1,$s2,$s3,$nk);
my ($foun,$nnn,$s1n,$s3n,$nkn,$an,$bn,$ns1,$ns2,$li1,$li2,$si1,$m1,$si2,$m2);
my ($foui,$nni,$s1i,$s3i,$nki,$ai,$bi);
my (@rabn,@rr1,@rr2,@val,@reac);
my (@rabi,@rr1i,@rr2i);
my (%ms,%ms1,%ms2,%master,%slive,%croc,%rmaster,%rslive);
my (%m_spe,%s_spe,%rea_l,%rea_r,%rrea_l,%rrea_r,%dubl);
 %ts3n = ();
 @species = ();

 $recfile= "<macr_${param_prefix}.rec";
 open (RFILErec, $recfile) or die "Can't open $recfile: $!\n";
 $fl=0;   %croc = ();
 while ( $line=<RFILErec> ) {
   if ( ($line =~ / MINIMUM set/) ) {
     $fl=1; next;
   }
   elsif ( ($line =~ / hash site_lig./)  ) {
     $fl=0; last;
   }
   elsif ( $fl ) {                              # work line
     $line =~ s/^\s+//;                         # A:a1, a1 r  A:a2, a2 r 
     ($tei1,$rec) = split(/\:/,$line,2);        # tei1=A rec=a1, a1 r
     @rabn = ();
     (undef,@rabn) = split(/\s+/,$rec);         # rabn =a1 r  (depend sites and activ site
     $out = "";
     $out = $tei1.$rabn[0]."_";                 #Aa1_
     foreach(@rabn) {
       if(!($_ eq $rabn[0])) { $out .= $_."_"; }    #Aa1_r_
     }
     chop($out);                               #Aa1_r
     push(@{$croc{$out}},@rabn);               # Aa1_r = (a1 r) nabor sites
     if ( exists($master{$tei1}) ) {      
       push(@{$slive{$tei1}},$out) ;           # key=A;  value=Aa2_r   SLIVE
     } else {
       $master{$tei1} = $out ;                 # key=A;  value=Aa1_r   MASTER
     }
     next;                                     # next line from dpp_site
   }
   else {
     next;
   }
 } ## whileREC
 close(RFILErec);        #########1

 %rmaster = reverse(%master);            
 %rslive = ();
 while( ($key,$rec) = each(%slive) ) {
   @rslive{@$rec} = ($key) ;               
 }
 %ms1 = (); %ms2 = ();
 while( ($key,$rec) = each(%rmaster) ) {
   $ms1{$key}=$rec;
 }
 while( ($key,$rec) = each(%rslive) ) {
    $ms1{$key}=$rec;
 }
 %ms2 = %ms1;
 %ms = %ms1;

     #adaptation  *.net             ###########2

 $rabfile= "macr_${param_prefix}.rab";
 $netfile= "macr_${param_prefix}.net";
 open (RFILEnet, "<", $netfile) or die "Can't open $netfile: $!\n";
 open (WFILErab, ">", $rabfile) or die "Can't open $netfile: $!\n";

 while ( $line=<RFILEnet> ) {            # search-copy "begin molecule types"
   if ( $line =~ /begin molecule types/ ) {
     print WFILErab "$line";
     last;
   } else {
     print WFILErab "$line";
   }
 } ## whileNET
 $ii = 0;
 while ( $line=<RFILEnet> ) {                # work molecule types
   if ( $line =~ /end molecule types/ ) {
     $ii++;
     $fm1 = sprintf "%3d %s\n", $ii, "TRASH";  
     print WFILErab "$fm1";                 
     print WFILErab "$line";        
     last;
   } else {
     print WFILErab "$line";  $ii++;
   }
 } ## whileNET

     #preparation %m_spe %s_spe  ###############3

 while ( $line=<RFILEnet> ) {            # search-copy "begin species"
   if ( $line =~ /begin species/ ) {
     print WFILErab "$line";
     last;
   } else {
     print WFILErab "$line";
   }
 } ## whileNET

     #preparation %m_spe %s_spe  ###############3

 @species=();  $ii = 0;
 while ( $line=<RFILEnet> ) {                # WORK SPECIES_macr
   if ( $line =~ /end species/ ) {
     $ii++;
     $fm1 = sprintf "%5d %s\n", $ii, "TRASH   0";  
     print WFILErab "$fm1";                 
     print WFILErab "$line";         
     last;
   } else {                                 
     print WFILErab "$line";    $ii++;     
     chomp($line);
     $line =~ s/^\s+// ;                     #3 Sykl_tSH2(l~Y,tSH2)   Syk_tot
     ($ns1,$li1,undef) = split(/\s+/,$line); #3 Sykl_tSH2(l~Y,tSH2)   Syk_tot
     push(@species,$li1);                    #Sykl_tSH2(l~Y,tSH2)
   } ##if
 } ## whileNET
              # @species good ##############4  @rr1 @rr2 %ms1

 while ( $line=<RFILEnet> ) {
   if ( $line =~ /begin reactions/ ) {
     print WFILErab "$line";
     last;
   } else {
     print WFILErab "$line";
   }
 } ## whileNET

 @reac = ();
 while ( $line=<RFILEnet> ) {
   if ( $line =~ /end reactions/ ) {
     $endl = $line;
     last;
   } else {
     push(@reac,$line);                  # full reac
   }
 } ## input reactions
              #################5 update reactions
   $nn = -1;
 foreach $linen ( @reac ) {    
   $nn++;
     if ( $nn == 0 ) { $reac[$nn] = $linen; next; } 
     if ( !($linen =~ /\,/) ) {   
       $reac[$nn] = $linen;            
     }else {
       @rr1 = (); @rr2 = (); @rabn = ();
       $line =$linen;
       $line =~ s/^\s+//;             #    27 14 20 km1
       chomp($line);
       ($r1,$r2) = split(/\,/,$line); #29 20 4,18 kmS  or #30 5,17 11 kpS
       @rr1 = split(/\s+/,$r1);       #29 20 4            #30 5
       @rr2 = split(/\s+/,$r2);       #18 kmS             #17 11 kpS
       push(@rabn,@rr1);
       push(@rabn,@rr2);
         if ($#rr1 == 1) {            ####LEFTvariant - 30 5,17 11 kpS
           ($nnn,$an,$bn,$s3n,$nkn) = @rabn;
           $n_adds = 0;  %dubl = ();
           for ( $ii=0; $ii<$nn; $ii++ ) {  
             @rr1i = (); @rr2i = (); @rabi = ();  
             if ($n_adds == 2) { %dubl = (); last; }       # out from 
             $linei = $reac[$ii];               # read from 1<n
             if ( !($linei =~ /\,/) ) { next; } # net zapjatoi
             $line = $linei;
             $line =~ s/^\s+//;                 #    27 14 20 km1
             chomp($line);  #30 5,17 11 kpS or  #30 5,17 11,7 kpS or #29 20 4,18 kmS
             ($r1i,$r2i) = split(/\,/,$line,2); #30 5(17 11 kpS) or 30 5(17 11,7 kpS) or #29 20 4(18 km
             @rr1i = split(/\s+/,$r1i);         # (30 5) or (29 20 4)
             @rr2i = split(/\s+/,$r2i);         #(17 11 kpS) or (17 11,7 kpS) or (18 kmS)
             push(@rabi,@rr1i);  
             push(@rabi,@rr2i);   # (30 5 17 11 kpS) or (30 5 17 11,7 kpS) 
             if ($#rr1i == 1) {         ####LEFTvariant_ii - 30 5,17 11 kpS or 30 5,17 11,7 kpS
               ($nni,$ai,$bi,$s3i,$nki) = @rabi;
               if ( $an!=$ai&&$an!=$bi&&$bn!=$ai&&$bn!=$bi ) {
                 next;
               }elsif ( $s3i =~ /,/ )  {
                 next;
               }else{
                 L_AiAn_BiBn(\%croc,\%ms,\@rabn,\@rabi,\@reac,\$n_adds,\%dubl);
               }
             }
           } ##ii LEFT
           if (!($n_adds)) { $reac[$nn] = $linen; }

         }else {            ####RIGHTvariant - 29 20 4,18 kmS
           ($nnn,$s1n,$an,$bn,$nkn) = @rabn;
           $n_subtracts = 0;  %dubl = ();
           for ( $ii=0; $ii<$nn; $ii++ ) {  # sverka s predydushimi records reactions
             @rr1i = (); @rr2i = (); @rabi = ();
             if ($n_subtracts == 2) { %dubl = (); last; }  # out from 
             $linei = $reac[$ii];               # read from 1<n
             if ( !($linei =~ /\,/) ) { next; } # net zapjatoi
             $line = $linei;
             $line =~ s/^\s+//;                #    27 14 20 km1
             chomp($line);
             ($r1i,$r2i) = split(/\,/,$line,2);  #29 20 4,18 kmS  or #30 5,17 11 kpS
             @rr1i = split(/\s+/,$r1i);        #29 20 4            #30 5
             @rr2i = split(/\s+/,$r2i);        #18 kmS             #17 11 kpS
             push(@rabi,@rr1i);  
             push(@rabi,@rr2i);
             if ($#rr1i == 2) {             ####RIGHTvariant_ii - 30 5 17,11 kpS
               ($nni,$s1i,$ai,$bi,$nki) = @rabi;
               if ( $an!=$ai&&$an!=$bi&&$bn!=$ai&&$bn!=$bi ) {
                 next;
               }else{
                 R_AiAn_BiBn(\%croc,\%ms,\@rabn,\@rabi,\@reac,\$n_subtracts,\%dubl);
               }
             }
             if ($n_subtracts == 2) { subTrash(\@rabn,\@reac); %dubl = (); }   # add musor
           } ##ii RIGHT
           if ( !($n_subtracts) ) { $reac[$nn] = $linen; }
         }##else if rr1<>1  #####RIGHT
     }##else if (",")
 } ## foreach NET reactions
 for ( $ii=0; $ii<=$nn; $ii++ ) {        # output $reac to *.rab
   print WFILErab "$reac[$ii]";
 }
 print WFILErab "$endl";                 # end reactions

              #################6 update droup
##@obser1vable
=pod
     foreach( ($key,@rabm) = %ts3n ) {    # prepare update GROUPS
       $egf = $species[$key-1];           # egf(r)   $key=1 @rabm=9,15,25,37,45,46
       $egf =~ s/\(.*//;                  # egf
       my $jj = '';
       foreach $ii ( @obser1vable ) {
         if ( $ii =~ /;$egf$/ ) {         #;Molecules;egf_tot;egf
           if ( $ii =~ /Molecules;(.*?);/ ) {
             @egf_tot{$1} = @rabm;        # %egf_tot
             last;
           }
         } ## if egf
       } ##for $ii @obser
     } ##foreach ts3n
=cut
{
my ($an, $bn, $key, $egf, $egfr1148, $jj);
     foreach( ($key,undef) = %ts3n ) {     # prepare update GROUPS
       my ($an,$bn) = split(".",$key);
       $egf = $species[$an-1];            # egf(r)   from 1 species
       $egfr1148 = $species[$bn-1];       # egfr1148(...)  from 5 spesies
       $egf =~ s/\(.*//;                  # egf
       $egfr1148 =~ s/\(.*//;             # egfr1148
       $jj = '';
       foreach $ii ( @obser1vable ) {
         if ( $ii =~ /;$egf$/ ) {           #;Molecules;egf_tot;egf
           if ( $ii =~ /Molecules;(.*?);/ ) {
             $egf_tot{$1} = @rabm;        # %egf_tot
             last;
           }
         } ## if egf
       } ##for $ii @obser
     } ##foreach ts3n
}
 while ( $line=<RFILEnet> ) {         
   if ( $line =~ /begin groups/ ) {
     print WFILErab "$line";
     last;                               # begin groups
   } else {
     print WFILErab "$line";
   }
 } ## whileNETbefore_group
 while ( $line=<RFILEnet> ) {
   if ( $line =~ /end groups/ ) {        # end groups
     print WFILErab "$line";
     last;
   } else {
     (undef,$name_group,undef) = split(' ',$line);
     if ( exists($egf_tot{$name_group}) ) {        # update GROUPS
       @rabm =@{$egf_tot{$name_group}};            # 9,15,25,37...  
       foreach ( @rabm ) {                         # 9,15,25,37...  
         $line =~ s/$_,|$_//;
       }
     } ## exists egf_tot
     print WFILErab "$line"; ##@obser1vable       
   }
 } ## whileNETafter_group
 close(RFILEnet);
 close(WFILErab);
rename($netfile,$netfile."isx");
rename($rabfile,$netfile);
} ## cor_net
#cor_net-end

#trans_rec-begin
sub trans_rec{         #---transform micro to macro(reactions)
my ($dpp_site, $lis1h, $lis1, $site_lig) = @_;
 my ($lin, $value, $val, $ind, $ino, $re_lin);
 my (@li);

 $re_lin = "";
 $ino = 0;
 $ind = -1;
 foreach $value (@{$lis1}){   #;Lig(l!1,l!2).Lyn(U!3,SH2).Rec(a!2,b~Y!3).Rec(a!1,b~Y);
   $ind++;
   @li = ();
   &r_mac($dpp_site,$lis1h,$value,$site_lig,$ind,\@li); #  @li=;Ssit1_site2(site1);+;K(s)...
   if (@li) {                    #$site_lig =S:site1         
     foreach $val (@li) {        #$val=;Ssite1_site2(site1);+;K(s);...
       $ino++;
       $lin = $ino.$val;         #10;Ssite1_site2(site1);+;K(s);<->;Ssite1_site2(site1!1).K(s!1);k1;k_1
       $lin =~ s/;/ /g;          #10 Ssite1_site2(site1) + K(s) 
       print WFILErec "$lin\n";
       print WFILErules "$lin\n";
     }
   } else {
       $ino++;
       $lin = $ino.$value;       #10;Ssite1_site2(site1);+;K(s);<->;Ssite1_site2(site1!1).K(s!1);k1;k_1
       $lin =~ s/;/ /g;          #10 Ssite1_site2(site1) + K(s) 
       print WFILErec "$lin\n";
       print WFILErules "$lin\n";
   }
 }##for value
}
#trans_rec-end

#trans_obs-begin
sub trans_obs{                     #---transform micro to macro(OBSERVABLES)
my ($dpp_site, $lis1h, $lis1, $skf) = @_;     
 my ($lin_mac,$rr,$group_type,$group_name);
 my ($mol,$mol_form,$n_form,$nobj_form);
 my (@re,@formula,@formulas);

 $nobj_form = 1;
 foreach (@{$lis1})  {             # line from observables
   $lin_mac="";
   $mol_form="";
   (undef,$group_type, $group_name, @re) =split(/\;/,$_); #;Molecules;RecMon;Rec(a);Rec(a!1).Lig(l!1,l)            
   $lin_mac = $group_type.";".$group_name." ";     #;Molecules;RecMon     ;Molecules;SA  OLD $lin_mac = $group_type.";+".$nobj_form."_0:".$group_name.$lin_mac;
   # Validate $group_name
   if (!($group_name=~/^[A-Za-z]/)){
       $_ =~ s/;/ /g;
       &exit_error("Invalid group name.  Must start with a letter.",$_);
   }
   for $rr (@re){                   #Rec(a) Rec(a!1).Lig(l!1,l)    Lyn(U,SH2)    S.A  from one string observables
       if( !($rr =~ /\(/ )) {
         $lin_mac .= &obs_mac0($rr, $dpp_site, $lis1h, $skf).";";    #Rec.Rec S.A => Ssite1_site2.A
       } else {
         $rr =~ /\(/;               #Rec(a) Rec(a!1).Lig(l!1,l)    Lyn(U,SH2)            
         keys %{$dpp_site};         # begin hash
         $mol = "";
         &obs_mac($rr, $dpp_site, $lis1h, \$mol, \$mol_form, $skf);  #Rec(a) Rec(a!1).Lig(l!1,l)    Lyn(U,SH2)
         if($mol){$lin_mac .= ";".$mol};       
       }  ##if
   } ##$rr
   if ($mol_form){                                # formulas
     $lin_mac .= ";";
     $lin_mac =~ s/;/ /g;
     chop($mol_form);
     @formulas = split(/\#/,$mol_form);
     foreach (@formulas){
       $n_form = 1;
       @formula = split(/\&/,$_);
       foreach (@formula){
         $lin_mac     .= $_."&";      # $lin_mac = $group_type.";*".$nobj_form."_".$n_form.":".$group_name.";".$_;
         ++$n_form;
       } ##formula    
       ++$nobj_form;
        $lin_mac = "#".$lin_mac;
        chop($lin_mac);
        chop($lin_mac);
        print WFILErec $lin_mac;
        print WFILEobser $lin_mac;
        $lin_mac = " ";
     } ##formulas
     print WFILErec "\n";
     print WFILEobser "\n";
   } else{
     $lin_mac =~ s/;/ /g;
     print WFILErec $lin_mac;
     print WFILErec "\n";
     print WFILEobser $lin_mac;
     print WFILEobser "\n";
   }
 }  ##lis1
}
#trans_obs-end

#r_mac-begin
sub r_mac{                      
my ($dpp_site, $lis1h, $value, $site_lig, $ind, $li) = @_;
 my ($out, $val, $aft, $lig, $fl, $key, $tei1, $tei2, $rnam);
 my (@re, @rabm, @rabms, @rabmn, @lir);
 my (%rdep, %rlig);

 %rlig = %{$site_lig};
 %rdep = %{$dpp_site};
 $lig = 0;
mlig:
 $fl  = 0;
 $val = $value;                              #R(l!+,a);+;A(r);->;R(l!+,a!1).A(r!1);kpS#;Rec(g~pY);+;Syk(tSH2);<->;Rec(g~pY!1).Syk(tSH2!1);kpS,;kmS
 foreach $key (sort keys %rdep) {            # A:a1, a1 r  A:a2, a2 r  # Syk:a_a_tSH2  Syk:l_l_tSH2
   ($tei1,undef) = split(/\:/,$key);         # tei1=A   
   if ( !($val =~ /[;\.]\Q$tei1\E\(/) ) {
     next;                                   # no Macro ;A(  and .A(
   }
   @rabms = ();                           
   @rabm= sort @{$rdep{$key}};               # rabm =a1 r  (depend sites and activ site
   foreach ( @rabm) { push(@rabms,$tei1.":".$_); }  # @rabms=A:a1 A:r
   $out = "";
   while ( $val =~ /[;\.]\Q$tei1\E\(/ ) {   # is ;A( or .A(
     @re = ();
     $tei2 = $&;                            #;A(
     $val = $';                             #r);->;R(l!+,a!1).A(r!1);kpS        #;<->;Rec(g~pY!1).Syk(tSH2!1);kpS,;kmS
     $out .= $`;                            #R(l!+,a);+    #;Rec(g~pY);+
     $' =~ /\)/;                            #r             #tSH2     a!2,b~Y!3     
     @re = split(/\,/,$`);                  #r             #tSH2     a!2 b~Y!3
     foreach (@re) {  s/[!~].*//;   }       #r          #tSH2     a b select name site
     if ( !(&inc_set(\@re,\@rabm)) ) {      # @re(tSH2) site include @rabm(a tSH2)?
       $out .= $tei2;                       # no
       next;
     }
     $fl = 1;
     chop($tei2);                           #;Syk
     $out .= $tei2.$rabm[0]."_";            #;Rec(g~pY);+;SyktSH2_
     foreach(@rabm) {
       if(!($_ eq $rabm[0])) { $out .= $_."_"; }    #;SyktSH2_a          !! a tSH2
     }    
     chop($out);                            #;Syka_tSH2
     $out .= '(';              #;Syka_tSH2(
     $val =~ /\)/;             #tSH2   a!2,b~Y!3)
     $out .= $`.")";           #;Syka_tSH2(tSH2)    ;Reca_b(a!2,b~Y!3) 
     $val = $';                #;<->;Rec(g~pY!1).Syk(tSH2!1);kpS,;kmS 
  } ##while
     foreach $aft (keys %{$lis1h}) {        # Syk:tSH2
       if ( &inc_elt($aft,\@rabms) )  {     # @rabms=Syk:l Syk:tSH2
         @rabmn = @{$lis1h->{$aft}};        # 0, 2, 10 - $ind string rules
         if ( &inc_elt($ind,\@rabmn) )  {
           delete $rdep{$key};              #  Syk:l
           if ( $value =~ /;\+;.*->;/ ) {   #;Reca_g(g~pY);+;SyktSH2_l(tSH2);<->
             if ( ($rlig{$aft} > 0)&&($lig==0)  ) {    # 2
               $lig = 1;                     # dalee $K(s)
             } else {                  #add  #;Reca_g(g~pY);+;SyktSH2_l(tSH2);<->
               @re = split(/\:/,$aft);        #Syk:tSH2
               $tei1 = $re[0].$re[1];        #SyktSH2
               @re = split(/;\+;/,$out);     #;Reca_g(g~pY) !!  SyktSH2_l(tSH2);<
               $out = $re[0].";+;".$re[1];   #;$Reca_g(g~pY);+;SyktSH2_l(tSH2);<->
             }
             --$rlig{$aft};                    # 2-1=> 1
             if(!($rlig{$aft})){ $lig = 0; }   # $lig=0
           }
           if ( $value =~ /->;.*;\+;/ ) {    #;Reca_g(g~pY);+;SyktSH2_l(tSH2);<->
             if ( ($rlig{$aft} > 0)&&($lig==0)  ) {    # 2
               $lig = 1;                     # dalee $K(s)
             } else {                  #add  #;Reca_g(g~pY);+;SyktSH2_l(tSH2);<->
               @re = split(/\:/,$aft);        #Syk:tSH2
               $tei1 = $re[0].$re[1];        #SyktSH2
               @re = split(/;\+;/,$out);     #;Reca_g(g~pY) !!  SyktSH2_l(tSH2);<
               $out = $re[0].";+;".$re[1];   #;$Reca_g(g~pY);+;SyktSH2_l(tSH2);<->
             }
             --$rlig{$aft};                    # 2-1=> 1
             if(!($rlig{$aft})){ $lig = 0; }   # $lig=0
           }
         }
       }
     } ##foreach %lis1h
   $val = $out.$val;
 } ##key %dpp_site
 if (!$fl) {  return(undef); } # is not ;S(site1...
 push(@$li,$val);
 if ($lig) { goto mlig; } 
}
#r_mac-end

#pre_obs1-begin
sub pre_obs1{
my ($nm_site, $nm2_site, $param_prefix, $skf, $dpp_site, $lis1h) = @_;
 my ($ii);
 my (@lis1);

 $obserfile= ">macr_${param_prefix}.obser";  # $obserfile= ">macr_${param_prefix}.obser";
 open (WFILEobser, $obserfile) or die "Can't open $obserfile: $!\n";
 print WFILEobser "begin observables\n";

 @lis1 = ();
 &del_blank(\@obser1vable,\@lis1);     #-into observ--delete blank e.t.

 print WFILErec " FULL set of observables (n/n pre_rules)\n";
 foreach (@lis1) {                    # print observables(tests)
   print WFILErec " $_ ";
   print WFILErec "\n";
 }
 &trans_obs($dpp_site,$lis1h,\@lis1,$skf);   #---transform micro to macro(OBSERVABLES)
 print WFILEobser "end observables\n";
 $ii=1;
 foreach( @gene1rate ) {
   if ( $_ =~ /^generate_network/) {  $gener1 = $_; }
   if ( $_ =~ /^simulate_ode/ )    {    $simul1 = $_; $ii--; }
 }
  print WFILEobser "$gener1"; 

close(WFILEobser);

                  #    ---------  SPECIES2-------------

 $spec2file= ">macr_${param_prefix}.spec2";  # $spec2file= ">macr_${param_prefix}.spec2";
 open (WFILEspec2, $spec2file) or die "Can't open $spec2file: $!\n";
 print WFILEspec2 "begin species\n";          # *.spec2

 @lis1 = ();
 &del_blank(\@species2,\@lis1);     #-into species2--delete blank e.t.

 print WFILErec " FULL set of species2 (n/n pre_rules)\n";
 foreach (@lis1) {                 # print species2(tests)
   print WFILErec " $_ ";
   print WFILErec "\n";
 }

 &hash_skf(\@lis1,$skf,$nm2_site,"species2");
 &trans_specie($dpp_site,$lis1h,\@lis1);    #---transform micro to macro(SPECIES)
 print WFILEspec2 "end species\n";          # *.spec2

 close(WFILEspec2);

}
#pre_obs1-end

#obs_mac0-begin
sub obs_mac0{
my ($comp, $dpp_site, $lis1h, $skf) =  @_;
 my ($key, $key2, $key3, $out, $value, $no_complex, $lin_mac, $ii, $jj, $ij);
 my (@rabm, @rab2, @re, @scaf, @rskf, @rabms);
 my ($sit2, $rnam);
                                        # %skf S:A site2
  $lin_mac = "";
  @scaf = split(/\./,$comp);            # S A  from S.A
 for ($ii=0; $ii<=$#scaf; $ii++) {
  for ($jj=0; $jj<=$#scaf; $jj++) {
    if ((!$#scaf)||($scaf[0] eq $scaf[1])) {    # one scaf  Syk or Rec.Rec
      @rskf=();                         # no sites link
    } else {
      $ij = $scaf[$ii].":".$scaf[$jj];  # S:A  A:S  A:A
      if ( !(defined($$skf{$ij})) )  { next; }
      @rskf = @{$skf->{$ij}};           # S:A  site2
    }
      @rabm=();
      @rab2=();
      $no_complex = 0;
      $key = $scaf[$ii];                #S
      $out = $key;                      #S   
      keys %{$dpp_site};                # begin hash
   while (($key2,$value) = each (%{$dpp_site}))  { #S:site4 site1 site3 site4
    ($key3,undef) = split(/\:/,$key2);   #  S  
    if ( $key eq $key3 ) {              #  S S 
       @rab2 = sort @{$value};               #  site1 site3 site4 
       if (!(@rabm)){                   #
         @rabm = @rab2                  #  site1 site3 site4  rabm=activnye_sites is not
       };
       @rabms = ();
       foreach ( @rab2) { push(@rabms,$key3.":".$_); }  # @rabms=S:site1 S:site3 S:site4

       if ( !(@rskf) ) {                # site1 site2
         if (&inc_set(\@rabm,\@rab2)) { #  rab2=site1 site3 site4 include rabm=?
           foreach(@rab2) {             #  rab2=site1 site3 site4
             $out .= $_."_";            # if(!($_ eq $rnam)) { $out ...= "_".$_; }    #  Ssite1_site3_site4
           }
           chop($out);                  # $out .=join("_",@rab2);      #Reca_b   !!
           ++$no_complex;
           last;
         }
       }
       if ( &inc_set(\@rskf,\@rabm) ) {      # site2 include site1 site3 site4%skf S:A site2
         if (&inc_set(\@rabm,\@rab2)) {      # rab2=456 include rabm=1234 ?
           foreach(@rab2) {
             $out .= $_."_";                 #if(!($_ eq $rnam)) { $out .= "_".$_; }    #;SyktSH2_a  
           }
           chop($out);                       # $out .=join("_",@rab2);      #Reca_b   !!
           ++$no_complex;
           last;
         }
       } else {
         @rabm = ();
       } ##if site2
    } ##if $key eq $key3
   } ##while
   if ($no_complex) {                 # there is no_complex R@rab2
     $lin_mac .= $out;                #Reca_b     Ssite1_site3_site4    OLD R1_2(1,0).
   } else {                           # it is complex  R.K
     if ($#scaf) {
       if ($lin_mac) {
          $lin_mac .= ".".$key;       #Ssite1_site3_site4.A    OLD R1_2(1,0).
       } else {
          $lin_mac .= $key;           #Ssite1_site3_site4          OLD R1_2(1,0).
       }
     } else{   
       $lin_mac .= $key;              #Lyn                     OLD R1_2(1,0).
     }
   } ##else
   if ( $scaf[0] eq $scaf[1] ) {
     return($lin_mac.".".$lin_mac);   # Rec.Rec
   }      
  } ## for jj     OLD foreach @scaf
 } ## for ii     OLD foreach @scaf
 return($lin_mac);
}
#obs_mac0-end

#obs_mac-begin
sub obs_mac{
my ($comp, $dpp_site, $lis1h, $mol, $mol_form, $skf) =  @_;              
 my ($key, $key2, $key3, $out, $zout, $value, $jj, $ii,$nrabm,$rscaf,$i1);
 my ($no_form, $lform, $hform, $zform, $mac, $sit,$no_dep);
 my (@rabm, @irabm, @rab2, @re, @scaf, @rabms, @pere, @obed);
 my ($sit2, $rnam);
 my (%dep_del);                        #Molecules     RecLig Lig(l1!1,l2).Rec(a1!1,b~y,g~Y)
  @scaf = split(/\./,$comp);           #Lig(l1!1,l2).Rec(a1!1,b~y,g~Y)
  foreach (@scaf) {  $rscaf = $_;      #Lig(l1!1,l2)
   if (!($_ =~ /\(/)) { $$mol .= &obs_mac0($_, $dpp_site, $lis1h, $skf)."."; next;   } 
   @rabm=();
   @rab2=();
   keys %{$dpp_site};                  # begin hash
   ($key,$mac) = split(/\(/,$_);       #Lig   l1!1,l2);   Rec  a!1,b~Y,g~Y)
   $mac =~ /\)/;                       #l1!1,l2);              a!1,b~Y,g~Y)
   $sit = $`;                          #l1!1,l2;               a!1,b~Y,g~Y
   @re = split(/\,/,$`);                #l1!1 l2;               a!1 b~Y g~Y
   $jj=0;
   @rabm = @re;                        #l1!1 l2;               a!1 b~Y g~Y
   foreach(@rabm) { $_ =~ s/[~!].*//;} #l1 l2;                 a b g
   $out = $key;                        #Lig;                   Rec
   $no_dep = 1;                        #Lig is not into dep;   Rec is dep
   $no_form = 0; @pere = ();
   while (($key2,$value) = each (%{$dpp_site}))  { #Rec:b a b
    ($key3,undef) = split(/\:/,$key2);  # Rec    
    if ($key eq $key3) {               # Lig Rec   else while; Rec Rec
       @rab2 = sort @{$value};         #                       a b    
       $no_dep = 0;                    #                       Rec is into dep
       @rabms = ();

       foreach ( @rab2) { push(@rabms,$key3.":".$_); }  #      @rabms=Rec:a Rec:b
       if (&inc_set(\@rabm,\@rab2)) {  # rab2=a b include @rabm a b g
         foreach(@rab2) {
           $out .= $_."_";              # if(!($_ eq $rnam)) { $out ...= "_".$_; }    #;SyktSH2_a 
         }
         chop($out);                    # Reca_b       
          ++$no_form;                   # it is not formala
         last;          # out from dpp_site
       } else {
         if (@pere) { &spere(\@pere,\@obed,\@rab2); } else {@pere = @rab2; @obed=@rab2;}
       }
    } ## if key eq key3
   } ##while dpp_site
   if ($no_dep) {         # Lig(l1!1,l2) is not dep
     $out .= '(';         # Lig(
     $out .= $sit;        # Lig(l1!1,l2 
     $out .= ').';        # Lig(l1!1,l2).
     $$mol .= $out;       # Lig(l1!1,l2).
   } else {               #                 Rec there is dep
     if ($no_form) {                      # there is no_form R@rab2
       $out .= '(';                             
       foreach(@re){ $out .= $_.','; }    # Rec(a!1,
       chop($out);                               
       $out .= ').';                      # Rec(a!1).      
       $$mol .= $out;                     # 
     } else { $hform = ''; $zform = ''; $lform = ''; $nrabm = @rabm;   # it is formulae
       for ($ii=0;$ii<$nrabm;$ii++){$irabm[$ii]=0;}  $ii =0; $i1=0; %dep_del =();
       while(!($i1)) {  if ($ii > 2*$nrabm) { print WFILErec "INVALID GROUP OBSERVABLES $rscaf sites=@rabm"; last; }
        &h_dep($dpp_site, \%dep_del, \@rabm, \@rab2, $out);   #@rab2= a b ---prepare @rab2
        $out .=join("_",@rab2)."(";           # Reca_b(;            Reca_g(
        foreach(@rab2){                            # a b;                a g
          if ($jj = &inc_elt($_,\@rabm)) {         # rabm = a b g;       rabm = a b g
            $irabm[$jj-1] = 1;                     # irabm = 1 1 0;      irabm = 2 1 1
            $out .= $re[$jj-1].',';               # Reca_b(a!1,b~Y,;    Reca_g(a!1,g~Y,
          } else {
            $out .= "*,";                         # Reca_b(*,
          }
        }
        chop($out);                                # Reca_b(a!1,b~Y      Reca_g(a!1,g~Y
        $out .= ")";                               # Reca_b(a!1,b~Y)     Reca_g(a!1,g~Y)
        if ($$mol) {$hform .= $$mol.$out."&";      # Lig(l1!1,l2).Reca_b(a!1,b~Y)&    Reca_g(a!1,g~Y)
        } else {$hform .= $out."&";}               # Reca_b(a!1,b~Y)&    Reca_g(a!1,g~Y)
        $i1=1; for (@irabm) { $i1*=$_; }
        $out = $key;                               # Rec
        if ( $ii > 0 ) {
            $zout = $out;                          # Rec
            $zout .=join("_",@rab2)."(";           # Reca_g(    znamenatel'
            foreach(@pere){                        # a;               
              if ($jj = &inc_elt($_,\@rabm)) {         # rabm = a b g;       rabm = a b g
                $zout .= $re[$jj-1].",";               # Reca_b(a!1,;        Reca_g(a!1,g~Y,
              }
            }##foreach
            chop($zout);                           # Reca_g or Reca_b(a!1) 
            if (@pere) {$zout .= ")";   }
            if ($$mol) {$zform .= $$mol.$zout."&";  # Lig(l1!1,l2).Reca_b(a!1,b~Y)&    Reca_g(a!1,g~Y)
            } else {$zform .= $zout."&";}           # Reca_g(a!1)&  
        } ## $ii > 0        
        $ii++;
       } ##while rabm (activ sites)
      chop($hform); chop($zform);
      $lform .="[".$hform."]/[".$zform."].";
      $$mol = "";
     } ##else no_form
   } ##if no_dep	
  } ## foreach @scaf
    if ($lform) { $$mol_form .= $lform."#"; }
    if ($$mol) { chop($$mol); }
  return;
}
#obs_mac-end

#trans_specie-begin
sub trans_specie{                   #---transform micro to macro(SPECIES)
my ($dpp_site, $lis1h, $lis1) = @_;   
 my ($key, $skey, $rr, $key_dep, $key_obs, $value, $lin_mac, $key_d, $rak, $ind);
 my ($stt, $sit, $is_dep, $sit2, $rnam);
 my (@re, @ra, @ram, @rabms);
 my (%spp, %rah);

   foreach (@{$lis1})  {              # line from species ONE RR-MOLECULES
    %rah=();
    %spp=();
    @ra=();
    @ram=();
    $is_dep = 0;
    $stt = $_;                             #;Syk(tSH2,l~Y,a~Y);Syk_tot  ;K(s);K_tot
      (undef,@re) =split(/\;/,$stt);       #Syk(tSH2,l~Y,a~Y) Syk_tot   #OLD Rr(1,0,1,0) 386 or Recstart
      $rr=$re[0];                          #Syk(tSH2,l~Y,a~Y)           S(site1,site2~Y,site3~Y,site4~Y) OLD Rr(1,0,1,0)=> $rr
        if( !($rr =~ /\(/ )) {             # search (
            $stt =~ s/;/ /g;               ## not ( ; to blank
            print WFILErec "$stt\n";       
            print WFILEspec2 "$stt\n";     ### S S_tot
            next;
        } else {                # it is (     
          $rr =~ /\(/;                          #Syk(tSH2,l~Y,a~Y)   S(site1,site2~Y,site3~Y,site4~Y) OLD 1,0,1,0)
          $key_obs = $`;                        #Syk                 S OLD Rr
          $sit = $';                            #tSH2,l~Y,a~Y) 
          foreach $key_dep ( keys %{$dpp_site}) {   #Rec:b
            $skey="";
            $value = $dpp_site->{$key_dep};         #a b             site1 site3 site4
            ($key_d,undef) = split(/\:/,$key_dep);  #Rec:b               S:site4 S=>$key_d  OLD R=>$key_d
            if ( $key_obs eq $key_d) {              #Syk Rec             S S
                @ra = split(/\,/,$sit);             #tSH2,l~Y,a~Y)      site1,site2~Y,site3~Y,site4~Y) OLD @(1 0 1 0))
                chop($ra[$#ra]);                    #tSH2 l~Y a~Y       site1 site2~Y site3~Y site4~Y OLD @(1 0 1 0)
                @ram = @ra;                         #@ram=tSH2 l~Y a~Y  site1 site2~Y site3~Y site4~Y OLD @(1 0 1 0)
                foreach (@ra) {  s/[!~].*//;   }    #@ra=tSH2 l a       site1 site2 site3 site4
                  $rak = "";
   @rabms = ();
   foreach ( @{$value}) { push(@rabms,$key_d.":".$_); }  # @rabms=Syk:a Syk:tSH2
     $skey = '';                                # old=>$skey = $rnam; 
     foreach(sort @{$value}) {
       $skey .=$_."_";                          # if(!($_ eq $rnam)) { $skey .=$_."_"; }   # a tSH2
     }
                foreach (sort @{$value}){        #    $skey .= $_."_";              # key =a_tSH2_    site1_site3_site4_ OLD 1_1_0_
                  $ind = &inc_elt($_,\@ra)-1;
                  $rak .= $ram[$ind].',';       # rak =tSH2,a~Y,  site1,site3,site4, OLD 1,1,0,
                }
                $skey = $key_obs.$skey;         # skey=Syka_tSH2  Ssite1_site3_site4_ OLD 1_1_0_Rr
                chop($rak);                     # rak =tSH2,a~Y   site1,site3~Y,site4~Y OLD 1,1,0
                chop($skey);                    # skey=Syka_tSH2     Ssite1_site3_site4
                $rah{$skey} = $rak;             # %rah{Syka_tSH2}=tSH2,a~Y Syk(Ssite1_site3_site4 site1,site3~Y,site4~Y) OLD (1_1_0_Rr 1,1,0)
                if ($re[1] =~ /^\D/){           # Syk_tot     S_tot  or 386
                  $spp{$skey} += $para1ms{$re[1]};  # summa start_species
                } else {
                  $spp{$skey} +=$re[1];             # summa start_species
                }
               $is_dep = 1;
                                 #Syk = Syk      K(s) K_tot
            } ##if                 
          }  ##key_dep
          if ( $is_dep ) {
               foreach $key ( keys %spp ){            # key=Syka_tSH2   Ssite1_site3_site4 OLD 1_1_0_Rr
                 $lin_mac = $key;                     # Syka_tSH2       S        OLD Rr
                 $lin_mac .= '(';                     # Syka_tSH2(      Ssite1_site3_site4( OLD Rr1_3_4(
                 $lin_mac .= $rah{$key}.');';         # Syka_tSH2(tSH2,a~Y);   Ssite1_site3_site4(site1,site3~Y,site4~Y) OLD Rr1_3_4(1,1,0);
                 $lin_mac .= $spp{$key};              # Syka_tSH2(tSH2,a~Y);summa
                 $lin_mac =~ s/;/ /g;                 # ; to blank
                 print WFILErec $lin_mac;             # Rr1_3_4(1,1,0) Rec_start
                 print WFILErec "\n";
                 print WFILEspec2 $lin_mac;           # Rr1_3_4(1,1,0) Rec_start
                 print WFILEspec2 "\n";
               } ##key
          } else {    # it is not into dep
            $stt =~ s/;/ /g;               # Lig(l,l) L_tot
            print WFILErec "$stt\n";
            print WFILEspec2 "$stt\n";     ### Lig(l,l) L_tot    S S_tot
            next;
          }
        } #else  it is (
     $is_dep = 0;
  } #lis1
}
#trans_specie-end

#get_line-begin
sub get_line{
    my $line="";
    while($line= shift(@$file_dat)){
      ++$line_number;
      chomp($line);
      $line=~ s/\#.*$//; # remove comments
      next unless $line=~/\S+/; # skip blank lines
      while ($line=~ s/\\\s*$//){
	++$line_number;
	my $nline= shift(@$file_dat);
	chomp($nline);
	$nline=~ s/\#.*$//; # remove comments
	$line.= $nline;
	next unless (!@$file_dat);
      }
      last;
    }
    return($line);
}
#get_line-end

#read_block_array-begin
sub read_block_array{
    my $name= shift;
    my @array=();

    my $got_end=0;
    while($_=get_line()){
      # Look for end of block or errors
      if (s/^\s*end\s+//){
	my $ename= $_;
	$ename=~ s/\s*$//;
	$ename=~ s/\s+/ /g;
        if ($ename ne $name){
	  return([],errgen("end $ename does not match begin $name"));
	} else {
	  $got_end=1;
	  #print "end at $line_number\n";
	  last;
	}
      }
      elsif (/^\s*begin\s*/){
	return([],errgen("begin block before end of previous block $name"));
      }
      # Add declarations from current line
      push @array, [($_, $line_number)];
      #print "$_ $line_number\n";
    }

    if (!$got_end){
      return([],errgen("begin $name has no matching end $name"));
    }

    return([@array]);
}
#read_block_array-end

#errgen-begin
sub errgen{
    my $err= shift;
    my $lno= (@_) ? shift : $line_number;
    $err=~ s/[*]/\*/g;
    my $reterr= sprintf "%s\n  at line $lno of file $file", $err;
    return($reterr);
}
#errgen-end

#version-begin (substituted)
sub version{
return $base_model->version();
}
#version-end (substituted)

#L_AiAn_BiBn-begin
sub L_AiAn_BiBn{     #  (\%croc,\%ms,\@rabn,\@rabi,\@reac,\$n_adds,\%dubl);
my ($croc,$ms,$rabn,$rabi,$reac,$n_adds,$dubl) = @_;
my ($nnn,$nni,$s3n,$s3i,$an,$bn,$ai,$bi,$nkn,$nki,$ln);
my (@rabm);
  ($nnn,$an,$bn,$s3n,$nkn) = @$rabn;
  ($nni,$ai,$bi,$s3i,$nki) = @$rabi;
  $tnnn = $nnn;
  $tnni = $nni;
if ( $nkn eq $nki ) {
  if ( $bi==$bn && !(exists($$dubl{$bi})) && cros($croc,$ms,$ai,$an) ) {
    &add_ts3n($an, $bn);                # add s3n into @$ts3n{$bn}
    $ln = $an.",".$bn." ".$s3n.",".$bn." ".$nkn;
    $$reac[$nnn-1] = sprintf "%5d %s\n", $nnn, $ln;
    $$rabn[3] = $s3n.",".$bn;
    $$n_adds++; $$dubl{$bi} = 1;
  }
  if ( $bi==$an && !(exists($$dubl{$bi})) && cros($croc,$ms,$ai,$bn) ) {
    &add_ts3n($an, $bn);                # add s3n into @$ts3n{$bn}
    $ln = $an.",".$bn." ".$s3n.",".$an." ".$nkn;
    $$reac[$nnn-1] = sprintf "%5d %s\n", $nnn, $ln;
    $$rabn[3] = $s3n.",".$an;
    $$n_adds++; $$dubl{$bi} = 1;
  }
  if ( $ai==$an && !(exists($$dubl{$ai})) && cros($croc,$ms,$bi,$bn) ) {
    &add_ts3n($an, $bn);                # add s3n into @$ts3n{$bn}
    $ln = $an.",".$bn." ".$s3n.",".$an." ".$nkn;
    $$reac[$nnn-1] = sprintf "%5d %s\n", $nnn, $ln;
    $$rabn[3] = $s3n.",".$an;
    $$n_adds++; $$dubl{$ai} = 1;
  }
  if ( $ai==$bn && !(exists($$dubl{$ai})) && cros($croc,$ms,$bi,$an) ) {
    &add_ts3n($an, $bn);                # add s3n into @$ts3n{$bn}
    push(@ts3n,$s3n);
    $ln = $an.",".$bn." ".$s3n.",".$bn." ".$nkn;
    $$reac[$nnn-1] = sprintf "%5d %s\n", $nnn, $ln;
    $$rabn[3] = $s3n.",".$bn;
    $$n_adds++; $$dubl{$ai} = 1;
  }
} ##if nkn
}
#L_AiAn_BiBn-end

#cros-begin
sub cros{                   #  cros(\%croc,\%ms,$ai,$an)
my ($croc,$ms,$ai,$an) = @_;
my ($li1,$li2,$na1,$na2,$teil1,$teil2,$ii,$jj,$tei1out,$tei2out);
my (@rr1,@rr1out,@sit1,@rr2,@rr2out,@sit2,@rr2mac,@am,@bm,@aonly,@par1,@par2);
my (%rr1mac,%rr2mac,%seen);

STEP1:
   @par1 = ();   @par2 = ();

   @rr1 = (); %rr1mac = ();
   $li1 = $species[$ai-1];   #Sykl_tSH2(l~Y,tSH2)
   @rr1 = split(/\./,$li1);   # NABOR [Lig Sykl_tSH2(l~Y,tSH2)] micro and macro belkov species1 
   foreach (@rr1) {
     $_ =~ /\(/;      # NABOR [Sykl_tSH2 Reca_b] macro-name into  @rr1mac
     if ( exists($$ms{$`}) ) { $rr1mac{$_} = $`; }  #Syka_tSH2(a~Y,tSH2)] Syka_tSH2
   }

   @rr2 = (); %rr2mac = ();
   $li2 = $species[$an-1];   #Syka_tSH2(a~Y,tSH2)
   @rr2 = split(/\./,$li2);   # NABOR [Lig Sykl_tSH2(l~Y,tSH2)] micro and macro belkov species2
   foreach (@rr2) {
     $_ =~ /\(/;      # NABOR [Syka_tSH2 Reca_g] macro-name into @rr2mac
     if ( exists($$ms{$`}) ) { $rr2mac{$_} = $`; }   #Sykl_tSH2(l~Y,tSH2)] Sykl_tSH2
   }

   while ( ($teil1,$na1) = each(%rr1mac) ) {      # pary (Q1 Q2) (R1 R2)...
     while ( ($teil2,$na2) = each(%rr2mac) ) {
       if ( $na1 ne $na2 && $$ms{$na1} eq $$ms{$na2} ) {
         push(@par1,$teil1);  push(@par2,$teil2);
       }
     }
   }
STEP2x3:
#   if ( !(defined(@par1)) ) {
   if ( !(@par1) ) {
     return(0);            # It's not odnorodny
   } else {                # It's odnorodny
     for ( $jj=0; $jj<=$#par1; $jj++) {               # pary macro-name (Q1 Q2) (R1 R2)...
       $teil1 = $par1[$jj]; $teil2 = $par2[$jj];      # pary macro-name (Q1 Q2) (R1 R2)...
       @rr1out = (); @rr2out = ();
       $na1 = $rr1mac{$teil1};    # Syka_tSH2
       $na2 = $rr2mac{$teil2};    # Sykl_tSH2
       @am = @{$$croc{$na1}};     # a b
       @bm = @{$$croc{$na2}};     # a g
       &del_set(\@am,\@bm,\@aonly);
##       %seen = ();          # search elements from $am which not into @bm
##       @seen{@am} = ();     # creat tabl view  dependence sites
##       delete @seen{@bm};   # 
##       @aonly = keys %seen; # only keys (am - bm) = neobshie sites(ab - ag => b)
       delsites(\@rr1,$teil1,\@aonly,\@rr1out,\$tei1out);
       $tei1out =~ s/$na1//;

       @am = @{$$croc{$na1}};
       @bm = @{$$croc{$na2}};
       &del_set(\@bm,\@am,\@aonly);
##       %seen = ();          # search elements from $bm which not into @am
##       @seen{@bm} = ();     # creat tabl view  dependence sites
##       delete @seen{@am};   #
##       @aonly = keys %seen; # only keys (bm - am) = neobshie sites(ag - ab => g)
       delsites(\@rr2,$teil2,\@aonly,\@rr2out,\$tei2out);
       $tei2out =~ s/$na2//;
       if ( $tei1out eq $tei2out ) {
         print WFILErec "Hom_spes: reac=$tnni,$tnnn spes=$ai,$an $teil1 <=> $teil2\n";
         return(1);
       }     # It's odnorodny
     } ##for $jj pary
     return(0);
   } ##if step2
}
#cros-end

#delsites-begin
sub delsites{             #  delsites(\@rr1,$teil1,\@aonly,\@rr1out,\tei1out);
my ($rr1,$teil1,$aonly,$rr1out,$tei1out) = @_;  #nabor_belkov  Reca_b(a,b~Y)   b
my ($ii,$jj,$me,$tei,$sit,$ind0,$ind1,$ind2,$ou1);
my (@met,@rr1sor,@si,@sisor,@keyrr1s);
my (%rr1s);

  @$rr1out = (); push(@$rr1out,@$rr1);
  for ($ii=0; $ii<=$#$aonly; $ii++) {    # b-site
    @met = ();
    $sit = $$aonly[$ii];            # g
    $tei = $teil1;                  #Reca_g(g~pY!3,a!1,t~..) or Reca_g(a!1,g~pY!3,t~..)
    $tei =~ /\($sit|\,$sit/;        # (g  or ,g
    $ind0 = $`;                     #Reca_g(a!1    or Reca_g
    $ind1 = $&;                     # (g  or ,g
    $ind2 = $';                     # ~pY!3,t~..) or ~Y,..  or ,t~.. or !3,t~. or !3)
    $ind2 =~ /\,|\)/;               #$`= ~pY!3  or ~Y or'' or !3 or !3
                                    #$'= t~..) or t~..) or t~..)  or''
    if ( "(".$sit eq $ind1 ) {
      $tei = $ind0."(".$'; 
      if ( $`) {
        (undef,@met) = split(/!/,$`);    #~pY  3   metki 3 i t.d.
      }   #~pY  3   metki 3 i t.d.
    } else {
      if ( $`) {
        (undef,@met) = split(/!/,$`);
      }   
      if ( $') { $tei = $ind0.",".$'; } else { $tei = $ind0.")"; }
    }
    $jj = 0;
    while ( $$rr1out[$jj] ne $teil1) { $jj++; }        # found teil1
    $$rr1out[$jj] = $tei;                              #
    $teil1 = $tei;                                     #

    while ( $#met >= 0 ) {            # psevdo_recurs
      $me = pop(@met);                   # 4
      for ($jj=0; $jj<=$#$rr1out; $jj++) {
        $tei = $$rr1out[$jj];            #Lig(l!1,l!4)
        if ( $tei =~ /!$me/ ) {          # found !4
          selectmet(\@met,$tei,$me);     #
          $$rr1out[$jj] = undef;
          last; ## jj
        } ##if
      } ## for $jj
    } ## while @met
  } ##for $ii b-sites

  for ($jj=0; $jj<=$#$rr1out; $jj++) {
    $ind0 = $$rr1out[$jj];
    $ind0 =~ s/!.+?[\,\)]/#,/g;           # !x12!5, or !x12!5)=> #, 
    $rr1s{$ind0} = $jj;
  }
  @keyrr1s = sort (keys %rr1s);            # SORTING keys %rr1s= Syka_tSH2(a~Y,tSH2#,
  foreach $jj (@keyrr1s) {
    push(@rr1sor,$$rr1out[$rr1s{$jj}]);                 # SORTING belkov
  }
  @$rr1out = ();  $ou1 = '';
  foreach $jj (@rr1sor) {
    if ( $jj) {                            #Syka_tSH2(a~Y,tSH2!3)
      ($ind0,$ind1) = split(/\(/,$jj);     #Syka_tSH2   a~Y,tSH2!3)
      chop($ind1);                         #  a~Y,tSH2!3
      @si = split(/,/,$ind1);              #  a~Y   tSH2!3
      @sisor = sort @si;                   # SORTING sites
      $ind2 = $ind0."(".join(",",@sisor).")";    #Syka_tSH2(a~Y,tSH2!3)
      $ou1 .= $ind2.".";                   #Reca_g(a!1,g~pY!3).Sykl_tSH2(l~Y,tSH2!3)
      push(@$rr1out, $ind2 );
    } ##if jj
  } ## foreach jj
  chop($ou1);
  $jj = 0;
  while ( $ou1 =~ /!/ ) {
    $jj++;
    $ou1 =~ /!.+?[\,\)\!]/;      #$&= !xx.., or !xx..) or !xx..!
    $ind2 = $&;
    chop($ind2);
    $ou1 =~ s/(?:$ind2)/#$jj/g;
  }
  $$tei1out = $ou1;
}
#delsites-end

#add_ts3n-begin
sub add_ts3n{                 # add_s3n($an,$bn);
my  ($an, $bn) = @_;          # add an.bn into %ts3n
    if ( !(exists($ts3n{"$an.$bn"})) ) {
      $ts3n{"$an.$bn"} = 1;
    }
}
#add_ts3n-end

#R_AiAn_BiBn-begin
sub R_AiAn_BiBn{     #(\%croc,\%ms,\@rabn,\@rabi,\@reac,\$n_subtracts,\%dubl);
my ($croc,$ms,$rabn,$rabi,$reac,$n_adds,$dubl) = @_;
my ($nnn,$nni,$s1n,$s1i,$an,$bn,$ai,$bi,$nkn,$nki,$ln);
  ($nnn,$s1n,$an,$bn,$nkn) = @$rabn;
  ($nni,$s1i,$ai,$bi,$nki) = @$rabi;
  $tnnn = $nnn;
  $tnni = $nni;
if ( $nkn eq $nki ) {
  if ( $bi==$bn && !(exists($$dubl{$bi})) && cros($croc,$ms,$ai,$an) ) {
    $ln = $s1n." ".$an." ".$nkn;
    $$reac[$nnn-1] = sprintf "%5d %s\n", $nnn, $ln;
    $$n_adds++; $$dubl{$bi} = 1;
  }
  if ( $bi==$an && !(exists($$dubl{$bi})) && cros($croc,$ms,$ai,$bn) ) {
    $ln = $s1n." ".$bn." ".$nkn;
    $$reac[$nnn-1] = sprintf "%5d %s\n", $nnn, $ln;
    $$n_adds++; $$dubl{$bi} = 1;
  }
  if ( $ai==$an && !(exists($$dubl{$ai})) && cros($croc,$ms,$bi,$bn) ) {
    $ln = $s1n." ".$bn." ".$nkn;
    $$reac[$nnn-1] = sprintf "%5d %s\n", $nnn, $ln;
    $$n_adds++; $$dubl{$ai} = 1;
  }
  if ( $ai==$bn && !(exists($$dubl{$ai})) && cros($croc,$ms,$bi,$an) ) {
    $ln = $s1n." ".$an." ".$nkn;
    $$reac[$nnn-1] = sprintf "%5d %s\n", $nnn, $ln;
    $$n_adds++; $$dubl{$ai} = 1;
  }
} ##if nkn
}
#R_AiAn_BiBn-end

#selectmet-begin
sub selectmet {             #  selectmet(\@met,$tei,$me); 
my ($met,$tei,$me) = @_; 
my ($te,$te1,$ii,$ch1,$ch2);
my (@rr,@mm);
    @rr = split(/,/,$tei);          #Reca_g(a!1   g~pY!3)
    ($ch1,$ch2) = split(/!/,$rr[0]); #Reca_g(a   1  or ..(...3)
    if ($ch2) {
      if ( $ch2 =~ /\)$/ ) { chop($ch2); }
      if ( $ch2 ne $me) { push( @{$met}, $ch2); }
      $ch1 =~ s/\(.//;               #Reca_g
      $ch1 .= "(";                   #Reca_g(
    }
    $te1 = $ch1;                     #Reca_g(a..)   or Reca_g(
    
    for ($ii=1; $ii<=$#rr; $ii++) {  
      $te = $rr[$ii];                #g~pY!3) or g~py!2 
      ($ch1,$ch2) = split(/!/,$te);  #g~pY  3)   or    g~py  2  
      if ($ch2) {
        $ch2 =~ s/\)$//;             # 3
        if ( $ch2 ne $me) { push( @{$met}, $ch2); }
      }
    } ##for $ii
}
#selectmet-end

#h_dep-begin
sub h_dep{                                          # @rabm = a b g  $out=Rec
my  ($dpp_site, $dep_del, $rabm, $rab2, $out) = @_; # @rab2 - output
 my ($n_in, $key2, $key, $key_mi, $value, $jj);
 my (@ind_var, @act_sites);

  $key_mi = "";             # @rabm = a b g;        a b g   
  @$rab2 = ();              #  %dep_v=();

  while (($key,$value) = each (%{$dpp_site}))  {    # Rec:b  a b;    Rec:g a g
   if ( !($$dep_del{"0".$key}) ) {
     ($key2,undef)= split(/\:/,$key);             # Rec 
     if ($out eq $key2) {                        # Rec Rec
      @ind_var = @{$value};                      # a b;                 a g
      $n_in = 0;
      $jj = 0;
      foreach (@{$rabm}){                        # a b g
        if (&inc_elt($$rabm[$jj],\@ind_var)) {    # a,b,g  a b;     a,b,g a g
          ++$n_in;                               # 2;              1
        }
        ++$jj;
      }##for                   # nige minimizaciy argymentov
      if ($n_in) {             # @{$dep_v{$key_mi}} =  @ind_var; # (Krist p197 and
        if (@$rab2) { if ( $#{$rab2} > $#ind_var) {@$rab2 = @ind_var;  $key_mi = $key; }   
        } else { @$rab2 = @ind_var; $key_mi = $key; }                # a b;             a g
      }
     } ##if out eq key2
   } ##ifdep_del
  } ##while dpp_site
  $$dep_del{"0".$key_mi}=1; 

return;
}
#h_dep-end

#########################
#########################

}

1;
##########################################################################################################################
