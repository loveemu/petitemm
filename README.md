PetiteMM
========
[![Travis Build Status](https://travis-ci.com/loveemu/petitemm.svg?branch=master)](https://travis-ci.com/loveemu/petitemm)

PetiteMM is a SMF (MIDI) to MML converter.

Features:

- Supports triplets such as `c12d12e12`
- No polyphonic support, only one note will be converted
- Timings between tracks will never desync like some other converters
- Control changes are *not* supported (they will be ignored)

How To Use
----------

1. Install [Java](http://java.com/download/) Runtime Environment (if you do not have yet)
2. Drag and drop .mid files into PetiteMM.bat, and .mml files will be saved in the input directory

You can run PetiteMM manually by `java -jar PetiteMM.jar (options) input.mid`

### Options

|Option               |Arguments        |Description                                                                       |
|---------------------|-----------------|----------------------------------------------------------------------------------|
|-o                   |[string]filename |Specify the output MML filename.                                                  |
|--dots               |[int]count       |Maximum dot counts allowed for dotted-note, -1 for infinity. (default=-1)         |
|--timebase           |[int]TPQN        |Timebase of target MML, 0 to keep the input timebase. (default=48)                |
|--input-timebase     |[int]TPQN        |Timebase of input sequence, 0 to keep the input timebase. (default=0)             |
|--quantize-precision |[int]length      |Specify the minimum note length for quantization. (example: 64 for 64th note)     |
|--no-quantize        |n/a              |Prevent adjusting note length. Result will be more accurate but more complicated. |
|--octave-reverse     |n/a              |Swap the octave symbol.                                                           |
|--use-triplet        |n/a              |Use triplet syntax if possible. (really not so smart)                             |

Special Thanks
--------------

- TinyMM: a similar converter, PetiteMM will never be created without it.
