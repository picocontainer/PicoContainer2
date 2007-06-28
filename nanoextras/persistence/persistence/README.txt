Missing hibernate dependencies shall be obtained from hibernate distribution.
Hibernate uses custom compiled  CGLIB, and JTA can not be distributed from 
central repository due to SUNs policy. 

Those jars are located in lib directory of hibernate distribution. 
You will have to rename jars to match specified dependency names 