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
import brave.propagation.TraceContextOrSamplingFlags;

@Component
public class EndListener implements JavaDelegate {

  @Autowired
  private Tracing tracing;

  public void execute(DelegateExecution ctx) throws Exception {

    Map<String, String> tracingContextSerialized = (Map<String, String>) ctx.getVariable("X-SLEUTH-TRACE-CONTEXT");
    Extractor<Map<String, String>> extractor = tracing.propagation().extractor(Map<String, String>::get);
    TraceContextOrSamplingFlags extract = extractor.extract(tracingContextSerialized);
    
//    Span span = tracing.tracer().joinSpan(extract.context());
    Span span = tracing.tracer().toSpan(extract.context());
    tracing.tracer().withSpanInScope(span);
//    tracing.tracer().currentSpanCustomizer();
    // NOPE - that would be a new span: Span span = tracing.tracer().nextSpan(extract);
//    span.annotate("end");
//    span.flush();

    span.finish();
  }  

}
