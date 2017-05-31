#!/usr/bin/perl

use FindBin;
use lib $FindBin::Bin;
use IO::Handle;
use CartesianProduct;

use strict;
use warnings;



my $numbers = [1,2,3,4];
print STDERR "numbers: ", join( ',', @$numbers ), "\n";

my $letters = ['A','B','C'];
print STDERR "letters: ", join( ',', @$letters ), "\n";

my $marshes = ['Bob','sally','Rob','John'];
print STDERR "marshes: ", join( ',', @$marshes ), "\n";


print STDERR "build list of lists..\n";
my $lists = [];
push @$lists, ($numbers, $letters, $marshes);

my $all_items = [];
foreach my $list ( @$lists )
{
    foreach my $item ( @$list )
    {
        push @$all_items, $item;
    }
}
print STDERR "all_items: ", join( ',', @$all_items ), "\n";



print STDERR "initialize CartesianProduct..\n";
my $cartprod = CartesianProduct::new();
$cartprod->initialize( $lists );



print STDERR "iterate over elements..\n";
my $elem = [];
while ( $cartprod->getNext($elem) )
{
    print STDERR "elem: ", join( ',', @$elem ), "\n";
}

print STDERR "add more numbers..\n";
if ( $cartprod->update(0,[5,6]) )
{
    print STDERR "valid? ", $cartprod->validate(), "\n";
    print STDERR "successful update! continue iteration..\n";
    while ( $cartprod->getNext($elem) )
    {
        print STDERR "elem: ", join( ',', @$elem ), "\n";
    }
}


print STDERR "add more marshes..\n";
if ( $cartprod->update(2,['Lisa','Poochie']) )
{
    print STDERR "valid? ", $cartprod->validate(), "\n";
    print STDERR "successful update! continue iteration..\n";
    while ( $cartprod->getNext($elem) )
    {
        print STDERR "elem: ", join( ',', @$elem ), "\n";
    }
}


