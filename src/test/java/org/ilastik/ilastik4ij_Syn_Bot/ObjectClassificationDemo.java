package org.ilastik.ilastik4ij_Syn_Bot;

import io.scif.io.ByteArrayHandle;
import io.scif.io.IRandomAccess;
import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.ImgPlus;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import org.ilastik.ilastik4ij_Syn_Bot.executors.ObjectClassification;
import org.ilastik.ilastik4ij_Syn_Bot.util.IOUtils;

import static org.ilastik.ilastik4ij_Syn_Bot.executors.AbstractIlastikExecutor.PixelPredictionType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ObjectClassificationDemo {
    public static <T extends RealType<T> & NativeType<T>> void main(String[] args) throws IOException {
        final String ilastikPath = "/opt/ilastik-1.3.3post1-Linux/run_ilastik.sh";
        final String inputImagePath = "/2d_cells_apoptotic.tif";
        final String inputProbabMaps = "/2d_cells_apoptotic_1channel-data_Probabilities.tif";
        final String ilastikProjectPath = "/obj_class_2d_cells_apoptotic.ilp";

        // Open ImageJ
        //
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();


        // Open input image
        //
        final InputStream inputFileStream = PixelClassificationDemo.class.getResourceAsStream(inputImagePath);
        final ByteBuffer bb1 = ByteBuffer.allocate(inputFileStream.available());
        while (inputFileStream.available() > 0) {
            bb1.put((byte) inputFileStream.read());
        }
        final IRandomAccess ira1 = new ByteArrayHandle(bb1);
        ij.scifio().location().mapFile("rawInputFile", ira1);
        final Dataset inputDataset = ij.scifio().datasetIO().open("rawInputFile");

        // Open pmaps image
        //
        final InputStream pmapsFileStream = PixelClassificationDemo.class.getResourceAsStream(inputProbabMaps);
        final ByteBuffer bb2 = ByteBuffer.allocate(pmapsFileStream.available());
        while (pmapsFileStream.available() > 0) {
            bb2.put((byte) pmapsFileStream.read());
        }
        final IRandomAccess ira2 = new ByteArrayHandle(bb2);
        ij.scifio().location().mapFile("pmapsFile", ira2);
        final Dataset pmapsDataset = ij.scifio().datasetIO().open("pmapsFile");

        ij.ui().show(inputDataset);

        // Copy project file to tmp
        //
        InputStream projectFileStream = PixelClassificationDemo.class.getResourceAsStream(ilastikProjectPath);
        Path tmpIlastikProjectFile = Paths.get(IOUtils.getTemporaryFileName("obj_class.ilp"));
        Files.copy(projectFileStream, tmpIlastikProjectFile);

        // Classify objects
        //
        final ObjectClassification prediction = new ObjectClassification(
                new File(ilastikPath),
                tmpIlastikProjectFile.toFile(),
                ij.log(),
                ij.status(),
                4,
                1024
        );

        final ImgPlus<T> classifiedObjects = prediction.classifyObjects(inputDataset.getImgPlus(), pmapsDataset.getImgPlus(),
                PixelPredictionType.Probabilities);

        ImageJFunctions.show(classifiedObjects, "Classified objects");
    }

}
