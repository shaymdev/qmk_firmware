Trying to get my feather bluefruit 32u4 working with qmk.

So far I have it working over usb, and it shows up as a bluetooth device but I can't seem to connect to it.

I followed the standard process for building the hex (from the root qmk dir called 'make handwired-onekey').
To program from my windows computer I have an old download of arduino IDE (probably work with just the avr dude tool but it's what I have. Here's my command:

C:\Users\smerrill\personal\dev\keyboards\arduino-1.6.9\hardware\tools\avr/bin/avrdude -CC:\Users\smerrill\personal\dev\keyboards\arduino-1.6.9\hardware\tools\avr/etc/avrdude.conf -v -patmega32u4 -cavr109 -PCOM4 -b57600 -D -Uflash:w:C:\Users\smerrill\personal\dev\keyboards\qmk_firmware\handwired_onekey_default.hex:i

Com4 is the port that the device shows as after pushing the reset button (it then switches to com5) so push the button the run the command.


