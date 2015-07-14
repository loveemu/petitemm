@setlocal
@set PMMOPTS=
@if "%~1"=="" @goto usage
@for %%a in (%*) do @java -jar "%~dp0PetiteMM.jar" %PMMOPTS% "%%~fa"
@goto heaven
:usage
@echo Note: Drag and drop MIDI files into the batch script
@echo.
@java -jar "%~dp0PetiteMM.jar"
:heaven
@endlocal
@pause
