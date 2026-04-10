#!/bin/bash
set -e

Xvfb :1 -screen 0 1280x800x24 -ac +extension GLX +render -noreset &
export DISPLAY=:1
sleep 1

x11vnc -display :1 -nopw -listen 0.0.0.0 -rfbport 5900 -forever -shared -quiet &
sleep 1

websockify --web=/usr/share/novnc/ 6082 localhost:5900 &

echo "============================================"
echo "  IPOS-SA GUI → http://localhost:6082/vnc.html"
echo "  VNC direct   → localhost:5900"
echo "  API server   → http://localhost:8081"
echo "============================================"

exec java \
  -Djava.awt.headless=false \
  -Dawt.useSystemAAFontSettings=on \
  -Dswing.defaultlaf=javax.swing.plaf.metal.MetalLookAndFeel \
  -jar app.jar 2>&1 | tee /tmp/java.log