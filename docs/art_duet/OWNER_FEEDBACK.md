# Round 2 Art Revision from Owner (Emulator Review)

## Owner Verbatim Feedback
> "i dont like hte kindom art, would rather it just be a castle or something. would really like them to try fixing the backgorund and bar background art its so ugly. Theres for sure a way to make it more ruenscapey with like the login screenstone or something."

## Round 2 Action Items (Gemini + Claude Art Duet on eat/piskel-art-duet, PR #1)
1. **Kingdom Art -> Miscellania Castle (KINGDOM_HEALTHY and KINGDOM_WARNING)**:
   - Replace the coffer/crown Kingdom of Miscellania vignettes in 	ools/art/, SceneArt.kt, and KingdomSummaryCard.kt with an iconic OSRS Miscellania Castle vignette:
     - Crenellated stone battlements, twin turrets, gatehouse portcullis, and royal pennant/banner on the keep roof.
     - KINGDOM_HEALTHY: Crisp stone keep with gold/emerald royal banner flying proudly and warm torchlight.
     - KINGDOM_WARNING: Weathered/dim stone keep with a tattered/drooping crimson warning banner and amber/red warning glow at the gatehouse.
2. **Background & Top/Bottom Bar Background Overhaul (OSRS Login-Screen Stone Masonry)**:
   - Inspect pp/src/main/java/com/pssst/theme/art/Textures.kt (drawStoneGround, drawPanelStone, drawOakBeam), Frames.kt, Plaques.kt, TopStatusStrip.kt, BottomTabStrip.kt, and MainActivity.kt.
   - Replace the ugly noisy/flat procedural stone & oak bar backgrounds with authentic **OSRS login-screen stone masonry**:
     - **App Background (drawStoneGround)**: Classic OSRS login-screen / dungeon-wall carved stone blocks — staggered ashlar stone courses with deep mortar joints, chiselled 1px top-left highlight and bottom-right shadow bevels on individual stone blocks, subtle weathered cracks/moss flecks, and a warm torchlit radial vignette from top-center.
     - **Top & Bottom Bar Backgrounds (TopStatusStrip, BottomTabStrip, drawOakBeam / Plaques)**: Replace the ugly bar background fills with heavy carved OSRS login-screen stone header/footer slabs (recessed dark-stone bevelled channels, chiseled stone arch/brazier framing, and bronze/gold corner rivets) so the top status bar and bottom navigation bar feel like genuine OSRS login-screen stone UI chrome.
3. **Duet & Verification**:
   - Run a fast Gemini + Claude duet pass (critique current Textures.kt/Frames.kt/Plaques.kt, design the exact OSRS login-screen stone masonry + castle vignette, implement, and visually inspect rendered PNGs including the stone background + bar backgrounds + castle card in a Round 2 Shot Review).
   - Run ./gradlew testDebugUnitTest assembleDebug, commit, and push to origin/feat/piskel-art-duet.
   - Notify Brainstorm Lead (66314d70-bc8b-4c2c-bc2c-09bf1de120f8) when pushed.

## Additional Owner Direction (2026-10-30 21:29 EDT)
> "it can really do a full runsecapeoverhaul with the art if it wants. Just dont replace anyhting that uses actual icons currently"

- **Green light for full OSRS UI/art overhaul**: Go all-in on making the app feel authentically Old School RuneScape across backgrounds (drawStoneGround), top/bottom bars (TopStatusStrip, BottomTabStrip, drawOakBeam), card/panel stone and parchment surfaces (drawPanelStone, drawParchmentSurface, Frames.kt, Plaques.kt), Miscellania Castle vignette (KINGDOM_HEALTHY / KINGDOM_WARNING), and procedural scene art.
- **HARD CONSTRAINT — Do NOT touch anything using actual icons**: Preserve all real/existing icon usages (ItemIcon.kt, OSRS item/skill/tab icons, drawable PNGs in es/drawable*, etc.). Only overhaul the procedural backgrounds, bar backgrounds, frames/plaques, and custom pixel-art vignettes/empty-state scenes.
