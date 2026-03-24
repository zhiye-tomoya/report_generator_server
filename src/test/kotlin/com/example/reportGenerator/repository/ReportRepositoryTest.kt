package com.example.reportGenerator.repository

import com.example.reportGenerator.entity.Report
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import jakarta.persistence.criteria.From

@DataJpaTest
class ReportRepositoryTest{

    @Autowired
    private lateinit var reportRepository: ReportRepository
    
    @Autowired
    private lateinit var entityManager: TestEntityManager

    private fun createReport(
        title: String,
        notes: String? = null,
        createdAt: LocalDateTime
    ): Report {
        val report = Report(
            title = title,
            notes = notes,
            createdAt = createdAt,
            updatedAt = createdAt
        )
        return entityManager.persistAndFlush(report)
    }


@Test
fun `should find reports within given date range`(){

    val startDate = LocalDateTime.of(2026, 3, 10, 0, 0, 0)
    val endDate = LocalDateTime.of(2026, 3, 20, 0, 0, 0)

    createReport(
        title = "Before range report",
        createdAt = LocalDateTime.of(2026, 3, 5, 12, 0, 0)
    )

    val reportInRange = createReport(
        title = "Within range report",
        createdAt = LocalDateTime.of(2026, 3, 15, 12, 0, 0)
    )

     createReport(
        title = "After range report",
        createdAt = LocalDateTime.of(2026, 3, 25, 12, 0, 0)
    )

    val result = reportRepository.findByCreatedAtBetween(startDate, endDate)

    assertThat(result).hasSize(1)
    assertThat(result[0].title).isEqualTo("Within range report")
    assertThat(result[0].id).isEqualTo(reportInRange.id)
    assertThat(result[0].createdAt).isBetween(startDate, endDate)

}
}

