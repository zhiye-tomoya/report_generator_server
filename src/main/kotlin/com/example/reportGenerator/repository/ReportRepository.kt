package com.example.reportGenerator.repository

import com.example.reportGenerator.entity.Report
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ReportRepository : JpaRepository<Report, Long> {
}
