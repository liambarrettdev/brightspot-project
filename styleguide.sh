#!/usr/bin/env bash
# workaround to replace deprecated method name in dependency
find ./node_modules/bsp-styleguide/lib  -name "server.js" -exec sed -i '' 's/tmpDir/tmpdir/' {} \;
# run styleguide
node/node node_modules/bsp-styleguide/bin/styleguide
