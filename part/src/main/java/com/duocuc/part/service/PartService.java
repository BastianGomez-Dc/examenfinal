package com.duocuc.part.service;

import com.duocuc.part.dto.PartRequest;
import com.duocuc.part.dto.PartResponse;
import com.duocuc.part.dto.StockAdjustmentRequest;

import java.util.List;

public interface PartService {

    PartResponse save(PartRequest request);

    List<PartResponse> findAll();

    PartResponse findById(Long id);

    PartResponse update(Long id, PartRequest request);

    PartResponse subtractStock(Long id, StockAdjustmentRequest request);

    void deactivate(Long id);
}
