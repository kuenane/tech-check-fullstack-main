package com.ikhokha.techcheck;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

public class CommentAnalyzer implements Runnable {

  // Used to check url on string
  private static final String URL_REGEX =
      "((http|https)://)(www.)?[a-zA-Z0-9@:%._\\+~#?&//=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%._\\+~#?&//=]*)";

  // Metrics list for reporting
  final static List<CommentQuestion> commentQuestionList = initializeMetrics();

  private File file;

  private ConcurrentMap<String, Integer> totalResults;

  public CommentAnalyzer(final File file, final ConcurrentMap<String, Integer> totalResults) {
    this.file = file;
    this.totalResults = totalResults;
  }

  @Override
  public void run() {

    final Map<String, Integer> resultsMap = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

      String line = null;
      while ((line = reader.readLine()) != null) {

        for (CommentQuestion commentQuestion : commentQuestionList) {
          if (commentQuestion.getValue().test(line)) {
            incOccurrence(resultsMap, commentQuestion.getKey());
          }
        }
      }

    } catch (FileNotFoundException e) {
      System.out.println("File not found: " + file.getAbsolutePath());
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("IO Error processing file: " + file.getAbsolutePath());
      e.printStackTrace();
    }

    // add count results into total results sequential
    synchronized (totalResults) {
      addReportResults(resultsMap, totalResults);
    }
  }

  /**
   * Initialize metrics for creating comment reports This method can be used to read metrics from
   * data source
   * 
   * @return List of metrics
   */
  private static List<CommentQuestion> initializeMetrics() {
    List<CommentQuestion> commentQuestions = new ArrayList<CommentQuestion>();

    commentQuestions.add(new CommentQuestion("SHORTER_THAN_15").value(line -> line.length() < 15));
    commentQuestions.add(
        new CommentQuestion("MOVER_MENTIONS").value(line -> line.toLowerCase().contains("mover")));
    commentQuestions.add(new CommentQuestion("SHAKER_MENTIONS")
        .value(line -> line.toLowerCase().contains("shaker")));
    commentQuestions.add(new CommentQuestion("QUESTIONS").value(line -> line.contains("?")));
    commentQuestions.add(
        new CommentQuestion("SPAM").value(line -> Pattern.compile(URL_REGEX).matcher(line).find()));

    return commentQuestions;
  }

  /**
   * This method adds the result counts from a source map to the target map
   * 
   * @param source the source map
   * @param target the target map
   */
  private static void addReportResults(Map<String, Integer> source, Map<String, Integer> target) {

    for (Map.Entry<String, Integer> entry : source.entrySet()) {
      target.putIfAbsent(entry.getKey(), 0);
      target.put(entry.getKey(), target.get(entry.getKey()) + entry.getValue());
    }

  }

  /**
   * This method increments a counter by 1 for a match type on the countMap. Uninitialized keys will
   * be set to 1
   * 
   * @param countMap the map that keeps track of counts
   * @param key the key for the value to increment
   */
  private void incOccurrence(Map<String, Integer> countMap, String key) {

    countMap.putIfAbsent(key, 0);
    countMap.put(key, countMap.get(key) + 1);
  }

public Map<String, Integer> analyze() {
    return null;
}

}
