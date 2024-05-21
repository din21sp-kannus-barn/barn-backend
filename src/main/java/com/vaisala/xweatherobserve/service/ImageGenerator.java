// /***
//  * 
//  * This is to be deleted. the purpose was to study server-side image rendering.
//  */


// package com.vaisala.xweatherobserve.service;

// import org.springframework.core.io.ClassPathResource;
// import org.springframework.stereotype.Service;
// import com.vaisala.xweatherobserve.model.dto.WeatherSnapshot;
// import java.awt.*;
// import java.awt.image.BufferedImage;
// import java.io.IOException;

// import javax.imageio.ImageIO;

// @Service
// public class ImageGenerator {

//     public BufferedImage generateImage(WeatherSnapshot snapshot) {
//         if (snapshot == null) {
//             throw new IllegalArgumentException("WeatherSnapshot cannot be null");
//         }
    
//         // Creating a new BufferedImage object with a dark blue background
//         BufferedImage image = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB); // Increase the size of the image
//         Graphics2D g = image.createGraphics();
//         g.setColor(new Color(0, 0, 139)); // Dark blue
//         g.fillRect(0, 0, image.getWidth(), image.getHeight());
    
//         // Seting the color for the text
//         g.setColor(Color.WHITE);
    
//         // Drawing the logos at the top corners
//         try {
//             Image vaisalaLogo = ImageIO.read(new ClassPathResource("static/vaisala_logo.png").getInputStream());
//             Image scaledVaisalaLogo = vaisalaLogo.getScaledInstance(60, 60, Image.SCALE_SMOOTH); // Resize to 60x60
//             g.drawImage(scaledVaisalaLogo, 20, 20, null);
    
//             Image kpeduLogo = ImageIO.read(new ClassPathResource("static/kpedu_logo.png").getInputStream());
//             Image scaledKpeduLogo = kpeduLogo.getScaledInstance(60, 60, Image.SCALE_SMOOTH); // Resize to 60x60
//             g.drawImage(scaledKpeduLogo, image.getWidth() - scaledKpeduLogo.getWidth(null) - 20, 20, null);
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
    
//         // Drawing the title and date
//         g.setFont(new Font("Arial", Font.BOLD, 30)); // Increase the font size
//         g.drawString("OLLIKKALAN SÄÄ", image.getWidth() / 2 - 100, 120); // Adjust the position
//         g.setFont(new Font("Arial", Font.PLAIN, 24)); // Increase the font size
//         g.drawString("Keskiviikko 25.3", image.getWidth() / 2 - 100, 150); // Adjust the position
    
//         // Drawing the weather data boxes
//         drawWeatherBox(g, "Temperature", snapshot.getTemperature(), 20, 180, 180, 90, "temperature_icon");
//         drawWeatherBox(g, "Wind Speed", snapshot.getWindSpeed(), 200, 180, 180, 90, "wind_speed_icon");
//         drawWeatherBox(g, "Rain Accumulation", snapshot.getRainAccumulation(), 20, 270, 180, 90, "rain_accumulation_icon");
//         drawWeatherBox(g, "Thermal Sum", snapshot.getThermalSum(), 200, 270, 180, 90, "thermal_sum_icon");

    
//         // Drawing the footer logo
//         try {
//             Image kannusLogo = ImageIO.read(new ClassPathResource("static/kannus_logo.png").getInputStream());
//             Image scaledKannusLogo = kannusLogo.getScaledInstance(60, 60, Image.SCALE_SMOOTH); // Resize to 60x60
//             g.drawImage(scaledKannusLogo, image.getWidth() / 2 - scaledKannusLogo.getWidth(null) / 2, image.getHeight() - scaledKannusLogo.getHeight(null) - 20, null);
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
    
//         // Disposing the Graphics object
//         g.dispose();
    
//         return image;
//     }
    
//     private void drawWeatherBox(Graphics2D g, String label, double value, int x, int y, int width, int height, String iconName) {
//         // Drawing the background of the box
//         g.setColor(new Color(173, 216, 230)); // Light blue
//         g.fillRect(x, y, width, height);
    
//         // Drawing the label and value
//         g.setColor(Color.WHITE);
//         g.setFont(new Font("Arial", Font.PLAIN, 18)); // Increase the font size
//         g.drawString(label + ": " + value, x + 10, y + 30); // Adjust the position
    
//         // Drawing the icon
//         try {
//             Image weatherIcon = ImageIO.read(new ClassPathResource("static/" + iconName + ".png").getInputStream());
//             Image scaledWeatherIcon = weatherIcon.getScaledInstance(40, 40, Image.SCALE_SMOOTH); // Resize to 40x40
//             g.drawImage(scaledWeatherIcon, x + width - 50, y + 10, null); // Adjust the position
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
    
    
// }
