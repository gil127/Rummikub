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
import rummikub.ws.GameDoesNotExists_Exception;
import rummikub.ws.InvalidParameters_Exception;
import utils.ServletUtils;

@WebServlet(name = "ChooseNamesServlet", urlPatterns = {"/chooseNames"})
public class ChooseNamesServlet extends HttpServlet {

    private ArrayList<String> playersNames = new ArrayList<String>();
    private ProxyManager manager;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        manager = ServletUtils.getGameManager();
        String method = request.getParameter("method");

        if (method.equals("getNames")) {
            setNamesInResponse(response);
        } else if (method.equals("removeChosenName")) {
            String responseToClient = handleChosenName(request, response);
            response.getWriter().write(responseToClient);
        }
    }

    private String handleChosenName(HttpServletRequest request, HttpServletResponse response) {
        ArrayList<String> responseToClient = new ArrayList<String>();
        Gson gson = new Gson();

        if (checkIfNameExistInList(request) == true) {
            try {
                responseToClient.add("true");
                removeChosenName(request);
                String gameName = (String) getServletContext().getAttribute("gameName");
                String nameOfChosenPlayer = request.getParameter("namesOfPlayer");
                manager.joinGame(gameName, nameOfChosenPlayer);
                responseToClient.add("waitRoom.html");
            } catch (GameDoesNotExists_Exception | InvalidParameters_Exception ex) {
            }
        } else {
            responseToClient.add("false");
            setNamesInResponse(response);
            responseToClient.add("The name already choosen, please try another name");
        }

        return gson.toJson(responseToClient);
    }

    private String getNames() {
        Gson gson = new Gson();

        setPlayersNames();

        return gson.toJson(playersNames);
    }

    private void removeChosenName(HttpServletRequest request) {
        String nameOfChosenPlayer = request.getParameter("namesOfPlayer");

        for (String playersName : playersNames) {
            if (playersName.equals(nameOfChosenPlayer)) {
                playersNames.remove(playersName);
                break;
            }
        }
        request.getSession(true).setAttribute("playerName", nameOfChosenPlayer);
    }

    private boolean checkIfNameExistInList(HttpServletRequest request) {
        String nameOfChosenPlayer = request.getParameter("namesOfPlayer");
        boolean isNameExistInList = false;

        setPlayersNames();
        
        for (String playersName : playersNames) {
            if (playersName.equals(nameOfChosenPlayer)) {
                isNameExistInList = true;
                break;
            }
        }

        return isNameExistInList;
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

    private void setPlayersNames() {
        if (playersNames.isEmpty() == true) {
            String gameName = (String) getServletContext().getAttribute("gameName");
            try {
                playersNames = manager.getWaitingHumanPlayersNames(gameName);
            } catch (GameDoesNotExists_Exception ex) {
            }
        }
    }

    private void setNamesInResponse(HttpServletResponse response) {
        try {
            String namesOfPlayers = getNames();
            response.getWriter().write(namesOfPlayers);
        } catch (IOException ex) {
        }
    }

}
