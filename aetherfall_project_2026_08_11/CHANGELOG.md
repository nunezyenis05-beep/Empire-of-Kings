# CHANGELOG - EMPIRE OF KINGS

## [Unreleased] - Android continuation

### Launch hardening batch (2026-08-11)
- Repeated the real `test assembleDebug` gate in one shell with the documented JDK 17/SDK 36 bootstrap, `--no-daemon`, matching JVM options, and `--offline`; every route reached Gradle daemon startup but failed at the sandbox daemon connection gate before project evaluation. No APK or test result was produced, and no final ZIP was created.
- Checked runtime availability: Platform Tools was present temporarily, but no `emulator`, system image, or AVD exists; `adb devices -l` could not keep its local daemon and listed no devices. Updated `ENVIRONMENT.md`, `docs/LAUNCH_READINESS.md`, and `docs/GRADLE_FIX_REPORT.md` with the exact blockers.
- Made the static 3D and 50-task preflight audits ignore temporary toolchains/build outputs so they remain safe to run after environment bootstrap; post-build static QA passes all declared checks.
- Added `docs/3d-assets/PREMIUM_REALISTIC_SOURCE_RESEARCH.md` with file/license/mesh/skin/animation/Android review of MakeHuman/MPFB, Mixamo, Reallusion CC Character Base, and Blender Studio candidates.
- Recorded the no-source-cleared decision in the 3D license manifest and avatar catalog review metadata. The ten Quaternius CC0 low-poly GLBs and Xbot technical fallback remain unchanged; no navigation, UI, server, runtime, APK, or final ZIP changes.
- Confirmed the premium visual-quality blocker remains unresolved; no candidate was fabricated or integrated.
- Removed the unlicensed Renderpeople GLBs (`renderpeople_sophia.glb` and `rp_posed_00178_29.glb`) from Android assets; the catalog and license manifest now record their exclusion without bundling or redistributing them.
- Re-ran the expanded GLB gate: 10 active Quaternius assets plus Xbot fallback, exact mesh/skin/JOINTS/WEIGHTS/animation/idle contracts, SHA-256 and CC0 sidecar/manifest consistency, and prohibited-asset absence all pass. `mysticalEssence` remains a live Room economy field.
- Re-attempted `test assembleDebug` using the documented JDK 17/SDK 36 bootstrap; Gradle reached daemon startup but could not connect in this sandbox. No APK or final ZIP is claimed; Android device/emulator validation remains unavailable.
- Replaced the eight remaining Sophia-derived catalog files with independently authored Quaternius CC0 GLBs: Maya, Amara, Elena, Nadia, Mateo, Karim, Daniel and Isaac; each has `1 mesh / 1 skin / 11 clips`, including `CharacterArmature|Idle`.
- Updated `AvatarCatalog.kt`, `avatar_catalog.json`, eight provenance sidecars, the license manifest, Android asset status, implementation notes and 3D QA to record all ten active slots as independently sourced while keeping premium visual/device gates open.
- Integrated independently authored Quaternius CC0 human GLBs into the existing `leo` slot (`1 mesh / 1 skin / 8 clips`) and `sofia` slot (`1 mesh / 1 skin / 11 clips` from `Casual_Female.fbx`), preserving IDs and recording source URLs, conversion, exact SHA-256 values and license terms in the provenance sidecars and manifest. Android/device playback and premium visual acceptance remain pending; the full cast is not claimed complete.
- Rejected the downloadable Renderpeople animated sample for redistribution: its current terms permit game rendering but prohibit transfer/disclosure/easy extraction of the 3D data without written consent.
- Added `docs/LAUNCH_READINESS.md` with the verified checks and the blockers that still prevent a public launch claim.
- Aligned `compileSdk` with the installed Android Platform 36 archive instead of requesting unavailable minor API 36.1.
- Kept wardrobe outfit selection and the persisted online avatar preset synchronized after equipping an outfit.
- Removed out-of-bounds battle projectiles to prevent unbounded memory growth during long matches.
- Prevented multiple kills from being awarded when several projectiles hit an already-eliminated bot in the same simulation tick.
- Static QA still passes; a real Gradle build remains unverified until a JDK 17/Android SDK environment is available.
- Marked the environment scripts executable so the documented preflight commands work directly from a clean checkout.
- Retried the reproducible `test assembleDebug` build with the temporary JDK 17 and Android SDK; the sandbox still drops the Gradle child process before compilation, so no build success is claimed.
- Adjusted `build_in_sandbox.sh` to disable the Gradle daemon/JVM override where the constrained runner permits it; the same sandbox limitation still prevented the build from reaching source compilation.
- Hardened local chat persistence with sender/message length limits and an explicit `GLOBAL`/`CLAN`/`SQUAD` allowlist.
- Hardened inbound Socket.IO action handling so unknown remote action names are ignored.
- Added a 50-worker parallel launch preflight covering resources, routes, Room migrations, secrets, networking, and battle guards; all 50 checks passed.

### Current continuation batch (2026-08-10) — Batch 4
- Batch 4 adds exactly 200 auditable source checks in `tools/qa_batch4_200.py`; the audit passes 200/200.
- Added additive Room schema 4→5 state for persistent clan progression and auditable local demo purchase receipts; no payment-card data or server project is used.
- Rebuilt Shop into a compact portrait-safe blue-black/gold catalog: real Room-backed weapon equip actions, roulette feedback, allowlisted recharge packs, and a clear no-real-money disclosure.
- Rebuilt Clan into a Room-backed progression panel with capped weekly progress, atomic 500-gold contribution debit/glory credit, persistent feedback, member status, and a real route into the CLAN chat channel.
- Routed payment demo confirmation through an allowlisted package ledger with atomic balance + receipt writes, duplicate-submit protection, method/card validation, and visible processing/result feedback.
- Preserved the nine fixed navigation destinations and existing social/chat validation; legacy audits were updated for the additive schema/file layout and pass alongside Batch 2, Batch 3, and Batch 4 audits.
- Build status: Gradle `test assembleDebug` was attempted but cannot start because this sandbox has no `java` executable; no compile, test, or assemble success is claimed.

### Current continuation batch (2026-08-10)
- Batch 3 completes exactly 200 auditable static tasks for Discoteca, Juegos, Battle Royale, state persistence, mobile composition, and safety checks; `tools/qa_batch3_200.py` reports 200/200.
- Added additive Room 3→4 state tables for the Discoteca selection/emote counter and resumable mini-game progress; schema-3 users are backfilled without deleting account, inventory, wardrobe, or settings data.
- Rebuilt Discoteca into a portrait-safe, Room-backed track/emote panel with audio-setting disclosure, replaceable-asset disclosure, one human fallback avatar, selected-state feedback, and real callbacks.
- Rebuilt Juegos into three compact playable local sessions whose rewards settle once after two controls, with allowlisted game IDs, persisted results, and visible economy feedback.
- Corrected Battle Royale rendering to transform the fixed simulation world into the actual portrait canvas, added a real Lobby exit action, displayed the equipped weapon, and bounded joystick input.
- Post-change verification: `qa_batch3_200.py`, `qa_batch2_200.py`, and `qa_400.py` each pass their declared checks; Java is unavailable in this sandbox, so no Gradle test or assemble result is claimed.

### Current continuation batch (2026-08-11)
- Batch 2 completes exactly 200 auditable static tasks for the wardrobe/profile/navigation continuation; the dedicated `tools/qa_batch2_200.py` audit reports 200/200.
- Added additive Room 2→3 migration for cosmetic wardrobe items and persisted profile avatar preset, bio, and presence status; existing progress remains intact and schema-2 users receive cosmetic defaults.
- Rebuilt Vestuario around real Room-backed cosmetic slots, equip actions, feedback, a weapon-forge section, and a single human-avatar fallback disclosure per selection.
- Reworked Perfil into a compact portrait profile editor with validated name/bio/status editing, persisted avatar presets, progress/economy statistics, and save feedback.
- Rebalanced the fixed nine-section bottom navigation into two visible compact rows to prevent portrait overflow; shared blue-black/gold reference chrome is used by the updated screens.
- Post-change verification: `qa_batch2_200.py` 200/200, legacy `qa_200.py` 200/200, XML parsing and delimiter checks passed; `./gradlew test assembleDebug --no-daemon --console=plain` remains blocked before Gradle startup because this sandbox has no `java` executable.
- Replaced the inventory's decorative `FILTRAR` label with real type filters derived from Room data; the grid, count, and selected detail panel now follow the active filter and recover safely when data changes.
- Added an explicit Room 1→2 migration for `app_settings`, preserving existing accounts, inventory, weapons, and chat instead of relying on destructive migration for this known schema change.
- Updated the 200-check audit's source-count assertion to document and cover the current 33-file Kotlin production layout (including the Clan route and split Room entities).

### Reliability & progression
- Prevented the Room seed routine from overwriting the local player's progress on every launch.
- Made weapon upgrades read the latest database rows and persist the user, weapon, and essence inventory atomically.
- Implemented the premium roulette economy: it charges 25 diamonds, checks the balance, and persists one of three rewards.
- Implemented the previously empty demo diamond purchase helper with input guards.
- Battle results now persist wins, kills, and victory rewards exactly once per match.

### Android project quality
- Aligned the namespace, Kotlin packages, tests, and instrumentation assertion with the real application id.
- Repaired the screenshot test references to use the project's actual theme and a real Compose content node.
- Added a real splash → local authentication → lobby flow with form validation instead of pre-filled fake credentials.
- Repaired the wardrobe selection state after Room emits data or an upgrade.
- Made shop catalog actions navigate to the wardrobe and made pack buttons pass their correct item and price.
- Made the offline battle roster internally consistent so a local victory is reachable.
- Added arena bounds and safe-zone damage; mini-game rewards now update the persisted currency balance.
- Connected currency badges on every screen to the demo payment modal and guarded chat/squad inputs.
- Added a persisted local session with a sign-out action, so returning players skip authentication without bypassing the form on first launch.
- Removed the placeholder WhatsApp payment URL; that option now clearly stays pending until the payment backend exists and never grants demo currency accidentally.
- Removed hard-coded signing-key requirements so a local Debug build does not depend on a missing private keystore or any server secret.
- Routed purchases, minigame rewards, and battle rewards through fresh Room snapshots to prevent stale UI state from overwriting newer currency or statistics.
- Prevented double-spinning the roulette while a result is pending and corrected the battle result panel so defeats do not display victory rewards.
- Kept the rendered safe-zone center aligned with the local arena simulation.
- Made first-login username persistence wait briefly for Room seeding, avoiding a race that could revert the chosen name to the default.
- Removed stale payment wording that claimed WhatsApp would open a sales chat and validated local admin currency inputs before applying them.
- Added a public-server health bridge using the verified Render URL; the Settings/Admin screen now reports checking, online, or offline while keeping Room as the offline-first source of truth.
- Added best-effort remote register/login calls with token storage; local authentication still succeeds when the free server is sleeping or unavailable.
- Added a Socket.IO client for online player registration, movement broadcasts, actions, matchmaking, and remote-player markers in the battle arena; the five-bot local battle remains the fallback.
- Remote attack actions now produce a short visual pulse around the corresponding online player; damage and rewards remain local until a server combat contract is explicitly approved.
- The battle HUD now displays the live Socket.IO state and the number of players in a found online match, and stale remote markers are cleared after disconnect.
- Added a non-blocking matchmaking banner with elapsed time; the local battle continues while the free server searches for another player.
- The found-match banner now lists the connected player IDs so the online room is visible before deeper combat synchronization is enabled.
- The online room roster now shows up to six connected players with individual human-avatar previews, crown/combatant markers, connection status, and the local player's equipped outfit/weapon.
- Remote loadouts now use the existing `playerLoadout` protocol: the client sends normalized outfit, weapon, armor, and accessory values, validates incoming allowlisted values, and renders the received equipment.
- Added a local ready toggle and explicit protocol guard; the current public server rejects `ready`/`not_ready` on `playerAction`, so the client no longer sends unsupported actions. The room still shows `LISTOS: X/Y` and marks remote ready synchronization as pending.
- Added validated online loadout exchange using the server's existing `playerLoadout` event. The client sends a normalized loadout on connect/reconnect and displays remote outfit, weapon and armor when received.
- Added a three-second visual deployment countdown when every visible player is ready, with the ready control locked during the countdown.
- Leaving the battle now disconnects Socket.IO and clears the remote roster, ready states, and stale actions.
- Centralized online action names to avoid string mismatches between attack and ready events.
- Added Socket.IO connection detection and automatic reconnection when returning to the battle screen.
- Prevented duplicate matchmaking requests and sanitized outgoing action names.
- Limited and de-duplicated match rosters to six players; empty `matchFound` payloads remain in matchmaking.
- The roster always includes the local player, clears stale remote state for a new match, and explains Render cold starts after eight seconds.
- Added safe coordinate/health validation for incoming remote movement and bounded outgoing movement values.
- Remote snapshots now expire visually after ten seconds without updates; stale roster entries show `SIN SEÑAL`.
- Added `REINTENTAR` and `JUGAR LOCAL` controls after a long matchmaking wait, plus a server-health badge in the battle HUD.
- Retry and cancel actions now reset the online room cleanly, while re-entry reconnects with the user's saved avatar preset.
- Socket.IO now has bounded reconnect attempts, delays, timeout, forced fresh connections, and light action debouncing.
- The HUD shows the number of remote signals and remote players now render a validated health bar.
- The public `/health` endpoint was rechecked successfully with HTTP 200 and `ok: true`.
- Added a reproducible environment bootstrap for Temurin JDK 17, Gradle 9.3.1, Android SDK Platform 36, Build Tools 36.0.0, Platform Tools, and command-line tools.
- Added and verified the Gradle Wrapper; `gradlew --version` reports Gradle 9.3.1 with Java 17.
- Reduced Gradle memory/worker settings for the constrained sandbox and documented the environment in `ENVIRONMENT.md`.
- Added ignore rules for generated JDK/Gradle state and removed those bulky temporary artifacts after verification to keep the workspace below quota.
- An actual `:app:compileDebugKotlin` attempt was made; the sandbox's Gradle child process could not remain alive, so no compile result is claimed.
- Added `build_in_sandbox.sh` and updated `ENVIRONMENT.md` so the temporary SDK and build can be prepared in the same process when `/tmp` is not shared between shell invocations.
- Added `tools/qa_200.py`; the complete 200-check audit (100 baseline + 100 deep checks) passes.
- Added `tools/qa_400.py`; its exactly 200 additional semantic checks for the Socket.IO/loadout batch pass without requiring unnecessary client acknowledgement listeners or redundant ViewModel branches.

## [1.0.0] - Initial Production Implementation

### Visual & Identity
- Configured official brand identity: **EMPIRE OF KINGS** / **AETHERFALL: EMPIRE OF KINGS**.
- Set unique package application ID: `com.aistudio.empireofkings.game`.
- Generated high-quality imperial fantasy assets: Custom adaptive launcher icon, full-bleed 16:9 palace lobby background artwork, full-body King Warrior character portrait.
- Created luxury dark M3 color palette: Obsidian stone (`#0B0E1B`), Imperial Gold (`#FFD700`), Mystic Purple (`#8A2BE2`), Cyan Fire (`#00FFFF`), Regal Crimson (`#D32F2F`).

### Full Application Architecture & Modules
- **Authentication & Entry Flow**: Splash screen with animated crown branding, connection & session checks, registration, login, phone/email verification, privacy consent, terms, avatar creation.
- **Main Lobby (Pantalla Completa 16:9 Adaptable)**:
  - Top Bar (~12% screen height): Profile circular avatar, name (`KING_PLAYER`), Level 99 Elite title, central game title (`AETHERFALL: EMPIRE OF KINGS`), currencies (Gold Coins & Coronarias/Diamonds).
  - Sanctuary Central Hall: Full-body King Warrior standing on magical rune circle with golden throne and dragon-lion guardians in background.
  - Squad & Social Panel (Left): 6 player squad slots for Duos & Clan members, friend invite actions, global and squad chat drawer.
  - Action Menu (Right): Glowing golden **JUGAR** button, **BAILAR**, **SALUDAR**, **REGALAR** quick action buttons.
  - Bottom Navigation Bar (8 sections): **LOBBY**, **TIENDA**, **ARMARIO**, **DISCOTECA**, **JUEGOS**, **INVENTARIO**, **PERFIL**, **CONFIGURACIÓN**.
- **Batalla de los Tronos (Battle Royale Mode)**:
  - Playable 50-player battle match simulation & interactive arena combat.
  - Portal of Invocation drop onto mystical island map.
  - Full Weaponry System: Normal weapons (Gold) vs Premium Mystical weapons (Diamonds).
  - 10 Weapon Classes: Imperial Pistols, Abyss Shotgun, Crystal Storm Submachine, King's Crown Rifle, Lion Eye Sniper, Titanic Forge Heavy Weapon, Energy Bow, Crystal Crossbow, Magic Staves, Shadow Gauntlets.
  - Interactive Combat Controls: Joystick/WASD movement, Aiming, Energy firing, Reload, Gloo Wall Crystal Shield, Healing Potions, Purple Fire Safe Zone shrinking.
  - Spectator mode and Victory "REINADO SUPREMO" screen with Gold, Diamonds & Mystical Essence rewards.
- **TIENDA & Premium Gacha (Ruleta Premium)**:
  - Mystical Premium Weapon Roulette with diamond spins and level upgrades.
  - Real payment gateways simulator: CubaPay, Zelle, PayPal, Credit/Debit Cards, CUP payment via WhatsApp.
- **ARMARIO (Wardrobe & Weapons Forge)**:
  - Level 1-15 Weapon Upgrade System using Mystical Essence + Diamonds.
  - Unlocks at Lv 5 (Aura), Lv 10 (Floating Runes), Lv 15 (Golden Mastery & Elimination Animation).
  - Outfit, Cape, Armor, Crown, and Emote customizer.
- **DISCOTECA IMPERIAL (Relics & Dance Floor)**:
  - Interactive dance floor with DJ music tracks, dance emote triggers, particle light effects.
- **JUEGOS (Mini-games)**:
  - Imperial Chess & Rune Cards battle mini-games.
- **INVENTARIO, PERFIL, CONFIGURACIÓN & ADMIN PANEL**:
  - Full Room database backing user profile, currencies, weapons, inventory items, friends, chat history.
  - Admin & Financial dashboard for user management, currency top-ups, weapon balance tweaks, payment history.
