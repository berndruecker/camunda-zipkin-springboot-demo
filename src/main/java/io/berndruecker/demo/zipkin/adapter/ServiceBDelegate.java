package io.berndruecker.demo.zipkin.adapter;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ServiceBDelegate implements JavaDelegate {

  @Autowired
  private RestTemplate rest;
  private String serviceBUrl = "http://localhost:8080/serviceA";

  public void execute(DelegateExecution ctx) throws Exception {
    String response = rest.getForObject(serviceBUrl, String.class);
    ctx.setVariable("serviceBResponse", response);
  }

}
