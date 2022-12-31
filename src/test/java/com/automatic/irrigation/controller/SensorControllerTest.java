package com.automatic.irrigation.controller;

import com.automatic.irrigation.constants.RequestURI;
import com.automatic.irrigation.dto.SensorDTO;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class SensorControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${local.server.port}")
    private int port;

    private String sensorApiUrl;

    @BeforeEach
    public void setup() {
        restTemplate
                .getRestTemplate()
                .setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        sensorApiUrl = "http://localhost:" + port + RequestURI.API + RequestURI.V1 + RequestURI.SENSORS;
    }

    @Test
    @Sql(scripts = "classpath:PopulateDB.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:CleanupDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getSensor() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        SensorDTO sensorDTO = createSensorDTO();

        // Add a Sensor
        ResponseEntity<Object> result = restTemplate.postForEntity(sensorApiUrl, sensorDTO, Object.class);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());

        SensorDTO sensorCreated = modelMapper.map(result.getBody(), SensorDTO.class);

        // Get Sensor by Id
        ResponseEntity<Object> response = restTemplate.getForEntity(sensorApiUrl + "/" + sensorCreated.getId(), Object.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SensorDTO sensorResponse = modelMapper.map(response.getBody(), SensorDTO.class);
        assertEquals(sensorCreated, sensorResponse);
    }

    @Test
    public void getSensorNotFound() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        ResponseEntity<Object> response = restTemplate.getForEntity(sensorApiUrl + "/" + "0dd4e592-cae3-481e-aa80-059dddf0590d", Object.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Sql(scripts = "classpath:PopulateDB.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:CleanupDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testUpdateSensor() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        SensorDTO sensorDTO = createSensorDTO();

        // Add a Sensor
        ResponseEntity<Object> result = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/sensors", sensorDTO, Object.class);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());

        SensorDTO sensorCreated = modelMapper.map(result.getBody(), SensorDTO.class);

        // Update sensor
        sensorCreated.setName("Sensor 11");
        HttpEntity<SensorDTO> requestEntity = new HttpEntity<>(sensorCreated);
        ResponseEntity<SensorDTO> updateResponse = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/sensors/" + sensorCreated.getId(), HttpMethod.PUT, requestEntity, SensorDTO.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals(sensorCreated.getName(), Objects.requireNonNull(updateResponse.getBody()).getName());

        // Get Sensor by Id
        ResponseEntity<Object> response = restTemplate.getForEntity("http://localhost:" + port + "/api/v1/sensors/" + sensorCreated.getId(), Object.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SensorDTO sensorResponse = modelMapper.map(response.getBody(), SensorDTO.class);
        assertEquals(sensorCreated, sensorResponse);
    }


    @Test
    @Sql(scripts = "classpath:PopulateDB.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:CleanupDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetAllSensor() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        SensorDTO sensorDTO = createSensorDTO();

        // Add a Sensor
        ResponseEntity<Object> result = restTemplate.postForEntity(sensorApiUrl, sensorDTO, Object.class);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());

        // Add a Sensor
        sensorDTO.setName("Sensor 02");
        result = restTemplate.postForEntity(sensorApiUrl, sensorDTO, Object.class);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());

        // Get all Plots
        ResponseEntity<List<SensorDTO>> response = restTemplate.exchange(
                sensorApiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<SensorDTO> sensors = response.getBody();
        assertEquals(2, sensors.size());
    }

    private SensorDTO createSensorDTO() {
        SensorDTO sensorDTO = new SensorDTO();
        sensorDTO.setName("Sensor 01");
        sensorDTO.setPlotId("6b98eb88-b05b-4ff5-8f77-ec8f0a4e4d70");
        return sensorDTO;
    }
}
