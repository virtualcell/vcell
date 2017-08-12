package edu.rpi.graphdrawing;

import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;


public
class Blackboard {

protected Vector _edges;
protected Vector _nodes;
protected Stack _history;
public Globals globals;
public Matrix3D projection;
  // Create and remove a meta-root and meta-edges to all nodes.
  //
protected Node _meta_root;
protected Vector _meta_edges;
// Add and remove dummy nodes between nodes between levels that are far apart
protected boolean _hasDummies = false;
protected Node _center;
  // Embedder stuff
  //
protected Embedder _embedder;
protected Hashtable _embedders;
private boolean _embedderChanged = true;
  // Size stuff
  //
protected double _lx;
protected double _ly;
protected double _ux;
protected double _uy;
public Blackboard() {
  _edges = new Vector();
  _nodes = new Vector();
  _history = new Stack();
//  _applet = (GraphApplet) applet;
  globals = new Globals(this);
  projection = new Matrix3D();
  _embedders = new Hashtable();
}
public synchronized void addDummies() {
  if( _hasDummies ) return;
  _hasDummies = true;
  int nodecnt = _nodes.size();
  for (int i = 0; i < nodecnt; ++i ) {
    Node to = (Node) _nodes.elementAt(i);
    if( to.dummy() ) continue;
    Vector inedges = to.inedges();
    int inedgecnt = inedges.size();
    for (int j = 0; j < inedgecnt; ++j ) {
      Edge e = (Edge) inedges.elementAt(j);
      if( e.from().dummy() ) continue;
      boolean showit = (e.from().showing() && to.showing());
      while( to.level() > e.from().level()+1 ) {
        Node from = e.from();
        Node dummy = new Node( this );          // Make new node
        if( showit ) dummy.showing( true );
        dummy.level( from.level()+1 );          // Set the level of the new node
        _nodes.addElement( dummy );
        Edge inedge = new Edge( this, from, dummy );  // First new edge 
        if( e.reversed() ) inedge.reverseDir();
        from.replace_out( e, inedge );
        dummy.add_in( inedge );
        Edge outedge = new Edge( this, dummy, to );   // Second new edge
        if( e.reversed() ) outedge.reverseDir();
        to.replace_in( e, outedge );
        dummy.add_out( outedge );
        _edges.addElement( inedge );            // Substitute new edges for old
        _edges.addElement( outedge );
        _edges.removeElement( e );
        e = outedge;
      }
    }
  }
}
public Edge addEdge(String f, String t) {
  Node from = findNode(f);
  Node to = findNode(t);
  Edge e = new Edge( this, from, to );
  _edges.addElement( e );
  from.add_out( e );
  to.add_in( e );
  return e;
}
public void addEmbedder( String name, Embedder embedder ) {
  _embedders.put( name, embedder );
}
public Node addNode(String lbl) {
  Node n = new Node( this, lbl );
  _nodes.addElement(n);
  return n;
}
public synchronized void backtrack() {
  if( _history.empty() )
    return;

  Object v[] = (Object[]) _history.pop();
  _nodes = (Vector) v[0];
  _edges = (Vector) v[1];
  Vector showing = (Vector) v[2];
  Node new_center = (Node) v[3];
  Vector ins = (Vector) v[4];
  Vector outs = (Vector) v[5];

  int nodecnt = _nodes.size();            // Initialize nodes
  for (int i = 0; i < nodecnt; ++i ) {
    Node u = (Node) _nodes.elementAt(i);
    u.showing( false );
    u.center( false );
  }
  int showcnt = showing.size();           // Show old showing nodes
  for (int i = 0; i < showcnt; ++i )
    ((Node) showing.elementAt(i)).showing( true );
  if( new_center != null )                // Mark the old center as center
    new_center.center( true );
  // Deal with removing the grouping, if any
  if( ins != null ) {             
    Vector ingrp = _center.inedges();      // Remove the edges to group center
    int ingrpcnt = ingrp.size();     
    for( int i = 0; i < ingrpcnt; ++i ) {
      Edge e = (Edge) ingrp.elementAt(i);
      e.from().remove_out( e );
    }
    Vector outgrp = _center.outedges();
    int outgrpcnt = outgrp.size();
    for( int i = 0; i < outgrpcnt; ++i ) {
      Edge e = (Edge) outgrp.elementAt(i);
      e.to().remove_in( e );
    }
    int inscnt = ins.size();               // Add the original edges back in
    for( int i = 0; i < inscnt; ++i ) {
      Edge e = (Edge) ins.elementAt(i);
      e.from().add_out( e );
    }
    int outscnt = outs.size();
    for( int i = 0; i < outscnt; ++i ) {
      Edge e = (Edge) outs.elementAt(i);
      e.to().add_in( e );
    }
  }
  _center = new_center;
}
protected final void break_cycles( Node curr ) {
  curr.mark();
  curr.pick( true );
  Vector outedges = curr.outedges();
  int outedgecnt = outedges.size();
  for (int i = 0; i < outedgecnt; ++i ) {
    Edge e = (Edge) outedges.elementAt(i);
    Node n = e.to();
    if( n.picked() ) 
      e.reverseDir();
    else if( !n.marked() )
      break_cycles( n );
  }
  curr.pick( false );
}
public boolean catchEmbedderChange() {
  if( _embedderChanged ) {
    _embedderChanged = false;
    return true;
  }
  return false;
}
  // Node and Edge stuff
  //
public final Vector edges() {
  return _edges;
}
public Embedder embedder() {
  return _embedder;
}
public synchronized void embedder( Embedder e ) {
  _embedder = e;
}
public Node findNode(String lbl) {
  int nodecnt = _nodes.size();
  for (int i = 0; i < nodecnt; ++i ) {
    Node n = (Node) _nodes.elementAt(i);
    if (n.label().equals(lbl))
      return n;
  }
  return addNode(lbl);
}
protected final void fixReversedEdges() {
  int edgecnt = _edges.size();
  for (int i = 0; i < edgecnt; ++i ) {
    Edge e = (Edge) _edges.elementAt(i);
    if( e.reversed() ) {
      Node n = e.to();
      n.remove_in( e );
      n.add_out( e );
      n = e.from();
      n.remove_out( e );
      n.add_in( e );
      e.swapDir();
    }
  }
}
  // Localizaton and grouping stuff
  //
public Vector get_fixed() {
  Vector fixed = new Vector();
  int nodecnt = _nodes.size();
  for (int i = 0; i < nodecnt; ++i ) {
    Node n = (Node) _nodes.elementAt(i);
    if( n.fixed() ) fixed.addElement( n );
  }
  return fixed;
}
public synchronized void group( Node center ) {
  Node grouped = new Node( this, "*" + center.label() + "*" );
  grouped.showing( true );
  grouped.x( center.x() );
  grouped.y( center.y() );
  grouped.z( center.z() );
  Vector ins = new Vector();
  Vector outs = new Vector();

  push_history( grouped, ins, outs );
  unmarkNodes();
  Vector fixed = get_fixed();
  if( fixed.size() == 1 ) {
    mark_local( center );
  }
  else {
    int nodecnt = _nodes.size();
    for (int i = 0; i < nodecnt; ++i ) {
      Node n = (Node) _nodes.elementAt( i );
      if( n.fixed() ) {
        n.mark();
        n.fix(false);
      }
    }
  }
  grouped.mark();

  // Make _nodes contain only unmarked nodes, complement the set of marked.
  Vector group_complement = new Vector();
  int nodecnt = _nodes.size();
  for (int i = 0; i < nodecnt; ++i ) {
    Node n = (Node) _nodes.elementAt( i );
    if( n.marked() )
      n.unmark();
    else {
      group_complement.addElement( n );
      n.mark();
    }
  }
  _nodes = group_complement;
  _nodes.addElement( grouped );
  grouped.mark();

  // Add all the relevant edges to the set of edges, modify if neccessary.
  Vector new_edges = new Vector();
  Vector seen_in_nodes = new Vector();
  Vector seen_out_nodes = new Vector();
  int edgecnt = _edges.size();
  for (int i = 0; i < edgecnt; ++i ) {
    Edge e = (Edge) _edges.elementAt(i);
    Node from = e.from();
    Node to = e.to();
    if( from.marked() && to.marked() )
      new_edges.addElement( e );
    else if( from.marked() && !to.marked() ) {
      ins.addElement( e );
      if( seen_in_nodes.contains( from ) ) {
        from.remove_out( e );
      } else {
        seen_in_nodes.addElement( from );
        Edge newin = new Edge( this, from, grouped );
        grouped.add_in( newin );
        from.replace_out( e, newin );
        new_edges.addElement( newin );
      }
    }
    else if( to.marked() && !from.marked() ) {
      outs.addElement( e );
      if( seen_out_nodes.contains( to ) ) {
        to.remove_in( e );
      } else {
        seen_out_nodes.addElement( to );
        Edge newout = new Edge( this, grouped, to );
        grouped.add_out( newout );
        to.replace_in( e, newout );
        new_edges.addElement( newout );
      }
    }
  }
  _edges = new_edges;
  grouped.center( true );
}
public boolean hasDummies() { 
  return _hasDummies; 
}
public void Init() {
  _embedder.Init();
  double k = globals.k();
}
public synchronized void localize( Node center ) {
  push_history( center, null, null );
  unmarkNodes();
  mark_local( center );
  only_show_marked();
  center.center( true );
}
public final double lx() { return _lx; }
public final double ly() { return _ly; }
protected Node makeMetaRoot() {
  _meta_root = new Node( this, "meta-root" );
  _meta_edges = new Vector();
  int nodecnt = _nodes.size();
  for (int i = 0; i < nodecnt; ++i ) {
    Node n = (Node) _nodes.elementAt(i);
    Edge e = new Edge( this, n, _meta_root );
    _meta_edges.addElement( e );
    _edges.addElement( e );
    n.add_out( e );
    _meta_root.add_in( e );
  }
  _nodes.addElement( _meta_root );
  return _meta_root;
}
protected void mark_local( Node center ) {
  // Mark all nodes within the right depth as showing
  center.mark();
  int nodecnt = _nodes.size();
  int edgecnt = _edges.size();
  for( int depth = 0; depth < globals.localizationDepth(); ++depth ) {
    for( int i = 0; i < edgecnt; ++i ) {
      Edge e = (Edge) _edges.elementAt(i);
      Node from = e.from();
      Node to = e.to();
      if( from.marked() && !to.marked() )  // Use center as flag for next round
        to.center( true );
      if( !from.marked() && to.marked() )
        from.center( true);
    }
    for( int i = 0; i < nodecnt; ++i ) {
      Node n = (Node) _nodes.elementAt(i);
      if( n.center() ) {
        n.center( false );
        n.mark();
      }
    }    
  }
  // Finish marking all the dummy-studded edges which are partially marked 
  for (int i = 0; i < edgecnt; ++i ) {
    Edge e = (Edge) _edges.elementAt(i);
    Node from = e.from();
    Node to = e.to();
    if( from.dummy() && from.marked() && !to.marked() ) {
      while( to.dummy() ) {
        to.mark();
        to = ((Edge) to.outedges().firstElement()).to();
      } 
      to.mark();
    }
    else if( to.dummy() && to.marked() && !from.marked() ) {
      while( from.dummy() ) {
        from.mark();
        from = ((Edge) from.inedges().firstElement()).from();
      }
      from.mark();
    }
  }
}
public final Vector nodes() {
  return _nodes;
}
public double Norm(Node u, Node v) {
  double dx = v.x() - u.x();
  double dy = v.y() - u.y();
  double dz = v.z() - u.z();
  return Math.sqrt( dx*dx + dy*dy + dz*dz );
}
public void only_show_marked() {
  int nodecnt = _nodes.size();
  for( int i = 0; i < nodecnt; ++i ) {
    Node u = (Node) _nodes.elementAt(i);
    if( u.marked() ) u.showing( true );
    else             u.showing( false );
  }
}
public void PreprocessNodes() {
  // Break any cycles in the graph by reversing some edges
  unmarkNodes();
  int nodecnt = _nodes.size();
  for (int i = 0; i < nodecnt; ++i ) {
    Node n = (Node) _nodes.elementAt(i);
    if( !n.marked() ) break_cycles( n );      
  }
  fixReversedEdges();

  // Do a topological sort on the meta-graph, then assign levels to nodes.
  Vector topoSortedNodes = new Vector();
  Node meta_root = makeMetaRoot();
  unmarkNodes();
  topoSort( meta_root, topoSortedNodes );
  removeMetaRoot();

  int toposize = topoSortedNodes.size();
  int maxlevel = 0;
  for (int i = 0; i < toposize; ++i ) {
    Node v = (Node) topoSortedNodes.elementAt(i);
    int level = 0;
    Vector inedges = v.inedges();
    int inedgecnt = inedges.size();
    for (int j = 0; j < inedgecnt; ++j ) {
      Node u = ((Edge) inedges.elementAt(j)).from();
      if( u.level() > level )
        level = u.level();
    }
    v.level( level+1 );
    if( level+1 > maxlevel )
      maxlevel = level+1;
  }
  // Hoist the nodes up to the maximum possible usage level.
  for (int i = toposize-1; i >= 0; --i ) {
    Node v = (Node) topoSortedNodes.elementAt(i);
    int min_useage_level = maxlevel;
    Vector outedges = v.outedges();
    int outedgecnt = outedges.size();
    if( outedgecnt == 0 )
      min_useage_level = v.level();
    for (int j = 0; j < outedgecnt; ++j ) {
      Node u = ((Edge) outedges.elementAt(j)).to();
      int useage_level = u.useage_level()-1;
      if( useage_level < min_useage_level )
        min_useage_level = useage_level;
    }
    v.useage_level( min_useage_level );
  }
  for (int i = 0; i < toposize; ++i ) {
    Node v = (Node) topoSortedNodes.elementAt(i);
    v.level( v.useage_level() );
  }
}
  // Some debug stuff
  //
public synchronized void printDebug() {
  int realminx = 0;
  int realmaxx = 0;
  int realminy = 0;
  int realmaxy = 0;
  int nodecnt = _nodes.size();
  for (int i = 0; i < nodecnt; ++i ) {
    Node n = (Node) _nodes.elementAt(i);
    if( n.x() > realmaxx )      realmaxx = (int)(n.x());
    else if( n.x() < realminx ) realminx = (int)(n.x());
    if( n.y() > realmaxy )      realmaxy = (int)(n.y());
    else if( n.y() < realminy ) realminy = (int)(n.y());
    System.err.println( n.toString() );
  }
  int edgecnt = _edges.size();
  for (int i = 0; i < edgecnt; ++i ) {
    Edge e = (Edge) _edges.elementAt(i);
    System.err.println( e.toString() );
  }
  System.err.println("lx:\t" + _lx);
  System.err.println("ly:\t" + _ly);
  System.err.println("ux:\t" + _ux);
  System.err.println("uy:\t" + _uy);
  System.err.println("D:\t" + globals.D());
  System.err.println("L:\t" + globals.L());
  System.err.println("realminx:\t" + realminx);
  System.err.println("realmaxx:\t" + realmaxx);
  System.err.println("realminy:\t" + realminy);
  System.err.println("realmaxy:\t" + realmaxy);
  System.err.println("depth3D:\t" + globals.depth3D() );
}
protected void push_history( Node center, Vector ins, Vector outs ) {
  // Save the old "stack"
  Vector showing = new Vector();
  Object v[] = new Object[6];
  v[0] = _nodes.clone();
  v[1] = _edges.clone();
  v[2] = showing;
  v[3] = _center;
  v[4] = ins;
  v[5] = outs;
  _history.push( v );
  int nodecnt = _nodes.size();
  for (int i = 0; i < nodecnt; ++i ) {
    Node u = (Node) _nodes.elementAt(i);
    if( u.showing() ) showing.addElement( u );
    u.center( false );
  }
  _center = center;
}
public synchronized void removeDummies() {
  if( !_hasDummies ) return;
  _hasDummies = false;
  unmarkNodes();
  Node meta_root = makeMetaRoot();
  removeDummiesDFS( meta_root, meta_root );
  removeMetaRoot();
}
protected final void removeDummiesDFS( Node curr, Node meta_root ) {
  curr.mark();
  Vector inedges = curr.inedges();
  int inedgecnt = inedges.size();
  for (int i = 0; i < inedgecnt; ++i ) {
    Edge e = (Edge) inedges.elementAt(i);
    Node n = e.from();
    if( n.dummy() ) {
      boolean reverse = e.reversed();
      _edges.removeElement( e );
      if( e.to() == meta_root ) continue;
      while( n.dummy() ) {
        _nodes.removeElement( n );
        Edge other = (Edge) n.inedges().firstElement();
        Node next = other.from();
        _edges.removeElement( other );
        if( !next.dummy() ) 
          next.remove_out( other );
        n = next;
      }
      Edge shortcut = new Edge( this, n, curr );
      if( reverse ) shortcut.reverseDir();
      n.add_out( shortcut );
      curr.replace_in( e, shortcut );
      _edges.addElement( shortcut );
    }
    if( !n.marked() )
      removeDummiesDFS( n, meta_root );
  }
  Update();
}
protected void removeMetaRoot() {
  int junkcnt = _meta_edges.size();
  for (int i = 0; i < junkcnt; ++i ) {
    Edge e = (Edge) _meta_edges.elementAt(i);
    _edges.removeElement( e );
    e.from().remove_out( e );
    e.to().remove_in( e );
  }
  _nodes.removeElement( _meta_root );
}
public void setArea(double lx, double ly, double ux, double uy ) {
  _lx = lx;
  _ly = ly;
  _ux = ux;
  _uy = uy;
}
public void setEmbedding( String name ) {
  _embedder = (Embedder) _embedders.get(name);
  _embedderChanged = true;
}
public final void topoSort( Node curr, Vector topoSortedNodes ) {
  curr.mark();
  Vector inedges = curr.inedges();
  int inedgecnt = inedges.size();
  for (int i = 0; i < inedgecnt; ++i ) {
    Node n = ((Edge) inedges.elementAt(i)).from();
    if( n.marked() == false )
      topoSort( n, topoSortedNodes );
  }
  topoSortedNodes.addElement( curr );
}
  // Assign layers, break cycles.
  //
protected final void unmarkNodes() {
  int nodecnt = _nodes.size();
  for (int i = 0; i < nodecnt; ++i ) {
    Node n = (Node) _nodes.elementAt(i);
    n.unmark();
  }
}
public synchronized void Update() {
  int edgecnt = _edges.size();
  for (int i = 0; i < edgecnt; ++i ) {
    Edge e = (Edge) _edges.elementAt(i);
    e.updateLength();
  }
}
public final double ux() { return _ux; }
public final double uy() { return _uy; }
} // class Blackboard
