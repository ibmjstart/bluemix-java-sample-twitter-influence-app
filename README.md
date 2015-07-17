# How to Run and Deploy the Twitter Influence Analyzer #

## Overview of the app ##

This is a Java app that uses the following cloud services:

-   Cloudant

Give it a try! Click the button below to fork into IBM DevOps Services and deploy your own copy of this application on Bluemix. Note the app will not yet work; you need to set the environment variables.

[![Deploy to Bluemix](https://bluemix.net/deploy/button.png)](https://bluemix.net/deploy?repository=https://github.com/ibmjstart/bluemix-java-sample-twitter-influence-app.git)

The environment variables are your Twitter API keys.

To set them, click on your app within Bluemix, click `Environment Variables` on the left pane, then select `USER_DEFINED` to add the three necessary environment variables. Put in your Twitter credentials with the exact names shown below:

  ![image](images/environmentVarSetup.png)

Hit save and you're done!

## License ##
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

## Pushing the app using Eclipse ##

### Step 1: Prerequisites ###

#### • Download IBM Bluemix Plug-in ####

You will also need to download the IBM Bluemix plug-in for Eclipse.  To do this, go to Eclipse and follow the instructions below:

  1. Click: Help > Eclipse Marketplace...
  2. Search: "Bluemix"
  3. Look for the item titled: "IBM Eclipse Tools for Bluemix" (It should be the first listing)
  4. Click: Install

![logo](images/bluemix_plugin.png)

### Step 2. Import the project into Eclipse ###

Next, you will need to import the project into [**Eclipse**](https://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/keplersr2).  Recommendation is to use the Eclipse IDE for Java EE Developers.  

#### Import the WAR File ####
  1. Navigate to https://github.com/ibmjstart/bluemix-java-sample-twitter-influence-app/releases
  2. Click the green button labeled "twitter_influence_analyzer-1.2.war" and that will download the WAR file.
  3. Open Eclipse
  4. Then File->Import
  5. Scroll down to the "Web" section, expand that section and click WAR File then click Next.
  6. Select the war file from where it was downloaded. Ensure that `Target Runtime` is targeting IBM Bluemix.
  7. Click `Next` and then `Finish` and the project should be imported into Eclipse

### Step 3. Acquiring External and Public APIs ###

**Your app will not work if you do not add your Twitter API keys and access Tokens to your environment variable.**

This app uses some external APIs. You need to register the app with Twitter to get the keys and tokens.

#### • Twitter v1.1 API ####

To access the Twitter API you need the consumer keys and access tokens, so you must register the app with Twitter. You can register your app [here](https://dev.twitter.com/).

When you set up your twitter application settings, it will ask for the fully-qualified URL to your website. This should match, exactly, the URL you plan to use for your bluemix app.
For example, if your Bluemix app will be located at `http://jstart-tia.mybluemix.net`, then the `Website` text box under Create an Application should read that exact URL. You will have to match this
to the subdomain that you give when your app is deployed.


[More information on how to register the app with Twitter](registerTwitter.md)

#### • Google Maps v3 API ####

This app uses the Google Maps v3 APIs. Google APIs are open for the developers and you do not need to register the app with Google. Here's the [link](https://developers.google.com/maps/documentation/javascript/tutorial) for the Google Maps APIs.

### Step 4. Deploying the app ###

#### • Set up Bluemix Server ####

Make sure you are in the Java EE [perspective](http://help.eclipse.org/juno/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Fconcepts%2Fconcepts-4.htm) in Eclipse.  

  1. In the bottom window section, select the **Servers** tab.  (Alternatively, you can click: `Window > Show View > Servers`)
  2. Right-Click inside the Servers panel and select `New > Server`
  3. Select, `IBM > IBM Bluemix` and click `Next`.
  4. Enter your login information for Bluemix in the email and password sections.
  5. From the URL dropdown menu, choose: `IBM Bluemix`
  6. Hit `Next` and Bluemix will automatically validate your account credentials.
  7. Optional: Select the Organization within your Bluemix account that you would like to deploy to.
  8. Click: Finish

#### • Push the app ####
  1. Right-Click on the Bluemix server and click: `Connect`  (Optional)
  2. Right-Click on the Bluemix server and select: `Add and Remove...`
  3. Select your Twitter Influence Analyzer project from the window on the left and click: `Add >`
  4. Click: `Finish`
  5. Enter a Name for your app and select: `Next`
  6. Enter THE SAME subdomain that you used to register with Twitter. (e.g. `https://`**`myTwitterApp`**`.mybluemix.net`) Click `Next`
  7. Create and bind the Cloudant NoSQLDB service. If it is not already created, select the icon in the top right. (Refer to Option B of [Creating a Cloudant Service](#cloudant) for how to search and create the service.)
    The application is built to assume that you leave the default name of "cloudantNoSQLDB" for your service name. If you change the name, the app may break. Hit `Next`  
  8. **IMPORTANT:** Add your Twitter API credentials under environment variables. Right click, and select `Add`. Provide this information:


   | Variable Name             |  Variable Value                      |
   |---------------------------|--------------------------------      |
   | TWITTER_CONSUMER_KEY      | `{Your Twitter API Key}`             |
   | TWITTER_CONSUMER_SECRET   | `{Your Twitter API Secret}`          |
   | TWITTER_ACCESS_TOKEN      | `{Your Twitter Access Token}`        |
   | TWITTER_ACCESS_KEY        | `{Your Twitter Access Token Secret}` |

   ![image](images/environment_variables.png)

   9. You may do this step later, but your application will fail without it. Click: `Finish`. Your app will deploy to Bluemix. If you haven't already created and bound your cloudant service, please refer to Step 5: Create a Cloudant Service

**CONGRATS!**  Your app is now published to Bluemix.  
(Note: It can take a few minutes to upload everything and deploy all of the services.)

### <a name="cloudant"></a> Step 5. Create a Cloudant service ###

There are two ways to create and bind the cloudant service to your application.

Option A. Using the ACE UI:

  1. In your web browser, go to: [https://ace.ng.bluemix.net](https://ace.ng.bluemix.net)
  2. Login and scroll down to the **Services** section
  3. Click: `Add a service`
  4. Click on the service labeled: `CloudantNoSQLDB`
  5. Click: `Add to Application`
  6. From the drop down menu, select your new app.
  7. Click: `Create`

Option B. Using the Eclipse plugin for Bluemix
  1. Double click your application under the IBM Bluemix server.
  2. Under the services selection, select the "add a service icon" in the top right. (It is just an icon)
  3. Search for cloudantNoSQLDB, and select the first option. Give it a name and pick the shared plan.
  ![image](images/cloudant_service.png)
  4. Select `Finish`. This will create the service in your Bluemix organization.
  5. Under services, where your new service (with the name you specified) shows, drag the new service to "Application Services" on the right side of Eclipse. (Verify that you have the right application with the application name.)
  ![image](images/Bluemix_plugin_dashboard_small.png)
  6. Click `Update and Restart` to restart your app with the new service.


### Step 6. Adding Environment Variables through ACE ###

Another option is to add the environment variables through ACE.
  1. Go to Bluemix, and navigate to your dashboard.
  2. Select the application that you deployed earlier on eclipse.
  3. On the app information page, click the buildpack, LIBERTY-WAR (IBMJDK)... either on the left side under your app name, or in the middle.
  ![image](images/app_dashboard.png)
  4. Under Environment Variables, select `USER_DEFINED` and enter the same information specified in the table under Step 4.
  5. Select `Save`. Restart your app. It may take a few minutes for the changes to be recorded.

### Step 7. Explore your app ####

  1. Navigate to the main dashboard view in Bluemix
  2. Find your new app on the Dashboard.
  3. Below the name of your app is a link that takes you to the running app.  Click on that link.

## Screenshots ##

This is the home screen of the app. You can enter a twitter screen name in the text box and click the Analyze button to see their influence. You can also view any records saved in the database by clicking on the 'View Database' button.

![image](images/home_page.png)

After entering the twitter name and clicking the Analyze button, you'll be able to see the influence analysis of that person on the left side. You will also see their last 10 tweets and any recent mentions in the tweets plotted on Google Maps (if there is geolocation data for a tweet).

![image](images/search_results.png)

These are the records of the Influencers in the database. The user can also delete the records.

![image](images/saved_record.png)
