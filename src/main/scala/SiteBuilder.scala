import java.io._
import java.util
import java.util.{Collections, Comparator}

import com.sun.syndication.feed.synd.{SyndEntry, SyndEntryImpl, SyndFeedImpl}
import com.sun.syndication.io.SyndFeedOutput
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

object SiteBuilder {

  val logger = LoggerFactory.getLogger("site builder")

  val encoding = "UTF-8"

  val pageSize: Int = 11
  val paginationDirectory = new File("pages")


  def main(args: Array[String]) {

    var files = FileUtils.listFiles(new File("./posts"), Array("html"), false)

    val fileList: java.util.List[File] = new util.ArrayList[File]()
    fileList.addAll(files)

    Collections.sort(fileList, (o1: File, o2: File) => o1.getName.compareTo(o2.getName))
    logger.info("postFile count={}", fileList.size)

    val postFiles = fileList.asScala.reverse
//    postFiles.foreach(f => println(f.getName))

    if (!paginationDirectory.exists()) FileUtils.forceMkdir(paginationDirectory) else FileUtils.cleanDirectory(paginationDirectory)

    var pageNumber = 1
    val postGroups = postFiles.grouped(pageSize)
    val pageCount = (postFiles.size / pageSize) + 1 // can't call size() on grouped result, since it will not work for the result iterator
    logger.info("page count after group: {}", pageCount)
    while (postGroups.hasNext) {
      val posts = postGroups.next()
      val tuples = posts.map(postFile => {
        val title = StringUtils.substringBeforeLast(StringUtils.substringBetween(scala.io.Source.fromFile(postFile, encoding).getLines().mkString("\r\n"), "<title>", "</title>"), "-")
        val fileName = postFile.getName
        val postDate = StringUtils.substring(fileName, 0, 10)
        (title, fileName, postDate)
      })

      val pageContent = tuples.foldLeft("")((index, tuple) => {
        val block =
          s"""
             |<header class="title">
             |	<div>
             |		<small>${tuple._3}</small>
             |		<h2><a href="/posts/${tuple._2}">${tuple._1}</a></h2>
             |	</div>
             |</header>
           """.stripMargin

        index ++ block
      })

      val pageFile = new File(paginationDirectory, s"p${pageNumber}.html")
      val pagination = paginate(pageNumber, pageCount)

      val pageTemplate = StringUtils.replace(FileUtils.readFileToString(new File("templates/index.html"), encoding), "%s", pageContent + pagination)
      FileUtils.writeStringToFile(pageFile, pageTemplate, encoding)

      if (pageNumber == 1) {
        // create index.html
        FileUtils.writeStringToFile(new File("index.html"), pageTemplate, encoding)
        // generate feeds
        val feed = new SyndFeedImpl
        feed.setFeedType("rss_2.0") // (rss_0.90, rss_0.91, rss_0.92, rss_0.93, rss_0.94, rss_1.0 rss_2.0 or atom_0.3)
        feed.setTitle("扶墙老师的博客 - 一个架构士的思考与实践之地")
        feed.setLink("http://afoo.me")
        feed.setDescription("扶墙老师的博客 - 一个架构士的思考与实践之地")

        val feeds = new util.ArrayList[SyndEntry]
        for (t <- tuples) {
          val entry = new SyndEntryImpl
          entry.setTitle(t._1)
          entry.setLink(s"http://afoo.me/posts/${t._2}")
          entry.setPublishedDate(DateUtils.parseDate(t._3, "yyyy-MM-dd"))
          feeds.add(entry)
        }
        feed.setEntries(feeds)
        val writer = new BufferedWriter(new FileWriter("feeds.xml"))
        val output = new SyndFeedOutput()
        try {
          output.output(feed, writer)
        } finally {
          writer.close()
        }
      }
      pageNumber = pageNumber + 1
    }
  }

  def paginate(currentPageNumber: Int, pageCount: Int) = {
    val previousLink = if (currentPageNumber - 1 >= 1) s"/pages/p${currentPageNumber - 1}.html" else "#"
    val nextLink = if (currentPageNumber + 1 <= pageCount) s"/pages/p${currentPageNumber + 1}.html" else "#"

    s"""
       |  <hr>
       |  <div class="paginator" style="text-align: center;">
       |				<a href="$previousLink" class="paginate previous">更新鲜(Newer)</a>
       |				<a href="$nextLink" class="paginate older">更早些时候(Older)</span>
       |	</div>
    """.stripMargin
  }

}
