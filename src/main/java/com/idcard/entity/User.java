package com.idcard.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Column(nullable = false)
    private String fullName;

    @Email(message = "Enter a valid email")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    private String department;
    private String designation;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phoneNumber;

    private String bloodGroup;
    private LocalDate dateOfBirth;
    private String photoPath;
    private String employeeId;
    private String address;
    private String organization;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private IDCard idCard;

    public enum Role { USER, ADMIN }
    public enum Status { PENDING, APPROVED, REJECTED }

    public User() {}

    public Long getId()              { return id; }
    public String getFullName()      { return fullName; }
    public String getEmail()         { return email; }
    public String getPassword()      { return password; }
    public Role getRole()            { return role; }
    public String getDepartment()    { return department; }
    public String getDesignation()   { return designation; }
    public String getPhoneNumber()   { return phoneNumber; }
    public String getBloodGroup()    { return bloodGroup; }
    public LocalDate getDateOfBirth(){ return dateOfBirth; }
    public String getPhotoPath()     { return photoPath; }
    public String getEmployeeId()    { return employeeId; }
    public String getAddress()       { return address; }
    public String getOrganization()  { return organization; }
    public Status getStatus()        { return status; }
    public IDCard getIdCard()        { return idCard; }

    public void setId(Long id)                  { this.id = id; }
    public void setFullName(String v)           { this.fullName = v; }
    public void setEmail(String v)              { this.email = v; }
    public void setPassword(String v)           { this.password = v; }
    public void setRole(Role v)                 { this.role = v; }
    public void setDepartment(String v)         { this.department = v; }
    public void setDesignation(String v)        { this.designation = v; }
    public void setPhoneNumber(String v)        { this.phoneNumber = v; }
    public void setBloodGroup(String v)         { this.bloodGroup = v; }
    public void setDateOfBirth(LocalDate v)     { this.dateOfBirth = v; }
    public void setPhotoPath(String v)          { this.photoPath = v; }
    public void setEmployeeId(String v)         { this.employeeId = v; }
    public void setAddress(String v)            { this.address = v; }
    public void setOrganization(String v)       { this.organization = v; }
    public void setStatus(Status v)             { this.status = v; }
    public void setIdCard(IDCard v)             { this.idCard = v; }
}
