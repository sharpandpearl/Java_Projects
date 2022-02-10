package geoCodeInterface;

public class Address
{

	// defaults

	String streetAddress = "";
	String unit = "";
	String city = "";
	String state = "";
	String zipCode = "";
	Number latitude = 0;
	Number longitude = 0;

	// Getters

	public String getStreetAddress()
	{
		return this.streetAddress;
	}

	public String getUnit()
	{
		return this.unit;
	}

	public String getCity()
	{
		return this.city;
	}

	public String getState()
	{
		return this.state;
	}

	public String getZipCode()
	{
		return this.zipCode;
	}

	public Number getLongitude()
	{
		return this.longitude;
	}

	public Number getLatitude()
	{
		return this.latitude;
	}

	// Setters

	public void setStreetAddress(String streetAddress)
	{
		this.streetAddress = streetAddress;
	}

	public void setUnit(String unit)
	{
		this.unit = unit;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public void setZipCode(String zipCode)
	{
		this.zipCode = zipCode;
	}

	public void setLongitude(Number longitude)
	{
		this.longitude = longitude;
	}

	public void setLatitude(Number latitude)
	{
		this.latitude = latitude;
	}

	public String toString()
	{
		return "street_address\tunit\tcity\tstate\tzip\tlatitude\tlongitude\n" + 
				streetAddress + '\t' + unit + '\t' + city + '\t' + state + '\t' + zipCode + '\t'
				+ latitude + '\t' + longitude;
	}

}
