package com.ikhokha.techcheck;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;
import java.util.concurrent.Future;  

public class Main {
	static void myThreads() {
		Map<String, Integer> results = new HashMap<String, Integer>();
		int maxThreads = 10; 
		String keywordsToFind = "mover,shaker"; 

		ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
		List<Future<Map<String, Integer>>> futures = new ArrayList<>();

		System.out.println(String.format("RUNNING REPORTS ON %s THREAD(S)", maxThreads));
				
		File docPath = new File("docs");
		File[] commentFiles = docPath.listFiles((d, n) -> n.endsWith(".txt"));

		System.out.println(String.format("%s DOCS FOUND", commentFiles.length));

		final long startTime = System.currentTimeMillis();
		
		for (File commentFile: commentFiles){
			Callable<Map<String, Integer>> commentAnalyzerWorker =  new CommentAnalyzer(commentFile, keywordsToFind);
			futures.add(executor.submit(commentAnalyzerWorker));
		}

		executor.shutdown();

		while (!executor.isTerminated()) { }

		for (Future<Map<String, Integer>> future : futures) {
			try {
				Map<String, Integer> commentFileResults = future.get();
				addReportResults(commentFileResults, results);
			} catch (Exception e) {
				e.printStackTrace();
			}
		 }
		
		System.out.println("RESULTS\n=======");
		results.forEach((k,v) -> System.out.println(k + " : " + v));
		System.out.println(String.format("Completed in %s ms", (System.currentTimeMillis() - startTime)));

	}
	
	public static void main(String[] args) {
		myThreads();
	}

	/**
	 * This method adds the result counts from a source map to the target map 
	 * @param source the source map
	 * @param target the target map
	 */
	private static void addReportResults(Map<String, Integer> source, Map<String, Integer> target) {

		for (Map.Entry<String, Integer> entry : source.entrySet()) {
			target.putIfAbsent(entry.getKey(), 0);
			target.put(entry.getKey(), target.get(entry.getKey()) + entry.getValue());
		}
		
	}

}
