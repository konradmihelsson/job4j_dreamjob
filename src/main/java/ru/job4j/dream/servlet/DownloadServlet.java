package ru.job4j.dream.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DownloadServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        File downloadFile = null;
        for (File file : new File("c:\\images\\").listFiles()) {
            String fullFileName = file.getName();
            int dotIndex = fullFileName.lastIndexOf(".");
            String fileName = fullFileName.substring(0, dotIndex);
            if (name.equals(fileName)) {
                downloadFile = file;
                break;
            }
        }
        if (downloadFile == null) {
            downloadFile = new File("c:\\images\\default_user_image.png");
        }
        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + downloadFile.getName() + "\"");
        try (FileInputStream stream = new FileInputStream(downloadFile)) {
            resp.getOutputStream().write(stream.readAllBytes());
        }
    }
}
