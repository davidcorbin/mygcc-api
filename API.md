# Unofficial myGCC API reference

## Authentication Endpoint

All API calls require an authentication token that is generated after the user is logged in with their myGCC username and password.

### Authenticate User

###### Request

    POST /1/auth 
    {
        username: un,
        password: pwd
    }
    
###### Response

    {
        time: 12341234123,
        token: asdf1234asdf1234
    }
    
###### Errors

- Invalid credentials (401)
        
    
### Deauthenticate User

###### Request

    DELETE /1/auth
    {
        token: adsf1234asdf1234
    }

###### Response

    {
        message: "Deauthenticated"
    }
    
###### Errors

- User not found (400)
    
## User Endpoint

### Name

###### Request

    GET /1/user/name
    {
        token: asdf1234asdf1234
    }
    
###### Response

    {
        name: Bob Smith
    }
    
###### Errors

- User not found (400)
- Name not found (404)

### Major

###### Request

    GET /1/user/major
    {
        token: asdf1234asdf1234
    }
    
###### Response

    {
        major: Computer Science
    }
    
###### Errors

- User not found (400)
- Major not found (404)
    
## Rate Limiting

Rate limiting should be based on authentication token.
