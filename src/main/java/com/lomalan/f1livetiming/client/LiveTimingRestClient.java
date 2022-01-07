package com.lomalan.f1livetiming.client;

import com.lomalan.f1livetiming.model.SessionInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class LiveTimingRestClient {

  private final RestTemplate restTemplate;

  @Value("${live.timing.api}")
  private String liveTimingApi;

  private static final String SESSION_INFO = "SessionInfo.json";

  public LiveTimingRestClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public SessionInfo getCurrentSessionInfo() {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(liveTimingApi.concat(SESSION_INFO));
    HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());
    return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, SessionInfo.class).getBody();
  }

}
