package pl.szkolenie.projekty.animacja;

public abstract class OnResponseFromServer{


    public abstract void Response(String html, boolean success, Exception exIfError);
}