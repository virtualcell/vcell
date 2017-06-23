package org.vcell;

import io.scif.SCIFIO;
import io.scif.formats.NRRDFormat;
import net.imagej.Data;
import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import org.scijava.command.Command;
import org.scijava.display.DisplayService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by kevingaffney on 6/19/17.
 */
@Plugin(type = Command.class, menuPath = "Plugins>VCell>Import Results")
public class ImportResults<T extends RealType<T>> implements Command {

    @Parameter
    private File file;

    @Parameter
    private DisplayService displayService;

    @Parameter
    private ImageJ ij;

    @Override
    public void run() {

//        Scanner scanner = null;
//        try {
//            scanner = new Scanner(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        scanner.useDelimiter(Pattern.compile("[,\n]"));
//
//        int length = 137 * 137;
//        int[] imageArray = new int[length];
//        int counter = 0;
//        while(scanner.hasNext() && counter < length) {
//            imageArray[counter] = Math.round(Float.parseFloat(scanner.next()));
//            counter++;
//        }
//        scanner.close();
//
//
//        ArrayImg<UnsignedIntType, IntArray> img = ArrayImgs.unsignedInts(imageArray, 137, 137);
//        displayService.createDisplay(img);

        Dataset dataSim = null;

        try {
            dataSim = ij.scifio().datasetIO().open(file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        displayService.createDisplay(dataSim);
    }
}
