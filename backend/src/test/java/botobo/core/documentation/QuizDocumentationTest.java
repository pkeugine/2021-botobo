package botobo.core.documentation;

import botobo.core.application.QuizService;
import botobo.core.domain.user.AppUser;
import botobo.core.dto.card.QuizRequest;
import botobo.core.dto.card.QuizResponse;
import botobo.core.ui.QuizController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("퀴즈 문서화 테스트")
@WebMvcTest(QuizController.class)
public class QuizDocumentationTest extends DocumentationTest {

    @MockBean
    private QuizService quizService;

    @Test
    @DisplayName("문제집 id(Long)를 이용해서 퀴즈 생성 - 성공")
    void createQuiz() throws Exception {
        // given
        QuizRequest quizRequest = new QuizRequest(Arrays.asList(1L, 2L, 3L), 10);
        given(quizService.createQuiz(any(QuizRequest.class), any(AppUser.class))).willReturn(generateQuizResponses());

        // when, then
        document()
                .mockMvc(mockMvc)
                .post("/api/quizzes", quizRequest)
                .auth(authenticatedToken())
                .build()
                .status(status().isOk())
                .identifier("quizzes-post-success");
    }

    @Test
    @DisplayName("문제집에서 바로 풀기 - 성공")
    void createQuizFromWorkbook() throws Exception {
        // given
        Long workbookId = 1L;
        given(quizService.createQuizFromWorkbook(workbookId)).willReturn(generateQuizResponses());

        // when, then
        document()
                .mockMvc(mockMvc)
                .get("/api/quizzes/{workbookId}", workbookId)
                .auth(authenticatedToken())
                .build()
                .status(status().isOk())
                .identifier("quizzes-from-workbook-get-success");
    }

    @Test
    @DisplayName("비회원용 퀴즈 생성 - 성공")
    void createQuizForGuest() throws Exception {
        // given
        given(quizService.createQuizForGuest()).willReturn(generateQuizResponses());

        // when, then
        document()
                .mockMvc(mockMvc)
                .get("/api/quizzes/guest")
                .build()
                .status(status().isOk())
                .identifier("quizzes-guest-get-success");
    }

    private List<QuizResponse> generateQuizResponses() {
        return Arrays.asList(
                QuizResponse.builder()
                        .id(3L)
                        .question("Javascript 질문 2")
                        .answer("Javascript 답 2")
                        .workbookName("Javascript")
                        .build(),
                QuizResponse.builder()
                        .id(10L)
                        .question("Java 질문 1")
                        .answer("Java 답 1")
                        .workbookName("JAVA")
                        .build(),
                QuizResponse.builder()
                        .id(2L)
                        .question("Java 질문 3")
                        .answer("Java 답 3")
                        .workbookName("JAVA")
                        .build(),
                QuizResponse.builder()
                        .id(11L)
                        .question("Spring 질문 1")
                        .answer("Java 답 1")
                        .workbookName("JAVA")
                        .build(),
                QuizResponse.builder()
                        .id(1L)
                        .question("Java 질문 2")
                        .answer("Java 답 2")
                        .workbookName("JAVA")
                        .build(),
                QuizResponse.builder()
                        .id(4L)
                        .question("Spring 질문 6")
                        .answer("Spring 답 6")
                        .workbookName("Spring")
                        .build(),
                QuizResponse.builder()
                        .id(7L)
                        .question("Javascript 질문 9")
                        .answer("Javascript 답 9")
                        .workbookName("Javascript")
                        .build(),
                QuizResponse.builder()
                        .id(21L)
                        .question("Spring 질문 3")
                        .answer("Spring 답 3")
                        .workbookName("Spring")
                        .build(),
                QuizResponse.builder()
                        .id(17L)
                        .question("Javascript 질문 4")
                        .answer("Javascript 답 4")
                        .workbookName("Javascript")
                        .build(),
                QuizResponse.builder()
                        .id(14L)
                        .question("Javascript 질문 1")
                        .answer("Javascript 답 1")
                        .workbookName("Javascript")
                        .build()
        );
    }
}
