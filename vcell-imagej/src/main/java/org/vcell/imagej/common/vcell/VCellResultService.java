package org.vcell.imagej.common.vcell;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ImgPlus;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;

/**
 * Created by kevingaffney on 7/5/17.
 */
public class VCellResultService {

    private OpService ops;
    private DatasetService datasetService;

    public VCellResultService(OpService ops, DatasetService datasetService) {
        this.ops = ops;
        this.datasetService = datasetService;
    }

    public Dataset importCsv(File directory) throws FileNotFoundException {

        File[] files = directory.listFiles();

        // TODO: Better handling
        if (files == null) return null;

        ArrayList<ArrayList<Float>> timeSeries = new ArrayList<>(files.length);

        Scanner scanner;
        int dataSize = 0;

        for (File file : files) {

            scanner = new Scanner(file);
            scanner.useDelimiter("[,\n]");

            while (scanner.hasNext() && !scanner.hasNextDouble()) {
                scanner.next();
            }

            if (!scanner.hasNextDouble()) {
            	scanner.close();
            	return null;
            }

            ArrayList<Float> data = new ArrayList<>();
            while (scanner.hasNextDouble()) {
                data.add(scanner.nextFloat());
            }

            scanner.close();

            timeSeries.add(data);
            dataSize = data.size();
        }

        int[] dimensions = {dataSize, timeSeries.size()};

        Img<FloatType> img = new ArrayImgFactory<FloatType>().create(dimensions, new FloatType());

        Cursor<FloatType> cursor = img.localizingCursor();
        while (cursor.hasNext()) {
            cursor.next();
            int xPos = cursor.getIntPosition(0);
            int tPos = cursor.getIntPosition(1);
            Float val = timeSeries.get(tPos).get(xPos);
            cursor.get().set(val);
        }
        
        
        Dataset dataset = datasetService.create(img);
        
        // Drop single dimensions
        @SuppressWarnings("unchecked")
		ImgPlus<FloatType> imgPlus = (ImgPlus<FloatType>) dataset.getImgPlus();
        FinalInterval interval = Intervals.createMinMax(0, 0, imgPlus.dimension(0) - 1, imgPlus.dimension(1) - 1);
        ImgPlus<FloatType> cropped = ops.transform().crop(imgPlus, interval, true);
        dataset.setImgPlus(cropped);
        System.out.println(dataset.numDimensions());
        dataset.setName(directory.getName());
        return dataset;
    }
}
