package com.pssst.theme.art

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Code-generated [ImageVector] glyphs drawn in one consistent hand:
 * - 24x24dp viewport
 * - 1.75f forged-iron/brass stroke width
 * - Beveled 45-degree joins and square caps
 * - Subtle 18% interior tint fill for body weight
 */
object ArtIcons {

    /**
     * Overview nav icon: Kite shield with a chiseled chevron boss and corner rivets.
     */
    val OverviewShield: ImageVector by lazy {
        buildLedgerIcon("OverviewShield") {
            // Subtle interior body fill
            path(
                fill = SolidColor(Color.White.copy(alpha = 0.18f)),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12f, 2.5f)
                lineTo(20f, 5.5f)
                lineTo(20f, 11.5f)
                lineTo(17.5f, 17.2f)
                lineTo(12f, 21.5f)
                lineTo(6.5f, 17.2f)
                lineTo(4f, 11.5f)
                lineTo(4f, 5.5f)
                close()
            }
            // Outer shield rim + inner chevron & vertical spine
            strokePath {
                moveTo(12f, 2.5f)
                lineTo(20f, 5.5f)
                lineTo(20f, 11.5f)
                lineTo(17.5f, 17.2f)
                lineTo(12f, 21.5f)
                lineTo(6.5f, 17.2f)
                lineTo(4f, 11.5f)
                lineTo(4f, 5.5f)
                close()
                // Central vertical ridge
                moveTo(12f, 2.8f)
                lineTo(12f, 21.0f)
                // Forged chevron boss
                moveTo(7.2f, 11.5f)
                lineTo(12f, 8.2f)
                lineTo(16.8f, 11.5f)
            }
        }
    }

    /**
     * Storage nav icon: Iron-bound arched coffer chest with lock hasp.
     */
    val StorageChest: ImageVector by lazy {
        buildLedgerIcon("StorageChest") {
            path(fill = SolidColor(Color.White.copy(alpha = 0.18f))) {
                moveTo(3.5f, 8.5f)
                lineTo(6f, 5f)
                lineTo(18f, 5f)
                lineTo(20.5f, 8.5f)
                lineTo(20.5f, 19.5f)
                lineTo(3.5f, 19.5f)
                close()
            }
            strokePath {
                // Beveled lid
                moveTo(3.5f, 9.5f)
                lineTo(5.8f, 5f)
                lineTo(18.2f, 5f)
                lineTo(20.5f, 9.5f)
                close()
                // Chest base box
                moveTo(3.5f, 9.5f)
                lineTo(20.5f, 9.5f)
                lineTo(20.5f, 19.5f)
                lineTo(3.5f, 19.5f)
                close()
                // Iron straps left & right
                moveTo(7.5f, 5f)
                lineTo(7.5f, 19.5f)
                moveTo(16.5f, 5f)
                lineTo(16.5f, 19.5f)
                // Center lock plate
                moveTo(10.3f, 8.2f)
                lineTo(13.7f, 8.2f)
                lineTo(13.7f, 13.0f)
                lineTo(12f, 14.4f)
                lineTo(10.3f, 13.0f)
                close()
            }
        }
    }

    /**
     * Exchange nav icon: Twin stacked GP coin columns with stamped rims.
     */
    val ExchangeCoins: ImageVector by lazy {
        buildLedgerIcon("ExchangeCoins") {
            path(fill = SolidColor(Color.White.copy(alpha = 0.18f))) {
                moveTo(3f, 10f)
                lineTo(13f, 10f)
                lineTo(13f, 19.5f)
                lineTo(3f, 19.5f)
                close()
                moveTo(11f, 5f)
                lineTo(21f, 5f)
                lineTo(21f, 16.5f)
                lineTo(11f, 16.5f)
                close()
            }
            strokePath {
                // Back stack (right, taller)
                moveTo(11.5f, 5.5f)
                lineTo(14f, 4f)
                lineTo(18.5f, 4f)
                lineTo(21f, 5.5f)
                lineTo(21f, 16f)
                lineTo(18.5f, 17.2f)
                lineTo(14f, 17.2f)
                // Coin tier grooves on back stack
                moveTo(11.5f, 5.5f)
                lineTo(14f, 6.8f)
                lineTo(18.5f, 6.8f)
                lineTo(21f, 5.5f)
                moveTo(14f, 10.2f)
                lineTo(18.5f, 10.2f)
                lineTo(21f, 9f)
                moveTo(14f, 13.6f)
                lineTo(18.5f, 13.6f)
                lineTo(21f, 12.4f)

                // Front stack (left, foreground)
                moveTo(3f, 10f)
                lineTo(5.8f, 8.4f)
                lineTo(10.7f, 8.4f)
                lineTo(13.5f, 10f)
                lineTo(13.5f, 18.5f)
                lineTo(10.7f, 20f)
                lineTo(5.8f, 20f)
                lineTo(3f, 18.5f)
                close()
                // Front stack coin tiers
                moveTo(3f, 10f)
                lineTo(5.8f, 11.4f)
                lineTo(10.7f, 11.4f)
                lineTo(13.5f, 10f)
                moveTo(3f, 14.2f)
                lineTo(5.8f, 15.6f)
                lineTo(10.7f, 15.6f)
                lineTo(13.5f, 14.2f)
            }
        }
    }

    /**
     * Progress nav icon: Chiseled 8-point compass star with faceted inner diamond.
     */
    val ProgressStar: ImageVector by lazy {
        buildLedgerIcon("ProgressStar") {
            path(fill = SolidColor(Color.White.copy(alpha = 0.18f))) {
                moveTo(12f, 2f)
                lineTo(14.6f, 8.2f)
                lineTo(21.5f, 9.2f)
                lineTo(16.3f, 13.8f)
                lineTo(17.8f, 20.8f)
                lineTo(12f, 17.2f)
                lineTo(6.2f, 20.8f)
                lineTo(7.7f, 13.8f)
                lineTo(2.5f, 9.2f)
                lineTo(9.4f, 8.2f)
                close()
            }
            strokePath {
                moveTo(12f, 2f)
                lineTo(14.6f, 8.2f)
                lineTo(21.5f, 9.2f)
                lineTo(16.3f, 13.8f)
                lineTo(17.8f, 20.8f)
                lineTo(12f, 17.2f)
                lineTo(6.2f, 20.8f)
                lineTo(7.7f, 13.8f)
                lineTo(2.5f, 9.2f)
                lineTo(9.4f, 8.2f)
                close()
                // Inner faceted diamond
                moveTo(12f, 8.8f)
                lineTo(15.2f, 12f)
                lineTo(12f, 15.2f)
                lineTo(8.8f, 12f)
                close()
            }
        }
    }

    /**
     * Timers nav icon: Wood-and-brass twin-bulb sandglass with side pillars.
     */
    val TimersHourglass: ImageVector by lazy {
        buildLedgerIcon("TimersHourglass") {
            path(fill = SolidColor(Color.White.copy(alpha = 0.18f))) {
                moveTo(6.5f, 4f)
                lineTo(17.5f, 4f)
                lineTo(17.5f, 7.5f)
                lineTo(12f, 12f)
                lineTo(17.5f, 16.5f)
                lineTo(17.5f, 20f)
                lineTo(6.5f, 20f)
                lineTo(6.5f, 16.5f)
                lineTo(12f, 12f)
                lineTo(6.5f, 7.5f)
                close()
            }
            strokePath {
                // Top and bottom oak/brass platens
                moveTo(4.5f, 3.5f)
                lineTo(19.5f, 3.5f)
                moveTo(4.5f, 20.5f)
                lineTo(19.5f, 20.5f)
                // Glass bulbs
                moveTo(6.8f, 3.5f)
                lineTo(6.8f, 7.5f)
                lineTo(12f, 12f)
                lineTo(6.8f, 16.5f)
                lineTo(6.8f, 20.5f)
                moveTo(17.2f, 3.5f)
                lineTo(17.2f, 7.5f)
                lineTo(12f, 12f)
                lineTo(17.2f, 16.5f)
                lineTo(17.2f, 20.5f)
                // Sand cone in lower bulb
                moveTo(9f, 18.5f)
                lineTo(12f, 15.5f)
                lineTo(15f, 18.5f)
                close()
            }
        }
    }

    /**
     * ReadyStrip & Timers glyph: Wooden birdhouse with pitched roof, entrance hole, and perch peg.
     */
    val Birdhouse: ImageVector by lazy {
        buildLedgerIcon("Birdhouse") {
            path(fill = SolidColor(Color.White.copy(alpha = 0.18f))) {
                moveTo(12f, 3.5f)
                lineTo(19f, 9f)
                lineTo(17f, 9f)
                lineTo(17f, 19f)
                lineTo(7f, 19f)
                lineTo(7f, 9f)
                lineTo(5f, 9f)
                close()
            }
            strokePath {
                // Pitched roof with eaves
                moveTo(4f, 9.5f)
                lineTo(12f, 3.2f)
                lineTo(20f, 9.5f)
                // House walls
                moveTo(6.8f, 8.2f)
                lineTo(6.8f, 19f)
                lineTo(17.2f, 19f)
                lineTo(17.2f, 8.2f)
                // Diamond entrance hole
                moveTo(12f, 10.2f)
                lineTo(14.2f, 12.4f)
                lineTo(12f, 14.6f)
                lineTo(9.8f, 12.4f)
                close()
                // Perch & post
                moveTo(12f, 16.5f)
                lineTo(15f, 16.5f)
                moveTo(12f, 19f)
                lineTo(12f, 21.5f)
            }
        }
    }

    /**
     * ReadyStrip & Timers glyph: Three-lobed herb leaf sprig with central stem vein.
     */
    val HerbSprig: ImageVector by lazy {
        buildLedgerIcon("HerbSprig") {
            path(fill = SolidColor(Color.White.copy(alpha = 0.18f))) {
                moveTo(12f, 3.5f)
                lineTo(18.5f, 8.5f)
                lineTo(16.5f, 15.5f)
                lineTo(12f, 18.5f)
                lineTo(7.5f, 15.5f)
                lineTo(5.5f, 8.5f)
                close()
            }
            strokePath {
                // Leaf blade
                moveTo(12f, 3.2f)
                lineTo(18.5f, 8.5f)
                lineTo(16.5f, 15.5f)
                lineTo(12f, 18.5f)
                lineTo(7.5f, 15.5f)
                lineTo(5.5f, 8.5f)
                close()
                // Central stem & lateral veins
                moveTo(12f, 6.2f)
                lineTo(12f, 21.2f)
                moveTo(12f, 11.2f)
                lineTo(15.5f, 8.8f)
                moveTo(12f, 14.0f)
                lineTo(8.5f, 11.6f)
            }
        }
    }

    /**
     * ReadyStrip & Timers glyph: Faceted tree crown over rooted trunk.
     */
    val TreeCrown: ImageVector by lazy {
        buildLedgerIcon("TreeCrown") {
            path(fill = SolidColor(Color.White.copy(alpha = 0.18f))) {
                moveTo(12f, 3f)
                lineTo(18.5f, 8.5f)
                lineTo(16.5f, 14.5f)
                lineTo(7.5f, 14.5f)
                lineTo(5.5f, 8.5f)
                close()
            }
            strokePath {
                // Crown polygon
                moveTo(12f, 3f)
                lineTo(18.5f, 8.5f)
                lineTo(16.8f, 14.5f)
                lineTo(7.2f, 14.5f)
                lineTo(5.5f, 8.5f)
                close()
                // Trunk & roots
                moveTo(10.5f, 14.5f)
                lineTo(10.5f, 20.5f)
                moveTo(13.5f, 14.5f)
                lineTo(13.5f, 20.5f)
                moveTo(7.5f, 20.5f)
                lineTo(16.5f, 20.5f)
                // Inner branch Y
                moveTo(12f, 14.5f)
                lineTo(12f, 9.5f)
                moveTo(12f, 11.5f)
                lineTo(9.5f, 9.2f)
                moveTo(12f, 10.8f)
                lineTo(14.5f, 8.5f)
            }
        }
    }

    /**
     * Top-bar drawer button icon: Three forged iron bars with diamond end-studs.
     */
    val DrawerLedgerMenu: ImageVector by lazy {
        buildLedgerIcon("DrawerLedgerMenu") {
            strokePath {
                moveTo(4f, 6.5f)
                lineTo(20f, 6.5f)
                moveTo(4f, 12f)
                lineTo(20f, 12f)
                moveTo(4f, 17.5f)
                lineTo(20f, 17.5f)
                // Left/right vertical iron bracket ticks
                moveTo(4f, 5f)
                lineTo(4f, 8f)
                moveTo(20f, 5f)
                lineTo(20f, 8f)
                moveTo(4f, 16f)
                lineTo(4f, 19f)
                moveTo(20f, 16f)
                lineTo(20f, 19f)
            }
        }
    }

    /**
     * Top-bar edit-timers button icon: Chiseled scribe's quill stylus over ledger rule.
     */
    val EditQuill: ImageVector by lazy {
        buildLedgerIcon("EditQuill") {
            path(fill = SolidColor(Color.White.copy(alpha = 0.18f))) {
                moveTo(15.5f, 4f)
                lineTo(20f, 8.5f)
                lineTo(9f, 19.5f)
                lineTo(4.5f, 19.5f)
                lineTo(4.5f, 15f)
                close()
            }
            strokePath {
                moveTo(15.5f, 4f)
                lineTo(20f, 8.5f)
                lineTo(9f, 19.5f)
                lineTo(4.5f, 19.5f)
                lineTo(4.5f, 15f)
                close()
                moveTo(13f, 6.5f)
                lineTo(17.5f, 11f)
                moveTo(4f, 21.5f)
                lineTo(20f, 21.5f)
            }
        }
    }

    /**
     * State badge icon: Ready (chamfered octagonal seal with chiseled checkmark).
     */
    val BadgeReady: ImageVector by lazy {
        buildLedgerIcon("BadgeReady") {
            path(fill = SolidColor(Color.White.copy(alpha = 0.22f))) {
                moveTo(8f, 3f)
                lineTo(16f, 3f)
                lineTo(21f, 8f)
                lineTo(21f, 16f)
                lineTo(16f, 21f)
                lineTo(8f, 21f)
                lineTo(3f, 16f)
                lineTo(3f, 8f)
                close()
            }
            strokePath {
                moveTo(8f, 3f)
                lineTo(16f, 3f)
                lineTo(21f, 8f)
                lineTo(21f, 16f)
                lineTo(16f, 21f)
                lineTo(8f, 21f)
                lineTo(3f, 16f)
                lineTo(3f, 8f)
                close()
                // Chiseled check
                moveTo(7.5f, 12.2f)
                lineTo(10.8f, 15.5f)
                lineTo(16.8f, 8.8f)
            }
        }
    }

    /**
     * State badge icon: Growing / In Progress (chamfered diamond frame with clock hands / sprout).
     */
    val BadgeGrowing: ImageVector by lazy {
        buildLedgerIcon("BadgeGrowing") {
            path(fill = SolidColor(Color.White.copy(alpha = 0.18f))) {
                moveTo(8f, 3f)
                lineTo(16f, 3f)
                lineTo(21f, 8f)
                lineTo(21f, 16f)
                lineTo(16f, 21f)
                lineTo(8f, 21f)
                lineTo(3f, 16f)
                lineTo(3f, 8f)
                close()
            }
            strokePath {
                moveTo(8f, 3f)
                lineTo(16f, 3f)
                lineTo(21f, 8f)
                lineTo(21f, 16f)
                lineTo(16f, 21f)
                lineTo(8f, 21f)
                lineTo(3f, 16f)
                lineTo(3f, 8f)
                close()
                // Clock/sundial hands
                moveTo(12f, 7f)
                lineTo(12f, 12.5f)
                lineTo(16f, 14.5f)
            }
        }
    }

    /**
     * State badge icon: Empty / Inactive (recessed hollow diamond with center dash).
     */
    val BadgeEmpty: ImageVector by lazy {
        buildLedgerIcon("BadgeEmpty") {
            strokePath {
                moveTo(12f, 3.5f)
                lineTo(20.5f, 12f)
                lineTo(12f, 20.5f)
                lineTo(3.5f, 12f)
                close()
                moveTo(9f, 12f)
                lineTo(15f, 12f)
            }
        }
    }

    /**
     * State badge icon: Warning / Diseased / Low Approval (chamfered iron triangle with exclamation).
     */
    val BadgeWarning: ImageVector by lazy {
        buildLedgerIcon("BadgeWarning") {
            path(fill = SolidColor(Color.White.copy(alpha = 0.20f))) {
                moveTo(12f, 3f)
                lineTo(21.5f, 19.5f)
                lineTo(2.5f, 19.5f)
                close()
            }
            strokePath {
                moveTo(12f, 3f)
                lineTo(21.5f, 19.5f)
                lineTo(2.5f, 19.5f)
                close()
                moveTo(12f, 9f)
                lineTo(12f, 14.2f)
                moveTo(12f, 16.8f)
                lineTo(12f, 17.4f)
            }
        }
    }

    private fun buildLedgerIcon(
        name: String,
        block: ImageVector.Builder.() -> Unit
    ): ImageVector {
        return ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply(block).build()
    }

    private fun ImageVector.Builder.strokePath(
        pathBuilder: androidx.compose.ui.graphics.vector.PathBuilder.() -> Unit
    ) {
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.75f,
            strokeLineCap = StrokeCap.Square,
            strokeLineJoin = StrokeJoin.Bevel,
            pathBuilder = pathBuilder
        )
    }
}
