package user;

import java.util.Optional;

public class UserController {

    private UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    public Optional<User> loginIntoUser(
            String username,
            String password
    ) {
        return service.loginIntoUser(username, password);
    }

}
