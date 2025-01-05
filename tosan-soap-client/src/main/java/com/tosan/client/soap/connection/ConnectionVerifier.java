package com.tosan.client.soap.connection;

import com.tosan.client.soap.config.SoapServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;

/**
 * @author MosiDev
 * @since 5/24/2014
 */
public class ConnectionVerifier {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionVerifier.class);
    private final SoapServiceConfig config;

    public ConnectionVerifier(SoapServiceConfig config) {
        this.config = config;
    }

    public static ConnectionVerifier verify(SoapServiceConfig config) {
        ConnectionVerifier connectionVerifier = new ConnectionVerifier(config);
        connectionVerifier.checkURLConnection();
        return connectionVerifier;
    }

    public void checkURLConnection() {
        URL serverUrl = config.getValidUrl();
        try {
            if (config.getProxy().isEnable()) {
                setProxyProperties(config);
            }
        } catch (Exception e) {
            logger.warn("Could not set system proxy settings before calling web service {}.", serverUrl);
        }

        try {
            if (config.isInitialConnection()) {
                if (serverUrl.getProtocol().equals("https")) {
                    HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)
                            -> hostname.equals(session.getPeerHost()));
                    testHttpsConnection(config, serverUrl);
                } else {
                    testHttpConnection(config, serverUrl);
                }
                logger.info("test connection to soap webservice was successful. tested url: {}", serverUrl);
            }
        } catch (Exception e) {
            logger.warn("Could not test web service before call it, maybe web service is down. tested url: '{}'", serverUrl);
        }
    }

    private void testHttpConnection(SoapServiceConfig config, URL serverUrl) throws IOException {
        HttpURLConnection httpURLConnection;
        httpURLConnection = (HttpURLConnection) serverUrl.openConnection();
        httpURLConnection.setConnectTimeout(config.getConnectionTimeout());
        httpURLConnection.connect();
    }

    private void testHttpsConnection(SoapServiceConfig config, URL serverUrl) throws IOException, GeneralSecurityException {
        String sslContext = config.getSsl().getContext();
        String privateKeyAlias = config.getSsl().getKeystoreAlias();
        String keyStoreLocation = config.getSsl().getKeystoreFilePath();
        byte[] keyStoreByteArray = config.getSsl().getKeystoreFileByteArray();
        String trustStoreLocation = config.getSsl().getTruststoreFilePath();
        byte[] trustStoreByteArray = config.getSsl().getTruststoreFileByteArray();
        String trustStorePassword = config.getSsl().getTruststorePassword();
        String keyStorePassword = config.getSsl().getKeystorePassword();
        String trustStoreAlias = config.getSsl().getTruststoreAlias();
        SSLSocketFactory socketFactory = new SSLSocketFactoryGenerator(sslContext,
                keyStoreLocation, keyStoreByteArray, privateKeyAlias, keyStorePassword,
                trustStoreLocation, trustStoreByteArray, trustStoreAlias, trustStorePassword)
                .getSSLSocketFactory();

        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) serverUrl.openConnection();
        httpsURLConnection.setConnectTimeout(config.getConnectionTimeout());
        httpsURLConnection.setSSLSocketFactory(socketFactory);
        httpsURLConnection.connect();
    }

    private void setProxyProperties(SoapServiceConfig config) {
        URL serverUrl = config.getValidUrl();
        if (serverUrl.getProtocol().equals("https")) {
            System.getProperties().put("https.proxyHost", config.getProxy().getHost());
            System.getProperties().put("https.proxyPort", config.getProxy().getPort());
            System.getProperties().put("https.proxyUser", config.getProxy().getUser());
            System.getProperties().put("https.proxyPassword", config.getProxy().getPassword());
        } else if (serverUrl.getProtocol().equals("http")) {
            System.getProperties().put("http.proxyHost", config.getProxy().getHost());
            System.getProperties().put("http.proxyPort", config.getProxy().getPort());
            System.getProperties().put("http.proxyUser", config.getProxy().getUser());
            System.getProperties().put("http.proxyPassword", config.getProxy().getPassword());
        }
    }
}
