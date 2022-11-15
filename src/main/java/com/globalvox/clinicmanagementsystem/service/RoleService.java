package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.Role;

import java.util.List;

public interface RoleService {
    List<Role> findAll();

    Role findById(Long roleId);
}
