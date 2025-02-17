package net.mausberg.recruitingtests.service;

import net.mausberg.recruitingtests.dto.QuestionDTO;
import net.mausberg.recruitingtests.model.Question;
import net.mausberg.recruitingtests.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    public Optional<QuestionDTO> getQuestionDTOById(Long id) {
        return questionRepository.findById(id).map(this::convertToDTO);
    }

    private QuestionDTO convertToDTO(Question question) {
        double avgTimeTaken = question.getOptions().stream()
                .flatMap(option -> option.getAnswers().stream())
                .mapToLong(answer -> answer.getTimeTaken())
                .average()
                .orElse(0.0);

        long totalAnswers = question.getOptions().stream()
                .flatMap(option -> option.getAnswers().stream())
                .count();

        double avgRight = totalAnswers > 0 ? question.getOptions().stream()
                .flatMap(option -> option.getAnswers().stream())
                .filter(answer -> answer.getGivenAnswer().isCorrect())
                .count() / (double) totalAnswers : 0.0;

        return new QuestionDTO(
                question.getId(),
                question.getCategory().name(),
                question.getComplexity(),
                question.getQuestion(),
                question.getLink(),
                avgTimeTaken,
                avgRight
        );
    }
}