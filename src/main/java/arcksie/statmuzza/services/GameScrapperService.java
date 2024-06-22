package arcksie.statmuzza.services;

import arcksie.statmuzza.models.Game;
import arcksie.statmuzza.models.GameImpl;
import arcksie.statmuzza.repositories.GameRepository;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Locale;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class GameScrapperService {

    @Autowired
    private GameRepository gameRepository;

    private WebDriver driver;

    @PostConstruct
    public void init() {
        // Configuración del WebDriver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Ejecuta Chrome en modo headless
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        if (gameRepository.count() == 0) {
            // La tabla de la base de datos está vacía
            getMatchday(1);
            //fillDB();
        }
    }

    @Scheduled(fixedRate = 30000) //30 secs
    public void checkForMatchesAndScrape() {
        //TBI
    }

    /**
     * getMatchday
     * Esta función recibe un parámetro numérico que representa el número de la jornada.
     * Ejecuta una función de JavaScript contra la página a scrappear.
     * Devuelve una lista de GamesImpl construidos en base al HTML de la página.
     */
    public List<GameImpl> getMatchday(int nro) {
        String url = "https://www.promiedos.com.ar/primera";
        driver.get(url);

        // Ejecutar la función de JavaScript
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String script = "irfecha('" + nro + "_14')";
        js.executeScript(script);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("fixturein")));

        String pageSource = driver.getPageSource();
        Document doc = Jsoup.parse(pageSource);
        Element fixtureinDiv = doc.getElementById("fixturein");
        Element tbody = fixtureinDiv.selectFirst("tbody");

        List<GameImpl> games = new ArrayList<>();

        // Iterar sobre todas las filas del tbody
        Elements rows = tbody.select("tr");
        String date = null; // Almacenar la fecha para todos los partidos en esta iteración
        for (Element row : rows) {
            if (row.hasClass("diapart")) {
                date = row.text().trim();
                continue;
            }

            if (row.hasClass("goles") || !row.hasAttr("id")) {
                continue;
            }

            Element firstCell = row.selectFirst("td");
            Game.Status status = Game.Status.NOT_PLAYED;
            String hour = "00:01";

            if (firstCell.hasClass("game-fin")) {
                status = Game.Status.FINALIZED;
            } else if (firstCell.hasClass("game-sus")) {
                status = Game.Status.SUSPENDED;
            } else if (firstCell.hasClass("game-time")) {
                String temp = firstCell.text().trim();
                if (!temp.equals("A conf. ")){
                    hour = temp;
                }
            } else if (firstCell.hasClass("game-play")) {
                status = Game.Status.PLAYING;
            }

            // Obtener información del partido de esta fila
            Element localTeamElement = row.select("td.game-t1").get(0).selectFirst(".datoequipo");
            String localTeam = localTeamElement.text();
            Element awayTeamElement = row.select("td.game-t1").get(1).selectFirst(".datoequipo");
            String awayTeam = awayTeamElement.text();
            int homeGoals = Integer.parseInt(row.selectFirst(".game-r1").text());
            int awayGoals = Integer.parseInt(row.selectFirst(".game-r2").text());

            DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("EEEE d 'de' MMMM").parseDefaulting(java.time.temporal.ChronoField.YEAR, LocalDate.now().getYear()).toFormatter(new Locale("es", "ES"));
            DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalDate parsedDate = LocalDate.parse(date, dateFormatter);
            LocalTime parsedHour = LocalTime.parse(hour, hourFormatter);
            LocalDateTime dateTime = parsedDate.atTime(parsedHour);

            // Crear un objeto GameImpl y agregarlo a la lista
            GameImpl game = new GameImpl(localTeam, homeGoals, awayGoals, awayTeam, status, dateTime);
            games.add(game);
        }

        return games;
    }

    /**
     * updateDB
     * Esta función ejecuta getMatchday() y actualiza los valores de los juegos en la base de datos.
     */
    public void updateDB() {
        // Lógica para actualizar los valores de los juegos en la base de datos
        // Actualizar la base de datos con la información scrappeada
    }

    /**
     * fillDB
     * Esta función llena por primera vez toda la base de datos.
     * Ejecuta getMatchday() según sea necesario para llenar todas las jornadas.
     */
    public void fillDB() {
        // Lógica para llenar la base de datos por primera vez
        // Insertar la información scrappeada en la base de datos
    }
}
