package com.progress.backend.services.file;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.progress.backend.connections.DbInitBean;
import com.progress.backend.entities.FileEntity;
import com.progress.backend.utils.CommonUtils;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

//~--- JDK imports ------------------------------------------------------------
import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;

import java.math.BigInteger;

import java.security.SecureRandom;

import javax.imageio.ImageIO;

/**
 *
 * @author Armen Arzumanyan
 */
@Service("fileService")
@Component
public class FileService extends DbInitBean implements Serializable {

    private static final Logger log = Logger.getLogger(FileService.class);
    private static final long serialVersionUID = 1L;
    private final SecureRandom random = new SecureRandom();
    @Autowired
    private DbInitBean initDatabase;
    private GridFS gfsPhoto;

    public FileService() {
        log.info("Backend: STARTED FileService");
    }

    private String getFileName() {
        return new BigInteger(60, random).toString(16);
    }

    public String addFile(FileEntity file) {
        String fileName = file.getTitle() + "-" + this.getFileName();
        try {
            gfsPhoto = new GridFS(initDatabase.getDatabase(), "filestorage");

            InputStream in = new ByteArrayInputStream(file.getContent());
            BufferedImage originalImage = ImageIO.read(in);

            int width = 400;
            int height = 400;

            BufferedImage thumbnail = Thumbnails.of(originalImage).size(height, width).asBufferedImage();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ImageIO.write(thumbnail, "jpg", baos);
            baos.flush();

            byte[] imageInByte = baos.toByteArray();

            baos.close();

            GridFSInputFile gfsFile = gfsPhoto.createFile(imageInByte);
            Long fileId = (CommonUtils.longValue(getNextId(initDatabase.getDatabase(), "filesGenSeq")));

            gfsFile.setId(fileId);
            gfsFile.setContentType(file.getMimetype());
            gfsFile.setFilename(fileName);
            gfsFile.save();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileName;
    }

    public GridFSDBFile getFile(String fileName) {
        try {
            gfsPhoto = new GridFS(getDatabase(), "younetdb");
        } catch (Exception e) {
            e.printStackTrace();
        }
        GridFSDBFile imageForOutput = gfsPhoto.findOne(fileName);
        return imageForOutput;
    }

    public void removeFileByFileName(String imageName) {
        if (imageName == null) {
            return;
        }
        BasicDBObject document = new BasicDBObject();
        document.put("filename", imageName);
        initDatabase.getFileCollection().remove(document);
    }

    public void removeFile(Long fileId) {
        BasicDBObject document = new BasicDBObject();
        document.put("id", fileId);
        initDatabase.getFileCollection().remove(document);
    }

}
