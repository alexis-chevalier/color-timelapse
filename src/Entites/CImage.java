package Entites;

//src: https://stackoverflow.com/questions/15297504/draw-image-pixel-by-pixel-in-java
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

public class CImage {

    private String url;
    private String titre;
    private int hauteur;
    
    public CImage(String url, String titre, int hauteur) {
        this.setUrl(url);
        this.setTitre(titre);
        this.setHauteur(hauteur);
    }

    public CImage(String url, String titre) {
        this.setUrl(url);
        this.setTitre("image");
        this.setHauteur(1080);
    }

    public CImage(String url) {
        this.setUrl(url);
        this.setTitre("image");
        this.setHauteur(1080);
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getHauteur() {
        return hauteur;
    }

    public void setHauteur(int hauteur) {
        this.hauteur = hauteur;
    }
    
    public void processImage(List<int[]> tabRGB, int hauteur, String cheminFichierFinal,String nomFichierFinal){
        int width = tabRGB.size();
        int height = hauteur;
        //create buffered image object img
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        //file object
        File f = null;
        //create random image pixel by pixel
        for (int x = 0; x < width; x++) {
            int a = 255;
            int r = tabRGB.get(x)[0];
            int g = tabRGB.get(x)[1];
            int b = tabRGB.get(x)[2];

            int p = (a << 24) | (r << 16) | (g << 8) | b; //pixel

            img.setRGB(x, 0, p);
            for (int y = 0; y < height; y++) {
                img.setRGB(x, y, p);
            }
        }
        //write image
        try {
            f = new File(cheminFichierFinal+nomFichierFinal);
            ImageIO.write(img, "png", f);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

}
