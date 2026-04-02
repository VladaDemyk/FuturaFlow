package dto;
public class FileUploadResponse {
    private String url;
    private String message;

    public FileUploadResponse(String url, String message) {
        this.url = url;
        this.message = message;
    }

    // Гетери (Getters) - обов'язкові для того, щоб Spring міг перетворити це в JSON
    public String getUrl() {
        return url;
    }

    public String getMessage() {
        return message;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}