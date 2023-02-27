package com.tosan.client.soap.connection;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;

/**
 * @author MosiDev
 * @since 5/24/2014
 */
public class SSLSocketFactoryGenerator {
    private final String context;
    private final String keystore;
    private final byte[] keystoreByteArray;
    private final String keystoreAlias;
    private final String keystorePassword;
    private final String truststore;
    private final byte[] truststoreByteArray;
    private final String truststoreAlias;
    private final String truststorePassword;

    public SSLSocketFactoryGenerator(String context,
                                     String keystore, byte[] keystoreByteArray, String keystoreAlias, String keystorePassword,
                                     String truststore, byte[] truststoreByteArray, String truststoreAlias, String truststorePassword) {
        this.context = context;
        this.keystore = keystore;
        this.keystoreByteArray = keystoreByteArray;
        this.keystoreAlias = keystoreAlias;
        this.keystorePassword = keystorePassword;
        this.truststore = truststore;
        this.truststoreByteArray = truststoreByteArray;
        this.truststoreAlias = truststoreAlias;
        this.truststorePassword = truststorePassword;
    }

    public String getContext() {
        return context;
    }

    public String getKeystore() {
        return keystore;
    }

    public byte[] getKeystoreByteArray() {
        return keystoreByteArray;
    }

    public String getKeystoreAlias() {
        return keystoreAlias;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public String getTruststore() {
        return truststore;
    }

    public byte[] getTruststoreByteArray() {
        return truststoreByteArray;
    }

    public String getTruststoreAlias() {
        return truststoreAlias;
    }

    public String getTruststorePassword() {
        return truststorePassword;
    }

    public SSLSocketFactory getSSLSocketFactory() throws IOException, GeneralSecurityException {
        KeyManager[] keyManagers = null;
        if (keystore != null && !keystore.isEmpty()) {
            keyManagers = getKeyManagers(true);
        } else if (keystoreByteArray != null && keystoreByteArray.length != 0) {
            keyManagers = getKeyManagers(false);
        }
        TrustManager[] trustManagers = new TrustManager[0];
        if (truststore != null && !truststore.isEmpty()) {
            trustManagers = getTrustManagers(true);
        } else if (truststoreByteArray != null && truststoreByteArray.length != 0) {
            trustManagers = getTrustManagers(false);
        }
        SSLContext context = SSLContext.getInstance(this.context);
        context.init(keyManagers, trustManagers, null);
        return context.getSocketFactory();
    }

    private KeyManager[] getKeyManagers(boolean isFilePath) throws IOException, GeneralSecurityException {
        //Init a key store with the given file.
        KeyManagerFactory kmFact = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        KeyStore ks = KeyStore.getInstance("jks");
        if (isFilePath) {
            FileInputStream fis = new FileInputStream(keystore);
            ks.load(fis, keystorePassword.toCharArray());
            fis.close();
        } else {
            InputStream inputStream = new ByteArrayInputStream(keystoreByteArray);
            ks.load(inputStream, keystorePassword.toCharArray());
            inputStream.close();
        }
        if (keystoreAlias != null) {
            Key key = ks.getKey(keystoreAlias, keystorePassword.toCharArray());

            Certificate certificate = ks.getCertificate(keystoreAlias);
            KeyStore newKeyStore = KeyStore.getInstance("jks");
            newKeyStore.load(null, new char[]{});
            newKeyStore.setKeyEntry("key", key, new char[]{}, new Certificate[]{certificate});
            kmFact.init(newKeyStore, new char[]{});
        } else {
            kmFact.init(ks, keystorePassword.toCharArray());
        }
        //Init the key manager factory with the loaded key store
        return kmFact.getKeyManagers();
    }

    protected TrustManager[] getTrustManagers(boolean isFilePath) throws IOException, GeneralSecurityException {
        String alg = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmFact = TrustManagerFactory.getInstance(alg);

        KeyStore ks = KeyStore.getInstance("jks");
        if (isFilePath) {
            FileInputStream fis = new FileInputStream(truststore);
            ks.load(fis, truststorePassword.toCharArray());
            fis.close();
        } else {
            InputStream inputStream = new ByteArrayInputStream(truststoreByteArray);
            ks.load(inputStream, truststorePassword.toCharArray());
            inputStream.close();
        }
        if (truststoreAlias != null) {
            KeyStore newKeyStore;
            Certificate certificate = ks.getCertificate(truststoreAlias);
            newKeyStore = KeyStore.getInstance("jks");
            newKeyStore.load(null, new char[]{});
            tmFact.init(newKeyStore);
            newKeyStore.setCertificateEntry("certificate", certificate);
            tmFact.init(newKeyStore);
        } else {
            tmFact.init(ks);
        }
        return tmFact.getTrustManagers();
    }
}
