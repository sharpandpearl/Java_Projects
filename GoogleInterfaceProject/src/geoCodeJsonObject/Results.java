
package geoCodeJsonObject;

import java.util.List;

public class Results{
   	private List address_components;
   	private String formatted_address;
   	private Geometry geometry;
   	private boolean partial_match;
   	private List types;

 	public List getAddress_components(){
		return this.address_components;
	}
	public void setAddress_components(List address_components){
		this.address_components = address_components;
	}
 	public String getFormatted_address(){
		return this.formatted_address;
	}
	public void setFormatted_address(String formatted_address){
		this.formatted_address = formatted_address;
	}
 	public Geometry getGeometry(){
		return this.geometry;
	}
	public void setGeometry(Geometry geometry){
		this.geometry = geometry;
	}
 	public List getTypes(){
		return this.types;
	}
	public void setTypes(List types){
		this.types = types;
	}
	public void setPartial_match(boolean partial_match){
		this.partial_match = partial_match;
	}
	public boolean getPartial_match(){
		return this.partial_match;
	}
}
