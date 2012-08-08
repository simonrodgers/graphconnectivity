package com.thaze.graphconnectivity;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

public class Vertex {
	private final String _name;
	private final Set<Vertex> connections = Sets.newLinkedHashSet();

	public Vertex(String name) {
		_name = name;
	}
	
	public String getName(){
		return _name;
	}
	
	public Iterable<Vertex> getConnections(){
		return Collections.unmodifiableSet(connections);
	}
	
	public int getConnectionCount(){
		return connections.size();
	}
	
	protected void add(Vertex other) {
		connections.add(other);
	}
	protected boolean remove(Vertex v){
		return connections.remove(v);
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
		
		return _name + "->" + StringUtils.join(Collections2.transform(connections, new Function<Vertex, String>(){
			public String apply(Vertex v) {return v._name;}
		}), "");
		
//			return Array.iterableArray(connections).foldLeft(Function.curry(new F2<String, Vertex, String>(){
//				@Override
//				public String f(String a, Vertex b) {
//					return a + b._name;
//				}
//			}), _name + "->");
	}
}