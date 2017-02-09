Readme
chengjiao yang
04/15/2014

# Burger & Burge Exercise 5.7 (page 85) (25 points): 
A. I use the pictures images.jpg, spine.jpg, rumway.jpg in picture folder to test.
B. I use the jar file "imagingbook.jar", so before run the code, must copy "/jar/imagingbook.jar" to "...\ImageJ\plugins\jars".
C. Drag several images into imagej, then run the code, choose the target image, others will be the reference image.
D. When images are almost the same, the effect is better, otherwise ,the pictures effect are totally different.

# Laplacian Filter (19 points):
A. I use spine.jpg to test, use the c value -4 look good.
B. too high or too low are both not good effect, there will rune the image structure and make bad looking.

# Restoration of degraded images (24 points):
A. In restore_img_a.java, its salt and pepper noise,I use median3X3 filter to restore.
B. In restore_img_d.java ,Its blur image, I use matrix -1 -1 -1 -1 12 -1 -1 -1 -1 to do the restore.

# Spatial Filtering (32 points):
I use pronghorn.tif to test.
A. I use w = -1, because I¡¯ = (1+w)*I-I*G equal to I¡¯ = I*G at w = -1, that is Gaussian_blur.
B. I use w = 1, because I¡¯ = I+w*(I-I~). The w*(i-i~) is the mask at w = 1. Use w =1 make pronghorn sharpen enough.
C. No, I can't, if we do blur first, the edge become less sensitive to the sharpen function.
D. There are bright and dark shadow like things at the edge, make the image looks more real, because the plugin do the x and y sharpen to the object bring out some effect of the image.

