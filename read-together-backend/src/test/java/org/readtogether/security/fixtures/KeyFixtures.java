package org.readtogether.security.fixtures;

import lombok.experimental.UtilityClass;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@UtilityClass
public class KeyFixtures {

    public static KeyPair generateTestRsaKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate test RSA key pair", e);
        }
    }

    public static String getTestPublicKeyPem() {
        return "-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1HmZ3A379M6Rv9UnMt9R\n" +
                "Wq0a6bpcnoOWJxTi2exwnecW3r1X1PjeUvsDogy7RYjhlxU0G+1r38gPWfUW2FNd\n" +
                "tsa3H+FDhJ6dcNc4uKVYPsiVJukHi4NrvWA8E8dPdLW1lNcijr4PqXjvZTLoS1QX\n" +
                "f30wnNLBNDwdPXTESodi/n87VoSH2ChgLZUVfoS3m/NlUN8Z58gGxRcpUyjl+MmC\n" +
                "hD2cfyWr2xdxKd+UQMrd36LfyoWh0IlONlxo0H5x8JIwlziLbPEAh7dJ9QYM0b5G\n" +
                "msXBAzrILvW5+POSq4u1vNlzSwdLe+AZ6bnCwQVrqvMn/I7JT+4+lY8BsP/gMRQL\n" +
                "awIDAQAB\n" +
                "-----END PUBLIC KEY-----";
    }

    public static String getTestPrivateKeyPem() {
        return "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDUeZncDfv0zpG/\n" +
                "1Scy31FarRrpulyeg5YnFOLZ7HCd5xbevVfU+N5S+wOiDLtFiOGXFTQb7WvfyA9Z\n" +
                "9RbYU122xrcf4UOEnp1w1zi4pVg+yJUm6QeLg2u9YDwTx090tbWU1yKOvg+peO9l\n" +
                "MuhLVBd/fTCc0sE0PB09dMRKh2L+fztWhIfYKGAtlRV+hLeb82VQ3xnnyAbFFylT\n" +
                "KOX4yYKEPZx/JavbF3Ep35RAyt3fot/KhaHQiU42XGjQfnHwkjCXOIts8QCHt0n1\n" +
                "BgzRvkaaxcEDOsgu9bn485Kri7W82XNLB0t74BnpucLBBWuq8yf8jslP7j6VjwGw\n" +
                "/+AxFAtrAgMBAAECggEARWpw5N7AuQsfvN+DjfA9oPU6/K9BARyWWrBNKMtBQ6Uy\n" +
                "6JRNdKvV3qBZYIDuUdpVcUmhG5qmipbOxSH4U7ZwwH0NaOHscBBt+WanBlQmj2Ry\n" +
                "riKlr2PBOD6Pghq0j7mp2DWs+ZuIfGKhO5u1Hp8bijA5SJLmQg19tA1I79xpcCFC\n" +
                "flY8WaBKJCTKtH3sisS9IqAtGx3+O2fwzk5oSA6DDuX+45zTLS1vlucwhTUTDE4j\n" +
                "UitYM5CnW9LzJ72ofWZBE29twECXQa+7YYXYniqUOzYZVhy7OiwYT9HH2fDGurmL\n" +
                "F4adtcmVS6CUM6OYSV8z1DXbwLcidTqwiAj9uZ8suQKBgQD2hb/Bimgoh0dT6q9N\n" +
                "pORikLW9L/6q3TuAfNJrrP63dwgooeCcHdk8Wh1zFkNibmfdhHQM5Hu3pQq0oxtG\n" +
                "XiH1WYhluDAjyq6ahvYasgGtdzh279VcicleP9x6B6tmWop+a9kM+Yh2r3Yb5KXr\n" +
                "ZyFWLeoEkCyeXdVr1tds119Q/QKBgQDcpMKXIAiayZmcHU03pNrDGhs7m2B18S6Z\n" +
                "WcXSQRf1d98wbzzfsrbHM9k8gX5VGvZb+rwl95SyevT1LSaGM44On4WXjCUlpUnb\n" +
                "8+mKONra8yXuWutB1aclPQddj4HAFGikJyBP45e/SXNTBPrK5cm/HULbgd76FxHv\n" +
                "QQZ2EbSOhwKBgA5pHR98LsCHv+So6Fx6khss6GLJxnJIgmztXwOKVk11ONXfOJkH\n" +
                "qaY8glIy7/d2Cr5JOttyE8VVcX3Dtxly8Ts9Y5rGnJHLDE/eKc6/rxdry7IwLOG+\n" +
                "8DWBOCst/Zf7HPNs7IA0qgR+F0JkKErNeYZnIrHnl6QeShaGtYsYP+slAoGBANRU\n" +
                "yd5dOWqb73NIz3Jo9w0iJmrqT52wh8OTnMeFVOUogmQ96Drt5O82eiu8AjMsS0Cg\n" +
                "vkdbRoGryefXl2c2XdK8uPbqKyVbNwSwaWJW7GYf77S9UgB89ujjHh9vZtHN0hWG\n" +
                "gZXf07yFlrGh7Scsk0WThy9uf4H0iZHQ5cLhrvwpAoGAAj3uW/aAJDhfHHVgBGhC\n" +
                "la8OzEqY8UbqMYrXKfrmYOzvFYKWd5BgrZvgINhSmjxMeil952gHVsH/td+KUyqe\n" +
                "dxTAgV9WVn8KVz6KoEKJV5CarN0IOl5fZVJz1i0Q5t6VdUXLC6teh4ByrEAlsRgi\n" +
                "h2UWcQCA6KqrmJbR2q6yj+s=\n" +
                "-----END PRIVATE KEY-----";
    }

    public static String getInvalidPublicKeyPem() {
        return "-----BEGIN PUBLIC KEY-----\n" +
                "INVALID_KEY_CONTENT\n" +
                "-----END PUBLIC KEY-----";
    }

    public static String getInvalidPrivateKeyPem() {
        return "-----BEGIN PRIVATE KEY-----\n" +
                "INVALID_KEY_CONTENT\n" +
                "-----END PRIVATE KEY-----";
    }
}