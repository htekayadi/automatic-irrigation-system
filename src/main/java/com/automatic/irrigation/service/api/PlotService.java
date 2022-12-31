package com.automatic.irrigation.service.api;

import com.automatic.irrigation.dto.PlotDTO;

import java.util.List;

public interface PlotService {

    PlotDTO addPlot(PlotDTO plotDTO);

    PlotDTO getPlot(String id);

    PlotDTO updatePlot(String id, PlotDTO plotDTO);

    List<PlotDTO> getAllPlots();
}
