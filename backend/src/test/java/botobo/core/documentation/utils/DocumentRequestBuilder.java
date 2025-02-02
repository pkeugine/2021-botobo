package botobo.core.documentation.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Objects;

import static botobo.core.documentation.utils.DocumentationUtils.getDocumentRequest;
import static botobo.core.documentation.utils.DocumentationUtils.getDocumentResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

public class DocumentRequestBuilder {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static MockMvc mockMvc;

    public MockMvcFunction build() {
        return new MockMvcFunction();
    }

    public static class MockMvcFunction {
        public <T> Options post(String path, T body, Object... params) {
            return new Options(new PostPerform<>(path, body, params));
        }

        public <T> Options put(String path, T body, Object... params) {
            return new Options(new PutPerform<>(path, body, params));
        }

        public Options putWithoutBody(String path, Object... params) {
            return new Options(new PutPerform<>(path, params));
        }

        public Options get(String path, Object... params) {
            return new Options(new GetPerform(path, params));
        }

        public Options delete(String path, Object... params) {
            return new Options(new DeletePerform(path, params));
        }

        public <T> Options multipart(String path, String body, String fileName) {
            return new Options(new Multipart<>(path, body, fileName));
        }

        public MockMvcFunction mockMvc(MockMvc mockMvc) {
            DocumentRequestBuilder.mockMvc = mockMvc;
            return this;
        }
    }

    public static class Options {
        private final HttpMethodRequest request;
        private ResultActions resultActions;
        private boolean loginFlag;
        private boolean headerFlag;
        private String location;
        private String accessToken;

        public Options(HttpMethodRequest request) {
            this.request = request;
            loginFlag = false;
        }

        public Options auth(String token) {
            this.accessToken = token;
            this.loginFlag = true;
            return this;
        }

        public Options locationHeader(String uri) {
            this.headerFlag = true;
            this.location = uri;
            return this;
        }

        public Options build() throws Exception {
            MockHttpServletRequestBuilder requestBuilder = request.doAction();
            if (loginFlag) {
                requestBuilder = requestBuilder.header("Authorization", "Bearer " + accessToken);
            }
            resultActions = mockMvc.perform(requestBuilder);
            if (headerFlag) {
                resultActions = resultActions.andExpect(header().string("Location", location));
            }
            return this;
        }

        public Options status(ResultMatcher resultStatus) throws Exception {
            resultActions = resultActions.andExpect(resultStatus);
            return this;
        }

        public void identifier(String documentIdentifier) throws Exception {
            resultActions.andDo(document(documentIdentifier,
                    getDocumentRequest(),
                    getDocumentResponse()));
        }
    }

    interface HttpMethodRequest {
        MockHttpServletRequestBuilder doAction() throws JsonProcessingException;
    }

    private static class PostPerform<T> implements HttpMethodRequest {
        private final String path;
        private final T body;
        private final Object[] params;

        public PostPerform(String path, T body, Object[] params) {
            this.path = path;
            this.body = body;
            this.params = params;
        }

        public MockHttpServletRequestBuilder doAction() throws JsonProcessingException {
            return post(path, params)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(body));
        }
    }

    private static class Multipart<T> implements HttpMethodRequest {
        private final String path;
        private final String body;
        private final String fileName;

        public Multipart(String path, String body, String fileName) {
            this.path = path;
            this.body = body;
            this.fileName = fileName;
        }

        public MockHttpServletRequestBuilder doAction() throws JsonProcessingException {
            return multipart(path).file(body, fileName.getBytes());
        }
    }

    private static class PutPerform<T> implements HttpMethodRequest {
        private final String path;
        private final T body;
        private final Object[] params;

        public PutPerform(String path, Object[] params) {
            this(path, null, params);
        }

        public PutPerform(String path, T body, Object[] params) {
            this.path = path;
            this.body = body;
            this.params = params;
        }

        public MockHttpServletRequestBuilder doAction() throws JsonProcessingException {
            MockHttpServletRequestBuilder mockHttpServletRequestBuilder = put(path, params)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE);

            if (Objects.nonNull(body)) {
                mockHttpServletRequestBuilder.content(objectMapper.writeValueAsString(body));
            }
            return mockHttpServletRequestBuilder;
        }
    }

    private static class GetPerform implements HttpMethodRequest {
        private final String path;
        private final Object[] params;

        public GetPerform(String path, Object[] params) {
            this.path = path;
            this.params = params;
        }

        @Override
        public MockHttpServletRequestBuilder doAction() {
            return get(path, params);
        }
    }

    private static class DeletePerform implements HttpMethodRequest {
        private final String path;
        private final Object[] params;

        public DeletePerform(String path, Object[] params) {
            this.path = path;
            this.params = params;
        }

        @Override
        public MockHttpServletRequestBuilder doAction() {
            return delete(path, params);
        }
    }
}
