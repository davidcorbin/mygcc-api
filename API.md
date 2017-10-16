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

### Contact Information

##### GET

###### Request

    GET/1/user/contact
    {
        token: asdf1234asdf1234
    }
    
###### Response

    {
        student: {
            address: {
                street: 1234 Johnny Cake Ridge Rd.,
                city: Apple Valley,
                state: MN,
                zipcode: 55124,
                country: UNITED STATES
            },
            phone: {
                home: 123-456-7890,
                mobile: 098-765-4321
            },
            email: legomaster79@gmail.com
        },
        father: {
            name: James Smith,
            occupation: Plumber,
            phone: {
                work: 123-456-7890,
                mobile: 098-765-4321
            },
            email: DoeJohn1970@hotmail.com
        },
        mother: {
            name: Mary Smith,
            occupation: none,
            phone: {
                work: 000-000-0000,
                mobile: 890-567-1234
            }
        },
        secondary: {
            name: Robert Smith,
            address: {
                street: 4321 Johnny Cake Ridge Rd.,
                city: Apple Valley,
                state: MN,
                zipcode: 55124,
                country: UNITED STATES
            }
        }
    }
    
###### Errors

- User not found (400)
- Contact information not found (404)

##### POST

###### Request

###### Response

###### Errors

### Health Insurance

##### GET

###### Request
       
###### Response
       
###### Errors

##### POST

###### Request
       
###### Response
       
###### Errors

### Chapel Attendance

###### Request

    GET/1/user/chapel
    {
        token: asdf1234asdf1234
    }
    
###### Response

    {
        attended: 10,
        remaining: 5
    }

###### Errors

- User not found (400)
- Chapel Attendance not found (404)
    
## Rate Limiting

Rate limiting should be based on authentication token.
