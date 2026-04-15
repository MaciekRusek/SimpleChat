package auth;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordService {

    public static String hashPassword(String password) {
        String salt = BCrypt.gensalt(12);

        return BCrypt.hashpw(password, salt);
    }

    public static Boolean comparePassword(String password, String hashPassword) {
        return BCrypt.checkpw(password, hashPassword);
    }

}
