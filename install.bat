@echo off

jpackage --copyright "Copyright kcode 2023, All rights reserved" --input target/ --dest . --name kcode --main-jar kcode-1.0.0-jar-with-dependencies.jar --main-class br.com.akowalski.Main --type exe --app-version 1.0.0 --win-console
