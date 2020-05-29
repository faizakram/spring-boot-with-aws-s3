![picture alt](https://faizakram.com/git-hub/aws-s3/spring_boot_aws_s3.jpeg "Amazon S3 Bucket using Spring Boot with Cloud")

As you may notice almost each application, mobile or web, gives users an ability to upload their images, photos, avatars etc. So you, as a developer, should choose the best way how to save and where to store these files

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

## Account Configuration ##
To start using S3 Bucket you need to create an account on **[Amazon website](https://aws.amazon.com/)**. Registration procedure is easy and clear enough, but you will have to verify your phone number and enter your credit card info (don’t worry, your card will not be charged if only you buy some services).

After account creation we need to create s3 bucket. Go to **Services -> S3**

![picture alt](https://faizakram.com/git-hub/aws-s3/s3-1.png "Amazon S3 Bucket using Spring Boot with Cloud")

Then press **‘Create bucket’** button.

![picture alt](https://faizakram.com/git-hub/aws-s3/s3-2.png "Amazon S3 Bucket using Spring Boot with Cloud")

### Rules for Bucket Naming ###
The following rules apply for naming S3 buckets:
- Bucket names must be between 3 and 63 characters long.
- Bucket names can consist only of lowercase letters, numbers, dots (.), and hyphens (-).
- Bucket names must begin and end with a letter or number.
- Bucket names must not be formatted as an IP address (for example, 192.168.5.4).
- Bucket names must be unique within a partition. A partition is a grouping of Regions. AWS currently has three partitions: aws (Standard Regions), aws-cn (China Regions), and aws-us-gov (AWS GovCloud [US] Regions).
- Buckets used with Amazon S3 Transfer Acceleration can't have dots (.) in their names. For more information about transfer acceleration, see [Amazon S3 Transfer Acceleration](https://docs.aws.amazon.com/AmazonS3/latest/dev/transfer-acceleration.html).

>NOTE: Amazon will give you 5GB of storage for free for the first year. After reaching this limit you will have to pay for using it.

After bucket creation, we need to give permission for users to access this bucket. It is not secured to give the access keys of your root user to your developer team or someone else. We need to create new IAM user and give him permission to use only S3 Bucket.

>AWS Identity and Access Management (IAM) is a web service that helps you securely control access to AWS resources.

Let’s create such user. **Go to Services -> IAM**. In the navigation pane, choose Users and then choose Add user.
![picture alt](https://faizakram.com/git-hub/aws-s3/s3-3.png "Amazon S3 Bucket using Spring Boot with Cloud")

Enter user’s name and check **Access type ‘Programatic access’**. Press next button. We need to add permissions to this user.

![picture alt](https://faizakram.com/git-hub/aws-s3/s3-4.png "Amazon S3 Bucket using Spring Boot with Cloud")

 Press **‘Attach existing policy directly’**, in the search field enter ‘s3’ and among found permissions choose **AmazonS3FullAccess**.
 
![picture alt](https://faizakram.com/git-hub/aws-s3/s3-5.png "Amazon S3 Bucket using Spring Boot with Cloud")

Then Press **Next Tags** 
Tags are optional.

**Review All the information.**

![picture alt](https://faizakram.com/git-hub/aws-s3/s3-6.png "Amazon S3 Bucket using Spring Boot with Cloud")

Then press next and **‘Create User’**. If you did everything right then you should see **Access key ID** and **Secret access key** for your user. There is also **‘Download .csv’** button for downloading these keys, so please click on it in order not to loose keys.
![picture alt](https://faizakram.com/git-hub/aws-s3/s3-7.png "Amazon S3 Bucket using Spring Boot with Cloud")
Our S3 Bucket configuration is done so let’s proceed to Spring Boot with Spring Cloud application.

## Spring Boot Part ##

Let’s create Spring Boot project and add aws cloud dependency.
check the complete dependency.
```
<properties>
		<java.version>1.8</java.version>
		<spring-cloud.version>Hoxton.SR4</spring-cloud.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-aws</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
	</dependencies>

	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>${spring-cloud.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
```
Now let’s add aws-s3 bucket properties to our application.properties file:

```
cloud.aws.credentials.access-key= <<AccessKey>>
cloud.aws.credentials.secret-key= <<SecretKey>>
cloud.aws.stack.auto = false
cloud.aws.region.static=ap-south-1
```
Now let’s create aws-s3 bucket configuration file:
Here I am using **@primary** annotation for override the exisiting Object.
```
@Configuration
public class AwsConfiguration {

	@Value("${cloud.aws.credentials.access-key}")
	private String awsAccessKey;

	@Value("${cloud.aws.credentials.secret-key}")
	private String awsSecretKey;

	@Primary
	@Bean
	public AmazonS3 amazonSQSAsync() {
		return AmazonS3ClientBuilder.standard().withRegion(Regions.AP_SOUTH_1)
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey)))
				.build();
	}
}
```

It’s time to create our **RestController** with following request mappings
-   /s3/bucket/create/{bucketName} :- For Bucket Creation
-   /s3/bucket/list :- Get All Buckets on the current region in properties file
-   /s3//bucket/files/{bucketName} :- Get all files details with selected bucket name
-  /s3/bucket/delete/hard/{bucketName} :- Delete selected bucket with all files
-  /s3/bucket/delete/{bucketName} :- Delete selected bucket
-  /s3/file/upload/{bucketName} :- Upload file on selected bucket
-  /s3/file/delete/{bucketName}/{fileName} :- Delete file with the selected bucket and file name
-  /s3/file/download/{bucketName}/{fileName} :- Download file with the selected bucket and file name

```
@RestController
@RequestMapping(value = "/s3")
public class FileUploadController {

	@Autowired
	private FileUploadService fileUploadService;

	@PostMapping(value = "/bucket/create/{bucketName}")
	public String createBucket(@PathVariable String bucketName) {
		return fileUploadService.createBucket(bucketName);
	}

	@GetMapping(value = "/bucket/list")
	public List<String> getBucketList() {
		return fileUploadService.getBucketList();
	}

	@GetMapping(value = "/bucket/files/{bucketName}")
	public List<FileUpload> getBucketfiles(@PathVariable String bucketName) {
		return fileUploadService.getBucketfiles(bucketName);
	}

	@DeleteMapping(value = "/bucket/delete/hard/{bucketName}")
	public String hardDeleteBucket(@PathVariable String bucketName) {
		return fileUploadService.hardDeleteBucket(bucketName);
	}

	@DeleteMapping(value = "/bucket/delete/{bucketName}")
	public String softDeleteBucket(@PathVariable String bucketName) {
		return fileUploadService.softDeleteBucket(bucketName);
	}

	@PostMapping(value = "/file/upload/{bucketName}")
	public String fileUplaod(@PathVariable String bucketName, MultipartFile file) {
		return fileUploadService.fileUplaod(bucketName, file);
	}

	@DeleteMapping(value = "/file/delete/{bucketName}/{fileName}")
	public String deleteFile(@PathVariable String bucketName, @PathVariable String fileName) {
		return fileUploadService.deleteFile(bucketName, fileName);
	}

	@GetMapping(value = "/file/download/{bucketName}/{fileName}")
	public StreamingResponseBody downloadFile(@PathVariable String bucketName, @PathVariable String fileName,
			HttpServletResponse httpResponse) {
		FileUpload downloadFile = fileUploadService.downloadFile(bucketName, fileName);
		httpResponse.setContentType("application/octet-stream");
		httpResponse.setHeader("Content-Disposition",
				String.format("inline; filename=\"%s\"", downloadFile.getFileName()));
		return new StreamingResponseBody() {
			@Override
			public void writeTo(OutputStream outputStream) throws IOException {
				outputStream.write(downloadFile.getFile());
				outputStream.flush();
			}
		};
	}
}
```
Now let's create a service class to write a business logic over here
```
@Service
public class FileUploadServiceImpl implements FileUploadService {

	private static final Logger log = LoggerFactory.getLogger(FileUploadServiceImpl.class);

	@Autowired
	private AmazonS3 amazonS3;

	@Override
	public String fileUplaod(String bucketName, MultipartFile file) {
		String fileName = "";
		try {
			if (!amazonS3.doesBucketExistV2(bucketName)) {
				return "Bucket Not Exist";
			}
			fileName = UUID.randomUUID() + file.getOriginalFilename();
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(file.getSize());
			amazonS3.putObject(bucketName, fileName, file.getInputStream(), metadata);
			log.info("File Uploaded");

		} catch (SdkClientException | IOException e) {
			log.info("File Uploading exception");
			return "Exception";
		}
		return "File Uploaded Successfully \nFileName:- " + fileName;
	}

	@Override
	public String createBucket(String bucketName) {
		if (!amazonS3.doesBucketExistV2(bucketName)) {
			// Because the CreateBucketRequest object doesn't specify a region, the
			// bucket is created in the region specified in the client.
			amazonS3.createBucket(new CreateBucketRequest(bucketName));
			// Verify that the bucket was created by retrieving it and checking its
			// location.
			return "Bucket Created \nBucket Name:-" + bucketName +"\nregion:-"
					+ amazonS3.getBucketLocation(new GetBucketLocationRequest(bucketName));
		}
		return "Bucket Already Exist";
	}

	@Override
	public List<String> getBucketList() {

		return amazonS3.listBuckets().stream().map(Bucket::getName).collect(Collectors.toList());
	}

	@Override
	public List<FileUpload> getBucketfiles(String bucketName) {
		if (!amazonS3.doesBucketExistV2(bucketName)) {
			log.error("No Bucket Found");
			return null;
		}
		return amazonS3.listObjectsV2(bucketName).getObjectSummaries().stream()
				.map(file -> new FileUpload(file.getKey(), file.getSize(), file.getETag()))
				.collect(Collectors.toList());
	}

	@Override
	public String softDeleteBucket(String bucketName) {
		if (!amazonS3.doesBucketExistV2(bucketName)) {
			log.error("No Bucket Found");
			return "No Bucket Found";
		}
		if (amazonS3.listObjectsV2(bucketName).isTruncated()) {
			amazonS3.deleteBucket(bucketName);
			return "Bucket Deleted Successfully";
		}
		return "Bucket have some files";
	}

	@Override
	public String hardDeleteBucket(String bucketName) {
		if (!amazonS3.doesBucketExistV2(bucketName)) {
			log.error("No Bucket Found");
			return "No Bucket Found";
		}
		ListObjectsV2Result results = amazonS3.listObjectsV2(bucketName);
		for (S3ObjectSummary s3ObjectSummary : results.getObjectSummaries()) {
			amazonS3.deleteObject(bucketName, s3ObjectSummary.getKey());
		}
		return "Bucket Deleted Successfully";
	}

	@Override
	public FileUpload downloadFile(String bucketName, String fileName) {
		if (!amazonS3.doesBucketExistV2(bucketName)) {
			log.error("No Bucket Found");
			return null;
		}
		S3Object s3object = amazonS3.getObject(bucketName, fileName);
		S3ObjectInputStream inputStream = s3object.getObjectContent();
		FileUpload fileUpload = new FileUpload();
		try {
			fileUpload.setFile(FileCopyUtils.copyToByteArray(inputStream));
			fileUpload.setFileName(s3object.getKey());
			return fileUpload;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String deleteFile(String bucketName, String fileName) {
		if (!amazonS3.doesBucketExistV2(bucketName)) {
			log.error("No Bucket Found");
			return "No Bucket Found";
		}
		amazonS3.deleteObject(bucketName, fileName);
		return "File Deleted Successfully";
	}

}
```

>Just import the below link in postman then you will get all the exposed api example https://www.getpostman.com/collections/6cf462f5f3fdf10223df

> [<img src="https://media.giphy.com/media/13Nc3xlO1kGg3S/giphy.gif">](https://www.youtube.com/watch?v=OzXUgGNT2WM&t=16s)
