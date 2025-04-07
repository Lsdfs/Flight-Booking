package SE2.flightBooking.controller;

import SE2.flightBooking.model.FaqCategory;
import SE2.flightBooking.model.FaqQuestion;
import SE2.flightBooking.repository.FaqCategoryRepository;
import SE2.flightBooking.repository.FaqQuestionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/faq")
public class FaqController {
    private final FaqCategoryRepository categoryRepository;
    private final FaqQuestionRepository questionRepository;

    public FaqController(FaqCategoryRepository categoryRepository, FaqQuestionRepository questionRepository) {
        this.categoryRepository = categoryRepository;
        this.questionRepository = questionRepository;
    }


    @GetMapping("/categories")
    public String getCategories(Model model) {
        List<FaqCategory> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "categories";
    }

    @GetMapping("/questions/{categoryId}")
    public String getQuestionsByCategory(@PathVariable Long categoryId, Model model) {
        Optional<FaqCategory> category = categoryRepository.findById(categoryId);
        if (category.isPresent()) {
            List<FaqQuestion> questions = questionRepository.findByCategoryId(categoryId);
            model.addAttribute("category", category.get());
            model.addAttribute("questions", questions);
            return "questions";
        }
        return "redirect:/faq/categories";
    }
}
