# AGENTS.md — Smart Lights Control Prototype

## Project Scope & Context
Single-screen Android prototype reproducing the **Manage Lights** widget on the Home screen and the **Light Drawer** bottom sheet from the Figma specification.
- **In Scope:** Home light widget (toggle, live brightness swipe, drawer trigger), Light Drawer (toggle, 0–100% slider, non-dimmable state), optimistic UI with error rollback, mock service layer, and automated tests.
- **Out of Scope:** Real hardware/network calls, authentication, blinds, scenes, and assistant chat (render non-light Home cards strictly as static placeholders).

## Tech Stack & Architecture
- **Language / UI:** Kotlin, Jetpack Compose (Material 3), Coroutines & `StateFlow`.
- **Architecture:** Clean Architecture (`domain`, `data`, `presentation`) with strict unidirectional **MVI**.
  - `domain/`: Pure Kotlin models (`Light`), repository interface, and business rules. Zero Android framework imports.
  - `data/`: `LightService` interface, `MockLightService` (300–800 ms simulated latency, configurable failure rate defaulting to `0.10f`), and repository implementation.
  - `presentation/`: Single `LightsViewModel` acting as the single source of truth for both the Home widget and Light Drawer, exposing immutable `LightsUiState`, `LightsIntent`, and one-off `LightsEffect`.

## Core Product & State Invariants
1. **Single Source of Truth:** Widget and Drawer must observe the exact same state flow; closing the drawer never requires manual state syncing.
2. **Optimistic UI & Rollback:** Apply power and brightness changes immediately in UI state. If `LightService` throws/fails, revert to the pre-action snapshot and emit `"Something went wrong. Try again later"`.
3. **Power & Brightness Memory:**
   - Tapping an **off** dimmable light turns it **on** and restores `lastBrightness` (e.g., `"On. 75%"`).
   - Tapping an **on** light turns it **off** (`"Off. 0%"`) while preserving `lastBrightness`.
   - Widget primary tap action always offers the opposite of the light's current power state.
4. **Dimmable vs. Non-Dimmable:**
   - Dimmable lights display brightness percentage on the widget and a 0–100% slider in the drawer.
   - Non-dimmable lights show only `"On"` / `"Off"` on the widget (no percentage) and replace the drawer slider with `"This is a non-dimmable light"`.
5. **Debounced Brightness:** Update UI fill and percentage live during horizontal widget swipe or drawer slider drag, but debounce/throttle `setBrightness` service calls so intermediate drag values do not flood the service.

## UI, Accessibility & Code Standards
- **Touch Targets & Semantics:** Enforce `Modifier.defaultMinSize(minWidth = 44.dp, minHeight = 44.dp)` and clear `semantics` / `contentDescription` on the widget, toggle buttons, drawer trigger, and brightness slider.
- **Clean Code:** Keep composables stateless and previewable; hoist all state and intents to `LightsViewModel`. No dead code or placeholder TODOs.
- **Pure MVI State Hoisting:** Never pass a `ViewModel` into screen or component composables. Screen composables must accept immutable `uiState` and `onIntent: (Intent) -> Unit` parameters, wrapped by a thin `*Route` composable at the Activity/navigation boundary.
- **Mandatory Previews:** Every `@Composable` component and screen file must include at least one `@Preview` function wrapped in `SmartLightsTheme` with realistic sample state.
- **Route / Screen File Separation:** Place stateful `*Route` composables (which collect `ViewModel` state) in their own dedicated `*Route.kt` file, completely separate from stateless `*Screen.kt` files.
- **Use Project Drawables:** Never generate custom `Canvas` illustrations when image assets exist in `res/drawable*`; render them with `Image(painterResource(...))`.

## Execution & Token-Efficiency Protocol
- **Scoped Edits:** Modify only the files explicitly requested in the current task. Do not scan unrelated directories or rewrite working modules.
- **Verification Command:** Run `./gradlew testDebugUnitTest` after logic or state changes and fix any compilation/test failures before completing the task.
- **Concise Responses:** Summarize changes in brief bullet points; omit lengthy explanations.

## Git Commit Protocol
- **Explicit Trigger Only:** NEVER stage (`git add`) or commit (`git commit`) changes automatically. Execute a commit only when explicitly instructed by the user after diff and test verification.
- **Atomic Scope:** Keep commits small and single-purpose, aligned with architectural boundaries (`domain`, `data`, `mvi`, `ui`, `test`).
- **Commit Message Format (Conventional Commits):**
  - **Header:** `<type>(<scope>): <imperative summary>` (max 72 chars; allowed types: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`).
  - **Body:** Provide a clear, structured explanation covering:
    1. **What** changed across modules or components.
    2. **Why** it was structured that way (architectural rationale, state invariants, UX rules, or edge cases handled).
  - **Example:**
    ```text
    feat(mvi): implement optimistic state transitions and rollback in LightsViewModel

    - Define immutable LightsUiState and LightsIntent as single source of truth for widget and drawer
    - Apply optimistic power toggles with lastBrightness memory and pre-action snapshot rollback
    - Debounce brightness service calls (250ms) while preserving live UI progress updates
    ```
- **No AI Attribution:** Strictly omit `Co-Authored-By` trailers, `Generated with` footers, or any mention of AI tools in commit headers, bodies, or metadata.