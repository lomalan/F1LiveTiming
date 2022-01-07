package com.lomalan.f1livetiming.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Data;

@Data
public class SessionInfo {
  @JsonProperty("Path")
  private String path;
  @JsonProperty("Type")
  private String type;
  private String status;

  @JsonProperty("ArchiveStatus")
  private void raceStatus(Map<String, Object> archiveStatus) {
    this.status = archiveStatus.get("Status").toString();
  }
}
