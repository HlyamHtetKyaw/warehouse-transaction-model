//PackagePurchase
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

//Track who purchased which packages
@Entity
@Table(name = "package_purchases")
public class PackagePurchase implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint", unique = true, nullable = false)
    private Long id;
    
    @Column(name = "purchase_id", columnDefinition = "varchar(100)", nullable = false, unique = true)
    private String purchaseId;
    
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @ManyToOne
    @JoinColumn(name = "package_id", nullable = false)
    private CreditPackage creditPackage;
    
    @Column(name = "credits_purchased", columnDefinition = "decimal(10, 2)", nullable = false)
    private BigDecimal creditsPurchased;
    
    @Column(name = "amount_paid", columnDefinition = "decimal(10, 2)", nullable = false)
    private BigDecimal amountPaid;
    
    @Column(name = "currency", columnDefinition = "varchar(3)", nullable = false)
    private String currency;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column(name = "payment_gateway_transaction_id", columnDefinition = "varchar(255)")
    private String paymentGatewayTransactionId;
    
    @Column(name = "purchased_on", updatable = false)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date purchasedOn;
    
    @Column(name = "completed_on")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date completedOn;
    
    @Column(name = "refunded_on")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date refundedOn;
    
    @Column(name = "notes", columnDefinition = "text")
    private String notes;
    
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
        if (this.purchasedOn == null) {
            this.purchasedOn = new Date();
        }
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

    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public CreditPackage getCreditPackage() {
        return creditPackage;
    }

    public void setCreditPackage(CreditPackage creditPackage) {
        this.creditPackage = creditPackage;
    }

    public BigDecimal getCreditsPurchased() {
        return creditsPurchased;
    }

    public void setCreditsPurchased(BigDecimal creditsPurchased) {
        this.creditsPurchased = creditsPurchased;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentGatewayTransactionId() {
        return paymentGatewayTransactionId;
    }

    public void setPaymentGatewayTransactionId(String paymentGatewayTransactionId) {
        this.paymentGatewayTransactionId = paymentGatewayTransactionId;
    }

    public Date getPurchasedOn() {
        return purchasedOn;
    }

    public void setPurchasedOn(Date purchasedOn) {
        this.purchasedOn = purchasedOn;
    }

    public Date getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(Date completedOn) {
        this.completedOn = completedOn;
    }

    public Date getRefundedOn() {
        return refundedOn;
    }

    public void setRefundedOn(Date refundedOn) {
        this.refundedOn = refundedOn;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

