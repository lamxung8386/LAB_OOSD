package controller;

import dao.TaiKhoanDAO;
import model.TaiKhoan;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/DangNhapServlet")
public class DangNhapServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String user = request.getParameter("username");
        String pass = request.getParameter("password");

        TaiKhoanDAO dao = new TaiKhoanDAO();
        TaiKhoan tk = dao.checkLogin(user, pass);

        if (tk != null) {
            // Luu tai khoan vao Session khi dang nhap thanh cong
            HttpSession session = request.getSession();
            session.setAttribute("acc", tk);
            response.sendRedirect("dangKyDoan.jsp");
        } else {
            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
            request.getRequestDispatcher("dangNhap.jsp").forward(request, response);
        }
    }
}