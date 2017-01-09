package com.yjt.http.listener.net.implement;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


public class CustomX509TrustManager implements X509TrustManager {

    private X509TrustManager mDefaultTrustManager;
    private X509TrustManager mLocalTrustManager;

    public CustomX509TrustManager(X509TrustManager localTrustManager) throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        factory.init((KeyStore) null);
        mDefaultTrustManager = chooseTrustManager(factory.getTrustManagers());
        this.mLocalTrustManager = localTrustManager;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String authType) throws CertificateException {
        try {
            mDefaultTrustManager.checkServerTrusted(x509Certificates, authType);
        } catch (CertificateException ce) {
            mLocalTrustManager.checkServerTrusted(x509Certificates, authType);
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    private X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (TrustManager manager : trustManagers) {
            if (manager instanceof X509TrustManager) {
                return (X509TrustManager) manager;
            }
        }
        return null;
    }
}
