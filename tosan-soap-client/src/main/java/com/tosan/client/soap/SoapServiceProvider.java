package com.tosan.client.soap;

import com.tosan.client.soap.config.SoapServiceConfig;
import io.opentelemetry.api.trace.Tracer;

import java.net.URL;

/**
 * <p>T : Service soap that service must return</p>
 * <p>V : Service config that extends from {@link SoapServiceConfig}</p>
 *
 * @author MosiDev
 * @since 5/24/2014
 */
public abstract class SoapServiceProvider<T, V extends SoapServiceConfig> {
    protected V config;
    private T service;
    private final Tracer tracer;

    public SoapServiceProvider(V config) {
        this.config = config;
        this.tracer = null;
    }

    public SoapServiceProvider(V config, Tracer tracer) {
        this.config = config;
        this.tracer = tracer;
    }

    /**
     * @return return web service Soap
     */
    public T getService() {
        if (config == null) {
            throw new RuntimeException("config class of web service is null, web service can not be started.");
        }
        if (service == null) {
            synchronized (this) {
                if (service == null) {
                    service = getServiceSoap(config, tracer);
                }
            }
        }
        return service;
    }

    protected abstract T getServiceSoap(V config, Tracer tracer);

    protected abstract T getServiceSoap(V config);

    protected URL getLocalWsdlUrl(String wsdlFilePath) {
        if (config.isOnlineWsdl()) {
            return config.getServerUrl();
        } else {
            return SoapServiceProvider.class.getResource(wsdlFilePath);
        }
    }
}
