# Smart Lights Control

A single-screen Android prototype that reproduces the **Manage Lights** widget and **Light Drawer** bottom sheet from the Figma specification. It demonstrates Clean Architecture, unidirectional MVI state management, optimistic UI with rollback, and a fully testable mock service layer.

---

## 1. Overview & Setup

### Prerequisites

| Tool | Minimum version |
|---|---|
| Android Studio | Meerkat (2024.3) or later |
| JDK | 17 (project toolchain enforces `jvmTarget = 11`) |
| Android SDK | API 26 (`minSdk`) — API 37 (`targetSdk`) |

### Build & run

```bash
./gradlew assembleDebug     # build debug APK
./gradlew installDebug      # install on connected device/emulator
```

### Run all tests

```bash
./gradlew testDebugUnitTest
```

Runs the full suite — pure domain unit tests, MVI ViewModel tests, and Robolectric Compose UI interaction tests — without an emulator.

---

## 2. Architecture & State Management

### Module layers

```
app/src/main/java/com/smartlights/control/
├── domain/
│   ├── model/Light.kt             # Pure Kotlin; zero Android imports
│   └── repository/LightRepository.kt
├── data/
│   ├── service/LightService.kt    # Interface + LightServiceException
│   ├── service/MockLightService.kt
│   └── repository/LightRepositoryImpl.kt
├── presentation/
│   ├── mvi/LightsContract.kt      # LightsUiState, LightsIntent, ErrorBanner
│   ├── viewmodel/LightsViewModel.kt
│   └── ui/
│       ├── HomeRoute.kt           # Stateful: collects ViewModel state
│       ├── HomeScreen.kt          # Stateless, previewable
│       └── components/
└── di/AppContainer.kt
```

### Unidirectional MVI

`LightsViewModel` is the **single source of truth** for both the Home widget and the Light Drawer. Both composables observe the same `uiState` `StateFlow`; closing the drawer never requires manual state synchronisation.

### Optimistic UI & rollback

1. `TogglePower` / `UpdateBrightness` immediately applies the change to `uiState`.
2. The service call runs concurrently.
3. **Success:** returned server state replaces the optimistic value.
4. **Failure:** pre-action snapshot is restored; `ErrorBanner("Something went wrong", "Try again later")` is emitted.

### Job-based brightness debouncing (250 ms)

Each `UpdateBrightness` intent updates `uiState` immediately, cancels any pending `brightnessDebounceJobs[lightId]`, and schedules a new coroutine with `delay(250)`. Only the coroutine that survives calls `repository.setBrightness(...)`.

---

## 3. Mock Service & Failure Rate

`MockLightService` simulates a device backend with configurable latency (`300–800 ms`) and failure rate.

### Seeded devices

| ID | Name | Dimmable | Initial state |
|---|---|---|---|
| `bed_light` | Bed light | ✓ | On, 75% |
| `all_lights` | All the lights in the house | ✓ | On, 100% |
| `porch_light` | Porch light | ✗ | Off |

### Adjusting `failureRate` (default `0.10` / 10%)

Edit `di/AppContainer.kt`:

```kotlin
val mockLightService = MockLightService(
    failureRate = 0.10   // 0.0 = never fail, 1.0 = always fail
)
```

---

## 4. Trade-Offs & Future Extensions

**Deliberate scope choices:**
- Static placeholders for out-of-scope Home cards (Blinds, Night time, Camera, Assistant).
- In-memory mock service (`MutableMap` + `Mutex`); no persistence or real network.
- Single Activity; drawer is a `ModalBottomSheet` owned by `HomeScreen`.

**Extending to production:**

| Concern | Extension path |
|---|---|
| Real-time telemetry | Replace `MockLightService` with a WebSocket/MQTT impl; ViewModel and UI unchanged. |
| Multi-room lists | Add `Room` domain model and `selectedRoomId` to `LightsUiState`. |
| Offline-first caching | Room database in `data/`; repository becomes a write-ahead sync layer. |
| Dependency injection | Swap `AppContainer` for Hilt modules without touching `domain/` or `presentation/`. |
| Propject structure | Scaling the app to multi-room or multi-device flows, it would be recommended to split `presentation/ui` into feature packages. |

---

## 5. AI Tooling & Engineering Workflow

Built with **Cline** (VS Code extension) driven by **Claude Sonnet** via OpenRouter, guided by `AGENTS.md` across incremental milestones: domain model → mock service → MVI contract → ViewModel → Compose UI → test suite.

**Human corrections during review:**

- **Extracted `HomeRoute.kt`** — AI initially mixed ViewModel collection with the stateless screen composable. Separating enforces the MVI rule of never passing a `ViewModel` into a screen composable and keeps `@Preview` functions dependency-free.
- **Removed duplicate template theme files** — scaffolding generated redundant stubs; deleted to avoid import ambiguity.
- **Replaced Canvas graphics with drawable assets** — AI drew the house illustration with `Canvas`; replaced with `Image(painterResource(...))` using existing `res/drawable*` assets as required by `AGENTS.md`.
