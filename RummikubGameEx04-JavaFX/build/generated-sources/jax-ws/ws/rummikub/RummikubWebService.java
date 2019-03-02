
package ws.rummikub;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.6-1b01 
 * Generated source version: 2.2
 * 
 */
@WebService(name = "RummikubWebService", targetNamespace = "http://rummikub.ws/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface RummikubWebService {


    /**
     * 
     * @param eventId
     * @param playerId
     * @return
     *     returns java.util.List<ws.rummikub.Event>
     * @throws InvalidParameters_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getEvents", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.GetEvents")
    @ResponseWrapper(localName = "getEventsResponse", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.GetEventsResponse")
    @Action(input = "http://rummikub.ws/RummikubWebService/getEventsRequest", output = "http://rummikub.ws/RummikubWebService/getEventsResponse", fault = {
        @FaultAction(className = InvalidParameters_Exception.class, value = "http://rummikub.ws/RummikubWebService/getEvents/Fault/InvalidParameters")
    })
    public List<Event> getEvents(
        @WebParam(name = "playerId", targetNamespace = "")
        int playerId,
        @WebParam(name = "eventId", targetNamespace = "")
        int eventId)
        throws InvalidParameters_Exception
    ;

    /**
     * 
     * @param xmlData
     * @return
     *     returns java.lang.String
     * @throws InvalidXML_Exception
     * @throws InvalidParameters_Exception
     * @throws DuplicateGameName_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "createGameFromXML", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.CreateGameFromXML")
    @ResponseWrapper(localName = "createGameFromXMLResponse", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.CreateGameFromXMLResponse")
    @Action(input = "http://rummikub.ws/RummikubWebService/createGameFromXMLRequest", output = "http://rummikub.ws/RummikubWebService/createGameFromXMLResponse", fault = {
        @FaultAction(className = DuplicateGameName_Exception.class, value = "http://rummikub.ws/RummikubWebService/createGameFromXML/Fault/DuplicateGameName"),
        @FaultAction(className = InvalidParameters_Exception.class, value = "http://rummikub.ws/RummikubWebService/createGameFromXML/Fault/InvalidParameters"),
        @FaultAction(className = InvalidXML_Exception.class, value = "http://rummikub.ws/RummikubWebService/createGameFromXML/Fault/InvalidXML")
    })
    public String createGameFromXML(
        @WebParam(name = "xmlData", targetNamespace = "")
        String xmlData)
        throws DuplicateGameName_Exception, InvalidParameters_Exception, InvalidXML_Exception
    ;

    /**
     * 
     * @param gameName
     * @return
     *     returns java.util.List<ws.rummikub.PlayerDetails>
     * @throws GameDoesNotExists_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getPlayersDetails", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.GetPlayersDetails")
    @ResponseWrapper(localName = "getPlayersDetailsResponse", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.GetPlayersDetailsResponse")
    @Action(input = "http://rummikub.ws/RummikubWebService/getPlayersDetailsRequest", output = "http://rummikub.ws/RummikubWebService/getPlayersDetailsResponse", fault = {
        @FaultAction(className = GameDoesNotExists_Exception.class, value = "http://rummikub.ws/RummikubWebService/getPlayersDetails/Fault/GameDoesNotExists")
    })
    public List<PlayerDetails> getPlayersDetails(
        @WebParam(name = "gameName", targetNamespace = "")
        String gameName)
        throws GameDoesNotExists_Exception
    ;

    /**
     * 
     * @param humanPlayers
     * @param name
     * @param computerizedPlayers
     * @throws InvalidParameters_Exception
     * @throws DuplicateGameName_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "createGame", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.CreateGame")
    @ResponseWrapper(localName = "createGameResponse", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.CreateGameResponse")
    @Action(input = "http://rummikub.ws/RummikubWebService/createGameRequest", output = "http://rummikub.ws/RummikubWebService/createGameResponse", fault = {
        @FaultAction(className = DuplicateGameName_Exception.class, value = "http://rummikub.ws/RummikubWebService/createGame/Fault/DuplicateGameName"),
        @FaultAction(className = InvalidParameters_Exception.class, value = "http://rummikub.ws/RummikubWebService/createGame/Fault/InvalidParameters")
    })
    public void createGame(
        @WebParam(name = "name", targetNamespace = "")
        String name,
        @WebParam(name = "humanPlayers", targetNamespace = "")
        int humanPlayers,
        @WebParam(name = "computerizedPlayers", targetNamespace = "")
        int computerizedPlayers)
        throws DuplicateGameName_Exception, InvalidParameters_Exception
    ;

    /**
     * 
     * @param gameName
     * @return
     *     returns ws.rummikub.GameDetails
     * @throws GameDoesNotExists_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getGameDetails", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.GetGameDetails")
    @ResponseWrapper(localName = "getGameDetailsResponse", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.GetGameDetailsResponse")
    @Action(input = "http://rummikub.ws/RummikubWebService/getGameDetailsRequest", output = "http://rummikub.ws/RummikubWebService/getGameDetailsResponse", fault = {
        @FaultAction(className = GameDoesNotExists_Exception.class, value = "http://rummikub.ws/RummikubWebService/getGameDetails/Fault/GameDoesNotExists")
    })
    public GameDetails getGameDetails(
        @WebParam(name = "gameName", targetNamespace = "")
        String gameName)
        throws GameDoesNotExists_Exception
    ;

    /**
     * 
     * @return
     *     returns java.util.List<java.lang.String>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getWaitingGames", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.GetWaitingGames")
    @ResponseWrapper(localName = "getWaitingGamesResponse", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.GetWaitingGamesResponse")
    @Action(input = "http://rummikub.ws/RummikubWebService/getWaitingGamesRequest", output = "http://rummikub.ws/RummikubWebService/getWaitingGamesResponse")
    public List<String> getWaitingGames();

    /**
     * 
     * @param gameName
     * @param playerName
     * @return
     *     returns int
     * @throws GameDoesNotExists_Exception
     * @throws InvalidParameters_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "joinGame", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.JoinGame")
    @ResponseWrapper(localName = "joinGameResponse", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.JoinGameResponse")
    @Action(input = "http://rummikub.ws/RummikubWebService/joinGameRequest", output = "http://rummikub.ws/RummikubWebService/joinGameResponse", fault = {
        @FaultAction(className = GameDoesNotExists_Exception.class, value = "http://rummikub.ws/RummikubWebService/joinGame/Fault/GameDoesNotExists"),
        @FaultAction(className = InvalidParameters_Exception.class, value = "http://rummikub.ws/RummikubWebService/joinGame/Fault/InvalidParameters")
    })
    public int joinGame(
        @WebParam(name = "gameName", targetNamespace = "")
        String gameName,
        @WebParam(name = "playerName", targetNamespace = "")
        String playerName)
        throws GameDoesNotExists_Exception, InvalidParameters_Exception
    ;

    /**
     * 
     * @param playerId
     * @return
     *     returns ws.rummikub.PlayerDetails
     * @throws GameDoesNotExists_Exception
     * @throws InvalidParameters_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getPlayerDetails", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.GetPlayerDetails")
    @ResponseWrapper(localName = "getPlayerDetailsResponse", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.GetPlayerDetailsResponse")
    @Action(input = "http://rummikub.ws/RummikubWebService/getPlayerDetailsRequest", output = "http://rummikub.ws/RummikubWebService/getPlayerDetailsResponse", fault = {
        @FaultAction(className = GameDoesNotExists_Exception.class, value = "http://rummikub.ws/RummikubWebService/getPlayerDetails/Fault/GameDoesNotExists"),
        @FaultAction(className = InvalidParameters_Exception.class, value = "http://rummikub.ws/RummikubWebService/getPlayerDetails/Fault/InvalidParameters")
    })
    public PlayerDetails getPlayerDetails(
        @WebParam(name = "playerId", targetNamespace = "")
        int playerId)
        throws GameDoesNotExists_Exception, InvalidParameters_Exception
    ;

    /**
     * 
     * @param tiles
     * @param playerId
     * @throws InvalidParameters_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "createSequence", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.CreateSequence")
    @ResponseWrapper(localName = "createSequenceResponse", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.CreateSequenceResponse")
    @Action(input = "http://rummikub.ws/RummikubWebService/createSequenceRequest", output = "http://rummikub.ws/RummikubWebService/createSequenceResponse", fault = {
        @FaultAction(className = InvalidParameters_Exception.class, value = "http://rummikub.ws/RummikubWebService/createSequence/Fault/InvalidParameters")
    })
    public void createSequence(
        @WebParam(name = "playerId", targetNamespace = "")
        int playerId,
        @WebParam(name = "tiles", targetNamespace = "")
        List<Tile> tiles)
        throws InvalidParameters_Exception
    ;

    /**
     * 
     * @param sequenceIndex
     * @param sequencePosition
     * @param tile
     * @param playerId
     * @throws InvalidParameters_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "addTile", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.AddTile")
    @ResponseWrapper(localName = "addTileResponse", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.AddTileResponse")
    @Action(input = "http://rummikub.ws/RummikubWebService/addTileRequest", output = "http://rummikub.ws/RummikubWebService/addTileResponse", fault = {
        @FaultAction(className = InvalidParameters_Exception.class, value = "http://rummikub.ws/RummikubWebService/addTile/Fault/InvalidParameters")
    })
    public void addTile(
        @WebParam(name = "playerId", targetNamespace = "")
        int playerId,
        @WebParam(name = "tile", targetNamespace = "")
        Tile tile,
        @WebParam(name = "sequenceIndex", targetNamespace = "")
        int sequenceIndex,
        @WebParam(name = "sequencePosition", targetNamespace = "")
        int sequencePosition)
        throws InvalidParameters_Exception
    ;

    /**
     * 
     * @param sequenceIndex
     * @param sequencePosition
     * @param playerId
     * @throws InvalidParameters_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "takeBackTile", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.TakeBackTile")
    @ResponseWrapper(localName = "takeBackTileResponse", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.TakeBackTileResponse")
    @Action(input = "http://rummikub.ws/RummikubWebService/takeBackTileRequest", output = "http://rummikub.ws/RummikubWebService/takeBackTileResponse", fault = {
        @FaultAction(className = InvalidParameters_Exception.class, value = "http://rummikub.ws/RummikubWebService/takeBackTile/Fault/InvalidParameters")
    })
    public void takeBackTile(
        @WebParam(name = "playerId", targetNamespace = "")
        int playerId,
        @WebParam(name = "sequenceIndex", targetNamespace = "")
        int sequenceIndex,
        @WebParam(name = "sequencePosition", targetNamespace = "")
        int sequencePosition)
        throws InvalidParameters_Exception
    ;

    /**
     * 
     * @param targetSequenceIndex
     * @param sourceSequencePosition
     * @param sourceSequenceIndex
     * @param targetSequencePosition
     * @param playerId
     * @throws InvalidParameters_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "moveTile", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.MoveTile")
    @ResponseWrapper(localName = "moveTileResponse", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.MoveTileResponse")
    @Action(input = "http://rummikub.ws/RummikubWebService/moveTileRequest", output = "http://rummikub.ws/RummikubWebService/moveTileResponse", fault = {
        @FaultAction(className = InvalidParameters_Exception.class, value = "http://rummikub.ws/RummikubWebService/moveTile/Fault/InvalidParameters")
    })
    public void moveTile(
        @WebParam(name = "playerId", targetNamespace = "")
        int playerId,
        @WebParam(name = "sourceSequenceIndex", targetNamespace = "")
        int sourceSequenceIndex,
        @WebParam(name = "sourceSequencePosition", targetNamespace = "")
        int sourceSequencePosition,
        @WebParam(name = "targetSequenceIndex", targetNamespace = "")
        int targetSequenceIndex,
        @WebParam(name = "targetSequencePosition", targetNamespace = "")
        int targetSequencePosition)
        throws InvalidParameters_Exception
    ;

    /**
     * 
     * @param playerId
     * @throws InvalidParameters_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "finishTurn", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.FinishTurn")
    @ResponseWrapper(localName = "finishTurnResponse", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.FinishTurnResponse")
    @Action(input = "http://rummikub.ws/RummikubWebService/finishTurnRequest", output = "http://rummikub.ws/RummikubWebService/finishTurnResponse", fault = {
        @FaultAction(className = InvalidParameters_Exception.class, value = "http://rummikub.ws/RummikubWebService/finishTurn/Fault/InvalidParameters")
    })
    public void finishTurn(
        @WebParam(name = "playerId", targetNamespace = "")
        int playerId)
        throws InvalidParameters_Exception
    ;

    /**
     * 
     * @param playerId
     * @throws InvalidParameters_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "resign", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.Resign")
    @ResponseWrapper(localName = "resignResponse", targetNamespace = "http://rummikub.ws/", className = "ws.rummikub.ResignResponse")
    @Action(input = "http://rummikub.ws/RummikubWebService/resignRequest", output = "http://rummikub.ws/RummikubWebService/resignResponse", fault = {
        @FaultAction(className = InvalidParameters_Exception.class, value = "http://rummikub.ws/RummikubWebService/resign/Fault/InvalidParameters")
    })
    public void resign(
        @WebParam(name = "playerId", targetNamespace = "")
        int playerId)
        throws InvalidParameters_Exception
    ;

}
