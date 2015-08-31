# OS X Screencast to animated GIF

This gist shows how to create a GIF screencast using only free OS X tools: *QuickTime, ffmpeg, and gifsicle*.

![Screencapture GIF](http://dl-web.dropbox.com/u/29440342/screenshots/OBDHSF-KJDFKJS-screencapture.gif)

## Instructions

To capture the video (filesize: 19MB), using the free "QuickTime Player" application:

* Open "Quicktime Player", 
* Go to File -> New Screen Recording
* Selected screen portion by dragging a rectangle, recorded 13 second video. 
* Go to File -> Export -> As Movie
  * Saved the video in **full quality** with the filename `in.mov` 

To convert in.mov into out.gif (filesize: 48KB), open Terminal to the folder with `in.mov` and run the following command:

    ffmpeg -i in.mov -s 600x400 -pix_fmt rgb24 -r 10 -f gif - | gifsicle --optimize=3 --delay=3 > out.gif

Notes on the arguments:

* `-r 10` tells ffmpeg to reduce the frame rate from 25 fps to 10
* `-s 600x400` tells ffmpeg the max-width and max-height
* `--delay=3` tells gifsicle to delay 30ms between each gif
* `--optimize=3` requests that gifsicle use the slowest/most file-size optimization 

To share the new GIF using [Dropbox](http://dropbox.com) and [Copy Public URL](https://github.com/dergachev/copy-public-url), run the following:

    cp out.gif ~/Dropbox/Public/screenshots/Screencast-`date +"%Y.%m.%d-%H.%M"`.gif
  
## Installation

The conversion process requires the following command-line tools:

* **ffmpeg** to process the video file
* **gifsicle** to create and optimize the an animated gif

If you use homebrew and homebrew-cask software packages, just type this in: 

    brew install ffmpeg 
    brew cask install x-quartz #dependency for gifsicle, only required for mountain-lion and above
    open /usr/local/Cellar/x-quartz/2.7.4/XQuartz.pkg # runs the XQuartz installer
    brew install gifsicle


## See also

I ended up rewriting this gist's functionality into [**screengif**](https://github.com/dergachev/screengif), a ruby script with significant quality improvements and a few gratuitous features. Check it out at https://github.com/dergachev/screengif

## Resources

* http://schneems.com/post/41104255619/use-gifs-in-your-pull-request-for-good-not-evil (primary source!)
* http://www.reddit.com/r/programming/comments/16zu7d/use_gifs_in_your_pull_requests_for_good_not_evil/
* http://superuser.com/questions/436056/how-can-i-get-ffmpeg-to-convert-a-mov-to-a-gif#_=_
* http://gnuski.blogspot.ca/2012/06/creating-animate-gif-with-free-software.html

## Related Ideas

* Extend https://github.com/dergachev/copy-public-url folder action for this use case
  * it would automate the conversion before copying Dropbox public URL
  * assign the folder action to ~/Dropbox/Public/Screenshots/gif
  * consider finding a way to simplify the dependency installation