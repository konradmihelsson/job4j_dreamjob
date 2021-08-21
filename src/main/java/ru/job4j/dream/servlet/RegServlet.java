package ru.job4j.dream.servlet;

import ru.job4j.dream.model.User;
import ru.job4j.dream.store.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RegServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        User user = PsqlStore.instOf().findUserByName(req.getParameter("name"));
        User userWithSameEmail = PsqlStore.instOf().findUserByEmail(req.getParameter("email"));
        if (user != null) {
            req.setAttribute("error", "Такой пользователь уже существует! Укажите другое имя.");
            req.getRequestDispatcher("reg.jsp").forward(req, resp);
        } else if (userWithSameEmail != null) {
            req.setAttribute("error", "Такой email уже используется! Укажите другой email.");
            req.getRequestDispatcher("reg.jsp").forward(req, resp);
        } else {
            user = new User();
            user.setName(req.getParameter("name"));
            user.setEmail(req.getParameter("email"));
            user.setPassword(req.getParameter("password"));
            PsqlStore.instOf().save(user);
            resp.sendRedirect(req.getContextPath() + "/login.do");
        }
    }
}
