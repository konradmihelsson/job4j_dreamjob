package ru.job4j.dream.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class UploadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletContext servletContext = this.getServletConfig().getServletContext();
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            List<FileItem> items = upload.parseRequest(req);
            final File folder = new File("c:\\images\\");
            if (!folder.exists()) {
                folder.mkdir();
            }
            Arrays.stream(folder.listFiles((dir, name) -> name.matches(req.getParameter("id") + ".*?")))
                    .forEach(File::delete);
            for (FileItem item : items) {
                if (!item.isFormField()) {
                    if (item.getSize() != 0) {
                        String fullFileItemName = item.getName();
                        int dotIndex = fullFileItemName.lastIndexOf(".");
                        String fileNameExtension = fullFileItemName.substring(dotIndex);

                        File file = new File(folder + File.separator
                                + req.getParameter("id") + fileNameExtension);
                        try (FileOutputStream out = new FileOutputStream(file)) {
                            out.write(item.getInputStream().readAllBytes());
                        }
                    }
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        resp.sendRedirect(req.getContextPath() + "/candidates.do");
    }
}
