package io.berndruecker.demo.zipkin.fakeservices;


import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FakeServicesRestController {  
  
  @RequestMapping(path = "/serviceA", method = GET)
  public String getSomethingFromServiceA() throws InterruptedException {
    Thread.sleep(100);
    return "Hello from Service A";
  }

  @RequestMapping(path = "/serviceB", method = GET)
  public String getSomethingFromServiceB() throws InterruptedException {
    Thread.sleep(100);
    return "Hello from Service B";
  }

}