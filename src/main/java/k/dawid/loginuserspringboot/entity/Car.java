package k.dawid.loginuserspringboot.entity;

import k.dawid.loginuserspringboot.localDate.DateTimeInterval;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Entity
@Table(name = "car")
public class Car {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "brand")
    private String brand;

    @Column(name = "model")
    private String model;

    @Column(name = "desciption")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "price_for_a_day")
    private int priceForADay;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "car_location",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id"))
    private Location location;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    private List<Reservation> reservations;

    public Car() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriceForADay() {
        return priceForADay;
    }

    public void setPriceForADay(int priceForADay) {
        this.priceForADay = priceForADay;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void refreshStatus() {
        List<DateTimeInterval> ongoingReservations = getOngoingResv();

        if (ongoingReservations.size() == 0) {
            setStatus("available");
        } else
            setStatus("unavailable");
    }

    public List<DateTimeInterval> getOngoingResv() {

        return reservations
                .stream()
                .map(reservation -> new DateTimeInterval(dateToLocalDate(reservation.getRentDate()), dateToLocalDate(reservation.getReturnDate())))
                .filter(reservation -> reservation.contains(LocalDate.now()))
                .collect(Collectors.toList());
    }

    public Date getOngoingResvReturnDate() {
        Optional<Date> ongReturnDate = getOngoingResv()
                .stream()
                .map(ongoingResv -> localDatetoDate(ongoingResv.getEnd()))
                .findAny();

        Date returnDate = null;
        if (status.equals("available")) {
            return null;
        } else {
            if (ongReturnDate.isPresent()) {
                returnDate = ongReturnDate.get();
            } else {
                throw new RuntimeException("Return date does not exist");
            }
        }
        return returnDate;
    }

    public String getOngoingResvReturnLocationCity() {
        Optional<String> ongReturnLocationCity = getReservations()
                .stream()
                .map(ongoingResv -> ongoingResv.getReturnLocationCity())
                .findAny();

        String returnCity = null;
        if (status.equals("available")) {
            return null;
        } else {
            if (ongReturnLocationCity.isPresent()) {
                returnCity = ongReturnLocationCity.get();
            } else {
                throw new RuntimeException("Return location city does not exist");
            }
        }
        return returnCity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getLocationCity() {
        return location.getCity();
    }

    public void setLocationCity(String city) {
        location.setCity(city);
    }

    public List<Reservation> getReservations() {
        return reservations.stream()
                .sorted(((o1, o2) -> o1.getRentDate().compareTo(o2.getRentDate())))
                .collect(Collectors.toList());
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public List<Reservation> getUpcomingReservations() {
        List<Reservation> upcomingReservations = new ArrayList<>();

        upcomingReservations = reservations.stream()
                .filter(reservation -> dateToLocalDate(reservation.getRentDate()).isAfter(LocalDate.now()))
                .sorted(((o1, o2) -> (o1.getRentDate().compareTo(o2.getRentDate()))))
                .collect(Collectors.toList());

        return upcomingReservations;
    }

    public Location getLocationOfLastReservation(Date rentDate, Date returnDate) {

        Location result;
        if (getReservations().size() == 0 ||
                rentDate.before(getReservations().get(0).getRentDate()) ||
                doesUpcomingReservationsContainsDate(rentDate) == true ||
                doesUpcomingReservationsContainsDate(returnDate) == true ||
                returnDate.before(rentDate))
        {
            result = getLocation();
        } else {
            Date lastReturnDate = getReservations()
                    .stream()
                    .map(reservation -> reservation.getReturnDate())
                    .filter(reservation -> reservation.before(rentDate))
                    .reduce((first, last) -> last)
                    .get();

            Location locationOfLastReservation = getReservations()
                    .stream()
                    .filter(reservation -> reservation.getReturnDate() == lastReturnDate)
                    .map(reservation -> reservation.getReturnLocation())
                    .findFirst()
                    .orElseThrow(null);

            result = locationOfLastReservation;
        }
        return result;
    }

    public List<Reservation> getUpcomingReservationsAfterThisReservation(Date rentDate){
        List<Reservation> upcomingReservationsAfterThisReservation = getUpcomingReservations()
                .stream()
                .filter(reservation -> reservation.getRentDate().after(rentDate))
                .collect(Collectors.toList());

        return upcomingReservationsAfterThisReservation;

    }

    public Location getRentLocationOfFirstUpcomingReservation(Date rentDate) {

            Date firstFutureRentDate = getUpcomingReservations()
                    .stream()
                    .map(reservation -> reservation.getRentDate())
                    .filter(reservation -> reservation.after(rentDate))
                    .sorted()
                    .reduce((first, last) -> first)
                    .get();

            Location rentLocationOfFirstUpcomingReservation = getUpcomingReservations()
                    .stream()
                    .filter(reservation -> reservation.getRentDate() == firstFutureRentDate)
                    .map(reservation -> reservation.getRentLocation())
                    .findFirst()
                    .orElseThrow(null);

        return rentLocationOfFirstUpcomingReservation;
    }

    public List<DateTimeInterval> getUpcomingReservationIntervals() {
        List<DateTimeInterval> upcomingReservationsDates = getUpcomingReservations()
                .stream()
                .map(reservation -> new DateTimeInterval(dateToLocalDate(reservation.getRentDate()), dateToLocalDate(reservation.getReturnDate())))
                .collect(Collectors.toList());

        return upcomingReservationsDates;
    }

    public boolean doesUpcomingReservationsContainsDate(Date date) {
        boolean result;
        if (getUpcomingReservationIntervals()
                .stream()
                .filter(interval -> interval.contains(dateToLocalDate(date)))
                .findFirst().isPresent()) {
            result = true;}
        else {
            result = false;
        }
        return result;
    }



    public void addReservation(Reservation reservation) {
        if (this.reservations.size() == 0) {
            this.reservations = Arrays.asList(reservation);
        } else {
            this.reservations.add(reservation);
        }
    }

    public void deleteReservation(Reservation reservation) {
        this.reservations.remove(reservation);
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
        return
                "No.: " + id + "., "+
                        "Brand: " + brand + ", " +
                        "Model: " + model + ", " +
                        "Description: " + description + ", " +
                        "Price for a day: " + priceForADay + " €, " +
                        "Status: " + status;
    }
}
