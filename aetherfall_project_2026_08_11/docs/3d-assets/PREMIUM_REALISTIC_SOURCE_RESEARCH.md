# Premium-realistic human source review

Review date: 2026-08-11
Project: Empire-of-Kings Android APK
Decision: **NO_SOURCE_CLEARED_FOR_INTEGRATION**

This review addresses only the remaining visual-quality blocker. It does not change navigation, Compose UI, Room, ViewModels, networking, or the server. No candidate was copied into `app/src/main/assets`, and no APK/ZIP was produced.

## Acceptance contract

A source had to satisfy all of these conditions at the same time:

1. Human character with a visibly more realistic/premium presentation than the current Quaternius low-poly fallback cast.
2. A legally downloadable, identifiable file (not a screenshot, ripped game asset, or an extraction from Sketchfab).
3. An explicit license or publisher permission allowing redistribution inside a closed-source Android APK and commercial use.
4. A production-ready rig/skin contract and actual animation clips that can be converted to GLB without inventing provenance.
5. A file that can be inspected and hash-recorded before integration.

Passing only a license check or only a realistic render is not sufficient.

## Sources checked

### MakeHuman / MPFB — legally promising, not a cleared production artifact

- License: [MakeHuman Community license](https://static.makehumancommunity.org/about/license.html) states that core graphics assets are CC0 and may be used as derivatives without attribution. The [closed-source FAQ](https://static.makehumancommunity.org/mpfb/faq/use_in_closed_source.html) explicitly says MPFB-generated characters can ship in closed-source games and that core assets are CC0.
- Downloadable material: the [MakeHuman system asset pack](https://static.makehumancommunity.org/assets/assetpacks/makehuman_system_assets.html) is a 267 MB CC0 asset pack. It contains source assets such as eyes and hair, not a finished Android GLB cast.
- Contract evidence: the [export documentation](https://static.makehumancommunity.org/makehuman/docs/exports_and_file_formats.html) describes FBX mesh export and BVH rig export; it says built-in animations are anticipated in the BVH export directory. It does not provide a prebuilt, hashable GLB with the required neutral and gameplay clips.
- Blocker: this is a generator/export pipeline, not an already downloaded human character file whose mesh, skin, animations, materials, and Android conversion can be inspected in this repository. Completing it would require authoring/exporting characters and validating the resulting GLBs, not simply integrating a cleared source artifact. No MakeHuman output was fabricated or mislabeled as production-ready.
- Verdict: **not integrated; legal path remains a future art-production option, not evidence that the blocker is resolved**.

### Adobe Mixamo — quality/rigging plausible, redistribution fails

- The [Adobe Mixamo FAQ](https://community.adobe.com/questions-696/mixamo-faq-licensing-royalties-ownership-eula-and-tos-589400) says games and commercial projects are allowed, but its explicit distribution table prohibits distributing character/animation raw files and prohibits free distribution of those raw files.
- The Android contract requires the character GLB and animation data to ship in the APK. That is precisely raw character/animation data distribution, even when packaged in an APK.
- Access also requires an account; no credentials were used and no bypass was attempted.
- Verdict: **rejected for this APK; do not download, bundle, or derive a candidate from Mixamo**.

### Reallusion CC Character Base — plausible realism/rigging, permission and access not cleared

- The [publisher page](https://www.reallusion.com/character-creator/free-3d-character-base.html) advertises five fully rigged models in FBX/OBJ/ZTL and says the free bases are available for personal and commercial purposes.
- The same page says the download requires registration or login and links an EULA. It does not, on the accessible license text, provide the required explicit permission to redistribute the raw character files in a closed-source Android APK.
- No account was used, no download was attempted through a bypass, and no actual file was available for mesh/skin/animation/hash inspection.
- Verdict: **not cleared; commercial project use alone is not treated as APK raw-asset redistribution permission**.

### Blender Studio character library — rigged files advertised, license/file gate unresolved

- The [Blender Studio character library](https://studio.blender.org/characters/) describes its entries as fully rigged characters from Blender Studio open movies.
- The public library page inspected here did not expose a per-file redistribution license and Android-ready downloadable GLB contract for a realistic human character. The visible library also includes stylized/film-production characters rather than a validated premium-realistic human cast.
- No file was copied or converted without a file-level license and provenance record.
- Verdict: **not cleared**.

### Renderpeople and Sketchfab — explicitly excluded

- Renderpeople candidates remain excluded because the project record already identifies transfer/disclosure restrictions; the task also expressly prohibits Renderpeople bundling.
- Sketchfab extraction is expressly prohibited by the task. Per-model download pages are not a substitute for a source license that has been checked for APK raw-file redistribution.
- No excluded candidate was inspected, copied, or integrated.

## Current files actually inspected

The ten active slots remain the existing Quaternius CC0 GLBs, with Xbot retained as the technical fallback. Static inspection of the actual bundled files found:

| Set | Files | Meshes | Skins | Animation clips | Neutral clip |
|---|---:|---:|---:|---:|---|
| Quaternius Ultimate Animated Character Pack | `maya`, `sofia`, `amara`, `elena`, `nadia`, `mateo`, `karim`, `daniel`, `isaac` | 1 each | 1 each | 11 each | `CharacterArmature|Idle` |
| Quaternius Animated Human Low Poly | `leo` | 1 | 1 | 8 | `Human Armature|Idle` |
| Technical fallback | `Xbot.glb` | 2 | 1 | 7 | renderer fallback only |

All ten active GLBs contain `POSITION`, `JOINTS_0`, and `WEIGHTS_0` attributes and have matching SHA-256 entries in the license manifest and per-avatar provenance sidecars. Their license and redistribution status remain CC0 and valid. Their blocker is visual: the source itself is explicitly low-poly/stylized and does not meet the requested premium-realistic finish.

## Decision and next action

No candidate met **all** legal, file-level, mesh/skin, animation, Android, and premium-realism gates. Therefore:

- The safe Quaternius cast and Xbot fallback remain unchanged.
- No catalog slot, route, UI, server contract, or runtime behavior was changed.
- No candidate hash, provenance, or license record was invented.
- The visual-quality blocker is **not resolved**.
- A future replacement requires either a commissioned/owned realistic character set or a source whose license expressly covers redistribution of the embedded raw files in a commercial closed-source Android APK, followed by actual GLB inspection and device playback.
