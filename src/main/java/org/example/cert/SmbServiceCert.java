package org.example.cert;

import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

@Service
public class SmbServiceCert {

    @Value("${smb.url.host}")
    private String smbUrlHost;

    @Value("${smb.url.path}")
    private String smbUrlPath;

    @Value("${smb.p12.filepath}")
    private String p12FilePath;

    @Value("${smb.p12.password}")
    private String p12Password;

    public void listFiles() throws Exception {
        // Load the PKCS12 keystore
        KeyStore keyStore = CertificateLoader.loadKeyStore(p12FilePath, p12Password);
        PrivateKey privateKey = CertificateLoader.getPrivateKey(keyStore, p12Password);
        X509Certificate certificate = CertificateLoader.getCertificate(keyStore);

        // Create a custom AuthenticationContext
        CertificateAuthenticationContext authContext = new CertificateAuthenticationContext(certificate, privateKey);

        // Connect to the SMB share and list files
        SMBClient client = new SMBClient();
        try (Connection connection = client.connect(smbUrlHost)) {
            Session session = connection.authenticate(authContext);
            try (DiskShare share = (DiskShare) session.connectShare(smbUrlPath)) {
                for (FileIdBothDirectoryInformation file : share.list("")) {
                    System.out.println("File: " + file.getFileName());
                }
            }
        }
    }

}
