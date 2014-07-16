import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Collections;

public class TextToColourRead {
	
    public static void main(String[] args) {
    	
		// Reading text from file.
    	String FILENAME = "data.in";
    	String text = new String();
    	try {
    		Scanner scanner = new Scanner(new File(FILENAME));
    		StringBuilder stringBuilder = new StringBuilder();
    		stringBuilder.append(scanner.nextLine());
    		String newLine = System.getProperty("line.separator");
	    	while (scanner.hasNextLine()) {
	    		stringBuilder.append(newLine + scanner.nextLine());
	    	}
	    	text = stringBuilder.toString();
    	}catch(FileNotFoundException ex) {
    		ex.printStackTrace();
    	}
    	System.out.println(text);
    	
		// Creating character mapping.
		ArrayList<Character> allChars = new ArrayList<Character>();
		for (int i = 0; i < text.length(); i++) {
			if (allChars.contains(text.charAt(i)) == false) {
				allChars.add(text.charAt(i));
			}
		}
		Collections.shuffle(allChars);
		System.out.println(allChars.size() + " - " + allChars);
		
		/*
		 // Determining the size the resulting image.
		int m = text.length();
		int n;
		if (m%3 == 0) n = m/3;
		else n = m/3 + 1;
		int roundm = n;
		System.out.println("m=" + m);
		System.out.println("initial n=round m/3=" + roundm);
		
		// Determining the optimal dimensions the resulting image.
		int THRESHOLD = 5;
		int sizeThreshold = (((THRESHOLD / 100) * n ) < 1) ? 1 : (THRESHOLD / 100) * n;
		int ni = 1, nj = 1, minDiff = n;
		for (int i = 0; i < sizeThreshold; i++) {
			int tempi = 1, tempj = 0, k = 1;
			do {
				k++;
				if (n%k == 0) {
					tempi = n/k;
					tempj = k;
				}
			}while (tempi > tempj);
			int tempDiff = tempj - tempi; 
			if (tempDiff < minDiff) {
				minDiff = tempDiff;
				ni = tempi;
				nj = tempj;
			}
			n++;
		}
		n = ni*nj;
		System.out.println("n=" + n + "=" + ni + "*" + nj);
		
		// Determining pixel values, obtained strictly from text characters,
		// with at most 2 rgb value fillers, to complete the last pixel.
		// - roundm reached
		// Creating image
		BufferedImage img = new BufferedImage(nj, ni, BufferedImage.TYPE_INT_RGB);
		int np = 0;
		int i;
		// permutations: rgb rbg grb gbr brg bgr
		int perm = 0;
		for (i = 0; i+3 <= m; i += 3) {
			int red = (int) text.charAt(i);
			int green = (int) text.charAt(i+1);
			int blue = (int) text.charAt(i+2);
			System.out.print("[ " + red + " " + green + " " + blue + " ] ");
			switch (perm) {
				case 0: {
					img.setRGB(np/ni, np%ni, ((red << 16) | (green << 8) | blue));
					perm++;
				}break;
				case 1: {
					img.setRGB(np/ni, np%ni, ((red << 16) | (blue << 8) | green));
					perm++;
				}break;
				case 2: {
					img.setRGB(np/ni, np%ni, ((green << 16) | (red << 8) | blue));
					perm++;
				}break;
				case 3: {
					img.setRGB(np/ni, np%ni, ((green << 16) | (blue << 8) | red));
					perm++;
				}break;
				case 4: {
					img.setRGB(np/ni, np%ni, ((blue << 16) | (red << 8) | green));
					perm++;
				}break;
				case 5: {
					img.setRGB(np/ni, np%ni, ((blue << 16) | (green << 8) | red));
					perm = 0;
				}break;
				default: {
					img.setRGB(np/ni, np%ni, ((red << 16) | (green << 8) | blue));	
				}break;
			}
			np++;
		}
		if (m - i > 0) {
			int red = (int) text.charAt(i);
			int green;
			if (m - i > 1) {
				green = (int) text.charAt(i+1);
			}
			else {
				green = 0;
			}
			int blue = 0;
			System.out.println("*[ " + red + " " + green + " " + blue + " ]* ");
			switch (perm) {
				case 0: {
					img.setRGB(np/ni, np%ni, ((red << 16) | (green << 8) | blue));
					perm++;
				}break;
				case 1: {
					img.setRGB(np/ni, np%ni, ((red << 16) | (blue << 8) | green));
					perm++;
				}break;
				case 2: {
					img.setRGB(np/ni, np%ni, ((green << 16) | (red << 8) | blue));
					perm++;
				}break;
				case 3: {
					img.setRGB(np/ni, np%ni, ((green << 16) | (blue << 8) | red));
					perm++;
				}break;
				case 4: {
					img.setRGB(np/ni, np%ni, ((blue << 16) | (red << 8) | green));
					perm++;
				}break;
				case 5: {
					img.setRGB(np/ni, np%ni, ((blue << 16) | (green << 8) | red));
					perm = 0;
				}break;
				default: {
					img.setRGB(np/ni, np%ni, ((red << 16) | (green << 8) | blue));	
				}break;
			}
			np++;
		}
		System.out.println();
		System.out.println("Number of pixels obtained: " + np + " - " + "Number of pixels expected: " + roundm);
			
		// Filling remaining pixels to completion.
		// - n reached
		for (i = roundm; i < n; i++) {
			int red = 0;
			int green = 0;
			int blue = 0;
			System.out.print("**[ " + red + " " + green + " " + blue + " ]** ");
			switch (perm) {
				case 0: {
					img.setRGB(np/ni, np%ni, ((red << 16) | (green << 8) | blue));
					perm++;
				}break;
				case 1: {
					img.setRGB(np/ni, np%ni, ((red << 16) | (blue << 8) | green));
					perm++;
				}break;
				case 2: {
					img.setRGB(np/ni, np%ni, ((green << 16) | (red << 8) | blue));
					perm++;
				}break;
				case 3: {
					img.setRGB(np/ni, np%ni, ((green << 16) | (blue << 8) | red));
					perm++;
				}break;
				case 4: {
					img.setRGB(np/ni, np%ni, ((blue << 16) | (red << 8) | green));
					perm++;
				}break;
				case 5: {
					img.setRGB(np/ni, np%ni, ((blue << 16) | (green << 8) | red));
					perm = 0;
				}break;
				default: {
					img.setRGB(np/ni, np%ni, ((red << 16) | (green << 8) | blue));	
				}break;
			}
			np++;
		}
		System.out.println();
		System.out.println("Number of pixels obtained: " + np + " - " + "Number of pixels expected: " + n);
		
		// Writing resulting image.
		try {
			ImageIO.write(img, "PNG", new File("image.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
    }
}
