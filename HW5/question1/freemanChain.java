import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.Point;
import java.util.List;

class freemanChain{
    int length;
    int[] code;
    Point[] point;
    
    public int[] getChaincode(){
        return code;
    }
    
    public freemanChain(Point[] point){
        this.point = point;
        this.length = point.length;
        code = new int[length];
        absoluteChain();       
    }
    
    public void absoluteChain(){
        int x;
        int y;        
        for(int i = 0; i <length; i++){
            if(i!=length-1){
                x = point[i+1].x - point[i].x;
                y = point[i+1].y - point[i].y;
                code[i] = generatecode(x,y);
            }
            else{
                x = point[0].x - point[i].x;
                y = point[0].y - point[i].y;
                code[i] = generatecode(x,y);
            }
        }
    }
        
    public int generatecode(int x,int y){
        if(x==1&&y==0) return 0;
        else if(x==1&&y==1) return 1;
        else if(x==0&&y==1) return 2;
        else if(x==-1&&y==1) return 3;
        else if(x==-1&&y==0) return 4;
        else if(x==-1&&y==-1) return 5;
        else if(x==0&&y==-1) return 6;
        else if(x==1&&y==-1) return 7;
        else return -1;
    }
}