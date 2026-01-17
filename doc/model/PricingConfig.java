package com.aplusbinary.binarypixor.doc.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "pricing_config", indexes = {
    @Index(name = "idx_pricing_code", columnList = "pricing_code"),
    @Index(name = "idx_service_type", columnList = "service_type"),
    @Index(name = "idx_active", columnList = "is_active, effective_from, effective_to")
})
public class PricingConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "pricing_code", nullable = false, unique = true, length = 50)
    private String pricingCode;

    @Column(name = "service_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Column(name = "pricing_model", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private PricingModel pricingModel;

    @Column(name = "unit_cost", nullable = false, precision = 10, scale = 6)
    private BigDecimal unitCost;

    @Column(name = "unit_name", nullable = false, length = 50)
    private String unitName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "provider", length = 50)
    private String provider;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "effective_from", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date effectiveFrom;

    @Column(name = "effective_to")
    @Temporal(TemporalType.TIMESTAMP)
    private Date effectiveTo;

    @Column(name = "created_on", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "modified_on", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedOn;

    // Constructors
    public PricingConfig() {
    }

    public PricingConfig(String pricingCode, ServiceType serviceType, String serviceName, 
                        PricingModel pricingModel, BigDecimal unitCost, String unitName) {
        this.pricingCode = pricingCode;
        this.serviceType = serviceType;
        this.serviceName = serviceName;
        this.pricingModel = pricingModel;
        this.unitCost = unitCost;
        this.unitName = unitName;
    }

    @PrePersist
    protected void onInsert() {
        this.modifiedOn = new Date();
        this.createdOn = new Date();
        if (this.effectiveFrom == null) {
            this.effectiveFrom = new Date();
        }
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

    public String getPricingCode() {
        return pricingCode;
    }

    public void setPricingCode(String pricingCode) {
        this.pricingCode = pricingCode;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public PricingModel getPricingModel() {
        return pricingModel;
    }

    public void setPricingModel(PricingModel pricingModel) {
        this.pricingModel = pricingModel;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Date getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(Date effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public Date getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(Date effectiveTo) {
        this.effectiveTo = effectiveTo;
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
