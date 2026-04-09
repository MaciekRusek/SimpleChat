package user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class UserService {
    private static final Logger logger = LogManager.getLogger();

    private UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Optional<User> createUser(
            String username,
            String pass
    ) {
        try {

            if (!userDao.checkIfUsernameExists(username)) {
                return userDao.insertUser(username, pass);
            }

            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.toString());
            return Optional.empty();
        }
    }

    public Optional<User> getUser(
            String username,
            String pass
    ) {
        try {
            return userDao.getUser(username, pass);
        } catch (Exception e) {
            logger.error("Error while login into user: {}", e.toString());
            return Optional.empty();
        }
    }

}