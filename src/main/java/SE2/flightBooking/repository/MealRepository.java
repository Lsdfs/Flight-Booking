package SE2.flightBooking.repository;

import SE2.flightBooking.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealRepository extends JpaRepository<Meal, Long> {

}