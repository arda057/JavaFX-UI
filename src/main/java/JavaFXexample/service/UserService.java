package JavaFXexample.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import JavaFXexample.exception.DuplicateEntityException;
import JavaFXexample.model.User;
import JavaFXexample.repository.UserRepository;
import JavaFXexample.util.PasswordUtil;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository = new UserRepository();

    public void register(String username, String password) {
        if (userRepository.findByUsername(username) != null) {
            throw new DuplicateEntityException("This user name is already taken.");
        }
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password, salt);
        userRepository.insertUser(username, hash, salt);
    }

    public boolean authenticate(String username, String password) {
        return login(username, password) != null;
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }

        boolean valid = PasswordUtil.verifyPassword(password, user.getSalt(), user.getPassword_hash());
        if (!valid) {
            return null;
        }

        if (PasswordUtil.needsRehash(user.getPassword_hash())) {
            try {
                userRepository.updatePasswordHash(user.getId(), PasswordUtil.hashPassword(password, user.getSalt()));
            } catch (RuntimeException e) {
                log.warn("Could not upgrade password hash for user {}", username, e);
            }
        }

        return user;
    }

    public void updatePhoto(int id, byte[] photo) {
        userRepository.updatePhoto(id, photo);
    }

    public void deletePhoto(int id) {
        userRepository.deletePhoto(id);
    }
}
