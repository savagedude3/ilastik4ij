package org.ilastik.ilastik4ij_Syn_Bot.ui;

import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import ij.IJ;

import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;

import org.ilastik.ilastik4ij_Syn_Bot.ui.Syn_Bot_Utils.*;

/**
 * Main class.
 * This class is a plugin for the ImageJ interface for calculating colocalizations within
 * the Syn_Bot macro
 *
 * 
 * @author Justin Savage (justin.savage at duke.edu)
 *
 */
@Plugin(type = Command.class, headless = true, menuPath = "Plugins>Syn_Bot>Syn_Bot_Triple")
public class Syn_Bot_Triple implements Command {

	@Override
	public void run() {

		if (IJ.isResultsWindow() == false) {
			//TODO throw error 
		}

		//Analyzer a = new Analyzer();
		ResultsTable rt = Analyzer.getResultsTable(); // get the system results table
		double[] redX = rt.getColumnAsDoubles(rt.getColumnIndex("redX"));
		double[] redY = rt.getColumnAsDoubles(rt.getColumnIndex("redY"));
		double[] redR = rt.getColumnAsDoubles(rt.getColumnIndex("redR"));
		double[] greenX = rt.getColumnAsDoubles(rt.getColumnIndex("greenX"));
		double[] greenY = rt.getColumnAsDoubles(rt.getColumnIndex("greenY"));
		double[] greenR = rt.getColumnAsDoubles(rt.getColumnIndex("greenR"));
		double[] blueX = rt.getColumnAsDoubles(rt.getColumnIndex("blueX"));
		double[] blueY = rt.getColumnAsDoubles(rt.getColumnIndex("blueY"));
		double[] blueR = rt.getColumnAsDoubles(rt.getColumnIndex("blueR"));

		int redCount = redX.length;
		int greenCount = greenX.length;
		int blueCount = blueX.length;
		
		//the maximum possible number of colocs is the sum of the individual puncta
		int maxColocs = Math.max(Math.max((redCount + greenCount), (redCount + blueCount)), (greenCount + blueCount));
		
		

		double[] colocX = new double[maxColocs];
		double[] colocY = new double[maxColocs];
		double[] colocAreaArray = new double[maxColocs];
		int colocCount = 0; 

		//for each red puncta
		for (int i = 0; i < redCount; i++) {

			//for each green puncta
			for (int j = 0; j < greenCount; j++) {
				
				//for each blue puncta
				for (int k = 0; k < blueCount; k++) {
				
					Puncta currentPuncta = tripleColoc(redX[i], redY[i], redR[i], greenX[j], greenY[j], greenR[j], blueX[k], blueY[k], blueR[k]);
					if (currentPuncta.area > 0) {
						//stores the x and y coordinates of the colocalized puncta
						colocX[colocCount] = currentPuncta.x;
						colocY[colocCount] = currentPuncta.y;
						colocAreaArray[colocCount] = currentPuncta.area;
						colocCount = colocCount + 1;
					}
				}
			}
		}

		int colocXTrueLength = 0;
		for (int i = 0; i < colocX.length; i++) {
			if (colocX[i] != 0) {
				colocXTrueLength = i;
			}
			else {
				break;
			}
		}

		int colocYTrueLength = 0;
		for (int i = 0; i < colocY.length; i++) {
			if (colocY[i] != 0) {
				colocYTrueLength = i;
			}
			else {
				break;
			}
		}

		double[] colocXOut = new double[colocXTrueLength];
		double[] colocYOut = new double[colocYTrueLength];

		for(int i = 0; i < colocXTrueLength; i++) {
			colocXOut[i] = colocX[i];
		}

		for(int i = 0; i < colocYTrueLength; i++) {
			colocYOut[i] = colocY[i];
		}

		ResultsTable rtOut = new ResultsTable();

		for(int n = 0; n < colocXOut.length; n++) {
			rtOut.incrementCounter();
			rtOut.addValue("n", n);
			rtOut.addValue("colocX", colocXOut[n]);
			rtOut.addValue("colocY", colocYOut[n]);
			rtOut.addValue("colocArea", colocAreaArray[n]);
		}
		
		IJ.renameResults("Results", "oldResults");
		
		rtOut.show("Results");
	}

	
	public static Puncta tripleColoc(double xa, double ya, double ra, double xb, double yb, double rb, double xc, double yc, double rc) {
		
	    Puncta colocABC = new Puncta(0, 0, 0);
	    
	    double d_AB = Math.sqrt(Math.pow((yb - ya), 2) + Math.pow(xb - xa, 2));
		double d_AC = Math.sqrt(Math.pow((yc - ya), 2) + Math.pow(xc - xa, 2));
		double d_BC = Math.sqrt(Math.pow((yc - yb), 2) + Math.pow(xc - xb, 2));
        
        //if red and green coloc 
		if (d_AB < ra + rb){
		    //if red and blue coloc
		    if (d_AC < ra + rc){
		        //if green and blue coloc
		        if (d_BC < rb + rc){
		        	
		        	Puncta colocAB = Syn_Bot_Utils.colocArea(xa, ya, ra, xb, yb, rb);
		            Puncta colocAC = Syn_Bot_Utils.colocArea(xa, ya, ra, xc, yc, rc);
        			Puncta colocBC = Syn_Bot_Utils.colocArea(xb, yb, rb, xc, yc, rc);
        			System.out.println("colocAB is: " + colocAB.x + ", " + colocAB.y + " area: " + colocAB.area);
		            System.out.println("colocAC is: " + colocAC.x + ", " + colocAC.y + " area: " + colocAC.area);
		            System.out.println("colocBC is: " + colocBC.x + ", " + colocBC.y + " area: " + colocBC.area);
		
        			//draw line between center of A and colocBC
        			//line is y = slope * x + intercept 
        			//slope is deltaY/deltaX
        			//Point-Slope equation y - y1 = slope * (x - x1)
        			//rearrange Point-Slope and set y to 0 for intercept
        			double slopeABC = (colocBC.y - ya)/(colocBC.x - xa);
        			double interceptABC = slopeABC*(-xa) + ya;
        			double slopeBAC = (colocAC.y - yb)/(colocAC.x - xb);
        			double interceptBAC = slopeBAC*(-xb) + yb;
        			double slopeCAB = (colocAB.y - yc)/(colocAB.x - xc);
        			double interceptCAB = slopeCAB*(-xc) + yc;
        			
        			colocABC.x = (interceptBAC - interceptABC)/(slopeABC - slopeBAC);
        			colocABC.y = slopeABC * colocABC.x + interceptABC;
        			//not sure how to calculate tripleColoc area
        			//setting it to 1 so we can easily differentiate from non-colocs
        			colocABC.area = 1;
		        }
		    }
		}
		return colocABC;
	}
	
}

