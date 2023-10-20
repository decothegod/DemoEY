package com.example.demoEY.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "phone")
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idPhone")
    @SequenceGenerator(name = "idPhone", initialValue = 5, allocationSize = 1)
    private Long id;
    private Long number;
    private Long citycode;
    private Long contrycode;
}
