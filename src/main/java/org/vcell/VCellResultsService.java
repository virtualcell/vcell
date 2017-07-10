package org.vcell;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.real.FloatType;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by kevingaffney on 7/5/17.
 */
public class VCellResultsService {

    private OpService ops;
    private DatasetService datasetService;

    public VCellResultsService(OpService ops, DatasetService datasetService) {
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

            if (!scanner.hasNextDouble()) return null;

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
        dataset.setName(directory.getName());
        return dataset;
    }
}
