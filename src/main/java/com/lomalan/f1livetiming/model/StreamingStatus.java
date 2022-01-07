package com.lomalan.f1livetiming.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StreamingStatus {
  @JsonProperty("Status")
  private String status;
}
