package simrat.cube26.model;

/**
 * Created by simrat on 12/3/16.
 */
public class PaymentGateway {
    private String name;
    private String description;
    private String rating;
    private String branding;
    private String trans_fee;
    private String setup_fee;
    private String currencies;
    private String how_to_url;
    private int likes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getCurrencies() {
        return currencies;
    }

    public void setCurrencies(String currencies) {
        this.currencies = currencies;
    }

    public String getHow_to_url() {
        return how_to_url;
    }

    public void setHow_to_url(String how_to_url) {
        this.how_to_url = how_to_url;
    }

    public String getSetup_fee() {
        return setup_fee;
    }

    public void setSetup_fee(String setup_fee) {
        this.setup_fee = setup_fee;
    }

    public String getTrans_fee() {
        return trans_fee;
    }

    public void setTrans_fee(String trans_fee) {
        this.trans_fee = trans_fee;
    }

    public String getBranding() {
        return branding;
    }

    public void setBranding(String branding) {
        this.branding = branding;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
