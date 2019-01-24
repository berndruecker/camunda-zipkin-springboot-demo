package io.berndruecker.demo.zipkin.adapter;

import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import brave.Span;
import brave.Tracing;
import brave.propagation.TraceContext.Extractor;

@Component
public class ServiceBDelegate implements JavaDelegate {

  @Autowired
  private RestTemplate rest;
  private String serviceBUrl = "http://localhost:8080/serviceB";
  
  @Autowired
  private Tracing tracing;

  public void execute(DelegateExecution ctx) throws Exception {
    Span span = restoreTracingContext(ctx);

    String response = rest.getForObject(serviceBUrl, String.class);
    ctx.setVariable("serviceBResponse", response);
    
//    span.finish();
  }
  
  private Span restoreTracingContext(DelegateExecution ctx) {
    Map<String, String> tracingContextSerialized = (Map<String, String>) ctx.getVariable("X-SLEUTH-TRACE-CONTEXT");
    Extractor<Map<String, String>> extractor = tracing.propagation().extractor(Map<String, String>::get);
    Span span = tracing.tracer().toSpan(extractor.extract(tracingContextSerialized).context());
//    Span span = tracing.tracer().nextSpan(extractor.extract(tracingContextSerialized));
    tracing.tracer().withSpanInScope(span);
    return span;
  }  

}
