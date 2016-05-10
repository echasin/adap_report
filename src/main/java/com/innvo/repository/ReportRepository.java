package com.innvo.repository;

import com.innvo.domain.Report;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Report entity.
 */
@SuppressWarnings("unused")
public interface ReportRepository extends JpaRepository<Report,Long> {

}
