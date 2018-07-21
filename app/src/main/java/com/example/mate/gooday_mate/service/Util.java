package com.example.mate.gooday_mate.service;

import android.content.Context;
import android.os.StrictMode;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.List;

/* Handles basic helper functions used throughout the app. */
public class Util {
    private static AmazonS3Client sS3Client;
    private CognitoCachingCredentialsProvider sCredProvider;
    private TransferUtility sTransferUtility;
    private boolean isDocument = false;

    /* Gets an instance of CognitoCachingCredentialsProvider which is constructed using the given Context. */
    private CognitoCachingCredentialsProvider getCredProvider(Context context) {

        if (sCredProvider == null) {
            sCredProvider = new CognitoCachingCredentialsProvider(
                    context.getApplicationContext(),
                    Config.COGNITO_POOL_ID,
                    Regions.fromName(Config.COGNITO_POOL_REGION));
        }
        return sCredProvider;
    }

    /* Gets an instance of a S3 client which is constructed using the given Context. */
    public AmazonS3Client getS3Client(Context context) {

        if (sS3Client == null) {
            sS3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext()));
            sS3Client.setRegion(Region.getRegion(Regions.fromName(Config.BUCKET_REGION)));
            sS3Client.setEndpoint(Config.BUCKET_ENDPOINT);
        }

        return sS3Client;
    }

    /* Gets an instance of the TransferUtility which is constructed using the given Context */
    public TransferUtility getTransferUtility(Context context) {

        if (sTransferUtility == null) {
            sTransferUtility = TransferUtility.builder()
                    .context(context.getApplicationContext())
                    .s3Client(getS3Client(context.getApplicationContext()))
                    .defaultBucket(Config.BUCKET_NAME)
                    .build();
        }
        return sTransferUtility;
    }

    public boolean getDocumentList(String birth, String document) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // The list of objects we find in the S3 bucket
        List<S3ObjectSummary> s3ObjList;

        s3ObjList = sS3Client.listObjects(Config.BUCKET_NAME, birth + "/").getObjectSummaries();

        for (S3ObjectSummary summary : s3ObjList) {
            if (summary.getKey().equals(birth + "/image.jpg")) {
                isDocument = true;
                break;
            }
        }
        return isDocument;
    }

}
