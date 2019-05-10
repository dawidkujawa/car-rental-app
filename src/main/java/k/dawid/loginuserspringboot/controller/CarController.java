//package k.dawid.loginuserspringboot.controller;
//
//import k.dawid.loginuserspringboot.entity.Car;
//import k.dawid.loginuserspringboot.entity.User;
//import k.dawid.loginuserspringboot.service.CarService;
//import k.dawid.loginuserspringboot.service.ReservationService;
//import k.dawid.loginuserspringboot.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.servlet.ModelAndView;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Controller
//public class CarController {
//
//    @Autowired
//    CarService carService;
//
//    @Autowired
//    UserService userService;
//
//    @RequestMapping(value = "/home/cars", method = RequestMethod.GET)
//    public ModelAndView viewAllCars() {
//        ModelAndView model = new ModelAndView();
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        User user = userService.findUserByEmail(auth.getName());
//
//        model.addObject("userName", user.getFirstname());
//
//        List<Car> cars = carService.findAll();
//
//        cars.forEach(car -> car.refreshStatus());
//
//        model.addObject("cars", cars);
//        model.setViewName("/home/cars");
//        return model;
//    }
//}
//
