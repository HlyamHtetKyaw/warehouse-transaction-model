package com.code.aplusbinary.accountmanagement.model.credit;

import com.code.aplusbinary.accountmanagement.model.Account;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

//Track user's current credit balance with reservation
@Entity
@Table(name = "user_credits")
public class UserCredits implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint", unique = true, nullable = false)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private Account account;
    
    @Column(name = "current_balance", columnDefinition = "decimal(10, 2) default 0.00", nullable = false)
    private BigDecimal currentBalance = BigDecimal.ZERO;
    
    @Column(name = "reserved_balance", columnDefinition = "decimal(10, 2) default 0.00", nullable = false)
    private BigDecimal reservedBalance = BigDecimal.ZERO;
    
    @Column(name = "total_purchased", columnDefinition = "decimal(10, 2) default 0.00", nullable = false)
    private BigDecimal totalPurchased = BigDecimal.ZERO;
    
    @Column(name = "total_consumed", columnDefinition = "decimal(10, 2) default 0.00", nullable = false)
    private BigDecimal totalConsumed = BigDecimal.ZERO;
    
    @Column(name = "allocated_to_children", columnDefinition = "decimal(10, 2) default 0.00", nullable = false)
    private BigDecimal allocatedToChildren = BigDecimal.ZERO;
    
    @Column(name = "allocated_from_parent", columnDefinition = "decimal(10, 2) default 0.00", nullable = false)
    private BigDecimal allocatedFromParent = BigDecimal.ZERO;
    
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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public BigDecimal getReservedBalance() {
        return reservedBalance;
    }

    public void setReservedBalance(BigDecimal reservedBalance) {
        this.reservedBalance = reservedBalance;
    }
    
    public BigDecimal getAvailableBalance() {
        if (currentBalance == null || reservedBalance == null) {
            return BigDecimal.ZERO;
        }
        return currentBalance.subtract(reservedBalance);
    }
    
    /**
     * Get the allocatable balance - credits that can be allocated to child accounts.
     * This is the available balance minus what's already allocated to children.
     */
    public BigDecimal getAllocatableBalance() {
        BigDecimal available = getAvailableBalance();
        if (available == null || allocatedToChildren == null) {
            return BigDecimal.ZERO;
        }
        return available;
    }

    public BigDecimal getTotalPurchased() {
        return totalPurchased;
    }

    public void setTotalPurchased(BigDecimal totalPurchased) {
        this.totalPurchased = totalPurchased;
    }

    public BigDecimal getTotalConsumed() {
        return totalConsumed;
    }

    public void setTotalConsumed(BigDecimal totalConsumed) {
        this.totalConsumed = totalConsumed;
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

    public BigDecimal getAllocatedToChildren() {
        return allocatedToChildren;
    }

    public void setAllocatedToChildren(BigDecimal allocatedToChildren) {
        this.allocatedToChildren = allocatedToChildren;
    }

    public BigDecimal getAllocatedFromParent() {
        return allocatedFromParent;
    }

    public void setAllocatedFromParent(BigDecimal allocatedFromParent) {
        this.allocatedFromParent = allocatedFromParent;
    }
}
