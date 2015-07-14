PetiteMM
========

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

You can run PetiteMM manually by `java -jar PetiteMM.jar (arguments)`

There are some conversion options. Run PetiteMM with no arguments for the list of options.

Special Thanks
--------------

- TinyMM: a similar converter, PetiteMM will never be created without it.
