// ProgrammaticFrameBacklighting.groovy

battery_x = 2.95*25.4
battery_y = 1.9*25.4
battery_z = 0.67*25.4

CSG battery_box = new Cube(battery_x,battery_y, battery_z).toCSG()