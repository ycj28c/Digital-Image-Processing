CS545 HW4 
Chengjiao Yang		04/09/2014

1. Burger & Burge Exercise 9.4 (page 171) (30 points): Implement as an ImageJ plugin, called Circular_Hough, the Hough Transform for finding circles and circular arcs with varying radii. Make use of a fast algorithm for generating circles, such as described in sec 9.4, in the accumulator array Find a suitable image to test your algorithm on the Internet and include at least one image with your submission. Also explain briefly how your algorithm works in your Readme file.

My way is the same as book, use brute force way, first use CreateAccumulatorSpace() function to find all the accumulate space, then for every pixel in the image, use fillHoughAccumulator() function to calculate if this pixel match the (u - x)^+(v - y)^ = p^. If matched, then add one to this houghArray(x,y,p). I didn't print out the result with image format, so you can't see it, instead of printing image, I print the log of all the data in houghArray array. 
