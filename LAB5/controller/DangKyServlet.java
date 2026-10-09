package controller;

import dao.TaiKhoanDAO;
import model.TaiKhoan;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/DangKyServlet")
public class DangKyServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        String user = request.getParameter("username");
        String pass = request.getParameter("password");
        String hoTen = request.getParameter("hoTen");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        TaiKhoan tk = new TaiKhoan(user, pass, hoTen, email, phone, "KhachHang");
        TaiKhoanDAO dao = new TaiKhoanDAO();

        if (dao.register(tk)) {
            request.setAttribute("message", "Đăng ký thành công! Vui lòng đăng nhập.");
            request.getRequestDispatcher("dangNhap.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Tên đăng nhập đã tồn tại hoặc có lỗi!");
            request.getRequestDispatcher("dangKy.jsp").forward(request, response);
        }
    }
}