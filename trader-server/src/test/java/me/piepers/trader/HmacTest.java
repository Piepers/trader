package me.piepers.trader;

import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HmacTest {
  private static final String HMAC_SHA256 = "HmacSHA256";

  @Test
  public void test_that_hmac_sha256_is_created_as_expected() {
    String bogusPrivateKey = "FRo9IjKoq0IOp0aiEo1pS0euRiQ02A01i9su0aiCOKA0AUfzXdCfVgBhNjMkoa0I";
    String bogusTotalParams = "symbol=LTCBTC&side=BUY&type=LIMIT&timeInForce=GTC&quantity=1&price=0.1&recvWindow=5000&timestamp=1499827319559";

    Mac sha256Hmac;
    String result;
    try {
      final byte[] byteKey = bogusPrivateKey.getBytes(StandardCharsets.UTF_8);
      sha256Hmac = Mac.getInstance(HMAC_SHA256);
      SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA256);
      sha256Hmac.init(keySpec);
      byte[] macData = sha256Hmac.doFinal(bogusTotalParams.getBytes(StandardCharsets.UTF_8));

//      result = Base64.getEncoder().encodeToString(macData);
//      result = new String(macData, StandardCharsets.UTF_8);
      result = bytesToHex(macData);
      System.out.printf(result);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      e.printStackTrace();
    }
  }

  private static final byte[] HEX_ARRAY = "0123456789abcdef".getBytes(StandardCharsets.US_ASCII);
  public static String bytesToHex(byte[] bytes) {
    byte[] hexChars = new byte[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars, StandardCharsets.UTF_8);
  }
}
