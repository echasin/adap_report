package com.innvo.web.rest;

import com.innvo.AdapReportApp;
import com.innvo.domain.Report;
import com.innvo.repository.ReportRepository;
import com.innvo.repository.search.ReportSearchRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ReportResource REST controller.
 *
 * @see ReportResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdapReportApp.class)
@WebAppConfiguration
@IntegrationTest
public class ReportResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_REPORTTEMPLATENAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_REPORTTEMPLATENAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    @Inject
    private ReportRepository reportRepository;

    @Inject
    private ReportSearchRepository reportSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restReportMockMvc;

    private Report report;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReportResource reportResource = new ReportResource();
        ReflectionTestUtils.setField(reportResource, "reportSearchRepository", reportSearchRepository);
        ReflectionTestUtils.setField(reportResource, "reportRepository", reportRepository);
        this.restReportMockMvc = MockMvcBuilders.standaloneSetup(reportResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        reportSearchRepository.deleteAll();
        report = new Report();
        report.setName(DEFAULT_NAME);
        report.setReporttemplatename(DEFAULT_REPORTTEMPLATENAME);
    }

    @Test
    @Transactional
    public void createReport() throws Exception {
        int databaseSizeBeforeCreate = reportRepository.findAll().size();

        // Create the Report

        restReportMockMvc.perform(post("/api/reports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(report)))
                .andExpect(status().isCreated());

        // Validate the Report in the database
        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeCreate + 1);
        Report testReport = reports.get(reports.size() - 1);
        assertThat(testReport.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testReport.getReporttemplatename()).isEqualTo(DEFAULT_REPORTTEMPLATENAME);

        // Validate the Report in ElasticSearch
        Report reportEs = reportSearchRepository.findOne(testReport.getId());
        assertThat(reportEs).isEqualToComparingFieldByField(testReport);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = reportRepository.findAll().size();
        // set the field null
        report.setName(null);

        // Create the Report, which fails.

        restReportMockMvc.perform(post("/api/reports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(report)))
                .andExpect(status().isBadRequest());

        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkReporttemplatenameIsRequired() throws Exception {
        int databaseSizeBeforeTest = reportRepository.findAll().size();
        // set the field null
        report.setReporttemplatename(null);

        // Create the Report, which fails.

        restReportMockMvc.perform(post("/api/reports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(report)))
                .andExpect(status().isBadRequest());

        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllReports() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reports
        restReportMockMvc.perform(get("/api/reports?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(report.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].reporttemplatename").value(hasItem(DEFAULT_REPORTTEMPLATENAME.toString())));
    }

    @Test
    @Transactional
    public void getReport() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get the report
        restReportMockMvc.perform(get("/api/reports/{id}", report.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(report.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.reporttemplatename").value(DEFAULT_REPORTTEMPLATENAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingReport() throws Exception {
        // Get the report
        restReportMockMvc.perform(get("/api/reports/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateReport() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);
        reportSearchRepository.save(report);
        int databaseSizeBeforeUpdate = reportRepository.findAll().size();

        // Update the report
        Report updatedReport = new Report();
        updatedReport.setId(report.getId());
        updatedReport.setName(UPDATED_NAME);
        updatedReport.setReporttemplatename(UPDATED_REPORTTEMPLATENAME);

        restReportMockMvc.perform(put("/api/reports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedReport)))
                .andExpect(status().isOk());

        // Validate the Report in the database
        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeUpdate);
        Report testReport = reports.get(reports.size() - 1);
        assertThat(testReport.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testReport.getReporttemplatename()).isEqualTo(UPDATED_REPORTTEMPLATENAME);

        // Validate the Report in ElasticSearch
        Report reportEs = reportSearchRepository.findOne(testReport.getId());
        assertThat(reportEs).isEqualToComparingFieldByField(testReport);
    }

    @Test
    @Transactional
    public void deleteReport() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);
        reportSearchRepository.save(report);
        int databaseSizeBeforeDelete = reportRepository.findAll().size();

        // Get the report
        restReportMockMvc.perform(delete("/api/reports/{id}", report.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean reportExistsInEs = reportSearchRepository.exists(report.getId());
        assertThat(reportExistsInEs).isFalse();

        // Validate the database is empty
        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchReport() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);
        reportSearchRepository.save(report);

        // Search the report
        restReportMockMvc.perform(get("/api/_search/reports?query=id:" + report.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(report.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].reporttemplatename").value(hasItem(DEFAULT_REPORTTEMPLATENAME.toString())));
    }
}
