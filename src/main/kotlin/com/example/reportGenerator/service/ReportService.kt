package com.example.reportGenerator.service

import com.example.reportGenerator.entity.Report
import com.example.reportGenerator.repository.ReportRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ReportService(
    private val reportRepository: ReportRepository
) {
    private val logger = LoggerFactory.getLogger(ReportService::class.java)

    fun getAllReports(): List<Report> {
        logger.info("Fetching all reports")
        val reports = reportRepository.findAll()
        logger.info("Found ${reports.size} reports")
        return reports
    }
}
