package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerIntegrationTest {

    private final MockMvc mockMvc;
    private final UserRepository userRepository;

    @Test
    void getUser_shouldReturnUserWhenExists() throws Exception {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        User savedUser = userRepository.save(user);

        mockMvc.perform(get("/users/{userId}", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedUser.getId())))
                .andExpect(jsonPath("$.name", is("Test User")))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        userRepository.deleteById(savedUser.getId());
    }

    @Test
    void getUser_shouldReturn404WhenNotExists() throws Exception {
        mockMvc.perform(get("/users/{userId}", 9999))
                .andExpect(status().isNotFound());
    }
}
