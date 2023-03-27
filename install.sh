#!/bin/bash

mvn clean install

jpackage --copyright "Copyright kcode 2023, All rights reserved" --input target/ --dest . \
--name kcode --main-jar kcode-1.0.0-jar-with-dependencies.jar --main-class br.com.akowalski.Main \
--type deb --app-version 1.0.0

sudo dpkg -i kcode*.deb

sudo ln -s /opt/kcode/bin/kcode /usr/bin/kcode
sudo chmod +x /usr/bin/kcode

rm kcode*.deb


