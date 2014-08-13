import org.apache.commons.io.FileUtils
import java.io._
import org.slf4j.LoggerFactory

import collection.JavaConversions._
import org.apache.commons.lang3.StringUtils

object SiteBuilder {

  val logger = LoggerFactory.getLogger("site builder")

  val encoding = "UTF-8"

  val pageSize: Int = 7
  val paginationDirectory = new File("pages")


  def main(args: Array[String]) {

    val postFiles = FileUtils.listFiles(new File("./posts"), Array("html"), false).toSeq.reverse

    logger.info("postFile count={}", postFiles.size)

    if (!paginationDirectory.exists()) FileUtils.forceMkdir(paginationDirectory) else FileUtils.cleanDirectory(paginationDirectory)

    var pageNumber = 1
    val postGroups = postFiles.grouped(pageSize)
    val pageCount = (postFiles.size / pageSize) + 1 // can't call size() on grouped result, since it will not work for the result iterator
    logger.info("page count after group: {}", pageCount)
    while (postGroups.hasNext) {
      val posts = postGroups.next()
      val pageContent = posts.foldLeft("")((index, file) => {
        index ++ assemblePostEntry(file)
      })
      //      logger.info("page {} content : {}", pageNumber, pageContent)

      val pageFile = new File(paginationDirectory, s"p${pageNumber}.html")
      val pagination = paginate(pageNumber, pageCount)
      //      logger.info("pagination : {}", pagination)

      val pageTemplate = StringUtils.replace(FileUtils.readFileToString(new File("templates/index.html"), encoding), "%s", pageContent + pagination)
      FileUtils.writeStringToFile(pageFile, pageTemplate, encoding)
      if (pageNumber == 1) FileUtils.writeStringToFile(new File("index.html"), pageTemplate, encoding)
      pageNumber = pageNumber + 1
    }
  }

  def paginate(currentPageNumber: Int, pageCount: Int) = {
    val previousLink = if (currentPageNumber - 1 >= 1) s"/pages/p${currentPageNumber - 1}.html" else "#"
    val nextLink = if (currentPageNumber + 1 <= pageCount) s"/pages/p${currentPageNumber + 1}.html" else "#"

    s"""
      |<hr>
      |<ul class="pager">
      |  <li><a href="$previousLink">Previous</a></li>
      |  <li><a href="$nextLink">Next</a></li>
      |</ul>
    """.stripMargin
  }

  def assemblePostEntry(postFile: File) = {
    val title = StringUtils.substringBetween(scala.io.Source.fromFile(postFile, encoding).getLines().mkString("\r\n"), "<title>", "</title>")
    val postDate = StringUtils.substring(postFile.getName, 0, 10)
    s"""<h3><a href="/posts/${postFile.getName}">$title</a></h3><p>by FuqiangWang at $postDate</p>"""
  }

}

