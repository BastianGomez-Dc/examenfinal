package com.duocuc.order.service.impl;

import com.duocuc.order.client.TechnicianClient;
import com.duocuc.order.service.TechnicianService;
import org.springframework.stereotype.Service;

// Wraps TechnicianClient: the rest of the service layer never depends on the HTTP client directly.
@Service
public class TechnicianServiceImpl implements TechnicianService {

    private final TechnicianClient technicianClient;

    public TechnicianServiceImpl(TechnicianClient technicianClient) {
        this.technicianClient = technicianClient;
    }

    @Override
    public boolean existsTechnician(Long technicianId) {
        return technicianClient.existsTechnician(technicianId);
    }
}
