package org.ilastik.ilastik4ij_Syn_Bot.ui;

public class Syn_Bot_Utils {
	
	//Calculates the intersectional area of two puncta to get the area of the colocalization
		//based on https://www.xarg.org/2016/07/calculate-the-intersection-area-of-two-circles/ 
		public static Puncta colocArea (double xa, double ya, double ra, double xb, double yb, double rb) {
			
			Puncta currentPuncta = new Puncta(0,0,0);
			
			double d = Math.sqrt(Math.pow((yb - ya), 2) + Math.pow(xb - xa, 2));

			if (d < ra + rb) {

				double a = ra * ra;
				double b = rb * rb;

				double x = (a - b + d * d) / (2.0 * d);
				double z = x * x;
				double y = Math.sqrt(a - z);
				
				//the angle above the horizontal of the line between red and green
		        double theta_AtoB = Math.tan((yb - ya)/(xb - xa));
		        
		        //tangent undefined right above or below
		        if(xb - xa == 0){
		            if (yb - ya > 0){
		                theta_AtoB = Math.PI/2.0;
		            }
		            if (yb - ya < 0){
		                theta_AtoB = Math.PI*3.0/2.0;
		            }
		        }
		       
		        currentPuncta.x = xa + x * Math.cos(theta_AtoB);
		        currentPuncta.y = ya + x * Math.sin(theta_AtoB);
		        
		        //if circles completely overlap, coloc is the smaller circle
		        if (d <= Math.abs(rb - ra)) {
				    if (ra < rb){
				        currentPuncta.area = Math.PI * a;
					    currentPuncta.x = xa;
					    currentPuncta.y = ya;
				    }
				    if (ra > rb){
				        currentPuncta.area = Math.PI * b;
					    currentPuncta.x = xb;
					    currentPuncta.y = yb;
				    }
					
				}
		        // if colocs don't completely overlap, calculate coloc area
				else {
				    currentPuncta.area = a * Math.asin(y / ra) + b * Math.asin(y / rb) - y * (x + Math.sqrt(z + b - a));
				}
			}
			return currentPuncta;
		}
	
	public static class Puncta {
	    public double x = 0;
	    public double y = 0;
	    public double area = 0;
	    //constructor
	    public Puncta(double x_coord, double y_coord, double area_value) {
	        x = x_coord;
	        y = y_coord;
	        area = area_value;
	    }
	}

}
