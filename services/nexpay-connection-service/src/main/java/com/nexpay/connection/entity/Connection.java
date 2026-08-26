package com.nexpay.connection.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "connection")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Connection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long connectionId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long connectedUserId;
}