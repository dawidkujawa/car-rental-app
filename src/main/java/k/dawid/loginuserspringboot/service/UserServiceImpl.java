package k.dawid.loginuserspringboot.service;

import k.dawid.loginuserspringboot.dao.RoleRepository;
import k.dawid.loginuserspringboot.dao.UserRepository;
import k.dawid.loginuserspringboot.entity.Role;
import k.dawid.loginuserspringboot.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;


@Service("userService")
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUser(User user) {

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(1);
        Role userRole = roleRepository.findByRole("CLIENT");
        user.setRoles(Arrays.asList(userRole));
        userRepository.save(user);
    }
//
//    replace saveUser() with method below if you want to add Admin to your database

//    @Override
//    public void saveUser(User user) {
//
//        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
//        user.setActive(1);
//        Role userRole = roleRepository.findByRole("ADMIN");
//        user.setRoles(Arrays.asList(userRole));
//        userRepository.save(user);
//    }
}
