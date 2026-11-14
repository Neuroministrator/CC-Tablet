print("cc:tablet hardware check")
print("------------------------")

local passed = 0
local failed = 0
local skipped = 0

local function result(state, name, detail)
    if state == "pass" then
        passed = passed + 1
        print("[ok] " .. name .. (detail and (": " .. detail) or ""))
    elseif state == "fail" then
        failed = failed + 1
        print("[!!] " .. name .. (detail and (": " .. detail) or ""))
    else
        skipped = skipped + 1
        print("[--] " .. name .. (detail and (": " .. detail) or ""))
    end
end

local function has_method(methods, name)
    for _, method in ipairs(methods) do
        if method == name then return true end
    end
    return false
end

local width, height = term.getSize()
if width == 40 and height == 20 then
    result("pass", "tablet terminal", "40x20")
elseif width == 18 and height == 10 then
    result("pass", "smartwatch terminal", "18x10")
else
    result("skip", "tablet/watch terminal", width .. "x" .. height .. " (run this on a tablet or watch)")
end

local speaker = peripheral.getType("right")
if speaker == "speaker" then
    result("pass", "built-in speaker", "right side")
elseif peripheral.find("speaker") then
    result("pass", "speaker peripheral", "found")
else
    result("skip", "built-in speaker", "no speaker attached")
end

local locator = peripheral.find("locator")
if locator then
    local ok, x, y, z = pcall(locator.getPosition)
    if ok and type(x) == "number" and type(y) == "number" and type(z) == "number" then
        result("pass", "smartwatch locator", "position returned")
    else
        result("fail", "smartwatch locator", tostring(x))
    end
else
    result("skip", "smartwatch locator", "no locator attached")
end

local hddCase = peripheral.find("hdd_case")
if hddCase then
    result("pass", "HDD case peripheral", "connected")
    for slot = 1, 2 do
        local labelOk, label = pcall(hddCase.getLabel, slot)
        if not labelOk then
            result("skip", "HDD slot " .. slot, "empty")
        else
            local readOnlyOk, readOnly = pcall(hddCase.isReadOnly, slot)
            local mountPath = "hdd" .. slot
            local mountExists = fs.exists(mountPath) and fs.isDir(mountPath)
            if mountExists then
                result("pass", "HDD slot " .. slot .. " mount", fs.getDrive(mountPath) or label)
            else
                result("fail", "HDD slot " .. slot .. " mount", "" .. mountPath .. " is missing")
            end

            if not readOnlyOk then
                result("fail", "HDD slot " .. slot .. " mode", tostring(readOnly))
            elseif readOnly then
                local testPath = fs.combine(mountPath, ".cctablet_write_probe")
                local opened, handle = pcall(fs.open, testPath, "w")
                if opened and handle then
                    handle.close()
                    fs.delete(testPath)
                    result("fail", "HDD slot " .. slot .. " read-only", "writes were allowed")
                else
                    result("pass", "HDD slot " .. slot .. " read-only", "writes are blocked")
                end

                if fs.exists(fs.combine(mountPath, "cctablet_test.lua")) then
                    result("pass", "HDD5 shared test program", "visible on the read-only drive")
                end
            else
                local testPath = fs.combine(mountPath, ".cctablet_write_probe")
                local expected = "cc:tablet hdd test"
                local opened, handle = pcall(fs.open, testPath, "w")
                if opened and handle then
                    handle.write(expected)
                    handle.close()
                    local readHandle = fs.open(testPath, "r")
                    local actual = readHandle and readHandle.readAll() or nil
                    if readHandle then readHandle.close() end
                    if fs.exists(testPath) then fs.delete(testPath) end
                    if actual == expected then
                        result("pass", "HDD slot " .. slot .. " read/write", "test file created, read, and removed")
                    else
                        result("fail", "HDD slot " .. slot .. " read/write", "file contents did not match")
                    end
                else
                    result("fail", "HDD slot " .. slot .. " read/write", tostring(handle))
                end
            end
        end
    end
else
    result("skip", "HDD case", "connect to an hdd_case peripheral over a cable")
end

local foundUniversalModem = false
for _, name in ipairs(peripheral.getNames()) do
    local methods = peripheral.getMethods(name) or {}
    if has_method(methods, "getEnder") then
        foundUniversalModem = true
        local ok, ender = pcall(peripheral.call, name, "getEnder")
        if ok and ender == "ender" then
            result("pass", "universal modem Ender constant", ender)
        else
            result("fail", "universal modem Ender constant", tostring(ender))
        end

        if has_method(methods, "getRange") then
            local rangeOk, range = pcall(peripheral.call, name, "getRange")
            if rangeOk and type(range) == "number" then
                result("pass", "universal modem range", tostring(range))
            else
                result("fail", "universal modem range", tostring(range))
            end
        else
            result("fail", "universal modem range", "getRange method missing")
        end

        if has_method(methods, "isIgnored") then
            local ignoredOk, ignored = pcall(peripheral.call, name, "isIgnored", "ender")
            if ignoredOk and type(ignored) == "boolean" then
                result("pass", "Ender ignore status", tostring(ignored))
            else
                result("fail", "Ender ignore status", tostring(ignored))
            end
        else
            result("fail", "Ender ignore status", "isIgnored method missing")
        end
    end
end

if not foundUniversalModem then
    result("skip", "universal modem", "no CC:Tablet universal modem connected")
end

print("------------------------")
print("results: " .. passed .. " passed, " .. failed .. " failed, " .. skipped .. " skipped")
if failed == 0 then print("looks good. skipped checks need the matching CC:Tablet hardware.") end
