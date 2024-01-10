import constant.CommonConstants;
import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;

    public WeatherAppGui() {
        super("Weather App");
        JFrame jframe = new JFrame("Weather App");
        // configure gui to end the program's process once it has been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //
        setSize(450, 650);

        //
        setLocationRelativeTo(null);

        // make our layout manager null to manually position our components within the gui
        setLayout(null);

        // prevent any resize of our gui
        setResizable(false);
        getContentPane().setBackground(CommonConstants.PRIMARY_COLOR);
        addGuiComponents();

    }
    private void addGuiComponents(){

        // search field
        JTextField searchTextField = new JTextField();
        // set the location and size of our component
        searchTextField.setBounds(15, 15, 351, 45);

        searchTextField.setForeground(CommonConstants.TEXT_COLOR);
        searchTextField.setBackground(CommonConstants.PRIMARY_COLOR);
        // change the font style and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));


        // change color
        add(searchTextField);

        // weather image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        // temperature text
        JLabel temperatureText = new JLabel("10 C");

        temperatureText.setForeground(CommonConstants.SECONDARY_COLOR);
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        // center text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);
        // weather condition description
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // humidity image
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        // humidity text
        JLabel humidityText = new JLabel("<html><b> Humidity </b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);

        humidityText.setForeground(CommonConstants.SECONDARY_COLOR);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);
        // windspeed image
        JLabel windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);
        // windspeed text
        JLabel windspeedText = new JLabel("<html><b> Windspeed </b> 15km/h </html>");
        windspeedText.setBounds(320, 500, 85, 55);

        windspeedText.setForeground(CommonConstants.SECONDARY_COLOR);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);
        // search button
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));
        // change the cursor to a hand cursor when bovering over this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get location from user
                String userInput = searchTextField.getText();
                // validate input
                if (userInput.replaceAll("\\s", "").isEmpty()) {
                    return;
                }
                // retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);

                // update gui
                // update weather image
                String weatherCondition = (String) weatherData.get("weather_condition");
//                JLabel weatherCondition2 = new JLabel(weatherCondition);
//                weatherCondition2.setFont(new Font("Dialog", Font.PLAIN, 30));
//                weatherCondition2.setHorizontalAlignment(SwingConstants.CENTER);
//                weatherCondition2.setBounds(310, 300, 85, 55);
//                add(weatherCondition2);
                // depending on condition update
                switch (weatherCondition) {
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;

                }
                //update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + "C");
                // update weather condition
                weatherConditionDesc.setText(weatherCondition);
                // update humidity
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html> <b>Humidity</b>" + humidity + "%</html>");
                // update windspeed text
                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html> <b>Windspeed</b>" + humidity + "km/h</html>");
            }
        });
        add(searchButton);
    }
    private ImageIcon loadImage(String resourcePath) {
        try{
            // read the image file from the path
            BufferedImage image = ImageIO.read(new File(resourcePath));
            // returns an image icon
            return new ImageIcon(image);
        }catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Could not find resource");
        return null;
    }

}