package com.lomalan.f1livetiming.client;

import static com.lomalan.f1livetiming.client.html.LiveTimingVariableStorage.DRIVERS_INFO_SECTION_NAME;
import static com.lomalan.f1livetiming.client.html.LiveTimingVariableStorage.RACE_NAME_SECTION_NAME;
import static com.lomalan.f1livetiming.client.html.LiveTimingVariableStorage.RACE_STATUS_SECTION_NAME;
import static com.lomalan.f1livetiming.client.html.LiveTimingVariableStorage.SCHUMACHER_SHORT_NAME;
import static com.lomalan.f1livetiming.client.html.LiveTimingVariableStorage.SCHUMACHER_SHORT_NAME_REPLACEMENT;

import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.javascript.SilentJavaScriptErrorListener;
import com.lomalan.f1livetiming.model.DriverInfo;
import com.lomalan.f1livetiming.model.LiveTimingInfo;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LiveTimingHtmlClient {

  private final String liveTimingUrl;

  public LiveTimingHtmlClient(@Value("${live.f1planet.url}") String liveTimingUrl) {
    this.liveTimingUrl = liveTimingUrl;
  }

  public Optional<LiveTimingInfo> getLiveTimingInfo() {
    try (WebClient webClient = new WebClient()) {
      setupWebClient(webClient);
      HtmlPage page = webClient.getPage(liveTimingUrl);
      webClient.waitForBackgroundJavaScriptStartingBefore(10000);
      Pair<String, String> raceNameAndStatus = getRaceNameAndStatus(page);
      List<DriverInfo> driverInfoTwos = getDriversInfo(page);
      return Optional
          .of(new LiveTimingInfo(raceNameAndStatus.getLeft(), raceNameAndStatus.getRight(), driverInfoTwos));
    } catch (IOException exc) {
      log.error(exc.getMessage());
    }

    return Optional.empty();
  }

  private List<DriverInfo> getDriversInfo(HtmlPage page) {
    HtmlTable driversTable = page.getHtmlElementById(DRIVERS_INFO_SECTION_NAME);

    return driversTable.getRows().stream()
        .map(this::mapToDriverInfo)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private void setupWebClient(WebClient webClient) {
    webClient.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());
    webClient.setIncorrectnessListener((s, o) -> {
      //empty implementation to avoid redundant log messages
    });

    webClient.setCssErrorHandler(new SilentCssErrorHandler());
    webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
    webClient.getOptions().setThrowExceptionOnScriptError(false);
    webClient.getOptions().setCssEnabled(false);
  }

  private Pair<String, String> getRaceNameAndStatus(HtmlPage page) {
    String raceName = page.getHtmlElementById(RACE_NAME_SECTION_NAME).asNormalizedText();
    List<Object> byXPath = page.getByXPath(RACE_STATUS_SECTION_NAME);
    if (byXPath.isEmpty()) {
      return Pair.of(raceName, StringUtils.EMPTY);
    }
    HtmlSpan htmlSpan = (HtmlSpan) byXPath.get(0);
    String raceStatus = htmlSpan.getChildElements().iterator().next().asNormalizedText();
    return Pair.of(raceName, raceStatus);
  }

  private DriverInfo mapToDriverInfo(HtmlTableRow row) {
    if (row.getCells().get(0).asNormalizedText().equals("POS")) {
      return null;
    }
    return new DriverInfo(row.getCells().get(0).asNormalizedText(),
        getDriverName(row.getCells().get(1).asNormalizedText()),
        row.getCells().get(3).asNormalizedText(),
        row.getCells().get(4).asNormalizedText());
  }

  private  String getDriverName(String stringValue) {
    if (stringValue.contains(StringUtils.SPACE)) {
      return getDriverShortName(stringValue);
    }
    return stringValue;
  }

  private String getDriverShortName(String stringValue) {
    String shortName = stringValue.split(StringUtils.SPACE)[1].substring(0, 3).toUpperCase(Locale.ROOT);
    if (shortName.equals(SCHUMACHER_SHORT_NAME)) {
      return SCHUMACHER_SHORT_NAME_REPLACEMENT;
    }
    return shortName;
  }
}
