package com.aplusbinary.binarypixor.doc.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

//Define what users pay (markup applied)
@Entity
@Table(name = "operation_pricing", indexes = {
    @Index(name = "idx_operation_code", columnList = "operation_code"),
    @Index(name = "idx_operation_type", columnList = "operation_type"),
    @Index(name = "idx_active", columnList = "is_active")
})
public class OperationPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "operation_code", nullable = false, unique = true, length = 50)
    private String operationCode;

    @Column(name = "operation_type", nullable = false, length = 50)
    private String operationType;

    @Column(name = "operation_name", nullable = false, length = 100)
    private String operationName;

    @Column(name = "credit_cost", nullable = false, precision = 10, scale = 4)
    private BigDecimal creditCost;

    @Column(name = "cost_model", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private CostModel costModel;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_on", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "modified_on", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedOn;

    // Constructors
    public OperationPricing() {
    }

    public OperationPricing(String operationCode, String operationType, String operationName, 
                           BigDecimal creditCost, CostModel costModel) {
        this.operationCode = operationCode;
        this.operationType = operationType;
        this.operationName = operationName;
        this.creditCost = creditCost;
        this.costModel = costModel;
    }

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
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public BigDecimal getCreditCost() {
        return creditCost;
    }

    public void setCreditCost(BigDecimal creditCost) {
        this.creditCost = creditCost;
    }

    public CostModel getCostModel() {
        return costModel;
    }

    public void setCostModel(CostModel costModel) {
        this.costModel = costModel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
