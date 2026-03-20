package com.termite.wallet.crypto;

import android.util.Log;

import org.bitcoinj.core.Base58;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.*;
import java.util.Arrays;
import android.util.Base64;

public class RsaAddressVerifier {

    public static PrivateKey decodePrivateKey(String base64PrivKey) throws Exception {
        byte[] keyBytes = Base64.decode(base64PrivKey, Base64.DEFAULT);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return kf.generatePrivate(spec);
    }

    public static PublicKey extractPublicKeyFromPrivate(PrivateKey privKey) throws Exception {
        if (!(privKey instanceof RSAPrivateCrtKey)) {
            throw new IllegalArgumentException("Invalid RSA private key");
        }
        RSAPrivateCrtKey rsaPriv = (RSAPrivateCrtKey) privKey;
        RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(rsaPriv.getModulus(), rsaPriv.getPublicExponent());
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(pubSpec);
    }

    public static String generateAddressFromPublicKey(PublicKey publicKey) throws Exception {
        byte[] pkcs1PubKey = convertPublicKeyToPKCS1(publicKey);

        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

        byte[] firstHash = sha256.digest(pkcs1PubKey);
        byte[] secondHash = sha256.digest(firstHash);
        byte[] checksum = Arrays.copyOfRange(firstHash, 0, 4);

        byte[] addressBytes = new byte[secondHash.length + checksum.length];
        System.arraycopy(secondHash, 0, addressBytes, 0, secondHash.length);
        System.arraycopy(checksum, 0, addressBytes, secondHash.length, checksum.length);

        return Base58.encode(addressBytes);
    }

    private static byte[] convertPublicKeyToPKCS1(PublicKey publicKey) throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pubSpec = kf.getKeySpec(publicKey, RSAPublicKeySpec.class);

        org.bouncycastle.asn1.ASN1EncodableVector v = new org.bouncycastle.asn1.ASN1EncodableVector();
        v.add(new org.bouncycastle.asn1.ASN1Integer(pubSpec.getModulus()));
        v.add(new org.bouncycastle.asn1.ASN1Integer(pubSpec.getPublicExponent()));
        org.bouncycastle.asn1.DERSequence seq = new org.bouncycastle.asn1.DERSequence(v);
        return seq.getEncoded("DER");
    }

    public static boolean isValid(String base64PrivKey, String addressFromUser) {
        try {
            PrivateKey privKey = decodePrivateKey(base64PrivKey);
            PublicKey pubKey = extractPublicKeyFromPrivate(privKey);
            String generatedAddress = generateAddressFromPublicKey(pubKey);

            Log.d("WalletValidator", "Generated Address: " + generatedAddress);
            Log.d("WalletValidator", "Provided Address: " + addressFromUser);

            int matchCount = 0;
            int minLen = Math.min(generatedAddress.length(), addressFromUser.length());

            for (int i = 0; i < minLen; i++) {
                if (generatedAddress.charAt(i) == addressFromUser.charAt(i)) {
                    matchCount++;
                } else {
                    break;
                }
            }

            double matchRatio = (double) matchCount / generatedAddress.length();

            Log.d("WalletValidator", "Match ratio: " + matchRatio);

            return matchRatio >= 0.75;
        } catch (Exception e) {
            Log.e("WalletValidator", "Validation failed: " + e.getMessage());
            return false;
        }
    }

}
