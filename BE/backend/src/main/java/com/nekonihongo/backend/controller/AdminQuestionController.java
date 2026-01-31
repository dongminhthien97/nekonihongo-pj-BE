// src/main/java/com/nekonihongo/backend/controller/AdminQuestionController.java
package com.nekonihongo.backend.controller;

import com.nekonihongo.backend.entity.GrammarQuestion;
import com.nekonihongo.backend.repository.GrammarQuestionRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/questions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminQuestionController {

    private final GrammarQuestionRepository grammarQuestionRepository;

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<?> getQuestionsByLesson(@PathVariable(name = "lessonId") Integer lessonId) {
        try {
            List<GrammarQuestion> questions = grammarQuestionRepository.findByLessonId(lessonId);

            if (questions.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "data", Collections.emptyList(),
                        "message", "No questions found for this lesson"));
            }

            List<Map<String, Object>> questionDTOs = questions.stream()
                    .map(this::convertToQuestionDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", questionDTOs,
                    "count", questions.size()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Error getting questions: " + e.getMessage()));
        }
    }

    @PostMapping("/evaluate-answers")
    public ResponseEntity<?> evaluateAnswers(@RequestBody EvaluateAnswersRequest request) {
        try {
            List<GrammarQuestion> questions = grammarQuestionRepository.findByLessonId(request.getLessonId());
            Map<Long, GrammarQuestion> questionMap = questions.stream()
                    .collect(Collectors.toMap(GrammarQuestion::getId, q -> q));

            List<EvaluatedAnswer> evaluatedAnswers = new ArrayList<>();
            int totalScore = 0;
            int maxPossibleScore = 0;

            Map<Long, List<UserAnswer>> answersByQuestion = request.getUserAnswers().stream()
                    .collect(Collectors.groupingBy(UserAnswer::getQuestionId));

            for (Map.Entry<Long, List<UserAnswer>> entry : answersByQuestion.entrySet()) {
                Long questionId = entry.getKey();
                List<UserAnswer> userAnswersForQuestion = entry.getValue();
                GrammarQuestion question = questionMap.get(questionId);

                if (question != null) {
                    maxPossibleScore += question.getPoints();

                    String[] correctAnswers = question.getCorrectAnswer().split(";");

                    for (int i = 0; i < userAnswersForQuestion.size(); i++) {
                        UserAnswer userAnswer = userAnswersForQuestion.get(i);

                        int subQuestionIndex = userAnswer.getSubQuestionIndex() != null
                                ? userAnswer.getSubQuestionIndex()
                                : i;

                        if (subQuestionIndex >= correctAnswers.length) {
                            subQuestionIndex = i % correctAnswers.length;
                        }

                        boolean isCorrect = evaluateAnswer(
                                userAnswer.getUserAnswer(),
                                question,
                                subQuestionIndex);

                        int points = isCorrect ? (question.getPoints() / correctAnswers.length) : 0;
                        totalScore += points;

                        String correctForSubQuestion = (subQuestionIndex < correctAnswers.length)
                                ? correctAnswers[subQuestionIndex].trim()
                                : correctAnswers[0].trim();

                        evaluatedAnswers.add(EvaluatedAnswer.builder()
                                .questionId(questionId)
                                .userAnswer(userAnswer.getUserAnswer())
                                .isCorrect(isCorrect)
                                .correctAnswer(correctForSubQuestion)
                                .allCorrectAnswers(question.getCorrectAnswer())
                                .subQuestionIndex(subQuestionIndex)
                                .points(points)
                                .maxPoints(question.getPoints() / correctAnswers.length)
                                .explanation(question.getExplanation())
                                .questionType(question.getType().name())
                                .questionText(question.getText())
                                .build());
                    }
                } else {
                    for (UserAnswer userAnswer : userAnswersForQuestion) {
                        evaluatedAnswers.add(EvaluatedAnswer.builder()
                                .questionId(questionId)
                                .userAnswer(userAnswer.getUserAnswer())
                                .isCorrect(false)
                                .correctAnswer("Câu hỏi không tồn tại")
                                .allCorrectAnswers("")
                                .subQuestionIndex(userAnswer.getSubQuestionIndex())
                                .points(0)
                                .maxPoints(0)
                                .explanation("Không tìm thấy câu hỏi trong database")
                                .questionType("unknown")
                                .questionText("")
                                .build());
                    }
                }
            }

            evaluatedAnswers.sort(Comparator
                    .comparing(EvaluatedAnswer::getQuestionId)
                    .thenComparing(EvaluatedAnswer::getSubQuestionIndex));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalScore", totalScore);
            response.put("maxPossibleScore", maxPossibleScore);
            response.put("percentage",
                    maxPossibleScore > 0 ? Math.round((double) totalScore / maxPossibleScore * 100) : 0);
            response.put("evaluatedAnswers", evaluatedAnswers);
            response.put("lessonId", request.getLessonId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Error evaluating answers: " + e.getMessage()));
        }
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<?> getQuestionById(@PathVariable(name = "questionId") Long questionId) {
        try {
            Optional<GrammarQuestion> questionOpt = grammarQuestionRepository.findById(questionId);

            if (questionOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of(
                        "success", false,
                        "message", "Question not found"));
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", convertToQuestionDTO(questionOpt.get())));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Error getting question"));
        }
    }

    @GetMapping("/lesson/{lessonId}/correct-answers")
    public ResponseEntity<?> getCorrectAnswersByLesson(@PathVariable(name = "lessonId") Integer lessonId) {
        try {
            List<GrammarQuestion> questions = grammarQuestionRepository.findByLessonId(lessonId);

            if (questions.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "data", List.of(),
                        "message", "No questions found for this lesson"));
            }

            List<Map<String, Object>> questionAnswers = questions.stream()
                    .map(q -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", q.getId());
                        map.put("lessonId", q.getLessonId());
                        map.put("type", q.getType().name());
                        map.put("text", q.getText());
                        map.put("correctAnswer", q.getCorrectAnswer());
                        map.put("points", q.getPoints());
                        map.put("explanation", q.getExplanation());

                        GrammarQuestion.QuestionType questionType = q.getType();

                        if (questionType == GrammarQuestion.QuestionType.fill_blank) {
                            String[] parts = q.getCorrectAnswer().split(";");
                            map.put("answerParts", Arrays.asList(parts));
                            map.put("numParts", parts.length);
                        } else if (questionType == GrammarQuestion.QuestionType.multiple_choice) {
                            String[] parts = q.getCorrectAnswer().split(";");
                            map.put("answerParts", Arrays.asList(parts));
                        }

                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", questionAnswers,
                    "count", questions.size()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Error getting correct answers: " + e.getMessage()));
        }
    }

    private boolean evaluateAnswer(String userAnswer, GrammarQuestion question, int subQuestionIndex) {
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return false;
        }

        String normalizedUser = userAnswer.trim().toLowerCase();
        String normalizedCorrect = question.getCorrectAnswer().trim().toLowerCase();

        GrammarQuestion.QuestionType type = question.getType();

        if (type == GrammarQuestion.QuestionType.fill_blank) {
            return evaluateFillBlankAnswer(normalizedUser, normalizedCorrect, subQuestionIndex);
        } else if (type == GrammarQuestion.QuestionType.multiple_choice) {
            return evaluateMultipleChoiceAnswer(normalizedUser, normalizedCorrect);
        } else if (type == GrammarQuestion.QuestionType.rearrange) {
            return evaluateRearrangeAnswer(normalizedUser, normalizedCorrect);
        } else {
            return normalizedUser.equals(normalizedCorrect);
        }
    }

    private boolean evaluateFillBlankAnswer(String userAnswer, String correctAnswers, int subQuestionIndex) {
        String[] allCorrectAnswers = correctAnswers.split(";");

        if (subQuestionIndex < 0 || subQuestionIndex >= allCorrectAnswers.length) {
            return false;
        }

        String correctForThisBlank = allCorrectAnswers[subQuestionIndex].trim();
        String[] possibleAnswers = correctForThisBlank.split("\\|");

        String normalizedUser = userAnswer.trim();

        for (String possible : possibleAnswers) {
            String normalizedPossible = possible.trim();

            if (normalizedUser.equals(normalizedPossible)) {
                return true;
            }

            if (normalizedUser.contains(normalizedPossible) || normalizedPossible.contains(normalizedUser)) {
                return true;
            }
        }

        return false;
    }

    private boolean evaluateMultipleChoiceAnswer(String userAnswer, String correctAnswers) {
        try {
            String[] allCorrectAnswers = correctAnswers.split(";");

            String normalizedUser = userAnswer.trim();

            for (String correct : allCorrectAnswers) {
                if (normalizedUser.equals(correct.trim())) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean evaluateRearrangeAnswer(String userAnswer, String correctAnswer) {
        String normalizedUser = userAnswer.trim();
        String normalizedCorrect = correctAnswer.trim();

        return normalizedUser.equals(normalizedCorrect);
    }

    private String getCorrectAnswerForSubQuestion(String allCorrectAnswers,
            GrammarQuestion.QuestionType type,
            int subQuestionIndex) {
        if (type == GrammarQuestion.QuestionType.fill_blank) {
            String[] parts = allCorrectAnswers.split(";");
            if (subQuestionIndex >= 0 && subQuestionIndex < parts.length) {
                return parts[subQuestionIndex].trim();
            }
        }

        return allCorrectAnswers;
    }

    private boolean compareSingleAnswer(String userAnswer, String correctAnswer, GrammarQuestion.QuestionType type) {
        if (type == GrammarQuestion.QuestionType.multiple_choice) {
            return userAnswer.equals(correctAnswer);
        } else if (type == GrammarQuestion.QuestionType.fill_blank) {
            String[] possibleAnswers = correctAnswer.split("\\|");
            return Arrays.stream(possibleAnswers)
                    .map(String::trim)
                    .anyMatch(correct -> userAnswer.equals(correct) ||
                            userAnswer.contains(correct) ||
                            correct.contains(userAnswer));
        } else if (type == GrammarQuestion.QuestionType.rearrange) {
            return userAnswer.equals(correctAnswer);
        } else {
            return userAnswer.equals(correctAnswer);
        }
    }

    private Map<String, Object> convertToQuestionDTO(GrammarQuestion question) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", question.getId());
        dto.put("lessonId", question.getLessonId());
        dto.put("type", question.getType().name());
        dto.put("example", question.getExample());
        dto.put("text", question.getText());
        dto.put("options", question.getOptions());
        dto.put("correctAnswer", question.getCorrectAnswer());
        dto.put("points", question.getPoints());
        dto.put("explanation", question.getExplanation());
        dto.put("createdAt", question.getCreatedAt());
        dto.put("updatedAt", question.getUpdatedAt());
        return dto;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvaluateAnswersRequest {
        private Integer lessonId;
        private List<UserAnswer> userAnswers;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserAnswer {
        private Long questionId;
        private String userAnswer;
        private Integer subQuestionIndex;
        private String originalAnswer;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvaluatedAnswer {
        private Long questionId;
        private String userAnswer;
        private Boolean isCorrect;
        private String correctAnswer;
        private String allCorrectAnswers;
        private Integer subQuestionIndex;
        private Integer points;
        private Integer maxPoints;
        private String explanation;
        private String questionType;
        private String questionText;
    }
}