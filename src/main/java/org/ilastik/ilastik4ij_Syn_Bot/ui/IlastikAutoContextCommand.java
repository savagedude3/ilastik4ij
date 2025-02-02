package org.ilastik.ilastik4ij_Syn_Bot.ui;

import net.imagej.Dataset;
import net.imagej.ImgPlus;
import net.imglib2.type.NativeType;

import org.ilastik.ilastik4ij_Syn_Bot.executors.Autocontext;
import org.scijava.ItemIO;
import org.scijava.app.StatusService;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.options.OptionsService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import static org.ilastik.ilastik4ij_Syn_Bot.executors.AbstractIlastikExecutor.PixelPredictionType;

import java.io.File;
import java.io.IOException;

@Plugin(type = Command.class, headless = true, menuPath = "Plugins>Syn_Bot>Run Autocontext Prediction for Syn_Bot")
public class IlastikAutoContextCommand implements Command {

    @Parameter
    public LogService logService;

    @Parameter
    public StatusService statusService;

    @Parameter
    public OptionsService optionsService;

    @Parameter
    public UIService uiService;

    @Parameter(label = "Trained ilastik project file")
    public File projectFileName;

    @Parameter(label = "Raw input image")
    public Dataset inputImage;

    @Parameter(label = "Output type", choices = {UiConstants.PIXEL_PREDICTION_TYPE_PROBABILITIES, UiConstants.PIXEL_PREDICTION_TYPE_SEGMENTATION}, style = "radioButtonHorizontal")
    public String AutocontextPredictionType;

    @Parameter(type = ItemIO.OUTPUT)
    private ImgPlus<? extends NativeType<?>> predictions;

    public IlastikOptions ilastikOptions;

    /**
     * Run method that calls ilastik
     */
    @Override
    public void run() {

        if (ilastikOptions == null)
            ilastikOptions = optionsService.getOptions(IlastikOptions.class);

        try {
            runClassification();
        } catch (IOException e) {
            logService.error("Autocontext command failed", e);
            throw new RuntimeException(e);
        }
    }

    private void runClassification() throws IOException {
        final Autocontext AutocontextPrediction = new Autocontext(ilastikOptions.getExecutableFile(),
                projectFileName, logService, statusService, ilastikOptions.getNumThreads(), ilastikOptions.getMaxRamMb());

        PixelPredictionType pixelPredictionType = PixelPredictionType.valueOf(AutocontextPredictionType);
        this.predictions = AutocontextPrediction.classifyPixels(inputImage.getImgPlus(), pixelPredictionType);

        // DisplayUtils.showOutput(uiService, predictions);
    }
}
