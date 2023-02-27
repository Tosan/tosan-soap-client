package com.tosan.client.soap.handler;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
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

    public SoapHandlerResolver(boolean showLog) {
        this(showLog, null);
    }

    public SoapHandlerResolver(boolean showLog, Set<String> secureParameters) {
        this.showLog = showLog;
        this.secureParameters = secureParameters;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<Handler> getHandlerChain(PortInfo portInfo) {
        List<Handler> handlerChain = new ArrayList<>();
        if (showLog) {
            LogHandler logHandler = new LogHandler(secureParameters);
            handlerChain.add(logHandler);
        }
        return handlerChain;
    }
}