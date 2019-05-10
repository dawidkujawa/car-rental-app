package k.dawid.loginuserspringboot.controller;

import k.dawid.loginuserspringboot.dao.CarRepository;
import k.dawid.loginuserspringboot.entity.Car;
import k.dawid.loginuserspringboot.entity.Location;
import k.dawid.loginuserspringboot.entity.Reservation;
import k.dawid.loginuserspringboot.entity.User;
import k.dawid.loginuserspringboot.localDate.DateTimeInterval;
import k.dawid.loginuserspringboot.service.CarService;
import k.dawid.loginuserspringboot.service.LocationService;
import k.dawid.loginuserspringboot.service.ReservationService;
import k.dawid.loginuserspringboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.Entity;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    public static String uploadDirectory = System.getProperty("user.dir") + "/src/main/resources/static/images";

    @Autowired
    CarService carService;

    @Autowired
    UserService userService;

    @Autowired
    LocationService locationService;

    @Autowired
    ReservationService reservationService;

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public ModelAndView admin() {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        List<Car> cars = carService.findAll();
        List<Location> locations = locationService.findAll();
        List<Reservation> reservations = reservationService.findAll();
        List<Reservation> upcomingReservations = getUpcomingReservations(reservations);
        List<Reservation> pastReservations = getPastReservations(reservations);
        List<Reservation> ongoingReservations = getOngoingReservations(reservations);
        int upcomingRentsProfit = sumProfit(upcomingReservations);
        int pastRentsProfit = sumProfit(pastReservations);
        int ongoingRentsProfit = sumProfit(ongoingReservations);

        model.addObject("user", user);
        model.addObject("car", new Car());
        model.addObject("cars", cars);
        model.addObject("locations", locations);
        model.addObject("upcomingReservations", upcomingReservations);
        model.addObject("pastReservations", pastReservations);
        model.addObject("ongoingReservations", ongoingReservations);
        model.addObject("upcomingRentsProfit", upcomingRentsProfit);
        model.addObject("pastRentsProfit", pastRentsProfit);
        model.addObject("ongoingRentsProfit", ongoingRentsProfit);
        model.addObject("totalProfit", ongoingRentsProfit+pastRentsProfit);
        model.setViewName("/admin/adminPage");
        return model;
    }

    @GetMapping("/admin/updateCar")
    public ModelAndView showFormForUpdate(@RequestParam("carId") int id) {
        ModelAndView model = new ModelAndView();
        Car car = carService.findById(id);
        List<Location> locations = locationService.findAll();
        model.addObject("car", car);
        model.addObject("locations", locations);
        model.setViewName("admin/carForm");
        return model;
    }

    @GetMapping("/admin/addCar")
    public ModelAndView showFormForAdd() {
        ModelAndView model = new ModelAndView();
        Car car = new Car();
        List<Location> locations = locationService.findAll();
        model.addObject("car", car);
        model.addObject("locations", locations);
        model.setViewName("admin/carForm");
        return model;
    }

    @GetMapping(value = "/admin/deleteCar")
    public ModelAndView deleteCar(@RequestParam("carId") int id) {
        carService.deleteById(id);
        ModelAndView model = new ModelAndView();
        model.setViewName("redirect:/admin");
        return model;
    }

    @RequestMapping(value = "admin/saveCar", method = RequestMethod.POST)
    public ModelAndView update(@ModelAttribute("file") MultipartFile file, @ModelAttribute("car") Car car) {
        ModelAndView model = new ModelAndView();
        StringBuilder fileName = new StringBuilder();
        Path fileNameAndPath = Paths.get(uploadDirectory, file.getOriginalFilename());
        try {
            Files.write(fileNameAndPath, file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        car.setImageUrl("/images/" + file.getOriginalFilename());
        Location location = locationService.findByCity(car.getLocationCity());
        car.setLocation(location);
        car.refreshStatus();
        carService.save(car);
        model.setViewName("redirect:/admin");
        return model;
    }

    @GetMapping("/admin/updateLocation")
    public ModelAndView showFormForUpdateLocation(@RequestParam("locationId") int id) {
        ModelAndView model = new ModelAndView();
        Location location = locationService.findById(id);
        model.addObject("location", location);
        model.setViewName("admin/locationForm");
        return model;
    }

    @GetMapping("/admin/addLocation")
    public ModelAndView showFormForAddLocation() {
        ModelAndView model = new ModelAndView();
        Location location = new Location();
        List<Location> locations = locationService.findAll();
        model.addObject("location", location);
        model.addObject("locations", locations);
        model.setViewName("admin/locationForm");
        return model;
    }

    @GetMapping(value = "/admin/deleteLocation")
    public ModelAndView deleteLocation(@RequestParam("locationId") int id) {
        locationService.deleteById(id);
        ModelAndView model = new ModelAndView();
        model.setViewName("redirect:/admin");
        return model;
    }

    @RequestMapping(value = "admin/saveLocation", method = RequestMethod.POST)
    public ModelAndView saveLocation(@ModelAttribute("location") Location location) {
        ModelAndView model = new ModelAndView();
        locationService.saveByLocation(location);
        model.setViewName("redirect:/admin");
        return model;
    }

    public List<Reservation> getUpcomingReservations(List<Reservation> reservations){
        return reservations.stream()
                .filter(reservation -> dateToLocalDate(reservation.getRentDate()).isAfter(LocalDate.now()))
                .sorted((o1, o2) -> o1.getRentDate().compareTo(o2.getRentDate()))
                .collect(Collectors.toList());
    }

    public List<Reservation> getPastReservations(List<Reservation> reservations){
        return reservations.stream()
                .filter(reservation -> dateToLocalDate(reservation.getReturnDate()).isBefore(LocalDate.now()))
                .sorted((o1, o2) -> o1.getRentDate().compareTo(o2.getRentDate()))
                .collect(Collectors.toList());
    }

    public List<Reservation> getOngoingReservations(List<Reservation> reservations){

        return reservations.stream()
                .filter(reservation -> (dateToLocalDate(reservation.getRentDate()).isBefore(LocalDate.now()) ||
                                       dateToLocalDate(reservation.getRentDate()).equals(LocalDate.now())) &&
                                       dateToLocalDate(reservation.getReturnDate()).isAfter(LocalDate.now()))
                .sorted((o1, o2) -> o1.getRentDate().compareTo(o2.getRentDate()))
                .collect(Collectors.toList());
    }

    public int sumProfit(List<Reservation> reservations){
        return reservations.stream()
                .mapToInt(reservation -> reservation.getPrice())
                .sum();
    }


    public LocalDate dateToLocalDate(Date date) {
        Instant instantDate = date.toInstant();
        LocalDate newLocalDate = instantDate.atZone(ZoneId.systemDefault()).toLocalDate();
        return newLocalDate;

    }



}