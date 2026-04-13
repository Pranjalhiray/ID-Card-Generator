package com.idcard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "id_cards")
public class IDCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(unique = true)
    private String cardNumber;

    private LocalDateTime issuedDate;
    private LocalDateTime expiryDate;
    private String pdfPath;

    @Enumerated(EnumType.STRING)
    private CardStatus cardStatus = CardStatus.ACTIVE;

    public enum CardStatus { ACTIVE, EXPIRED, REVOKED }

    public IDCard() {}

    public Long getId()                 { return id; }
    public User getUser()               { return user; }
    public String getCardNumber()       { return cardNumber; }
    public LocalDateTime getIssuedDate(){ return issuedDate; }
    public LocalDateTime getExpiryDate(){ return expiryDate; }
    public String getPdfPath()          { return pdfPath; }
    public CardStatus getCardStatus()   { return cardStatus; }

    public void setId(Long v)                       { this.id = v; }
    public void setUser(User v)                     { this.user = v; }
    public void setCardNumber(String v)             { this.cardNumber = v; }
    public void setIssuedDate(LocalDateTime v)      { this.issuedDate = v; }
    public void setExpiryDate(LocalDateTime v)      { this.expiryDate = v; }
    public void setPdfPath(String v)                { this.pdfPath = v; }
    public void setCardStatus(CardStatus v)         { this.cardStatus = v; }
}
