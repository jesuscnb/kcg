@echo off

jpackage --copyright "Open Source Kowalski Code Generator 2023" --input target/ --dest . --name kcg --main-jar kcg-1.0.0-jar-with-dependencies.jar --main-class br.com.akowalski.Main --type exe --app-version 1.0.0 --win-console
