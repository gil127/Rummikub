package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import dataObject.Action;
import dataObject.ActionType;
import dataObject.Cube;
import dataObject.CubeBoard;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import manager.ProxyManager;
import rummikub.ws.InvalidParameters_Exception;
import utils.ServletUtils;

@WebServlet(name = "GameSessionServlet", urlPatterns = {"/GameSession"})
public class GameSessionServlet extends HttpServlet {

    /*private static final Timer timer = new Timer();
    private TimerTask currentTask;*/
    private ProxyManager manager;

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

        manager = ServletUtils.getGameManager();
        String method = request.getParameter("method");
        
        if (method.equals("getBoard")) {
            String board = getBoard();
            response.getWriter().write(board);
        } else if (method.equals("getHand")){
            String playerHand = getPlayerHand();
            response.getWriter().write(playerHand);
        } else if (method.equals("isPlayerTurn")) {
            String playerTurn = manager.isCurrentPlayerTurn();
            response.getWriter().write(playerTurn);
        } else if (method.equals("getGameOver")) {
            String isGameOver = getGameOver();
            response.getWriter().write(isGameOver);
        } else if (method.equals("finishTurnClicked")) {
            try {
                manager.finishTurn();
            } catch (InvalidParameters_Exception ex) {
                response.getWriter().write(ex.getMessage());
            }
        } else if (method.equals("quitClicked")) {
            try {
                manager.currentPlayerRetired();
            } catch (InvalidParameters_Exception ex) {
                response.getWriter().write(ex.getMessage());
            }
            finally {
                response.sendRedirect("gameOver.html");
            }
        } else if (method.equals("playerDetailsClicked")) {
            response.sendRedirect("playersDetails.html");
        } else if (method.equals("getMsg")) {
            response.getWriter().write(manager.getMsg());
        } else if (method.equals("doAction")) {
            String action = request.getParameter("Action");
            String insertInto = request.getParameter("InsertInto");
            String serialNumber = request.getParameter("SerialNumber");
            String cubes = request.getParameter("Cubes");
            ArrayList<String> responseToClient = new ArrayList<String>();
            Gson gson = new Gson();
            try {
                doAction(action, insertInto, serialNumber, cubes);
                responseToClient.add("true");
                response.getWriter().write(gson.toJson(responseToClient));
            } catch (InvalidParameters_Exception ex) {
                
                responseToClient.add("false");
                responseToClient.add(ex.getMessage());
                response.getWriter().write(gson.toJson(responseToClient));
            }
        } else if (method.equals("IsPlayerCanUseBoard")) {
            response.getWriter().write(Boolean.toString(manager.isPlayerCanUseBoard()));
        }
    }

    private String getGameOver() {
        boolean isGameOver = manager.checkIfGameOver();

        return Boolean.toString(isGameOver);
    }

    private String getBoard() {
        Gson gson = new Gson();
        List<ArrayList<Cube>> cubesInBoard = manager.getBoard();
        
        ArrayList<String> htmlCubes = convertBoardIntoHtmlBoard(cubesInBoard);
        
        return gson.toJson(htmlCubes);
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

    private ArrayList<String> convertBoardIntoHtmlBoard(List<ArrayList<Cube>> board) {
        ArrayList<String> res = new ArrayList<String>();
        int id = 0;
        
        for (ArrayList<Cube> serial : board) {
            res.addAll(convertCubesIntoHtmlCubes(serial, false, id));
            res.add("<br>");
            id += serial.size();
        }
        
        return res;
    }
    
    private ArrayList<String> convertCubesIntoHtmlCubes(List<Cube> serial, boolean isPlayerHand, int idToAdd) {
        ArrayList<String> res = new ArrayList<String>();
        String classType = isPlayerHand? "cube" : "cubeBoard";
        
        if (serial != null) {
            int i = 1 + idToAdd;
            String id;
            for (Cube cube : serial) {
                String path = getPathFromCube(cube);
                id = classType + i;
                String htmlCube = "<input id=\"" + id + "\" class=\"" + classType + "\" type=\"image\" src=\"" + path + "\" name=\"image\" width=\"40\" height=\"55\" alt=\"Submit\"  onclick=\"onCubeClicked(this)\">";
                res.add(htmlCube);
                i++;
            }
        }
        return res;
    }
    
    private String getPathFromCube(Cube cube) {
        String imageExtension = ".png";
        String imageDir = "resources\\images\\Cube's images\\";
        String fileName = null;
        
        if (!cube.isJoker())  {
            imageDir += cube.getColor().name() + "\\";
            fileName = Integer.toString(cube.getValue());
        }
        else {
            imageDir += "Joker\\";
            fileName = "Joker";
        }
        
        return imageDir + fileName + imageExtension;
    }

    private String getPlayerHand() {
        Gson gson = new Gson();
        List<Cube> playerHand = manager.getPlayerCubesFromServer();
        ArrayList<String> htmlCubes = convertCubesIntoHtmlCubes(playerHand, true, 0);
        
        return gson.toJson(htmlCubes);
    }

    private void doAction(String action, String insertInto, String serialNumber, String cubes) throws InvalidParameters_Exception {
        Action currAction = new Action();
        
        setActionType(currAction, action, insertInto);
        currAction.setSerialNumber(Integer.valueOf(serialNumber));
        setCubesForAction(currAction, cubes);
        
        manager.sendActionToLogic(currAction);
    }

    private void setActionType(Action currAction, String action, String insertInto) {
        switch (action) {
            case "create": currAction.setActionType(ActionType.CREATE_SERIAL);
                break;
            case "replace": currAction.setActionType(ActionType.REPLACE_JOKER);
                break;
            case "insert": setInsertType(currAction, insertInto);
                break;
            case "takeBack": currAction.setActionType(ActionType.TAKE_TILE_BACK);
                break;
        }
    }

    private void setInsertType(Action currAction, String insertInto) {
        switch(insertInto) {
            case "Begin": currAction.setActionType(ActionType.ADD_TO_BOTTOM_EDGE);
                break;
            case "End": currAction.setActionType(ActionType.ADD_TO_TOP_EDGE);
                break;
            case "Middle": currAction.setActionType(ActionType.SPLIT);
                break;
        }
    }

    private void setCubesForAction(Action currAction, String cubes) {
        String[] idOfCubes = cubes.split(",");
        
        for (String id : idOfCubes) {
            if (id.contains("cubeBoard")) {
                CubeBoard boardCubeByID = getBoardCubeByID(id.replace("cubeBoard", ""));
                if (boardCubeByID != null)
                    currAction.addBoardCube(boardCubeByID);
            } else {
                Cube cubeByID = getCubeByID(id.replace("cube", ""));
                if (cubeByID != null)
                    currAction.addPlayerCubes(cubeByID);
            }
        }
    }

    private CubeBoard getBoardCubeByID(String id) {
        List<ArrayList<Cube>> board = manager.getBoard();
        int index = 1;
        int integerID = Integer.valueOf(id);
        int serialNumber = 1;
        
        for (ArrayList<Cube> serial : board) {
            for (Cube cube : serial) {
                if (index == integerID) {
                    return new CubeBoard(cube, serialNumber);
                }
                index++;
            }
            serialNumber++;
        }
        
        return null;
    }

    private Cube getCubeByID(String id) {
        List<Cube> playerCubes = manager.getPlayerCubes();
        Cube cubeLookingFor = null;
        
        if (playerCubes != null) {
            int index = 1;
            int integerID = Integer.valueOf(id);
            
            for (Cube cube : playerCubes) {
                if (index == integerID) {
                    cubeLookingFor = cube;
                    break;
                }
                index++;
            }
        }
        return cubeLookingFor;
    }
}