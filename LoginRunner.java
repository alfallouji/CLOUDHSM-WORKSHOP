
/*
 * Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

 /**
    Modified version of the Login LoginRunner
 
    # Use key management util and create a sym key
    $ /opt/cloudhsm/bin/key_mgmt_util

    # Login into the HSM
    > loginHSM -u CU -s <keyowner> -p <password>

    # listUsers and note the userid of the user which is using the key
    > listUsers


    > getKeyInfo -h -k <key handle>
    # Generate Symmetric keys
    genSymKey -t 31 -s 32 -l aes256 -nex -u <uid of the other user>

    // node Key ID

    > quit

    # check

    $ /opt/cloudhsm/bin/key_mgmt_util
    > loginHSM -u CU -s <keyuser> -p <password>
    > getKeyInfo -k <keyhandle>
    > getAttribute -o 524299 -a 512 -out usethiskeytest.out

  */
package com.amazonaws.cloudhsm.examples;

import com.cavium.cfm2.CFM2Exception;
import com.cavium.cfm2.LoginManager;
import com.cavium.key.parameter.CaviumAESKeyGenParameterSpec;
import com.cavium.key.parameter.CaviumDESKeyGenParameterSpec;
import com.cavium.key.parameter.CaviumKeyGenAlgorithmParameterSpec;
import com.cavium.key.parameter.CaviumECGenParameterSpec;
import com.cavium.cfm2.CFM2Exception;
import com.cavium.cfm2.ImportKey;
import com.cavium.cfm2.Util;
import com.cavium.key.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.BadPaddingException;
import javax.crypto.KeyGenerator;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.Key;
import java.security.Security;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import java.util.Base64;
import java.util.Enumeration;

public class LoginRunner {
    private static String helpString = "LoginRunner\n" +
               "This sample demonstrates the different methods of authentication that can be used with the JCE.\n" +
               "\n" +
               "Options\n" +
               "\t--method [explicit, system-properties, environment]\n" +
               "\t--user <username>\n" +
               "\t--password <password>\n" +
               "\t--partition <partition>\n\n";

    public static void main(String[] args) throws Exception {
        try {
            Security.addProvider(new com.cavium.provider.CaviumProvider());
        } catch (IOException ex) {
            System.out.println(ex);
            return;
        }

        if (args.length % 2 != 0) {
            return;
        }

        String method = null;
        String user = null;
        String pass = null;
        String partition = null;

        for (int i = 0; i < args.length; i+=2) {
            String arg = args[i];
            String value = args[i+1];
            switch (arg) {
                case "--method":
                    method = value;
                    break;
                case "--user":
                    user = value;
                    break;
                case "--password":
                    pass = value;
                    break;
                case "--partition":
                    partition = value;
                    break;
            }
        }

        if (null == method) {
            help();
            return;
        }

        if (method.equals("explicit") || method.equals("system-properties")) {
            if (null == user || null == pass || null == partition) {
                help();
                return;
            }
        }

        if (method.equals("explicit")) {
            loginWithExplicitCredentials(user, pass, partition);
        } else if (method.equals("system-properties")) {
            loginUsingJavaProperties(user, pass, partition);
        } else if (method.equals("environment")) {
            loginWithEnvVariables();
        }

        // Replace Handle ID
        long handleID = 524299;
        displayKeyInfo(getKeyByHandle(handleID));

        //byte[] iv = generateFipsCompliantIV(16);
        byte[] iv = new String("my secret static").getBytes("UTF-8");
        String toencrypt = "We Want to encrypt this text";
        String transformation = "AES/CBC/PKCS5Padding";
        String cipher;

        cipher = Encrypt(transformation, getKeyByHandle(handleID), iv, toencrypt);
        System.out.printf("Encrypted String: %s\n", cipher);
        String clear;

        clear = Decrypt(transformation, getKeyByHandle(handleID), iv, cipher);
        System.out.printf("Decrypted String: %s\n",  clear);

        logout();
    }

    public static void help() {
        System.out.println(helpString);
    }

    public static void loginWithExplicitCredentials(String user, String pass, String partition) {
        LoginManager lm = LoginManager.getInstance();
        try {
            lm.login(partition, user, pass);
            System.out.printf("\nLogin successful!\n\n");
        } catch (CFM2Exception e) {
            if (CFM2Exception.isAuthenticationFailure(e)) {
                System.out.printf("\nDetected invalid credentials\n\n");
            }

            e.printStackTrace();
        }
    }

    public static void loginUsingJavaProperties(String user, String pass, String partition) throws Exception {
      System.setProperty("HSM_PARTITION", partition);
      System.setProperty("HSM_USER", user);
      System.setProperty("HSM_PASSWORD", pass);

      Key aesKey = null;

      try {
          aesKey = SymmetricKeys.generateAESKey(256, "Implicit Java Properties Login Key");
      } catch (Exception e) {
          if (CFM2Exception.isAuthenticationFailure(e)) {
              System.out.printf("\nDetected invalid credentials\n\n");
              e.printStackTrace();
              return;
          }

          throw e;
      }

      assert(aesKey != null);
      System.out.printf("\nLogin successful!\n\n");
    }

   public static void loginWithEnvVariables() throws Exception {
      Key aesKey = null;

      try {
          aesKey = SymmetricKeys.generateAESKey(256, "Implicit Java Properties Login Key");
      } catch (Exception e) {
          if (CFM2Exception.isAuthenticationFailure(e)) {
              System.out.printf("\nDetected invalid credentials\n\n");
              e.printStackTrace();
              return;
          }

          throw e;
      }

      System.out.printf("\nLogin successful!\n\n");
  }

    public static void logout() {
        try {
            LoginManager.getInstance().logout();
        } catch (CFM2Exception e) {
            e.printStackTrace();
        }
    }

    private static void displayKeyInfo(CaviumKey key) {
    	System.out.printf("Key handle %d with label %s\n", key.getHandle(), key.getLabel());
    	System.out.println("Is Key Extractable? : " + key.isExtractable());
    	System.out.println("Is Key Persistent? : " + key.isPersistent());
    	System.out.println("Key Algo : " + key.getAlgorithm());
    	System.out.println("Key Size : " + key.getSize());
    }

    private static CaviumKey getKeyByHandle(long handle) throws CFM2Exception {
        byte[] keyAttribute = Util.getKeyAttributes(handle);
        CaviumKeyAttributes cka = new CaviumKeyAttributes(keyAttribute);

        if(cka.getKeyType() == CaviumKeyAttributes.KEY_TYPE_AES) {
            CaviumAESKey aesKey = new CaviumAESKey(handle, cka);
            return aesKey;
        }

        return null;
    }

    public static byte[] generateFipsCompliantIV(final int ivSizeinBytes)
                throws NoSuchAlgorithmException, NoSuchProviderException {
        SecureRandom sr;
        sr = SecureRandom.getInstance("AES-CTR-DRBG", "Cavium");
        byte[] iv = new byte[ivSizeinBytes];
        sr.nextBytes(iv);

        return iv;
    }

    public static String Encrypt(String transformation, Key key, byte[] iv,String plainText)
    throws NoSuchAlgorithmException,
                NoSuchProviderException,
                NoSuchPaddingException,
                InvalidKeyException,
                InvalidAlgorithmParameterException,
                UnsupportedEncodingException,
                IllegalBlockSizeException,
                BadPaddingException {

    	IvParameterSpec ivSpec = new IvParameterSpec(iv);
    	Cipher encryptCipher = Cipher.getInstance(transformation, "Cavium");
    	encryptCipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
    	byte[] cipherText = encryptCipher.doFinal(plainText.getBytes("UTF-8"));
        //System.out.println("Base64 cipher text = " + Base64.getEncoder().encodeToString(cipherText));

        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static String Decrypt(String transformation, Key key, byte[] iv,String ScipherText)
    throws NoSuchAlgorithmException,
                NoSuchProviderException,
                NoSuchPaddingException,
                InvalidKeyException,
                InvalidAlgorithmParameterException,
                UnsupportedEncodingException,
                IllegalBlockSizeException,
                BadPaddingException {

        IvParameterSpec ivSpec = new IvParameterSpec(iv);
    	byte[] cipherText = Base64.getDecoder().decode(new String(ScipherText).getBytes("UTF-8"));
    	Cipher decryptCipher = Cipher.getInstance(transformation, "Cavium");
        decryptCipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] decryptedText = decryptCipher.doFinal(cipherText);
        //System.out.println("Decrypted text = " + new String(decryptedText, "UTF-8"));
    	String out = new String(decryptedText, "UTF-8");

        return out;
    }
}
