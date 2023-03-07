package com.tosan.client.soap.config;

import javax.xml.ws.WebServiceException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * @author MosiDev
 * @since 5/24/2014
 */
public class SoapServiceConfig {
    private URL serverUrl;
    private URL endPointUrl;
    private String username;
    private String password;
    private boolean onlineWsdl;
    private boolean initialConnection;
    private boolean showLog;
    private int connectionTimeout;
    private int requestTimeout;
    private SSLConfig ssl = new SSLConfig();
    private ProxyConfig proxy = new ProxyConfig();

    public SoapServiceConfig() {
    }

    /**
     * Load parameters from configFile and create WebServiceConfig instance
     *
     * @param configFile config file address in classpath
     * @throws FileNotFoundException if the config file is not exist
     */
    public SoapServiceConfig(String configFile) throws FileNotFoundException {
        InputStream input = getFileInputStream(configFile);
        loadStream(input);
    }

    protected InputStream getFileInputStream(String configFile) throws FileNotFoundException {
        InputStream input;
        URL url = SoapServiceConfig.class.getResource(configFile);
        if (url == null) {
            try {
                input = new FileInputStream(configFile);
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException("can not find: " + configFile);
            }
        } else {
            input = SoapServiceConfig.class.getResourceAsStream(configFile);
        }
        if (input == null) {
            throw new FileNotFoundException("can not open: " + configFile);
        }
        return input;
    }

    protected Properties loadStream(InputStream input) {
        Properties props = new Properties();
        try {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        onlineWsdl = Boolean.parseBoolean(props.getProperty("onlineWsdl"));
        if (props.getProperty("onlineWsdl") != null && props.getProperty("serverUrl") != null &&
                !props.getProperty("serverUrl").equals("") && onlineWsdl) {
            try {
                serverUrl = new URL(props.getProperty("serverUrl"));
            } catch (MalformedURLException e) {
                throw new RuntimeException("Invalid server url", e);
            }
        }
        if (props.getProperty("onlineWsdl") != null && !onlineWsdl) {
            try {
                endPointUrl = new URL(props.getProperty("endPointUrl"));
            } catch (MalformedURLException e) {
                throw new RuntimeException("Invalid endpoint url", e);
            }
        }
        username = props.getProperty("username");
        password = props.getProperty("password");
        initialConnection = Boolean.parseBoolean(props.getProperty("initialConnection"));
        showLog = Boolean.parseBoolean(props.getProperty("showLog"));
        connectionTimeout = Integer.parseInt(props.getProperty("connectionTimeout"));
        requestTimeout = Integer.parseInt(props.getProperty("requestTimeout"));

        ssl.setKeystoreFile(props.getProperty("ssl.keystoreFile"));
        ssl.setKeystorePassword(props.getProperty("ssl.keystorePassword"));
        ssl.setKeystoreFileByteArray(props.getProperty("ssl.keystoreFileByteArray") != null
                ? props.getProperty("ssl.keystoreFileByteArray").getBytes() : null);
        ssl.setKeystoreAlias(props.getProperty("ssl.keystoreAlias"));
        ssl.setTruststoreFile(props.getProperty("ssl.truststoreFile"));
        ssl.setTruststoreFileByteArray(props.getProperty("ssl.truststoreFileByteArray") != null
                ? props.getProperty("ssl.truststoreFileByteArray").getBytes() : null);
        ssl.setTruststorePassword(props.getProperty("ssl.truststorePassword"));
        ssl.setTruststoreAlias(props.getProperty("ssl.truststoreAlias"));

        proxy.setEnable(props.getProperty("proxy.enable") != null && Boolean.parseBoolean(props.getProperty("proxy.enable")));
        if (proxy.isEnable()) {
            proxy.setHost(props.getProperty("proxy.host"));
            proxy.setPort(props.getProperty("proxy.port"));
            proxy.setUser(props.getProperty("proxy.user"));
            proxy.setPassword(props.getProperty("proxy.password"));
        }
        return props;
    }

    public URL getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(URL serverUrl) {
        this.serverUrl = serverUrl;
    }

    public URL getEndPointUrl() {
        return endPointUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEndPointUrl(URL endPointUrl) {
        this.endPointUrl = endPointUrl;
    }

    public boolean isOnlineWsdl() {
        return onlineWsdl;
    }

    /**
     * @param onlineWsdl use wsdl url from the external ip address or use local wsdl with endpoint url
     */
    public void setOnlineWsdl(boolean onlineWsdl) {
        this.onlineWsdl = onlineWsdl;
    }

    public boolean isInitialConnection() {
        return initialConnection;
    }

    /**
     * @param initialConnection enable/disable initial connection on startup
     */
    public void setInitialConnection(boolean initialConnection) {
        this.initialConnection = initialConnection;
    }

    /**
     * @return used to enable/disable request/response logging
     */
    public boolean isShowLog() {
        return showLog;
    }

    public void setShowLog(boolean showLog) {
        this.showLog = showLog;
    }

    /**
     * @return connectionTimeout in milliseconds
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * @param connectionTimeout connectionTimeout in milliseconds
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * @return requestTimeout in milliseconds
     */
    public int getRequestTimeout() {
        return requestTimeout;
    }

    /**
     * @param requestTimeout requestTimeout in milliseconds
     */
    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public SSLConfig getSsl() {
        return ssl;
    }

    public void setSsl(SSLConfig ssl) {
        this.ssl = ssl;
    }

    public ProxyConfig getProxy() {
        return proxy;
    }

    public void setProxy(ProxyConfig proxy) {
        this.proxy = proxy;
    }

    /**
     * get valid url from service url or endpoint url or the url in the default config file
     */
    public URL getValidUrl() {
        if (getServerUrl() != null) {
            return getServerUrl();
        } else if (getEndPointUrl() != null) {
            return getEndPointUrl();
        } else {
            throw new WebServiceException("Neither of Service url nor endpoint url are valid.");
        }
    }
}
