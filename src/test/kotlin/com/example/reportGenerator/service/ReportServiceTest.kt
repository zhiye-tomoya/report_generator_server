package com.example.reportGenerator.service

import com.example.reportGenerator.entity.Report
import com.example.reportGenerator.repository.ReportRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

class ReportServiceTest {

    private val reportRepository: ReportRepository = mock()
    private val reportService = ReportService(reportRepository)

    @Test
    fun `should return all reports from repository`() {
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
                notes = "Notes for report 2",
                createdAt = now,
                updatedAt = now
            )
        )

        whenever(reportRepository.findAll()).thenReturn(reports)

        // When
        val result = reportService.getAllReports()

        // Then
        assertThat(result).hasSize(2)
        assertThat(result).isEqualTo(reports)
        verify(reportRepository).findAll()
    }

    @Test
    fun `should return empty list when no reports exist`() {
        // Given
        whenever(reportRepository.findAll()).thenReturn(emptyList())

        // When
        val result = reportService.getAllReports()

        // Then
        assertThat(result).isEmpty()
        verify(reportRepository).findAll()
    }

    @Test
    fun `should return correct number of reports`() {
        // Given
        val now = LocalDateTime.now()
        val reports = listOf(
            Report(id = 1L, title = "Report 1", notes = null, createdAt = now, updatedAt = now),
            Report(id = 2L, title = "Report 2", notes = null, createdAt = now, updatedAt = now),
            Report(id = 3L, title = "Report 3", notes = null, createdAt = now, updatedAt = now)
        )

        whenever(reportRepository.findAll()).thenReturn(reports)

        // When
        val result = reportService.getAllReports()

        // Then
        assertThat(result).hasSize(3)
        verify(reportRepository).findAll()
    }
}