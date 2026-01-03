//CreditTransaction
package com.code.aplusbinary.accountmanagement.model.credit;

import com.code.aplusbinary.accountmanagement.model.Account;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

//Track all credit movements (purchases, consumption, refunds)
@Entity
@Table(name = "credit_transactions")
public class CreditTransaction implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint", unique = true, nullable = false)
    private Long id;
    
    @Column(name = "transaction_id", columnDefinition = "varchar(100)", nullable = false, unique = true)
    private String transactionId;
    
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;
    
    @Column(name = "amount", columnDefinition = "decimal(10, 2)", nullable = false)
    private BigDecimal amount;
    
    @Column(name = "balance_before", columnDefinition = "decimal(10, 2)", nullable = false)
    private BigDecimal balanceBefore;
    
    @Column(name = "balance_after", columnDefinition = "decimal(10, 2)", nullable = false)
    private BigDecimal balanceAfter;
    
    @Column(name = "reserved_before", columnDefinition = "decimal(10, 2) default 0.00", nullable = false)
    private BigDecimal reservedBefore = BigDecimal.ZERO;
    
    @Column(name = "reserved_after", columnDefinition = "decimal(10, 2) default 0.00", nullable = false)
    private BigDecimal reservedAfter = BigDecimal.ZERO;
    
    @Column(name = "reservation_id", columnDefinition = "varchar(100)")
    private String reservationId;
    
    @Column(name = "reference_id", columnDefinition = "varchar(100)")
    private String referenceId;
    
    @Column(name = "reference_type", columnDefinition = "varchar(50)")
    private String referenceType;
    
    @ManyToOne
    @JoinColumn(name = "package_purchase_id")
    private PackagePurchase packagePurchase;
    
    @Column(name = "description", columnDefinition = "text")
    private String description;
    
    @Column(name = "created_on", updatable = false)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date createdOn;

    @Column(name = "modified_on")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date modifiedOn;
    
    @PrePersist
    protected void onInsert() {
        this.modifiedOn = new Date();
        this.createdOn = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifiedOn = new Date();
    }

    // Getters and Setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(BigDecimal balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public BigDecimal getReservedBefore() {
        return reservedBefore;
    }

    public void setReservedBefore(BigDecimal reservedBefore) {
        this.reservedBefore = reservedBefore;
    }

    public BigDecimal getReservedAfter() {
        return reservedAfter;
    }

    public void setReservedAfter(BigDecimal reservedAfter) {
        this.reservedAfter = reservedAfter;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public PackagePurchase getPackagePurchase() {
        return packagePurchase;
    }

    public void setPackagePurchase(PackagePurchase packagePurchase) {
        this.packagePurchase = packagePurchase;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }
}
