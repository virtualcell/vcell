#!/usr/bin/perl

package HNauty;
require Exporter;

our @ISA    = qw(Exporter);
our @EXPORT = qw(HNauty deepCopy adj_permute get_adj_str);

#our @EXPORT_OK= qw(deepCopy);
our $VERSION = 1.00;

# NOTE: Many undeclared global symbols
#use strict;
########### HNauty program

# list of subroutines:

# adj_permute
# intersection
# is_discrete
# lex_ordered
# HNauty
# partition_meet
# partition_value
# pfixp
# refinement
# Run
# update_automorphisms

#Run();

sub Run {
  use Data::Dumper;

  my $le_part = [];
  push @$le_part, [ 0, 1, 2, 3 ], [ 4, 5, 6, 7 ], [8], [9], [10];

  my $debug_partition_value = 0;
  if ($debug_partition_value) {
    my $value = partition_value($le_part);
    print "@$value\n";
  }

  my $le_adj = {
    0  => { 6 => [0], 7  => [0], 8  => [0] },
    1  => { 9 => [0], 10 => [0], 11 => [0] },
    2  => { 9 => [0], 10 => [0], 11 => [0] },
    3  => { 6 => [0], 12 => [0], 14 => [0] },
    4  => { 7 => [0], 12 => [0], 13 => [0] },
    5  => { 8 => [0], 13 => [0], 14 => [0] },
    6  => { 3 => [0], 0  => [0] },
    7  => { 4 => [0], 0  => [0] },
    8  => { 0 => [0], 5  => [0] },
    9  => { 2 => [0], 1  => [0] },
    10 => { 1 => [0], 2  => [0] },
    11 => { 1 => [0], 2  => [0] },
    12 => { 3 => [0], 4  => [0] },
    13 => { 5 => [0], 4  => [0] },
    14 => { 3 => [0], 5  => [0] }
  };

  my $adj_out = deepCopy($le_adj);

  # define adj_in - note that this example is not really directed
  my ( $i, $j );
  $adj_in = {};
  for $i ( keys %$adj_out ) {
    for $j ( keys %{ $adj_out->{$i} } ) {
      @{ $adj_in->{$j}{$i} } = @{ $adj_out->{$i}{$j} };
    }
  }

  my $the_partition = [ [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 ] ];

  my $le_perm = {
    0  => 1,
    1  => 2,
    2  => 3,
    3  => 4,
    4  => 5,
    5  => 6,
    6  => 7,
    7  => 8,
    8  => 9,
    9  => 10,
    10 => 11,
    11 => 12,
    12 => 13,
    13 => 14,
    14 => 0
  };

  my $debug_adj_permute = 0;
  if ($debug_adj_permute) {
    my $new_adj = adj_permute( $le_adj, $le_perm );
    print Data::Dumper->Dump( [$new_adj] );
  }

  my $term = [
    [1], [2], [3], [4], [5], [6], [7], [8],
    [9], [10], [11], [12], [13], [14], [0]
  ];
  my $c_perm = [
    [0], [1], [2], [3], [4], [5], [6], [7],
    [8], [9], [10], [11], [12], [13], [14]
  ];

  my $debug_get_adj_str = 0;
  if ($debug_get_adj_str) {
    my $adj_infor = get_adj_str( $le_adj, $term );
    print Data::Dumper->Dump( [$adj_infor] );
  }

  my $debug_lex_ordered = 0;
  if ($debug_lex_ordered) {
    my $new_adj = adj_permute( $le_adj, $le_perm );

    # print Data::Dumper->Dump([$le_adj, $new_adj]);
    my $le_a = get_adj_str( $le_adj,  $c_perm );
    my $le_b = get_adj_str( $new_adj, $c_perm );
    my $result = lex_ordered( $le_a, $le_b );
    print "@$result\n";
    $result = lex_ordered( [ 0, 1, 2, 2 ], [ 0, 1, 2, 2, 2 ] );
    print "@$result\n";
    $result = lex_ordered( [ 2, 1, 99 ], [ 2, 1, 0 ] );
    print "@$result\n";
    $result =
      lex_ordered( [ [ 1, 2, 3, 4 ], [2] ], [ [ 1, 2, 3, 4 ], [ 1, 10 ] ] );
    print "@$result\n";
  }

  my $debug_is_discrete = 0;
  if ($debug_is_discrete) {
    my $result = is_discrete($c_perm);
    print "$result\n";
    $result = is_discrete( [ [ 0, 1 ], [2], [3] ] );
    print "$result\n";
  }

  my $debug_intersection = 0;
  if ($debug_intersection) {
    my $result = intersection( [ 0, 1, 2, 3, 4, 5 ], [ 5, 1, 7, 8, 9, 0 ] );
    print "@$result\n";
    print Data::Dumper->Dump( [$result] );
  }

  my $debug_partition_meet = 0;
  if ($debug_partition_meet) {
    my $result = partition_meet(
      [ [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 ] ],
      [
        [ [5], [ 1, 2, 3, 4, 6, 7, 8 ], [ 9, 0 ] ],
        [ [ 0, 1, 2, 3 ], [ 4, 5, 6 ], [ 7, 8, 9 ] ],
        [ [ 9, 0, 4, 1, 3 ], [ 2, 5, 6, 7 ], [8] ]
      ]
    );
    print Data::Dumper->Dump( [$result] );
  }

  my $newp = [ [ 6, 11, 7, 9, 12, 14, 8, 10, 13 ], [4], [ 1, 3, 0, 2, 5 ] ];
  my $autos = [];
  my $otra_perm = {
    1  => 1,
    2  => 3,
    3  => 4,
    4  => 2,
    5  => 6,
    6  => 5,
    7  => 7,
    8  => 9,
    9  => 10,
    10 => 11,
    11 => 12,
    12 => 13,
    13 => 8,
    14 => 14,
    0  => 0
  };
  my $fix;
  my $orbit_reps;
  my $otra_part =
    [ [0], [ 10, 2, 3 ], [1] . [ 4, 5, 6 ], [7], [ 8, 9, 11, 12, 13 ], [14] ];

  my $debug_update_automorphisms = 0;
  if ($debug_update_automorphisms) {
    ( $fix, $orbit_reps, $autos ) = update_automorphisms( $autos, $le_perm );
    print Data::Dumper->Dump( [ $fix, $orbit_reps, $autos ] );
    ( $fix, $orbit_reps, $autos ) = update_automorphisms( $autos, $otra_perm );
    print Data::Dumper->Dump( [ $fix, $orbit_reps, $autos ] );
  }

  my $debug_pfixp = 0;
  if ($debug_pfixp) {
    ( $fix, $orbit_reps, $autos ) = update_automorphisms( $autos, $otra_perm );
    print Data::Dumper->Dump( [$autos] );
    print pfixp( $c_perm,    $fix ), "\n\n";
    print pfixp( $otra_part, $fix ), "\n\n";
  }

  my $some_cell = [ 0, 1, 2, 3, 4 ];

  my $debug_update_cell = 0;
  if ($debug_update_cell) {
    ( $fix, $orbit_reps, $autos ) = update_automorphisms( $autos, $le_perm );
    ( $fix, $orbit_reps, $autos ) = update_automorphisms( $autos, $otra_perm );
    print Data::Dumper->Dump( [$autos] );
    my $result = update_cell( $otra_part, $some_cell, $autos );
    print @$result;
  }

  my $he_out = {
    0  => { 1  => [1], 12 => [0] },
    1  => { 0  => [1], 14 => [0] },
    2  => { 3  => [1], 14 => [0] },
    3  => { 2  => [1], 13 => [0] },
    4  => { 5  => [1], 14 => [0] },
    5  => { 4  => [1], 15 => [0] },
    6  => { 15 => [0], 7  => [1] },
    7  => { 17 => [0], 6  => [1] },
    8  => { 9  => [1], 17 => [0] },
    9  => { 8  => [1], 20 => [0] },
    10 => { 12 => [0] },
    11 => { 12 => [0] },
    16 => { 17 => [0] },
    18 => { 20 => [0] },
    19 => { 20 => [0] }
  };

  # define he_in
  $he_in = {};
  for $i ( keys %$he_out ) {
    for $j ( keys %{ $he_out->{$i} } ) {
      @{ $he_in->{$j}{$i} } = @{ $he_out->{$i}{$j} };
    }
  }
  my $he_part = [
    [
      0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20
    ]
  ];
  my $he_perm = {
    0  => 1,
    1  => 2,
    2  => 20,
    20 => 3,
    3  => 19,
    19 => 0,
    4  => 5,
    5  => 6,
    6  => 7,
    7  => 4,
    8  => 8,
    9  => 10,
    10 => 9,
    11 => 12,
    12 => 13,
    13 => 14,
    14 => 15,
    15 => 16,
    16 => 17,
    17 => 18,
    18 => 11
  };

  my $debug_refinement = 0;
  if ($debug_refinement) {
    my $result;
    $result = refinement( $adj_in, $adj_out, $the_partition, $the_partition );
    print Data::Dumper->Dump( [$result] );
    print "#############\n";
    print "#############\n";
    $result = refinement( $adj_in, $adj_out, $newp, [ [4] ] );
    print Data::Dumper->Dump( [$result] );
    print "#############\n";
    print "#############\n";
    $result = refinement( $he_out, $he_in, $he_part, $he_part );
    print Data::Dumper->Dump( [$result] );
  }

  my $debug_nauty = 1;
  if ($debug_nauty) {
    @$result = HNauty( $adj_in, $adj_out, $the_partition );
    my $adj_a = get_adj_str( $adj_out, @$result[0] );
    print @$adj_a, "\n\n";

    # permute
    $adj_in  = adj_permute( $adj_in,  $le_perm );
    $adj_out = adj_permute( $adj_out, $le_perm );
    @$result = HNauty( $adj_in, $adj_out, $the_partition );
    my $adj_b = get_adj_str( $adj_out, @$result[0] );
    print @$adj_b;
    $is_equal = lex_ordered( $adj_a, $adj_b );
    if ( @$is_equal[0] eq 'eq' ) { print "\nThey are equal!\n\n"; }

    # permute
    $adj_in  = adj_permute( $adj_in,  $otra_perm );
    $adj_out = adj_permute( $adj_out, $otra_perm );
    @$result = HNauty( $adj_in, $adj_out, $the_partition );
    my $adj_c = get_adj_str( $adj_out, @$result[0] );
    print @$adj_c;
    $is_equal = lex_ordered( $adj_c, $adj_b );
    if ( @$is_equal[0] eq 'eq' ) { print "\nThey are equal!\n\n"; }

    print "\n\n\nNow Hierarchical Example!!!\n";

    $adj_in        = $he_in;
    $adj_out       = $he_out;
    $the_partition = $he_part;
    $le_perm       = $he_perm;

    @$result = HNauty( $adj_in, $adj_out, $the_partition );
    $adj_a = get_adj_str( $adj_out, @$result[0] );
    print @$adj_a, "\n\n";

    # permute
    $adj_in  = adj_permute( $adj_in,  $le_perm );
    $adj_out = adj_permute( $adj_out, $le_perm );
    @$result = HNauty( $adj_in, $adj_out, $the_partition );
    $adj_b = get_adj_str( $adj_out, @$result[0] );
    print @$adj_b;
    $is_equal = lex_ordered( $adj_a, $adj_b );
    if ( @$is_equal[0] eq 'eq' ) { print "\nThey are equal!\n\n"; }

    # permute
    $adj_in  = adj_permute( $adj_in,  $le_perm );
    $adj_out = adj_permute( $adj_out, $le_perm );
    @$result = HNauty( $adj_in, $adj_out, $the_partition );
    $adj_c = get_adj_str( $adj_out, @$result[0] );
    print @$adj_c;
    $is_equal = lex_ordered( $adj_c, $adj_b );
    if ( @$is_equal[0] eq 'eq' ) { print "\nThey are equal!\n\n"; }

  }
}

sub update_cell {
  my $node  = shift;    #array
  my $cell  = shift;    #array
  my $perms = shift;    #array

  my $i;

#  $fixing = [];
  for $i ( 0 .. $#{$perms} ) {
    if ( pfixp( $node, @{ @$perms[$i] }[0] ) ) {
      $cell = intersection( $cell, @{ @$perms[$i] }[1] );
    }
  }
  return $cell;
}

sub pfixp {
  my $partition = shift;    #array
  my $perm_fix  = shift;    #array
  my ( $i, $fixed );

  $fixed = 1;
  $i     = 0;
  while ( $fixed and $i < scalar @$partition ) {
    if ( scalar @{ @$partition[$i] } == 1
      and ( not defined( $perm_fix->{ @{ @$partition[$i] }[0] } ) ) )
    {
      $fixed = 0;
    }
    $i++;
  }
  return $fixed;
}

sub update_automorphisms {
  my $autos = shift;    #array
  my $perm  = shift;    #hash

  my $un_used;          #hash
  my $fix        = {};  #hash
  my $orbit_reps = [];  # array
  my $something;        #array
  my $cycle;
  my ( $element, $done, $cycle_done );

  $un_used = deepCopy($perm);
  $done    = 0;
  while ( not $done ) {
    @$something = sort keys %$un_used;
    if ( scalar @$something == 0 ) {
      $done = 1;
    }
    else {
      $element    = @$something[0];
      $cycle      = [$element];
      $cycle_done = 0;
      while ( not $cycle_done ) {
        delete( $un_used->{$element} );
        $element = $perm->{$element};
        if ( not defined( $un_used->{$element} ) ) {
          $cycle_done = 1;
        }
        else {
          push @$cycle, $element;
        }
      }
      @$cycle = sort @$cycle;
      push @$orbit_reps, @$cycle[0];
      if ( scalar @$cycle == 1 ) {
        $fix->{ @$cycle[0] } = 1;
      }
    }
  }
  push @$autos, [ $fix, $orbit_reps ];
  return ( $fix, $orbit_reps, $autos );
}

sub partition_value {
  my $part = shift;    #array
  my $indicator;       #array
  my $tmp;             #hash
  my $other;           # array
  my $i;

  $tmp = {};
  for $j (@$part) {
    $tmp->{ scalar @$j } += 1;
  }
  $indicator = [];
  @$other    = sort keys %$tmp;
  for $j ( 1 .. @$other[ $#{$other} ] ) {
    if ( defined( $tmp->{$j} ) ) {
      push @$indicator, $tmp->{$j};
    }
    else {
      push @$indicator, 0;
    }
  }
  return $indicator;
}

sub preprocess { }

sub adj_permute {
  my $adj  = shift;    #hash  adj_out
  my $perm = shift;    #hash

  my $new_adj;            #hash
  my ( $i, $j, $new_i, $new_j );

  $new_adj = {};
  for $i ( keys %$adj ) {
    $new_i = $perm->{$i};
    $new_adj->{$new_i} = {};
    for $j ( keys %{ $adj->{$i} } ) {
      $new_j = $perm->{$j};
      @{ $new_adj->{$new_i}{$new_j} } = @{ $adj->{$i}{$j} };
    }
  }
  return $new_adj;
}

sub get_adj_str {
  my $adj  = shift;    #hash (adj_out)
  my $part = shift;    #array  or $hsah

  my $perm;            #hash
  my $new_adj;         # array
  my $adj_info;        # unfortunately also an array
  my ( $i, $j, $value, $e, $length );

#  $le_adj = [];
  if ( ref $part eq 'ARRAY' ) {
    for $i ( 0 .. $#{$part} ) {
      $perm->{ @{ @$part[$i] }[0] } = $i;
    }
  }
  else {
    $perm = $part;
  }
  $length = ( scalar keys %$adj ) - 1;
  $new_adj = adj_permute( $adj, $perm );
  for $i ( 0 .. $length ) {
    for $j ( 0 .. $length ) {
      if ( defined( $new_adj->{$i}{$j} ) ) {
        $value = 0;
        for $e ( @{ $new_adj->{$i}{$j} } ) {
          $value += 2**$e;
        }
        push @$adj_info, $value;
      }
      else {
        push @$adj_info, 0;
      }
    }
  }
  return $adj_info;
}

sub lex_ordered {

  # in fact returns appropriate counter value in this case..
  my $le_primier  = shift;    # array
  my $le_deuxieme = shift;    #array

  my $tmp;                    #array
  my ( $i, $status, $length, $result );

  @$tmp   = sort ( scalar @$le_primier, scalar @$le_deuxieme );
  $length = @$tmp[0];
  $status = ['eq'];
  $i      = 0;
  while ( @$status[0] eq 'eq' && $i < $length ) {
    if ( ref @$le_primier[$i] eq 'ARRAY' ) {
      $status = lex_ordered( @$le_primier[$i], @$le_deuxieme[$i] );
      if ( @$status[0] eq 'eq' ) {
        $i++;
      }
    }
    else {
      if ( @$le_primier[$i] > @$le_deuxieme[$i] ) {
        @$status[0] = 'gt';
      }
      elsif ( @$le_primier[$i] < @$le_deuxieme[$i] ) {
        @$status[0] = 'lt';
      }
      else {
        $i += 1;
      }
    }
  }
  @$status[1] = $i;
  return $status;
}

sub is_discrete {
  my $partition = shift;    #array

  my $discrete = 1;
  my $i;

  $i = 0;
  while ( $discrete and $i < scalar @$partition ) {
    if ( scalar @{ @$partition[$i] } > 1 ) {
      $discrete = 0;
    }
    $i += 1;
  }
  return $discrete;
}

sub intersection {
  my $le_premier  = shift;    #array
  my $le_deuxieme = shift;    #array

  my ( $union, $inter, $ele, $result );

  $union = {};
  $inter = {};

  for $ele ( @$le_premier, @$le_deuxieme ) {
    $union->{$ele}++ && $inter->{$ele}++;
  }
  @$result = ( keys %$inter );
  return $result;
}

sub partition_meet {
  my $the_set    = shift;    #array
  my $partitions = shift;    #array

  my $new_part;              #array
  my $newer_part;            #array
  my $p;                     #array
  my ( $i, $j );
  my $intersection;          #array

  $new_part = deepCopy($the_set);
  for $p (@$partitions) {
    if ( not scalar @$p == 1 ) {
      $newer_part = [];
      for $i (@$new_part) {
        for $j (@$p) {
          $intersection = intersection( $i, $j );
          if ( scalar @$intersection > 0 ) {
            push @$newer_part, [];
            push @{ @$newer_part[ $#{$newer_part} ] }, @$intersection;
          }
        }
      }
      $new_part = deepCopy($newer_part);
    }
  }
  return $new_part;
}

sub refinement {
  my $adj_in            = shift;    #hash
  my $adj_out           = shift;    #hash
  my $ordered_partition = shift;    #array
  my $thecells          = shift;    #array

  my $cells;                        #array
  my $equitable_partition;          #array
  my $cell;                         #hash
  my $in_degree_count;              #hash
  my $out_degree_count;             #hash
  my $count;                        #hash
  my $parts;                        #array
  my $new_part;                     #array
  my $index_set;                    #array

  my ( $cell_count, $partition_count, $v, $u, $e, $size, $t, $n );

  #Everything is a scalar!!!!!

  $cell_count = 0;

  # need a cleen copy of cells and equitable_partition
  $cells               = deepCopy($thecells);
  $equitable_partition = deepCopy($ordered_partition);
  while ( ( not is_discrete($equitable_partition) )
    and $cell_count < scalar @$cells )
  {

    # retrieve cell from the cells
    $cell = {};
    for $v ( @{ @$cells[$cell_count] } ) {
      $cell->{$v} = 1;
    }
    $cell_count += 1;
    $partition_count = 0;
    while ( $partition_count < scalar @$equitable_partition ) {
      if ( scalar @{ @$equitable_partition[$partition_count] } > 1 ) {
        $in_degree_count  = {};
        $out_degree_count = {};
        for $v ( @{ @$equitable_partition[$partition_count] } ) {
          $count = {};
          for $u ( keys %{ $adj_out->{$v} } ) {
            for $e ( @{ $adj_out->{$v}{$u} } ) {
              if ( defined( $cell->{$u} ) ) {
                $count->{$e} += 1;
              }
            }
          }
          for $e ( keys %$count ) {
            if ( not defined( $out_degree_count->{$e} ) ) {
              $out_degree_count->{$e}{0} =
                +{ map { $_ => 1 }
                  @{ @$equitable_partition[$partition_count] } };
            }
            $out_degree_count->{$e}{ $count->{$e} }{$v} = 1;
            if ( defined( $out_degree_count->{$e}{0}{$v} ) ) {
              delete( $out_degree_count->{$e}{0}{$v} );
            }
          }

          # repeat for out degrees:
          $count = {};
          for $u ( keys %{ $adj_in->{$v} } ) {
            for $e ( @{ $adj_in->{$v}{$u} } ) {
              if ( defined( $cell->{$u} ) ) {
                $count->{$e} += 1;
              }
            }
          }
          for $e ( keys %$count ) {
            if ( not defined( $in_degree_count->{$e} ) ) {
              $in_degree_count->{$e}{0} =
                +{ map { $_ => 1 }
                  @{ @$equitable_partition[$partition_count] } };
            }
            $in_degree_count->{$e}{ $count->{$e} }{$v} = 1;
            if ( defined( $in_degree_count->{$e}{0}{$v} ) ) {
              delete( $in_degree_count->{$e}{0}{$v} );
            }
          }
        }

        # Now we make the partitions
        $parts = [];
        for $e ( sort ( keys %$out_degree_count ) ) {
          $new_part = [];
          for $size ( sort ( keys %{ $out_degree_count->{$e} } ) ) {
            if ( scalar( keys %{ $out_degree_count->{$e}{$size} } ) > 0 ) {
              push @$new_part, [];
              push @{ @$new_part[ $#{$new_part} ] },
                ( keys %{ $out_degree_count->{$e}{$size} } );
            }
          }
          if ( scalar @$new_part > 1 ) {
            push @$parts, [];
            push @{ @$parts[ $#{$parts} ] }, @$new_part;
          }
        }

        # do the same for $in_degree_count
        for $e ( sort ( keys %$in_degree_count ) ) {
          $new_part = [];
          for $size ( sort ( keys %{ $in_degree_count->{$e} } ) ) {
            if ( scalar( keys %{ $in_degree_count->{$e}{$size} } ) > 0 ) {
              push @$new_part, [];
              push @{ @$new_part[ $#{$new_part} ] },
                ( keys %{ $in_degree_count->{$e}{$size} } );
            }
          }
          if ( scalar @$new_part > 1 ) {
            push @$parts, [];
            push @{ @$parts[ $#{$parts} ] }, @$new_part;
          }
        }

        # now we update the equitable partition and cells
        if ( scalar @$parts > 0 ) {
          if ( scalar @$parts > 1 ) {
            $new_part =
              partition_meet( [ @$equitable_partition[$partition_count] ],
              $parts );
          }
          else {
            $new_part = @$parts[0];
          }

          # find smallest 't' bit here
          $size = 0;
          $t    = 0;
          for $n ( 0 .. $#{$new_part} ) {
            if ( scalar @{ @$new_part[$n] } > $size ) {
              $t    = $n;
              $size = scalar @{ @$new_part[$n] };
            }
          }

          # update cells list as appropriate
          for $n ( ( $cell_count + 1 ) .. $#{$cells} ) {
            if (
              @{ @$cells[$n] } == @{ @$equitable_partition[$partition_count] } )
            {
              push @{ @$cells[$n] }, @{ @$new_part[$t] };
            }
          }
          $index_set = [];
          for $n ( 0 .. $#{$new_part} ) {
            if ( not $n == $t ) {
              push @$cells, [];
              push @{ @$cells[ $#{$cells} ] }, @{ @$new_part[$n] };
            }
          }
          @$equitable_partition = (
            @$equitable_partition[ 0 .. $partition_count - 1 ],
            @$new_part,
            @$equitable_partition[ $partition_count +
              1 .. $#{$equitable_partition} ]
          );
        }

      }
      $partition_count += 1;
    }
  }
  return $equitable_partition;
}

sub HNauty {
  my $adj_in        = shift;    #hash
  my $adj_out       = shift;    # hash
  my $node_coloring = shift;    #array

  my $ordered_partition;        #array
  my $current_node;             #array
  my $cells;                    #array
  my $tplacement;               #array
  my $node_indicator;           #array
  my $best_node;                #array
  my $first_terminal_node;      #array
  my $automorphisms;            #array
  my $generators;               #array of hashes
  my $prune_autos;              #array
  my $this_partition;           #array
  my $newset;                   #array
  my $new_node;                 #array
  my $some_hash;                #hash
  my $some_array;               #array
  my $new_auto;                 #hash
  my $perm;                     #hash
  my $fix;                      #array
  my $orbit_reps;               #array
  my ( $complete, $jump_back, $search_vertex );    #Boolean
  my $new_adj;
  my ( $counter, $i, $j, $t, $n, $size, $vertex, $a, $b, $ntn );

# do some preprocessing????
# for input: graph should vertices labeled from 0 to number vertices.  Edge types
# should also be labeled from 0 on up.  adj_in is hash of hash of arrays
# the hashes are keyed by vertices and the array adj_in{v1}{v2} contains the
# edge types going from vertex from v2 INTO v1.  adj_out is set up similarily.
  $complete  = $jump_back           = 0;
  $best_node = $first_terminal_node = [];
  $automorphisms     = [];
  $counter           = 0;
  $ntn               = 0;
  $ordered_partition = deepCopy($node_coloring);
  push @$current_node,
    refinement( $adj_in, $adj_out, $ordered_partition, $ordered_partition );
  $node_indicator = [ [] ];

  if ( is_discrete( @$current_node[0] ) ) {
    $new_adj = get_adj_str( $adj_out, @$current_node[$counter] );
    @$best_node =
      ( @$current_node[$counter], @$node_indicator[$counter], $new_adj );
    $complete = 1;
  }
  while ( not $complete ) {

    #main loop
    if ( is_discrete( @$current_node[$counter] ) ) {
#      if ( $first_terminal_node == ( @$current_node[$counter], @$node_indicator[$counter], $new_adj ) ){
   	  if ( $first_terminal_node == [ @$current_node[$counter], @$node_indicator[$counter], $new_adj ] ){
        @$best_node = @$first_terminal_node;
      }
      $counter += -1;
      if ( $counter < 0 ) {
        $complete = 1;
      }
    }
    elsif ( not $jump_back ) {

      # find first nontrivial cell of smallest size
      $size = scalar keys %$adj_in;
      $t    = 0;
      for $n ( 0 .. $#{ @$current_node[$counter] } ) {
        if ( 1 < scalar @{ @{ @$current_node[$counter] }[$n] }
          && scalar @{ @{ @$current_node[$counter] }[$n] } < $size )
        {
          $t    = $n;
          $size = scalar @{ @{ @$current_node[$counter] }[$n] };
        }
      }
      @$cells[$counter] = [];
      push @{ @$cells[$counter] }, @{ @{ @$current_node[$counter] }[$t] };
      @$tplacement[$counter]  = $t;
      @$prune_autos[$counter] = undef;
      @$current_node          = @$current_node[ 0 .. $counter ];
    }
    else {
      @$current_node = @$current_node[ 0 .. $counter ];
    }
    $search_vertex = 1;
    $i             = 0;
    while ( ( not $complete ) && $search_vertex ) {
      while ( scalar @{ @$cells[$counter] } == 0 && not $complete ) {
        $counter += -1;
        if ( $counter < 0 ) {
          $complete = 1;
        }
      }
      if ( ( not defined( @$prune_autos[$counter] ) ) or $complete ) {
        @$prune_autos[$counter] = 0;
      }
      else {
        if ( @$prune_autos[$counter] < scalar @$automorphisms ) {
          @$cells[$counter] = update_cell(
            @$current_node[$counter],
            @$cells[$counter],
            [
              @$automorphisms[ @$prune_autos[$counter] .. $#{$automorphisms} ]
            ]
          );
          @$prune_autos[$counter] = scalar $#{$automorphisms};
        }
      }
      if ( not scalar @{ @$cells[$counter] } == 0 ) {
        @{ @$cells[$counter] } = sort @{ @$cells[$counter] };
        $vertex        = @{ @$cells[$counter] }[0];
        $search_vertex = 0;
      }
    }
    if ( not $complete ) {
      shift @{ @$cells[$counter] };
      $this_partition = deepCopy( @$current_node[$counter] );
      $t              = @$tplacement[$counter];
      $newset         = deepCopy( @$this_partition[$t] );
      $some_hash      = +{ map { $_ => 1 } @$newset };
      delete( $some_hash->{$vertex} );
      @$newset         = ( keys %$some_hash );
      @$this_partition = (
        @$this_partition[ 0 .. $t - 1 ],
        [$vertex], $newset, @$this_partition[ $t + 1 .. $#{$this_partition} ]
      );
      $new_node =
        refinement( $adj_in, $adj_out, $this_partition, [ [$vertex] ] );
      @$node_indicator[ $counter + 1 ] = partition_value($new_node);
      @$node_indicator = @$node_indicator[ 0 .. $counter + 1 ];

      # number of nodes += 1
      $jump_back = 0;
      if ( not scalar @$best_node == 0 ) {
        $i = lex_ordered( @$node_indicator[$counter], @$best_node[1] );
        if ( @$i[0] eq 'gt' ) {
#      	  $counter   = $i[1] - 1;
          $counter   = @$i[1] - 1;
          $jump_back = 1;
        }
      }
      if ( not $jump_back ) {
        @$current_node[ $counter + 1 ] = deepCopy($new_node);
        $counter += 1;
        if ( is_discrete($new_node) ) {
          $ntn++;
          $new_adj = get_adj_str( $adj_out, $new_node );
          if ( scalar @$first_terminal_node == 0 ) {
            @$first_terminal_node[0] = deepCopy( @$current_node[$counter] );
            @$first_terminal_node[1] = deepCopy( @$node_indicator[$counter] );
            @$first_terminal_node[2] = deepCopy($new_adj);
            $best_node               = deepCopy($first_terminal_node);
          }
          else {
            $i = lex_ordered( [ @$best_node[ 1 .. 2 ] ],
              [ @$node_indicator[$counter], $new_adj ] );
            if ( @$i[0] eq 'lt' ) {
              $best_node = deepCopy(
                [
                  @$current_node[$counter], @$node_indicator[$counter],
                  $new_adj
                ]
              );
            }
            else {
              $i = lex_ordered( @$best_node[2], $new_adj );
              if ( @$i[0] eq 'eq' ) {

                # we have found an automorphism!
                $new_auto = {};
                for $i ( 0 .. $#{ @$best_node[0] } ) {
                  $new_auto->{ @{ @{ @$best_node[0] }[$i] }[0] } =
                    @{ @{ @$current_node[$counter] }[$i] }[0];
                }
                push @$generators, $new_auto;
                ( $fix, $orbit_reps, $automorphisms ) =
                  update_automorphisms( $automorphisms, $new_auto );
                $some_array = [];
                for $i ( 0 .. $#{$current_node} ) {
                  if ( $i < $counter and pfixp( @$current_node[$i], $fix ) ) {
                    push @$some_array, $i;
                  }
                }
                @$some_array = sort @$some_array;
                $a           = @$some_array[ $#{$some_array} ];
                $jump_back   = 1;
              }
            }
            $i = lex_ordered( @$first_terminal_node[2], $new_adj );
            if ( @$i[0] eq 'eq' ) {

              # we have found an automorphism with first term node!
              $new_auto = {};
              for $i ( 0 .. $#{ @$first_terminal_node[0] } ) {
                $new_auto->{ @{ @{ @$first_terminal_node[0] }[$i] }[0] } =
                  @{ @{ @$current_node[$counter] }[$i] }[0];
              }
              push @$generators, $new_auto;
              ( $fix, $orbit_reps, $automorphisms ) =
                update_automorphisms( $automorphisms, $new_auto );
              $some_array = [];
              for $i ( 0 .. $#{$current_node} ) {
                if ( $i < $counter and pfixp( @$current_node[$i], $fix ) ) {
                  push @$some_array, $i;
                }
              }
              @$some_array = sort @$some_array;
              $b           = @$some_array[ $#{$some_array} ];
              if ($jump_back) {
                @$some_array = ( $a, $b );
                @$some_array = sort @$some_array;
                $counter     = @$some_array[0];
              }
              else {
                $counter   = $b;
                $jump_back = 1;
              }
            }
            elsif ($jump_back) {
              $counter = $a;
            }
          }
        }
      }
    }
  }
  $perm = {};
  for $i ( 0 .. $#{ @$best_node[0] } ) {
    $perm->{ @{ @{ @$best_node[0] }[$i] }[0] } = $i;
  }

  # maybe do some post processing here?
  # print "number terminal nodes: $ntn\n";
  # print "number autos: ", scalar @$generators, "\n";
  return ( $perm, $generators );
}

sub deepCopy {
  my $this = shift;
  if ( not ref $this ) {
    $this;
  }
  elsif ( ref $this eq "ARRAY" ) {
    [ map deepCopy($_), @$this ];
  }
  elsif ( ref $this eq "HASH" ) {
    +{ map { $_ => deepCopy( $this->{$_} ) } keys %$this };
  }
  else { die "what type is $_?" }
}

1;
