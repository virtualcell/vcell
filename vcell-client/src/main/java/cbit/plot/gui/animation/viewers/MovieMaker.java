/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cbit.plot.gui.animation.viewers;


import org.monte.media.*;
// import org.monte.media.VideoFormatKeys.*;
import org.monte.media.math.Rational;
// import org.monte.media.avi.*;

import java.awt.image.*;
import java.io.*;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
public class MovieMaker {

    public static void makeAVI(File file, BufferedImage [] frames, int fps)
        throws IOException {
        MovieWriter out = Registry.getInstance().getWriter(file);
        
        Format format = new Format(VideoFormatKeys.MediaTypeKey, VideoFormatKeys.MediaType.VIDEO, //
                VideoFormatKeys.EncodingKey, VideoFormatKeys.ENCODING_AVI_PNG,
                VideoFormatKeys.FrameRateKey, new Rational(fps, 1),//
                VideoFormatKeys.WidthKey, frames[0].getWidth(), //
                VideoFormatKeys.HeightKey, frames[0].getHeight(),//
                VideoFormatKeys.DepthKey, 24
                );
        
        int track = out.addTrack(format);
        
        Buffer buf = new Buffer();
        
        buf.format = new Format(VideoFormatKeys.DataClassKey, BufferedImage.class);
        buf.sampleDuration = format.get(VideoFormatKeys.FrameRateKey).inverse();
        for (BufferedImage frame : frames) {
            buf.data = frame;
            out.write(track, buf);
        }
       
        out.close();
        
      
    }
    
    
    public static void makeQuicktime(File file, BufferedImage [] frames, int fps)
        throws IOException {
        MovieWriter out = Registry.getInstance().getWriter(file);
        
        Format format = new Format(VideoFormatKeys.MediaTypeKey, VideoFormatKeys.MediaType.VIDEO, //
                VideoFormatKeys.EncodingKey, VideoFormatKeys.ENCODING_QUICKTIME_PNG,
                VideoFormatKeys.FrameRateKey, new Rational(fps, 1),//
                VideoFormatKeys.WidthKey, frames[0].getWidth(), //
                VideoFormatKeys.HeightKey, frames[0].getHeight(),//
                VideoFormatKeys.DepthKey, 24
                );
        
        int track = out.addTrack(format);
        

        Buffer buf = new Buffer();
        
        buf.format = new Format(VideoFormatKeys.DataClassKey, BufferedImage.class);
        buf.sampleDuration = format.get(VideoFormatKeys.FrameRateKey).inverse();
        for (BufferedImage frame : frames) {
            buf.data = frame;
            out.write(track, buf);
        }
       
        out.close();
      
    }
    
    public static void makeAnimagedGIF(File file, BufferedImage [] frames, int fps)
        throws IOException {
        
        try (ImageOutputStream output = new FileImageOutputStream(file)) {
            GifSequenceWriter gsw = new GifSequenceWriter(output,frames[0].getType(),fps*1000,false);

            for (BufferedImage bi1 : frames) {
                gsw.writeToSequence(bi1);
            }

            gsw.close();
        } catch(FileNotFoundException fne){
            fne.printStackTrace(System.out);
        } catch(IOException ioe){
            ioe.printStackTrace(System.out);
        }
    }
    
}
