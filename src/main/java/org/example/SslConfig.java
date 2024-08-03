package org.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

@Configuration
public class SslConfig {

    @Bean
    public SSLContext sslContext() throws Exception {
        String keyStoreType = "PKCS12";
        String keyStorePath = "/Users/mario/Documents/development/workspace/test/smb-mac-connect-test/src/main/resources/cert.p12";
//        String keyStorePath = "path/to/your.p12";
        String keyStorePassword = "test";

        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        try (FileInputStream keyStoreInputStream = new FileInputStream(keyStorePath)) {
            keyStore.load(keyStoreInputStream, keyStorePassword.toCharArray());
        }

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        return sslContext;
    }
}
