package com.duocuc.technician.service.impl;

import com.duocuc.technician.dto.SpecialtyRequest;
import com.duocuc.technician.dto.SpecialtyResponse;
import com.duocuc.technician.entity.Specialty;
import com.duocuc.technician.exception.BusinessRuleException;
import com.duocuc.technician.exception.DuplicateResourceException;
import com.duocuc.technician.exception.ResourceNotFoundException;
import com.duocuc.technician.repository.SpecialtyRepository;
import com.duocuc.technician.service.SpecialtyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialtyServiceImpl implements SpecialtyService {

    private static final Logger log = LoggerFactory.getLogger(SpecialtyServiceImpl.class);

    private final SpecialtyRepository specialtyRepository;

    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    public SpecialtyResponse save(SpecialtyRequest request) {
        // Business rule: the specialty name must be unique
        if (specialtyRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("A specialty already exists with name: " + request.name());
        }
        Specialty specialty = new Specialty();
        specialty.setName(request.name());
        specialty.setDescription(request.description());
        Specialty saved = specialtyRepository.save(specialty);
        log.info("Specialty created with id={} name={}", saved.getId(), saved.getName());
        return toResponse(saved);
    }

    @Override
    public List<SpecialtyResponse> findAll() {
        log.info("Listing all specialties");
        return specialtyRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public SpecialtyResponse findById(Long id) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + id));
        return toResponse(specialty);
    }

    @Override
    public SpecialtyResponse update(Long id, SpecialtyRequest request) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + id));

        if (Boolean.FALSE.equals(specialty.getActive())) {
            throw new BusinessRuleException("Cannot modify an inactive specialty (id: " + id + ")");
        }
        if (!specialty.getName().equalsIgnoreCase(request.name())
                && specialtyRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("A specialty already exists with name: " + request.name());
        }
        specialty.setName(request.name());
        specialty.setDescription(request.description());
        Specialty updated = specialtyRepository.save(specialty);
        log.info("Specialty updated id={}", updated.getId());
        return toResponse(updated);
    }

    @Override
    public void deactivate(Long id) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + id));
        specialty.setActive(false);
        specialtyRepository.save(specialty);
        log.info("Specialty deactivated id={}", id);
    }

    private SpecialtyResponse toResponse(Specialty s) {
        return new SpecialtyResponse(s.getId(), s.getName(), s.getDescription(), s.getActive());
    }
}
