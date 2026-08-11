# Android 3D integration

## What is now fixed

- Added the native Filament renderer through `io.github.sceneview:sceneview:3.3.0`.
- Bundled the validated animated glTF/GLB smoke-test model at `app/src/main/assets/models/Xbot.glb`.
- Added `ui/components/HumanAvatar3D.kt` as the single rendering surface for avatars.
- Replaced the old JPG-only avatar surface in the profile, registration selector, top bar, wardrobe preview and disco preview.
- The wardrobe detail preview supports orbit and zoom gestures; compact previews keep gestures disabled to avoid stealing screen taps.
- Registration now persists the selected preset and the online handshake uses that same preset immediately, instead of always sending the default warrior.
- Kept the existing Compose screens, navigation, mystical background and one-avatar-per-tile layout intact.

## Asset boundary

`Xbot.glb` is the technical pipeline validator copied from the existing handoff's Three.js model set (the upstream source is the Three.js example asset). It has a skeleton and animation clips, but it is not the final realistic premium cast; its redistribution terms must still be kept with the asset inventory before a commercial release. The Renderpeople FBX in the separate web handoff cannot be copied into Android unchanged: Filament requires glTF/GLB, and commercial redistribution/conversion must be license-compliant. The production next step is to provide licensed realistic GLB characters, clothing, weapons and combat clips, then replace the single asset path in `HumanAvatar3D.kt` with the validated asset registry.

Ten independently authored Quaternius CC0 GLBs are now integrated in the active avatar slots. Leo has one skin and eight clips from `Animated Human Low Poly`; Sofia plus Maya, Amara, Elena, Nadia, Mateo, Karim, Daniel and Isaac use independently authored entries from the `Ultimate Animated Character Pack`, with one skin and eleven clips each, including `CharacterArmature|Idle`. Their source URLs, conversions, exact hashes and license records are in `docs/3d-assets/<id>.json` and the license manifest. Static GLB QA passes, but device/emulator playback, visual distinction and premium visual acceptance remain pending. Renderpeople free downloads were not integrated because their current terms prohibit redistribution/disclosure or easy extraction of the individual 3D data. Do not call the premium 3D art deliverable complete until Android playback, visual distinction and quality acceptance are tested.
