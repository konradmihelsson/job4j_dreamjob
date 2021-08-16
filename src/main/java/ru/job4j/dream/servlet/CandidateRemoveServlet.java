package ru.job4j.dream.servlet;

import ru.job4j.dream.store.PsqlStore;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class CandidateRemoveServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final File folder = new File("c:\\images\\");
        PsqlStore.instOf().removeCandidateById(Integer.parseInt(req.getParameter("id")));
        Arrays.stream(folder.listFiles((dir, name) -> name.matches(req.getParameter("id") + ".*?")))
                .forEach(File::delete);
        resp.sendRedirect(req.getContextPath() + "/candidates.do");
    }
}
