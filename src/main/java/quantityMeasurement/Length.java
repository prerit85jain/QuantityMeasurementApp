package quantityMeasurement;

import java.util.Objects;

public class Length {
	// Instance Variable
	private double value;
	private LengthUnit unit;

	// Constructor to initialize length value and unit
	public Length(double value, LengthUnit unit) {

		if(!Double.isFinite(value)) {
			throw new IllegalArgumentException("Value must be finite");
		}
		if(unit == null) {
			throw new IllegalArgumentException("Unit cannot be null");
		}
		this.value = value;
		this.unit = unit;
	}
	public double getValue(){return value;}
	public LengthUnit getUnit(){return unit;}
	
	// Compare two length objects for equality based on their values in the base unit
	public boolean compare(Length thatLength) {
		if(thatLength == null) {return false;}

		return Double.compare(this.unit.convertToBaseUnit(this.value), thatLength.unit.convertToBaseUnit(thatLength.value)) == 0;
	}
	
	@Override
	public boolean equals(Object o) {
		
		// Reference check
		if(o == this) {return true;}

		// Null check and Same class check
		if(o == null || o.getClass() != this.getClass()) {return false;}
		
		
		// Cast
		Length length = (Length) o;
		return Double.compare(this.unit.convertToBaseUnit(this.value), length.unit.convertToBaseUnit(length.value)) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.unit.convertToBaseUnit(this.value));
	}
	
	// Convert the length to a specific target unit
	public Length convertTo(LengthUnit targetUnit) {
		if(targetUnit == null) {
			throw new IllegalArgumentException("Target unit connot be null");
		}
		
		double baseValue = this.unit.convertToBaseUnit(this.value);
		double convertValue = baseValue / targetUnit.getConversionFactor();
		
		return new Length(round(convertValue), targetUnit);
	}

	// Add a length to this length and return to the unit of this instance
	public Length add(Length thatLength){
		return addAndConvert(thatLength, this.unit);
	}

	// Overload add method to convert the result to a specific target
	public Length add(Length length, LengthUnit targetUnit){
		return addAndConvert(length, targetUnit);
	}

	// Add the length value and return it in inch unit
	private Length addAndConvert(Length length, LengthUnit targetUnit){
		Length thisToInch = this.convertTo(LengthUnit.INCHES);
		Length thatToInch = length.convertTo(LengthUnit.INCHES);

		Length totalLength = new Length(round(thisToInch.value + thatToInch.value), LengthUnit.INCHES);
		return totalLength.convertTo(targetUnit);
	}

	//
	private double convertToBaseUnit(){
		return unit.convertToBaseUnit(value);
	}

	//
	private double convertFromBaseToTargetUnit(double lengthInInches, LengthUnit targetUnit){
		return targetUnit.convertFromBaseUnit(lengthInInches);
	}
	
	// Round the values to the two decimal places
	private double round(double value) {
		return (double)Math.round(value*100)/100;
	}
	
	@Override
	public String toString() {
		return String.format("%.2f %s", value, unit);
	}

}
