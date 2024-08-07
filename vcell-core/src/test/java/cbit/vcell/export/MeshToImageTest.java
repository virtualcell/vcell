package cbit.vcell.export;


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;


@Tag("Fast")
public class MeshToImageTest {



    @Test
    public void testMeshToImage2D(){
        // 3x4
        final double[] exampleData = {0,1,2,3, 4,5,6,7, 8,9,10,11};
        final double[] expectedTransformation = {0,1,1,2, 3,4,4,5, 3,4,4,5, 6,7,7,8, 6,7,7,8, 9,10,10,11};
        MeshToImage.ImageFromMesh result = MeshToImage.convertMeshIntoImage(exampleData, 3, 4, 0, false);

        assertArrayEquals(expectedTransformation, result.data());
    }

    @Test
    public void testMeshToImage3D(){
        final double[] exampleData = {0,1, 2,3, 4,5,  6,7, 8,9, 10,11,  12,13, 14,15, 16,17,  18,19, 20,21, 22,23};
        final double[] expectedTransformation = {0, 1, 2, 3, 2, 3, 4, 5, 6, 7, 8, 9, 8, 9, 10, 11, 6, 7, 8, 9, 8, 9, 10, 11, 12, 13, 14, 15, 14, 15, 16, 17, 12, 13, 14, 15, 14, 15, 16, 17, 18, 19, 20, 21, 20, 21, 22, 23};
        MeshToImage.ImageFromMesh result = MeshToImage.convertMeshIntoImage(exampleData, 2, 3, 4, true);

        assertArrayEquals(expectedTransformation, result.data());
    }


    @Test
    public void testFullRound(){
        double[] exampleMesh = {0, 1, 2, 3, 4, 5, 6, 7,8,9,10,11,12,13,14,15,16,17,18,19};
        MeshToImage.ImageFromMesh imageResult = MeshToImage.convertMeshIntoImage(exampleMesh, 4, 5, 1, false);
        MeshToImage.ImageFromMesh meshResult = MeshToImage.convertImageIntoMesh(imageResult.data(), 6, 8, 1, false);
        assertArrayEquals(exampleMesh, meshResult.data());

        Random random = new Random();
        exampleMesh = random.doubles(24).toArray();
        imageResult = MeshToImage.convertMeshIntoImage(exampleMesh, 4, 6, 1, false);
        meshResult = MeshToImage.convertImageIntoMesh(imageResult.data(), 6, 10, 1, false);
        assertArrayEquals(exampleMesh, meshResult.data());


        final double[] exampleImage = {0, 1, 1, 2, 3, 4, 4, 5, 3,4,4,5, 6,7,7,8,6,7,7,8, 9,10,10,11};
        meshResult = MeshToImage.convertImageIntoMesh(exampleImage, 4, 6, 1, false);
        imageResult = MeshToImage.convertMeshIntoImage(meshResult.data(), 3, 4, 1, false);

        assertArrayEquals(exampleImage, imageResult.data());
    }




}
