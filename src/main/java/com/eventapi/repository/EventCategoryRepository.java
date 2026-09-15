package com.eventapi.repository;

import com.eventapi.entiy.EventCategory;
import com.eventapi.entiy.EventCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventCategoryRepository
        extends JpaRepository<EventCategory, EventCategoryId> {

    //Query Method: Verifica si esa categoría está usada en al menos un event
    /* existsBy → genera una consulta de existencia (no trae la entidad completa, solo comprueba si hay resultados).
        - CategoryIdCategory → navega la relación EventCategory.category (el @ManyToOne) y llega hasta el campo idCategory de Category.
    */
    boolean existsByCategoryIdCategory(Long categoryId);
}