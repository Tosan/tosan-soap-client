package com.tosan.client.soap;

import com.sun.xml.ws.developer.JAXWSProperties;
import com.tosan.client.soap.config.SoapServiceConfig;
import com.tosan.client.soap.connection.SSLSocketFactoryGenerator;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.WebServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSocketFactory;

/**
 * @author MosiDev
 * @since 5/24/2014
 */
public class SoapRequestContext {
    private static final Logger logger = LoggerFactory.getLogger(SoapRequestContext.class);
    private final BindingProvider provider;
    private final SoapServiceConfig config;

    /**
     * @param provider {@link BindingProvider}
     * @param config   webservice config
     */
    public SoapRequestContext(BindingProvider provider, SoapServiceConfig config) {
        this.provider = provider;
        this.config = config;
    }

    public BindingProvider getProvider() {
        return provider;
    }

    public void build() {
        endpointSetting();
        authenticationSetting();
        sslSetting();
        timeoutSetting();
    }

    public static SoapRequestContext build(BindingProvider provider, SoapServiceConfig config) {
        SoapRequestContext requestContext = new SoapRequestContext(provider, config);
        requestContext.build();
        return requestContext;
    }

    public void endpointSetting() {
        if (!config.isOnlineWsdl()) {
            provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                    config.getEndPointUrl().toString());
        }
    }

    public void authenticationSetting() {
        if (config.getUsername() != null && !config.getUsername().isEmpty()) {
            provider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, config.getUsername());
        }
        if (config.getPassword() != null && !config.getPassword().isEmpty()) {
            provider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, config.getPassword());
        }
    }

    /**
     * customize  sslSocket factory using jks and aliases
     */
    public void sslSetting() {
        if (config.getValidUrl().getProtocol().equals("https")) {
            //for customize sslSocket factory using jks and aliases
            String keystoreAlias = config.getSsl().getKeystoreAlias();
            String keystoreLocation = null;
            byte[] keystoreByteArray = new byte[0];
            if (config.getSsl().getKeystoreFilePath() != null && !config.getSsl().getKeystoreFilePath().isEmpty()) {
                keystoreLocation = config.getSsl().getKeystoreFilePath();
            } else if (config.getSsl().getKeystoreFileByteArray() != null && config.getSsl().getKeystoreFileByteArray().length != 0) {
                keystoreByteArray = config.getSsl().getKeystoreFileByteArray();
            }
            String truststoreLocation = null;
            byte[] truststoreByteArray = new byte[0];
            if (config.getSsl().getTruststoreFilePath() != null && !config.getSsl().getTruststoreFilePath().isEmpty()) {
                truststoreLocation = config.getSsl().getTruststoreFilePath();
            } else if (config.getSsl().getTruststoreFileByteArray() != null && config.getSsl().getTruststoreFileByteArray().length != 0) {
                truststoreByteArray = config.getSsl().getTruststoreFileByteArray();
            }
            String sslContext = config.getSsl().getContext();
            String truststorePassword = config.getSsl().getTruststorePassword();
            String keystorePassword = config.getSsl().getKeystorePassword();
            String truststoreAlias = config.getSsl().getTruststoreAlias();

            try {
                SSLSocketFactory socketFactory = new SSLSocketFactoryGenerator(sslContext,
                        keystoreLocation, keystoreByteArray, keystoreAlias, keystorePassword,
                        truststoreLocation, truststoreByteArray, truststoreAlias, truststorePassword)
                        .getSSLSocketFactory();
                provider.getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, socketFactory);
            } catch (Exception e) {
                if (config.isOnlineWsdl()) {
                    throw new WebServiceException("Could not generate instance of SSLSocketFactory'"
                            + config.getServerUrl() + "' value", e
                    );
                } else {
                    logger.warn("Could not create https connection may jks file is not correctly configured.", e);
                }
            }
        }
    }

    public void timeoutSetting() {
        provider.getRequestContext().put(JAXWSProperties.CONNECT_TIMEOUT, config.getConnectionTimeout());
        provider.getRequestContext().put(JAXWSProperties.REQUEST_TIMEOUT, config.getRequestTimeout());
    }
}
