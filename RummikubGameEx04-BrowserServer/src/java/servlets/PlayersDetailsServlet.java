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
import rummikub.ws.GameDetails;
import rummikub.ws.GameDoesNotExists_Exception;
import rummikub.ws.PlayerDetails;
import utils.ServletUtils;

@WebServlet(name = "PlayersDetailsServlet", urlPatterns = {"/PlayersDetails"})
public class PlayersDetailsServlet extends HttpServlet {

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

        if (method.equals("getPlayersDetails")) {   
            addPlayersDetailsToResponse(response);
	}
        else if (method.equals("back")) {
            response.sendRedirect("gameSession.html");
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

    private void addPlayersDetailsToResponse(HttpServletResponse response) {
        ArrayList<String> responseToClient = new ArrayList<String>();
        Gson gson = new Gson();
        boolean gotDetails = false;
        ProxyManager manager = ServletUtils.getGameManager();
        String gameName = (String) getServletContext().getAttribute("gameName");
        try {
            List<PlayerDetails> playersDetails = manager.getPlayersDetails(gameName);
            responseToClient.addAll(convertPlayersDetailsToList(playersDetails));
            gotDetails = true;
        } catch (GameDoesNotExists_Exception ex) {
            gotDetails = false;
        }
        
        try {
            if (gotDetails)
                response.getWriter().write(gson.toJson(responseToClient));
            else
                response.sendRedirect("gameSession.html");
        } catch (IOException ex) {
        }
    }

    private ArrayList<String> convertPlayersDetailsToList(List<PlayerDetails> playersDetails) {
        ArrayList<String> details = new ArrayList<String>();

        for (PlayerDetails player : playersDetails) {
            StringBuilder playerDetails = new StringBuilder();
            playerDetails.append("Name: " + player.getName());
            playerDetails.append(",    Num of tiles: " + player.getNumberOfTiles());
            playerDetails.append(",    Status: " + player.getStatus().name());
            playerDetails.append(",    Type: " + player.getType().value());
            details.add(playerDetails.toString());
        }
        
        return details;
    }
}