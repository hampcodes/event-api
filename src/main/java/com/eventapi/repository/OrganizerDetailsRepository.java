package com.eventapi.repository;

import com.eventapi.entiy.OrganizerDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizerDetailsRepository extends JpaRepository<OrganizerDetails, Long> {
}