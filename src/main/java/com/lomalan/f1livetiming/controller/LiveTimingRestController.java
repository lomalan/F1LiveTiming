package com.lomalan.f1livetiming.controller;

import com.lomalan.f1livetiming.service.LiveTimingService;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/live")
public class LiveTimingRestController {

  private final LiveTimingService liveTimingService;

  public LiveTimingRestController(LiveTimingService liveTimingService) {
    this.liveTimingService = liveTimingService;
  }

  @GetMapping("/data")
  public ResponseEntity<String> getLiveData() {
    Optional<String> raceInfo = liveTimingService.getRaceInfo();
    if (raceInfo.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(raceInfo.get());
  }
}
