package com.aplusbinary.binarypixor.doc.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Temporary invoice entity for storing invoice data during processing
 * before it's finalized and moved to the main Invoice table
 */
@Entity
@Table(name = "temp_invoices")
public class TempInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    // Vendor information stored as separate fields (not foreign key)
    @Column(name = "vendor_name", length = 500)
    private String vendorName;

    @Column(name = "vendor_address", columnDefinition = "TEXT")
    private String vendorAddress;

    @Column(name = "vendor_phone", length = 100)
    private String vendorPhone;

    @Column(name = "vendor_email", length = 250)
    private String vendorEmail;

    @Column(name = "vendor_tax_id", length = 100)
    private String vendorTaxId;

    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "currency", length = 50)
    private String currency;

    @OneToMany(mappedBy = "tempInvoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TempInvoiceItem> items = new ArrayList<>();

    @Column(name = "original_file_name", length = 500)
    private String originalFileName;

    @Column(name = "stored_file_path", length = 500)
    private String storedFilePath;

    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private ProcessingStatus status = ProcessingStatus.PENDING;

    @Column(name = "ingest_source", length = 50)
    @Enumerated(EnumType.STRING)
    private IngestSource ingestSource;

    @Column(name = "raw_text", columnDefinition = "TEXT")
    private String rawText;

    @Column(name = "validation_errors", columnDefinition = "TEXT")
    private String validationErrors;

    // OpenAI/NLP Request and Response for training purposes
    @Column(name = "nlp_request", columnDefinition = "TEXT")
    private String nlpRequest;

    @Column(name = "nlp_response", columnDefinition = "TEXT")
    private String nlpResponse;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "account_management_id")
    private String accountManagementId;

    @Column(name = "created_by", columnDefinition="BIGINT", nullable=false)
    private Long createdBy;
    
    @Column(name = "enduser_id", columnDefinition="BIGINT", nullable=true)
    private Long endUserId;
    
    @Column(name = "modified_by_name", columnDefinition="varchar(250)", nullable=false)
    private String modifiedByName;

    // Optional vendor ID if we want to link to existing vendor
    @Column(name = "vendor_id")
    private Long vendorId;

    // Session or batch identifier for grouping temp invoices
    @Column(name = "session_id", length = 100)
    private String sessionId;

    // Expiration timestamp for automatic cleanup
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // Flag to indicate if this temp invoice is ready for final processing
    @Column(name = "is_ready_for_processing")
    private Boolean isReadyForProcessing = false;

    // Constructors
    public TempInvoice() {
    }

    // Helper methods
    public void addItem(TempInvoiceItem item) {
        items.add(item);
        item.setTempInvoice(this);
    }

    public void removeItem(TempInvoiceItem item) {
        items.remove(item);
        item.setTempInvoice(null);
    }

    @PrePersist
    protected void onInsert() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        // Set default expiration to 24 hours from creation
        if (this.expiresAt == null) {
            this.expiresAt = now.plusHours(24);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorAddress() {
        return vendorAddress;
    }

    public void setVendorAddress(String vendorAddress) {
        this.vendorAddress = vendorAddress;
    }

    public String getVendorPhone() {
        return vendorPhone;
    }

    public void setVendorPhone(String vendorPhone) {
        this.vendorPhone = vendorPhone;
    }

    public String getVendorEmail() {
        return vendorEmail;
    }

    public void setVendorEmail(String vendorEmail) {
        this.vendorEmail = vendorEmail;
    }

    public String getVendorTaxId() {
        return vendorTaxId;
    }

    public void setVendorTaxId(String vendorTaxId) {
        this.vendorTaxId = vendorTaxId;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<TempInvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<TempInvoiceItem> items) {
        this.items = items;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getStoredFilePath() {
        return storedFilePath;
    }

    public void setStoredFilePath(String storedFilePath) {
        this.storedFilePath = storedFilePath;
    }

    public ProcessingStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessingStatus status) {
        this.status = status;
    }

    public IngestSource getIngestSource() {
        return ingestSource;
    }

    public void setIngestSource(IngestSource ingestSource) {
        this.ingestSource = ingestSource;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public String getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(String validationErrors) {
        this.validationErrors = validationErrors;
    }

    public String getNlpRequest() {
        return nlpRequest;
    }

    public void setNlpRequest(String nlpRequest) {
        this.nlpRequest = nlpRequest;
    }

    public String getNlpResponse() {
        return nlpResponse;
    }

    public void setNlpResponse(String nlpResponse) {
        this.nlpResponse = nlpResponse;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getAccountManagementId() {
        return accountManagementId;
    }

    public void setAccountManagementId(String accountManagementId) {
        this.accountManagementId = accountManagementId;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getEndUserId() {
        return endUserId;
    }

    public void setEndUserId(Long endUserId) {
        this.endUserId = endUserId;
    }

    public String getModifiedByName() {
        return modifiedByName;
    }

    public void setModifiedByName(String modifiedByName) {
        this.modifiedByName = modifiedByName;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getIsReadyForProcessing() {
        return isReadyForProcessing;
    }

    public void setIsReadyForProcessing(Boolean isReadyForProcessing) {
        this.isReadyForProcessing = isReadyForProcessing;
    }
}
