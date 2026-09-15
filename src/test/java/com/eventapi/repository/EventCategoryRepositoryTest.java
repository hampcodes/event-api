package com.eventapi.repository;

import com.eventapi.entiy.Category;
import com.eventapi.entiy.Event;
import com.eventapi.entiy.EventCategory;
import com.eventapi.entiy.EventStatus;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EventCategoryRepositoryTest {

    private final EventCategoryRepository eventCategoryRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;

    @Test
    void existsByCategoryIdCategory_shouldReturnTrue() {
        System.out.println(">>> Probando: Query Method que verifica si una categoría está usada en al menos un evento.");

        Event event = new Event();
        event.setName("Concierto de Rock");
        event.setDescription("Festival al aire libre");
        event.setDate(LocalDateTime.now().plusDays(10));
        event.setLocation("Parque Central");
        event.setImageUrl("https://example.com/images/concierto-rock.jpg");
        event.setCapacity(100);
        event.setAvailableTickets(100);
        event.setPrice(new BigDecimal("45.00"));
        event.setStatus(EventStatus.ACTIVE);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);

        Category category = new Category();
        category.setName("Música");
        categoryRepository.save(category);

        EventCategory eventCategory = new EventCategory();
        eventCategory.setEvent(event);
        eventCategory.setCategory(category);
        eventCategory.setIsPrimary(true);
        eventCategoryRepository.save(eventCategory);

        boolean exists = eventCategoryRepository.existsByCategoryIdCategory(category.getIdCategory());

        System.out.println(">>> Resultado: " + exists);

        assertThat(exists).isTrue();
    }
}
