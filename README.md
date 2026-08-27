# CC: Tablet (Forge 1.20.1)

A CC:Tweaked addon for Forge 1.20.1 which adds two portable Advanced computers:

- **Advanced Tablet Computer** — a wider handheld computer with a **40×20** terminal.
- **Advanced Smartwatch Computer** — a wrist-mounted computer with an **18×10** terminal, Curios equipment slot, built-in locator, built-in speaker, and dedicated watch-style GUI.

Both devices keep CC:Tweaked's normal CraftOS environment and pocket-computer functionality while adding their own hardware and presentation.

## Target versions

- Minecraft 1.20.1
- Forge 47.4.10 or newer 47.4.x
- CC:Tweaked 1.120.2
- Curios 5.14.1 or newer 5.x
- Java 17
- Install the built JAR on **both the dedicated server and every client**.

This mod intentionally uses CC:Tweaked implementation classes in addition to its public API. CC:Tweaked does not expose all of the hooks required for per-item pocket terminal sizes and the smartwatch presentation, so CC:Tablet is pinned tightly to CC:Tweaked 1.120.2.

## Devices

### Advanced Tablet Computer

The tablet is a larger Advanced Pocket Computer intended for programs which benefit from more screen space.

- Item ID: `cctablet:tablet`
- Terminal size: **40 columns × 20 rows**
- Computer family: **Advanced**
- Built-in speaker on the `right` side
- Normal removable pocket upgrade remains available on the back
- Uses CC:Tweaked's normal pocket-computer lifecycle, CraftOS, IDs, filesystem, HTTP, rednet APIs, terminal networking, mouse/touch events, and upgrade system

The normal CC:Tweaked pocket upgrade recipe works because the tablet is a `PocketComputerItem` subclass. For example, an **Advanced Wireless Modem (Ender Modem)** can be installed using the normal pocket-computer upgrade recipe.

### Advanced Smartwatch Computer

The smartwatch is a small wearable Advanced computer designed to remain equipped while in use.

- Item ID: `cctablet:smartwatch`
- Terminal size: **18 columns × 10 rows**
- Computer family: **Advanced**
- Equipped in a dedicated Curios `watch` slot
- Built-in locator on the `left` side
- Built-in speaker on the `right` side
- Normal removable pocket upgrade remains available on the back
- Keeps running and ticking while equipped
- Press **V** by default to open the equipped smartwatch
- Uses a dedicated smartwatch-shaped GUI instead of CC:Tweaked's normal pocket-computer frame
- Uses a dedicated wrist renderer when equipped

The smartwatch GUI frame is stored as an editable texture at:

```text
assets/cctablet/textures/gui/smartwatch.png
```

The live CC:Tweaked terminal is rendered on top of that texture, so changing the PNG changes the watch body without changing the terminal implementation.

## Crafting

### Crafting the tablet

The tablet conversion recipe uses:

- 1× Advanced Pocket Computer
- 2× Gold Ingots

The recipe uses CC:Tweaked's `changeItem` path, so it preserves the source pocket computer's **computer ID, filesystem, label, colour, installed pocket upgrade, and powered-on state**.

Because this is a custom data-preserving crafting recipe, JEI may not automatically display it like a normal vanilla recipe unless explicit JEI integration is added.

### Crafting the smartwatch

The smartwatch is crafted directly and does **not** consume a pocket computer.

```text
G G G
G C G
G G G
```

Where:

- `G` = Gold Nugget
- `C` = Clock

This creates one fresh **Advanced Smartwatch Computer**.

## Built-in speaker

Both the tablet and smartwatch have a built-in CC:Tweaked pocket speaker on the computer's `right` side.

You can find it normally:

```lua
local speaker = peripheral.find("speaker")
```

or address it directly:

```lua
local speaker = peripheral.wrap("right")
```

The speaker uses CC:Tweaked's real `PocketSpeakerPeripheral`, so it supports the normal speaker API including `playNote`, `playSound`, `playAudio`, and `stop`, with CC:Tweaked's normal buffering and lifecycle behaviour.

The removable pocket-upgrade slot is still available separately.

## Smartwatch locator

The smartwatch includes a built-in `locator` peripheral on its `left` side. It reports the live world position of the wearer while the watch is equipped and does not require GPS towers.

Get the peripheral with:

```lua
local locator = peripheral.wrap("left")
```

### `getPosition()`

Returns the precise X, Y, and Z coordinates of the wearer/device.

```lua
local x, y, z = locator.getPosition()
print(x, y, z)
```

### `getBlockPosition()`

Returns integer block coordinates.

```lua
local x, y, z = locator.getBlockPosition()
print(x, y, z)
```

### `getDimension()`

Returns the current dimension ID.

```lua
print(locator.getDimension())
-- Example: minecraft:overworld
```

### `getFacing()`

Returns the wearer's horizontal facing direction.

```lua
print(locator.getFacing())
```

### `getRotation()`

Returns yaw and pitch in degrees.

```lua
local yaw, pitch = locator.getRotation()
print(yaw, pitch)
```

### `locate()`

Returns all locator information in one Lua table.

```lua
local info = locator.locate()

print(info.x, info.y, info.z)
print(info.blockX, info.blockY, info.blockZ)
print(info.dimension)
print(info.facing)
print(info.yaw, info.pitch)
```

The table contains:

```text
x
y
z
blockX
blockY
blockZ
dimension
facing
yaw
pitch
```

## Smartwatch controls

When the smartwatch is equipped in the Curios `watch` slot, press:

```text
V
```

to open it without removing it from your wrist.

The keybind appears in Minecraft's Controls menu under the **CC:Tablet** category and can be rebound normally.

## Curios integration

Curios is required for the smartwatch equipment slot.

The mod registers a dedicated:

```text
watch
```

slot for players and assigns `cctablet:smartwatch` to it.

Internally, CC:Tablet bridges the Curios `watch[0]` stack into CC:Tweaked's pocket-computer holder system so the computer can remain alive, synchronized, and usable while outside the vanilla player inventory.

## Peripheral layout

### Tablet

```text
RIGHT = built-in speaker
BACK  = removable pocket upgrade
```

### Smartwatch

```text
LEFT  = built-in locator
RIGHT = built-in speaker
BACK  = removable pocket upgrade
```

## Building

Use **Gradle 8.1.1** with **Java 17**.

If needed, generate/update the wrapper with:

```bash
gradle wrapper --gradle-version 8.1.1
```

The build requires Curios on the compile/runtime classpath. With CurseMaven configured, the dependency is:

```gradle
implementation fg.deobf("curse.maven:curios-309927:6418456")
```

Then build with:

```bash
gradlew build
```

or on Windows:

```bat
gradlew.bat clean build
```

The reobfuscated JAR will be produced under:

```text
build/libs/cctablet-(VERSION).jar
```

## Why there are mixins

CC:Tweaked normally creates all pocket computers with the configured pocket terminal dimensions and assumes player-held pocket computers live in vanilla inventory slots. It also applies its own map-like rendering path to pocket computer items.

CC:Tablet uses small targeted mixins to extend those assumptions without replacing CC:Tweaked's computer implementation:

- **PocketServerComputer mixin** — selects **40×20** for tablets and **18×10** for smartwatches when the server computer is created.
- **PocketItemRenderer mixin** — provides the correct fallback terminal dimensions before synchronized terminal data is available.
- **PocketHolder.PlayerHolder bridge** — redirects the smartwatch's synthetic holder slot to Curios `watch[0]`.
- **Client rendering hook** — prevents the smartwatch from being forced through CC:Tweaked's normal two-handed/map-like pocket-computer item presentation.

The smartwatch then uses CC:Tweaked's normal terminal widget and computer input handling inside a custom watch-shaped screen, preserving normal keyboard, mouse, terminal, and upload behaviour.

## Changelog

### 0.2.3

- Added the **Advanced Smartwatch Computer**.
- Added a dedicated Curios `watch` equipment slot.
- Added an **18×10** smartwatch terminal.
- Added a default **V** keybind for opening the equipped smartwatch.
- Added a custom smartwatch GUI and editable `textures/gui/smartwatch.png` frame.
- Added a dedicated smartwatch wrist renderer.
- Added a built-in `locator` peripheral on the smartwatch's `left` side.
- Added precise position, block position, dimension, facing, yaw, and pitch locator methods.
- Added a built-in speaker on the smartwatch's `right` side.
- Kept the standard removable pocket-upgrade slot available on the smartwatch's back.
- Added a direct crafting recipe using a clock surrounded by eight gold nuggets.
- Added the Curios-to-CC:Tweaked holder bridge so the smartwatch continues running while equipped.

### 0.2.2

- Lowkey forgot


### 0.2.1

- Replaced the hand-written tablet speaker with CC:Tweaked's real `PocketSpeakerPeripheral`.
- Restored normal speaker semantics, DFPWM `playAudio`, real `stop()`, CC:Tweaked rate limiting, and normal attach/detach cleanup.
- Made recipe matching allocation-free; NBT copying now only happens when the tablet recipe is actually assembled.

### 0.2.0

- Added a built-in **speaker** peripheral to the tablet while keeping the normal single pocket-upgrade slot.
- Updated the tablet item texture to look more like a wider CC pocket computer.
- Kept the stable `@Redirect`-based terminal-size mixin used by the working 0.1.2 build.
