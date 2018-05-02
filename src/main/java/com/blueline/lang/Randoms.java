package com.blueline.lang;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Randoms {
    public Randoms(){
        throw new RuntimeException("This operation is not supported.");
    }
    public final static ICalculate VALUE=new ICalculate() {
        @Override
        public String compute(String... arges) {
            String random =String.format( "%d%010d",
                    System.currentTimeMillis() , Math.abs(new Random().nextLong()));
            try {

                MessageDigest messageDigest = MessageDigest.getInstance("md5");
                byte[] digest = messageDigest.digest(random.getBytes());
                random=  printHexString(digest);
            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
            }
            return random;
        }

        private String printHexString(byte[] b) {
            String a = "";
            for (int i = 0; i < b.length; i++) {
                String hex = Integer.toHexString(b[i] & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                a = a+hex;
            }
            return a;
        }
    };

    public final static ICalculate INT=new ICalculate() {
        @Override
        public String compute(String... arges) {
            switch (arges.length){
                case 1:
                    return String.valueOf(new Random().nextInt(Integer.valueOf(arges[0])));
                case 2:
                    return String.valueOf(new Random().nextInt(Integer.valueOf(arges[1])-Integer.valueOf(arges[0]))+Integer.valueOf(arges[0]));
                default:
                    return String.valueOf(Math.abs(new Random().nextInt()));
            }
        }
    };

    public final static ICalculate LONG=new ICalculate() {
        @Override
        public String compute(String... arges) {
                    return String.valueOf(Math.abs(new Random().nextLong()));
        }
    };

    public final static ICalculate UUID=new ICalculate() {
        @Override
        public String compute(String... arges) {
           return java.util.UUID.randomUUID().toString();
        }
    };


}
