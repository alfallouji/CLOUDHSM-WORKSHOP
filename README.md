# CLOUDHSM-WORKSHOP
https://docs.aws.amazon.com/cloudhsm/latest/userguide/introduction.html

## Step 1 - VPC creation
Use the [VPC wizard](https://console.aws.amazon.com/vpc/home?#wizardSelector:) to quickly create a new VPC (choose the 2nd option - with private and public subnets). 

### Create an Elastic IP
Make sure to create an elastic IP before going through the VPC wizard - you can use this [link (for Frankfurt)](https://console.aws.amazon.com/ec2/v2/home#AllocateAddress:). 

<p align="center">
  <img src="https://raw.githubusercontent.com/alfallouji/CLOUDHSM-WORKSHOP/master/eip.png" /></p>


 ### Select VPC with private and public subnets
 [Use VPC wizard](https://eu-central-1.console.aws.amazon.com/vpc/home?region=eu-central-1#wizardSelector:)
<p align="center">
  <img src="https://raw.githubusercontent.com/alfallouji/CLOUDHSM-WORKSHOP/master/vpc1.png" /></p>

### Configure the VPC
Give it a name and select the elastic IP address that you have created.
<p align="center">
  
  <img src="https://raw.githubusercontent.com/alfallouji/CLOUDHSM-WORKSHOP/master/vpc2.png" /></p>


## Step 2 - Create a CloudHSM cluster
https://docs.aws.amazon.com/cloudhsm/latest/userguide/create-cluster.html

A cluster is a collection of individual HSMs\. AWS CloudHSM synchronizes the HSMs in each cluster so that they function as a logical unit\.

**Important**  
When you create a cluster, AWS CloudHSM creates a [service\-linked role](https://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html) named AWSServiceRoleForCloudHSM\. If AWS CloudHSM cannot create the role or the role does not already exist, you may not be able to create a cluster\. For more information, see [Resolving Cluster Creation Failures](troubleshooting-create-cluster.md)\. For more information about service–linked roles, see [Service\-Linked Roles for AWS CloudHSM](service-linked-roles.md)\. 

When you create a cluster, AWS CloudHSM creates a security group for the cluster on your behalf\. This security group controls network access to the HSMs in the cluster\. It allows inbound connections only from Amazon Elastic Compute Cloud \(Amazon EC2\) instances that are in the security group\. By default, the security group doesn't contain any instances\. Later, you [launch a client instance](launch-client-instance.md) and [configure the cluster's security group](configure-sg.md) to allow communication and connections with the HSM\.

You can create a cluster from the [AWS CloudHSM console](https://console.aws.amazon.com/cloudhsm/), the [AWS Command Line Interface \(AWS CLI\)](https://aws.amazon.com/cli/), or the AWS CloudHSM API\. 

**To create a cluster \(console\)**

1. Open the AWS CloudHSM console at [https://console\.aws\.amazon\.com/cloudhsm/](https://console.aws.amazon.com/cloudhsm/)\.

1. On the navigation bar, use the region selector to choose one of the [AWS Regions where AWS CloudHSM is currently supported](https://docs.aws.amazon.com/general/latest/gr/rande.html#cloudhsm_region)\. 

1. Choose **Create cluster**\.

1. In the **Cluster configuration** section, do the following:

   1. For **VPC**, select the VPC that you created\.

   1. For **AZ\(s\)**, next to each Availability Zone, choose the private subnet that you created\. 
**Note**  
Even if AWS CloudHSM is not supported in a given Availability Zone, performance should not be affected, as AWS CloudHSM automatically load balances across all HSMs in a cluster\. See [AWS CloudHSM Regions and Endpoints](https://docs.aws.amazon.com/general/latest/gr/rande.html#cloudhsm_region) in the *AWS General Reference* to see Availability Zone support for AWS CloudHSM\.

1. Choose **Next: Review**\.

1. Review your cluster configuration, and then choose **Create cluster**\.

**To create a cluster \([AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/)\)**
+ At a command prompt, run the [create\-cluster](https://docs.aws.amazon.com/cli/latest/reference/cloudhsmv2/create-cluster.html) command\. Specify the HSM instance type and the subnet IDs of the subnets where you plan to create HSMs\. Use the subnet IDs of the private subnets that you created\. Specify only one subnet per Availability Zone\. 

  ```
  $ aws cloudhsmv2 create-cluster --hsm-type hsm1.medium --subnet-ids <subnet ID 1> <subnet ID 2> <subnet ID N>
  
  {
      "Cluster": {
          "BackupPolicy": "DEFAULT",
          "VpcId": "vpc-50ae0636",
          "SubnetMapping": {
              "us-west-2b": "subnet-49a1bc00",
              "us-west-2c": "subnet-6f950334",
              "us-west-2a": "subnet-fd54af9b"
          },
          "SecurityGroup": "sg-6cb2c216",
          "HsmType": "hsm1.medium",
          "Certificates": {},
          "State": "CREATE_IN_PROGRESS",
          "Hsms": [],
          "ClusterId": "cluster-igklspoyj5v",
          "CreateTimestamp": 1502423370.069
      }
  }
  ```

**To create a cluster \(AWS CloudHSM API\)**
+ Send a [https://docs.aws.amazon.com/cloudhsm/latest/APIReference/API_CreateCluster.html](https://docs.aws.amazon.com/cloudhsm/latest/APIReference/API_CreateCluster.html) request\. Specify the HSM instance type and the subnet IDs of the subnets where you plan to create HSMs\. Use the subnet IDs of the private subnets that you created\. Specify only one subnet per Availability Zone\.

If your attempts to create a cluster fail, it might be related to problems with the AWS CloudHSM service\-linked roles\. For help on resolving the failure, see [Resolving Cluster Creation Failures](troubleshooting-create-cluster.md)\.


## Step 3 - Review cluster security group
https://docs.aws.amazon.com/cloudhsm/latest/userguide/configure-sg.html

When you create a cluster, AWS CloudHSM creates a security group with the name `cloudhsm-cluster-clusterID-sg`\. This security group contains a preconfigured TCP rule that allows inbound and outbound communication within the cluster security group over ports 2223\-2225\. This rule allows HSMs in your cluster to communicate with each other\. 

**Warning**  
Note the following:  
 Do not delete or modify the preconfigured TCP rule, which is populated in the cluster security group\. This rule can prevent connectivity issues and unauthorized access to your HSMs\. 
 The cluster security group prevents unauthorized access to your HSMs\. Anyone that can access instances in the security group can access your HSMs\. Most operations require a user to log in to the HSM\. However, it's possible to zeroize HSMs without authentication, which destroys the key material, certificates, and other data\. If this happens, data created or modified after the most recent backup is lost and unrecoverable\. To prevent the unauthorized access, ensure that only trusted administrators can modify or access the instances in the default security group\. 

 In the next step, you can [launch an Amazon EC2 instance](launch-client-instance.md) and connect it to your HSMs by [attaching the cluster security group](configure-sg-client-instance.md) to it\.

## Step 4 - Launch an Amazon EC2 Client Instance
https://docs.aws.amazon.com/cloudhsm/latest/userguide/launch-client-instance.html

To interact with and manage your AWS CloudHSM cluster and HSM instances, you must be able to communicate with the elastic network interfaces of your HSMs\. The easiest way to do this is to use an EC2 instance in the same VPC as your cluster\. You can also use the following AWS resources to connect to your cluster: 
+ [Amazon VPC Peering](https://docs.aws.amazon.com/vpc/latest/peering/Welcome.html)
+ [AWS Direct Connect](https://docs.aws.amazon.com/directconnect/latest/UserGuide/Welcome.html)
+ [VPN Connections](https://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/vpn-connections.html)

 The AWS CloudHSM documentation typically assumes that you are using an EC2 instance in the same VPC and Availability Zone \(AZ\) in which you create your cluster\. 

**To create an EC2 instance**

1. Open the Amazon EC2 console at [https://console\.aws\.amazon\.com/ec2/](https://console.aws.amazon.com/ec2/)\.

1. On the **EC2 Dashboard**, choose **Launch Instance**\.

1. Choose **Select** for an Amazon Machine Image \(AMI\)\. Choose a Linux AMI or a Windows Server AMI\.

1. Choose an instance type and then choose **Next: Configure Instance Details**\.

1. For **Network**, choose the VPC that you previously created for your cluster\.

1. For **Subnet**, choose the public subnet that you created for the VPC\.

1. For **Auto\-assign Public IP**, choose **Enable**\.

1. Choose **Next: Add Storage** and configure your storage\.

1. Choose **Next: Add Tags** and add any name–value pairs that you want to associate with the instance\. We recommend that you at least add a name\. Choose **Add Tag** and type a name for the **Key** and up to 255 characters for the **Value**\. 

1. Choose **Next: Configure Security Group**

1.  For **Assign a security group**, choose **Select an existing security group**\. 

1. Choose the default Amazon VPC security group from the list\.

1. Choose **Review and Launch**\.

   On the **Review Instance Launch** page, choose **Launch**\.

1.  When prompted for a key pair, choose **Create a new key pair**, enter a name for the key pair, and then choose **Download Key Pair**\. This is the only chance for you to save the private key file, so download it and store it in a safe place\. You must provide the name of your key pair when you launch an instance\. In addition, you must provide the corresponding private key each time that you connect to the instance\. Then choose the key pair that you created when getting set up\. 

   Alternatively, you can use an existing key pair\. Choose **Choose an existing key pair**, and then choose the desired key pair\. 
**Warning**  
Don't choose **Proceed without a key pair**\. If you launch your instance without a key pair, you won't be able to connect to it\.

   When you are ready, select the acknowledgement check box, and then choose **Launch Instances**\.

For more information about creating a Linux Amazon EC2 client, see [Getting Started with Amazon EC2 Linux Instances](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/EC2_GetStarted.html)\. For information about connecting to the running client, see the following topics: 
+ [Connecting to Your Linux Instance Using SSH](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/AccessingInstancesLinux.html)
+ [Connecting to Your Linux Instance from Windows Using PuTTY](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/putty.html)

 The Amazon EC2 user guide contains detailed instructions for setting up and using your Amazon EC2 instances\. The following list provides an overview of available documentation for Linux and Windows Amazon EC2 clients: 
+ To create a Linux Amazon EC2 client, see [Getting Started with Amazon EC2 Linux Instances](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/EC2_GetStarted.html)\.

  For information about connecting to the running client, see the following topics:
  + [Connecting to your Linux Instance Using SSH](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/AccessingInstancesLinux.html)
  + [Connecting to Your Linux Instance from Windows Using PuTTY](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/putty.html)
+  To create a Windows Amazon EC2 client, see [Getting Started with Amazon EC2 Windows Instances](https://docs.aws.amazon.com/AWSEC2/latest/WindowsGuide/EC2_GetStarted.html)\. For more information about connecting to your Windows client, see [Connect to Your Windows Instance](https://docs.aws.amazon.com/AWSEC2/latest/WindowsGuide/EC2_GetStarted.html#ec2-connect-to-instance-windows)\. 

**Note**  
 Your EC2 instance can run all of the AWS CLI commands contained in this guide\. If the AWS CLI is not installed, you can download it from [AWS Command Line Interface](https://aws.amazon.com/cli/)\. If you are using Windows, you can download and run a 64\-bit or 32\-bit Windows installer\. If you are using Linux or macOS, you can install the CLI using pip\. 
 
## Step 5 - Connect Amazon EC2 Instance to AWS CloudHSM Cluster
https://docs.aws.amazon.com/cloudhsm/latest/userguide/configure-sg-client-instance.html

When you launched an Amazon EC2 instance, you associated it with a default Amazon VPC security group\. This topic explains how to associate the cluster security group with the EC2 instance\. This association allows the AWS CloudHSM client running on your EC2 instance to communicate with your HSMs\. To connect your EC2 instance to your AWS CloudHSM cluster, you must properly configure the VPC default security group *and* associate the cluster security group with the instance\.

## Modify the Default Security Group<a name="configure-sg-client-instance-modify-default-security-group"></a>

You need to modify the default security group to permit the SSH or RDP connection so that you can download and install client software, and interact with your HSM\.

**To modify the default security group**

1. Open the Amazon EC2 console at [https://console\.aws\.amazon\.com/ec2/](https://console.aws.amazon.com/ec2/)\.

1. On the Amazon EC2 dashboard, select the check box for the EC2 instance on which you want to install the AWS CloudHSM client\.

1. Under the **Description** tab, choose the security group named **Default**\.

1. At the top of the page, choose **Actions**, and then **Edit Inbound Rules**\.

1. Select **Add Rule**\.

1. For **Type**, do one of the following:
   + For a Windows Server Amazon EC2 instance, choose **RDP**\. The port range `3389` is automatically populated\.
   + For a Linux Amazon EC2 instance, choose **SSH**\. The port range `22` is automatically populated\.

1. For either option, set **Source** to **My IP** to allow the client to communicate with the AWS CloudHSM cluster\.
**Important**  
Do not specify 0\.0\.0\.0/0 as the port range to avoid allowing anyone to access your instance\.

1. Choose **Save**\.

## Connect the Amazon EC2 Instance to the AWS CloudHSM Cluster<a name="configure-sg-client-instance-connect-the-ec2-instance-to-the-HSM-cluster"></a>

You must attach the cluster security group to the EC2 instance so that the EC2 instance can communicate with HSMs in your cluster\. The cluster security group contains a preconfigured rule that allows inbound communication over ports 2223\-2225\.

**To connect the EC2 instance to the AWS CloudHSM cluster**

1. Open the Amazon EC2 console at [https://console\.aws\.amazon\.com/ec2/](https://console.aws.amazon.com/ec2/)\.

1. On the Amazon EC2 dashboard, select the check box for the EC2 instance on which you want to install the AWS CloudHSM client\.

1. At the top of the page, choose **Actions**, **Networking**, and then **Change Security Groups**\.

1. Select the security group with the group name that matches your cluster ID, such as `cloudhsm-cluster-clusterID-sg`\.

1. Choose **Assign Security Groups**\.

**Note**  
 You can assign a maximum of five security groups to an Amazon EC2 instance\. If you have reached the maximum limit, you must modify the default security group of the Amazon EC2 instance and the cluster security group:  
In the default security group, do the following:  
Add an outbound rule to permit traffic on all ports to `0.0.0.0/0`\.
Add an inbound rule to permit traffic using the TCP protocol over ports `2223-2225` from the cluster security group\.
In the cluster security group, do the following:  
Add an outbound rule to permit traffic on all ports to `0.0.0.0/0`\.
Add an inbound rule to permit traffic using the TCP protocol over ports `2223-2225` from the default security group\.

## Step 6 - Create an HSM
https://docs.aws.amazon.com/cloudhsm/latest/userguide/create-hsm.html

After you create a cluster, you can create an HSM\. However, before you can create an HSM in your cluster, the cluster must be in the uninitialized state\. To determine the cluster's state, view the [clusters page in the AWS CloudHSM console](https://console.aws.amazon.com/cloudhsm/home), use the AWS CLI to run the [describe\-clusters](https://docs.aws.amazon.com/cli/latest/reference/cloudhsmv2/describe-clusters.html) command, or send a [DescribeClusters](https://docs.aws.amazon.com/cloudhsm/latest/APIReference/API_DescribeClusters.html) request in the AWS CloudHSM API\. You can create an HSM from the [AWS CloudHSM console](https://console.aws.amazon.com/cloudhsm/), the [AWS CLI](https://aws.amazon.com/cli/), or the AWS CloudHSM API\. 

**To create an HSM \(console\)**

1. Open the AWS CloudHSM console at [https://console\.aws\.amazon\.com/cloudhsm/](https://console.aws.amazon.com/cloudhsm/)\.

1. Choose **Initialize** next to the cluster that you created previously\.

1. Choose an Availability Zone \(AZ\) for the HSM that you are creating\. Then choose **Create**\.

**To create an HSM \([AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/)\)**
+ At a command prompt, run the [create\-hsm](https://docs.aws.amazon.com/cli/latest/reference/cloudhsmv2/create-hsm.html) command\. Specify the cluster ID of the cluster that you created previously and an Availability Zone for the HSM\. Specify the Availability Zone in the form of `us-west-2a`, `us-west-2b`, etc\.

  ```
  $ aws cloudhsmv2 create-hsm --cluster-id <cluster ID> --availability-zone <Availability Zone>
  
  {
      "Hsm": {
          "HsmId": "hsm-ted36yp5b2x",
          "EniIp": "10.0.1.12",
          "AvailabilityZone": "us-west-2a",
          "ClusterId": "cluster-igklspoyj5v",
          "EniId": "eni-5d7ade72",
          "SubnetId": "subnet-fd54af9b",
          "State": "CREATE_IN_PROGRESS"
      }
  }
  ```

**To create an HSM \(AWS CloudHSM API\)**
+ Send a [https://docs.aws.amazon.com/cloudhsm/latest/APIReference/API_CreateHsm.html](https://docs.aws.amazon.com/cloudhsm/latest/APIReference/API_CreateHsm.html) request\. Specify the cluster ID of the cluster that you created previously and an Availability Zone for the HSM\. 

After you create a cluster and HSM, you can optionally [verify the identity of the HSM](verify-hsm-identity.md), or proceed directly to [Initialize the Cluster](initialize-cluster.md)\.

## Step 7 - Initialize cluster
https://docs.aws.amazon.com/cloudhsm/latest/userguide/initialize-cluster.html

Complete the steps in the following topics to initialize your AWS CloudHSM cluster\.

**Note**  
Before you initialize the cluster, review the process by which you can [verify the identity and authenticity of the HSMs](verify-hsm-identity.md)\. This process is optional and works only until a cluster is initialized\. After the cluster is initialized, you cannot use this process to get your certificates or verify the HSMs\. 

**Topics**
+ [Get the Cluster CSR](#get-csr)
+ [Sign the CSR](#sign-csr)
+ [Initialize the Cluster](#initialize)

## Get the Cluster CSR<a name="get-csr"></a>

Before you can initialize the cluster, you must download and sign a certificate signing request \(CSR\) that is generated by the cluster's first HSM\. If you followed the steps to [verify the identity of your cluster's HSM](verify-hsm-identity.md), you already have the CSR and you can sign it\. Otherwise, get the CSR now by using the [AWS CloudHSM console](https://console.aws.amazon.com/cloudhsm/), the [AWS Command Line Interface \(AWS CLI\)](https://aws.amazon.com/cli/), or the AWS CloudHSM API\. 

**To get the CSR \(console\)**

1. Open the AWS CloudHSM console at [https://console\.aws\.amazon\.com/cloudhsm/](https://console.aws.amazon.com/cloudhsm/)\.

1. Choose **Initialize** next to the cluster that you [created previously](create-cluster.md)\. 

1. When the CSR is ready, you see a link to download it\.  
![\[Download certificate signing request page in the AWS CloudHSM console.\]](http://docs.aws.amazon.com/cloudhsm/latest/userguide/images/download-csr-hsm-cert.png)

   Choose **Cluster CSR** to download and save the CSR\.

**To get the CSR \([AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/)\)**
+ At a command prompt, run the following [describe\-clusters](https://docs.aws.amazon.com/cli/latest/reference/cloudhsmv2/describe-clusters.html) command, which extracts the CSR and saves it to a file\. Replace *<cluster ID>* with the ID of the cluster that you [created previously](create-cluster.md)\. 

  ```
  $ aws cloudhsmv2 describe-clusters --filters clusterIds=<cluster ID> \
                                     --output text \
                                     --query 'Clusters[].Certificates.ClusterCsr' \
                                     > <cluster ID>_ClusterCsr.csr
  ```

**To get the CSR \(AWS CloudHSM API\)**

1. Send a [https://docs.aws.amazon.com/cloudhsm/latest/APIReference/API_DescribeClusters.html](https://docs.aws.amazon.com/cloudhsm/latest/APIReference/API_DescribeClusters.html) request\.

1. Extract and save the CSR from the response\.

## Sign the CSR<a name="sign-csr"></a>

Currently, you must create a self\-signed signing certificate and use it to sign the CSR for your cluster\. You do not need the AWS CLI for this step, and the shell does not need to be associated with your AWS account\. To sign the CSR, you must do the following:

1. Get the CSR \(see [Get the Cluster CSR](#get-csr)\)\.

1. Create a private key\.

1. Use the private key to create a signing certificate\.

1. Sign your cluster CSR\.

### Create a private key<a name="sign-csr-create-key"></a>

Use the following command to create a private key\. For a production cluster, the key should be created in a secure manner using a trusted source of randomness\. We recommend that you use a secured offsite and offline HSM or the equivalent\. Store the key safely\. If you can demonstrate that you own the key, you can also demonstrate that you own the cluster and the data it contains\. 

During development and test, you can use any convenient tool \(such as OpenSSL\) to create and sign the cluster certificate\. The following example shows you how to create a key\. After you have used the key to create a self\-signed certificate \(see below\), you should store it in a safe manner\. To sign into your AWS CloudHSM instance, the certificate must be present, but the private key does not\. You use the key only for specific purposes such as restoring from a backup\. 

```
$ openssl genrsa -aes256 -out customerCA.key 2048
Generating RSA private key, 2048 bit long modulus
........+++
............+++
e is 65537 (0x10001)
Enter pass phrase for customerCA.key:
Verifying - Enter pass phrase for customerCA.key:
```

### Use the private key to create a self\-signed certificate<a name="sign-csr-create-cert"></a>

The trusted hardware that you use to create the private key for your production cluster should also provide a software tool to generate a self\-signed certificate using that key\. The following example uses OpenSSL and the private key that you created in the previous step to create a signing certificate\. The certificate is valid for 10 years \(3652 days\)\. Read the on\-screen instructions and follow the prompts\. 

```
$ openssl req -new -x509 -days 3652 -key customerCA.key -out customerCA.crt
Enter pass phrase for customerCA.key:
You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.
-----
Country Name (2 letter code) [AU]:
State or Province Name (full name) [Some-State]:
Locality Name (eg, city) []:
Organization Name (eg, company) [Internet Widgits Pty Ltd]:
Organizational Unit Name (eg, section) []:
Common Name (e.g. server FQDN or YOUR name) []:
Email Address []:
```

This command creates a certificate file named `customerCA.crt`\. Put this certificate on every host from which you will connect to your AWS CloudHSM cluster\. If you give the file a different name or store it in a path other than the root of your host, you should edit your client configuration file accordingly\. Use the certificate and the private key you just created to sign the cluster certificate signing request \(CSR\) in the next step\. 

### Sign the Cluster CSR<a name="sign-csr-sign-cluster-csr"></a>

The trusted hardware that you use to create your private key for your production cluster should also provide a tool to sign the CSR using that key\. The following example uses OpenSSL to sign the cluster's CSR\. The example uses your private key and the self\-signed certificate that you created in the previous step\. 

```
$ openssl x509 -req -days 3652 -in <cluster ID>_ClusterCsr.csr \
                              -CA customerCA.crt \
                              -CAkey customerCA.key \
                              -CAcreateserial \
                              -out <cluster ID>_CustomerHsmCertificate.crt
Signature ok
subject=/C=US/ST=CA/O=Cavium/OU=N3FIPS/L=SanJose/CN=HSM:<HSM identifer>:PARTN:<partition number>, for FIPS mode
Getting CA Private Key
Enter pass phrase for customerCA.key:
```

This command creates a file named `<cluster ID>_CustomerHsmCertificate.crt`\. Use this file as the signed certificate when you initialize the cluster\. 

## Initialize the Cluster<a name="initialize"></a>

Use your signed HSM certificate and your signing certificate to initialize your cluster\. You can use the [AWS CloudHSM console](https://console.aws.amazon.com/cloudhsm/), the [AWS CLI](https://aws.amazon.com/cli/), or the AWS CloudHSM API\. 

**To initialize a cluster \(console\)**

1. Open the AWS CloudHSM console at [https://console\.aws\.amazon\.com/cloudhsm/](https://console.aws.amazon.com/cloudhsm/)\.

1. Choose **Initialize** next to the cluster that you created previously\.

1. On the **Download certificate signing request** page, choose **Next**\. If **Next** is not available, first choose one of the CSR or certificate links\. Then choose **Next**\.

1. On the **Sign certificate signing request \(CSR\)** page, choose **Next**\.

1. On the **Upload the certificates** page, do the following:

   1. Next to **Cluster certificate**, choose **Upload file**\. Then locate and select the HSM certificate that you signed previously\. If you completed the steps in the previous section, select the file named `<cluster ID>_CustomerHsmCertificate.crt`\.

   1. Next to **Issuing certificate**, choose **Upload file**\. Then select your signing certificate\. If you completed the steps in the previous section, select the file named `customerCA.crt`\. 

   1. Choose **Upload and initialize**\.

**To initialize a cluster \([AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/)\)**
+ At a command prompt, run the [initialize\-cluster](https://docs.aws.amazon.com/cli/latest/reference/cloudhsmv2/initialize-cluster.html) command\. Provide the following: 
  + The ID of the cluster that you created previously\.
  + The HSM certificate that you signed previously\. If you completed the steps in the previous section, it's saved in a file named `<cluster ID>_CustomerHsmCertificate.crt`\. 
  + Your signing certificate\. If you completed the steps in the previous section, the signing certificate is saved in a file named `customerCA.crt`\.

  ```
  $ aws cloudhsmv2 initialize-cluster --cluster-id <cluster ID> \
                                      --signed-cert file://<cluster ID>_CustomerHsmCertificate.crt \
                                      --trust-anchor file://customerCA.crt
  {
      "State": "INITIALIZE_IN_PROGRESS",
      "StateMessage": "Cluster is initializing. State will change to INITIALIZED upon completion."
  }
  ```

**To initialize a cluster \(AWS CloudHSM API\)**
+ Send an [https://docs.aws.amazon.com/cloudhsm/latest/APIReference/API_InitializeCluster.html](https://docs.aws.amazon.com/cloudhsm/latest/APIReference/API_InitializeCluster.html) request with the following:
  + The ID of the cluster that you created previously\.
  + The HSM certificate that you signed previously\.
  + Your signing certificate\.
  +
## Step 8 - Install and Configure the AWS CloudHSM Client (Linux)
https://docs.aws.amazon.com/cloudhsm/latest/userguide/install-and-configure-client-linux.html

To interact with the HSM in your AWS CloudHSM cluster, you need the AWS CloudHSM client software for Linux\. You should install it on the Linux EC2 client instance that you created previously\. You can also install a client if you are using Windows\. For more information, see [Install and Configure the AWS CloudHSM Client \(Windows\)](install-and-configure-client-win.md)\. 

**Topics**
+ [Install the AWS CloudHSM Client and Command Line Tools](#install-client)
+ [Edit the Client Configuration](#edit-client-configuration)

## Install the AWS CloudHSM Client and Command Line Tools<a name="install-client"></a>

Connect to your client instance and run the following commands to download and install the AWS CloudHSM client and command line tools\.

------
#### [ Amazon Linux ]

```
wget https://s3.amazonaws.com/cloudhsmv2-software/CloudHsmClient/EL6/cloudhsm-client-latest.el6.x86_64.rpm
```

```
sudo yum install -y ./cloudhsm-client-latest.el6.x86_64.rpm
```

------
#### [ Amazon Linux 2 ]

```
wget https://s3.amazonaws.com/cloudhsmv2-software/CloudHsmClient/EL7/cloudhsm-client-latest.el7.x86_64.rpm
```

```
sudo yum install -y ./cloudhsm-client-latest.el7.x86_64.rpm
```

------
#### [ CentOS 6 ]

```
sudo yum install wget
```

```
wget https://s3.amazonaws.com/cloudhsmv2-software/CloudHsmClient/EL6/cloudhsm-client-latest.el6.x86_64.rpm
```

```
sudo yum install -y ./cloudhsm-client-latest.el6.x86_64.rpm
```

------
#### [ CentOS 7 ]

```
sudo yum install wget
```

```
wget https://s3.amazonaws.com/cloudhsmv2-software/CloudHsmClient/EL7/cloudhsm-client-latest.el7.x86_64.rpm
```

```
sudo yum install -y ./cloudhsm-client-latest.el7.x86_64.rpm
```

------
#### [ RHEL 6 ]

```
wget https://s3.amazonaws.com/cloudhsmv2-software/CloudHsmClient/EL6/cloudhsm-client-latest.el6.x86_64.rpm
```

```
sudo yum install -y ./cloudhsm-client-latest.el6.x86_64.rpm
```

------
#### [ RHEL 7 ]

```
sudo yum install wget
```

```
wget https://s3.amazonaws.com/cloudhsmv2-software/CloudHsmClient/EL7/cloudhsm-client-latest.el7.x86_64.rpm
```

```
sudo yum install -y ./cloudhsm-client-latest.el7.x86_64.rpm
```

------
#### [ Ubuntu 16\.04 LTS ]

```
wget https://s3.amazonaws.com/cloudhsmv2-software/CloudHsmClient/Xenial/cloudhsm-client_latest_amd64.deb
```

```
sudo dpkg -i cloudhsm-client_latest_amd64.deb
```

------

## Edit the Client Configuration<a name="edit-client-configuration"></a>

Before you can use the AWS CloudHSM client to connect to your cluster, you must edit the client configuration\.

**To edit the client configuration**

1. Copy your issuing certificate—[the one that you used to sign the cluster's certificate](initialize-cluster.md#sign-csr)—to the following location on the client instance: `/opt/cloudhsm/etc/customerCA.crt`\. You need instance root user permissions on the client instance to copy your certificate to this location\. 

1. Use the following [configure](configure-tool.md) command to update the configuration files for the AWS CloudHSM client and command line tools, specifying the IP address of the HSM in your cluster\. To get the HSM's IP address, view your cluster in the [AWS CloudHSM console](https://console.aws.amazon.com/cloudhsm/), or run the [describe\-clusters](https://docs.aws.amazon.com/cli/latest/reference/cloudhsmv2/describe-clusters.html) AWS CLI command\. In the command's output, the HSM's IP address is the value of the `EniIp` field\. If you have more than one HSM, choose the IP address for any of the HSMs; it doesn't matter which one\. 

   ```
   sudo /opt/cloudhsm/bin/configure -a <IP address>
   	
   Updating server config in /opt/cloudhsm/etc/cloudhsm_client.cfg
   Updating server config in /opt/cloudhsm/etc/cloudhsm_mgmt_util.cfg
   ```

1. Go to [Activate the Cluster](activate-cluster.md)\.
2. 
## Step 9 - Activate the Cluster
https://docs.aws.amazon.com/cloudhsm/latest/userguide/activate-cluster.html

When you activate an AWS CloudHSM cluster, the cluster's state changes from initialized to active\. You can then [manage the HSM's users](manage-hsm-users.md) and [use the HSM](use-hsm.md)\. 

To activate the cluster, log in to the HSM with the credentials of the [precrypto officer \(PRECO\)](hsm-users.md)\. This a temporary user that exists only on the first HSM in an AWS CloudHSM cluster\. The first HSM in a new cluster contains a PRECO user with a default user name and password\. When you change the password, the PRECO user becomes a crypto officer \(CO\)\.

**To activate a cluster**

1. Connect to the client instance that you launched in previously\. For more information, see [Launch an Amazon EC2 Client Instance](launch-client-instance.md)\. You can launch a Linux instance or a Windows Server\. 

1. Use the following command to start the `cloudhsm_mgmt_util` command line utility\.
**Note**  
If you are using an AMI that uses Amazon Linux 2, see [Known Issues for Amazon EC2 Instances Running Amazon Linux 2](KnownIssues.md#ki-al2)\.

------
#### [ Amazon Linux ]

   ```
   $ /opt/cloudhsm/bin/cloudhsm_mgmt_util /opt/cloudhsm/etc/cloudhsm_mgmt_util.cfg
   ```

------
#### [ Ubuntu ]

   ```
   $ /opt/cloudhsm/bin/cloudhsm_mgmt_util /opt/cloudhsm/etc/cloudhsm_mgmt_util.cfg
   ```

------
#### [ Windows ]

   ```
   C:\Program Files\Amazon\CloudhSM>cloudhsm_mgmt_util.exe C:\ProgramData\Amazon\CloudHSM\data\cloudhsm_mgmt_util.cfg
   ```

------

1. Use the enable\_e2e command to enable end\-to\-end encryption\.

   ```
   aws-cloudhsm>enable_e2e
   
   E2E enabled on server 0(server1)
   ```

1. \(Optional\) Use the listUsers command to display the existing users\.

   ```
   aws-cloudhsm>listUsers
   Users on server 0(server1):
   Number of users found:2
   
       User Id             User Type       User Name                          MofnPubKey    LoginFailureCnt         2FA
            1              PRECO           admin                                    NO               0               NO
            2              AU              app_user                                 NO               0               NO
   ```

1. Use the loginHSM command to log in to the HSM as the PRECO user\. This is a temporary user that exists on the first HSM in your cluster\. 

   ```
   aws-cloudhsm>loginHSM PRECO admin password
   
   loginHSM success on server 0(server1)
   ```

1. Use the changePswd command to change the password for the PRECO user\. When you change the password, the PRECO user becomes a crypto officer \(CO\)\. 

   ```
   aws-cloudhsm>changePswd PRECO admin <NewPassword>
   
   *************************CAUTION********************************
   This is a CRITICAL operation, should be done on all nodes in the
   cluster. Cav server does NOT synchronize these changes with the
   nodes on which this operation is not executed or failed, please
   ensure this operation is executed on all nodes in the cluster.
   ****************************************************************
   
   Do you want to continue(y/n)?y
   Changing password for admin(PRECO) on 1 nodes
   ```

   We recommend that you write down the new password on a password worksheet\. Do not lose the worksheet\. We recommend that you print a copy of the password worksheet, use it to record your critical HSM passwords, and then store it in a secure place\. We also recommended that you store a copy of this worksheet in secure off\-site storage\. 

1. \(Optional\) Use the listUsers command to verify that the user's type changed to [crypto officer \(CO\)](hsm-users.md#crypto-officer)\. 

   ```
   aws-cloudhsm>listUsers
   Users on server 0(server1):
   Number of users found:2
   
       User Id             User Type       User Name                          MofnPubKey    LoginFailureCnt         2FA
            1              CO              admin                                    NO               0               NO
            2              AU              app_user                                 NO               0               NO
   ```

1. Use the quit command to stop the cloudhsm\_mgmt\_util tool\.

   ```
   aws-cloudhsm>quit
   ```

## Step 10 - Test using the PKCS or JCE
### Java version with JCE
https://github.com/aws-samples/aws-cloudhsm-jce-examples

You can test with the LoginRunner, AESGCMEncryptDecryptRunner, etc.

### C version with PKCS
https://github.com/aws-samples/aws-cloudhsm-pkcs11-examples
