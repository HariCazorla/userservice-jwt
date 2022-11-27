# Getting Started
We have a user service where we can register a user. Upon registration user will be stored in the database.

To call other REST APIs we need to perform follwing steps,

1. Perform login, get the access_token from response headers.
2. Add Authorization header with "Bearer access_token"