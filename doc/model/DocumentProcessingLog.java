package com.aplusbinary.binarypixor.doc.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "document_processing_log", indexes = {
    @Index(name = "idx_processing_id", columnList = "processing_id"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_status", columnList = "processing_status"),
    @Index(name = "idx_created_on", columnList = "created_on"),
    @Index(name = "idx_reservation_id", columnList = "reservation_id"),
    @Index(name = "idx_transaction_id", columnList = "transaction_id"),
    @Index(name = "idx_operation_pricing_id", columnList = "operation_pricing_id")
})
public class DocumentProcessingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "processing_id", nullable = false, unique = true, length = 100)
    private String processingId;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "document_id", length = 100)
    private String documentId;

    @Column(name = "reservation_id", length = 100)
    private String reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_pricing_id")
    private OperationPricing operationPricing;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_size_kb")
    private Integer fileSizeKb;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "processing_status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ProcessingStatusType processingStatus;

    // User charges
    @Column(name = "credits_required", precision = 10, scale = 4)
    private BigDecimal creditsRequired;

    @Column(name = "credits_charged", precision = 10, scale = 4)
    private BigDecimal creditsCharged;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    // Actual costs breakdown - GPT
    @Column(name = "gpt_input_tokens")
    private Integer gptInputTokens;

    @Column(name = "gpt_output_tokens")
    private Integer gptOutputTokens;

    @Column(name = "gpt_input_cost", precision = 10, scale = 6)
    private BigDecimal gptInputCost;

    @Column(name = "gpt_output_cost", precision = 10, scale = 6)
    private BigDecimal gptOutputCost;

    @Column(name = "gpt_total_cost", precision = 10, scale = 6)
    private BigDecimal gptTotalCost;

    // Actual costs breakdown - S3
    @Column(name = "s3_storage_size_mb", precision = 10, scale = 4)
    private BigDecimal s3StorageSizeMb;

    @Column(name = "s3_storage_cost", precision = 10, scale = 6)
    private BigDecimal s3StorageCost;

    @Column(name = "s3_transfer_cost", precision = 10, scale = 6)
    private BigDecimal s3TransferCost;

    @Column(name = "s3_total_cost", precision = 10, scale = 6)
    private BigDecimal s3TotalCost;

    // Actual costs breakdown - OCR
    @Column(name = "ocr_pages")
    private Integer ocrPages;

    @Column(name = "ocr_cost", precision = 10, scale = 6)
    private BigDecimal ocrCost;

    // Total actual cost and profit
    @Column(name = "total_actual_cost", precision = 10, scale = 6)
    private BigDecimal totalActualCost;

    @Column(name = "profit_margin", precision = 10, scale = 6)
    private BigDecimal profitMargin;

    // Processing details
    @Column(name = "gpt_api_called")
    private Boolean gptApiCalled = false;

    @Column(name = "s3_stored")
    private Boolean s3Stored = false;

    @Column(name = "s3_url", columnDefinition = "TEXT")
    private String s3Url;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    // Timestamps
    @Column(name = "reserved_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reservedAt;

    @Column(name = "processing_started_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date processingStartedAt;

    @Column(name = "processing_completed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date processingCompletedAt;

    @Column(name = "created_on", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "modified_on", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedOn;

    // Constructors
    public DocumentProcessingLog() {
    }

    public DocumentProcessingLog(String processingId, String userId, String fileName, ProcessingStatusType processingStatus) {
        this.processingId = processingId;
        this.userId = userId;
        this.fileName = fileName;
        this.processingStatus = processingStatus;
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
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProcessingId() {
        return processingId;
    }

    public void setProcessingId(String processingId) {
        this.processingId = processingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public OperationPricing getOperationPricing() {
        return operationPricing;
    }

    public void setOperationPricing(OperationPricing operationPricing) {
        this.operationPricing = operationPricing;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getFileSizeKb() {
        return fileSizeKb;
    }

    public void setFileSizeKb(Integer fileSizeKb) {
        this.fileSizeKb = fileSizeKb;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public ProcessingStatusType getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(ProcessingStatusType processingStatus) {
        this.processingStatus = processingStatus;
    }

    public BigDecimal getCreditsRequired() {
        return creditsRequired;
    }

    public void setCreditsRequired(BigDecimal creditsRequired) {
        this.creditsRequired = creditsRequired;
    }

    public BigDecimal getCreditsCharged() {
        return creditsCharged;
    }

    public void setCreditsCharged(BigDecimal creditsCharged) {
        this.creditsCharged = creditsCharged;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getGptInputTokens() {
        return gptInputTokens;
    }

    public void setGptInputTokens(Integer gptInputTokens) {
        this.gptInputTokens = gptInputTokens;
    }

    public Integer getGptOutputTokens() {
        return gptOutputTokens;
    }

    public void setGptOutputTokens(Integer gptOutputTokens) {
        this.gptOutputTokens = gptOutputTokens;
    }

    public BigDecimal getGptInputCost() {
        return gptInputCost;
    }

    public void setGptInputCost(BigDecimal gptInputCost) {
        this.gptInputCost = gptInputCost;
    }

    public BigDecimal getGptOutputCost() {
        return gptOutputCost;
    }

    public void setGptOutputCost(BigDecimal gptOutputCost) {
        this.gptOutputCost = gptOutputCost;
    }

    public BigDecimal getGptTotalCost() {
        return gptTotalCost;
    }

    public void setGptTotalCost(BigDecimal gptTotalCost) {
        this.gptTotalCost = gptTotalCost;
    }

    public BigDecimal getS3StorageSizeMb() {
        return s3StorageSizeMb;
    }

    public void setS3StorageSizeMb(BigDecimal s3StorageSizeMb) {
        this.s3StorageSizeMb = s3StorageSizeMb;
    }

    public BigDecimal getS3StorageCost() {
        return s3StorageCost;
    }

    public void setS3StorageCost(BigDecimal s3StorageCost) {
        this.s3StorageCost = s3StorageCost;
    }

    public BigDecimal getS3TransferCost() {
        return s3TransferCost;
    }

    public void setS3TransferCost(BigDecimal s3TransferCost) {
        this.s3TransferCost = s3TransferCost;
    }

    public BigDecimal getS3TotalCost() {
        return s3TotalCost;
    }

    public void setS3TotalCost(BigDecimal s3TotalCost) {
        this.s3TotalCost = s3TotalCost;
    }

    public Integer getOcrPages() {
        return ocrPages;
    }

    public void setOcrPages(Integer ocrPages) {
        this.ocrPages = ocrPages;
    }

    public BigDecimal getOcrCost() {
        return ocrCost;
    }

    public void setOcrCost(BigDecimal ocrCost) {
        this.ocrCost = ocrCost;
    }

    public BigDecimal getTotalActualCost() {
        return totalActualCost;
    }

    public void setTotalActualCost(BigDecimal totalActualCost) {
        this.totalActualCost = totalActualCost;
    }

    public BigDecimal getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(BigDecimal profitMargin) {
        this.profitMargin = profitMargin;
    }

    public Boolean getGptApiCalled() {
        return gptApiCalled;
    }

    public void setGptApiCalled(Boolean gptApiCalled) {
        this.gptApiCalled = gptApiCalled;
    }

    public Boolean getS3Stored() {
        return s3Stored;
    }

    public void setS3Stored(Boolean s3Stored) {
        this.s3Stored = s3Stored;
    }

    public String getS3Url() {
        return s3Url;
    }

    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(Date reservedAt) {
        this.reservedAt = reservedAt;
    }

    public Date getProcessingStartedAt() {
        return processingStartedAt;
    }

    public void setProcessingStartedAt(Date processingStartedAt) {
        this.processingStartedAt = processingStartedAt;
    }

    public Date getProcessingCompletedAt() {
        return processingCompletedAt;
    }

    public void setProcessingCompletedAt(Date processingCompletedAt) {
        this.processingCompletedAt = processingCompletedAt;
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
