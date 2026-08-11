# UI Reference Lock — LELO / EMPIRE OF KINGS

**Status:** authoritative design constraint
**Reference image:** `docs/reference/ui-reference-master.jpg`

## Do not change arbitrarily

The original visual and functional structure shown in the reference image is the source of truth. Do not replace it with a generic dashboard, a web mockup, or a new lobby composition.

Preserve:

- dark blue/black imperial fantasy background;
- thin gold frames, borders, and premium game-panel styling;
- compact mobile portrait composition;
- one avatar/character per character view;
- original hierarchy, panels, labels, icons, and bottom navigation;
- existing categories and settings;
- navigation and visible interaction model.

## Reference screens

1. Splash / start
2. Login / registration
3. Main lobby
4. Shop
5. Inventory
6. Wardrobe
7. Profile
8. Imperial Disco
9. Games
10. Battle Royale
11. Chat
12. Friends
13. Royal Pass
14. Weapons / upgrade weapon
15. Recharge
16. Settings

## Bottom navigation

Lobby, Shop, Inventory, Wardrobe, Profile, Disco, Clan, Games, Settings.

## Implementation rule

New systems must be integrated inside this visual language. Internal implementation may be refactored when necessary, but the visible result must respect the reference. Every important visible action must be wired to real logic, data, persistence, and feedback; no decorative-only controls.

## Completion constraints

Use Kotlin, Jetpack Compose, ViewModel, and Room. Preserve persistence, economy, inventory, equipment, navigation, social systems, combat, rewards, 3D fallback architecture, sound settings, validation, optimization, and test coverage described in the master prompt. Do not call the app complete until compilation, tests, persistence, and critical flows are verified.
