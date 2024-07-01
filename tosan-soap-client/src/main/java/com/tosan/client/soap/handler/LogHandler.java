package com.tosan.client.soap.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author MosiDev
 * @since 5/24/2014
 */
public class LogHandler implements SOAPHandler<SOAPMessageContext> {
    private static final Logger logger = LoggerFactory.getLogger(LogHandler.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private Set<String> securedParameterNames;
    ThreadLocal<Long> startTimeMillis = new ThreadLocal<>();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    public LogHandler() {
    }

    public LogHandler(Set<String> securedParameterNames) {
        this.securedParameterNames = securedParameterNames;
    }

    public boolean handleMessage(SOAPMessageContext messageContext) {
        SOAPMessage msg = messageContext.getMessage();
        boolean request = (Boolean) messageContext.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY);
        OutputStream logStream = new ByteArrayOutputStream();
        Map<String, String> logParams = new LinkedHashMap<>();
        try {
            msg.writeTo(logStream);
            if (request) {
                logParams.put("soap-request", getBody(logStream));
                startTimeMillis.set(System.currentTimeMillis());
            } else {
                logParams.put("soap-response", getBody(logStream));
                logParams.put("duration", getDurationLogMessage());
            }
            logger.info(mapper.writeValueAsString(logParams));
        } catch (Exception e) {
            logger.info("Error in logger: ", e);
        } finally {
            if (!request) {
                startTimeMillis.remove();
            }
        }
        return true;
    }

    private String getBody(OutputStream logStream) {
        return LogEncryptor.encrypt(logStream.toString(), securedParameterNames);
    }

    public boolean handleFault(SOAPMessageContext c) {
        SOAPMessage msg = c.getMessage();
        try {
            OutputStream logStream = new ByteArrayOutputStream();
            msg.writeTo(logStream);
            Map<String, String> params = new LinkedHashMap<>();
            params.put("soap-fault", getBody(logStream));
            params.put("duration", getDurationLogMessage());
            logger.info(mapper.writeValueAsString(params));
        } catch (Exception e) {
            logger.info("Error in logger: ", e);
        } finally {
            startTimeMillis.remove();
        }
        return true;
    }

    private String getDurationLogMessage() {
        return (System.currentTimeMillis() - startTimeMillis.get()) / 1000.0 + "s";
    }

    public void close(MessageContext c) {
    }

    public Set<QName> getHeaders() {
        return null;
    }
}