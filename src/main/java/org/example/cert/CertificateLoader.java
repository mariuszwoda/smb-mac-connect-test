package org.example.cert;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class CertificateLoader {
    public static KeyStore loadKeyStore(String p12FilePath, String p12Password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(p12FilePath)) {
            keyStore.load(fis, p12Password.toCharArray());
        }
        return keyStore;
    }

    public static PrivateKey getPrivateKey(KeyStore keyStore, String p12Password) throws Exception {
        Enumeration<String> aliases = keyStore.aliases();
        String alias = aliases.nextElement();
        return (PrivateKey) keyStore.getKey(alias, p12Password.toCharArray());
    }

    public static X509Certificate getCertificate(KeyStore keyStore) throws Exception {
        Enumeration<String> aliases = keyStore.aliases();
        String alias = aliases.nextElement();
        return (X509Certificate) keyStore.getCertificate(alias);
    }
}
