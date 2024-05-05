package de.telran.userrepo;

import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;

@RunWith(SpringRunner.class) // JUnit4 тест для спринг приложения
// по-умолчанию спринг стартует на порту 8080 но во время тестирования этот
// порт может быть занят
// WebEnvironment.RANDOM_PORT говорит спрингу чтобы для тестирования
// приложение стартовало на каком-то рандомном порту
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    // в эту переменну спринг сохранит номер порта на котором запустился
    @Value(value = "${local.server.port}")
    private int port;

    // ручка с помощью которой можно вызвать методы контроллеров по http
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository repository;

    @Test
    public void validUserIsCreated() throws Exception {

        assertEquals(repository.count(), 0L);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<String> request = new HttpEntity<>(
                "{  \"name\": \"max\",   \"email\": \"max@gmail.com\"   }",
                headers
        );

        String result =
                restTemplate.postForEntity(
                        "http://localhost:" + port + "/users",
                        request,
                        String.class
                ).getBody();

        assertEquals(repository.count(), 1L);

        User user = repository.findById(1L).orElse(null);
        assertNotNull(user);
        assertEquals(user.getEmail(), "max@gmail.com");
        assertEquals(user.getName(), "max");

        assertEquals(result, "User is valid");
    }

    @Test
    public void invalidEmail() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<String> request = new HttpEntity<>(
                "{  \"name\": \"max\",   \"email\": \"gmail.com\"   }",
                headers
        );

        String result =
                restTemplate.postForEntity(
                        "http://localhost:" + port + "/users",
                        request,
                        String.class
                ).getBody();


        Matchers.contains(jsonPath("$.email", Is.is("Email should be valid")));

    }

    @Test
    public void helloUpper()
    {

        String result =
                restTemplate.getForEntity(
                        "http://localhost:" + port + "/upper/hello",
                        String.class
                ).getBody();

        assertEquals("HELLO", result);
    }



}
