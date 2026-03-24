package com.example.reportGenerator.controller

import com.example.reportGenerator.entity.Report
import com.example.reportGenerator.service.ReportService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class ReportControllerTest {

    private val reportService: ReportService = mock()
    private val reportController = ReportController(reportService)

    @Test
    fun `should return all reports with status 200`() {
        // Given
        val now = LocalDateTime.now()
        val reports = listOf(
            Report(
                id = 1L,
                title = "Report 1",
                notes = "Notes for report 1",
                createdAt = now,
                updatedAt = now
            ),
            Report(
                id = 2L,
                title = "Report 2",
                notes = null,
                createdAt = now,
                updatedAt = now
            )
        )

        whenever(reportService.getAllReports()).thenReturn(reports)

        // When
        val response = reportController.getAllReports()

        // Then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(reports)
        assertThat(response.body).hasSize(2)
        verify(reportService).getAllReports()
    }

    @Test
    fun `should return empty list with status 200 when no reports exist`() {
        // Given
        whenever(reportService.getAllReports()).thenReturn(emptyList())

        // When
        val response = reportController.getAllReports()

        // Then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEmpty()
        verify(reportService).getAllReports()
    }

    @Test
    fun `should call service getAllReports method`() {
        // Given
        val now = LocalDateTime.now()
        val reports = listOf(
            Report(id = 1L, title = "Test Report", notes = null, createdAt = now, updatedAt = now)
        )
        whenever(reportService.getAllReports()).thenReturn(reports)

        // When
        reportController.getAllReports()

        // Then
        verify(reportService).getAllReports()
    }

}
