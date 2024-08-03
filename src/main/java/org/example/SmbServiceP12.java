package org.example;

import jcifs.CIFSContext;
import jcifs.config.PropertyConfiguration;
import jcifs.context.BaseContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Properties;

@Service
public class SmbServiceP12 {

    @Value("${smb.url}")
    private String smbUrl;

    @Value("${smb.p12.filepath}")
    private String p12FilePath;

    @Value("${smb.p12.password}")
    private String p12Password;

    public void listFiles() throws Exception {
        // Load the PKCS12 keystore from the file
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(p12FilePath)) {
            keyStore.load(fis, p12Password.toCharArray());
        }

        // Extract the private key and certificate
        Enumeration<String> aliases = keyStore.aliases();
        String alias = aliases.nextElement();
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, p12Password.toCharArray());
        X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);

        // Set up the CIFS context with authentication
        Properties props = new Properties();
        props.setProperty("jcifs.smb.client.disablePlainTextPasswords", "false");
        CIFSContext baseContext = new BaseContext(new PropertyConfiguration(props));
        CIFSContext authContext = baseContext.withCredentials(
                new NtlmPasswordAuthenticator(cert.getSubjectX500Principal().getName(), new String(privateKey.getEncoded()))
        );

        // Connect to the SMB share and list files
        try (SmbFile smbDir = new SmbFile(smbUrl, authContext)) {
            if (smbDir.exists() && smbDir.isDirectory()) {
                for (SmbFile file : smbDir.listFiles()) {
                    System.out.println("File: " + file.getName());
                }
            } else {
                System.out.println("Directory does not exist or is not a directory.");
            }

        } catch (ClassCastException e) {
            System.err.println("A ClassCastException occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (jcifs.CIFSException e) {
            System.err.println("Problem connecting to SMB host:");
            System.err.println("SMB URL: " + smbUrl);
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}
