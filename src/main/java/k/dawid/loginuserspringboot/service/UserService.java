package k.dawid.loginuserspringboot.service;

import k.dawid.loginuserspringboot.entity.User;

public interface UserService {

    public User findUserByEmail(String email);

    public void saveUser(User user);

}
