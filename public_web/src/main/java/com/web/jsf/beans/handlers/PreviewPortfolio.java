package com.web.jsf.beans.handlers;

//~--- non-JDK imports --------------------------------------------------------



import com.mongodb.gridfs.GridFSDBFile;
import com.progress.backend.services.file.FileService;
import com.progress.backend.services.file.PortfolioFileService;


import org.apache.commons.io.IOUtils;

//~--- JDK imports ------------------------------------------------------------

import java.io.*;

import javax.faces.context.FacesContext;

import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Armen Arzumanyan
 */
@WebServlet(urlPatterns = { "/PreviewPortfolio" })
public class PreviewPortfolio extends HttpServlet implements SingleThreadModel {
    private static final long serialVersionUID = -6624464650990859671L;
    private PortfolioFileService       fileAction       = new PortfolioFileService();

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPreviewImage(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {}

    private void doPreviewImage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        FacesContext context   = FacesContext.getCurrentInstance();
        String       fileIdStr = request.getParameter("fileId");
        String       widthStr  = request.getParameter("w");
        GridFSDBFile file      = null;
        int          width     = 0;

        if ((widthStr != null) && (widthStr.length() > 0)) {
            try {
                width = Integer.parseInt(widthStr);
            } catch (NumberFormatException e) {}
        }

        if (fileIdStr != null) {
            if (fileAction != null) {
                file = fileAction.getFile(fileIdStr.trim());
            }

            if (file != null) {
                byte[] content = IOUtils.toByteArray(file.getInputStream());

                if (content != null) {
                    String mimeType = file.getContentType();

                    response.addHeader("Pragma", "cache");
                    response.addHeader("Cache-Control", "max-age=3600, must-revalidate");
                    response.addDateHeader("Expires", System.currentTimeMillis() + 1000 * 3600 * 10);
                    response.setContentType(mimeType);

                    try {
                        if (((mimeType != null)
                                && (mimeType.equalsIgnoreCase("image/gif") || mimeType.equalsIgnoreCase("image/x-png")
                                    || mimeType.equalsIgnoreCase("image/png") || mimeType.equalsIgnoreCase("image/jpg")
                                    || mimeType.equalsIgnoreCase("image/jpeg"))) || (width == 0)) {
                            response.getOutputStream().write(content);
                        } else {

//                          ByteArrayInputStream bi = new ByteArrayInputStream(content);
//                          InputStream thumbStream = scaleImageJPG(bi, width);
//                          byte[] thumbContent = new byte[thumbStream.available()];
//                          thumbStream.read(thumbContent);
                            response.getOutputStream().write(content);
                        }
                    } catch (IOException e) {

                        // log.error("file content send error");
                        e.printStackTrace();
                    } catch (Exception e) {

                        // log.error("file exception: " + e);
                        e.printStackTrace();
                    } finally {
                        content = null;
                        file    = null;
                    }

                    return;
                }
            } else {

                // TODO add page not found
                response.addHeader("Pragma", "no-cache");
                response.addDateHeader("Expires", System.currentTimeMillis() - 1000 * 3600);

                try {
                    response.getWriter().println("file object is null");
                } catch (Exception e) {}

                return;
            }
        }

        // TODO add page not found
        response.addHeader("Pragma", "no-cache");
        response.addDateHeader("Expires", System.currentTimeMillis() - 1000 * 3600);

        try {
            response.getWriter().println("file id is not set");
        } catch (Exception e) {}

        // log.debug("file ID parameter is not set or file is not found");
        return;
    }

    // </editor-fold>
}


//~ Formatted by Jindent --- http://www.jindent.com
