package com.eventapi.repository;

import com.eventapi.entiy.Category;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CategoryRepositoryTest {

    private final CategoryRepository categoryRepository;

    @Test
    void save_shouldPersistCategory() {
        System.out.println(">>> Probando: guardar una categoría nueva.");

        Category category = new Category();
        category.setName("Concierto");

        Category saved = categoryRepository.save(category);

        System.out.println(">>> Resultado: " + saved);

        assertThat(saved.getIdCategory()).isNotNull();
    }

    @Test
    void findByName_shouldReturnCategory() {
        System.out.println(">>> Probando: Query Method que busca una categoría por su nombre.");

        categoryRepository.save(new Category(null, "Deporte"));

        Optional<Category> found = categoryRepository.findByName("Deporte");

        System.out.println(">>> Resultado: " + found.orElse(null));

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Deporte");
    }
}
