# chirp
An API built for speech pathology training and data collection, powered by Scala, Play, PostgreSQL, and ElasticSearch

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8801f33e7ed44e2db5162d448e9cbddf)](https://www.codacy.com/app/levit/chirp?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=birdfeed/chirp&amp;utm_campaign=Badge_Grade)
[![CircleCI](https://circleci.com/gh/birdfeed/chirp/tree/master.svg?style=shield)](https://circleci.com/gh/birdfeed/chirp/tree/master)

## Getting Started

### Requirements
- JDK 8
- Latest Scala and SBT
- Typesafe Activator

### Running the project

- Modify `.env` with appropriate values
- psql: `CREATE SCHEMA "chirp";`
- `activator run`

### Running tests
`activator test` should do everything for you