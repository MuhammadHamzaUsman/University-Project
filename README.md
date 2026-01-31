# Forge Voxel

**Forge Voxel** is a Windows-only 3D programming puzzle game developed as a **University capstone project**.  
Players solve puzzles by writing code in a custom scripting language that procedurally generates 3D voxel structures.

The game provides real-time visual feedback by rendering user-generated voxel grids alongside target solutions, allowing players to learn programming concepts through spatial reasoning and experimentation.

---

## 🎮 What Is Forge Voxel?

Forge Voxel combines:
- A **custom programming language**
- A **3D voxel-based rendering engine**
- **Puzzle-driven learning mechanics**

Players write code that runs once per `(x, y, z)` coordinate in a voxel grid.  
Their output is compared against a target structure, and completion is measured by a matching percentage.

---

## ✨ Key Features

- 🧠 Learn programming through visual 3D puzzles
- 🧩 Custom scripting language with:
  - Variables
  - Functions
  - Conditionals
  - Loops
- 🧱 Multiple voxel shapes (Cube, Sphere, Cylinder)
- 🎨 Colors, sizes, materials, and shape control via code
- 📊 Automatic grid comparison and completion percentage
- ✂️ Interactive 3D slicing gizmo
- 🎥 Animated voxel placement with sound feedback
- 💾 Persistent level progress

---

## 🕹️ Game Flow

1. Launch the application
2. **Main Menu** opens
3. Navigate to **Level Selection**
4. Select a puzzle level
5. Write code in the built-in editor
6. Execute code to generate a voxel grid
7. Compare your result against the target structure

---

## 🎮 Controls

### Camera
- **SHIFT + Drag Mouse** — Rotate camera
- **Mouse Wheel** — Zoom in/out
- **Hover** — Highlight voxels

### Slicing Gizmo
- **CTRL + Click + Drag** — Move slicing planes along X, Y, or Z axis  
  (Dynamically hides voxels beyond the slice)

### Code Execution
- Run via the **code editor UI**
- Errors and match percentage shown in real time

---

## 🛠️ Tech Stack

- **Java 21**
- **Gradle**
- **jMonkeyEngine 3** — 3D rendering
- **JavaFX** — UI and code editor
- **Custom Interpreter** — Lexer, Parser, AST, Interpreter

---

## 📂 Project Structure

```text
ForgeVoxel
└── app
    ├── assets
    │   ├── Images
    │   ├── Models
    │   └── Sounds
    ├── levels
    ├── levelsOrigin
    │   ├── 1
    │   ├── 2
    │   └── 3
    ├── settings
    └── src
        ├── main
        │   ├── java
        │   │   └── com
        │   │       └── example
        │   │           ├── io
        │   │           ├── shape
        │   │           └── TextEditor
        │   │               └── Interpreter
        │   │                   ├── interpreter
        │   │                   ├── statements
        │   │                   ├── tokens
        │   │                   └── values
        │   └── resources
        │       ├── fonts
        │       ├── images
        │       └── sounds
        └── test
            └── java
                └── forgevoxel
```

---

## 🧠 Custom Scripting Language

The built-in language is interpreted at runtime and supports:

- Numeric & boolean values
- Maps (used to define voxel properties)
- User-defined and native functions
- Conditionals and loops
- Lexical scoping

Each puzzle executes user code once per grid coordinate to determine whether a voxel exists and which properties it has.

---

<!-- ## 📸 Screenshots & Media

> _Screenshots, GIFs, and videos will be added here._

You can place media files in:



And embed them later in this section.

--- -->

## 🚀 Running the Project (Development)

### Requirements
- **Windows**
- **Java 21**
- **Gradle**

### Build & Run

```bash
./gradlew run 
```

## ⚠️ Assets & Licensing

Most visual assets were created by the me.

Some assets (e.g. background images, sounds, and models) were sourced from the internet a long time ago, and their original sources are unknown.

No license is currently provided for this project. This repository is intended for academic and demonstration purposes only.

---