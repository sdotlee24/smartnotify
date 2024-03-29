﻿# "SmartNotify" - A filtering solution for Gmail messages
 ### * *IN DEVELOPMENT* *

 ## 1. Motivation
> - Develop a tool that keeps track of job application status, by filtering new emails.

## 2. Learning Objectives
> - Familiarize myself with Java Spring Boot technologies, such as Data JPA and Spring Web.
> - Learn testing with JUnit and Mockito.
> - Use Data JPA to abstract the communication with Postgres DB.
> - Employ Google pub/sub to trigger a POST endpoint when a new email is sent.
> - Engineer OpenAI's GPT api to faciliate filtering of email body.

## 3. Functionality
### 1. Webhook
Combined Google's Pub/Sub and the Gmail API to form a webhook that gets triggered when a new email is recieved.
> - Used Google Pub/Sub's PUSH to trigger "/sent" POST endpoint when a new email is recieved. (The payload attatched to this event contained irrelevant data)
> - Used Gmail Api to fetch the most recent email, when the POST endpoint was triggered.

### 2. Filtering Procedure
1. Check if email is a application confirmation email, by checking for the existence of keywords.
2. If "false", check if the sender's email exists in the database. (For the case where it is a update email)
3. If "true", use GPT's API to get the company's name.
4. Store sender's email, company name, application status in the database.

*Steps 1-2 are used to filter out the obviously irrelevant emails, to limit calls to GPT's API and reduce token usage.*



