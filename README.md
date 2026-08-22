# CC: Tablet (Forge 1.20.1)

A deliberately small CC:Tweaked addon which adds one **Advanced Tablet Computer**: a normal Advanced Pocket Computer with a wider **40×20** terminal.

## Target versions

- Minecraft 1.20.1
- Forge 47.4.10 or newer 47.4.x
- CC:Tweaked 1.120.2
- Java 17
- Install the built JAR on **both the dedicated server and every client**.

This mod intentionally uses CC:Tweaked implementation classes (not just its stable public API), because CC:T does not expose an API for defining a new pocket-computer terminal size per item. It is therefore pinned tightly to CC:Tweaked 1.120.2.

## What it does

- Adds `cctablet:tablet` / **Advanced Tablet Computer**.
- Terminal size is **40 columns × 20 rows**.
- Inherits CC:Tweaked's normal pocket-computer lifecycle, CraftOS, IDs, filesystem, HTTP, rednet APIs, terminal networking, touch/mouse events, and live first-person rendering.
- Uses CC:Tweaked's normal **single pocket-upgrade slot** on Minecraft 1.20.1.
- Adds a **built-in speaker peripheral** on the tablet's `right` side, while keeping the ordinary pocket-upgrade slot free for any normal pocket upgrade.
- The normal CC:T pocket upgrade recipe works automatically because the tablet is a `PocketComputerItem` subclass. Put an **Advanced Wireless Modem (Ender Modem)** above it in a crafting grid just like a normal pocket computer.

## Crafting the tablet

The conversion recipe is custom and preserves data:

- 1× Advanced Pocket Computer
- 2× Gold Ingots

The recipe uses CC:Tweaked's `changeItem` path, so it preserves the source pocket computer's **computer ID, filesystem, label, colour and installed pocket upgrade**.

Because this is a custom data-preserving crafting recipe, JEI may not automatically display it like a normal vanilla recipe unless explicit JEI integration is added.

## Built-in speaker

The built-in speaker is attached on the tablet's `right` side, so you can use it with:

```lua
local speaker = peripheral.find("speaker")
```

or:

```lua
local speaker = peripheral.wrap("right")
```

It uses CC:Tweaked's real pocket-speaker implementation, so it supports the normal speaker API including `playNote`, `playSound`, `playAudio`, and `stop`, with CC:Tweaked's normal buffering and lifecycle handling.

## Building

Use **Gradle 8.1.1** with **Java 17**.

If needed, generate/update the wrapper with:

```bash
gradle wrapper --gradle-version 8.1.1
```

Then build with:

```bash
gradlew build
```

The reobfuscated JAR will be produced under:

```text
build/libs/cctablet-0.2.1.jar
```

## Why there is a mixin

CC:Tweaked ultimately constructs each server computer's `NetworkedTerminal` from the terminal dimensions in its server-computer properties. Pocket computers normally feed the globally configured pocket size into those properties. The tablet keeps all normal pocket code and uses one small mixin on `PocketServerComputer` to substitute **40×20** only while a `TabletComputerItem` is synchronously creating its server-side brain.

The actual GUI and held-item renderer do not need to be replaced: CC:Tweaked reads the terminal's transmitted dimensions and renders the frame around whatever size it receives.

## 0.2.0

- Added a built-in **speaker** peripheral while keeping the normal single pocket-upgrade slot.
- Updated the tablet item texture to look more like a wider CC pocket computer.
- Keeps the stable `@Redirect`-based terminal-size mixin used by the working 0.1.2 build.


## 0.2.1

- Replaced the hand-written tablet speaker with CC:Tweaked's real `PocketSpeakerPeripheral`.
- Restores normal speaker semantics, DFPWM `playAudio`, real `stop()`, CC:Tweaked rate limiting, and normal attach/detach cleanup.
- Made the tablet-construction `ThreadLocal` lazy so normal pocket computers do not leave a zero-value entry on the long-lived server thread.
- Made recipe matching allocation-free; NBT copying now only happens when the recipe is actually assembled.
