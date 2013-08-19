package com.thaze.graphconnectivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;


public class FamilyIdentifier {

	public static final String XCORR_POSTPROCESS_FILE = "xcorr.postprocess";
	public static final String XCORR_FAMILY_FILE = "xcorr.families";

	public static void main(String[] args) throws IOException {
		
		if (args.length != 2){
			System.err.println("Expected arguments: <input file> <output file>");
			System.exit(0);
		}
		
		String input = args[0];
		if (!new File(input).exists()){
			System.err.println("input file " + input + " not found");
			System.exit(0);
		}
		String output = args[1];
		
		MostConnectedVertexGraphIterator.Builder b = MostConnectedVertexGraphIterator.newBuilder();
		
		

		try (	BufferedReader br = new BufferedReader(new FileReader(input)); 
				BufferedWriter bw = new BufferedWriter(new FileWriter(output)); ) {
			String line;
			
			System.out.println("reading file " + input);
			while (null != (line = br.readLine())) {

				String[] sa = line.split("\t", 3);
				if (sa.length < 2) {
					System.err.println("line invalid, expected tab-separated event names: '" + line + "'");
					System.exit(0);
				}

				// optional label - everything after the second column
				String label = sa.length > 2 ? sa[2] : null;
				b.addEdge(sa[0], sa[1], label);
			}
			
			System.out.println("iterating by most connected vertex ...");
			
			MostConnectedVertexGraphIterator g = b.build();
			
			while (g.hasNext()){
				Vertex v = g.next();
				
				// tab-separated, one family per line
//				bw.write(v.getName() + "\t");
//				for (Vertex o: v.getEdges())
//					bw.write(o.getName() + "\t");
//				bw.write('\n');
				
				// one item per line, indented
				bw.write(v.getName() + " " + v.getConnectionCount() + "\n");
				for (Map.Entry<Vertex, String> e: v.getEdgeLabels())
					bw.write("\t" + e.getKey().getName() + "\t" + e.getValue() + "\n");
				
//				System.out.println(v.getName() + " " + v.getConnectionCount());
			}
		}
		
		System.out.println("done");
	}
}
