package io.tradeledger.searchfacade;

import io.tradeledger.searchfacade.controller.LanguageController;
import io.tradeledger.searchfacade.filter.Filter;
import io.tradeledger.searchfacade.filter.LanguageFilter;
import io.tradeledger.searchfacade.model.Language;
import io.tradeledger.searchfacade.repository.LanguageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(LanguageController.class)
public class LanguageControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private LanguageRepository languageRepository;

    @Test
    public void findLanguageById() throws Exception {
        when(languageRepository.findById("1"))
                .thenReturn(Optional.of(new Language("1",
                        "Java", 8, true)));

        // Valid ID
        mvc.perform(get("/languages/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":\"1\"," +
                        "\"language\":\"Java\",\"version\":8,\"jvmBased\":true}"));

        // Invalid ID
        mvc.perform(get("/languages/INVALID_ID"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void searchWithValueFilter() throws Exception {
        Filter[] filters = {new LanguageFilter("language", "eq",
                "Java", null, null)};

        List<Language> languages = Collections.singletonList(new Language("1",
                "Java", 8, true));
        when(languageRepository.search(filters)).thenReturn(languages);

        URI targetUrl = UriComponentsBuilder.fromUriString("/languages/")
                .path("search")
                .queryParam("filter", "{\"attribute\":\"language\"," +
                        "\"operator\":\"eq\",\"value\":\"Java\"}")
                .build()
                .toUri();
        String expected = "[{\"id\":\"1\",\"language\":\"Java\"" +
                ",\"version\":8,\"jvmBased\":true}]";

        // Language found
        mvc.perform(get(targetUrl)).andExpect(status().isOk())
                .andExpect(content().string(expected));

        targetUrl = UriComponentsBuilder.fromUriString("/languages/")
                .path("search")
                .queryParam("filter", "{\"attribute\":\"language\"," +
                        "\"operator\":\"eq\",\"value\":\"INVALID_LANGUAGE\"}")
                .build()
                .toUri();

        // Language not found
        mvc.perform(get(targetUrl)).andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void searchWithRangeFilter() throws Exception {
        Filter[] filters = {new LanguageFilter("version", "eq",
                null, 7, 8)};

        List<Language> languages = Arrays.asList(new Language("1",
                        "Java", 8, true),
                new Language("2",
                        "Java", 7, true));
        when(languageRepository.search(filters)).thenReturn(languages);

        URI targetUrl = UriComponentsBuilder.fromUriString("/languages/")
                .path("search")
                .queryParam("filter", "{\"attribute\":\"version\"," +
                        "\"operator\":\"eq\",\"range\":{\"from\":7,\"to\":8}}")
                .build()
                .toUri();
        String expected = "[{\"id\":\"1\",\"language\":\"Java\"" +
                ",\"version\":8,\"jvmBased\":true}," +
                "{\"id\":\"2\",\"language\":\"Java\"" +
                ",\"version\":7,\"jvmBased\":true}]";

        // Languages found
        mvc.perform(get(targetUrl)).andExpect(status().isOk())
                .andExpect(content().string(expected));

        targetUrl = UriComponentsBuilder.fromUriString("/languages/")
                .path("search")
                .queryParam("filter", "{\"attribute\":\"language\"," +
                        "\"operator\":\"eq\",\"value\":\"Java\"}")
                .queryParam("filter", "{\"attribute\":\"version\"," +
                        "\"operator\":\"eq\",\"range\":{\"from\":0,\"to\":-1}}")
                .build()
                .toUri();

        // Languages not found
        mvc.perform(get(targetUrl)).andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void searchWithoutFilter() throws Exception {
        URI targetUrl = UriComponentsBuilder.fromUriString("/languages/")
                .path("search")
                .build()
                .toUri();

        mvc.perform(get(targetUrl)).andExpect(status().isBadRequest());
    }

    @Test
    public void searchWithTwoFilters() throws Exception {
        Filter[] filters = {new LanguageFilter("language", "eq",
                "Java", null, null),
                new LanguageFilter("version", "eq",
                        null, 7, 8)};

        List<Language> languages = Arrays.asList(new Language("1",
                        "Java", 8, true),
                new Language("2",
                        "Java", 7, true));
        when(languageRepository.search(filters)).thenReturn(languages);

        URI targetUrl = UriComponentsBuilder.fromUriString("/languages/")
                .path("search")
                .queryParam("filter", "{\"attribute\":\"language\"," +
                        "\"operator\":\"eq\",\"value\":\"Java\"}")
                .queryParam("filter", "{\"attribute\":\"version\"," +
                        "\"operator\":\"eq\",\"range\":{\"from\":7,\"to\":8}}")
                .build()
                .toUri();
        String expected = "[{\"id\":\"1\",\"language\":\"Java\"" +
                ",\"version\":8,\"jvmBased\":true}," +
                "{\"id\":\"2\",\"language\":\"Java\"" +
                ",\"version\":7,\"jvmBased\":true}]";

        // Languages found
        mvc.perform(get(targetUrl)).andExpect(status().isOk())
                .andExpect(content().string(expected));

        targetUrl = UriComponentsBuilder.fromUriString("/languages/")
                .path("search")
                .queryParam("filter", "{\"attribute\":\"language\"," +
                        "\"operator\":\"eq\",\"value\":\"Java\"}")
                .queryParam("filter", "{\"attribute\":\"version\"," +
                        "\"operator\":\"eq\",\"range\":{\"from\":0,\"to\":-1}}")
                .build()
                .toUri();

        // Languages not found
        mvc.perform(get(targetUrl)).andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void searchWithInvalidFilterField() throws Exception {
        URI targetUrl = UriComponentsBuilder.fromUriString("/languages/")
                .path("search")
                .queryParam("filter", "{\"attribute\":\"language\"," +
                        "\"operator\":\"eq\",\"value\":\"Java\",\"INVALID_FIELD\":\"Java\"}")
                .build()
                .toUri();

        mvc.perform(get(targetUrl)).andExpect(status().isBadRequest());
    }

    @Test
    public void searchWithInvalidOperator() throws Exception {
        URI targetUrl = UriComponentsBuilder.fromUriString("/languages/")
                .path("search")
                .queryParam("filter", "{\"attribute\":\"language\"," +
                        "\"operator\":\"INVALID_OPERATOR\",\"value\":\"Java\"}")
                .build()
                .toUri();

        mvc.perform(get(targetUrl)).andExpect(status().isBadRequest());
    }

    @Test
    public void searchWithoutMandatoryFields() throws Exception {
        // Missing attribute
        URI targetUrl = UriComponentsBuilder.fromUriString("/languages/")
                .path("search")
                .queryParam("filter", "{\"operator\":\"eq\",\"value\":\"Java\"}")
                .build()
                .toUri();

        mvc.perform(get(targetUrl)).andExpect(status().isBadRequest());

        // Missing operator
        targetUrl = UriComponentsBuilder.fromUriString("/languages/")
                .path("search")
                .queryParam("filter", "{\"attribute\":\"language\",\"value\":\"Java\"}")
                .build()
                .toUri();

        mvc.perform(get(targetUrl)).andExpect(status().isBadRequest());
    }

    @Test
    public void searchWithValueAndRange() throws Exception {
        URI targetUrl = UriComponentsBuilder.fromUriString("/languages/")
                .path("search")
                .queryParam("filter", "{\"attribute\":\"language\",\"operator\":\"eq\"," +
                        "\"value\":\"Java\",\"range\":{\"from\":1,\"to\":4}}")
                .build()
                .toUri();

        mvc.perform(get(targetUrl)).andExpect(status().isBadRequest());
    }

    @Test
    public void searchWithRangeAndNotEquals() throws Exception {
        // Operator: gte
        URI targetUrl = UriComponentsBuilder.fromUriString("/languages/")
                .path("search")
                .queryParam("filter", "{\"attribute\":\"language\"," +
                        "\"operator\":\"gte\",\"range\":{\"from\":1,\"to\":4}}")
                .build()
                .toUri();

        mvc.perform(get(targetUrl)).andExpect(status().isBadRequest());

        // Operator: lte
        targetUrl = UriComponentsBuilder.fromUriString("/languages/")
                .path("search")
                .queryParam("filter", "{\"attribute\":\"language\"," +
                        "\"operator\":\"lte\",\"range\":{\"from\":1,\"to\":4}}")
                .build()
                .toUri();

        mvc.perform(get(targetUrl)).andExpect(status().isBadRequest());
    }

}
