package org.example;

import jcifs.CIFSContext;
import jcifs.context.SingletonContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class SmbServiceCopy {

    private static final Logger logger = LoggerFactory.getLogger(SmbServiceCopy.class);

    @Value("${smb.url}")
    private String smbUrl;

    @Value("${smb.username}")
    private String username;

    @Value("${smb.password}")
    private String password;

    private CIFSContext authedContext;

    @PostConstruct
    public void init() {
        NtlmPasswordAuthenticator auth = new NtlmPasswordAuthenticator(username, password);
        CIFSContext baseContext = SingletonContext.getInstance();
        authedContext = baseContext.withCredentials(auth);
    }

    public void listFiles() {
        try {
            SmbFile smbDir = new SmbFile(smbUrl, authedContext);
            if (smbDir.exists() && smbDir.isDirectory()) {
                listFilesRecursive(smbDir);
            } else {
                logger.warn("Directory does not exist or is not a directory.");
            }
        } catch (IOException e) {
            logger.error("Error listing files in SMB directory", e);
        }
    }

    private void listFilesRecursive(SmbFile dir) throws IOException {
        for (SmbFile file : dir.listFiles()) {
            if (file.isDirectory()) {
                listFilesRecursive(file);
            } else {
                logger.info("File: " + file.getPath());
            }
        }
    }

    public void copyFileToNewFolder(String fileName, String newFolderName) {
        if (fileName == null || fileName.isEmpty() || newFolderName == null || newFolderName.isEmpty()) {
            logger.warn("Invalid file name or folder name.");
            return;
        }

        try {
            SmbFile smbDir = new SmbFile(smbUrl, authedContext);
            if (!smbDir.exists() || !smbDir.isDirectory()) {
                logger.warn("Directory does not exist or is not a directory.");
                return;
            }

            SmbFile sourceFile = new SmbFile(smbUrl + "/" + fileName, authedContext);
            if (!sourceFile.exists()) {
                logger.warn("Source file does not exist.");
                return;
            }

            SmbFile newFolder = new SmbFile(smbUrl + "/" + newFolderName, authedContext);
            if (!newFolder.exists()) {
                newFolder.mkdirs();
            }

            SmbFile destFile = new SmbFile(newFolder.getPath() + "/" + fileName, authedContext);
            if (destFile.exists()) {
                logger.info("Destination file already exists. No action taken.");
                return;
            }

            sourceFile.copyTo(destFile);
            logger.info("File copied to new folder successfully.");
        } catch (IOException e) {
            logger.error("Error copying file to new folder", e);
        }
    }
}
