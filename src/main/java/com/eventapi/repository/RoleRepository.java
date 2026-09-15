package com.eventapi.repository;

import com.eventapi.entiy.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}