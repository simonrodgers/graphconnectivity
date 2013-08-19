Highest-connectivity vertex iterator
====================================

Specialised undirected graph implementation with functionality to efficiently iterate vertices in order of highest degree (connectivity), and remove that vertex and all its immediate connections

Extraction of the most connected vertex reduces the degree of its second-order siblings, and typically changes the ordering of subsequent removal.

This re-ordering is handled efficiently; the implementation is similar to pigeonhole sort - a non-comparison-based sort, which isn't subject to the lower bounds on comparison sorts' running time of O(n log n)

Note: once built, this is a single-pass stateful iterator - removing each sub-graph (vertex and its immediate connections) mutates the graph for subsequent calls.

Algorithm
---------

    Build an ordered map M of {integer degree : [set of all vertices with that degree]}
    While M is not empty:
        an arbitrary vertex V is removed from the set S with highest degree
        for V and each of V's immediately connected vertices O :
        	for each of O's immediately connected vertices P :
	            O is removed from P's connections
	            P's degree is lowered by 1, and moved to the correct set within M

running time scales as O((m + n) * log(D)) to iterate over all vertices, where m = number of edges, n = number of vertices, D = cardinality of degrees of all vertices

log(D) can typically be treated as a small constant factor, this isn't typically sensitive to variance in m or n.

Code usage
----------

Vertexes are created implicitly by creating edges. Vertexes are represented by unique Strings.

    Builder b = MostConnectedVertexGraphIterator.newBuilder();
    b.addEdge("a", "b");
    b.addEdge("a", "c");
    b.addEdge("c", "d", "optional edge label");
    ...
    MostConnectedVertexGraphIterator g = b.build();
    while (g.hasNext()){
		Vertex v = g.next();
		for (Vertex o: v.getEdges()){ // or v.getEdgeLabels() to return vertexs and their labels
			...
		}
    }

Compilation & assembly
----------------------
	mvn compile
    mvn assembly:single

FamilyIdentifier
----------------

Simple wrapper around MostConnectedVertexGraphIterator, reading from input file of graph edges. In turn, the largest family (immediately connected sub-graph) is repeatedly removed from the graph, and written to the output file. 

Input file: one line per edge, tab-separated, to represent edges in the graph (family relationships). (optional) anything after the first two columns is treated as an edge label.

    a	b	label 1
    a	c	label 2
    a	d	label 3
    d	e	label 4
    ...

output file: indented section per most-connected node, with the most connected node at the top of each indented section with its connectivity count, and each connected vertex and its label on a separate line. change FamilyIdentifier's output to whatever you prefer

    java -Xmx2048M -jar target/graphconnectivity-jar-with-dependencies.jar <input file> <output file>