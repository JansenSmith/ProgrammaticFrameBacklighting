import eu.mihosoft.vrl.v3d.*
import eu.mihosoft.vrl.v3d.svg.*

// ProgrammaticFrameBacklighting.groovy

def is_prototype_only = 0

def painting_thickness_z = 6.1
def painting_edge_y = 4


def led_cutout_y = 0.12*25.4 // too small? smallest possible
//def led_cutout_y = 5
def led_cutout_z = 0.31*25.4

def lip_z = 1.5

def side_thickness_y = 2
def back_thickness_z = 1
def front_thickness_z = 5

def front_back_space_y = 0.1
def front_back_space_z = 0.1

def mid_seat_y = 2
def rim_y = 1.5
def dovetail_y = 1
def extra_y = painting_edge_y - dovetail_y - rim_y + front_back_space_y

def shift_y = -dovetail_y

def inset_z = 1

def c_clamp_spring_x = 1.5
def c_clamp_breadth_y = side_thickness_y
def c_clamp_depth_z = 6

def c_clamp_tab_hard_stop_distance = 1

def section_x = 30

def frame_back_x = section_x
def frame_back_y = side_thickness_y + led_cutout_y + mid_seat_y + rim_y + dovetail_y + extra_y
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
//back panel, side panel
trench_frame_back = trench_frame_back.union(new Cube(frame_back_x, side_thickness_y, frame_back_z).toCSG()
								.toZMin()
								.toYMax()
								.movey(frame_back_y))
//back panel, seat for mid plate
trench_frame_back = trench_frame_back.union(new Cube(frame_back_x, mid_seat_y, back_thickness_z+led_cutout_z).toCSG()
								.toZMax()
								.movez(back_thickness_z+led_cutout_z)
								.toYMax()
								.movey(frame_back_y-side_thickness_y))
//back panel, square of dovetail
trench_frame_back = trench_frame_back.union(new Cube(frame_back_x, rim_y, lip_z).toCSG()
								.toZMin()
								.movez(back_thickness_z)
								.toYMax()
								.movey(frame_back_y-side_thickness_y-led_cutout_y-mid_seat_y))
//back panel, wedge of dovetail
trench_frame_back = trench_frame_back.union(new Wedge(dovetail_y, frame_back_x, lip_z).toCSG()
								.rotz(270)
								.rotx(180)
								.toZMin()
								.movez(back_thickness_z)
								.toYMax()
								.movey(frame_back_y-side_thickness_y-led_cutout_y-mid_seat_y-rim_y)
								.moveToCenterX())
// back panel, move to make y zero be the edge of diffusion paper
trench_frame_back = trench_frame_back.movey(shift_y)

//front panel, down to painting
CSG trench_frame_front = new Cube(frame_front_x, frame_front_y, front_thickness_z).toCSG()
								.toZMax()
								.movez(frame_front_z)
								.toYMin()
//front panel, down to mid plate
trench_frame_front = trench_frame_front.union(new Cube(frame_front_x, led_cutout_y+mid_seat_y-front_back_space_y, painting_thickness_z+front_back_space_z+front_thickness_z).toCSG()
								.toZMax()
								.movez(frame_front_z)
								.toYMax()
								.movey(frame_back_y-side_thickness_y-front_back_space_y))
//front panel, down to back panel side plate
trench_frame_front = trench_frame_front.union(new Cube(frame_front_x, side_thickness_y*2 + front_back_space_y*2, frame_front_z- frame_back_z- 2*front_back_space_z).toCSG()
								.toZMax()
								.movez(frame_front_z)
								.toYMax()
								.movey(frame_front_y))
//front panel, side panel
trench_frame_front = trench_frame_front.union(new Cube(frame_front_x, side_thickness_y, frame_front_z).toCSG()
								.toZMin()
								.toYMax()
								.movey(frame_front_y))
trench_frame_front = trench_frame_front.difference(new Wedge(front_thickness_z*(2/3),frame_front_x,front_thickness_z*(1/2)).toCSG()
														.rotz(270)
														.rotx(90)
														.toZMax()
														.movez(trench_frame_front.maxZ)
														.toYMin()
														.movey(trench_frame_front.minY)
														.moveToCenterX())
//front panel, move to make y zero be the edge of diffusion paper
trench_frame_front = trench_frame_front.movey(shift_y)

//middle spacer
CSG trench_frame_mid = new Cube(frame_front_x,led_cutout_y+mid_seat_y+rim_y-front_back_space_y,frame_front_z-led_cutout_z-back_thickness_z-front_back_space_z*3-painting_thickness_z-front_thickness_z).toCSG()
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
								.movey(frame_back_y-side_thickness_y-led_cutout_y-mid_seat_y-front_back_space_y)
								.moveToCenterX())
// move to make y zero be the edge of diffusion paper
trench_frame_mid = trench_frame_mid.movey(shift_y)

// double-sided c-clamp snap fit joint connecting front and back
CSG clamp_bars = new Cube(c_clamp_spring_x, c_clamp_breadth_y, c_clamp_depth_z).toCSG()
								.toZMin()
								.toYMax()
								.movey(frame_back_y)
								.movez(frame_back_z)
								.movex(-5)
clamp_bars = clamp_bars.union(clamp_bars.mirrorx())
								.movey(shift_y)
								//.hull()
CSG clamp_wedge = new Wedge(0.75,c_clamp_breadth_y,1).toCSG()
								.toZMax()
								.movez(clamp_bars.maxZ)
								.toXMin()
								.movex(clamp_bars.maxX)
								.toYMax()
								.movey(clamp_bars.maxY)
clamp_wedge = clamp_wedge.union(clamp_wedge.mirrorz()
								.scalez(0.6)
								.toZMax()
								.movez(clamp_wedge.minZ))
clamp_wedge = clamp_wedge.union(clamp_wedge.mirrorx())
CSG cclamp = clamp_bars.union(clamp_wedge)
// move to make y zero be the edge of diffusion paper
//cclamp = cclamp.movey(shift_y)
//cclamp = cclamp.movey(shift_y+3)

//CSG clamp_hard_stop = new Cube(clamp_bars.maxX - c_clamp_spring_x- c_clamp_tab_hard_stop_distance, cclamp.totalY, cclamp.totalZ).toCSG()
CSG clamp_hard_stop = new Cube( 2 * (clamp_bars.maxX - c_clamp_spring_x - c_clamp_tab_hard_stop_distance), cclamp.totalY, cclamp.maxZ).toCSG()
								.toZMin()
								.toYMax()
								.movey(cclamp.maxY)

// attach the c clamp to the back panel
trench_frame_back = trench_frame_back.union(cclamp.movez(-0.35-front_back_space_z), clamp_hard_stop) // try 0.3 if tabs break

// take a diff of the c clamp, to create pockets in the front panel
CSG pocket = cclamp.union(clamp_bars.hull()).toolOffset(0.5)
trench_frame_front = trench_frame_front.difference(pocket)
//trench_frame_front = trench_frame_front.minkowskiDifference(cclamp.hull(), 0.91)

// model the painting, for reference
CSG painting = new Cube(section_x, painting_edge_y, painting_thickness_z).toCSG()
								.toZMax()
								.movez(inset_z + painting_thickness_z + led_cutout_z + back_thickness_z)
								.toYMin()
// move to make y zero be the edge of diffusion paper
painting = painting.movey(shift_y)

// model the LED strip, for reference
CSG led = new Cube(section_x, led_cutout_y, led_cutout_z).toCSG()
								.toZMin()
								.movez(back_thickness_z)
								.toYMax()
								.movey(frame_back_y-side_thickness_y-mid_seat_y)
// move to make y zero be the edge of diffusion paper
led = led.movey(shift_y)

// battery box
CSG battery_box = new Cube(battery_x,battery_y, battery_z).toCSG()
							.movex(300)

trench_frame_back.setColor(javafx.scene.paint.Color.WHITESMOKE)
					.setName("frame_back")
					.setManufacturing({ toMfg ->
						return toMfg
								//.rotx(90)// fix the orientation
								.toZMin()//move it down to the flat surface
					})
					
led.setColor(javafx.scene.paint.Color.CYAN)
					.setName("led_strip")
					.addAssemblyStep(1, new Transform().movez(15))
					.setManufacturing({ toMfg ->
						return toMfg
								//.rotx(90)// fix the orientation
								.toZMin()//move it down to the flat surface
					})
					.addAssemblyStep(inset_z, null)
					
trench_frame_mid.setColor(javafx.scene.paint.Color.SILVER)
					.setName("frame_mid")
					.addAssemblyStep(2, new Transform().movez(20))
					.setManufacturing({ toMfg ->
						return toMfg
								.rotx(180)// fix the orientation
								.toZMin()//move it down to the flat surface
					})
					
painting.setColor(javafx.scene.paint.Color.DARKRED)
					.setName("painting_edge")
					.addAssemblyStep(3, new Transform().movez(25))
					.setManufacturing({ toMfg ->
						return toMfg
								//.rotx(90)// fix the orientation
								.toZMin()//move it down to the flat surface
					})
					
trench_frame_front.setColor(javafx.scene.paint.Color.web('#505050'))
					.setName("frame_front")
					.addAssemblyStep(4, new Transform().movez(35))
					.setManufacturing({ toMfg ->
						return toMfg
								.rotx(180)// fix the orientation
								.toZMin()//move it down to the flat surface
					})
					
battery_box = battery_box.setColor(javafx.scene.paint.Color.YELLOW)
					.setName("battery_box")
					.addAssemblyStep(4, new Transform().movez(0))
					.setManufacturing({ toMfg ->
						return toMfg
								//.rotx(90)// fix the orientation
								.toZMin()//move it down to the flat surface
					})

if(is_prototype_only)
	return [trench_frame_back, trench_frame_mid, trench_frame_front, led, painting]

// code to slice the frame at X=0 and extrude 
CSG frame_section = trench_frame_front.union(trench_frame_mid, led, painting, trench_frame_back) 

//def slice_trans = new Transform().roty(90).movez(1)
def slice_trans = new Transform().movez(1)
List<Polygon> polys = Slice.slice(frame_section,slice_trans,0).collect{it.transformed(slice_trans)}

String url = "https://github.com/JansenSmith/ProgrammaticFrameBacklighting.git"
String filename="frame_slice.svg"
File earCoreFile = ScriptingEngine.fileFromGit(url,
		filename);
SVGExporter svg = new SVGExporter();
		
for( Polygon p: polys){
	svg.toPolyLine(p);
	svg.colorTick();
}
println "Pushing changes"
ScriptingEngine.pushCodeToGit(url, "main", filename, svg.make(), "Making Cached SVG "+filename, true)
ArrayList<Polygon> frame_slice=new ArrayList<Polygon>();

SVGLoad l=new SVGLoad(earCoreFile.toURI())
for(String s:l.getLayers()) {
	frame_slice.addAll(l.getPolygonByLayers().get(s))
}

//return frame_slice
return [frame_section.movez(-30),frame_slice]

