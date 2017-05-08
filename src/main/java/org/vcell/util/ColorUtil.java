package org.vcell.util;

import java.awt.Color;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Vector;

public class ColorUtil {

	public static Color[] generateAutoColor(int numColors,Color backgroundColor,Integer randomColorSetSeed){
			
		if(randomColorSetSeed == null){
			Color[] originalMethodColors = new Color[numColors];
			for (int i = 0; i < numColors; i++) {
				originalMethodColors[i] = Color.getHSBColor((float) i / numColors, 1.0f, 1.0f);
			}
			return originalMethodColors;
		}
		
		Vector<Color> colorV = null;
		Random random = new Random(randomColorSetSeed);
		int red = 0;
		int grn = 0;
		int blu = 0;
		final int ITERATION_LIMIT = 200*numColors;
		final int CONTRAST_DELTA = 10;
		final int BRIGHT_DELTA = 0; //3;
		
		int brightThreshold = 200;//90;
		for (int contrastThreshold = 300; contrastThreshold >= 0; contrastThreshold-= CONTRAST_DELTA) {
			colorV = new Vector<Color>();
	//		colorV.add(new Color(0x00FF0000));
	//		if(colorV.size() < numColors){colorV.add(new Color(0x0000FF00));}
	//		if(colorV.size() < numColors){colorV.add(new Color(0x000000FF));}
	//		if(colorV.size() < numColors){colorV.add(new Color(0x00FFFF00));}
	//		if(colorV.size() < numColors){colorV.add(new Color(0x00FF00FF));}
	//		if(colorV.size() < numColors){colorV.add(new Color(0x0000FFFF));}
	
			boolean bFailed = false;
			int iterations = 0;
			while(colorV.size() < numColors){
				iterations++;
				if(iterations > ITERATION_LIMIT){
					bFailed = true;
					break;
				}			
				int newred = random.nextInt(256);
				int newgrn = random.nextInt(256);
				int newblu = random.nextInt(256);
				boolean bAllContrastOK = (backgroundColor == null?true:
					isContrastOK(contrastThreshold,brightThreshold,
						backgroundColor.getRed(), newred, backgroundColor.getGreen(), newgrn, backgroundColor.getBlue(), newblu));
				bAllContrastOK = bAllContrastOK &&
					isContrastOK(contrastThreshold,brightThreshold,
						Color.black.getRed(), newred, Color.black.getGreen(), newgrn, Color.black.getBlue(), newblu);
				if(bAllContrastOK){
					for (int i = 0; i < colorV.size(); i++) {
						red = colorV.elementAt(i).getRed();
						grn = colorV.elementAt(i).getGreen();
						blu = colorV.elementAt(i).getBlue();
						if(!isContrastOK(contrastThreshold,brightThreshold,red, newred, grn, newgrn, blu, newblu)){
							bAllContrastOK = false;
							break;				
						}
					}
				}
				if(bAllContrastOK){
					red = newred;
					grn = newgrn;
					blu = newblu;
					colorV.add(new Color(red, grn, blu));
				}
			}
			if(!bFailed){
				break;
			}
			brightThreshold-= BRIGHT_DELTA;
		}
		if(colorV == null || colorV.size() != numColors){
			throw new RuntimeException("Plot2DPanel: couldn't generate "+numColors+" autoContrast colors");
		}
		Color[] sortedColors = colorV.toArray(new Color[0]);
		Arrays.sort(sortedColors,
			new Comparator<Color>(){
				public int compare(Color o1, Color o2) {
					return calcBrightness(o1.getRed(), o1.getGreen(), o1.getBlue()) -
					calcBrightness(o2.getRed(), o2.getGreen(), o2.getBlue());
				}
		}
		);
		Color[] alternatingColors = new Color[sortedColors.length];
		for (int i = 0; i < alternatingColors.length; i++) {
			int endIndex = sortedColors.length-1-i;
			if(endIndex < i){
				break;
			}
			alternatingColors[i*2] = sortedColors[i];
			if(endIndex > i){
				alternatingColors[i*2+1] = sortedColors[endIndex];
			}
		}
		return alternatingColors;
	}

	public static boolean isContrastOK(int contrastThreshold,int brightThreshold,int red,int newred,int grn,int newgrn,int blu,int newblu){
		//http://www.wat-c.org/tools/CCA/1.1/
		int contrDiff = Math.abs(red-newred)+Math.abs(grn-newgrn)+Math.abs(blu-newblu);
		int bright1 = calcBrightness(red,grn,blu);
		int bright2 = calcBrightness(newred, newgrn, newblu);
		if((contrDiff > contrastThreshold || Math.abs(bright1-bright2)>brightThreshold)){
			return true;
		}
		return false;
	}

	public static int calcBrightness(int red,int grn,int blu){
		return (red*299+grn*587+blu*114)/1000;
	}

}
