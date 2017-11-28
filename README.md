# Unofficial myGCC API

[![Build Status](https://travis-ci.com/davidcorbin/mygcc-api.svg?token=dxqddm4qxdWvzPBrhpv6&branch=master)](https://travis-ci.com/davidcorbin/mygcc-api)
[![Dependency Status](https://www.versioneye.com/user/projects/59f299a215f0d70039a4a8cf/badge.svg?style=flat)](https://www.versioneye.com/user/projects/59f299a215f0d70039a4a8cf)

REST API to interface with the myGCC website.

See [API.md](API.md) for examples and a reference.

## Development

### Packaging
```mvn clean package```

If tests are failing or skipping make sure you have four environmental variables set:
- `myGCC_username` A **valid** myGCC username/id number.
- `myGCC_password` The password for that username.
- `enckey` A 16 character string for encrypting the tokens.
- `initvect` Another 16 character string for encrypting the tokens.

### Run server
```java -cp "target/classes:target/dependency/*" com.mygcc.api.Main```
