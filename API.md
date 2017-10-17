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
        message: "deauthenticated"
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

    GET /1/user/contact
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
                home: 1234567890,
                mobile: 0987654321
            },
            email: legomaster79@gmail.com
        },
        father: {
            name: James Smith,
            occupation: Plumber,
            phone: {
                work: 1234567890,
                mobile: 0987654321
            },
            email: DoeJohn1970@hotmail.com
        },
        mother: {
            name: Mary Smith,
            occupation: none,
            phone: {
                work: 0000000000,
                mobile: 8905671234
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

    POST /1/user/contact
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
                    home: 1234567890,
                    mobile: 0987654321
                },
                email: legomaster79@gmail.com
            },
            father: {
                name: James Smith,
                occupation: Plumber,
                phone: {
                    work: 1234567890,
                    mobile: 0987654321
                },
                email: DoeJohn1970@hotmail.com
            },
            mother: {
                name: Mary Smith,
                occupation: none,
                phone: {
                    work: 0000000000,
                    mobile: 8905671234
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

###### Response

    {
        time: 902419320,
        message: "success"
    }

###### Errors

- User not found (400)
- Data invalid/Wrong format (422)

### Health Insurance

##### GET

###### Request

    GET /1/user/insurance
    {
        token: asdf1234asdf1234
    }
       
###### Response
If under the college's insurance:

    {
        usesCollegeInsurance: true,
        insurance: {}
    }
If uses their own insurance:

    {
        usesCollegeInsurance: false,
        insurance: {
            primary: {
                companyName: Blue Cross Blue Shield,
                phone: 8668735943,
                address: 3535 Blue Cross Road Eagan, MN 55122,
                policy: XYZ123123123123,
                group: 78978978912,
                subscriber: {
                    name: James Jones,
                    employer: {
                        name: Target,
                        address: 900 Nicollet Mall, Minneapolis, MN 55403
                    },
                    relationship: guardian
                }
            },
            secondary: {
                companyName: Blue Cross Blue Shield,
                phone: 8668735943,
                address: 3535 Blue Cross Road Eagan, MN 55122,
                policy: XYZ123123123123,
                group: 78978978912,
                subscriber: {
                    name: James Jones,
                    employer: {
                        name: Target,
                        address: 900 Nicollet Mall, Minneapolis, MN 55403
                    },
                    relationship: guardian
                }
            },
            physician: {
                name: Dr. James Lewis,
                phone: 8905671234
            }
        } 
    }
       
###### Errors

- User not found (400)
- Insurance not found (404)

##### POST

###### Request

    POST /1/user/insurance
    
    {
        usesCollegeInsurance: false,
        insurance: {
            primary: {
                companyName: Blue Cross Blue Shield,
                phone: 8668735943,
                address: 3535 Blue Cross Road Eagan, MN 55122,
                policy: XYZ123123123123,
                group: 78978978912,
                subscriber: {
                    name: James Jones,
                    employer: {
                        name: Target,
                        address: 900 Nicollet Mall, Minneapolis, MN 55403
                    },
                    relationship: guardian
                }
            },
            secondary: {
                companyName: Blue Cross Blue Shield,
                phone: 8668735943,
                address: 3535 Blue Cross Road Eagan, MN 55122,
                policy: XYZ123123123123,
                group: 78978978912,
                subscriber: {
                    name: James Jones,
                    employer: {
                        name: Target,
                        address: 900 Nicollet Mall, Minneapolis, MN 55403
                    },
                    relationship: guardian
                }
            },
            physician: {
                name: Dr. James Lewis,
                phone: 8905671234
            }
        } 
    }
       
###### Response

    {
        time: 902419320,
        message: "success"
    }
       
###### Errors

- User not found (400)
- Data invalid/Wrong format (422)

### Chapel Attendance

###### Request

    GET /1/user/chapel
    {
        token: asdf1234asdf1234
    }
    
###### Response

    {
        required: 16,
        makeups: 9,
        attended: 12,
        remaining: 13,
        special: 0
    }

###### Errors

- User not found (400)
- Chapel Attendance not found (404)

### Class Schedule

###### Request

    GET /1/user/schedule
    {
        token: asdf1234asdf1234
    }
       
###### Response

    {
        {
            course: BIOL 101 A,
            title: GENERAL BIOLOGY I,
            professor: Dr. Gerald Stauff,
            times: [
                {
                    day: M,
                    start: 09:00,
                    end: 09:50
                },
                {
                    day: W,
                    start: 09:00,
                    end: 09:50
                },
                {
                    day: F,
                    start: 09:00,
                    end: 09:50
                }
            ]
        },
        {
            course: MATH 213 B,
            title: DISCRETE MATH/COMP SCI,
            professor: Dr. Eric Bancroft,
            times: [
                {
                    day: M,
                    start: 14:00,
                    end: 14:50
                },
                {
                    day: W,
                    start: 14:00,
                    end: 14:50
                },
                {
                    day: F,
                    start: 14:00,
                    end: 14:50
                }
            ]
        },
        {
            course: MATH 261 C,
            title: CALCULUS III,
            professor: Dr. Dale McIntyre,
            times: [
                {
                    day: M,
                    start: 11:00,
                    end: 11:50
                },
                {
                    day: W,
                    start: 11:00,
                    end: 11:50
                },
                {
                    day: T,
                    start: 11:30,
                    end: 12:20
                },
                {
                    day: F,
                    start: 11:00,
                    end: 11:50
                },
            ]
        }
    }

Days of the week correspond to letters:
- Sunday: U
- Monday: M
- Tuesday: T
- Wednesday: W
- Thursday: R
- Friday: F
- Saturday: S

###### Errors
- User not found (400)
- Insurance not found (404)

## Other Errors:
- myGCC not responding (502)

## Rate Limiting

Rate limiting should be based on authentication token.
