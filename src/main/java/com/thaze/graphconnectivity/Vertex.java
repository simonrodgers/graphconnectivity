package com.thaze.graphconnectivity;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.Map;

public class Vertex {
	private final String _name;
//	private final Set<Vertex> edges = Sets.newLinkedHashSet();
	private final Map<Vertex, String> edges = Maps.newLinkedHashMap();

	private final static String NO_LABEL = "NO_LABEL";

	public Vertex(String name) {
		_name = name;
	}
	
	public String getName(){
		return _name;
	}
	
	public Iterable<Vertex> getEdges(){
		return Collections.unmodifiableSet(edges.keySet());
	}

	public Iterable<Map.Entry<Vertex, String>> getEdgeLabels(){
		return Collections.unmodifiableSet(edges.entrySet());
	}
	
	public int getConnectionCount(){
		return edges.size();
	}

	protected void add(Vertex other, String edgeLabel) {
		edges.put(other, edgeLabel == null ? NO_LABEL : edgeLabel);
	}

	protected boolean remove(Vertex v){
		return edges.remove(v) != null;
	}

	@Override
	public int hashCode() {
		return _name.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return _name.equals(((Vertex)o)._name);
	}
	
	@Override
	public String toString(){

		return _name + "->[" + StringUtils.join(Collections2.transform(edges.entrySet(), new Function<Map.Entry<Vertex, String>, String>(){
			public String apply(Map.Entry<Vertex, String> e) {return e.getKey()._name + (e.getValue() == NO_LABEL ? "" : (":" + e.getValue()));}
		}), ",") + "]";
		
//			return Array.iterableArray(edges).foldLeft(Function.curry(new F2<String, Vertex, String>(){
//				@Override
//				public String f(String a, Vertex b) {
//					return a + b._name;
//				}
//			}), _name + "->");
	}
}