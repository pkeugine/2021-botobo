package botobo.core.documentation;

import botobo.core.application.AuthService;
import botobo.core.application.CardService;
import botobo.core.dto.card.CardRequest;
import botobo.core.dto.card.CardResponse;
import botobo.core.dto.card.CardUpdateRequest;
import botobo.core.dto.card.CardUpdateResponse;
import botobo.core.dto.card.NextQuizCardsRequest;
import botobo.core.ui.CardController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("카드 문서화 테스트")
@WebMvcTest(CardController.class)
public class CardDocumentationTest extends DocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CardService cardService;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("카드 생성 - 성공")
    void createCard() throws Exception {
        // given
        CardRequest request = CardRequest.builder()
                .question("이것은 질문입니다!")
                .answer("그리고 답변입니다~")
                .workbookId(1L)
                .build();
        CardResponse response = CardResponse.builder()
                .id(1L)
                .question("이것은 질문입니다!")
                .answer("그리고 답변입니다~")
                .workbookId(1L)
                .encounterCount(0)
                .bookmark(false)
                .nextQuiz(false)
                .build();
        String token = "botobo.access.token";
        given(cardService.createCard(any())).willReturn(response);

        // when, then
        document()
                .mockMvc(mockMvc)
                .post("/api/cards", request)
                .auth(token)
                .build()
                .status(status().isCreated())
                .identifier("cards-post-success");
    }

    @Test
    @DisplayName("카드 수정 - 성공")
    void updateCard() throws Exception {
        // given
        CardUpdateRequest request = CardUpdateRequest.builder()
                .question("수정된 질문입니다!")
                .answer("그리고 수정된 답변입니다~")
                .workbookId(1L)
                .encounterCount(5)
                .bookmark(true)
                .nextQuiz(true)
                .build();
        CardUpdateResponse response = CardUpdateResponse.builder()
                .id(1L)
                .question("수정된 질문입니다!")
                .answer("그리고 수정된 답변입니다~")
                .workbookId(1L)
                .encounterCount(5)
                .bookmark(true)
                .nextQuiz(true)
                .build();
        String token = "botobo.access.token";
        given(cardService.updateCard(anyLong(), any())).willReturn(response);

        // when, then
        document()
                .mockMvc(mockMvc)
                .put("/api/cards/1", request)
                .auth(token)
                .build()
                .status(status().isOk())
                .identifier("cards-put-success");
    }

    @Test
    @DisplayName("카드 삭제 - 성공")
    void deleteCard() throws Exception {
        // given
        String token = "botobo.access.token";

        // when, then
        document()
                .mockMvc(mockMvc)
                .delete("/api/cards/1")
                .auth(token)
                .build()
                .status(status().isNoContent())
                .identifier("cards-delete-success");
    }

    @Test
    @DisplayName("또 보기 원하는 카드 선택 - 성공")
    void selectNextQuizCards() throws Exception {
        // given
        NextQuizCardsRequest request = NextQuizCardsRequest.builder()
                .cardIds(List.of(1L, 2L, 3L))
                .build();
        String token = "botobo.access.token";

        // when, then
        document()
                .mockMvc(mockMvc)
                .put("/api/cards/next-quiz", request)
                .auth(token)
                .build()
                .status(status().isNoContent())
                .identifier("cards-next-quiz-put-success");
    }
}
