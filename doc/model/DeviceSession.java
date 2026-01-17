package com.aplusbinary.binarypixor.doc.model.am;

import java.io.Serializable;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "am_device_session")
public class DeviceSession implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_session_id", columnDefinition = "bigint", unique = true, nullable = false)
    private Long deviceSessionId;
    
    @Column(name = "device_id", columnDefinition = "varchar(255)", nullable = false)
    private String deviceId;
    
    @Column(name = "device_name", columnDefinition = "varchar(255)", nullable = true)
    private String deviceName;
    
    @Column(name = "device_type", columnDefinition = "varchar(100)", nullable = true)
    private String deviceType;
    
    @Column(name = "ip_address", columnDefinition = "varchar(45)", nullable = true)
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "text", nullable = true)
    private String userAgent;
    
    @Column(name = "session_token", columnDefinition = "varchar(500)", nullable = false, unique = true)
    private String sessionToken;
    
    @Column(name = "is_active", columnDefinition = "boolean default true", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "login_time", nullable = false)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date loginTime;
    
    @Column(name = "last_activity_time", nullable = false)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date lastActivityTime;
    
    @Column(name = "logout_time", nullable = true)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date logoutTime;
    
    @Column(name = "created_on", updatable = false)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date createdOn;
    
    @Column(name = "modified_on")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date modifiedOn;
    
    @Column(name = "account_id", nullable = false)
    private Long accountId;
    
    @Column(name = "end_user_id", nullable = true)
    private Long endUserId;
    
    public boolean isSessionValid() {
        return this.isActive && this.logoutTime == null;
    }
    
    public long getSessionDurationMinutes() {
        Date endTime = this.logoutTime != null ? this.logoutTime : new Date();
        return (endTime.getTime() - this.loginTime.getTime()) / (1000 * 60);
    }
    
    public boolean isEndUserSession() {
        return this.endUserId != null;
    }
    
    public boolean isAdminSession() {
        return this.endUserId == null;
    }
    
    public void updateLastActivity() {
        this.lastActivityTime = new Date();
        this.modifiedOn = new Date();
    }

    // Getters and Setters
    public Long getDeviceSessionId() { return deviceSessionId; }
    public void setDeviceSessionId(Long deviceSessionId) { this.deviceSessionId = deviceSessionId; }
    
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    
    public String getSessionToken() { return sessionToken; }
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Date getLoginTime() { return loginTime; }
    public void setLoginTime(Date loginTime) { this.loginTime = loginTime; }
    
    public Date getLastActivityTime() { return lastActivityTime; }
    public void setLastActivityTime(Date lastActivityTime) { this.lastActivityTime = lastActivityTime; }
    
    public Date getLogoutTime() { return logoutTime; }
    public void setLogoutTime(Date logoutTime) { this.logoutTime = logoutTime; }
    
    public Date getCreatedOn() { return createdOn; }
    public void setCreatedOn(Date createdOn) { this.createdOn = createdOn; }
    
    public Date getModifiedOn() { return modifiedOn; }
    public void setModifiedOn(Date modifiedOn) { this.modifiedOn = modifiedOn; }
    
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    
    public Long getEndUserId() { return endUserId; }
    public void setEndUserId(Long endUserId) { this.endUserId = endUserId; }
}

