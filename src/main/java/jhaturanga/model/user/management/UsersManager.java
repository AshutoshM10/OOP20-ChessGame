package jhaturanga.model.user.management;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import jhaturanga.model.user.User;
import jhaturanga.model.user.UserBuilderImpl;

public interface UsersManager {

    /**
     * Default user for no login.
     */
    User GUEST = new UserBuilderImpl().username("GUEST").build();

    /**
     * 
     * @param username to login the game
     * @param password to login the game
     * @return the user identified by user name and password. If there is no user
     *         find, the Optional will be empty
     * @throws IOException 
     */
    Optional<User> login(String username, String password) throws IOException;

    /**
     * 
     * @param username for future login
     * @param password for future login
     * @return the registered user. If the user name already exist, the Optional
     *         will be empty
     */
    Optional<User> register(String username, String password) throws IOException;

    /**
     * 
     * @return a {@link java.util.Collections} of all Users
     */
    Collection<User> getAllUsers() throws IOException;

}
