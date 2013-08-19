package com.thaze.graphconnectivity;

import junit.framework.Assert;
import org.junit.Test;

public class GraphTest {
	
	@Test
	public void testMostConnectedVertexIterator(){
		MostConnectedVertexGraphIterator.Builder gb = MostConnectedVertexGraphIterator.newBuilder();

		gb.addEdge("a", "b", "edge1");
		gb.addEdge("a", "c", "edge2");
		gb.addEdge("a", "d", "edge3");
		gb.addEdge("a", "e", "edge4");
		gb.addEdge("e", "f", "edge5");
		gb.addEdge("e", "g", "edge6");
		gb.addEdge("f", "g", "edge7");
		gb.addEdge("g", "z");
		gb.addEdge("d", "h", "edge9");
		gb.addEdge("h", "i");
		
		gb.addEdge("k", "l", "edge11");
		gb.addEdge("l", "m", "edge12");
		gb.addEdge("l", "n", "edge13");
		
		/* 
		 *   -------                   
		 *  /       \                   
		 * f -- e -- g -- z      f -------- g -- z      f -------- g -- z       
		 *      |    |           
		 * c -- a -- b           
		 *      |		   
		 *      d            ->                     ->                      -> 
		 *      |
		 * i -- h                 i -- h                 i -- h             i -- h
		 * 
		 * k -- l -- m            k -- l -- m
		 *      |                      |
		 *      n                      n
		 */
		
		MostConnectedVertexGraphIterator g = gb.build();
		
//		while (g.hasNext()){
//			Vertex v = g.next();
//			System.out.println(v);
//			for (Vertex other: v.getEdges())
//				System.out.println("\t" + other);			
//		}
		
		// first vertex popped will be the most connected one (a: 4), and all of its siblings will be removed
		// could have also been g, with a connectivity of 4
		// (aside: note that the order within the group is deterministic but not easily predictable) 
		// (depends on whether a node started with N connections or ended up with N after a more highly connected sibling node was removed before now)
		Assert.assertEquals("a->[b:edge1,c:edge2,d:edge3,e:edge4]", g.next().toString());
		
		// then the next most connected one (l: 3)
		// note that 'g', which started with a connectivity of 4, is now 2, so won't be popped yet
		Assert.assertEquals("l->[k:edge11,m:edge12,n:edge13]", g.next().toString());

		// etc
		Assert.assertEquals("g->[f:edge7,z]", g.next().toString());
		Assert.assertEquals("i->[h]", g.next().toString());
		
		Assert.assertFalse(g.hasNext());
		try{
			g.next();
			Assert.fail("should have thrown IllegalStateException");
		} catch (IllegalStateException e){}
	}
}
