package com.tosan.client.soap.config;

import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author MosiDev
 * @since 2/21/2023
 */
public class SSLConfig {
    private String context = "SSL";
    private String keystoreFile;
    private String keystorePassword;
    private byte[] keystoreFileByteArray;
    private String keystoreFilePath;
    private String keystoreAlias;
    private String truststoreFile;
    private String truststorePassword;
    private byte[] truststoreFileByteArray;
    private String truststoreFilePath;
    private String truststoreAlias;

    public String getContext() {
        return context;
    }

    public SSLConfig setContext(String context) {
        this.context = context;
        return this;
    }

    public String getKeystoreFile() {
        return keystoreFile;
    }

    public SSLConfig setKeystoreFile(String keystoreFile) {
        this.keystoreFile = keystoreFile;
        return this;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public SSLConfig setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
        return this;
    }

    public byte[] getKeystoreFileByteArray() {
        return keystoreFileByteArray;
    }

    public SSLConfig setKeystoreFileByteArray(byte[] keystoreFileByteArray) {
        this.keystoreFileByteArray = keystoreFileByteArray;
        return this;
    }

    public String getKeystoreFilePath() {
        if (keystoreFilePath != null) {
            return keystoreFilePath;
        }
        keystoreFilePath = keystoreFile == null || keystoreFile.isEmpty() ? "" : getFileAbsolutePath(keystoreFile);
        return keystoreFilePath;
    }

    public SSLConfig setKeystoreFilePath(String keystoreFilePath) {
        this.keystoreFilePath = keystoreFilePath;
        return this;
    }

    public String getKeystoreAlias() {
        return keystoreAlias;
    }

    public SSLConfig setKeystoreAlias(String keystoreAlias) {
        this.keystoreAlias = keystoreAlias;
        return this;
    }

    public String getTruststoreFile() {
        return truststoreFile;
    }

    public SSLConfig setTruststoreFile(String truststoreFile) {
        this.truststoreFile = truststoreFile;
        return this;
    }

    public String getTruststorePassword() {
        return truststorePassword;
    }

    public SSLConfig setTruststorePassword(String truststorePassword) {
        this.truststorePassword = truststorePassword;
        return this;
    }

    public byte[] getTruststoreFileByteArray() {
        return truststoreFileByteArray;
    }

    public SSLConfig setTruststoreFileByteArray(byte[] truststoreFileByteArray) {
        this.truststoreFileByteArray = truststoreFileByteArray;
        return this;
    }

    public String getTruststoreFilePath() {
        if (truststoreFilePath != null) {
            return truststoreFilePath;
        }
        truststoreFilePath = truststoreFile == null || truststoreFile.isEmpty() ? "" : getFileAbsolutePath(truststoreFile);
        return truststoreFilePath;
    }

    public SSLConfig setTruststoreFilePath(String truststoreFilePath) {
        this.truststoreFilePath = truststoreFilePath;
        return this;
    }

    public String getTruststoreAlias() {
        return truststoreAlias;
    }

    public SSLConfig setTruststoreAlias(String truststoreAlias) {
        this.truststoreAlias = truststoreAlias;
        return this;
    }

    protected static String getFileAbsolutePath(String relativePath) {
        URL resource = SoapServiceConfig.class.getResource(relativePath);
        try {
            return resource != null ? resource.toURI().getPath() : relativePath;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}