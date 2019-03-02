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
import rummikub.ws.GameDoesNotExists_Exception;
import rummikub.ws.InvalidParameters_Exception;
import utils.ServletUtils;

@WebServlet(name = "JoinGameServlet", urlPatterns = {"/joinGame"})
public class JoinGameServlet extends HttpServlet {

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
        Gson gson = new Gson();
        ArrayList<String> responseToClient = new ArrayList<String>();
        String error = null;

        String playerName = request.getParameter("playerName");
        String gameName = request.getParameter("gameName");
        
        String method = request.getParameter("method");
        if (method.equals("displayWaitingGames")) {
            response.sendRedirect("waitingGamesView.html");
            return;
        } else if (method.equals("displayGameDetails") && gameName != null && !gameName.isEmpty()) {
            getServletContext().setAttribute("gameName", gameName);
            responseToClient.add("true");
            responseToClient.add("gameDetails.html");
        } else if (method.equals("back")) {
            response.sendRedirect("index.html");
        } else {
            ProxyManager manager = ServletUtils.getGameManager();

            boolean isPlayerNameValid = true, isGameExist = true;

            try {
                manager.joinGame(gameName, playerName);
            } catch (GameDoesNotExists_Exception ex) {
                isGameExist = false;
                error = ex.getMessage();
            } catch (InvalidParameters_Exception ex) {
                isPlayerNameValid = false;
                error = ex.getMessage();
            }

            if (isPlayerNameValid && isGameExist) {
                responseToClient.add("true");
                getServletContext().setAttribute("gameName", gameName);
                responseToClient.add("waitRoom.html");
            }
            else {
                responseToClient.add("false");
                responseToClient.add(error);
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
