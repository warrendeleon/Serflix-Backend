package com.proyecto.serflix;

import com.proyecto.serflix.config.Constants;
import com.proyecto.serflix.config.DefaultProfileUtil;
import com.proyecto.serflix.config.JHipsterProperties;
import com.proyecto.serflix.domain.Location;
import com.proyecto.serflix.domain.Movie;
import com.proyecto.serflix.service.MovieDatabase.MovieDTOService;
import com.proyecto.serflix.service.WeatherDatabase.WeatherDTOService;
import com.proyecto.serflix.service.dto.MovieDatabase.Genre;
import com.proyecto.serflix.service.dto.MovieDatabase.MovieDTO;
import com.proyecto.serflix.service.dto.WeatherDatabase.Currently;
import com.proyecto.serflix.service.dto.WeatherDatabase.LocationDTO;
import com.proyecto.serflix.service.dto.WeatherDatabase.WeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@ComponentScan
@EnableAutoConfiguration(exclude = { MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class })
@EnableConfigurationProperties({ JHipsterProperties.class, LiquibaseProperties.class })
public class SerflixApp {

    private static final Logger log = LoggerFactory.getLogger(SerflixApp.class);

    @Inject
    private Environment env;

    /**
     * Initializes Serflix.
     * <p>
     * Spring profiles can be configured with a program arguments --spring.profiles.active=your-active-profile
     * <p>
     * You can find more information on how profiles work with JHipster on <a href="http://jhipster.github.io/profiles/">http://jhipster.github.io/profiles/</a>.
     */
    @PostConstruct
    public void initApplication() {
        log.info("Running with Spring profile(s) : {}", Arrays.toString(env.getActiveProfiles()));
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(Constants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(Constants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have misconfigured your application! It should not run " +
                "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(Constants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(Constants.SPRING_PROFILE_CLOUD)) {
            log.error("You have misconfigured your application! It should not" +
                "run with both the 'dev' and 'cloud' profiles at the same time.");
        }
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments
     * @throws UnknownHostException if the local host name could not be resolved into an address
     */
    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(SerflixApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:{}\n\t" +
                "External: \thttp://{}:{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            env.getProperty("server.port"),
            InetAddress.getLocalHost().getHostAddress(),
            env.getProperty("server.port"));
        //Prueba pelicula
        MovieDTOService movieDTOService = new MovieDTOService();
        MovieDTO movie = movieDTOService.getMovie(274870);
        System.out.println("Movie: ");
        System.out.println(movie);

        //Prueba lista de generos
        List<Genre> lista = movieDTOService.getGenres();
        System.out.println("Lista de generos: ");
        System.out.println(lista);

        //Prueba obtener Movie(Domain) from Movie(DTO)
        Movie movieDomain = movieDTOService.getMovieFromDto(movie);
        System.out.println("Movie(Domain): ");
        System.out.println(movieDomain);

        //Prueba para obtener Weather
        WeatherDTOService weatherDTOService = new WeatherDTOService();
        LocationDTO locationDTO = new LocationDTO(37.8267,-122.4233);
        WeatherData weatherData = weatherDTOService.getWeatherData(locationDTO);
        System.out.println("Weather in L.A.");
        System.out.println(weatherData);

    }
}
