package com.example.spring.restdocs.demo;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.IdGenerator;
import org.springframework.util.StringUtils;

import java.util.UUID;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.restdocs.snippet.Attributes.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;

@RunWith(SpringRunner.class)
@WebMvcTest
@AutoConfigureRestDocs("target/generated-snippets")
public class ApiDocumentMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestDocumentationResultHandler documentationHandler;

    private static final FieldDescriptor[] accountFields = new FieldDescriptor[]{
            fieldWithPath("id").description("An account's identity"),
            fieldWithPath("name").description("An user's name")};


    @Test
    public void postAccount() throws Exception {
        ConstrainedFields fields = new ConstrainedFields(SpringRestDocsDemoApplication.Account.class);
        this.mockMvc
                // Test
                .perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(JsonPath.parse("{}")
                                .put("$", "name", "山田　二郎")
                                .jsonString()))
                // Assertions
                .andExpect(status().isCreated())
                // Documentation
                .andDo(documentationHandler.document(
                        requestHeaders(
                                headerWithName("Content-Type").description("A media type for JSON ('application/json;charset=UTF-8')")),
                        requestFields(
                                fields.withPath("name").description("An user's name"))
                ));
    }

    @Test
    public void getAccounts() throws Exception {
        this.mockMvc
                // Test
                .perform(get("/api/v1/accounts"))
                // Assertions
                .andExpect(status().isOk())
                // Documentation
                .andDo(documentationHandler.document(
                        responseFields(
                                fieldWithPath("[]").description("A list of account")).andWithPrefix("[].", accountFields)));
    }

    @Test
    public void getAccount() throws Exception {
        this.mockMvc
                // Test
                .perform(get("/api/v1/accounts/{id}", "59cbbe8a-27da-484c-9859-771279460049"))
                // Assertions
                .andExpect(status().isOk())
                // Documentation
                .andDo(documentationHandler.document(
                        pathParameters(
                                parameterWithName("id").description("An account's identity")),
                        responseFields(
                                accountFields)));
    }

    private static class ConstrainedFields {

        private final ConstraintDescriptions constraintDescriptions;

        ConstrainedFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        private FieldDescriptor withPath(String path) {
            return fieldWithPath(path).attributes(key("constraints").value(StringUtils
                    .collectionToDelimitedString(this.constraintDescriptions
                            .descriptionsForProperty(path), " ", "", ".")));
        }
    }

    @TestConfiguration
    static class TestContext {
        @Bean
        IdGenerator idGenerator() {
            return () -> UUID.fromString("48f6f7fb-6582-43b3-b552-b1403d9e9e1f");
        }

        @Bean
        public RestDocumentationResultHandler documentationHandler() {
            return document(
                    "{method-name}"
                    , preprocessRequest(
                            prettyPrint()
                    )
                    , preprocessResponse(
                            prettyPrint()
                    )
            );
        }
    }

}
