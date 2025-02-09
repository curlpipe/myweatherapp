# MyWeather App Tech Test

Todo:
- [ ] If possible, include exception handling in the controller.

## Code Explanation

I added two new endpoints

- `/longest_daylight` - This will return the name of the city with the longest daylight
- `/raining_in` - This will return which cities currently have rain in them

These can be found in `WeatherController.java`

## Design Choices

### General Design Choices

- I spotted that there was a risk my API key could be committed in the `application.properties` file - I made sure to add this to `.gitignore`
- I used JavaDoc style doc strings throughout the weather controller class for clarity of implementation - this can be generated through the maven plug-in I added: `mvn javadoc:javadoc`
- I used a tool called `Spotless` attached to maven to help ensure code was correctly formatted

### Weather Controller

- For both endpoints, I used HTTP arguments over path variables because they seem more appropriate when passing in multiple arguments.
- Tests can be found in `WeatherControllerTest.java` - I have tested the utility functions and simulated mock data / HTTP requests to verify my methods work correctly
- My tests attempt to cover the entirety of the input domain to ensure no stone is unturned
- Each endpoint in the weather controller is robust and will handle exceptions including missing HTTP arguments and API query issues

## Submission

* Once you're ready to submit, raise a Pull Request to merge your changes with your main branch and share the repo with us.
