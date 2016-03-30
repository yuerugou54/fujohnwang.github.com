#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# created by fujohnwang at 2016-03-29

# this python script is mainly for practice by rewriting the SiteBuilder.scala.
# we will mainly focus on String and File operations.

import glob
import os
import os.path
import re


def clear_dir(dir, pattern):
    files = glob.glob1(dir, pattern)
    for f in files:
        os.remove(os.path.join(dir, f))


def generate(posts_dir="posts",
             pages_dir="pages2",
             charset='utf8',
             glob_pattern="*.html",
             page_size=11):
    if not os.path.exists(pages_dir):
        os.mkdir(pages_dir)
    else:
        clear_dir(pages_dir, glob_pattern)

    posts = glob.glob1(posts_dir, glob_pattern)
    page_number = 1
    page_count = len(posts)
    index = 0
    index_page_content = ''

    posts.reverse()
    for p in posts:
        index += 1
        with open(os.path.join(posts_dir, p), encoding=charset) as post_file:
            title = re.search('%s(.*)%s' % ("<title>", "</title>"), '\n'.join(post_file.readlines())).group(1).split('-')[0]
            date = p[:10]

            index_page_content += """
            <div class="entry">
                <div class="entry-content">
                    <h2 class="entry-title"><a href="/posts/{}">{}</a></h2>
                    <p class="entry-date">发布日期: {}</p>
                </div>
            </div>
            """.format(p, title, date)

            if index % page_size == 0:
                pagination = paginate(page_number, page_count, pages_dir)
                with open('templates/index.html', encoding=charset) as template:
                    content = '\n'.join(template.readlines()).replace('%s', index_page_content + '\n' + pagination)
                    with open(os.path.join(pages_dir, 'p{}.html'.format(page_number)), 'w', encoding=charset) as output_file:
                        output_file.write(content)
                    if page_number == 1:
                        with open('index.html', 'w', encoding=charset) as index_page:
                            index_page.write(content)
                page_number += 1
                index_page_content = ''


def paginate(page_number, page_count, prefix='page2'):
    previous = '#'
    if page_number - 1 >= 1:
        previous = "/{}/p{}.html".format(prefix, page_number - 1)

    nextLink = '#'
    if page_number + 1 <= page_count:
        nextLink = "/{}/p{}.html".format(prefix, page_number + 1)

    return """
        <hr>
        <div class="paginator">
            <a href="{}" class="paginate previous">前(Previous)</a>
            <a href="{}" class="paginate older">后(Next)</span>
        </div>
    """.format(previous, nextLink)


# to enable this in production, pass 'pages_dir' argument explicitly!
generate()
