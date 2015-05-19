#!/usr/bin/env python
# -*- coding: utf-8 -*-

# from subprocess import *
# from sys import *

import os, shutil, logging


logging.basicConfig(level=logging.DEBUG, format='%(asctime)s - %(levelname)s - %(message)s')
# logging.disable(logging.INFO)

def generate_index_page(body):
    with open("templates/index.html") as template:
        return template.read() % body


def paginate():
	createPaginationFolderIfNecessary()
	posts = [post for post in os.listdir('posts') if post.endswith('.html')]
	posts.sort(reverse=True)
	
	pageSize = 11
	pageCount = (len(posts) / pageSize) + 1
	logging.debug('pagesize = %s' % str(pageSize))
	logging.debug('pageCount= %s' % str(pageCount))

	pages = []
	pageNumber = 0
	for post in posts:
		pages.append(post)
		if len(pages) == pageSize:
			logging.debug('generate a page = %s' % pageNumber)
			if pageNumber == 0:
				logging.debug('geneate index page')
			pages = []
			pageNumber += 1

	if len(pages) > 0:
		logging.debug('geneate the last page')




		



def generate_rss_feeds():
    pass


def createPaginationFolderIfNecessary():
	paginationFolder = 'pages'
	if not os.path.exists(paginationFolder):
		os.mkdir(paginationFolder)

# logging.debug(generate_index_page("你好"))

paginate()












# for folderName, subFolder, files in os.walk("."):
#     print("" + folderName + ", " + "|".join(subFolder) + ", \n" + "|".join(files))





