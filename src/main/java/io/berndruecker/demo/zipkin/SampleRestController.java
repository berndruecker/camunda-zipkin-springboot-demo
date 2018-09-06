package io.berndruecker.demo.zipkin;


import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import brave.Tracing;
import brave.propagation.TraceContext;

@RestController
public class SampleRestController {
  
  @Autowired
  private ProcessEngine camunda;
  
  @Autowired
  private RestTemplate rest;
  
  @Autowired
  private Tracing tracing;
  
  @RequestMapping(path = "/test", method = PUT)
  public String placeOrder(@RequestParam(value = "someParam") String someParam) {

    // this would be in the right trace:
    // rest.getForObject("http://localhost:8080/serviceA", String.class);
    
    VariableMap variables = Variables.createVariables();
    
    TraceContext context = captureTracingContext(variables);
    
    camunda.getRuntimeService().startProcessInstanceByKey("sample", variables);

    return "{\"traceId\": \"" + context.traceIdString() + "\"}";
  }

  private TraceContext captureTracingContext(VariableMap variables) {
    TraceContext context = tracing.currentTraceContext().get();
    variables
      .putValue("X-SLEUTH-TRACE-ID", context.traceId())
      .putValue("X-SLEUTH-TRACE-ID-HIGH", context.traceIdHigh())
      .putValue("X-SLEUTH-SPAN-ID", context.spanId());
    return context;
  }

}