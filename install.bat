@echo off

jpackage --copyright "Copyright kowalski code generator 2023, All rights reserved" --input target/ --dest . --name kcg --main-jar kcg-1.0.0-jar-with-dependencies.jar --main-class br.com.akowalski.Main --type exe --app-version 1.0.0 --win-console
