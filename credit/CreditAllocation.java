//CreditAllocation
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

/**

 * Track credit allocations from parent accounts to child accounts in the hierarchy.
 * Maintains the flow of credits through the account hierarchy ensuring proper tallying.
 */
@Entity
@Table(name = "credit_allocations")
public class CreditAllocation implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint", unique = true, nullable = false)
    private Long id;
    
    @Column(name = "allocation_id", columnDefinition = "varchar(100)", nullable = false, unique = true)
    private String allocationId;
    
    @ManyToOne
    @JoinColumn(name = "from_account_id", nullable = false)
    private Account fromAccount;
    
    @ManyToOne
    @JoinColumn(name = "to_account_id", nullable = false)
    private Account toAccount;
    
    @Column(name = "allocated_amount", columnDefinition = "decimal(10, 2)", nullable = false)
    private BigDecimal allocatedAmount;
    
    @Column(name = "remaining_amount", columnDefinition = "decimal(10, 2)", nullable = false)
    private BigDecimal remainingAmount;
    
    @Column(name = "consumed_amount", columnDefinition = "decimal(10, 2) default 0.00", nullable = false)
    private BigDecimal consumedAmount = BigDecimal.ZERO;
    
    @Column(name = "returned_amount", columnDefinition = "decimal(10, 2) default 0.00", nullable = false)
    private BigDecimal returnedAmount = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AllocationStatus status = AllocationStatus.ACTIVE;
    
    @Column(name = "allocated_on", updatable = false)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date allocatedOn;
    
    @Column(name = "revoked_on")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date revokedOn;
    
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
        if (this.allocatedOn == null) {
            this.allocatedOn = new Date();
        }
        if (this.remainingAmount == null) {
            this.remainingAmount = this.allocatedAmount;
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

    public String getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(String allocationId) {
        this.allocationId = allocationId;
    }

    public Account getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(Account fromAccount) {
        this.fromAccount = fromAccount;
    }

    public Account getToAccount() {
        return toAccount;
    }

    public void setToAccount(Account toAccount) {
        this.toAccount = toAccount;
    }

    public BigDecimal getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(BigDecimal allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public BigDecimal getConsumedAmount() {
        return consumedAmount;
    }

    public void setConsumedAmount(BigDecimal consumedAmount) {
        this.consumedAmount = consumedAmount;
    }

    public BigDecimal getReturnedAmount() {
        return returnedAmount;
    }

    public void setReturnedAmount(BigDecimal returnedAmount) {
        this.returnedAmount = returnedAmount;
    }

    public AllocationStatus getStatus() {
        return status;
    }

    public void setStatus(AllocationStatus status) {
        this.status = status;
    }

    public Date getAllocatedOn() {
        return allocatedOn;
    }

    public void setAllocatedOn(Date allocatedOn) {
        this.allocatedOn = allocatedOn;
    }

    public Date getRevokedOn() {
        return revokedOn;
    }

    public void setRevokedOn(Date revokedOn) {
        this.revokedOn = revokedOn;
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
