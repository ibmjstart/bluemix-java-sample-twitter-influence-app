Instructions to run the Twitter Influence Analyzer Application(Java version):
==========================================


Using the terminal/command line interface:
------------------------------------------

-   **Prerequisites:**
    -   Before we begin, we first need to install the command line tool that will be used to upload and manage your application. Cloud Foundry uses a tool called [**cf**](https://github.com/cloudfoundry/cf). This is a Ruby application, so you must have Ruby installed. If you are running on Windows, you can install Ruby from [this](http://rubyinstaller.org/downloads/) website. 
    -   For Linux systems, consult your documentation for how to install the **ruby** package - for Ubuntu the command **apt-get install ruby** should work for you.

    -   Once Ruby is installed, cf can be installed by using the **gem install** command:
        
        \> **gem install cf**
        
-   **Download and Load App Into Eclipse**
    - There are two ways to download the app and import it into Eclipse:
        - 1. Import the Eclipse project by following these instructions:
            - A. clone the current repository, i.e. 
                    **git clone https://github.com/ibmjstart/bluemix-java-twitter-search.git** 
            - B. Open Eclipse
            - C. Then File->Import
            - D. Under the header labeled "General", click "Existing Projects Into Workspace" and click Next
            - E. Click "Browse" next to the first text field, and navigate to the cloned repository and find the folder labeled "app" and click ok.
            - F. Under Projects you should now see a project called "TwitterSearch", make sure the checkbox next to the "TwitterSearch" project is checked and then click "Finish
            - G. You should now see the "TwitterSearch" project in your list of projects in Eclipse.
        
        - 2. Import the WAR File
            - A. Navigate to https://github.com/ibmjstart/bluemix-java-twitter-search/releases
            - B. Click the green button labeled "TwitterSearch.war" and that will download the WAR file.
            - C. Open Eclipse
            - D. Then File->Import
            - E. Scroll down to the "Web" section, expand that section and click WAR File then click Next.
            - F. Click next and then Finish and the project should be imported into Eclipse

-   **Overview of the app:** This is a Java Web(Standalone) app that uses the following cloud service:
    -   MongoDB (backend database)

-   **Download and modify app code:**
    -   **Download the app**
        - clone the current repository, i.e. 
            **git clone https://github.com/ibmjstart/bluemix-java-sample-twitter-influence-app.git**

    -   **External and Public APIs:**

        This app uses some external APIs. You need to register the app with Twitter and Klout to get the keys and tokens.

        -   **Twitter v1.1 API:**

            To access the Twitter API you need the consumer keys and access tokens, so you must register the app with Twitter. You can register your app [here](https://dev.twitter.com/).

            [More information on how to register the app with Twitter](registerTwitter.md)

        -   **Klout API:**

            You can register the app with Klout [here](http://developer.klout.com/member/). When you register with Klout, you'll get a Klout Key, which you can use to create a Klout Object as shown in the code.

        -   **Google Maps v3 API:**

            This app uses the Google Maps v3 APIs. Google APIs are open for the developers and you do not need to register the app with Google. Here's the [link](https://developers.google.com/maps/documentation/javascript/tutorial) for the Google Maps APIs.

        -   The twitter credentials are entered in the file called as twitter4j.properties which is present in the classpath (src directory). Just copy paste the credentials in twitter4j.properties file that you get after registering the app with twitter. Also the Klout API key is entered in the file called klout.properties present in the classpath as shown below:

            ![image](/images/klout_key.png)

    -   **Deploy the App:**

        Now that you have included the twitter keys and tokens and klout key as shown above, you are all set to deploy the app. In the terminal, go in the directory of the app. The application is wrapped in a WAR file. You can directly deploy/push the WAR file using push command:

        \> **cf push**

        Just follow the instructions on the screen. You can select the default settings for deploying the app, i.e. for URL, memory reservations (512 Recommended), number of instances. You need to bind the MongoDB service to the app.

        Binding a Service to Your App

        -   Create the service instance and bind the service instance while deploying the app. The **cf push** command will ask, "Create services to bind to 'appname'?" Answer yes and go through the menu. See the screenshots of a sample app push below for more clarity. 

        Note: This app expects details of the mongoDB service to be present in the environment variables and will generate exception if you try to deploy it without first binding the service.

        Here are  some snapshots of how one would deploy the app and create services required for the app: 
        
        ![Deploy steps](/images/push-app1.png)

        ![Deploy steps](/images/push-app-2.png)

        ![Deploy steps](/images/push-app-3.png)


    -   After the application is deployed using **cf push**, you can check the status of the app using the following command: **cf apps**. If the status is RUNNING, you can hit the URL in the browser and see the application is running.


Troubleshooting
-----------------------------------
-   Sometimes your app may not work as expected and debugging needs to be done. The cf command line tool can be used to assist with debugging. With the cf you can check your app's logs by typing the command **cf logs [app_name]** 

-   When you first start using the cf tool, you may potentially have trouble logging in due to no target being set. To view the target that is set, type **cf target** and if you want to set a new target type **cf target [target_url]**. Note: The target URL will usually be in the form of http://api.xxx.tld

-   From time to time your app may stop working, this means it could require a restart. To do this you must first stop it by typing **cf stop**. Once the app has been stopped, you can type **cf start** and if there are no other problems your app should start. 


Some screen-shots of the running app
------------------------------------

-   This is the home screen of the app. You can enter a twitter screen name in the text box and click the Analyze button to see their influence. You can also view any records saved in the database by clicking on the 'View Database' button.

    ![image](/images/home.png)

-   After entering the twitter name and clicking the Analyze button, you'll be able to see the influence analysis of that person on the left side. You will also see their last 10 tweets and any recent mentions in the tweets plotted on Google Maps (if there is geolocation data for a tweet).

    ![image](/images/results.png)

-   These are the records of the Influencers in the database. The user can also delete the records.

    ![image](/images/saved_records.png)

