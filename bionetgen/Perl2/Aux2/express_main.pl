#!/usr/bin/perl

use FindBin;
use lib $FindBin::Bin;
use Param;
use ParamList;
use Expression;

$a1= Expression->new;
$a1->Type('NUM');
$a1->Arglist([5]);

$a2= Expression->new;
$a2->Type('NUM');
$a2->Arglist([12]);

#$a3= Expression->new;

$expr= Expression->new;
$expr->Type('-');
$expr->Arglist([($a1,$a2)]);

printf "a1 evaluates to %s\n", $a1->evaluate();
printf "a2 evaluates to %s\n", $a2->evaluate();

($val,$err) = $expr->evaluate();
if ($err){
  die $err;
}
printf "expr evaluates to %s\n", $val;

$string="45e-26";
if (($number=getNumber(\$string)) ne ""){
  printf "Number is %g\n", $number;
} else {
  printf "%s does not start with a valid number\n", $string;
}

$plist= ParamList->new();
$plist->set("a",5);
$plist->set("b",10);
$plist->set("c",-10);

#$estring="1.01e-2*2-a/b+c^2";
$estring="-a^2 + b";
$estring_sav=$estring;
$expr= Expression->new();
if ($err=$expr->readString(\$estring,$plist)){
  die $err;
}
printf "expr $estring_sav prints as %s\n", $expr->toString($plist);
($val,$err) = $expr->evaluate($plist);
if ($err){
  die $err;
}
printf "expr $estring_sav evaluates to %g\n", $val;

printf "Before assignment, a=%d\n", $plist->evaluate("a");
$estring="a=a+5";
$err= ($expr=Expression->new())->readString(\$estring,$plist);
printf "After assignment, a=%d\n", $plist->evaluate("a");

$estring="x=(-a+5)/(c-10)";
#$estring="x=5";
$estring_sav=$estring;
$expr= Expression->new();
if ($err=$expr->readString(\$estring,$plist)){
  die $err;
}
printf "expr $estring_sav prints as %s\n", $expr->toString($plist);
$val = $expr->evaluate($plist);
if ($expr->Err){
  die $expr->Err;
}
printf "expr $estring_sav evaluates to %g\n", $val;

$estring="y=100^(d=1/2) g";
$estring_sav=$estring;
$expr= Expression->new();
if ($err=$expr->readString(\$estring,$plist,'\s')){
  die $err;
}
printf "expr $estring_sav prints as %s\n", $expr->toString($plist);
$val = $expr->evaluate($plist);
if ($expr->Err){
  die $expr->Err;
}
printf "expr $estring_sav evaluates to %g\n", $val;

print $plist->writeBNGL();
print $plist->toString();

#sub getNumber(){
sub getNumber{
  my $string=shift;
  my $number="";

  # Decimal part
  if ($$string=~ s/^([+-]?\d+[.]?\d*)//){
    $number=$1;
  }
  elsif ($$string=~ s/^([+-]?[.]\d+)//){
    $number=$1;
  } else {
    return("");
  }

  # Exponent part
  if ($$string=~ s/^([DEFGdefg][+-]?\d+)//){
    $number.=$1;
  }
  return($number);
}

