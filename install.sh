#!/bin/bash

mvn clean install

jpackage --copyright "Copyright kcode 2023, All rights reserved" --input target/ --dest . \
--name kcg --main-jar kcg-1.0.0-jar-with-dependencies.jar --main-class br.com.akowalski.Main \
--type deb --app-version 1.0.0

sudo dpkg -i kcg*.deb

sudo ln -s /opt/kcode/bin/kcg /usr/bin/kcg
sudo chmod +x /usr/bin/kcg

rm kcg*.deb


