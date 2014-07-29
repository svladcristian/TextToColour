import java.util.Scanner;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

class positionCoordinates {
	int perm, pixelCount;
	positionCoordinates(int p, int pc) {
		perm = p;
		pixelCount = pc;
	}
	int getPerm() {
		return perm;
	}
	int getPixelCount() {
		return pixelCount;
	}
	void resetPerm() {
		perm = 0;
		pixelCount++;
	}
	void increment() {
		perm++;
		pixelCount++;
	}
}

class encodedImage {
	BufferedImage image;
	int ni, nj;
	encodedImage(int j, int i) {
		ni = i; nj = j;
		image = new BufferedImage(nj, ni, BufferedImage.TYPE_INT_RGB);
	}
		
	void encode(positionCoordinates pos, int red, int green, int blue) {
		// permutations: rgb rbg grb gbr brg bgr
		switch (pos.getPerm()) {
			case 0: {
				image.setRGB(pos.getPixelCount()/ni, pos.getPixelCount()%ni, ((red << 16) | (green << 8) | blue));
				pos.increment();
			}break;
			case 1: {
				image.setRGB(pos.getPixelCount()/ni, pos.getPixelCount()%ni, ((red << 16) | (blue << 8) | green));
				pos.increment();
			}break;
			case 2: {
				image.setRGB(pos.getPixelCount()/ni, pos.getPixelCount()%ni, ((green << 16) | (red << 8) | blue));
				pos.increment();
			}break;
			case 3: {
				image.setRGB(pos.getPixelCount()/ni, pos.getPixelCount()%ni, ((green << 16) | (blue << 8) | red));
				pos.increment();
			}break;
			case 4: {
				image.setRGB(pos.getPixelCount()/ni, pos.getPixelCount()%ni, ((blue << 16) | (red << 8) | green));
				pos.increment();
			}break;
			case 5: {
				image.setRGB(pos.getPixelCount()/ni, pos.getPixelCount()%ni, ((blue << 16) | (green << 8) | red));
				pos.resetPerm();
			}break;
			default: {
				image.setRGB(pos.getPixelCount()/ni, pos.getPixelCount()%ni, ((red << 16) | (green << 8) | blue));	
			}break;
		}
	}
}

class pixelBuild extends Thread {
	String text;
	int start, end;
	ArrayList<Character> allChars;
	int intervalSpan;
	encodedImage image;
	int ni, nj;
	positionCoordinates pos;
	pixelBuild(String txt, int strt, int ed, ArrayList<Character> allChrs, int intvlSpn, encodedImage img, int i, int j, positionCoordinates ps) {
		text = txt;
		start = strt; end = ed;
		allChars = allChrs;
		intervalSpan = intvlSpn;
		image = img;
		ni = i; nj = j;
		pos = ps;
	}
	public void run() {
		Random rand = new Random();
		int range, randNum;
		for (int i = start; i+2 <= end; i += 3) {
			//int red = (int) text.charAt(i);
			//int green = (int) text.charAt(i+1);
			//int blue = (int) text.charAt(i+2);
			
			// character mapping
			int red = allChars.indexOf(text.charAt(i));
			range = ((((red + 1) * intervalSpan) - 1) - (red * intervalSpan))  + 1;
			randNum =  rand.nextInt(range) + (red * intervalSpan);
			red = randNum;
			
			int green = allChars.indexOf(text.charAt(i+1));
			range = (((green + 1) * intervalSpan) - 1) - (green * intervalSpan)  + 1;
			randNum =  rand.nextInt(range) + (green * intervalSpan);
			green = randNum;
			
			int blue = allChars.indexOf(text.charAt(i+2));
			range = (((blue + 1) * intervalSpan) - 1) - (blue * intervalSpan)  + 1;
			randNum =  rand.nextInt(range) + (blue * intervalSpan);
			blue = randNum;
			
			System.out.print("[ " + red + " " + green + " " + blue + " ] ");
			image.encode(pos, red, green, blue);
			//np++;
		}
	}
}

public class TextToColourRead {
	String text;
	ArrayList<Character> allChars;
	int intervalSpan;
	encodedImage image;
	int n, m, roundm, ni, nj;
	public TextToColourRead(String INPUTFILENAME) {
		// Reading text from file.
		text = new String();
    	try {
    		Scanner scanner = new Scanner(new File(INPUTFILENAME));
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
    	
    	mapCharacters();
	}
	
	void mapCharacters() {
		// Creating character mapping.
		allChars = new ArrayList<Character>();
		for (int i = 0; i < text.length(); i++) {
			if (allChars.contains(text.charAt(i)) == false) {
				allChars.add(text.charAt(i));
			}
		}
		allChars.add('\0');
		Collections.shuffle(allChars);
		System.out.println("Charmap size=" + allChars.size());
		intervalSpan = 256 / allChars.size(); 
		
		String CHARMAPFILENAME = "charmap.ser";
		writeCharMap(CHARMAPFILENAME);
	}
	
	void writeCharMap(String CHARMAPFILENAME) {
		try {
			charMap cm = new charMap(allChars);
			FileOutputStream fos = new FileOutputStream(CHARMAPFILENAME);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(cm);
			oos.close();
			fos.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		double THRESHOLDVALUE = 5;
		determineDimensions(THRESHOLDVALUE);
	}
	
	void determineDimensions(double THRESHOLDVALUE) {
		// Determining the size the resulting image.
		m = text.length();
		if (m%3 == 0) n = m/3;
		else n = m/3 + 1;
		roundm = n;
		System.out.println("m=" + m);
		System.out.println("initial n=round m/3=" + roundm);
		
		// Determining the optimal dimensions the resulting image.
		double thresholdSize = (((THRESHOLDVALUE / 100) * n ) < 1) ? 1 : (THRESHOLDVALUE / 100) * n;
		ni = 1; nj = 1; int minDiff = n;
		for (int i = 0; i < thresholdSize; i++) {
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
		
		buildImage();
	}
	
	void buildImage() {
		// Determining pixel values, obtained strictly from text characters,
		// with at most 2 rgb value fillers, to complete the last pixel.
		// - roundm reached
		// Creating image
		image = new encodedImage(nj, ni);
		// int np = 0;
		int i;
		//positionCoordinates pos = new positionCoordinates(0,0);
		//intWrapper perm = new intWrapper(0);
		
		int li, lj;
		Thread[] threads = new Thread[3];
		li = 0; lj = ((((m / 3) / 3) * 3) - 1);
		System.out.println(li + " - " + lj);
		threads[0] = new pixelBuild(text,li,lj,allChars,intervalSpan,image,ni,nj,new positionCoordinates(0,0));
		threads[0].start();
		
		li = lj + 1;
		lj += ((((m / 3) / 3) * 3));
		System.out.println(li + " - " + lj);
		threads[1] = new pixelBuild(text,li,lj,allChars,intervalSpan,image,ni,nj,new positionCoordinates(((li / 3) % 6),(li / 3)));
		threads[1].start();
		
		li = lj + 1;
		lj = ((m - (m % 3)) - 1 );
		System.out.println(li + " - " + lj);
		threads[2] = new pixelBuild(text,li,lj,allChars,intervalSpan,image,ni,nj,new positionCoordinates(((li / 3) % 6),(li / 3)));
		threads[2].start();
		
		Random rand = new Random();
		int range, randNum;
		i = lj + 1;
		positionCoordinates pos = new positionCoordinates(((i / 3) % 6),(i / 3));
		System.out.println("i=" + i);
		if (m - i > 0) {
			//int red = (int) text.charAt(i);
			int red = allChars.indexOf(text.charAt(i));
			range = (((red + 1) * intervalSpan) - 1) - (red * intervalSpan)  + 1;
			randNum =  rand.nextInt(range) + (red * intervalSpan);
			red = randNum;
			
			int green;
			if (m - i > 1) {
				green = allChars.indexOf(text.charAt(i+1));
				range = (((green + 1) * intervalSpan) - 1) - (green * intervalSpan)  + 1;
				randNum =  rand.nextInt(range) + (green * intervalSpan);
				green = randNum;
			}
			else {
				green = allChars.indexOf('\0');
				range = ((((green + 1) * intervalSpan) - 1) - (green * intervalSpan))  + 1;
				randNum =  rand.nextInt(range) + (green * intervalSpan);
				green = randNum;
			}
			int blue = allChars.indexOf('\0');
			range = ((((blue + 1) * intervalSpan) - 1) - (blue * intervalSpan))  + 1;
			randNum =  rand.nextInt(range) + (blue * intervalSpan);
			blue = randNum;
			System.out.println("*[ " + red + " " + green + " " + blue + " ]* ");
			image.encode(pos, red, green, blue);
			//np++;
		}
		System.out.println();
		System.out.println("Number of pixels obtained: " + pos.getPixelCount() + " - " + "Number of pixels expected: " + roundm);
			
		// Filling remaining pixels to completion.
		// - n reached
		for (i = roundm; i < n; i++) {
			int red = allChars.indexOf('\0');
			range = ((((red + 1) * intervalSpan) - 1) - (red * intervalSpan))  + 1;
			randNum =  rand.nextInt(range) + (red * intervalSpan);
			red = randNum;
			
			int green = allChars.indexOf('\0');
			range = ((((green + 1) * intervalSpan) - 1) - (green * intervalSpan))  + 1;
			randNum =  rand.nextInt(range) + (green * intervalSpan);
			green = randNum;
			
			int blue = allChars.indexOf('\0');
			range = ((((blue + 1) * intervalSpan) - 1) - (blue * intervalSpan))  + 1;
			randNum =  rand.nextInt(range) + (blue * intervalSpan);
			blue = randNum;
			
			System.out.print("**[ " + red + " " + green + " " + blue + " ]** ");
			image.encode(pos, red, green, blue);
			//np++;
		}
		System.out.println();
		System.out.println("Number of pixels obtained: " + pos.getPixelCount() + " - " + "Number of pixels expected: " + n);
		
		try {
			threads[0].join();
			threads[1].join();
			threads[2].join();
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
		
		writeImage();
	}
	
	void writeImage() {
		// Writing resulting image.
		try {
			ImageIO.write(image.image, "PNG", new File("image.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    public static void main(String[] args) {
    	
    	String INPUTFILENAME = "data.in";
    	TextToColourRead encode = new TextToColourRead(INPUTFILENAME);

    }
}
