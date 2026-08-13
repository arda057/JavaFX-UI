package JavaFXexample.service;

import JavaFXexample.model.User;
import JavaFXexample.repository.UserRepository;
import JavaFXexample.util.PasswordUtil;

public class UserService {
    UserRepository userRepository = new UserRepository();

    public boolean register(String username, String password){
        if(userRepository.findByUsername(username) != null){
            return false;
        }
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password, salt);

        return userRepository.insertUser(username, hash, salt);
    }

    public boolean authenticate(String username, String password){
        User user = userRepository.findByUsername(username);

        if(user == null){
            return false;
        }

        return PasswordUtil.verifyPassword(password, user.getSalt(), user.getPassword_hash());
    }

    public User login(String username, String password){
        User user = userRepository.findByUsername(username);

        if(user == null){
            return null;
        }

        boolean valid = PasswordUtil.verifyPassword(password, user.getSalt(), user.getPassword_hash());

        return valid ? user : null;
    }

    public boolean updatePhoto(int id, byte[] photo){
        return userRepository.updatePhoto(id, photo);
    }

    public boolean deletePhoto(int id){
        return userRepository.deletePhoto(id);
    }




}
