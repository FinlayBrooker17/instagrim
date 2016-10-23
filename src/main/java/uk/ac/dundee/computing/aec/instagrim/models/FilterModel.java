/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.models;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.imgscalr.Scalr;
import static org.imgscalr.Scalr.*;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;

/**
 *
 * @author Finlay
 */
public class FilterModel {
    Cluster cluster;

    public FilterModel() {
        
    }
    
    
    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }
    
    public void addFilter(Pic p,String filter,Color c) {
        try {
            String picid = p.getSUUID();
            String type = p.getType();
            System.out.println(type);
            byte[] b = p.getBytes();
            String types[]=Convertors.SplitFiletype(type);
            Boolean success = (new File("/var/tmp/instagrim/")).mkdirs();
            FileOutputStream output = new FileOutputStream(new File("/var/tmp/instagrim/" + picid));
            output.write(b);
            ////
            byte[] newImage = applyFilter(picid,filter,types[1],c);
            int processedlength=newImage.length;
            ByteBuffer bb = ByteBuffer.wrap(newImage);

            byte []  thumbb = thumbFilter(picid,filter,types[1],c);
            int thumblength= thumbb.length;
            ByteBuffer thum = ByteBuffer.wrap(thumbb);
            ////
            Session session = cluster.connect("instagrim");

            PreparedStatement psInsertPic = session.prepare("insert into pics (picid,thumb,processed,thumblength,processedlength,type) values(?,?,?,?,?,?)");
            BoundStatement bsInsertPic = new BoundStatement(psInsertPic);
            session.execute(bsInsertPic.bind(UUID.fromString(picid),thum,bb,thumblength,processedlength,type));
            session.close();
            ////
            return;
        } catch (IOException et) {
                
        }
        return;
    }
    
    public byte[] applyFilter(String picid,String filter,String type,Color c){
            try {
                BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/" + picid));
                BufferedImage processed = BI;
                System.out.println(filter);
                switch (filter) {
                    case "GREY":
                        processed = grayProcessed(BI);
                        break;
                    case "BRIGHTEN":
                        processed = lighterProcessed(BI);
                        break;
                    case "DARKEN":
                        processed = darkerProcessed(BI);
                        break;
                    case "COLOUR":
                        processed = createProcessed(tint(c,BI));
                        break;
                    default:
                        break;
                }
                
                byte[] imageInByte;
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(processed, type, baos);
                    baos.flush();
                    imageInByte = baos.toByteArray();
                }
                ByteBuffer bb = ByteBuffer.wrap(imageInByte);
                return imageInByte;
            }   catch (IOException ex) {
            Logger.getLogger(FilterModel.class.getName()).log(Level.SEVERE, null, ex);
        }
            return null;
    }
    
    public byte[] thumbFilter(String picid,String filter,String type,Color c) {
        try {
                BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/" + picid));
                BufferedImage processed = BI;
                switch (filter) {
                    case "GREY":
                        processed = grayThumbnail(BI);
                        break;
                    case "BRIGHTEN":
                        processed = lighterThumbnail(BI);
                        break;
                    case "DARKEN":
                        processed = darkerThumbnail(BI);
                        break;
                    case "COLOUR":
                        processed = createThumbnail(tint(c,BI));
                        break;
                    default:
                        break;
                }
                
                byte[] imageInByte;
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(processed, type, baos);
                    baos.flush();
                    imageInByte = baos.toByteArray();
                }
                ByteBuffer bb = ByteBuffer.wrap(imageInByte);
                return imageInByte;
            }   catch (IOException ex) {
            Logger.getLogger(FilterModel.class.getName()).log(Level.SEVERE, null, ex);
        }
            return null;
    }
    
    public static BufferedImage grayProcessed(BufferedImage img) {
        int Width=img.getWidth()-1;
        img = resize(img, Scalr.Method.SPEED, Width, OP_ANTIALIAS, OP_GRAYSCALE);
        return pad(img, 4);
    }
    public static BufferedImage grayThumbnail(BufferedImage img) {
        img = resize(img, Scalr.Method.SPEED, 250, OP_ANTIALIAS, OP_GRAYSCALE);
        // Let's add a little border before we return result.
        return pad(img, 2);
    }
    
    public static BufferedImage lighterProcessed(BufferedImage img) {
        int Width=img.getWidth()-1;
        img = resize(img, Scalr.Method.SPEED, Width, OP_ANTIALIAS, OP_BRIGHTER);
        return pad(img, 4);
    }
    public static BufferedImage lighterThumbnail(BufferedImage img) {
        img = resize(img, Scalr.Method.SPEED, 250, OP_ANTIALIAS, OP_BRIGHTER);
        // Let's add a little border before we return result.
        return pad(img, 2);
    }
    public static BufferedImage darkerProcessed(BufferedImage img) {
        int Width=img.getWidth()-1;
        img = resize(img, Scalr.Method.SPEED, Width, OP_ANTIALIAS, OP_DARKER);
        return pad(img, 4);
    }
    public static BufferedImage darkerThumbnail(BufferedImage img) {
        img = resize(img, Scalr.Method.SPEED, 250, OP_ANTIALIAS, OP_DARKER);
        // Let's add a little border before we return result.
        return pad(img, 2);
    }
    public static BufferedImage createProcessed(BufferedImage img) {
        int Width=img.getWidth()-1;
        img = resize(img, Scalr.Method.SPEED, Width, OP_ANTIALIAS);
        return pad(img, 4);
    }
    public static BufferedImage createThumbnail(BufferedImage img) {
        img = resize(img, Scalr.Method.SPEED, 250, OP_ANTIALIAS);
        // Let's add a little border before we return result.
        return pad(img, 2);
    }
    //// I modified this code from http://stackoverflow.com/questions/4248104/applying-a-tint-to-an-image-in-java
    public BufferedImage tint(Color c, BufferedImage src) {
        float r = c.getRed();
        float g = c.getGreen();
        float b = c.getBlue();
        float a = c.getAlpha();
        //// copying image
        BufferedImage newImage = new BufferedImage(src.getWidth(), src.getHeight(),BufferedImage.TRANSLUCENT);
        Graphics2D graphics = newImage.createGraphics();
        graphics.drawImage(src, 0, 0, null);
        graphics.dispose();

        // tinting image
        for (int i = 0; i < newImage.getWidth(); i++) {
            for (int j = 0; j < newImage.getHeight(); j++) {
                int ax = newImage.getColorModel().getAlpha(newImage.getRaster().getDataElements(i, j, null));
                int rx = newImage.getColorModel().getRed(newImage.getRaster().getDataElements(i, j, null));
                int gx = newImage.getColorModel().getGreen(newImage.getRaster().getDataElements(i, j, null));
                int bx = newImage.getColorModel().getBlue(newImage.getRaster().getDataElements(i, j, null));
                rx *= r;
                gx *= g;
                bx *= b;
                
                newImage.setRGB(i, j, (ax << 24) | (rx << 16) | (gx << 8) | (bx << 0));
            }
        }
        return newImage;
    }
    
    
}
