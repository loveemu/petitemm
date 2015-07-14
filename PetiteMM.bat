@setlocal
@set PMMOPTS=
@if "%~1"=="" @goto usage
@for %%a in (%*) do @java -jar "%~dp0PetiteMM.jar" %PMMOPTS% "%%~fa"
@goto heaven
:usage
@java -jar "%~dp0PetiteMM.jar"
:heaven
@endlocal
@pause
