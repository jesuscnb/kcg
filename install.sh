#!/bin/bash

mvn clean install

jpackage --copyright "Open Source Kowalski Code Generator 2023" --input target/ --dest . \
--name kcg --main-jar kcg-1.0.0-jar-with-dependencies.jar --main-class br.com.akowalski.Main \
--type deb --app-version 1.0.0

sudo dpkg -i kcg*.deb

sudo ln -s /opt/kcg/bin/kcg /usr/bin/kcg
sudo chmod +x /usr/bin/kcg

rm kcg*.deb


