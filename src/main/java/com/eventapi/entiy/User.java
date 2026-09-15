package com.eventapi.entiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    @ManyToOne
    @JoinColumn(name = "role_id")//FK role_id
    private Role role;

    @Column(nullable = false, unique = true, length = 60)
    private String username;

    @Column(nullable = false, length = 36, name = "supabase_user_id")
    private String supabaseUserId;

    @Column(nullable = false)
    private boolean enabled;


    //Muchos User puede agregar a Muchos Event como Favorito
    @ManyToMany
    @JoinTable(name = "favorites", //PK Compuesta buyer_id + event_id
            joinColumns = @JoinColumn(name = "buyer_id"), //PK FK buyer_id
            inverseJoinColumns = @JoinColumn(name = "event_id")) // PK FK event_id
    private List<Event> favoriteEvents;

}