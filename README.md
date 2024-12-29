
# Taxi - App

<div align="center">

| Demo                                                                 | Description                                                                      |
| :------------------------------------------------------------------- | :---------------------------------------------------------------------------------
| <img src="app/AppDemo/taxi_app.gif" width="107" alt="App Demo">      | This is a native Android application developed as part of a technical test for a ride-hailing service. It allows users to request a ride, choose betweendifferent ride options, and view their ride history.                                      

</div>


## Features

*   Request a ride by specifying the origin and destination addresses.
*   View the ride in a static map.
*   View available ride options with details about the driver, vehicle, and estimated fare.
*   Confirm a ride and automatically add it to the ride history.
*   View past rides with details about the date, time, driver, and fare.
*   Error handling for various scenarios, providing the API messages for each case or using custom informative messages to the user.
*   Debouncing to prevent accidental multiple requests.
*   Filter by driver to display the appropriate data from the random data the API sends.
*   Sort by date to display the history data in chronological order as it should appear, regardless of the order of the random data the API sends.
*   For testing purposes, there is an autocomplete function for the default API customer, origin, and destination.
*   For testing purposes, a fake driver instance is added to confirm the error handling case.
*   In ride history, labels are added to the cards to demonstrate if the ride is from the local database or from the random data the API sends.

## Technical Details

The app was built using Kotlin and Jetpack Compose, following Clean Architecture principles and the MVI (Model-View-Intent) pattern. 
It uses the following libraries and technologies:

*   **Room Database:** For persisting ride history data locally.
*   **Dagger Hilt:** For dependency injection.
*   **Ktor:** For making API calls to the backend.
*   **Kotlin Coroutines:** For managing asynchronous operations.
*   **Jetpack Compose:** For building the user interface.
*   **Google Maps:** For showing a static map of the origin and destination.

The app also includes Unit Tests to ensure the correctness of the core logic.

## Project Structure

                                    +---------------------+
                                    |    Presentation     |
                                    +---------------------+
                                    |  screens(ViewModels)|
                                    |  common             |
                                    |  navigation         |
                                    +---------------------+
                                             ^
                                             |
                                    +---------------------+
                                    |       Domain        |
                                    +---------------------+
                                    |   usecases          |
                                    |   model             | 
                                    |   error             |
                                    |   repository        |
                                    |   remote            |
                                    +---------------------+
                                             ^
                                             |
                                    +---------------------+
                                    |        Data         |
                                    +---------------------+
                                    |  mapper             |
                                    |  model              |
                                    |  repository(error)  | 
                                    +---------------------+
                                             |
                                             |
                                    +---------------------+
                                    |      Source         |
                                    +---------------------+
                                          /         \
                                         /           \
                                        /             \
                                +----------+       +----------+
                                |  Local   |       |  Remote  | 
                                +----------+       +----------+
                                |  room    |       | API(Ktor)| 
                                +----------+       +----------+

## API Integration

The app interacts with a simulated backend API that provides endpoints for estimating ride fares, confirming rides, and retrieving ride history. The API endpoints and their specifications are as follows:

*   **POST /ride/estimate:** Calculates the estimated fare for a ride based on the origin and destination addresses.
*   **PATCH /ride/confirm:** Confirms a ride with a selected driver.
*   **GET /ride/{customer_id}:** Retrieves the ride history for a given customer.
*   **GET /ride/{customer_id}?{driver_id}:** Retrieves the ride history for a given customer and driver.

## Error Handling

The app includes robust error handling to address various scenarios, such as:

*   Invalid input data.
*   Network errors.
*   Server errors.
*   No available drivers.
*   Distance limitations for certain drivers.
*   Failure to save rides to the database.


## How to Run

1.  Clone the repository
2.  Open the project in Android Studio
3.  Build and run the app on an emulator or device
4. Use your own Google Maps API key to the manifest file in the "google_maps_api" property:
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="${google_maps_api}" />
```

## Conclusion

This project demonstrates the core concept of a well-structured, maintainable, scalable, and functional Android application using modern technologies, 
clean code, clean architecture, MVI and best practices.
