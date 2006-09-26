package cbit.util.graph;
/**
 * Insert the type's description here.
 * Creation date: (1/2/2006 11:32:40 AM)
 * @author: Jim Schaff
 */
public class TarjansAlgorithm {
	private java.util.Stack stack = null;
	private Graph graph = null;
	private Node[] nodes = null;
	private Edge[] edges = null;

	public final static int WHITE=0;

	private int root[] = null;
	private int color[] = null;
	private int counter;

/**
 * TarjansAlgorithm constructor comment.
 */
public TarjansAlgorithm(Graph argGraph) {
	super();
	this.graph = argGraph;
	this.nodes = argGraph.getNodes();
	this.edges = argGraph.getEdges();
	this.stack = new java.util.Stack(); // empty

	this.root = new int[nodes.length];
	this.color = new int[nodes.length];

	tarjan_external();
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/2006 10:09:24 AM)
 * @return int[]
 */
public int[] getRoots() {
	return root;
}


/** Returns the strongly connected cluster roots with size as a value.
* Cluster membership can be seen from the content of the array {@link #root};
* each node has the root of the strongly connected cluster it belongs to.
* A word of caution: for large graphs that have a large diameter and that
* are strongly connected (such as large rings) you can get stack overflow
* because of the large depth of recursion.
*/
//XXX implement a non-recursive version ASAP!!!
private java.util.Map tarjan_external() {
	
	stack.clear();
	for( int i=0; i<nodes.length; ++i) {
		color[i]=WHITE;
		root[i]=0;
	}
	counter = 1;
	
	// color is WHITE (0): not visited
	// not WHITE, positive (c>1): visited as the c-th node
	// color is negative (c<1): inComponent true
	for(int i=0; i<nodes.length; ++i)
	{
		if( color[i]==WHITE ) tarjanVisit(i);
	}
	for( int i=0; i<nodes.length; ++i) color[i]=0;
	for( int i=0; i<nodes.length; ++i) color[root[i]]++;
	java.util.Hashtable ht = new java.util.Hashtable();
	for(int j=0; j<nodes.length; ++j)
	{
		if(color[j]>0)
		{
			ht.put(new Integer(j),new Integer(color[j]));
		}
	}
	
	return ht;
}


/** The recursive part of the Tarjan algorithm. */
private void tarjanVisit(int i) {

	color[i]=counter++;
	root[i]=i;
	stack.push(new Integer(i));
	int j;

	for (int z = 0; z < edges.length; z++){
		if (edges[z].getNode1() == nodes[i]){
			j = graph.getIndex(edges[z].getNode2());
		}else{
			continue;
		}
		if( color[j]==WHITE )
		{
			tarjanVisit(j);
		}
		if( color[j]>0 && color[root[j]]<color[root[i]] )
		// inComponent is false and have to update root
		{
			root[i]=root[j];
		}
	}

	if(root[i]==i) //this node is the root of its cluster
	{
		do
		{
			j=((Integer)stack.pop()).intValue();
			color[j]=-color[j];
			root[j]=i;
		}
		while(j!=i);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/2/2006 12:30:48 PM)
 */
public static void test() {
	Graph graph = new Graph();
	//Node f1 = new Node("f1");
	//Node f2 = new Node("f2");
	//Node f3 = new Node("f3");
	//Node f4 = new Node("f4");
	//Node f5 = new Node("f5");
	//Node z1 = new Node("z1");
	//Node z2 = new Node("z2");
	//Node z3 = new Node("z3");
	//Node z4 = new Node("z4");
	//Node z5 = new Node("z5");
	//Node nodes[] = { f1, f2, f3, f4, f5, z1, z2, z3, z4, z5 };
	//Edge edges[] = {
					//new Edge(z1,f4), 
					 //new Edge(z1,f5), 
					//new Edge(z2,f2), 
					 //new Edge(z2,f3), 
					 //new Edge(z2,f4), 
					 //new Edge(z3,f1), 
					 //new Edge(z3,f3), 
					//new Edge(z3,f5), 
					//new Edge(z4,f1), 
					//new Edge(z5,f3), 
					 //new Edge(z5,f5) 
	//};
	
	//Node a = new Node("a");
	//Node b = new Node("b");
	//Node c = new Node("c");
	//Node d = new Node("d");
	//Node e = new Node("e");
	//Node f = new Node("f");
	//Node g = new Node("g");
	//Node h = new Node("h");
	//Node nodes[] = { a,b,c,d,e,f,g,h };
	//Edge edges[] = {
		//new Edge(a,b),
		//new Edge(b,e),
		//new Edge(b,f),
		//new Edge(b,c),
		//new Edge(c,g),
		//new Edge(c,d),
		//new Edge(d,c),
		//new Edge(d,h),
		//new Edge(e,a),
		//new Edge(e,f),
		//new Edge(h,h),
		//new Edge(f,g),
		//new Edge(g,f),
		//new Edge(g,h)
	//};
	Node a = new Node("a");
	Node b = new Node("b");
	Node c = new Node("c");
	Node d = new Node("d");
	Node e = new Node("e");
	Node f = new Node("f");
	Node nodes[] = { a,b,c,d,e,f };
	Edge edges[] = {
		new Edge(a,b),
		new Edge(a,c),
		new Edge(b,c),
		new Edge(c,d),
		new Edge(d,b),
		new Edge(d,e),
		new Edge(e,f),
		new Edge(f,e)
	};

	for (int i = 0; i < nodes.length; i++){
		graph.addNode(nodes[i]);
	}
	for (int i = 0; i < edges.length; i++){
		graph.addEdge(edges[i]);
	}
	TarjansAlgorithm ta = new TarjansAlgorithm(graph);
	//ta.tarjan();
	//for (int i = 0; i < nodes.length; i++){
		//System.out.println(i+") number="+ta.number[i]+", lowlink="+ta.lowlink[i]+", componentAssignment="+ta.componentAssignment[i]);
	//}
	java.util.Map map = ta.tarjan_external();
	for (int i = 0; i < nodes.length; i++){
		System.out.println(i+") color="+ta.color[i]+", root="+ta.root[i]);
	}
	//cbit.gui.graph.SimpleGraphModelPanelTest.renderGraph(graph);
	
}
}