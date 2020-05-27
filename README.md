# CLOUDHSM-WORKSHOP
https://docs.aws.amazon.com/cloudhsm/latest/userguide/introduction.html

## Step 1 - VPC creation
Use the following cloudformation template `cfn/vpc.yaml` or the use the VPC wizard to quickly create a new VPC (with private and public subnets)

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
