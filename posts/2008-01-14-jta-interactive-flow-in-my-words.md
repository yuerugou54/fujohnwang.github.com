% JTA interactive flow in my words

> 2014年从msn space存档中重新恢复出来！

XA resource

1. JTA impl register XAResource to TransactionManager;
2. JTA impl distribute raw Resource binded with XAResource to client who requested it;
3. client codes ask for resource for data access operation, at the same time, TransactionManager invoke the start() method of the XAResource;
4. client codes finished the operation and invoke the close() method of the resource, JTA impl will make TransactonManager know this event, and TransactionManager will invoke end(Xid) method of the XAResource;
5. if a second XAResource exists, almost same flow will repeat;
6. when all of the XAResource finish their operations and the commit（）method of TM is invoked , the TM will commit the global tx with 2pc protocal:
         6.1 invoke prepare(Xid) method of each XAResource to ask for preparation before tx commi;
         6.2 if all of the responses are OK, then invoke commit(Xid) method of eache XAResource;
7. end !
 
easy Plus simple,hehe