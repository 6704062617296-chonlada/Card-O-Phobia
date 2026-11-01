
package cards;

import java.awt.image.BufferedImage;

public class CardData {

    private String id;
    private String name;
    private int cost;
    private BufferedImage image;

    public CardData(String id, String name, int cost, BufferedImage image) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.image = image;
    }

    // Getters

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public BufferedImage getImage() {
        return image;
    }

    // Setters

    public void setName(String name) {
        this.name = name;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
