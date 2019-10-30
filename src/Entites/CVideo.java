package Entites;

//src: https://stackoverflow.com/questions/17401852/open-video-file-with-opencv-java
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class CVideo {

    private String url;
    private String titre;
    private List<int[]> tabRGB = new ArrayList<>();

    public CVideo(String url, List<int[]> tabRGB) {
        this.setUrl(url);
        this.setTabRGB(tabRGB);
    }

    public CVideo(String url, String titre) {
        this.setUrl(url);
        this.setTitre(titre);
    }

    public CVideo(String url) {
        this.setUrl(url);
    }

    public List<int[]> getTabRGB() {
        return tabRGB;
    }

    public void setTabRGB(List<int[]> tabRGB) {
        this.tabRGB = tabRGB;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public BufferedImage Mat2BufferedImage(Mat m) {
        //Method converts a Mat to a Buffered Image
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

    public VideoCapture ouvrirVideo(String url) {
        VideoCapture video = new VideoCapture(url);
        return video;
    }

    public boolean getCommonColourFrame(Mat frame, VideoCapture videoData, CVideo video) {
        if (videoData.read(frame)) {

            BufferedImage image = video.Mat2BufferedImage(frame);
            int height = image.getHeight();
            int width = image.getWidth();

            Map m = new HashMap();
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int rgb = image.getRGB(i, j);
                    int[] rgbArr = getRGBArr(rgb);
                    if (!isGray(rgbArr)) {
                        Integer counter = (Integer) m.get(rgb);
                        if (counter == null) {
                            counter = 0;
                        }
                        counter++;
                        m.put(rgb, counter);
                    }
                }
            }
            if (!m.isEmpty()) {
                String colourHex = getMostCommonColour(m, video);
                System.out.println(colourHex);
            }
            return true;
        }
        return false;
    }

    public static String getMostCommonColour(Map map, CVideo video) {
        LinkedList list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });
        Map.Entry me = (Map.Entry) list.get(list.size() - 1);
        int[] rgb = getRGBArr((Integer) me.getKey());
        video.getTabRGB().add(rgb);
        return "" + rgb[0] + rgb[1] + rgb[2];
    }

    public static int[] getRGBArr(int pixel) {
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        return new int[]{red, green, blue};

    }

    public static boolean isGray(int[] rgbArr) {
        int rgDiff = rgbArr[0] - rgbArr[1];
        int rbDiff = rgbArr[0] - rgbArr[2];
        int tolerance = 10;
        if (rgDiff > tolerance || rgDiff < -tolerance) {
            if (rbDiff > tolerance || rbDiff < -tolerance) {
                return false;
            }
        }
        return true;
    }

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public void processVideo(CVideo video) {
        boolean go = true;
        Mat frame = new Mat();
        VideoCapture videoData = video.ouvrirVideo(video.getUrl());
        while (go) {
            go = video.getCommonColourFrame(frame, videoData, video);
        }
    }

    //Les couleurs principales sont maintenant affichées !!!!!!!!!
}
