% Gotchas With JUnit's Execution Model

Maybe you have known it before, or maybe not, no matter what, It’s a funny topic.

The Code below seems simple, so let’s see what happens with it:

~~~~~~~ {.java}
public class MyTest extends TestCase {  
    private int count = 0;  
    public void test1() {  
        count++;  
        assertEquals(1, count);  
    }  
    public void test2() {  
        count++;  
        assertEquals(1, count);  
    }  
}  

~~~~~~~
 
If you run this unit test, will it pass or not?

I seldom wrote too many unit tests before unless I feel it’s necessary, so I didn’t notice the problem of JUnit’s execution model, even you have written many unit tests, but don’t write enough functional tests or integration tests, I don’t think you can find the same problem either.

Although JUnit’s designer have their reasons or philosophy, but I do think the unit test and their execution model should be separated and are different things.  The execution model should not influence the unit test definition. But many JUnit’s best practice are advocated with implicit precondition of its execution model. I know someone(usually don’t know what they are doing) prefer to use static fields or methods, but most of the times, it’s a smell, bad smell of the code. Have u thought why you have to use too many static fields or methods, even static blocks? It’s because JUnit’s execution model mandate u to do it. It works, but it doesn’t mean it’s natural , rational or the best.

Until now, I have talked too much that have nothing to do with our unit test code above, OK, I admit I do it on purpose, ‘cause  I want u to find out what’s really going on with it. Now, I tell you that the unit test above will PASS. 

If you have been familiar with JUnit’s execution model for a long time, you will think it’s not a big deal, BUT I should say, it make me sick for a while and still be sick with it, that’s why  I have moved all of my unit tests to TestNG. If you can go back without any JUnit framework’s experience, just think about it that if we have a class definition like the unit test above, what’s the result you will expect? I think a natural reaction should be that it should fail. Otherwise, the code makes u feel unnatural, right? 

JUnit will create a new instance for each test method when executing the test, most of the time, you may let it go because it doesn’t bother you too much, even it does, you will resort to the best practices of JUnit and get through it. It makes sense as per JUnit’s design philosophy, but it doesn’t make sense to me. It mixes 2 concerns together and make things “dirty”. When you have to setup heavy resources, this model makes things worse, yeah, I know you would suggest to setup such things in static block, but why should I do things this way, is it the best and only way? No, even you have been fucked by GGFFWW for a long time and become to accept it, but it doesn’t mean u are treated fairly and there are no other options.

If you do still want to use it, use it in proper way and avoid getting trapped by gotchas like this one.