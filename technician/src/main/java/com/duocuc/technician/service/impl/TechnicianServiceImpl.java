package com.duocuc.technician.service.impl;

import com.duocuc.technician.dto.TechnicianRequest;
import com.duocuc.technician.dto.TechnicianResponse;
import com.duocuc.technician.entity.Specialty;
import com.duocuc.technician.entity.Technician;
import com.duocuc.technician.exception.BusinessRuleException;
import com.duocuc.technician.exception.DuplicateResourceException;
import com.duocuc.technician.exception.ResourceNotFoundException;
import com.duocuc.technician.repository.SpecialtyRepository;
import com.duocuc.technician.repository.TechnicianRepository;
import com.duocuc.technician.service.TechnicianService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TechnicianServiceImpl implements TechnicianService {

    private static final Logger log = LoggerFactory.getLogger(TechnicianServiceImpl.class);

    private final TechnicianRepository technicianRepository;
    private final SpecialtyRepository specialtyRepository;

    public TechnicianServiceImpl(TechnicianRepository technicianRepository, SpecialtyRepository specialtyRepository) {
        this.technicianRepository = technicianRepository;
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    public TechnicianResponse save(TechnicianRequest request) {
        // Business rule: the email must be unique
        if (technicianRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("A technician already exists with email: " + request.email());
        }
        // Business rule: the specialty must exist
        Specialty specialty = specialtyRepository.findById(request.specialtyId())
                .orElseThrow(() -> new BusinessRuleException("The specified specialty does not exist: " + request.specialtyId()));

        Technician technician = new Technician();
        technician.setName(request.name());
        technician.setEmail(request.email());
        technician.setPhone(request.phone());
        technician.setSpecialty(specialty);
        Technician saved = technicianRepository.save(technician);
        log.info("Technician created with id={} email={}", saved.getId(), saved.getEmail());
        return toResponse(saved);
    }

    @Override
    public List<TechnicianResponse> findAll() {
        log.info("Listing all technicians");
        return technicianRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public TechnicianResponse findById(Long id) {
        Technician technician = technicianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found with id: " + id));
        return toResponse(technician);
    }

    @Override
    public TechnicianResponse update(Long id, TechnicianRequest request) {
        Technician technician = technicianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found with id: " + id));

        // Business rule: an inactive technician cannot be modified
        if (Boolean.FALSE.equals(technician.getActive())) {
            throw new BusinessRuleException("Cannot modify an inactive technician (id: " + id + ")");
        }
        if (!technician.getEmail().equals(request.email()) && technicianRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("A technician already exists with email: " + request.email());
        }
        Specialty specialty = specialtyRepository.findById(request.specialtyId())
                .orElseThrow(() -> new BusinessRuleException("The specified specialty does not exist: " + request.specialtyId()));

        technician.setName(request.name());
        technician.setEmail(request.email());
        technician.setPhone(request.phone());
        technician.setSpecialty(specialty);
        Technician updated = technicianRepository.save(technician);
        log.info("Technician updated id={}", updated.getId());
        return toResponse(updated);
    }

    @Override
    public void deactivate(Long id) {
        Technician technician = technicianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found with id: " + id));
        // Business rule: deletion is logical. Kept simple on purpose: no remote check
        // against active work orders before deactivating (see project notes).
        technician.setActive(false);
        technicianRepository.save(technician);
        log.info("Technician deactivated id={}", id);
    }

    private TechnicianResponse toResponse(Technician t) {
        return new TechnicianResponse(
                t.getId(), t.getName(), t.getEmail(), t.getPhone(),
                t.getSpecialty().getId(), t.getSpecialty().getName(),
                t.getActive(), t.getHireDate()
        );
    }
}
