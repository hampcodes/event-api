package com.eventapi.entiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "profiles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {

    @Id
    private Long idUser;

    @OneToOne // Asociacion de un User  se asocia con un solo perfil
    @MapsId //PK profiles sera id_user
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Column(length = 150)
    private String address;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}