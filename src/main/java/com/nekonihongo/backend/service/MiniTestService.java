package com.nekonihongo.backend.service;

import com.nekonihongo.backend.dto.*;
import com.nekonihongo.backend.entity.MiniTestSubmission;
import com.nekonihongo.backend.entity.MiniTestSubmission.Status;
import com.nekonihongo.backend.entity.User;
import com.nekonihongo.backend.repository.MiniTestSubmissionRepository;
import com.nekonihongo.backend.repository.UserRepository;
import com.nekonihongo.backend.security.UserPrincipal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MiniTestService {

    private final MiniTestSubmissionRepository submissionRepository;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    public List<MiniTestSubmissionDTO> getAllSubmissions() {
        List<MiniTestSubmission> all = submissionRepository.findAllByOrderBySubmittedAtDesc();
        return all.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public CheckTestResponseDTO checkUserTestStatus(Long userId, Integer lessonId) {
        List<MiniTestSubmission> submissions = submissionRepository
                .findByUserIdAndLessonIdOrderBySubmittedAtDesc(userId, lessonId);
        boolean hasSubmitted = !submissions.isEmpty();
        Long submissionId = hasSubmitted ? submissions.get(0).getId() : null;

        return CheckTestResponseDTO.builder()
                .hasSubmitted(hasSubmitted)
                .submissionId(submissionId)
                .build();
    }

    @Transactional
    public SubmitTestResponseDTO submitTest(SubmitTestRequestDTO request) {
        try {
            if (request.getUserId() == null) {
                return SubmitTestResponseDTO.builder()
                        .success(false)
                        .message("User ID không được để trống")
                        .build();
            }

            if (request.getLessonId() == null) {
                return SubmitTestResponseDTO.builder()
                        .success(false)
                        .message("Lesson ID không được để trống")
                        .build();
            }

            String answersJson;
            try {
                answersJson = objectMapper.writeValueAsString(request.getAnswers());
            } catch (JsonProcessingException e) {
                return SubmitTestResponseDTO.builder()
                        .success(false)
                        .message("Lỗi khi xử lý câu trả lời: " + e.getMessage())
                        .build();
            }

            MiniTestSubmission submission = MiniTestSubmission.builder()
                    .userId(request.getUserId())
                    .lessonId(request.getLessonId())
                    .answers(answersJson)
                    .timeSpent(request.getTimeSpent() != null ? request.getTimeSpent() : 0)
                    .submittedAt(request.getSubmittedAt() != null ? request.getSubmittedAt() : LocalDateTime.now())
                    .status(Status.pending)
                    .feedback(null)
                    .feedbackAt(null)
                    .score(null)
                    .build();

            MiniTestSubmission savedSubmission = submissionRepository.save(submission);

            return SubmitTestResponseDTO.builder()
                    .success(true)
                    .message("Bài test đã được nộp thành công!")
                    .testId(savedSubmission.getId())
                    .submissionId(savedSubmission.getId())
                    .build();

        } catch (Exception e) {
            return SubmitTestResponseDTO.builder()
                    .success(false)
                    .message("Lỗi hệ thống khi nộp bài: " + e.getMessage())
                    .build();
        }
    }

    @Transactional
    public SubmitTestResponseDTO provideFeedback(Long submissionId, String feedback) {
        try {
            Optional<MiniTestSubmission> submissionOpt = submissionRepository.findById(submissionId);

            if (submissionOpt.isEmpty()) {
                return SubmitTestResponseDTO.builder()
                        .success(false)
                        .message("Không tìm thấy bài nộp")
                        .build();
            }

            MiniTestSubmission submission = submissionOpt.get();
            submission.setFeedback(feedback);
            submission.setFeedbackAt(LocalDateTime.now());
            submission.setStatus(Status.feedbacked);

            submissionRepository.save(submission);

            return SubmitTestResponseDTO.builder()
                    .success(true)
                    .message("Đã gửi feedback thành công")
                    .submissionId(submissionId)
                    .build();
        } catch (Exception e) {
            return SubmitTestResponseDTO.builder()
                    .success(false)
                    .message("Lỗi khi gửi feedback: " + e.getMessage())
                    .build();
        }
    }

    @Transactional
    public SubmitTestResponseDTO scoreAndFeedback(Long submissionId, String feedback, Integer score) {
        try {
            Optional<MiniTestSubmission> submissionOpt = submissionRepository.findById(submissionId);

            if (submissionOpt.isEmpty()) {
                return SubmitTestResponseDTO.builder()
                        .success(false)
                        .message("Không tìm thấy bài nộp")
                        .build();
            }

            MiniTestSubmission submission = submissionOpt.get();

            if (score == null) {
                score = 0;
            }

            if (score < 0) {
                score = 0;
            }

            Integer oldScore = submission.getScore() != null ? submission.getScore() : 0;
            boolean alreadyScored = submission.getScore() != null;

            submission.setFeedback(feedback);
            submission.setFeedbackAt(LocalDateTime.now());
            submission.setStatus(Status.feedbacked);
            submission.setScore(score);

            submissionRepository.save(submission);

            try {
                Optional<User> userOpt = userRepository.findById(submission.getUserId());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    int currentUserPoints = user.getPoints();
                    int newPoints;

                    if (alreadyScored) {
                        newPoints = currentUserPoints - oldScore + score;
                    } else {
                        newPoints = currentUserPoints + score;
                    }

                    newPoints = Math.max(newPoints, 0);
                    user.setPoints(newPoints);
                    userRepository.save(user);
                }
            } catch (Exception e) {
            }

            return SubmitTestResponseDTO.builder()
                    .success(true)
                    .message("Đã chấm điểm và gửi feedback thành công")
                    .submissionId(submissionId)
                    .build();
        } catch (Exception e) {
            return SubmitTestResponseDTO.builder()
                    .success(false)
                    .message("Lỗi khi chấm điểm: " + e.getMessage())
                    .build();
        }
    }

    public List<MiniTestSubmissionDTO> getUserSubmissions() {
        try {
            Long userId = getCurrentUserId();
            List<MiniTestSubmission> entities = submissionRepository.findByUserIdOrderBySubmittedAtDesc(userId);
            return entities.stream().map(this::convertToDto).collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    public int getUserFeedbackCount() {
        try {
            Long userId = getCurrentUserId();
            return (int) submissionRepository.countByUserIdAndStatus(userId, Status.feedbacked);
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public SubmitTestResponseDTO deleteUserSubmission(Long submissionId) {
        try {
            Long userId = getCurrentUserId();
            MiniTestSubmission entity = submissionRepository.findById(submissionId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bài nộp"));

            if (!entity.getUserId().equals(userId)) {
                return SubmitTestResponseDTO.builder()
                        .success(false)
                        .message("Không có quyền xóa bài nộp này")
                        .build();
            }

            Integer entityScore = entity.getScore();
            if (entityScore != null && entityScore > 0 && entity.getStatus() == Status.pending) {
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    int currentPoints = user.getPoints();
                    int newPoints = Math.max(currentPoints - entityScore, 0);
                    user.setPoints(newPoints);
                    userRepository.save(user);
                }
            }

            submissionRepository.delete(entity);

            return SubmitTestResponseDTO.builder()
                    .success(true)
                    .message("Đã xóa bài nộp thành công")
                    .submissionId(submissionId)
                    .build();
        } catch (Exception e) {
            return SubmitTestResponseDTO.builder()
                    .success(false)
                    .message("Lỗi khi xóa bài nộp: " + e.getMessage())
                    .build();
        }
    }

    public long getPendingCount() {
        try {
            return submissionRepository.countByStatus(Status.pending);
        } catch (Exception e) {
            return 0;
        }
    }

    public List<MiniTestSubmissionDTO> getPendingSubmissions() {
        try {
            List<MiniTestSubmission> submissions = submissionRepository
                    .findByStatusOrderBySubmittedAtDesc(Status.pending);
            return submissions.stream().map(this::convertToDto).collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<MiniTestSubmissionDTO> getAllSubmissionsForAdmin() {
        try {
            List<MiniTestSubmission> submissions = submissionRepository.findAllByOrderBySubmittedAtDesc();
            return submissions.stream().map(this::convertToDto).collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    public Optional<MiniTestSubmission> getSubmissionById(Long submissionId) {
        try {
            return submissionRepository.findById(submissionId);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<User> getUserInfoForSubmission(Long submissionId) {
        try {
            Optional<MiniTestSubmission> submissionOpt = submissionRepository.findById(submissionId);
            if (submissionOpt.isPresent()) {
                return userRepository.findById(submissionOpt.get().getUserId());
            }
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Transactional
    public SubmitTestResponseDTO deleteSubmissionByAdmin(Long submissionId) {
        try {
            MiniTestSubmission entity = submissionRepository.findById(submissionId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bài nộp"));

            Long userId = entity.getUserId();
            Integer entityScore = entity.getScore();
            if (entityScore != null && entityScore > 0 && entity.getStatus() == Status.pending) {
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    int currentPoints = user.getPoints();
                    int newPoints = Math.max(currentPoints - entityScore, 0);
                    user.setPoints(newPoints);
                    userRepository.save(user);
                }
            }

            submissionRepository.delete(entity);

            return SubmitTestResponseDTO.builder()
                    .success(true)
                    .message("Đã xóa bài nộp thành công")
                    .submissionId(submissionId)
                    .build();
        } catch (Exception e) {
            return SubmitTestResponseDTO.builder()
                    .success(false)
                    .message("Lỗi khi xóa bài nộp: " + e.getMessage())
                    .build();
        }
    }

    public long countPendingByLesson(Integer lessonId) {
        return submissionRepository.countPendingByLessonId(lessonId);
    }

    public long countFeedbackedByLesson(Integer lessonId) {
        return submissionRepository.countFeedbackedByLessonId(lessonId);
    }

    public List<MiniTestSubmission> getSubmissionsByLesson(Integer lessonId) {
        return submissionRepository.findByLessonId(lessonId);
    }

    private MiniTestSubmissionDTO convertToDto(MiniTestSubmission entity) {
        MiniTestSubmissionDTO dto = MiniTestSubmissionDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .lessonId(entity.getLessonId().longValue())
                .submittedAt(entity.getSubmittedAt())
                .feedback(entity.getFeedback())
                .feedbackAt(entity.getFeedbackAt())
                .status(entity.getStatus().name())
                .score(entity.getScore())
                .timeSpent(entity.getTimeSpent())
                .build();

        try {
            if (entity.getAnswers() != null && !entity.getAnswers().isEmpty()) {
                List<MiniTestSubmissionDTO.AnswerDTO> answers = parseAnswersJson(entity.getAnswers());
                dto.setAnswers(answers);
            } else {
                dto.setAnswers(List.of());
            }
        } catch (Exception e) {
            dto.setAnswers(List.of());
        }

        return dto;
    }

    private List<MiniTestSubmissionDTO.AnswerDTO> parseAnswersJson(String answersJson) {
        List<MiniTestSubmissionDTO.AnswerDTO> answers = new ArrayList<>();

        if (answersJson == null || answersJson.trim().isEmpty()) {
            return answers;
        }

        try {
            JsonNode rootNode = objectMapper.readTree(answersJson);

            if (rootNode.isArray()) {
                parseArrayFormat(rootNode, answers);
            } else if (rootNode.isObject()) {
                parseObjectWithArrayFormat(rootNode, answers);
            }

        } catch (Exception e) {
        }

        answers.sort(Comparator.comparing(MiniTestSubmissionDTO.AnswerDTO::getQuestionId));
        return answers;
    }

    private void parseArrayFormat(JsonNode arrayNode, List<MiniTestSubmissionDTO.AnswerDTO> answers) {
        for (JsonNode node : arrayNode) {
            try {
                Long questionId = extractQuestionId(node);
                String userAnswer = extractUserAnswer(node);

                if (questionId != null && userAnswer != null) {
                    MiniTestSubmissionDTO.AnswerDTO answerDTO = MiniTestSubmissionDTO.AnswerDTO.builder()
                            .questionId(questionId)
                            .userAnswer(userAnswer)
                            .build();
                    answers.add(answerDTO);
                }
            } catch (Exception e) {
            }
        }
    }

    private void parseObjectWithArrayFormat(JsonNode objectNode, List<MiniTestSubmissionDTO.AnswerDTO> answers) {
        Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
        int globalQuestionNumber = 1;

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String sectionKey = entry.getKey();
            JsonNode valueNode = entry.getValue();

            try {
                int sectionNumber;
                try {
                    sectionNumber = Integer.parseInt(sectionKey);
                } catch (NumberFormatException e) {
                    sectionNumber = globalQuestionNumber;
                }

                if (valueNode.isArray()) {
                    int questionInSection = 1;
                    for (JsonNode answerNode : valueNode) {
                        Long questionId = (long) globalQuestionNumber;
                        String userAnswer;

                        if (answerNode.isTextual()) {
                            userAnswer = answerNode.asText();
                        } else if (answerNode.isNumber()) {
                            userAnswer = String.valueOf(answerNode.asLong());
                        } else if (answerNode.isBoolean()) {
                            userAnswer = String.valueOf(answerNode.asBoolean());
                        } else if (answerNode.isObject()) {
                            userAnswer = extractUserAnswer(answerNode);
                            if (userAnswer == null) {
                                userAnswer = answerNode.toString();
                            }
                        } else {
                            userAnswer = answerNode.toString();
                        }

                        if (userAnswer != null && !userAnswer.isEmpty()) {
                            MiniTestSubmissionDTO.AnswerDTO answerDTO = MiniTestSubmissionDTO.AnswerDTO.builder()
                                    .questionId(questionId)
                                    .userAnswer(userAnswer)
                                    .build();
                            answers.add(answerDTO);
                        }

                        globalQuestionNumber++;
                        questionInSection++;
                    }
                } else if (valueNode.isObject()) {
                    Long questionId = extractQuestionIdFromKey(sectionKey, valueNode);
                    String userAnswer = extractUserAnswer(valueNode);

                    if (questionId == null) {
                        questionId = extractQuestionId(valueNode);
                    }

                    if (questionId != null && userAnswer != null) {
                        MiniTestSubmissionDTO.AnswerDTO answerDTO = MiniTestSubmissionDTO.AnswerDTO.builder()
                                .questionId(questionId)
                                .userAnswer(userAnswer)
                                .build();
                        answers.add(answerDTO);
                        globalQuestionNumber++;
                    }
                } else if (valueNode.isTextual() || valueNode.isNumber() || valueNode.isBoolean()) {
                    Long questionId = extractQuestionIdFromKey(sectionKey, null);
                    String userAnswer = valueNode.asText();

                    if (userAnswer == null || userAnswer.isEmpty()) {
                        userAnswer = String.valueOf(valueNode.asText());
                    }

                    if (questionId != null) {
                        MiniTestSubmissionDTO.AnswerDTO answerDTO = MiniTestSubmissionDTO.AnswerDTO.builder()
                                .questionId(questionId)
                                .userAnswer(userAnswer)
                                .build();
                        answers.add(answerDTO);
                        globalQuestionNumber++;
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    private Long extractQuestionId(JsonNode node) {
        if (node == null)
            return null;

        if (node.has("question_id") && !node.get("question_id").isNull()) {
            return node.get("question_id").asLong();
        }
        if (node.has("questionId") && !node.get("questionId").isNull()) {
            return node.get("questionId").asLong();
        }
        if (node.has("id") && !node.get("id").isNull()) {
            return node.get("id").asLong();
        }
        if (node.has("qid") && !node.get("qid").isNull()) {
            return node.get("qid").asLong();
        }
        if (node.has("question") && !node.get("question").isNull()) {
            return node.get("question").asLong();
        }

        return null;
    }

    private String extractUserAnswer(JsonNode node) {
        if (node == null)
            return null;

        if (node.has("user_answer") && !node.get("user_answer").isNull()) {
            return node.get("user_answer").asText();
        }
        if (node.has("userAnswer") && !node.get("userAnswer").isNull()) {
            return node.get("userAnswer").asText();
        }
        if (node.has("answer") && !node.get("answer").isNull()) {
            return node.get("answer").asText();
        }
        if (node.has("value") && !node.get("value").isNull()) {
            return node.get("value").asText();
        }
        if (node.has("text") && !node.get("text").isNull()) {
            return node.get("text").asText();
        }

        if (node.isTextual()) {
            return node.asText();
        }
        if (node.isNumber()) {
            return String.valueOf(node.asLong());
        }
        if (node.isBoolean()) {
            return String.valueOf(node.asBoolean());
        }

        return null;
    }

    private Long extractQuestionIdFromKey(String key, JsonNode valueNode) {
        try {
            String cleanKey = key.toLowerCase()
                    .replace("question_", "")
                    .replace("question", "")
                    .replace("q", "")
                    .replace("item_", "")
                    .replace("item", "")
                    .replace("_", "")
                    .trim();

            if (cleanKey.matches("\\d+")) {
                return Long.parseLong(cleanKey);
            }

            String[] parts = cleanKey.split("[^\\d]+");
            for (String part : parts) {
                if (!part.isEmpty() && part.matches("\\d+")) {
                    return Long.parseLong(part);
                }
            }

            if (valueNode != null) {
                Long questionId = extractQuestionId(valueNode);
                if (questionId != null) {
                    return questionId;
                }
            }

            try {
                return Long.parseLong(key);
            } catch (NumberFormatException e2) {
                return null;
            }

        } catch (Exception e) {
            return null;
        }
    }

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("Không xác thực được user");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }

        if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user từ token: " + username));
            return user.getId();
        }

        if (principal instanceof String username) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user từ token: " + username));
            return user.getId();
        }

        throw new RuntimeException("Loại principal không hỗ trợ: " + principal.getClass().getName());
    }

    public Map<String, Object> parseAnswersFromJson(String answersJson) {
        try {
            if (answersJson == null || answersJson.isEmpty()) {
                return Map.of();
            }
            return objectMapper.readValue(answersJson,
                    new TypeReference<Map<String, Object>>() {
                    });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi khi parse answers JSON", e);
        }
    }

    public List<MiniTestSubmissionDTO.AnswerDTO> parseAnswersToDtoList(String answersJson) {
        return parseAnswersJson(answersJson);
    }

    public String convertMapToJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting map to JSON", e);
        }
    }

    public Map<String, Object> convertJsonToMap(String json) {
        try {
            if (json == null || json.isEmpty()) {
                return Map.of();
            }
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to map", e);
        }
    }

    public void debugAllSubmissions() {
        List<MiniTestSubmission> allSubmissions = submissionRepository.findAll();
        for (MiniTestSubmission sub : allSubmissions) {
            try {
                if (sub.getAnswers() != null && !sub.getAnswers().isEmpty()) {
                    try {
                        JsonNode node = objectMapper.readTree(sub.getAnswers());
                        List<MiniTestSubmissionDTO.AnswerDTO> parsed = parseAnswersJson(sub.getAnswers());
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) {
            }
        }
    }
}