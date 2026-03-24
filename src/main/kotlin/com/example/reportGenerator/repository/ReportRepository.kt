package com.example.reportGenerator.repository

import com.example.reportGenerator.entity.Report
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.time.LocalDateTime


@Repository
interface ReportRepository : JpaRepository<Report, Long> {
    fun findByCreatedAtBetween(from:LocalDateTime, to:LocalDateTime):List<Report>;
}
