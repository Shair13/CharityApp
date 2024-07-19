package pl.coderslab.charity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.coderslab.charity.model.Donation;
import pl.coderslab.charity.model.User;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    @Query("SELECT SUM(d.quantity) FROM Donation d")
    Integer sumQuantity();

    @Query("SELECT COUNT(d) FROM Donation d")
    Integer countDonations();

    @Query("SELECT d FROM Donation d WHERE d.user = ?1 ORDER BY d.archived ASC, d.realPickUpDate DESC, d.realPickUpTime DESC")
    List<Donation> findAllSortedByArchivedAndPickUpDate(User user);
}
