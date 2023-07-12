# Weather Tracker App
This Android application displays weather information and temperature from your current location. You can also select a specific location on the map . The app allows you to add favorite locations and view weather details for those locations. Additionally, you can set alerts for various weather conditions such as rain, wind, temperature, fog, snow, and more.

# Features
1-Current temperature display

2-Current date and time

3-Humidity, wind speed, pressure, and cloud information

4-City name and weather icon

5-Weather description (e.g., clear sky, light rain)

6-Hourly forecast for the current date

7-day forecast with past weather data

8-Settings to choose location options (GPS or map) and temperature units (Kelvin, Celsius, Fahrenheit)

9-Settings to choose wind speed units and language (Arabic, English)

10-Weather alerts with customizable alarm settings

# Screens
The app consists of the following screens:

1-Settings Screen: Allows users to choose location options and temperature units.

2- Home Screen: Displays current weather information, including temperature, date, time, humidity, wind speed, pressure, clouds, city, weather icon, and description. Also shows hourly and 7-day forecasts.

3 -Weather Alerts Screen: Lets users set weather alerts with customizable alarm settings.

4-Favorite Screen: Lists favorite locations with the ability to view forecast details for each location. Users can add new favorite locations using the map or auto-complete text search.

# Architecture
The project follows the MVVM (Model-View-ViewModel) design pattern, which separates the user interface (View) from the underlying data (Model) and introduces a ViewModel to handle the business logic and communication between the View and Model. This pattern promotes a clean architecture and facilitates easier testing and maintenance of the codebase.

# Dependencies
The project utilizes the following dependencies:

1- Picasso - Image loading and caching library.

2- Retrofit - Type-safe HTTP client for API requests.

3- Room - Database library for data persistence.

4- WorkManager - Library for managing background tasks, such as weather updates and notifications. Google Maps API - Enables map functionality and location selection.

5- Lottie: For the splash screen animation.

Unit Testing
The project includes unit testing for the ViewModel and Repository components. Unit testing is an essential part of software development, as it helps ensure the correctness and reliability of the code.

# SDK
![Screenshot (885)](https://github.com/ShroukMohamed16/WeatherTracker/assets/96442266/37aa25f3-f367-4dd7-8fb8-7c49c59564fe)
