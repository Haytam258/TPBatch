package com.example.tpbatch.entities;

import lombok.*;

import java.time.LocalDateTime;
import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private Double montant;

    @Column(nullable = false)
    private LocalDateTime dateTransaction;

    @Column(nullable = false)
    private LocalDateTime dateDebit;

    @ManyToOne
    @JoinColumn(name = "compte_id", nullable = false)
    private Compte compte;
}
