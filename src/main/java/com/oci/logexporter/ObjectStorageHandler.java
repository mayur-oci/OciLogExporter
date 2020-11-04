package com.oci.logexporter;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
import com.oracle.bmc.objectstorage.transfer.UploadManager;

import java.io.File;
import java.util.Map;

public class ObjectStorageHandler {

    static ObjectStorage client = ObjectStorageClient.builder().region(ConfigHolder.regionOci).build(ConfigHolder.ociAuthProvider);

    static Map<String, String> metadata = null;
    static String contentType = null;
    static String contentEncoding = null;
    static String contentLanguage = null;

    // configure upload settings as desired
    static UploadConfiguration uploadConfiguration =
            UploadConfiguration.builder()
                    .allowMultipartUploads(false)
                    .allowParallelUploads(false)
                    .build();

    static UploadManager uploadManager = new UploadManager(client, uploadConfiguration);

    static boolean putLogObject(String objectName, File logFileObj) {

        PutObjectRequest request =
                PutObjectRequest.builder()
                        .bucketName(ConfigHolder.parentBucketName)
                        .namespaceName(ConfigHolder.osNamespace)
                        .objectName(objectName)
                        .contentType(contentType)
                        .contentLanguage(contentLanguage)
                        .contentEncoding(contentEncoding)
                        .opcMeta(metadata)
                        .build();

        UploadManager.UploadRequest uploadDetails =
                UploadManager.UploadRequest.builder(logFileObj).allowOverwrite(true).build(request);

        // upload request and print result
        // if multi-part is used, and any part fails, the entire upload fails and will throw BmcException
        UploadManager.UploadResponse response = uploadManager.upload(uploadDetails);

        return false;
    }

    static boolean putLogObject(String osNamespace, String bucketName, String objectName, File logFileObj) {

        PutObjectRequest request =
                PutObjectRequest.builder()
                        .bucketName(bucketName)
                        .namespaceName(osNamespace)
                        .objectName(objectName)
                        .contentType(contentType)
                        .contentLanguage(contentLanguage)
                        .contentEncoding(contentEncoding)
                        .opcMeta(metadata)
                        .build();

        UploadManager.UploadRequest uploadDetails =
                UploadManager.UploadRequest.builder(logFileObj).allowOverwrite(true).build(request);

        // upload request and print result
        // if multi-part is used, and any part fails, the entire upload fails and will throw BmcException
        UploadManager.UploadResponse response = uploadManager.upload(uploadDetails);

        return false;
    }


}
