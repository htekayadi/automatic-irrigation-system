package com.automatic.irrigation.controller;

import com.automatic.irrigation.constants.RequestURI;
import com.automatic.irrigation.dto.PlotDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class PlotControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ModelMapper modelMapper;

    @Value("${local.server.port}")
    private int port;

    private String plotApiUrl;

    @BeforeEach
    public void setup() {
        restTemplate
                .getRestTemplate()
                .setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        plotApiUrl = "http://localhost:" + port + RequestURI.API + RequestURI.V1 + RequestURI.PLOTS;
    }

    @Test
    public void getPlot() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        // Add a Plot
        PlotDTO plotDTO = createPlotDTO("Plot 01");
        ResponseEntity<Object> result = restTemplate.postForEntity(plotApiUrl, plotDTO, Object.class);
        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());

        PlotDTO plotCreated = modelMapper.map(result.getBody(), PlotDTO.class);

        // Get Plot by Id
        ResponseEntity<Object> response = restTemplate.getForEntity(plotApiUrl + "/" + plotCreated.getId(), Object.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        PlotDTO plotResponse = modelMapper.map(response.getBody(), PlotDTO.class);
        Assertions.assertEquals(plotCreated, plotResponse);
    }

    @Test
    public void getPlotNotFound() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        ResponseEntity<Object> response = restTemplate.getForEntity(plotApiUrl + "/" + "0dd4e592-cae3-481e-aa80-059dddf0590d", Object.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void updatePlot() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        // Add a Plot
        PlotDTO plotDTO = createPlotDTO("Plot 01");
        ResponseEntity<Object> result = restTemplate.postForEntity(plotApiUrl, plotDTO, Object.class);
        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());

        PlotDTO plotCreated = modelMapper.map(result.getBody(), PlotDTO.class);

        //update the plot
        plotCreated.setName("Plot 02");
        HttpEntity<PlotDTO> requestEntity = new HttpEntity<>(plotCreated);
        ResponseEntity<PlotDTO> updateResponse = restTemplate.exchange(
                plotApiUrl + "/" + plotCreated.getId(), HttpMethod.PUT, requestEntity, PlotDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        Assertions.assertEquals(plotCreated.getName(), updateResponse.getBody().getName());

        // Get Plot by Id
        ResponseEntity<Object> response = restTemplate.getForEntity(plotApiUrl + "/" + plotCreated.getId(), Object.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        PlotDTO plotResponse = modelMapper.map(response.getBody(), PlotDTO.class);
        Assertions.assertEquals(plotCreated, plotResponse);
    }

    @Test
    public void getAllPlots() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        // Add a Plot
        PlotDTO plot1 = createPlotDTO("Plot 01");
        ResponseEntity<Object> result = restTemplate.postForEntity(plotApiUrl, plot1, Object.class);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());

        // Add a Plot
        PlotDTO plot2 = createPlotDTO("Plot 02");
        result = restTemplate.postForEntity(plotApiUrl, plot2, Object.class);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());

        // Get all Plots
        ResponseEntity<List<PlotDTO>> response = restTemplate.exchange(
                plotApiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        List<PlotDTO> plots = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, plots.size());
    }

    private PlotDTO createPlotDTO(String name) {
        PlotDTO plotDTO = new PlotDTO();
        plotDTO.setName(name);
        return plotDTO;
    }
}
