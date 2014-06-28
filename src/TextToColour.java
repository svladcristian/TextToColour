import java.util.Scanner;

public class TextToColour {
	
    public static void main(String[] args) {
    	
		Scanner sc = new Scanner(System.in);
		System.out.print("m=");
		int m = sc.nextInt();
		
		int n;
		if (m%3 == 0) n = m/3;
		else n = m/3 + 1;
		System.out.println("n=" + n);

		int i = 0, j = 1, k = 1;
		do {
			do {
				k++;
				if (n%k == 0) {
					j = n/k; i = k;
				}
			}while (i < j);
			if (j == 1) {
				i = 0; j = 1; k = 1;
				n++;
			}
		}while (i < j);
		System.out.println(n + "=" + i + "*" + j);
		
    }
}
