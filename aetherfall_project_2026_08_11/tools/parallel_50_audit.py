from concurrent.futures import ThreadPoolExecutor, as_completed
from pathlib import Path
import re
import urllib.request
import json
import xml.etree.ElementTree as ET

ROOT = Path('.')

def text(path):
    return Path(path).read_text(errors='ignore')

def exists(path):
    return lambda: (Path(path).exists(), f'exists {path}')

def contains(source, needle, label):
    return lambda: (needle in source, label)

def xml_ok(path):
    def check():
        try:
            ET.parse(path)
            return True, f'xml {path}'
        except Exception as exc:
            return False, f'xml {path}: {exc}'
    return check

main_source = '\n'.join(p.read_text(errors='ignore') for p in Path('app/src/main').rglob('*') if p.is_file())
repo_source = '\n'.join(
    p.read_text(errors='ignore') for p in Path('.').rglob('*')
    if p.is_file() and not any(part in {'.git', '.gradle', '.toolchains', 'build', 'outputs', '__pycache__'} for part in p.parts)
)
manifest = text('app/src/main/AndroidManifest.xml')
gradle = text('app/build.gradle.kts')
database = text('app/src/main/java/com/aistudio/empireofkings/game/data/EmpireDatabase.kt')
repository = text('app/src/main/java/com/aistudio/empireofkings/game/data/EmpireRepository.kt')
socket = text('app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt')
battle = text('app/src/main/java/com/aistudio/empireofkings/game/ui/screens/BattleRoyaleScreen.kt')
app = text('app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireApp.kt')

# Exactly 50 independent checks.
tasks = [
    exists('app/build.gradle.kts'),
    exists('settings.gradle.kts'),
    exists('gradlew'),
    lambda: (Path('gradlew').stat().st_mode & 0o111 != 0, 'gradlew executable'),
    xml_ok('app/src/main/AndroidManifest.xml'),
    xml_ok('app/src/main/res/values/strings.xml'),
    xml_ok('app/src/main/res/values/themes.xml'),
    xml_ok('app/src/main/res/values/colors.xml'),
    xml_ok('app/src/main/res/xml/backup_rules.xml'),
    xml_ok('app/src/main/res/xml/data_extraction_rules.xml'),
    xml_ok('app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml'),
    xml_ok('app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml'),
    contains(main_source, 'sealed class ScreenRoute', 'ScreenRoute sealed class'),
    contains(main_source, 'ScreenRoute.Battle', 'Battle route'),
    contains(app, 'is ScreenRoute.SettingsAdmin', 'settings route wired'),
    contains(text('app/src/main/java/com/aistudio/empireofkings/game/ui/components/BottomNavBar.kt'), 'ScreenRoute.Clan', 'clan nav wired'),
    exists('app/src/main/java/com/aistudio/empireofkings/game/data/EmpireDao.kt'),
    exists('app/src/main/java/com/aistudio/empireofkings/game/data/EmpireDatabase.kt'),
    contains(database, 'version = 5', 'Room v5'),
    contains(database, 'Migration(1, 2)', 'migration 1-2'),
    contains(database, 'Migration(2, 3)', 'migration 2-3'),
    contains(database, 'Migration(3, 4)', 'migration 3-4'),
    contains(database, 'Migration(4, 5)', 'migration 4-5'),
    lambda: ('fallbackToDestructiveMigration' not in database, 'no destructive Room fallback'),
    contains(database, 'PaymentTransaction::class', 'payment entity registered'),
    contains(repository, 'wpn_tormenta_cristal', 'crystal weapon seeded'),
    contains(repository, 'wpn_ojo_leon', 'lion weapon seeded'),
    contains(repository, 'wpn_baston_flama_azul', 'blue flame staff seeded'),
    contains(repository, 'initialWardrobeItems', 'wardrobe seed'),
    contains(text('app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireRemoteClient.kt'), 'https://empire-of-kings-server.onrender.com/', 'HTTPS backend'),
    contains(socket, 'reconnectionAttempts = 5', 'bounded socket retries'),
    lambda: (all('http://' not in p.read_text(errors='ignore') for p in Path('app/src/main/java').rglob('*.kt')), 'no plaintext HTTP in Kotlin'),
    lambda: (not re.search(r'BEGIN (?:RSA |EC |OPENSSH )?PRIVATE KEY|AIza[0-9A-Za-z_-]{20,}|sk_live_[0-9A-Za-z]+', main_source), 'no private API secrets'),
    contains(gradle, 'EMPIRE_RELEASE_STORE_FILE', 'release signing env'),
    lambda: (not Path('app/release.keystore').exists() and not Path('debug.keystore').exists(), 'no keystore committed'),
    contains(manifest, 'android.permission.INTERNET', 'internet permission'),
    contains(manifest, 'android:exported="true"', 'launcher exported'),
    contains(gradle, 'minSdk = 24', 'min sdk'),
    contains(gradle, 'targetSdk = 36', 'target sdk'),
    contains(gradle, 'buildFeatures {\n    compose = true', 'Compose enabled'),
    contains(gradle, 'google.devtools.ksp', 'KSP plugin'),
    lambda: (len(list(Path('app/src/test').rglob('*Test.kt'))) >= 1, 'unit tests present'),
    lambda: (len(list(Path('app/src/androidTest').rglob('*Test.kt'))) >= 1, 'instrumented tests present'),
    exists('tools/qa_100.py'),
    exists('tools/qa_400.py'),
    contains(repository, 'CHAT_CHANNELS', 'chat allowlist'),
    contains(socket, 'protocolActions', 'socket inbound allowlist'),
    contains(battle, 'toRemoveProj', 'projectile cleanup'),
    contains(battle, 'wasAlive', 'duplicate kill guard'),
    lambda: ((lambda response: (response.status == 200 and json.loads(response.read()).get('ok') is True))(urllib.request.urlopen('https://empire-of-kings-server.onrender.com/health', timeout=20)), 'server health 200 ok'),
]

assert len(tasks) == 50, len(tasks)
results = []
with ThreadPoolExecutor(max_workers=50) as executor:
    futures = {executor.submit(task): index + 1 for index, task in enumerate(tasks)}
    for future in as_completed(futures):
        index = futures[future]
        try:
            ok, label = future.result()
        except Exception as exc:
            ok, label = False, f'task {index}: {type(exc).__name__}: {exc}'
        results.append((index, ok, label))

for index, ok, label in sorted(results):
    print(f'{index:02d} {"PASS" if ok else "FAIL"} {label}')
passed = sum(ok for _, ok, _ in results)
print(f'\nTOTAL tasks=50 passed={passed} failed={50 - passed} parallel_workers=50')
if passed != 50:
    raise SystemExit(1)
