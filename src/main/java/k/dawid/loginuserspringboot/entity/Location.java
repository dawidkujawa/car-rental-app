package k.dawid.loginuserspringboot.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="location")
public class Location {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="city")
    private String city;

    @Column(name="address")
    private String address;

    @Column(name="phone_number")
    private int phoneNumber;

    @OneToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name="car_location",
            joinColumns = @JoinColumn(name="location_id"),
            inverseJoinColumns = @JoinColumn(name="car_id"))
    private List<Car> cars;

    public Location() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Car> getCars() {
        return cars;
    }

    public int getCarsNumber() {
        int carsNumber = 0;
        for (int i = 0; i < cars.size(); i++) {
            carsNumber++;
        }
        return carsNumber;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", cars=" + cars +
                '}';
    }
}
