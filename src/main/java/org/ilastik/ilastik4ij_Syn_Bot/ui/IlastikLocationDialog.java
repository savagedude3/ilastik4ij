package org.ilastik.ilastik4ij_Syn_Bot.ui;

import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import ij.IJ;

import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.filter.Analyzer;
import fiji.util.gui.GenericDialogPlus;

/**
 * Main class.
 * This class is a plugin for the ImageJ interface for getting ilastik locations
 * the Syn_Bot macro
 *
 * 
 * @author Justin Savage (justin.savage at duke.edu)
 *
 */
@Plugin(type = Command.class, headless = true, menuPath = "Plugins>Syn_Bot>Syn_Bot_Dialog")

public class IlastikLocationDialog implements Command {

	@Override
	public void run() {

		GenericDialogPlus gd = new GenericDialogPlus("Select ilastik files");
		gd.addDirectoryOrFileField("ilastik location", "ilastik.exe");
		gd.addFileField("ilp Red location", "Red_Channel.ilp");
		gd.addFileField("ilp Green location", "Green_Channel.ilp");
		gd.showDialog();
		String ilastikPath = gd.getNextString();
		String ilpRedPath = gd.getNextString();
		String ilpGreenPath = gd.getNextString();
		
		ResultsTable rt = new ResultsTable();

		rt.incrementCounter();
		rt.addValue("paths", ilastikPath);
		rt.incrementCounter();
		rt.addValue("paths", ilpRedPath);
		rt.incrementCounter();
		rt.addValue("paths", ilpGreenPath);
		
		rt.show("Results");
		
	}

	

}


