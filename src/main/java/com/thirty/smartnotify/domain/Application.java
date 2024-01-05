package com.thirty.smartnotify.domain;

import com.thirty.smartnotify.model.StatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "email")
    private String senderEmail;
    @Column(name = "company")
    private String companyName;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEnum status;

    public Application(String senderEmail, String companyName, StatusEnum status) {
        this.senderEmail = senderEmail;
        this.companyName = companyName;
        this.status = status;
    }
}
