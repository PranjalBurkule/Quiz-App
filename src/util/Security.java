package util;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
public class Security {
    public static String generateSalt(){
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    public static String hashPassword(String password, String salt){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            md.update(password.getBytes());
            byte[] hashed = md.digest();
            return Base64.getEncoder().encodeToString(hashed);
        } catch(NoSuchAlgorithmException e){ throw new RuntimeException(e); }
    }
    public static boolean verify(String password, String salt, String hash){
        return hashPassword(password,salt).equals(hash);
    }
}
