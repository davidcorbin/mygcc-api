# Unofficial myGCC API reference

[Authentication Endpoint](#authentication-endpoint)
- [Authenticate User](#authenticate-user)
- [Deauthenticate User](#deauthenticate-user)

[User Endpoint](#user-endpoint)
- [Personal Information](#user-endpoint)
- [Contact Information](#contact-information)
- [Health Insurance](#health-insurance)
- [Chapel Attendance](#chapel-attendance)
- [Class Schedule](#class-schedule)
- [Homework](#homework)
- [Grades](#grades)
- [Crimson Cash](#crimson-cash)
- [Vehicle Information](#vehicle-information)

## Authentication Endpoint

All API calls require an authentication token that is generated after the user is logged in with their myGCC username and password.
You must send this token as the content of a HTTP header named `Authorization` in every request.

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

User deauthentication is not currently supported since myGCC automatically kills sessions after they haven't been used for a certain amount of time.
    
## User Endpoint

###### Request

    GET /1/user/
    
###### Response

    {
        "ID": "123456",
        "birth": "1/1/2000",
        "degree": "Bachelor of Science",
        "email": "LastFM1@gcc.edu",
        "ethnicity": "White",
        "gender": "Male",
        "major": "Computer Science",
        "marital": "Single",
        "name": "Mr. First Middle Last"
    }
    
###### Errors

- User not found (400)

### Contact Information

##### GET

###### Request

    GET /1/user/contact
    
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
                work: 4561237890,
                mobile: 8905671234
            },
            email: DoeJane1969@yahoo.com
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
                    street2: Box 123,
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
        message: success
    }

###### Errors

- User not found (400)
- Data invalid/Wrong format (422)

### Health Insurance

##### GET

###### Request

    GET /1/user/insurance
       
###### Response
If under the college's insurance:

    {
        usesCollegeInsurance: true,
    }
If uses their own insurance:

    {
        insurance: {
            primary: {
                companyName: Blue Cross Blue Shield,
                phone: 8668735943,
                address: 3535 Blue Cross Road Eagan\nMN 55122,
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
                address: 3535 Blue Cross Road Eagan\nMN 55122,
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
            }
        },
        physician: {
            name: Dr. James Lewis,
            phone: 8905671234
        },
        usesCollegeInsurance: false
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
        message: success
    }
       
###### Errors

- User not found (400)
- Data invalid/Wrong format (422)

### Chapel Attendance

###### Request

    GET /1/user/chapel
    
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
       
###### Response

    [
        {
            course: BIOL 101 A, 
            credits: 4.00, 
            location: [
                MAIN - HAL - 108
            ], 
            professor: [
                Stauff, Dr. Devin Lee
            ], 
            times: [
                {
                    day: M, 
                    end: 9:50, 
                    start: 9:00
                }, 
                {
                    day: W, 
                    end: 9:50, 
                    start: 9:00
                }, 
                {
                    day: F, 
                    end: 9:50, 
                    start: 9:00
                }
            ], 
            title: GENERAL BIOLOGY I
        },
        {
            course: MATH 261 C, 
            credits: 4.00, 
            location: [
                MAIN - HAL - 204, 
                MAIN - HAL - 204
            ], 
            professor: [
                McIntyre, Dr. Dale L.
            ], 
            times: [
                {
                    day: M, 
                    end: 11:50, 
                    start: 11:00
                }, 
                {
                    day: W, 
                    end: 11:50, 
                    start: 11:00
                }, 
                {
                    day: F, 
                    end: 11:50, 
                    start: 11:00
                }, 
                {
                    day: T, 
                    end: 12:20, 
                    start: 11:30
                }
            ], 
            title: CALCULUS III
        }
    ]

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
- Schedule not found (404)

### Homework

For graded work see [grades](#grades).

###### Request

    GET /1/class/XXXXNNNX/homework
    {
        token: asdf1234asdf1234
    }
    
Replace XXXXNNNX With the course code of the class you want homework for. (e.g. BIOL101A or MATH213B)
###### Response

    {
        next: [
            {
                due: 20171103,
                title: Celebration of Learning 3,
                points: 90,
                finalGrade: true
                grade: 0;
            },
            {
                due: 20171117,
                title: Celebration of Learning 4,
                points: 70,
                finalGrade: true
                grade: 0;
            }
        ],
        assigned: {
            {
                due: 20171211,
                title: Celebration of Learning 5,
                points: 70,
                finalGrade: true
                grade: 0;
            }
        },
        finished: {
            {
                due: 20170922,
                title: Celebration of Learning 1,
                points: 70,
                finalGrade: true
                grade: 90;
            },
            {
                due: 20171013,
                title: Celebration of Learning 2,
                points: 90,
                finalGrade: true
                grade: 90;
            }
        }
    }

###### Errors

- User not found (404)
- User not in class (422)

### Grades

###### Request

###### Response

###### Errors

### Crimson Cash

###### Request

    GET /1/user/ccash

###### Response

    {
        balance: 0.25
    }

###### Errors

- User not found (400)
- Crimson Cash balance not found (404)

### Vehicle Information

###### Request

    GET /1/user/vehicle
    
###### Response

If no vehicle registered:

    {
        vehicles: null
    }

Else if vehicle is registered:

    {
        vehicles: [
            {
                desc: 2003 HONDA ACCORD,
                location: GCC Main Campus,
                permit: RS-20666,
                expiration: expired,
                license: KKK2849,
                violations: null
            },
            {
                desc: 2005 VOLVO S60,
                location: GCC Main Campus,
                permit: RS-20733,
                expiration: 8/15/2018,
                license: LMN3214,
                violations: null
            }
        ]
    }

###### Errors

- User not found (400)
- Vehicle Information not found (404)

## Other Errors:
- myGCC not responding (502)

## Rate Limiting

Rate limiting should be based on authentication token.
