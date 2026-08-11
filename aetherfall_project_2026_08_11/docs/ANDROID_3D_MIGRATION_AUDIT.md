# Android 3D migration audit

Date: 2026-08-10

## Evidence reviewed

The 44 MB archive received through the user's WhatsApp self-chat was extracted under `uploaded-app-review/`. It is a source handoff, not an Android APK. It contains three separate lines of work:

- `proyecto-empire/`: web/Three.js multiplayer client and server.
- `empire-of-kings-app/`: the místico web prototype and 2D reference assets.
- `legacy/`: older server/prototype material.

## 3D assets found in the uploaded handoff

- `proyecto-empire/public/models/renderpeople/rp_sophia_animated_003_idling.fbx` — human model with skeleton, textures and an idle animation.
- `proyecto-empire/public/models/renderpeople/tex/*` — model textures.
- `proyecto-empire/public/models/Xbot.glb` — humanoid fallback with test animation clips.
- `proyecto-empire/public/game3d.js` — Three.js renderer, FBX/GLTF loaders, skeleton cloning and animation bank.

The handoff documentation explicitly says the final 12-avatar cast, interchangeable clothes, weapons, accessories and complete combat animation bank are not included yet.

## Android project status

The Android project now bundles `app/src/main/assets/models/Xbot.glb` and uses the native Filament/SceneView Compose renderer behind `HumanAvatar3D.kt`. `Mystic3DBackground.kt` remains a separate Compose `Canvas` effect (particles, gradients and runes), not the avatar renderer. Profile, auth selection, top bar, wardrobe preview and disco preview now use the native GLB surface; the saved preset is also sent to the online handshake.

## Safe migration decision

Do not copy the web server or redesign the Android UI. The Android migration must be isolated behind the existing avatar surfaces and preserve the current navigation and visual language.

The web implementation is useful as a behavioral reference, but its FBX model cannot be assumed to load natively on Android. Android should receive a validated GLB/glTF asset and a native renderer. The included `Xbot.glb` is suitable only as a pipeline smoke-test asset; it is not the requested premium realistic cast. The Renderpeople FBX needs a documented, license-compliant conversion to GLB before production use.

## Release gate

The Android beta must not be called complete until the native renderer loads a real human GLB on a device/emulator, the selected preset persists, the model appears once per selection card, and the asset/license inventory is recorded. No visual redesign is authorized by this audit.
