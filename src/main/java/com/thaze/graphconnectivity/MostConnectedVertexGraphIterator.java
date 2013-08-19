package com.thaze.graphconnectivity;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Specialised undirected graph implementation with functionality to efficiently iterate vertices in order of highest degree (connectivity), and remove that vertex and all its immediate connections<br/>
 * <br/>
 * Extraction of the most connected vertex reduces the degree of its immediate siblings, and potentially changes the ordering of 
 * subsequent removal.<br/> 
 * <br/>
 * This re-ordering is handled efficiently; the implementation is similar to pigeonhole sort - a 
 * non-comparison-based sort, which isn't subject to the lower bounds on comparison sorts' running time of O(n log n)<br/>
 * <br/>
 * Note: once built, this is a single-pass stateful iterator - removing each vertex mutates the graph for subsequent calls.<br/>
 * <br/>
 * Algorithm:<br/>
 * <br/>
 * <blockquote><pre>
 *   Build an ordered map M of {integer degree : [set of all vertices with that degree]}
 *   While M is not empty:
 *       an arbitrary vertex V is removed from the set S with highest degree
 *       for V and each of V's immediately connected vertices O :
 *       	for each of O's immediately connected vertices P :
 *	            O is removed from P's connections
 *	            P's degree is lowered by 1, and moved to the correct set within M
 * </pre></blockquote>
 * running time scales as O((m + n) * log (D)) to iterate over all vertices, where m = #edges, n = #vertices, D = cardinality of degrees of all vertices<br/>
 * <br/>
 * Usage:<br/>
 * <br/>
 * Vertexes are created implicitly by creating edges between vertexes. Vertexes are represented by unique Strings.<br/>
 * <br/>
 * <blockquote><pre>
 * Builder b = MostConnectedVertexGraphIterator.newBuilder();
 * b.addEdge("a", "b");
 * b.addEdge("a", "c");
 * b.addEdge("c", "d");
 * ...
 * MostConnectedVertexGraphIterator g = b.build();
 * while (g.hasNext()){
 *		Vertex v = g.next();
 *		for (Vertex o: v.getEdges()){
 *			...
 *		}
 * }
 * </pre></blockquote>
 * 
 * @author Simon Rodgers
 */
public class MostConnectedVertexGraphIterator implements Iterator<Vertex> {
	
	// use MostConnectedVertexGraphIterator.newBuilder()
	private MostConnectedVertexGraphIterator(){}

	// index of {vertex name : vertex} 
	private final LoadingCache<String, Vertex> index = CacheBuilder.newBuilder().build(new CacheLoader<String, Vertex>() {
		public Vertex load(String s) {
			return new Vertex(s);
		}
	});
	
	
	private final Map<Integer, Set<Vertex>> buckets = Maps.newTreeMap(new Comparator<Integer>() {
		@Override
		public int compare(Integer i1, Integer i2) { 
			return i2-i1; // ordered descending
		}
	});
	
	private boolean built=false;
	
	public static Builder newBuilder(){return new Builder();}
	
	@Override
	public boolean hasNext() {
		return !buckets.isEmpty();
	}

	@Override
	public Vertex next() {
		if (!hasNext())
			throw new IllegalStateException("nothing left in graph");
		
		// first entry in buckets is the bucket of the largest
		Integer largestSize = buckets.keySet().iterator().next();
		Set<Vertex> bucket = buckets.get(largestSize);
		Vertex v = bucket.iterator().next();
		
		popVertex(v);
		for (Vertex sibling: v.getEdges())
			popVertex(sibling);
		
		return v;
	}

	private void popVertex(Vertex v) throws AssertionError {
		// remove from this bucket, remove bucket if it's now empty
		int vertexConnectionSize = v.getConnectionCount();
		Set<Vertex> bucket = buckets.get(vertexConnectionSize);
		bucket.remove(v);
		if (bucket.isEmpty())
			buckets.remove(vertexConnectionSize);
		
		// remove connection to v from all its connections
		// then move each of those connections down a bucket
		for (Vertex other: v.getEdges()){
			int otherConnectionCount = other.getConnectionCount();
			
			// find the existing bucket for the other end of this edge
			Set<Vertex> otherOldBucket = buckets.get(otherConnectionCount); // treemap lookup takes O(log N)
			if (otherOldBucket == null)
				throw new AssertionError("bucket not found, shouldn't happen");
			
			// remove from this bucket, remove bucket if it's now empty
			otherOldBucket.remove(other);
			if (otherOldBucket.isEmpty())
				buckets.remove(otherConnectionCount);
			
			// add (create if necessary) to the next bucket down
			Set<Vertex> otherNewBucket = buckets.get(otherConnectionCount-1);
			if (null == otherNewBucket){
				otherNewBucket = Sets.newLinkedHashSet();
				buckets.put(otherConnectionCount-1, otherNewBucket);
			}
			otherNewBucket.add(other);
			
			// remove from other
			other.remove(v);
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public static class Builder{
		
		// use MostConnectedVertexGraphIterator.newBuilder()
		private Builder(){}
		
		private final MostConnectedVertexGraphIterator g = new MostConnectedVertexGraphIterator();

		public void addEdge(String s1, String s2){
			addEdge(s1, s2, null);
		}

		public void addEdge(String s1, String s2, String edgeLabel){
			if (g.built)
				throw new IllegalStateException("graph already built");
			
			Vertex v1 = g.index.getUnchecked(s1);
			Vertex v2 = g.index.getUnchecked(s2);
			
			v1.add(v2, edgeLabel);
			v2.add(v1, edgeLabel);
		}
		
		public MostConnectedVertexGraphIterator build(){
			if (g.built)
				throw new IllegalStateException("graph already built");
			
			// for each vertex in the graph, store it in a map of {connection count : [set of all vertexes with that connection count] }
			for (Vertex v: g.index.asMap().values()){
				Set<Vertex> bucket = g.buckets.get(v.getConnectionCount());
				if (null == bucket){
					bucket = Sets.newLinkedHashSet(); // preserve input order
					g.buckets.put(v.getConnectionCount(), bucket);
				}
				bucket.add(v);
			}
			g.built=true;
			return g;
		}
	}

}