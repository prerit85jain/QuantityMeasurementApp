package quantityMeasurement;

import java.util.Objects;

public class Length {
	
	private double value;
	private LengthUnit unit;
	
	// Enum with conversion factor to base unit (inches)
	public enum LengthUnit{
		FEET(12.0),
		Inches(1.0);
		
		private final double conversionFactor;
		
		LengthUnit(double conversionFactor){
			this.conversionFactor = conversionFactor;
		}
		
		public double getConversionFactor() {
			return conversionFactor;
		}
	}
	
	// Constructor to initialize length value and unit
	public Length(double value, LengthUnit unit) {
		if(unit == null) {
			throw new IllegalArgumentException("Unit cannot be null");
		}
		this.value = value;
		this.unit = unit;
	}
	
	// Convert the length value to the base unit (inches)
	private double convertToBaseUnit() {
		return this.value * this.unit.getConversionFactor();
	}
	
	// Compare two length objects for equality based on their values in the base unit
	public boolean compare(Length thatLength) {
		if(thatLength == null) {return false;}
		return Double.compare(this.convertToBaseUnit(), thatLength.convertToBaseUnit()) == 0;
	}
	
	@Override
	public boolean equals(Object o) {
		
		// Null check and Same class check
		if(o == null || o.getClass() != this.getClass()) {return false;}
		
		// Reference check
		if(o == this) {return true;}
		
		// Cast
		Length length = (Length) o;
		return Double.compare(this.convertToBaseUnit(), length.convertToBaseUnit()) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(convertToBaseUnit());
	}
	
	// Main method
	public static void main(String[] args) {
		Length length1 = new Length(1.0, LengthUnit.FEET);
		Length length2 = new Length(12.0, LengthUnit.Inches);
		
		System.out.println("Are lengths equals? " + length1.equals(length2));
	}
}
