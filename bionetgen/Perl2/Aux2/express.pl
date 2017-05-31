#!/usr/bin/perl

use Data::Dumper;

$express= {
	   type=>'AND',
#	   type=>'OR',
#	   arg1=>{type=>'REF', arg1=>'a'},
	   arg1=>{type=>'NOT', arg1=>{type=>'REF', arg1=>'a'}},
   	   arg2=>{type=>'REF', arg1=>'b'}
	  };

$list= ['a','b', 'ab','abc', 'ac', 'c','bc'];

#print Dumper($express);
#print Dumper($list);

# WARNING: NOT doesn't work properly unless it's surrounded by parenthesis

#$string= "a AND (NOT c) AND b";
#$string= "NOT (a AND b AND c)";
#$string= "(a AND (NOT b)) OR (b AND (NOT a))";
#$string= "(a OR b) AND (NOT (a AND b))";
$string= "(a) AND (b)";
$express= read_express(\$string);
print Dumper($express),"\n";
print join(" ",@{eval_express($express, $list)}),"\n";

sub eval_express{
  my $express= shift;
  my $list= shift;

  my $type= $express->{type};
  my $arg1= $express->{arg1};
  my $arg2= $express->{arg2};
  if ($type eq 'AND'){
    return(eval_express($arg2, eval_express($arg1,$list)));
  } 
  elsif ($type eq 'OR'){
    my $list1= eval_express($arg1, $list);
    my $list2= eval_express($arg2, $list);
    return([@$list1,@$list2]);
  } 
  elsif ($type eq 'XOR'){
    my $list1= eval_express($arg1, $list);
    my $list2= eval_express($arg2, $list);
    my %remove=();
    for my $item (@$list1){
      $remove{$item}=1;
    }
    my @lnew=();
    for my $item (@$list2){
      push @lnew, $item if (!($remove{$item}));
    }
    return([@lnew]);
  }
  elsif ($type eq 'NOT'){
    my $list1= eval_express($arg1, $list);
    my %remove=();
    for my $item (@$list1){
      $remove{$item}=1;
    }
    my @lnew=();
    for my $item (@$list){
      push @lnew, $item if (!($remove{$item}));
    }
    return([@lnew]);
  } 
  elsif ($type eq 'REF'){
    my @lnew= ();
    for my $item (@$list){
      if ($item=~ /$arg1/){
	push @lnew, $item;
      }
    }
    return([@lnew]);
  }   
  else {
    return("");
  }
}
 
{
  my $open=0;

  sub read_express {
    my $string= shift;
    my $express="";
    my $expect_arg1=0;
    my $expect_arg2=0;
    while ($$string ne ""){
      my $nh={};
      $$string=~ s/^\s+//;
      if ($$string=~ s/^\(// ){
	++$open;
	$nh= read_express($string);
      }
      elsif ($$string=~ s/^AND\s+//){
	$nh->{type}= 'AND';
	die "Null first operand for AND" unless ($express ne "");
	$nh->{arg1}= $express;
	$express= $nh;
	$expect_arg2=1;
	next;
      }
      elsif ($$string=~ s/^OR\s+//){
	$nh->{type}= 'OR';
	die "Null first operand for OR" unless ($express ne "");
	$nh->{arg1}= $express;
	$express= $nh;
	$expect_arg2=1;
	next;
      }
      elsif ($$string=~ s/^XOR\s+//){
	die "XOR not yet implemented";
	$nh->{type}= 'XOR';
	die "Null first operand for XOR" unless ($express ne "");
	$nh->{arg1}= $express;
	$express= $nh;
	$expect_arg2=1;
	next;
      }
      elsif ($$string=~ s/^NOT\s+//){
	$nh->{type}= 'NOT';
	$express= $nh;
	$expect_arg1=1;
	next;
      }
      elsif ($$string=~ s/^([^\s\)]+)//){
	$nh->{type}='REF';
	$nh->{arg1}=$1;
      }
      elsif ($$string=~ s/^\)// ){
	--$open;
	die "Unmatched end parenthesis" if ($open<0);
	die "Empty parenthesis" if ($express eq "");
	return($express);
      }
      # print Dumper($nh),"\n";
      if ($express eq ""){
	$express= $nh;
      } 
      elsif($expect_arg1){
	$express->{arg1}=$nh;
	$expect_arg1=0;
      }
      elsif($expect_arg2){
	$express->{arg2}=$nh;
	$expect_arg2=0;
      }
      else{
	die "Expression without operator";
      }
    }
    if ($expect_arg1){
      die "Missing unary operand";
    }
    if ($expect_arg2){
      die "Missing binary operand";
    }
    die "Missing end parenthesis" unless ($open==0);
    return($express);
  }
}
