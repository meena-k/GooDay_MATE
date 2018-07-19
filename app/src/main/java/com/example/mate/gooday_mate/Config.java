package com.example.mate.gooday_mate;

public class Config {
    /* AWS EC2 */
    public static final String URL = "http://ec2-13-124-197-52.ap-northeast-2.compute.amazonaws.com/";
    public static final String SOCKET_URL = "192.168.110.39";
    public static String manager_name;
    public static String PATIENT_NAME;

    /* AWS S3 */
    public static final String COGNITO_POOL_ID = "ap-northeast-2:48a8ebb7-e536-4e52-a595-2a0d49f76395";
    public static final String COGNITO_POOL_REGION = "ap-northeast-2";
    public static final String BUCKET_NAME = "mate-bucket";
    public static final String BUCKET_REGION = "ap-northeast-2";
    public static final String BUCKET_ENDPOINT ="s3.ap-northeast-2.amazonaws.com";

    //Keys for email and password as defined in our $_POST['key'] in login.php
}
