package com.futura.FuturaFlow.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String username;

    @Column(name = "last_name")
    private String lastName;

    private String email;
    private String password;
    private String phone;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    // --- НОВІ ПОЛЯ ДЛЯ ЮРИДИЧНОЇ ЛОГІКИ (BankID, YouControl, Договори) ---

    @Column(name = "ipn", length = 10)
    private String ipn; // Ідентифікаційний код (РНОКПП)

    @Column(name = "passport_data")
    private String passportData; // Серія, номер, ким виданий

    @Column(name = "legal_address")
    private String legalAddress; // Юридична адреса прописки

    @Column(name = "role")
    private String role; // Роль у системі: "FOP" або "INVESTOR"

    // ---------------------------------------------------------------------

    public User() {}

    // Стандартні геттери та сеттери
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    // Геттери та сеттери для нових полів
    public String getIpn() { return ipn; }
    public void setIpn(String ipn) { this.ipn = ipn; }

    public String getPassportData() { return passportData; }
    public void setPassportData(String passportData) { this.passportData = passportData; }

    public String getLegalAddress() { return legalAddress; }
    public void setLegalAddress(String legalAddress) { this.legalAddress = legalAddress; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}