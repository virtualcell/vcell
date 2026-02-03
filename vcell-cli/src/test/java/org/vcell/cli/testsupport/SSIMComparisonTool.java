package org.vcell.cli.testsupport;

import java.awt.image.BufferedImage;

public class SSIMComparisonTool {
    private static final double STANDARD_RED_WEIGHT = 0.299;
    private static final double STANDARD_GREEN_WEIGHT = 0.587;
    private static final double STANDARD_BLUE_WEIGHT = 0.114;
    private static final double DYNAMIC_RANGE = 0xFF; // 2^8 - 1

    private static final double STANDARD_LUMINANCE_CONSTANT = 0.01;
    private static final double STANDARD_CONTRAST_CONSTANT = 0.03;
    //private double STANDARD_STRUCTURE_CONSTANT = CONTRAST_CONSTANT / Math.sqrt(2);

    private static final double ALPHA_WEIGHT = 1;
    private static final double BETA_WEIGHT = 1;
    private static final double GAMMA_WEIGHT = 1;

    private final double LUMINANCE_STABILIZER;
    private final double CONTRAST_STABILIZER;
    private final double STRUCTURE_STABILIZER;

    public SSIMComparisonTool(){
        this(STANDARD_LUMINANCE_CONSTANT, STANDARD_CONTRAST_CONSTANT, STANDARD_CONTRAST_CONSTANT / 2);
    }

    /**
     * Builds the stabilizer variables from luminance and contrast constants
     * @param luminanceConstant
     * @param contrastConstant
     */
    public SSIMComparisonTool(double luminanceConstant, double contrastConstant){
        this(
                DYNAMIC_RANGE * DYNAMIC_RANGE * luminanceConstant * luminanceConstant,
                DYNAMIC_RANGE * DYNAMIC_RANGE * contrastConstant * contrastConstant,
                (DYNAMIC_RANGE * DYNAMIC_RANGE * contrastConstant * contrastConstant) / 2
        );
    }

    /**
     * Assign pre-computed stabilizers for SSIM calculations
     * @param luminanceStabilizer
     * @param contrastStabilizer
     * @param structureStabilizer
     */
    public SSIMComparisonTool(double luminanceStabilizer, double contrastStabilizer, double structureStabilizer){
        this.LUMINANCE_STABILIZER = luminanceStabilizer;
        this.CONTRAST_STABILIZER = contrastStabilizer;
        this.STRUCTURE_STABILIZER = structureStabilizer;
    }

    public Results performSSIMComparison(BufferedImage original, BufferedImage contender){
        double[][] originalGrayscaleData = SSIMComparisonTool.getGrayScaleData(original);
        double[][] contenderGrayscaleData = SSIMComparisonTool.getGrayScaleData(contender);
        double originalPSM = SSIMComparisonTool.pixelSampleMean(originalGrayscaleData);
        double contenderPSM = SSIMComparisonTool.pixelSampleMean(contenderGrayscaleData);
        double originalVariance = SSIMComparisonTool.sampleVariance(originalGrayscaleData, originalPSM);
        double contenderVariance = SSIMComparisonTool.sampleVariance(contenderGrayscaleData, contenderPSM);
        double coVariance = SSIMComparisonTool.sampleCovariance(originalGrayscaleData, originalPSM, contenderGrayscaleData, contenderPSM);
        double luminance = this.luminanceCalculation(originalPSM, contenderPSM);
        double contrast = this.contrastCalculation(originalVariance, contenderVariance);
        double structure = this.structureCalculation(originalVariance, contenderVariance, coVariance);
        double weightedLuminance = Math.pow(luminance, SSIMComparisonTool.ALPHA_WEIGHT);
        double weightedContrast = Math.pow(contrast, SSIMComparisonTool.BETA_WEIGHT);
        double weightedStructure = Math.pow(structure, SSIMComparisonTool.GAMMA_WEIGHT);
        double product = weightedLuminance * weightedContrast * weightedStructure;
        return new Results(product, weightedLuminance, weightedContrast, weightedStructure);
    }

    private double luminanceCalculation(double originalPSM, double contenderPSM){
        double opsmSquared = originalPSM * originalPSM;
        double cpsmSquared = contenderPSM * contenderPSM;
        double numerator = 2 * originalPSM * contenderPSM + this.LUMINANCE_STABILIZER;
        double denominator = opsmSquared + cpsmSquared + this.LUMINANCE_STABILIZER;
        return numerator / denominator;
    }

    private double contrastCalculation(double originalVariance, double contenderVariance){
        double originalSD = Math.sqrt(originalVariance);
        double contenderSD = Math.sqrt(contenderVariance);
        double numerator = 2 * originalSD * contenderSD + this.CONTRAST_STABILIZER;
        double denominator = originalVariance + contenderVariance + this.CONTRAST_STABILIZER;
        return numerator / denominator;
    }

    private double structureCalculation(double originalVariance, double contenderVariance, double coVariance){
        double numerator = coVariance + this.STRUCTURE_STABILIZER;
        double denominator = Math.sqrt(originalVariance * contenderVariance) + this.STRUCTURE_STABILIZER;
        return numerator / denominator;
    }

    private static double pixelSampleMean(double[][] grayscaleData){
        int numRows = grayscaleData.length;
        int numCols = grayscaleData[0].length;
        double sum = 0;
        for (var row : grayscaleData) for (double v : row) sum += v;
        return sum / (numRows * numCols);
    }

    private static double sampleVariance(double[][] grayscaleData, double mean) {
        int numRows = grayscaleData.length;
        int numCols = grayscaleData[0].length;
        double sum = 0;
        for (var row : grayscaleData) for (double v : row) sum += (v - mean) * (v - mean);
        return sum / (numRows * numCols);
    }

    private static double sampleCovariance(double[][] originalGrayscaleData, double originalMean,
                                           double[][] contenderGrayscaleData, double contenderMean) {
        double sum = 0;
        int originalNumRows = originalGrayscaleData.length, originalNumCols = originalGrayscaleData[0].length;
        for (int i = 0; i < originalNumRows; i++) for (int j = 0; j < originalNumCols; j++)
            sum += (originalGrayscaleData[i][j] - originalMean) * (contenderGrayscaleData[i][j] - contenderMean);
        return sum / (originalNumRows * originalNumCols);
    }

    // The idea for SSIM is based on human perception, so grayscale works well
    private static double[][] getGrayScaleData(BufferedImage image){
        int w = image.getWidth();
        int h = image.getHeight();
        double[][] convertedData = new double[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = image.getRGB(x, y);
                double blue = STANDARD_BLUE_WEIGHT * (0xff & rgb);
                rgb = (rgb >> 8);
                double green = STANDARD_GREEN_WEIGHT * (0xff & rgb);
                rgb = (rgb >> 8);
                double red = STANDARD_RED_WEIGHT * (0xff & rgb);

                convertedData[y][x] = red + green + blue;
            }
        }
        return convertedData;
    }

    public record Results (double product, double luminanceComponent, double contrastComponent, double structureComponent) {}
}
