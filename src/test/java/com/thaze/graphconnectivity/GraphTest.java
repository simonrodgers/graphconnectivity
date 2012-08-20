package com.thaze.graphconnectivity;

import junit.framework.Assert;

import org.junit.Test;

import com.thaze.graphconnectivity.MostConnectedVertexGraphIterator;

public class GraphTest {
	
	@Test
	public void testMostConnectedVertexIterator(){
		MostConnectedVertexGraphIterator.Builder gb = MostConnectedVertexGraphIterator.newBuilder();

		gb.addEdge("a", "b");
		gb.addEdge("a", "c");
		gb.addEdge("a", "d");
		gb.addEdge("a", "e");
		gb.addEdge("e", "f");
		gb.addEdge("e", "g");
		gb.addEdge("f", "g");
		gb.addEdge("g", "z");
		gb.addEdge("d", "h");
		gb.addEdge("h", "i");
		
		gb.addEdge("k", "l");
		gb.addEdge("l", "m");
		gb.addEdge("l", "n");
		
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
//			for (Vertex other: v.getConnections())
//				System.out.println("\t" + other);			
//		}
		
		// first vertex popped will be the most connected one (a: 4), and all of its siblings will be removed
		// could have also been g, with a connectivity of 4
		// (aside: note that the order within the group is deterministic but not easily predictable) 
		// (depends on whether a node started with N connections or ended up with N after a more highly connected sibling node was removed before now)
		Assert.assertEquals("a->bcde", g.next().toString());
		
		// then the next most connected one (l: 3)
		// note that 'g', which started with a connectivity of 4, is now 2, so won't be popped yet
		Assert.assertEquals("l->kmn", g.next().toString());

		// etc
		Assert.assertEquals("g->fz", g.next().toString());
		Assert.assertEquals("i->h", g.next().toString());
		
		Assert.assertFalse(g.hasNext());
		try{
			g.next();
			Assert.fail("should have thrown IllegalStateException");
		} catch (IllegalStateException e){}
	}
}
