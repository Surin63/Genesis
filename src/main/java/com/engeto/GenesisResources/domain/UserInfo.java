package com.engeto.GenesisResources.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLOutput;

@Entity
@Getter
@Setter
@ToString
public class UserInfo {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String surname;

    @Column(nullable = false, unique = true, length =  12)
    private String personId;

    @Column(nullable = false, unique = true)
    private String uuid;

    public UserInfo(){

    }

    public UserInfo(String name, String surname, String personId, String uuid) {
        this.name = name;
        this.surname = surname;
        this.personId = personId;
        this.uuid = uuid;
    }
}


