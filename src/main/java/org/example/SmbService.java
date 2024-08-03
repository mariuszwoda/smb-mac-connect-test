package org.example;

import jcifs.CIFSContext;
import jcifs.context.SingletonContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SmbService {

    @Value("${smb.url}")
    private String smbUrl;

    @Value("${smb.username}")
    private String username;

    @Value("${smb.password}")
    private String password;

    public void listFiles() {
        NtlmPasswordAuthenticator auth = new NtlmPasswordAuthenticator(username, password);
        CIFSContext baseContext = SingletonContext.getInstance();
        CIFSContext authedContext = baseContext.withCredentials(auth);

        try {
            SmbFile smbDir = new SmbFile(smbUrl, authedContext);
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
            System.err.println("Username: " + username);
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
