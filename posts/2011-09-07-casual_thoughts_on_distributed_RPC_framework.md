% Casual Thoughts On Distributed RPC Service Framework
% fujohnwang
% 2011-09-07
__author: fujohnwang__

# service identity
 [artifact version] + FQname + version


# service identity
 [artifact version] + FQname + version

# service lookup
 1. how many services exist?
 2. what kind of details operations/services in each service?!
 3. what's the signature of each operations?

 
# service extensions
 1. can I replace the data structure serialization and deserialization?!
 	- use custom serialization mechanism
	- some flag to indicate the mechanism that's used by service providers
 2. can I intercept service operations without service implementations' intrusion?!
 3. can I replace default service lookup service provider?
 4. can I hook in custom LB and failover mechanisms in the service users' side?
 5. can I hook in audit and monitoring concerns?
 6. can I tune the server side of the service exposion?
	- TcpNoDelay?!
	- send buffer or receive buffer size
	- timeout
	- etc.
 7. can I expose same service via different transports at the same time?!

# service upgrade
 1. service framework upgrade
 2. service upgrade

# service deployment
 1. how to ease the large scale service deployment?! even make it automatically?
 2. template publication node?!

# potential points
 1. are overloaded methods allowed?!
 2. are multiple languages inter-operations supported?!
 3. If you want to provide OO-RPC interface for clients to use, what kind of interception mechanisms u want to use? reflection or bytecode gen?  Are there some overheads in this point?
 
 
 
