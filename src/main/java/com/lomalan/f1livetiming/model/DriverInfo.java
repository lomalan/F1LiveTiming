package com.lomalan.f1livetiming.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DriverInfo {
  private final String position;
  private final String name;
  private final String gap;
  private final String stops;
}
