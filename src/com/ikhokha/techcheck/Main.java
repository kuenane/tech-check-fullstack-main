package com.ikhokha.techcheck;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

  public static void main(String[] args) { 

    ConcurrentMap<String, Integer> totalResults = new ConcurrentHashMap<>();;

    File docPath = new File("docs");
    File[] commentFiles = docPath.listFiles((d, n) -> n.endsWith(".txt"));

    ExecutorService executor = Executors.newFixedThreadPool(10);

    for (File commentFile : commentFiles) {

      executor.submit(new CommentAnalyzer(commentFile, totalResults));
       CommentAnalyzer commentAnalyzer = new CommentAnalyzer(commentFile, totalResults);
       Map<String, Integer> fileResults = commentAnalyzer.analyze();
       addReportResults(fileResults, totalResults);

    }

    try {
      executor.shutdown();
      executor.awaitTermination(2, TimeUnit.MINUTES);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e.getCause());
    }

    System.out.println("RESULTS\n=======");
    totalResults.forEach((k, v) -> System.out.println(k + " : " + v));
  }

  /**
   * This method adds the result counts from a source map to the target map
   * 
   * @param source the source map
   * @param target the target map
   */
  private static void addReportResults(Map<String, Integer> source, Map<String, Integer> target) {

    for (Map.Entry<String, Integer> entry : source.entrySet()) {

      Integer i = 0;
      if (target.containsKey(entry.getKey())) {
        i = target.get(entry.getKey()) + entry.getValue();
      } else {
        i = entry.getValue();
      }

      target.put(entry.getKey(), i);
    }

  }

}
