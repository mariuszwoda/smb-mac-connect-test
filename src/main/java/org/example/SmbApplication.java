package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.import org.springframework.beans.factory.annotation.Autowired;
import org.example.cert.SmbServiceCert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmbApplication implements CommandLineRunner {

    @Autowired
    private SmbService smbService;
    @Autowired
    private SmbServiceCopy smbServiceCopy;
    @Autowired
    private SmbServiceP12 smbServiceP12;
    @Autowired
    private SmbServiceCert smbServiceCert;

    public static void main(String[] args) {
        SpringApplication.run(SmbApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        smbService.listFiles();
//        smbServiceP12.listFiles();
//        smbServiceCert.listFiles(); //com.hierynomus.smbj.common.SMBRuntimeException: Could not find a configured authenticator for mechtypes: [] and authentication context: AuthenticationContext[null@null]

        smbServiceCopy.listFiles();
        smbServiceCopy.copyFileToNewFolder("test01.txt", "newTargetFolder3");
    }
}
