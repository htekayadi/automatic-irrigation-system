package com.automatic.irrigation.controller;

import com.automatic.irrigation.constants.RequestURI;
import com.automatic.irrigation.dto.TimeslotDTO;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@DirtiesContext
public class TimeslotControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ModelMapper modelMapper;

    @Value("${local.server.port}")
    private int port;

    private String timeslotApiUrl;

    @BeforeEach
    public void setup() {
        restTemplate
                .getRestTemplate()
                .setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        timeslotApiUrl = "http://localhost:" + port + RequestURI.API + RequestURI.V1 + RequestURI.TIMESLOTS;
    }

    @Test
    @Sql(scripts = "classpath:PopulateDB.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:CleanupDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getSlot() {

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        TimeslotDTO timeslotDTO = createTimeslotDTO();
        timeslotDTO.setPlotId("6b98eb88-b05b-4ff5-8f77-ec8f0a4e4d70");

        // Add a Slot
        ResponseEntity<Object> result = restTemplate.postForEntity(timeslotApiUrl, timeslotDTO, Object.class);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());

        TimeslotDTO timeslot = modelMapper.map(result.getBody(), TimeslotDTO.class);

        // Get Slot by Id
        ResponseEntity<Object> response = restTemplate.getForEntity(timeslotApiUrl + "/" + timeslot.getId(), Object.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        TimeslotDTO slotResponse = modelMapper.map(response.getBody(), TimeslotDTO.class);
        assertEquals(timeslot, slotResponse);
    }

    @Test
    public void getTimeslotNotFound() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        ResponseEntity<Object> response = restTemplate.getForEntity(timeslotApiUrl + "/" + "0dd4e592-cae3-481e-aa80-059dddf0590d", Object.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Sql(scripts = "classpath:PopulateDB.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:CleanupDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateSlot() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        TimeslotDTO timeslotDTO = createTimeslotDTO();
        timeslotDTO.setPlotId("6b98eb88-b05b-4ff5-8f77-ec8f0a4e4d70");
        timeslotDTO.setAmountOfWater(10L);

        // Add a Slot
        ResponseEntity<Object> result = restTemplate.postForEntity(timeslotApiUrl, timeslotDTO, Object.class);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());

        TimeslotDTO timeslot = modelMapper.map(result.getBody(), TimeslotDTO.class);

        // Update slot
        timeslot.setAmountOfWater(20L);
        HttpEntity<TimeslotDTO> requestEntity = new HttpEntity<>(timeslot);
        ResponseEntity<TimeslotDTO> updateResponse = restTemplate.exchange(
                timeslotApiUrl + "/" + timeslot.getId(),
                HttpMethod.PUT,
                requestEntity,
                TimeslotDTO.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals(timeslot.getName(), Objects.requireNonNull(updateResponse.getBody()).getName());

        // Get Slot by Id
        ResponseEntity<Object> response = restTemplate.getForEntity(timeslotApiUrl + "/" + timeslot.getId(), Object.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        TimeslotDTO slotResponse = modelMapper.map(response.getBody(), TimeslotDTO.class);
        assertEquals(timeslot, slotResponse);
    }

    @Test
    @Sql(scripts = "classpath:PopulateDB.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:CleanupDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAllSlot() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        TimeslotDTO timeslotDTO = createTimeslotDTO();
        timeslotDTO.setPlotId("6b98eb88-b05b-4ff5-8f77-ec8f0a4e4d70");

        // Add a Slot
        timeslotDTO.setName("Slot 01");
        ResponseEntity<Object> result = restTemplate.postForEntity(timeslotApiUrl, timeslotDTO, Object.class);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());

        // Add a Slot
        timeslotDTO.setName("Slot 02");
        result = restTemplate.postForEntity(timeslotApiUrl, timeslotDTO, Object.class);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());

        // Get all Plots
        ResponseEntity<List<TimeslotDTO>> response = restTemplate.exchange(
                timeslotApiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        List<TimeslotDTO> slots = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, slots.size());
    }

    private TimeslotDTO createTimeslotDTO() {
        TimeslotDTO slotDTO = new TimeslotDTO();
        slotDTO.setName("Slot 01");
        slotDTO.setAmountOfWater(10L);
        slotDTO.setStartTime(Instant.now());
        slotDTO.setEndTime(Instant.now().plus(1, ChronoUnit.HOURS));
        return slotDTO;
    }
}
