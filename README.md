# cc: tablet (forge 1.20.1)

A CC:Tweaked addon for Forge 1.20.1 that adds two portable advanced computers:

- **advanced tablet computer**: a wider handheld computer with a **40×20** terminal.
- **advanced smartwatch computer**: a wrist computer with an **18×10** terminal, Curios slot, built-in locator and speaker, plus its own watch-style screen.

Both devices still use the usual CC:Tweaked CraftOS and pocket computer stuff, with some extra hardware and their own look.

## target versions

- Minecraft 1.20.1
- Forge 47.4.10 or newer 47.4.x
- CC:Tweaked 1.120.2
- Curios 5.14.1 or newer 5.x
- Java 17
- put the built JAR on **the dedicated server and every client**.

This mod uses some CC:Tweaked implementation classes as well as its public API. The public API doesn't have all the hooks needed for per-item terminal sizes and the watch screen, so this version is tied pretty closely to CC:Tweaked 1.120.2.

## devices

### advanced tablet computer

The tablet is a bigger Advanced Pocket Computer for programs that want a bit more screen space.

- Item ID: `cctablet:tablet`
- Terminal size: **40 columns × 20 rows**
- Computer family: **advanced**
- Built-in speaker on the `right` side
- Normal removable pocket upgrade remains available on the back
- Uses the normal CC:Tweaked pocket-computer lifecycle, CraftOS, IDs, filesystem, HTTP, rednet, terminal networking, mouse and touch events, and upgrades

The regular CC:Tweaked pocket upgrade recipe works since the tablet extends `PocketComputerItem`. So you can install an **Advanced Wireless Modem (Ender Modem)** with the usual recipe too.

### advanced smartwatch computer

The smartwatch is a small wearable advanced computer that can stay equipped while you use it.

- Item ID: `cctablet:smartwatch`
- Terminal size: **18 columns × 10 rows**
- Computer family: **advanced**
- Equipped in a dedicated Curios `watch` slot
- Built-in locator on the `left` side
- Built-in speaker on the `right` side
- Normal removable pocket upgrade remains available on the back
- Keeps running while equipped
- Press **V** by default to open it
- Has its own watch-shaped screen instead of the regular pocket-computer frame
- Uses a wrist renderer while equipped

The smartwatch GUI frame is stored as an editable texture at:

```text
assets/cctablet/textures/gui/smartwatch.png
```

The CC:Tweaked terminal is drawn over this texture. Change the PNG to change the watch body; the terminal code can stay as it is.

## crafting

### crafting the tablet

To convert a pocket computer into a tablet, use:

- 1× Advanced Pocket Computer
- 2× Gold Ingots

The recipe goes through CC:Tweaked's `changeItem` path, which keeps the original computer's **ID, filesystem, label, colour, installed upgrade, and powered-on state**.

It's shapeless, so put the Advanced Pocket Computer and two Gold Ingots in separate slots, any arrangement is fine. It shows up in JEI and unlocks in the recipe book when you get an Advanced Pocket Computer. Smartwatches and tablets aren't valid inputs.

### crafting the smartwatch

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

It's CC:Tweaked's actual `PocketSpeakerPeripheral`, so the usual speaker calls work: `playNote`, `playSound`, `playAudio`, and `stop`. Buffering and lifecycle are handled the normal way too.

The removable pocket-upgrade slot is still available separately.

## smartwatch locator

The smartwatch has a built-in `locator` peripheral on the `left`. It reports the wearer's current world position while equipped, no GPS towers needed.

This is its own peripheral API. Use `peripheral.find("locator").getPosition()` instead of `gps.locate()`. CraftOS `gps.locate()` still needs a wireless modem and GPS hosts.

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

## smartwatch controls

With the smartwatch in the Curios `watch` slot, press:

```text
V
```

to open it without removing it from your wrist.

The keybind is in Minecraft's Controls menu under **CC:Tablet**. You can change it there.

## curios integration

Curios is needed for the smartwatch slot.

The mod adds a:

```text
watch
```

slot for players. `cctablet:smartwatch` goes in there.

Under the hood, CC:Tablet connects the Curios `watch[0]` stack to CC:Tweaked's pocket-computer holder system. That lets the computer stay alive, sync, and work outside the normal player inventory.

## peripheral layout

### tablet

```text
RIGHT = built-in speaker
BACK  = removable pocket upgrade
```

### universal modem

The standalone `universal_modem` block uses the usual CC:Tweaked API:
`modem.transmit(channel, replyChannel, message)` still has three arguments.
It adds configurable range with `modem.setRange(3)` or `modem.setRange(30)`,
and Ender-style networking with `modem.setRange("ender")`. Use
`modem.getRange()`, `modem.ignore("ender")`, `modem.allow("ender")`, and
`modem.isIgnored("ender")` to inspect and control the setting. `modem.getEnder()`
returns the Ender constant value for Lua environments which cannot expose
peripheral fields directly.

The modem item is also a CC:Tweaked pocket upgrade. Add it to a regular advanced
pocket computer, tablet, or smartwatch with the standard upgrade recipe. It uses
the BACK slot, it isn't built into those devices.

### smartwatch

```text
LEFT  = built-in locator
RIGHT = built-in speaker
BACK  = removable pocket upgrade
```

## building

Build with **Gradle 8.1.1** and **Java 17**.

If you need to make or update the wrapper, run:

```bash
gradle wrapper --gradle-version 8.1.1
```

Curios needs to be on the compile and runtime classpath. With CurseMaven set up, add:

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

The reobfuscated JAR ends up here:

```text
build/libs/cctablet-(VERSION).jar
```

## why there are mixins

CC:Tweaked normally gives pocket computers the configured terminal size and expects player-held computers to be in vanilla inventory slots. It also draws pocket computer items like a map.

CC:Tablet has a few small mixins to handle these cases, while keeping CC:Tweaked's computer implementation:

- **PocketServerComputer mixin**: picks **40×20** for tablets and **18×10** for watches when the server computer is created.
- **PocketItemRenderer mixin**: gives the right fallback terminal size before synced terminal data arrives.
- **PocketHolder.PlayerHolder bridge**: points the watch's synthetic holder slot at Curios `watch[0]`.
- **Client rendering hook**: stops CC:Tweaked from drawing the watch like its regular two-handed, map-like pocket computer item.

The smartwatch uses CC:Tweaked's regular terminal widget and input handling inside its watch screen. Keyboard, mouse, terminal, and upload behaviour still work as usual.

## changelog

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

### 0.2.2

- kinda forgot what changed here


### 0.2.1

- Replaced the hand-written tablet speaker with CC:Tweaked's real `PocketSpeakerPeripheral`.
- Restored normal speaker semantics, DFPWM `playAudio`, real `stop()`, CC:Tweaked rate limiting, and normal attach/detach cleanup.
- Made recipe matching allocation-free; NBT copying now only happens when the tablet recipe is actually assembled.

### 0.2.0

- Added a built-in **speaker** peripheral to the tablet while keeping the normal single pocket-upgrade slot.
- Updated the tablet item texture to look more like a wider CC pocket computer.
- Kept the stable `@Redirect`-based terminal-size mixin used by the working 0.1.2 build.
