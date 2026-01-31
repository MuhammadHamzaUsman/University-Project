# **FORGE VOXEL: PROJECT DOCUMENTATION**

## **1. PROJECT OVERVIEW**

**Forge Voxel** is an interactive 3D puzzle-solving application built in Java that combines graphics rendering, functional programming interpretation, and puzzle mechanics. The application allows users to write code in a custom scripting language to generate and manipulate voxel grids (3D grids of unit cubes, spheres, and cylinders). Users solve programming puzzles by writing code that produces target 3D structures, with their output compared against expected solutions using a matching percentage algorithm.

The project employs **jMonkeyEngine 3** for 3D graphics rendering, **JavaFX** for the user interface, and a custom-built **expression interpreter** that processes user code to dynamically generate voxel objects with various properties (colors, sizes, shapes).

---

## **2. PROBLEM STATEMENT**

The core problem addressed by Forge Voxel is:

**How can we create an interactive educational tool that teaches programming concepts (variables, functions, conditionals, loops) through visual 3D puzzle feedback, where users write procedural code to generate and manipulate 3D geometric structures?**

Specifically, the system must:
- Parse and interpret a custom scripting language
- Execute user-written code in a controlled runtime environment
- Dynamically generate 3D voxel objects based on code execution
- Provide real-time visual feedback and performance metrics
- Support persistent level storage and progress tracking
- Enable interactive manipulation of the 3D scene (camera control, slice visualization)

---

## **3. OBJECT-ORIENTED PROGRAMMING (OOP) CONCEPTS USED**

### **3.1 Encapsulation**

Encapsulation hides internal implementation details while providing public interfaces. Examples include:

- **Voxel class**: Encapsulates voxel properties (position, color, size, shape, material) with private fields and public getter/setter methods. The class hides the complexity of 3D model loading and transformation.
  
- **VoxelGrid**: Wraps a 3D array `Voxel[][][]` with controlled access through methods like `placeVoxels()`, `updateGrid()`, and dimension getters/setters.

- **Value class** (interpreter): Encapsulates different value types (double, boolean, null, function, map) behind factory methods (`Value.of(double)`, `Value.ofNull()`, etc.) rather than exposing constructor logic.

- **Variables class**: Manages a scope-based variable storage system, encapsulating the HashMap of variables and providing `declareVariable()`, `getValue()`, and `assignValue()` methods.

### **3.2 Inheritance**

Inheritance enables code reuse and polymorphic behavior:

- **Voxel (abstract base class)** → **Cube, Sphere, Cylinder** (concrete implementations)
  - All three shape classes inherit from `Voxel`, inheriting common properties (position x, y, z; color; size; material; shape).
  - Each concrete class overrides the abstract `draw(Node, float)` method with shape-specific rendering logic.

- **Statement (AST interface) → Multiple concrete statement types**
  - Abstract syntax tree nodes (NumericLiteral, BooleanLiteral, BinaryExpression, IfStatement, WhileStatement, etc.) all extend or implement `Statement`.
  - Each implements `getType()` to return a `StatementType` enum value.

- **ExpressionStatement (parent) → Specific expression types**
  - Numeric/Boolean/Null literals, identifiers, binary/unary expressions, etc., all extend `ExpressionStatement`.

### **3.3 Polymorphism**

Polymorphism allows objects of different types to be treated uniformly:

- **Voxel subclasses**: The `VoxelGrid` calls `draw()` on each voxel without knowing its concrete type (Cube, Sphere, or Cylinder). The 3D engine invokes the appropriate overridden method.

- **Statement interpretation**: The `Interpreter.interpret()` method uses a switch statement on `ast.getType()` to dispatch to different evaluation methods, enabling polymorphic AST node handling.

- **FunctionCall (interface) → Native and User-Defined Functions**
  - Both built-in math functions and user-defined functions implement or extend function-call semantics, allowing the interpreter to invoke them uniformly via `funcCall.execute(args)`.

- **Statement evaluation**: Binary expressions, unary expressions, literals, and assignments are all handled through polymorphic `interpret()` calls, each returning a `Value`.

### **3.4 Abstraction**

Abstraction defines interfaces and abstract classes to represent concepts at a higher level:

- **Abstract Voxel class**: Defines the contract for 3D shapes (properties and the `draw()` method) without specifying how each shape renders.

- **VoxelGridInterface (functional interface)**: Abstracts the logic for determining which voxel exists at each grid coordinate, allowing different functions to define different grid patterns without exposing implementation details.

- **Statement and StatementType**: Abstract the structure of the program into an abstract syntax tree, separating parsing logic from interpretation logic.

- **Value and ValueType**: Abstract the concept of a runtime value, supporting multiple types (double, boolean, null, map, function) with a unified interface.

- **Gizmo**: Abstracts the interactive 3D manipulation interface for slicing and viewing the voxel grid, providing axis-aligned cutting planes.

---

## **4. CLASS DESCRIPTIONS AND RESPONSIBILITIES**

### **4.1 Core 3D Graphics Classes**

#### **Voxel (Abstract Base Class)**
- **Responsibility**: Define the interface and shared properties for all 3D voxel objects.
- **Key Members**: Position (x, y, z), color, size, shape, material.
- **Methods**: Abstract `draw()` method; static methods for asset management (loading borders, pop sounds); comparison methods (`isSame()`).
- **OOP Role**: Base class for Cube, Sphere, Cylinder; uses static factory pattern for shared assets.

#### **Cube, Sphere, Cylinder (Concrete Voxel Subclasses)**
- **Responsibility**: Implement shape-specific 3D rendering logic.
- **Key Difference**: Each loads its own 3D model (from GLTF files), applies material, and calculates animation delay based on distance from grid center.
- **Method**: Override `draw()` to attach the shape to the scene graph with animation controls.

#### **VoxelGrid**
- **Responsibility**: Manage a 3D array of voxels and handle grid rendering, updates, and limits.
- **Key Data**: 3D array `Voxel[][][]`, gridNode (JME3 scene graph node).
- **Methods**: `placeVoxels()` (populate grid), `updateGrid()` (asynchronous update), `matchPercentage()` (compare grids), `draw()` (render to screen), dimension setters for animated slicing.
- **OOP Role**: Composition; contains voxels and manages their collective behavior.

#### **VoxelAnimation**
- **Responsibility**: Control time-based scaling animations for voxels as they appear.
- **Key Features**: Easing function (ease-out-back), radial delay based on distance from origin, pop sound playback.
- **OOP Role**: Control class inheriting from `AbstractControl` (JME3 lifecycle).

#### **Gizmo**
- **Responsibility**: Provide interactive 3D slicing/viewing controls via draggable axis-aligned cones and bars.
- **Key Interactions**: Mouse drag to move selectors along X, Y, Z axes; CTRL+click to activate.
- **Methods**: `intializeAxisBars()` (create UI elements), `updateSelector()` (handle input), `setSelectorXPosition()` (synchronize with grid).
- **OOP Role**: Mediator; bridges user input, camera, and grid state.

#### **OrbitCamera**
- **Responsibility**: Manage interactive 3D camera rotation, zoom, and highlight detection.
- **Key Features**: SHIFT+click+drag to rotate; mouse wheel to zoom; hover detection via raycasting.
- **OOP Role**: Observer; listens to input events and updates camera pose.

### **4.2 Voxel Property Classes (Enums)**

#### **Colors (Enum)**
- **Responsibility**: Define a palette of 16 colors for voxels (white, gray, black, skin, pink, purple, red, orange, yellow, light green, dark green, dark blue, blue, light blue, light brown, dark brown).
- **Methods**: `getColor()` (returns jMonkeyEngine ColorRGBA), `getFxColor()` (JavaFX Color for UI).

#### **Size (Enum)**
- **Responsibility**: Define three size scales (small, medium, large) with scaling factors (0.25, 0.625, 1.0).
- **OOP Role**: Data class; used to parameterize voxel dimensions.

#### **Shape (Enum)**
- **Responsibility**: Define three shape types (cube, sphere, cylinder) with base dimensions.
- **OOP Role**: Data class; used to select voxel model and rendering behavior.

#### **MaterialEnum (Enum)**
- **Responsibility**: Define three material properties (matte, shiny, transparent) and provide a static factory method to create jMonkeyEngine materials.
- **Methods**: `createMaterial()` – static method that configures ambient, diffuse, specular, and blend properties.

### **4.3 Puzzle and Level Management**

#### **Puzzel (Puzzle Class)**
- **Responsibility**: Orchestrate puzzle execution, comparing user-generated grids against target grids.
- **Key Components**: 
  - `targetGrid` and `userGrid` (VoxelGrid instances)
  - `targetGridFunction` and user code (as Statement AST)
  - Interpreter, Parser, Lexer for code execution
  - Variables scope with pre-loaded colors, sizes, shapes, and math functions
- **Methods**: 
  - `updateUserGrid()` – compile user code and regenerate user grid
  - `updateGrid()` – execute a Statement function with x, y, z coordinates to generate voxels
  - `updateCompletion()` – calculate match percentage and set completion flag
  - `resetPuzzel()` – clear user progress
- **OOP Role**: Facade; hides complexity of interpretation and grid comparison.

#### **PuzzelLevel (Level Data Class)**
- **Responsibility**: Hold metadata and state for a single puzzle level.
- **Key Members**: levelName, width, height, depth, image (thumbnail), targetFunction (serialized Statement), userFunction (code string), filePath, completed (boolean).
- **Methods**: `toPuzzel()` – factory method to instantiate a Puzzel from level data.

#### **LevelIO (File I/O)**
- **Responsibility**: Persist and retrieve puzzle levels, including code, metadata, and completion status.
- **Key Methods**:
  - `readPuzzels()` – load all levels from disk
  - `readLevel()` – load a single level by path
  - `loadFunc()` – load the target function AST
  - `updateCompletion()`, `updateUserCode()` – save user progress
  - `writeLastPlayedLevel()` – remember the last played level for session continuity
- **OOP Role**: Repository pattern; abstracts file I/O and serialization.

### **4.4 Interpreter and Code Execution Classes**

#### **Lexer**
- **Responsibility**: Convert raw source code text into a stream of tokens.
- **Process**: Scan input character by character, recognizing keywords, identifiers, numbers, operators, and punctuation.
- **Output**: List of Token objects with type and value.

#### **Token & Tokens (Enum)**
- **Token**: Holds token type and string value.
- **Tokens (enum)**: Defines all token types (NUMBER, IDENTIFIER, KEYWORD, OPERATOR, punctuation, etc.).

#### **Parser**
- **Responsibility**: Convert a token stream into an abstract syntax tree (AST).
- **Methods**: 
  - `parseProgram()` – entry point; returns BlockStatement
  - Recursive descent methods: `parseStatement()`, `parseExpression()`, `parseIfStatement()`, `parseWhileStatement()`, `parseFunctionDeclarationStatement()`, etc.
  - Operator precedence handling: `parseOROperator()` → `parseAndOperator()` → ... → `parsePrimaryExpression()`
- **Output**: Statement AST ready for interpretation.

#### **Interpreter**
- **Responsibility**: Execute the AST by visiting each node and evaluating it.
- **Methods**:
  - `executeProgram(Statement)` – entry point
  - `interpret(Statement, Variables)` – main dispatch method with switch on statement type
  - Evaluation methods: `evaluateBinaryExpression()`, `evaluateIfStatement()`, `evaluateWhileStatement()`, `evaluateCallExpression()`, etc.
  - Handles return statements via custom exception `ReturnExcpetion`
- **Scope Management**: Uses nested Variables scopes for if/while blocks and function calls.

#### **Variables (Scope)**
- **Responsibility**: Manage variable bindings (name → Value) and support scope nesting.
- **Key Concept**: Each Variables instance has a reference to its parent scope, enabling lexical scoping.
- **Methods**: `declareVariable()`, `getValue()`, `assignValue()`, `isVaraibleDeclared()`, `getVaraibles()` (for clearing on reset).

#### **Value**
- **Responsibility**: Represent a runtime value with support for multiple types (double, boolean, null, map, function, user-defined function).
- **Factory Methods**: `Value.of(double)`, `Value.of(boolean)`, `Value.ofNull()`, `Value.ofFunction()`, `Value.ofUserFunction()`, `Value.ofMap()`.
- **OOP Role**: Tagged union; uses ValueType enum to distinguish types.
- **Special Method**: `getVoxelProperties()` – extract Color, Size, Shape from a map-type value for voxel creation.

#### **ValueType (Enum)**
- **Responsibility**: Enumerate the possible runtime value types.
- **Values**: DOUBLE, BOOLEAN, NULL, FUNCTION, USER_FUNCTION, MAP.

#### **Functions**
- **Responsibility**: Provide a library of 40+ native math functions (sin, cos, sqrt, abs, pow, random, etc.) as Function objects.
- **OOP Role**: Factory; `loadFunctions()` populates Variables scope with pre-defined functions.

#### **FunctionCall (Interface) & UserDefinedFunction**
- **FunctionCall**: Interface for executable functions with `execute(Value[])` and `requiredArgs()`.
- **UserDefinedFunction**: Stores user-defined function name, parameter names, closure scope, and body statements. When called, creates a new scope, binds arguments, and executes body.

#### **Statement & Related Classes**
- **Statement**: Base interface for all AST nodes, defining `getType()` and `getType()` returning StatementType.
- **Concrete Statement Types**:
  - Literals: `NumericLiteral`, `BooleanLiteral`, `NullLiteral`, `MapLiteral`
  - Expressions: `Identifer`, `BinaryExpression`, `UnaryExpression`, `CallExpression`, `AssignmentExpression`
  - Statements: `VarDeclaration`, `BlockStatement`, `IfStatement`, `WhileStatement`, `ReturnStatement`, `FunctionDecalarationStatement`, `ExpressionStatement`
- **OOP Role**: Visitor pattern; Interpreter acts as a visitor dispatching on statement type.

#### **Evaluator**
- **Responsibility**: (Legacy/Alternative) Convert postfix notation tokens to values; less actively used in current design.

---

### **4.5 User Interface Classes**

#### **App (JME3 SimpleApplication)**
- **Responsibility**: Main application class; orchestrate 3D rendering, input handling, and UI integration.
- **Key Components**:
  - Two viewports (top for user grid, bottom for target grid) with separate cameras and lighting
  - Puzzle instance
  - CodeEditorUI for code input
  - OrbitCamera and Gizmo for each viewport
  - Asynchronous error checking via Future
- **Methods**:
  - `simpleInitApp()` – initialize scene, lights, shadows, viewports
  - `simpleUpdate()` – per-frame updates (input, error checking)
  - `runCode()` – enqueue code execution and update UI with results
  - `nextLevel()` – transition to a new puzzle level
- **OOP Role**: Main controller; orchestrates all subsystems.

#### **CodeEditorUI**
- **Responsibility**: Provide a JavaFX text editor for code input with syntax highlighting and error display.
- **Integration**: Runs in a separate JavaFX thread; communicates with App via Platform.runLater() callbacks.
- **Features**: Real-time error messages, match percentage display, completion notifications, geometry display hints.

#### **MainMenu**
- **Responsibility**: JavaFX startup window; load levels, manage global state (volume, player preferences), launch the 3D app.
- **Key State**: Static LevelIO instance, list of PuzzelLevel objects, audio volume setting.

#### **LevelSelectionUI**
- **Responsibility**: Display available levels with thumbnails and completion status; allow level selection.
- **Integration**: Loaded into the App window; communicates level transitions.

---

## **5. PROGRAM WORKFLOW**

### **5.1 Startup Flow**

1. **Application Launch**
   - User runs the application; `MainMenu` (JavaFX) initializes
   - `LevelIO.readPuzzels()` loads all level metadata from disk

2. **Level Selection**
   - User sees level thumbnails and completion status in `LevelSelectionUI`
   - User selects a level

3. **Puzzle Initialization**
   - `MainMenu` creates an App instance (JME3 SimpleApplication)
   - `App.simpleInitApp()` runs:
     - Registers assets (models, sounds, fonts)
     - Creates two viewports (top: user grid, bottom: target grid)
     - Initializes lighting and shadows
     - Creates `Puzzel` from the selected `PuzzelLevel`
     - Instantiates `CodeEditorUI` (JavaFX window)
     - Instantiates `OrbitCamera` and `Gizmo` for each viewport
   - If user previously wrote code for this level, it is loaded and executed

### **5.2 User Coding and Execution Flow**

1. **User enters code** in the `CodeEditorUI` text editor

2. **User presses execute (or auto-runs)**
   - `App.runCode(String code)` is called

3. **Code Parsing & Interpretation** (asynchronous):
   ```
   Code String
     ↓ [Lexer.lexer()] 
   Token List
     ↓ [Parser.parseProgram()]
   AST (BlockStatement)
     ↓ [Interpreter.executeProgram()]
   Code Execution:
     For each (x, y, z) in grid:
       - Evaluate AST with x, y, z bound to variables
       - Result is a Value (either null or a map with Color, Size, Shape)
       - Create appropriate Voxel object (Cube, Sphere, or Cylinder)
   ```

4. **Grid Update**
   - `Puzzel.updateUserGrid()` calls `VoxelGrid.updateGrid()` with a VoxelGridInterface function
   - This function executes the user code for each coordinate, returning a Voxel or null

5. **Comparison & Feedback**
   - `VoxelGrid.matchPercentage()` compares user grid against target grid
   - If match == 100%, level is marked complete
   - `CodeEditorUI` displays:
     - Match percentage
     - Any runtime errors
     - Completion notification

6. **Visualization**
   - The 3D renderer displays both grids with:
     - Animations (voxels scale in with ease-out-back easing)
     - Shadows and lighting
     - Gizmo controls for slicing and viewing
     - Highlight on hover

### **5.3 3D Rendering Pipeline**

1. **VoxelGrid.draw()** iterates through all voxels in the grid
2. Each `Voxel` subclass's `draw(Node, radius)` method:
   - Calculates animation delay (based on distance from center)
   - Creates a `VoxelAnimation` control
   - Positions the voxel in 3D space
   - Attaches to the scene graph
3. `VoxelAnimation.controlUpdate()` runs each frame:
   - Increments time
   - Applies easing function to scale voxel from 0 to max size
   - Triggers pop sound at appropriate moment
4. JME3 engine renders the scene with shadows and lighting

### **5.4 Interactive Control Flow**

- **Gizmo (Slicing)**: User CTRL+clicks and drags a cone along an axis to adjust draw limits, dynamically hiding voxels beyond the slice plane
- **OrbitCamera (Viewing)**: User SHIFT+clicks and drags to rotate the camera around the grid; mouse wheel to zoom
- **Hover Detection**: OrbitCamera raycasts from camera through mouse position to detect highlighted voxel and display its name/properties

---

## **6. CONCLUSION**

**Forge Voxel** is a well-architected educational application that demonstrates sophisticated use of object-oriented principles. The project separates concerns into distinct layers:

- **Presentation Layer**: App, `CodeEditorUI`, `MainMenu`, `LevelSelectionUI`, `OrbitCamera`, `Gizmo`
- **Logic Layer**: `Puzzel`, `Interpreter`, `Parser`, `Lexer`, `VoxelGrid`
- **Data Layer**: `LevelIO`, `PuzzelLevel`, `Value`, `Variables`
- **3D Graphics Layer**: `Voxel`, `Cube`, `Sphere`, `Cylinder`, `VoxelAnimation`

**Key OOP Achievements**:
1. **Encapsulation** hides implementation complexity behind clean public interfaces
2. **Inheritance** enables code reuse across voxel shapes and statement types
3. **Polymorphism** allows uniform treatment of different voxel types and statement nodes
4. **Abstraction** defines clear contracts for shapes, statements, and values

**Strengths**:
- Modular design facilitates testing and extension
- Custom interpreter allows educational control over allowed language features
- Real-time visual feedback makes learning engaging
- Persistent storage tracks user progress

**Potential Future Enhancements**:
- Additional shape types (torus, pyramid)
- More complex language features (arrays, string manipulation)
- Collaborative puzzle solving
- Custom level editor for instructors

This project effectively demonstrates how OOP principles, combined with 3D graphics and language design, can create an interactive, educational tool that makes programming concepts tangible and engaging.

---