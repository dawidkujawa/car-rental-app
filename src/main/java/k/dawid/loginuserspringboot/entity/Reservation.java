package k.dawid.loginuserspringboot.entity;

import k.dawid.loginuserspringboot.localDate.DateTimeInterval;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;


import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name="reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name="rent_date")
    private Date rentDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name="return_date")
    private Date returnDate;

    @ManyToOne(cascade={CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name="car_id")
    private Car car;

    @ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="price")
    private int price;

    @OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name="rent_location_id")
    private Location rentLocation;

    @OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name="return_location_id")
    private Location returnLocation;

    public Reservation(Date rentDate, Date returnDate, Car car, User user, Location rentLocation, Location returnLocation) {
        this.rentDate = rentDate;
        this.returnDate = returnDate;
        this.car = car;
        this.user = user;
        this.rentLocation = rentLocation;
        this.returnLocation = returnLocation;
    }

    public Reservation() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getRentDate() {
        return rentDate;
    }

    public void setRentDate(Date rentDate) {
        this.rentDate = rentDate;
    }

    public Date getReturnDate() {

        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Location getRentLocation() {
        return rentLocation;
    }

    public String getRentLocationCity() { return rentLocation.getCity();
    }

    public void setRentLocation(Location rentLocation) {
        this.rentLocation = rentLocation;
    }

    public Location getReturnLocation() { return returnLocation;
    }

    public String getReturnLocationCity() { return returnLocation.getCity();
    }

    public void setReturnLocation(Location returnLocation) {
        this.returnLocation = returnLocation;
    }

    public void calculatePrice(Car car){
        int priceForADay = car.getPriceForADay();

        ZoneId defaultZoneId = ZoneId.systemDefault();

        Instant instantRent = rentDate.toInstant();
        Instant instantReturn = returnDate.toInstant();

        LocalDate rentLocalDate = instantRent.atZone(defaultZoneId).toLocalDate();
        LocalDate returnLocalDate = instantReturn.atZone(defaultZoneId).toLocalDate();

        Period intervalPeriod = Period.between(rentLocalDate, returnLocalDate);
        this.price = priceForADay * intervalPeriod.getDays();
    }

    public int getPrice() {
        return price;
    }

    public String getStatus(){

        String result = "";

        DateTimeInterval reservationInterval = new DateTimeInterval(dateToLocalDate(rentDate), dateToLocalDate(returnDate));
        if (reservationInterval.contains(LocalDate.now())){
            result = "ongoing";
        } else if (reservationInterval.getStart().isAfter(LocalDate.now())) {
            result = "upcoming";
        } else {
            result = "past";
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

    @Override
    public String toString() {
        return "Rent date: " + rentDate + ", return date: " + returnDate;
    }
}




















