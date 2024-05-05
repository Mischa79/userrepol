package de.telran.userrepo;


import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest // спринг загружает только контроллеры
@AutoConfigureMockMvc // предоставляет ручку для http запросов
public class ControllerTest {

    @Autowired
    MockMvc mockMvc; // ручка для http запросов

    @MockBean // мок - "заглушка"
    UserRepository repository;

    @Test
    public void createValidUser() throws Exception
    {
        mockMvc
                .perform(
                        post("/users")
                                .content("{ \"name\": \"max\", \"email\": \"max@gmail.com\" }")
                                .contentType("application/json")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("User is valid"));
    }

    @Test
    public void blankNameAndBlankEmail() throws Exception
    {
        mockMvc
                .perform(
                        post("/users")
                                .content("{  }")
                                .contentType("application/json")
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.email", Is.is("Email is mandatory"))
                )
                .andExpect(
                        jsonPath("$.name", Is.is("Name is mandatory"))
                );
    }


    @Test
    public void nameEmailCombo() throws Exception {
        // настройка mock - "заглушки"
        when(repository.findById(1L)).thenReturn(
                Optional.of(new User(1L, "max", "max@gmail.com"))
        );


        mockMvc.perform(
                get("/user/1")
        )
                .andExpect(status().isOk())
                .andExpect(content().string("max:max@gmail.com"));


        // функция findById мока вызывалась с параметром 1L один раз
        verify(repository, times(1)).findById(1L);

        // больше никаких взаимодействий с моком не было
        verifyNoMoreInteractions(repository);

    }



}
