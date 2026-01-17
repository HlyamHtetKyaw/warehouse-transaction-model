package com.aplusbinary.binarypixor.doc.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

//Link operations to their actual cost components
@Entity
@Table(name = "operation_cost_mapping", indexes = {
    @Index(name = "idx_operation_pricing_id", columnList = "operation_pricing_id"),
    @Index(name = "idx_pricing_config_id", columnList = "pricing_config_id")
})
public class OperationCostMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_pricing_id", nullable = false)
    private OperationPricing operationPricing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pricing_config_id", nullable = false)
    private PricingConfig pricingConfig;

    @Column(name = "cost_multiplier", precision = 10, scale = 4)
    private BigDecimal costMultiplier = BigDecimal.ONE;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_on", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "modified_on", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedOn;

    // Constructors
    public OperationCostMapping() {
    }

    public OperationCostMapping(OperationPricing operationPricing, PricingConfig pricingConfig) {
        this.operationPricing = operationPricing;
        this.pricingConfig = pricingConfig;
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

    public OperationPricing getOperationPricing() {
        return operationPricing;
    }

    public void setOperationPricing(OperationPricing operationPricing) {
        this.operationPricing = operationPricing;
    }

    public PricingConfig getPricingConfig() {
        return pricingConfig;
    }

    public void setPricingConfig(PricingConfig pricingConfig) {
        this.pricingConfig = pricingConfig;
    }

    public BigDecimal getCostMultiplier() {
        return costMultiplier;
    }

    public void setCostMultiplier(BigDecimal costMultiplier) {
        this.costMultiplier = costMultiplier;
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
