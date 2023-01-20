package com.classes;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class hash
{

    public static void main(String[] args)
    {
        //for(int i=1; i<=6; i++) {
           // System.out.println("pat"+i);
            String salt = createSalt();
            System.out.println(salt);
            System.out.println(hashPassword("admin", salt));
       // }
    }

    private static String createSalt()
    {
        try
        {
            SecureRandom random = new SecureRandom();

            byte bytes[]= new byte[20];
            random.nextBytes(bytes);

            for(int i = 0; i< bytes.length; i++)
            {
                if(bytes[i] < 0)
                    bytes[i] = (byte) -bytes[i];
            }

            return new String(bytes, "UTF-8");

        }
        catch (IOException e)
        {
            System.out.println("IOException: "+e.toString());
            return "";
        }
    }

    private static String hashPassword(String password, String salt)
    {
        // Hash the password.
        final String toHash = salt + password + salt;
        MessageDigest messageDigest = null;
        try
        {
            messageDigest = MessageDigest.getInstance("MD5");

//            Class.forName("com.mysql.jdbc.Driver");
//            Connection con= DriverManager.getConnection(
//                    "jdbc:mysql://localhost:3306/doctorappointment","root","test123");
//
//            System.out.println("edww");
            //Connection con = datasource.getConnection();

//            PreparedStatement st = con.prepareStatement("UPDATE doctor SET salt=?, hashedpassword=? WHERE username=doctor3");
//            st.setString(1,salt);
//            st.setString(2,password);
//            st.execute();
//
//            con.close();

        } catch (Exception ex)
        {
            System.out.println(ex.toString());
            return "00000000000000000000000000000000";
        }
        messageDigest.update(toHash.getBytes(), 0, toHash.length());
        String hashed = new BigInteger(1, messageDigest.digest()).toString(16);
        if (hashed.length() < 32)
        {
            hashed = "0" + hashed;
        }
        return hashed.toUpperCase();
    }

}
