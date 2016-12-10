# chirp
An API built for speech pathology training and data collection, powered by Scala, Play, and PostgreSQL

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8801f33e7ed44e2db5162d448e9cbddf)](https://www.codacy.com/app/levit/chirp?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=birdfeed/chirp&amp;utm_campaign=Badge_Grade)
[![CircleCI](https://circleci.com/gh/birdfeed/chirp/tree/master.svg?style=shield)](https://circleci.com/gh/birdfeed/chirp/tree/master)

## Set-up

### Requirements
Install the following through your favorite package manager.

- JDK 8
- Latest Scala and SBT
- Typesafe Activator
- PostgreSQL 9.5+
- Valid AWS access key and secret. [Amazon IAM Documentation](https://aws.amazon.com/blogs/security/wheres-my-secret-access-key/)

### Starting the API

#### Setting up fixtures
You may want to play with some test data. To do so, please clear out your test database's `public` schema space and then run the following:

```bash
CREATE_FIXTURES=true sbt testProd
```

The server will start, insert fixtures, and then kill its own PID from `target/universal/stage/RUNNING_PID`. We eventually plan to refactor this into a command you can invoke via `sbt`, but this works for now.

#### Development
All you really need to do is:
```bash
git clone git@github.com:birdfeed/chirp.git
cd chirp
# Configure your AWS keys and PostgreSQL credentials
vim .env
sbt run
```

#### Production
We recommend using a PaaS such as [Heroku](https://heroku.com) for convenience, but you can feel free to set `chirp` up behind your favorite web server. Play provides [documentation on how to set up](https://www.playframework.com/documentation/2.5.x/HTTPServer) a project using `nginx`, `apache`, and `lighttpd`.

All that is required of you is to define the `PLAY_SESSION_SECRET` environment variable. See `application.conf` for more details. You can feel free to provide tinfoil-hat-friendly high entropy salts, but we feel that the Play `sbt` hook does the job well enough.

##### Quick and dirty DIY production deploy with Netty
Please be mindful and run web services as non-privileged users.

```bash
export PLAY_SESSION_SECRET=$(sbt generatePlaySecret)
# sbt testProd will simulate production but you should not use it
cd chirp/target/universal/stage
chmod +x chirp
./chirp
```
## API
```
hostname/v1/{resource}
```
View the `routes` file for available resources and actions

### Security
The Chirp API uses a two-key system to authorize clients to the service, and users to their resources. Your headers should define `Chirp-Api-Key` and `Chirp-Access-Token`. API keys are issued by administrators to developers and access tokens are issued per-session, per-user as resources are requested. Both the API key and access token are ISO-11578 compliant UUIDv4 strings.

*It is absolutely imperative you use [HSTS-compliant HTTPS](https://en.wikipedia.org/wiki/HTTP_Strict_Transport_Security) to avoid interception of your requests*

TODO: Enforce refresh tokens

##### Request
```http
POST /v1/user/authenticate
Chirp-Api-Key: [your key]
Content-Type: application/json

{
    "email": "foo@bar.baz",
    "password": "hunter12"
}
```
##### Response
```http
200 OK

{
  "userId": 1,
  "token": "d647f4e1-1198-4523-87dc-44dba7e9671b",
  "refreshToken": "fd44f936-aba6-40ff-a53b-220192a1d423"
}
```

### Running tests
Due to poor mocking support in recent versions of ScalaTest, you must place your AWS keys in `.env` to run the test suite or the AWS client we use will throw a fatal exception. We are planning to rectify this as soon as there is a new `ScalaMock` milestone.

Otherwise: `sbt test`
