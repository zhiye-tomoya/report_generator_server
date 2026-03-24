package com.example.reportGenerator.controller

import com.example.reportGenerator.entity.Report
import com.example.reportGenerator.service.ReportService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reports")
class ReportController(
    private val reportService: ReportService
) {
    private val logger = LoggerFactory.getLogger(ReportController::class.java)

    @GetMapping
    fun getAllReports(): ResponseEntity<List<Report>> {
        logger.info("GET /api/reports - Get all reports request received")
        val reports = reportService.getAllReports()
        return ResponseEntity.ok(reports)
    }
}
