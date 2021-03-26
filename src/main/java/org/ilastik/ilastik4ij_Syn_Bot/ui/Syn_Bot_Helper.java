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
@Plugin(type = Command.class, headless = true, menuPath = "Plugins>Syn_Bot>Syn_Bot_Helper")
public class Syn_Bot_Helper implements Command {

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

		int redCount = redX.length;
		int greenCount = greenX.length;
		
		//the maximum possible number of colocs is the sum of the individual puncta
		int maxColocs = redCount + greenCount;
		
		

		double[] colocX = new double[maxColocs];
		double[] colocY = new double[maxColocs];
		double[] colocAreaArray = new double[maxColocs];
		int colocCount = 0; 

		//for each red puncta
		for (int i = 0; i < redCount; i++) {

			//for each green puncta
			for (int j = 0; j < greenCount; j++) {
				Puncta currentPuncta = Syn_Bot_Utils.colocArea(redX[i], redY[i], redR[i], greenX[j], greenY[j], greenR[j]);
				if (currentPuncta.area > 0) {
					//stores the x and y coordinates of the colocalized
					//puncta, using midpos to get an integer
					colocX[colocCount] = currentPuncta.x;
					colocY[colocCount] = currentPuncta.y;
					colocAreaArray[colocCount] = currentPuncta.area;
					colocCount = colocCount + 1;
					
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
		//Analyzer.setResultsTable(rt);
		//a.displayResults(); 
	}

}
