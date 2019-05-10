package k.dawid.loginuserspringboot.controller;

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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ClientController {

    @Autowired
    CarService carService;

    @Autowired
    UserService userService;

    @Autowired
    ReservationService reservationService;

    @Autowired
    LocationService locationService;

    @GetMapping(value = "/home/home")
    public ModelAndView home() {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        model.addObject("user", user);
        model.setViewName("home/home");
        return model;
    }

    @GetMapping(value = "/home/cars")
    public ModelAndView viewAllCars() {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        model.addObject("user", user);
        List<Car> cars = carService.findAll();
        model.addObject("cars", cars);
        model.setViewName("/home/cars");
        return model;
    }

    @GetMapping(value = "/home/cars/bookCar")
    public ModelAndView bookCarForm(@RequestParam("carId") int id) throws ParseException {
        ModelAndView model = new ModelAndView();
        Car car = carService.findById(id);
        List<Location> locations = locationService.findAll();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        Reservation reservation = new Reservation();
        reservation.setCar(car);
        user.addReservation(reservation);
        model.addObject("user", user);
        model.addObject("car", car);
        model.addObject("reservation", reservation);
        model.addObject("locations", locations);
        model.addObject("rentDate", reservation.getRentDate());
        model.addObject("returnDate", reservation.getReturnDate());
        model.setViewName("/home/bookCarForm");
        return model;
    }

    @PostMapping(value = "/home/cars/bookCar")
    public ModelAndView bookCar(@ModelAttribute("reservation") @Valid Reservation reservation,
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                @RequestParam Date rentDate,
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                @RequestParam Date returnDate,
                                BindingResult bindingResult) {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        Car car = reservation.getCar();
        Location tempReturnLocation = locationService.findByCity(reservation.getReturnLocationCity());
        reservation.setReturnLocation(tempReturnLocation);
        reservation.setUser(user);
        reservation.setRentDate(rentDate);
        reservation.setReturnDate(returnDate);
        reservation.setRentLocation(car.getLocationOfLastReservation(rentDate, returnDate));
        reservation.calculatePrice(car);

        if (validReservation(reservation, rentDate, returnDate,
                bindingResult, model, car) == true) {
            user.addReservation(reservation);
            car.addReservation(reservation);
            car.refreshStatus();
            reservationService.save(reservation);
            model.addObject("msg", "Your reservation has been saved");
        }
        model.addObject("user", user);
        List<Reservation> reservations = user.getReservations();
        int totalPrice = sumPriceOfUnpaidRents(reservations);
        model.addObject("reservations", reservations);
        model.addObject("totalPrice", totalPrice);
        model.setViewName("home/myAccount");
        return model;
    }

    @GetMapping(value = "/home/account")
    public ModelAndView myAccount() {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        model.addObject("user", user);
        List<Reservation> reservations = user.getReservations();
        int totalPrice = sumPriceOfUnpaidRents(reservations);
        model.addObject("reservations", reservations);
        model.addObject("totalPrice", totalPrice);
        model.setViewName("home/myAccount");
        return model;
    }

    @PostMapping(value = "/home/account/cancelBooking")
    public ModelAndView deleteCar(@RequestParam("reservationId") int id) {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        Reservation reservation = reservationService.findById(id);
        Car car = reservation.getCar();
        car.deleteReservation(reservation);
        car.refreshStatus();
        reservationService.deleteById(id);
        List<Reservation> reservations = user.getReservations();
        int totalPrice = sumPriceOfUnpaidRents(reservations);
        model.addObject("reservations", reservations);
        model.addObject("totalPrice", totalPrice);
        model.addObject("user", user);
        model.addObject("reservations", user.getReservations());
        model.setViewName("/home/myAccount");
        return model;
    }

    @GetMapping(value = "/home/departments")
    public ModelAndView viewAllDepartments() {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        model.addObject("user", user);
        List<Location> locations = locationService.findAll();
        model.addObject("locations", locations);
        model.setViewName("/home/departments");
        return model;
    }

    @GetMapping(value = "/home/aboutus")
    public ModelAndView viewAboutUs() {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        model.addObject("user", user);
        model.setViewName("/home/aboutus");
        return model;
    }

    private boolean validReservation(Reservation reservation, Date rentDate, Date returnDate, BindingResult bindingResult, ModelAndView model, Car car) {

        boolean result = false;
        List<DateTimeInterval> intervalsWithRentDate = getDateTimeIntervals(rentDate, car);
        List<DateTimeInterval> intervalsWithReturnDate = getDateTimeIntervals(returnDate, car);
        int intervalsWithRentDateCount = intervalsWithRentDate.size();
        int intervalsWithReturnDateCount = intervalsWithReturnDate.size();
        int upcomingReservationsAfterThisReservation = car.getUpcomingReservationsAfterThisReservation(rentDate).size();

        if (intervalsWithRentDateCount > 0 || intervalsWithReturnDateCount > 0) {
            bindingResult.hasErrors();
            model.addObject("msg1", "The car is not available during this period. Your reservation has not been saved");
        } else {
            if (returnDate.before(rentDate)) {
                bindingResult.hasErrors();
                model.addObject("msg1", "Incorrect date. Your reservation has not been saved");
            } else {
                if (rentDate.before(localDatetoDate(LocalDate.now()))) {
                    bindingResult.hasErrors();
                    model.addObject("msg1", "You can not book a car for the past date. Your reservation has not been saved");
                } else {
                    if (upcomingReservationsAfterThisReservation > 0) {
                        Location rentLocationOfFirstUpcomingReservation = car.getRentLocationOfFirstUpcomingReservation(rentDate);
                        if (reservation.getReturnLocation() != rentLocationOfFirstUpcomingReservation) {
                            bindingResult.hasErrors();
                            model.addObject("msg1", "You can not return the car at this location. Next customer wants to pick up the car in "
                                    + rentLocationOfFirstUpcomingReservation.getCity());
                        } else {
                            result = true;
                        }
                    } else {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    public LocalDate dateToLocalDate(Date date) {
        Instant instantDate = date.toInstant();
        LocalDate newLocalDate = instantDate.atZone(ZoneId.systemDefault()).toLocalDate();
        return newLocalDate;

    }

    public Date localDatetoDate(LocalDate localDate) {
        return java.util.Date.from(localDate.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    private List<DateTimeInterval> getDateTimeIntervals(Date date, Car car) {
        return car.getUpcomingReservationIntervals()
                .stream()
                .filter(interval -> interval.contains(dateToLocalDate(date)))
                .collect(Collectors.toList());
    }


    public int sumPriceOfUnpaidRents(List<Reservation> reservations){
        return reservations.stream()
                .filter(reservation -> dateToLocalDate(reservation.getRentDate()).isAfter(LocalDate.now()))
                .mapToInt(reservation -> reservation.getPrice())
                .sum();
    }
}
