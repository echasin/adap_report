package com.innvo.repository;

import com.innvo.domain.Reportparameter;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Reportparameter entity.
 */
@SuppressWarnings("unused")
public interface ReportparameterRepository extends JpaRepository<Reportparameter,Long> {

}
