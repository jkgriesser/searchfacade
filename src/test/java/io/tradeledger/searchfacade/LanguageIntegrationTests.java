package io.tradeledger.searchfacade;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LanguageIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void findById() throws JSONException, IOException {
        String java8ObjectId = getJsonValue(searchForJava8(), "id");
        URI targetUrl = UriComponentsBuilder.fromUriString(createURLWithPort())
                .path("languages/" + java8ObjectId)
                .build()
                .toUri();
        ResponseEntity<String> response = getResponse(targetUrl);
        String expected = "{\"id\":\"" + java8ObjectId
                + "\",\"language\":\"Java\",\"version\":8,\"jvmBased\":true}";

        // Language found
        JSONAssert.assertEquals(expected, response.getBody(), true);

        targetUrl = UriComponentsBuilder.fromUriString(createURLWithPort())
                .path("languages/INVALID_ID")
                .build()
                .toUri();
        response = getResponse(targetUrl);

        assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void searchWithEqualsAndValue() {
        URI targetUrl = UriComponentsBuilder.fromUriString(createURLWithPort())
                .path("languages/search")
                .queryParam("filter", "{\"attribute\":\"language\"," +
                        "\"operator\":\"eq\",\"value\":\"Java\"}")
                .build()
                .toUri();
        ResponseEntity<String> response = getResponse(targetUrl);
        String expected = "\"language\":\"Java\",\"version\":7";

        // Languages found
        assertThat(response.getBody()).contains(expected);

        targetUrl = UriComponentsBuilder.fromUriString(createURLWithPort())
                .path("languages/search")
                .queryParam("filter", "{\"attribute\":\"language\"," +
                        "\"operator\":\"eq\",\"value\":\"INVALID_LANGUAGE\"}")
                .build()
                .toUri();
        response = getResponse(targetUrl);
        expected = "[]";

        // Language not found
        assertThat(response.getBody()).contains(expected);
    }

    @Test
    public void searchWithEqualsAndRange() {
        URI targetUrl = UriComponentsBuilder.fromUriString(createURLWithPort())
                .path("languages/search")
                .queryParam("filter", "{\"attribute\":\"version\"," +
                        "\"operator\":\"eq\",\"range\":{\"from\":7,\"to\":8}}")
                .build()
                .toUri();
        ResponseEntity<String> response = getResponse(targetUrl);
        String expectedVersion7 = "\"language\":\"Java\",\"version\":7";
        String expectedVersion8 = "\"language\":\"Java\",\"version\":8";

        // Languages found
        assertThat(response.getBody()).contains(expectedVersion7);
        assertThat(response.getBody()).contains(expectedVersion8);

        targetUrl = UriComponentsBuilder.fromUriString(createURLWithPort())
                .path("languages/search")
                .queryParam("filter", "{\"attribute\":\"version\"," +
                        "\"operator\":\"eq\",\"range\":{\"from\":-1,\"to\":0}}")
                .build()
                .toUri();
        response = getResponse(targetUrl);
        String expected = "[]";

        // Language not found
        assertThat(response.getBody()).contains(expected);
    }

    @Test
    public void searchWithGreaterThanEquals() {
        URI targetUrl = UriComponentsBuilder.fromUriString(createURLWithPort())
                .path("languages/search")
                .queryParam("filter", "{\"attribute\":\"version\"," +
                        "\"operator\":\"gte\",\"value\":8}")
                .build()
                .toUri();
        ResponseEntity<String> response = getResponse(targetUrl);
        String expected = "\"language\":\"Java\",\"version\":8";

        // Language found
        assertThat(response.getBody()).contains(expected);

        targetUrl = UriComponentsBuilder.fromUriString(createURLWithPort())
                .path("languages/search")
                .queryParam("filter", "{\"attribute\":\"version\"," +
                        "\"operator\":\"gte\",\"value\":999}")
                .build()
                .toUri();
        response = getResponse(targetUrl);
        expected = "[]";

        // Language not found
        assertThat(response.getBody()).contains(expected);
    }

    @Test
    public void searchWithLowerThanEquals() {
        URI targetUrl = UriComponentsBuilder.fromUriString(createURLWithPort())
                .path("languages/search")
                .queryParam("filter", "{\"attribute\":\"version\"," +
                        "\"operator\":\"lte\",\"value\":7}")
                .build()
                .toUri();
        ResponseEntity<String> response = getResponse(targetUrl);
        String expected = "\"language\":\"Java\",\"version\":7";

        // Language found
        assertThat(response.getBody()).contains(expected);

        targetUrl = UriComponentsBuilder.fromUriString(createURLWithPort())
                .path("languages/search")
                .queryParam("filter", "{\"attribute\":\"version\"," +
                        "\"operator\":\"lte\",\"value\":0}")
                .build()
                .toUri();
        response = getResponse(targetUrl);
        expected = "[]";

        // Language not found
        assertThat(response.getBody()).contains(expected);
    }

    @Test
    public void searchWithBoolean() {
        URI targetUrl = UriComponentsBuilder.fromUriString(createURLWithPort())
                .path("languages/search")
                .queryParam("filter", "{\"attribute\":\"isJvmBased\"," +
                        "\"operator\":\"eq\",\"value\":\"true\"}")
                .build()
                .toUri();
        ResponseEntity<String> response = getResponse(targetUrl);
        String expected = "\"language\":\"Java\",\"version\":7";

        // Language found
        assertThat(response.getBody()).contains(expected);

        targetUrl = UriComponentsBuilder.fromUriString(createURLWithPort())
                .path("languages/search")
                .queryParam("filter", "{\"attribute\":\"isJvmBased\"," +
                        "\"operator\":\"eq\",\"value\":\"false\"}")
                .build()
                .toUri();
        response = getResponse(targetUrl);
        expected = "[]";

        // Language not found
        assertThat(response.getBody()).contains(expected);
    }

    @Test
    public void searchWithTwoFilters() {
        URI targetUrl = UriComponentsBuilder.fromUriString("/languages/")
                .path("search")
                .queryParam("filter", "{\"attribute\":\"language\"," +
                        "\"operator\":\"eq\",\"value\":\"Java\"}")
                .queryParam("filter", "{\"attribute\":\"version\"," +
                        "\"operator\":\"eq\",\"range\":{\"from\":7,\"to\":8}}")
                .build()
                .toUri();
        ResponseEntity<String> response = getResponse(targetUrl);
        String expectedVersion7 = "\"language\":\"Java\",\"version\":7";
        String expectedVersion8 = "\"language\":\"Java\",\"version\":8";

        // Languages found
        assertThat(response.getBody()).contains(expectedVersion7);
        assertThat(response.getBody()).contains(expectedVersion8);

        targetUrl = UriComponentsBuilder.fromUriString("/languages/")
                .path("search")
                .queryParam("filter", "{\"attribute\":\"language\"," +
                        "\"operator\":\"eq\",\"value\":\"Java\"}")
                .queryParam("filter", "{\"attribute\":\"version\"," +
                        "\"operator\":\"eq\",\"range\":{\"from\":-1,\"to\":0}}")
                .build()
                .toUri();
        response = getResponse(targetUrl);
        String expected = "[]";

        // No languages found
        assertThat(response.getBody()).contains(expected);
    }

    @Test
    public void searchWithTwoFiltersAndDuplicateAttributes() {
        URI targetUrl = UriComponentsBuilder.fromUriString("/languages/")
                .path("search")
                .queryParam("filter", "{\"attribute\":\"language\"," +
                        "\"operator\":\"eq\",\"value\":\"Java\"}")
                .queryParam("filter", "{\"attribute\":\"language\"," +
                        "\"operator\":\"eq\",\"value\":\"Java\"}")
                .build()
                .toUri();
        ResponseEntity<String> response = getResponse(targetUrl);

        assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private String createURLWithPort() {
        return "http://localhost:" + port;
    }

    private ResponseEntity<String> getResponse(URI targetUrl) {
        HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());
        return restTemplate.exchange(targetUrl,
                HttpMethod.GET, entity, String.class);
    }

    private String searchForJava8() {
        URI targetUrl = UriComponentsBuilder.fromUriString(createURLWithPort())
                .path("languages/search")
                .queryParam("filter", "{\"attribute\":\"language\",\"operator\":\"eq\",\"value\":\"Java\"}")
                .queryParam("filter", "{\"attribute\":\"version\",\"operator\":\"eq\",\"value\":8}")
                .build()
                .toUri();

        HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());
        return restTemplate.exchange(targetUrl, HttpMethod.GET, entity, String.class)
                .getBody();
    }

    private String getJsonValue(String json, String nodeName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(json);
        return actualObj.findPath(nodeName).asText();
    }

}
