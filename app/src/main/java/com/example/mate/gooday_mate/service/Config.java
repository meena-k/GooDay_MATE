package com.example.mate.gooday_mate.service;

public class Config {
    /* AWS EC2 */
    public static final String URL = "http://ec2-13-209-77-32.ap-northeast-2.compute.amazonaws.com/";
    public static final String RASPI_URL = "http://192.168.130.54/UploadToServer.php";
    public static String manager_name;
    public static String KEY_BIRTH;
    public static String PATIENT_NAME;
    public static String PATIENT_IMG;
    public static String PATIENT_PRES;
    public static String PATIENT_XRAY;
    public static String PATIENT_MRI;

    /* AWS S3 */
    public static final String COGNITO_POOL_ID = "ap-northeast-2:48a8ebb7-e536-4e52-a595-2a0d49f76395";
    public static final String COGNITO_POOL_REGION = "ap-northeast-2";
    public static final String BUCKET_NAME = "mate-bucket";
    public static final String BUCKET_REGION = "ap-northeast-2";
    public static final String BUCKET_ENDPOINT = "s3.ap-northeast-2.amazonaws.com";
}
