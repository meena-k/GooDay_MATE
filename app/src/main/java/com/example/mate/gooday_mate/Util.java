package com.example.mate.gooday_mate;

import android.content.Context;
import android.net.Uri;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/* Handles basic helper functions used throughout the app. */
public class Util {
    private static AmazonS3Client sS3Client;
    private CognitoCachingCredentialsProvider sCredProvider;
    private TransferUtility sTransferUtility;

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

    /* Copies the data from the passed in Uri, to a new file for use with the Transfer Service */

    public File copyContentUriToFile(Context context, Uri uri) throws IOException {

        InputStream is = context.getContentResolver().openInputStream(uri);
        File copiedData = new File(context.getDir("SampleImagesDir", Context.MODE_PRIVATE), UUID
                .randomUUID().toString());
        copiedData.createNewFile();

        FileOutputStream fos = new FileOutputStream(copiedData);
        byte[] buf = new byte[2046];
        int read = -1;
        while ((read = is.read(buf)) != -1) {
            fos.write(buf, 0, read);
        }

        fos.flush();
        fos.close();

        return copiedData;
    }


}
