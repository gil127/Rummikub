package servlets;

import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import manager.ProxyManager;
import rummikub.ws.DuplicateGameName_Exception;
import rummikub.ws.InvalidParameters_Exception;
import utils.ServletUtils;

@WebServlet(name = "CreateNewGameServlet", urlPatterns = {"/CreateNewGame"})
public class CreateNewGameServlet extends HttpServlet {

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
        ArrayList<String> responseToClient = new ArrayList<String>();
        Gson gson = new Gson();

        int numOfPlayers = Integer.parseInt(request.getParameter("numOfPlayers"));
        String nameOfGame = request.getParameter("gameName");
        int numOfHumanPlayers = Integer.parseInt(request.getParameter("numOfHumanPlayers"));

        ProxyManager manager = ServletUtils.getGameManager();

        if (nameOfGame.isEmpty()) {
            responseToClient.add("true");
            responseToClient.add("Please insert a name for the game");
        } else {
            try {
                manager.createGame(nameOfGame, numOfHumanPlayers, numOfPlayers - numOfHumanPlayers);
                responseToClient.add("false");
                responseToClient.add("joinGame.html");
            } catch (DuplicateGameName_Exception | InvalidParameters_Exception ex) {
                responseToClient.add("true");
                responseToClient.add(ex.getMessage());
            }
        }

        response.getWriter().write(gson.toJson(responseToClient));
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

}
