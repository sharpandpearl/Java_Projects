1. Ensure you have java version 1.6 or higher installed on your machine
2. Set the address that you would like to get the coordinates for in the file "geocoding_input.txt" seperate each section with a tab delimiter
	NOTE: Be sure to use a tab to sepearte EACH section(address--TAB--unit--TAB--city--TAB--state--TAB--zip)
	--> If ther is no value for one of the sections (i.e. no unit) still be sure to use a tab delimiter in its stead
3. If using linux - run get GetCoordinates.sh; if using windows - run GetCoordinates.bat
4. Your results will be in the file geocoding_output.txt