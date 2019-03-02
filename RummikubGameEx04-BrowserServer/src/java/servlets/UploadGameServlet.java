package servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import manager.ProxyManager;
import rummikub.ws.DuplicateGameName_Exception;
import rummikub.ws.InvalidParameters_Exception;
import rummikub.ws.InvalidXML_Exception;

import utils.ServletUtils;

@WebServlet(name = "UploadGameServlet", urlPatterns = {"/uploadGame"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class UploadGameServlet extends HttpServlet {

    String error = null;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (error != null)
            response.getWriter().write(error);
        error = null;

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Collection<Part> parts = request.getParts();
        error = null;
        StringBuilder fileContent = new StringBuilder();
        ProxyManager manager = ServletUtils.getGameManager();

        for (Part part : parts) {
            try {
                fileContent.append(readFromInputStream(part.getInputStream()));
            } catch (Exception ex) {
            }
        }
        String gameName = "";
        try {
            gameName = manager.loadGameFromXmlFile(fileContent);
        } catch (DuplicateGameName_Exception | InvalidParameters_Exception | InvalidXML_Exception ex) {
            error = ex.getMessage();
        }

        if (error == null) {
            response.sendRedirect("chooseNameFromXml.html");
            getServletContext().setAttribute("gameName", gameName);
        } else {
            getServletContext().setAttribute("error", error);
            response.sendRedirect("errorPage.html");
        }
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}