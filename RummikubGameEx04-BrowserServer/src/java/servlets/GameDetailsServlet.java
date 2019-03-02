package servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import manager.ProxyManager;
import rummikub.ws.GameDetails;
import rummikub.ws.GameDoesNotExists_Exception;
import utils.ServletUtils;

@WebServlet(name = "GameDetailsServlet", urlPatterns = {"/GameDetails"})
public class GameDetailsServlet extends HttpServlet {

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

        if (method.equals("getGameDetails")) {   
            addGameDetailsToResponse(response);
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

    private void addGameDetailsToResponse(HttpServletResponse response) {
        ArrayList<String> responseToClient = new ArrayList<String>();
        Gson gson = new Gson();
        boolean gotDetails = false;
        ProxyManager manager = ServletUtils.getGameManager();
        String gameName = (String) getServletContext().getAttribute("gameName");
        try {
            GameDetails gameDetails = manager.getGameDetails(gameName);
            responseToClient.addAll(convertGameDetailsToList(gameDetails));
            gotDetails = true;
        } catch (GameDoesNotExists_Exception ex) {
            gotDetails = false;
        }
        
        try {
            if (gotDetails)
                response.getWriter().write(gson.toJson(responseToClient));
            else
                response.sendRedirect("joinGame.html");
        } catch (IOException ex) {
        }
    }

    private ArrayList<String> convertGameDetailsToList(GameDetails gameDetails) {
        ArrayList<String> details = new ArrayList<String>();

        details.add(gameDetails.getName());
        details.add(gameDetails.getStatus().name());
        details.add(Integer.toString(gameDetails.getHumanPlayers()));
        details.add(Integer.toString(gameDetails.getComputerizedPlayers()));
        details.add(Integer.toString(gameDetails.getJoinedHumanPlayers()));
        details.add(Boolean.toString(gameDetails.isLoadedFromXML()));
        
        return details;
    }

}