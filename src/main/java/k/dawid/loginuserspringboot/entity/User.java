package k.dawid.loginuserspringboot.entity;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name="user")
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id")
    private int id;

    @Column(name="firstname")
    private String firstname;

    @Column(name="lastname")
    private String lastname;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="active")
    private int active;

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name="user_role",
            joinColumns=@JoinColumn(name="user_id"),
            inverseJoinColumns=@JoinColumn(name="role_id"))
    private List<Role> roles;

    @OneToMany(mappedBy="user", cascade={CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    private List<Reservation> reservations;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Reservation> getReservations() {
        return reservations.stream()
                .sorted(((o1, o2) -> o1.getRentDate().compareTo(o2.getRentDate())))
                .collect(Collectors.toList());
    }

    public void addReservation (Reservation reservation) {
        if (this.reservations.size() == 0){
            this.reservations = Arrays.asList(reservation);
        } else {
            this.reservations.add(reservation);
        }
    }

    public boolean getIsAdmin(){
        boolean result;

        List<String> stringRoles = roles.stream()
                .map(role -> role.getRole())
                .collect(Collectors.toList());

        if (stringRoles.contains("ADMIN")){
            result = true;
        } else
            result = false;
        return result;
    }
}
