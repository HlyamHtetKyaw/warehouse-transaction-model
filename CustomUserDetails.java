package com.code.aplusbinary.warehouse.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final Long adminId;
    private final Long memberId;
    private final Long roleId;
    private final String url;
    private final String mainRole;
    private final int status;
 

    private final Collection<? extends GrantedAuthority> authorities;
    
    public CustomUserDetails(String username, String password, Long adminId, Long memberId, Long roleId, String url, String mainRole, Collection<? extends GrantedAuthority> authorities, int status) {
        this.username = username;
        this.password = password;
        this.adminId = adminId;
        this.memberId = memberId;
        this.roleId = roleId;
        this.url = url;
        this.mainRole = mainRole;
        this.authorities = authorities;
        this.status = status;
    }

    public CustomUserDetails(String username, String password, Long adminId, Long memberId, Long roleId, String url, String mainRole, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.adminId = adminId;
        this.memberId = memberId;
        this.roleId = roleId;
        this.url = url;
        this.mainRole = mainRole;
        this.authorities = authorities;
        this.status = 1;
    }

    public CustomUserDetails(String username, String password, Long adminId, Long memberId, Long roleId, String mainRole, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.adminId = adminId;
        this.memberId = memberId;
        this.roleId = roleId;
        this.url = "";
        this.mainRole = mainRole;
        this.authorities = authorities;
        this.status = 1;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    @Override
    public boolean isEnabled() {
    	return true;
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return username; }
    
    public Long getAdminId() { return adminId; }

    public Long getMemberId() { return memberId; }
    
    public int getStatus() {return status; }

    public Long getRoleId() { return roleId; }

    public String getUrl() { return url; }

    public String getMainRole() { return mainRole; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }
}
