package com.globalvox.clinicmanagementsystem.service;
import com.globalvox.clinicmanagementsystem.exception.NotFoundException;
import com.globalvox.clinicmanagementsystem.entity.Role;
import com.globalvox.clinicmanagementsystem.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional
    public Role findById(Long roleId) {
        Optional<Role> result = roleRepository.findById(roleId);
        Role role = null;
        if (result.isPresent()) {
            role = result.get();
        } else {
            throw new NotFoundException("Role Not Found !!");
        }
        return role;
    }
}
