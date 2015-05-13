#!/usr/bin/env python
# -*- coding: utf-8 -*-

from subprocess import call
from sys import *

def generate_index_page(body):
	with open("templates/index.html") as template:
		return template.read() % body

def compile_markdown(md):
	pass

def compile_markdowns(md_folder):
	pass

def paginate():
	pass



print(generate_index_page("你好"))

# call(["ls", "-l"])

# print "hello, there. 你好"

# subprocess.call(["pandoc -s -N --toc --template=templates/post -f markdown -t html ", "posts/2014-10-27-digest-of-a-PD-s-talk.md", " > ", "posts/2014-10-27-digest-of-a-PD-s-talk.html"])







