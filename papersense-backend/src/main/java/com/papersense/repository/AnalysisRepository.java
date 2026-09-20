package com.papersense.repository;

import com.papersense.model.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

    Optional<Analysis> findByPaperId(Long paperId);

    void deleteByPaperId(Long paperId);
}
