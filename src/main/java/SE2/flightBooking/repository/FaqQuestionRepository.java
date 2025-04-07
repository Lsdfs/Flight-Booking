package SE2.flightBooking.repository;

import SE2.flightBooking.model.FaqQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqQuestionRepository extends JpaRepository<FaqQuestion, Long> {
    List<FaqQuestion> findByCategoryId(Long categoryId);
    @Query(value = "SELECT * FROM faq_question WHERE MATCH(question) AGAINST(:keyword IN NATURAL LANGUAGE MODE)", nativeQuery = true)
    List<FaqQuestion> searchFaq(@Param("keyword") String keyword);
}

