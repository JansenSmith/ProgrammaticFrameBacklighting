import eu.mihosoft.vrl.v3d.*

// ProgrammaticFrameBacklighting.groovy

painting_z = 6.1

inset_z = 5

//led_cutout_y = 0.12*25.4 // too small, smallest possible
led_cutout_y = 5
led_cutout_z = 0.31*25.4

lip_z = 1.5

side_thickness_y = 2
back_thickness_z = 3

rim_y = 3

dovetail_y = 2

section_x = 50

battery_x = 2.68*25.4
battery_y = 2.535*25.4
battery_z = 0.76*25.4

frame_box_x = section_x
frame_box_y = side_thickness_y + led_cutout_y + rim_y + dovetail_y
frame_box_z =  inset_z + painting_z + led_cutout_z + back_thickness_z

//bottom plate
CSG trench_frame_back = new Cube(frame_box_x, frame_box_y, back_thickness_z).toCSG()
								.toZMin()
								.toYMin()
//side panel
trench_frame_back = trench_frame_back.union(new Cube(frame_box_x, side_thickness_y, frame_box_z).toCSG()
								.toZMin()
								.toYMax()
								.movey(frame_box_y))
//rim
trench_frame_back = trench_frame_back.union(new Cube(frame_box_x, rim_y, lip_z).toCSG()
								.toZMin()
								.movez(back_thickness_z)
								.toYMax()
								.movey(frame_box_y-side_thickness_y-led_cutout_y))
//dovetail
trench_frame_back = trench_frame_back.union(new Wedge(dovetail_y, frame_box_x, lip_z).toCSG()
								.rotz(270)
								.rotx(180)
								.toZMin()
								.movez(back_thickness_z)
								.toYMax()
								.movey(frame_box_y-side_thickness_y-led_cutout_y-rim_y)
								.moveToCenterX())
// move to make y zero be the edge of diffusion paper
trench_frame_back = trench_frame_back.movey(-dovetail_y)


CSG battery_box = new Cube(battery_x,battery_y, battery_z).toCSG()
							.movex(300)

return trench_frame_back