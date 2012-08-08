package com.thaze.graphconnectivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class FamilyIndentifier {

	public static final String XCORR_POSTPROCESS_FILE = "/home/srodgers/devel/users/srodgers/peakmatch/family/xcorr.postprocess";
	public static final String XCORR_FAMILY_FILE = "/home/srodgers/devel/users/srodgers/peakmatch/family/xcorr.families";

	public static void main(String[] args) throws IOException {
		
		MostConnectedVertexGraphIterator.Builder b = MostConnectedVertexGraphIterator.newBuilder();

		try (	BufferedReader br = new BufferedReader(new FileReader(XCORR_POSTPROCESS_FILE)); 
				BufferedWriter bw = new BufferedWriter(new FileWriter(XCORR_FAMILY_FILE)); ) {
			String line;
			while (null != (line = br.readLine())) {

				String[] sa = line.split("\t");
				if (sa.length != 3) {
					System.err.println("line invalid: '" + line + "'");
					continue;
				}

				b.addEdge(sa[0], sa[1]);
			}
			
			MostConnectedVertexGraphIterator g = b.build();
			
			while (g.hasNext()){
				Vertex v = g.next();
				// tab-separated, one family per line
//				bw.write(v.getName() + "\t");
//				for (Vertex o: v.getConnections())
//					bw.write(o.getName() + "\t");
//				bw.write('\n');
				// family indented
				bw.write(v.getName() + " " + v.getConnectionCount() + "\n");
				for (Vertex o: v.getConnections())
					bw.write("\t" + o.getName() + "\n");
				
//				System.out.println(v.getName() + " " + v.getConnectionCount());
			}
		}
		
		System.out.println("done");
	}
}
