package servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import manager.ProxyManager;
import utils.ServletUtils;

@WebServlet(name = "WaitingGamesViewServlet", urlPatterns = {"/WaitingGames"})
public class WaitingGamesViewServlet extends HttpServlet {

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
        
        String method = request.getParameter("method");

        if (method.equals("displayWaitingGames")) {   
            addWaitingGames(response);
	}
        else if (method.equals("back")) {
            response.sendRedirect("joinGame.html");
	}
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
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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

    private void addWaitingGames(HttpServletResponse response) {
        ArrayList<String> responseToClient = new ArrayList<String>();
        Gson gson = new Gson();
        ProxyManager manager = ServletUtils.getGameManager();
        List<String> gameNames = manager.getWaitingGames();
        responseToClient.addAll(gameNames);
        boolean gotWaitingGames = gameNames.size() > 0;
        
        try {
            if (gotWaitingGames)
                response.getWriter().write(gson.toJson(responseToClient));
            else
                response.sendRedirect("joinGame.html");
        } catch (IOException ex) {
        }
    }

}
