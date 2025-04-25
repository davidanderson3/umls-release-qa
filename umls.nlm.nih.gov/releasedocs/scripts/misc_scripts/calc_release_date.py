from datetime import datetime, timedelta, date
import json
from pathlib import Path

def calculate_release_date(release_version, override_release_date=None):
    """
    Calculates the release date and previous version based on the given release version.

    Args:
        release_version (str): The release version in the format 'YYYY<letter><letter>'.
        override_release_date (str, optional): The overridden release date in the format 'MM/DD/YYYY'. Defaults to None.

    Returns:
        dict: A dictionary containing release information.
    """

    # Check if release version is valid
    if len(release_version) != 6 or not release_version[:4].isdigit() or not release_version[4:6].isalpha():
        return {"error": "Invalid release version"}

    # Extract year and sequence number from the release version
    year = int(release_version[:4])
    sequence_number = release_version[4:].upper()
    prevy = str(year - 1)

    # Calculate release date
    if override_release_date:
        try:
            release_date = datetime.strptime(override_release_date, "%m/%d/%Y").date()
        except ValueError:
            return {"error": "Invalid overridden release date"}
    else:
        if sequence_number == "AA":
            release_date = find_first_monday(year, 5)  # First Monday of May
        elif sequence_number == "AB":
            release_date = find_first_monday(year, 11)  # First Monday of November
        else:
            return {"error": "Invalid sequence number"}

    # Calculate previous version
    previous_version = ""
    if sequence_number == "AA":
        previous_version = str(year - 1) + "AB"
    else:
        previous_version = str(year) + "AA"

    # Calculate previous version year
    #
    
    # Format the release date into separate variables
    formatted_date = release_date.strftime("%m/%d/%Y")
    formatted_date_string = release_date.strftime("%B%e, %Y")   # Calculate the formatted date string without leading zero on the day
    formatted_date_my = release_date.strftime("%B %Y")
    formatted_date_y = release_date.strftime("%Y")


    # Create a dictionary to store the release information
    release_info = {
        "targetVersion": release_version,
        "previousVersion": previous_version,
        "date": formatted_date,
        "dateString": formatted_date_string,
        "dateMY": formatted_date_my,
        "dateY": formatted_date_y,
        "datePrevY": prevy
    }

    # Convert the dictionary to JSON format
    json_data = json.dumps(release_info, indent=4)

    return release_info, json_data


# =======================================

def find_first_monday(year, month):
    """
    Find the first Monday of the given month in the specified year.

    Args:
        year (int): The year.
        month (int): The month.

    Returns:
        date: The date of the first Monday.
    """
    for day in range(1, 8):
        candidate = date(year, month, day)
        if candidate.weekday() == 0:  # Monday is represented by 0 in the weekday() function
            return candidate

    raise ValueError("First Monday not found")


# --- Testing ----

# Example release version
release_version = '2025AA'

# Call the calculate_release_date function to get the release info dictionary and JSON data
release_info, json_data = calculate_release_date(release_version)

# Print the formatted JSON data
print(json_data)

# Add comments at the top of the file
comments = "# This dictionary of release information for the target release is created using the calc_release_date.py file.\n"
comments += "# You can then use the calc_release_date.py function (which results in the release info dictionary) as the data for jinja template files.\n\n"

# Prepare the file name
file_name = f"Dict_release_info for {release_info['targetVersion']}.txt"

# Prepare the file path in the "testing" folder
testing_folder = Path(r"C:\Users\rewolinskija\Documents\umls-source-release-1\umls.nlm.nih.gov\releasedocs\testing")
file_path = testing_folder / file_name

# Write the result to the file
with file_path.open("w") as file:
    file.write(str(release_info))
    print(f"Result saved to {file_path}")


#____________________________________________________________________________________

'''
|-----------------------------------|
|   -   -   -   -   -   -   -   -   |
|-------[      TESTING      ]-------|
|   -   -   -   -   -   -   -   -   |
|-----------------------------------|


# Test the calculate_release_date function in the console
release_version = '2023AA'
release_info, json_data = calculate_release_date(release_version)
print(json_data)

release_version = '2023AB'
release_info, json_data = calculate_release_date(release_version)
print(json_data)


# Test overriding the release date
release_version = '2023AA'
override_date = '06/15/2023'
result = calculate_release_date(release_version, override_release_date=override_date)
print(result)

# =======================================

# Test  print dictionary of release info to new file
# This will create a file in the "Testing" folder with the name "Dict_release_info for 2023AB" (assuming the target version is'2023AB').
# The file will contain the dictionary with the release information.

'''