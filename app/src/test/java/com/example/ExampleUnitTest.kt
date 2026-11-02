package com.example

import com.example.data.FeedingLog
import com.example.data.FeedingType
import com.example.data.VolumeUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testAmountFormattingMlAndOz() {
    val log = FeedingLog(
      amountMl = 120f,
      feedingType = FeedingType.BOTTLE_FORMULA.name
    )

    assertEquals("120 ml", log.getFormattedAmount(VolumeUnit.ML))
    assertTrue(log.getFormattedAmount(VolumeUnit.OZ).contains("oz"))
    assertTrue(log.getFormattedAmount(VolumeUnit.OZ).contains("120 ml"))
  }

  @Test
  fun testNursingDurationFormatting() {
    val nursingLog = FeedingLog(
      amountMl = 0f,
      durationMinutes = 20,
      nursingSide = "Both",
      feedingType = FeedingType.NURSING.name
    )

    assertEquals("20m", nursingLog.getFormattedAmount(VolumeUnit.ML))
    assertEquals("20m", nursingLog.getFormattedAmount(VolumeUnit.OZ))
  }

  @Test
  fun testElapsedCalculation() {
    val now = 10000000L
    val tenMinsAgo = now - (10 * 60 * 1000L)
    val twoHoursAgo = now - (2 * 60 * 60 * 1000L + 15 * 60 * 1000L)

    val diff1 = (now - tenMinsAgo) / (1000 * 60)
    assertEquals(10L, diff1)

    val diff2Hours = (now - twoHoursAgo) / (1000 * 60 * 60)
    val diff2Mins = ((now - twoHoursAgo) / (1000 * 60)) % 60
    assertEquals(2L, diff2Hours)
    assertEquals(15L, diff2Mins)
  }

  @Test
  fun testQuickAmountsCustomizationLogic() {
    val rawList = listOf(150f, 60f, 120f, 90f, 60f)
    val sortedDistinct = rawList.distinct().filter { it > 0f }.sorted()
    assertEquals(listOf(60f, 90f, 120f, 150f), sortedDistinct)
  }

  @Test
  fun testDiaperStatusAndBadges() {
    val peeLog = FeedingLog(
      diaperStatus = com.example.data.DiaperStatus.PEE.name
    )
    assertTrue(peeLog.hasDiaperChange())
    assertEquals("💧 Pee", peeLog.getDiaperBadge())

    val poopLog = FeedingLog(
      diaperStatus = com.example.data.DiaperStatus.POOP.name
    )
    assertTrue(poopLog.hasDiaperChange())
    assertEquals("💩 Poop", poopLog.getDiaperBadge())

    val bothLog = FeedingLog(
      diaperStatus = com.example.data.DiaperStatus.BOTH.name
    )
    assertTrue(bothLog.hasDiaperChange())
    assertEquals("💧💩 Both", bothLog.getDiaperBadge())

    val noneLog = FeedingLog(
      diaperStatus = com.example.data.DiaperStatus.NONE.name
    )
    org.junit.Assert.assertFalse(noneLog.hasDiaperChange())
    org.junit.Assert.assertNull(noneLog.getDiaperBadge())
  }

  @Test
  fun testIdleDisplayThemes() {
    val themes = com.example.data.IdleDisplayTheme.values()
    assertTrue(themes.isNotEmpty())
    assertTrue(themes.any { it.id == "red_night" })
    assertTrue(themes.any { it.id == "amber" })
    assertTrue(themes.any { it.id == "mint" })
    assertTrue(themes.any { it.id == "lavender" })
    assertTrue(themes.any { it.id == "monochrome" })
  }
}
