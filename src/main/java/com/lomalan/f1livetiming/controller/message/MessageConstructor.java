package com.lomalan.f1livetiming.controller.message;

import com.lomalan.f1livetiming.model.DriverInfo;
import com.lomalan.f1livetiming.model.LiveTimingInfo;
import java.util.Optional;
import java.util.stream.Collectors;

public class MessageConstructor {

  private MessageConstructor() {}

  public static Optional<String> constructMessage(Optional<LiveTimingInfo> liveTimingOptional) {
    if (liveTimingOptional.isEmpty()) {
      return Optional.empty();
    }
    LiveTimingInfo liveTimingInfo = liveTimingOptional.get();
    return Optional.of(liveTimingInfo.getRaceName().concat("\n\n")
        .concat(liveTimingInfo.getLapStatus()).concat("\n\n")
        .concat("P. Driver Time(Gap) Stops").concat("\n\n")
        .concat(liveTimingInfo.getDriverInfo().stream()
            .map(MessageConstructor::processDriverData)
            .collect(Collectors.joining("\n"))));
  }


  private static String processDriverData(DriverInfo info) {
    return info.getPosition().concat(". ")
        .concat(info.getName()).concat(" ")
        .concat(info.getGap().equals("") ? "+0.000" : info.getGap()).concat(" ")
        .concat(info.getStops());
  }
}
