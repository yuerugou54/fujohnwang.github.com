#!/usr/bin/env python
# -*- coding: utf-8 -*-

# from subprocess import *
# from sys import *

import os, shutil, logging


logging.basicConfig(level=logging.DEBUG, format='%(asctime)s - %(levelname)s - %(message)s')


def generate_index_page(body):
    with open("templates/index.html") as template:
        return template.read() % body


def compile_markdown(mdFile):
    pass


def compile_markdowns(mdDirectory):
    pass


def paginate():
    pass


def generate_rss_feeds():
    pass


# logging.debug(generate_index_page("你好"))


# for folderName, subFolder, files in os.walk("."):
#     print("" + folderName + ", " + "|".join(subFolder) + ", \n" + "|".join(files))





