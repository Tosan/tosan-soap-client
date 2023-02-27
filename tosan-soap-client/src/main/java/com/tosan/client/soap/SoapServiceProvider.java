package com.tosan.client.soap;

import com.tosan.client.soap.config.SoapServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * <p>T : Service soap that service must return</p>
 * <p>V : Service config that extends from {@link SoapServiceConfig}</p>
 *
 * @author MosiDev
 * @since 5/24/2014
 */
public abstract class SoapServiceProvider<T, V extends SoapServiceConfig> {
    private static final Logger logger = LoggerFactory.getLogger(SoapServiceProvider.class);
    protected V config;
    private T service;

    public SoapServiceProvider(V config) {
        this.config = config;
    }

    /**
     * @return return web service Soap
     */
    public T getService() {
        if (config == null) {
            logger.warn("config class of web service is null, web service can not be started.");
            throw new RuntimeException("config class of web service is null, web service can not be started.");
        }
        if (service == null) {
            synchronized (this) {
                if (service == null) {
                    service = getServiceSoap(config);
                }
            }
        }
        return service;
    }

    protected abstract T getServiceSoap(V config);

    protected URL getLocalWsdlUrl(String wsdlFilePath) {
        if (config.isOnlineWsdl()) {
            return config.getServerUrl();
        } else {
            return SoapServiceProvider.class.getResource(wsdlFilePath);
        }
    }
}
