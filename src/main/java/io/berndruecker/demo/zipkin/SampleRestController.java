package io.berndruecker.demo.zipkin;


import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class SampleRestController {
  
  @Autowired
  private ProcessEngine camunda;
  
  @Autowired
  private RestTemplate rest;
  
  @RequestMapping(path = "/test", method = PUT)
  public String placeOrder(@RequestParam(value = "someParam") String someParam) {
    
    rest.getForObject("http://localhost:8080/serviceA", String.class);

    camunda.getRuntimeService().startProcessInstanceByKey("sample");

    return "{\"traceId\": \"" + "-" + "\"}";
  }

}