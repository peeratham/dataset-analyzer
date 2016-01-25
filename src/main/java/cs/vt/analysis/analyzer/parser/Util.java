package cs.vt.analysis.analyzer.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.jsoup.Jsoup;

public class Util {
	public static final String baseDownLoadURL = "http://projects.scratch.mit.edu/internalapi/project/%1$d/get/";
	
	
		public static String readFile(String path) throws IOException {
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, StandardCharsets.UTF_8);
		}
	
		// convert InputStream to String
		public static String getStringFromInputStream(InputStream is) {

			BufferedReader br = null;
			StringBuilder sb = new StringBuilder();

			String line;
			try {

				br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			return sb.toString();

		}
		
		public static File[] getFileListing(String path){
			File folder = new File(path);
			File[] listOfFiles = folder.listFiles();

		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
		        System.out.println("File " + listOfFiles[i].getName());
		      } else if (listOfFiles[i].isDirectory()) {
		        System.out.println("Directory " + listOfFiles[i].getName());
		      }
		    }
		    
		    return listOfFiles;
		}
		

		
		public static String retrieveProjectOnline(int projectID) throws IOException{
			
			String URL = String.format(baseDownLoadURL,projectID);
			String doc = Jsoup.connect(URL).ignoreContentType(true).execute().body();
			return doc;
		}
		
		

}
