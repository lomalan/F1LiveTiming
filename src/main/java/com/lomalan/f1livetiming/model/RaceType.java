package com.lomalan.f1livetiming.model;

public enum RaceType {

  RACE("Race"),
  QUALIFICATION("Qualification"),
  PRACTICE("Practice");

  private final String value;

  RaceType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
