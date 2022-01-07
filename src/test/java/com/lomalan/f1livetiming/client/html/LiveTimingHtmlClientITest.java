package com.lomalan.f1livetiming.client.html;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lomalan.f1livetiming.client.LiveTimingHtmlClient;
import com.lomalan.f1livetiming.model.LiveTimingInfo;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class LiveTimingHtmlClientITest {

  private final LiveTimingHtmlClient client = new LiveTimingHtmlClient("https://live.planetf1.com/liverace");

  @Test
  void liveTimingInfoSuccessPath() {
    //when
    Optional<LiveTimingInfo> liveTimingInfo = client.getLiveTimingInfo();
    //then
    assertTrue(liveTimingInfo.isPresent());
    LiveTimingInfo info = liveTimingInfo.get();
    assertNotNull(info.getLapStatus());
    assertNotNull(info.getRaceName());
    assertFalse(info.getDriverInfo().isEmpty());
    assertTrue(info.getLapStatus().contains("Status"));
    assertTrue(info.getLapStatus().contains("of"));
    assertTrue(info.getRaceName().contains("Grand Prix"));
    assertTrue(info.getDriverInfo().size() <= 20);
    assertEquals("1", info.getDriverInfo().get(0).getPosition());
  }
}
