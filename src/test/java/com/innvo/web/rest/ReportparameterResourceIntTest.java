package com.innvo.web.rest;

import com.innvo.AdapReportApp;
import com.innvo.domain.Reportparameter;
import com.innvo.repository.ReportparameterRepository;
import com.innvo.repository.search.ReportparameterSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ReportparameterResource REST controller.
 *
 * @see ReportparameterResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapReportApp.class)
@WebAppConfiguration
@IntegrationTest
public class ReportparameterResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_LABEL = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_LASTMODIFIEDBY = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_LASTMODIFIEDBY = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_LASTMODIFIEDDATETIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_LASTMODIFIEDDATETIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_LASTMODIFIEDDATETIME_STR = dateTimeFormatter.format(DEFAULT_LASTMODIFIEDDATETIME);

    @Inject
    private ReportparameterRepository reportparameterRepository;

    @Inject
    private ReportparameterSearchRepository reportparameterSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restReportparameterMockMvc;

    private Reportparameter reportparameter;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReportparameterResource reportparameterResource = new ReportparameterResource();
        ReflectionTestUtils.setField(reportparameterResource, "reportparameterSearchRepository", reportparameterSearchRepository);
        ReflectionTestUtils.setField(reportparameterResource, "reportparameterRepository", reportparameterRepository);
        this.restReportparameterMockMvc = MockMvcBuilders.standaloneSetup(reportparameterResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        reportparameterSearchRepository.deleteAll();
        reportparameter = new Reportparameter();
        reportparameter.setLabel(DEFAULT_LABEL);
        reportparameter.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        reportparameter.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
    }

    @Test
    @Transactional
    public void createReportparameter() throws Exception {
        int databaseSizeBeforeCreate = reportparameterRepository.findAll().size();

        // Create the Reportparameter

        restReportparameterMockMvc.perform(post("/api/reportparameters")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(reportparameter)))
                .andExpect(status().isCreated());

        // Validate the Reportparameter in the database
        List<Reportparameter> reportparameters = reportparameterRepository.findAll();
        assertThat(reportparameters).hasSize(databaseSizeBeforeCreate + 1);
        Reportparameter testReportparameter = reportparameters.get(reportparameters.size() - 1);
        assertThat(testReportparameter.getLabel()).isEqualTo(DEFAULT_LABEL);
        assertThat(testReportparameter.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testReportparameter.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);

        // Validate the Reportparameter in ElasticSearch
        Reportparameter reportparameterEs = reportparameterSearchRepository.findOne(testReportparameter.getId());
        assertThat(reportparameterEs).isEqualToComparingFieldByField(testReportparameter);
    }

    @Test
    @Transactional
    public void checkLabelIsRequired() throws Exception {
        int databaseSizeBeforeTest = reportparameterRepository.findAll().size();
        // set the field null
        reportparameter.setLabel(null);

        // Create the Reportparameter, which fails.

        restReportparameterMockMvc.perform(post("/api/reportparameters")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(reportparameter)))
                .andExpect(status().isBadRequest());

        List<Reportparameter> reportparameters = reportparameterRepository.findAll();
        assertThat(reportparameters).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = reportparameterRepository.findAll().size();
        // set the field null
        reportparameter.setLastmodifiedby(null);

        // Create the Reportparameter, which fails.

        restReportparameterMockMvc.perform(post("/api/reportparameters")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(reportparameter)))
                .andExpect(status().isBadRequest());

        List<Reportparameter> reportparameters = reportparameterRepository.findAll();
        assertThat(reportparameters).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = reportparameterRepository.findAll().size();
        // set the field null
        reportparameter.setLastmodifieddatetime(null);

        // Create the Reportparameter, which fails.

        restReportparameterMockMvc.perform(post("/api/reportparameters")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(reportparameter)))
                .andExpect(status().isBadRequest());

        List<Reportparameter> reportparameters = reportparameterRepository.findAll();
        assertThat(reportparameters).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllReportparameters() throws Exception {
        // Initialize the database
        reportparameterRepository.saveAndFlush(reportparameter);

        // Get all the reportparameters
        restReportparameterMockMvc.perform(get("/api/reportparameters?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(reportparameter.getId().intValue())))
                .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)));
    }

    @Test
    @Transactional
    public void getReportparameter() throws Exception {
        // Initialize the database
        reportparameterRepository.saveAndFlush(reportparameter);

        // Get the reportparameter
        restReportparameterMockMvc.perform(get("/api/reportparameters/{id}", reportparameter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(reportparameter.getId().intValue()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR));
    }

    @Test
    @Transactional
    public void getNonExistingReportparameter() throws Exception {
        // Get the reportparameter
        restReportparameterMockMvc.perform(get("/api/reportparameters/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateReportparameter() throws Exception {
        // Initialize the database
        reportparameterRepository.saveAndFlush(reportparameter);
        reportparameterSearchRepository.save(reportparameter);
        int databaseSizeBeforeUpdate = reportparameterRepository.findAll().size();

        // Update the reportparameter
        Reportparameter updatedReportparameter = new Reportparameter();
        updatedReportparameter.setId(reportparameter.getId());
        updatedReportparameter.setLabel(UPDATED_LABEL);
        updatedReportparameter.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedReportparameter.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);

        restReportparameterMockMvc.perform(put("/api/reportparameters")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedReportparameter)))
                .andExpect(status().isOk());

        // Validate the Reportparameter in the database
        List<Reportparameter> reportparameters = reportparameterRepository.findAll();
        assertThat(reportparameters).hasSize(databaseSizeBeforeUpdate);
        Reportparameter testReportparameter = reportparameters.get(reportparameters.size() - 1);
        assertThat(testReportparameter.getLabel()).isEqualTo(UPDATED_LABEL);
        assertThat(testReportparameter.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testReportparameter.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);

        // Validate the Reportparameter in ElasticSearch
        Reportparameter reportparameterEs = reportparameterSearchRepository.findOne(testReportparameter.getId());
        assertThat(reportparameterEs).isEqualToComparingFieldByField(testReportparameter);
    }

    @Test
    @Transactional
    public void deleteReportparameter() throws Exception {
        // Initialize the database
        reportparameterRepository.saveAndFlush(reportparameter);
        reportparameterSearchRepository.save(reportparameter);
        int databaseSizeBeforeDelete = reportparameterRepository.findAll().size();

        // Get the reportparameter
        restReportparameterMockMvc.perform(delete("/api/reportparameters/{id}", reportparameter.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean reportparameterExistsInEs = reportparameterSearchRepository.exists(reportparameter.getId());
        assertThat(reportparameterExistsInEs).isFalse();

        // Validate the database is empty
        List<Reportparameter> reportparameters = reportparameterRepository.findAll();
        assertThat(reportparameters).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchReportparameter() throws Exception {
        // Initialize the database
        reportparameterRepository.saveAndFlush(reportparameter);
        reportparameterSearchRepository.save(reportparameter);

        // Search the reportparameter
        restReportparameterMockMvc.perform(get("/api/_search/reportparameters?query=id:" + reportparameter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reportparameter.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)));
    }
}
