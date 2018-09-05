package io.berndruecker.demo.zipkin.adapter;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ServiceADelegate implements JavaDelegate {

  @Autowired
  private RestTemplate rest;
  private String serviceAUrl = "http://localhost:8080/serviceA";

  public void execute(DelegateExecution ctx) throws Exception {
    String response = rest.getForObject(serviceAUrl, String.class);
    ctx.setVariable("serviceAResponse", response);
  }

}
