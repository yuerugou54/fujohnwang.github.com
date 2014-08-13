% "Playing BitTorrent with ttorrent"
% fujohnwang
% 2012-04-09

_Author: fujohnwang, 2012-04-10_

Before we get our hands dirty with the code, we need to know something basic to BitTorrent...

# A BigPicture Of BitTorrent

I will first show u a flow chart to demenstrate how BitTorrent works:
![BitTorrent Process Flow](../images/ttorrent/BT-Process-Flow.png)

First, we need to generate a .torrent metainfo file from the source file(s), in this step, the souce file(s) will be split into pieces with size of 512K-1M, and hashed with SHA-1, these hashes, the tracker, the file name(s) and other information will be collected into the generated .torrent file.

After we get a .torrent metainfo file, we need to distribute it to others so that they can download and share source files as per the metainfo in .torrent file. There are many ways to distribute the .torrrent file, like sending via email, hosted on a web server, etc. Most of the time, we will publish the .torrent file to some web server so that other peers can download it by clicking the link.

When some peer get the .torrent file, it can start to download or upload(sharing) the source files as per metainfo in the .torrent file, each peer needs to connect to the tracker(whose address can be retrieved from .torrent file) to get available peers to interact with, they send handshake, messages, and other packets to tracker to notify and receive corresponding states or events from each peer, more details can be found in the Reference part, I will not elaborate it here.

In the big picture of bit torrent, several roles you should pay attention to:

1. **tracker** 
    - the tracker is the central part of the whole ecosystem of bit torrent, it will coordinate the peers in the swarm(that's, the peer group).
2. **initial seeder**
    - each bit torrent sharing should be initialized by some peer, this peer has the whole piece of the source file and will generate .torrent metainfo file and publish it, this peer is called the initial seeder.
3. **peers**
    - **seeder** (the peers who has a complete source file, that's, it has completed the download and only offering upload to other peers)
    - **lecher** (the peers who don't have some complate source file and need to download from other peers)
    
    NOTE: a seeder can be a lecher, and a lecher can be a seeder, maybe just for different source file(s)
    
4. **.torrent publisher**
    - usually a web server which hosts the download service of the .torrent files

So we have know some basic thing about bit torrent, let's get to the real...

# TTorrent Explained

TURN's ttorrent is a java library which can be embeded in our java applications so that we can use bit torrent to do something cool. I heard about it from esty team's blog(you can find the blog in the Reference part).

ttorrent is hosted at <https://github.com/turn/ttorrent>, you can 'git clone' it to your local and browse its code to get the idea how it works.

Generally, ttorrent hide all of the trivial and complicated protocol things behind several components/classes. The codebase of ttorent is simple, just 4 top source packages:

1.  **bcodec**,  responsible for encoding/decoding of data types
2.  **client**, peer client which hide communication protocols behind to offer better use API
3.  **common**, .torrent metainfo object abstraction and peer abstraction
4.  **tracker**,  provide tracker service use simple framework to serve http requests.

# Hello, <del>World</del> ttorrent

We have talked too much, it's time to get our hands dirty with some code, here we go.


```scala    val parent = new File("/Users/XXX/Movies/Fightings/")
    val tracker = new Tracker(InetAddress.getLocalHost, "version-1")
    tracker.start()
    println("ttorrent tracker is running...")
    try {
      println("create new .torrent metainfo file...")
      val sharedFile = "/Users/XXX/Movies/Fightings/RoK-FrontKick.asf"
      val torrent = Torrent.create(new File(sharedFile), tracker.getAnnounceUrl, "createdByDarren")

      println("save .torrent to file...")
      torrent.save(new File("seed.torrent"))

      println("announce new .torrent available...")
      tracker.announce(torrent)

      println("kick off seeder to share...")
      val seeder = new Client(InetAddress.getLocalHost, new SharedTorrent(torrent, parent, true))
      seeder.share()
      try {
        if (!ClientState.ERROR.equals(seeder.getState())) {
          val reader = new BufferedReader(new InputStreamReader(System.in))
          try {
            reader.readLine()
          } finally {
            reader.close()
          }
        }
      } finally {
        seeder.stop()
      }
    } finally {
      tracker.stop()
    }```


We start a Tracker at localhost first, then we decide to publish a movie, so we use Torrent class to create a .torrent metainfo file to the movie and announce it to the tracker. After that, we need to start a Client(which works as a peer) which will serve as the initial seeder to share the movie. To make a Client to work as the initial seeder, we need to use SharedTorrent(torrent, parent, seeder) constructor and make seeder=true to create a SharedTorrent instance for Client to use. 
At the end, call seeder.share to kick off  the sharing.


Since we have the tracker and initial seeder started, let's add a new peer to the swarm to download the movie, the code listed below:



```scala    import util.control.Breaks._
    val client = new Client(InetAddress.getLocalHost, SharedTorrent.fromFile(new File("seed.torrent"), new File(".")))
    try {
      println("start to download...")
      client.share() // SEEDING for completion signal
      //      client.download()    // DONE for completion signal

      breakable {
        while (!ClientState.SEEDING.equals(client.getState)) {
          if (ClientState.ERROR.equals(client.getState())) {
            throw new Exception("ttorrent client Error State")
          }
          TimeUnit.SECONDS.sleep(1)
        }
        println("download completed.")
      }
    } finally {
      println("stop client.")
      client.stop()
    }```


we will run the new peer at local host too, and since we have saved the .torrent into 'seed.torrent' file before, we can just loaded it here, but with different parent when create the SharedTorrent instance, suppose, we just want to download the movie into the folder where the 'seed.torrent' file resides.

The new peer's job is simple, just download/share the movie with the .torrent, as the code speaks, create a Client and call share method, then we are done. The left code just for proper cleanups.

If you just want the new peer to download and exit, you can call download method of the Client instead of share, but it's not a recommended way, since BT is better that other P2P solutions with its "tit-for-tat" principle.

So that's all, when we start the tracker, the initial seeder and then the new peer, we can see that the movie is donwloaded successfully, try to add more peers to gain more benefits :-)

    Trick: 
        1. SharedTorrent's constructor needs a parent parameter(type of java.io.File), If you want to use the result SharedTorrent with the initial seeder, try to point this parent location to the path where your source files(the movies, for example) reside; For other peers, point the parent to the location where you would like to download the source files to. 
        2. You can create .torrent metainfo file for single source file or a collection of source files with Torrent class's helper methods.
        3. Tracker, Seeder, Common Peers are all linked together with the .torrent file, so to get better understanding of bit torrent, read more about .torrent stucture or specification :-)

# Why BT matters?

I know maybe you have used BT to share your audio/video with your friends every day, but as a software engineer, you may would like to escalate the usage scenarios of BT, just like esty, facebook or twitter do:

1. etsy uses BT to replicate their Solr index;
2. facebook and twitter use BT to do software artifacts distribution and deployment. 

Can we explore further?  

* large clusters' ops automation
* large distributed system state replication
* infrustructure maintainance and sync up?!
* ...

# References
1. [BitTorrent (protocol)](http://en.wikipedia.org/wiki/BitTorrent_(protocol))
2. [Ttorrent-Turn's BitTorrent library](http://turn.github.com/ttorrent/)
3. [Turbocharging Solr Index Replication with BitTorrent](http://codeascraft.etsy.com/2012/01/23/solr-bittorrent-index-replication/)
4. [Bittorrent Protocol Specification v1.0](http://wiki.theory.org/BitTorrentSpecification)

