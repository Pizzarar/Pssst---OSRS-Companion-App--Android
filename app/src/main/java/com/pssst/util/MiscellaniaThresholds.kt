package com.pssst.util

/**
 * Single source of truth for when the Kingdom of Miscellania needs attention. Shared by the
 * Overview card (approval colour and Advisor Ghrim vignette) and the sync-diff notifications,
 * so the card can never look healthy while a warning notification fires.
 */
object MiscellaniaThresholds {
    const val APPROVAL_WARN_PERCENT: Int = 90
    const val COFFER_WARN_GP: Long = 750_000L

    fun isWarning(approvalPercent: Int, cofferGp: Long): Boolean =
        approvalPercent < APPROVAL_WARN_PERCENT || cofferGp < COFFER_WARN_GP
}
