package com.oci.logexporter;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.BasicAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class ConfigHolder {
    public static long timeWindowIncrement = 2 * 60 * 1000; // 2 minutes
    public static int chunkSize = 5000;
    static public File newLogDir;
    static BasicAuthenticationDetailsProvider ociAuthProvider = null;
    // this sets region for both logs and export destination bucket
    static String region = "US_PHOENIX_1";
    static String endpointForLogSearchOci = null;
    static String searchQuery = "search \"ocid1.tenancy.oc1..aaaaaaaaopbu45aomik7sswe4nzzll3f6ii6pipd5ttw4ayoozez37qqmh3a\"  | sort by datetime asc";
    static Date startDate = new Date(1600185891709l);
    static Date endDate = new Date(1600185891709l + 2000 * 60 * 1000); // 1hr logs
    static int logFetchApiLimit = 999;
    static String osNamespace = "intrandallbarnes";
    static String parentBucketName = "LogExport_";
    static Region regionOci = null;
    static private String ociConfigFilePath = "/Users/mraleras/.oci/config";
    static private String loggingOciProfileName = "DEFAULT";

    static void initialize(String[] args) {
        try {
            ociAuthProvider = getOciAuthProvider();
            regionOci = Region.fromRegionCodeOrId(region);
            endpointForLogSearchOci = "https://logging." + regionOci.getRegionId() + ".oci.oraclecloud.com";

            File file = new File(System.getProperty("user.home"));
            newLogDir = new File(file, "logDir");
            if (newLogDir.exists()) {
                FileUtils.deleteDirectory(newLogDir);
                newLogDir.mkdir();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static BasicAuthenticationDetailsProvider getOciAuthProvider() {
        final InstancePrincipalsAuthenticationDetailsProvider provider = null;
        try {
            int a = 10 / 0;
            //provider = InstancePrincipalsAuthenticationDetailsProvider.builder().build();
        } catch (Exception e1) {

            System.out.println(
                    "This sample only works when running on an OCI instance. Are you sure you’re running on an OCI instance? For more info see: https://docs.cloud.oracle.com/Content/Identity/Tasks/callingservicesfrominstances.htm");
            try {
                File file = new File(ociConfigFilePath);
                file.setReadOnly();
                final ConfigFileReader.ConfigFile configFile;
                configFile = ConfigFileReader.parse(file.getAbsolutePath(), loggingOciProfileName);
                return
                        new ConfigFileAuthenticationDetailsProvider(configFile);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            throw e1;
        }
        return provider;
    }
}
