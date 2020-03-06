package com.codeoftheweb.Salvo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
public class SalvoApplication {
	@Autowired
	PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository,GameRepository gameRepository, GameplayerRepository gameplayerRepository, ShipRepository shipRepository, ScoreRepository scoreRepository) {
		return (args) -> {
			// save a couple of customers
			Player player1 = new Player("j.bauer@ctu.gov",passwordEncoder.encode("24"));
			Player player2 = new Player("c.obraian@ctu.gov",passwordEncoder.encode("42"));
			Player player3 = new Player("kim_bauer@gmail.com",passwordEncoder.encode("kb"));
			Player player4 = new Player("t.almeida@ctu.gov",passwordEncoder.encode("mole"));
			Player player5 = new Player("d.palmer@whitehouse.gov",passwordEncoder.encode("EJEMPLO"));
			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(player3);
			playerRepository.save(player4);
			playerRepository.save(player5);
			Game game1 = new Game(LocalDateTime.now());
			Game game2 = new Game(LocalDateTime.now().plusHours(1));
			Game game3 = new Game(LocalDateTime.now().plusHours(2));
			Game game4 = new Game(LocalDateTime.now().plusHours(3));
			Game game5 = new Game(LocalDateTime.now().plusHours(4));
			Game game6 = new Game(LocalDateTime.now().plusHours(5));
			Game game7 = new Game(LocalDateTime.now().plusHours(6));
			Game game8 = new Game(LocalDateTime.now().plusHours(7));
			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);
			gameRepository.save(game4);
			gameRepository.save(game5);
			gameRepository.save(game6);
			gameRepository.save(game7);
			gameRepository.save(game8);

			GamePlayer gamePlayer1 = new GamePlayer(game1,player1);
			GamePlayer gamePlayer2 = new GamePlayer(game1,player2);
			GamePlayer gamePlayer3 = new GamePlayer(game2,player1);
			GamePlayer gamePlayer4 = new GamePlayer(game2,player2);
			GamePlayer gamePlayer5 = new GamePlayer(game3,player2);
			GamePlayer gamePlayer6 = new GamePlayer(game3,player4);
			GamePlayer gamePlayer7 = new GamePlayer(game4,player2);
			GamePlayer gamePlayer8 = new GamePlayer(game4,player1);
			GamePlayer gamePlayer9 = new GamePlayer(game5,player4);
			GamePlayer gamePlayer10 = new GamePlayer(game5,player1);
			GamePlayer gamePlayer11 = new GamePlayer(game6,player3);
			GamePlayer gamePlayer12 = new GamePlayer(game7,player4);
			GamePlayer gamePlayer13 = new GamePlayer(game8,player3);
			GamePlayer gamePlayer14 = new GamePlayer(game8,player4);



			Ship ship1 =  new Ship("Battleship", Arrays.asList("B1", "B2", "B3","B4"));
			Ship ship2 =  new Ship("Patrol Boat", Arrays.asList("B4", "B5"));
			Ship ship3 =  new Ship("Submarine", Arrays.asList("E1", "F1", "G1"));
			Ship ship4 =  new Ship("Carrier", Arrays.asList("E4", "E5", "E6","E7","E8"));
			Ship ship5 =  new Ship("Destroyer", Arrays.asList("H2", "H3", "H4"));
			Ship ship6 =  new Ship("Battleship", Arrays.asList("F1", "F2", "F3","F4"));
			Ship ship7 =  new Ship("Patrol Boat", Arrays.asList("F1", "F2"));
			Ship ship8 =  new Ship("Submarine", Arrays.asList("J9", "I9", "H9"));
			Ship ship9 =  new Ship("Carrier", Arrays.asList("F4", "F5", "F6","F7","F8"));
			Ship ship10 =  new Ship("Destroyer", Arrays.asList("B5", "C5", "D5"));


			Salvo salvo1 = new Salvo(Arrays.asList("A6","B6"),1);
			Salvo salvo2 = new Salvo(Arrays.asList("A1","A2"),2);
			Salvo salvo3 = new Salvo(Arrays.asList("F6","F7","F8"),3);
			Salvo salvo4 = new Salvo(Arrays.asList("J9","I9","H9"),4);
			Salvo salvo5 = new Salvo(Arrays.asList("F4","F5","F6","F7","F8"),5);
			Salvo salvo6 = new Salvo(Arrays.asList("A7","B7"),6);
			Salvo salvo7 = new Salvo(Arrays.asList("E2","E3","E4","E5"),7);
			Salvo salvo8 = new Salvo(Arrays.asList("D2","D3"),8);
			Salvo salvo9 = new Salvo(Arrays.asList("H2","H3"),9);
			Salvo salvo10 = new Salvo(Arrays.asList("C2","C3"),10);

			gamePlayer1.addShip(ship1);
			gamePlayer1.addShip(ship2);
			gamePlayer1.addShip(ship3);
			gamePlayer1.addShip(ship4);

			gamePlayer2.addShip(ship5);
			gamePlayer2.addShip(ship6);
			gamePlayer2.addShip(ship7);
			gamePlayer2.addShip(ship8);

			gamePlayer1.addSalvo(salvo1);
			gamePlayer1.addSalvo(salvo2);
			gamePlayer1.addSalvo(salvo3);
			gamePlayer1.addSalvo(salvo4);

			gamePlayer2.addSalvo(salvo5);
			gamePlayer2.addSalvo(salvo6);
			gamePlayer2.addSalvo(salvo7);
			gamePlayer2.addSalvo(salvo8);

			Score score1 = new Score (player1, game1, 1);
			Score score2 = new Score (player2, game1, 0.5);

			gameplayerRepository.save(gamePlayer1);
			gameplayerRepository.save(gamePlayer2);
			gameplayerRepository.save(gamePlayer3);
			gameplayerRepository.save(gamePlayer4);
			gameplayerRepository.save(gamePlayer5);
			gameplayerRepository.save(gamePlayer6);
			gameplayerRepository.save(gamePlayer7);
			gameplayerRepository.save(gamePlayer8);
			gameplayerRepository.save(gamePlayer9);
			gameplayerRepository.save(gamePlayer10);
			gameplayerRepository.save(gamePlayer11);
			gameplayerRepository.save(gamePlayer12);
			gameplayerRepository.save(gamePlayer13);
			gameplayerRepository.save(gamePlayer14);


			scoreRepository.save (score1);
			scoreRepository.save (score2);
		};
	}
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userName -> {
			Player player = playerRepository.findByUserName(userName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + userName);
			}
		});
	}

}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/web/game.html", "/api/game_view/**").hasAuthority("USER")
				.antMatchers("/**").permitAll();


		http.formLogin()
				.usernameParameter("name")
				.passwordParameter("pwd")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");

		//If you have an X-frame error you only need to add this line to your WebSecurityConfig class.
		//So we need to add the below line of code to be able to see the H2-console in our database.
		http.headers().frameOptions().disable();

		//Be sure to include your login URL in the list of URLs accessible to users who are not logged in!
		//Don't forget to override the default settings that send HTML forms when unauthenticated access happens and when someone logs in or out.
		//Be sure to include your login URL in the list of URLs accessible to users who are not logged in!
		//Don't forget to override the default settings that send HTML forms when unauthenticated access happens and when someone logs in or out.


		//See the Resources for example code. Be sure to follow the example for web services. You want Spring
		// to just sent HTTP success and response codes, no HTML pages.

		// turn off checking for CSRF tokens.CSRF tokens are disabled because supporting them requires a bit of work, and
		// this kind of attack is more typical with regular web page browsing.
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

	}

		private void clearAuthenticationAttributes(HttpServletRequest request) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
			}
		}
	}