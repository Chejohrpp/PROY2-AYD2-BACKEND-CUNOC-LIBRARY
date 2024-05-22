package com.hrp.libreriacunocbackend.controllers.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrp.libreriacunocbackend.controllers.AbstractMvcTest;
import com.hrp.libreriacunocbackend.dto.editorial.EditorialRequestDTO;
import com.hrp.libreriacunocbackend.dto.editorial.EditorialResponseDTO;
import com.hrp.libreriacunocbackend.entities.book.Book;
import com.hrp.libreriacunocbackend.entities.book.Editorial;
import com.hrp.libreriacunocbackend.exceptions.DuplicatedEntityException;
import com.hrp.libreriacunocbackend.service.editorial.EditorialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@WebMvcTest(EditorialController.class)
@ContextConfiguration(classes = {EditorialController.class, EditorialService.class})
public class EditorialControllerTest extends AbstractMvcTest {

    @MockBean
    private EditorialService editorialService;

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testCreate() throws Exception {
        // Arrange
        EditorialRequestDTO editorialRequestDTO = new EditorialRequestDTO("1", "Editorial Test");

        Editorial editorial = new Editorial();
        editorial.setIdEditorial("1");
        editorial.setName("Editorial Test");

        EditorialResponseDTO editorialResponseDTO = new EditorialResponseDTO(editorial);

        when(editorialService.create(any(EditorialRequestDTO.class))).thenReturn(editorialResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/v1/editorial/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editorialRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    // Deserializar el JSON en un Map
                    Map<String, Object> map = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});

                    // Obtener los valores de las propiedades del Map
                    String idEditorial = (String) map.get("idEditorial");
                    String name = (String) map.get("name");

                    // Realizar las aserciones
                    assert name.equals("Editorial Test");
                    assert idEditorial.equals("1");
                });
    }


    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testGetByNameFilter() throws Exception {
        // Arrange
        Editorial editorial1 = new Editorial();
        editorial1.setIdEditorial("1");
        editorial1.setName("Editorial A");
        EditorialResponseDTO editorialDto1 = new EditorialResponseDTO(editorial1);
        List<EditorialResponseDTO> responseList = Collections.singletonList(editorialDto1);

        when(editorialService.getByFilter("Editorial")).thenReturn(responseList);

        // Act & Assert
        mockMvc.perform(get("/v1/editorial/get_name_filter")
                        .param("filter", "Editorial")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(Collections.singletonList(editorialDto1))));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void getByNameFilter_noResults() throws Exception {
        when(editorialService.getByFilter("Nonexistent")).thenReturn(List.of());

        mockMvc.perform(get("/v1/editorial/get_name_filter")
                        .param("filter", "Nonexistent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void getByNameFilter_badRequest() throws Exception {
        mockMvc.perform(get("/v1/editorial/get_name_filter")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
