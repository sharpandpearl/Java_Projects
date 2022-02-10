package geoCodeInterface;

import geoCodeJsonObject.GeoCodeResponse;
import geoCodeJsonObject.Location;
import geoCodeJsonObject.Results;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class GoogleGeoCode
{

	// Pulls the address info from a file and creates/returns an Address object
	public Address setAddressInfo(String aFileName) throws IOException
	{
		Address geoInputAddress = new Address();
		String[] fileInfo = new String[10];
		BufferedReader br = null;

		try
		{

			int counter = 0;
			br = new BufferedReader(new FileReader(aFileName));

			while ((fileInfo[counter] = br.readLine()) != null)
			{
				counter++;
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (br != null)
					br.close();
			} catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}

		// Splits file input into variables based a tab(\t) delimiter
		String[] addressValues = fileInfo[1].split("\\t", -1);

		geoInputAddress.setStreetAddress(addressValues[0]);
		geoInputAddress.setUnit(addressValues[1]);
		geoInputAddress.setCity(addressValues[2]);
		geoInputAddress.setState(addressValues[3]);
		geoInputAddress.setZipCode(addressValues[4]);

		return geoInputAddress;
	}

	// Writes an Address object to an output file
	public static void writeOutputFile(Address addy, String aFileName) throws IOException
	{
		FileOutputStream fop = null;
		File outputFile;

		try
		{

			outputFile = new File(aFileName);
			fop = new FileOutputStream(outputFile);

			// if file doesn't exists, then create it
			if (!outputFile.exists())
			{
				outputFile.createNewFile();
			}

			// get the content in bytes
			byte[] contentInBytes = addy.toString().getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

			System.out.println("Done");

		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (fop != null)
				{
					fop.close();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	// Method for swapping out spaces for '+' signs in a String array - Method
	// is deprecated
	public String[] swapSpaces(String[] swapString)
	{
		int index = 0;
		while (index < swapString.length)
		{
			char[] cs = swapString[index].toCharArray();
			for (int i = 0; i < cs.length; i++)
			{
				if (cs[i] == ' ')
					cs[i] = '+';
			}
			swapString[index] = new String(cs);
			index++;
		}
		return swapString;
	}

	// Method used to swap white spaces in the address and city with '+' signs
	public static Address removeSpaces(Address addy)
	{
		char[] addyCharArray = addy.getStreetAddress().toCharArray();
		char[] cityCharArray = addy.getCity().toCharArray();
		for (int i = 0; i < addyCharArray.length; i++)
		{
			if (addyCharArray[i] == ' ')
				addyCharArray[i] = '+';
		}
		for (int i = 0; i < cityCharArray.length; i++)
		{
			if (cityCharArray[i] == ' ')
				cityCharArray[i] = '+';
		}

		addy.setStreetAddress(new String(addyCharArray));
		addy.setCity(new String(cityCharArray));

		return addy;
	}

	// Method used to swap '+' signs in the address and city with spaces
	public static Address addSpaces(Address addy)
	{
		char[] addyCharArray = addy.getStreetAddress().toCharArray();
		char[] cityCharArray = addy.getCity().toCharArray();
		for (int i = 0; i < addyCharArray.length; i++)
		{
			if (addyCharArray[i] == '+')
				addyCharArray[i] = ' ';
		}
		for (int i = 0; i < cityCharArray.length; i++)
		{
			if (cityCharArray[i] == '+')
				cityCharArray[i] = ' ';
		}

		addy.setStreetAddress(new String(addyCharArray));
		addy.setCity(new String(cityCharArray));

		return addy;
	}

	/*
	 * Method for stringing together the GeoCoding API web address based off of
	 * the address data
	 */
	public static URL getGeoAPIWebAddress(Address inputAddress) throws MalformedURLException
	{
		removeSpaces(inputAddress);
		String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address="
				+ inputAddress.getStreetAddress() + ",+" + inputAddress.getUnit() + ",+"
				+ inputAddress.getCity() + ",+" + inputAddress.getState() + ",+"
				+ inputAddress.getZipCode() + "&sensor=false";

		URL geoApiURL = new URL(urlString);

		return geoApiURL;
	}

	/*
	 * Method that pulls the coordinates from the JSON response and assigns them
	 * to a Location object
	 */
	public static Location getCoordinates(URL geoCodeURL)
	{

		// creating new Location object to store Longitude and Latitude in
		Location geoLocation = new Location();

		try
		{
			GeoCodeResponse geoResults = new GeoCodeResponse();
			URLConnection yc = geoCodeURL.openConnection();

			/*
			 * Creating ObjectMapper to map JSON response to the GeoCodeResponse
			 * object
			 */
			ObjectMapper mapper = new ObjectMapper();
			mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);

			byte[] jsonData = IOUtils.toByteArray(yc.getInputStream());

			geoResults = mapper.readValue(jsonData, GeoCodeResponse.class);

			// Used to Print Results of GeoCoding API hit
			// System.out.println(mapper.writeValueAsString(geoResults));

			/*
			 * Create Results object and assigns it to the results from the API
			 * request.
			 */
			Results results = geoResults.getResults().get(0);

			// Assign latitude and longitude results to Location object
			geoLocation.setLat(results.getGeometry().getLocation().getLat());
			geoLocation.setLng(results.getGeometry().getLocation().getLng());
			return geoLocation;

		} catch (JsonGenerationException e)
		{
			// setting coordinate defaults
			geoLocation.setLat(0);
			geoLocation.setLng(0);
			e.printStackTrace();
			return geoLocation;

		} catch (JsonMappingException e)
		{
			// setting coordinate defaults
			geoLocation.setLat(0);
			geoLocation.setLng(0);
			e.printStackTrace();
			return geoLocation;

		} catch (IOException e)
		{
			// setting coordinate defaults
			geoLocation.setLat(0);
			geoLocation.setLng(0);
			e.printStackTrace();
			return geoLocation;

		}

	}

}
