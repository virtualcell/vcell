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
				if(newred == 240 && newgrn == 223) {
					System.out.println("newred == 240");
				}
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
			if(newred > 210 && newgrn > 200 && newblu < 40) {
				return false;	// arbitrarily eliminate some (yellowish) hues that just look bad
			}
			return true;
		}
		return false;
	}

	public static int calcBrightness(int red,int grn,int blu){
		return (red*299+grn*587+blu*114)/1000;
	}

	public static final Color[] TABLEAU20 = {
			new Color(31,119,180),    // deep blue
			new Color(255,127,14),    // orange
			new Color(44,160,44),     // green
			new Color(214,39,40),     // red
			new Color(148,103,189),   // purple
			new Color(23,190,207),    // teal
			new Color(140,86,75),     // brown
			new Color(227,119,194),   // pink
			new Color(127,127,127),   // gray
			new Color(188,189,34),    // olive
			new Color(174,199,232),   // light blue
			new Color(255,187,120),   // light orange
			new Color(152,223,138),   // light green
			new Color(255,152,150),   // light red
			new Color(197,176,213),   // light purple
			new Color(219,219,141),   // light olive
			new Color(196,156,148),   // light brown
			new Color(247,182,210),   // light pink
			new Color(199,199,199),   // light gray
			new Color(158,218,229)    // light teal
	};

	public static final Color[] COLORBLIND20 = {
			new Color(0,114,178),     // deep blue
			new Color(213,94,0),      // orange
			new Color(0,158,115),     // green
			new Color(170,51,119),    // magenta
			new Color(0,170,170),     // teal
			new Color(153,153,153),   // gray
			new Color(0,150,255),     // azure
			new Color(0,170,0),       // emerald
			new Color(200,55,0),      // brick red
			new Color(102,0,153),     // purple
			new Color(86,180,233),    // sky blue
			new Color(120,190,32),    // lime green
			new Color(230,159,0),     // goldenrod
			new Color(204,121,167),   // rose
			new Color(0,200,200),     // cyan
			new Color(102,102,102),   // dark gray
			new Color(0,90,160),      // deep navy
			new Color(40,120,40),     // forest green
			new Color(153,102,204),   // lavender
			new Color(136,34,85)      // plum
	};

	public static final Color[] DARK20 = {
			new Color(31,119,180),   // dark blue
			new Color(214,39,40),    // dark red
			new Color(44,160,44),    // dark green
			new Color(148,103,189),  // dark purple
			new Color(140,86,75),    // brown
			new Color(23,190,207),   // dark cyan
			new Color(188,189,34),   // olive
			new Color(127,127,127),  // gray
			new Color(57,59,121),    // indigo
			new Color(82,84,163),    // muted violet
			new Color(107,110,207),  // periwinkle
			new Color(156,158,222),  // muted lavender
			new Color(99,121,57),    // moss green
			new Color(140,162,82),   // muted lime
			new Color(181,207,107),  // muted olive green
			new Color(206,109,189),  // muted magenta
			new Color(140,109,49),   // dark gold/brown
			new Color(189,158,57),   // mustard
			new Color(231,186,82),   // muted amber
			new Color(231,203,148)   // muted beige
	};

}
