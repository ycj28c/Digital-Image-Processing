HW5

Chengjiao Yang 							04/23/2014

1. Burger & Burge Exercise 11.5 (page 236) (25 points): Implement a Java class for describing a binary image region using chain codes. It is up to you, whether you want to use an absolute or differential chain code. The implementation should be able to encode closed contours as chain codes and also reconstruct the contours given a chain code. Call your Java class Chain_codes
How to use: 
1) copy "imagingbook.jar" to ...\ImageJ\plugins\jar
2) copy files in question1 to the ...\ImageJ\plugins
3) use imagej, drag a picture(I use spine.jpg), then run the ChainCode.java
 
The chain code will list like this, each line is a region.

2. Burger & Burge Exercise 11.12 (page 237) (25 points): Write an ImageJ plugin that (a) finds (labels) all regions in a binary image (b) computes the orientation and eccentricity for each region, and (c) shows the results as a direction vector and the equivalent ellipse on top of each region (as exemplified in Fig 11.19. Hint: Use Eqn. (11.33) to develop a method for drawing ellipses at arbitrary orientations (not available in ImageJ). Call your ImageJ plugin Region_labeling.java
How to use:
1) copy "imagingbook.jar" to ...\ImageJ\plugins\jar
2) copy files in question2 to the ...\ImageJ\plugins 
3) use imagej, drag a picture(I use spine.jpg), then run the Region_labeling.java
