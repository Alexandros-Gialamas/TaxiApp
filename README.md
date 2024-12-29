
# Taxi - App

[Include a screenshot or GIF of your app in action here]

This is a native Android application developed as part of a technical test for a ride-hailing service. 
It allows users to request a ride, choose between different ride options, and view their ride history.

## Features

*   Request a ride by specifying the origin and destination addresses.
*   View the ride in a static map.
*   View available ride options with details about the driver, vehicle, and estimated fare.
*   Confirm a ride and automatically add it to the ride history.
*   View past rides with details about the date, time, driver, and fare.
*   Error handling for various scenarios, providing the api messages for each case or using custom informative messages to the user.
*   Debouncing to prevent accidental multiple requests.
*   Filter by driver for showing the appropiate data from the random data api sends.
*   Sort by date for showing the history data as it should happend in order data from the random data api sends.
*   For testing purpuses there is an autocomplete function to the default api customer, origin, and destination.
*   For testing purpuses added a fake driver instance to confirm the error handling case.
*   In ride history added labels to the cards for demonstrating if the ride is from the local database or from the random data api sends.

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

[Describe the project structure here, including the main packages and modules. You can also include a simple diagram if it helps with visualization.]

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
*   Fail to save rides to database.

## Screenshots

[Include more screenshots here, showcasing different screens and features of your app]


## How to Run

1.  Clone the repository
2.  Open the project in Android Studio
3.  Build and run the app on an emulator or device
4. Use your own Google Maps Api key to the manifest file in the "google_maps_api" property
<meta-data android:name="com.google.android.geo.API_KEY"
            android:value="${google_maps_api}"/> 

## Conclusion

This project demonstrates the core concept of a well-structured, maintainable, scalable and functional Android application using modern technologies, 
clean code, clean architecture and best practices.
