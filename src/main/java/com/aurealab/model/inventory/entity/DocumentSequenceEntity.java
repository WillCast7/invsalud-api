package com.aurealab.model.inventory.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "document_sequences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSequenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String prefix;

    @Column(name = "last_value")
    private Long lastValue;
}
