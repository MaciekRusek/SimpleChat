package user;

import java.util.Optional;

public class UserController {

    private UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    public Optional<User> createUser(
            String username,
            String password
    ) {
        return service.createUser(username, password);
    }

    public Optional<User> getUser(
            String username,
            String password
    ) {
        return service.getUser(username, password);
    }
}
