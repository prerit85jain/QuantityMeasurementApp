package app;

import feetEquality.FeetMeasurmentquality.Feet;

public class App {
	public static void main(String[] args) {
		Feet feet1 = new Feet(1.0);
        Feet feet2 = new Feet(1.0);

        boolean result = feet1.equals(feet2);

        System.out.println("Input: 1.0 ft and 1.0 ft");
        System.out.println("Output: Equal (" + result + ")");
	}
}
