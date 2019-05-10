package k.dawid.loginuserspringboot.controller;

import k.dawid.loginuserspringboot.entity.Car;
import k.dawid.loginuserspringboot.entity.User;
import k.dawid.loginuserspringboot.service.CarService;
import k.dawid.loginuserspringboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CarService carService;

    @RequestMapping(value = {"/", "/login"}, method = RequestMethod.GET)
    public ModelAndView login() {
        ModelAndView model = new ModelAndView();
        List<Car> cars = carService.findAll();
        for (int i = 0; i < cars.size(); i++) {
            cars.get(i).refreshStatus();
            carService.save(cars.get(i));
        }

//        model.addObject("cars", cars);
        model.setViewName("user/login");
        return model;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public ModelAndView signup() {
        ModelAndView model = new ModelAndView();
        User user = new User();
        model.addObject("user", user);
        model.setViewName("/user/signup");

        return model;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ModelAndView createUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView model = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());

        if (userExists != null) {
            bindingResult.rejectValue("email", "error.user",
                    "This email already exists");
        }
        if (bindingResult.hasErrors()) {
            model.setViewName("user/signup");
        } else {
            userService.saveUser(user);
            model.addObject("msg", "User has been registrated" +
                    " succesfully");
            model.addObject("user", new User());
            model.setViewName("user/signup");
        }
        return model;
    }

    @RequestMapping(value = "/access_denied", method = RequestMethod.GET)
    public ModelAndView accessDenied() {
        ModelAndView model = new ModelAndView();
        model.setViewName("errors/access_denied");
        return model;
    }
}





















