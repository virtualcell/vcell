#!/usr/bin/perl

for $file (@ARGV){
  open(FILE,$file) || die "Couldn't open $file: $!\n";
  print "$file:\n";
  while(<FILE>){
    if (/^s*begin\s+(\S.*)$/){
      $bname= $1;
      $bname=~ s/\s*$//;
      $nentry=0;
      while(<FILE>){
	if (/^\s*end\s+(\S.*)$/){
	  my $ename= $1;
	  $ename=~ s/\s*$//;
	  if ($bname ne $ename){
	    die "end $ename not compatible with $bname\n";
	  }
	  last;
	}
        ++$nentry;
      }
      printf  "%7d %s\n", $nentry, $bname;
    }
  }
  close(FILE);
}
