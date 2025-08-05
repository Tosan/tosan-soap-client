package com.tosan.client.soap.handler;

import io.opentelemetry.api.trace.Tracer;
import jakarta.xml.ws.handler.Handler;
import jakarta.xml.ws.handler.HandlerResolver;
import jakarta.xml.ws.handler.PortInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author MosiDev
 * @since 5/24/2014
 */
public class SoapHandlerResolver implements HandlerResolver {
    private final boolean showLog;
    private final Set<String> secureParameters;
    private final Tracer tracer;
    private final String clientName;

    public SoapHandlerResolver(boolean showLog) {
        this(showLog, null, null, null);
    }

    public SoapHandlerResolver(boolean showLog, Set<String> secureParameters) {
        this.showLog = showLog;
        this.secureParameters = secureParameters;
        tracer = null;
        clientName = null;
    }

    public SoapHandlerResolver(boolean showLog, Set<String> secureParameters, Tracer tracer, String clientName) {
        this.showLog = showLog;
        this.secureParameters = secureParameters;
        this.tracer = tracer;
        this.clientName = clientName;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<Handler> getHandlerChain(PortInfo portInfo) {
        List<Handler> handlerChain = new ArrayList<>();
        if (tracer != null) {
            MonitoringHandler monitoringHandler = new MonitoringHandler(tracer, clientName);
            handlerChain.add(monitoringHandler);
        }
        if (showLog) {
            LogHandler logHandler = new LogHandler(secureParameters);
            handlerChain.add(logHandler);
        }
        return handlerChain;
    }
}