# CONTENTS

This package contains the NetLogo Wiimote extension.
It uses Bluecove 2.1.0 to enable your computer to 
communicate via Java with your wiimote through Bluetooth.
It uses WiiRemoteJ 1.6 to interface with the sensors and
output devices on your wiimote.

Bluecove: BlueCove.org
WiiRemoteJ: http://www.world-of-cha0s.hostrocket.com/WiiRemoteJ/

The NetLogo Wiimote Extension is written and maintained
by Michelle Wilkerson-Jerde, Gregory Dam, and Nathan Holbert.

# INSTRUCTIONS

This extension is intended for use with NetLogo. To 
use it, move NetLogo-Wiimote-Extension to the "extensions"
directory of your NetLogo installation. 

To get this working with a version of NetLogo besides the one
it is currently set for, recompile from within the directory:

jar cvfm wii.jar manifest.txt -C classes .

# REQUIREMENTS

This extension has been tested on Mac OSX.

