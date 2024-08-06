package cbit.vcell.export;


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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




}
