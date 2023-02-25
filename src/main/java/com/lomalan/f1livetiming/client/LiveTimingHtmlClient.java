package com.lomalan.f1livetiming.client;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import static com.lomalan.f1livetiming.client.html.LiveTimingVariableStorage.DRIVERS_INFO_SECTION_NAME;
import static com.lomalan.f1livetiming.client.html.LiveTimingVariableStorage.RACE_DATA_SECTION_NAME;
import static com.lomalan.f1livetiming.client.html.LiveTimingVariableStorage.SCHUMACHER_SHORT_NAME;
import static com.lomalan.f1livetiming.client.html.LiveTimingVariableStorage.SCHUMACHER_SHORT_NAME_REPLACEMENT;

import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.javascript.SilentJavaScriptErrorListener;
import com.lomalan.f1livetiming.model.DriverInfo;
import com.lomalan.f1livetiming.model.LiveTimingInfo;
import java.io.IOException;
import java.util.Iterator;
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
      LiveTimingInfo liveTimingInfo = createLiveTimingInfo(page);
      log.info("Live timing info data: {}", liveTimingInfo);
      return Optional.of(liveTimingInfo);
    } catch (IOException exc) {
      log.error(exc.getMessage());
    }
    return Optional.empty();
  }

  private LiveTimingInfo createLiveTimingInfo(HtmlPage page) {
    Pair<String, String> raceAndCircuit = getRaceAndCircuitName(page);
    List<DriverInfo> driversInfo = getDriversInfo(page);
    return new LiveTimingInfo(raceAndCircuit.getLeft(), raceAndCircuit.getRight(), driversInfo);
  }
  private Pair<String, String> getRaceAndCircuitName(HtmlPage page) {
    Iterator<DomElement> iterator = getIteratorFromXPath(page.getByXPath(RACE_DATA_SECTION_NAME));
    //skipping first three elements
    iterator.next();iterator.next();iterator.next();
    String raceName = iterator.next().asNormalizedText();
    String circuitName = iterator.next().asNormalizedText();
    return Pair.of(raceName, circuitName);
  }

  private List<DriverInfo> getDriversInfo(HtmlPage page) {
    HtmlTable driversTable = getTableFromXPath(page.getByXPath(DRIVERS_INFO_SECTION_NAME));

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

  private DriverInfo mapToDriverInfo(HtmlTableRow row) {
    if (row.getCells().get(0).asNormalizedText().equalsIgnoreCase("POS")) {
      return null;
    }
    return new DriverInfo(row.getCells().get(0).asNormalizedText(),
        getDriverName(row.getCells().get(2).asNormalizedText()),
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

  private Iterator<DomElement> getIteratorFromXPath(List<Object> byXPath) {
    HtmlDivision htmlSpan = (HtmlDivision) byXPath.get(0);
    return htmlSpan.getChildElements().iterator();
  }

  private HtmlTable getTableFromXPath(List<Object> byXPath) {
    HtmlDivision htmlDivision = (HtmlDivision) byXPath.get(0);
    return (HtmlTable) htmlDivision.getChildElements().iterator().next();
  }
}
