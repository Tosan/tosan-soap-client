package com.tosan.client.soap.handler;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.Set;

/**
 * @author a.ebrahimi
 * @since 5/24/2014
 */
public class MonitoringHandler implements SOAPHandler<SOAPMessageContext> {
    public static final String SOAP_REQUEST = "soap request";
    public static final String SOAP_OPERATION = "soap.operation";
    public static final String OTEL_SPAN = "otel.span";
    public static final String OTEL_SCOPE = "otel.scope";
    private static final String ENDPOINT_KEY = "jakarta.xml.ws.service.endpoint.address";
    public static final String HTTP_URL = "http.url";
    public static final String URI = "uri";
    public static final String OUTCOME = "outcome";
    public static final String ERROR = "ERROR";
    public static final String CLIENT_NAME = "client.name";
    public static final String SUCCESS = "SUCCESS";
    private static final Logger log = LoggerFactory.getLogger(MonitoringHandler.class);

    private final Tracer tracer;
    private final String clientName;

    public MonitoringHandler(Tracer tracer, String clientName) {
        this.tracer = tracer;
        this.clientName = clientName;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        Boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outbound) {
            startSpan(context);
        } else {
            finishSpan(context);
        }
        return true;
    }

    private void startSpan(SOAPMessageContext context) {
        QName operation = (QName) context.get(SOAPMessageContext.WSDL_OPERATION);
        String operationName = operation != null ? operation.getLocalPart() : "Unknown";
        Span span = tracer.spanBuilder(SOAP_REQUEST)
                .setSpanKind(SpanKind.CLIENT)
                .setParent(io.opentelemetry.context.Context.current())
                .startSpan();
        span.setAttribute(SOAP_OPERATION, operationName);
        Scope scope = span.makeCurrent();
        context.put(OTEL_SPAN, span);
        context.put(OTEL_SCOPE, scope);
        String url = context.get(ENDPOINT_KEY).toString();
        span.setAttribute(HTTP_URL, url);
        span.setAttribute(URI, url);
        String clientNameValue = clientName != null ? clientName : url;
        span.setAttribute(CLIENT_NAME, clientNameValue);
    }

    private void finishSpan(SOAPMessageContext context) {
        Span span = (Span) context.get(OTEL_SPAN);
        try (Scope scope = (Scope) context.get(OTEL_SCOPE)) {
            SOAPMessage message = context.getMessage();
            if (message != null && message.getSOAPBody().hasFault()) {
                span.setAttribute(OUTCOME, ERROR);
                span.setStatus(StatusCode.ERROR);
            } else {
                span.setAttribute(OUTCOME, SUCCESS);
                span.setStatus(StatusCode.OK);
            }
        } catch (SOAPException e) {
            span.setAttribute(OUTCOME, ERROR);
            span.setStatus(StatusCode.ERROR);
        } finally {
            span.end();
        }
    }

    @Override
    public void close(MessageContext context) {
        Scope scope = (Scope) context.get(OTEL_SCOPE);
        Span span = (Span) context.get(OTEL_SPAN);
        if (span != null && span.getSpanContext().isValid() && span.isRecording()) {
            span.setAttribute(OUTCOME, ERROR);
            span.setStatus(StatusCode.ERROR);
            span.end();
        }
        if (scope != null) {
            scope.close();
        }
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        Span span = (Span) context.get(OTEL_SPAN);
        if (span != null) {
            span.setAttribute(OUTCOME, ERROR);
            try {
                span.recordException(new RuntimeException(context.getMessage().getSOAPBody().getFault().getFaultCode()));
            } catch (SOAPException e) {
                log.info("error in monitoring handler: ", e);
                span.setAttribute(OUTCOME, ERROR);
                span.setStatus(StatusCode.ERROR);
            }
            span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR);
            span.end();
        }
        Scope scope = (Scope) context.get(OTEL_SCOPE);
        if (scope != null) {
            scope.close();
        }
        return true;
    }

    @Override
    public Set<QName> getHeaders() {
        return Collections.emptySet();
    }
}
