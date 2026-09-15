package com.eventapi.entiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "organizer_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizerDetails {

    @Id
    private Long idUser;

    @OneToOne //UN user tipo Organizador solo tien un solo Detalle
    @MapsId //PK id_user
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "business_name", length = 100)
    private String businessName;

    @Column(name = "tax_id", length = 20)
    private String taxId;

    @Column(name = "bank_account", length = 30)
    private String bankAccount;
}