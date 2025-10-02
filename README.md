# Colour Tracker Robot

## Overview
The Colour Tracker Robot is an autonomous LEGO EV3 robot designed to follow coloured lines, pick up blocks, deliver them to destinations, and return to its home base. It uses a combination of line and block colour sensors, motor controls, and behaviour-based programming to navigate and complete tasks efficiently and safely.

---

## Table of Contents
- [Technical Explanation](#technical-explanation)
- [Robot Behaviours](#robot-behaviours)
- [Colour Sensor Mappings](#colour-sensor-mappings)
- [Control Flow](#control-flow)

---

## Technical Explanation

### Driver
The `Driver` class is the main controller of the robot. Its responsibilities include:

- Displaying a welcome screen on the EV3 with robot and author information.
- Initialising the robot’s movement system using the `MovePilot` functionality.
- Calling all robot behaviours, including movement, turning, destination handling, and home behaviour.
- Managing emergency behaviours such as touch sensor emergency stop and low battery shutdown.

Behaviours are managed using the `Arbitrator`, which prioritises execution to ensure safety and efficiency.

**Behaviour Priority Table:**

| Priority | Behaviour |
|----------|-----------|
| 1 (Top)  | BatteryLevel |
| 2        | ExitBehaviour |
| 3        | TouchSensorEmergencyStop |
| 4        | Home |
| 5        | GoHome |
| 6        | DestinationDetection |
| 7        | Turning |
| 8 (Bottom) | Trundle |

---

### ColourSensor
The `ColourSensor` class handles all colour sensor data:

- Line Colour Sensor connected to port S1.
- Block Colour Sensor connected to port S4.
- Stores RGB samples dynamically based on sensor readings.
- Functions include:
  - `getLineRGBSample()`
  - `getBlockRGBSample()`
  - `detectBlockColour()` – detects red or blue blocks.
  - Colour detection functions for lines such as `isRedDetected()`, `isYellowDetected()`, etc., using hardcoded RGB ranges.

#### Colour Mappings

**Line Sensor Colour Ranges:**

| Colour  | R        | G        | B        |
|---------|----------|----------|----------|
| White   | 0.1-0.4  | 0.1-0.4  | 0.1-0.35 |
| Purple  | 0.08-0.17| <=0.1    | 0.1-0.2  |
| Green   | 0.05-0.15| 0.2-0.3  | <=0.1    |
| Blue    | <=0.1    | 0.1-0.2  | 0.15-0.25|
| Red     | 0.3-0.4  | <=0.1    | <=0.07   |
| Yellow  | 0.3-0.4  | 0.18-0.25| <=0.07   |

**Block Sensor Colour Ranges:**

| Colour | R         | G         | B         |
|--------|-----------|-----------|-----------|
| Blue   | <=0.1     | 0.08-0.12 | 0.08-0.12 |
| Red    | 0.19-0.26 | <=0.1     | <=0.1     |

Testing has been conducted to validate these colour ranges for both block and line sensors.

---

### Turning
The `Turning` class handles turning behaviours when the robot deviates from the line or encounters white.

- Rotates in small 10° increments to scan for target colours.
- Uses `scanForLine()` to detect when back on track.
- Ensures minimal overcorrection and efficient realignment.

---

### Trundle
The `Trundle` class controls forward movement when following a line:

- Moves forward when line sensor detects blue or red lines matching the block colour.
- Stops at yellow or green lines when conditions indicate a destination or return path.
- Continues until another behaviour with higher priority takes control.

---

### DestinationDetection
Handles behaviour at the delivery destination:

- Activates on detecting yellow line with red or blue block in tray.
- Waits for block removal before signalling `GoHome`.

---

### GoHome
Manages return to the home base:

- Activates when green line is detected and `destinationDetection` is true.
- Turns robot to align with return path.
- Transfers control back to `Trundle` after returning to line.

---

### Home
Handles robot behaviour at the home base:

- Activates on detecting purple line.
- Waits for parcel placement and user confirmation.
- Aligns robot to follow correct path to delivery destination.
- Resets `Home` variable to enable next delivery cycle.

---

### BatteryLevel
- Monitors battery voltage.
- Activates shutdown when voltage < 1000 mV (10%).
- Displays low battery message before system exit.

---

### TouchSensorEmergencyStop
- Emergency stop triggered via touch sensor.
- Stops robot and waits until sensor is pressed again to resume.

---

### ExitBehaviour
- Allows user to exit program anytime with ESC key.
- Highest priority after battery shutdown.
- Safely terminates all behaviours and stops the program.

---

## Control Flow
1. Welcome message is displayed with authors and version.
2. Motors, sensors, and `MovePilot` are initialised.
3. All behaviours are registered with the `Arbitrator`.
4. Behaviours execute based on priority:
   - Low battery or ESC triggers program exit.
   - Emergency stop or other behaviours run according to their priority.
5. Robot performs pick-up, delivery, and return tasks autonomously.
