package geoCodeInterface;

import geoCodeJsonObject.Location;

import java.io.IOException;
import java.net.URL;

public class Main
{

	final static String INPUT_FILE_NAME = "";
	final static String OUTPUT_FILE_NAME = "geocoding_output.txt";

	public static void main(String[] args) throws IOException
	{
		if(args.length !=  1){
			System.out.println("No arguments specified\n");
			System.out.println("Please use the following format to run: java -jar GeoCodingMain.jar $inputFileLocation\n");
			System.out.println("i.e. java -jar GeoCodingMain.jar C:\\Temp\\geocoding_input.txt");
			System.exit(1);
		}
		GoogleGeoCode text = new GoogleGeoCode();
		Address userAddress = text.setAddressInfo(args[0]);
		GoogleGeoCode.removeSpaces(userAddress);
		URL geoCodingAPI_URL = GoogleGeoCode.getGeoAPIWebAddress(userAddress);
		System.out.println("The set URL is: " + geoCodingAPI_URL);
		Location userLocation = GoogleGeoCode.getCoordinates(geoCodingAPI_URL);
		System.out.println("User Longitude: " + userLocation.getLng());
		System.out.println("User Latitude: " + userLocation.getLat());
		userAddress.setLatitude(userLocation.getLat());
		userAddress.setLongitude(userLocation.getLng());
		GoogleGeoCode.addSpaces(userAddress);
		System.out.println(userAddress.toString());
		GoogleGeoCode.writeOutputFile(userAddress, OUTPUT_FILE_NAME);

	}

}
