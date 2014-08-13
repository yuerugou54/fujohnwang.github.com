% Programming with a thought considerate of others

# add necessary comment into the javadoc of your classes/components as much as possible. 
Usually, you are not the only user of the classes or components you defined before, in order to ease other people to better or fully understand your thinking at the time you define your classes or components, adding some sample code or remark words into javadoc will be more helpful to others, even to yourself.
 
You may wonder why I would like to mention this point, here are some reasons for this:
 
After on board CSTS, I have to dig into others' code to find out what this piece of work is for, so that I can do something, such as refactoring or develop new features on the fundation of those code. It takes time and sucks to try to understand those code pieces, no matter the code piece is good one or bad one, if the original author add some comment for his/her masterpiece, that will save me a lot.

Another story is , Since the original system is still using older version of some framework(can't upgrade for a reason of stability), in order to support some functionality, I have to implement some support components/classes for this, but after that, I found others were using it in a way of going against my orginal philosophy, and almost mess up my design and structure. So I have to add more comment into the javadoc to notify others how this comoponent is supposed to be used.
 
So, u see, it's a bilateral relationship, both for the orginal code author or the code user.
 
There are two camps in the world of software solutions, the open source ones and the commercial ones. I remembered some words Dino said before, he said, "the most pain or pitfall of open source software is, they are seldom good-documented". To some extent, I have to agree. It's true that the commercial software solutions always provide well-prepared document and materials,  although some open souce solutions do so, e.g. the spring framework, jboss solutions, most of the open source software do not provide proper document , even necessary document.

Bad-documented open source software solutions take more time for users to get close to them, seldom to become popular. So add more document to your software solutions as much as possible.

Even down to the fundamental parts of the software, I mean, source code, we should add necessary comment to it. You and others will benefit from what you do one day.

# If you TDD first, Code second, then please document everything last as more details as possible.
 
    Although you and others can find more information about your code later, but only unit tests are not enough, even though, you can't make sure whether you have created any unit tests. 
    So please go ahead to write document for your design and implementations, Agile doesn't mean no document, after you have finished your document, add links to your code.  In this way, others or yourself later can find out what's going on with your design and implementations soon.
    Everyone can benefit a lot from what you do.
 
Stay tuned!