package com.nekonihongo.backend.service;

import com.nekonihongo.backend.dto.QuestionResponseDTO;
import com.nekonihongo.backend.entity.GrammarQuestion;
import com.nekonihongo.backend.repository.GrammarQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GrammarQuestionService {

    private final GrammarQuestionRepository questionRepository;

    public List<QuestionResponseDTO> getQuestionsByLesson(Integer lessonId) {
        List<GrammarQuestion> questions = questionRepository.findByLessonId(lessonId);
        return questions.stream()
                .map(QuestionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public boolean hasQuestionsForLesson(Integer lessonId) {
        return questionRepository.existsByLessonId(lessonId);
    }

    public long countQuestionsByLesson(Integer lessonId) {
        return questionRepository.findByLessonId(lessonId).size();
    }
}