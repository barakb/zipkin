package com.gigaspaces;

//import brave.Tracing;
//import zipkin2.Span;
//import zipkin2.reporter.AsyncReporter;
//import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * Hello world!
 *
 */
public class App {

    public static void main( String[] args ) {
//        OkHttpSender sender = OkHttpSender.create("http://127.0.0.1:9411/api/v2/spans");
//        AsyncReporter<Span> spanReporter = AsyncReporter.create(sender);
//        Tracing tracing = Tracing.newBuilder()
//                .localServiceName("App")
//                .spanReporter(spanReporter)
//                .build();
        System.out.println( "Hello World!" );
    }
}
