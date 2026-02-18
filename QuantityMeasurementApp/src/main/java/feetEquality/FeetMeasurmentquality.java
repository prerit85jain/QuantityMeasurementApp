package feetEquality;

public class FeetMeasurmentquality {

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
}
