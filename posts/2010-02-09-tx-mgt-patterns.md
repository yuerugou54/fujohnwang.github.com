% Transaction Management Patterns In Brief

There are several patterns you can take when you have faced with balances on transaction management, especially when you are using spring framework to do such things. The idea is not mine, I just give a brief note here, although I have used some patterns myself, but I didn't name them.

I start from the simplest pattern.

 
# Wing-And-A-Prayer Pattern

this is a type of anti-pattern or worse practice, it means, you manage transaction on A resource, but data access on B resource, in chinese "驴唇不对马嘴", :-)

# Shared Transaction Resources Pattern

the data access request are sent to resource A, the transaction are managed against resource A too, that's, the normal situation, right?

# Non-Transactional Access Pattern

we have 2 or more resources,  in this pattern, we only guarantee ONE long transaction against one of the resources,  as to data access requests against other resources, we let them go ( auto commit, as u see). 

# Best Efforts 1PC Pattern
we have 2 or more resources too, just like the scenario in Non-Transactional Access Pattern, but only guarantee ONE long transaction is not enough, and we don't want to use XA distributed transaction management either, so what we do? 

In Best Efforts 1PC Pattern, we start all of the local transaction against each resources we manage, we commit or rollback them all as per situation, but don't guarantee the data integration when errors occurs at the point of commit or rollback, there is only 1 phase commit and we do our best to make sure the transactions to be managed properly, that's why it's called Best Efforts 1 PC Pattern. 

# XA 2PC Distributed Transaction Pattern

it's natural to resort to XA 2PC Distributed Transaction Pattern when the transaction involves more resources, we all know it, but we also know that XA 2PC distributed transaction brings performance penalty too.

So, you see, no matter which pattern you choose, choose one that's suitable to your scenario!

BTW, Taobao guys have another solution for transaction management, but their solution is specific to their scenario too, so pick the one that 's best suitable to you. 