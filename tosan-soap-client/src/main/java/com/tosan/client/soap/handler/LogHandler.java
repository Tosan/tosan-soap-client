package com.tosan.client.soap.handler;

import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Set;

/**
 * @author MosiDev
 * @since 5/24/2014
 */
public class LogHandler implements SOAPHandler<SOAPMessageContext> {
    private static final Logger logger = LoggerFactory.getLogger(LogHandler.class);
    private Set<String> securedParameterNames;

    public LogHandler() {
    }

    public LogHandler(Set<String> securedParameterNames) {
        this.securedParameterNames = securedParameterNames;
    }

    public boolean handleMessage(SOAPMessageContext messageContext) {
        SOAPMessage msg = messageContext.getMessage();
        boolean request = (Boolean) messageContext.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY);
        OutputStream logStream = new ByteArrayOutputStream();
        try {
            if (request) {
                logStream.write("Soap Request: ".getBytes());
            } else {
                logStream.write("Soap Response: ".getBytes());
            }
            msg.writeTo(logStream);
            logger.info(LogEncryptor.encrypt(logStream.toString(), securedParameterNames));
        } catch (Exception e) {
            logger.info("Error in logger: ", e);
        }
        return true;
    }

    public boolean handleFault(SOAPMessageContext c) {
        SOAPMessage msg = c.getMessage();
        try {
            OutputStream logStream = new ByteArrayOutputStream();
            logStream.write("Soap Fault Recognized: ".getBytes());
            msg.writeTo(logStream);
            logger.info(logStream.toString());
        } catch (Exception e) {
            logger.info("Error in logger: ", e);
        }
        return true;
    }

    public void close(MessageContext c) {
    }

    public Set<QName> getHeaders() {
        return null;
    }
}