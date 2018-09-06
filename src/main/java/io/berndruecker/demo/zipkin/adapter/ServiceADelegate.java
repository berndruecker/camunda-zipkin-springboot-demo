package io.berndruecker.demo.zipkin.adapter;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContext;

@Component
public class ServiceADelegate implements JavaDelegate {

  @Autowired
  private RestTemplate rest;
  private String serviceAUrl = "http://localhost:8080/serviceA";

  @Autowired
  private Tracing tracing;

  public void execute(DelegateExecution ctx) throws Exception {
    
    restoreTracingContext(ctx);
    
    // But this is in a disconnected trace
    String response = rest.getForObject(serviceAUrl, String.class);
    ctx.setVariable("serviceAResponse", response);
    
  }

  private void restoreTracingContext(DelegateExecution ctx) {
    long traceId = (long) ctx.getVariable("X-SLEUTH-TRACE-ID");
    long traceIdHigh = (long) ctx.getVariable("X-SLEUTH-TRACE-ID-HIGH");
    long spanId = (long) ctx.getVariable("X-SLEUTH-SPAN-ID");
    
    TraceContext traceContext = TraceContext.newBuilder()
        .traceId(traceId)
        .spanId(spanId)
        .build();
    
    tracing.tracer().joinSpan(traceContext);
  }

}
