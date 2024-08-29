import eu.mihosoft.vrl.v3d.*

// ProgrammaticFrameBacklighting.groovy

def painting_thickness_z = 6.1
def painting_edge_y = 4


def led_cutout_y = 0.12*25.4 // too small? smallest possible
//def led_cutout_y = 5
def led_cutout_z = 0.31*25.4

def lip_z = 1.5

def side_thickness_y = 2
def back_thickness_z = 3
def front_thickness_z = 5

def front_back_space_y = 0.1
def front_back_space_z = 0.1

def rim_y = 1.5
def dovetail_y = 1
def extra_y = painting_edge_y - dovetail_y - rim_y + front_back_space_y

def inset_z = 2

def c_clamp_spring_x = 1.5
def c_clamp_breadth_y = side_thickness_y
def c_clamp_depth_z = 6

def section_x = 30

def frame_back_x = section_x
def frame_back_y = side_thickness_y + led_cutout_y + rim_y + dovetail_y + extra_y
def frame_back_z =  inset_z + 2 + led_cutout_z + back_thickness_z

def frame_front_x = section_x
def frame_front_y = frame_back_y + front_back_space_y + side_thickness_y
def frame_front_z = inset_z + painting_thickness_z + led_cutout_z + back_thickness_z + front_back_space_z + front_thickness_z

def battery_x = 2.68*25.4
def battery_y = 2.535*25.4
def battery_z = 0.76*25.4

//back plate
CSG trench_frame_back = new Cube(frame_back_x, frame_back_y, back_thickness_z).toCSG()
								.toZMin()
								.toYMin()
//side panel
trench_frame_back = trench_frame_back.union(new Cube(frame_back_x, side_thickness_y, frame_back_z).toCSG()
								.toZMin()
								.toYMax()
								.movey(frame_back_y))
//rim
trench_frame_back = trench_frame_back.union(new Cube(frame_back_x, rim_y, lip_z).toCSG()
								.toZMin()
								.movez(back_thickness_z)
								.toYMax()
								.movey(frame_back_y-side_thickness_y-led_cutout_y))
//dovetail
trench_frame_back = trench_frame_back.union(new Wedge(dovetail_y, frame_back_x, lip_z).toCSG()
								.rotz(270)
								.rotx(180)
								.toZMin()
								.movez(back_thickness_z)
								.toYMax()
								.movey(frame_back_y-side_thickness_y-led_cutout_y-rim_y)
								.moveToCenterX())
// move to make y zero be the edge of diffusion paper
trench_frame_back = trench_frame_back.movey(-dovetail_y)

//front plate
CSG trench_frame_front = new Cube(frame_front_x, frame_front_y, front_thickness_z).toCSG()
								.toZMax()
								.movez(frame_front_z)
								.toYMin()
trench_frame_front = trench_frame_front.union(new Cube(frame_front_x, led_cutout_y-front_back_space_y, painting_thickness_z+front_back_space_z+front_thickness_z).toCSG()
								.toZMax()
								.movez(frame_front_z)
								.toYMax()
								.movey(frame_back_y-side_thickness_y-front_back_space_y))
trench_frame_front = trench_frame_front.union(new Cube(frame_front_x, side_thickness_y*2 + front_back_space_y*2, frame_front_z- frame_back_z- front_back_space_z).toCSG()
								.toZMax()
								.movez(frame_front_z)
								.toYMax()
								.movey(frame_front_y))
trench_frame_front = trench_frame_front.union(new Cube(frame_front_x, side_thickness_y, frame_front_z).toCSG()
								.toZMin()
								.toYMax()
								.movey(frame_front_y))
// move to make y zero be the edge of diffusion paper
trench_frame_front = trench_frame_front.movey(-dovetail_y)

//middle spacer
CSG trench_frame_mid = new Cube(frame_front_x,led_cutout_y+rim_y-front_back_space_y,frame_front_z-led_cutout_z-back_thickness_z-front_back_space_z*3-painting_thickness_z-front_thickness_z).toCSG()
								.toZMax()
								.movez(frame_front_z-front_back_space_z*2-painting_thickness_z-front_thickness_z)
								.toYMax()
								.movey(frame_back_y-side_thickness_y-front_back_space_y)
trench_frame_mid = trench_frame_mid.union(new Wedge(rim_y-front_back_space_y, frame_front_x, lip_z).toCSG()
								.rotz(270)
								.rotx(180)
								.toZMax()
								.movez(led_cutout_z+back_thickness_z+front_back_space_z)
								.toYMax()
								.movey(frame_back_y-side_thickness_y-led_cutout_y-front_back_space_y)
								.moveToCenterX())
// move to make y zero be the edge of diffusion paper
trench_frame_mid = trench_frame_mid.movey(-dovetail_y)

// double-sided c-clamp snap fit joint connecting front and back
CSG cclamp = new Cube(c_clamp_spring_x, c_clamp_breadth_y, c_clamp_depth_z).toCSG()
								.toZMin()
								.toYMin()
								.movex(-5)
CSG clamp_wedge = new Wedge(0.75,c_clamp_breadth_y,1).toCSG()
								.rotz(180)
								.toZMax()
								.movez(cclamp.maxZ)
								.toXMax()
								.movex(cclamp.minX)
								.toYMin()
cclamp = cclamp.union(clamp_wedge, clamp_wedge.mirrorz().toZMax().movez(clamp_wedge.minZ))
cclamp = cclamp.union(cclamp.mirrorx())
cclamp = cclamp.toYMax()
				.movey(frame_back_y)
				.movez(frame_back_z)
// move to make y zero be the edge of diffusion paper
cclamp = cclamp.movey(-dovetail_y)

// attach the c clamp to the back panel
trench_frame_back = trench_frame_back.union(cclamp)

// take a diff of the c clamp, to create pockets in the front panel
trench_frame_front = trench_frame_front.difference(trench_frame_back)

// model the painting, for reference
CSG painting = new Cube(section_x, painting_edge_y, painting_thickness_z).toCSG()
								.toZMax()
								.movez(inset_z + painting_thickness_z + led_cutout_z + back_thickness_z)
								.toYMin()
								.setColor(javafx.scene.paint.Color.DARKRED)
// move to make y zero be the edge of diffusion paper
painting = painting.movey(-dovetail_y)

// model the LED strip, for reference
CSG led = new Cube(section_x, led_cutout_y, led_cutout_z).toCSG()
								.toZMin()
								.movez(back_thickness_z)
								.toYMax()
								.movey(frame_back_y-side_thickness_y)
								.setColor(javafx.scene.paint.Color.CYAN)
// move to make y zero be the edge of diffusion paper
led = led.movey(-dovetail_y)

// battery box
CSG battery_box = new Cube(battery_x,battery_y, battery_z).toCSG()
							.movex(300)

return [trench_frame_back, trench_frame_mid, trench_frame_front, led, painting]
//return cclamp