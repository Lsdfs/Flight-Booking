package SE2.flightBooking.controller;

import SE2.flightBooking.model.FaqQuestion;
import SE2.flightBooking.repository.FaqQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatbox")
@CrossOrigin(origins = "*")
public class ChatboxController {

    @Autowired
    private FaqQuestionRepository faqQuestionRepository;

    @GetMapping("/ask")
    public String getAnswer(@RequestParam String question) {
        try {
            List<FaqQuestion> matchedQuestions = faqQuestionRepository.searchFaq(question);

            if (matchedQuestions == null || matchedQuestions.isEmpty()) {
                return "Xin lỗi, tôi không tìm thấy câu trả lời phù hợp.";
            }
            return matchedQuestions.get(0).getAnswer(); // Trả lời câu hỏi khớp nhất

        } catch (Exception e) {
            return "Lỗi hệ thống. Vui lòng thử lại sau!";
        }
    }
}
