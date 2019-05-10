package k.dawid.loginuserspringboot.service;

import k.dawid.loginuserspringboot.dao.ReservationRepository;
import k.dawid.loginuserspringboot.entity.Car;
import k.dawid.loginuserspringboot.entity.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("reservationService")
public class ReservationServiceImpl implements ReservationService {

    private ReservationRepository reservationRepository;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation findById(int id) {

        Optional<Reservation> result = reservationRepository.findById(id);

        Reservation reservation = null;
        if(result.isPresent()){
            reservation = result.get();
        } else {
            throw new RuntimeException("Did not find reservation id: " + id);
        }
        return reservation;
    }


    @Override
    public void save(Reservation reservation) {
        reservationRepository.save(reservation);
    }

    @Override
    public void deleteById(int id) {
        reservationRepository.deleteById(id);
    }
}
