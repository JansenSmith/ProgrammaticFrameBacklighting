import eu.mihosoft.vrl.v3d.*

// ProgrammaticFrameBacklighting.groovy

painting_z = 6.1

led_cutout_y = 0.12*25.4
led_cutout_z = 0.31*25.4

lip_z = 1.5

back_thickness_z = 3

section_x = 10


battery_x = 2.95*25.4
battery_y = 1.9*25.4
battery_z = 0.67*25.4

CSG battery_box = new Cube(battery_x,battery_y, battery_z).toCSG()
							.movex(300)
