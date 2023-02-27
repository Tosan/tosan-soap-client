package com.tosan.client.soap.config;

/**
 * @author Mostafa Abdollahi
 * @since 2/21/2023
 */
public class ProxyConfig {
    private boolean enable;

    /**
     * hostname (IP or DNS name)
     */
    private String host;

    /**
     * port number
     */
    private String port;

    /**
     * proxy username
     */
    private String user;

    /**
     * proxy password
     */
    private String password;

    public boolean isEnable() {
        return enable;
    }

    public ProxyConfig setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public String getHost() {
        return host;
    }

    public ProxyConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public String getPort() {
        return port;
    }

    public ProxyConfig setPort(String port) {
        this.port = port;
        return this;
    }

    public String getUser() {
        return user;
    }

    public ProxyConfig setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public ProxyConfig setPassword(String password) {
        this.password = password;
        return this;
    }
}