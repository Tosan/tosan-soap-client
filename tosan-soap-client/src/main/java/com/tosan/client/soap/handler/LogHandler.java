package com.tosan.client.soap.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
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
                logStream.write("Soap Request Message: ".getBytes());
                msg.writeTo(logStream);
                logger.info(LogEncryptor.encrypt(logStream.toString(), securedParameterNames));
            } else {
                logStream.write("Soap Response Message: ".getBytes());
                msg.writeTo(logStream);
                logger.info(LogEncryptor.encrypt(logStream.toString(), securedParameterNames));
            }
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