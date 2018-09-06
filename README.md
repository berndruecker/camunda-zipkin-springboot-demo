

* Download https://search.maven.org/remote_content?g=io.zipkin.java&a=zipkin-server&v=LATEST&c=exec[Zipkin Server via Maven]
* Run

```java -jar zipkin-server-1.30.3-exec.jar```

* Look at http://localhost:9411/zipkin/.
* See http://zipkin.io/pages/quickstart.html[Quickstart] for more details, e.g. how to run via Docker


# Thoughts / Open discussions

- Which requests to sample? Can we start when we have incidents? But that's probably too late? 