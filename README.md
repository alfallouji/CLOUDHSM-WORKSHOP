# CLOUDHSM-WORKSHOP
https://docs.aws.amazon.com/cloudhsm/latest/userguide/introduction.html

## Step 1 - VPC creation
Use the [VPC wizard](https://eu-central-1.console.aws.amazon.com/vpc/home?region=eu-central-1#wizardSelector:) to quickly create a new VPC (choose the 2nd option - with private and public subnets). 

### Create an Elastic IP
Make sure to create an elastic IP before going through the VPC wizard - you can use this [link (for Frankfurt)](https://eu-central-1.console.aws.amazon.com/ec2/v2/home?region=eu-central-1#AllocateAddress:). 

<p align="center">
  <img src="https://raw.githubusercontent.com/alfallouji/CLOUDHSM-WORKSHOP/master/eip.png" /></p>


 ### Select VPC with private and public subnets
<p align="center">
  <img src="https://raw.githubusercontent.com/alfallouji/CLOUDHSM-WORKSHOP/master/vpc1.png" /></p>

### Configure the VPC
Give it a name and select the elastic IP address that you have created.
<p align="center">
  
  <img src="https://raw.githubusercontent.com/alfallouji/CLOUDHSM-WORKSHOP/master/vpc2.png" /></p>


### Step 2 - Create a CloudHSM cluster
https://docs.aws.amazon.com/cloudhsm/latest/userguide/create-cluster.html

### Step 3 - Review cluster security group
https://docs.aws.amazon.com/cloudhsm/latest/userguide/configure-sg.html

### Step 4 - Launch an Amazon EC2 Client Instance
https://docs.aws.amazon.com/cloudhsm/latest/userguide/launch-client-instance.html

### Step 5 - Connect Amazon EC2 Instance to AWS CloudHSM Cluster
https://docs.aws.amazon.com/cloudhsm/latest/userguide/configure-sg-client-instance.html

### Step 6 - Create an HSM
https://docs.aws.amazon.com/cloudhsm/latest/userguide/create-hsm.html

### Step 7 - Initialize cluster
https://docs.aws.amazon.com/cloudhsm/latest/userguide/initialize-cluster.html

### Step 8 - Install and Configure the AWS CloudHSM Client (Linux)
https://docs.aws.amazon.com/cloudhsm/latest/userguide/install-and-configure-client-linux.html

### Step 9 - Activate the Cluster
https://docs.aws.amazon.com/cloudhsm/latest/userguide/activate-cluster.html

### Step 10 - Test using the PKCS interface
#### Java version
https://github.com/aws-samples/aws-cloudhsm-jce-examples

You can test with the LoginRunner, AESGCMEncryptDecryptRunner, etc.

#### C version
https://github.com/aws-samples/aws-cloudhsm-pkcs11-examples
