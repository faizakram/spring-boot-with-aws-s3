# File uploading on Amazon S3 Bucket using Spring Boot with Cloud. #

As you may notice almost each application, mobile or web, gives users an ability to upload their images, photos, avatars etc. So you, as a developer, should choose the best way how to save and where to store these files.

There are different approaches for storing files:
   - directly on the server where an application is deployed
   - in the database as a binary file
   - using some cloud storages

In my developer practice I always prefer the last approach as it seems to be the best one for me (but of course you might have your own opinion). As a cloud service I use **S3 Bucket Service** of **Amazon** Company. 
And here is why:

   - it’s easy to programmaticaly upload any file using their API
   - Amazon supports a lot of programming languages
   - there is a web interface where you can see all of your uploaded files
   - you can manually upload/delete any file using web interface

https://www.getpostman.com/collections/6cf462f5f3fdf10223df
