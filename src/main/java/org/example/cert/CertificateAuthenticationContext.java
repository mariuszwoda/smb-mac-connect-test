package org.example.cert;

import com.hierynomus.smbj.auth.AuthenticationContext;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class CertificateAuthenticationContext extends AuthenticationContext {
    private final X509Certificate certificate;
    private final PrivateKey privateKey;

    public CertificateAuthenticationContext(X509Certificate certificate, PrivateKey privateKey) {
        super(null, "test".toCharArray(), null);
        this.certificate = certificate;
        this.privateKey = privateKey;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    // Override other methods if needed to integrate with SMBJ
}
