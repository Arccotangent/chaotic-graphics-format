# Chaotic Graphics Format

Chaotic Graphics Format (CGF) is an advanced, but very compact format that can be used to generate graphics on-the-fly with small variations.

The idea for CGF came from the Numberphile video here: [Chaos Game - Numberphile](https://www.youtube.com/watch?v=kbKtFN71Lfs)

CGF is in alpha development and is not suitable for production use at the moment.

CGF is licensed under the GNU AGPLv3. The full license text can be found in LICENSE.md in this repository.

## Usage

CGF consists of two parts: the CGF data file and the image generator.

### Basic Info About CGF

Examples of what CGF is capable of:
* Scalable graphics
* Variable graphics

Examples of what CGF is NOT capable of:
* Bitmap pattern storage
* Use with cameras or other photographic equipment

CGF files must be created from scratch. The format is very simple.

### Basic CGF Format

The current CGF (version 1) basic file format is as follows:

```
[CGF:version]

[ITERATIONS: itercount]

[SIZE: x, y]

[POINTS] 
    [A: x1, y1]
    [B: x2, y2]
    [C: x3, y3]
    ...
[END-POINTS]

[PROPORTIONS]
    [A: prop1]
    [B: prop2]
    [C: prop3]
    ...
[END-PROPORTIONS]

[END-CGF]
```

Size is in pixels, x is width, and y is height.

Proportion names signify their target point.

Invalid names for points (and consequently proportions) currently include:
* `CGFSEEDPOINT`

Every point must have a corresponding proportion and vice versa.
Lingering points or proportions will cause a parser error.

Valid proportion values range from 0.0 to 1.0, inclusive.

A value of 0.0 means the point will not move from its position.
A value of 0.5 means the point will move halfway to the target point. 
A value of 1.0 means the point will move to the target point's exact position.

Example:

```
//this is an example CGF file
[CGF:1]

[ITERATIONS: 2000]

[SIZE: 300, 300]

[POINTS]
    [A: 0, 0]
    [B: 150, 300]
    [C: 300, 0]
[END-POINTS]

[PROPORTIONS]
    [A: 0.5]
    [B: 0.5]
    [C: 0.5]
[END-PROPORTIONS]

[END-CGF]
```

This CGF file will produce an equilateral Sierpinski triangle in a 300x300 image.

## Building and Using the Generator

### Compiling from Source

Dependencies are managed by Gradle. Native images are built using GraalVM.
If you don't have GraalVM installed, the script will download it for you.

To build an executable JAR from the source root:

`./gradlew build` - *nix

`gradlew build` - Windows

The JAR will be in `build/libs/cgf.jar`

For a native image:

`./gradlew nativeImage` - *nix

`gradlew nativeImage` - Windows

The native binary will be at `build/graal/cgf.bin`

The native build takes longer, but the resulting binary runs faster and doesn't require Java to be installed.
It runs like any other executable or program.

### Usage

First, you must design a graphic.
At the moment, there is no official editor to help you, so you must design it without graphical help.

The iteration count specifies how many points to generate.
This value increases with the image size and should be tuned to your needs.

Too few iterations will produce an unclear image while too many iterations will produce an image so sharp and well defined it would be counterproductive.
So you must use trial and error to choose an iteration count.

## Rudimentary Benchmarks

These are benchmarks from my own PC.

`samples/sierpinski-triangle.cgf` - About 5300-5400 iterations/sec.
