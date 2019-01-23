package io.berndruecker.demo.zipkin;


import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import brave.ScopedSpan;
import brave.Span;
import brave.SpanCustomizer;
import brave.Tracing;
import brave.propagation.TraceContext.Injector;

@RestController
public class SampleRestController {
  
  @Autowired
  private ProcessEngine camunda;
  
  @Autowired
  private RestTemplate rest;
  
  @Autowired
  private Tracing tracing;
  
  @RequestMapping(path = "/test", method = PUT)
  public String placeOrder() {

    // this would be in the right trace:
//     rest.getForObject("http://localhost:8080/serviceB", String.class);
    
    VariableMap variables = Variables.createVariables();
        
    startWorkflowInstance(variables);

    return ""; //"{\"traceId\": \"" + context.traceIdString() + "\"}";
  }
  
  @Autowired
  private Tracing tracer;
  
//  @NewSpan
  private void startWorkflowInstance(VariableMap variables) {
//    ScopedSpan newSpan = tracer.tracer().startScopedSpan("workflowInstance");
//    tracer.tracer().currentSpan().
    Span span = this.tracer.tracer().nextSpan().name("workflowInstance");
//    this.tracer.tracer().joinSpan(span.context());
   
    Map<String,String> tracingContextSerialized = new HashMap<String,String>();
    Injector<Map<String,String>> injector = tracing.propagation().injector(Map<String,String>::put);
    injector.inject(span.context(), tracingContextSerialized);
    variables.putValue("X-SLEUTH-TRACE-CONTEXT", tracingContextSerialized);

   
    span.start();
//    span.annotate("start");
    span.flush();
    
    // capture with new span
//    captureTracingContext(variables);

    camunda.getRuntimeService().startProcessInstanceByKey("sample", variables);
   // newSpan.finish();
  }

  private void captureTracingContext(VariableMap variables) {
    Map<String, String> serializeTracingContext = serializeTracingContext();
    variables.putValue("X-SLEUTH-TRACE-CONTEXT", serializeTracingContext);
    
    // JUst FYI
/*    TraceContext context = tracing.currentTraceContext().get();
    variables
      .putValue("X-SLEUTH-TRACE-ID", context.traceId())
      .putValue("X-SLEUTH-TRACE-ID-HIGH", context.traceIdHigh())
      .putValue("X-SLEUTH-SPAN-ID", context.spanId());*/
  }  
  
  private Map<String,String> serializeTracingContext() {      
    Map<String,String> tracingContextSerialized = new HashMap<String,String>();
    Injector<Map<String,String>> injector = tracing.propagation().injector(Map<String,String>::put);
    injector.inject(tracing.currentTraceContext().get(), tracingContextSerialized);
    return tracingContextSerialized;
  }


}