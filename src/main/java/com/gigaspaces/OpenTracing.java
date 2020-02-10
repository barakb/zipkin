package com.gigaspaces;


import brave.Tracing;
import brave.opentracing.BraveTracer;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

// https://github.com/opentracing-contrib/java-opentracing-walkthrough/tree/master/microdonuts
// https://github.com/yurishkuro/opentracing-tutorial/tree/master/java

public class OpenTracing {
    public static void main(String[] args) throws Exception {
        Properties config = loadConfig(args);
        if (!configureGlobalTracer(config, "HelloOpenTracing"))
            throw new Exception("Could not configure the global tracer");
        try{
            twoCalls();
        }finally {
            Thread.sleep(1000);
            if(GlobalTracer.isRegistered()) {
                GlobalTracer.get().close();
            }
        }
    }
    private static void twoCalls() throws InterruptedException {
        Tracer tracer = GlobalTracer.get();
        Span span = tracer.buildSpan("twoCalls").start();
        //noinspection unused
        try (Scope scope = tracer.scopeManager().activate(span)) {
//            Thread.sleep(500);
//            fact(6);
//            fact(6);
            fib(7);
        } catch (Exception e) {
            span.log(e.toString());
            throw e;
        } finally {
            // Optionally finish the Span if the operation it represents
            // is logically completed at this point.
            span.finish();
        }
    }

    private static int fact(int n) {
        Tracer tracer = GlobalTracer.get();
        Span span = tracer.buildSpan("fact").start();
        //noinspection unused
        try (Scope scope = tracer.scopeManager().activate(span)) {
            span.setTag("n", String.valueOf(n));
            span.setOperationName("fact");
            if (n == 0) {
                return 1;
            } else {
                return fact(n - 1) * n;
            }
        } catch (Exception e) {
            span.log(e.toString());
            throw e;
        } finally {
            // Optionally finish the Span if the operation it represents
            // is logically completed at this point.
            span.finish();
        }
    }

    static int fib(int n)
    {
        Tracer tracer = GlobalTracer.get();
        Span span = tracer.buildSpan("fib").start();
        span.setTag("n", String.valueOf(n));
        //noinspection unused
        try (Scope scope = tracer.scopeManager().activate(span)) {
            if (n <= 1)
                return n;
            return fib(n-1) + fib(n-2);
        } catch (Exception e) {
            span.log(e.toString());
            throw e;
        } finally {
            // Optionally finish the Span if the operation it represents
            // is logically completed at this point.
            span.finish();
        }
    }

    static Properties loadConfig(String [] args) throws IOException
    {
        String file = "tracer_config.properties";
        if (args.length > 0)
            file = args[0];

        FileInputStream fs = new FileInputStream(file);
        Properties config = new Properties();
        config.load(fs);
        return config;
    }


    static boolean configureGlobalTracer(Properties config, @SuppressWarnings("SameParameterValue") String componentName) {
        String tracerName = config.getProperty("tracer");
        Tracer tracer = null;
        if ("zipkin".equals(tracerName)){
            OkHttpSender sender = OkHttpSender.create(
                    "http://" +
                            config.getProperty("zipkin.reporter_host") + ":" +
                            config.getProperty("zipkin.reporter_port") + "/api/v2/spans");
            Reporter<zipkin2.Span> reporter = AsyncReporter.builder(sender).build();
            tracer = BraveTracer.create(Tracing.newBuilder()
                    .localServiceName(componentName)
                    .spanReporter(reporter)
                    .build());
        } else {
            return false;
        }
        GlobalTracer.registerIfAbsent(tracer);
        return true;
    }
}

