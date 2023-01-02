package com.example.tpbatch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferDto {
    private Integer idTransaction;
    private Integer idCompte;
    private Double montant;
    private LocalDateTime dateTransation;
}
