# CC:Tablet (forge 1.20.1)

A CC:Tweaked addon for Forge 1.20.1 that adds several various things:

- **Advanced tablet computer**: a wider handheld computer with a **40×20** terminal.
- **Advanced smartwatch computer**: a wrist computer with an **18×10** terminal, Curios slot, built-in locator and speaker, plus its own watch-style screen.
- **Hard Drives**: a storage solution holding more than the standart floppy disk.
- **Universal Modem**: A modem that can act as both an Ender modem and a Basic modem.

## Target Versions

- Minecraft 1.20.1
- Forge 47.4.10 or newer 47.4.x
- CC:Tweaked 1.120.2
- Curios 5.14.1 or newer 5.x
- Java 17
- Install the built JAR on the dedicated server and every client

This mod uses some CC:Tweaked implementation classes as well as its public API. The public API doesn't have all the hooks needed for per-item terminal sizes and the watch screen, so this version is tied pretty closely to CC:Tweaked 1.120.2.

## Devices

### Advanced Tablet

The Tablet is a bigger Advanced Pocket Computer for programs that want a bit more screen space.

- Item ID: `cctablet:tablet`
- Terminal size: **40 columns × 20 rows**
- Built-in speaker on the `right` side
- Normal removable pocket upgrade remains available on the back
- Just like a normal advanced pocket computer but wider. every function the advanced pocket computer has, the tablet has aswell

### Advanced Smartwatch

The smartwatch is a small wearable advanced computer that you carry on your wrist (curio slot)

- Item ID: `cctablet:smartwatch`
- Terminal size: **18 columns × 10 rows**
- Equipped in a dedicated Curios `watch` slot
- Built-in locator on the `left` side
- Built-in speaker on the `right` side
- Normal removable pocket upgrade remains available on the back
- Keeps running while equipped
- Press **V** by default to open it (can be changed in the keybind settings)
- Has its own watch-shaped screen instead of the regular pocket-computer frame


## Hard Drives

Insert HDD items into the two-slot HDD Case block.
Breaking the case drops its installed HDDs.

The case exposes an `hdd_case` peripheral through CC:Tweaked's peripheral
capability, so it connects through wired network cables. Its two filesystems are
mounted on connected computers at `/hdd1` and `/hdd2`. HDDs do not work in the
regular CC:Tweaked disk drive.

HDD1 through HDD4 each have their own writable filesystem in the world's save
data. Their labels stay with the item and can be set at an anvil or from a
connected computer with `hdd_case.setLabel(slot, label)`.

- `cctablet:harddrive1` has **1 MiB** by default.
- `cctablet:harddrive2` has **2 MiB** by default.
- `cctablet:harddrive3` has **4 MiB** by default.
- `cctablet:harddrive4` has **8 MiB** by default.
- `cctablet:harddrive5` is a shared, read-only program drive with no private storage.

Capacities for HDD1 through HDD4 are configurable in `config/cctablet-common.toml`.
Each drive saves its configured capacity the first time it is mounted. Changing
the config later does not resize drives that already have a saved capacity.

On first launch, the mod creates `config/cctablet/hdd5/programs/`. Every HDD5
mounts this folder directly and read-only. Adding a file makes it appear on all
HDD5s, and removing a file removes it from all HDD5s. HDD5 has no private
filesystem and cannot be written to from a computer.

The mod copies `cctablet_test.lua` into that folder on first launch. Run it from
the drive's mount, for example `hdd1/cctablet_test.lua`. It checks terminal size,
speaker and locator peripherals, HDD mounts and write access, and the universal
modem's Ender methods. It creates and removes a small probe file on writable
HDDs, but does not change modem settings. You can remove the test program from
the config folder if you do not need it.

## Crafting

### Crafting the Tablet

To convert a pocket computer into a tablet, use:

- 1× Advanced Pocket Computer
- 2× Gold Ingots

The recipe uses CC:Tweaked's `changeItem` method to keep the computer's **ID, filesystem, label, colour, installed upgrade, and powered-on state**.

The recipe is shapeless. Put the Advanced Pocket Computer and two Gold Ingots in any three separate slots. It appears in JEI and unlocks in the recipe book when you obtain an Advanced Pocket Computer. Smartwatches and tablets are not valid inputs.

### Crafting the Smartwatch

The smartwatch is crafted directly, and does **not** use up a pocket computer.

```text
G G G
G C G
G G G
```

Where:

- `G` = Gold Nugget
- `C` = Clock

This makes a fresh **Advanced Smartwatch Computer**.

## built-in speaker

Both devices have a built-in CC:Tweaked pocket speaker on the computer's `right` side.

You can find it normally:

```lua
local speaker = peripheral.find("speaker")
```

or address it directly:

```lua
local speaker = peripheral.wrap("right")
```

It uses CC:Tweaked's `PocketSpeakerPeripheral`, so the standard `playNote`, `playSound`, `playAudio`, and `stop` calls are available.

The removable pocket-upgrade slot is still available separately.

## Smartwatch Locator

The smartwatch has a built-in `locator` peripheral on the `left`. It reports the wearer's world position while equipped and does not require GPS towers.

Use `peripheral.find("locator").getPosition()` 

Get the peripheral with:

```lua
local locator = peripheral.wrap("left")
```

### `getPosition()`

Returns the wearer's precise X, Y, and Z coordinates.

```lua
local x, y, z = locator.getPosition()
print(x, y, z)
```

### `getBlockPosition()`

Returns whole block coordinates.

```lua
local x, y, z = locator.getBlockPosition()
print(x, y, z)
```

### `getDimension()`

Returns the current dimension id.

```lua
print(locator.getDimension())
-- Example: minecraft:overworld
```

### `getFacing()`

Returns which horizontal direction the wearer is facing.

```lua
print(locator.getFacing())
```

### `getRotation()`

Returns yaw and pitch, in degrees.

```lua
local yaw, pitch = locator.getRotation()
print(yaw, pitch)
```

### `locate()`

Returns all the locator info in one Lua table.

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

## curios integration

Curios is needed for the smartwatch slot.

The mod adds a:

```text
watch
```

slot for players. `cctablet:smartwatch` goes in there.

CC:Tablet connects the Curios `watch[0]` stack to CC:Tweaked's pocket-computer holder system. This keeps the computer running and syncing while it is outside the normal player inventory.

## peripheral layout

### Tablet

```text
RIGHT = built-in speaker
BACK  = removable pocket upgrade
```

### Universal modem

The standalone `universal_modem` block uses the standard CC:Tweaked API.
`modem.transmit(channel, replyChannel, message)` keeps its three arguments.
Set a numeric range with `modem.setRange(3)` or `modem.setRange(30)`, or enable
Ender-style networking with `modem.setRange("ender")`. Use
`modem.getRange()`, `modem.ignore("ender")`, `modem.allow("ender")`, and
`modem.isIgnored("ender")` to inspect and control the setting. `modem.getEnder()`
returns the Ender constant for Lua environments that cannot expose
peripheral fields directly.

The modem item is also a CC:Tweaked pocket upgrade. Install it on an advanced
pocket computer, tablet, or smartwatch with the standard upgrade recipe. It uses
the BACK slot and is not built into those devices.

### Smartwatch

```text
LEFT  = built-in locator
RIGHT = built-in speaker
BACK  = removable pocket upgrade
```

## building

Build with **Gradle 8.1.1** and **Java 17**.

To create or update the Gradle wrapper, run:

```bash
gradle wrapper --gradle-version 8.1.1
```

Then build with:

```bash
gradlew build
```

or on Windows:

```bat
gradlew.bat clean build
```

The reobfuscated JAR ends up here:

```text
build/libs/cctablet-(VERSION).jar
```

## why there are mixins

CC:Tweaked normally gives pocket computers the configured terminal size and expects player-held computers to be in vanilla inventory slots. It also renders pocket computers as map-like items.

CC:Tablet has a few small mixins to handle these cases, while keeping CC:Tweaked's computer implementation:

- **PocketServerComputer mixin**: picks **40×20** for tablets and **18×10** for watches when the server computer is created.
- **PocketItemRenderer mixin**: gives the right fallback terminal size before synced terminal data arrives.
- **PocketHolder.PlayerHolder bridge**: points the watch's synthetic holder slot at Curios `watch[0]`.
- **Client rendering hook**: stops CC:Tweaked from drawing the watch like its regular two-handed, map-like pocket computer item.

The smartwatch uses CC:Tweaked's regular terminal widget and input handling inside its watch screen. Keyboard, mouse, terminal, and upload behavior work as usual.

## changelog

### 0.2.5

- Added Hard Drives with various storage (1MiB to 4MiB standart, can be changed in the config)
- Added a Config folder
- Changed the mod to have its own Creative Tab

### 0.2.5BETA

- Added the Universal Modem

### 0.2.4 

- I forgor

### 0.2.3

- Added the **advanced smartwatch computer**.
- Added a Curios `watch` slot.
- Added an **18×10** smartwatch terminal.
- Added a default **V** keybind to open the equipped watch.
- Added a custom smartwatch GUI and editable `textures/gui/smartwatch.png` frame.
- Added a dedicated smartwatch wrist renderer.
- Added a built-in `locator` peripheral on the smartwatch's `left` side.
- Added precise position, block position, dimension, facing, yaw, and pitch locator methods.
- Added a built-in speaker on the smartwatch's `right` side.
- Kept the standard removable pocket-upgrade slot available on the smartwatch's back.
- Added a direct crafting recipe using a clock surrounded by eight gold nuggets.
- Added the Curios-to-CC:Tweaked holder bridge so the smartwatch continues running while equipped.

### 0.2.1

- Replaced the hand-written tablet speaker with CC:Tweaked's real `PocketSpeakerPeripheral`.
- Restored normal speaker semantics, DFPWM `playAudio`, real `stop()`, CC:Tweaked rate limiting, and normal attach/detach cleanup.
- Made recipe matching allocation-free; NBT copying now only happens when the tablet recipe is actually assembled.

### 0.2.0

- Added a built-in **speaker** peripheral to the tablet while keeping the normal single pocket-upgrade slot.
- Updated the tablet item texture to look more like a wider CC pocket computer.
- Kept the stable `@Redirect`-based terminal-size mixin used by the working 0.1.2 build.
