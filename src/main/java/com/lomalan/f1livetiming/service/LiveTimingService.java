package com.lomalan.f1livetiming.service;

import static com.lomalan.f1livetiming.client.html.LiveTimingVariableStorage.RACE_COMPLETE_STATUS;
import static com.lomalan.f1livetiming.controller.message.MessageConstructor.constructMessage;

import com.lomalan.f1livetiming.client.LiveTimingHtmlClient;
import com.lomalan.f1livetiming.client.LiveTimingRestClient;
import com.lomalan.f1livetiming.model.LiveTimingInfo;
import com.lomalan.f1livetiming.model.RaceType;
import com.lomalan.f1livetiming.model.SessionInfo;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class LiveTimingService {

  private final LiveTimingHtmlClient liveTimingHtmlClient;
  private final LiveTimingRestClient liveTimingRestClient;

  public LiveTimingService(LiveTimingHtmlClient liveTimingHtmlClient,
      LiveTimingRestClient liveTimingRestClient) {
    this.liveTimingHtmlClient = liveTimingHtmlClient;
    this.liveTimingRestClient = liveTimingRestClient;
  }

  public Optional<String> getRaceInfo() {
    return getLiveInfo();
  }

  private Optional<String> getLiveInfo() {
    SessionInfo sessionInfo = liveTimingRestClient.getCurrentSessionInfo();
    if (isNotValidToProceed(sessionInfo)) {
      return Optional.empty();
    }
    Optional<LiveTimingInfo> liveTimingInfo = liveTimingHtmlClient.getLiveTimingInfo();
    return constructMessage(liveTimingInfo);
  }

  private boolean isNotValidToProceed(SessionInfo sessionInfo) {
    return sessionInfo.getStatus().equals(RACE_COMPLETE_STATUS)
        || sessionInfo.getType().equals(RaceType.PRACTICE.getValue());
  }
}
