package quantityMeasurement;

public class QuantityMeasurementApp {

	// Inner class to represent Feet measurement
	public static class Feet{
		private final double value;

	    public Feet(double value) {
	        this.value = value;
	    }
	    
	    @Override
	    public boolean equals(Object obj) {

	        // Reference check
	        if (this == obj) {
	            return true;
	        }

	        // Null check
	        if (obj == null) {
	            return false;
	        }

	        // Type check
	        if (this.getClass() != obj.getClass()) {
	            return false;
	        }

	        Feet other = (Feet) obj;
	        
	        return Double.compare(this.value, other.value) == 0;
	    }
	}
	
	// Inner class to represent Inches measurement
	public static class Inches{
		private final double value;
		
		public Inches(double value) {
			this.value = value;
		}
		
		@Override
		public boolean equals(Object obj) {
			// Reference check
			if(this == obj) {
				return true;
			}
			
			// Null check
			if(obj == null) {
				return false;
			}
			
			//Type Check
			if(this.getClass() != obj.getClass()) {
				return false;
			}
			
			Inches other = (Inches) obj;
			return Double.compare(this.value, other.value) == 0;
		}
	}
	
	// Define a static method to demonstrate Feet equality check
	public static void demonstrateFeetEquality() {
		Feet feet1 = new Feet(1.0);
		Feet feet2 = new Feet(1.0);
		
		boolean result = feet1.equals(feet2);

        System.out.println("Input: 1.0 ft and 1.0 ft");
        System.out.println("Output: Equal (" + result + ")");
	}
	
	// Define a static method to demonstrate Inches equality check
	public static void demonstrateInchesEquality() {
		Inches inch1 = new Inches(1.0);
		Inches inch2 = new Inches(1.0);
		
		boolean result = inch1.equals(inch2);

        System.out.println("Input: 1.0 Inches and 1.0 Inches");
        System.out.println("Output: Equal (" + result + ")");
	}
	
	// Main method
	public static void main(String[] args) {
		demonstrateFeetEquality();
		demonstrateInchesEquality();
	}
}
