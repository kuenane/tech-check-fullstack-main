package com.ikhokha.techcheck;

import java.util.function.Predicate;

public class CommentQuestion {

  private String key;
  private Predicate<String> value;

  public CommentQuestion(String key) {
    super();
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public Predicate<String> getValue() {
    return value;
  }

  public void setValue(Predicate<String> value) {
    this.value = value;
  }

  public CommentQuestion value(final Predicate<String> value) {
    this.setValue(value);
    return this;
  }
}
