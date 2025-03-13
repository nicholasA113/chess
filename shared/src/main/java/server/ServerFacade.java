package server;

public class ServerFacade {

    public RegisterResult register(RegisterRequest request){
        var path = "/user";
        return this.makeRequest("POST", path, request, RegisterRequest.class);
    }

    public LoginResult login(LoginRequest request){
        var path = "/session";
        return this.makeRequest("POST", path, request, LoginRequest.class);
    }

    public LogoutResult logout(){
        var path = "/session";
        return this.makeRequest("DELETE", path, request, LogoutResult.class);
    }

    private<T> T makeRequest(String method, String path, Object request, Class<T> responseClass){

    }

}
